package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val caption: String,
    val videoDurationSec: Int, // in seconds
    val sizeMb: Double, // File size in MB
    val convertedCoins: Double, // Gold coins converted from upload data (e.g., 1 MB = 1 Coin)
    val uploaderId: String,
    val uploaderName: String,
    val uploaderHasYellowTick: Boolean = false,
    val viewsCount: Int = 0,
    val likesCount: Int = 0,
    val totalViewsCoins: Double = 0.0, // Coins generated from views (views * 2.0 coins)
    val userViewsEarnings: Double = 0.0, // Creator's share (50% of totalViewsCoins)
    val ownerViewsEarnings: Double = 0.0, // Owner's share (50% of totalViewsCoins)
    val timestamp: Long = System.currentTimeMillis(),
    val mediaType: String = "video", // "video" or "picture"
    val isMatureContent: Boolean = false // True if uploaded under the telegram-like mature section
)

