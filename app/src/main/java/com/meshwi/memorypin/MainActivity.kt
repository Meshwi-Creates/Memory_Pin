package com.meshwi.memorypin

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.homeRecyclerView)

        val layoutManager =
            GridLayoutManager(this, 3)

        layoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {

                override fun getSpanSize(position: Int): Int {

                    return if (position == 0) {
                        3
                    } else {
                        1
                    }
                }
            }

        recyclerView.layoutManager = layoutManager

        recyclerView.isNestedScrollingEnabled = true

        recyclerView.adapter = HomeAdapter()
    }

    override fun onResume() {
        super.onResume()

        recyclerView.adapter = HomeAdapter()
    }

    private inner class HomeAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val memories: JSONArray

        init {
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
        }

        override fun getItemCount(): Int {

            return memories.length() + 1
        }

        override fun getItemViewType(
            position: Int
        ): Int {

            return if (position == 0) {
                0
            } else {
                1
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {

            if (viewType == 0) {

                return HeaderHolder(
                    createHeader()
                )
            }

            return MemoryHolder(
                createMemoryCard()
            )
        }

        override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {

            if (
                holder is MemoryHolder &&
                position > 0
            ) {

                val memory =
                    memories.getJSONObject(
                        position - 1
                    )

                holder.bind(memory)
            }
        }
    }

    private inner class HeaderHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)

    private inner class MemoryHolder(
        private val card: LinearLayout
    ) : RecyclerView.ViewHolder(card) {

        fun bind(
            memory: JSONObject
        ) {

            card.removeAllViews()

            val image =
                ImageView(this@MainActivity)

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
                        dp(8).toFloat()
                }

            val imageParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(100)
                )

            card.addView(
                image,
                imageParams
            )

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

            val location =
                TextView(this@MainActivity)

            location.text =
                "📍 " +
                        memory.optString(
                            "location",
                            "Unknown"
                        )

            location.textSize = 9f

            location.setTextColor(
                Color.rgb(
                    80,
                    70,
                    75
                )
            )

            location.maxLines = 1

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

            val caption =
                memory.optString(
                    "caption",
                    ""
                )

            if (caption.isNotEmpty()) {

                val captionView =
                    TextView(this@MainActivity)

                captionView.text =
                    caption

                captionView.textSize = 9f

                captionView.setTextColor(
                    Color.rgb(
                        100,
                        92,
                        100
                    )
                )

                captionView.maxLines = 2

                captionView.ellipsize =
                    TextUtils.TruncateAt.END

                captionView.setPadding(
                    dp(2),
                    dp(2),
                    dp(2),
                    dp(2)
                )

                card.addView(
                    captionView
                )
            }

            card.setOnClickListener {

                openMemory(memory)
            }
        }
    }

    private fun createHeader(): LinearLayout {

        val root =
            LinearLayout(this)

        root.orientation =
            LinearLayout.VERTICAL

        root.setBackgroundColor(
            Color.rgb(
                253,
                251,
                247
            )
        )

        root.setPadding(
            dp(16),
            dp(18),
            dp(16),
            dp(15)
        )

        // ================= HEADER =================

        val header =
            LinearLayout(this)

        header.orientation =
            LinearLayout.HORIZONTAL

        header.gravity =
            Gravity.CENTER_VERTICAL

        root.addView(
            header,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val titleBox =
            LinearLayout(this)

        titleBox.orientation =
            LinearLayout.VERTICAL

        header.addView(
            titleBox,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val title =
            TextView(this)

        title.text =
            "MEMORY PIN"

        title.textSize = 27f

        title.setTextColor(
            Color.rgb(
                64,
                59,
                67
            )
        )

        title.typeface =
            Typeface.create(
                "serif",
                Typeface.BOLD
            )

        titleBox.addView(title)

        val tagline =
            TextView(this)

        tagline.text =
            "Your memories, beautifully kept."

        tagline.textSize = 13f

        tagline.setTextColor(
            Color.rgb(
                107,
                99,
                107
            )
        )

        tagline.typeface =
            Typeface.create(
                "serif",
                Typeface.ITALIC
            )

        titleBox.addView(
            tagline
        )

        val pin =
            TextView(this)

        pin.text = "📌"

        pin.textSize = 25f

        pin.gravity =
            Gravity.CENTER

        header.addView(
            pin,
            LinearLayout.LayoutParams(
                dp(48),
                dp(48)
            )
        )

        // ================= LATEST =================

        addSectionTitle(
            root,
            "LATEST MEMORY"
        )

        val latestCard =
            LinearLayout(this)

        latestCard.orientation =
            LinearLayout.VERTICAL

        latestCard.setPadding(
            dp(7),
            dp(7),
            dp(7),
            dp(7)
        )

        latestCard.background =
            cardBackground()

        root.addView(
            latestCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(9)
            }
        )

        val latestImage =
            ImageView(this)

        latestImage.scaleType =
            ImageView.ScaleType.FIT_CENTER

        latestImage.setBackgroundColor(
            Color.rgb(
                244,
                234,
                213
            )
        )

        latestCard.addView(
            latestImage,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(210)
            )
        )

        val latestLocation =
            TextView(this)

        latestLocation.text =
            "📍 Your latest journey"

        latestLocation.textSize = 11f

        latestLocation.setTextColor(
            Color.rgb(
                64,
                59,
                67
            )
        )

        latestLocation.setPadding(
            dp(10),
            dp(7),
            dp(10),
            dp(2)
        )

        latestCard.addView(
            latestLocation
        )

        val latestCaption =
            TextView(this)

        latestCaption.text =
            "Your favourite moment, saved forever."

        latestCaption.textSize = 13f

        latestCaption.setTextColor(
            Color.rgb(
                64,
                59,
                67
            )
        )

        latestCaption.typeface =
            Typeface.create(
                "serif",
                Typeface.NORMAL
            )

        latestCaption.setPadding(
            dp(10),
            dp(3),
            dp(10),
            dp(5)
        )

        latestCard.addView(
            latestCaption
        )

        val viewMemory =
            TextView(this)

        viewMemory.text =
            "View Memory →"

        viewMemory.textSize = 11f

        viewMemory.gravity =
            Gravity.END

        viewMemory.setTextColor(
            Color.rgb(
                196,
                100,
                74
            )
        )

        viewMemory.setTypeface(
            null,
            Typeface.BOLD
        )

        viewMemory.setPadding(
            dp(10),
            dp(3),
            dp(10),
            dp(8)
        )

        latestCard.addView(
            viewMemory
        )

        if (memoriesExist()) {

            val memory =
                getMemory(0)

            val path =
                memory.optString(
                    "previewPath",
                    ""
                )

            if (
                path.isNotEmpty() &&
                File(path).exists()
            ) {

                latestImage.setImageBitmap(
                    BitmapFactory.decodeFile(
                        path
                    )
                )
            }

            val place =
                memory.optString(
                    "location",
                    ""
                )

            if (place.isNotEmpty()) {

                latestLocation.text =
                    "📍 $place"
            }

            val caption =
                memory.optString(
                    "caption",
                    ""
                )

            if (caption.isNotEmpty()) {

                latestCaption.text =
                    caption
            }

            val clickListener =
                View.OnClickListener {

                    openMemory(memory)
                }

            latestCard.setOnClickListener(
                clickListener
            )

            viewMemory.setOnClickListener(
                clickListener
            )
        }

        // ================= EXPLORE =================

        addSectionTitle(
            root,
            "EXPLORE"
        )

        val exploreRow =
            LinearLayout(this)

        exploreRow.orientation =
            LinearLayout.HORIZONTAL

        root.addView(
            exploreRow,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(96)
            ).apply {
                topMargin = dp(9)
            }
        )

        val photos =
            createExploreButton(
                "📸",
                "See Photos",
                "Visual exploration"
            )

        val map =
            createExploreButton(
                "🗺️",
                "Memory Map",
                "Geographic visualisation"
            )

        exploreRow.addView(
            photos,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                rightMargin = dp(4)
            }
        )

        exploreRow.addView(
            map,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                leftMargin = dp(4)
            }
        )

        photos.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SeePhotosActivity::class.java
                )
            )
        }

        map.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MemoryMapActivity::class.java
                )
            )
        }

        // ================= GENERATE =================

        val generate =
            LinearLayout(this)

        generate.orientation =
            LinearLayout.HORIZONTAL

        generate.gravity =
            Gravity.CENTER_VERTICAL

        generate.setPadding(
            dp(15),
            0,
            dp(10),
            0
        )

        generate.background =
            GradientDrawable().apply {

                setColor(
                    Color.rgb(
                        226,
                        125,
                        96
                    )
                )

                cornerRadius =
                    dp(12).toFloat()
            }

        root.addView(
            generate,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
            ).apply {
                topMargin = dp(9)
            }
        )

        val sparkle =
            TextView(this)

        sparkle.text = "✨"

        sparkle.textSize = 24f

        sparkle.gravity =
            Gravity.CENTER

        generate.addView(
            sparkle,
            LinearLayout.LayoutParams(
                dp(42),
                dp(42)
            )
        )

        val generateText =
            LinearLayout(this)

        generateText.orientation =
            LinearLayout.VERTICAL

        generate.addView(
            generateText,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val generateTitle =
            TextView(this)

        generateTitle.text =
            "Generate a New Memory"

        generateTitle.textSize = 14f

        generateTitle.setTextColor(
            Color.WHITE
        )

        generateTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        generateText.addView(
            generateTitle
        )

        val generateSub =
            TextView(this)

        generateSub.text =
            "Turn your photos into a story"

        generateSub.textSize = 10f

        generateSub.setTextColor(
            Color.rgb(
                255,
                243,
                238
            )
        )

        generateText.addView(
            generateSub
        )

        val arrow =
            TextView(this)

        arrow.text = "→"

        arrow.textSize = 19f

        arrow.setTextColor(
            Color.WHITE
        )

        arrow.gravity =
            Gravity.CENTER

        generate.addView(
            arrow,
            LinearLayout.LayoutParams(
                dp(35),
                dp(42)
            )
        )

        generate.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CreateMemoryActivity::class.java
                )
            )
        }

        // ================= YOUR MEMORIES =================

        val memoryHeader =
            LinearLayout(this)

        memoryHeader.orientation =
            LinearLayout.HORIZONTAL

        memoryHeader.gravity =
            Gravity.CENTER_VERTICAL

        root.addView(
            memoryHeader,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(25)
            }
        )

        val memoryTitle =
            TextView(this)

        memoryTitle.text =
            "YOUR MEMORIES"

        memoryTitle.textSize = 11f

        memoryTitle.setTextColor(
            Color.rgb(
                64,
                59,
                67
            )
        )

        memoryTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        memoryHeader.addView(
            memoryTitle,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val memoryCount =
            TextView(this)

        memoryCount.text =
            "${getMemoryCount()} memories"

        memoryCount.textSize = 10f

        memoryCount.setTextColor(
            Color.rgb(
                154,
                144,
                152
            )
        )

        memoryHeader.addView(
            memoryCount
        )

        // ================= EMPTY =================

        if (!memoriesExist()) {

            val empty =
                TextView(this)

            empty.text =
                "📍 No memories yet — start your first one!"

            empty.textSize = 13f

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
                dp(30),
                dp(20),
                dp(30)
            )

            empty.background =
                cardBackground()

            root.addView(
                empty,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(10)
                }
            )
        }

        // ================= FOOTER =================

        val footer =
            TextView(this)

        footer.text =
            "Pin your moments. Preserve your stories. ❤️"

        footer.textSize = 10f

        footer.gravity =
            Gravity.CENTER

        footer.setTextColor(
            Color.rgb(
                154,
                144,
                152
            )
        )

        footer.typeface =
            Typeface.create(
                "serif",
                Typeface.ITALIC
            )

        footer.setPadding(
            0,
            dp(25),
            0,
            dp(10)
        )

        root.addView(
            footer
        )

        return root
    }

    private fun createMemoryCard():
            LinearLayout {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            dp(4),
            dp(4),
            dp(4),
            dp(6)
        )

        card.background =
            cardBackground()

        val params =
            GridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(
            dp(3),
            dp(4),
            dp(3),
            dp(4)
        )

        card.layoutParams =
            params

        return card
    }

    private fun createExploreButton(
        emoji: String,
        title: String,
        subtitle: String
    ): LinearLayout {

        val box =
            LinearLayout(this)

        box.orientation =
            LinearLayout.VERTICAL

        box.gravity =
            Gravity.CENTER

        box.background =
            cardBackground()

        val icon =
            TextView(this)

        icon.text = emoji

        icon.textSize = 26f

        icon.gravity =
            Gravity.CENTER

        box.addView(icon)

        val titleView =
            TextView(this)

        titleView.text =
            title

        titleView.textSize = 12f

        titleView.setTextColor(
            Color.rgb(
                64,
                59,
                67
            )
        )

        titleView.setTypeface(
            null,
            Typeface.BOLD
        )

        box.addView(
            titleView
        )

        val subtitleView =
            TextView(this)

        subtitleView.text =
            subtitle

        subtitleView.textSize = 9f

        subtitleView.setTextColor(
            Color.rgb(
                107,
                99,
                107
            )
        )

        box.addView(
            subtitleView
        )

        return box
    }

    private fun addSectionTitle(
        root: LinearLayout,
        text: String
    ) {

        val title =
            TextView(this)

        title.text = text

        title.textSize = 11f

        title.setTextColor(
            Color.rgb(
                64,
                59,
                67
            )
        )

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.letterSpacing =
            0.12f

        root.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(24)
            }
        )
    }

    private fun cardBackground():
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
                dp(12).toFloat()

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

    private fun memoriesExist(): Boolean {

        return getMemoryCount() > 0
    }

    private fun getMemoryCount(): Int {

        val preferences =
            getSharedPreferences(
                "MemoryPin",
                MODE_PRIVATE
            )

        val json =
            preferences.getString(
                "memories",
                "[]"
            ) ?: "[]"

        return try {
            JSONArray(json).length()
        } catch (e: Exception) {
            0
        }
    }

    private fun getMemory(
        index: Int
    ): JSONObject {

        val preferences =
            getSharedPreferences(
                "MemoryPin",
                MODE_PRIVATE
            )

        val json =
            preferences.getString(
                "memories",
                "[]"
            ) ?: "[]"

        return JSONArray(json)
            .getJSONObject(index)
    }

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