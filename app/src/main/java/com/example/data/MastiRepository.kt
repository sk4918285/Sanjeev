package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class MastiRepository(private val mastiDao: MastiDao) {
    val loggedInUser: Flow<UserEntity?> = mastiDao.getLoggedInUserFlow()
    val allVideos: Flow<List<VideoEntity>> = mastiDao.getAllVideosFlow()
    val allUsers: Flow<List<UserEntity>> = mastiDao.getAllUsersFlow()

    fun getVideosByUploader(uploaderId: String): Flow<List<VideoEntity>> = 
        mastiDao.getVideosByUploaderFlow(uploaderId)

    suspend fun getLoggedInUserDirect(): UserEntity? = mastiDao.getLoggedInUser()

    suspend fun getUserById(userId: String): UserEntity? = mastiDao.getUserById(userId)

    suspend fun loginWithOtp(phoneOrEmail: String, username: String): UserEntity {
        mastiDao.logoutAllUsers()
        val existing = mastiDao.getUserByPhoneOrEmail(phoneOrEmail)
        if (existing != null) {
            val updated = existing.copy(isLoggedIn = true)
            mastiDao.insertUser(updated)
            return updated
        } else {
            // Starts user at 430 followers to make the 500 follower milestone fun, immediate, and easy to reach!
            val newUser = UserEntity(
                id = phoneOrEmail,
                username = username.ifEmpty { "creator_" + Random.nextInt(1000, 9999) },
                phoneOrEmail = phoneOrEmail,
                followersCount = 430, // 430 followers initially so they can easily cross 500 to unlock monetization
                coinsBalance = 15.0, // Start with 15 coins to see the balance immediately!
                ownerCoinsBalance = 0.0,
                isMonetized = false,
                hasYellowTick = false,
                isLoggedIn = true
            )
            mastiDao.insertUser(newUser)
            return newUser
        }
    }

    suspend fun logout() {
        mastiDao.logoutAllUsers()
    }

    suspend fun uploadVideo(title: String, caption: String, durationSec: Int, sizeMb: Double, uploaderId: String, mediaType: String = "video", isMatureContent: Boolean = false) {
        val user = mastiDao.getUserById(uploaderId) ?: return
        
        // 1 MB = 1 Gold Coin upload reward
        val coinsReward = sizeMb 
        
        val video = VideoEntity(
            title = title,
            caption = caption,
            videoDurationSec = durationSec,
            sizeMb = sizeMb,
            convertedCoins = coinsReward,
            uploaderId = uploaderId,
            uploaderName = user.username,
            uploaderHasYellowTick = user.hasYellowTick,
            viewsCount = 0,
            likesCount = 0,
            totalViewsCoins = 0.0,
            userViewsEarnings = 0.0,
            ownerViewsEarnings = 0.0,
            mediaType = mediaType,
            isMatureContent = isMatureContent
        )
        mastiDao.insertVideo(video)

        // Update user coins with upload reward
        val updatedUser = user.copy(
            coinsBalance = user.coinsBalance + coinsReward
        )
        mastiDao.updateUser(updatedUser)
    }

    suspend fun recordView(videoId: Int, viewerId: String) {
        val video = mastiDao.getVideoById(videoId) ?: return
        val uploader = mastiDao.getUserById(video.uploaderId) ?: return

        // If the uploader has yellow tick, boost view multiplier
        val increment = if (uploader.hasYellowTick) 5 else 1
        val newViews = video.viewsCount + increment
        val newLikes = video.likesCount + if (Random.nextFloat() > 0.4) increment else 0

        // Views earnings split:
        // 1 view generates 2.0 coins under-the-hood.
        // User gets 50% = 1.0 coins per view.
        // Owner gets 50% = 1.0 coins per view.
        // Yellow Tick uploader boosts views count, thus earning faster!
        val coinValuePerView = 2.0 // 2 coins total generated per view
        val totalCoinsGenerated = increment * coinValuePerView
        val userEarnedPart = totalCoinsGenerated * 0.5 // 50% user share (1.0 coin per view)
        val ownerEarnedPart = totalCoinsGenerated * 0.5 // 50% owner share (1.0 coin per view)

        val updatedVideo = video.copy(
            viewsCount = newViews,
            likesCount = newLikes,
            totalViewsCoins = video.totalViewsCoins + totalCoinsGenerated,
            userViewsEarnings = video.userViewsEarnings + userEarnedPart,
            ownerViewsEarnings = video.ownerViewsEarnings + ownerEarnedPart
        )
        mastiDao.updateVideo(updatedVideo)

        // If uploader is monetized (>= 500 followers), update their coin balance!
        val isUploaderMonetized = uploader.followersCount >= 500 || uploader.isMonetized
        val finalUserCoins = if (isUploaderMonetized) {
            uploader.coinsBalance + userEarnedPart
        } else {
            uploader.coinsBalance // Earns nothing if not monetized
        }

        // Always credit owner coins under-the-hood (hidden from user)
        val finalOwnerCoins = uploader.ownerCoinsBalance + ownerEarnedPart

        val updatedUploader = uploader.copy(
            coinsBalance = finalUserCoins,
            ownerCoinsBalance = finalOwnerCoins,
            isMonetized = isUploaderMonetized
        )
        mastiDao.updateUser(updatedUploader)

        // Simulate social activity: Give uploader followers to grow!
        if (viewerId != video.uploaderId) {
            val gainedFollowers = Random.nextInt(1, 4) * increment
            val currentUploader = mastiDao.getUserById(video.uploaderId) ?: return
            val freshFollowersCount = currentUploader.followersCount + gainedFollowers
            val freshMonetized = freshFollowersCount >= 500
            
            mastiDao.updateUser(
                currentUploader.copy(
                    followersCount = freshFollowersCount,
                    isMonetized = freshMonetized
                )
            )
        }
    }

    suspend fun buyYellowTick(userId: String, planType: String, price: Double): Boolean {
        val user = mastiDao.getUserById(userId) ?: return false
        val durationMs = when (planType) {
            "1_month" -> 30L * 24 * 60 * 60 * 1000
            "6_months" -> 180L * 24 * 60 * 60 * 1000
            "1_year" -> 365L * 24 * 60 * 60 * 1000
            else -> 30L * 24 * 60 * 60 * 1000
        }
        val expiry = System.currentTimeMillis() + durationMs
        val updated = user.copy(
            hasYellowTick = true,
            yellowTickExpiry = expiry,
            selectedPlanType = planType
        )
        mastiDao.updateUser(updated)
        return true
    }

    suspend fun redeemCoins(userId: String, amountCoins: Double): Boolean {
        val user = mastiDao.getUserById(userId) ?: return false
        if (user.coinsBalance < amountCoins) return false
        val updated = user.copy(
            coinsBalance = user.coinsBalance - amountCoins
        )
        mastiDao.updateUser(updated)
        return true
    }

    suspend fun simulateFollowersBoost(userId: String, amount: Int) {
        val user = mastiDao.getUserById(userId) ?: return
        val newFollowers = user.followersCount + amount
        val updated = user.copy(
            followersCount = newFollowers,
            isMonetized = newFollowers >= 500
        )
        mastiDao.updateUser(updated)
    }

    suspend fun updateUserDirect(user: UserEntity) {
        mastiDao.updateUser(user)
    }

    // Insert dummy videos if database is empty so user immediately has an Instagram-like experience
    suspend fun prepopulateDatabase(currentUserPhone: String) {
        // Create other creators
        val creators = listOf(
            UserEntity("rohit_fun", "Rohit Sharma", "rohit_fun@masti.com", 1200, 10.0, 10.0, true, true),
            UserEntity("riya_dance", "Riya Singh", "riya_dance@masti.com", 2400, 150.0, 150.0, true, false),
            UserEntity("masti_stars", "Masti Official", "official@masti.com", 15000, 500.0, 500.0, true, true)
        )
        for (creator in creators) {
            if (mastiDao.getUserById(creator.id) == null) {
                mastiDao.insertUser(creator)
            }
        }

        // Insert sample videos if none exist
        // Note: Flow collector checks this.
    }
}
