/**
 * couriermate-API
 * couriermate-API
 *
 * OpenAPI spec version: 0.0.1-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
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
 * UserMaster : Response model for the user Info API.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
data class UserMaster (
    @SerializedName("createdBy")
    val createdBy: kotlin.String? = null,

    @SerializedName("createdOn")
    val createdOn: java.sql.Timestamp? = null,

    @SerializedName("description")
    val description: kotlin.String? = null,

    @SerializedName("deviceToken")
    val deviceToken: kotlin.String? = null,

    @SerializedName("deviceType")
    val deviceType: kotlin.String? = null,

    @SerializedName("displayName")
    val displayName: kotlin.String? = null,

    @SerializedName("driverId")
    val driverId: kotlin.Int? = null,

    @SerializedName("email")
    val email: kotlin.String? = null,

    @SerializedName("isDeleted")
    val isDeleted: kotlin.Boolean? = null,

    @SerializedName("mobileNo")
    val mobileNo: kotlin.String? = null,

    @SerializedName("password")
    var password: kotlin.String? = null,

    @SerializedName("roleId")
    val roleId: kotlin.Int? = null,

    @SerializedName("updatedBy")
    val updatedBy: kotlin.String? = null,

    @SerializedName("updatedOn")
    val updatedOn: java.sql.Timestamp? = null,

    @SerializedName("userId")
    val userId: kotlin.Long? = null,

    @SerializedName("userType")
    val userType: kotlin.String? = null,

    @SerializedName("username")
    var username: kotlin.String? = null,

    @SerializedName("ccuId")
    val ccuId:String?=null,

    @SerializedName("chatUserName")
    val chatUserName: kotlin.String? = null,

    @SerializedName("userImageString")
    val userImageString: String? = null,

    @SerializedName("chatUserPassword")
    var chatUserPassword: kotlin.String? = null,

    @SerializedName("chatGroupName")
    val chatGroupName:String?=null,

    @SerializedName("chatDomainName")
    val chatDomainName: String?=null,

    @SerializedName("chatHistorySize")
    val chatHistorySize: Int?=null
)

