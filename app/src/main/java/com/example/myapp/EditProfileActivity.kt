package com.example.myapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.inappmessaging.internal.Logging
import com.google.firebase.inappmessaging.internal.Logging.TAG
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit)

        val surnameField: EditText = findViewById(R.id.changeSurname)
        val nameField: EditText = findViewById(R.id.changeName)
        val usernameField: EditText = findViewById(R.id.changeUsername)
        val cityField: EditText = findViewById(R.id.changeCity)
        val countryField: EditText = findViewById(R.id.changeCountry)

            val userId = "user_ana0maria"
            val db = Firebase.firestore
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { document->
                if (document != null && document.exists()) {
                    val name = document.getString("name")
                    val surname = document.getString("surname")
                    val username = document.getString("username")
                    val city = document.getString("city")
                    val country = document.getString("country")

                    nameField.setText(name)
                    surnameField.setText(surname)
                    usernameField.setText(username)
                    cityField.setText(city)
                    countryField.setText(country)
                } else {
                    Log.d(TAG, "Document does not exist")
                }
            }

        val saveBtn: Button = findViewById(R.id.save)

        saveBtn.setOnClickListener{

            val surnameValue = surnameField.text.toString()
            val nameValue = nameField.text.toString()
            val usernameValue = usernameField.text.toString()
            val cityValue = cityField.text.toString()
            val countryValue = countryField.text.toString()

            val database = Firebase.firestore
            val updateInfo = hashMapOf<String, Any>()

            if (usernameValue.isNotEmpty()) {
                updateInfo["username"] = usernameValue
            }
            if (nameValue.isNotEmpty()) {
                updateInfo["name"] = nameValue
            }
            if (surnameValue.isNotEmpty()) {
                updateInfo["surname"] = surnameValue
            }
            if (cityValue.isNotEmpty()) {
                updateInfo["city"] = cityValue
            }
            if (countryValue.isNotEmpty()) {
                updateInfo["country"] = countryValue
            }

            val doc = database.collection("users").document("user_ana0maria")

            doc.update(updateInfo)
                .addOnSuccessListener {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "An error occurred while updating the information :(", Toast.LENGTH_SHORT).show()
                }

        }
        val homeBtn: ImageButton = findViewById(R.id.home)
        val messagesBtn: ImageButton = findViewById(R.id.message)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)

        homeBtn.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        messagesBtn.setOnClickListener {
            val intent = Intent(this, MessageListActivity::class.java)
            startActivity(intent)
            finish()
        }

        productsBtn.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
            finish()
        }

        wishlistBtn.setOnClickListener {
            val intent = Intent(this, WishlistActivity::class.java)
            startActivity(intent)
            finish()
        }

        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}