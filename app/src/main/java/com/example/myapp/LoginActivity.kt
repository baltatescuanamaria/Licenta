package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val backButton:ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        auth = FirebaseAuth.getInstance()
        val loginButton: Button = findViewById(R.id.login_btn)
        val passwordField: EditText = findViewById(R.id.password_input)
        val emailField: EditText = findViewById(R.id.email_input)
        /*val emailError: TextView = findViewById(R.id.emailError)
        val passwordError: TextView = findViewById(R.id.passwordError)*/

        loginButton.setOnClickListener{
            val emailValue = emailField.text.toString()
            val passwordValue = passwordField.text.toString()

            var hasError = false
            if (emailValue.isEmpty()){
                emailField.setError("Acest camp este obligatoriu")
                hasError = true
            }

            if (passwordValue.isEmpty()){
                passwordField.setError("Acest camp este obligatoriu")
                hasError = true
            }

            if (!hasError) {
                loginUser(emailValue, passwordValue)
            }
        }

        val passwordInputLayout: TextInputLayout = findViewById(R.id.password_input_layout)

        passwordInputLayout.setEndIconOnClickListener {
            val passwordEditText = passwordInputLayout.editText
            if (passwordEditText != null) {
                val currentVisibility = passwordEditText.transformationMethod
                val newVisibility = if (currentVisibility == null) {
                    PasswordTransformationMethod.getInstance()
                } else {
                    null
                }
                passwordEditText.transformationMethod = newVisibility
            }
        }


        val registerButton: Button = findViewById(R.id.register_member)
        registerButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        val forgotPasswordButton: Button = findViewById(R.id.forgot_password)
        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }

    private fun loginUser(emailValue:String, passwordValue: String){
        auth.signInWithEmailAndPassword(emailValue, passwordValue)
            .addOnCompleteListener(this) {action ->
                if (action.isSuccessful) {
                    Toast.makeText(this, "Autentificare reusita", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomescreenActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                } else {
                    Toast.makeText(this, "Datele nu sunt corecte", Toast.LENGTH_SHORT).show()
                }
            }
    }
}


