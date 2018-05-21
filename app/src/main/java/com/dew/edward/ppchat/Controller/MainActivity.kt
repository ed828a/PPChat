package com.dew.edward.ppchat.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.dew.edward.ppchat.Adapter.MessageRecyclerViewAdapter
import com.dew.edward.ppchat.Controller.App.Companion.localBroadcastManager
import com.dew.edward.ppchat.Model.Channel
import com.dew.edward.ppchat.Model.Message
import com.dew.edward.ppchat.Model.UserProfile
import com.dew.edward.ppchat.R
import com.dew.edward.ppchat.Services.AuthService
import com.dew.edward.ppchat.Services.MessageService
import com.dew.edward.ppchat.Utilities.BROADCAST_USER_DATA_CHANGED
import com.dew.edward.ppchat.Utilities.CHECK_TAG
import com.dew.edward.ppchat.Utilities.SELECTED_CHANNEL
import com.dew.edward.ppchat.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var listViewAdaper: ArrayAdapter<Channel>  // for notification
    private var selectedChannel: Channel? = null
    val socket = IO.socket(SOCKET_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        localBroadcastManager.registerReceiver(userDataChangedReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGED))

        setupListViewAdapter()
        listChannels.setOnItemClickListener { parent, view, position, id ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        setupRecyclerView()

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        with(UserProfile) {
            isLoggedIn = App.sharedPrefs.isLoggedIn
            authToken = App.sharedPrefs.authToken
            userEmail = App.sharedPrefs.userEmail
            userPassword = App.sharedPrefs.password
            userId = App.sharedPrefs.userId
        }

        Log.v(CHECK_TAG, "UserProfile onCreate: $UserProfile")

        if (UserProfile.isLoggedIn) {
            AuthService.findUserByEmail(UserProfile.userEmail) { findSuccess ->
                if (findSuccess && (savedInstanceState != null)) {
                    selectedChannel = savedInstanceState?.getParcelable(SELECTED_CHANNEL)

                }
            }
        }

        textMessageEditField.setOnEditorActionListener { textView, actionId, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                onSendButtonClick(textView)
            }
            true
        }
    }

    private fun setupListViewAdapter() {
        listViewAdaper = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        listChannels.adapter = listViewAdaper
    }

    private fun setupRecyclerView() {
        recyclerMessageList.adapter = MessageRecyclerViewAdapter(this, MessageService.messages)
        recyclerMessageList.layoutManager = LinearLayoutManager(this)
    }

    private val userDataChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (UserProfile.isLoggedIn) {
                textNavHeaderUserName.text = UserProfile.userName
                textNavHeaderUserEmail.text = UserProfile.userEmail
                val resourceId = resources.getIdentifier(UserProfile.avatarName, "drawable", packageName)
                imageUserNavHeader.setImageResource(resourceId)
                imageUserNavHeader.setBackgroundColor(UserProfile.returnAvatarColor(UserProfile.avatarColor))

                buttonLogin.text = "Logout"

                if (context != null) {
                    MessageService.getChannels { complete ->
                        if (complete) {
                            if (MessageService.channels.count() > 0) {
                                listViewAdaper.notifyDataSetChanged()
                                if (selectedChannel == null) {
                                    // app just started
                                    selectedChannel = MessageService.getChannelById(App.sharedPrefs.selectedChannelId)
                                }
                                updateWithChannel()
                            }
                        }
                    }
                }
            } else {
                // nothing to do yet
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putParcelable(SELECTED_CHANNEL, selectedChannel)
        Log.d(CHECK_TAG, "onSaveInstanceState: selectedChannel = $selectedChannel")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedChannel = savedInstanceState?.getParcelable(SELECTED_CHANNEL)
        println("onRestoreInstanceState: selectChannel = $selectedChannel")
    }

    override fun onStop() {
        Log.v(CHECK_TAG, "UserProfile onStop: $UserProfile")
        exit()
        super.onStop()
    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(userDataChangedReceiver)
        socket.disconnect()

        super.onDestroy()
    }

    fun updateWithChannel() {
        textChannelName.text = "#${selectedChannel?.name}"
        if (selectedChannel != null) {
            Log.d(CHECK_TAG, "updateWithChannel: selectedChannel = $selectedChannel")
            MessageService.getMessages(selectedChannel!!.id) { complete ->
                if (complete) {
                    recyclerMessageList.adapter.notifyDataSetChanged()

                    if (recyclerMessageList.adapter.itemCount > 0) {
                        recyclerMessageList.smoothScrollToPosition(recyclerMessageList.adapter.itemCount - 1)
                    }
                } else {
                    Toast.makeText(this, "Something went to Wrong, please try again!",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun onLoginButtonClick(view: View) {
        if (UserProfile.isLoggedIn) {
            logout()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun onAddChannelButtonClick(view: View) {
        if (UserProfile.isLoggedIn) {
            val dialogBuilder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            dialogBuilder.setView(dialogView)
                    .setPositiveButton("Add") { dialog, which ->
                        // store channel name and description
                        val channelName = dialogView.textDialogChannelName.text.toString()
                        val channelDescription = dialogView.textDialogChannelDescription.text.toString()
                        if (channelName.isNotEmpty() && channelDescription.isNotEmpty()) {
                            socket.emit("newChannel", channelName, channelDescription)
                        } else {
                            Toast.makeText(this, "Please fill out Channel Name and Channel Description Both!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        // nothing to do
                    }
                    .show()
        }
    }

    fun onSendButtonClick(view: View) {
        if (UserProfile.isLoggedIn && textMessageEditField.text.isNotEmpty() && selectedChannel != null) {
            val userId = UserProfile.userId
            val channelId = selectedChannel!!.id
            socket.emit("newMessage", textMessageEditField.text.toString(),
                    userId, channelId, UserProfile.userName, UserProfile.avatarName,
                    UserProfile.avatarColor)

            textMessageEditField.text.clear()
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    private fun logout() {
        buttonLogin.text = "Login"

        with(App.sharedPrefs) {
            isLoggedIn = false
            authToken = ""
            userEmail = ""
            password = ""
            selectedChannelId = ""
        }

        with(UserProfile) {
            userName = ""
            userEmail = ""
            userPassword = ""
            avatarName = ""
            avatarColor = "[1, 1, 1, 1]"
            userId = ""
            authToken = ""
            isLoggedIn = false
        }
        imageUserNavHeader.setImageResource(R.drawable.profiledefault)
        imageUserNavHeader.setBackgroundColor(Color.TRANSPARENT)
        textNavHeaderUserEmail.text = ""
        textNavHeaderUserName.text = ""
        textChannelName.text = "Please Log In"

        MessageService.clearChannelsAndMessages()
        listViewAdaper.notifyDataSetChanged()
        recyclerMessageList.adapter.notifyDataSetChanged()
    }

    private fun exit() {
        App.sharedPrefs.isLoggedIn = UserProfile.isLoggedIn
        App.sharedPrefs.authToken = UserProfile.authToken
        App.sharedPrefs.userEmail = UserProfile.userEmail
        App.sharedPrefs.password = UserProfile.userPassword
        App.sharedPrefs.userId = UserProfile.userId
        if (selectedChannel != null) {
            App.sharedPrefs.selectedChannelId = selectedChannel!!.id
        }
    }

    /**
     * this callback performs on a worker or background thread.
     */
    private val onNewChannel = Emitter.Listener { args: Array<out Any>? ->
        if (UserProfile.isLoggedIn) {
            runOnUiThread {
                if (args != null) { // the order of the parameters are determined by the API
                    val channelName = args[0] as String
                    val channelDescription = args[1] as String
                    val channelId = args[2] as String

                    val newChannel = Channel(channelName, channelDescription, channelId)
                    MessageService.channels.add(newChannel)
                    listViewAdaper.notifyDataSetChanged()

                    selectedChannel = newChannel
                    textChannelName.text = newChannel.name
                    MessageService.messages.clear()
                    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }

                    recyclerMessageList.adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args: Array<out Any>? ->
        if (UserProfile.isLoggedIn) {
            runOnUiThread {
                if (args != null) {
                    val channelId = args[2] as String

                    if (channelId == selectedChannel?.id) {
                        val msgBody = args[0] as String
                        val userId = args[1] as String
                        val userName = args[3] as String
                        val userAvatar = args[4] as String
                        val userAvatarColor = args[5] as String
                        val msgId = args[6] as String
                        val msgTimeStamp = args[7] as String

                        val newMessage = Message(msgBody, msgTimeStamp, userName,
                                userAvatar, userAvatarColor)
                        MessageService.messages.add(newMessage)

                        with(recyclerMessageList) {
                            adapter.notifyDataSetChanged()
                            smoothScrollToPosition(adapter.itemCount - 1)

                        }
                    }
                }
            }
        }
    }
}
