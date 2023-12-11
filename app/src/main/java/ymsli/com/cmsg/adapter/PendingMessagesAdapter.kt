package ymsli.com.cmsg.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ymsli.com.cmsg.R
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.databinding.MessageListItemBinding
import ymsli.com.cmsg.databinding.PendingListItemBinding


/*
 * Project Name : CMsg App
 * @company YMSLI
 * @author  Hitesh (VE00YM128)
 * @date    27/01/2022 09:00 PM
 * Copyright (c) 2020, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * YourBikesAdapter : Recycler Adapter for the Pending Messages screen.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

class PendingMessagesAdapter(
    private var dataSet: List<MessageEntity>
) : RecyclerView.Adapter<PendingMessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val viewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.pending_list_item, parent, false)
        return ViewHolder.create(viewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        val v = holder.itemView
        holder.bindItems(currentItem)


    }

    override fun getItemCount() = dataSet.size

    class ViewHolder private constructor(private val binding: PendingListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems(item: MessageEntity) {
            binding.tvOrderNo.text = "Order Number :"+item.orderId
            binding.tvContactName.text = item.receiverName
            binding.tvPhoneNo.text = item.receiverMobileNo
            binding.tvSmsContent.text = item.messageBody
         //   binding.tvDatetime.text = item.msgId

            binding.tvOrderNo.setOnClickListener { }
        }


        companion object {
            fun create(v: View): ViewHolder {
                val binding = PendingListItemBinding.inflate(LayoutInflater.from(v.context))
                return ViewHolder(binding)
            }
        }
    }
}