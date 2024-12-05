package com.example.notetaking

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPassword : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var tempCodeEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Initialize the views
        emailEditText = findViewById(R.id.emailEditText)
        tempCodeEditText = findViewById(R.id.tempCodeEditText)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        resetButton = findViewById(R.id.resetButton)

        // Fetch the email from the Intent or SharedPreferences
        val email = intent.getStringExtra("email") ?: getEmailFromPreferences()
        if (!email.isNullOrBlank()) {
            emailEditText.setText(email)
        }

        resetButton.setOnClickListener {
            val enteredEmail = emailEditText.text.toString().trim()
            val temp_password = tempCodeEditText.text.toString().trim()
            val password = newPasswordEditText.text.toString().trim()

            // Validate inputs
            if (enteredEmail.isBlank() || temp_password.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call the API to reset the password
            ApiClient.registerAccount(
                context = this,
                email = enteredEmail,
                password = password,
                temp_password = temp_password, // Temporary code or password
                onSuccess = {
                    // Show success message
                    Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity and return to the previous screen
                },
                onError = { error ->
                    // Show error message
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Helper function to fetch email from SharedPreferences
    private fun getEmailFromPreferences(): String? {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("email", null)
    }

    // Helper function to validate the new password
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6 // Add additional validation rules if required
    }
}
