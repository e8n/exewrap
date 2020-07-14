jetty-startのソースコード ダウンロード
https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-start/

たとえば Jetty 9.4.1 の jetty-start の場合…
https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-start/9.4.1.v20170120/
ここから
jetty-start-9.4.1.v20170120-sources.jar
をダウンロードします。

ダウンロードしたZIPを展開して
org を src/main/java にコピー
METAINF-INF を src/main/resources にコピー

jetty-9.4.11ではjetty-startだけでなくjetty-utilも必要になりました。
jetty-startが依存しているjetty-util内の
JavaVersion.java
TopologicalSort.java
の2つが必要です。

src/main/java/exewrap/jetty/Main.java のコメントを読んで org にコピーしたソースを修正します。


2017/10/17
下記usage.txtを移動させる作業は不要になりました。
 * src/main/java/org/eclipse/jetty/start/usage.txt を
 * src/main/resources/org/eclipse/jetty/start/usage.txt に移動する。

build.gradleに下記の設定をすることで、
src/main/java 下位の usage.txt も自動的にjar に含まれるようになったためです。

sourceSets {
	main {
		resources {
			srcDirs "src/main/resources", "src/main/java"
		}
	}
}

2015/10/04
jetty/lib/cdi-websocket-9.3.3.v20150827.jar が存在するとdemo-baseがエラーになる。
cdi-websocket-9.3.3.v20150827.jarを消せば問題なく動作するようです。

2016-11-23
jetty/lib/cdi-websocket が存在しても問題なくなったみたい？

2020-05-15
Java 11でJAXBを使うには…

JAXBに必要なライブラリを取得します。
以下のbuild.gradleを設定してcopyJAXBタスクを実行すると build/JAXB 以下に必要なライブラリが出力されます。
これを jetty 配布フォルダーの lib/ext/jaxb などにまるごとコピーしておけばJAXBが使えるようになります。

repositories {
	mavenCentral()
}

dependencies {
	implementation "javax.xml.bind:jaxb-api:2.3.1"
	implementation "org.glassfish.jaxb:jaxb-runtime:2.3.1"
}

task copyJAXB(type: Copy) {
	setGroup("build")
	setDescription("Copy JAXB libraries.")
	from (sourceSets.main.runtimeClasspath) {
		include '*.jar'
	}
	from('lib') {
		include '*.dll'
	}
	into "${buildDir}/JAXB/"
}

