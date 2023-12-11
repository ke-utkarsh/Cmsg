package ymsli.com.cmsg.views.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.databinding.ActivityResetPasswordBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.utils.Resource
import ymsli.com.cmsg.views.login.LoginActivity

class ResetPasswordActivity :
    BaseActivity<ResetPasswordViewModel, ActivityResetPasswordBinding>() {

    private var emailId: String? = null
    private var otpValue: String? = null

    override fun provideViewBinding() = ActivityResetPasswordBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.actionBarTopResetPassword)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.title = "Reset Password"
        supportActionBar?.setIcon(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        emailId = intent.getStringExtra("EMAIL_RESPONSE")
        otpValue = intent.getStringExtra("OTP_VALUE")

        binding.btnResetPwd.setOnClickListener {
            hideKeyboard(binding.constraintLayoutLoginActivityRoot)
            viewModel.resetPassword(binding.inputResetPasswordText.text.toString(),binding.inputResetConfirmPasswordText.text.toString()
                ,emailId!!, otpValue!!.toInt())
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.isPasswordReset.observe(this,{
            it.getIfNotHandled()?.let {
                if(it){
                    // ack user that password is reset
                    viewModel.messageStringId.postValue(Resource.error(R.string.PASSWORD_CHANGE_SUCCESS))
                    //after delay of 2-3 seconds, redirect to login activity
                    Handler().postDelayed({
                        openLoginActivity()
                    },1500)

                }
                else{
                    // show user that password reset failed
                    viewModel.messageStringId.postValue(Resource.error(R.string.ERROR_PASSWORD_CHANGE_FAILED))
                }
            }
        })
    }

    private fun openLoginActivity(){
        val loginIntent = Intent(this,LoginActivity::class.java)
        finishAffinity()
        startActivity(loginIntent)
    }
}