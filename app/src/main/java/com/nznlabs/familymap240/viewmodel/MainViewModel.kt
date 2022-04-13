package com.nznlabs.familymap240.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.Polyline
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

    var unfilteredPersons: Map<String, Person> = mapOf()
    var unfilteredEvents: Map<String, Event> = mapOf()
    var unfilteredPersonEvents: Map<String, List<Event>> = mapOf()
    var paternalAncestors: Set<String> = setOf()
    var maternalAncestors: Set<String> = setOf()
    var lines: MutableList<Polyline> = mutableListOf()

    // Live data
    val persons: LiveData<MutableMap<String, Person>> = MutableLiveData()// personID
    val events: LiveData<MutableMap<String, Event>> = MutableLiveData() // personID
    val personEvents: LiveData<MutableMap<String, MutableList<Event>>> = MutableLiveData() // personID
    val settings: LiveData<Settings> = MutableLiveData(Settings())
    val selectedEvent: LiveData<Event> = MutableLiveData()
    val message: LiveData<String?> = MutableLiveData()

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val loginResult: LoginResult = serverProxy.login(loginRequest)
            if (loginResult.success) {
                val errorMessage = getUserData(loginResult.authtoken, loginResult.personID)
                if (errorMessage == null) {
                    initAuthUser(loginResult.authtoken, loginResult.username, loginResult.personID)
                    initUnfilteredData()
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
                    initUnfilteredData()
                    postMessage("Welcome ${sessionManager.rootPerson.firstName} ${sessionManager.rootPerson.lastName}")
                } else {
                    postMessage(errorMessage)
                }
            } else {
                postMessage(registerResult.message)
            }
        }
    }

    fun clearDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = serverProxy.clearDB()
            if (!result.success) {
                postMessage(result.message)
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
        setPersonEvents(eventsResult.data.asList())

        this.persons.value?.let {
            setPaternalAncestors(personMap, rootPersonID)
        }
        this.persons.value?.let {
            setMaternalAncestors(personMap, rootPersonID)
        }

        sessionManager.rootPerson = personMap[rootPersonID]!!
    }


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

    private fun initUnfilteredData() {
        unfilteredPersons = persons.value!!
        unfilteredEvents = events.value!!
        unfilteredPersonEvents = personEvents.value!!
    }

    fun filterData() {
        val settings = settings.value!!
        val filteredPersons = filterPersons(unfilteredPersons, settings)
        setPersons(filteredPersons)

        val filteredEvents = filterEvents(unfilteredEvents, settings)
        setEvents(filteredEvents)
        setPersonEvents(filteredEvents)
    }

    private fun filterPersons(unfilteredPersons: Map<String, Person>, settings: Settings): List<Person> {
        val filteredPersons = mutableListOf<Person>()
        filteredPersons.add(unfilteredPersons[sessionManager.personID]!!)

        if (settings.fatherSide) {
            val persons = paternalAncestors.map { unfilteredPersons[it]!! }
            filteredPersons.addAll(persons)
        }
        if (settings.motherSide) {
            val persons = maternalAncestors.map { unfilteredPersons[it]!! }
            filteredPersons.addAll(persons)
        }
        return filteredPersons
    }

    private fun filterEvents(events: Map<String, Event>, settings: Settings): List<Event> {
        val filteredEvents = mutableListOf<Event>()
        val filteredPersonSet = mutableSetOf<String>()
        for (person in persons.value!!.values) {
            filteredPersonSet.add(person.personID)
        }
        val personFilteredEvents: List<Event> = events.values.filter { filteredPersonSet.contains(it.personID) }

        if (settings.maleEvents) {
            personFilteredEvents.filterTo(filteredEvents) { persons.value!![it.personID]?.gender == "m" }
        }
        if (settings.femaleEvents) {
            personFilteredEvents.filterTo(filteredEvents) { persons.value!![it.personID]?.gender == "f" }
        }
        return filteredEvents
    }

    // SETTERS
    private fun setPersons(newPersons: List<Person>): Map<String, Person> {
        val tmpPersons = mutableMapOf<String, Person>()
        for (person in newPersons) {
            tmpPersons[person.personID] = person
        }
        persons.set(tmpPersons)
        return tmpPersons
    }

    private fun setEvents(newEvents: List<Event>) {
        val tmpEvents = mutableMapOf<String, Event>()
        for (event in newEvents) {
            tmpEvents[event.eventID] = event
        }
        events.set(tmpEvents)
    }

    fun setSelectedEvent(event: Event) {
        selectedEvent.set(event)
    }

    fun setSettings(newSettings: Settings) {
        settings.set(newSettings)
    }

    private fun setPersonEvents(events: List<Event>) {
        val tmp = mutableMapOf<String, MutableList<Event>>()
        for (event in events) {
            if (tmp[event.personID] != null) {
                tmp[event.personID]!!.add(event)
            } else {
                tmp[event.personID] = mutableListOf(event)
            }
        }

        personEvents.set(tmp)
    }

    private fun setPaternalAncestors(persons: Map<String, Person>, rootPersonID: String) {
        if (persons.isEmpty()) return

        val paternalSet = mutableSetOf<String>()
        val rootPerson: Person = persons[rootPersonID]!!  // adds root user to this set
        rootPerson.fatherID?.let {
            getAncestorHelper(it, persons, paternalSet)
        }

        paternalAncestors = paternalSet
    }

    private fun setMaternalAncestors(persons: Map<String, Person>, rootPersonID: String) {
        if (persons.isEmpty()) return

        val maternalSet = mutableSetOf<String>()
        val rootPerson: Person = persons[rootPersonID]!! // adds root user to this set
        rootPerson.motherID?.let {
            getAncestorHelper(it, persons, maternalSet)
        }

        maternalAncestors = maternalSet
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

    fun addPolyline(line: Polyline) {
        this.lines.add(line)
    }

    fun postMessage(newMessage: String?) {
        message.postValue(newMessage)
    }
}