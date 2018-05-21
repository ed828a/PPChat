package com.dew.edward.ppchat.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.dew.edward.ppchat.Controller.App
import com.dew.edward.ppchat.Model.Channel
import com.dew.edward.ppchat.Model.Message
import com.dew.edward.ppchat.Model.UserProfile
import com.dew.edward.ppchat.Utilities.*
import org.json.JSONException


/*
 * Created by Edward on 5/20/2018.
 */

object MessageService {
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun clearChannelsAndMessages(){
        channels.clear()
        messages.clear()
    }

    fun getChannels(complete: (Boolean) -> Unit){
        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null,
                Response.Listener { response ->
                    Log.v(RES_TAG, "getChannels Response: $response")
                    clearChannelsAndMessages()

                    try {
                        for (i in 0 until response.length()){
                            val channel = response.getJSONObject(i)
                            with(channel){
                                val newChannel = Channel(getString("name"),
                                        getString("description"), getString("_id"))

                                channels.add(newChannel)
                            }
                        }

                        complete(true)  // notify channel list adapter that data changed.

                    } catch (e: JSONException) {
                        Log.d(J_EXCEPTION_TAG, e.localizedMessage)
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d(ERR_TAG, "getChannels Error: $error")
                    complete(false)
                }){

            override fun getBodyContentType(): String = BODY_CONTENT_TYPE

            override fun getHeaders() = mapOf("Authorization" to "Bearer ${UserProfile.authToken}")
        }

        App.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelId: String, complete: (Boolean) -> Unit){
        val url = "$URL_GET_MESSAGES$channelId"

        val messagesRequest = object : JsonArrayRequest(Method.GET, url, null,
                Response.Listener { response ->
                    Log.v(RES_TAG, "getMessages Response: $response")
                    messages.clear()
                    try {
                        for (i in 0 until response.length()){
                            val message = response.getJSONObject(i)
                            with(message){
                                val newMessage = Message(getString("messageBody"),
                                        getString("timeStamp"), getString("userName"),
                                        getString("userAvatar"), getString("userAvatarColor"))

                                messages.add(newMessage)
                            }
                        }

                        complete(true)
                    } catch (e: JSONException) {
                        Log.d(J_EXCEPTION_TAG, e.localizedMessage)
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d(ERR_TAG, "getMessages Error: $error")
                    complete(false)
                }){

            override fun getBodyContentType(): String = BODY_CONTENT_TYPE

            override fun getHeaders() = mapOf("Authorization" to "Bearer ${UserProfile.authToken}")
        }

        App.requestQueue.add(messagesRequest)
    }



    /**
     * default return is the first Channel
     */
    fun getChannelById(channelId: String) : Channel {
        for (channel in channels) {
            if (channel.id == channelId) {
                return channel
            }
        }

        return channels[0]
    }
}