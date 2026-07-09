package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VideoEntity
import com.example.ui.MastiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(viewModel: MastiViewModel) {
    val videos by viewModel.allVideos.collectAsState()
    val loggedInUser by viewModel.loggedInUser.collectAsState()
    val commentsMap by viewModel.videoComments.collectAsState()

    var showCommentSheet by remember { mutableStateOf(false) }
    var selectedVideoIdForComments by remember { mutableStateOf<Int?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // 0 = Public Main Feed, 1 = Mature Safeguard (Telegram Mode)
    var selectedFeedChannel by remember { mutableStateOf(0) }
    var matureToastMessage by remember { mutableStateOf<String?>(null) }

    // Map to track active watching state per video (simulating play time)
    val watchingStates = remember { mutableStateMapOf<Int, Boolean>() }
    val watchProgress = remember { mutableStateMapOf<Int, Float>() }

    // Filtered videos based on the active channel selection
    val filteredVideos = videos.filter {
        if (selectedFeedChannel == 1) {
            it.isMatureContent
        } else {
            !it.isMatureContent
        }
    }

    LaunchedEffect(matureToastMessage) {
        if (matureToastMessage != null) {
            delay(4000)
            matureToastMessage = null
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A12))
    ) {
        // 1. Top Logo Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "masti time",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White
                    )
                    Text(
                        text = "Convert Views to Gold Coins!",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }

                IconButton(onClick = { viewModel.setTab("wallet") }) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "Wallet shortcut",
                            tint = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB300))
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }
        }

        // 2. Channel Filter Toggle (Main Feed vs. NSFW Mature Option)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF16162C))
                    .padding(4.dp)
            ) {
                // Public Feed
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedFeedChannel == 0) Color(0xFFF77737) else Color.Transparent)
                        .clickable { selectedFeedChannel = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Public Masti Feed", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Mature Safeguard Channel (Telegram-style option)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedFeedChannel == 1) Color(0xFF833AB4) else Color.Transparent)
                        .clickable {
                            if (loggedInUser?.ageCategory == "Adult (18+)") {
                                selectedFeedChannel = 1
                            } else {
                                matureToastMessage = "Access Restricted! You must register as Adult (18+) with valid Biometrics to view Mature contents."
                            }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Mature", tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mature Option (18+)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Warning banner
        matureToastMessage?.let { err ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x33F44336)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(err, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                }
            }
        }

        // 3. Stories Row
        item {
            Spacer(modifier = Modifier.height(10.dp))
            StoriesRow(viewModel)
            Divider(color = Color(0xFF1E1E2F), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
        }

        // Empty Feed Indicator
        if (filteredVideos.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = "none", tint = Color.Gray, modifier = Modifier.size(42.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No posts available in this channel. Be the first to upload!", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }

        // 4. Feed posts
        items(filteredVideos, key = { it.id }) { video ->
            var isLiked by remember { mutableStateOf(false) }
            val isWatching = watchingStates[video.id] ?: false
            val progress = watchProgress[video.id] ?: 0f

            LaunchedEffect(isWatching) {
                if (isWatching) {
                    var currentProgress = 0f
                    while (currentProgress < 1f) {
                        delay(200)
                        currentProgress += 0.05f
                        watchProgress[video.id] = currentProgress
                    }
                    // Finished viewing! report view to credit creator wallet splits!
                    viewModel.watchVideoAndRecordView(video.id)
                    watchingStates[video.id] = false
                    watchProgress[video.id] = 0f
                }
            }

            VideoPostItem(
                video = video,
                isLiked = isLiked,
                onLikeClick = { isLiked = !isLiked },
                isWatching = isWatching,
                watchProgress = progress,
                onPlayClick = {
                    watchingStates[video.id] = !isWatching
                },
                onCommentClick = {
                    selectedVideoIdForComments = video.id
                    showCommentSheet = true
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // BOTTOM COMMENTS SHEET
    if (showCommentSheet && selectedVideoIdForComments != null) {
        val vidId = selectedVideoIdForComments!!
        val comments = commentsMap[vidId] ?: emptyList()
        var newCommentText by remember { mutableStateOf("") }

        ModalBottomSheet(
            onDismissRequest = { showCommentSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF16162C)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Comments (${comments.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(16.dp)
                )

                Divider(color = Color(0xFF2E2E44))

                Box(
                    modifier = Modifier
                        .height(240.dp)
                        .fillMaxWidth()
                ) {
                    if (comments.isEmpty()) {
                        Text(
                            text = "No comments yet. Start the conversation!",
                            color = Color.Gray,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            fontSize = 13.sp
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(comments) { comment ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE1306C)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                comment.author.take(1).uppercase(),
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = comment.author,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = comment.text,
                                        color = Color.LightGray,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(start = 32.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Divider(color = Color(0xFF2E2E44))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Write a comment...", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFF77737),
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                val currentAuthor = loggedInUser?.username ?: "Anonymous"
                                viewModel.addComment(vidId, currentAuthor, newCommentText)
                                newCommentText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF77737))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Post Comment",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoriesRow(viewModel: MastiViewModel) {
    val loggedInUser by viewModel.loggedInUser.collectAsState()

    val activeStoryCreators = listOf(
        StoryItemData("masti_stars", "Masti Official", Color(0xFFFFB300)),
        StoryItemData("rohit_fun", "Rohit Sharma", Color(0xFF42A5F5)),
        StoryItemData("riya_dance", "Riya Singh", Color(0xFFEC407A)),
        StoryItemData("creative_mind", "Ajay Dev", Color(0xFFAB47BC)),
        StoryItemData("comedy_comedy", "Sunil Comedy", Color(0xFF26A69A))
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { viewModel.setTab("profile") }
            ) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A2A44)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (loggedInUser?.username?.take(2)?.uppercase() ?: "MT"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF77737))
                            .border(1.5.dp, Color(0xFF0A0A12), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add story",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "My Story",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        items(activeStoryCreators) { story ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .border(
                            width = 2.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFF833AB4),
                                    Color(0xFFF77737),
                                    Color(0xFFFFDC80),
                                    Color(0xFF833AB4)
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(story.avatarBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = story.username.take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = story.displayName,
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(68.dp)
                )
            }
        }
    }
}

data class StoryItemData(
    val username: String,
    val displayName: String,
    val avatarBgColor: Color
)

@Composable
fun VideoPostItem(
    video: VideoEntity,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    isWatching: Boolean,
    watchProgress: Float,
    onPlayClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131326)),
        shape = RoundedCornerShape(0.dp) // Flat borderless Instagram feed style
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            
            // 1. Post Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E88E5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        video.uploaderName.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = video.uploaderName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    
                    if (video.uploaderHasYellowTick) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Yellow Tick Verified Creator",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (video.isMatureContent) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("X 18+", color = Color.Red, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = Color.LightGray)
                }
            }

            // 2. Content Player Frame (Simulated Video vs Picture Frame)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E1E38), Color(0xFF0F0F1A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Background visual wave decoration
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    drawCircle(
                        color = Color(0xFFF77737).copy(alpha = 0.15f),
                        radius = size.minDimension / 3,
                        center = center,
                        style = Stroke(width = 3.dp.toPx(), pathEffect = pathEffect)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = if (video.mediaType == "picture") Icons.Default.Image else Icons.Default.Videocam,
                        contentDescription = "Media type",
                        tint = if (video.mediaType == "picture") Color(0xFFFFB300).copy(alpha = 0.7f) else Color(0xFFF77737).copy(alpha = 0.7f),
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = video.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (video.mediaType == "picture") "Media type: High-res Picture" else "Duration: ${video.videoDurationSec} seconds",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated watch & earn action
                    Button(
                        onClick = onPlayClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isWatching) Color(0xFFD32F2F) else Color(0xFFF77737)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("watch_video_button_${video.id}")
                    ) {
                        if (isWatching) {
                            CircularProgressIndicator(
                                progress = { watchProgress },
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Loading... ${(watchProgress * 100).toInt()}%", fontSize = 13.sp)
                        } else {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play Icon")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (video.mediaType == "picture") "Simulate View & Earn" else "Simulate Watch & Earn",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // DATA CONVERTED TO GOLD COINS BADGE
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xE6FFB300))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🪙 ${video.sizeMb} MB = ${String.format("%.1f", video.convertedCoins)} Coins",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // 4. Interaction Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClick, modifier = Modifier.testTag("like_button_${video.id}")) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.White
                    )
                }

                IconButton(onClick = onCommentClick, modifier = Modifier.testTag("comment_button_${video.id}")) {
                    Icon(
                        imageVector = Icons.Outlined.Comment,
                        contentDescription = "Comment",
                        tint = Color.White
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Monetized Watch",
                    color = Color(0xFFFFB300),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            // Uploader Caption & stats
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${video.viewsCount} views • ${video.likesCount} likes",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.caption,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Just now",
                    color = Color.DarkGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
