package com.example.stopwatch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class ForegroundService : Service() {

    private var isServiceStarted = false // запущен ли сервис
    private var notificationManager: NotificationManager? = null // менеджер нотификаций
    private var job: Job? = null // одно из заданий нашей корутины

    private val builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Simple Timer")
            .setGroup("Timer")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent()) // наш путь на возвращение в MainActivity
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_baseline_access_time_24)
    }

    // то что отправляем

    override fun onCreate() {
        super.onCreate()
        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }


    // вызывается когда сервис запускается и передаем туда параметры
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // получаем данные из Intent и определяем что делать (пуск/остановка)
    private fun processCommand(intent: Intent?) {
        when (intent?.extras?.getString(COMMAND_ID) ?: INVALID) {
            COMMAND_START -> {
                val startTime = intent?.extras?.getLong(STARTED_TIMER_TIME_MS) ?: return
                commandStart(startTime)
            }
            COMMAND_STOP -> commandStop()
            INVALID -> return
        }
    }

    private fun commandStart(startTime: Long) {

        if (isServiceStarted)
            return

        try {
            moveToStartedState()
            startForegroundAndShowNotification()
            continueTimer(startTime)
        } finally {
            isServiceStarted = true
        }

    }

    // отсчет времени в нотификации
    private fun continueTimer(startTime: Long) {
        var time = startTime
        var task = true
        job = GlobalScope.launch(Dispatchers.Main) {

            while (task) {
                notificationManager?.notify(
                    NOTIFICATION_ID,
                    getNotification((time).displayTime()))
                delay(INTERVAL)
                time -= INTERVAL

                if (time <= 0L){
                    task = false
                }

            }

            notificationManager?.notify(
                NOTIFICATION_ID,
                getNotification("Timer Stop"))

            notificationManager?.notify(
                NOTIFICATION_ID,
                getNotificationBackground())

        }
    }

    // остановка работы
    private fun commandStop() {

        if (!isServiceStarted)
            return

        try {
            job?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }

    }

    // была команда на старт
    private fun moveToStartedState() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            startService(Intent(this, ForegroundService::class.java))
        }

    }

    // создаем канал
    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = getNotification("content")
        startForeground(NOTIFICATION_ID, notification)
    }


    private fun getNotification(content: String) = builder.setContentText(content).build()

    private fun getNotificationBackground() = builder.setColor(Color.MAGENTA).build()




    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "pomodoro"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }


}