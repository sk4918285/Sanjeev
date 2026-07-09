package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // Phone number or email used during login
    val username: String,
    val phoneOrEmail: String,
    val followersCount: Int = 0,
    val coinsBalance: Double = 0.0, // User's share (50% of views-generated + 100% of upload-generated)
    val ownerCoinsBalance: Double = 0.0, // Owner's share (50% of views-generated - hidden from user)
    val isMonetized: Boolean = false, // True if followersCount >= 500
    val hasYellowTick: Boolean = false,
    val yellowTickExpiry: Long = 0L, // Timestamp when yellow tick subscription expires
    val isLoggedIn: Boolean = false,
    val selectedPlanType: String = "", // "1_month", "6_months", "1_year"
    val ageCategory: String = "Adult (18+)", // "Adult (18+)" or "Minor (Under 18)"
    val isBiometricVerified: Boolean = false,
    val verificationMethod: String = "", // "Face Scan" or "Aadhar Card"
    val aadharNumber: String = ""
)

