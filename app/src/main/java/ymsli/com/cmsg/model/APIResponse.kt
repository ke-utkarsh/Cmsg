package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

/**
 * Project Name : Couriemate
 * @company YMSLI
 * @author  Balraj VE00YM023
 * @date   Oct 15, 2019
 * Copyright (c) 2019, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * APIResponse : This data class is used as the response of login API
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
data class APIResponse (
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("responseCode")
    val responseCode: Int? = null,
    @SerializedName("responseData")
    val responseData: SuccessfullLoginDTO? = null
)
