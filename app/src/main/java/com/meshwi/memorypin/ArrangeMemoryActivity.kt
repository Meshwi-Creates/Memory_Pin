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

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_arrange_memory
        )

        memoryCanvas =
            findViewById(R.id.memoryCanvas)

        val photos =
            intent.getStringArrayListExtra("photos")
                ?: arrayListOf()

        val location =
            intent.getStringExtra("location")
                ?: ""

        val caption =
            intent.getStringExtra("caption")
                ?: ""

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

    // ====================================================
    // CREATE SCRAPBOOK
    // ====================================================

    private fun createMemoryCanvas(
        photos: ArrayList<String>,
        location: String,
        caption: String,
        stickers: ArrayList<Int>
    ) {

        memoryCanvas.removeAllViews()

        val canvasWidth =
            memoryCanvas.width

        val canvasHeight =
            memoryCanvas.height

        val sidePadding =
            dpToPx(12)

        val gap =
            dpToPx(8)

        val availableWidth =
            canvasWidth - (sidePadding * 2)

        // ------------------------------------------------
        // 1 PHOTO
        // ------------------------------------------------

        if (photos.size == 1) {

            val photoSize =
                min(
                    (availableWidth * 0.72f).toInt(),
                    dpToPx(250)
                )

            addPhoto(
                photos[0],
                photoSize,
                photoSize,
                (canvasWidth - photoSize) / 2,
                dpToPx(100)
            )
        }

        // ------------------------------------------------
        // 2 PHOTOS
        // ------------------------------------------------

        else if (photos.size == 2) {

            val photoWidth =
                ((availableWidth - gap) * 0.47f)
                    .toInt()

            val photoHeight =
                min(
                    photoWidth,
                    dpToPx(210)
                )

            val totalWidth =
                photoWidth * 2 + gap

            val startLeft =
                (canvasWidth - totalWidth) / 2

            addPhoto(
                photos[0],
                photoWidth,
                photoHeight,
                startLeft,
                dpToPx(105)
            )

            addPhoto(
                photos[1],
                photoWidth,
                photoHeight,
                startLeft + photoWidth + gap,
                dpToPx(145)
            )
        }

        // ------------------------------------------------
        // 3 PHOTOS
        // ------------------------------------------------

        else if (photos.size >= 3) {

            val photoWidth =
                ((availableWidth - gap) * 0.47f)
                    .toInt()

            val photoHeight =
                min(
                    photoWidth,
                    dpToPx(170)
                )

            val totalWidth =
                photoWidth * 2 + gap

            val startLeft =
                (canvasWidth - totalWidth) / 2

            addPhoto(
                photos[0],
                photoWidth,
                photoHeight,
                startLeft,
                dpToPx(85)
            )

            addPhoto(
                photos[1],
                photoWidth,
                photoHeight,
                startLeft + photoWidth + gap,
                dpToPx(115)
            )

            val thirdWidth =
                min(
                    dpToPx(175),
                    availableWidth
                )

            addPhoto(
                photos[2],
                thirdWidth,
                thirdWidth,
                (canvasWidth - thirdWidth) / 2,
                dpToPx(270)
            )
        }

        // ------------------------------------------------
        // LOCATION
        // ------------------------------------------------

        if (location.isNotEmpty()) {

            val locationView =
                TextView(this)

            locationView.text =
                "📍 $location"

            locationView.textSize =
                17f

            locationView.setTextColor(
                Color.rgb(60, 55, 63)
            )

            locationView.setSingleLine(false)

            locationView.maxLines = 5

            locationView.setPadding(
                dpToPx(7),
                dpToPx(4),
                dpToPx(7),
                dpToPx(4)
            )

            locationView.background =
                createTextBackground()

            val locationWidth =
                min(
                    dpToPx(180),
                    availableWidth - dpToPx(10)
                )

            val params =
                FrameLayout.LayoutParams(
                    locationWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.leftMargin =
                dpToPx(12)

            params.topMargin =
                dpToPx(12)

            memoryCanvas.addView(
                locationView,
                params
            )

            locationView.tag =
                ElementTag(
                    type = "location"
                )

            makeDraggable(
                locationView
            )

            selectView(
                locationView,
                "location"
            )
        }

        // ------------------------------------------------
        // CAPTION
        // ------------------------------------------------

        if (caption.isNotEmpty()) {

            val captionView =
                TextView(this)

            captionView.text =
                caption

            captionView.textSize =
                15f

            captionView.setTextColor(
                Color.rgb(70, 65, 72)
            )

            captionView.setSingleLine(false)

            captionView.maxLines =
                20

            captionView.ellipsize =
                null

            captionView.setPadding(
                dpToPx(8),
                dpToPx(6),
                dpToPx(8),
                dpToPx(6)
            )

            captionView.background =
                createTextBackground()

            val captionWidth =
                min(
                    dpToPx(220),
                    availableWidth - dpToPx(10)
                )

            val params =
                FrameLayout.LayoutParams(
                    captionWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.leftMargin =
                (canvasWidth - captionWidth) / 2

            params.topMargin =
                canvasHeight - dpToPx(100)

            memoryCanvas.addView(
                captionView,
                params
            )

            captionView.tag =
                ElementTag(
                    type = "caption"
                )

            makeDraggable(
                captionView
            )

            selectView(
                captionView,
                "caption"
            )
        }

        // ------------------------------------------------
        // STICKERS
        // ------------------------------------------------

        for (i in stickers.indices) {

            val stickerId =
                stickers[i]

            val stickerNumber =
                getStickerNumber(
                    stickerId
                )

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

            val stickerSize =
                dpToPx(58)

            val params =
                FrameLayout.LayoutParams(
                    stickerSize,
                    stickerSize
                )

            val positions =
                arrayOf(
                    Pair(
                        dpToPx(10),
                        dpToPx(70)
                    ),
                    Pair(
                        canvasWidth - dpToPx(70),
                        dpToPx(65)
                    ),
                    Pair(
                        dpToPx(15),
                        canvasHeight - dpToPx(130)
                    ),
                    Pair(
                        canvasWidth - dpToPx(75),
                        canvasHeight - dpToPx(145)
                    ),
                    Pair(
                        canvasWidth / 2 - dpToPx(30),
                        dpToPx(45)
                    ),
                    Pair(
                        dpToPx(5),
                        canvasHeight / 2
                    ),
                    Pair(
                        canvasWidth - dpToPx(65),
                        canvasHeight / 2
                    ),
                    Pair(
                        canvasWidth / 2 - dpToPx(30),
                        canvasHeight - dpToPx(130)
                    )
                )

            val position =
                positions[
                    i % positions.size
                ]

            params.leftMargin =
                max(
                    0,
                    position.first
                )

            params.topMargin =
                max(
                    0,
                    position.second
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

            makeDraggable(
                sticker
            )

            selectView(
                sticker,
                "sticker"
            )
        }
    }

    // ====================================================
    // ADD PHOTO
    // ====================================================

    private fun addPhoto(
        photoUri: String,
        width: Int,
        height: Int,
        left: Int,
        top: Int
    ) {

        val imageView =
            ImageView(this)

        imageView.setImageURI(
            Uri.parse(photoUri)
        )

        imageView.scaleType =
            ImageView.ScaleType.CENTER_CROP

        imageView.setPadding(
            dpToPx(4),
            dpToPx(4),
            dpToPx(4),
            dpToPx(4)
        )

        imageView.background =
            createPhotoBackground()

        val params =
            FrameLayout.LayoutParams(
                width,
                height
            )

        params.leftMargin =
            max(
                0,
                left
            )

        params.topMargin =
            max(
                0,
                top
            )

        memoryCanvas.addView(
            imageView,
            params
        )

        imageView.tag =
            ElementTag(
                type = "photo",
                photoUri = photoUri
            )

        makeDraggable(
            imageView
        )

        selectView(
            imageView,
            "photo"
        )
    }

    // ====================================================
    // DRAG
    // ====================================================

    private fun makeDraggable(
        view: View
    ) {

        view.setOnTouchListener { v, event ->

            when (event.actionMasked) {

                MotionEvent.ACTION_DOWN -> {

                    selectView(
                        v,
                        getViewType(v)
                    )

                    startX =
                        event.rawX

                    startY =
                        event.rawY

                    startViewX =
                        v.x

                    startViewY =
                        v.y

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

                    val maxX =
                        max(
                            0f,
                            (
                                    memoryCanvas.width -
                                            v.width
                                    ).toFloat()
                        )

                    val maxY =
                        max(
                            0f,
                            (
                                    memoryCanvas.height -
                                            v.height
                                    ).toFloat()
                        )

                    newX =
                        min(
                            maxX,
                            max(
                                0f,
                                newX
                            )
                        )

                    newY =
                        min(
                            maxY,
                            max(
                                0f,
                                newY
                            )
                        )

                    v.x =
                        newX

                    v.y =
                        newY

                    true
                }

                MotionEvent.ACTION_UP -> {
                    true
                }

                else -> false
            }
        }
    }

    // ====================================================
    // SELECT
    // ====================================================

    private fun selectView(
        view: View,
        type: String
    ) {

        selectedView?.background =
            when (
                getViewType(
                    selectedView
                )
            ) {

                "photo" ->
                    createPhotoBackground()

                "location",
                "caption" ->
                    createTextBackground()

                else ->
                    null
            }

        selectedView =
            view

        selectedType =
            type

        view.background =
            createSelectedBackground(
                type
            )
    }

    // ====================================================
    // BUTTONS
    // ====================================================

    private fun setupButtons() {

        val small =
            findViewById<Button>(
                R.id.btnPhotoSmall
            )

        val big =
            findViewById<Button>(
                R.id.btnPhotoBig
            )

        val rotate =
            findViewById<Button>(
                R.id.btnPhotoRotate
            )

        val edit =
            findViewById<Button>(
                R.id.btnEditText
            )

        val delete =
            findViewById<Button>(
                R.id.btnPhotoDelete
            )

        val save =
            findViewById<Button>(
                R.id.btnSaveArrangement
            )

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

                memoryCanvas.removeView(
                    it
                )

                selectedView =
                    null

                selectedType =
                    ""

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

    // ====================================================
    // RESIZE
    // ====================================================

    private fun resizeSelected(
        amount: Int
    ) {

        val view =
            selectedView
                ?: return

        // =================================================
        // TEXT RESIZE
        // =================================================

        if (
            selectedType == "caption" ||
            selectedType == "location"
        ) {

            val textView =
                view as? TextView
                    ?: return

            val density =
                resources
                    .displayMetrics
                    .scaledDensity

            val currentSize =
                textView.textSize /
                        density

            val newSize =
                max(
                    8f,
                    min(
                        40f,
                        currentSize +
                                if (amount > 0) {
                                    2f
                                } else {
                                    -2f
                                }
                    )
                )

            // Change font size
            textView.textSize =
                newSize

            textView.setSingleLine(
                false
            )

            textView.ellipsize =
                null

            if (
                selectedType ==
                "caption"
            ) {

                textView.maxLines =
                    20

            } else {

                textView.maxLines =
                    5
            }

            // =================================================
            // IMPORTANT:
            // GROW WIDTH
            // =================================================

            val currentWidth =
                textView.width

            val widthChange =
                if (amount > 0) {
                    dpToPx(20)
                } else {
                    -dpToPx(20)
                }

            val minimumWidth =
                dpToPx(100)

            val maximumWidth =
                memoryCanvas.width -
                        dpToPx(10)

            var newWidth =
                currentWidth +
                        widthChange

            newWidth =
                max(
                    minimumWidth,
                    min(
                        maximumWidth,
                        newWidth
                    )
                )

            // =================================================
            // SET NEW WIDTH
            // =================================================

            val params =
                textView.layoutParams

            params.width =
                newWidth

            // Height MUST be automatic
            params.height =
                ViewGroup.LayoutParams.WRAP_CONTENT

            textView.layoutParams =
                params

            // =================================================
            // FORCE ANDROID TO MEASURE AGAIN
            // =================================================

            textView.measure(
                View.MeasureSpec.makeMeasureSpec(
                    newWidth,
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(
                    0,
                    View.MeasureSpec.UNSPECIFIED
                )
            )

            val newHeight =
                textView.measuredHeight

            params.height =
                newHeight

            textView.layoutParams =
                params

            // =================================================
            // KEEP INSIDE CANVAS
            // =================================================

            textView.post {

                val maxX =
                    max(
                        0f,
                        (
                                memoryCanvas.width -
                                        textView.width
                                ).toFloat()
                    )

                val maxY =
                    max(
                        0f,
                        (
                                memoryCanvas.height -
                                        textView.height
                                ).toFloat()
                    )

                textView.x =
                    min(
                        maxX,
                        max(
                            0f,
                            textView.x
                        )
                    )

                textView.y =
                    min(
                        maxY,
                        max(
                            0f,
                            textView.y
                        )
                    )

                textView.requestLayout()
            }

            return
        }

        // =================================================
        // PHOTO / STICKER RESIZE
        // =================================================

        val params =
            view.layoutParams

        val newWidth =
            max(
                dpToPx(40),
                view.width +
                        dpToPx(amount)
            )

        val newHeight =
            when (selectedType) {

                "photo",
                "sticker" ->
                    newWidth

                else ->
                    max(
                        dpToPx(30),
                        view.height +
                                dpToPx(amount)
                    )
            }

        params.width =
            newWidth

        params.height =
            newHeight

        view.layoutParams =
            params
    }

    // ====================================================
    // EDIT TEXT
    // ====================================================

    private fun editSelectedText() {

        val view =
            selectedView as? TextView
                ?: return

        val input =
            EditText(this)

        input.setText(
            view.text.toString()
                .replace(
                    "📍 ",
                    ""
                )
        )

        input.setSelection(
            input.text.length
        )

        AlertDialog.Builder(this)
            .setTitle(
                if (
                    selectedType ==
                    "location"
                ) {
                    "Edit Location"
                } else {
                    "Edit Caption"
                }
            )
            .setView(input)
            .setPositiveButton(
                "Save"
            ) { _, _ ->

                val newText =
                    input.text
                        .toString()
                        .trim()

                if (
                    selectedType ==
                    "location"
                ) {

                    view.text =
                        "📍 $newText"

                } else {

                    view.text =
                        newText
                }

                view.requestLayout()
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    // ====================================================
    // SAVE MEMORY
    // ====================================================

    private fun saveMemory() {

        if (
            memoryCanvas.width <= 0 ||
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

        val oldSelected =
            selectedView

        if (oldSelected != null) {

            oldSelected.background =
                when (
                    getViewType(
                        oldSelected
                    )
                ) {

                    "photo" ->
                        createPhotoBackground()

                    "location",
                    "caption" ->
                        createTextBackground()

                    else ->
                        null
                }
        }

        // ------------------------------------------------
        // CREATE SCRAPBOOK IMAGE
        // ------------------------------------------------

        val bitmap =
            Bitmap.createBitmap(
                memoryCanvas.width,
                memoryCanvas.height,
                Bitmap.Config.ARGB_8888
            )

        val canvas =
            Canvas(bitmap)

        memoryCanvas.draw(
            canvas
        )

        if (oldSelected != null) {

            oldSelected.background =
                createSelectedBackground(
                    getViewType(
                        oldSelected
                    )
                )
        }

        // ------------------------------------------------
        // SAVE PREVIEW
        // ------------------------------------------------

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

        // ------------------------------------------------
        // SAVE ELEMENT DATA
        // ------------------------------------------------

        val elements =
            JSONArray()

        for (
        i in 0 until
                memoryCanvas.childCount
        ) {

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

            if (
                tag.type ==
                "photo"
            ) {

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

            if (
                tag.type ==
                "sticker"
            ) {

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
                    textView?.text
                        ?.toString()
                        ?: ""
                )

                element.put(
                    "textSize",
                    textView?.textSize
                        ?: 0f
                )
            }

            elements.put(
                element
            )
        }

        // ------------------------------------------------
        // SAVE MEMORY JSON
        // ------------------------------------------------

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

        newMemories.put(
            memory
        )

        for (
        i in 0 until
                oldMemories.length()
        ) {

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

    // ====================================================
    // COPY PHOTO
    // ====================================================

    private fun copyPhotoToInternalStorage(
        uriString: String,
        timestamp: Long,
        index: Int
    ): String {

        try {

            val input =
                contentResolver
                    .openInputStream(
                        Uri.parse(
                            uriString
                        )
                    )

            if (input != null) {

                val file =
                    File(
                        filesDir,
                        "memory_photo_${timestamp}_$index.jpg"
                    )

                FileOutputStream(
                    file
                ).use { output ->

                    input.copyTo(
                        output
                    )
                }

                input.close()

                return file.absolutePath
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        return ""
    }

    // ====================================================
    // GET LOCATION
    // ====================================================

    private fun getLocationFromCanvas():
            String {

        for (
        i in 0 until
                memoryCanvas.childCount
        ) {

            val view =
                memoryCanvas.getChildAt(i)

            val tag =
                view.tag as? ElementTag
                    ?: continue

            if (
                tag.type ==
                "location"
            ) {

                return (
                        view as? TextView
                        )?.text
                    ?.toString()
                    ?.replace(
                        "📍 ",
                        ""
                    )
                    ?.trim()
                    ?: ""
            }
        }

        return ""
    }

    // ====================================================
    // GET CAPTION
    // ====================================================

    private fun getCaptionFromCanvas():
            String {

        for (
        i in 0 until
                memoryCanvas.childCount
        ) {

            val view =
                memoryCanvas.getChildAt(i)

            val tag =
                view.tag as? ElementTag
                    ?: continue

            if (
                tag.type ==
                "caption"
            ) {

                return (
                        view as? TextView
                        )?.text
                    ?.toString()
                    ?.trim()
                    ?: ""
            }
        }

        return ""
    }

    // ====================================================
    // GET VIEW TYPE
    // ====================================================

    private fun getViewType(
        view: View?
    ): String {

        val tag =
            view?.tag as? ElementTag

        return tag?.type
            ?: ""
    }

    // ====================================================
    // STICKER NUMBER
    // ====================================================

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

    // ====================================================
    // PHOTO BACKGROUND
    // ====================================================

    private fun createPhotoBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.WHITE
            )

            cornerRadius =
                dpToPx(8).toFloat()

            setStroke(
                dpToPx(2),
                Color.WHITE
            )
        }
    }

    // ====================================================
    // TEXT BACKGROUND
    // ====================================================

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

    // ====================================================
    // SELECTED BACKGROUND
    // ====================================================

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

    // ====================================================
    // DP TO PX
    // ====================================================

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
                dp *
                        resources
                            .displayMetrics
                            .density
                ).toInt()
    }
}