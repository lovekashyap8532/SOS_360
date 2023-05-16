package com.example.sos360

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.abs


class Background : Service(), SensorEventListener, LocationListener {
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var stopClicked = false
    private var lastUpdate = 0L
    private var last_x = 0.0f
    private var last_y = 0.0f
    private var last_z = 0.0f
    private var locationManager: LocationManager? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null
    private val phoneNumbers = listOf("8532069310") // Replace with your list of phone numbers
    private var smsCountMap = mutableMapOf<String, Int>()
    private val handler = Handler(Looper.getMainLooper())

    // Add variables for notification and its actions
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var notification: Notification
    private lateinit var notificationManager: NotificationManager
    private val NOTIFICATION_CHANNEL_ID = "ForegroundServiceChannel"
    private val NOTIFICATION_CHANNEL_NAME = "Foreground Service Channel"
    private val NOTIFICATION_ID = 1
    private val ACTION_OK = "com.example.sos360.action.OK"
    private val ACTION_CANCEL = "com.example.sos360.action.CANCEL"

    companion object {
        const val LOCATION_REFRESH_TIME: Long = 3000 // 3 seconds
        const val LOCATION_REFRESH_DISTANCE: Float = 10f // 10 meters
        const val MOTION_THRESHOLD = 800 // adjust this as needed
        const val TAG = "BackgroundService"
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ALL), SensorManager.SENSOR_DELAY_NORMAL)

        if (accelerometer == null) {
            Log.e(TAG, "Accelerometer sensor not found")
        } else {
            sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check if the user has granted location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE,
                this
            )
            fusedLocationClient!!.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Save the location
                    currentLocation = location
                    Log.d(TAG, "Latitude: " + location.latitude + ", Longitude: " + location.longitude)
                }
            }
        } else {
            Log.e(TAG, "Location permission not granted")
        }
        smsCountMap = mutableMapOf()

//        Notification()
    }

    private fun Notification() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ACTION_OK -> {
                        // Handle OK action
                        Toast.makeText(applicationContext, "It is OK", Toast.LENGTH_SHORT).show()
                        sendSMS()
                        makePhoneCall()
//                        notificationManager.cancel(NOTIFICATION_ID)
                        stopForeground(true)


                    }
                    ACTION_CANCEL -> {
                        Toast.makeText(applicationContext, "It is Cancel", Toast.LENGTH_SHORT).show()

                        // Handle CANCEL action
                        // Get the notification manager
                        val notificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        // Cancel the notification
                        notificationManager.cancel(NOTIFICATION_ID)
                        // Stop the foreground service
                        stopForeground(true)
//                        stopSelf()
                    }
                }
            }
        }

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Motion Detected")
            .setContentText("If this is false alarm then click on CANCEL ")
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(
                R.mipmap.ic_launcher,
                "OK",
                PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent(ACTION_OK),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .addAction(
                R.mipmap.ic_launcher,
                "CANCEL",
                PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent(ACTION_CANCEL),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()

        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, notification)

        // Register the broadcast receiver
        val filter = IntentFilter()
        filter.addAction(ACTION_OK)
        registerReceiver(broadcastReceiver, filter)
        filter.addAction(ACTION_CANCEL)
        registerReceiver(broadcastReceiver, filter)


//        onDestroy()
//        super.onDestroy()
//        unregisterReceiver(broadcastReceiver)

    }


    override fun onDestroy() {
        super.onDestroy()
        val filter = IntentFilter()
        filter.addAction(ACTION_OK)
        unregisterReceiver(broadcastReceiver)
        filter.addAction(ACTION_CANCEL)
        unregisterReceiver(broadcastReceiver)
        sensorManager?.unregisterListener(this)
        locationManager?.removeUpdates(this)
//        unregisterReceiver(broadcastReceiver)
        stopClicked = true
        stopForeground(true)
        stopSelf()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            if (curTime - lastUpdate > 100) {
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed =
                    abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000

                if (speed > MOTION_THRESHOLD) {
                    Log.e(TAG, "Motion Motion")
                    Notification()
                    // Phone is in motion, send SMS messages
//                    sendSMS()
                }

                last_x = x
                last_y = y
                last_z = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not implemented
    }

    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "Location changed: $location")
        currentLocation = location
    }

    override fun onProviderDisabled(provider: String) {
        // Not implemented
    }

    override fun onProviderEnabled(provider: String) {
        // Not implemented
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Not implemented
    }

    private fun makePhoneCall() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + phoneNumbers[0])
            startActivity(intent)
        }
    }

    private fun sendSMS() {
        // Get the SMS message count for each phone number
        for (phoneNumber in phoneNumbers) {
            val count = smsCountMap[phoneNumber] ?: 0

            // Only send an SMS if the count is less than 3
            if (count < 1) {
                val smsManager = SmsManager.getDefault()

                // Construct the message
                val message =
                    "Help! I need assistance. My current location is: https://www.google.com/maps?q=${currentLocation?.latitude},${currentLocation?.longitude}"
                // Send the message
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)

                // Update the SMS count for the phone number
                smsCountMap[phoneNumber] = count + 1
            }
        }
    }
}

