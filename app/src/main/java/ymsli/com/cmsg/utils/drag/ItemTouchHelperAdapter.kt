package ymsli.com.cmsg.views.tasklist.drag

/**
 * Project Name : CMSG
 * @company YMSLI
 * @author  Sushant Somani
 * @date   Feb 3, 2022
 * Copyright (c) 2022, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -------------------------------------------------------------------------------------
 * ItemTouchHelperAdapter : Adapter used for the drag and drop feature on the assigned
 *                          tasks list.
 * -------------------------------------------------------------------------------------
 * Revision History
 * -------------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -------------------------------------------------------------------------------------
 */
interface ItemTouchHelperAdapter {

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and not at the end of a "drop" event.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then end position of the moved item.
     */
    fun onItemMove(fromPosition: Int, toPosition: Int)

    /**
     * Called when an item has been dismissed by a swipe.
     * @param position The position of the item dismissed.
     */
    fun onItemDismiss(position: Int)
}