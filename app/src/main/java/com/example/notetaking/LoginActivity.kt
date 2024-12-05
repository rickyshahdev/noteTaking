package com.example.notetaking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var newUserButton: Button
    private lateinit var forgotPasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize the views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        newUserButton = findViewById(R.id.newUserButton)
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton)

        // Set up listeners for the buttons
        loginButton.setOnClickListener {
            // Logic for login here
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call login authentication using ApiClient
            ApiClient.authenticate(
                this,
                email,
                password,
                onSuccess = { token ->
                    // Successful login, navigate to the main screen or next activity
                    Toast.makeText(this, "Login successful. Token: $token", Toast.LENGTH_SHORT).show()
                    Log.d("LoginActivity", "Response: $token")
                    // Navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()  // Finish this activity to prevent going back to LoginActivity
                },
                onError = { errorMessage ->
                    // Show error message if login fails
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }

        // New User Button Click
        newUserButton.setOnClickListener {
            // Redirect to Register Activity (CreateAccountActivity)
            val intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
        }

        // Forgot Password Button Click
        forgotPasswordButton.setOnClickListener {
            // Redirect to Forgot Password Activity
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }


}
