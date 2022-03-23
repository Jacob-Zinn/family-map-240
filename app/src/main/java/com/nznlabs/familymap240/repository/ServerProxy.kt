package com.nznlabs.familymap240.repository

import com.google.gson.Gson
import com.nznlabs.familymap240.session.SessionManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import requests.LoginRequest
import requests.RegisterRequest
import results.LoginResult
import results.RegisterResult
import timber.log.Timber
import util.IO.readString
import util.IO.writeString
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class ServerProxy : KoinComponent {

    private val sessionManager: SessionManager by inject()
    private val gson: Gson by inject()

    lateinit var serverHost: String
    lateinit var serverPort: String

    fun login(request: LoginRequest): LoginResult {
        var loginResult: LoginResult
        val url = URL("http://$serverHost:$serverPort/user/login")
        val http: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            http.requestMethod = "POST"
            http.doOutput = true
//            http.addRequestProperty("Authorization", "afj232hj2332")
            http.addRequestProperty("Accept", "application/json")
            http.connect()

            val reqData: String = gson.toJson(request)
            val reqBody: OutputStream = http.outputStream
            writeString(reqData, reqBody)
            reqBody.close()

            if (http.responseCode == HttpURLConnection.HTTP_OK) {
                val respBody: InputStream = http.inputStream
                val respData: String = readString(respBody)
                Timber.d("login response: $respData")
                loginResult = gson.fromJson(respData, LoginResult::class.java)
            } else {
                Timber.d("ERROR: " + http.responseMessage)
                val respBody: InputStream = http.errorStream
                val respData: String = readString(respBody)
                Timber.d("login error response: $respData")
                loginResult = gson.fromJson(respData, LoginResult::class.java)
            }
        } catch (e: IOException) {
            loginResult = LoginResult("ERROR: Connection refused during login.", false)
            e.printStackTrace()
        } finally {
            http.disconnect()
        }
        return loginResult
    }

    fun register(request: RegisterRequest): RegisterResult {
        var registerResult: RegisterResult
        val url = URL("http://$serverHost:$serverPort/user/register")
        val http: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            http.requestMethod = "POST"
            http.doOutput = true
//            http.addRequestProperty("Authorization", "afj232hj2332")
            http.addRequestProperty("Accept", "application/json")
            http.connect()

            val reqData: String = gson.toJson(request)
            val reqBody: OutputStream = http.outputStream
            writeString(reqData, reqBody)
            reqBody.close()

            if (http.responseCode == HttpURLConnection.HTTP_OK) {
                val respBody: InputStream = http.inputStream
                val respData: String = readString(respBody)
                Timber.d("register response: $respData")
                registerResult = gson.fromJson(respData, RegisterResult::class.java)
            } else {
                Timber.d("ERROR: " + http.responseMessage)
                val respBody: InputStream = http.errorStream
                val respData: String = readString(respBody)
                Timber.d("register error response: $respData")
                registerResult = gson.fromJson(respData, RegisterResult::class.java)
            }
        } catch (e: IOException) {
            registerResult = RegisterResult("ERROR: Connection refused during registration.", false)
            e.printStackTrace()
        } finally {
            http.disconnect()
        }
        return registerResult
    }

//    fun getPeople(request: PeopleRe): LoginResult {
//        try {
//            val url = URL("http://$serverHost:$serverPort/routes/claim")
//            val http: HttpURLConnection = url.openConnection() as HttpURLConnection
//            http.requestMethod = "GET"
//            http.doOutput = false
//            http.addRequestProperty("Authorization", "afj232hj2332")
//            http.addRequestProperty("Accept", "application/json")
//            http.connect()
//
//            if (http.responseCode == HttpURLConnection.HTTP_OK) {
//                val respBody: InputStream = http.inputStream
//                val respData: String = readString(respBody)
//                Timber.d(respData)
//            } else {
//                Timber.d("ERROR: " + http.responseMessage)
//                val respBody: InputStream = http.errorStream
//                val respData: String = readString(respBody)
//                Timber.d(respData)
//            }
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//
//    fun getEvents(): LoginResult {
//
//    }


}