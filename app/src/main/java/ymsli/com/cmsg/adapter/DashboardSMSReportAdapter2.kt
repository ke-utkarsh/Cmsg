package ymsli.com.cmsg.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ymsli.com.cmsg.R
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.databinding.ActivityDashboardListItemBinding
import ymsli.com.cmsg.databinding.MessageListItemBinding


/*
 * Project Name : CMsg App
 * @company YMSLI
 * @author  Hitesh (VE00YM128)
 * @date    12/01/2022 09:00 PM
 * Copyright (c) 2020, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * DashboardSMSReportAdapter : Recycler Adapter for the Dashboard Report screen.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

class DashboardSMSReportAdapter2(
    private var dataSet: List<MessageEntity>
) : RecyclerView.Adapter<DashboardSMSReportAdapter2.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val viewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_dashboard_list_item, parent, false)
        return ViewHolder.create(viewHolder,dataSet)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        val v = holder.itemView
        holder.bindItems(currentItem)


    }

    override fun getItemCount() = dataSet.size

    class ViewHolder private constructor(private val binding: ActivityDashboardListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItems(item: MessageEntity) {
            binding.countTvTotal.text = messages.size.toString()
            val delivered = (messages.forEach { it.messageStatus.equals("sent",true) }) as List<MessageEntity>
            binding.countTvDelivered.text = delivered.size.toString()
            binding.countTvUndelivered.text = (messages.size - delivered.size).toString()
            binding.tvSimName.text = item.serviceProvider
        }


        companion object {
            lateinit var messages: List<MessageEntity>
            fun create(v: View,dataSet: List<MessageEntity>): ViewHolder {
                messages = dataSet
                val binding = ActivityDashboardListItemBinding.inflate(LayoutInflater.from(v.context))
                return ViewHolder(binding)
            }
        }
    }

}