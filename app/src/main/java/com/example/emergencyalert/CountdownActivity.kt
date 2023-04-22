package com.example.emergencyalert

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.emergencyalert.Utils2
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class CountdownActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient // Reference to location services
    private lateinit var timer: CountDownTimer
    private var emergencyContacts: MutableList<Contact> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.countdown_screen)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) // Location services

        // Get a reference to the "cancel" button
        val cancelButton: Button = findViewById(R.id.cancelButton)

        // Set an OnClickListener on the "cancel" button
        cancelButton.setOnClickListener {
            // Cancel the countdown timer and finish the activity
            timer.cancel()
            finish()
        }

        // Start the countdown timer
        startTimer()
    }

    private fun startTimer() {
        // Get a reference to the TextView that will display the countdown
        val countdownTextView: TextView = findViewById(R.id.timeLeft)

        // Start a countdown timer that will update the TextView every second
        timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                countdownTextView.text = "$secondsRemaining seconds"
            }

            override fun onFinish() {


                // Get the user's last known location
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                var smsManager: SmsManager = SmsManager.getDefault()
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->

                    // Check if the location is not null
                    if (location != null) {
                        // Send the emergency text messages with the location information
                        val mapsLink = "https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
                        emergencyContacts = Utils2.loadContacts(applicationContext)
                        Utils2.sendEmergencyMessages(smsManager, emergencyContacts, mapsLink)
                    } else {
                        // TODO Handle user not having location permissions turned on.
                    }

                    // Finish the activity
                    finish()
                }
            }
        }
        timer.start()
    }
}