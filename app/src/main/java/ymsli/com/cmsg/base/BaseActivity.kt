package ymsli.com.cmsg.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import ymsli.com.cmsg.CMsgApplication
import ymsli.com.cmsg.R
import ymsli.com.cmsg.common.*
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.di.component.DaggerActivityComponent
import ymsli.com.cmsg.views.login.LoginActivity
import ymsli.com.cmsg.views.sync.SyncActivity
import ymsli.com.cmsg.views.userengagement.ProgressFragment
import javax.inject.Inject
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.utils.MessageStatusEnum
import ymsli.com.cmsg.utils.MessageTypeEnum
import ymsli.com.cmsg.utils.Utils
import java.io.Serializable
import java.lang.RuntimeException
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * Project Name : CMSG
 * @company  YMSLI
 * @author   VE00YM023
 * @date     October 21, 2021
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * BaseActivity : Base class for all the activities in the project.
 *                Responsible for dependency injection and other common tasks (dialog, toast) etc.
 *
 * VM : Type of Activity ViewModel
 * VB : Type of Activity ViewBinding
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity(),
    java.util.Observer {

    @Inject
    lateinit var viewModel: VM

    @Inject
    lateinit var progressFragment: ProgressFragment

    protected lateinit var binding: VB private set

    protected abstract fun provideViewBinding(): VB
    protected abstract fun injectDependencies(ac: ActivityComponent)
    protected abstract fun setupView(savedInstanceState: Bundle?)

    lateinit var messageEntity: ArrayList<MessageEntity>

    private var messageTypes: Int = MessageTypeEnum.PENDING.messageType
    private var providerTypes: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(buildActivityComponent())
        super.onCreate(savedInstanceState)

        binding = provideViewBinding()
        setContentView(binding.root)
        setupObservers()
        setupView(savedInstanceState)
        setupReceiver()
        ObservableObject.getInstance().addObserver(this)
    }

    private fun setupReceiver() {

        val filter = IntentFilter("ymsli.com.cmsg.SMS_SENT_COMPLETE")
        registerReceiver(receiver, filter)
    }

    private fun buildActivityComponent(): ActivityComponent {
        return DaggerActivityComponent.factory()
            .create((application as CMsgApplication).appComponent, this)
    }


    protected open fun setupObservers() {
        /* Show snackbar messages on the UI */
        viewModel.message.observe(this, { event ->
            event?.getIfNotHandled()?.let { showMessage(it) }
        })

        viewModel.showProgress.observe(this, {
            it.getIfNotHandled()?.let {
                showProgress(it)
            }
        })


        viewModel.messageStringId.observe(this, Observer {
            it?.data?.run { showMessage(this) }
        })


    }

    /** Post a message to the UI using Toast */
    private fun showMessage(message: String) =
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()

    private fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))

    /**
     * show or hide progress based on input parameter
     */
    fun showProgress(showProgress: Boolean) {
        if (showProgress) {
            if (!progressFragment.isAdded) {
                val frag = supportFragmentManager.findFragmentByTag(ProgressFragment.TAG)
                if (!(frag != null && frag is ProgressFragment)) {
                    progressFragment.show(supportFragmentManager, ProgressFragment.TAG)
                    progressFragment.isCancelable = false
                }
            }
        } else if (progressFragment.isResumed) progressFragment.dismiss()
    }

    /**
     * Displays a confirmation dialog containing a message and two actions buttons.
     * @param msg confirmation message to be displayed
     * @param labelAction label of 'POSITIVE' Action
     * @param actionCancel
     * @param actionOk
     */
    protected fun showConfirmationDialog(
        msg: String, labelAction: String = getString(R.string.ACTION_OK),
        actionCancel: () -> Unit, actionOk: () -> Unit, titleDialog: String
    ) {
        var (dialogView, btnOk, btnCancel) = inflateConfirmationDialogView(
            msg,
            labelAction,
            titleDialog
        )
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setCancelable(false)
            .setView(dialogView)
            .create()
        btnCancel.setOnClickListener { actionCancel(); dialog.dismiss() }
        btnOk.setOnClickListener { dialog.dismiss(); actionOk() }
        dialog.show()
    }

    /**
     * Displays a confirmation dialog containing a message and two actions buttons.
     * @param msg confirmation message to be displayed
     * @param labelAction label of 'POSITIVE' Action
     * @param actionCancel
     * @param actionOk
     */
    protected fun showConfirmationDialog(
        msg: String, labelAction: String = getString(R.string.ACTION_OK),
        actionCancel: () -> Unit, actionOk: () -> Unit, titleDialog: String, iconType: Int
    ) {
        var (dialogView, btnOk, btnCancel) = inflateConfirmationDialogView(
            msg,
            labelAction,
            titleDialog,
            iconType
        )
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setCancelable(false)
            .setView(dialogView)
            .create()
        btnCancel.setOnClickListener { actionCancel(); dialog.dismiss() }
        btnOk.setOnClickListener { dialog.dismiss(); actionOk() }
        dialog.show()
    }

    private fun inflateConfirmationDialogView(message: String, labelOk: String, titleDialog: String)
            : Triple<View, Button, Button> {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null, false)
        val tvTitle = dialogView.findViewById(R.id.tv_logout) as TextView
        val tvMessage = dialogView.findViewById(R.id.tv_message) as TextView
        val btnCancel = dialogView.findViewById(R.id.btn_back) as Button
        val btnOk = dialogView.findViewById(R.id.btn_confirm) as Button
        tvTitle.text = titleDialog
        btnOk.text = labelOk
        tvMessage.text = message
        return Triple(dialogView, btnOk, btnCancel)
    }

    private fun inflateConfirmationDialogView(
        message: String,
        labelOk: String,
        titleDialog: String,
        iconType: Int
    )
            : Triple<View, Button, Button> {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null, false)
        val tvTitle = dialogView.findViewById(R.id.tv_logout) as TextView
        val tvMessage = dialogView.findViewById(R.id.tv_message) as TextView
        val btnCancel = dialogView.findViewById(R.id.btn_back) as Button
        val iconInfo = dialogView.findViewById(R.id.iv_logout) as ImageView
        val btnOk = dialogView.findViewById(R.id.btn_confirm) as Button
        tvTitle.text = titleDialog
        btnOk.text = labelOk
        tvMessage.text = message
        iconInfo.setImageDrawable(ContextCompat.getDrawable(this, iconType))
        return Triple(dialogView, btnOk, btnCancel)
    }

    /**
     * confirms user for logout
     */
    fun logout() {
        val message = getString(R.string.message_logout)
        val labelLogout = getString(R.string.confirm)
        val titleDialog = getString(R.string.logout)
        val actionCancel = {}
        val actionOk = {
            performLogout()
        }
        showConfirmationDialog(message, labelLogout, actionCancel, actionOk, titleDialog)
    }

    private fun performLogout() {
        viewModel.setLoggedIn(false)
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t10B. Logout Event")
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        finishAffinity()
        startActivity(loginIntent)
    }

    /**
     * start sync operation
     * to re-sync data
     */
    fun startSync() {
        // launch Sync Activity
        val syncIntent = Intent(this, SyncActivity::class.java)
        syncIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        finishAffinity()
        startActivity(syncIntent)
    }

    private lateinit var smsIntent: Intent

    /**
     * triggers service which
     * sends SMS to the user
     */
    fun sendSMS(messageEntity: MessageEntity) {
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tSingle send SMS triggered for smsId: " + messageEntity.smsId)
        smsIntent = Intent(this, SendSMSService::class.java)
        smsIntent.putExtra("SMSID", messageEntity.smsId)
        startService(smsIntent)
    }

    private fun sendBulkMessages(messages: ArrayList<MessageEntity>) {
        // show progress and start sending messages
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t***Send bulk SMS triggered***")
        val smsIntent = Intent(this, SendSMSService::class.java)
        smsIntent.putExtra("SMS_LIST", messages as Serializable)
        smsIntent.putExtra("MESSAGE_TYPE", messageTypes)
        smsIntent.putExtra("PROVIDER_TYPE", providerTypes)
        startService(smsIntent)
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ymsli.com.cmsg.SMS_SENT_COMPLETE") {
                messageTypes = intent.getIntExtra("MESSAGE_TYPE", 0)
                providerTypes = intent.getStringExtra("PROVIDER_TYPE")
                Log.i("ProviderType", providerTypes.toString())
                if (messageTypes == MessageTypeEnum.PENDING.messageType) {
                    viewModel.getPendingMessages(MessageStatusEnum.PENDING.status, messageTypes)
                        .observe(this@BaseActivity) {
                            if (it != null && it.isNotEmpty()) {
                                messageEntity = it.toCollection(ArrayList())
                                if (messageTypes == MessageTypeEnum.SENT_API.messageType || messageTypes == MessageTypeEnum.DELIVERED.messageType) {
                                    messageEntity = (messageEntity.filter {
                                        it.messageStatus.equals(
                                            MessageStatusEnum.SENT.status,
                                            true
                                        )
                                    }).toCollection(ArrayList())
                                }
                                Log.i("ListOfPendingMessage", messageEntity.size.toString())
                                if (messageEntity.isNotEmpty()) {
                                    showSnackbarWithDuration(messageEntity)
                                }
                            }
                        }
                }
                else if (providerTypes != null) {
                    if (messageTypes == MessageTypeEnum.NOT_DELIVERED.messageType) {
                        viewModel.getMessagesOfProvider(
                            MessageTypeEnum.SENT_API.messageType,
                            providerTypes!!
                        )
                            .observe(this@BaseActivity) {
                                if (it != null && it.isNotEmpty()) {
                                    messageEntity = it.toCollection(ArrayList())
                                    if (messageTypes == MessageTypeEnum.SENT_API.messageType) {
                                        messageEntity = (messageEntity.filter {
                                            it.messageStatus.equals(
                                                MessageStatusEnum.SENT.status,
                                                true
                                            )
                                        }).toCollection(ArrayList())
                                    } else if (messageTypes == MessageTypeEnum.NOT_DELIVERED.messageType) {
                                        messageEntity = (messageEntity.filterNot {
                                            it.messageStatus.equals(
                                                MessageStatusEnum.SENT.status,
                                                true
                                            )
                                        }).toCollection(ArrayList())
                                    }
                                    Log.i("ListOfUndeliveredMessage", messageEntity.size.toString())
                                    if (messageEntity.isNotEmpty()) {
                                        showSnackbarWithDuration(messageEntity)
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    private fun showSnackbarWithDuration(longTouchSelectionMessageList: ArrayList<MessageEntity>) {
        val customSnackbarView = LayoutInflater.from(this).inflate(R.layout.custome_snakebar, null)
        val snackbar = Snackbar.make(binding.root, "", 4000)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        if (longTouchSelectionMessageList.size > 500) {
            customSnackbarView.findViewById<TextView>(R.id.snackbar_title).text =
                "Sending"
            customSnackbarView.findViewById<TextView>(R.id.snackbar_text).text =
                "Sending 500 out of ${longTouchSelectionMessageList.size} messages"

        } else {
            customSnackbarView.findViewById<TextView>(R.id.snackbar_title).text = "Sending"
            customSnackbarView.findViewById<TextView>(R.id.snackbar_text).text =
                "Sending ${longTouchSelectionMessageList.size} messages"
        }
        supportActionBar?.subtitle = null
        // Add the custom layout to the Snackbar
        snackbarLayout.addView(customSnackbarView, 0)
        // Show the Snackbar
        snackbar.show()
        // Dismiss the Snackbar after the specified duration
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            snackbar.dismiss()
            sendBulkMessages(messageEntity)
            //sendBulkSMS(longTouchSelectionMessageList)
        }, 4000)
    }

    private fun showSnackbarWithDuration(
        view: View,
        title: String,
        message: String,
        durationInMillis: Long
    ) {
        Log.i("ListOf", "SnackBar")
        val customSnackbarView = LayoutInflater.from(this).inflate(R.layout.custome_snakebar, null)
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        customSnackbarView.findViewById<TextView>(R.id.snackbar_title).text = title
        customSnackbarView.findViewById<TextView>(R.id.snackbar_text).text = message
        // Add the custom layout to the Snackbar
        snackbarLayout.addView(customSnackbarView, 0)
        // Show the Snackbar
        snackbar.show()
        // Dismiss the Snackbar after the specified duration
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            snackbar.dismiss()
        }, durationInMillis)

    }

    fun hideKeyboard(view: View) =
        (this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as? InputMethodManager)!!
            .hideSoftInputFromWindow(view.windowToken, 0)
    /**
     * deletes the corresponding message
     */
    fun deleteMessage(messageEntity: MessageEntity) {
        val deleteMessages = ArrayList<MessageEntity>()
        deleteMessages.add(messageEntity)
        viewModel.deleteMessage(deleteMessages)
    }

    override fun update(o: Observable?, arg: Any?) {
        if ((arg as Intent).extras?.get("sms_update") != null) {
            val smsId = arg.getLongExtra("sms_update", -1)
            val status = arg.getStringExtra("sms_status")
            Log.i("Device Status", status.toString());
            if (smsId > 0) {
                when (status) {
                    MessageStatusEnum.SENT.status -> {
                        viewModel.updateMessageStatus(smsId, MessageTypeEnum.SENT_OFFLINE.name)
                        viewModel.messageString.postValue(Event(resources.getString(R.string.msg_delivered)))
                        if (this::smsIntent.isInitialized) {
                            stopService(smsIntent)
                        }
                    }

                    MessageStatusEnum.OPERATOR_NOT_SUPPORTED.status -> {
                        Log.i("Device", "Update");
                        viewModel.messageString.postValue(Event(resources.getString(R.string.no_provider_found)))
                    }

                    MessageStatusEnum.FAILED.status -> {
                        viewModel.updateMessageStatus(smsId, MessageStatusEnum.FAILED.status)
                        viewModel.messageString.postValue(Event(resources.getString(R.string.msg_not_delivered)))
                    }

                    MessageStatusEnum.PENDING.status -> {
                        viewModel.messageString.postValue(Event(resources.getString(R.string.message_triggered)))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}