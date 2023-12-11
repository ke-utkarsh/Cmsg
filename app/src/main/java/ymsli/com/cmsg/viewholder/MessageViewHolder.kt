package ymsli.com.cmsg.viewholder

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import com.google.android.material.shape.CornerFamily
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseItemViewHolder
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.databinding.PendingListItemBinding
import ymsli.com.cmsg.di.component.ViewHolderComponent
import ymsli.com.cmsg.itemviewmodel.MessageItemViewModel
import ymsli.com.cmsg.views.messagedetail.MessageDetailsActivity

class MessageViewHolder(parent: ViewGroup,private val pendingListItemBinding: PendingListItemBinding)
    :BaseItemViewHolder<MessageEntity,MessageItemViewModel>(R.layout.pending_list_item,parent,pendingListItemBinding){

    companion object{
        var selectAll: Boolean = false
    }

    override fun injectDependencies(viewHolderComponent: ViewHolderComponent) = viewHolderComponent.inject(this)

    override fun setupView(view: View) {
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.contactNo.observe(this,{
            val number  = viewModel.getShowReceiverNumber()
            if(!number){
                pendingListItemBinding.tvPhoneNo.visibility = View.GONE
            }
            else if(pendingListItemBinding.tvPhoneNo.isVisible) {
                pendingListItemBinding.tvPhoneNo.text = it
            }
        })

        viewModel.receiverName.observe(this,{
            val name  = viewModel.getShowReceiverName()
            if(!name){
                pendingListItemBinding.ivContact.visibility = View.GONE
                pendingListItemBinding.tvContactName.visibility = View.GONE
            }
            else pendingListItemBinding.tvContactName.text = it
        })

        viewModel.messageBody.observe(this,{
            val msg  = viewModel.getShowReceiverMessage()
            if(!msg){
                pendingListItemBinding.ivSms.visibility = View.GONE
                pendingListItemBinding.tvSmsContent.visibility = View.GONE
            }
            else pendingListItemBinding.tvSmsContent.text = it
        })

        viewModel.orderNo.observe(this,{
            pendingListItemBinding.tvOrderNo.text = "${itemView.context.resources.getString(R.string.order_number_label)}${it}"
            if (selectAll) {
                // make border color green
                pendingListItemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.color_primary_light))
            }
        })
    }

    /**
     * Bind the recycler item, if this item is activated by the multi selection mode
     * then set the background of this item to grey.
     */
    fun bind(data: MessageEntity, isActivated: Boolean) {
        super.bind(data,0)
        initializeRecyclerView(data)
        itemView.isActivated = isActivated
        if(isActivated){
            pendingListItemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.color_primary_light))
        }
        else{
            pendingListItemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.white))
        }
    }

    /**
     * Load the data in recycler view on the initialization.
     */
    private fun initializeRecyclerView(data: MessageEntity){
/*        var taskStatus = TaskStatus.getEnumById(data.taskStatusId!!)
        itemView.text_task_list_order_no.text = data.orderNo
        itemView.task_list_text_updatedOn.text = if(data.updatedOn == null) {
            Utils.formatDateForListItem(data.createdOn!!)
        } else {
            Utils.formatDateForListItem(data.updatedOn!!)
        }
        itemView.task_list_text_taskStatus.text = taskStatus!!.name
        itemView.task_list_text_taskStatus.setTextColor(Color.parseColor(TaskStatus.getEnumById(data.taskStatusId!!)?.color))
        itemView.task_list_circle.setBackgroundColor(Color.parseColor(TaskStatus.getEnumById(data.taskStatusId!!)?.color))
        setDateByTaskStatusId(taskStatus, data)
        setAddressByTaskStatusId(taskStatus, data)*/
    }
}