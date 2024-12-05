package com.example.notetaking

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editPassword: EditText
    private lateinit var spinnerTheme: Spinner
    private lateinit var btnSaveSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize views
        editFirstName = findViewById(R.id.edit_first_name)
        editLastName = findViewById(R.id.edit_last_name)
        editPassword = findViewById(R.id.edit_password)
        spinnerTheme = findViewById(R.id.spinner_theme)
        btnSaveSettings = findViewById(R.id.btn_save_settings)

        // Load current settings
        loadSettings()

        // Save button click listener
        btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE)
        editFirstName.setText(sharedPreferences.getString("first_name", ""))
        editLastName.setText(sharedPreferences.getString("last_name", ""))
        spinnerTheme.setSelection(sharedPreferences.getInt("theme", 0))
    }

    private fun saveSettings() {
        val firstName = editFirstName.text.toString()
        val lastName = editLastName.text.toString()
        val password = editPassword.text.toString()
        val theme = spinnerTheme.selectedItemPosition

        if (firstName.isBlank() || lastName.isBlank() || password.isBlank()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        // Save settings locally
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("first_name", firstName)
        editor.putString("last_name", lastName)
        editor.putString("password", password)
        editor.putInt("theme", theme)
        editor.apply()

        // Optionally save settings to the server using an API request
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
    }
}
