package com.example.notetaking

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ShareActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var documentIdEditText: EditText
    private lateinit var shareButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        // Initialize UI components
        emailEditText = findViewById(R.id.email_edit_text)
        documentIdEditText = findViewById(R.id.document_id_edit_text)
        shareButton = findViewById(R.id.share_button)

        // Set click listener for Share button
        shareButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val documentId = documentIdEditText.text.toString().trim()

            if (email.isEmpty() || documentId.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else {
                shareDocument(email, documentId)
            }
        }
    }

    private fun shareDocument(email: String, documentId: String) {
        // Retrieve token from SharedPreferences
        val authToken = getTokenFromSharedPreferences(this)

        if (authToken.isEmpty()) {
            Toast.makeText(this, "Authentication token is missing. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        // Call ApiClient to share the document
        ApiClient.shareDocument(
            context = this,
            documentId = documentId,
            accessors = listOf(email),
            authToken = authToken,
            onSuccess = { token ->
                handleSuccessResponse(token)
            },
            onError = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun handleSuccessResponse(response: JSONObject) {
        val success = response.optBoolean("success", false)
        if (success) {
            Toast.makeText(this, "Document shared successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to share document.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTokenFromSharedPreferences(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("authToken", "") ?: ""
    }
}
