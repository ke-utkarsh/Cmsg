package ymsli.com.cmsg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import ymsli.com.cmsg.base.BaseAdapter
import ymsli.com.cmsg.databinding.ActivityDashboardListItemBinding
import ymsli.com.cmsg.model.SentMessageDashboardModel
import ymsli.com.cmsg.viewholder.SentMessageDashboardViewHolder

class SentMessageDashboardAdapter(private var parentLifeCycle: Lifecycle, messageList: ArrayList<SentMessageDashboardModel>,
private var isCalledFromSentDashboard: Boolean)
    : BaseAdapter<SentMessageDashboardModel, SentMessageDashboardViewHolder>(parentLifeCycle,messageList){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SentMessageDashboardViewHolder {
        val binding = ActivityDashboardListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SentMessageDashboardViewHolder(parent,binding,isCalledFromSentDashboard)
    }
}