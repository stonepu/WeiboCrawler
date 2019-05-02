package com.neo.sk.todos2018.utils

import java.text.SimpleDateFormat
/**
  * User: sky
  * Date: 2018/5/24
  * Time: 15:31
  */
object TimeUtil {

  def date2TimeStamp(date: String): Long = {
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime
  }

  def timeStamp2Date(timestamp: Long) = {
    new SimpleDateFormat("yyyyMMddHHmmss").format(timestamp)
  }

  def timeStamp2yyyyMMdd(timestamp: Long) = {
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp)
  }

  def timestamp2DateOnly(timestamp: Long): String = {
    new SimpleDateFormat("yyyy/MM/dd").format(timestamp)
  }

  def date2Timestamp(date: String): Long = {
    new SimpleDateFormat("yyyy/MM/dd").parse(date).getTime
  }

  def getTodayZeroStamp: Long = {
    val curTime = System.currentTimeMillis()
    val date = TimeUtil.timestamp2DateOnly(curTime).split("/").take(3).mkString("-")
    val zeroStamp = TimeUtil.date2TimeStamp(date + " 00:00:00")
    zeroStamp
  }

  def getTodayStamp(date: String): Long = {
    val curTime = System.currentTimeMillis()
    val dates = TimeUtil.timestamp2DateOnly(curTime).split("/").take(3).mkString("-")
    val todayStamp = TimeUtil.date2TimeStamp(dates + " " + date + ":00")
    todayStamp
  }

  def minAgo(date: Int): Long = {
    val curTime = System.currentTimeMillis()
    curTime - date * 1000 * date * 60
  }

  def addYear(date: String): Long = {//MM月dd日 hh:mm
    val curTime = System.currentTimeMillis()
    val yy = TimeUtil.timestamp2DateOnly(curTime).split("/")(0)
    val time = yy + "-" + date.replaceAll("月", "-").replaceAll("日","") + ":00"
    TimeUtil.date2TimeStamp(time)
  }

}
