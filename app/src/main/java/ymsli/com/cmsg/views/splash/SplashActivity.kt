package ymsli.com.cmsg.views.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.databinding.ActivitySplashBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.views.dashboard.DashboardActivity
import ymsli.com.cmsg.views.login.LoginActivity
import ymsli.com.cmsg.views.sync.SyncActivity
import ymsli.com.smsapp.views.splash.SplashViewModel
import java.io.File

class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {
    override fun provideViewBinding() = ActivitySplashBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        setPermissions()
    }

    private fun setPermissions() {
        if (checkPermissions()) {
            Handler(Looper.myLooper()!!).postDelayed({ startNextActivity() }, Constants.SPLASH_DELAY)
        } else {
            requestPermission()
        }
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,Manifest.permission.SEND_SMS,Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 100
        )
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= 30) {
            if (Environment.isExternalStorageManager()) {
                val internal = File("/sdcard")
                val internalContents = internal.listFiles()
            } else {
                val permissionIntent = Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(permissionIntent)
            }
        }
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun startNextActivity() {
        val isLoggedIn = viewModel.isLoggedIn()
        if (isLoggedIn) {
            // sync data and start sync activity
            val intent = Intent(this, SyncActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for(num in grantResults){
            if(num==PackageManager.PERMISSION_DENIED){
                showPermissionDenied()
                return
            }
        }
        startNextActivity()
    }


    /**
     * confirms user for permission denied
     */
    private fun showPermissionDenied() {
        val message = getString(R.string.permission_denied)
        val titleDialog = getString(R.string.error_label)
        val actionCancel = {
            finish()
        }
        val actionOk = {
            finish()
        }
        showPermissionDeniedDialog(message, null, actionCancel, actionOk, titleDialog)
    }

    /**
     * Displays a confirmation dialog containing a message and two actions buttons.
     */
    protected fun showPermissionDeniedDialog(
        msg: String, labelAction: String? = getString(R.string.ACTION_OK),
        actionCancel: () -> Unit, actionOk: () -> Unit, titleDialog: String
    ) {
        val (dialogView, btnOk, btnCancel) = inflateConfirmationDialogView(msg, labelAction,titleDialog)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setCancelable(false)
            .setView(dialogView)
            .create()
        btnCancel.setOnClickListener { actionCancel(); dialog.dismiss() }
        dialog.show()
    }

    private fun inflateConfirmationDialogView(message: String, labelOk: String?,  titleDialog: String)
            : Triple<View, Any, Button> {
        val dialogView = layoutInflater.inflate(R.layout.permission_denied, null, false)
        val tvTitle = dialogView.findViewById(R.id.tv_logout) as TextView
        val tvMessage = dialogView.findViewById(R.id.tv_message) as TextView
        val btnCancel = dialogView.findViewById(R.id.btn_back) as Button
        tvTitle.text = titleDialog
        tvMessage.text = message
        return Triple(dialogView, 1 , btnCancel)
    }
}