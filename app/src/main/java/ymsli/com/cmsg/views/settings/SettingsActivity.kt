package ymsli.com.cmsg.views.settings

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.databinding.ActivitySettingsBinding
import ymsli.com.cmsg.di.component.ActivityComponent

class SettingsActivity: BaseActivity<SettingsViewModel,ActivitySettingsBinding>() {

    override fun provideViewBinding(): ActivitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)

    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    private var showName = true
    private var showNum = true
    private var showMessage = true

    override fun setupView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.actionBarTopSentDashboard)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        showName = viewModel.getShowReceiverName()
        showNum = viewModel.getShowReceiverNumber()
        showMessage = viewModel.getShowReceiverMessage()

        binding.cbRecName.isChecked = showName
        binding.cbRecNumber.isChecked = showNum
        binding.cbRecMsg.isChecked = showMessage

        binding.cbRecName.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                showName = isChecked
            }
        })

        binding.cbRecNumber.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                showNum = isChecked
            }
        })

        binding.cbRecMsg.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                showMessage = isChecked
            }
        })

        binding.buttonApply.setOnClickListener {
            viewModel.setShowReceiverName(showName)
            viewModel.setShowReceiverNumber(showNum)
            viewModel.setShowReceiverMessage(showMessage)
            showConfirmationDialog()// show confirmation dialog to the user
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * shows confirmatoin dialog to the user
     * after saving settings in shared prefs
     */
    private fun showConfirmationDialog(){
        val message = getString(R.string.settings_saved)
        val titleDialog = getString(R.string.msg_label)
        val actionCancel = {
            finish()
        }
        val actionOk = {
            finish()
        }
        showPermissionDeniedDialog(message, null, actionCancel, actionOk, titleDialog)
    }

    /**
     * Displays a confirmation dialog containing a message and one actions buttons.
     */
    protected fun showPermissionDeniedDialog(
        msg: String, labelAction: String? = getString(R.string.ACTION_OK),
        actionCancel: () -> Unit, actionOk: () -> Unit, titleDialog: String
    ) {
        val (dialogView, btnOk, btnCancel) = inflateConfirmationDialogView(msg, labelAction,titleDialog)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setCancelable(false)
            .setView(dialogView)
            .create()
        btnCancel.setOnClickListener { actionCancel(); dialog.dismiss() }
        dialog.show()
    }

    private fun inflateConfirmationDialogView(message: String, labelOk: String?,  titleDialog: String)
            : Triple<View, Any, Button> {
        val dialogView = layoutInflater.inflate(R.layout.permission_denied, null, false)
        val tvTitle = dialogView.findViewById(R.id.tv_logout) as TextView
        val iconTitle = dialogView.findViewById(R.id.iv_logout) as ImageView
        val tvMessage = dialogView.findViewById(R.id.tv_message) as TextView
        val btnCancel = dialogView.findViewById(R.id.btn_back) as Button
        tvTitle.text = titleDialog
        tvMessage.text = message
        iconTitle.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_check))
        return Triple(dialogView, 1 , btnCancel)
    }
}