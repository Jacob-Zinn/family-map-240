package com.nznlabs.familymap240.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import models.Person

class SessionManager() {

    val authToken: LiveData<String?> = MutableLiveData()
    var username: String? = null
    var personID: String? = null

    lateinit var rootPerson: Person
}
