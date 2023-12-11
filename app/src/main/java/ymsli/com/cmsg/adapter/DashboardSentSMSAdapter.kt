package ymsli.com.cmsg.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ymsli.com.cmsg.R
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.databinding.ActivityDashboardListItemBinding
import ymsli.com.cmsg.databinding.ActivitySentDashboardListItemBinding
import ymsli.com.cmsg.databinding.MessageListItemBinding


/*
 * Project Name : CMSg App
 * @company YMSLI
 * @author  Hitesh (VE00YM128)
 * @date    17/01/2022 09:00 PM
 * Copyright (c) 2020, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * YourBikesAdapter : Recycler Adapter for the DashboardSentSMSAdapter screen.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

class DashboardSentSMSAdapter(
    private var dataSet: List<MessageEntity>
) : RecyclerView.Adapter<DashboardSentSMSAdapter.ViewHolder>() {

    companion object {
        const val NO_BIKE_SELECTED = -1
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val viewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_sent_dashboard_list_item, parent, false)
        return ViewHolder.create(viewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        val v = holder.itemView
        holder.bindItems(currentItem)


    }

    override fun getItemCount() = dataSet.size

    class ViewHolder private constructor(private val binding: ActivitySentDashboardListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems(item: MessageEntity) {
         //   binding.txtServiceRequestId.text= item.msgId
         //   binding.
//            binding.tvOrderNo.text = "Order Number :"+item.orderNo
//            binding.tvContactName.text = item.receiverName
//            binding.tvPhoneNo.text = item.contactNo
//            binding.tvSmsContent.text = item.messageBody
//            binding.tvDatetime.text = item.msgId
//
//            binding.tvOrderNo.setOnClickListener { }
        }


        companion object {
            fun create(v: View): ViewHolder {
                val binding = ActivitySentDashboardListItemBinding.inflate(LayoutInflater.from(v.context))
                return ViewHolder(binding)
            }
        }
    }

    interface BikeSelectionListener {
        fun selectedBike(chassisNumber: String)
    }
}