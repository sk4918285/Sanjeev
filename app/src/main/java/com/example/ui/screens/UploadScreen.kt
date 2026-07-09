package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MastiViewModel
import kotlinx.coroutines.delay

@Composable
fun UploadScreen(viewModel: MastiViewModel) {
    var title by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    
    var mediaType by remember { mutableStateOf("video") } // "video" or "picture"
    var durationSec by remember { mutableFloatStateOf(15f) } // Max 30s as requested
    var sizeMb by remember { mutableFloatStateOf(18.5f) }
    var isMature by remember { mutableStateOf(false) } // NSFW Toggle
    
    var isUploading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Automatically dismiss upload message after some seconds
    LaunchedEffect(viewModel.uploadMessage) {
        if (viewModel.uploadMessage != null) {
            delay(5000)
            viewModel.uploadMessage = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A12))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.setTab("feed") }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Masti Creator Studio",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info / Conversion notification banner
        viewModel.uploadMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x334CAF50)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Green),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = msg,
                    color = Color.Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131326)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = "Upload Icon",
                    tint = Color(0xFFF77737),
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Convert Media Data to Gold Coins",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Every Megabyte (MB) used to compile your 30s clip or picture transforms instantly into real Gold Coins!",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // MEDIA TYPE SELECTOR (Video vs. Picture)
                Text(
                    text = "Select Content Media Type:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Video Option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (mediaType == "video") Color(0x33F77737) else Color(0xFF1E1E34))
                            .clickable { mediaType = "video" }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = "video", tint = Color(0xFFF77737), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("30s Video", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Picture Option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (mediaType == "picture") Color(0x33F77737) else Color(0xFF1E1E34))
                            .clickable { mediaType = "picture" }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = "picture", tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Picture", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Content Title") },
                    placeholder = { Text("e.g. Delicious cheese burger tasting! 🍔") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF77737),
                        focusedLabelColor = Color(0xFFF77737),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_title_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Caption Input
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Caption & Hashtags") },
                    placeholder = { Text("e.g. Spicy food reviewer vibes #foodie #foryou #masti") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF77737),
                        focusedLabelColor = Color(0xFFF77737),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_caption_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Slider 1: Video Duration (Only if type is video)
                if (mediaType == "video") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Video Duration Limit", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${durationSec.toInt()} Seconds (Max 30s)", color = Color(0xFFF77737), fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = durationSec,
                            onValueChange = { durationSec = it },
                            valueRange = 5f..30f, // STRIKT limit of 30 seconds
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFF77737),
                                activeTrackColor = Color(0xFFF77737)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Slider 2: Media File Size (determines Gold coins split!)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Simulated File Size (MB)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${String.format("%.1f", sizeMb)} MB", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = sizeMb,
                        onValueChange = { sizeMb = it },
                        valueRange = 1f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFB300),
                            activeTrackColor = Color(0xFFFFB300)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // MATURE SAFETY SAFEGUARD OPTION
                if (viewModel.loggedInUser.value?.ageCategory == "Adult (18+)") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E1E34))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mature Content Safeguard Option 🤫", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Tag this post as mature (Telegram mode, zero explicit nudity)", color = Color.LightGray, fontSize = 10.sp)
                        }
                        Switch(
                            checked = isMature,
                            onCheckedChange = { isMature = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFF77737))
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Dynamic Live Conversion Calculator Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2E241E), Color(0xFF1E1E1E))
                            )
                        )
                        .border(1.dp, Color(0xFFFFB300), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "info",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Conversion Formula: 1 Megabyte = 1 Gold Coin",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Instant Reward: ${String.format("%.1f", sizeMb)} Coins (₹${String.format("%.1f", sizeMb)})",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Publish & Convert button
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            isUploading = true
                            viewModel.uploadVideo(
                                title = title,
                                caption = caption,
                                durationSec = if (mediaType == "video") durationSec.toInt() else 0,
                                sizeMb = sizeMb.toDouble(),
                                mediaType = mediaType,
                                isMature = isMature
                            )
                            isUploading = false
                            title = ""
                            caption = ""
                            viewModel.setTab("feed")
                        }
                    },
                    enabled = title.isNotBlank() && !isUploading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("publish_video_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF77737),
                        disabledContainerColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (isUploading) "Publishing..." else "Publish & Convert to Coins!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
