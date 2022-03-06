package com.csanad.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.csanad.projemanag.R
import com.csanad.projemanag.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.etEmail
import kotlinx.android.synthetic.main.activity_sign_in.etPassword
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : BaseActivity() {

    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth= FirebaseAuth.getInstance()

        setupActionBar()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(tbSignInActivity)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        tbSignInActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser(){
        val email:String=etEmail.text.toString().trim { it<=' ' }
        val password:String=etPassword.text.toString().trim { it<=' ' }

        if (validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sign in", "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this,MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign in", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an e-mail address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }
            else->{
                true
            }
        }
    }

    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}