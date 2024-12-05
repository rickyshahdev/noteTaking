package com.example.notetaking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val emailEditText: EditText = findViewById(R.id.email)
        val resetButton: Button = findViewById(R.id.resetButton)

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString()

            if (email.isNotBlank()) {
                ApiClient.forgotPassword(this, email, {
                    Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ResetPassword::class.java)
                        .apply {
                        putExtra("email", email)
                    }
                    startActivity(intent)
                    finish()
                }, { error ->
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
