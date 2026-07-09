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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.IconButtonDefaults
import com.example.ui.LiveStream
import com.example.ui.MastiViewModel
import kotlin.random.Random

@Composable
fun LiveScreen(viewModel: MastiViewModel) {
    val streams by viewModel.liveStreams.collectAsState()
    val activeUserStream by viewModel.activeUserLiveStream.collectAsState()
    val loggedInUser by viewModel.loggedInUser.collectAsState()

    var activeViewStreamId by remember { mutableStateOf<String?>(null) }
    var isStartingStream by remember { mutableStateOf(false) }

    // Go Live state inputs
    var streamTitleInput by remember { mutableStateOf("") }
    var streamCategoryInput by remember { mutableStateOf("BGMI") } // BGMI, PUBG, Vlog, etc.
    var streamModeInput by remember { mutableStateOf("Public") } // Public, Subscriber, Followers mode
    var isMatureStream by remember { mutableStateOf(false) }
    var isGamingStream by remember { mutableStateOf(true) }

    val activeViewingStream = streams.find { it.id == activeViewStreamId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A14))
    ) {
        if (activeUserStream == null && activeViewingStream == null) {
            // --- LIVE DIRECTORY SCREEN ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Masti Live Stream",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Go Live to Earn Hearts & Coins! 🪙🎙️",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = { isStartingStream = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77737)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("go_live_trigger_button")
                ) {
                    Icon(imageVector = Icons.Default.Sensors, contentDescription = "Live", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Go Live", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // SETUP LIVE BROADCAST SHEET
            if (isStartingStream) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16162E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF77737)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Configure Live Stream", fontWeight = FontWeight.Bold, color = Color.White)
                            IconButton(onClick = { isStartingStream = false }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = streamTitleInput,
                            onValueChange = { streamTitleInput = it },
                            label = { Text("Stream Title") },
                            placeholder = { Text("e.g. Classic Squad Rush on PUBG Mobile!") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF77737),
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("stream_title_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Dropdown Selection
                        Text("Stream Category / Game:", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("BGMI", "PUBG", "Free Fire", "COD", "Vlog").forEach { cat ->
                                val selected = (streamCategoryInput == cat)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) Color(0xFFF77737) else Color(0xFF22223C))
                                        .border(1.dp, if (selected) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable {
                                            streamCategoryInput = cat
                                            isGamingStream = (cat != "Vlog")
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(cat, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stream Mode (Public / Subscriber / Followers)
                        Text("Live Stream Audience Restriction:", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Public", "Followers Only", "Subscribers Only").forEach { mode ->
                                val selected = (streamModeInput == mode)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) Color(0xFF833AB4) else Color(0xFF22223C))
                                        .clickable { streamModeInput = mode }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(mode, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mature NSFW Safeguard Option
                        if (loggedInUser?.ageCategory == "Adult (18+)") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Mature Safeguard (Telegram Style)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Stream under the safeguarded mature channel", color = Color.Gray, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = isMatureStream,
                                    onCheckedChange = { isMatureStream = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFF77737))
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Start stream button
                        Button(
                            onClick = {
                                viewModel.startUserLiveStream(
                                    title = streamTitleInput,
                                    category = streamCategoryInput,
                                    mode = streamModeInput,
                                    isMature = isMatureStream,
                                    isGaming = isGamingStream
                                )
                                isStartingStream = false
                                streamTitleInput = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                                .testTag("confirm_go_live_button")
                        ) {
                            Text("Launch Live Broadcast 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // LIST OF RUNNING LIVE STREAMS
            Text(
                text = "Active Live Streamers",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )

            if (streams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No live streams running right now. Be the first to go live!", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(streams) { stream ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.9f)
                                .clickable { activeViewStreamId = stream.id },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF16162C)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Dynamic Game Gradient background
                                val categoryGrad = if (stream.isGaming) {
                                    Brush.verticalGradient(listOf(Color(0xFF833AB4), Color(0xFF1A0B2E)))
                                } else {
                                    Brush.verticalGradient(listOf(Color(0xFFE1306C), Color(0xFF2E0B1A)))
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(categoryGrad)
                                        .padding(12.dp)
                                ) {
                                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                                        // Header elements
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Red Live Badge
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color.Red)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("LIVE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }

                                            // Viewer Count
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.Group, contentDescription = "Viewers", tint = Color.White, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("${stream.viewerCount}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Central Game Graphics Mock
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                            Icon(
                                                imageVector = if (stream.isGaming) Icons.Default.Gamepad else Icons.Default.Podcasts,
                                                contentDescription = "stream type",
                                                tint = Color.White,
                                                modifier = Modifier.size(38.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = stream.category,
                                                color = Color(0xFFFFB300),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Footer Info
                                        Column {
                                            Text(
                                                text = stream.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                                                Text(
                                                    text = "@${stream.streamerName}",
                                                    color = Color.LightGray,
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                if (stream.isMature) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color.Black)
                                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                                    ) {
                                                        Text("X 18+", color = Color.Red, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else if (activeUserStream != null) {
            // --- ACTIVE MY LIVE HOSTING SCREEN ---
            val myStream = activeUserStream!!
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF131326))
            ) {
                // Game Streaming Simulation Overlay head
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // Host Title Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.Green)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Broadcasting Live", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Button(
                            onClick = { viewModel.stopUserLiveStream() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("stop_live_stream_button")
                        ) {
                            Text("Stop Stream", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gaming video simulator container box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                            .border(1.5.dp, Color(0xFFF77737), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (myStream.isGaming) Icons.Default.Gamepad else Icons.Default.Podcasts,
                                contentDescription = "Active stream logo",
                                tint = Color(0xFFF77737),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Streaming Mode: ${myStream.category} Overlay Active",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Title: ${myStream.title}",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Audience Filter: ${myStream.streamMode}",
                                color = Color(0xFFFFB300),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Statistics floating HUD
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x99000000))
                                .padding(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("🔴 Live Stream Active", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Viewers: ${myStream.viewerCount}", color = Color.White, fontSize = 9.sp)
                                Text("Hearts: ${myStream.heartsReceived} ❤️", color = Color(0xFFE1306C), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("Redeemed: 🪙 ${(myStream.heartsReceived * 10.0).toInt()} Coins", color = Color(0xFFFFB300), fontSize = 9.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated live viewer commentary feed
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C36)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Live Chat Feedback", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.DarkGray))
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyColumn {
                                item {
                                    Text("GamerPro: That headshot was insane! 🔥", color = Color.LightGray, fontSize = 11.sp)
                                    Text("MastiQueen: Sending hearts for coin support! ❤️", color = Color.LightGray, fontSize = 11.sp)
                                    Text("Amit_Live: What a performance bro, BGMI King!", color = Color.LightGray, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

        } else {
            // --- VIEWER INTERACTIVE STREAMING COMPOSABLE ---
            val stream = activeViewingStream!!

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Interactive Game Simulator background or display
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // Close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { activeViewStreamId = null },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF1E1E34))
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        // User profile metrics
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Red)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${stream.viewerCount} watching", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gaming Video Content screen simulation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF16162E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (stream.isGaming) Icons.Default.Gamepad else Icons.Default.Podcasts,
                                contentDescription = "Video simulation",
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stream.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
                            Text("Broadcasting: ${stream.category}", color = Color.LightGray, fontSize = 12.sp)
                            
                            // Restriction HUD if not public
                            if (stream.streamMode != "Public") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x66833AB4))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stream.streamMode, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Hearts count HUD
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x99000000))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Hearts", tint = Color.Red, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${stream.heartsReceived} Hearts", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Send heart gift option costing 10 Coins
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C30)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1306C))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Send Virtual Heart Gift ❤️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Costs 10 Coins. Streamer gets 100% split!", color = Color.LightGray, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { viewModel.sendGiftHeart(stream.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("send_heart_gift_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Heart", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Send Gift (10 Coins)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
