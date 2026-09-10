package com.meshwi.memorypin

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var memoryContainer: LinearLayout
    private lateinit var tvEmpty: TextView

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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        memoryContainer =
            findViewById(R.id.memoryContainer)

        tvEmpty =
            findViewById(R.id.tvEmpty)

        val btnCreateMemory =
            findViewById<Button>(R.id.btnCreateMemory)

        btnCreateMemory.setOnClickListener {

            val intent =
                Intent(
                    this,
                    CreateMemoryActivity::class.java
                )

            startActivity(intent)
        }
    }

    override fun onResume() {

        super.onResume()

        loadMemories()
    }

    // ---------------- LOAD MEMORIES ----------------

    private fun loadMemories() {

        memoryContainer.removeAllViews()

        val preferences =
            getSharedPreferences(
                "MemoryPin",
                MODE_PRIVATE
            )

        val savedMemories =
            preferences.getString(
                "memories",
                "[]"
            )

        val memoriesArray =
            JSONArray(savedMemories)

        if (memoriesArray.length() == 0) {

            tvEmpty.visibility =
                View.VISIBLE

            return
        }

        tvEmpty.visibility =
            View.GONE

        // Newest memory first

        for (
        i in memoriesArray.length() - 1 downTo 0
        ) {

            val memory =
                memoriesArray.getJSONObject(i)

            addMemoryCard(memory)
        }
    }

    // ---------------- MEMORY CARD ----------------

    private fun addMemoryCard(
        memory: org.json.JSONObject
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            15,
            15,
            15,
            15
        )

        // Card background

        val background =
            GradientDrawable()

        background.setColor(
            Color.rgb(
                248,
                245,
                240
            )
        )

        background.cornerRadius =
            25f

        card.background =
            background

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.setMargins(
            0,
            0,
            0,
            25
        )

        card.layoutParams =
            cardParams


        // ---------------- PHOTO ----------------

        val photos =
            memory.getJSONArray(
                "photos"
            )

        if (photos.length() > 0) {

            val image =
                ImageView(this)

            image.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    220
                )

            image.scaleType =
                ImageView.ScaleType.CENTER_CROP

            val photoPath =
                photos.getString(0)

            val photoFile =
                File(photoPath)

            if (photoFile.exists()) {

                val bitmap =
                    BitmapFactory.decodeFile(
                        photoFile.absolutePath
                    )

                if (bitmap != null) {

                    image.setImageBitmap(
                        bitmap
                    )
                }
            }

            card.addView(
                image
            )
        }


        // ---------------- LOCATION ----------------

        val location =
            memory.getString(
                "location"
            )

        val locationText =
            TextView(this)

        locationText.text =
            "📍 $location"

        locationText.textSize =
            20f

        locationText.setTextColor(
            Color.BLACK
        )

        locationText.setPadding(
            5,
            15,
            5,
            5
        )

        card.addView(
            locationText
        )


        // ---------------- CAPTION ----------------

        val caption =
            memory.getString(
                "caption"
            )

        if (caption.isNotEmpty()) {

            val captionText =
                TextView(this)

            captionText.text =
                caption

            captionText.textSize =
                16f

            captionText.setTextColor(
                Color.DKGRAY
            )

            captionText.setPadding(
                5,
                5,
                5,
                10
            )

            card.addView(
                captionText
            )
        }


        // ---------------- STICKERS ----------------

        val stickers =
            memory.getJSONArray(
                "stickers"
            )

        if (stickers.length() > 0) {

            val stickerLayout =
                LinearLayout(this)

            stickerLayout.orientation =
                LinearLayout.HORIZONTAL

            for (
            j in 0 until stickers.length()
            ) {

                val stickerId =
                    stickers.getInt(j)

                val stickerNumber =
                    getStickerNumber(
                        stickerId
                    )

                if (stickerNumber != -1) {

                    val sticker =
                        ImageView(this)

                    sticker.setImageResource(
                        stickerDrawables[
                            stickerNumber
                        ]
                    )

                    sticker.layoutParams =
                        LinearLayout.LayoutParams(
                            60,
                            60
                        )

                    sticker.setPadding(
                        5,
                        5,
                        5,
                        5
                    )

                    stickerLayout.addView(
                        sticker
                    )
                }
            }

            card.addView(
                stickerLayout
            )
        }

        memoryContainer.addView(
            card
        )
    }

    // ---------------- STICKER NUMBER ----------------

    private fun getStickerNumber(
        stickerId: Int
    ): Int {

        return when (stickerId) {

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
    }
}