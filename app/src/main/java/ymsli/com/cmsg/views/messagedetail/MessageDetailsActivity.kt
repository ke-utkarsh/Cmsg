package ymsli.com.cmsg.views.messagedetail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.database.entity.PhoneSIMEntity
import ymsli.com.cmsg.databinding.ActivityMessageDetailsBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.utils.MessageStatusEnum
import ymsli.com.cmsg.utils.MessageTypeEnum
import ymsli.com.cmsg.utils.Utils
import ymsli.com.cmsg.views.dashboard.DashboardActivity

class MessageDetailsActivity :
    BaseActivity<MessageDetailsViewModel, ActivityMessageDetailsBinding>() {
    override fun provideViewBinding() = ActivityMessageDetailsBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_msg_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.actionBarMsgDetail)
        val messageEntity =
            intent.getSerializableExtra(Constants.MESSAGE_DETAIL_INTENT) as MessageEntity
        val isReadOnly = intent.getBooleanExtra(Constants.READ_ONLY_MESSAGES_INTENT, false)

        if (isReadOnly) {
            // change button apprearing at the bottom of the layout
            binding.buttonSend.visibility = View.GONE
        }
        if (messageEntity.messageStatus.equals(MessageStatusEnum.SENT.status, true) ||
            messageEntity.messageStatus.equals(MessageTypeEnum.SENT_OFFLINE.toString(), true)
        ) {
            binding.buttonDelete.visibility = View.GONE
        }
        supportActionBar?.title = messageEntity.orderNo.toString()
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //setup texts in view
        binding.tvUserName.text = messageEntity.receiverName
        binding.tvPhone.text = messageEntity.receiverMobileNo
        binding.tvMsg.text = messageEntity.messageBody

        binding.buttonSend.setOnClickListener {
            //Sending SMS
            if (viewModel.checkInternetConnection()) {
                sendingSMS(messageEntity)
            } else {
                viewModel.messageString.postValue(Event(resources.getString(R.string.network_connection_error)))
            }

        }

        binding.buttonDelete.setOnClickListener {
            // show confirmation dialog
            // deletes message and delete it from the local DB
            showDeleteConfirmation(messageEntity)


        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendingSMS(messageEntity: MessageEntity) {
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
            //Send SMS, When Sim is available
            sendSMS(messageEntity)
        } else {
            //Clear the Sim List, When no sim available.
            simList.clear()
            //Update the empty sim array.
            viewModel.storeSIMInfo(simList.toTypedArray())
            //Show dialog to user that Sim is not available
            Utils.showNoSimDialog(this@MessageDetailsActivity)
        }
    }


    override fun setupObservers() {
        super.setupObservers()
        viewModel.isMessageDeleted.observe(this, {
            it.getIfNotHandled()?.let {
                if (it) {
                    // ack user and finish activity
                    viewModel.messageString.postValue(Event(resources.getString(R.string.message_successfully_deleted)))
                    finish()
                } else {
                    // ack user
                    viewModel.messageString.postValue(Event(resources.getString(R.string.network_default_error)))
                }
            }
        })

        viewModel.messageString.observe(this, {
            it.getIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.home) {
            // finish current activity
            // and open dashboard activity
            val dashboardIntent = Intent(this, DashboardActivity::class.java)
            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            finishAffinity()
            startActivity(dashboardIntent)
        } else if (item.itemId == R.id.logout) {
            //proceed for logout
            logout()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteConfirmation(messageEntity: MessageEntity) {
        val message = getString(R.string.message_delete_confirm)
        val labelLogout = getString(R.string.confirm)
        val titleDialog = getString(R.string.confirmation)
        val actionCancel = {}
        val actionOk = {
            if (viewModel.checkInternetConnection()) {
                deleteMessage(messageEntity)
            } else {
                viewModel.messageString.postValue(Event(resources.getString(R.string.network_connection_error)))
            }
        }
        showConfirmationDialog(
            message,
            labelLogout,
            actionCancel,
            actionOk,
            titleDialog,
            R.drawable.ic_error
        )
    }


}