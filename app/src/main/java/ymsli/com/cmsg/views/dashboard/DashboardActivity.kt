package ymsli.com.cmsg.views.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ymsli.com.cmsg.R
import ymsli.com.cmsg.adapter.MessageAdapter
import ymsli.com.cmsg.adapter.SentMessageDashboardAdapter
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.databinding.ActivityDashboardBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.model.SentMessageDashboardModel
import ymsli.com.cmsg.utils.MessageStatusEnum
import ymsli.com.cmsg.utils.MessageTypeEnum
import ymsli.com.cmsg.utils.Utils
import ymsli.com.cmsg.views.pendingsms.list.PendingMsgListActivity
import ymsli.com.cmsg.views.profile.ProfileActivity
import ymsli.com.cmsg.views.settings.SettingsActivity
import java.sql.Timestamp

class DashboardActivity : BaseActivity<DashboardViewModel, ActivityDashboardBinding>() {
    override fun provideViewBinding() = ActivityDashboardBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var sentMessageDashboardAdapter: SentMessageDashboardAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.actionBarTopDashboard)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setIcon(R.drawable.logo)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.pendingConstLeft.setOnClickListener {
            val pendingIntent = Intent(this, PendingMsgListActivity::class.java)
            pendingIntent.putExtra(Constants.READ_ONLY_MESSAGES_INTENT, false)
            pendingIntent.putExtra(
                Constants.MESSAGE_TYPE_INTENT, MessageTypeEnum.PENDING.messageType)
            startActivity(pendingIntent)
        }

        binding.sentConstRight.setOnClickListener {
            val sentIntent = Intent(this, PendingMsgListActivity::class.java)
            sentIntent.putExtra(Constants.READ_ONLY_MESSAGES_INTENT, true)
            sentIntent.putExtra(
                Constants.MESSAGE_TYPE_INTENT,
                MessageTypeEnum.SENT_OFFLINE.messageType
            )
            sentIntent.putExtra(Constants.LOCAL_SENT_INTENT, true)
            startActivity(sentIntent)
        }

        viewModel.getPendingMessagesCount(
            MessageStatusEnum.PENDING.status,
            MessageTypeEnum.PENDING.messageType
        ).observe(this, {
            binding.countTvDelivered.text = it.toString()
        })

        viewModel.getSentMessagesOfflineCount().observe(this, {
            binding.countTvUndelivered.text = it.toString()
        })

        viewModel.getMessagesOfType(MessageTypeEnum.SENT_API.messageType).observe(this) {
            if (!it.isEmpty()) viewModel.createMessageSentModel(it)
        }

        binding.tvName.text = "Hi " + viewModel.getUserName()
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.sentMessageDashboardModel.observe(this, {
            it.getIfNotHandled()?.let {
                // set this array list as adapter to RV of Message Sent in 7 days
                setupMessageSentData(it)
            }
        })
    }

    /**
     * Handle the toolbar item selection.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            logout()
        } else if (item.itemId == R.id.refresh) {
            if (viewModel.isInternetConnected()) {
                Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tDashBoard Refresh Called")
                startSync()
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.network_connection_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (item.itemId == R.id.settings) {
            launchSettingsActivity()
        } else if (item.itemId == R.id.profile) {
            openProfileActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * launches user profile
     * settings screen
     */
    private fun openProfileActivity() {
        val profileIntent = Intent(this, ProfileActivity::class.java)
        startActivity(profileIntent)
    }

    /**
     * launch settings activity
     */
    private fun launchSettingsActivity() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

    /**
     * setup RV for messages
    36    * sent in last 7 days
     */
    private fun setupMessageSentData(sentMessagesInfoList: ArrayList<SentMessageDashboardModel>) {
        linearLayoutManager = LinearLayoutManager(this)
        sentMessageDashboardAdapter = SentMessageDashboardAdapter(this.lifecycle, ArrayList(), true)
        binding.recyclerSentMsgList.adapter = sentMessageDashboardAdapter
        binding.recyclerSentMsgList.layoutManager = linearLayoutManager
        binding.recyclerSentMsgList.setHasFixedSize(true)
        sentMessageDashboardAdapter.loadData(sentMessagesInfoList)
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.getMessagesOfType(MessageTypeEnum.SENT_API.messageType).observe(this) {
            if (!it.isEmpty()) viewModel.createMessageSentModel(it)
        }
        if (viewModel.isInternetConnected()) {
            // clean local DB & pull pending messages again
            Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tPending List Refresh Called")
            viewModel.cleanMessageOfType(MessageTypeEnum.PENDING.messageType)
            viewModel.getPendingMessages()
            viewModel.getSentMessages()
        } else {
            viewModel.messageString.postValue(Event(resources.getString(R.string.network_connection_error)))
        }
    }
}