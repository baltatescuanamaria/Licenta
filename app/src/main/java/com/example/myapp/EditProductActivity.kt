package com.example.myapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.inappmessaging.internal.Logging.TAG
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class EditProductActivity : AppCompatActivity() {
    private var selectedItem: String? = null
    private var selectedItemPrice: String? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var imageUrl: Uri? = null
    private var nameValue: String = " "
    private var rndmUUID: String = " "
    private var oldImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_page_edit)

        val nameField: EditText = findViewById(R.id.changeName)
        val priceField: EditText = findViewById(R.id.changePrice)
        val descriptionField: EditText = findViewById(R.id.changeDescription)
        val productImageField: ImageView = findViewById(R.id.picturePlace)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        // Dropdown lists
        val listDropdown = listOf("Fruits", "Vegetables", "Animal Product", "Viticulture", "Beekeeping", "Preserved Food")
        val listDropdownPrice = listOf("Bottle", "Piece", "Kilogram", "Jar", "Grams")
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, listDropdown)
        val adapterPrice = ArrayAdapter(this, R.layout.dropdown_item, listDropdownPrice)

        val categoryField: AutoCompleteTextView = findViewById(R.id.options)
        val packagingField: AutoCompleteTextView = findViewById(R.id.optionsPrice)
        categoryField.setAdapter(adapter)
        categoryField.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            selectedItem = adapterView.getItemAtPosition(i).toString()
        }

        packagingField.setAdapter(adapterPrice)
        packagingField.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            selectedItemPrice = adapterView.getItemAtPosition(i).toString()
        }

        val nameProduct = intent.getStringExtra("PRODUCT_NAME")
        val nameDocument = intent.getStringExtra("URL")
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        val userDocRef = db.collection("users").document(documentName).collection("products").document("$nameDocument")

        userDocRef.get().addOnSuccessListener { document ->
                val url = document.getString("key")
                if (document != null && url == nameDocument) {
                    val name = document.getString("product_name")
                    val price = document.getString("price")
                    val description = document.getString("description")
                    val imageUrl = document.getString("image_url")
                    oldImageUrl = document.getString("image_url")
                    val category = document.getString("category")
                    val packaging = document.getString("package")

                    nameField.setText(name)
                    priceField.setText(price)
                    descriptionField.setText(description)

                    if (category != null) {
                        categoryField.setText(category, false)
                    }
                    if (packaging != null) {
                        packagingField.setText(packaging, false)
                    }

                    imageUrl?.let {
                        val storageRef = storage.reference.child("images/$it")
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(this)
                                .load(uri)
                                .into(productImageField)
                        }.addOnFailureListener {
                            Log.e("Firebase Storage", "Error getting image URL", it)
                        }
                    }

                } else {
                    Log.d("Firestore", "Document does not exist")
                }
        }

        val saveBtn: Button = findViewById(R.id.save)
        saveBtn.setOnClickListener {
            val nameValue = nameField.text.toString()
            val priceValue = priceField.text.toString()
            val descriptionValue = descriptionField.text.toString()
            val imageValue = productImageField.imageAlpha.toString()

            val database = Firebase.firestore
            val updateInfo = hashMapOf<String, Any>()

            if (nameValue.isNotEmpty()) {
                updateInfo["product_name"] = nameValue
            }
            if (descriptionValue.isNotEmpty()) {
                updateInfo["description"] = descriptionValue
            }
            if (selectedItem?.isNotEmpty() == true) {
                updateInfo["category"] = selectedItem!!
            }
            if (selectedItemPrice?.isNotEmpty() == true) {
                updateInfo["package"] = selectedItemPrice!!
            }
            if (priceValue.isNotEmpty()) {
                updateInfo["price"] = priceValue
            }
            if (imageUrl != null) {
                uploadImage()
                updateInfo["image_url"] = rndmUUID
            }


            val doc = database.collection("users").document(documentName).collection("products").document("$nameDocument")

            doc.update(updateInfo)
                .addOnSuccessListener {
                    //val intent = Intent(this, ProductsActivity::class.java)
                    //startActivity(intent)
                    //overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "An error occurred while updating the information :(", Toast.LENGTH_SHORT).show()
                }



            val productsCollection = database.collection("products")
            productsCollection.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val productName = document.getString("product_name")
                    val idOwner = document.getString("userId")

                    if (nameProduct == productName && userId == idOwner) {
                        val productDocRef = productsCollection.document(document.id)
                        productDocRef.update(updateInfo)
                            .addOnSuccessListener {
                                val intent = Intent(this, ProductsActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "An error occurred while updating the product in the main collection :(", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "An error occurred while retrieving products :(", Toast.LENGTH_SHORT).show()
            }

            database.collection("users").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "Country value matches for user: ${document.id}")
                    val currentUser = document.id
                    database.collection("users").document(currentUser).collection("reccs2").get()
                        .addOnSuccessListener { products ->
                            for (product in products) {
                                val productName = product.getString("product_name")
                                val idOwner = product.getString("userId")
                                Log.d(TAG, "Country value matches for user: ${product.id}")
                                if (nameProduct == productName && userId == idOwner) {
                                    val productDocRef =  database.collection("users").document(currentUser).collection("reccs2").document(product.id)
                                    productDocRef.update(updateInfo)
                                        .addOnSuccessListener {
                                            val intent = Intent(this, ProductsActivity::class.java)
                                            startActivity(intent)

                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                this,
                                                "An error occurred while updating the product in the main collection :(",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "An error occurred while retrieving products :(",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "An error occurred while retrieving users :(",
                    Toast.LENGTH_SHORT
                ).show()
            }

            database.collection("users").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "Country value matches for user: ${document.id}")
                    val currentUser = document.id
                    database.collection("users").document(currentUser).collection("reccs3").get()
                        .addOnSuccessListener { products ->
                            for (product in products) {
                                val productName = product.getString("product_name")
                                val idOwner = product.getString("userId")
                                Log.d(TAG, "Country value matches for user: ${product.id}")
                                if (nameProduct == productName && userId == idOwner) {
                                    val productDocRef =  database.collection("users").document(currentUser).collection("reccs2").document(product.id)
                                    productDocRef.update(updateInfo)
                                        .addOnSuccessListener {
                                            val intent = Intent(this, ProductsActivity::class.java)
                                            startActivity(intent)

                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                this,
                                                "An error occurred while updating the product in the main collection :(",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "An error occurred while retrieving products :(",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "An error occurred while retrieving users :(",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

        val deleteBtn: Button = findViewById(R.id.delete)
        deleteBtn.setOnClickListener {
            userDocRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProductsActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error in deleting the product", Toast.LENGTH_SHORT).show()
                }
            val productsCollection = db.collection("products")
            productsCollection.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val productName = document.getString("product_name")
                    val idOwner = document.getString("userId")

                    if (nameProduct == productName && userId == idOwner) {
                        val productDocRef = productsCollection.document(document.id)
                        productDocRef.delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                                //val intent = Intent(this, ProductsActivity::class.java)
                                //startActivity(intent)
                                //overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "An error occurred while deleting the product from the main collection :(", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "An error occurred while retrieving products :(", Toast.LENGTH_SHORT).show()
            }

            db.collection("users").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "Country value matches for user: ${document.id}")
                    val currentUser = document.id
                    db.collection("users").document(currentUser).collection("reccs2").get()
                        .addOnSuccessListener { products ->
                            for (product in products) {
                                val productName = product.getString("product_name")
                                val idOwner = product.getString("userId")
                                Log.d(TAG, "Country value matches for user: ${product.id}")
                                if (nameProduct == productName && userId == idOwner) {
                                    val productDocRef =  db.collection("users").document(currentUser).collection("reccs2").document(product.id)
                                    productDocRef.delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                                            //val intent = Intent(this, ProductsActivity::class.java)
                                            //startActivity(intent)
                                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "An error occurred while deleting the product from the main collection :(", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "An error occurred while retrieving products :(",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "An error occurred while retrieving users :(",
                    Toast.LENGTH_SHORT
                ).show()
            }

            db.collection("users").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "Country value matches for user: ${document.id}")
                    val currentUser = document.id
                    db.collection("users").document(currentUser).collection("reccs2").get()
                        .addOnSuccessListener { products ->
                            for (product in products) {
                                val productName = product.getString("product_name")
                                val idOwner = product.getString("userId")
                                Log.d(TAG, "Country value matches for user: ${product.id}")
                                if (nameProduct == productName && userId == idOwner) {
                                    val productDocRef =  db.collection("users").document(currentUser).collection("reccs3").document(product.id)
                                    productDocRef.delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                                            //val intent = Intent(this, ProductsActivity::class.java)
                                            //startActivity(intent)
                                            //overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "An error occurred while deleting the product from the main collection :(", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "An error occurred while retrieving products :(",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "An error occurred while retrieving users :(",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val addPictureBtn: Button = findViewById(R.id.selectImageButton)
        val homeBtn: ImageButton = findViewById(R.id.home)
        val productsBtn: ImageButton = findViewById(R.id.products)
        val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
        val profileBtn: ImageButton = findViewById(R.id.profile)
        val backBtn: ImageButton = findViewById(R.id.back_button)
        backBtn.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
            finish()
        }
        addPictureBtn.setOnClickListener {
            openGallery()
        }
        homeBtn.setOnClickListener {
            val intent = Intent(this, HomescreenActivity::class.java)
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

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select an image"), AddProductActivity.PICK_IMAGE_REQUEST
        )
    }

    private fun uploadImage() {
        if (imageUrl != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.show()

            rndmUUID = UUID.randomUUID().toString()
            val ref = storageReference.child("images/$rndmUUID")

            oldImageUrl?.let { oldImage ->
                val oldImageRef = storageReference.child("images/$oldImage")
                oldImageRef.delete().addOnSuccessListener {
                    Log.d("Firebase Storage", "Old image deleted successfully")
                }.addOnFailureListener {
                    Log.e("Firebase Storage", "Failed to delete old image", it)
                }
            }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imagePlace: ImageView = findViewById(R.id.picturePlace)
        if (requestCode == AddProductActivity.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
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
