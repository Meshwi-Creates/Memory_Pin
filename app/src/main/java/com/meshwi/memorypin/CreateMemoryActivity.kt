package com.meshwi.memorypin

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CreateMemoryActivity : AppCompatActivity() {

    private val selectedPhotos = ArrayList<Uri>()

    private val photoPicker =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->

            if (uris.size in 1..3) {

                selectedPhotos.clear()
                selectedPhotos.addAll(uris)

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

        val btnAddPhotos = findViewById<Button>(R.id.btnAddPhotos)

        btnAddPhotos.setOnClickListener {
            photoPicker.launch("image/*")
        }
    }
}