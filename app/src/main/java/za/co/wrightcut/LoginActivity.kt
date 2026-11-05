package za.co.wrightcut

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import za.co.wrightcut.data.repository.WrightCutRepository

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.credentials.CustomCredential


class LoginActivity : AppCompatActivity() {
    private lateinit var repository: WrightCutRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        repository = WrightCutRepository(this)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val googleBtn = findViewById<Button>(R.id.googleBtn)
        val registerLink = findViewById<TextView>(R.id.registerLink)

        loginBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    val result = repository.login(email, password)

                    if (result.isSuccess) {

                        // Retrieve the customer's profile through the Firestore REST API
                        val restResult = repository.getCustomerProfileViaRest()

                        if (restResult.isSuccess) {

                            val profileJson = restResult.getOrNull()

                            val fullName = profileJson
                                ?.getAsJsonObject("fields")
                                ?.getAsJsonObject("fullName")
                                ?.get("stringValue")
                                ?.asString ?: "Customer"

                            Toast.makeText(
                                this@LoginActivity,
                                "REST API Success! Welcome $fullName",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {

                            Toast.makeText(
                                this@LoginActivity,
                                "Login successful, but REST API request failed: " +
                                        (restResult.exceptionOrNull()?.message ?: "Unknown error"),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        // Continue to the main screen
                        startActivity(
                            Intent(this@LoginActivity, MainActivity::class.java)
                        )
                        finish()

                    } else {

                        Toast.makeText(
                            this@LoginActivity,
                            result.exceptionOrNull()?.localizedMessage ?: "Login failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }
        }


        googleBtn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val credentialManager =
                        CredentialManager.create(this@LoginActivity)

                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(
                            "433121581963-copegrdohf4vn7o04aae94r99qsisrnh.apps.googleusercontent.com"
                        )
                        .setAutoSelectEnabled(false)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val response = credentialManager.getCredential(
                        context = this@LoginActivity,
                        request = request
                    )

                    val credential = response.credential

                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        val result = repository.loginWithGoogle(
                            googleCredential.idToken
                        )

                        if (result.isSuccess) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Google Sign-In successful!",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                            )
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                result.exceptionOrNull()?.localizedMessage
                                    ?: "Google authentication failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Unsupported Google credential",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: GetCredentialException) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Google Sign-In cancelled or unavailable: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Google Sign-In error: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
