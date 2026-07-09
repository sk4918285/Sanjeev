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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LiveHelp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MastiViewModel
import com.example.ui.SupportMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AiSupportScreen(viewModel: MastiViewModel) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0 = User Support Chat, 1 = AI Owner Dashboard
    val supportMsgs by viewModel.supportMessages.collectAsState()
    val loggedInUser by viewModel.loggedInUser.collectAsState()

    var userQuestionText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F1E))
    ) {
        // TAB ROW FOR USER HELP vs OWNER PANEL
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color(0xFF16162C),
            contentColor = Color(0xFFF77737)
        ) {
            Tab(
                selected = (selectedSubTab == 0),
                onClick = { selectedSubTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SupportAgent, contentDescription = "Support")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Support Chat", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("subtab_user_support")
            )
            Tab(
                selected = (selectedSubTab == 1),
                onClick = { selectedSubTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = "Owner")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Owner Board", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("subtab_owner_board")
            )
        }

        if (selectedSubTab == 0) {
            // --- USER HELP CHAT INTERFACE ---
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C36))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB300)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Masti AI Help Desk", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Text("Ask us about Coin redemptions, UPI payouts, anti-bot rules, etc.", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                }

                // Chat Messages Feed
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    items(supportMsgs) { msg ->
                        val isAi = (msg.senderName == "Masti Support AI")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isAi) Color(0xFF22223C) else Color(0xFFF77737)
                                ),
                                border = if (isAi) androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFB300)) else null,
                                shape = RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (isAi) 0.dp else 12.dp,
                                    bottomEnd = if (isAi) 12.dp else 0.dp
                                ),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = msg.senderName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isAi) Color(0xFFFFB300) else Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = msg.text,
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp)),
                                        fontSize = 8.sp,
                                        color = Color.LightGray,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }

                // Message text input bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF16162C))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userQuestionText,
                        onValueChange = { userQuestionText = it },
                        placeholder = { Text("Ask Masti AI...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF22223B),
                            unfocusedContainerColor = Color(0xFF22223B)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("support_input_field")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (userQuestionText.isNotBlank()) {
                                viewModel.sendSupportMessage(userQuestionText.trim())
                                userQuestionText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFF77737)),
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("support_send_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send Help", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

        } else {
            // --- AI OWNER DASHBOARD / MANAGER CONTROLS ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                // Header Alert info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C30)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = "Shield", tint = Color(0xFFFFB300), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("AI Owner Assistant Status: Active", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text("Your content ecosystem is automatically scanned for Direct Nudity or fully sexual content. Infractions are auto-deleted.", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats rows
                Text("Ecosystem Biometrics & Anti-Bot Registry", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF16162C))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Verified Original Users", color = Color.Gray, fontSize = 10.sp)
                            Text("100%", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 2.dp))
                            Text("Biometric Scan Logged", color = Color.LightGray, fontSize = 9.sp)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF16162C))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Bot Clones Eliminated", color = Color.Gray, fontSize = 10.sp)
                            Text("${viewModel.aiBotDeletionSweepCount}", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 2.dp))
                            Text("AI Auto-Scans", color = Color.LightGray, fontSize = 9.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Control Switch items
                Text("AI Automation Safety Handles", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16162C))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Switch 1: Auto moderation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("AI Auto-Moderaion Filter", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Detects and blocks fully sexual or fully nude uploads instantly", color = Color.Gray, fontSize = 10.sp)
                            }
                            Switch(
                                checked = viewModel.aiAutomaticSafetyMode,
                                onCheckedChange = { viewModel.aiAutomaticSafetyMode = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFF77737))
                            )
                        }

                        Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))

                        // Trigger Bot Sweep Action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Anti-Bot AI Sweep", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Trigger a real-time sweep to scan for duplicate account logs", color = Color.Gray, fontSize = 10.sp)
                            }

                            Button(
                                onClick = { viewModel.aiTriggerBotCleanup() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77737)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("ai_sweep_button")
                            ) {
                                Text("Run Scan", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Guidelines reference
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF22223C))
                        .padding(12.dp)
                ) {
                    Row {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Masti AI is programmed under owner instruction to protect creative revenues, prevent fake follower bots, and guarantee payouts within 5 minutes. Your biometric parameters are secured inside local memory storage.",
                            color = Color.LightGray,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
