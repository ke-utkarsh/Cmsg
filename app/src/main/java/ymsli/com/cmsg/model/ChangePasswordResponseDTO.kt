package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

/**
 * Project Name : CMsg
 * @company YMSLI
 * @author  Sushant
 * @date   Feb 15, 2022
 * Copyright (c) 2022, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * ChangePasswordResponseDTO : Used for the change password API response
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
data class ChangePasswordResponseDTO (
    @SerializedName("passwordUpdated")
    val passwordUpdated: Boolean? = null
)