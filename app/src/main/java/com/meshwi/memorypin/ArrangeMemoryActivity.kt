package com.meshwi.memorypin

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
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

    // Currently selected element
    private var selectedView: View? = null

    // Is selected element a photo?
    private var selectedIsPhoto = false

    private val photos =
        ArrayList<String>()

    private val stickers =
        ArrayList<Int>()

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

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_arrange_memory
        )

        canvas =
            findViewById(
                R.id.memoryCanvas
            )

        // Get photos

        val photoList =
            intent.getStringArrayListExtra(
                "photos"
            )

        if (photoList != null) {

            photos.addAll(
                photoList
            )
        }

        // Get location

        location =
            intent.getStringExtra(
                "location"
            ) ?: ""

        // Get caption

        caption =
            intent.getStringExtra(
                "caption"
            ) ?: ""

        // Get stickers

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

        setupButtons()
    }

    // =================================================
    // CREATE ALL ELEMENTS
    // =================================================

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
                stickers[i],
                i
            )
        }
    }

    // =================================================
    // PHOTO
    // =================================================

    private fun addPhoto(
        photoUri: String,
        number: Int
    ) {

        val image =
            ImageView(this)

        image.layoutParams =
            FrameLayout.LayoutParams(
                180,
                180
            )

        image.scaleType =
            ImageView.ScaleType.CENTER_CROP

        image.setImageURI(
            Uri.parse(photoUri)
        )

        image.translationX =
            25f + number * 45f

        image.translationY =
            30f + number * 45f

        image.setBackgroundColor(
            Color.WHITE
        )

        image.setPadding(
            4,
            4,
            4,
            4
        )

        image.setOnClickListener {

            selectView(
                image,
                true
            )
        }

        makeDraggable(
            image,
            true
        )

        canvas.addView(
            image
        )
    }

    // =================================================
    // STICKER
    // =================================================

    private fun addSticker(
        stickerId: Int,
        number: Int
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

        image.scaleType =
            ImageView.ScaleType.CENTER_INSIDE

        image.translationX =
            170f +
                    (number % 3) * 65f

        image.translationY =
            100f +
                    (number % 4) * 65f

        image.setPadding(
            4,
            4,
            4,
            4
        )

        image.setOnClickListener {

            selectView(
                image,
                false
            )
        }

        makeDraggable(
            image,
            false
        )

        canvas.addView(
            image
        )
    }

    // =================================================
    // SELECT ELEMENT
    // =================================================

    private fun selectView(
        view: View,
        isPhoto: Boolean
    ) {

        // Remove border from previous selection

        selectedView?.background =
            null

        // Select new element

        selectedView =
            view

        selectedIsPhoto =
            isPhoto

        // Add purple border

        val border =
            GradientDrawable()

        border.setColor(
            Color.TRANSPARENT
        )

        border.setStroke(
            4,
            Color.rgb(
                120,
                80,
                180
            )
        )

        border.cornerRadius =
            8f

        view.background =
            border

        view.bringToFront()
    }

    // =================================================
    // DRAG
    // =================================================

    private fun makeDraggable(
        view: View,
        isPhoto: Boolean
    ) {

        var lastX = 0f

        var lastY = 0f

        view.setOnTouchListener {

                v,
                event ->

            when (
                event.action
            ) {

                MotionEvent.ACTION_DOWN -> {

                    lastX =
                        event.rawX

                    lastY =
                        event.rawY

                    selectView(
                        v,
                        isPhoto
                    )

                    true
                }

                MotionEvent.ACTION_MOVE -> {

                    val dx =
                        event.rawX -
                                lastX

                    val dy =
                        event.rawY -
                                lastY

                    v.translationX +=
                        dx

                    v.translationY +=
                        dy

                    lastX =
                        event.rawX

                    lastY =
                        event.rawY

                    true
                }

                MotionEvent.ACTION_UP -> {

                    true
                }

                else -> false
            }
        }
    }

    // =================================================
    // CONTROLS
    // =================================================

    private fun setupButtons() {

        val btnSmall =
            findViewById<Button>(
                R.id.btnPhotoSmall
            )

        val btnBig =
            findViewById<Button>(
                R.id.btnPhotoBig
            )

        val btnRotate =
            findViewById<Button>(
                R.id.btnPhotoRotate
            )

        val btnDelete =
            findViewById<Button>(
                R.id.btnPhotoDelete
            )

        val btnSave =
            findViewById<Button>(
                R.id.btnSaveArrangement
            )

        // SMALLER

        btnSmall.setOnClickListener {

            if (selectedView == null) {

                showSelectMessage()

                return@setOnClickListener
            }

            resizeSelected(
                -15
            )
        }

        // BIGGER

        btnBig.setOnClickListener {

            if (selectedView == null) {

                showSelectMessage()

                return@setOnClickListener
            }

            resizeSelected(
                15
            )
        }

        // ROTATE

        btnRotate.setOnClickListener {

            if (selectedView == null) {

                showSelectMessage()

                return@setOnClickListener
            }

            selectedView?.rotation =
                (selectedView?.rotation ?: 0f) +
                        15f
        }

        // DELETE

        btnDelete.setOnClickListener {

            if (selectedView == null) {

                showSelectMessage()

                return@setOnClickListener
            }

            canvas.removeView(
                selectedView
            )

            selectedView =
                null
        }

        // SAVE

        btnSave.setOnClickListener {

            saveMemory()
        }
    }

    // =================================================
    // RESIZE
    // =================================================

    private fun resizeSelected(
        amount: Int
    ) {

        val view =
            selectedView
                ?: return

        val params =
            view.layoutParams

        val newWidth =
            params.width + amount

        val newHeight =
            params.height + amount

        val minimumSize =
            if (selectedIsPhoto) {
                100
            } else {
                40
            }

        val maximumSize =
            if (selectedIsPhoto) {
                400
            } else {
                200
            }

        if (
            newWidth >= minimumSize &&
            newWidth <= maximumSize
        ) {

            params.width =
                newWidth

            params.height =
                newHeight

            view.layoutParams =
                params
        }
    }

    // =================================================
    // MESSAGE
    // =================================================

    private fun showSelectMessage() {

        Toast.makeText(
            this,
            "Tap a photo or sticker first",
            Toast.LENGTH_SHORT
        ).show()
    }

    // =================================================
    // LOCATION
    // =================================================

    private fun addLocation() {

        val text =
            TextView(this)

        text.text =
            "📍 $location"

        text.textSize =
            18f

        text.gravity =
            Gravity.CENTER

        text.setTextColor(
            Color.BLACK
        )

        text.setPadding(
            10,
            5,
            10,
            5
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
            270f

        makeTextDraggable(
            text
        )

        canvas.addView(
            text
        )
    }

    // =================================================
    // CAPTION
    // =================================================

    private fun addCaption() {

        val text =
            TextView(this)

        text.text =
            caption

        text.textSize =
            16f

        text.gravity =
            Gravity.CENTER

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
            350f

        makeTextDraggable(
            text
        )

        canvas.addView(
            text
        )
    }

    // =================================================
    // DRAG TEXT / OTHER ELEMENTS
    // =================================================

    private fun makeTextDraggable(
        view: View
    ) {

        var lastX = 0f

        var lastY = 0f

        view.setOnTouchListener {

                v,
                event ->

            when (
                event.action
            ) {

                MotionEvent.ACTION_DOWN -> {

                    lastX =
                        event.rawX

                    lastY =
                        event.rawY

                    v.bringToFront()

                    true
                }

                MotionEvent.ACTION_MOVE -> {

                    val dx =
                        event.rawX -
                                lastX

                    val dy =
                        event.rawY -
                                lastY

                    v.translationX +=
                        dx

                    v.translationY +=
                        dy

                    lastX =
                        event.rawX

                    lastY =
                        event.rawY

                    true
                }

                else -> true
            }
        }
    }

    // =================================================
    // BACKGROUND
    // =================================================

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

    // =================================================
    // STICKER NUMBER
    // =================================================

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

    // =================================================
    // SAVE
    // =================================================

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

        // Save photos

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

    // =================================================
    // COPY PHOTO
    // =================================================

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