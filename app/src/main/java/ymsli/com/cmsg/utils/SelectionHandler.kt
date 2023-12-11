package ymsli.com.cmsg.utils

import androidx.recyclerview.widget.RecyclerView
import ymsli.com.cmsg.adapter.MessageListAdapter
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.database.entity.MessageListItem

/**
 * This class handles the multiple select functionality for the RV
 */
class SelectionHandler(private val adapter: MessageListAdapter){
    var selection = HashMap<Int, RecyclerView.ViewHolder>()
        private set
    var selectionMode = false
        private set
    var isActive = true
    var observer = object: Observer {
        override fun onSelectionModeOn() {}
        override fun onSelectionModeOff() {}
    }

    fun isSelected(position: Int) = selection.containsKey(position)

    fun clearSelection(){
        selectionMode = false
        selection.clear()
        adapter.notifyDataSetChanged()
        observer.onSelectionModeOff()
    }

    fun getSelectedTasks(): List<MessageEntity>{
        var taskList = ArrayList<MessageEntity>()
        for(holder in selection.values){
            taskList.add(adapter.messageItems[holder.adapterPosition] as MessageEntity)
        }
        return taskList
    }

    fun onClick(viewHolder: RecyclerView.ViewHolder){
        var itemView = viewHolder.itemView
        if((selectionMode) && (isClickable(viewHolder))){
            val messageViewHolder = (viewHolder as MessageListAdapter.MessageListViewHolder)
            var select = !itemView.isActivated
            if(select){
                itemView.isActivated = true
                selection[viewHolder.adapterPosition] = viewHolder
                viewHolder.bind(adapter.messageItems[viewHolder.adapterPosition] as MessageEntity,true)
            }
            else{
                itemView.isActivated = false
                selection.remove(viewHolder.adapterPosition)
                viewHolder.bind(adapter.messageItems[viewHolder.adapterPosition] as MessageEntity,false)
            }
            if(selection.isEmpty()) {
                selectionMode = false
                observer.onSelectionModeOff()
            }
        }
    }

    fun onLongClick(viewHolder: MessageListAdapter.MessageListViewHolder):Boolean{
        if((isActive) && (!selectionMode) && (isClickable(viewHolder))){
            selectionMode = true
            adapter.selectedMessages.add(getMessage(viewHolder))
            viewHolder.itemView.isActivated = true
            selection[viewHolder.adapterPosition] = viewHolder
            viewHolder.bind(adapter.messageItems[viewHolder.adapterPosition] as MessageEntity,true)
            observer.onSelectionModeOn()
        }
        return true
    }

    private fun isClickable(viewHolder: RecyclerView.ViewHolder): Boolean{
        val task = adapter.messageItems[viewHolder.adapterPosition]
        return task is MessageEntity
     }

    private fun getMessage(viewHolder: RecyclerView.ViewHolder): MessageEntity{
        val task = adapter.messageItems[viewHolder.adapterPosition]
        return task as MessageEntity
    }

    interface Observer{
        fun onSelectionModeOn()
        fun onSelectionModeOff()
    }
}