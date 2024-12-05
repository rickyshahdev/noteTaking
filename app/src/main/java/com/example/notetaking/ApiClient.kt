package com.example.notetaking

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.UUID
import kotlin.math.log

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:12345"
    private var MY_TOKEN: String? = null


    private fun saveTokenToSharedPreferences(
        context: Context,
        token: String,
        owner: String,
        ttl: Long,
        creationDate: Long
    ) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("autho_token", token)
            .putString("auth_owner", owner)
            .putLong("auth_ttl", ttl)
            .putLong("auth_creation_date", creationDate)
            .apply()
    }

    fun getTokenFromSharedPreferences(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("autho_token", "") ?: ""
    }


    fun createAccount(
        context: Context,
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val params = JSONObject()
        params.put("method", "createAccount")
        params.put("first_name", firstName)
        params.put("last_name", lastName)
        params.put("email", email)
        params.put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST, "$BASE_URL/register", params,
            { onSuccess() },
            { error -> onError(error.message ?: "Registration error occurred") }
        )
        Volley.newRequestQueue(context).add(request)
    }

    fun registerAccount(
        context: Context,
        password: String,
        temp_password: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "http://10.0.2.2:12345"

        val jsonBody = JSONObject().apply {
            put("method", "registerAccount")
            put("password", password)
            put("temp_password", temp_password)
            put("email", email)
        }

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            { response ->
                onSuccess()
            },
            { error ->
                onError(error.message ?: "An error occurred")  // Handle errors
            }
        )

        // Add to the Volley request queue
        Volley.newRequestQueue(context).add(request)
    }


    /**
     * Authenticate user and fetch a time-based access token.
     */
    fun authenticate(
        context: Context,
        email: String,
        password: String,
        timeSpan: Int = 3000,
        timeUnit: String = "SECONDS",
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Construct the JSON body
        val jsonBody = JSONObject()
        jsonBody.put("method", "authenticate")
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        jsonBody.put("time_span", timeSpan)
        jsonBody.put("time_unit", timeUnit)

        // Create the request
        val request = object : JsonObjectRequest(
            Method.POST,
            "$BASE_URL/authenticate",
            jsonBody,
            { response ->
                Log.d("ApiClient", "Response: $response")
                try {
                    val token = response.getString("token")
                    val owner = response.getString("owner")
                    val ttl = response.getLong("ttl")
                    val creationDate = response.getLong("creation_date")

                    // Save token and related info to SharedPreferences
                    saveTokenToSharedPreferences(context, token, owner, ttl, creationDate)
                    onSuccess(token)
                } catch (e: Exception) {
                    onError("Error parsing server response: ${e.message}")
                }
            },
            { error ->
                Log.e("ApiClient", "Error: ${error.message}")
                when (error.networkResponse?.statusCode) {
                    400 -> onError("Invalid request: Check time_span or time_unit.")
                    401 -> onError("Unauthorized: Incorrect email or password.")
                    else -> onError(error.message ?: "An unknown error occurred.")
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                val token = getTokenFromSharedPreferences(context)
                if (token.isNotEmpty()) {
                    headers["authoTable"] = token
                }
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)
    }


    fun forgotPassword(
        context: Context,
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val params = JSONObject()
        params.put("method", "forgotPassword")
        params.put("email", email)

        val request = JsonObjectRequest(
            Request.Method.POST, "$BASE_URL/forgot-password", params,
            { onSuccess() },
            { error -> onError(error.message ?: "Error sending reset email") }
        )
        Volley.newRequestQueue(context).add(request)
    }

    private fun refreshToken(
        context: Context,
        timeSpan: Int = 3000,
        timeUnit: String = "SECONDS",
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Construct the JSON body
        val jsonBody = JSONObject().apply {
            put("method", "refresh")
            put("time_span", timeSpan)
            put("time_unit", timeUnit)
        }

        // Create the request
        val request = object : JsonObjectRequest(
            Request.Method.POST,
            "$BASE_URL/refreshToken",
            jsonBody,
            { response ->
                Log.d("ApiClient", "Response: $response")
                try {
                    val token = response.getString("token")
                    val owner = response.getString("owner")
                    val ttl = response.getLong("ttl")
                    val creationDate = response.getLong("creation_date")

                    // Save the new token and related info to SharedPreferences
                    saveTokenToSharedPreferences(context, token, owner, ttl, creationDate)
                    onSuccess(token)
                } catch (e: Exception) {
                    onError("Error parsing server response: ${e.message}")
                }
            },
            { error ->
                Log.e("ApiClient", "Error: ${error.message}")
                when (error.networkResponse?.statusCode) {
                    400 -> onError("Invalid request: Please check time_span or time_unit.")
                    401 -> onError("Unauthorized: Session expired or invalid credentials.")
                    else -> onError(error.message ?: "An unknown error occurred.")
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                val token = getTokenFromSharedPreferences(context)
                if (token.isNotEmpty()) {
                    headers["autho_token"] = token
                }
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)
    }

    fun saveDocument(
        context: Context,
        title: String,
        content: String,
        documentId: String? = null, // Pass null for new document, or the existing document ID
        timeSpan: Int = 2000,
        timeUnit: String = "SECONDS",
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        // Construct the document JSON object
        Log.d("sharedPreferences123: ", Context.MODE_PRIVATE.toString())
        Log.d("sharedPreferences title: ", title)
        Log.d("sharedPreferences123 text: ", content)

        // Ensure the document ID is being set correctly
        val documentJson = JSONObject().apply {
            val documentIdToUse = documentId ?: generateNewDocumentId()  // Generate new ID if null
            put("id", documentIdToUse) // Ensure the ID is included
            put("title", title)
            put("text", content)
            put("creation_date", System.currentTimeMillis())
        }

        // Log the document to ensure it's correct
        Log.d("ApiClient SaveDocument DocumentJson: ", documentJson.toString())

        // Construct the final JSON request body
        val jsonBody = JSONObject().apply {
            put("method", "setDocument")
            put("document", documentJson)
        }

        // Get token from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("autho_token", "")
        Log.v("ApiClient SaveDocument: Token", token.toString())

        // Function to send the document save request
        fun sendSaveRequest(token: String) {
            val request = object : JsonObjectRequest(
                Request.Method.POST,
                "http://10.0.2.2:12345/setDocument",
                jsonBody,
                { response ->
                    Log.d("ApiClient", "Response: $response")
                    onSuccess(response)
                },
                { error ->
                    Log.e("ApiClient", "Error: ${error.message}")
                    when (error.networkResponse?.statusCode) {
                        401 -> {
                            Log.d("ApiClient", "Token expired or invalid, refreshing...")
                            refreshToken(context, timeSpan, timeUnit, { newToken ->
                                sharedPreferences.edit().putString("autho_token", newToken).apply()
                                sendSaveRequest(newToken)
                            }, onError)
                        }
                        else -> onError(error.message ?: "An unknown error occurred.")
                    }
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    if (token.isNullOrEmpty()) {
                        Log.d("ApiClient", "Token is missing, refreshing...")
                        refreshToken(context, timeSpan, timeUnit, { newToken ->
                            sharedPreferences.edit().putString("autho_token", newToken).apply()
                            headers["autho_token"] = newToken
                        }, onError)
                    } else {
                        headers["autho_token"] = token
                    }
                    return headers
                }
            }

            Volley.newRequestQueue(context).add(request)
        }

        if (token.isNullOrEmpty()) {
            Log.d("ApiClient", "Token missing, refreshing...")
            refreshToken(context, timeSpan, timeUnit, { newToken ->
                sharedPreferences.edit().putString("autho_token", newToken).apply()
                sendSaveRequest(newToken)
            }, onError)
        } else {
            sendSaveRequest(token)
        }
    }



    // Function to generate a new valid UUID
    private fun generateNewDocumentId(): String {
        return UUID.randomUUID().toString()
    }


    fun shareDocument(
        context: Context,
        documentId: String,
        accessors: List<String>,
        authToken: String,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        // Construct the URL and request body
        val url = BASE_URL
        val requestBody = JSONObject().apply {
            put("method", "setDocumentAccessors")
            put("document_id", documentId)
            put("accessors", accessors)
        }

        // Create the request
        val request = object : JsonObjectRequest(
            Request.Method.POST,
            url,
            requestBody,
            { response -> onSuccess(response) },
            { error -> onError(error.message ?: "An error occurred") }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Authorization" to "Bearer $authToken")
            }
        }

        // Add request to Volley queue
        Volley.newRequestQueue(context).add(request)
    }




}

