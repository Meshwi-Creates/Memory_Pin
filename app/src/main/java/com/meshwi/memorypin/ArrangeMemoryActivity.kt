package com.meshwi.memorypin

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

class ArrangeMemoryActivity : AppCompatActivity() {

    private lateinit var memoryCanvas: FrameLayout

    private var selectedView: View? = null
    private var selectedType = ""

    private var startX = 0f
    private var startY = 0f
    private var startViewX = 0f
    private var startViewY = 0f

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

    data class ElementTag(
        val type: String,
        val photoUri: String = "",
        val stickerId: Int = -1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arrange_memory)

        memoryCanvas = findViewById(R.id.memoryCanvas)

        val photos =
            intent.getStringArrayListExtra("photos") ?: arrayListOf()

        val location =
            intent.getStringExtra("location") ?: ""

        val caption =
            intent.getStringExtra("caption") ?: ""

        val stickers =
            intent.getIntegerArrayListExtra("stickers")
                ?: arrayListOf()

        memoryCanvas.post {
            createMemoryCanvas(
                photos,
                location,
                caption,
                stickers
            )
        }

        setupButtons()
    }

    // ----------------------------------------------------
    // CREATE SCRAPBOOK
    // ----------------------------------------------------

    private fun createMemoryCanvas(
        photos: ArrayList<String>,
        location: String,
        caption: String,
        stickers: ArrayList<Int>
    ) {

        memoryCanvas.removeAllViews()

        // Add photos
        for (i in photos.indices) {

            val imageView = ImageView(this)

            imageView.setImageURI(Uri.parse(photos[i]))

            // VERY IMPORTANT:
            // keeps original photo proportions
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            imageView.setPadding(5, 5, 5, 5)

            imageView.background =
                createPhotoBackground()

            val size = dpToPx(150)

            val params =
                FrameLayout.LayoutParams(
                    size,
                    size
                )

            when (i) {
                0 -> {
                    params.leftMargin = dpToPx(20)
                    params.topMargin = dpToPx(30)
                }

                1 -> {
                    params.leftMargin = dpToPx(150)
                    params.topMargin = dpToPx(110)
                }

                2 -> {
                    params.leftMargin = dpToPx(70)
                    params.topMargin = dpToPx(260)
                }
            }

            memoryCanvas.addView(
                imageView,
                params
            )

            imageView.tag =
                ElementTag(
                    type = "photo",
                    photoUri = photos[i]
                )

            makeDraggable(imageView)
            selectView(imageView, "photo")
        }

        // Add location
        if (location.isNotEmpty()) {

            val locationView =
                TextView(this)

            locationView.text =
                "📍 $location"

            locationView.textSize = 18f
            locationView.setTextColor(
                Color.rgb(60, 55, 63)
            )

            locationView.setPadding(
                dpToPx(8),
                dpToPx(5),
                dpToPx(8),
                dpToPx(5)
            )

            locationView.background =
                createTextBackground()

            val params =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.leftMargin = dpToPx(15)
            params.topMargin = dpToPx(10)

            memoryCanvas.addView(
                locationView,
                params
            )

            locationView.tag =
                ElementTag(
                    type = "location"
                )

            makeDraggable(locationView)
            selectView(locationView, "location")
        }

        // Add caption
        if (caption.isNotEmpty()) {

            val captionView =
                TextView(this)

            captionView.text = caption

            captionView.textSize = 16f
            captionView.setTextColor(
                Color.rgb(70, 65, 72)
            )

            captionView.setPadding(
                dpToPx(10),
                dpToPx(8),
                dpToPx(10),
                dpToPx(8)
            )

            captionView.maxWidth =
                dpToPx(300)

            captionView.background =
                createTextBackground()

            val params =
                FrameLayout.LayoutParams(
                    dpToPx(300),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.leftMargin = dpToPx(25)
            params.topMargin = dpToPx(410)

            memoryCanvas.addView(
                captionView,
                params
            )

            captionView.tag =
                ElementTag(
                    type = "caption"
                )

            makeDraggable(captionView)
            selectView(captionView, "caption")
        }

        // Add stickers
        for (stickerId in stickers) {

            val stickerNumber =
                getStickerNumber(stickerId)

            if (stickerNumber == -1) {
                continue
            }

            val sticker =
                ImageView(this)

            sticker.setImageResource(
                stickerDrawables[stickerNumber]
            )

            sticker.scaleType =
                ImageView.ScaleType.FIT_CENTER

            val size = dpToPx(75)

            val params =
                FrameLayout.LayoutParams(
                    size,
                    size
                )

            params.leftMargin =
                dpToPx(
                    20 + (stickerNumber * 35) % 250
                )

            params.topMargin =
                dpToPx(
                    80 + (stickerNumber * 55) % 350
                )

            memoryCanvas.addView(
                sticker,
                params
            )

            sticker.tag =
                ElementTag(
                    type = "sticker",
                    stickerId = stickerId
                )

            makeDraggable(sticker)
            selectView(sticker, "sticker")
        }
    }

    // ----------------------------------------------------
    // DRAG
    // ----------------------------------------------------

    private fun makeDraggable(view: View) {

        view.setOnTouchListener { v, event ->

            when (event.actionMasked) {

                MotionEvent.ACTION_DOWN -> {

                    selectView(
                        v,
                        getViewType(v)
                    )

                    startX = event.rawX
                    startY = event.rawY

                    startViewX = v.x
                    startViewY = v.y

                    v.bringToFront()

                    true
                }

                MotionEvent.ACTION_MOVE -> {

                    val dx =
                        event.rawX - startX

                    val dy =
                        event.rawY - startY

                    var newX =
                        startViewX + dx

                    var newY =
                        startViewY + dy

                    // Keep the object INSIDE the canvas
                    val maxX =
                        max(
                            0f,
                            (memoryCanvas.width - v.width).toFloat()
                        )

                    val maxY =
                        max(
                            0f,
                            (memoryCanvas.height - v.height).toFloat()
                        )

                    newX =
                        min(
                            maxX,
                            max(0f, newX)
                        )

                    newY =
                        min(
                            maxY,
                            max(0f, newY)
                        )

                    v.x = newX
                    v.y = newY

                    true
                }

                MotionEvent.ACTION_UP -> {
                    true
                }

                else -> false
            }
        }
    }

    // ----------------------------------------------------
    // SELECT
    // ----------------------------------------------------

    private fun selectView(
        view: View,
        type: String
    ) {

        selectedView?.background =
            when (getViewType(selectedView)) {
                "photo" ->
                    createPhotoBackground()

                "location",
                "caption" ->
                    createTextBackground()

                else ->
                    null
            }

        selectedView = view
        selectedType = type

        view.background =
            createSelectedBackground(type)
    }

    // ----------------------------------------------------
    // BUTTONS
    // ----------------------------------------------------

    private fun setupButtons() {

        val small =
            findViewById<Button>(R.id.btnPhotoSmall)

        val big =
            findViewById<Button>(R.id.btnPhotoBig)

        val rotate =
            findViewById<Button>(R.id.btnPhotoRotate)

        val edit =
            findViewById<Button>(R.id.btnEditText)

        val delete =
            findViewById<Button>(R.id.btnPhotoDelete)

        val save =
            findViewById<Button>(R.id.btnSaveArrangement)

        small.setOnClickListener {
            resizeSelected(-20)
        }

        big.setOnClickListener {
            resizeSelected(20)
        }

        rotate.setOnClickListener {
            selectedView?.let {
                it.rotation += 15f
            }
        }

        edit.setOnClickListener {

            if (
                selectedType == "caption" ||
                selectedType == "location"
            ) {
                editSelectedText()
            }
        }

        delete.setOnClickListener {

            selectedView?.let {

                memoryCanvas.removeView(it)

                selectedView = null
                selectedType = ""

                Toast.makeText(
                    this,
                    "Element deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        save.setOnClickListener {
            saveMemory()
        }
    }

    // ----------------------------------------------------
    // RESIZE
    // ----------------------------------------------------

    private fun resizeSelected(
        amount: Int
    ) {

        val view = selectedView ?: return

        val params =
            view.layoutParams

        val newWidth =
            max(
                dpToPx(40),
                view.width + dpToPx(amount)
            )

        val newHeight =
            when (selectedType) {

                "photo",
                "sticker" -> newWidth

                else ->
                    max(
                        dpToPx(30),
                        view.height + dpToPx(amount)
                    )
            }

        params.width = newWidth
        params.height = newHeight

        view.layoutParams = params
    }

    // ----------------------------------------------------
    // EDIT TEXT
    // ----------------------------------------------------

    private fun editSelectedText() {

        val view =
            selectedView as? TextView
                ?: return

        val input =
            EditText(this)

        input.setText(
            view.text.toString()
                .replace("📍 ", "")
        )

        input.setSelection(
            input.text.length
        )

        AlertDialog.Builder(this)
            .setTitle(
                if (selectedType == "location")
                    "Edit Location"
                else
                    "Edit Caption"
            )
            .setView(input)
            .setPositiveButton("Save") { _, _ ->

                val newText =
                    input.text.toString().trim()

                if (selectedType == "location") {

                    view.text =
                        "📍 $newText"

                } else {

                    view.text =
                        newText
                }
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    // ----------------------------------------------------
    // SAVE MEMORY
    // ----------------------------------------------------

    private fun saveMemory() {

        if (memoryCanvas.width <= 0 ||
            memoryCanvas.height <= 0
        ) {
            Toast.makeText(
                this,
                "Please wait and try again",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val timestamp =
            System.currentTimeMillis()

        // Remove selection border before screenshot
        val oldSelected =
            selectedView

        if (oldSelected != null) {

            oldSelected.background =
                when (getViewType(oldSelected)) {

                    "photo" ->
                        createPhotoBackground()

                    "location",
                    "caption" ->
                        createTextBackground()

                    else ->
                        null
                }
        }

        // Create exact screenshot of scrapbook
        val bitmap =
            Bitmap.createBitmap(
                memoryCanvas.width,
                memoryCanvas.height,
                Bitmap.Config.ARGB_8888
            )

        val canvas =
            Canvas(bitmap)

        memoryCanvas.draw(canvas)

        // Put selection border back
        if (oldSelected != null) {

            oldSelected.background =
                createSelectedBackground(
                    getViewType(oldSelected)
                )
        }

        // Save scrapbook preview
        val previewFile =
            File(
                filesDir,
                "scrapbook_$timestamp.png"
            )

        try {

            FileOutputStream(
                previewFile
            ).use { output ->

                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    output
                )
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Could not save scrapbook",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // ---------------------------------------------
        // Save element information
        // ---------------------------------------------

        val elements =
            JSONArray()

        for (i in 0 until memoryCanvas.childCount) {

            val view =
                memoryCanvas.getChildAt(i)

            val tag =
                view.tag as? ElementTag
                    ?: continue

            val element =
                JSONObject()

            element.put(
                "type",
                tag.type
            )

            element.put(
                "x",
                view.x
            )

            element.put(
                "y",
                view.y
            )

            element.put(
                "width",
                view.width
            )

            element.put(
                "height",
                view.height
            )

            element.put(
                "rotation",
                view.rotation
            )

            if (tag.type == "photo") {

                val savedPhoto =
                    copyPhotoToInternalStorage(
                        tag.photoUri,
                        timestamp,
                        i
                    )

                element.put(
                    "photoPath",
                    savedPhoto
                )
            }

            if (tag.type == "sticker") {

                element.put(
                    "stickerId",
                    tag.stickerId
                )
            }

            if (
                tag.type == "caption" ||
                tag.type == "location"
            ) {

                val textView =
                    view as? TextView

                element.put(
                    "text",
                    textView?.text?.toString()
                        ?: ""
                )
            }

            elements.put(element)
        }

        // ---------------------------------------------
        // Main memory JSON
        // ---------------------------------------------

        val preferences =
            getSharedPreferences(
                "MemoryPin",
                MODE_PRIVATE
            )

        val oldMemories =
            JSONArray(
                preferences.getString(
                    "memories",
                    "[]"
                )
            )

        val memory =
            JSONObject()

        memory.put(
            "location",
            getLocationFromCanvas()
        )

        memory.put(
            "caption",
            getCaptionFromCanvas()
        )

        memory.put(
            "previewPath",
            previewFile.absolutePath
        )

        memory.put(
            "elements",
            elements
        )

        memory.put(
            "date",
            timestamp
        )

        val newMemories =
            JSONArray()

        newMemories.put(memory)

        for (i in 0 until oldMemories.length()) {
            newMemories.put(
                oldMemories.getJSONObject(i)
            )
        }

        preferences
            .edit()
            .putString(
                "memories",
                newMemories.toString()
            )
            .apply()

        Toast.makeText(
            this,
            "Memory saved! ❤️",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    // ----------------------------------------------------
    // COPY PHOTO
    // ----------------------------------------------------

    private fun copyPhotoToInternalStorage(
        uriString: String,
        timestamp: Long,
        index: Int
    ): String {

        try {

            val input =
                contentResolver.openInputStream(
                    Uri.parse(uriString)
                )

            if (input != null) {

                val file =
                    File(
                        filesDir,
                        "memory_photo_${timestamp}_$index.jpg"
                    )

                FileOutputStream(file).use { output ->

                    input.copyTo(output)
                }

                input.close()

                return file.absolutePath
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    // ----------------------------------------------------
    // GET LOCATION
    // ----------------------------------------------------

    private fun getLocationFromCanvas(): String {

        for (
        i in 0 until memoryCanvas.childCount
        ) {

            val view =
                memoryCanvas.getChildAt(i)

            val tag =
                view.tag as? ElementTag
                    ?: continue

            if (tag.type == "location") {

                return (
                        view as? TextView
                        )?.text?.toString()
                    ?.replace("📍 ", "")
                    ?.trim()
                    ?: ""
            }
        }

        return ""
    }

    // ----------------------------------------------------
    // GET CAPTION
    // ----------------------------------------------------

    private fun getCaptionFromCanvas(): String {

        for (
        i in 0 until memoryCanvas.childCount
        ) {

            val view =
                memoryCanvas.getChildAt(i)

            val tag =
                view.tag as? ElementTag
                    ?: continue

            if (tag.type == "caption") {

                return (
                        view as? TextView
                        )?.text?.toString()
                    ?.trim()
                    ?: ""
            }
        }

        return ""
    }

    // ----------------------------------------------------
    // HELPERS
    // ----------------------------------------------------

    private fun getViewType(
        view: View?
    ): String {

        val tag =
            view?.tag as? ElementTag

        return tag?.type ?: ""
    }

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

    private fun createPhotoBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(Color.WHITE)

            cornerRadius =
                dpToPx(8).toFloat()

            setStroke(
                dpToPx(2),
                Color.WHITE
            )
        }
    }

    private fun createTextBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.argb(
                    180,
                    255,
                    255,
                    255
                )
            )

            cornerRadius =
                dpToPx(10).toFloat()
        }
    }

    private fun createSelectedBackground(
        type: String
    ): GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.TRANSPARENT
            )

            cornerRadius =
                dpToPx(8).toFloat()

            setStroke(
                dpToPx(2),
                Color.rgb(
                    112,
                    82,
                    170
                )
            )
        }
    }

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }
}