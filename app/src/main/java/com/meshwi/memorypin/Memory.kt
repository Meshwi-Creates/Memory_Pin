package com.meshwi.memorypin

data class Memory(
    val location: String,
    val caption: String,
    val photos: ArrayList<String>,
    val stickers: ArrayList<Int>
)