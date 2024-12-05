package com.example.notetaking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreateAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val firstNameEditText: EditText = findViewById(R.id.firstName)
        val lastNameEditText: EditText = findViewById(R.id.lastName)
        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val createButton: Button = findViewById(R.id.createButton)

        createButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                // Save the account details to SharedPreferences
                saveAccountDetails(email, password, this)

                // Call API to create the account and send temporary password
                ApiClient.createAccount(this, firstName, lastName, email, password, {
                    Toast.makeText(this, "Temporary password sent to your email.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, FinishRegistrationActivity::class.java)
                    startActivity(intent)
                }, { error ->
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Helper function to save the email and password to SharedPreferences
    private fun saveAccountDetails(email: String, password: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()  // Save the data asynchronously
    }
}
