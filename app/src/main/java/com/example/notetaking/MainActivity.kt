package com.example.notetaking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var documentTitle: EditText
    private lateinit var documentContent: EditText
    private lateinit var saveButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var lockButton: ImageButton
    private lateinit var shareButton: ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        documentTitle = findViewById(R.id.document_title)
        documentContent = findViewById(R.id.document_content)
        saveButton = findViewById(R.id.save_button)
        deleteButton = findViewById(R.id.delete_button)
        lockButton = findViewById(R.id.lock_button)
        shareButton = findViewById(R.id.share_button)

        // Save button functionality
        saveButton.setOnClickListener {
            val content = documentContent.text.toString()
            if (content.isNotBlank()) {
                saveDocument(content)
            } else {
                Toast.makeText(this, "Document content is empty!", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete button functionality
        deleteButton.setOnClickListener {
            deleteDocument()
        }

        // Lock button functionality
        lockButton.setOnClickListener {
            lockDocument()
        }

        // Share button functionality
        shareButton.setOnClickListener {
            val intent = Intent(this, ShareActivity::class.java)
            startActivity(intent)
        }
    }

    // Save document to server
    private fun saveDocument(content: String) {
        val title = findViewById<EditText>(R.id.document_title).text.toString()

        if (title.isBlank()) {
            Toast.makeText(this, "Title is required!", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.saveDocument(
            this,
            title,
            content,
            onSuccess = { token ->
                Log.d("MainActivity", "Response: $token")
                Toast.makeText(this, "Document saved successfully!", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                Toast.makeText(this, "Failed to save document: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }


    // Delete document placeholder functionality
    private fun deleteDocument() {
        // Placeholder for delete functionality
        Toast.makeText(this, "Delete functionality not implemented yet.", Toast.LENGTH_SHORT).show()
    }

    // Lock document placeholder functionality
    private fun lockDocument() {
        // Placeholder for lock functionality
        Toast.makeText(this, "Lock functionality not implemented yet.", Toast.LENGTH_SHORT).show()
    }

    // Share document via Intent
//    private fun shareDocument() {
//        val content = documentContent.text.toString()
//        val title = documentTitle.text.toString()
//        if (content.isNotBlank() && title.isNotBlank()) {
//            val shareIntent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_TEXT, "Title: $title\n\n$content")
//                type = "text/plain"
//            }
//            startActivity(Intent.createChooser(shareIntent, "Share document via"))
//        } else {
//            Toast.makeText(this, "Cannot share an empty document.", Toast.LENGTH_SHORT).show()
//        }
//    }

}
