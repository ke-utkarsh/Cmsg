package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

/**
 * Project Name : Couriemate
 * @company YMSLI
 * @author  Balraj VE00YM023
 * @date   Jan 13, 2020
 * Copyright (c) 2020, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * CompanyDetails : Used as the response of company details API
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
data class CompanyDetails(
    @SerializedName("contactNo")
    val contactNumber : String,

    @SerializedName("address")
    val address: String,

    @SerializedName("defaultMsgToCustomer")
    val defaultMsgToCustomer: String?,

    @SerializedName("companyCode")
    val companyCode: String
)