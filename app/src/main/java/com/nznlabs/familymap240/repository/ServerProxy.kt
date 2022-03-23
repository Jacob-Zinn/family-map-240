package com.nznlabs.familymap240.repository

import com.google.gson.Gson
import requests.LoginRequest
import requests.RegisterRequest
import results.*
import timber.log.Timber
import util.IO.readString
import util.IO.writeString
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class ServerProxy {
    lateinit var serverHost: String
    lateinit var serverPort: String

    private val gson: Gson = Gson()

    fun login(request: LoginRequest): LoginResult {
        var loginResult: LoginResult
        val url = URL("http://$serverHost:$serverPort/user/login")
        val http: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            http.requestMethod = "POST"
            http.doOutput = true
            http.addRequestProperty("Accept", "application/json")
            http.connect()

            val reqData: String = gson.toJson(request)
            val reqBody: OutputStream = http.outputStream
            writeString(reqData, reqBody)
            reqBody.close()

            if (http.responseCode == HttpURLConnection.HTTP_OK) {
                val respBody: InputStream = http.inputStream
                val respData: String = readString(respBody)
                loginResult = gson.fromJson(respData, LoginResult::class.java)
            } else {
                Timber.d("ERROR: " + http.responseMessage)
                val respBody: InputStream = http.errorStream
                val respData: String = readString(respBody)
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
            http.addRequestProperty("Accept", "application/json")
            http.connect()

            val reqData: String = gson.toJson(request)
            val reqBody: OutputStream = http.outputStream
            writeString(reqData, reqBody)
            reqBody.close()

            if (http.responseCode == HttpURLConnection.HTTP_OK) {
                val respBody: InputStream = http.inputStream
                val respData: String = readString(respBody)
                registerResult = gson.fromJson(respData, RegisterResult::class.java)
            } else {
                Timber.d("ERROR: " + http.responseMessage)
                val respBody: InputStream = http.errorStream
                val respData: String = readString(respBody)
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

    fun getPersons(authToken: String): PersonsResult {
        var personsResult: PersonsResult
        val url = URL("http://$serverHost:$serverPort/person")
        val http: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            http.requestMethod = "GET"
            http.doOutput = false
            http.addRequestProperty("Authorization", authToken)
            http.addRequestProperty("Accept", "application/json")
            http.connect()

            if (http.responseCode == HttpURLConnection.HTTP_OK) {
                val respBody: InputStream = http.inputStream
                val respData: String = readString(respBody)
                personsResult = gson.fromJson(respData, PersonsResult::class.java)
            } else {
                Timber.d("ERROR: " + http.responseMessage)
                val respBody: InputStream = http.errorStream
                val respData: String = readString(respBody)
                personsResult = gson.fromJson(respData, PersonsResult::class.java)
            }
            return personsResult
        } catch (e: IOException) {
            personsResult = PersonsResult("ERROR: Unable to retrieve user data", false)
            e.printStackTrace()
        } finally {
            http.disconnect()
        }
        return personsResult
    }

    fun getEvents(authToken: String): EventsResult {
        var eventsResult: EventsResult
        val url = URL("http://$serverHost:$serverPort/event")
        val http: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            http.requestMethod = "GET"
            http.doOutput = false
            http.addRequestProperty("Authorization", authToken)
            http.addRequestProperty("Accept", "application/json")
            http.connect()

            if (http.responseCode == HttpURLConnection.HTTP_OK) {
                val respBody: InputStream = http.inputStream
                val respData: String = readString(respBody)
                eventsResult = gson.fromJson(respData, EventsResult::class.java)
            } else {
                Timber.d("ERROR: " + http.responseMessage)
                val respBody: InputStream = http.errorStream
                val respData: String = readString(respBody)
                eventsResult = gson.fromJson(respData, EventsResult::class.java)
            }
            return eventsResult
        } catch (e: IOException) {
            eventsResult = EventsResult("ERROR: Unable to retrieve user data", false)
            e.printStackTrace()
        } finally {
            http.disconnect()
        }
        return eventsResult
    }

    fun clearDB(): ClearResult{
        var clearResult: ClearResult
        val url = URL("http://$serverHost:$serverPort/clear")
        val http: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            http.requestMethod = "POST"
            http.doOutput = false
            http.addRequestProperty("Accept", "application/json")
            http.connect()

            if (http.responseCode == HttpURLConnection.HTTP_OK) {
                val respBody: InputStream = http.inputStream
                val respData: String = readString(respBody)
                clearResult = gson.fromJson(respData, ClearResult::class.java)
            } else {
                Timber.d("ERROR: " + http.responseMessage)
                val respBody: InputStream = http.errorStream
                val respData: String = readString(respBody)
                clearResult = gson.fromJson(respData, ClearResult::class.java)
            }
        } catch (e: IOException) {
            clearResult = ClearResult("ERROR: Failed to clear database.", false)
            e.printStackTrace()
        } finally {
            http.disconnect()
        }
        return clearResult
    }

}