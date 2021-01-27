package dev.psuchanek.jonsfueltracker_v_1_1.other

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UtilsTest {

    @Test
    fun testStringToTimestampConverter() {
        //Given
        val stringDate = "2021-01-27 00:00:00"
        //When
        val result = stringDate.convertDateStringToTimestamp()
        //Then
        println(result)
        assertThat(result).isInstanceOf(java.lang.Long::class.java)

    }

    @Test
    fun testTimestampToDateString() {
        //Given
        val stringDate = "2021-01-27 00:00:00"

        //When
        val timestamp = stringDate.convertDateStringToTimestamp()
        val result = timestamp.convertTimestampToDateString()

        //Then
        assertThat(result).isEqualTo(stringDate)
    }

}