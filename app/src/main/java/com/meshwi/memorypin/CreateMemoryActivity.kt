package com.meshwi.memorypin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CreateMemoryActivity : AppCompatActivity() {

    // ---------------- PHOTOS ----------------

    private val selectedPhotos = ArrayList<Uri>()

    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView


    // ---------------- STICKERS ----------------

    private lateinit var stickerContainer: LinearLayout
    private lateinit var stickerScroll: HorizontalScrollView

    private val stickerDrawables = arrayOf(
        R.drawable.map,
        R.drawable.airplane,
        R.drawable.suitcase,
        R.drawable.mountain,
        R.drawable.wave,
        R.drawable.ticket,
        R.drawable.globe,
        R.drawable.camera,
        R.drawable.heart,
        R.drawable.pin,
        R.drawable.sunglasses,
        R.drawable.polaroid,
        R.drawable.sun,
        R.drawable.palm,
        R.drawable.rainbow
    )


    // ---------------- PHOTO PICKER ----------------

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


    // ---------------- STICKER PICKER ----------------

    private val stickerPicker =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val stickers =
                    result.data?.getIntegerArrayListExtra(
                        "selectedStickers"
                    )

                if (stickers != null) {
                    showSelectedStickers(stickers)
                }
            }
        }


    // ---------------- ON CREATE ----------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_memory)


        // Find photo ImageViews
        image1 = findViewById(R.id.image1)
        image2 = findViewById(R.id.image2)
        image3 = findViewById(R.id.image3)


        // Find sticker views
        stickerContainer = findViewById(R.id.stickerContainer)
        stickerScroll = findViewById(R.id.stickerScroll)


        // Choose Photos button
        val btnAddPhotos =
            findViewById<Button>(R.id.btnAddPhotos)

        btnAddPhotos.setOnClickListener {
            photoPicker.launch("image/*")
        }


        // Add Stickers button
        val btnStickers =
            findViewById<Button>(R.id.btnStickers)

        btnStickers.setOnClickListener {

            val intent =
                Intent(this, StickerActivity::class.java)

            stickerPicker.launch(intent)
        }
    }


    // ---------------- SHOW PHOTOS ----------------

    private fun showPhotos() {

        // Hide all photos first
        image1.visibility = View.GONE
        image2.visibility = View.GONE
        image3.visibility = View.GONE


        // Show first photo
        if (selectedPhotos.size >= 1) {

            image1.setImageURI(selectedPhotos[0])
            image1.visibility = View.VISIBLE
        }


        // Show second photo
        if (selectedPhotos.size >= 2) {

            image2.setImageURI(selectedPhotos[1])
            image2.visibility = View.VISIBLE
        }


        // Show third photo
        if (selectedPhotos.size >= 3) {

            image3.setImageURI(selectedPhotos[2])
            image3.visibility = View.VISIBLE
        }
    }


    // ---------------- SHOW SELECTED STICKERS ----------------

    private fun showSelectedStickers(
        stickers: ArrayList<Int>
    ) {

        // Remove previously displayed stickers
        stickerContainer.removeAllViews()


        // If no stickers selected
        if (stickers.isEmpty()) {

            stickerScroll.visibility = View.GONE

            return
        }


        // Show sticker area
        stickerScroll.visibility = View.VISIBLE


        // Add selected stickers
        for (stickerId in stickers) {

            val stickerNumber = when (stickerId) {

                R.id.sticker1 -> 0
                R.id.sticker2 -> 1
                R.id.sticker3 -> 2
                R.id.sticker4 -> 3
                R.id.sticker5 -> 4
                R.id.sticker6 -> 5
                R.id.sticker7 -> 6
                R.id.sticker8 -> 7
                R.id.sticker9 -> 8
                R.id.sticker10 -> 9
                R.id.sticker11 -> 10
                R.id.sticker12 -> 11
                R.id.sticker13 -> 12
                R.id.sticker14 -> 13
                R.id.sticker15 -> 14

                else -> -1
            }


            if (stickerNumber != -1) {

                val imageView = ImageView(this)

                imageView.setImageResource(
                    stickerDrawables[stickerNumber]
                )

                imageView.layoutParams =
                    LinearLayout.LayoutParams(70, 70)

                imageView.setPadding(
                    5,
                    5,
                    5,
                    5
                )

                stickerContainer.addView(imageView)
            }
        }
    }
}