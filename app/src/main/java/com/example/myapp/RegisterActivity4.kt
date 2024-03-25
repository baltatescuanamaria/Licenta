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
        val phoneNumberField: EditText = findViewById(R.id.phoneNumber)
        val cityField: EditText = findViewById(R.id.city)
        val countryField: EditText = findViewById(R.id.country)
        val streetField: EditText = findViewById(R.id.address_street)
        val numberField: EditText = findViewById(R.id.address_number)
        val phoneError: TextView = findViewById(R.id.phoneError)
        val cityError: TextView = findViewById(R.id.cityError)
        val countryError: TextView = findViewById(R.id.countryError)
        val streetError: TextView = findViewById(R.id.streetError)
        val noError: TextView = findViewById(R.id.noError)

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
            }

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

            val regexModel = Regex("^\\+?4?0?[1-9][0-9]{8}\\\$")

            if(!regexModel.matches(numberValue)) {
                    numberField.setError("Numarul de telefon nu este valid")
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
                        Toast.makeText(this, "Register completed", Toast.LENGTH_SHORT).show()
                        val newIntent = Intent(this, HomescreenActivity::class.java)
                        /*newIntent.putExtras(intent)
                        newIntent.putExtra("PHONE_KEY", phoneNumberValue)
                        newIntent.putExtra("CITY_KEY", cityValue)
                        newIntent.putExtra("COUNTRY_KEY", countryValue)
                        newIntent.putExtra("STREET_KEY", streetValue)
                        newIntent.putExtra("NUMBER_KEY", numberValue)*/
                        //TODO: de adaugat un mod in care sa valaidez adresa de email
                        startActivity(newIntent)
                        finish()
                    } else {
                        Toast.makeText(this, ":(", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


}