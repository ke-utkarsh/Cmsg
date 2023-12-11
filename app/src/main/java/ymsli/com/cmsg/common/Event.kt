package ymsli.com.cmsg.common

/**
 * Project Name : CMSG
 * @company  YMSLI
 * @author   VE00YM023
 * @date     October 21, 2021
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * Event : Allows only a single lookup in event style.
 *         only returns the enclosed data for first lookup after that returns null
 *
 * T : Type of enclosed data
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
class Event<T>(private val data: T) {
    private var handled = false

    /**
     * Returns data if this is the first call to this function,
     * otherwise returns null.
     */
    fun getIfNotHandled() = if(handled) { null } else { handled = true; data }
}