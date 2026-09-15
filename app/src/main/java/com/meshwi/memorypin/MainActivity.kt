package com.meshwi.memorypin

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var memoryContainer: GridLayout
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        memoryContainer =
            findViewById(R.id.memoryContainer)

        tvEmpty =
            findViewById(R.id.tvEmpty)

        val btnCreateMemory =
            findViewById<Button>(R.id.btnCreateMemory)

        btnCreateMemory.setOnClickListener {

            val intent =
                Intent(
                    this,
                    CreateMemoryActivity::class.java
                )

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        loadMemories()
    }

    private fun loadMemories() {

        memoryContainer.removeAllViews()

        val preferences =
            getSharedPreferences(
                "MemoryPin",
                MODE_PRIVATE
            )

        val jsonString =
            preferences.getString(
                "memories",
                "[]"
            )

        val memories =
            JSONArray(jsonString)

        if (memories.length() == 0) {

            tvEmpty.visibility =
                View.VISIBLE

            return
        }

        tvEmpty.visibility =
            View.GONE

        for (i in 0 until memories.length()) {

            val memory =
                memories.getJSONObject(i)

            addMemoryCard(memory)
        }
    }

    private fun addMemoryCard(
        memory: JSONObject
    ) {

        // -----------------------------------------
        // CARD
        // -----------------------------------------

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            dpToPx(3),
            dpToPx(3),
            dpToPx(3),
            dpToPx(3)
        )

        card.background =
            createCardBackground()

        val cardParams =
            GridLayout.LayoutParams().apply {

                width = 0

                height =
                    GridLayout.LayoutParams.WRAP_CONTENT

                columnSpec =
                    GridLayout.spec(
                        GridLayout.UNDEFINED,
                        1f
                    )

                setMargins(
                    dpToPx(3),
                    dpToPx(4),
                    dpToPx(3),
                    dpToPx(8)
                )
            }

        card.layoutParams =
            cardParams

        // -----------------------------------------
        // EXACT SCRAPBOOK PREVIEW
        // -----------------------------------------

        val previewPath =
            memory.optString(
                "previewPath",
                ""
            )

        if (
            previewPath.isNotEmpty() &&
            File(previewPath).exists()
        ) {

            val preview =
                ImageView(this)

            val bitmap =
                BitmapFactory.decodeFile(
                    previewPath
                )

            preview.setImageBitmap(bitmap)

            /*
             * Do NOT give the ImageView a fixed height.
             *
             * The ImageView calculates its height from
             * the original scrapbook image ratio.
             */

            preview.scaleType =
                ImageView.ScaleType.FIT_CENTER

            preview.adjustViewBounds =
                true

            preview.setBackgroundColor(
                Color.WHITE
            )

            val imageParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            preview.layoutParams =
                imageParams

            card.addView(preview)

        } else {

            addOldMemoryPreview(
                card,
                memory
            )
        }

        memoryContainer.addView(card)
    }

    // ---------------------------------------------
    // OLD MEMORY FALLBACK
    // ---------------------------------------------

    private fun addOldMemoryPreview(
        card: LinearLayout,
        memory: JSONObject
    ) {

        val elements =
            memory.optJSONArray("elements")

        var firstPhotoPath = ""

        if (elements != null) {

            for (i in 0 until elements.length()) {

                val element =
                    elements.getJSONObject(i)

                if (
                    element.optString("type")
                    == "photo"
                ) {

                    firstPhotoPath =
                        element.optString(
                            "photoPath",
                            ""
                        )

                    break
                }
            }
        }

        if (
            firstPhotoPath.isNotEmpty() &&
            File(firstPhotoPath).exists()
        ) {

            val image =
                ImageView(this)

            image.setImageBitmap(
                BitmapFactory.decodeFile(
                    firstPhotoPath
                )
            )

            image.scaleType =
                ImageView.ScaleType.CENTER_CROP

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(100)
                )

            image.layoutParams =
                params

            card.addView(image)
        }

        val location =
            memory.optString(
                "location",
                ""
            )

        if (location.isNotEmpty()) {

            val locationText =
                TextView(this)

            locationText.text =
                "📍 $location"

            locationText.textSize =
                11f

            locationText.setTextColor(
                Color.DKGRAY
            )

            card.addView(
                locationText
            )
        }

        val caption =
            memory.optString(
                "caption",
                ""
            )

        if (caption.isNotEmpty()) {

            val captionText =
                TextView(this)

            captionText.text =
                caption

            captionText.textSize =
                10f

            captionText.setTextColor(
                Color.DKGRAY
            )

            card.addView(
                captionText
            )
        }
    }

    // ---------------------------------------------
    // CARD BACKGROUND
    // ---------------------------------------------

    private fun createCardBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.rgb(
                    255,
                    253,
                    252
                )
            )

            cornerRadius =
                dpToPx(12).toFloat()

            setStroke(
                dpToPx(1),
                Color.rgb(
                    230,
                    222,
                    232
                )
            )
        }
    }

    // ---------------------------------------------
    // DP TO PX
    // ---------------------------------------------

    private fun dpToPx(dp: Int): Int {

        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }
}