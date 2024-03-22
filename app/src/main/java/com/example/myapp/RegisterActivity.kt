package com.example.myapp

import android.annotation.SuppressLint
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

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_part1)

        auth = FirebaseAuth.getInstance()
        val nextButton: Button = findViewById(R.id.next_btn)
        val nameField: EditText = findViewById(R.id.name_input)
        val surnameField: EditText = findViewById(R.id.surname_input)
        val usernameField: EditText = findViewById(R.id.username_input)
        val emailField: EditText = findViewById(R.id.email_input)
        val passwordField: EditText = findViewById(R.id.password_input)
        val verifyPasswordField: EditText = findViewById(R.id.verify_password_input)
        val nameError: TextView = findViewById(R.id.nameError)
        val surnameError: TextView = findViewById(R.id.surnameError)
        val usernameError: TextView = findViewById(R.id.usernameError)
        val emailError: TextView = findViewById(R.id.emailError)
        val passwordError: TextView = findViewById(R.id.passwordError)
        val verifyPasswordError: TextView = findViewById(R.id.verifyPasswordError)

        nextButton.setOnClickListener{
            val nameValue = nameField.text.toString()
            val surnameValue = surnameField.text.toString()
            val usernameValue = usernameField.text.toString()
            val emailValue = emailField.text.toString()
            val passwordValue = passwordField.text.toString()
            val verifyPasswordValue = verifyPasswordField.text.toString()

            if (nameValue.isEmpty()){
                nameError.visibility = View.VISIBLE
                nameError.text = "Acest camp este obligatoriu"
            } else{
                nameError.visibility = View.GONE
            }

            if (surnameValue.isEmpty()){
                surnameError.visibility = View.VISIBLE
                surnameError.text = "Acest camp este obligatoriu"
            } else{
                surnameError.visibility = View.GONE
            }

            if (usernameValue.isEmpty()){
                usernameError.visibility = View.VISIBLE
                usernameError.text = "Acest camp este obligatoriu"
            } else{
                usernameError.visibility = View.GONE
            }

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

            if (verifyPasswordValue.isEmpty()){
                verifyPasswordError.visibility = View.VISIBLE
                verifyPasswordError.text = "Acest camp este obligatoriu"
            } else{
                verifyPasswordError.visibility = View.GONE
            }

            if (surnameValue.isNotEmpty() && nameValue.isNotEmpty() && usernameValue.isNotEmpty() && emailValue.isNotEmpty() && passwordValue.isNotEmpty() && verifyPasswordValue.isNotEmpty()) {
                registerUser(nameValue, surnameValue, usernameValue, emailValue, passwordValue)
            }
        }
    }

    private fun registerUser(nameValue:String, surnameValue: String, usernameValue:String, emailValue: String, passwordValue:String){
        auth.createUserWithEmailAndPassword(emailValue, passwordValue)
            .addOnCompleteListener(this) {action ->
                if (action.isSuccessful) {
                    val intent = Intent(this, RegisterActivity4::class.java)
                    intent.putExtra("NAME_KEY", nameValue)
                    intent.putExtra("SURNAME_KEY", surnameValue)
                    intent.putExtra("USERNAME_KEY", usernameValue)
                    intent.putExtra("EMAIL_KEY", emailValue)
                    intent.putExtra("PASSWORD_KEY", passwordValue)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Pas 2 esec", Toast.LENGTH_SHORT).show()
                }
            }
    }
}