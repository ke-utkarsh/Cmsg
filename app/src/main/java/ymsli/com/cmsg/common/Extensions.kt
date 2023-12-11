package ymsli.com.cmsg.common

/** Regex and pattern fields for email validations */
private const val VALID_EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"
private val VALID_EMAIL_ADDRESS_REGEX = Regex(VALID_EMAIL_PATTERN, RegexOption.IGNORE_CASE)

private const val VALID_INPUT_OTP_PATTERN = "^[0-9]{4}$"
private val VALID_INPUT_OTP_REGEX = Regex(VALID_INPUT_OTP_PATTERN)


fun String.isValidEmail() = matches(VALID_EMAIL_ADDRESS_REGEX)


fun String.isValidOTP() = matches(VALID_INPUT_OTP_REGEX)

