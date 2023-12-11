package ymsli.com.couriemate.views.tasklist.swipe

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import ymsli.com.cmsg.R
import ymsli.com.cmsg.adapter.MessageAdapter
import ymsli.com.cmsg.adapter.MessageListAdapter
import ymsli.com.cmsg.adapter.PendingMessagesAdapter
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.database.entity.MessageListItem
import ymsli.com.cmsg.database.entity.SMSAppEntity

class SwipeHandler(private val context: Context,
                   private val recyclerView: RecyclerView,
                   private val recyclerAdapter: MessageListAdapter
){

    private lateinit var leftSwipeCallback: SwipeCallback
    private lateinit var rightSwipeCallback: SwipeCallback
    var leftSwipeAction: SwipeAction? = null
    var rightSwipeAction: SwipeAction? = null
    private lateinit var task : MessageListItem

    init{
        configureSwipeCallbacks()
    }

    /**
     * Configure the left and right swipe handlers and attach them to the given recycler view.
     */
    private fun configureSwipeCallbacks(){
        /** Configure the left swipe for the call feature */
        leftSwipeCallback = getSwipeCallback(
            ItemTouchHelper.LEFT, Color.parseColor("#DC3545"),
            ContextCompat.getDrawable(context, R.drawable.icon_delete)!!,
            object : SwipeCallback.Swipeable {
                override fun isSwipeable(viewHolder: RecyclerView.ViewHolder): Boolean {
                    return viewHolder.itemViewType==MessageListItem.TYPE_MESSAGE
                }
            })
        val itemTouchHelper = ItemTouchHelper(leftSwipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        /** Configure the right swipe for the task pickup feature */
        rightSwipeCallback = getSwipeCallback(
            ItemTouchHelper.RIGHT, Color.parseColor("#007565"),
            ContextCompat.getDrawable(context, R.drawable.icon_msg_swipe)!!,
            object: SwipeCallback.Swipeable {
                /** Only allow the swipe feature on the items with task status assigned */
                override fun isSwipeable(viewHolder: RecyclerView.ViewHolder): Boolean {
                    return viewHolder.itemViewType==MessageListItem.TYPE_MESSAGE
                }
            })

        val itemPickHelper = ItemTouchHelper(rightSwipeCallback)
        itemPickHelper.attachToRecyclerView(recyclerView)
    }


    /**
     * Utility function to get a swipe callback for a specific direction. (ie. left or right)
     */
    private fun getSwipeCallback(swipeDirection: Int, backgroundColor: Int,
                                 backgroundIcon: Drawable, swipeable: SwipeCallback.Swipeable
    ): SwipeCallback {
        return object: SwipeCallback(swipeDirection, backgroundColor, backgroundIcon, swipeable) {

            /**
             * When any item is swiped pass the selected item to implementing activity,
             * using the perform function of callback interface.
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val messages = recyclerAdapter.messageItems //(recyclerAdapter.messageItems.filter { it.getType()==MessageListItem.TYPE_MESSAGE } as ArrayList<MessageEntity>)
                task = messages.get(viewHolder.adapterPosition)
                if(direction== ItemTouchHelper.RIGHT){
                    leftSwipeAction?.perform(task as MessageEntity)
                    recyclerView.adapter = recyclerAdapter
                }
                else{
                    viewHolder.itemView.isClickable = false
                    rightSwipeAction?.perform(task as MessageEntity)
                    viewHolder.itemView.isClickable = true
                }
            }
        }

    }

    /********* Utility functions that can be used by the activity to enable/disable swipe *******/
    fun enableSwipe(){
        leftSwipeCallback.isSwipable = true
        rightSwipeCallback.isSwipable = true
    }

    fun disableSwipe(){
        leftSwipeCallback.isSwipable = false
        rightSwipeCallback.isSwipable = false
    }

    /**
     * This interface is used by this class to communicate with the implementing
     * activity to perform the operations when any item is swiped.
     */
    interface SwipeAction{
        fun perform(task: MessageEntity)
    }
}