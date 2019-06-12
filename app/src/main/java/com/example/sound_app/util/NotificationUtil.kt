package com.example.sound_app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.example.sound_app.Constants
import com.example.sound_app.R
import com.example.sound_app.TimerActivity
import com.example.sound_app.TimerNotificationActionReceiver
import java.text.SimpleDateFormat
import java.util.*

// Thong bao Timer
class NotificationUtil {
    companion object{
        private const val CHANNEL_ID_TIMER = "menu_timer"
        private const val CHANNEL_NAME_TIMER = "Sound App Timer"
        private const val TIMER_ID = 0

        //Start
        fun showTimerExpired (context: Context){
            val startIntent = Intent(context,TimerNotificationActionReceiver::class.java)
            startIntent.action =Constants.ACTION_START
            val startPendingIntent = PendingIntent.getBroadcast(context,0,startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            nBuilder.setContentTitle("Timer Expired!")
                .setContentText("Do you want to start again?")
                .setContentIntent(getPendingIntentWithStack(context,TimerActivity::class.java))
                .addAction(R.drawable.ic_play_arrow,"Start", startPendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER,true)

            nManager.notify(TIMER_ID,nBuilder.build())
        }

        //Running
        fun showTimerRunning (context: Context, wakeUpTime:Long){
            val stopIntent = Intent(context,TimerNotificationActionReceiver::class.java)
            stopIntent.action =Constants.ACTION_STOP
            val stopPendingIntent = PendingIntent.getBroadcast(context,0,stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            val pauseIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            pauseIntent.action = Constants.ACTION_PAUSE
            val pausePendingIntent = PendingIntent.getBroadcast(context,
                0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            //Dinh dang ngay
            val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            nBuilder.setContentTitle("Timer is Running!")
                .setContentText("End: ${df.format(Date(wakeUpTime))}")
                .setContentIntent(getPendingIntentWithStack(context,TimerActivity::class.java))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop,"Stop", stopPendingIntent)
                .addAction(R.drawable.ic_pause,"Pause", pausePendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER,true)

            nManager.notify(TIMER_ID,nBuilder.build())
        }

        //Paused
        fun showTimerPaused (context: Context){
            val resumeIntent = Intent(context,TimerNotificationActionReceiver::class.java)
            resumeIntent.action =Constants.ACTION_RESUME
            val resumePendingIntent = PendingIntent.getBroadcast(context,0,resumeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            nBuilder.setContentTitle("Timer is paused!")
                .setContentText("Do you want to resume?")
                .setContentIntent(getPendingIntentWithStack(context,TimerActivity::class.java))
                .setOngoing(true)
                .addAction(R.drawable.ic_play_arrow,"Resume", resumePendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER,true)

            nManager.notify(TIMER_ID,nBuilder.build())
        }

        //An thong b
        fun hideTimerNotification(context: Context){
            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.cancel(TIMER_ID)
        }

        //
        private fun getBasicNotificationBuilder(context: Context,channelId:String,playSound:Boolean)
            :NotificationCompat.Builder{
                val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val nBuilder = NotificationCompat.Builder(context,channelId)
                    .setSmallIcon(R.drawable.ic_timer)
                    .setAutoCancel(true)
                    .setDefaults(0)
                if(playSound) nBuilder.setSound(notificationSound)
                return nBuilder
            }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        private fun <T> getPendingIntentWithStack(context: Context, javaClass: Class<T>): PendingIntent{
            val resultsIntent = Intent(context,javaClass)
            resultsIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultsIntent)

            return stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }


        private fun NotificationManager.createNotificationChannel(channelID: String,
                                                                  channelName: String,
                                                                  playSound: Boolean){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
                else NotificationManager.IMPORTANCE_LOW
                val nChannel = NotificationChannel(channelID, channelName, channelImportance)
                nChannel.enableLights(true)
                nChannel.lightColor = Color.GREEN
                this.createNotificationChannel(nChannel)
            }
        }
    }
}