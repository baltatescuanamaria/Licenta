package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val loginButton: Button = findViewById(R.id.login_btn)
        val passwordField: EditText = findViewById(R.id.password_input)
        val emailField: EditText = findViewById(R.id.email_input)
        val emailError: TextView = findViewById(R.id.emailError)
        val passwordError: TextView = findViewById(R.id.passwordError)

        loginButton.setOnClickListener{
            val emailValue = emailField.text.toString()
            val passwordValue = passwordField.text.toString()

            if (emailValue.isEmpty()){
                emailError.visibility = View.VISIBLE
                emailError.text = "Acest camp este obligatoriu"
            } else{
                emailError.visibility = View.GONE
            }

            if (passwordValue.isEmpty()){
                passwordError.visibility = View.VISIBLE
                passwordError.text = "Acest camp este obligatoriu"
            } else{
                passwordError.visibility = View.GONE
            }

            if (emailValue.isNotEmpty() && passwordValue.isNotEmpty()) {
                loginUser(emailValue, passwordValue)
            }
        }

        val registerButton: Button = findViewById(R.id.register_member)
        registerButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }

    private fun loginUser(emailValue:String, passwordValue: String){
        auth.signInWithEmailAndPassword(emailValue, passwordValue)
            .addOnCompleteListener(this) {action ->
                if (action.isSuccessful) {
                    Toast.makeText(this, "Autentificare reușită", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomescreenActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Autentificare eșuată", Toast.LENGTH_SHORT).show()
                }
            }
    }
}


