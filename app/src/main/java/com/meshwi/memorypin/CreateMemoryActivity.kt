package com.meshwi.memorypin

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.widget.FrameLayout

class CreateMemoryActivity : AppCompatActivity() {

    // =========================================================
    // PHOTOS
    // =========================================================

    private val selectedPhotos =
        ArrayList<Uri>()

    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView

    private lateinit var photoSlot1: View
    private lateinit var photoSlot2: View
    private lateinit var photoSlot3: View

    private lateinit var removeBadge1: View
    private lateinit var removeBadge2: View
    private lateinit var removeBadge3: View


    // =========================================================
    // STICKERS
    // =========================================================

    private val selectedStickerIds =
        ArrayList<Int>()

    private lateinit var stickerCount: TextView

    private lateinit var stickerPaletteRow:
            LinearLayout


    // =========================================================
    // STICKER DRAWABLES
    // =========================================================

    private val stickerDrawables =
        mapOf(

            R.id.sticker1 to R.drawable.map,

            R.id.sticker2 to R.drawable.airplane,

            R.id.sticker3 to R.drawable.suitcase,

            R.id.sticker4 to R.drawable.mountain,

            R.id.sticker5 to R.drawable.wave,

            R.id.sticker6 to R.drawable.ticket,

            R.id.sticker7 to R.drawable.globe,

            R.id.sticker8 to R.drawable.camera,

            R.id.sticker9 to R.drawable.heart,

            R.id.sticker10 to R.drawable.pin,

            R.id.sticker11 to R.drawable.sunglasses,

            R.id.sticker12 to R.drawable.polaroid,

            R.id.sticker13 to R.drawable.sun,

            R.id.sticker14 to R.drawable.palm,

            R.id.sticker15 to R.drawable.rainbow
        )


    // =========================================================
    // PHOTO PICKER
    // =========================================================

    private val photoPicker =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->

            if (uris.isEmpty()) {
                return@registerForActivityResult
            }


            if (uris.size > 3) {

                Toast.makeText(
                    this,
                    "Maximum 3 photos allowed",
                    Toast.LENGTH_SHORT
                ).show()

                return@registerForActivityResult
            }


            selectedPhotos.clear()

            selectedPhotos.addAll(
                uris.take(3)
            )

            showPhotos()
        }


    // =========================================================
    // STICKER PICKER
    // =========================================================

    private val stickerPicker =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (
                result.resultCode !=
                RESULT_OK
            ) {
                return@registerForActivityResult
            }


            val stickers =
                result.data
                    ?.getIntegerArrayListExtra(
                        "selectedStickers"
                    )


            if (stickers != null) {

                selectedStickerIds.clear()

                selectedStickerIds.addAll(
                    stickers.take(8)
                )

                updateStickerSection()
            }
        }


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_create_memory
        )


        // -----------------------------------------------------
        // PHOTO VIEWS
        // -----------------------------------------------------

        image1 =
            findViewById(
                R.id.image1
            )

        image2 =
            findViewById(
                R.id.image2
            )

        image3 =
            findViewById(
                R.id.image3
            )


        photoSlot1 =
            findViewById(
                R.id.photoSlot1
            )

        photoSlot2 =
            findViewById(
                R.id.photoSlot2
            )

        photoSlot3 =
            findViewById(
                R.id.photoSlot3
            )


        removeBadge1 =
            findViewById(
                R.id.removeBadge1
            )

        removeBadge2 =
            findViewById(
                R.id.removeBadge2
            )

        removeBadge3 =
            findViewById(
                R.id.removeBadge3
            )


        // -----------------------------------------------------
        // STICKER VIEWS
        // -----------------------------------------------------

        stickerCount =
            findViewById(
                R.id.tvStickerCount
            )

        stickerPaletteRow =
            findViewById(
                R.id.stickerPaletteRow
            )


        // -----------------------------------------------------
        // BACK
        // -----------------------------------------------------

        findViewById<View>(
            R.id.btnBack
        ).setOnClickListener {

            finish()
        }


        // -----------------------------------------------------
        // ADD PHOTOS
        // -----------------------------------------------------

        findViewById<View>(
            R.id.btnAddPhotos
        ).setOnClickListener {

            photoPicker.launch(
                "image/*"
            )
        }


        // -----------------------------------------------------
        // REMOVE PHOTO
        // -----------------------------------------------------

        removeBadge1.setOnClickListener {
            removePhoto(0)
        }

        removeBadge2.setOnClickListener {
            removePhoto(1)
        }

        removeBadge3.setOnClickListener {
            removePhoto(2)
        }


        // -----------------------------------------------------
        // LOCATION
        // -----------------------------------------------------

        val location =
            findViewById<EditText>(
                R.id.etLocation
            )

        val locationError =
            findViewById<TextView>(
                R.id.tvLocationError
            )


        location.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    if (
                        s.toString()
                            .trim()
                            .isNotEmpty()
                    ) {

                        locationError.visibility =
                            View.GONE
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )


        // -----------------------------------------------------
        // CAPTION
        // -----------------------------------------------------

        val caption =
            findViewById<EditText>(
                R.id.etCaption
            )

        val wordCount =
            findViewById<TextView>(
                R.id.tvWordCount
            )


        caption.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val text =
                        s?.toString()
                            ?.trim()
                            ?: ""


                    val words =
                        if (
                            text.isEmpty()
                        ) {
                            0
                        } else {
                            text.split(
                                "\\s+".toRegex()
                            ).size
                        }


                    wordCount.text =
                        "$words / 50 words"


                    if (
                        words > 50
                    ) {

                        caption.error =
                            "Maximum 50 words allowed"
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )


        // -----------------------------------------------------
        // CHOOSE MORE STICKERS
        // -----------------------------------------------------

        findViewById<View>(
            R.id.btnStickers
        ).setOnClickListener {

            if (
                selectedStickerIds.size >= 8
            ) {

                Toast.makeText(
                    this,
                    "Maximum 8 stickers selected",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            val intent =
                Intent(
                    this,
                    StickerActivity::class.java
                )


            intent.putIntegerArrayListExtra(
                "selectedStickers",
                selectedStickerIds
            )


            stickerPicker.launch(
                intent
            )
        }


        // -----------------------------------------------------
        // CONTINUE
        // -----------------------------------------------------

        findViewById<View>(
            R.id.btnSaveMemory
        ).setOnClickListener {

            openArrangeScreen()
        }


        // -----------------------------------------------------
        // INITIAL
        // -----------------------------------------------------

        showPhotos()

        updateStickerSection()
    }


    // =========================================================
    // PHOTOS
    // =========================================================

    private fun showPhotos() {

        image1.visibility =
            View.GONE

        image2.visibility =
            View.GONE

        image3.visibility =
            View.GONE


        removeBadge1.visibility =
            View.GONE

        removeBadge2.visibility =
            View.GONE

        removeBadge3.visibility =
            View.GONE


        photoSlot1.visibility =
            if (
                selectedPhotos.size >= 1
            ) {
                View.GONE
            } else {
                View.VISIBLE
            }


        photoSlot2.visibility =
            if (
                selectedPhotos.size >= 2
            ) {
                View.GONE
            } else {
                View.VISIBLE
            }


        photoSlot3.visibility =
            if (
                selectedPhotos.size >= 3
            ) {
                View.GONE
            } else {
                View.VISIBLE
            }


        if (
            selectedPhotos.size >= 1
        ) {

            image1.setImageURI(
                selectedPhotos[0]
            )

            image1.visibility =
                View.VISIBLE

            removeBadge1.visibility =
                View.VISIBLE
        }


        if (
            selectedPhotos.size >= 2
        ) {

            image2.setImageURI(
                selectedPhotos[1]
            )

            image2.visibility =
                View.VISIBLE

            removeBadge2.visibility =
                View.VISIBLE
        }


        if (
            selectedPhotos.size >= 3
        ) {

            image3.setImageURI(
                selectedPhotos[2]
            )

            image3.visibility =
                View.VISIBLE

            removeBadge3.visibility =
                View.VISIBLE
        }
    }


    private fun removePhoto(
        position: Int
    ) {

        if (
            position >=
            selectedPhotos.size
        ) {
            return
        }


        selectedPhotos.removeAt(
            position
        )

        showPhotos()
    }


    // =========================================================
    // STICKER SECTION
    // =========================================================

    private fun updateStickerSection() {

        stickerPaletteRow.removeAllViews()


        stickerCount.text =
            "${selectedStickerIds.size} / 8 selected"


        // -----------------------------------------------------
        // NOTHING SELECTED
        // SHOW ORIGINAL 8 CHOICES
        // -----------------------------------------------------

        if (
            selectedStickerIds.isEmpty()
        ) {

            showStickerChoices()

            return
        }


        // -----------------------------------------------------
        // SOMETHING SELECTED
        // SHOW ONLY SELECTED STICKERS
        // -----------------------------------------------------

        for (
        stickerId in selectedStickerIds
        ) {

            val drawable =
                stickerDrawables[
                    stickerId
                ]


            if (
                drawable != null
            ) {

                addSelectedSticker(
                    stickerId,
                    drawable
                )
            }
        }
    }


    // =========================================================
    // SHOW DEFAULT STICKER CHOICES
    // =========================================================

    private fun showStickerChoices() {

        val choices =
            listOf(

                R.id.sticker2,

                R.id.sticker8,

                R.id.sticker7,

                R.id.sticker9,

                R.id.sticker10,

                R.id.sticker12,

                R.id.sticker14,

                R.id.sticker4
            )


        for (
        stickerId in choices
        ) {

            val drawable =
                stickerDrawables[
                    stickerId
                ]


            if (
                drawable != null
            ) {

                addStickerChoice(
                    stickerId,
                    drawable
                )
            }
        }
    }


    // =========================================================
    // DEFAULT STICKER
    // =========================================================

    private fun addStickerChoice(
        stickerId: Int,
        drawable: Int
    ) {

        val image =
            ImageView(this)


        image.setImageResource(
            drawable
        )


        image.scaleType =
            ImageView.ScaleType.CENTER_INSIDE


        image.setPadding(
            dp(8),
            dp(8),
            dp(8),
            dp(8)
        )


        image.background =
            createStickerBox()


        val params =
            LinearLayout.LayoutParams(
                dp(78),
                dp(78)
            )


        params.setMargins(
            dp(5),
            0,
            dp(5),
            0
        )


        stickerPaletteRow.addView(
            image,
            params
        )


        image.setOnClickListener {

            if (
                selectedStickerIds.size >= 8
            ) {

                Toast.makeText(
                    this,
                    "Maximum 8 stickers allowed",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            if (
                !selectedStickerIds
                    .contains(stickerId)
            ) {

                selectedStickerIds.add(
                    stickerId
                )

                updateStickerSection()
            }
        }
    }


    // =========================================================
    // SELECTED STICKER
    // =========================================================

    private fun addSelectedSticker(
        stickerId: Int,
        drawable: Int
    ) {

        val wrapper =
            FrameLayout(
                this
            )


        wrapper.background =
            createSelectedStickerBox()


        val image =
            ImageView(this)


        image.setImageResource(
            drawable
        )


        image.scaleType =
            ImageView.ScaleType.CENTER_INSIDE


        image.setPadding(
            dp(5),
            dp(5),
            dp(5),
            dp(5)
        )


        wrapper.addView(
            image,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )


        // Small remove X

        val remove =
            TextView(this)


        remove.text =
            "×"


        remove.textSize =
            15f


        remove.setTextColor(
            Color.WHITE
        )


        remove.gravity =
            Gravity.CENTER


        remove.background =
            GradientDrawable().apply {

                shape =
                    GradientDrawable.OVAL

                setColor(
                    Color.rgb(
                        235,
                        125,
                        94
                    )
                )
            }


        val removeParams =
            FrameLayout.LayoutParams(
                dp(25),
                dp(25)
            )


        removeParams.gravity =
            Gravity.TOP or
                    Gravity.END


        wrapper.addView(
            remove,
            removeParams
        )


        val params =
            LinearLayout.LayoutParams(
                dp(85),
                dp(85)
            )


        params.setMargins(
            dp(5),
            0,
            dp(5),
            0
        )


        stickerPaletteRow.addView(
            wrapper,
            params
        )


        // Tap sticker itself → remove

        image.setOnClickListener {

            selectedStickerIds.remove(
                stickerId
            )

            updateStickerSection()
        }


        // Tap X → remove

        remove.setOnClickListener {

            selectedStickerIds.remove(
                stickerId
            )

            updateStickerSection()
        }
    }


    // =========================================================
    // OPEN CANVAS
    // =========================================================

    private fun openArrangeScreen() {

        // -----------------------------------------------------
        // PHOTO
        // -----------------------------------------------------

        if (
            selectedPhotos.isEmpty()
        ) {

            Toast.makeText(
                this,
                "Please select at least 1 photo",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // -----------------------------------------------------
        // LOCATION
        // -----------------------------------------------------

        val location =
            findViewById<EditText>(
                R.id.etLocation
            )


        val locationText =
            location.text
                .toString()
                .trim()


        if (
            locationText.isEmpty()
        ) {

            location.error =
                "Location is required"

            location.requestFocus()

            return
        }


        // -----------------------------------------------------
        // CAPTION
        // -----------------------------------------------------

        val caption =
            findViewById<EditText>(
                R.id.etCaption
            )


        val captionText =
            caption.text
                .toString()
                .trim()


        val words =
            if (
                captionText.isEmpty()
            ) {
                0
            } else {
                captionText
                    .split(
                        "\\s+".toRegex()
                    )
                    .size
            }


        if (
            words > 50
        ) {

            caption.error =
                "Maximum 50 words allowed"

            caption.requestFocus()

            return
        }


        // -----------------------------------------------------
        // PHOTOS
        // -----------------------------------------------------

        val photoStrings =
            ArrayList<String>()


        for (
        photo in selectedPhotos
        ) {

            photoStrings.add(
                photo.toString()
            )
        }


        // -----------------------------------------------------
        // CANVAS INTENT
        // -----------------------------------------------------

        val intent =
            Intent(
                this,
                ArrangeMemoryActivity::class.java
            )


        intent.putStringArrayListExtra(
            "photos",
            photoStrings
        )


        intent.putExtra(
            "location",
            locationText
        )


        intent.putExtra(
            "caption",
            captionText
        )


        intent.putIntegerArrayListExtra(
            "stickers",
            selectedStickerIds
        )


        startActivity(
            intent
        )
    }


    // =========================================================
    // STICKER BOX
    // =========================================================

    private fun createStickerBox():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.rgb(
                    248,
                    238,
                    216
                )
            )

            cornerRadius =
                dp(8).toFloat()
        }
    }


    private fun createSelectedStickerBox():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.rgb(
                    248,
                    238,
                    216
                )
            )

            cornerRadius =
                dp(10).toFloat()

            setStroke(
                dp(3),
                Color.rgb(
                    235,
                    125,
                    94
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
                        resources.displayMetrics.density
                ).toInt()
    }
}