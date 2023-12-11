package ymsli.com.cmsg.views.sentsms.dashboard

import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import ymsli.com.cmsg.adapter.SentMessageDashboardAdapter
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.databinding.ActivitySentDashboardBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.model.SentMessageDashboardModel
import ymsli.com.cmsg.utils.MessageTypeEnum

class SentDashboardActivity : BaseActivity<SentDashboardViewModel, ActivitySentDashboardBinding>() {
    override fun provideViewBinding() = ActivitySentDashboardBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    lateinit var sentMessageDashboardAdapter: SentMessageDashboardAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun setupView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.actionBarTopSentDashboard)
        supportActionBar?.title = "Sent Messages Dashboard"
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel.getMessagesOfType(MessageTypeEnum.SENT_API.messageType).observe(this,{
            if(!it.isEmpty()) viewModel.createMessageSentModel(it)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.sentMessageDashboardModel.observe(this,{
            it.getIfNotHandled()?.let {
                // set this array list as adapter to RV of Message Sent in 7 days
                setupMessageSentData(it)
            }
        })
    }

    /**
     * setup RV for messages
     * sent in last 7 days
     */
    private fun setupMessageSentData(sentMessagesInfoList: ArrayList<SentMessageDashboardModel>){
        linearLayoutManager = LinearLayoutManager(this)
        sentMessageDashboardAdapter = SentMessageDashboardAdapter(this.lifecycle, ArrayList(),true)
        binding.recyclerSentMsgList.adapter = sentMessageDashboardAdapter
        binding.recyclerSentMsgList.layoutManager = linearLayoutManager
        binding.recyclerSentMsgList.setHasFixedSize(true)
        sentMessageDashboardAdapter.loadData(sentMessagesInfoList)
    }
}