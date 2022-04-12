package com.nznlabs.familymap240.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nznlabs.familymap240.model.Settings
import com.nznlabs.familymap240.repository.ServerProxy
import com.nznlabs.familymap240.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Event
import models.Person
import org.koin.core.component.inject
import requests.LoginRequest
import requests.RegisterRequest
import results.EventsResult
import results.LoginResult
import results.PersonsResult
import results.RegisterResult

class MainViewModel : BaseViewModel() {

    private val sessionManager: SessionManager by inject()
    private val serverProxy: ServerProxy by inject()

    // Live data
    val persons: LiveData<MutableMap<String,Person>> = MutableLiveData()// personID
    val events: LiveData<MutableMap<String, Event>> = MutableLiveData() // personID
    val personEvents: LiveData<MutableMap<String, MutableList<Event>>> = MutableLiveData() // personID
    val paternalAncestors: LiveData<Set<String>> = MutableLiveData() // personID
    val maternalAncestors: LiveData<Set<String>> = MutableLiveData() // personID
    val lines: LiveData<List<LatLng>> = MutableLiveData()
    val settings: LiveData<Settings> = MutableLiveData(Settings())
    val message: LiveData<String?> = MutableLiveData()


    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val loginResult: LoginResult = serverProxy.login(loginRequest)
            if (loginResult.success) {
                val errorMessage = getUserData(loginResult.authtoken, loginResult.personID)
                if (errorMessage == null) {
                    initAuthUser(loginResult.authtoken, loginResult.username, loginResult.personID)
                    postMessage("Welcome ${sessionManager.rootPerson.firstName} ${sessionManager.rootPerson.lastName}")
                } else {
                    postMessage(errorMessage)
                }
            } else {
                postMessage(loginResult.message)
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val registerResult: RegisterResult = serverProxy.register(registerRequest)
            if (registerResult.success) {
                val errorMessage = getUserData(registerResult.authtoken, registerResult.personID)
                if (errorMessage == null) {
                    initAuthUser(registerResult.authtoken, registerResult.username, registerResult.personID)
                } else {
                    postMessage(errorMessage)
                }
            } else {
                postMessage(registerResult.message)
            }
        }
    }

    private suspend fun getUserData(authToken: String, personID: String): String? {
        val personsResult = serverProxy.getPersons(authToken)
        if (!personsResult.success) {
            return personsResult.message
        }
        val eventsResult = serverProxy.getEvents(authToken)
        if (!eventsResult.success) {
            return eventsResult.message
        }

        withContext(Dispatchers.Main) {
            initUserData(personsResult, eventsResult, personID)
        }
        return null
    }

    private fun initUserData(personsResult: PersonsResult, eventsResult: EventsResult, rootPersonID: String) {
        val personMap = setPersons(personsResult.data.asList())
        setEvents(eventsResult.data.asList())
        postPersonEvents(eventsResult.data.asList())

        this.persons.value?.let {
            postPaternalAncestors(personMap, rootPersonID)
        }
        this.persons.value?.let {
            postMaternalAncestors(personMap, rootPersonID)
        }

        sessionManager.rootPerson = personMap[rootPersonID]!!
    }


    // SETTERS
    private fun initAuthUser(newAuthToken: String, newUsername: String, newPersonID: String) {
        sessionManager.authToken.postValue(newAuthToken)
        sessionManager.username = newUsername
        sessionManager.personID = newPersonID
    }

    fun logout() {
        sessionManager.username = null
        sessionManager.personID = null
        sessionManager.authToken.postValue(null)
    }

    private fun setPersons(newPersons: List<Person>): Map<String, Person> {
        val tmpPersons = persons.value?.toMutableMap() ?: mutableMapOf()
        for (person in newPersons) {
            tmpPersons[person.personID] = person
        }
        persons.set(tmpPersons)
        return tmpPersons
    }

    private fun setEvents(newEvents: List<Event>) {
        val tmpEvents = events.value?.toMutableMap() ?: mutableMapOf()
        for (event in newEvents) {
            tmpEvents[event.eventID] = event
        }
        events.set(tmpEvents)
    }

    fun setSettings(newSettings: Settings) {
        settings.set(newSettings)
    }

    private fun postPersonEvents(events: List<Event>) {
        val tmp = personEvents.value?.toMutableMap() ?: mutableMapOf()
        for (event in events) {
            if (tmp[event.personID] != null) {
                tmp[event.personID]!!.add(event)
            } else {
                tmp[event.personID] = mutableListOf(event)
            }
        }

        personEvents.postValue(tmp)
    }

    private fun postPaternalAncestors(persons: Map<String,Person>, rootPersonID: String) {
        if (persons.isEmpty()) return

        val paternalSet = mutableSetOf<String>()
        paternalSet.add(rootPersonID)
        val rootPerson: Person = persons[rootPersonID]!!  // adds root user to this set
        rootPerson.fatherID?.let {
            getAncestorHelper(it, persons, paternalSet)
        }

        paternalAncestors.postValue(paternalSet)
    }

    private fun postMaternalAncestors(persons: Map<String,Person>, rootPersonID: String) {
        if (persons.isEmpty()) return

        val maternalSet = mutableSetOf<String>()
        maternalSet.add(rootPersonID)
        val rootPerson: Person = persons[rootPersonID]!! // adds root user to this set
        rootPerson.motherID?.let {
            getAncestorHelper(it, persons, maternalSet)
        }

        maternalAncestors.postValue(maternalSet)
    }

    private fun getAncestorHelper(personID: String, persons: Map<String, Person>, ancestorSet: MutableSet<String>) {
        ancestorSet.add(personID)
        val descendant: Person = persons[personID]!!
        descendant.fatherID?.let {
            getAncestorHelper(it, persons, ancestorSet)
        }
        descendant.motherID?.let {
            getAncestorHelper(it, persons, ancestorSet)
        }
    }

    fun postMessage(newMessage: String?) {
        message.postValue(newMessage)
    }
}