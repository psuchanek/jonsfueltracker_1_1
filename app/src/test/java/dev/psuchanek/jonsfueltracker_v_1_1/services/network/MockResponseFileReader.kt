package dev.psuchanek.jonsfueltracker_v_1_1.services.network

import java.io.InputStreamReader

class MockResponseFileReader(path: String) {
    var content: String = ""

    init {
        val reader = InputStreamReader(this.javaClass.getResourceAsStream(path))
        content = reader.readText()
        reader.close()
    }
}