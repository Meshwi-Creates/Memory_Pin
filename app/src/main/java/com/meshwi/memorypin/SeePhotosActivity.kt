package com.meshwi.memorypin

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class SeePhotosActivity : AppCompatActivity() {

    private lateinit var allContent: LinearLayout
    private lateinit var placesContent: LinearLayout
    private lateinit var galleryGrid: GridLayout
    private lateinit var searchBox: EditText

    private var memories = JSONArray()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContentView(
            R.layout.activity_see_photos
        )

        // =====================================================
        // FIND VIEWS
        // =====================================================

        val backButton =
            findViewById<View>(
                R.id.btnBack
            )

        val allTab =
            findViewById<LinearLayout>(
                R.id.tabAll
            )

        val placesTab =
            findViewById<LinearLayout>(
                R.id.tabPlaces
            )

        allContent =
            findViewById(
                R.id.allContent
            )

        placesContent =
            findViewById(
                R.id.placesContent
            )

        galleryGrid =
            findViewById(
                R.id.dynamicGalleryGrid
            )

        searchBox =
            findViewById(
                R.id.etSearch
            )

        // =====================================================
        // BACK BUTTON
        // =====================================================

        backButton.setOnClickListener {
            finish()
        }

        // =====================================================
        // ALL MEMORIES TAB
        // =====================================================

        allTab.setOnClickListener {

            allContent.visibility =
                View.VISIBLE

            placesContent.visibility =
                View.GONE

            allTab.background =
                GradientDrawable().apply {

                    setColor(
                        Color.rgb(
                            226,
                            125,
                            96
                        )
                    )

                    cornerRadius =
                        dp(8).toFloat()
                }

            placesTab.background =
                null
        }

        // =====================================================
        // PLACES & PEOPLE TAB
        // =====================================================

        placesTab.setOnClickListener {

            allContent.visibility =
                View.GONE

            placesContent.visibility =
                View.VISIBLE

            placesTab.background =
                GradientDrawable().apply {

                    setColor(
                        Color.rgb(
                            226,
                            125,
                            96
                        )
                    )

                    cornerRadius =
                        dp(8).toFloat()
                }

            allTab.background =
                null
        }

        // =====================================================
        // LOAD MEMORIES
        // =====================================================

        loadMemories()

        // =====================================================
        // SEARCH
        // =====================================================

        searchBox.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    showMemories(
                        s?.toString()?.trim()
                            ?: ""
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    // =========================================================
    // LOAD SAVED MEMORIES
    // =========================================================

    private fun loadMemories() {

        val preferences =
            getSharedPreferences(
                "MemoryPin",
                MODE_PRIVATE
            )

        memories =
            try {

                JSONArray(
                    preferences.getString(
                        "memories",
                        "[]"
                    ) ?: "[]"
                )

            } catch (e: Exception) {

                JSONArray()
            }

        showMemories("")
    }

    // =========================================================
    // SHOW MEMORIES
    // =========================================================

    private fun showMemories(
        searchText: String
    ) {

        galleryGrid.removeAllViews()

        val search =
            searchText.lowercase()

        val filtered =
            ArrayList<JSONObject>()

        for (
        i in 0 until memories.length()
        ) {

            val memory =
                memories.getJSONObject(i)

            val location =
                memory.optString(
                    "location",
                    ""
                )

            val caption =
                memory.optString(
                    "caption",
                    ""
                )

            if (
                search.isEmpty() ||
                location.lowercase()
                    .contains(search) ||
                caption.lowercase()
                    .contains(search)
            ) {

                filtered.add(
                    memory
                )
            }
        }

        // =====================================================
        // NO RESULTS
        // =====================================================

        if (filtered.isEmpty()) {

            val empty =
                TextView(this)

            empty.text =
                if (search.isEmpty()) {
                    "📸 No memories yet"
                } else {
                    "🔍 No memories found"
                }

            empty.textSize =
                14f

            empty.gravity =
                Gravity.CENTER

            empty.setTextColor(
                Color.rgb(
                    107,
                    99,
                    107
                )
            )

            empty.setPadding(
                dp(20),
                dp(45),
                dp(20),
                dp(45)
            )

            val params =
                GridLayout.LayoutParams().apply {

                    width =
                        GridLayout.LayoutParams.MATCH_PARENT

                    height =
                        GridLayout.LayoutParams.WRAP_CONTENT

                    columnSpec =
                        GridLayout.spec(
                            0,
                            3
                        )
                }

            galleryGrid.addView(
                empty,
                params
            )

            return
        }

        // =====================================================
        // ADD MEMORY CARDS
        // 3 CARDS PER ROW
        // =====================================================

        for (
        memory in filtered
        ) {

            val card =
                createMemoryCard(
                    memory
                )

            val params =
                GridLayout.LayoutParams().apply {

                    width = 0

                    height =
                        dp(180)

                    columnSpec =
                        GridLayout.spec(
                            GridLayout.UNDEFINED,
                            1f
                        )

                    setMargins(
                        dp(4),
                        dp(5),
                        dp(4),
                        dp(5)
                    )
                }

            galleryGrid.addView(
                card,
                params
            )
        }
    }

    // =========================================================
    // CREATE MEMORY CARD
    // =========================================================

    private fun createMemoryCard(
        memory: JSONObject
    ): LinearLayout {

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
            createCardBackground()

        // =====================================================
        // PHOTO
        // =====================================================

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
                    dp(7).toFloat()
            }

        val previewPath =
            memory.optString(
                "previewPath",
                ""
            )

        if (
            previewPath.isNotEmpty() &&
            File(previewPath).exists()
        ) {

            image.setImageBitmap(
                BitmapFactory.decodeFile(
                    previewPath
                )
            )
        }

        card.addView(
            image,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(120)
            )
        )

        // =====================================================
        // LOCATION
        // =====================================================

        val location =
            TextView(this)

        val locationName =
            memory.optString(
                "location",
                ""
            )

        location.text =
            if (
                locationName.isNotEmpty()
            ) {
                "📍 $locationName"
            } else {
                "📍 Unknown"
            }

        location.textSize =
            9f

        location.setTextColor(
            Color.rgb(
                196,
                100,
                74
            )
        )

        location.setTypeface(
            null,
            Typeface.BOLD
        )

        location.maxLines =
            1

        location.ellipsize =
            TextUtils.TruncateAt.END

        location.setPadding(
            dp(2),
            dp(5),
            dp(2),
            0
        )

        card.addView(
            location
        )

        // =====================================================
        // CAPTION
        // =====================================================

        val caption =
            memory.optString(
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
                    100,
                    92,
                    100
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

        // =====================================================
        // CLICK → MEMORY PREVIEW
        // =====================================================

        card.setOnClickListener {

            openMemory(
                memory
            )
        }

        return card
    }

    // =========================================================
    // OPEN MEMORY PREVIEW
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
            )?.toString()
                ?: "[]"
        )

        startActivity(
            intent
        )
    }

    // =========================================================
    // CARD BACKGROUND
    // =========================================================

    private fun createCardBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.rgb(
                    255,
                    253,
                    250
                )
            )

            cornerRadius =
                dp(10).toFloat()

            setStroke(
                dp(1),
                Color.rgb(
                    230,
                    222,
                    232
                )
            )
        }
    }

    // =========================================================
    // DP TO PX
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