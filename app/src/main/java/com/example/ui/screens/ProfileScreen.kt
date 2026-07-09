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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.ui.MastiViewModel

@Composable
fun ProfileScreen(viewModel: MastiViewModel) {
    val loggedInUser by viewModel.loggedInUser.collectAsState()
    val videos by viewModel.allVideos.collectAsState()

    val user = loggedInUser
    if (user == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A12)),
            contentAlignment = Alignment.Center
        ) {
            Text("Please login to see your profile.", color = Color.White)
        }
        return
    }

    // Filter user's uploaded videos
    val myVideos = videos.filter { it.uploaderId == user.id }

    var selectedPlanType by remember { mutableStateOf("1_month") }
    var selectedPlanPrice by remember { mutableStateOf(50.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A12))
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Creator Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.White
            )

            // Logout Button
            IconButton(
                onClick = { viewModel.logout() },
                modifier = Modifier.testTag("logout_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Log Out",
                    tint = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Profile Bio Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile avatar bubble
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE1306C)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.username.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.username,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (user.hasYellowTick) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Verified Creator",
                            tint = Color(0xFFFFB300), // Glowing gold/yellow tick badge
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = user.phoneOrEmail,
                    fontSize = 12.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Followers/Subscribers and Posts Counters
                Row {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${user.followersCount}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text("Subscribers", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${myVideos.size}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text("Videos", color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Yellow Verification Tick Subscription Offers (₹50 / ₹250 / ₹500 PLANS)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131326)),
            shape = RoundedCornerShape(20.dp),
            border = if (user.hasYellowTick) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)) else null
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (user.hasYellowTick) "Premium Yellow Tick is Active!" else "Unlock Premium Yellow Tick! 🌟",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (user.hasYellowTick) Color(0xFFFFB300) else Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "verified badge",
                        tint = if (user.hasYellowTick) Color(0xFFFFB300) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Yellow Tick verification badges give your content an ultimate 5x boost in community views & coin velocity!",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    lineHeight = 16.sp
                )

                if (!user.hasYellowTick) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Plan selection rows
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // 1. ₹50 Plan
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1E34))
                                .clickable {
                                    selectedPlanType = "1_month"
                                    selectedPlanPrice = 50.0
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (selectedPlanType == "1_month"),
                                onClick = {
                                    selectedPlanType = "1_month"
                                    selectedPlanPrice = 50.0
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFB300))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("₹50 - 1 Month Plan", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("Boost 30 Days", color = Color(0xFFFFB300), fontSize = 11.sp)
                        }

                        // 2. ₹250 Plan
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1E34))
                                .clickable {
                                    selectedPlanType = "6_months"
                                    selectedPlanPrice = 250.0
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (selectedPlanType == "6_months"),
                                onClick = {
                                    selectedPlanType = "6_months"
                                    selectedPlanPrice = 250.0
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFB300))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("₹250 - 6 Months Plan", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("Best Seller", color = Color(0xFFFFB300), fontSize = 11.sp)
                        }

                        // 3. ₹500 Plan
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1E34))
                                .clickable {
                                    selectedPlanType = "1_year"
                                    selectedPlanPrice = 500.0
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (selectedPlanType == "1_year"),
                                onClick = {
                                    selectedPlanType = "1_year"
                                    selectedPlanPrice = 500.0
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFB300))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("₹500 - 1 Year Plan", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("Value Deal", color = Color(0xFFFFB300), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Purchase trigger button
                    Button(
                        onClick = { viewModel.showYellowTickPurchaseDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .testTag("subscribe_yellow_tick_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Get Yellow Tick for ₹${selectedPlanPrice.toInt()}", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FFB300))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Premium Active Plan: ${user.selectedPlanType.replace("_", " ").uppercase()}. Your video uploads now automatically generate 5x more viewing traffic & accelerated coin splits!",
                            color = Color(0xFFFFB300),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 3. Simulated UPI payment dialog sheet
        AnimatedVisibility(visible = viewModel.showYellowTickPurchaseDialog) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C30)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFB300))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masti UPI Secure Payment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Plan: Yellow Tick ${selectedPlanType.replace("_", " ")} - Amount: ₹${selectedPlanPrice.toInt()}",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text("Google Pay", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).padding(6.dp))
                        Text("PhonePe", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).padding(6.dp))
                        Text("UPI QR", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).padding(6.dp))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.showYellowTickPurchaseDialog = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text("Cancel", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                viewModel.buyYellowTick(selectedPlanType, selectedPlanPrice)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("confirm_payment_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
                        ) {
                            Text("Pay Securely", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Divider(color = Color(0xFF1E1E2F), modifier = Modifier.padding(bottom = 12.dp))

        // 4. Grid of user's published videos (Sleek Instagram layout)
        Text(
            text = "My Published Videos (${myVideos.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (myVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF131326)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No videos published yet.\nGo to Upload to convert your video data!",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(myVideos) { video ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1.0f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1E1E34), Color(0xFF0F0F1A))
                                )
                            )
                            .clickable { viewModel.setTab("feed") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color(0xFFF77737),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = video.title,
                                color = Color.White,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            Text(
                                text = "🪙 ${video.convertedCoins.toInt()} Coins",
                                color = Color(0xFFFFB300),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}
