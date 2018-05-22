package com.dew.edward.ppchat.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.dew.edward.ppchat.Model.UserProfile
import com.dew.edward.ppchat.R
import com.dew.edward.ppchat.Services.AuthService
import com.dew.edward.ppchat.Utilities.BROADCAST_USER_DATA_CHANGED
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColor = "[1, 1, 1, 1]"// sets the color to white, and a transparency level of 100%

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        spinnerRegister.visibility = View.INVISIBLE
    }

    fun onGenerateUserAvatarClick(view: View){
        val random = Random()
        val type = random.nextInt(2)
        val avatar = random.nextInt(28)

        userAvatar = when(type) {
            0 -> "light$avatar"
            1 -> "dark$avatar"
            else -> "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        imageRegisterUserAvatar.setImageResource(resourceId)
    }

    /**
     * generate and set UserAvatar background color
     * and convert the color to the format of transport for Creating a User
     */
    fun onGenerateBackgroundColorClick(view: View){
        val random = Random()
        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)
        imageRegisterUserAvatar.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255
        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun onCreateUserClick(view: View){
        val userName = textNavHeaderUserName.text.toString()
        val email = textRegisterEmail.text.toString()
        val password = textRegisterPassword.text.toString()

        // you can also do other check here
        if (userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            enableSpinner(true)
            AuthService.registerUser(email, password){ registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(email, password){ loginSuccess ->
                        if (loginSuccess){
                            AuthService.createUser(userName, email, avatarColor, userAvatar){createSuccess ->
                                if (createSuccess){
                                    val userDataChanged = Intent(BROADCAST_USER_DATA_CHANGED)
                                    App.localBroadcastManager.sendBroadcast(userDataChanged)

                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }

                } else {
                    errorToast()
                }
            }
        } else {
            enableSpinner(false)
            Toast.makeText(this,
                    getString(R.string.need_name_email_password),
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableSpinner(enable: Boolean){
        if (enable){
            spinnerRegister.visibility = View.VISIBLE
        } else {
            spinnerRegister.visibility = View.INVISIBLE
        }
        buttonGenerateBackgroundColor.isEnabled = !enable
        buttonCreateUser.isEnabled = !enable
        imageRegisterUserAvatar.isEnabled = !enable
    }

    private fun errorToast(){
        enableSpinner(false)
        Toast.makeText(this,
                getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
    }
}
