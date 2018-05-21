package com.dew.edward.ppchat.Services

import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import com.dew.edward.ppchat.Controller.App
import com.dew.edward.ppchat.Model.UserProfile
import com.dew.edward.ppchat.Utilities.*
import org.json.JSONException


/*
 * Created by Edward on 5/20/2018.
 */

object AuthService {

    fun registerUser(email: String, password: String, complete: (Boolean) -> Unit) {

        // build JsonBody of requestBody
        val jsonBody = JSONObject(mapOf("email" to email, "password" to password))
        val requestBody = jsonBody.toString()

        // StringRequest() used for expecting string response
        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER,
                Response.Listener { response ->
                    Log.v(RES_TAG, "Register Response: $response")
                    with(UserProfile){
                        userEmail = email
                        userPassword = password
                    }
                    complete(true) // in complete (true), name, email, password should be stored
                },
                Response.ErrorListener { error ->
                    Log.d(ERR_TAG, "Register Error: $error")
                    complete(false)
                }) {

            override fun getBodyContentType(): String = BODY_CONTENT_TYPE

            override fun getBody(): ByteArray = requestBody.toByteArray()
        }

        App.requestQueue.add(registerRequest)
    }

    fun loginUser(email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject(mapOf("email" to email, "password" to password))
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
                Response.Listener { response ->
                    Log.v(RES_TAG, "Login Response: $response")
                    try {
                        with(UserProfile) {
                            authToken = response.getString("token")
                            userEmail = response.getString("user")
                            isLoggedIn = true
                        }
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d(J_EXCEPTION_TAG, e.localizedMessage)
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d(ERR_TAG, "Login Error: $error")
                    complete(false)
                }) {

            override fun getBodyContentType(): String = BODY_CONTENT_TYPE

            override fun getBody(): ByteArray = requestBody.toByteArray()
        }

        App.requestQueue.add(loginRequest)
    }

    fun createUser(name: String, email: String,
                   avatarColor: String, avatarName: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject(mapOf("name" to name, "email" to email,
                "avatarColor" to avatarColor, "avatarName" to avatarName))
        val requestBody = jsonBody.toString()
        println("Check: ${UserProfile}")
        val createRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null,
                Response.Listener { response ->
                    Log.v(RES_TAG, "CreateUser Response: $response")
                    try {
                        with(response) {
                            UserProfile.avatarColor = getString("avatarColor")
                            UserProfile.avatarName = getString("avatarName")
                            UserProfile.userEmail = getString("email")
                            UserProfile.userName = getString("name")
                            UserProfile.userId = getString("_id")
                        }
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d(J_EXCEPTION_TAG, e.localizedMessage)
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d(ERR_TAG, "CreateUser Error: $error")
                    complete(false)
                }) {

            override fun getBodyContentType(): String = BODY_CONTENT_TYPE

            override fun getBody(): ByteArray = requestBody.toByteArray()

            override fun getHeaders() = mapOf("Authorization" to "Bearer ${UserProfile.authToken}")
        }

        App.requestQueue.add(createRequest)
    }

    fun findUserByEmail(email: String, complete: (Boolean) -> Unit){
        val url = "$URL_GET_USER$email"

        val findUserRequest = object : JsonObjectRequest(Method.GET, url, null,
                Response.Listener { response ->
                    Log.v(RES_TAG, "CreateUser Response: $response")
                    try {
                        with(UserProfile){
                            avatarColor = response.getString("avatarColor")
                            avatarName = response.getString("avatarName")
                            userEmail = response.getString("email")
                            userName = response.getString("name")
                            userId = response.getString("_id")
                        }
                        val userDataChanged = Intent(BROADCAST_USER_DATA_CHANGED)
                        App.localBroadcastManager.sendBroadcast(userDataChanged)
                        complete(true)

                    } catch (e: JSONException){
                        Log.d(J_EXCEPTION_TAG, e.localizedMessage)
                        complete(false)
                    }
                },
                Response.ErrorListener {error ->
                    Log.d(ERR_TAG, "findUserByEmail Error: $error")
                    complete(false)
                }) {

            override fun getBodyContentType(): String = BODY_CONTENT_TYPE

            override fun getHeaders() = mapOf("Authorization" to "Bearer ${UserProfile.authToken}")
        }
        App.requestQueue.add(findUserRequest)
    }
}