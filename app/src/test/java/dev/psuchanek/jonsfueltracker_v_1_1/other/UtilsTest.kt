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

    @Test
    fun `test getTimePeriodTimestamp works`() {
        val one = getTimePeriodTimestamp(TimePeriod.THREE_MONTHS)
        val two = getTimePeriodTimestamp(TimePeriod.SIX_MONTHS)
        val three = getTimePeriodTimestamp(TimePeriod.ONE_YEAR)
        val four = getTimePeriodTimestamp(TimePeriod.THREE_YEARS)
        println(one)
        println(two)
        println(three)
        println(four)

        //Then
        assertThat((one > two) && (two > three) && (three > four)).isTrue()
    }


}