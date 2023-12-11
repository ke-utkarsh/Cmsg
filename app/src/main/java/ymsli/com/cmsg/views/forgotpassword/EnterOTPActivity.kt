package ymsli.com.cmsg.views.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.databinding.ActivityEnterOtpBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.utils.Resource

class EnterOTPActivity :
    BaseActivity<EnterOTPViewModel, ActivityEnterOtpBinding>() {

    private var emailId: String? = null
    private var otpValidity: Int? = null
    private var otpValue: String? = null

    override fun provideViewBinding() = ActivityEnterOtpBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        emailId = intent.getStringExtra("EMAIL_RESPONSE")
        otpValidity = intent.getIntExtra("OTP_DURATION",10)

        // show timer of OTP duration at below text
        binding.textResendOtp.base = SystemClock.elapsedRealtime() + (otpValidity!! * 1000 * 60)
        binding.textResendOtp.start()
        binding.btnSubmit.setOnClickListener {
            hideKeyboard(binding.constraintLayoutLoginActivityRoot)
            otpValue = binding.inputEnterOtpText.text.toString()
            viewModel.verifyOTP(emailId,otpValue)
        }

        binding.textResendOtp.onChronometerTickListener =
            Chronometer.OnChronometerTickListener { chronometer ->
                val elapsed = SystemClock.elapsedRealtime() - chronometer!!.base
                if (elapsed >= 500) {
                    chronometer.stop()
                    binding.textResendOtp.visibility = View.GONE
                    binding.tvTimerLabel.text = "OTP Expired"
                }
            }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.isOTPVerified.observe(this,{
            it.getIfNotHandled()?.let {
                if(it){
                    val intent = Intent(this, ResetPasswordActivity::class.java)
                    intent.putExtra("EMAIL_RESPONSE",emailId)
                    intent.putExtra("OTP_VALUE",otpValue)
                    finishAndRemoveTask()
                    startActivity(intent)

                }
                else{
                    // show user that OTP is not verified
                    viewModel.messageStringId.postValue(Resource.error(R.string.otp_verification_error))
                }
            }
        })
    }
}