package ymsli.com.cmsg.viewholder

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseItemViewHolder
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.databinding.ActivityDashboardListItemBinding
import ymsli.com.cmsg.di.component.ViewHolderComponent
import ymsli.com.cmsg.itemviewmodel.SentMessageDashboardItemViewModel
import ymsli.com.cmsg.model.SentMessageDashboardModel
import ymsli.com.cmsg.utils.MessageTypeEnum
import ymsli.com.cmsg.views.pendingsms.list.PendingMsgListActivity
import ymsli.com.cmsg.views.sentsms.dashboard.SentDashboardActivity

class SentMessageDashboardViewHolder(parent: ViewGroup,private val itemBinding: ActivityDashboardListItemBinding,private var isCalledFromSentDashboard: Boolean)
    : BaseItemViewHolder<SentMessageDashboardModel, SentMessageDashboardItemViewModel>(R.layout.activity_dashboard_list_item,parent,itemBinding) {

    override fun injectDependencies(viewHolderComponent: ViewHolderComponent) = viewHolderComponent.inject(this)

    override fun setupView(view: View) {
        
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.carrierName.observe(this,{
            itemBinding.tvSimName.text = it.uppercase()
            handleItemClick()
        })

        viewModel.totalMessages.observe(this,{
            itemBinding.countTvTotal.text = it
        })

        viewModel.deliveredMessages.observe(this,{
            itemBinding.countTvDelivered.text = it
        })

        viewModel.undeliveredMessages.observe(this,{
            itemBinding.countTvUndelivered.text = it
        })

    }

    private fun handleItemClick() {
        if (!isCalledFromSentDashboard) {
            itemBinding.clListItem.setOnClickListener {
                // open SentDashboardActivity
                val sentIntent = Intent(itemView.context, SentDashboardActivity::class.java)
                itemView.context.startActivity(sentIntent)
            }
        } else {
            itemBinding.deliveredConstLayout.setOnClickListener {
                val listIntent = Intent(itemView.context, PendingMsgListActivity::class.java)
                listIntent.putExtra(Constants.READ_ONLY_MESSAGES_INTENT, true)
                listIntent.putExtra(Constants.MESSAGE_TYPE_INTENT,MessageTypeEnum.SENT_API.messageType)
                listIntent.putExtra(Constants.PROVIDER_TYPE_INTENT, ((viewModel.data.value)?.carrierName)?.uppercase())
                itemView.context.startActivity(listIntent)
            }

            itemBinding.undeliveredConstLayout.setOnClickListener {
                val listIntent = Intent(itemView.context, PendingMsgListActivity::class.java)
                listIntent.putExtra(Constants.READ_ONLY_MESSAGES_INTENT, false)
                listIntent.putExtra(Constants.MESSAGE_TYPE_INTENT, MessageTypeEnum.NOT_DELIVERED.messageType)
                listIntent.putExtra(Constants.PROVIDER_TYPE_INTENT, ((viewModel.data.value)?.carrierName)?.uppercase())
                itemView.context.startActivity(listIntent)
            }
        }
    }
}