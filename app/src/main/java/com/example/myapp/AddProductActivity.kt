package com.example.myapp;

import android.annotation.SuppressLint
import android.app.Activity;
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
    private var imageUrl: String? = null
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    private var selectedItem: String? = null
    private var selectedItemPrice: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_product)

        auth = FirebaseAuth.getInstance()

        //dropdown
        val listDropdown = listOf("Fruits", "Vegetables", "Animal Product", "Viticulture", "Beekeeping", "Preserved Food")
        val listDropdownPrice = listOf("Bottle", "Piece", "Kilogram", "Jar", "Grams")
        val autoComplete : AutoCompleteTextView = findViewById(R.id.options)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, listDropdown)
        val autoCompletePrice : AutoCompleteTextView = findViewById(R.id.optionsPrice)
        val adapterPrice = ArrayAdapter(this, R.layout.dropdown_item, listDropdownPrice)

        autoComplete.setAdapter(adapter)
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
            selectedItem = adapterView.getItemAtPosition(i).toString()
        }

        autoCompletePrice.setAdapter(adapterPrice)
        autoCompletePrice.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
            selectedItemPrice = adapterView.getItemAtPosition(i).toString()
        }

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val selectImageButton : Button = findViewById(R.id.selectImageButton)
        val addItemBtn : Button = findViewById(R.id.addItem)
        val backButton: ImageButton = findViewById(R.id.back_button)

        val nameField: EditText = findViewById(R.id.nameProduct)
        val priceField: EditText = findViewById(R.id.price)
        val descriptionField: EditText = findViewById(R.id.description_text_input)


        backButton.setOnClickListener{
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        selectImageButton.setOnClickListener {
            openGallery()
        }

        addItemBtn.setOnClickListener {
            val nameValue = nameField.text.toString()
            val priceValue = priceField.text.toString()
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
            if (descriptionValue.isEmpty()){
                descriptionField.setError("Acest camp este obligatoriu")
                hasError = true
            }
            if (selectedItem?.isEmpty() == true){
                Toast.makeText(this, "Selectați o categorie", Toast.LENGTH_SHORT).show()
                hasError = true
            }
            if (selectedItemPrice?.isEmpty() == true){
                Toast.makeText(this, "Selectați o categorie", Toast.LENGTH_SHORT).show()
                hasError = true
            }
            if (!hasError) {
                addItem(nameValue, priceValue, descriptionValue)
            }
        }
        homeBtn.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }


        productsBtn.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        wishlistBtn.setOnClickListener {
            val intent = Intent(this, WishlistActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }


    }

    private fun addItem(nameValue: String, priceValue: String, descriptionValue: String){
        val db = FirebaseFirestore.getInstance()
        val productInput = hashMapOf(
            "product_name" to nameValue,
            "price" to priceValue,
            "description" to descriptionValue,
            "category" to selectedItem,
            "package" to selectedItemPrice,
            "image_url" to imageUrl,
            "userId" to auth.currentUser?.uid
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        db.collection("users")
            .document(documentName).collection("products").document("product_${nameValue}")
            .set(productInput)
            .addOnSuccessListener {
                Log.d(TAG, "Added with success")
                Toast.makeText(this, "Product added with success", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        db.collection("products").document(nameValue).set(productInput).addOnSuccessListener {
            val newIntent = Intent(this, ProductsActivity::class.java)
            startActivity(newIntent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
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
                imageUrl = selectedImage.toString()

                val usersCollection = Firebase.firestore.collection("products")
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                val imageName = "product_pic"

                val imageInfo = hashMapOf(
                    "url" to selectedImage,
                    "name" to imageName
                )

                currentUserId?.let { userId ->
                    usersCollection.document("products_$userId")
                        .set(imageInfo, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d(Logging.TAG, "Imaginea a fost salvată cu succes în subcolecție")
                        }
                        .addOnFailureListener { e ->
                            Log.e(Logging.TAG, "Eroare la salvarea imaginii în subcolecție", e)
                        }
                }

            }
        }
    }
}


