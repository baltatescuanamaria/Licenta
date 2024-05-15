package com.example.myapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging

class CartActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart)
        auth = FirebaseAuth.getInstance()

        val nameField: EditText = findViewById(R.id.name)
        val surnameField: EditText = findViewById(R.id.surname)
        val phoneNumberField: EditText = findViewById(R.id.phone)
        val emailField: EditText = findViewById(R.id.email)

        val cityField: EditText = findViewById(R.id.city)
        val countryField: EditText = findViewById(R.id.country)
        val addressField: EditText = findViewById(R.id.address)
        val detailsField: EditText = findViewById(R.id.details)


        val placeOrder: Button = findViewById(R.id.placeOrder)
        placeOrder.setOnClickListener{

            /*val address = addressField.text.toString()
            val city = cityField.text.toString()
            val country = countryField.text.toString()
            val phoneNumber = phoneNumberField.text.toString()
            val name_user = nameField.text.toString()
            val surname_user = surnameField.text.toString()
            val methodPaying =*/

            val intent = Intent(this, FinishOrderActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            //sendToSeller(address, city, country, phoneNumber, product, quantity, name_user, surname_user, methodPaying, userId)
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

    private fun sendToSeller(addressValue:String, cityValue:String, countryValue:String, phoneNumberValue:String, productName:String, quantity:String, userName:String, methodPaying:String, userId:String){
        val db = FirebaseFirestore.getInstance()
        val productInput = hashMapOf(
            "address" to addressValue,
            "city" to cityValue,
            "country" to countryValue,
            "phone_number" to phoneNumberValue,
            "product" to productName,
            "quantity" to quantity,
            "user_name" to userName,
            "method_of_paying" to methodPaying,
            "userId" to userId
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        db.collection("users")
            .document(documentName).collection("orders").document("order_${productName}")
            .set(productInput)
            .addOnSuccessListener {
                Log.d(Logging.TAG, "Added with success")
                Toast.makeText(this, "Product added with success", Toast.LENGTH_SHORT).show()
                val newIntent = Intent(this, HomescreenActivity::class.java)
                startActivity(newIntent)
                finish()
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
            .addOnFailureListener { e ->
                Log.w(Logging.TAG, "Error adding document", e)
                Toast.makeText(this, "An error occurred while registering the information :(", Toast.LENGTH_SHORT).show()
            }
    }
    }
