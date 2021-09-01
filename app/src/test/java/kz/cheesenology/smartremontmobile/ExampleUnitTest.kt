package kz.cheesenology.smartremontmobile

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun dateCheck() {
        val lastYear = GregorianCalendar()
        lastYear.set(Calendar.DAY_OF_MONTH, 1)
        lastYear.set(Calendar.MONTH, 5)
        lastYear.set(Calendar.YEAR, 2018)

        println(lastYear.get(Calendar.DAY_OF_MONTH))
        println(lastYear.get(Calendar.MONTH))
        println(lastYear.get(Calendar.YEAR))
    }

    @Test
    fun strToDashDate(){
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val pointDate = sdf.parse("01.01.2012")

        val dash = SimpleDateFormat("dd-MM-yyyy")
        val dashDate = dash.format(pointDate)

        println(dashDate)
    }
}
