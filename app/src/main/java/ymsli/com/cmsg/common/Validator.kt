package ymsli.com.cmsg.common

import ymsli.com.cmsg.R
import ymsli.com.cmsg.model.ChangePasswordValidationModel
import ymsli.com.cmsg.model.UserMaster
import ymsli.com.cmsg.utils.Resource
import java.util.regex.Pattern

/**
 * Project Name : Couriemate
 * @company YMSLI
 * @author  Sushant Somani (VE00YM129)
 * @date   January 10, 2020
 * Copyright (c) 2019, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * Validator : used for user input validation
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 *
 * -----------------------------------------------------------------------------------
 */
object Validator {
    private const val MIN_PASSWORD_LENGTH = 5
    /**
     * validates username and password
     */
    fun validateLoginFields(user: UserMaster): List<Validation> =
        ArrayList<Validation>().apply {
            when {
                user.username.isNullOrBlank() ->
                    add(
                        Validation(
                            Validation.Field.USERNAME,
                            Resource.error(R.string.username_field_empty)
                        )
                    )
                else -> add(
                    Validation(
                        Validation.Field.USERNAME,
                        Resource.success()
                    )
                )
            }
            when {
                user.password.isNullOrBlank() ->
                    add(
                        Validation(
                            Validation.Field.EMPTY_PASSWORD,
                            Resource.error(R.string.password_field_empty)
                        )
                    )
                user.password!!.length < MIN_PASSWORD_LENGTH ->
                    add(
                        Validation(
                            Validation.Field.PASSWORD,
                            Resource.error(R.string.password_field_small_length)
                        )
                    )
                else -> add(
                    Validation(
                        Validation.Field.PASSWORD,
                        Resource.success()
                    )
                )
            }
        }

    /**
     * called when both COD received and Signature fields are mandatory
     */
    fun validateCod(codExpected: Double, totalAmountReceived: Double): List<Validation> {
        val result = ArrayList<Validation>()
        when {
            totalAmountReceived < 0.0 -> {
                result.add(Validation(Validation.Field.CODAMOUNT,
                                      Resource.error(R.string.CodError)))
            }
            totalAmountReceived > codExpected -> {
                result.add(Validation(Validation.Field.CODAMOUNT,
                                       Resource.error(R.string.CodComparisonError)))
            }
            else -> result.add(Validation(Validation.Field.CODAMOUNT, Resource.success()))
        }
        return result
    }

    /**
     * validates refusal comments are entered if
     * delivery is refused
     */
    fun validateRefusedDelivery(refusalComments: String?,refusalReason: String?): List<Validation> =
        ArrayList<Validation>().apply {
            when {
                refusalComments.isNullOrEmpty() && refusalReason!!.contains("Other",true) ->
                    add(
                        Validation(
                            Validation.Field.COMMENTS,
                            Resource.error(R.string.RefusalCommentError)
                        )
                    )
                refusalComments?.trim()?.length == 0  && refusalReason!!.contains("Other",true) ->
                    add(
                        Validation(
                            Validation.Field.COMMENTS,
                            Resource.error(R.string.RefusalCommentError)
                        )
                    )
            }
        }

    /**
     * validates the inpuot parameters at
     * the time of failed delivery
     * to the end customer
     */
    fun validateFailedCustomerDelivery(
        failureReason: String?,
        failureComments: String?,
        retryDate: String?
    ): List<Validation> =
        ArrayList<Validation>().apply {
            val result = ArrayList<Validation>()
            when{
                failureReason.isNullOrBlank() -> {
                    result.add(
                        Validation(
                            Validation.Field.FAILUREREASON,
                            Resource.error(R.string.failReason)
                        )
                    )
                }
            }
            when{
                retryDate.isNullOrBlank() || retryDate.contains("null",true)-> {
                    if((failureReason!=null)&&(!failureReason.contains("Customer unreachable",true))) {
                        add(
                            Validation(
                                Validation.Field.RETRYDATE,
                                Resource.error(R.string.postponmentErrorMessage)
                            )
                        )
                    }
                    else {
                        add(
                            Validation(
                                Validation.Field.RETRYDATE,
                                Resource.success()
                            )
                        )
                    }
                }
            }
            when{
                !failureReason!!.contains("Customer unreachable",true)  -> {
                    if(failureComments.isNullOrBlank()){
                        add(
                            Validation(
                                Validation.Field.FAILUREREASON,
                                Resource.error(R.string.enter_failure_comments)
                            )
                        )
                    }
                    else {
                        add(
                            Validation(
                                Validation.Field.FAILUREREASON,
                                Resource.success()
                            )
                        )
                    }
                }
            }
        }

    /**
     * validates user input at payment registration fragment
     */
    fun validatePaymentRegistrationInput(amountPaid: Double?,bank: String?, notes: String?, paymentReceipt: String?) : List<Validation> =
        ArrayList<Validation>().apply {
            when {
                amountPaid == null ->
                    add(
                        Validation(
                            Validation.Field.AMOUNT_PAID,
                            Resource.error(R.string.amount_paid_error)
                        )
                    )

                paymentReceipt.isNullOrBlank() ->
                    add(
                        Validation(
                            Validation.Field.RECEIPT_IMAGE,
                            Resource.error(R.string.no_receipt_error)
                        )
                    )

                bank!!.contains("Other",true) && notes.isNullOrBlank()->
                    add(
                        Validation(
                            Validation.Field.PAYMENT_NOTES,
                            Resource.error(R.string.no_payment_notes_error)
                        )
                    )
            }
        }

    /**
     * validates user input at transaction registration fragment
     */
    fun validateTransactionRegistrationInput(amountPaid: String?,signature: ByteArray?, receipt: Boolean,isBankDeposit: Boolean,isUpdate: Boolean) : List<Validation> =
        ArrayList<Validation>().apply {
            when {
                amountPaid.isNullOrBlank() ->
                    add(
                        Validation(
                            Validation.Field.AMOUNT_PAID,
                            Resource.error(R.string.amount_error)
                        )
                    )

                !Pattern.compile("[0-9]*").matcher(amountPaid!!).matches() -> {
                    add(
                        Validation(
                            Validation.Field.AMOUNT_PAID,
                            Resource.error(R.string.invalid_amount_error)
                        )
                    )
                }

                !isUpdate && !isBankDeposit && !receipt && signature==null-> {
                    add(
                        Validation(
                            Validation.Field.SIGNATURE,
                            Resource.error(R.string.signature_validation)
                        )
                    )
                }

                !isUpdate && !isBankDeposit && !receipt && signature!!.isEmpty() -> {
                    add(
                        Validation(
                            Validation.Field.SIGNATURE,
                            Resource.error(R.string.signature_validation)
                        )
                    )
                }

                isUpdate && !receipt && (signature==null || signature.isEmpty()) -> {
                    add(
                            Validation(
                                    Validation.Field.SIGNATURE,
                                    Resource.error(R.string.signature_validation)
                            )
                    )
                }
            }
        }

    /**
     * validates the input fields of the change password activity.
     */
    fun validateChangePasswordFields(validationModel: ChangePasswordValidationModel): List<Validation> {
        var result = ArrayList<Validation>()
        when {
            validationModel.enteredCurrentPassword.isNullOrBlank() ->
                result.add(
                    Validation(
                        Validation.Field.CURRENT_PASSWORD,
                        Resource.error(R.string.ERROR_EMPTY_CURRENT_PASSWORD)
                    )
                )
            validationModel.enteredCurrentPassword!!.length < Constants.PASSWORD_LENGTH_MINIMUM ->
                result.add(
                    Validation(
                        Validation.Field.CURRENT_PASSWORD,
                        Resource.error(R.string.password_field_small_length)
                    )
                )

            validationModel.newPassword.isNullOrBlank() ->
                result.add(
                    Validation(
                        Validation.Field.NEW_PASSWORD,
                        Resource.error(R.string.ERROR_EMPTY_NEW_PASSWORD)
                    )
                )

            /** Validate the length of new password, it must be at least 6 chars long*/
            validationModel.newPassword!!.length < Constants.PASSWORD_LENGTH_MINIMUM ->
                result.add(
                    Validation(
                        Validation.Field.NEW_PASSWORD,
                        Resource.error(R.string.password_field_small_length)
                    )
                )

            validationModel.retypeNewPassword.isNullOrBlank() ->
                result.add(
                    Validation(
                        Validation.Field.RETYPE_NEW_PASSWORD,
                        Resource.error(R.string.ERROR_EMPTY_RETYPE_NEW_PASSWORD)
                    )
                )

            /** Validate that new password is entered correctly both the times */
            validationModel.retypeNewPassword != validationModel.newPassword ->
                result.add(
                    Validation(
                        Validation.Field.RETYPE_NEW_PASSWORD,
                        Resource.error(R.string.ERROR_MISMATCH_RETYPE_NEW_PASSWORD)
                    )
                )

            validationModel.currentPassword != validationModel.enteredCurrentPassword ->
                result.add(
                    Validation(
                        Validation.Field.CURRENT_PASSWORD,
                        Resource.error(R.string.ERROR_MISMATCH_CURRENT_PASSWORD)
                    )
                )

        }
        return result
    }

    fun validateEmail(email: String?): List<Validation> =
        ArrayList<Validation>().apply {
            when{
                email.isNullOrBlank() -> {
                    add(Validation(Validation.Field.EMAIL, Resource.error(R.string.empty_email)))
                }
                !email.isValidEmail() ->{
                    add(Validation(Validation.Field.EMAIL, Resource.error(R.string.invalid_email)))
                }
                else ->{
                    add(Validation(Validation.Field.EMAIL, Resource.success()))
                }
            }
        }

    fun validateOTP(otp: String?): List<Validation> =
        ArrayList<Validation>().apply {
            when{
                otp.isNullOrBlank() -> {
                    add(Validation(Validation.Field.OTP, Resource.error(R.string.empty_otp)))
                }
                !otp.isValidOTP() ->{
                    add(Validation(Validation.Field.OTP, Resource.error(R.string.invalid_otp)))
                }
                else ->{
                    add(Validation(Validation.Field.OTP, Resource.success()))
                }
            }
        }

    /**
     * validates the input fields of the change password activity.
     */
    fun validateResetPasswordFields(newPassword: String?,retypeNewPassword: String?): List<Validation> {
        var result = ArrayList<Validation>()
        when {
            newPassword.isNullOrBlank() ->
                result.add(
                    Validation(
                        Validation.Field.NEW_PASSWORD,
                        Resource.error(R.string.ERROR_EMPTY_NEW_PASSWORD)
                    )
                )

            /** Validate the length of new password, it must be at least 6 chars long*/
            newPassword!!.length < Constants.PASSWORD_LENGTH_MINIMUM ->
                result.add(
                    Validation(
                        Validation.Field.NEW_PASSWORD,
                        Resource.error(R.string.ERROR_PASSWORD_LENGTH)
                    )
                )

            retypeNewPassword.isNullOrBlank() ->
                result.add(
                    Validation(
                        Validation.Field.RETYPE_NEW_PASSWORD,
                        Resource.error(R.string.ERROR_EMPTY_RETYPE_NEW_PASSWORD)
                    )
                )

            /** Validate that new password is entered correctly both the times */
            (!retypeNewPassword.equals(newPassword)) ->
                result.add(
                    Validation(
                        Validation.Field.RETYPE_NEW_PASSWORD,
                        Resource.error(R.string.ERROR_MISMATCH_RETYPE_NEW_PASSWORD)
                    )
                )

        }
        return result
    }

}

data class Validation(val field: Field, val resource: Resource<Int>) {
    enum class Field {
        USERNAME,
        PASSWORD,
        EMPTY_PASSWORD,
        CODAMOUNT,
        EMAIL,
        OTP,
        RETRYDATE,
        FAILUREREASON,
        COMMENTS,
        CURRENT_PASSWORD,
        NEW_PASSWORD,
        RETYPE_NEW_PASSWORD,
        AMOUNT_PAID,
        RECEIPT_IMAGE,
        PAYMENT_NOTES,
        SIGNATURE

    }
}
