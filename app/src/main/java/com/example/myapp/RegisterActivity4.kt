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

class RegisterActivity4 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_part4)

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

            if (phoneNumberValue.isEmpty()){
                phoneError.visibility = View.VISIBLE
                phoneError.text = "Acest camp este obligatoriu"
            } else{
                phoneError.visibility = View.GONE
            }

            if (cityValue.isEmpty()){
                cityError.visibility = View.VISIBLE
                cityError.text = "Acest camp este obligatoriu"
            } else {
                cityError.visibility = View.GONE
            }

            if (countryValue.isEmpty()){
                countryError.visibility = View.VISIBLE
                countryError.text = "Acest camp este obligatoriu"
            } else {
                countryError.visibility = View.GONE
            }

            if (streetValue.isEmpty()){
                streetError.visibility = View.VISIBLE
                streetError.text = "Acest camp este obligatoriu"
            } else{
                streetError.visibility = View.GONE
            }

            if (numberValue.isEmpty()){
                noError.visibility = View.VISIBLE
                noError.text = "Acest camp este obligatoriu"
            } else{
                noError.visibility = View.GONE
            }

            if (phoneNumberValue.isNotEmpty() && cityValue.isNotEmpty() && countryValue.isNotEmpty() && streetValue.isNotEmpty() && numberValue.isNotEmpty()) {
                registerUser(phoneNumberValue, cityValue, countryValue, streetValue, numberValue)
            }
        }
    }

    private fun registerUser(phoneNumberValue: String, cityValue: String, countryValue: String, streetValue: String, numberValue: String) {
        val emailValue = intent.getStringExtra("EMAIL_KEY")
        val passwordValue = intent.getStringExtra("PASSWORD_KEY")

        if (passwordValue != null && emailValue != null) {
            auth.createUserWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(this) { action ->
                    if (action.isSuccessful) {
                        Toast.makeText(this, "Register completed", Toast.LENGTH_SHORT).show()
                        val newIntent = Intent(this, HomescreenActivity::class.java)
                        newIntent.putExtras(intent)
                        newIntent.putExtra("PHONE_KEY", phoneNumberValue)
                        newIntent.putExtra("CITY_KEY", cityValue)
                        newIntent.putExtra("COUNTRY_KEY", countryValue)
                        newIntent.putExtra("STREET_KEY", streetValue)
                        newIntent.putExtra("NUMBER_KEY", numberValue)
                        startActivity(newIntent)
                        finish()
                    } else {
                        Toast.makeText(this, ":(", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


}