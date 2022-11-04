#include <jni.h>
#include <string>
#include <android/log.h>
#include <fstream>
#include <iostream>
#include <unistd.h>
#include <dirent.h>

//方便用日志查看
#define LOG_TAG "C_NATIVE_LIB"
#define LOG_D(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOG_I(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOG_E(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOG_W(...)  __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOG_V(...)  __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
//使用宏来调用日志库
/**
 * 	%c　　　　一个字符(char)
 *	%d　　　　有符号十进制整数(int)
 *	%e　　　　浮点数、e-记数法
 *	%f　　　　单精度浮点数(默认float)、十进制记数法（%.nf  这里n表示精确到小数位后n位.十进制计数）
 *	%i      有符号十进制数（与％d相同）
 *	%p　　　 指针
 *	%s　　　 对应字符串char*（%S 对应宽字符串WCAHR*（%u无符号十进制整数(unsigned int)
 *	%%　　　 打印一个百分号
 */

extern "C" JNIEXPORT jstring JNICALL
Java_com_miaxis_disktest_utils_LogUtil_write(
        JNIEnv *env,
        jobject /* this */,
        jstring str,
        jstring message,
        jint length,
        jint count) {
    std::string hello = "Hello from C++";

    //本地代码绝不能修改字符串的内容
    const char *str_path = env->GetStringUTFChars(str, JNI_FALSE);
    if (strlen(str_path) == 0) {
        LOG_D("filepath length is 0");
        return env->NewStringUTF("error path");
    }

    struct timeval tv;
    gettimeofday(&tv, NULL);

    FILE *file = NULL;
    file = fopen(str_path, "a+");
    if (file == NULL) {
        LOG_D("Fail to open file!");
        return env->NewStringUTF("file == NULL");
    }

    const char *msg = env->GetStringUTFChars(message, JNI_FALSE);
    LOG_D("msg == %s\n", msg);
    LOG_D("length == %d\n", length);
    LOG_D("count == %d\n", count);

    fwrite(msg, length, count, file);
    //保证了将IO缓冲写入系统缓冲中，使IO读操作能成功
    fflush(file);
    //获取文件描述符
    int fd = ::fileno(file);
    //强制写硬盘
    ::fsync(fd);
    fclose(file);

    env->ReleaseStringUTFChars(str, str_path);
    env->ReleaseStringUTFChars(message, msg);

    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_com_miaxis_disktest_utils_LogUtil_delete(
        JNIEnv *env,
        jobject /* this */,
        jstring str) {
    //本地代码绝不能修改字符串的内容
    const char *str_path = env->GetStringUTFChars(str, JNI_FALSE);
    if (strlen(str_path) == 0) {
        LOG_D("filepath length is 0");
        return -1;
    }
    FILE *file = fopen(str_path, "rw");
    if (file == NULL) {
        LOG_D("Fail to open file!");
    } else {
        LOG_D("suc to open file!");
        remove(str_path);
    }
    if (file != NULL) {
        fflush(file);
        //获取文件描述符
        int fd = ::fileno(file);
        //强制写硬盘
        ::fsync(fd);
        fclose(file);
    }
    env->ReleaseStringUTFChars(str, str_path);
    return 0;
}

extern "C" JNIEXPORT int JNICALL
Java_com_miaxis_disktest_utils_LogUtil_deleteAll(JNIEnv *env, jobject instance,
                                                 jstring fileFolder) {
    const char *file_folder = env->GetStringUTFChars(fileFolder, 0);

    struct dirent *file;
    char szFileName[128];
    char szFolderName[128];
    strcpy(szFolderName, file_folder);
    strcat(szFolderName, "/%s");
    DIR *pDir = opendir(file_folder);
    if (pDir==NULL) {
        LOG_D("can not open dir,check path or permission!");
        return -1;
    }
    // 遍历文件夹并删除文件
    while ((file = readdir(pDir)) != NULL) {
        //判断是不是 . 或者 .. 文件夹 ==0 是 ！=0 则不是
        if (strcmp(file->d_name, ".") != 0 && strcmp(file->d_name, "..") != 0) {
            if (file->d_type != DT_DIR) {//文件
                //拼接文件名称
                sprintf(szFileName, szFolderName, file->d_name);
                remove(szFileName);
            } else {
                //是文件夹则遍历
                //构建文件夹路径
                char *path = new char[256];
                memset(path, 0, 256);
                strcpy(path, file_folder);
                strcat(path, "/");
                strcat(path, file->d_name);
                jstring tDir = env->NewStringUTF(path);
                //读取指定文件夹
                Java_com_miaxis_disktest_utils_LogUtil_deleteAll(env, instance, tDir);
                //释放文件夹路径内存
                free(path);
            }
        }
    }
    if (pDir != NULL) {
        remove(file_folder);//删除文件夹
        closedir(pDir);
    }
    env->ReleaseStringUTFChars(fileFolder, file_folder);
    return 0;
}