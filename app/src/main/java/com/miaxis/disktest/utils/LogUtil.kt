package com.miaxis.disktest.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

open class LogUtil {
    var logSwitch = false;
    private val logTag = "TEST";
    private var logPath: String? = null

    companion object {

        val instance: LogUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LogUtil()
        }
    }

    // Used to load the 'test' library on application startup.
    init {
        System.loadLibrary("test")
    }

    private external fun write(path: String, message: String, length: Int, count: Int): String

    private external fun delete(path: String): Int

    private external fun deleteAll(path: String): Int

    fun deleteAll(context: Context){
        val externalFilesDir = context.getExternalFilesDir(null)
        if (externalFilesDir != null) {
            val file = File("${externalFilesDir.path}/Log")
            if (file.exists()) {
                deleteAll(file.path)
            }
        }
    }

    fun init(context: Context) {
        val externalFilesDir = context.getExternalFilesDir(null)
        if (externalFilesDir != null) {
            val file = File("${externalFilesDir.path}/Log")
            if (!file.exists()) {
                try {
                    file.mkdirs()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            logPath = file.path + "/" + TimeUtil.nowTimeOfMinute() + ".txt"
            deleteFile()
        }
    }

    private fun deleteFile(){
        delete(logPath!!)
    }

    /**
     * @param tag          标签
     * @param msg          信息
     * @param recordLocal  是否本地日志保存
     */
    fun d(msg: String, recordLocal: Boolean = false) {
        if (recordLocal && logPath != null) {
            // 本地日志保存
            write(logPath!!, msg, msg.length, 1)
        }
    }

}