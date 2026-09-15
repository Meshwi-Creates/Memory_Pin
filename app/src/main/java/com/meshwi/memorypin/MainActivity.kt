package com.meshwi.memorypin

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var memoryContainer: LinearLayout
    private lateinit var tvEmpty: TextView

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_main
        )

        memoryContainer =
            findViewById(
                R.id.memoryContainer
            )

        tvEmpty =
            findViewById(
                R.id.tvEmpty
            )

        val btnCreateMemory =
            findViewById<Button>(
                R.id.btnCreateMemory
            )

        btnCreateMemory.setOnClickListener {

            val intent =
                android.content.Intent(
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
                TextView.VISIBLE

            return
        }

        tvEmpty.visibility =
            TextView.GONE

        // Newest memory first
        for (
        i in 0 until memories.length()
        ) {

            val memory =
                memories.getJSONObject(i)

            addMemoryCard(memory)
        }
    }

    private fun addMemoryCard(
        memory: JSONObject
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            dpToPx(10),
            dpToPx(10),
            dpToPx(10),
            dpToPx(10)
        )

        card.background =
            createCardBackground()

        val cardParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        cardParams.setMargins(
            0,
            dpToPx(8),
            0,
            dpToPx(16)
        )

        card.layoutParams =
            cardParams

        // ---------------------------------------------
        // EXACT SCRAPBOOK PREVIEW
        // ---------------------------------------------

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

            // Keep exact scrapbook proportions
            preview.scaleType =
                ImageView.ScaleType.FIT_CENTER

            preview.adjustViewBounds =
                true

            preview.setBackgroundColor(
                Color.WHITE
            )

            val imageParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            preview.layoutParams =
                imageParams

            card.addView(preview)

        } else {

            // -----------------------------------------
            // OLD MEMORY FALLBACK
            // -----------------------------------------

            addOldMemoryPreview(
                card,
                memory
            )
        }

        memoryContainer.addView(card)
    }

    private fun addOldMemoryPreview(
        card: LinearLayout,
        memory: JSONObject
    ) {

        val elements =
            memory.optJSONArray(
                "elements"
            )

        var firstPhotoPath =
            ""

        if (elements != null) {

            for (
            i in 0 until elements.length()
            ) {

                val element =
                    elements.getJSONObject(i)

                if (
                    element.optString(
                        "type"
                    ) == "photo"
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
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(180)
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
                16f

            locationText.setTextColor(
                Color.DKGRAY
            )

            locationText.setPadding(
                0,
                dpToPx(5),
                0,
                dpToPx(3)
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
                14f

            captionText.setTextColor(
                Color.DKGRAY
            )

            card.addView(
                captionText
            )
        }
    }

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
                dpToPx(18).toFloat()

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

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }
}