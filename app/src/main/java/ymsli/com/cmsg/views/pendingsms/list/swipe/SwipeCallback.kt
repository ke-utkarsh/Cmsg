package ymsli.com.couriemate.views.tasklist.swipe

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeCallback(private val swipeDirection: Int, private var backgroundColor: Int,
                             private val backgroundIcon: Drawable, private val swipeable: Swipeable
):
    ItemTouchHelper.SimpleCallback(0, swipeDirection) {

    private val intrinsicWidth = backgroundIcon?.intrinsicWidth
    private val intrinsicHeight = backgroundIcon?.intrinsicHeight
    private val background = ColorDrawable()
    private val coordinate = IntArray(8)
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    /** This property can be used by the consumers of this implementation to disable the
     *  swipe feature at any time.
     */
    var isSwipable = true

    override fun isItemViewSwipeEnabled(): Boolean {
        return isSwipable
    }

    /**
     * Builds the swipe directions for a particular item
     * @author Balraj VE00YM023
     */
    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if(swipeable.isSwipeable(viewHolder)){
            super.getSwipeDirs(recyclerView, viewHolder)
        } else{
            0
        }
    }

    /**
     * This implementation doesn't provide move feature.
     * @author Balraj VE00YM023
     */
    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        return false
    }

    /**
     * This function is responsible for drawing the background color when any item in
     * recycler view is swiped left or right.
     *
     * @author Balraj VE00YM023
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        itemView.isLongClickable = false
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            itemView.isLongClickable = true
            return
        }

        /** If swipe direction is left then draw on the left side */
        if(swipeDirection== ItemTouchHelper.LEFT){
            coordinate[BACKGROUND_LEFT]   = itemView.right + dX.toInt()
            coordinate[BACKGROUND_TOP]    = itemView.top
            coordinate[BACKGROUND_RIGHT]  = itemView.right
            coordinate[BACKGROUND_BOTTOM] = itemView.bottom

            val backgroundIconMargin = (itemHeight - intrinsicHeight) / 2
            coordinate[BACKGROUND_ICON_TOP] = itemView.top + (itemHeight - intrinsicHeight!!) / 2
            coordinate[BACKGROUND_ICON_LEFT] = itemView.right - backgroundIconMargin - intrinsicWidth!!
            coordinate[BACKGROUND_ICON_RIGHT] = itemView.right - backgroundIconMargin
            coordinate[BACKGROUND_ICON_BOTTOM] = coordinate[BACKGROUND_ICON_TOP] + intrinsicHeight
        }
        else{
            coordinate[BACKGROUND_LEFT]   = itemView.left
            coordinate[BACKGROUND_TOP]    = itemView.top
            coordinate[BACKGROUND_RIGHT]  = itemView.left+dX.toInt()
            coordinate[BACKGROUND_BOTTOM] = itemView.bottom

            val backgroundIconMargin = (itemHeight - intrinsicHeight) / 2
            coordinate[BACKGROUND_ICON_TOP] = itemView.top + (itemHeight - intrinsicHeight!!) / 2
            coordinate[BACKGROUND_ICON_LEFT] = itemView.left + backgroundIconMargin
            coordinate[BACKGROUND_ICON_RIGHT] = itemView.left + backgroundIconMargin + intrinsicWidth!!
            coordinate[BACKGROUND_ICON_BOTTOM] = coordinate[BACKGROUND_ICON_TOP] + intrinsicHeight
        }

        background.color = backgroundColor
        background.setBounds(coordinate[BACKGROUND_LEFT], coordinate[BACKGROUND_TOP],
            coordinate[BACKGROUND_RIGHT], coordinate[BACKGROUND_BOTTOM])
        background.draw(c)

        backgroundIcon!!.setBounds(coordinate[BACKGROUND_ICON_LEFT], coordinate[BACKGROUND_ICON_TOP],
            coordinate[BACKGROUND_ICON_RIGHT], coordinate[BACKGROUND_ICON_BOTTOM])
        backgroundIcon.draw(c)
        itemView.isLongClickable = true
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

    companion object{
        const val BACKGROUND_ICON_LEFT = 0
        const val BACKGROUND_ICON_TOP = 1
        const val BACKGROUND_ICON_RIGHT = 2
        const val BACKGROUND_ICON_BOTTOM = 3
        const val BACKGROUND_LEFT = 4
        const val BACKGROUND_TOP = 5
        const val BACKGROUND_RIGHT = 6
        const val BACKGROUND_BOTTOM = 7
    }

    interface Swipeable{
        fun isSwipeable(viewHolder: RecyclerView.ViewHolder) : Boolean
    }
}