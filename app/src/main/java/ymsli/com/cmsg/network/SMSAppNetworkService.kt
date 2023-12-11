package ymsli.com.cmsg.network

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.database.entity.MessageEntityResponseModel
import ymsli.com.cmsg.model.*
import javax.inject.Singleton

@Singleton
interface SMSAppNetworkService {

    @GET("appversion/getstatus")
    fun fetchAppStatusAndApiInfo(@Query("appVersion") appVersion:String): Single<Any>

    @GET("sms/fetch-pending-msgs")
    fun getPendingMessages(): Single<MessageEntityResponseModel>

    @Headers("Accept: */*")
    @POST("login")
    fun doLogin(@Body user: UserMaster):Single<Response<APIResponse>>

    @GET("user/usermanagement/get-user-info")
    fun userInfo(@Query("userId") userId:Long):Single<List<UserMaster>>

    @GET("common/get-company-details")
    fun getCompanyDetails():Single<CompanyDetails>

    @GET("fetch-service-provider")
    fun getServiceProvider():Single<ServiceProviderResponse>

    @GET("sms/fetch-sent-msgs")
    fun getSentMessagesProvider(@Query("fromDayCount") numDays: Int):Single<MessageEntityResponseModel>

    @Headers("Content-Type: application/json")
    @POST("sms/delete-msgs")
    fun deleteMessages(@Body deleteMessageRequestModel: EditMessageRequestModel,@Header("Authorization") jwtToken : String?): Single<DeleteMessageResponseModel>

    @Headers("Content-Type: application/json")
    @POST("sms/update-msg-status")
    fun updateMessages(@Body updateMessageRequestModel: EditMessageRequestModel,@Header("Authorization") jwtToken : String?) : Single<DeleteMessageResponseModel>

    @PUT("user/usermanagement/change-password")
    fun changePassword(@Body changePasswordRequestRequest: ChangePasswordRequestDTO,
                       @Header("Authorization") jwtToken : String?)
            : Single<ChangePasswordResponseDTO>

    @Headers("Content-Type: application/json")
    @POST("user/usermanagement/update-user-profile")
    fun getUserProfile(@Body userProfileRequestModel: UserProfileRequestModel,@Header("Authorization") jwtToken : String?): Single<UserProfileResponseModel>

    @POST("user/create-otp")
    fun triggerOTP(@Body forgotPasswordRequestModel: ForgotPasswordRequestModel): Single<ForgotPasswordResponseModel>

    @POST("user/verify-otp")
    fun verifyOTP(@Body verifyOTPRequestModel: VerifyOTPRequestModel): Single<VerifyOTPResponseModel>

    @POST("user/reset-password")
    fun resetPassword(@Body resetPasswordRequestModel: ResetPasswordRequestModel): Single<ResetPasswordResponseModel>

}