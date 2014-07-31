#include <windows.h>
#include <locale.h>
#include <mbctype.h>
#include "include/message.h"

const char* get_message(int msg_id);
static int get_lang_index();
static void init_message();

static int lang_index = -1;
static const char** msg;

const char* get_message(int msg_id) {
	if(lang_index < 0) {
		lang_index = get_lang_index();
		init_message();
	}
	return msg[MSG_ID_COUNT * lang_index + msg_id];
}

static int get_lang_index() {
	int codepage = GetConsoleCP();
	char* locale = setlocale(LC_ALL, "");
	char* p = locale;

	switch(codepage) {
	case 65001: return MSG_LANG_ID_EN;
	case 437: return MSG_LANG_ID_EN;
	case 932: return MSG_LANG_ID_JA;
	}

	while(*p != '\0') {
		*p = tolower(*p);
		p++;
	}

	if(strstr(locale, "japan") != NULL) {
		return MSG_LANG_ID_JA;
	}
	if(strstr(locale, "932") != NULL) {
		return MSG_LANG_ID_JA;
	}

	return MSG_LANG_ID_EN;
}

static void init_message() {
	msg = (const char**)calloc(MSG_LANG_ID_COUNT * (MSG_ID_COUNT + 1), sizeof(const char*));

	//
	// ENGLISH
	//
	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_CREATE_JVM_UNKNOWN] =
	"Failed to create the Java Virtual Machine. An unknown error has occurred.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_CREATE_JVM_EDETACHED] =
	"Failed to create the Java Virtual Machine. Thread detached from the Java Virtual Machine.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_CREATE_JVM_EVERSION] =
	"Failed to create the Java Virtual Machine. JNI version error.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_CREATE_JVM_ENOMEM] =
	"Failed to create the Java Virtual Machine. Not enough memory available to create the Java Virtual Machine.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_CREATE_JVM_EEXIST] =
	"Failed to create the Java Virtual Machine. The Java Virtual Machine already exists.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_CREATE_JVM_EINVAL] =
	"Failed to create the Java Virtual Machine. An invalid argument was supplied.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_CREATE_JVM_ELOADLIB] =
	"Failed to create the Java Virtual Machine. Failed to load jvm.dll.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_TARGET_VERSION] =
	"Java %s or higher is required to run this program.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_DEFINE_CLASS] =
	"Class not found: %s";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_GET_METHOD] =
	"Method not found: %s";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_NEW_OBJECT] =
	"Failed to create object: %s";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_FIND_CLASSLOADER] =
	"Class Loader not found.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_REGISTER_NATIVE] =
	"Failed to register native methods: %s";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_LOAD_MAIN_CLASS] =
	"Failed to load the Main Class.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_FIND_MAIN_METHOD] =
	"Main method is not implemented.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_FIND_METHOD_SERVICE_START] =
	"The service could not be started. Requires to define a method within main class: public static void start(String[] args)";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_FIND_METHOD_SERVICE_STOP] =
	"The service could not be started. Requires to define a method within main class: public static void stop()";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_ERR_SERVICE_ABORT] =
	"The %s service was terminated abnormally.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_SUCCESS_SERVICE_INSTALL] =
	"%s サービスをインストールしました。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_SUCCESS_SERVICE_REMOVE] =
	"%s サービスは正常に開始されました。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_SERVICE_STARTING] =
	"%s サービスを開始します.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_SERVICE_STOPING] =
	"%s サービスを停止中です.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_SUCCESS_SERVICE_START] =
	"The %s service was started successfully.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_SUCCESS_SERVICE_STOP] =
	"The %s service was stopped successfully.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_CTRL_SERVICE_STOP] =
	"%s: The service is stopping.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_CTRL_SERVICE_TERMINATE] =
	"%s: The service is terminating.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_EN + MSG_ID_CTRL_BREAK] =
	"%s: ";

	//
	// JAPANESE
	//
	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_CREATE_JVM_UNKNOWN] =
	"JavaVMを作成できませんでした。不明なエラーが発生しました。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_CREATE_JVM_EDETACHED] =
	"JavaVMを作成できませんでした。スレッドがJavaVMに接続されていません。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_CREATE_JVM_EVERSION] =
	"JavaVMを作成できませんでした。サポートされていないバージョンです。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_CREATE_JVM_ENOMEM] =
	"JavaVMを作成できませんでしった。メモリが不足しています。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_CREATE_JVM_EEXIST] =
	"JavaVMを作成できませんでした。JavaVMは既に作成されています。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_CREATE_JVM_EINVAL] =
	"JavaVMを作成できませんでした。引数が不正です。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_CREATE_JVM_ELOADLIB] =
	"JavaVMを作成できませんでした。jvm.dllをロードできませんでした。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_TARGET_VERSION] =
	"このプログラムの実行には Java %s 以上が必要です。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_DEFINE_CLASS] =
	"クラスが見つかりません: %s";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_GET_METHOD] =
	"メソッドが見つかりません: %s";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_NEW_OBJECT] =
	"オブジェクトの作成に失敗しました: %s";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_FIND_CLASSLOADER] =
	"クラスローダーが見つかりません。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_REGISTER_NATIVE] =
	"ネイティブメソッドの登録に失敗しました: %s";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_LOAD_MAIN_CLASS] =
	"メインクラスのロードに失敗しました。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_FIND_MAIN_METHOD] =
	"mainメソッドが実装されていません。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_FIND_METHOD_SERVICE_START] =
	"サービスを開始できません。Windowsサービスは、メインクラスに public static void start(String[] args) を実装する必要があります。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_FIND_METHOD_SERVICE_STOP] =
	"サービスを開始できません。Windowsサービスは、メインクラスに public static void stop() を実装する必要があります。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_ERR_SERVICE_ABORT] =
	"%s サービスは異常終了しました。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_SUCCESS_SERVICE_INSTALL] =
	"%s サービスをインストールしました。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_SUCCESS_SERVICE_REMOVE] =
	"%s サービスを削除しました。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_SERVICE_STARTING] =
	"%s サービスを開始します.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_SERVICE_STOPING] =
	"%s サービスを停止中です.";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_SUCCESS_SERVICE_START] =
	"%s サービスは正常に開始されました。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_SUCCESS_SERVICE_STOP] =
	"%s サービスは正常に停止されました。";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_CTRL_SERVICE_STOP] =
	"%s: サービスを停止中です...";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_CTRL_SERVICE_TERMINATE] =
	"%s: 強制終了します...";

	msg[MSG_ID_COUNT * MSG_LANG_ID_JA + MSG_ID_CTRL_BREAK] =
	"%s: ";
}
