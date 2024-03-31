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
import com.example.myapp.HomescreenActivity
import com.example.myapp.R
import com.example.myapp.RegisterActivity4
import com.google.firebase.auth.FirebaseAuth

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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            selectedImage?.let {
                val inputStream = contentResolver.openInputStream(selectedImage)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imagePlace.setImageBitmap(bitmap)


                //TODO: de adaugat imaginea in baza de date (atunci cand o sa o fac)
            }
        }
    }
}
