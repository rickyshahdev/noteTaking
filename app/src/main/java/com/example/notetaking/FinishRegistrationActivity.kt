package com.example.notetaking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FinishRegistrationActivity : AppCompatActivity() {

    private lateinit var tempPasswordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_registration)

        // Initialize the views
        tempPasswordEditText = findViewById(R.id.tempPasswordEditText)
        registerButton = findViewById(R.id.registerButton)

        // Register button click listener
        registerButton.setOnClickListener {
            val tempPassword = tempPasswordEditText.text.toString()

            // Validate the temporary password
            if (isValidTempPassword(tempPassword)) {
                // Get the email from SharedPreferences (assuming it was stored during account creation)
                val email = getEmailFromSharedPreferences()

                // Get the password from SharedPreferences
                val password = getPasswordFromSharedPreferences()

                // Call the API to verify the temporary password and complete the registration
                ApiClient.registerAccount(
                    context = this,
                    password = password,
                    temp_password = tempPassword,
                    email = email,
                    onSuccess = {
                        // Registration is successful, proceed to the next screen (e.g., login or home)
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                        // Redirect to login or home activity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onError = { error ->
                        // Show error message
                        Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                // Show error if the temporary password is invalid
                Toast.makeText(this, "Please enter a valid temporary password with at least one uppercase letter", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Helper function to retrieve the email from SharedPreferences (if stored during account creation)
    private fun getEmailFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", "") ?: ""
    }

    // Helper function to retrieve the password from SharedPreferences (if stored during account creation)
    private fun getPasswordFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("password", "") ?: ""
    }

    // Function to validate the temporary password
    private fun isValidTempPassword(tempPassword: String): Boolean {
        // Ensure the temporary password is not empty, has a minimum length, and contains at least one uppercase letter
        return tempPassword.isNotEmpty() && tempPassword.length >= 6 &&
                tempPassword.contains(Regex("[A-Z]")) // Requires at least one uppercase letter
    }
}
