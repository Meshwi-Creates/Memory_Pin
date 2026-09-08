package com.meshwi.memorypin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StickerActivity : AppCompatActivity() {

    private val selectedStickers = ArrayList<Int>()

    private lateinit var stickerCount: TextView

    private val stickerIds = arrayOf(
        R.id.sticker1,
        R.id.sticker2,
        R.id.sticker3,
        R.id.sticker4,
        R.id.sticker5,
        R.id.sticker6,
        R.id.sticker7,
        R.id.sticker8,
        R.id.sticker9,
        R.id.sticker10,
        R.id.sticker11,
        R.id.sticker12,
        R.id.sticker13,
        R.id.sticker14,
        R.id.sticker15
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sticker)

        stickerCount = findViewById(R.id.tvStickerCount)

        for (id in stickerIds) {

            val sticker = findViewById<ImageView>(id)

            sticker.setOnClickListener {
                selectSticker(sticker)
            }
        }

        val btnDone = findViewById<Button>(R.id.btnDoneStickers)

        btnDone.setOnClickListener {

            val result = intent

            result.putIntegerArrayListExtra(
                "selectedStickers",
                selectedStickers
            )

            setResult(RESULT_OK, result)

            finish()
        }
    }

    private fun selectSticker(sticker: ImageView) {

        val stickerId = sticker.id

        if (selectedStickers.contains(stickerId)) {

            selectedStickers.remove(stickerId)

            sticker.alpha = 1.0f

        } else {

            if (selectedStickers.size >= 8) {

                Toast.makeText(
                    this,
                    "You can select maximum 8 stickers",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }

            selectedStickers.add(stickerId)

            sticker.alpha = 0.5f
        }

        stickerCount.text =
            "Selected: ${selectedStickers.size} / 8"
    }
}