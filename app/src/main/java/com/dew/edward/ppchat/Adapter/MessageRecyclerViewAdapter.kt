package com.dew.edward.ppchat.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dew.edward.ppchat.Model.Message
import com.dew.edward.ppchat.Model.UserProfile
import com.dew.edward.ppchat.R
import com.dew.edward.ppchat.Utilities.CHECK_TAG
import com.dew.edward.ppchat.Utilities.PARSE_TAG
import kotlinx.android.synthetic.main.message_cell_view.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Edward on 5/21/2018.
 */

class MessageRecyclerViewAdapter (val context: Context, val messages: ArrayList<Message>):
        RecyclerView.Adapter<MessageRecyclerViewAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.message_cell_view, parent, false)

        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int = messages.count()

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        Log.d(CHECK_TAG, message.senderAvatar)

        val resourceId = context.resources.getIdentifier(message.senderAvatar,
                "drawable", context.packageName)

        holder.userAvatar?.setImageResource(resourceId)
        holder.userAvatar?.setBackgroundColor(UserProfile.returnAvatarColor(message.senderAvatarColor))
        holder.textUserName?.text = message.senderName
        holder.messageBody?.text = message.messageBody
        holder.timeStamp?.text = parseTimeStamp(message.timeStamp)
    }

    inner class MessageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        val textUserName = itemView?.textCellUserName
        val timeStamp = itemView?.textCellTimeStamp
        val messageBody = itemView?.textCellMessageBody
        val userAvatar = itemView?.imageCellUserAvatar
    }

    /**
     * @param: 2018-05-18T11:39:44.888Z
     * @return:  Monday 4:35 PM
     */
    fun parseTimeStamp(timeStamp: String): String{
        val formatterIn = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        formatterIn.timeZone = TimeZone.getTimeZone("UTC")
        var convertedDate = Date()
        try {
            convertedDate = formatterIn.parse(timeStamp)
        } catch (e: ParseException){
            Log.d(PARSE_TAG, "can't parse date: ${e.localizedMessage}")
            e.printStackTrace()
        }

        val formatterOut = SimpleDateFormat("EEE, MMM d, h:mm a", Locale.getDefault())
        val output = formatterOut.format(convertedDate)

        return output
    }
}