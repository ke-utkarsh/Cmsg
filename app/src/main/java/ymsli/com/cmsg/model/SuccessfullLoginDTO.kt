package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

/**
 * Project Name : Couriemate
 * @company YMSLI
 * @author  Balraj VE00YM023
 * @date   Dec 3, 2019
 * Copyright (c) 2019, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * SuccessfullLoginDTO : this class contains the user information part in the response
 *                       packet of login API.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
class SuccessfullLoginDTO (
    @SerializedName("userName")
    var userName: String,
    @SerializedName("role")
    var role: String)