package ymsli.com.cmsg.views.sync

import android.annotation.SuppressLint
import android.app. AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.database.entity.PhoneSIMEntity
import ymsli.com.cmsg.databinding.ActivitySyncDataBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.utils.Utils
import ymsli.com.cmsg.views.dashboard.DashboardActivity
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import ymsli.com.cmsg.R
import ymsli.com.cmsg.common.Constants
import java.io.File
import java.sql.Timestamp


class SyncActivity : BaseActivity<SyncViewModel, ActivitySyncDataBinding>() {

    override fun provideViewBinding(): ActivitySyncDataBinding =
        ActivitySyncDataBinding.inflate(layoutInflater)

    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        binding.progressBar.max = 100
        binding.progressBar.progress = 20

        //Added Condition when internet is available.
        if (viewModel.checkInternetConnection()) {
            //Execute the API and perform room database operation
            viewModel.getServiceProvider()
            //clear older data
            viewModel.clearRoomData()
        } else {
            Toast.makeText(applicationContext, Constants.NO_NETWORK_AVAILABLE, Toast.LENGTH_LONG)
                .show()
            //Show the dialog when No internet connection available.
            //getSIMInformation()
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.smsAPICallCompleted.observe(this, {
            it.getIfNotHandled()?.let {
                if (it) {
                    viewModel.getSentMessages()
                }
            }
        })

        viewModel.openNextActivity.observe(this, {
            it.getIfNotHandled()?.let {
                if (it) {
                    getSIMInformation()
                }
            }
        })
    }

    private fun openDashboardActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    /**
     * gets information of
     * SIM(s) from the phone
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getSIMInformation() {
//        val subscription = SubscriptionManager.from(
//            applicationContext
//        ).activeSubscriptionInfoList
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
        var subscriptionManager = getSystemService(SubscriptionManager::class.java)
        val simList: ArrayList<PhoneSIMEntity> = ArrayList()
        simList.clear()
        val subscription = subscriptionManager.activeSubscriptionInfoList
        val sim1 = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0)
        val sim2 = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1)
        if (sim1 != null || sim2 != null) {
            for (i in subscription.indices) {
                val simInfo = PhoneSIMEntity(
                    slot = subscription[i].simSlotIndex + 1,
                    carrierName = subscription[i].carrierName.toString(),
                    subscriptionId = subscription[i].subscriptionId
                )
                simList.add(simInfo)
            }
            viewModel.storeSIMInfo(simList.toTypedArray())
            openDashboardActivity()
        } else {
            simList.clear()
            viewModel.storeSIMInfo(simList.toTypedArray())
            showNoSimDailog()
        }
    // }

    }

    private fun showNoSimDailog() {

        val message = getString(R.string.no_sim_message)
        val titleDialog = getString(R.string.msg_label)
        val actionCancel = {
            finish()
        }
        val actionOk = {
            finish()
        }
        showPermissionDeniedDialog(message, null, actionCancel, actionOk, titleDialog)

    }


    protected fun showPermissionDeniedDialog(
        msg: String, labelAction: String? = getString(R.string.ACTION_OK),
        actionCancel: () -> Unit, actionOk: () -> Unit, titleDialog: String
    ) {
        val (dialogView, btnOk, btnCancel) = inflateConfirmationDialogView(
            msg,
            labelAction,
            titleDialog
        )
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setCancelable(false)
            .setView(dialogView)
            .create()
        btnCancel.setOnClickListener { actionOk(); dialog.dismiss(); goToDashboard() }
        dialog.show()
    }

    private fun goToDashboard() {
        var intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun inflateConfirmationDialogView(
        message: String,
        labelOk: String?,
        titleDialog: String
    )
            : Triple<View, Any, Button> {
        val dialogView = layoutInflater.inflate(R.layout.permission_denied, null, false)
        val tvTitle = dialogView.findViewById(R.id.tv_logout) as TextView
        val iconTitle = dialogView.findViewById(R.id.iv_logout) as ImageView
        val tvMessage = dialogView.findViewById(R.id.tv_message) as TextView
        val btnCancel = dialogView.findViewById(R.id.btn_back) as Button
        tvTitle.text = titleDialog
        tvMessage.text = message
        iconTitle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_check))
        return Triple(dialogView, 1, btnCancel)
    }

}