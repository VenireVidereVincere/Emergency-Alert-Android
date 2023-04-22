package com.example.emergencyalert.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.emergencyalert.Contact
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

object Utils {

    fun requestPermissions(activity: Activity) {
        var missingPerms = emptyArray<String>()
        var isMissingPerms = 0
        val permissionAll = 111
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        )
        for (permission in permissions){
            if (!hasPermissions(activity, permission)) {
                missingPerms = missingPerms.plus(permission)
                isMissingPerms +=1
            }
        }
        if(isMissingPerms > 0){
            ActivityCompat.requestPermissions(activity, missingPerms, permissionAll)
        } else {
            Toast.makeText(activity,"All permissions checked. If you denied any, the app won't work properly.",
                Toast.LENGTH_LONG).show()
        }
    }
    fun loadContacts(context: Context): MutableList<Contact> {
        val contactsFile = File(context.filesDir, "contacts.txt")
        if (contactsFile.exists()) {
            return readFromFile(contactsFile)
        }
        return mutableListOf()
    }

    fun writeToFile(contactsFile: File, content: String) {
        val writer = BufferedWriter(FileWriter(contactsFile))
        writer.write(content)
        writer.close()
    }

    private fun readFromFile(contactsFile: File): MutableList<Contact> {
        val reader = BufferedReader(FileReader(contactsFile))
        val content = reader.readText()
        reader.close()
        return Gson().fromJson(content, object : TypeToken<MutableList<Contact>>() {}.type)
    }

    fun hasPermissions(context: Context, perm: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
    }

    fun sendEmergencyMessages(smsManager: SmsManager, emergencyContacts: MutableList<Contact>, message: String) {
        // Code to send the emergency text messages
        val phoneNumbers: List<String> = emergencyContacts.map { it.phoneNumber }
        for (phoneNumber in phoneNumbers) {
            smsManager.sendTextMessage(phoneNumber, null, message,null, null)
        }
    }
}