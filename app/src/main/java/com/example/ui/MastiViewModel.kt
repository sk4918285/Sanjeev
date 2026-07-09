package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MastiRepository
import com.example.data.UserEntity
import com.example.data.VideoEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class MastiViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: MastiRepository
    
    val loggedInUser: StateFlow<UserEntity?>
    val allVideos: StateFlow<List<VideoEntity>>
    val allUsers: StateFlow<List<UserEntity>>

    // --- UI Authentication & Anti-Bot Biometric Variables ---
    var phoneOrEmailInput by mutableStateOf("")
    var usernameInput by mutableStateOf("")
    var otpInput by mutableStateOf("")
    var isOtpSent by mutableStateOf(false)
    var simulatedOtpCode by mutableStateOf("")
    
    var ageCategoryInput by mutableStateOf("Adult (18+)") // "Adult (18+)" or "Minor (Under 18)"
    var verificationMethodInput by mutableStateOf("Face Scan") // "Face Scan" or "Aadhar Card"
    var aadharInput by mutableStateOf("")
    var isVerifyingBiometrics by mutableStateOf(false)
    var isBiometricSuccess by mutableStateOf(false)
    
    var loginError by mutableStateOf<String?>(null)
    var walletMessage by mutableStateOf<String?>(null)
    var uploadMessage by mutableStateOf<String?>(null)

    // Current navigation tab: "feed", "upload", "wallet", "profile" or our new "chats", "live", "ai_support" tabs
    private val _currentTab = MutableStateFlow("feed")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Dialog & Interaction states
    var showYellowTickPurchaseDialog by mutableStateOf(false)
    var showUploadDialog by mutableStateOf(false)
    var showWithdrawDialog by mutableStateOf(false)
    
    // For commenting system (Simulated)
    var activeCommentVideoId by mutableStateOf<Int?>(null)
    private val _videoComments = MutableStateFlow<Map<Int, List<Comment>>>(emptyMap())
    val videoComments: StateFlow<Map<Int, List<Comment>>> = _videoComments.asStateFlow()

    // --- CHAT GROUPS STATE ---
    private val _chatGroups = MutableStateFlow<List<ChatGroup>>(emptyList())
    val chatGroups: StateFlow<List<ChatGroup>> = _chatGroups.asStateFlow()

    // --- LIVE STREAMING STATE ---
    private val _liveStreams = MutableStateFlow<List<LiveStream>>(emptyList())
    val liveStreams: StateFlow<List<LiveStream>> = _liveStreams.asStateFlow()

    private val _activeUserLiveStream = MutableStateFlow<LiveStream?>(null)
    val activeUserLiveStream: StateFlow<LiveStream?> = _activeUserLiveStream.asStateFlow()

    // --- WALLET WITHDRAWAL DETAILS & TRANSACTION HISTORY ---
    var withdrawMethod by mutableStateOf("Google Pay") // "Google Pay", "PhonePe", "Other UPI", "Direct Bank Account"
    var withdrawDetailsInput by mutableStateOf("") // UPI ID or Bank Account No.
    var withdrawBankIfsc by mutableStateOf("") // IFSC Code if Bank Account selected
    
    private val _withdrawalHistory = MutableStateFlow<List<WithdrawalTx>>(emptyList())
    val withdrawalHistory: StateFlow<List<WithdrawalTx>> = _withdrawalHistory.asStateFlow()

    // --- SUPPORT CHAT STATE ---
    private val _supportMessages = MutableStateFlow<List<SupportMessage>>(emptyList())
    val supportMessages: StateFlow<List<SupportMessage>> = _supportMessages.asStateFlow()

    // --- AI OWNER CONTROLS ---
    var aiAutomaticSafetyMode by mutableStateOf(true)
    var aiBotDeletionSweepCount by mutableStateOf(24) // Simulated deleted spam accounts
    var isAdultSectionUnlocked by mutableStateOf(false) // Toggle Telegram-like Mature Option

    init {
        val database = AppDatabase.getDatabase(application)
        val dao = database.mastiDao()
        repository = MastiRepository(dao)
        
        loggedInUser = repository.loggedInUser.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        allVideos = repository.allVideos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allUsers = repository.allUsers.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Initialize chats, live streams, support and video comments
        initializeDefaultComments()
        initializeDefaultChats()
        initializeDefaultLiveStreams()
        initializeDefaultSupport()

        // Sync and pre-populate db if empty
        viewModelScope.launch {
            repository.allVideos.collect { videos ->
                if (videos.isEmpty()) {
                    createDefaultVideos()
                }
            }
        }
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    // --- AUTHENTICATION & BIOMETRICS FLOW ---
    fun sendOtp() {
        if (phoneOrEmailInput.trim().isEmpty()) {
            loginError = "Please enter a valid Phone Number or Email"
            return
        }
        if (usernameInput.trim().isEmpty()) {
            loginError = "Please choose a Display Name first"
            return
        }
        if (verificationMethodInput == "Aadhar Card" && aadharInput.trim().length != 12) {
            loginError = "Aadhar Card must be exactly 12 digits"
            return
        }
        
        loginError = null
        isVerifyingBiometrics = true
        
        viewModelScope.launch {
            // Simulated animated anti-bot scanning
            delay(1500)
            isVerifyingBiometrics = false
            isBiometricSuccess = true
            
            // Generate a random 4-digit OTP code to phone/email
            simulatedOtpCode = Random.nextInt(1000, 9999).toString()
            isOtpSent = true
        }
    }

    fun verifyOtp() {
        if (otpInput.trim() != simulatedOtpCode) {
            loginError = "Invalid OTP. Please try again."
            return
        }
        loginError = null
        viewModelScope.launch {
            // Log in user
            val user = repository.loginWithOtp(phoneOrEmailInput.trim(), usernameInput.trim())
            
            // Save age category & biometric status
            val updatedUser = user.copy(
                ageCategory = ageCategoryInput,
                isBiometricVerified = true,
                verificationMethod = verificationMethodInput,
                aadharNumber = if (verificationMethodInput == "Aadhar Card") aadharInput.trim() else ""
            )
            repository.updateUserDirect(updatedUser)
            repository.prepopulateDatabase(user.id)
            
            // Unlock mature features if verified adult
            isAdultSectionUnlocked = (ageCategoryInput == "Adult (18+)")

            // Clear variables
            phoneOrEmailInput = ""
            usernameInput = ""
            otpInput = ""
            aadharInput = ""
            isOtpSent = false
            simulatedOtpCode = ""
            isBiometricSuccess = false
            _currentTab.value = "feed"
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _currentTab.value = "feed"
        }
    }

    // --- CONTENT UPLOAD FLOW ---
    fun uploadVideo(title: String, caption: String, durationSec: Int, sizeMb: Double, mediaType: String, isMature: Boolean) {
        val currentUser = loggedInUser.value ?: return
        
        // Limit upload duration to 30s as requested by user
        val finalDuration = if (mediaType == "video" && durationSec > 30) 30 else durationSec
        
        viewModelScope.launch {
            repository.uploadVideo(
                title = title,
                caption = caption,
                durationSec = finalDuration,
                sizeMb = sizeMb,
                uploaderId = currentUser.id,
                mediaType = mediaType,
                isMatureContent = isMature
            )
            val fileTypeName = if (mediaType == "video") "30s Video" else "Picture"
            uploadMessage = "$fileTypeName Uploaded! Converted $sizeMb MB into ${String.format("%.1f", sizeMb)} Gold Coins!"
            showUploadDialog = false
        }
    }

    fun watchVideoAndRecordView(videoId: Int) {
        val currentUser = loggedInUser.value
        val viewerId = currentUser?.id ?: "anonymous_viewer"
        viewModelScope.launch {
            repository.recordView(videoId, viewerId)
        }
    }

    // --- MONETIZATION & YELLOW TICK FLOW ---
    fun buyYellowTick(planType: String, price: Double) {
        val currentUser = loggedInUser.value ?: return
        viewModelScope.launch {
            val success = repository.buyYellowTick(currentUser.id, planType, price)
            if (success) {
                walletMessage = "Congratulations! Your Yellow Tick verification is now active! Enjoy 5x view boosts!"
                showYellowTickPurchaseDialog = false
            }
        }
    }

    fun withdrawCoins(amount: Double) {
        val currentUser = loggedInUser.value ?: return
        if (amount <= 0) {
            walletMessage = "Enter a valid coin amount"
            return
        }
        if (currentUser.coinsBalance < amount) {
            walletMessage = "Insufficient coins. You only have ${String.format("%.1f", currentUser.coinsBalance)} coins."
            return
        }
        if (withdrawDetailsInput.trim().isEmpty()) {
            walletMessage = "Please enter your UPI ID or Bank Account Details"
            return
        }

        viewModelScope.launch {
            val success = repository.redeemCoins(currentUser.id, amount)
            if (success) {
                // Log payout transaction history
                val detailsCombined = if (withdrawMethod == "Direct Bank Account") {
                    "Bank A/C: $withdrawDetailsInput, IFSC: $withdrawBankIfsc"
                } else {
                    "$withdrawMethod UPI ID: $withdrawDetailsInput"
                }

                val newTx = WithdrawalTx(
                    id = UUID.randomUUID().toString().take(8).uppercase(),
                    amountCoins = amount,
                    paymentMethod = withdrawMethod,
                    paymentDetails = detailsCombined,
                    timestamp = System.currentTimeMillis(),
                    status = "SUCCESS"
                )
                
                _withdrawalHistory.value = listOf(newTx) + _withdrawalHistory.value
                walletMessage = "Withdrawal request of ₹${String.format("%.1f", amount)} processed instantly via $withdrawMethod!"
                showWithdrawDialog = false
                
                // Clear input field
                withdrawDetailsInput = ""
                withdrawBankIfsc = ""
            }
        }
    }

    fun boostMyFollowers() {
        val currentUser = loggedInUser.value ?: return
        viewModelScope.launch {
            repository.simulateFollowersBoost(currentUser.id, 45)
        }
    }

    // --- CHAT GROUPS FLOW ---
    fun createChatGroup(name: String) {
        val currentUser = loggedInUser.value ?: return
        val newGroup = ChatGroup(
            id = "g_" + UUID.randomUUID().toString().take(6),
            name = name.ifEmpty { "Fun Masti Group" },
            creatorName = currentUser.username,
            messages = listOf(
                GroupMessage(
                    senderName = "Masti Bot",
                    text = "Welcome to the group '${name}' created by ${currentUser.username}! Direct bot and spam accounts are strictly disabled here.",
                    timestamp = System.currentTimeMillis()
                )
            )
        )
        _chatGroups.value = _chatGroups.value + newGroup
    }

    fun sendGroupMessage(groupId: String, text: String) {
        if (text.trim().isEmpty()) return
        val currentUser = loggedInUser.value ?: return
        
        _chatGroups.value = _chatGroups.value.map { group ->
            if (group.id == groupId) {
                val updatedMsgs = group.messages + GroupMessage(
                    senderName = currentUser.username,
                    text = text.trim(),
                    timestamp = System.currentTimeMillis()
                )
                group.copy(messages = updatedMsgs)
            } else {
                group
            }
        }
    }

    // --- LIVE STREAMING FLOW ---
    fun startUserLiveStream(title: String, category: String, mode: String, isMature: Boolean, isGaming: Boolean) {
        val currentUser = loggedInUser.value ?: return
        
        val myStream = LiveStream(
            id = "user_stream_" + currentUser.id,
            streamerId = currentUser.id,
            streamerName = currentUser.username,
            title = title.ifEmpty { "Going Live on Masti! ✨" },
            viewerCount = Random.nextInt(15, 65), // Instant audience simulated
            streamMode = mode, // Public, Subscriber, Followers mode
            category = category, // BGMI, PUBG, Call of Duty, Free Fire, Vlog, etc.
            isMature = isMature,
            heartsReceived = 0,
            isGaming = isGaming
        )
        
        _activeUserLiveStream.value = myStream
        _liveStreams.value = listOf(myStream) + _liveStreams.value
    }

    fun stopUserLiveStream() {
        val active = _activeUserLiveStream.value ?: return
        _liveStreams.value = _liveStreams.value.filter { it.id != active.id }
        _activeUserLiveStream.value = null
    }

    fun sendGiftHeart(streamId: String) {
        val currentUser = loggedInUser.value ?: return
        if (currentUser.coinsBalance < 10.0) {
            walletMessage = "Insufficient coins to buy Heart gift. (Hearts cost 10 Coins)"
            return
        }
        
        viewModelScope.launch {
            // Deduct 10 coins from viewer
            val viewSuccess = repository.redeemCoins(currentUser.id, 10.0)
            if (viewSuccess) {
                // Increment stream hearts
                _liveStreams.value = _liveStreams.value.map { stream ->
                    if (stream.id == streamId) {
                        // Credit streamer coins balance
                        val streamer = repository.getUserById(stream.streamerId)
                        if (streamer != null) {
                            repository.updateUserDirect(
                                streamer.copy(coinsBalance = streamer.coinsBalance + 10.0)
                            )
                        }
                        stream.copy(
                            heartsReceived = stream.heartsReceived + 1,
                            viewerCount = stream.viewerCount + Random.nextInt(1, 3) // sending gifts draws more viewers!
                        )
                    } else {
                        stream
                    }
                }
                
                // Update active user stream too if viewing self or other
                val active = _activeUserLiveStream.value
                if (active != null && active.id == streamId) {
                    _activeUserLiveStream.value = active.copy(heartsReceived = active.heartsReceived + 1)
                }
            }
        }
    }

    // --- Masti AI Owner Support Helper ---
    fun sendSupportMessage(text: String) {
        if (text.trim().isEmpty()) return
        val currentUser = loggedInUser.value ?: return
        
        val userMsg = SupportMessage(
            senderName = currentUser.username,
            text = text.trim(),
            timestamp = System.currentTimeMillis()
        )
        _supportMessages.value = _supportMessages.value + userMsg

        viewModelScope.launch {
            delay(1000)
            val replyText = when {
                text.contains("coin", ignoreCase = true) || text.contains("paisa", ignoreCase = true) -> {
                    "Coins are generated through uploads (1 MB = 1 Coin) and views. Verified yellow-tick creators receive a massive 5x boost in views and accelerated splits!"
                }
                text.contains("withdraw", ignoreCase = true) || text.contains("bank", ignoreCase = true) || text.contains("upi", ignoreCase = true) -> {
                    "Under the 'Wallet' tab, select withdraw. Enter your Google Pay, PhonePe UPI ID, or direct bank account fields to withdraw. Our AI processes payouts securely in 5 mins."
                }
                text.contains("yellow", ignoreCase = true) || text.contains("tick", ignoreCase = true) || text.contains("verify", ignoreCase = true) -> {
                    "Yellow Verification Tick increases view and follow rates by 500%. Select a subscription package on the Profile page (₹50 / ₹250 / ₹500) to activate it immediately."
                }
                text.contains("game", ignoreCase = true) || text.contains("pubg", ignoreCase = true) || text.contains("bgmi", ignoreCase = true) || text.contains("free", ignoreCase = true) -> {
                    "Masti Time fully supports BGMI, PUBG, Free Fire, and COD live screen streaming! Launch 'Live Tab', check 'Gaming Mode', choose your game overlay, and start streaming to followers."
                }
                text.contains("adult", ignoreCase = true) || text.contains("mature", ignoreCase = true) || text.contains("sex", ignoreCase = true) || text.contains("telegram", ignoreCase = true) -> {
                    "Fully nude or explicitly sexual content is strictly blocked by the AI Owner filter. However, mild and mature media belongs under the 'Masti Mature Option' section, allowing followers or subscriber-only live streams."
                }
                else -> {
                    "I am the Masti AI Owner Assistant. Your account is verified under anti-bot protection (${currentUser.verificationMethod}). I can help you with withdrawals, streaming setups, and profile monetization!"
                }
            }
            val aiMsg = SupportMessage(
                senderName = "Masti Support AI",
                text = replyText,
                timestamp = System.currentTimeMillis()
            )
            _supportMessages.value = _supportMessages.value + aiMsg
        }
    }

    // --- AI OWNER DASHBOARD FUNCTIONS ---
    fun aiTriggerBotCleanup() {
        viewModelScope.launch {
            delay(1200)
            aiBotDeletionSweepCount += Random.nextInt(10, 30)
            walletMessage = "AI Bot Detector Sweep Finished! Deleted duplicate bot accounts. 100% Genuine Creator Followers Maintained!"
        }
    }

    // --- COMMENT SIMULATION ---
    fun addComment(videoId: Int, author: String, text: String) {
        val current = _videoComments.value[videoId] ?: emptyList()
        val newComment = Comment(author, text, System.currentTimeMillis())
        _videoComments.value = _videoComments.value.toMutableMap().apply {
            put(videoId, current + newComment)
        }
    }

    private fun initializeDefaultComments() {
        _videoComments.value = mapOf(
            101 to listOf(
                Comment("Riya Singh", "Hahaha so accurate! 😂 Chai is pure emotion.", System.currentTimeMillis() - 1000000),
                Comment("Rahul_Vlogs", "Nice editing bro! Keep it up.", System.currentTimeMillis() - 500000)
            ),
            102 to listOf(
                Comment("Masti Official", "Incredible moves! Yellow Tick deserved 🌟🔥", System.currentTimeMillis() - 800000),
                Comment("Amit_99", "What a choreo, link for full video?", System.currentTimeMillis() - 200000)
            ),
            103 to listOf(
                Comment("Rohit Sharma", "Oh my god, now I'm craving a burger too! 🤤🍔", System.currentTimeMillis() - 1200000),
                Comment("Sneha_k", "Sasta aur mast food! Masti Time is getting awesome.", System.currentTimeMillis() - 30000)
            )
        )
    }

    private fun initializeDefaultChats() {
        _chatGroups.value = listOf(
            ChatGroup(
                id = "g_bgmi",
                name = "BGMI Pro Gamers 🎮",
                creatorName = "Masti Official",
                messages = listOf(
                    GroupMessage("Rohit_Sharma", "Hey, who wants to push Conqueror rank live on Masti today?", System.currentTimeMillis() - 10000000),
                    GroupMessage("Riya_Singh", "Count me in! I will gift you hearts when you get Chicken Dinner!", System.currentTimeMillis() - 5000000)
                )
            ),
            ChatGroup(
                id = "g_creators",
                name = "Masti Creator Hub ✨",
                creatorName = "Masti Official",
                messages = listOf(
                    GroupMessage("Masti_Official", "Welcome creators! Upload your 30s clips or pictures to earn instant coins.", System.currentTimeMillis() - 20000000)
                )
            ),
            ChatGroup(
                id = "g_adult",
                name = "Adult Chat Lounge 🤫",
                creatorName = "Riya_Singh",
                messages = listOf(
                    GroupMessage("Riya_Singh", "This chat is 18+ restricted! Safe but mature topics allowed.", System.currentTimeMillis() - 15000000)
                )
            )
        )
    }

    private fun initializeDefaultLiveStreams() {
        _liveStreams.value = listOf(
            LiveStream(
                id = "stream_bgmi_1",
                streamerId = "rohit_fun",
                streamerName = "Rohit_Sharma",
                title = "BGMI Conqueror Lobby Push Live! 🎮🔥",
                viewerCount = 124,
                streamMode = "Public",
                category = "BGMI",
                isMature = false,
                heartsReceived = 42,
                isGaming = true
            ),
            LiveStream(
                id = "stream_pubg_1",
                streamerId = "riya_dance",
                streamerName = "Riya_Singh",
                title = "PUBG Classic Custom Room with Viewers 🏆",
                viewerCount = 89,
                streamMode = "Followers Only",
                category = "PUBG",
                isMature = false,
                heartsReceived = 15,
                isGaming = true
            ),
            LiveStream(
                id = "stream_mature_1",
                streamerId = "masti_stars",
                streamerName = "Masti_Official",
                title = "Late Night Safe Mature Talk show (No Nudity) 🤫",
                viewerCount = 210,
                streamMode = "Subscribers Only",
                category = "Vlog",
                isMature = true,
                heartsReceived = 112,
                isGaming = false
            )
        )
    }

    private fun initializeDefaultSupport() {
        _supportMessages.value = listOf(
            SupportMessage(
                senderName = "Masti Support AI",
                text = "Hello! I am Masti Help Assistant, managed by the Owner's AI. How can I help you today? Ask me about coin conversions, Yellow Verification Ticks, safe mature content limits, anti-bot biometric logs, or withdrawing your money!",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    private suspend fun createDefaultVideos() {
        // Pre-create other accounts
        val creators = listOf(
            UserEntity("rohit_fun", "Rohit_Sharma", "rohit_fun@masti.com", 1200, 10.0, 10.0, true, true),
            UserEntity("riya_dance", "Riya_Singh", "riya_dance@masti.com", 2400, 150.0, 150.0, true, false),
            UserEntity("masti_stars", "Masti_Official", "official@masti.com", 15000, 500.0, 500.0, true, true)
        )
        for (creator in creators) {
            repository.loginWithOtp(creator.id, creator.username)
        }

        // Add sample videos under other accounts
        val v1 = VideoEntity(
            id = 101,
            title = "When Chai is Life! ☕😂",
            caption = "Tag that one tea lover who cannot live without garam chai! #chailove #mastitime #funny",
            videoDurationSec = 15,
            sizeMb = 18.4,
            convertedCoins = 18.4,
            uploaderId = "rohit_fun",
            uploaderName = "Rohit_Sharma",
            uploaderHasYellowTick = true,
            viewsCount = 345,
            likesCount = 124,
            totalViewsCoins = 690.0,
            userViewsEarnings = 345.0,
            ownerViewsEarnings = 345.0,
            mediaType = "video",
            isMatureContent = false
        )
        val v2 = VideoEntity(
            id = 102,
            title = "Dance till the sunset! 💃✨",
            caption = "Sunday mood with this amazing beat! How is the drop? #dance #foryou #trending",
            videoDurationSec = 22,
            sizeMb = 32.5,
            convertedCoins = 32.5,
            uploaderId = "riya_dance",
            uploaderName = "Riya_Singh",
            uploaderHasYellowTick = false,
            viewsCount = 180,
            likesCount = 76,
            totalViewsCoins = 360.0,
            userViewsEarnings = 180.0,
            ownerViewsEarnings = 180.0,
            mediaType = "video",
            isMatureContent = false
        )
        val v3 = VideoEntity(
            id = 103,
            title = "Ultimate Cheese Burger Review! 🍔",
            caption = "Cheesy goodness at just Rs 40! Street food review inside Delhi. #foodie #burger #yummy",
            videoDurationSec = 28,
            sizeMb = 42.0,
            convertedCoins = 42.0,
            uploaderId = "masti_stars",
            uploaderName = "Masti_Official",
            uploaderHasYellowTick = true,
            viewsCount = 940,
            likesCount = 423,
            totalViewsCoins = 1880.0,
            userViewsEarnings = 940.0,
            ownerViewsEarnings = 940.0,
            mediaType = "video",
            isMatureContent = false
        )

        val database = AppDatabase.getDatabase(getApplication())
        val dao = database.mastiDao()
        dao.insertVideo(v1)
        dao.insertVideo(v2)
        dao.insertVideo(v3)
    }
}

// --- CORE SIMULATED DATA STRUCTURES ---

data class Comment(
    val author: String,
    val text: String,
    val timestamp: Long
)

data class ChatGroup(
    val id: String,
    val name: String,
    val creatorName: String,
    val messages: List<GroupMessage>
)

data class GroupMessage(
    val senderName: String,
    val text: String,
    val timestamp: Long
)

data class LiveStream(
    val id: String,
    val streamerId: String,
    val streamerName: String,
    val title: String,
    val viewerCount: Int,
    val streamMode: String, // "Public", "Subscriber Only", "Followers Only"
    val category: String, // "BGMI", "PUBG", "Free Fire", "Call of Duty", "Vlog", etc.
    val isMature: Boolean,
    val heartsReceived: Int,
    val isGaming: Boolean = false
)

data class WithdrawalTx(
    val id: String,
    val amountCoins: Double,
    val paymentMethod: String,
    val paymentDetails: String,
    val timestamp: Long,
    val status: String // "SUCCESS", "PENDING"
)

data class SupportMessage(
    val senderName: String,
    val text: String,
    val timestamp: Long
)
