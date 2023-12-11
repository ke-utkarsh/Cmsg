package ymsli.com.cmsg.views.changepassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.databinding.ActivityChangePasswordBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.utils.Utils

class ChangePasswordActivity :
    BaseActivity<ChangePasswordViewModel, ActivityChangePasswordBinding>() {
    override fun provideViewBinding() = ActivityChangePasswordBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.actionBarTopResetPassword)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setInputFilters()

        binding.btnChangePwd.setOnClickListener {
            // hide keyboard
            hideKeyboard(binding.constraintLayoutLoginActivityRoot)
            // call password change API and finish activity
            viewModel.changePassword()
        }

        binding.inputOldPasswordText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onCurrentPasswordChange(s.toString().trim())
            }
        })

        binding.inputNewPasswordText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onNewPasswordChange(s.toString().trim())
            }
        })

        binding.inputResetConfirmPasswordText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onRetypeNewPasswordChange(s.toString().trim())
            }
        })
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.apiRequestActive.observe(this,{
            if(it){
                // ack user that password changes successfully
                showConfirmationDialog(true)
            }
            else{
                // ack user that password changes failed
                showConfirmationDialog(false)
            }
        })
    }

    /**
     * Sets the input filters for all the edit text's.
     * this particular applyReturnListFilter disallows any space character input.
     */
    private fun setInputFilters(){
        val filters = arrayOf(Utils.getSpaceFilter())
        binding.inputOldPasswordText.filters = filters
        binding.inputNewPasswordText.filters = filters
        binding.inputResetConfirmPasswordText.filters = filters
    }

    /**
     * shows confirmatoin dialog to the user
     * after saving settings in shared prefs
     */
    private fun showConfirmationDialog(success: Boolean){
        var message = getString(R.string.PASSWORD_CHANGE_SUCCESS)
        var titleDialog = getString(R.string.success)
        if(!success){
            message = getString(R.string.ERROR_PASSWORD_CHANGE_FAILED)
            titleDialog = getString(R.string.failed_label)
        }

        val actionCancel = {
            finish()
        }
        val actionOk = {
            finish()
        }
        showPermissionDeniedDialog(message, null, actionCancel, actionOk, titleDialog,success)
    }

    /**
     * Displays a confirmation dialog containing a message and one actions buttons.
     */
    protected fun showPermissionDeniedDialog(
        msg: String, labelAction: String? = getString(R.string.ACTION_OK),
        actionCancel: () -> Unit, actionOk: () -> Unit, titleDialog: String,
        isSuccess: Boolean
    ) {
        val (dialogView, btnOk, btnCancel) = inflateConfirmationDialogView(msg, labelAction,titleDialog,isSuccess)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setCancelable(false)
            .setView(dialogView)
            .create()
        btnCancel.setOnClickListener { actionCancel(); dialog.dismiss() }
        dialog.show()
    }

    private fun inflateConfirmationDialogView(message: String, labelOk: String?,  titleDialog: String,isSuccess: Boolean)
            : Triple<View, Any, Button> {
        val dialogView = layoutInflater.inflate(R.layout.permission_denied, null, false)
        val tvTitle = dialogView.findViewById(R.id.tv_logout) as TextView
        val iconTitle = dialogView.findViewById(R.id.iv_logout) as ImageView
        val tvMessage = dialogView.findViewById(R.id.tv_message) as TextView
        val btnCancel = dialogView.findViewById(R.id.btn_back) as Button
        tvTitle.text = titleDialog
        tvMessage.text = message
        if(isSuccess) iconTitle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_check))
        else iconTitle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_error))
        return Triple(dialogView, 1 , btnCancel)
    }
}