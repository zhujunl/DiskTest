package com.miaxis.disktest.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.miaxis.disktest.R
import com.miaxis.disktest.utils.LogUtil
import com.miaxis.disktest.utils.WriteDiskUtil


class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 0x01
        private val PERMISSIONS_STORAGE = arrayOf(
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.MANAGE_EXTERNAL_STORAGE"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyStoragePermissions()

        LogUtil.instance.logSwitch = true
        LogUtil.instance.deleteAll(applicationContext)
        WriteDiskUtil.instance.init(application)

        if (!Settings.canDrawOverlays(this)) {
            //若未授权则请求权限
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, 0)
        }
    }

    private fun verifyStoragePermissions() {
        try {
            //检测是否有写的权限
            val permission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
            }
        } catch (e: Exception) {
            e.printStackTrace();
        }

    }
}