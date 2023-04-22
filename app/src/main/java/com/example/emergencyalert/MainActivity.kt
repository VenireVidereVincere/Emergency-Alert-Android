package com.example.emergencyalert

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import com.example.emergencyalert.CountdownActivity
import com.example.emergencyalert.ContactsActivity
import com.example.emergencyalert.Utils2

data class Contact(val id: String,
                   val name: String,
                   val phoneNumber: String,
                   var personalizedMessage: String = "EMERGENCY! I need your help, my location at the moment this message is sent is:") : Serializable

class MainActivity : AppCompatActivity() {

    private var contacts: MutableList<Contact> = mutableListOf()

    //contact pick code
    private val CONTACT_PICK_CODE = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var contactsFile = File(applicationContext.filesDir, "contacts.txt")
        // Attempt to create a new file for the emergency contacts to be stored when the app starts for the first time.
        // If the file already exists, load the file contents and store them to global contacts var.
        if (contactsFile.createNewFile()) {
            // New file created
            var initContacts: MutableList<Contact> = mutableListOf()
            val jsonString = Gson().toJson(initContacts)
            Utils2.writeToFile(contactsFile,jsonString)
        } else {
            // File already exists
            Utils2.loadContacts(applicationContext)
        }

        val emergencyButton = findViewById<Button>(R.id.emergencyButton) as Button
        emergencyButton.setOnClickListener {
            contacts = Utils2.loadContacts(applicationContext)
            val intent = Intent(this, CountdownActivity::class.java)
            startActivity(intent)
        }

        // Button to start Manage Contacts activity.
        val buttonContacts = findViewById<Button>(R.id.manageContactsButton)
        buttonContacts.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }

        // Button to request permissions, recommended for the user to grant all permissions required upfront to avoid hassle during an emergency.
        val permissionsButton = findViewById<Button>(R.id.permissionsButton) as Button
        permissionsButton.setOnClickListener {
            Utils2.requestPermissions(this)
        }

        val addContactButton = findViewById<Button>(R.id.contactsButton)
        addContactButton.setOnClickListener {
            // Check if the app already has the necessary perms. If not, request them. If yes, then start contact intent.
            if(!Utils2.hasPermissions(this,Manifest.permission.READ_CONTACTS) or !Utils2.hasPermissions(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) or !Utils2.hasPermissions(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Utils2.requestPermissions(this)
                // Make a second check to see if the user provided the permissions. If they did, start contact intent. Otherwise, show message asking to grant perms.
                if(Utils2.hasPermissions(this, Manifest.permission.READ_CONTACTS) and Utils2.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) and Utils2.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Utils2.loadContacts(applicationContext)
                    pickContact()
                } else {
                    Toast.makeText(this,"Please grant the requested permissions to select your Emergency Contacts", Toast.LENGTH_LONG).show()
                }
            } else {
                Utils2.loadContacts(applicationContext)
                pickContact()
            }
        }

    }

    // Loads existing contacts from the contacts.txt file to the global contacts var.


    private fun pickContact(){
        //intent ti pick contact
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, CONTACT_PICK_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //handle intent results || calls when user from Intent (Contact Pick) picks or cancels pick contact
        if (resultCode == Activity.RESULT_OK){
            //calls when user click a contact from contacts (intent) list
            if (requestCode == CONTACT_PICK_CODE){
                val contactData = data!!.data
                val c = contentResolver.query(contactData!!,null,null,null,null)
                if(c!!.moveToFirst()){
                    var phoneNumber = ""
                    var emailAddress = ""
                    val name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))

                    var hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == "1"


                    if (hasPhone) {
                        val phones = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(contactId),
                            null
                        )

                        if (phones != null) {
                            while (phones.moveToNext()) {
                                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            }
                            phones.close()
                        }
                    }

                    val isUnique = contacts.none { it.id == contactId }
                    if (isUnique) {
                        var contact = Contact(contactId, name, phoneNumber)
                        contacts.add(contact)
                        Utils2.writeToFile(File(applicationContext.filesDir,"contacts.txt"), Gson().toJson(contacts))
                        Toast.makeText(this,"Emergency Contact added successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this,"The contact is already an Emergency Contact!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        else{
            //cancelled picking contact
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}






