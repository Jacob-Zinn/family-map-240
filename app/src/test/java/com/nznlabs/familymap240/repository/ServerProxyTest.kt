package com.nznlabs.familymap240.repository

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import requests.LoginRequest
import requests.RegisterRequest


class ServerProxyTest {

    private val serverProxy = ServerProxy()
    private lateinit var loginRequest: LoginRequest
    private lateinit var registerRequest: RegisterRequest

    @Before
    fun setUp() {
        serverProxy.serverHost = "localhost"
        serverProxy.serverPort = "8080"

        serverProxy.clearDB()
        loginRequest = LoginRequest("JacobZinn", "FamilyMapPass")
        registerRequest = RegisterRequest("JacobZinn", "FamilyMapPass", "Jacobpzinn@gmail.com", "Jacob", "Zinn", "m")
    }

    @Test
    fun testRegister() {
        val registerResult = serverProxy.register(registerRequest)
        assertTrue(registerResult.success)
        assertNotNull(registerResult.authtoken)
    }

    @Test
    fun testRegister_failByUsernameInUse() {
        val successResult = serverProxy.register(registerRequest)
        assertTrue(successResult.success)
        val failureResult = serverProxy.register(registerRequest)
        assertFalse(failureResult.success)
        assertEquals("Error: username already in use", failureResult.message)
    }

    @Test
    fun testRegister_failByBadParams() {
        registerRequest.username = null
        val result = serverProxy.register(registerRequest)
        assertFalse(result.success)
        assertEquals("Error: username param not valid", result.message)
    }

    @Test
    fun testLogin() {
        val registerResult = serverProxy.register(registerRequest)
        assertTrue(registerResult.success)

        val loginResult = serverProxy.login(loginRequest)
        assertTrue(loginResult.success)
        assertNotNull(loginResult.authtoken)
    }

    @Test
    fun testLogin_failByIncorrectPassword() {
        val registerResult = serverProxy.register(registerRequest)
        assertTrue(registerResult.success)

        loginRequest.password = "Not Correct"
        val failureResult = serverProxy.login(loginRequest)
        assertFalse(failureResult.success)
        assertEquals("Error: Login failed - incorrect password", failureResult.message)
    }

    @Test
    fun testGetPersons() {
        val registerResult = serverProxy.register(registerRequest)
        assertTrue(registerResult.success)
        assertNotNull(registerResult.authtoken)

        val personsResult = serverProxy.getPersons(registerResult.authtoken)
        assertTrue(personsResult.success)
        assertNotNull(personsResult.data)
        val persons = personsResult.data.asList()

        assertEquals(31, persons.size)
    }

    @Test
    fun testGetPersons_badAuthToken() {
        val registerResult = serverProxy.register(registerRequest)
        assertTrue(registerResult.success)
        assertNotNull(registerResult.authtoken)

        val personsResult = serverProxy.getPersons("random authToken")
        assertFalse(personsResult.success)
        assertEquals("Error: Not Authorized", personsResult.message)
    }

    @Test
    fun testGetEvents() {
        val registerResult = serverProxy.register(registerRequest)
        assertTrue(registerResult.success)
        assertNotNull(registerResult.authtoken)

        val eventsResult = serverProxy.getEvents(registerResult.authtoken)
        assertTrue(eventsResult.success)
        assertNotNull(eventsResult.data)
        val events = eventsResult.data.asList()

        assertEquals(91, events.size)
    }

    @Test
    fun testGetEvents_badAuthToken() {
        val registerResult = serverProxy.register(registerRequest)
        assertTrue(registerResult.success)
        assertNotNull(registerResult.authtoken)

        val eventsResult = serverProxy.getEvents("random authToken")
        assertFalse(eventsResult.success)
        assertEquals("Error: Not Authorized", eventsResult.message)
    }
}