package com.csanad.projemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.csanad.projemanag.R
import com.csanad.projemanag.firebase.FirestoreClass
import com.csanad.projemanag.models.User
import com.csanad.projemanag.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var mSelectedImageFileUri:Uri?=null
    private var mProfileImageUrl:String=""
    private lateinit var mUserDetails:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        ivUserImageMyProfileActivity.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        btnUpdate.setOnClickListener {
            if (mSelectedImageFileUri!=null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,"You denied the permission for storage. You can enable it from the settings.",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK&&requestCode== Constants.PICK_IMAGE_REQUEST_CODE&&data!!.data!=null) {
            mSelectedImageFileUri = data.data
            try {
                Glide.with(this).load(mSelectedImageFileUri).centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder).into(ivUserImageMyProfileActivity)

            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(tbMyProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        tbMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun setUserDataInUI(user:User){
        mUserDetails=user
        Glide.with(this).load(user.image).centerCrop().placeholder(R.drawable.ic_user_place_holder).into(ivUserImageMyProfileActivity)
        etNameMyProfileActivity.setText(user.name)
        etEmailMyProfileActivity.setText(user.email)
        if (user.mobile!=0L) {
            etMobile.setText(user.mobile.toString())
        }
    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri!=null){//szükségtelen
            val sRef:StorageReference=FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener{
                taskSnapshot-> Log.i("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageUrl=uri.toString()

                    updateUserData()
                }
            }.addOnFailureListener {
                exception->Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun updateUserData(){
        val userHashMap=HashMap<String,Any>()
        var changed=false

        if (mProfileImageUrl.isNotEmpty()&&mProfileImageUrl!=mUserDetails.image){
            userHashMap[Constants.IMAGE]=mProfileImageUrl
            changed=true
        }
        if (etNameMyProfileActivity.text.toString()!=mUserDetails.name){
            userHashMap[Constants.NAME]=etNameMyProfileActivity.text.toString()
            changed=true
        }
        if (etMobile.text.toString()!=mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=etMobile.text.toString().toLong()
            changed=true
        }
        if (changed) {
            FirestoreClass().updateUserData(this, userHashMap)
        }else{
            hideProgressDialog()
        }
    }
}