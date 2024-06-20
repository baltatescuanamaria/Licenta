package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging

class CartActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var total: String = ""

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

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        val userDocRef = db.collection("users").document(documentName)
        userDocRef.get().addOnSuccessListener { document->
            if (document != null && document.exists()) {
                val name = document.getString("name")
                val surname = document.getString("surname")
                val city = document.getString("city")
                val country = document.getString("country")
                val addressStreet = document.getString("street")
                val addressNumber = document.getString("number")
                val phoneNumber = document.getString("phoneNumber")
                //val email = document.getString("email")

                nameField.setText(name)
                surnameField.setText(surname)
                cityField.setText(city)
                countryField.setText(country)
                addressField.setText("$addressStreet, nr. $addressNumber")
                phoneNumberField.setText(phoneNumber)
            } else {
                Log.d(Logging.TAG, "Document does not exist")
            }
        }

        val priceTotal: TextView = findViewById(R.id.price)

        total = intent.getIntExtra("TOTAL", 0).toString()
        priceTotal.text = "$total lei"

        val placeOrder: Button = findViewById(R.id.placeOrder)
        placeOrder.setOnClickListener {

            val nameValue = nameField.text.toString()
            val surnameValue = surnameField.text.toString()
            val phoneNumberValue = phoneNumberField.text.toString()
            val emailValue = emailField.text.toString()

            val cityValue = cityField.text.toString()
            val countryValue = countryField.text.toString()
            val addressValue = addressField.text.toString()
            val detailsValue = detailsField.text.toString()

            var hasError = false
            if (nameValue.isEmpty()){
                nameField.setError("One field is empty")
                hasError = true
            }

            if (surnameValue.isEmpty()){
                surnameField.setError("One field is empty")
                hasError = true
            }

            if (phoneNumberValue.isEmpty()){
                phoneNumberField.setError("One field is empty")
                hasError = true
            }

            if (emailValue.isEmpty()){
                emailField.setError("One field is empty")
                hasError = true
            }

            if (cityValue.isEmpty()){
                cityField.setError("One field is empty")
                hasError = true
            }

            if (countryValue.isEmpty()){
                countryField.setError("One field is empty")
                hasError = true
            }

            if (addressValue.isEmpty()){
                addressField.setError("One field is empty")
                hasError = true
            }

            if (!hasError) {
                val intent = Intent(this, FinishOrderActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                if (userId != null) {
                    sendToSeller(addressValue, cityValue, countryValue, phoneNumberValue, nameValue, surnameValue, userId)
                }
            }

        }
    }

    private fun sendToSeller(
        addressValue: String,
        cityValue: String,
        countryValue: String,
        phoneNumberValue: String,
        surnameValue: String,
        nameValue: String,
        currentUserId: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val orderInfo = hashMapOf(
            "address" to addressValue,
            "city" to cityValue,
            "country" to countryValue,
            "phone_number" to phoneNumberValue,
            "userId" to currentUserId,
            "name" to nameValue,
            "surname" to surnameValue
        )

        val cartRef = db.collection("users").document("user_${currentUserId}").collection("cart")
        cartRef.get().addOnSuccessListener { documents ->
            val batch = db.batch()

            for (doc in documents) {
                val ownerId = doc.getString("idSeller")
                val productName = doc.getString("product_name")
                val quantity = doc.getString("quantity")
                val key = doc.get("key").toString()
                val category = doc.getString("category").toString()
                val productId = doc.id

                val data = hashMapOf(
                    "category" to category
                )

                db.collection("users")
                    .document("user_${currentUserId}").collection("reccs1").document(key)
                    .set(data)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener {

                    }

                if (ownerId != null && productName != null && quantity != null && total != null) {
                    val sellerOrderRef = db.collection("users").document("user_${ownerId}").collection("orders").document("$productName")
                    val productOrderInfo = orderInfo.toMutableMap()
                    productOrderInfo["product_name"] = productName
                    productOrderInfo["product_id"] = productId
                    productOrderInfo["quantity"] = quantity
                    productOrderInfo["priceTotal"] = total

                    sellerOrderRef.set(productOrderInfo)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {
                        }
                } else {

                }
                batch.delete(cartRef.document(doc.id))
            }

            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Log.e("Order", "Error deleting items from cart", e)
            }
        }.addOnFailureListener { e ->
            Log.e("Order", "Error retrieving cart items", e)
        }
    }

}
