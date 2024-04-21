package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging.TAG
import java.util.UUID

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
        val nameValue = intent.getStringExtra("NAME_KEY")
        val surnameValue = intent.getStringExtra("SURNAME_KEY")
        val usernameValue = intent.getStringExtra("USERNAME_KEY")

        if (passwordValue != null && emailValue != null) {
            auth.createUserWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(this) { action ->
                    if (action.isSuccessful) {
                        val database = FirebaseFirestore.getInstance()
                        val userInput = hashMapOf(
                            "name" to nameValue,
                            "username" to usernameValue,
                            "surname" to surnameValue,
                            "phoneNumber" to phoneNumberValue,
                            "city" to cityValue,
                            "country" to countryValue,
                            "street" to streetValue,
                            "number" to numberValue,
                            "userId" to auth.currentUser?.uid
                        )

                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        val documentName = "user_${userId}"
                        database.collection("users")
                            .document(documentName)
                            .set(userInput)
                            .addOnSuccessListener {
                                Log.d(TAG, "added with ID: $documentName")
                                val newIntent = Intent(this, AddImageActivity::class.java)
                                startActivity(newIntent)
                                finish()
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                                Toast.makeText(this, "An error occurred while registering the information :(", Toast.LENGTH_SHORT).show()
                            }
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
                        val newIntent = Intent(this, AddImageActivity::class.java)
                        newIntent.putExtras(intent)
                        newIntent.putExtra("EMAIL_KEY", emailValue)
                        newIntent.putExtra("PASSWORD_KEY", passwordValue)
                        newIntent.putExtra("NAME_KEY", nameValue)
                        newIntent.putExtra("SURNAME_KEY", surnameValue)
                        newIntent.putExtra("USERNAME_KEY", usernameValue)
                        newIntent.putExtra("PHONE_KEY", phoneNumberValue)
                        newIntent.putExtra("CITY_KEY", cityValue)
                        newIntent.putExtra("COUNTRY_KEY", countryValue)
                        newIntent.putExtra("STREET_KEY", streetValue)
                        newIntent.putExtra("NUMBER_KEY", numberValue)
                        //TODO: de adaugat un mod in care sa validez adresa de email
                        startActivity(newIntent)
                        finish()
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                    } else {
                        Toast.makeText(this, ":(", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


}