package com.example.myapp;

import android.app.Activity;
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.inappmessaging.internal.Logging
import com.google.firebase.inappmessaging.internal.Logging.TAG

class AddProductActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_product)

        auth = FirebaseAuth.getInstance()

        val homeBtn: ImageButton = findViewById(R.id.home)
        val messagesBtn: ImageButton = findViewById(R.id.message)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val selectImageButton : Button = findViewById(R.id.selectImageButton)
        val addItemBtn : Button = findViewById(R.id.addItem)

        val nameField: EditText = findViewById(R.id.nameProduct)
        val priceField: EditText = findViewById(R.id.price)
        val quantityField: EditText = findViewById(R.id.quantity)
        val descriptionField: EditText = findViewById(R.id.description)

        selectImageButton.setOnClickListener {
            openGallery()
        }

        addItemBtn.setOnClickListener {
            val nameValue = nameField.text.toString()
            val priceValue = priceField.text.toString()
            val quantityValue = quantityField.text.toString()
            val descriptionValue = descriptionField.text.toString()

            var hasError = false
            if (nameValue.isEmpty()){
                nameField.setError("Acest camp este obligatoriu")
                hasError = true
            }
            if (priceValue.isEmpty()){
                priceField.setError("Acest camp este obligatoriu")
                hasError = true
            }
            if (quantityValue.isEmpty()){
                quantityField.setError("Acest camp este obligatoriu")
                hasError = true
            }
            if (descriptionValue.isEmpty()){
                descriptionField.setError("Acest camp este obligatoriu")
                hasError = true
            }
            if (!hasError) {
                addItem(nameValue, priceValue, quantityValue, descriptionValue)
            }
        }
        homeBtn.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        messagesBtn.setOnClickListener {
            val intent = Intent(this, MessageActivity::class.java)
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

    private fun addItem(nameValue: String, priceValue: String, quantityValue: String, descriptionValue: String){
        val db = FirebaseFirestore.getInstance()
        val productInput = hashMapOf(
            "product_name" to nameValue,
            "price" to priceValue,
            "quantity" to quantityValue,
            "description" to descriptionValue,
            "userId" to auth.currentUser?.uid
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "products_${userId}"
        db.collection("products")
            .document(documentName)
            .set(productInput)
            .addOnSuccessListener {
                Log.d(TAG, "Added with success")
                Toast.makeText(this, "Product added with success", Toast.LENGTH_SHORT).show()
                val newIntent = Intent(this, ProductsActivity::class.java)
                startActivity(newIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(this, "An error occurred while registering the information :(", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, AddImageActivity.PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imagePlace : ImageView = findViewById(R.id.picturePlace)
        if (requestCode == AddImageActivity.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            selectedImage?.let {
                val inputStream = contentResolver.openInputStream(selectedImage)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imagePlace.setImageBitmap(bitmap)

                /*val usersCollection = Firebase.firestore.collection("pictures")
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                val imageName = "profile_pic"

                val imageInfo = hashMapOf(
                    "url" to selectedImage,
                    "name" to imageName
                )

                currentUserId?.let { userId ->
                    usersCollection.document("pictures_$userId")
                        .set(imageInfo, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d(Logging.TAG, "Imaginea a fost salvată cu succes în subcolecție")
                        }
                        .addOnFailureListener { e ->
                            Log.e(Logging.TAG, "Eroare la salvarea imaginii în subcolecție", e)
                        }
                }*/

            }
        }
    }
}
