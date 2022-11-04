package com.miaxis.disktest.utils

import java.text.SimpleDateFormat
import java.util.*

const val ONE_DAY = 24 * 60 * 60 * 1000

object TimeUtil {

    fun nowTimeOfMinute(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        return formatter.format(Date())
    }

    fun nowTimeOfSeconds(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        return formatter.format(Date())
    }

    fun nowTimeOfMillisecond(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
        return formatter.format(Date())
    }

    fun nowTimeOfDay(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        return formatter.format(Date())
    }

    fun earlyMorningOfTheDay(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.clear(Calendar.MINUTE)
        c.clear(Calendar.SECOND)
        c.clear(Calendar.MILLISECOND)
        return formatter.format(c.time)
    }

    /**
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     */
    fun oldTimeOfSeconds(distanceDay: Int = 0): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val c = Calendar.getInstance()
        c.timeInMillis = c.timeInMillis + ONE_DAY * distanceDay
        return formatter.format(c.time)
    }
}