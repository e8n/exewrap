package exewrap.jetty;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.UUID;

/** https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-start/
 * 
 * 2018-12-26 追記
 * 9.4.14 では jetty-start-9.4.14.v20181114-sources.jar と jetty-start-9.4.14.v20181114-shaded-sources.jar があった。
 * shared-sources.jar のほうは依存クラスも一緒に入っている模様。こちらのほうがよいか。
 * org/eclipse/jetty/start/shared/util を org/eclipse/jetty/util に移動させる。
 * 
 * 
 * modified: org/eclipse/jetty/start/Main.java
 * 
 * (1) public static void main(String[] args) の戻り値を boolean に変更して、
 *     main.start(startArgs) の次行に return startArgs.isRun(); を追加する。
 *     メソッドの末尾には return false; を追加する。
 * 
 * (2) usageExit method: java -jar $JETTY_HOME/start.jar  ==>  jetty.exe
 * 
 * modified: src/main/resources/org/eclipse/jetty/start/usage.txt
 * 
 * (2) java -jar start.jar  ==>  jetty.exe
 * 
 * (3) Usage 直後の説明文をカットする。
 * 
 * (4) "Startup / Shutdown Command Line" block removed.
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
 * Implementation-Versionをファイル名の一部としてJARファイルをロードするのでとても重要です。
 * Implementation-Versionが未設定だったり異なっていたりすると適切にJARファイルをロードできず起動に失敗します。
 * 
 * Implementation-Vendor: Eclipse Jetty Project
 * Implementation-Version: 9.4.20.v20190813
 * 
 * (7) demo-base/etc/test-realm.xml の修正
 * etc/test-realm.xmlはjetty.baseからの相対パスとして解決されるべきだが、
 * カレントディレクトリ(user.dir)からの相対パスとして解決されてしまうようになっている。
 *  configを以下のようにしてjetty.baseからの相対パスとして解決されるように修正します。
 *  
 *  <Set name="config"><Property name="jetty.base" default="."/>/<Property name="jetty.demo.realm" default="etc/realm.properties"/></Set>
 * 
 */

public class Main {
	
	public static final int IANA_EPHEMERAL_PORT_START = 49152;
	public static final int IANA_EPHEMERAL_PORT_END   = 65535;
	
	private static String stopPort;
	private static String stopKey;
	private static Object stopMonitor;
	
	private static void prepare() {
		if(System.getProperty("jetty.home") == null) {
			System.setProperty("jetty.home",
					System.getProperty("java.application.path"));
		}
		if(System.getProperty("jetty.base") == null) {
			System.setProperty("jetty.base",
					System.getProperty("user.dir"));
		}
		if(System.getProperty("jetty.version") == null) {
			System.setProperty("jetty.version",
					Main.class.getPackage().getImplementationVersion());
		}
	}
	
	public static void main(String[] args) throws IOException {
		prepare();
		
		org.eclipse.jetty.start.Main.main(args);
	}
	
	public static void start(String[] args) throws IOException, Exception {
		prepare();

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
		prepare();
		
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
