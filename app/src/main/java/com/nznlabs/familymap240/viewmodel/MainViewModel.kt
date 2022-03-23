package com.nznlabs.familymap240.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nznlabs.familymap240.repository.ServerProxy
import com.nznlabs.familymap240.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import requests.LoginRequest
import requests.RegisterRequest
import results.LoginResult
import results.RegisterResult

class MainViewModel : BaseViewModel() {

    private val sessionManager: SessionManager by inject()
    private val serverProxy: ServerProxy by inject()


    // Live data
    val lines: LiveData<List<LatLng>> = MutableLiveData()
    val message: LiveData<String?> = MutableLiveData()


    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val loginResult: LoginResult = serverProxy.login(loginRequest)
            withContext(Dispatchers.Main) {
                if (loginResult.success) {
                    initAuthUser(loginResult.authtoken, loginResult.username, loginResult.personID)
                } else {
                    setMessage(loginResult.message)
                }
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val registerResult: RegisterResult = serverProxy.register(registerRequest)
            withContext(Dispatchers.Main) {
                if (registerResult.success) {
                    initAuthUser(registerResult.authtoken, registerResult.username, registerResult.personID)
                } else {
                    setMessage(registerResult.message)
                }
            }
        }
    }


    // SETTERS
    private fun initAuthUser(newAuthToken: String, newUsername: String, newPersonID: String) {
        sessionManager.authToken.set(newAuthToken)
        sessionManager.username = newUsername
        sessionManager.personID = newPersonID
    }
    private fun setMessage(newMessage: String) {
        message.set(newMessage)
    }
}