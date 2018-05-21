package com.dew.edward.ppchat.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.dew.edward.ppchat.Model.UserProfile
import com.dew.edward.ppchat.R
import com.dew.edward.ppchat.R.id.*
import com.dew.edward.ppchat.Services.AuthService
import com.dew.edward.ppchat.Utilities.BROADCAST_USER_DATA_CHANGED
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        spinnerLogin.visibility = View.INVISIBLE
    }

    fun onLoginButtonClick(view: View){
        val email = textLoginEmail.text.toString()
        val password = textLoginPassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            hideKeyboard()
            enableSpinner(true)
            AuthService.loginUser(email, password){loginSuccess ->
                if (loginSuccess) {
                    UserProfile.userEmail = email
                    UserProfile.userPassword = password

                    AuthService.findUserByEmail(email) { findSuccess ->
                        if (findSuccess){
                            enableSpinner(false)
                            finish()
                        } else {
                            errorToast()
                        }
                    }
                }
                else {
                    errorToast()
                }
            }
        } else {
            enableSpinner(false)
            Toast.makeText(this,
                    getString(R.string.need_email_password),
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableSpinner(enable: Boolean){
        if (enable){
            spinnerLogin.visibility = View.VISIBLE
        } else {
            spinnerLogin.visibility = View.INVISIBLE
        }
        buttonLoginLogin.isEnabled = !enable
        buttonLoginRegister.isEnabled = !enable
        textLoginPassword.isEnabled = !enable
        textLoginEmail.isEnabled = !enable
    }

    private fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    private fun errorToast(){
        enableSpinner(false)
        Toast.makeText(this,
                getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
    }

    fun onRegisterButtonClick(view: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}
