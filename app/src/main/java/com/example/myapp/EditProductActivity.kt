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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.inappmessaging.internal.Logging
import com.google.firebase.ktx.Firebase

class EditProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_page_edit)

        val nameField: EditText = findViewById(R.id.changeName)
        val priceField: EditText = findViewById(R.id.changePrice)
        val quantityField: EditText = findViewById(R.id.changeQuantity)
        val descriptionField: EditText = findViewById(R.id.changeDescription)

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "products_${userId}"
        val userDocRef = db.collection("products").document(documentName)
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val name = document.getString("product_name")
                val price  = document.getString("price")
                val quantity = document.getString("quantity")
                val description = document.getString("description")
                nameField.setText(name)
                priceField.setText(price)
                quantityField.setText(quantity)
                descriptionField.setText(description)
            } else {
                Log.d(Logging.TAG, "Document does not exist")
            }
        }
    val saveBtn: Button = findViewById(R.id.save)
    saveBtn.setOnClickListener{

        val nameValue = nameField.text.toString()
        val priceValue = priceField.text.toString()
        val quantityValue = quantityField.text.toString()
        val descriptionValue = descriptionField.text.toString()

        val database = Firebase.firestore
        val updateInfo = hashMapOf<String, Any>()

        if (nameValue.isNotEmpty()) {
            updateInfo["product_name"] = nameValue
        }
        if (priceValue.isNotEmpty()) {
            updateInfo["price"] = priceValue
        }
        if (quantityValue.isNotEmpty()) {
            updateInfo["quantity"] = quantityValue
        }
        if (descriptionValue.isNotEmpty()) {
            updateInfo["description"] = descriptionValue
        }

        val doc = database.collection("products").document("products_${userId}")

        doc.update(updateInfo)
            .addOnSuccessListener {
                val intent = Intent(this, ProductsActivity::class.java)
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