package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MastiViewModel
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.UploadScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.LiveScreen
import com.example.ui.screens.AiSupportScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    
    private val viewModel: MastiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val loggedInUser by viewModel.loggedInUser.collectAsState()
                
                if (loggedInUser == null) {
                    LoginScreen(viewModel = viewModel)
                } else {
                    MastiAppScaffold(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MastiAppScaffold(viewModel: MastiViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF131326),
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("masti_bottom_navigation_bar"),
                tonalElevation = 8.dp
            ) {
                // 1. Home Feed Tab
                NavigationBarItem(
                    selected = (currentTab == "feed"),
                    onClick = { viewModel.setTab("feed") },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Feed") },
                    label = { Text("Feed", color = Color.White, fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF77737),
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color(0x22F77737)
                    ),
                    modifier = Modifier.testTag("tab_feed")
                )

                // 2. Chat Channel Tab
                NavigationBarItem(
                    selected = (currentTab == "chats"),
                    onClick = { viewModel.setTab("chats") },
                    icon = { Icon(imageVector = Icons.Default.Chat, contentDescription = "Chats") },
                    label = { Text("Chats", color = Color.White, fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF77737),
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color(0x22F77737)
                    ),
                    modifier = Modifier.testTag("tab_chats")
                )

                // 3. Live Broadcast Tab
                NavigationBarItem(
                    selected = (currentTab == "live"),
                    onClick = { viewModel.setTab("live") },
                    icon = { Icon(imageVector = Icons.Default.Sensors, contentDescription = "Live") },
                    label = { Text("Live", color = Color.White, fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF77737),
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color(0x22F77737)
                    ),
                    modifier = Modifier.testTag("tab_live")
                )

                // 4. Upload Content Tab
                NavigationBarItem(
                    selected = (currentTab == "upload"),
                    onClick = { viewModel.setTab("upload") },
                    icon = { Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Upload") },
                    label = { Text("Upload", color = Color.White, fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF77737),
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color(0x22F77737)
                    ),
                    modifier = Modifier.testTag("tab_upload")
                )

                // 5. Wallet / Payouts Tab
                NavigationBarItem(
                    selected = (currentTab == "wallet"),
                    onClick = { viewModel.setTab("wallet") },
                    icon = { Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                    label = { Text("Wallet", color = Color.White, fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF77737),
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color(0x22F77737)
                    ),
                    modifier = Modifier.testTag("tab_wallet")
                )

                // 6. AI Support Tab
                NavigationBarItem(
                    selected = (currentTab == "ai_support"),
                    onClick = { viewModel.setTab("ai_support") },
                    icon = { Icon(imageVector = Icons.Default.SupportAgent, contentDescription = "AI Help") },
                    label = { Text("AI Help", color = Color.White, fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF77737),
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color(0x22F77737)
                    ),
                    modifier = Modifier.testTag("tab_ai_support")
                )

                // 7. Profile Tab
                NavigationBarItem(
                    selected = (currentTab == "profile"),
                    onClick = { viewModel.setTab("profile") },
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", color = Color.White, fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF77737),
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color(0x22F77737)
                    ),
                    modifier = Modifier.testTag("tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                "feed" -> FeedScreen(viewModel = viewModel)
                "chats" -> ChatsScreen(viewModel = viewModel)
                "live" -> LiveScreen(viewModel = viewModel)
                "upload" -> UploadScreen(viewModel = viewModel)
                "wallet" -> WalletScreen(viewModel = viewModel)
                "ai_support" -> AiSupportScreen(viewModel = viewModel)
                "profile" -> ProfileScreen(viewModel = viewModel)
            }
        }
    }
}
