package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MastiDao {
    
    // --- USER QUERIES ---
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getLoggedInUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUser(): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE phoneOrEmail = :phoneOrEmail LIMIT 1")
    suspend fun getUserByPhoneOrEmail(phoneOrEmail: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("SELECT * FROM users ORDER BY followersCount DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>


    // --- VIDEO QUERIES ---
    @Query("SELECT * FROM videos ORDER BY timestamp DESC")
    fun getAllVideosFlow(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE uploaderId = :uploaderId ORDER BY timestamp DESC")
    fun getVideosByUploaderFlow(uploaderId: String): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Query("SELECT * FROM videos WHERE id = :videoId")
    suspend fun getVideoById(videoId: Int): VideoEntity?
}
