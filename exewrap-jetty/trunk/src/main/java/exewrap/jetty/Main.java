package exewrap.jetty;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.UUID;

/** http://repo1.maven.org/maven2/org/eclipse/jetty/jetty-start/
 * 
 * 2018-12-26 追記
 * 9.4.14 では jetty-start-9.4.14.v20181114-sources.jar と jetty-start-9.4.14.v20181114-shaded-sources.jar があった。
 * shared-sources.jar のほうは依存クラスも一緒に入っている模様。こちらのほうがよいか。
 * org/eclipse/jetty/start/shared/util を org/eclipse/jetty/util に移動させる。
 * 
 * 
 * modified: org/eclipse/jetty/start/Main.java
 * 
 * (1) usageExit method: java -jar $JETTY_HOME/start.jar  ==>  jetty.exe
 * 
 * (2) public static void main(String[] args) の戻り値を boolean に変更して、
 *     main.start(startArgs) の次行に return startArgs.isRun(); を追加する。
 *     メソッドの末尾には return false; を追加する。
 * 
 * modified: src/main/resources/org/eclipse/jetty/start/usage.txt
 * 
 * (2) java -jar start.jar  ==>  jetty.exe
 * 
 * (3) "Startup / Shutdown Command Line" block removed.
 * 
 * (4) Usage 直後の説明文をカットする。
 * 
 * (5) Properties: 説明文の STOP.HOST, STOP.PORT, STOP.KEY, STOP.WAIT を削除する。
 * 
 * (6) 以下のサービスオプションの記載を追加する。
##### ここから #####
Service Options:
----------------

  Install service
  
    [install-options] -install [runtime-arguments]
  
    install-options:
      -n <display-name>  set service display name.
      -i                 allow interactive.
      -m                 
      -d <dependencies>  
      -u <username>      
      -p <password>      
      -s                 start service.
  
  Remove service
  
    [remove-options] -remove
  
    remove-options:
      -s                 stop service.
##### ここまで #####

 * (6) META-INF/MANIFEST.MF には以下の内容が必要です。(ビルド時のマニフェストとして指定すること)
 * 無い場合は Bundle-Vendor と Bundle-Version の行をコピーして Bundle の部分を Implementaion にする。
 * 
 * Implementation-Vendor: Eclipse.org - Jetty
 * Implementation-Version: 9.3.7.v20160115
 * 
 */

public class Main {
	
	public static final int IANA_EPHEMERAL_PORT_START = 49152;
	public static final int IANA_EPHEMERAL_PORT_END   = 65535;
	
	private static String stopPort;
	private static String stopKey;
	private static Object stopMonitor;
	
	public static void main(String[] args) throws IOException {
		args = prepare(args);
		org.eclipse.jetty.start.Main.main(args);
	}
	
	public static void start(String[] args) throws IOException, Exception {
		args = prepare(args);
		stopPort = String.valueOf(getStopPort());
		stopKey = UUID.randomUUID().toString();
		stopMonitor = new Object();
		
		System.setProperty("STOP.PORT", stopPort);
		System.setProperty("STOP.KEY", stopKey);
		
		boolean isRun = org.eclipse.jetty.start.Main.main(args);
		if(isRun) {
			synchronized(stopMonitor) {
				stopMonitor.wait();
			}
		}
	}
	
	public static void stop() throws IOException {
		String[] args = prepare(new String[] { "--stop" });
		System.setProperty("STOP.PORT", stopPort);
		System.setProperty("STOP.KEY", stopKey);
		
		org.eclipse.jetty.start.Main.main(args);
		
		synchronized(stopMonitor) {
			stopMonitor.notifyAll();
		}
	}
	
	private static int getStopPort() {
		int port = IANA_EPHEMERAL_PORT_START;
		InetAddress address;
		ServerSocket s = null;
		try {
			address = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
			while(port < IANA_EPHEMERAL_PORT_END - 1) {
				try {
					s = new ServerSocket();
					s.setReuseAddress(true);
					s.bind(new InetSocketAddress(address, port));
				} catch (IOException e) {
					port++;
					continue;
				} finally {
					try { s.close(); } catch (Exception e) { }
				}
				break;
			}
		} catch (UnknownHostException e1) {
			
		}
		return port;
	}

	private static String[] prepare(String[] args) throws IOException {
		String jettyHome = null;
		String jettyBase = null;
		
		for(int i = 0; i < args.length; i++) {
			String s = args[i].toLowerCase();
			if(s.length() > 11 && s.startsWith("jetty.home=")) {
				jettyHome = new File(args[i].substring(11)).getCanonicalPath();
				args[i] = "jetty.home=" + jettyHome;
			}
			if(s.length() > 11 && s.startsWith("jetty.base=")) {
				jettyBase = new File(args[i].substring(11)).getCanonicalPath();
				args[i] = "jetty.base=" + jettyBase;
			}
		}
		
		if(jettyBase != null) {
			System.setProperty("user.dir", jettyBase);
		}
		
		if(jettyHome == null) {
			String[] newArgs = new String[args.length + 1];
			newArgs[0] = "jetty.home=" + System.getProperty("java.application.path");
			for(int i = 0; i < args.length; i++) {
				newArgs[i + 1] = args[i];
			}
			return newArgs;
		}
		return args;
	}
}
