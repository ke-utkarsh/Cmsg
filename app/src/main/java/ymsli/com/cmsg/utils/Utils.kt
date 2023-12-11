package ymsli.com.cmsg.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Environment
import android.telephony.SubscriptionManager
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import ymsli.com.cmsg.BuildConfig
import ymsli.com.cmsg.R
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.common.UpdateMessageWorkManager
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.database.entity.MessageHeader
import ymsli.com.cmsg.database.entity.MessageListItem
import ymsli.com.cmsg.database.entity.PhoneSIMEntity
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.sql.Timestamp
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Project Name : Couriemate
 * @company YMSLI
 * @author  Sushant Somani (VE00YM129)
 * @date   January 10, 2020
 * Copyright (c) 2019, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * Utils : utils used throughtout the app
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 *
 * -----------------------------------------------------------------------------------
 */
object Utils {

    private val dateFormatter = java.text.SimpleDateFormat(Constants.FORMAT_COURIEMATE_DATE)
    private val currentDateFormmater = java.text.SimpleDateFormat("yyyy-MM-dd")
    private val notificationTimeFormmater =
        java.text.SimpleDateFormat(Constants.NOTIFICATION_TIME_FORMAT)
    private const val TIME_ZONE_KEY_GMT = "GMT"
    private const val MESSAGE_CREATION_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    private const val TRIP_TIME_FORMAT = "hh:mm a"

    /**
     * sends time to API in required format
     */
    fun getCurrentTimeInServerFormat(): String {
        var tempTime = Timestamp(Date().time).toString()
        tempTime = tempTime.replace(" ", "T")
        return tempTime + getTimeZoneOffset()
    }

    /**
     * used to animate progress over the UI
     */
    fun animateProgressBar(progressBar: ProgressBar, duration: Long, progress: Int) {
        ObjectAnimator.ofInt(progressBar, "progress", progress)
            .setDuration(duration)
            .start()
    }

    /**
     * formats time to server format
     */
    fun formatTimestampToServerFormat(timestamp: Timestamp): String {
        return timestamp.toString().replace(" ", "T") + getTimeZoneOffset()
    }

    /**
     * returns difference between local time & GMT
     */
    fun getTimeZoneOffset(): String {
        val rawOffset = ((TimeZone.getDefault().rawOffset) / 1000) / 60
        val hourOffset = rawOffset / 60
        val minuteOffset = rawOffset % 60
        var hourOffsetString = hourOffset.toString()
        var minuteOffsetString = minuteOffset.toString()

        if (hourOffset >= 0) hourOffsetString = ("+" + hourOffset)
        if (hourOffsetString.length < 3) hourOffsetString =
            (hourOffsetString.substring(0, 1) + "0" + hourOffsetString.substring(
                1,
                2
            ))
        if (minuteOffsetString.length < 2) minuteOffsetString = ("0" + minuteOffsetString)
        return hourOffsetString + minuteOffsetString
    }

    /**
     * formats data for filter UI
     */
    fun formatDateForFilterDialog(timestamp: Timestamp): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("YYYY/MM/dd").format(timestamp)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
    }

    /**
     * filters erroneous tasks from task list
     */
//    fun getUpdateSyncList(
//        updateList: List<TaskRetrievalResponse>,
//        errorList: List<TaskRetrievalResponse>
//    ): List<TaskRetrievalResponse> {
//        val map = HashMap<Long, TaskRetrievalResponse>()
//        for (task in updateList) map[task.taskId!!] = task
//        for (task in errorList) map.remove(task.taskId)
//        return map.values.toList()
//    }

    /**
     * returns time offset of GMT
     */
    fun getGMTOffset(): String {
        return Date().toString().substring(Date().toString().indexOf("GMT")).substring(3, 9)
    }


    /**
     * Given a timestamp object this method returns a string representation of date formatted in couriemate format
     * timestamp object to be formatted
     * @author Balraj
     */
    fun formatDate(dateString: String): String {
        val timestamp = Timestamp.valueOf(getDateTimeWithoutTimeZone(dateString))
        val timeZone = getTimeZoneOffset().replace("+", "") //getTimeZone(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = timestamp
        adjustCalenderForTimeZone(calendar, timeZone)
        val formattedDate = dateFormatter.format(calendar.time)
        val indexOfFirstSpace = formattedDate.indexOf(" ")
        val dayOfMonth = Integer.parseInt(formattedDate.substring(0, indexOfFirstSpace))
        val daySuffix = getDaySuffix(dayOfMonth)
        return dayOfMonth.toString() + daySuffix + formattedDate.substring(indexOfFirstSpace)
    }

    /**
     * Helper method to format date.
     * given a day of month it returns the appropriate suffix for that particular day.
     * @param dayOfMonth day of month for which a suffix is required
     * @return day suffix which can be any one of the {st, nd, rd, th}
     * @author Balraj VE00YM023
     */
    private fun getDaySuffix(dayOfMonth: Int): String {
        when (dayOfMonth) {
            1 -> return Constants.DAY_1_SUFFIX
            2 -> return Constants.DAY_2_SUFFIX
            3 -> return Constants.DAY_3_SUFFIX
        }
        return Constants.DAY_DEFAULT_SUFFIX
    }

    /**
     * retrieves current data time with no offset
     */
    private fun getDateTimeWithoutTimeZone(dateString: String): String {
        return dateString.replace("T", " ")
            .substring(0, dateString.indexOf("+"))
    }

    /**
     * returns local time zone
     */
    private fun getTimeZone(dateString: String): String {
        return dateString.substring(dateString.indexOf("+") + 1)
    }

    /**
     * used to show local time, the GMT time is manipulated
     * based on local timezone
     */
    private fun adjustCalenderForTimeZone(calendar: Calendar, timeZone: String) {
        if (isUTC(timeZone)) {
            val localOffset = getTimeZoneOffset().substring(1)
            val hours =
                if (localOffset[0] == '0') localOffset.substring(1, 2) else localOffset.substring(
                    0,
                    2
                )
            val minutes = localOffset.substring(2)
            calendar.add(Calendar.HOUR, hours.toInt())
            calendar.add(Calendar.MINUTE, minutes.toInt())
        }
    }

    private fun isUTC(timeZone: String): Boolean {
        return timeZone == "0000"
    }

    /**
     * This method is used to convert currency values to couriemate format (12,123.12)
     * @param value currency value to be formatted
     * @author Balraj
     */
    fun formatCurrencyValue(value: Double): String {
        if (value.equals(0.0)) {
            return "0.00"
        }
        val formatter = DecimalFormat(Constants.FORMAT_COURIEMATE_CURRENCY)
        return formatter.format(value)
    }

    /**
     * Given a timestamp object this method returns a string representation of date formatted in couriemate format
     * @param timestamp timestamp object to be formatted
     * @author Balraj
     */
    fun formatDate(timestamp: Timestamp): String {
        val formattedDate = dateFormatter.format(timestamp)
        val indexOfFirstSpace = formattedDate.indexOf(" ")
        val dayOfMonth = Integer.parseInt(formattedDate.substring(0, indexOfFirstSpace))
        val daySuffix = getDaySuffix(dayOfMonth)
        return dayOfMonth.toString() + daySuffix + formattedDate.substring(indexOfFirstSpace)

    }

    fun getCurrentDatePrefix(): String = currentDateFormmater.format(Date()) + "%"

    /**
     * returns timestamp of older day
     * passed as parameter in method
     */
    fun getPreviousDayTimeStamp(anotherDay: Int, isPast: Boolean): Timestamp {
        val cal = Calendar.getInstance()
        if (isPast) cal.add(Calendar.DATE, -anotherDay)
        else cal.add(Calendar.DATE, anotherDay)

        val s = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyy-MM-dd")
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Timestamp((SimpleDateFormat("yyyy-MM-dd").parse(s.format(Date(cal.timeInMillis)))).time)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
    }

//    /**
//     * used to filter tasks which
//     * lie between particular dates
//     */
//    fun getTasksBetweenDuration(
//        tasks: ArrayList<TaskRetrievalResponse>,
//        fromDate: Timestamp?,
//        toDate: Timestamp?
//    ): List<TaskRetrievalResponse> {
//        val recentList: List<TaskRetrievalResponse> = ArrayList<TaskRetrievalResponse>()
//        for (t in tasks) {
//            val taskEndDate = t.endDate
//            val temp = getDateTimeWithoutTimeZone(taskEndDate!!)
//            val taskTime = (SimpleDateFormat("yyyy-MM-dd hh:mm").parse(temp))
//            if (taskTime.after(fromDate) && (taskTime.before(toDate))) {
//                (recentList as java.util.ArrayList).add(t)
//            }
//        }
//        val sortedDoneTasks = recentList.sortedWith(ViewUtils.taskListComparatorEndDate)
//        return sortedDoneTasks
//    }

    /**
     * used to filter task history which
     * lie between particular dates
     */
//    fun getTasksHistoryBetweenDuration(
//        tasks: ArrayList<TaskHistoryResponse>,
//        fromDate: Timestamp?,
//        toDate: Timestamp?
//    ): List<TaskHistoryResponse> {
//        val recentList: List<TaskHistoryResponse> = ArrayList<TaskHistoryResponse>()
//        for (t in tasks) {
//            val taskEndDate = t.endDate
//            val temp = getDateTimeWithoutTimeZone(taskEndDate)
//            val taskTime = (SimpleDateFormat("yyyy-MM-dd hh:mm").parse(temp))
//            if (taskTime.after(fromDate) && (taskTime.before(toDate))) {
//                (recentList as java.util.ArrayList).add(t)
//            }
//        }
//        val sortedDoneTasks = recentList.sortedWith(ViewUtils.taskHistoryComparatorEndDate)
//        return sortedDoneTasks
//    }

    /**
     * formats given data as YYYY/MM/dd
     */
    fun formatDateForListItem(dateString: String): String {
        val timestamp = Timestamp.valueOf(getDateTimeWithoutTimeZone(dateString))
        val timeZone = getTimeZone(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = timestamp
        adjustCalenderForTimeZone(calendar, timeZone)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("YYYY/MM/dd").format(calendar.time)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
    }

    /**
     * formats time for device notifications
     */
    fun formatTimeForNotification(timeStamp: String): String {
        val timestamp = Timestamp.valueOf(getDateTimeWithoutTimeZone(timeStamp))
        val timeZone = getTimeZoneOffset().replace("+", "")
        val calendar = Calendar.getInstance()
        calendar.time = timestamp
        adjustCalenderForTimeZone(calendar, timeZone)
        return notificationTimeFormmater.format(calendar.time)
    }

    /**
     * Puts item in spinner when task refuse fragment is active
     */
//    fun getDefaultRefuseReasonData() = ArrayList<DropDownEntity>().apply {
//        add(DropDownEntity(1, "No Money"))
//        add(DropDownEntity(2, "Item not needed anymore"))
//        add(DropDownEntity(3, "Wrong item"))
//        add(DropDownEntity(4, "Doubt item"))
//        add(DropDownEntity(5, "Others"))
//    }

    /**
     * Puts item in spinner when task delivery fragment is active
     */
//    fun getDefaultMobileMoneyTypes() = ArrayList<DropDownEntity>().apply {
//        add(DropDownEntity(1, "MTN"))
//        add(DropDownEntity(2, "Airtel"))
//    }

    /** returns time in milliseconds in GMT Zone */
    fun getTimeInMilliSec(): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE_KEY_GMT))
        return cal.timeInMillis
    }

    /**
     * returns current time in format
     * required to upload file to
     * DAPIoT server
     */
    fun getTimeForLogsFormat(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(Date())
    }

    /** returns time in DAPIoT format specified in DAPIoT document */
//    fun getTimeForFileName(fileNameTime: String): String? {
//        val formatter =
//            android.icu.text.SimpleDateFormat(DAP_IOT_DATE_TIME_FORMAT)
//        val formatterNew =
//            android.icu.text.SimpleDateFormat("YYYYMMddHHmmssSSS")
//        try {
//            return formatterNew.format(formatter.parse(fileNameTime))
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//        return null
//    }

    /** Returns the date part from the given date time instance. */
    fun getDeliveryTime(millis: Long): String {
        return try {
            java.text.SimpleDateFormat(TRIP_TIME_FORMAT).format(millis)
        } catch (cause: Exception) {
            Constants.NA_KEY
        }
    }

    fun getDateTimeForTxHistory(inputDate: String): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(inputDate)
        return java.text.SimpleDateFormat("hh:mm a").format(date)
    }

    fun getTimestampForDate(): Long {
        val calendar: Calendar = GregorianCalendar()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        val d2 = calendar.time
        return d2.time
    }

    fun formatTimestampForUI(millis: Long): String {
        val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyy-MM-dd h:mm a")
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        return formatter.format(millis)
    }

    fun writeToFile(data: String) {
        val finalData = data +" **** APP VERSION IS ****"+BuildConfig.VERSION_NAME
        var logFile: File
        if (Build.VERSION.SDK_INT >= 30) {
            logFile = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/CMsg_SMS_Logs.txt")
        } else {
            logFile = File("/sdcard/CMsg_SMS_Logs.txt")
        }
        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(finalData)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Return the input applyReturnListFilter to disallow any space character.
     */
    fun getSpaceFilter(): InputFilter {
        return spaceFilter
    }

    /**
     * Input applyReturnListFilter implementation to disallow any space character input.
     * this applyReturnListFilter is used by password input fields, so that user can not input any space.
     *
     */
    private val spaceFilter = object : InputFilter {
        override fun filter(source: CharSequence, start: Int, end: Int,
                            dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            for (i in start until end) {
                if (Character.isSpaceChar(source[i])) {
                    return Constants.EMPTY_STRING
                }
            }
            return null
        }
    }

    /**
     * filters message entity based on today, yesterday
     * or older messages and returns filtered list
     */
    fun filterMessageEntityByTime(messageEntityList: List<MessageEntity>,filterType: Int): ArrayList<MessageEntity>{
        var filteredList: ArrayList<MessageEntity> = ArrayList()
        when(filterType){
            FilterDateEnum.TODAY.code -> {
                val todaysTime = getTimestampForDate()
                for(msg in messageEntityList){
                    val d = SimpleDateFormat(MESSAGE_CREATION_TIME_FORMAT).parse(msg.createdOn)
                    val msgTime = d.time
                    if(msgTime>=todaysTime){
                        filteredList.add(msg)
                    }
                }
            }

            FilterDateEnum.YESTERDAY.code -> {
                val todaysTime = getTimestampForDate()
                val yesterdayTime = getYesterdayTimestamp()
                for(msg in messageEntityList){
                    val d = SimpleDateFormat(MESSAGE_CREATION_TIME_FORMAT).parse(msg.createdOn)
                    val msgTime = d.time
                    if(msgTime>=yesterdayTime && msgTime<todaysTime){
                        filteredList.add(msg)
                    }
                }
            }

            FilterDateEnum.OLDER.code -> {
                val yesterdayTime = getYesterdayTimestamp()
                for(msg in messageEntityList){
                    val d = SimpleDateFormat(MESSAGE_CREATION_TIME_FORMAT).parse(msg.createdOn)
                    val msgTime = d.time
                    if(msgTime<yesterdayTime){
                        filteredList.add(msg)
                    }
                }
            }
        }
        return filteredList
    }

    /**
     * generates timestamp of previous day at 12:00AM
     */
    fun getYesterdayTimestamp(): Long{
        val calendar: Calendar = GregorianCalendar()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        calendar.add(Calendar.DATE,-1)

        val d2 = calendar.time
        return d2.time
    }



    /**
     * starts Trip Config Work manager
     */
    fun startSMSUpdateWorkManager(baseContext: Context,smsId: Long){
        val smsIdData = Data.Builder()
        smsIdData.putLong(Constants.SMS_WORKER_TAG,smsId)
        val tripConfig: WorkRequest = OneTimeWorkRequest.Builder(UpdateMessageWorkManager::class.java).setInitialDelay(0,
            TimeUnit.SECONDS).setInputData(smsIdData.build()).build()
        WorkManager.getInstance(baseContext).enqueue(tripConfig)
    }

    fun getMessageItems(messageEntityList: ArrayList<MessageEntity>): ArrayList<MessageListItem>{
        var messageItems: ArrayList<MessageListItem> = ArrayList()
        val todayList = Utils.filterMessageEntityByTime(messageEntityList,FilterDateEnum.TODAY.code)
        if(todayList.isNotEmpty()){
            val header1 = MessageHeader(FilterDateEnum.TODAY.code)
            messageItems.add(header1)
            messageItems.addAll(todayList)
        }
        val yesterdayList = Utils.filterMessageEntityByTime(messageEntityList,FilterDateEnum.YESTERDAY.code)
        if(yesterdayList.isNotEmpty()){
            val header2 = MessageHeader(FilterDateEnum.YESTERDAY.code)
            messageItems.add(header2)
            messageItems.addAll(yesterdayList)
        }

        val olderList = Utils.filterMessageEntityByTime(messageEntityList,FilterDateEnum.OLDER.code)
        if(olderList.isNotEmpty()){
            val header3 = MessageHeader(FilterDateEnum.OLDER.code)
            messageItems.add(header3)
            messageItems.addAll(olderList)
        }
        return messageItems
    }



    public fun showNoSimDialog(activity:Activity) {

        val message = activity.getString(R.string.no_sim_message)
        val titleDialog = activity.getString(R.string.msg_label)
        val actionCancel = {
            activity.finish()
        }
        val actionOk = {

        }
        showPermissionDeniedDialog(message, null, actionCancel, actionOk, titleDialog,activity)
    }


    private fun showPermissionDeniedDialog(
        msg: String, labelAction: String? = Constants.ACTION_OK,
        actionCancel: () -> Unit, actionOk: () -> Unit, titleDialog: String, activity: Activity
    ) {
        val (dialogView, btnOk, btnCancel) = inflateConfirmationDialogView(
            msg,
            labelAction,
            titleDialog,
            activity
        )
        val dialog = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(dialogView)
            .create()
        btnCancel.setOnClickListener {
            actionOk(); dialog.dismiss();
        }
        dialog.show()
    }

    private fun inflateConfirmationDialogView(
        message: String,
        labelOk: String?,
        titleDialog: String,
        activity: Activity
    )
            : Triple<View, Any, Button> {
        val dialogView = activity.layoutInflater.inflate(R.layout.permission_denied, null, false)
        val tvTitle = dialogView.findViewById(R.id.tv_logout) as TextView
        val iconTitle = dialogView.findViewById(R.id.iv_logout) as ImageView
        val tvMessage = dialogView.findViewById(R.id.tv_message) as TextView
        val btnCancel = dialogView.findViewById(R.id.btn_back) as Button
        tvTitle.text = titleDialog
        tvMessage.text = message
        iconTitle.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icon_check))
        return Triple(dialogView, 1, btnCancel)
    }
}

/**
 * Converts values received in format '2020-02-26T17:34:27.071+0000'
 * to '2020-02-26 17:34:27.071'
 * @author Balraj VE00YM023
 */
fun String.toLastSync(): String? {
    if (this.isEmpty() || this.isBlank()) return null
    val indexOfPlus = this.indexOf("+")
    if (indexOfPlus < 0) return null
    return this.substring(0, indexOfPlus).replace("T", " ")
}






