package exewrap.jetty;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.UUID;

import org.eclipse.jetty.start.StartArgs;

/** http://repo1.maven.org/maven2/org/eclipse/jetty/jetty-start/
 * 
 * modified: org/eclipse/jetty/start/Main.java
 * 
 * (1) usageExit method: java -jar $JETTY_HOME/start.jar  ==>  jetty.exe
 * 
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
	
	public static void main(String[] args) {
		org.eclipse.jetty.start.Main.main(args);
	}
	
	public static void start(String[] args) throws IOException, Exception {
		stopPort = String.valueOf(getStopPort());
		stopKey = UUID.randomUUID().toString();
		stopMonitor = new Object();
		
		System.setProperty("STOP.PORT", stopPort);
		System.setProperty("STOP.KEY", stopKey);
		
		org.eclipse.jetty.start.Main.main(args);
		
		StartArgs startArgs = new org.eclipse.jetty.start.Main().processCommandLine(args);
		if(startArgs.isRun()) {
			synchronized(stopMonitor) {
				stopMonitor.wait();
			}
		}
	}
	
	public static void stop() {
		System.setProperty("STOP.PORT", stopPort);
		System.setProperty("STOP.KEY", stopKey);
		
		org.eclipse.jetty.start.Main.main(new String[] { "--stop" });
		
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
	
}
