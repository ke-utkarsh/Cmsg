package ymsli.com.cmsg.views.forgotpassword

import android.content.Intent
import android.os.Bundle
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.databinding.ActivityForgotPasswordBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.utils.Resource

class ForgotPasswordActivity :
    BaseActivity<ForgotPasswordViewModel, ActivityForgotPasswordBinding>() {
    override fun provideViewBinding() = ActivityForgotPasswordBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        binding.btnSubmit.setOnClickListener {
            // trigger OTP to email
            hideKeyboard(binding.constraintLayoutLoginActivityRoot)
            viewModel.triggerOTP(binding.inputForgotPasswordEmailText.text.toString())
        }

        binding.textBack.setOnClickListener {
            finish()
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.otpTriggered.observe(this,{
            it.getIfNotHandled()?.let {
                if(it){
                    val intent = Intent(this, EnterOTPActivity::class.java)
                    intent.putExtra("OTP_DURATION",viewModel.otpDuration)
                    intent.putExtra("EMAIL_RESPONSE",viewModel.emailIdResponse)
                    startActivity(intent)
                }
                else{
                    // show user that OTP not triggered
                    viewModel.messageStringId.postValue(Resource.error(R.string.otp_trigger_error))
                }
            }
        })
    }
}