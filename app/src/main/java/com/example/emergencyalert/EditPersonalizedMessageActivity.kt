package com.example.emergencyalert

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.File

class EditPersonalizedMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_personalized_message)

        val contact = intent.getSerializableExtra("CONTACT") as? Contact
        val contactsList = intent.getSerializableExtra("CONTACT_LIST") as? MutableList<Contact>
        val nameTextView: TextView = findViewById(R.id.textViewContactName)
        val phoneTextView: TextView = findViewById(R.id.textViewContactNumber)
        val confirmPersonalizedMessageButton: Button = findViewById(R.id.confirmPersonalizedMessageButton)
        val personalizedMessageEditText: EditText = findViewById(R.id.editTextPersonalizedMessage)

        nameTextView.text = contact!!.name
        phoneTextView.text = contact.phoneNumber
        personalizedMessageEditText.setText(contact.personalizedMessage)

        confirmPersonalizedMessageButton.setOnClickListener{
            val personalizedMessage = personalizedMessageEditText.text.toString()
            contact.personalizedMessage = personalizedMessage
            val index = contactsList!!.indexOfFirst { it.id == contact.id }
            contactsList[index] = contact
            Utils2.writeToFile(File(applicationContext.filesDir, "contacts.txt"),Gson().toJson(contactsList))
            Toast.makeText(this,"Personalized message successfully updated!", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}