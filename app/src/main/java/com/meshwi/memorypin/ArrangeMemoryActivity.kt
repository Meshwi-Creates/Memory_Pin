package com.meshwi.memorypin

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
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

    // Type of selected element
    private var selectedType = ""

    private val photos =
        ArrayList<String>()

    private val stickers =
        ArrayList<Int>()

    private var location = ""

    private var caption = ""

    private lateinit var locationTextView: TextView

    private lateinit var captionTextView: TextView

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
    // CREATE ELEMENTS
    // =================================================

    private fun createCanvasElements() {

        for (i in photos.indices) {

            addPhoto(
                photos[i],
                i
            )
        }

        if (location.isNotEmpty()) {

            addLocation()
        }

        if (caption.isNotEmpty()) {

            addCaption()
        }

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
            30f + number * 45f

        image.translationY =
            30f + number * 45f

        image.setPadding(
            4,
            4,
            4,
            4
        )

        image.setBackgroundColor(
            Color.WHITE
        )

        makeDraggable(
            image,
            "photo"
        )

        canvas.addView(
            image
        )
    }

    // =================================================
    // LOCATION
    // =================================================

    private fun addLocation() {

        locationTextView =
            TextView(this)

        locationTextView.text =
            "📍 $location"

        locationTextView.textSize =
            18f

        locationTextView.gravity =
            Gravity.CENTER

        locationTextView.setTextColor(
            Color.BLACK
        )

        locationTextView.setPadding(
            15,
            8,
            15,
            8
        )

        locationTextView.background =
            createBackground(
                Color.WHITE
            )

        locationTextView.layoutParams =
            FrameLayout.LayoutParams(
                220,
                60
            )

        locationTextView.translationX =
            30f

        locationTextView.translationY =
            270f

        makeDraggable(
            locationTextView,
            "location"
        )

        canvas.addView(
            locationTextView
        )
    }

    // =================================================
    // CAPTION
    // =================================================

    private fun addCaption() {

        captionTextView =
            TextView(this)

        captionTextView.text =
            caption

        captionTextView.textSize =
            16f

        captionTextView.gravity =
            Gravity.CENTER

        captionTextView.setTextColor(
            Color.DKGRAY
        )

        captionTextView.setPadding(
            15,
            10,
            15,
            10
        )

        captionTextView.background =
            createBackground(
                Color.WHITE
            )

        captionTextView.layoutParams =
            FrameLayout.LayoutParams(
                240,
                100
            )

        captionTextView.translationX =
            30f

        captionTextView.translationY =
            350f

        makeDraggable(
            captionTextView,
            "caption"
        )

        canvas.addView(
            captionTextView
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

        image.setPadding(
            4,
            4,
            4,
            4
        )

        image.translationX =
            170f + number * 55f

        image.translationY =
            100f + number * 55f

        makeDraggable(
            image,
            "sticker"
        )

        canvas.addView(
            image
        )
    }

    // =================================================
    // DRAG
    // =================================================

    private fun makeDraggable(
        view: View,
        type: String
    ) {

        var lastX = 0f
        var lastY = 0f

        view.setOnTouchListener {

                v,
                event ->

            when (
                event.actionMasked
            ) {

                MotionEvent.ACTION_DOWN -> {

                    lastX =
                        event.rawX

                    lastY =
                        event.rawY

                    selectView(
                        v,
                        type
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
    // SELECT
    // =================================================

    private fun selectView(
        view: View,
        type: String
    ) {

        // Remove previous border

        selectedView?.let {

            when (selectedType) {

                "photo" -> {

                    it.background =
                        createPhotoBackground()
                }

                "location",
                "caption" -> {

                    it.background =
                        createBackground(
                            Color.WHITE
                        )
                }

                "sticker" -> {

                    it.background =
                        null
                }
            }
        }

        selectedView =
            view

        selectedType =
            type

        // Purple selection border

        val border =
            GradientDrawable()

        border.setColor(
            Color.TRANSPARENT
        )

        border.setStroke(
            3,
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
    // BUTTONS
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

        val btnEdit =
            findViewById<Button>(
                R.id.btnEditText
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

        // EDIT TEXT

        btnEdit.setOnClickListener {

            if (
                selectedType == "caption" ||
                selectedType == "location"
            ) {

                editTextElement(
                    selectedType
                )

            } else {

                Toast.makeText(
                    this,
                    "Select caption or location to edit",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

            selectedType =
                ""
        }

        // SAVE

        btnSave.setOnClickListener {

            saveMemory()
        }
    }

    // =================================================
    // EDIT TEXT
    // =================================================

    private fun editTextElement(
        type: String
    ) {

        val editText =
            EditText(this)

        if (type == "caption") {

            editText.setText(
                caption
            )

            editText.hint =
                "Enter your caption"

        } else {

            editText.setText(
                location
            )

            editText.hint =
                "Enter location"
        }

        editText.inputType =
            if (type == "caption") {

                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE

            } else {

                InputType.TYPE_CLASS_TEXT
            }

        val title =
            if (type == "caption") {

                "Edit Caption"

            } else {

                "Edit Location"
            }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setNegativeButton(
                "CANCEL",
                null
            )
            .setPositiveButton(
                "SAVE"
            ) { _, _ ->

                val newText =
                    editText.text
                        .toString()
                        .trim()

                if (type == "caption") {

                    caption =
                        newText

                    captionTextView.text =
                        newText

                } else {

                    location =
                        newText

                    locationTextView.text =
                        "📍 $newText"
                }
            }
            .show()
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

        val minimumSize =
            when (selectedType) {

                "photo" ->
                    100

                "sticker" ->
                    40

                "location" ->
                    120

                "caption" ->
                    140

                else ->
                    50
            }

        val maximumSize =
            when (selectedType) {

                "photo" ->
                    400

                "sticker" ->
                    200

                "location" ->
                    400

                "caption" ->
                    450

                else ->
                    400
            }

        val newWidth =
            params.width + amount

        val newHeight =
            params.height + amount

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
            "Tap an element first",
            Toast.LENGTH_SHORT
        ).show()
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

    private fun createPhotoBackground():
            GradientDrawable {

        val background =
            GradientDrawable()

        background.setColor(
            Color.WHITE
        )

        background.cornerRadius =
            8f

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