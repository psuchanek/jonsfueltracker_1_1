package dev.psuchanek.jonsfueltracker_v_1_1.utils

open class Event<out T>(private val content: T) {
    var hasBeenHandled: Boolean = false
        private set

    fun getContentIfNotHandled() = if (hasBeenHandled) {
        null
    } else {
        hasBeenHandled = true
        content
    }

    fun peekContent() = content
}