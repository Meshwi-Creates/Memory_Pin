package com.meshwi.memorypin

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemoryPreviewActivity : AppCompatActivity() {

    private lateinit var mainImage: ImageView
    private lateinit var similarContainer: LinearLayout

    private lateinit var filterSameLocation: Chip
    private lateinit var filterSameDate: Chip
    private lateinit var filterSamePeople: Chip

    private var currentLocation = ""
    private var currentDate = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContentView(
            R.layout.activity_memory_preview
        )

        mainImage =
            findViewById(R.id.previewMainImage)

        similarContainer =
            findViewById(R.id.similarMemoryContainer)

        currentLocation =
            intent.getStringExtra("location") ?: ""

        currentDate =
            intent.getLongExtra(
                "date",
                System.currentTimeMillis()
            )

        val previewPath =
            intent.getStringExtra("previewPath")

        val caption =
            intent.getStringExtra("caption") ?: ""

        // =====================================================
        // MAIN PHOTO
        // =====================================================

        if (
            !previewPath.isNullOrEmpty() &&
            File(previewPath).exists()
        ) {
            mainImage.setImageBitmap(
                BitmapFactory.decodeFile(previewPath)
            )
        }

        // =====================================================
        // LOCATION
        // =====================================================

        val locationText =
            findViewById<TextView>(
                R.id.previewLocation
            )

        locationText.text =
            if (currentLocation.isNotEmpty()) {
                "📍 $currentLocation"
            } else {
                "📍 Travel Memory"
            }

        // =====================================================
        // CAPTION
        // =====================================================

        val captionText =
            findViewById<TextView>(
                R.id.previewCaption
            )

        captionText.text =
            if (caption.isNotEmpty()) {
                caption
            } else {
                "A special moment worth remembering ✨"
            }

        // =====================================================
        // DATE
        // =====================================================

        val dateText =
            findViewById<TextView>(
                R.id.previewDate
            )

        dateText.text =
            formatDate(currentDate)

        // =====================================================
        // PLACE
        // =====================================================

        val placeText =
            findViewById<TextView>(
                R.id.previewPlaceName
            )

        placeText.text =
            if (currentLocation.isNotEmpty()) {
                currentLocation
            } else {
                "Not added"
            }

        // =====================================================
        // PEOPLE
        // =====================================================

        val peopleText =
            findViewById<TextView>(
                R.id.previewPeople
            )

        peopleText.text = "Not added"

        // =====================================================
        // STICKERS
        // =====================================================

        val stickerCount =
            findViewById<TextView>(
                R.id.previewStickerCount
            )

        val elementsString =
            intent.getStringExtra("elements") ?: "[]"

        stickerCount.text =
            countStickers(elementsString).toString()

        // =====================================================
        // BACK
        // =====================================================

        findViewById<View>(
            R.id.btnBackPreview
        ).setOnClickListener {
            finish()
        }

        // =====================================================
        // FILTER CHIPS
        // =====================================================

        filterSameLocation =
            findViewById(
                R.id.filterSameLocation
            )

        filterSameDate =
            findViewById(
                R.id.filterSameDate
            )

        filterSamePeople =
            findViewById(
                R.id.filterSamePeople
            )

        setupFilterChips()

        // =====================================================
        // SIMILAR MEMORIES
        // =====================================================

        createSimilarMemories(previewPath)
    }

    // =========================================================
    // FILTER CHIP SETUP
    // =========================================================

    private fun setupFilterChips() {

        // Start with Same Location selected
        filterSameLocation.isChecked = true
        filterSameDate.isChecked = false
        filterSamePeople.isChecked = false

        updateChipColors()

        filterSameLocation.setOnClickListener {

            filterSameLocation.isChecked = true
            filterSameDate.isChecked = false
            filterSamePeople.isChecked = false

            updateChipColors()
        }

        filterSameDate.setOnClickListener {

            filterSameLocation.isChecked = false
            filterSameDate.isChecked = true
            filterSamePeople.isChecked = false

            updateChipColors()
        }

        filterSamePeople.setOnClickListener {

            filterSameLocation.isChecked = false
            filterSameDate.isChecked = false
            filterSamePeople.isChecked = true

            updateChipColors()
        }
    }

    // =========================================================
    // UPDATE CHIP COLORS
    // =========================================================

    private fun updateChipColors() {

        setChipColor(
            filterSameLocation,
            filterSameLocation.isChecked
        )

        setChipColor(
            filterSameDate,
            filterSameDate.isChecked
        )

        setChipColor(
            filterSamePeople,
            filterSamePeople.isChecked
        )
    }

    private fun setChipColor(
        chip: Chip,
        selected: Boolean
    ) {

        val selectedColor =
            Color.rgb(
                226,
                125,
                96
            )

        val unselectedColor =
            Color.rgb(
                240,
                232,
                225
            )

        val selectedTextColor =
            Color.WHITE

        val unselectedTextColor =
            Color.rgb(
                100,
                94,
                102
            )

        chip.chipBackgroundColor =
            ColorStateList.valueOf(
                if (selected) {
                    selectedColor
                } else {
                    unselectedColor
                }
            )

        chip.setTextColor(
            if (selected) {
                selectedTextColor
            } else {
                unselectedTextColor
            }
        )

        chip.chipStrokeColor =
            ColorStateList.valueOf(
                if (selected) {
                    Color.rgb(
                        196,
                        100,
                        74
                    )
                } else {
                    Color.rgb(
                        225,
                        215,
                        205
                    )
                }
            )

        chip.chipStrokeWidth =
            dp(1).toFloat()

        chip.isCheckedIconVisible =
            selected
    }

    // =========================================================
    // SMART SIMILAR MEMORIES
    // =========================================================

    private fun createSimilarMemories(
        currentPreviewPath: String?
    ) {

        similarContainer.removeAllViews()

        val memories =
            getMemories()

        val similarMemories =
            ArrayList<JSONObject>()

        // Find related memories
        for (i in 0 until memories.length()) {

            val memory =
                memories.getJSONObject(i)

            val path =
                memory.optString(
                    "previewPath",
                    ""
                )

            if (
                !currentPreviewPath.isNullOrEmpty() &&
                path == currentPreviewPath
            ) {
                continue
            }

            val location =
                memory.optString(
                    "location",
                    ""
                )

            val date =
                memory.optLong(
                    "date",
                    0L
                )

            val sameLocation =
                currentLocation.isNotEmpty() &&
                        location.equals(
                            currentLocation,
                            ignoreCase = true
                        )

            val sameDate =
                isSameDay(
                    currentDate,
                    date
                )

            if (
                sameLocation ||
                sameDate
            ) {
                similarMemories.add(
                    memory
                )
            }
        }

        // Fill remaining spaces with other memories
        if (similarMemories.size < 3) {

            for (i in 0 until memories.length()) {

                if (
                    similarMemories.size >= 3
                ) {
                    break
                }

                val memory =
                    memories.getJSONObject(i)

                val path =
                    memory.optString(
                        "previewPath",
                        ""
                    )

                if (
                    !currentPreviewPath.isNullOrEmpty() &&
                    path == currentPreviewPath
                ) {
                    continue
                }

                var alreadyAdded = false

                for (
                existing in similarMemories
                ) {

                    if (
                        existing.optString(
                            "previewPath",
                            ""
                        ) == path
                    ) {
                        alreadyAdded = true
                        break
                    }
                }

                if (!alreadyAdded) {
                    similarMemories.add(
                        memory
                    )
                }
            }
        }

        // =====================================================
        // THREE PHOTO CARDS
        // =====================================================

        val row =
            LinearLayout(this)

        row.orientation =
            LinearLayout.HORIZONTAL

        row.gravity =
            Gravity.CENTER

        row.setPadding(
            dp(2),
            dp(5),
            dp(2),
            dp(10)
        )

        similarContainer.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val screenWidth =
            resources.displayMetrics.widthPixels

        val sidePadding =
            dp(32)

        val gap =
            dp(8)

        val cardWidth =
            (screenWidth -
                    sidePadding -
                    (gap * 2)) / 3

        // Always show 3 cards
        for (i in 0 until 3) {

            val card =
                LinearLayout(this)

            card.orientation =
                LinearLayout.VERTICAL

            card.setPadding(
                dp(5),
                dp(5),
                dp(5),
                dp(6)
            )

            card.background =
                createSimilarCardBackground()

            val params =
                LinearLayout.LayoutParams(
                    cardWidth,
                    dp(175)
                )

            if (i > 0) {
                params.leftMargin = gap
            }

            row.addView(
                card,
                params
            )

            // =================================================
            // PHOTO
            // =================================================

            val photo =
                ImageView(this)

            photo.scaleType =
                ImageView.ScaleType.CENTER_CROP

            photo.background =
                GradientDrawable().apply {

                    setColor(
                        Color.rgb(
                            244,
                            234,
                            213
                        )
                    )

                    cornerRadius =
                        dp(7).toFloat()
                }

            if (
                i < similarMemories.size
            ) {

                val memory =
                    similarMemories[i]

                val path =
                    memory.optString(
                        "previewPath",
                        ""
                    )

                if (
                    path.isNotEmpty() &&
                    File(path).exists()
                ) {

                    photo.setImageBitmap(
                        BitmapFactory.decodeFile(
                            path
                        )
                    )
                }

            } else {

                setEmptyPhotoBackground(
                    photo
                )
            }

            card.addView(
                photo,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(125)
                )
            )

            // =================================================
            // LOCATION
            // =================================================

            val label =
                TextView(this)

            if (
                i < similarMemories.size
            ) {

                val memory =
                    similarMemories[i]

                val location =
                    memory.optString(
                        "location",
                        ""
                    )

                label.text =
                    if (
                        location.isNotEmpty()
                    ) {
                        "📍 $location"
                    } else {
                        "✨ Memory"
                    }

            } else {

                label.text =
                    "✨ New memory"
            }

            label.textSize =
                9f

            label.setTextColor(
                Color.rgb(
                    196,
                    100,
                    74
                )
            )

            label.setTypeface(
                null,
                Typeface.BOLD
            )

            label.maxLines =
                1

            label.ellipsize =
                TextUtils.TruncateAt.END

            label.setPadding(
                dp(2),
                dp(5),
                dp(2),
                0
            )

            card.addView(
                label
            )

            // =================================================
            // CAPTION
            // =================================================

            if (
                i < similarMemories.size
            ) {

                val caption =
                    similarMemories[i]
                        .optString(
                            "caption",
                            ""
                        )

                if (
                    caption.isNotEmpty()
                ) {

                    val captionView =
                        TextView(this)

                    captionView.text =
                        caption

                    captionView.textSize =
                        8f

                    captionView.setTextColor(
                        Color.rgb(
                            107,
                            99,
                            107
                        )
                    )

                    captionView.maxLines =
                        2

                    captionView.ellipsize =
                        TextUtils.TruncateAt.END

                    captionView.setPadding(
                        dp(2),
                        dp(2),
                        dp(2),
                        0
                    )

                    card.addView(
                        captionView
                    )
                }

                card.setOnClickListener {

                    openMemory(
                        similarMemories[i]
                    )
                }
            }
        }
    }

    // =========================================================
    // EMPTY PHOTO BACKGROUND
    // =========================================================

    private fun setEmptyPhotoBackground(
        imageView: ImageView
    ) {

        imageView.setImageDrawable(null)

        imageView.background =
            GradientDrawable().apply {

                setColor(
                    Color.rgb(
                        244,
                        234,
                        213
                    )
                )

                setStroke(
                    dp(1),
                    Color.rgb(
                        220,
                        205,
                        190
                    )
                )

                cornerRadius =
                    dp(7).toFloat()
            }
    }

    // =========================================================
    // OPEN MEMORY
    // =========================================================

    private fun openMemory(
        memory: JSONObject
    ) {

        val intent =
            Intent(
                this,
                MemoryPreviewActivity::class.java
            )

        intent.putExtra(
            "previewPath",
            memory.optString(
                "previewPath",
                ""
            )
        )

        intent.putExtra(
            "location",
            memory.optString(
                "location",
                ""
            )
        )

        intent.putExtra(
            "caption",
            memory.optString(
                "caption",
                ""
            )
        )

        intent.putExtra(
            "date",
            memory.optLong(
                "date",
                System.currentTimeMillis()
            )
        )

        intent.putExtra(
            "elements",
            memory.optJSONArray(
                "elements"
            )?.toString() ?: "[]"
        )

        startActivity(intent)
    }

    // =========================================================
    // GET MEMORIES
    // =========================================================

    private fun getMemories(): JSONArray {

        val preferences =
            getSharedPreferences(
                "MemoryPin",
                MODE_PRIVATE
            )

        return try {

            JSONArray(
                preferences.getString(
                    "memories",
                    "[]"
                ) ?: "[]"
            )

        } catch (e: Exception) {

            JSONArray()
        }
    }

    // =========================================================
    // SAME DATE
    // =========================================================

    private fun isSameDay(
        first: Long,
        second: Long
    ): Boolean {

        if (
            first <= 0L ||
            second <= 0L
        ) {
            return false
        }

        val formatter =
            SimpleDateFormat(
                "yyyyMMdd",
                Locale.getDefault()
            )

        return formatter.format(
            Date(first)
        ) ==
                formatter.format(
                    Date(second)
                )
    }

    // =========================================================
    // DATE
    // =========================================================

    private fun formatDate(
        timestamp: Long
    ): String {

        return SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(
            Date(timestamp)
        )
    }

    // =========================================================
    // STICKER COUNT
    // =========================================================

    private fun countStickers(
        elementsString: String
    ): Int {

        return try {

            val elements =
                JSONArray(
                    elementsString
                )

            var count = 0

            for (
            i in 0 until elements.length()
            ) {

                val element =
                    elements.getJSONObject(i)

                if (
                    element.optString(
                        "type"
                    ) == "sticker"
                ) {
                    count++
                }
            }

            count

        } catch (e: Exception) {

            0
        }
    }

    // =========================================================
    // CARD BACKGROUND
    // =========================================================

    private fun createSimilarCardBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.rgb(
                    255,
                    253,
                    250
                )
            )

            setStroke(
                dp(1),
                Color.rgb(
                    225,
                    215,
                    205
                )
            )

            cornerRadius =
                dp(10).toFloat()
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