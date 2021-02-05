package dev.psuchanek.jonsfueltracker_v_1_1.services.network

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test


class ApiResponseTest {

    lateinit var server: MockWebServer

    @Before
    fun setup() {
        server = MockWebServer()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `response sends proper body`() {
        server.apply{
            enqueue(MockResponse().setBody(MockResponseFileReader("history.json").content))
        }


    }


}