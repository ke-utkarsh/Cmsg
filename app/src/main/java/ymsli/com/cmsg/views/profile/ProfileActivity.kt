package ymsli.com.cmsg.views.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseActivity
import ymsli.com.cmsg.databinding.ActivityProfileBinding
import ymsli.com.cmsg.di.component.ActivityComponent
import ymsli.com.cmsg.views.changepassword.ChangePasswordActivity
import java.io.ByteArrayOutputStream

class ProfileActivity :
    BaseActivity<ProfileViewModel, ActivityProfileBinding>() {

    companion object {
        const val OPEN_CAMERA = 1
        const val OPEN_GALLERY = 2
    }

    override fun provideViewBinding() = ActivityProfileBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.actionBarMsgDetail)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel.getUserProfile()
        binding.tvUserName.text = viewModel.getUserDisplayName()
        binding.tvPhone.text = viewModel.getUserPhoneNum()

        binding.tvChangePassword.setOnClickListener {
            // open change password activity
            val changePasswordIntent = Intent(this,ChangePasswordActivity::class.java)
            startActivity(changePasswordIntent)
        }

        binding.cameraConstraintLayout.setOnClickListener {
            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            if (cameraIntent.resolveActivity(packageManager) == null){

            }
            else{
                startActivityForResult(cameraIntent, OPEN_CAMERA)
                binding.uploadedConstraintLayout.visibility = View.VISIBLE
            }
        }

        binding.galleryConstraintLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(packageManager) == null){

            }
            else{
                startActivityForResult(intent, OPEN_GALLERY)
                binding.uploadedConstraintLayout.visibility = View.VISIBLE
            }
        }

        binding.buttonSave.setOnClickListener {
            viewModel.saveUserProfile()
        }

        val imageString = viewModel.getUserImage()
        if(!imageString.isNullOrBlank()){
            val decodedString = Base64.decode(imageString,Base64.DEFAULT)
            val bm = BitmapFactory.decodeByteArray(decodedString,0,decodedString.size)
            binding.uploadedImageView.setImageBitmap(bm)
        }
        else{
            binding.uploadedImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_user_placeholder))
        }

        binding.uploadedImageView.setOnClickListener {
            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            if (cameraIntent.resolveActivity(packageManager) == null){

            }
            else{
                startActivityForResult(cameraIntent, OPEN_CAMERA)
                binding.uploadedConstraintLayout.visibility = View.VISIBLE
            }
        }
    }

    private lateinit var photo: Bitmap
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                OPEN_CAMERA -> {//uploading via Camera
                    photo = data?.extras?.get("data") as Bitmap
                    binding.uploadedImageView.setImageBitmap(photo)
                    val baos = ByteArrayOutputStream()
                    photo.compress(Bitmap.CompressFormat.PNG,100,baos)

                    val byteArr : ByteArray= baos.toByteArray() //if image size is more than 2 MB, compress it by 40%
                    var tempBaos: ByteArrayOutputStream? = null
                    if((byteArr.size)/1000>=2000){
                        val tempPhoto = data?.extras?.get("data") as Bitmap
                        tempBaos = ByteArrayOutputStream()
                        tempPhoto.compress(Bitmap.CompressFormat.JPEG,10,tempBaos)
                        viewModel.profilePic.postValue(tempBaos?.toByteArray())
                    }
                    else{
                        viewModel.profilePic.postValue(byteArr)
                    }

                }

                OPEN_GALLERY -> { //uploading via Gallery
                    val selectedImage
                            : Uri = data!!.data!!
                    val filePath =
                        arrayOf(MediaStore.Images.Media.DATA)
                    val c
                            : Cursor = this.getContentResolver().query(
                        selectedImage,
                        filePath,
                        null,
                        null,
                        null
                    )!!
                    c.moveToFirst()
                    val columnIndex
                            : Int = c.getColumnIndex(filePath[0])
                    val picturePath
                            : String = c.getString(columnIndex)
                    c.close()
                    val thumbnail: Bitmap = BitmapFactory.decodeFile(picturePath)
                    binding.uploadedImageView.setImageBitmap(thumbnail)
                    val baos = ByteArrayOutputStream()
                    thumbnail.compress(Bitmap.CompressFormat.JPEG,100,baos)

                    val byteArr : ByteArray= baos.toByteArray()
                    if((byteArr.size)/1000>=2000){ //if image size is more than 2 MB, compress it by 40%
                        //thumbnail.compress(Bitmap.CompressFormat.PNG,40,baos)
                        val smallImage : Bitmap = BitmapFactory.decodeFile(picturePath)
                        val tempBaos = ByteArrayOutputStream()
                        smallImage.compress(Bitmap.CompressFormat.JPEG,10,tempBaos)
                        //viewModel.paymentReceipt.postValue(HttpRequest.Base64.encodeBytes(tempBaos.toByteArray()))
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setPermissions()
    }

    /**
     * Set the required permissions if not allowed by the user.
     */
    private fun setPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),100)
        }

        else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA
            ) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(
                Manifest.permission.CAMERA
            ),101)
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.uploadSuccess.observe(this,{
            it.getIfNotHandled()?.let {
                if(it){
                    showConfirmationDialog()
                }
            }
        })
    }

    /**
     * shows confirmatoin dialog to the user
     * after saving settings in shared prefs
     */
    private fun showConfirmationDialog(){
        val message = getString(R.string.profile_updated)
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