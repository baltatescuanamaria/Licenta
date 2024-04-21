package com.example.myapp

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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
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
        val productImage: ImageView = findViewById(R.id.picturePlace)

        val nameProduct  = intent.getStringExtra("PRODUCT_NAME")
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentName = "user_${userId}"
        val userDocRef = db.collection("users").document(documentName).collection("products").document("product_${nameProduct}")
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val name = document.getString("product_name")
                val price  = document.getString("price")
                val quantity = document.getString("quantity")
                val description = document.getString("description")
                val imageUrl = document.getString("image_url")

                nameField.setText(name)
                priceField.setText(price)
                quantityField.setText(quantity)
                descriptionField.setText(description)
                imageUrl?.let {
                    Glide.with(this)
                        .load(it)
                        .into(productImage)
                }
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

        val doc = database.collection("users").document("user_${userId}").collection("products").document("products_${nameProduct}")

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
    val deleteBtn:Button = findViewById(R.id.delete)
    deleteBtn.setOnClickListener {
            userDocRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Deleted with succes", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProductsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Error in deleting the product", Toast.LENGTH_SHORT).show()
                }
        }
    val addPicture: Button = findViewById(R.id.selectImageButton)
    val homeBtn: ImageButton = findViewById(R.id.home)
    val messagesBtn: ImageButton = findViewById(R.id.message)
    val productsBtn: ImageButton = findViewById(R.id.products)
    val wishlistBtn: ImageButton = findViewById(R.id.wishlist)
    val profileBtn: ImageButton = findViewById(R.id.profile)
    val backButton: ImageButton = findViewById(R.id.back_button)
    backButton.setOnClickListener{
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    addPicture.setOnClickListener {
        openGallery()
    }
    homeBtn.setOnClickListener{
        val intent = Intent(this, HomescreenActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }

    messagesBtn.setOnClickListener {
        val intent = Intent(this, MessageListActivity::class.java)
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

                val usersCollection = com.google.firebase.Firebase.firestore.collection("products")
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                val imageName = "profile_pic"

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