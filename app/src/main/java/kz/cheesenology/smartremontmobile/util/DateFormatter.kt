package kz.cheesenology.smartremontmobile.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    fun pointWithYear(cal: Calendar): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        return dateFormat.format(cal.time)
    }

    fun pointWithYear(date: Date): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        return dateFormat.format(date)
    }

    fun pointWithYearAndTime(date: Date): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        return dateFormat.format(date)
    }

    fun strToDate(str: String): Date {
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        return sdf.parse(str)
    }

    fun strToDashDate(str: String): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val pointDate = sdf.parse(str)

        val dash = SimpleDateFormat("dd-MM-yyyy")
        val dashDate = dash.format(pointDate)

        return dashDate
    }
}