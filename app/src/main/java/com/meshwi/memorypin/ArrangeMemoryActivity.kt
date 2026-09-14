package com.meshwi.memorypin

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class ArrangeMemoryActivity : AppCompatActivity() {

    private lateinit var canvas: FrameLayout

    private val photos = ArrayList<String>()
    private val stickers = ArrayList<Int>()

    private var location = ""
    private var caption = ""

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

        setContentView(
            R.layout.activity_arrange_memory
        )

        canvas =
            findViewById(
                R.id.memoryCanvas
            )

        // Get data from Create Memory screen

        val photoList =
            intent.getStringArrayListExtra(
                "photos"
            )

        if (photoList != null) {

            photos.addAll(
                photoList
            )
        }

        location =
            intent.getStringExtra(
                "location"
            ) ?: ""

        caption =
            intent.getStringExtra(
                "caption"
            ) ?: ""

        val stickerList =
            intent.getIntegerArrayListExtra(
                "stickers"
            )

        if (stickerList != null) {

            stickers.addAll(
                stickerList
            )
        }

        createCanvasElements()


        // Save button

        val btnSave =
            findViewById<Button>(
                R.id.btnSaveArrangement
            )

        btnSave.setOnClickListener {

            saveMemory()
        }
    }

    // ---------------- CREATE ELEMENTS ----------------

    private fun createCanvasElements() {

        // Photos

        for (i in photos.indices) {

            addPhoto(
                photos[i],
                i
            )
        }

        // Location

        if (location.isNotEmpty()) {

            addLocation()
        }

        // Caption

        if (caption.isNotEmpty()) {

            addCaption()
        }

        // Stickers

        for (i in stickers.indices) {

            addSticker(
                stickers[i]
            )
        }
    }

    // ---------------- ADD PHOTO ----------------

    private fun addPhoto(
        photoUri: String,
        number: Int
    ) {

        val image =
            ImageView(this)

        image.layoutParams =
            FrameLayout.LayoutParams(
                150,
                150
            )

        image.scaleType =
            ImageView.ScaleType.CENTER_CROP

        image.setImageURI(
            Uri.parse(photoUri)
        )

        // Different starting positions

        image.translationX =
            30f + (number * 45f)

        image.translationY =
            30f + (number * 50f)

        makeDraggable(image)

        canvas.addView(
            image
        )
    }

    // ---------------- ADD LOCATION ----------------

    private fun addLocation() {

        val text =
            TextView(this)

        text.text =
            "📍 $location"

        text.textSize =
            18f

        text.setTextColor(
            Color.BLACK
        )

        text.setPadding(
            10,
            8,
            10,
            8
        )

        text.background =
            createBackground(
                Color.WHITE
            )

        text.layoutParams =
            FrameLayout.LayoutParams(
                220,
                60
            )

        text.translationX =
            30f

        text.translationY =
            250f

        makeDraggable(text)

        canvas.addView(
            text
        )
    }

    // ---------------- ADD CAPTION ----------------

    private fun addCaption() {

        val text =
            TextView(this)

        text.text =
            caption

        text.textSize =
            16f

        text.setTextColor(
            Color.DKGRAY
        )

        text.setPadding(
            10,
            10,
            10,
            10
        )

        text.background =
            createBackground(
                Color.WHITE
            )

        text.layoutParams =
            FrameLayout.LayoutParams(
                240,
                100
            )

        text.translationX =
            30f

        text.translationY =
            330f

        makeDraggable(text)

        canvas.addView(
            text
        )
    }

    // ---------------- ADD STICKER ----------------

    private fun addSticker(
        stickerId: Int
    ) {

        val stickerNumber =
            getStickerNumber(
                stickerId
            )

        if (stickerNumber == -1) {
            return
        }

        val image =
            ImageView(this)

        image.layoutParams =
            FrameLayout.LayoutParams(
                80,
                80
            )

        image.setImageResource(
            stickerDrawables[
                stickerNumber
            ]
        )

        // Start stickers at different positions

        val position =
            canvas.childCount

        image.translationX =
            180f + ((position % 3) * 60f)

        image.translationY =
            100f + ((position % 4) * 70f)

        makeDraggable(image)

        canvas.addView(
            image
        )
    }

    // ---------------- DRAGGING ----------------

    private fun makeDraggable(
        view: View
    ) {

        view.setOnTouchListener(
            object : View.OnTouchListener {

                private var downX = 0f
                private var downY = 0f

                private var originalX = 0f
                private var originalY = 0f

                override fun onTouch(
                    v: View,
                    event: MotionEvent
                ): Boolean {

                    when (event.action) {

                        MotionEvent.ACTION_DOWN -> {

                            downX =
                                event.rawX

                            downY =
                                event.rawY

                            originalX =
                                v.translationX

                            originalY =
                                v.translationY

                            // Bring selected item to front

                            v.bringToFront()

                            return true
                        }

                        MotionEvent.ACTION_MOVE -> {

                            val moveX =
                                event.rawX - downX

                            val moveY =
                                event.rawY - downY

                            v.translationX =
                                originalX + moveX

                            v.translationY =
                                originalY + moveY

                            return true
                        }

                        MotionEvent.ACTION_UP -> {

                            return true
                        }
                    }

                    return true
                }
            }
        )
    }

    // ---------------- BACKGROUND ----------------

    private fun createBackground(
        color: Int
    ): GradientDrawable {

        val background =
            GradientDrawable()

        background.setColor(
            color
        )

        background.cornerRadius =
            15f

        background.setStroke(
            1,
            Color.LTGRAY
        )

        return background
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

    // ---------------- SAVE ----------------

    private fun saveMemory() {

        if (photos.isEmpty()) {

            Toast.makeText(
                this,
                "Please add at least one photo",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val preferences =
            getSharedPreferences(
                "MemoryPin",
                MODE_PRIVATE
            )

        val oldMemories =
            preferences.getString(
                "memories",
                "[]"
            )

        val memoriesArray =
            JSONArray(
                oldMemories
            )

        val memoryObject =
            JSONObject()

        memoryObject.put(
            "location",
            location
        )

        memoryObject.put(
            "caption",
            caption
        )

        // Save photo paths

        val photoArray =
            JSONArray()

        for (photo in photos) {

            val savedPath =
                copyPhotoToStorage(
                    Uri.parse(photo)
                )

            if (savedPath != null) {

                photoArray.put(
                    savedPath
                )
            }
        }

        memoryObject.put(
            "photos",
            photoArray
        )

        // Save stickers

        val stickerArray =
            JSONArray()

        for (sticker in stickers) {

            stickerArray.put(
                sticker
            )
        }

        memoryObject.put(
            "stickers",
            stickerArray
        )

        memoriesArray.put(
            memoryObject
        )

        preferences.edit()
            .putString(
                "memories",
                memoriesArray.toString()
            )
            .apply()

        Toast.makeText(
            this,
            "Memory saved! ❤️",
            Toast.LENGTH_SHORT
        ).show()

        finishAffinity()
    }

    // ---------------- COPY PHOTO ----------------

    private fun copyPhotoToStorage(
        uri: Uri
    ): String? {

        return try {

            val fileName =
                "memory_${System.currentTimeMillis()}_${System.nanoTime()}.jpg"

            val file =
                File(
                    filesDir,
                    fileName
                )

            val input =
                contentResolver.openInputStream(
                    uri
                )

            val output =
                FileOutputStream(
                    file
                )

            input?.use { inputStream ->

                output.use { outputStream ->

                    inputStream.copyTo(
                        outputStream
                    )
                }
            }

            file.absolutePath

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }
}