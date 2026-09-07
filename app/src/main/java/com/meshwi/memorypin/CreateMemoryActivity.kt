package com.meshwi.memorypin

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CreateMemoryActivity : AppCompatActivity() {

    private val selectedPhotos = ArrayList<Uri>()

    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView

    private val photoPicker =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->

            if (uris.size in 1..3) {

                selectedPhotos.clear()
                selectedPhotos.addAll(uris)

                showPhotos()

                Toast.makeText(
                    this,
                    "${uris.size} photo(s) selected",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (uris.isNotEmpty()) {

                Toast.makeText(
                    this,
                    "Please select maximum 3 photos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_memory)

        image1 = findViewById(R.id.image1)
        image2 = findViewById(R.id.image2)
        image3 = findViewById(R.id.image3)

        val btnAddPhotos = findViewById<Button>(R.id.btnAddPhotos)

        btnAddPhotos.setOnClickListener {
            photoPicker.launch("image/*")
        }
    }

    private fun showPhotos() {

        image1.visibility = View.GONE
        image2.visibility = View.GONE
        image3.visibility = View.GONE

        if (selectedPhotos.size >= 1) {
            image1.setImageURI(selectedPhotos[0])
            image1.visibility = View.VISIBLE
        }

        if (selectedPhotos.size >= 2) {
            image2.setImageURI(selectedPhotos[1])
            image2.visibility = View.VISIBLE
        }

        if (selectedPhotos.size >= 3) {
            image3.setImageURI(selectedPhotos[2])
            image3.visibility = View.VISIBLE
        }
    }
}