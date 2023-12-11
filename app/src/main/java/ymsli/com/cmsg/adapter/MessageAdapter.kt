package ymsli.com.cmsg.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import ymsli.com.cmsg.base.BaseAdapter
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.databinding.PendingListItemBinding
import ymsli.com.cmsg.utils.SelectionHandler
import ymsli.com.cmsg.viewholder.MessageViewHolder
import ymsli.com.cmsg.views.tasklist.drag.ItemTouchHelperAdapter

class MessageAdapter(private var parentLifeCycle: Lifecycle,messageList: ArrayList<MessageEntity>,private val selectAll: Boolean,
private val selectedMessagesListener:SelectedMessagesListener,private var supportSelection: Boolean)
    : BaseAdapter<MessageEntity, MessageViewHolder>(parentLifeCycle,messageList),
    ItemTouchHelperAdapter {

    //var selectionHandler = SelectionHandler(this)
    private var selectedMessages: ArrayList<MessageEntity> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = PendingListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        MessageViewHolder.selectAll = selectAll
        return MessageViewHolder(parent,binding)

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        setListeners(dataList[position],holder)
        super.onBindViewHolder(holder, position)
    }

    /**
     * Set the list item click listeners.
     * If multi selection mode is on then simply add clicked item to selected items.
     * otherwise move the user to appropriate next screen( TaskDetailActivity, ReturnTaskDetailActivity)
     */
    private fun setListeners(task: MessageEntity, viewHolder: MessageViewHolder){
        viewHolder.itemView.setOnClickListener {
           /* val act = selectionHandler.isSelected(viewHolder.adapterPosition)//tells about current state
            if(selectionHandler.selectionMode){
                selectionHandler.onClick(viewHolder)
                if(!act){
                    selectedMessages.add(task)
                    selectedMessagesListener.getSelectedMessages(selectedMessages)
                }
                else if(act){
                    selectedMessages.remove(task)
                    selectedMessagesListener.getSelectedMessages(selectedMessages)
                }
            } else {
                // open message detail activity
                selectedMessagesListener.startMessageDetail(task)
            }*/
        }
        if(supportSelection) {
            viewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    //selectionHandler.onLongClick(viewHolder)
                    selectedMessages.clear()
                    selectedMessages.add(task)
                    selectedMessagesListener.getSelectedMessages(selectedMessages)
                    return true
                }
            })
        }
    }

    /**
     * This function is used to support the drag and drop feature
     */
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val prev = dataList.removeAt(fromPosition)
        dataList.add(if(toPosition > fromPosition) toPosition -1 else toPosition, prev)
        notifyItemMoved(fromPosition, toPosition)
        //Utils.sortedAssingedTasks = dataList
    }

    override fun onItemDismiss(position: Int) {

    }

    interface SelectedMessagesListener{
        fun getSelectedMessages(messages: List<MessageEntity>)
        fun startMessageDetail(message: MessageEntity)
    }
}