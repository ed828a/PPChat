package com.dew.edward.ppchat.Utilities

/*
 * Created by Edward on 5/20/2018.
 */

// webRequest URL constants
// for local hosting: http://10.0.2.2:3005/v1/   and the default ip for the simulator is 10.0.2.2
const val BASE_URL = "https://ed828chat.herokuapp.com/v1/"    // v1 is a part of the endpoint
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"
const val URL_GET_CHANNELS = "${BASE_URL}channel/"
const val URL_GET_MESSAGES = "${BASE_URL}message/byChannel/"
const val BODY_CONTENT_TYPE = "application/json; charset=utf-8"

// webResponse key constants
const val TOKEN = "token"
const val USER = "user"

// webSocket URL constants
const val SOCKET_URL = "https://ed828chat.herokuapp.com/"

// Broadcast Constants
const val BROADCAST_USER_DATA_CHANGED = "BROADCAST_USER_DATA_CHANGED"

// SharedPreferences Constants
const val PREFS_FILENAME = "prefs"
const val IS_LOGGED_IN = "isLoggedIn"
const val AUTH_TOKEN = "authToken"
const val USER_EMAIL = "userEmail"
const val USER_PASSWORD = "password"
const val USER_ID = "userId"
const val SELECTED_CHANNEL_ID = "channelId"

// SavedInstanceState Constants
const val SELECTED_CHANNEL = "selected channel"

// Logging Constants
const val ERR_TAG = "ERROR"
const val RES_TAG = "RESPONSE"
const val J_EXCEPTION_TAG = "JSONException"
const val CHECK_TAG = "CHECK INFO"
const val PARSE_TAG = "PARSE"