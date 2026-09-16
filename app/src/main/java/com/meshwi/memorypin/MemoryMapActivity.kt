package com.meshwi.memorypin

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MemoryMapActivity : AppCompatActivity() {

    private lateinit var photoContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        createScreen()
    }

    private fun createScreen() {

        // =====================================================
        // ROOT SCROLL
        // =====================================================

        val scrollView = ScrollView(this)

        scrollView.setBackgroundColor(
            Color.rgb(255, 253, 250)
        )

        val root = LinearLayout(this)

        root.orientation =
            LinearLayout.VERTICAL

        root.setPadding(
            dp(16),
            dp(18),
            dp(16),
            dp(30)
        )

        scrollView.addView(root)

        setContentView(scrollView)

        // =====================================================
        // HEADER
        // =====================================================

        val header =
            LinearLayout(this)

        header.orientation =
            LinearLayout.HORIZONTAL

        header.gravity =
            Gravity.CENTER_VERTICAL

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        val back =
            TextView(this)

        back.text = "←"

        back.textSize = 25f

        back.setTextColor(
            Color.rgb(
                80,
                72,
                80
            )
        )

        back.gravity =
            Gravity.CENTER

        back.setOnClickListener {
            finish()
        }

        header.addView(
            back,
            LinearLayout.LayoutParams(
                dp(42),
                dp(50)
            )
        )

        val titleContainer =
            LinearLayout(this)

        titleContainer.orientation =
            LinearLayout.VERTICAL

        header.addView(
            titleContainer,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val title =
            TextView(this)

        title.text =
            "Memory Map"

        title.textSize =
            25f

        title.setTextColor(
            Color.rgb(
                65,
                59,
                68
            )
        )

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        titleContainer.addView(
            title
        )

        val subtitle =
            TextView(this)

        subtitle.text =
            "Your journeys, pinned on a map."

        subtitle.textSize =
            12f

        subtitle.setTextColor(
            Color.rgb(
                125,
                112,
                125
            )
        )

        titleContainer.addView(
            subtitle
        )

        // =====================================================
        // MAP AREA
        // =====================================================

        val map =
            LinearLayout(this)

        map.orientation =
            LinearLayout.VERTICAL

        map.gravity =
            Gravity.CENTER

        map.background =
            GradientDrawable().apply {

                setColor(
                    Color.rgb(
                        234,
                        224,
                        207
                    )
                )

                cornerRadius =
                    dp(8).toFloat()
            }

        root.addView(
            map,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(290)
            ).apply {
                topMargin = dp(10)
            }
        )

        val mapTitle =
            TextView(this)

        mapTitle.text =
            "M E M O R Y   M A P"

        mapTitle.textSize =
            22f

        mapTitle.setTextColor(
            Color.rgb(
                190,
                181,
                170
            )
        )

        mapTitle.gravity =
            Gravity.CENTER

        map.addView(
            mapTitle
        )

        // =====================================================
        // LOCATION ROW
        // =====================================================

        val locations =
            LinearLayout(this)

        locations.orientation =
            LinearLayout.HORIZONTAL

        locations.gravity =
            Gravity.CENTER

        map.addView(
            locations,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(120)
            )
        )

        addMapLocation(
            locations,
            "📍",
            "Manali",
            true
        )

        addMapLocation(
            locations,
            "📍",
            "Shimla",
            false
        )

        addMapLocation(
            locations,
            "📍",
            "Kutch",
            false
        )

        // =====================================================
        // JOURNEYS TITLE
        // =====================================================

        val journeyTitle =
            TextView(this)

        journeyTitle.text =
            "YOUR JOURNEYS"

        journeyTitle.textSize =
            11f

        journeyTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        journeyTitle.setTextColor(
            Color.rgb(
                80,
                72,
                80
            )
        )

        journeyTitle.setPadding(
            dp(0),
            dp(20),
            dp(0),
            dp(8)
        )

        root.addView(
            journeyTitle
        )

        // =====================================================
        // MANALI
        // =====================================================

        val manaliCard =
            createLocationCard(
                "🏔️",
                "Manali",
                "15 memories  •  3 sublocations"
            )

        root.addView(
            manaliCard
        )

        // =====================================================
        // SOLANG VALLEY
        // =====================================================

        val solang =
            TextView(this)

        solang.text =
            "      📍  Solang Valley   ›"

        solang.textSize =
            13f

        solang.setTextColor(
            Color.rgb(
                90,
                80,
                90
            )
        )

        solang.setPadding(
            dp(10),
            dp(14),
            dp(10),
            dp(14)
        )

        solang.background =
            createLightCard()

        root.addView(
            solang,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
            ).apply {
                topMargin = dp(5)
            }
        )

        // =====================================================
        // PHOTO CONTAINER
        // =====================================================

        photoContainer =
            LinearLayout(this)

        photoContainer.orientation =
            LinearLayout.HORIZONTAL

        photoContainer.gravity =
            Gravity.CENTER

        photoContainer.visibility =
            View.GONE

        root.addView(
            photoContainer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(180)
            )
        )

        solang.setOnClickListener {

            if (
                photoContainer.visibility ==
                View.GONE
            ) {

                showSolangPhotos()

                photoContainer.visibility =
                    View.VISIBLE

            } else {

                photoContainer.visibility =
                    View.GONE
            }
        }

        // =====================================================
        // SHIMLA
        // =====================================================

        root.addView(
            createLocationCard(
                "🏔️",
                "Shimla",
                "8 memories  •  2 sublocations"
            )
        )

        // =====================================================
        // KUTCH
        // =====================================================

        root.addView(
            createLocationCard(
                "🌅",
                "Kutch",
                "5 memories  •  1 sublocation"
            )
        )

        // =====================================================
        // FOOTER
        // =====================================================

        val footer =
            TextView(this)

        footer.text =
            "Every pin holds a story. ❤️"

        footer.textSize =
            12f

        footer.setTextColor(
            Color.rgb(
                155,
                140,
                150
            )
        )

        footer.gravity =
            Gravity.CENTER

        footer.setPadding(
            dp(0),
            dp(28),
            dp(0),
            dp(10)
        )

        root.addView(
            footer
        )
    }

    // =========================================================
    // MAP LOCATION
    // =========================================================

    private fun addMapLocation(
        parent: LinearLayout,
        icon: String,
        name: String,
        selected: Boolean
    ) {

        val container =
            LinearLayout(this)

        container.orientation =
            LinearLayout.VERTICAL

        container.gravity =
            Gravity.CENTER

        val pin =
            TextView(this)

        pin.text =
            if (selected) {
                "📍"
            } else {
                "📌"
            }

        pin.textSize =
            28f

        pin.gravity =
            Gravity.CENTER

        if (selected) {

            pin.background =
                GradientDrawable().apply {

                    setColor(
                        Color.rgb(
                            226,
                            125,
                            96
                        )
                    )

                    shape =
                        GradientDrawable.OVAL
                }
        }

        container.addView(
            pin,
            LinearLayout.LayoutParams(
                dp(55),
                dp(55)
            )
        )

        val nameText =
            TextView(this)

        nameText.text =
            name

        nameText.textSize =
            10f

        nameText.setTextColor(
            Color.rgb(
                70,
                65,
                72
            )
        )

        nameText.gravity =
            Gravity.CENTER

        container.addView(
            nameText
        )

        parent.addView(
            container,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )
    }

    // =========================================================
    // LOCATION CARD
    // =========================================================

    private fun createLocationCard(
        icon: String,
        location: String,
        details: String
    ): LinearLayout {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.HORIZONTAL

        card.gravity =
            Gravity.CENTER_VERTICAL

        card.setPadding(
            dp(12),
            dp(8),
            dp(12),
            dp(8)
        )

        card.background =
            createLightCard()

        val iconView =
            TextView(this)

        iconView.text =
            icon

        iconView.textSize =
            22f

        iconView.gravity =
            Gravity.CENTER

        iconView.background =
            GradientDrawable().apply {

                setColor(
                    Color.rgb(
                        244,
                        234,
                        213
                    )
                )

                cornerRadius =
                    dp(5).toFloat()
            }

        card.addView(
            iconView,
            LinearLayout.LayoutParams(
                dp(48),
                dp(48)
            )
        )

        val textContainer =
            LinearLayout(this)

        textContainer.orientation =
            LinearLayout.VERTICAL

        textContainer.setPadding(
            dp(10),
            0,
            0,
            0
        )

        card.addView(
            textContainer,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val locationText =
            TextView(this)

        locationText.text =
            location

        locationText.textSize =
            15f

        locationText.setTextColor(
            Color.rgb(
                65,
                59,
                68
            )
        )

        locationText.setTypeface(
            null,
            Typeface.BOLD
        )

        textContainer.addView(
            locationText
        )

        val detailText =
            TextView(this)

        detailText.text =
            details

        detailText.textSize =
            10f

        detailText.setTextColor(
            Color.rgb(
                115,
                105,
                115
            )
        )

        textContainer.addView(
            detailText
        )

        val arrow =
            TextView(this)

        arrow.text =
            "→"

        arrow.textSize =
            18f

        arrow.setTextColor(
            Color.rgb(
                226,
                125,
                96
            )
        )

        card.addView(
            arrow
        )

        return card.apply {

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(65)
                )

            params.topMargin =
                dp(6)

            layoutParams =
                params
        }
    }

    // =========================================================
    // SOLANG PHOTOS
    // =========================================================

    private fun showSolangPhotos() {

        photoContainer.removeAllViews()

        val memories =
            getMemories()

        val manaliMemories =
            ArrayList<JSONObject>()

        for (
        i in 0 until memories.length()
        ) {

            val memory =
                memories.optJSONObject(i)
                    ?: continue

            val location =
                memory.optString(
                    "location",
                    ""
                )

            if (
                location.equals(
                    "manali",
                    ignoreCase = true
                )
            ) {

                manaliMemories.add(
                    memory
                )
            }
        }

        for (i in 0 until 3) {

            val memory =
                if (
                    i < manaliMemories.size
                ) {
                    manaliMemories[i]
                } else {
                    null
                }

            photoContainer.addView(
                createPhotoCard(
                    memory
                ),
                LinearLayout.LayoutParams(
                    0,
                    dp(170),
                    1f
                ).apply {

                    setMargins(
                        dp(3),
                        0,
                        dp(3),
                        0
                    )
                }
            )
        }
    }

    // =========================================================
    // PHOTO CARD
    // =========================================================

    private fun createPhotoCard(
        memory: JSONObject?
    ): LinearLayout {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            dp(4),
            dp(4),
            dp(4),
            dp(5)
        )

        card.background =
            createLightCard()

        val image =
            ImageView(this)

        image.scaleType =
            ImageView.ScaleType.CENTER_CROP

        image.background =
            GradientDrawable().apply {

                setColor(
                    Color.rgb(
                        244,
                        234,
                        213
                    )
                )

                cornerRadius =
                    dp(6).toFloat()
            }

        if (memory != null) {

            val path =
                memory.optString(
                    "previewPath",
                    ""
                )

            if (
                path.isNotEmpty() &&
                File(path).exists()
            ) {

                image.setImageBitmap(
                    BitmapFactory.decodeFile(path)
                )
            }
        }

        card.addView(
            image,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(115)
            )
        )

        val place =
            TextView(this)

        place.text =
            "📍 Solang Valley"

        place.textSize =
            8f

        place.setTextColor(
            Color.rgb(
                196,
                100,
                74
            )
        )

        place.setTypeface(
            null,
            Typeface.BOLD
        )

        card.addView(
            place
        )

        val caption =
            TextView(this)

        caption.text =
            if (memory != null) {
                memory.optString(
                    "caption",
                    "Beautiful moment"
                )
            } else {
                "Memory photo"
            }

        caption.textSize =
            7f

        caption.maxLines =
            2

        caption.setTextColor(
            Color.rgb(
                100,
                92,
                100
            )
        )

        card.addView(
            caption
        )

        return card
    }

    // =========================================================
    // LIGHT CARD
    // =========================================================

    private fun createLightCard():
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
                    230,
                    222,
                    232
                )
            )

            cornerRadius =
                dp(10).toFloat()
        }
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