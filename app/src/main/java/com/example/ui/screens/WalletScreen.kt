package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MastiViewModel
import com.example.ui.WithdrawalTx
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(viewModel: MastiViewModel) {
    val loggedInUser by viewModel.loggedInUser.collectAsState()
    val withdrawalHistory by viewModel.withdrawalHistory.collectAsState()
    val scrollState = rememberScrollState()

    var withdrawAmountInput by remember { mutableStateOf("") }

    // Automatically clear feedback notifications
    LaunchedEffect(viewModel.walletMessage) {
        if (viewModel.walletMessage != null) {
            delay(5000)
            viewModel.walletMessage = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A12))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Page Title
        Text(
            text = "Masti Creator Hub & Wallet",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Status Messages Banner
        viewModel.walletMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x334CAF50)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Green),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = msg,
                    color = Color.Green,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        val user = loggedInUser
        if (user == null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131326)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Please log in to access your Creator Hub and view balance.",
                    color = Color.LightGray,
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val isMonetized = user.followersCount >= 500 || user.isMonetized
            val followersProgress = (user.followersCount.toFloat() / 500f).coerceAtMost(1f)

            // 1. Monetization Eligibility Card (THE 500 SUBSCRIBERS MILESTONE)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131326)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monetization Eligibility Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isMonetized) Color(0x334CAF50) else Color(0x33F44336))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isMonetized) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isMonetized) Color.Green else Color.Red,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isMonetized) "ELIGIBLE" else "LOCKED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMonetized) Color.Green else Color.Red
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Requirement: Reach 500 genuine followers to unlock UPI & bank account withdrawals. Keep bots out with our AI scanning!",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress metrics row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.People, contentDescription = "Subs", tint = Color(0xFFF77737), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Your Followers (Original)", color = Color.LightGray, fontSize = 12.sp)
                        }
                        Text("${user.followersCount} / 500", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Milestone progress bar
                    LinearProgressIndicator(
                        progress = { followersProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFF77737),
                        trackColor = Color(0xFF2E2E4F),
                    )

                    // THE SIMULATOR BOOSTER TOOL (AWESOME TESTING COMPONENT)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.boostMyFollowers() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33F77737)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF77737)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("boost_subscribers_button")
                    ) {
                        Icon(imageVector = Icons.Default.OfflineBolt, contentDescription = "Boost", tint = Color(0xFFF77737))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simulate +45 Followers Boost", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }

            // 2. Premium Gold Wallet Card (10 COINS = ₹10 VALUE PROPOSITION)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFB300), // Vibrant yellow gold
                                    Color(0xFFE5A93B), // Soft metallic gold
                                    Color(0xFFFFA000)  // Deep orange gold
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GOLD COINS BALANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = Color.Black.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🪙 ${String.format("%.1f", user.coinsBalance)} Coins",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 10 Coins = 10 Rupees pricing (1 Coin = 1 Rupee value explanation)
                    Text(
                        text = "Value: ₹${String.format("%.1f", user.coinsBalance)} (Exchange: 10 Coins = ₹10)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(color = Color.Black.copy(alpha = 0.15f))

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Yellow Tick Creator Status", fontSize = 10.sp, color = Color.Black.copy(alpha = 0.6f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (user.hasYellowTick) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "Active", tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ACTIVE (5X VIEW BOOST)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                                } else {
                                    Text("INACTIVE (1X VIEWS)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black.copy(alpha = 0.8f))
                                }
                            }
                        }

                        // Withdraw Trigger Button (which prompts or opens the withdraw container)
                        Button(
                            onClick = { viewModel.showWithdrawDialog = !viewModel.showWithdrawDialog },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            enabled = isMonetized,
                            modifier = Modifier.testTag("withdraw_trigger_button")
                        ) {
                            Text(
                                text = "Withdraw Money",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // 3. Dynamic Withdraw Fields Dialog panel
            AnimatedVisibility(visible = viewModel.showWithdrawDialog && isMonetized) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16162C)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Redeem Coins to Bank/UPI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Instant 5-minute automated processing by Masti AI Owner.",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Method selection Chips
                        Text("Select Payout Method:", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Google Pay", "PhonePe", "Direct Bank Account").forEach { method ->
                                val selected = (viewModel.withdrawMethod == method)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) Color(0xFFF77737) else Color(0xFF1E1E34))
                                        .clickable { viewModel.withdrawMethod = method }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (method == "Direct Bank Account") "Bank A/C" else method,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Coins count input
                        OutlinedTextField(
                            value = withdrawAmountInput,
                            onValueChange = { withdrawAmountInput = it },
                            label = { Text("Enter Coins to Withdraw") },
                            placeholder = { Text("e.g. 100") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Paid, contentDescription = "coins", tint = Color(0xFFFFB300)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFB300),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_coins_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dynamic Input details based on selected withdrawal method
                        if (viewModel.withdrawMethod == "Direct Bank Account") {
                            // Bank Details Inputs
                            OutlinedTextField(
                                value = viewModel.withdrawDetailsInput,
                                onValueChange = { viewModel.withdrawDetailsInput = it },
                                label = { Text("Bank Account Number") },
                                placeholder = { Text("e.g. 34098127339") },
                                leadingIcon = { Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "bank", tint = Color(0xFFF77737)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFF77737),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = viewModel.withdrawBankIfsc,
                                onValueChange = { viewModel.withdrawBankIfsc = it.uppercase() },
                                label = { Text("Bank IFSC Code") },
                                placeholder = { Text("e.g. SBIN0001824") },
                                leadingIcon = { Icon(imageVector = Icons.Default.CreditCard, contentDescription = "ifsc", tint = Color(0xFFF77737)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFF77737),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            // UPI Details Input
                            OutlinedTextField(
                                value = viewModel.withdrawDetailsInput,
                                onValueChange = { viewModel.withdrawDetailsInput = it },
                                label = { Text("${viewModel.withdrawMethod} UPI ID Address") },
                                placeholder = { Text("e.g. name@okhdfcbank or 9999988888@ybl") },
                                leadingIcon = { Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "upi", tint = Color(0xFFF77737)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFF77737),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("withdraw_upi_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val coins = withdrawAmountInput.toDoubleOrNull()
                                if (coins == null || coins <= 0) {
                                    viewModel.walletMessage = "Enter a valid Coin amount."
                                } else if (viewModel.withdrawDetailsInput.isBlank()) {
                                    viewModel.walletMessage = "Fill in your UPI / Bank details first."
                                } else if (viewModel.withdrawMethod == "Direct Bank Account" && viewModel.withdrawBankIfsc.isBlank()) {
                                    viewModel.walletMessage = "IFSC code is required for Bank transfers."
                                } else {
                                    viewModel.withdrawCoins(coins)
                                    withdrawAmountInput = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_withdrawal_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Confirm Withdrawal", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                        }
                    }
                }
            }

            // 4. Withdrawal Transaction Log History
            if (withdrawalHistory.isNotEmpty()) {
                Text(
                    text = "Withdrawal Transaction History Logs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

                withdrawalHistory.forEach { tx ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF16162C))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x334CAF50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "success", tint = Color.Green, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Tx ID: #${tx.id}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("To: ${tx.paymentDetails}", color = Color.Gray, fontSize = 10.sp)
                                    Text(
                                        text = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(tx.timestamp)),
                                        fontSize = 9.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("- ₹${String.format("%.1f", tx.amountCoins)}", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(tx.status, color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 5. Creator Stats Summary Card (Hiding splits securely)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131326)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Creator Income Breakdown Rules",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bandwidth Upload Coins", color = Color.LightGray, fontSize = 13.sp)
                        Text("1 MB = 1 Coin (Instant)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Interactive View Earnings", color = Color.LightGray, fontSize = 13.sp)
                        Text("1 View = 1 Coin split (Automated)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1C1C30))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "note",
                                tint = Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Earnings are generated automatically when other users watch your content. Your views and subscribers metrics are safe and fully verified.",
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
