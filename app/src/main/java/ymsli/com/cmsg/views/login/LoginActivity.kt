package ymsli.com.cmsg.views.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.lifecycle.Observer
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.databinding.ActivityLoginBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.views.dashboard.DashboardActivity
import ymsli.com.cmsg.views.forgotpassword.ForgotPasswordActivity
import ymsli.com.cmsg.views.sync.SyncActivity

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    override fun provideViewBinding() = ActivityLoginBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        binding.btnLogin.setOnClickListener {

            var countryCode = Constants.COUNTRY_CODE_UGANDA
            viewModel.doLogin(countryCode)
            binding.inputLoginUserid.error = null
            binding.inputLoginPasswordText.error = null
        }

        binding.textForgotPassword.setOnClickListener {
            var intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.inputLoginUserid.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                viewModel.onUsernameChange(s.toString().trim())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.inputLoginPasswordText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                viewModel.onPasswordChange(s.toString().trim())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    override fun setupObservers() {
        super.setupObservers()
        //error observers
        viewModel.invalidUsername.observe(this, Observer {
            binding.inputLoginUserid.error = resources.getString(R.string.username_field_empty)
            binding.inputLoginUserid.requestFocus();
        })

        viewModel.invalidPassword.observe(this, Observer {
            if(binding.inputLoginPasswordText.text!!.isEmpty())
            {
                binding.inputLoginPasswordText.error = resources.getString(R.string.password_field_empty)
                binding.inputLoginPasswordText.requestFocus();

            }else{
                binding.inputLoginPasswordText.error = resources.getString(R.string.password_field_small_length)
                binding.inputLoginPasswordText.requestFocus();

            }
        })

        viewModel.appDataLoaded.observe(this,{
            var intent = Intent(this, SyncActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        })
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
}