package ymsli.com.cmsg.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ymsli.com.cmsg.R
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.database.entity.MessageHeader
import ymsli.com.cmsg.database.entity.MessageListItem
import ymsli.com.cmsg.databinding.ItemGroupBinding
import ymsli.com.cmsg.databinding.PendingListItemBinding
import ymsli.com.cmsg.utils.FilterDateEnum
import ymsli.com.cmsg.utils.SelectionHandler
import ymsli.com.cmsg.views.tasklist.drag.ItemTouchHelperAdapter
import java.lang.IllegalStateException
import kotlin.collections.ArrayList

class MessageListAdapter(var messageItems: ArrayList<MessageListItem>,private var supportSelection: Boolean,
                         private val selectedMessagesListener: MessageListAdapter.SelectedMessagesListener,
                         private val selectAll: Boolean
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),ItemTouchHelperAdapter {

    var selectionHandler = SelectionHandler(this)
    var selectedMessages: ArrayList<MessageEntity> = ArrayList()

    override fun getItemViewType(position: Int): Int {
        return messageItems.get(position).getType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if(viewType==MessageListItem.TYPE_HEADER){
            val headerBinding = ItemGroupBinding.inflate(layoutInflater,parent,false)
            return ClassificationViewHolder(headerBinding)
        }
        else if(viewType==MessageListItem.TYPE_MESSAGE){
            val itemBinding = PendingListItemBinding.inflate(layoutInflater, parent, false)
            return MessageListViewHolder(itemBinding,selectAll)
        }
        else throw IllegalStateException("Item type is not supported!!")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setListeners(messageItems[position],holder)
        val viewType = getItemViewType(position)
        if(viewType==MessageListItem.TYPE_HEADER){
            val header = messageItems.get(position) as MessageHeader
            val viewHolder = holder as ClassificationViewHolder
            when(header.getHeaderType()){
                FilterDateEnum.TODAY.code -> {
                    viewHolder.bind("TODAY")
                }
                FilterDateEnum.YESTERDAY.code -> {
                    viewHolder.bind("YESTERDAY")
                }
                FilterDateEnum.OLDER.code -> {
                    viewHolder.bind("OLDER")
                }
            }

        }
        else if(viewType==MessageListItem.TYPE_MESSAGE){
            val msg = messageItems.get(position) as MessageEntity
            val viewHolder = holder as MessageListViewHolder
            if(selectionHandler.isSelected(viewHolder.adapterPosition)){
                viewHolder.bind(msg,true)
            }else{
                viewHolder.bind(msg,false)
            }
        }
        else throw IllegalStateException("Item type is not supported!!")
    }

    override fun getItemCount(): Int {
        return messageItems.size
    }


    class MessageListViewHolder(private var pendingListItemBinding: PendingListItemBinding,private var selectAll: Boolean):RecyclerView.ViewHolder(pendingListItemBinding.root){
        /**
         * Bind the recycler item, if this item is activated by the multi selection mode
         * then set the background of this item to green.
         */
        fun bind(data: MessageEntity, isActivated: Boolean) {
            //super.bind(data,0)
            itemView.isActivated = isActivated
            if(isActivated || selectAll){
                pendingListItemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.color_primary_light))
            }
            else{
                pendingListItemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.white))
            }
            pendingListItemBinding.tvOrderNo.text = data.orderNo
            pendingListItemBinding.tvContactName.text = data.receiverName
            pendingListItemBinding.tvSmsContent.text = data.messageBody
            pendingListItemBinding.tvPhoneNo.text = data.receiverMobileNo
        }
    }

    class ClassificationViewHolder(private var itemGroupBinding: ItemGroupBinding): RecyclerView.ViewHolder(itemGroupBinding.root){
        fun bind(classification: String){
            itemGroupBinding.tvHeader.text = classification
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Log.d("Item","Moved")
    }

    override fun onItemDismiss(position: Int) {
        Log.d("Item","Dismissed")
    }

    /**
     * Set the list item click listeners.
     * If multi selection mode is on then simply add clicked item to selected items.
     * otherwise move the user to appropriate next screen( TaskDetailActivity, ReturnTaskDetailActivity)
     */
    private fun setListeners(item: MessageListItem, viewHolder: RecyclerView.ViewHolder){
        if(viewHolder is MessageListViewHolder){
            // handle click events on messages
            viewHolder.itemView.setOnClickListener {
                val currentState = selectionHandler.isSelected(viewHolder.adapterPosition)
                if(selectionHandler.selectionMode){
                    selectionHandler.onClick(viewHolder)
                    if(!currentState){
                        selectedMessages.add(item as MessageEntity)
                        selectedMessagesListener.getSelectedMessages(selectedMessages)
                    }
                    else{
                        selectedMessages.remove(item)
                        selectedMessagesListener.getSelectedMessages(selectedMessages)
                    }
                }
                else{
                    // open message detail activity
                    selectedMessagesListener.startMessageDetail(item as MessageEntity)
                }

            }
            viewHolder.itemView.setOnLongClickListener {
                selectionHandler.onLongClick(viewHolder)
                //selectedMessages.clear()
                selectedMessagesListener.getSelectedMessages(selectedMessages)
                true
            }
            /*if(supportSelection){

            }*/
        }
    }

    interface SelectedMessagesListener{
        fun getSelectedMessages(messages: List<MessageEntity>)
        fun startMessageDetail(message: MessageEntity)
    }
}