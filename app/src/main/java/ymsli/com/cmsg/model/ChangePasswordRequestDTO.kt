package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName


/**
 * Project Name : CMsg
 * @company YMSLI
 * @date   Feb 15, 2022
 * Copyright (c) 2022, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * ChangePasswordRequestDTO : Used for the change password API request
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
data class ChangePasswordRequestDTO (
    @SerializedName("currentPassword")
    val currentPassword: String? = null,
    @SerializedName("newPassword")
    val newPassword: String? = null,
    @SerializedName("passwordResetFlag")
    val passwordResetFlag: Boolean? = null,
    @SerializedName("source")
    val source: String? = null,
    @SerializedName("timezoneOffset")
    val timezoneOffset: String? = null,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("userName")
    val userName: String? = null
) 
