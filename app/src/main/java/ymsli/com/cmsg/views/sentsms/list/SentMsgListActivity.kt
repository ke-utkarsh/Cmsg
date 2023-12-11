package ymsli.com.cmsg.views.sentsms.list

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ymsli.com.cmsg.adapter.PendingMessagesAdapter
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.databinding.ActivitySentListBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.views.login.LoginActivity
import ymsli.com.smsapp.views.splash.SplashViewModel

class SentMsgListActivity : BaseActivity<SplashViewModel, ActivitySentListBinding>() {
    override fun provideViewBinding() = ActivitySentListBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    lateinit var linearLayoutManager: LinearLayoutManager

    override fun setupView(savedInstanceState: Bundle?) {

        linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerSentMsgList.adapter =
            PendingMessagesAdapter(createDummyContent<MessageEntity>())

        binding.recyclerSentMsgList.layoutManager = linearLayoutManager
    }

    private fun <MessageEntity> createDummyContent(): MutableList<ymsli.com.cmsg.database.entity.MessageEntity> {
        val arrayListMsgEntries = mutableListOf<ymsli.com.cmsg.database.entity.MessageEntity>()

        return arrayListMsgEntries
    }



}