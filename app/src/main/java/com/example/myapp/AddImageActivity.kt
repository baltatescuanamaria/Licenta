package com.example.myapp

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.inappmessaging.inAppMessaging
import com.google.firebase.inappmessaging.internal.Logging.TAG
import com.google.firebase.initialize
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.component1
import com.google.firebase.storage.component2
import com.google.firebase.storage.component3
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class AddImageActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_image)

        val selectImageButton : Button = findViewById(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            openGallery()
        }

        val nextAction: Button = findViewById(R.id.next_btn)
        nextAction.setOnClickListener{
            val intent = Intent(this, HomescreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity4::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imagePlace : ImageView = findViewById(R.id.picturePlace)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            selectedImage?.let {
                val inputStream = contentResolver.openInputStream(selectedImage)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imagePlace.setImageBitmap(bitmap)

                val usersCollection = Firebase.firestore.collection("pictures")
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
                            Log.d(TAG, "Imaginea a fost salvată cu succes în subcolecție")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Eroare la salvarea imaginii în subcolecție", e)
                        }
                }

            }
        }
    }
}
