package com.dew.edward.ppchat.Model

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable

/*
 * Created by Edward on 5/20/2018.
 */
// User profile should be initialized in onCreateUserClick(),
// to use in authRequest(), store to SharedPreferences after successful registered
object UserProfile {
    var userName: String = ""
    var userEmail: String = ""
    var userPassword: String = ""
    var avatarName = ""
    var avatarColor = "[1, 1, 1, 1]"
    var userId = ""
    var authToken = ""
    var isLoggedIn = false

    override fun toString(): String {
        return "userName: $userName" + " \n userEmail: $userEmail" +
                " \n userPassword: $userPassword" + " \n authToken: $authToken" +
                " \n isLoggedIn = $isLoggedIn" + " \n userAvatar: $avatarName" +
                " \n avatarColor: $avatarColor"
    }

    fun returnAvatarColor(colorString: String): Int {
        //[0.1568627450980392, 0.6509803921568628, 0.8862745098039215, 1]
        /**
         * cheap and efficient way,
         * the key is String.split() function
         * CharSequence.split(vararg delimiters: String,
         *                    ignoreCase: Boolean = false, limit: Int = 0): List<String>
         */
        val strings = colorString.split(", ", "[", "]")
        val red = (strings[1].toDouble() * 255).toInt()
        val green = (strings[2].toDouble() * 255).toInt()
        val blue = (strings[3].toDouble() * 255).toInt()

        return Color.rgb(red, green, blue)
    }
}

class Channel(val name: String, val description: String, val id: String): Parcelable{

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Channel> {
        override fun createFromParcel(parcel: Parcel): Channel {
            return Channel(parcel)
        }

        override fun newArray(size: Int): Array<Channel?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "#$name"
    }
}

data class Message(val messageBody: String, val timeStamp: String, val senderName: String,
                   val senderAvatar: String, val senderAvatarColor: String)

