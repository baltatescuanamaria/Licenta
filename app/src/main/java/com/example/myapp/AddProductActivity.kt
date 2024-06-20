package com.example.myapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.inappmessaging.internal.Logging.TAG
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.processNextEventInCurrentThread
import java.util.*

class AddProductActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var imageUrl: Uri? = null
    private var nameValue: String = " "
    private var rndmUUID: String = " "
    private var rndmUUID2: String = " "
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    private var selectedItem: String? = null
    private var selectedItemPrice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_product)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        //dropdown
        val listDropdown = listOf("Fruits", "Vegetables", "Animal Product", "Viticulture", "Beekeeping", "Preserved Food")
        val listDropdownPrice = listOf("Bottle", "Piece", "Kilogram", "Jar", "Grams")
        val autoComplete: AutoCompleteTextView = findViewById(R.id.options)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, listDropdown)
        val autoCompletePrice: AutoCompleteTextView = findViewById(R.id.optionsPrice)
        val adapterPrice = ArrayAdapter(this, R.layout.dropdown_item, listDropdownPrice)

        autoComplete.setAdapter(adapter)
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            selectedItem = adapterView.getItemAtPosition(i).toString()
        }

        autoCompletePrice.setAdapter(adapterPrice)
        autoCompletePrice.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            selectedItemPrice = adapterView.getItemAtPosition(i).toString()
        }

        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        val addItemBtn: Button = findViewById(R.id.addItem)
        val backButton: ImageButton = findViewById(R.id.back_button)

        val nameField: EditText = findViewById(R.id.name_input)
        val priceField: EditText = findViewById(R.id.price)
        val descriptionField: EditText = findViewById(R.id.description_text_input)
        val quantityField: EditText = findViewById(R.id.quantity_text_input)

        backButton.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        selectImageButton.setOnClickListener {
            openGallery()
        }

        addItemBtn.setOnClickListener {
            nameValue = nameField.text.toString()
            val priceValue = priceField.text.toString()
            val descriptionValue = descriptionField.text.toString()
            val quantityValue = quantityField.text.toString()

            var hasError = false
            if (nameValue.isEmpty()) {
                nameField.error = "Acest camp este obligatoriu"
                hasError = true
            }

            if (priceValue.isEmpty()) {
                priceField.error = "Acest camp este obligatoriu"
                hasError = true
            }
            if (descriptionValue.isEmpty()) {
                descriptionField.error = "Acest camp este obligatoriu"
                hasError = true
            }
            if (selectedItem?.isEmpty() == true) {
                Toast.makeText(this, "Selectați o categorie", Toast.LENGTH_SHORT).show()
                hasError = true
            }
            if (selectedItemPrice?.isEmpty() == true) {
                Toast.makeText(this, "Selectați o categorie", Toast.LENGTH_SHORT).show()
                hasError = true
            }
            if (quantityValue.isEmpty()) {
                Toast.makeText(this, "Trebuie sa exista o cantitate disponibila", Toast.LENGTH_SHORT).show()
                hasError = true
            }
            if (!hasError) {
                addItem(nameValue, priceValue, descriptionValue, quantityValue)
                uploadImage()
            }
        }

        homeBtn.setOnClickListener {
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

    private fun uploadImage() {
        if (imageUrl != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Uploading...")
            progressDialog.show()

            rndmUUID = UUID.randomUUID().toString()

            val ref = storageReference.child("images/$rndmUUID")
            ref.putFile(imageUrl!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed " + it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addItem(nameValue: String, priceValue: String, descriptionValue: String, quantityValue: String) {
        var nameUserValue: String = ""
        var surnameValue: String = ""
        var cityValue: String = ""
        var countryValue: String = ""
        var addressStreet: String = ""
        var addressNumber: String = ""
        var usernameValue: String = ""
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        val userDocRef = db.collection("users").document(documentName)
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                nameUserValue = document.getString("name").toString()
                surnameValue = document.getString("surname").toString()
                cityValue = document.getString("city").toString()
                countryValue = document.getString("country").toString()
                addressStreet = document.getString("street").toString()
                addressNumber = document.getString("number").toString()
                usernameValue = document.getString("username").toString()
            }

            rndmUUID2 = UUID.randomUUID().toString()

            val productInput = hashMapOf(
                "product_name" to nameValue,
                "price" to priceValue,
                "description" to descriptionValue,
                "category" to selectedItem,
                "package" to selectedItemPrice,
                "image_url" to rndmUUID,
                "userId" to auth.currentUser?.uid,
                "quantity" to quantityValue,
                "surname" to surnameValue,
                "name" to nameUserValue,
                "city" to cityValue,
                "country" to countryValue,
                "username" to usernameValue,
                "key" to rndmUUID2
            )

            db.collection("users")
                .document(documentName).collection("products").document(rndmUUID2)
                .set(productInput)
                .addOnSuccessListener {
                    Log.d(TAG, "Added with success")
                    Toast.makeText(this, "Product added with success", Toast.LENGTH_SHORT).show()
                    val newIntent = Intent(this, ProductsActivity::class.java)
                    startActivity(newIntent)
                    finish()
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
            db.collection("products").document(rndmUUID2).set(productInput).addOnSuccessListener {
                val newIntent = Intent(this, ProductsActivity::class.java)
                startActivity(newIntent)
                finish()
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(
                        this,
                        "An error occurred while registering the information :(",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            db.collection("users").get().addOnSuccessListener { users->
                for(user in users){
                    val county = user.getString("country")
                    val cuurentUser = user.getString("userId")
                    if(countryValue == county && cuurentUser != auth.currentUser?.uid){
                        db.collection("users").document(user.id).collection("reccs2").document(rndmUUID2).set(productInput).addOnSuccessListener {
                            val newIntent = Intent(this, ProductsActivity::class.java)
                            startActivity(newIntent)
                            finish()
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                        }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                                Toast.makeText(
                                    this,
                                    "An error occurred while registering the information :(",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select an image"), PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imagePlace: ImageView = findViewById(R.id.picturePlace)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            selectedImage?.let {
                val inputStream = contentResolver.openInputStream(selectedImage)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imagePlace.setImageBitmap(bitmap)
                imageUrl = selectedImage
            }
        }
    }
}
