package com.meshwi.memorypin

import android.content.Intent
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

    private var downX = 0f
    private var downY = 0f
    private var startX = 0f
    private var startY = 0f

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

        setContentView(
            R.layout.activity_arrange_memory
        )

        memoryCanvas =
            findViewById(R.id.memoryCanvas)

        setupButtons()
        styleControlButtons()

        val photos =
            intent.getStringArrayListExtra(
                "photos"
            ) ?: arrayListOf()

        val location =
            intent.getStringExtra(
                "location"
            ) ?: ""

        val caption =
            intent.getStringExtra(
                "caption"
            ) ?: ""

        val stickers =
            intent.getIntegerArrayListExtra(
                "stickers"
            ) ?: arrayListOf()

        memoryCanvas.post {

            createMemoryCanvas(
                photos,
                location,
                caption,
                stickers
            )
        }
    }

    // =========================================================
    // CREATE CANVAS
    // =========================================================

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

        val padding =
            dp(12)

        val gap =
            dp(8)

        val availableWidth =
            canvasWidth - padding * 2

        // -----------------------------------------------------
        // PHOTOS
        // -----------------------------------------------------

        when {

            photos.size == 1 -> {

                val size =
                    min(
                        dp(250),
                        (availableWidth * 0.72f).toInt()
                    )

                addPhoto(
                    photos[0],
                    size,
                    size,
                    (canvasWidth - size) / 2,
                    dp(100)
                )
            }

            photos.size == 2 -> {

                val width =
                    ((availableWidth - gap) * 0.47f)
                        .toInt()

                val height =
                    min(
                        width,
                        dp(210)
                    )

                val total =
                    width * 2 + gap

                val left =
                    (canvasWidth - total) / 2

                addPhoto(
                    photos[0],
                    width,
                    height,
                    left,
                    dp(105)
                )

                addPhoto(
                    photos[1],
                    width,
                    height,
                    left + width + gap,
                    dp(145)
                )
            }

            photos.size >= 3 -> {

                val width =
                    ((availableWidth - gap) * 0.47f)
                        .toInt()

                val height =
                    min(
                        width,
                        dp(170)
                    )

                val total =
                    width * 2 + gap

                val left =
                    (canvasWidth - total) / 2

                addPhoto(
                    photos[0],
                    width,
                    height,
                    left,
                    dp(80)
                )

                addPhoto(
                    photos[1],
                    width,
                    height,
                    left + width + gap,
                    dp(105)
                )

                val third =
                    min(
                        dp(175),
                        availableWidth
                    )

                addPhoto(
                    photos[2],
                    third,
                    third,
                    (canvasWidth - third) / 2,
                    dp(285)
                )
            }
        }

        // -----------------------------------------------------
        // LOCATION
        // -----------------------------------------------------

        if (location.isNotEmpty()) {

            val locationView =
                TextView(this)

            locationView.text =
                "📍 $location"

            locationView.textSize =
                16f

            locationView.setTextColor(
                Color.rgb(65, 61, 70)
            )

            locationView.setPadding(
                dp(10),
                dp(5),
                dp(10),
                dp(5)
            )

            locationView.background =
                createTextBackground()

            val width =
                min(
                    dp(180),
                    availableWidth - dp(10)
                )

            val params =
                FrameLayout.LayoutParams(
                    width,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.leftMargin =
                dp(12)

            params.topMargin =
                dp(12)

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
        }

        // -----------------------------------------------------
        // CAPTION
        // -----------------------------------------------------

        if (caption.isNotEmpty()) {

            val captionView =
                TextView(this)

            captionView.text =
                caption

            captionView.textSize =
                15f

            captionView.setTextColor(
                Color.rgb(65, 61, 70)
            )

            captionView.setPadding(
                dp(8),
                dp(6),
                dp(8),
                dp(6)
            )

            captionView.setSingleLine(false)

            captionView.maxLines =
                20

            captionView.background =
                createTextBackground()

            val width =
                min(
                    dp(220),
                    availableWidth - dp(10)
                )

            val params =
                FrameLayout.LayoutParams(
                    width,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.leftMargin =
                (canvasWidth - width) / 2

            params.topMargin =
                canvasHeight - dp(100)

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
        }

        // -----------------------------------------------------
        // STICKERS
        // -----------------------------------------------------

        for (i in stickers.indices) {

            addSticker(
                stickers[i],
                i,
                canvasWidth,
                canvasHeight
            )
        }
    }

    // =========================================================
    // PHOTO
    // =========================================================

    private fun addPhoto(
        uriString: String,
        width: Int,
        height: Int,
        left: Int,
        top: Int
    ) {

        val image =
            ImageView(this)

        image.setImageURI(
            Uri.parse(uriString)
        )

        image.scaleType =
            ImageView.ScaleType.CENTER_CROP

        image.setPadding(
            dp(4),
            dp(4),
            dp(4),
            dp(4)
        )

        image.background =
            createPhotoBackground()

        val params =
            FrameLayout.LayoutParams(
                width,
                height
            )

        params.leftMargin =
            max(0, left)

        params.topMargin =
            max(0, top)

        memoryCanvas.addView(
            image,
            params
        )

        image.tag =
            ElementTag(
                type = "photo",
                photoUri = uriString
            )

        makeDraggable(
            image
        )
    }

    // =========================================================
    // STICKER
    // =========================================================

    private fun addSticker(
        stickerId: Int,
        index: Int,
        canvasWidth: Int,
        canvasHeight: Int
    ) {

        val number =
            getStickerNumber(
                stickerId
            )

        if (number == -1) {
            return
        }

        val sticker =
            ImageView(this)

        sticker.setImageResource(
            stickerDrawables[number]
        )

        sticker.scaleType =
            ImageView.ScaleType.CENTER_INSIDE

        sticker.setPadding(
            dp(3),
            dp(3),
            dp(3),
            dp(3)
        )

        val size =
            dp(65)

        val params =
            FrameLayout.LayoutParams(
                size,
                size
            )

        val positions =
            arrayOf(
                Pair(dp(8), dp(65)),
                Pair(canvasWidth - dp(75), dp(60)),
                Pair(dp(8), canvasHeight - dp(145)),
                Pair(canvasWidth - dp(75), canvasHeight - dp(155)),
                Pair(canvasWidth / 2 - dp(32), dp(45)),
                Pair(dp(5), canvasHeight / 2),
                Pair(canvasWidth - dp(70), canvasHeight / 2),
                Pair(canvasWidth / 2 - dp(32), canvasHeight - dp(145))
            )

        val position =
            positions[
                index % positions.size
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
    }

    // =========================================================
    // DRAG
    // =========================================================

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

                    downX =
                        event.rawX

                    downY =
                        event.rawY

                    startX =
                        v.x

                    startY =
                        v.y

                    v.bringToFront()

                    true
                }

                MotionEvent.ACTION_MOVE -> {

                    val dx =
                        event.rawX - downX

                    val dy =
                        event.rawY - downY

                    var newX =
                        startX + dx

                    var newY =
                        startY + dy

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

    // =========================================================
    // SELECT
    // =========================================================

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
            createSelectedBackground()

        val label =
            findViewById<TextView>(
                R.id.selectedElementLabel
            )

        label.text =
            when (type) {

                "photo" ->
                    "📸  PHOTO SELECTED"

                "sticker" ->
                    "✨  STICKER SELECTED"

                "location" ->
                    "📍  LOCATION SELECTED"

                "caption" ->
                    "✍️  CAPTION SELECTED"

                else ->
                    "SELECT AN ELEMENT"
            }

        label.setTextColor(
            Color.rgb(
                112,
                82,
                170
            )
        )
    }

    // =========================================================
    // STYLE CONTROLS
    // =========================================================

    private fun styleControlButtons() {

        styleButton(
            findViewById(R.id.btnPhotoSmall),
            "−",
            false
        )

        styleButton(
            findViewById(R.id.btnPhotoBig),
            "+",
            false
        )

        styleButton(
            findViewById(R.id.btnPhotoRotate),
            "↻",
            false
        )

        styleButton(
            findViewById(R.id.btnBringFront),
            "↑",
            false
        )

        styleButton(
            findViewById(R.id.btnSendBack),
            "↓",
            false
        )

        styleButton(
            findViewById(R.id.btnEditText),
            "✎",
            false
        )

        styleButton(
            findViewById(R.id.btnPhotoDelete),
            "🗑",
            true
        )
    }

    private fun styleButton(
        button: Button,
        text: String,
        danger: Boolean
    ) {

        button.text =
            text

        button.textSize =
            if (danger) 18f else 22f

        button.setTextColor(
            if (danger) {
                Color.WHITE
            } else {
                Color.rgb(
                    70,
                    65,
                    75
                )
            }
        )

        button.background =
            GradientDrawable().apply {

                cornerRadius =
                    dp(12).toFloat()

                setColor(
                    if (danger) {
                        Color.rgb(
                            235,
                            125,
                            94
                        )
                    } else {
                        Color.WHITE
                    }
                )

                setStroke(
                    dp(1),
                    if (danger) {
                        Color.rgb(
                            235,
                            125,
                            94
                        )
                    } else {
                        Color.rgb(
                            220,
                            210,
                            198
                        )
                    }
                )
            }

        button.elevation =
            dp(2).toFloat()

        button.setPadding(
            0,
            0,
            0,
            0
        )
    }

    // =========================================================
    // BUTTON ACTIONS
    // =========================================================

    private fun setupButtons() {

        findViewById<View>(
            R.id.btnBackCanvas
        ).setOnClickListener {

            finish()
        }

        findViewById<Button>(
            R.id.btnPhotoSmall
        ).setOnClickListener {

            resizeSelected(
                -20
            )
        }

        findViewById<Button>(
            R.id.btnPhotoBig
        ).setOnClickListener {

            resizeSelected(
                20
            )
        }

        findViewById<Button>(
            R.id.btnPhotoRotate
        ).setOnClickListener {

            selectedView?.let {

                it.rotation += 15f
            }
        }

        findViewById<Button>(
            R.id.btnBringFront
        ).setOnClickListener {

            selectedView?.bringToFront()
        }

        findViewById<Button>(
            R.id.btnSendBack
        ).setOnClickListener {

            selectedView?.let {

                val parent =
                    it.parent

                if (
                    parent is ViewGroup
                ) {

                    parent.removeView(
                        it
                    )

                    parent.addView(
                        it,
                        0
                    )
                }
            }
        }

        findViewById<Button>(
            R.id.btnEditText
        ).setOnClickListener {

            if (
                selectedType == "caption" ||
                selectedType == "location"
            ) {

                editSelectedText()

            } else {

                Toast.makeText(
                    this,
                    "Select a caption or location first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        findViewById<Button>(
            R.id.btnPhotoDelete
        ).setOnClickListener {

            selectedView?.let {

                memoryCanvas.removeView(
                    it
                )

                selectedView =
                    null

                selectedType =
                    ""

                findViewById<TextView>(
                    R.id.selectedElementLabel
                ).text =
                    "SELECT AN ELEMENT"
            }
        }

        // IMPORTANT:
        // This is the correct button in the canvas XML.
        // NOT btnSaveMemory.

        findViewById<Button>(
            R.id.btnSaveArrangement
        ).setOnClickListener {

            saveMemory()
        }
    }

    // =========================================================
    // RESIZE
    // =========================================================

    private fun resizeSelected(
        amount: Int
    ) {

        val view =
            selectedView
                ?: return

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
                                if (amount > 0)
                                    2f
                                else
                                    -2f
                    )
                )

            textView.textSize =
                newSize

            return
        }

        val params =
            view.layoutParams

        val newSize =
            max(
                dp(45),
                min(
                    dp(280),
                    view.width + dp(amount)
                )
            )

        params.width =
            newSize

        params.height =
            newSize

        view.layoutParams =
            params
    }

    // =========================================================
    // EDIT TEXT
    // =========================================================

    private fun editSelectedText() {

        val textView =
            selectedView as? TextView
                ?: return

        val input =
            EditText(this)

        input.setText(
            textView.text
                .toString()
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

            .setView(
                input
            )

            .setPositiveButton(
                "Save"
            ) { _, _ ->

                val text =
                    input.text
                        .toString()
                        .trim()

                if (
                    selectedType ==
                    "location"
                ) {

                    textView.text =
                        "📍 $text"

                } else {

                    textView.text =
                        text
                }

                textView.requestLayout()
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }

    // =========================================================
    // SAVE MEMORY
    // =========================================================

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

        // -----------------------------------------------------
        // Temporarily remove selection border
        // -----------------------------------------------------

        val oldSelected =
            selectedView

        if (
            oldSelected != null
        ) {

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

        // -----------------------------------------------------
        // CREATE PREVIEW
        // -----------------------------------------------------

        val bitmap =
            Bitmap.createBitmap(
                memoryCanvas.width,
                memoryCanvas.height,
                Bitmap.Config.ARGB_8888
            )

        val bitmapCanvas =
            Canvas(bitmap)

        memoryCanvas.draw(
            bitmapCanvas
        )

        // Restore selection border

        if (
            oldSelected != null
        ) {

            oldSelected.background =
                createSelectedBackground()
        }

        // -----------------------------------------------------
        // SAVE IMAGE
        // -----------------------------------------------------

        val previewFile =
            File(
                filesDir,
                "memory_$timestamp.png"
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

        } catch (
            e: Exception
        ) {

            Toast.makeText(
                this,
                "Could not save memory",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // -----------------------------------------------------
        // SAVE ELEMENTS
        // -----------------------------------------------------

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
                tag.type == "photo"
            ) {

                val path =
                    copyPhoto(
                        tag.photoUri,
                        timestamp,
                        i
                    )

                element.put(
                    "photoPath",
                    path
                )
            }

            if (
                tag.type == "sticker"
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

                val text =
                    view as? TextView

                element.put(
                    "text",
                    text?.text
                        ?.toString()
                        ?: ""
                )

                element.put(
                    "textSize",
                    text?.textSize
                        ?: 0f
                )
            }

            elements.put(
                element
            )
        }

        // -----------------------------------------------------
        // GET OLD MEMORIES
        // -----------------------------------------------------

        val prefs =
            getSharedPreferences(
                "MemoryPin",
                MODE_PRIVATE
            )

        val oldMemories =
            JSONArray(
                prefs.getString(
                    "memories",
                    "[]"
                )
            )

        // -----------------------------------------------------
        // CREATE MEMORY
        // -----------------------------------------------------

        val memory =
            JSONObject()

        memory.put(
            "location",
            getLocation()
        )

        memory.put(
            "caption",
            getCaption()
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

        // -----------------------------------------------------
        // PHOTOS
        // -----------------------------------------------------

        val photos =
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

            if (
                tag.type == "photo"
            ) {

                photos.put(
                    tag.photoUri
                )
            }
        }

        memory.put(
            "photos",
            photos
        )

        // -----------------------------------------------------
        // PUT NEW MEMORY FIRST
        // -----------------------------------------------------

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

        prefs.edit()
            .putString(
                "memories",
                newMemories.toString()
            )
            .apply()

        // -----------------------------------------------------
        // GO HOME
        // -----------------------------------------------------

        Toast.makeText(
            this,
            "Memory saved successfully ❤️",
            Toast.LENGTH_SHORT
        ).show()

        val homeIntent =
            Intent(
                this,
                MainActivity::class.java
            )

        homeIntent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(
            homeIntent
        )

        finish()
    }

    // =========================================================
    // COPY PHOTO
    // =========================================================

    private fun copyPhoto(
        uriString: String,
        timestamp: Long,
        index: Int
    ): String {

        return try {

            val input =
                contentResolver
                    .openInputStream(
                        Uri.parse(
                            uriString
                        )
                    )

            if (
                input != null
            ) {

                val file =
                    File(
                        filesDir,
                        "photo_${timestamp}_$index.jpg"
                    )

                FileOutputStream(
                    file
                ).use { output ->

                    input.copyTo(
                        output
                    )
                }

                input.close()

                file.absolutePath

            } else {
                ""
            }

        } catch (
            e: Exception
        ) {

            ""
        }
    }

    // =========================================================
    // GET LOCATION
    // =========================================================

    private fun getLocation(): String {

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
                tag.type == "location"
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

    // =========================================================
    // GET CAPTION
    // =========================================================

    private fun getCaption(): String {

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
                tag.type == "caption"
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

    // =========================================================
    // VIEW TYPE
    // =========================================================

    private fun getViewType(
        view: View?
    ): String {

        return (
                view?.tag as? ElementTag
                )?.type ?: ""
    }

    // =========================================================
    // STICKER NUMBER
    // =========================================================

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

    // =========================================================
    // BACKGROUNDS
    // =========================================================

    private fun createPhotoBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.WHITE
            )

            cornerRadius =
                dp(8).toFloat()
        }
    }

    private fun createTextBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.argb(
                    220,
                    255,
                    255,
                    255
                )
            )

            cornerRadius =
                dp(10).toFloat()
        }
    }

    private fun createSelectedBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.TRANSPARENT
            )

            cornerRadius =
                dp(8).toFloat()

            setStroke(
                dp(3),
                Color.rgb(
                    112,
                    82,
                    170
                )
            )
        }
    }

    // =========================================================
    // DP
    // =========================================================

    private fun dp(
        value: Int
    ): Int {

        return (
                value *
                        resources
                            .displayMetrics
                            .density
                ).toInt()
    }
}