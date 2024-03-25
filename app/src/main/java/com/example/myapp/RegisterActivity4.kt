package com.example.myapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity4 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_part4)

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()
        val nextButton: Button = findViewById(R.id.register_btn)
        val phoneNumberField: EditText = findViewById(R.id.phone_number_input)
        val cityField: EditText = findViewById(R.id.city_input)
        val countryField: EditText = findViewById(R.id.country_input)
        val streetField: EditText = findViewById(R.id.street_input)
        val numberField: EditText = findViewById(R.id.address_number_input)

        nextButton.setOnClickListener{
            val phoneNumberValue = phoneNumberField.text.toString()
            val cityValue = cityField.text.toString()
            val countryValue = countryField.text.toString()
            val streetValue = streetField.text.toString()
            val numberValue = numberField.text.toString()

            var hasError = false
            if (phoneNumberValue.isEmpty()){
                phoneNumberField.setError("Acest camp este obligatoriu")
                hasError = true
            }/* else {
                val regexPhoneNumber = Regex("^07\\d{8}\$\n")
                if(!regexPhoneNumber.matches(phoneNumberValue)) {
                    phoneNumberField.setError("Numarul de telefon nu este valid")
                    hasError = true
                }
            }
            */

            if (cityValue.isEmpty()){
                cityField.setError("Acest camp este obligatoriu")
                hasError = true
            }

            if (countryValue.isEmpty()){
                countryField.setError("Acest acmp este obligatoriu")
                hasError = true
            }

            if (streetValue.isEmpty()){
                streetField.setError("Acest camp este obligatoriu")
                hasError = true
            }

            if (numberValue.isEmpty()){
                numberField.setError("Acest camp este obligatoriu")
                hasError = true
            }

            if (!hasError) {
                registerUser(phoneNumberValue, cityValue, countryValue, streetValue, numberValue)
            }
        }
    }

    private fun registerUser(phoneNumberValue: String, cityValue: String, countryValue: String, streetValue: String, numberValue: String) {
        val intent = intent
        val emailValue = intent.getStringExtra("EMAIL_KEY")
        val passwordValue = intent.getStringExtra("PASSWORD_KEY")
        if (passwordValue != null && emailValue != null) {
            auth.createUserWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(this) { action ->
                    if (action.isSuccessful) {

                        //TODO: cum sa verific confirmarea inregistrarii contului
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Sent the confirmation mail", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        val newIntent = Intent(this, HomescreenActivity::class.java)
                        /*newIntent.putExtras(intent)
                        newIntent.putExtra("PHONE_KEY", phoneNumberValue)
                        newIntent.putExtra("CITY_KEY", cityValue)
                        newIntent.putExtra("COUNTRY_KEY", countryValue)
                        newIntent.putExtra("STREET_KEY", streetValue)
                        newIntent.putExtra("NUMBER_KEY", numberValue)*/
                        //TODO: de adaugat un mod in care sa validez adresa de email
                        startActivity(newIntent)
                        finish()
                    } else {
                        Toast.makeText(this, ":(", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


}