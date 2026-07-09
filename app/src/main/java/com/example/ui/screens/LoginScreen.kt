package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MastiViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun LoginScreen(viewModel: MastiViewModel) {
    // Gradient brush for modern visual styling
    val primaryGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF833AB4), // Deep Purple
            Color(0xFFF77737), // Coral
            Color(0xFFFFDC80)  // Golden Glow
        )
    )

    val cyberNeonGreen = Color(0xFF00FF66)
    val cyberNeonCyan = Color(0xFF00E5FF)
    val deepNavy = Color(0xFF0F0F1E)
    val cardBg = Color(0xFF16162C)

    // Onboarding stage state machine
    var onboardingStage by remember { mutableStateOf("DETAILS") } // "DETAILS", "FACESCAN", "OTP"
    
    // Face scanning internal steps (0: Ready, 1: Aligning, 2: Blink Liveness, 3: Smile Liveness, 4: Deep Scan & Hash Matching, 5: Success)
    var scanPhase by remember { mutableStateOf(0) }
    var terminalLogs by remember { mutableStateOf(listOf<String>()) }
    var botProbabilityScore by remember { mutableStateOf(1.0) } // Starts at 100% (unknown/bot-like) and drops to 0.01% as human actions verified

    // Sync with VM state changes (e.g., if OTP is successfully sent by VM, transition to OTP stage)
    LaunchedEffect(viewModel.isOtpSent) {
        if (viewModel.isOtpSent) {
            onboardingStage = "OTP"
        } else {
            // Reset to DETAILS if OTP cleared
            if (onboardingStage == "OTP") {
                onboardingStage = "DETAILS"
                scanPhase = 0
                botProbabilityScore = 1.0
                terminalLogs = emptyList()
            }
        }
    }

    // Interactive sweep laser animation for face scanning
    val infiniteTransition = rememberInfiniteTransition(label = "Laser")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserSweep"
    )

    // Pulse animation for scan boundaries
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    // Auto-advance scanning scripts for high realism
    LaunchedEffect(scanPhase) {
        when (scanPhase) {
            1 -> {
                terminalLogs = listOf(
                    "[SYS] Frontal optical sensor online.",
                    "[SYS] Searching for human face geometry bounds..."
                )
                delay(1200)
                terminalLogs = terminalLogs + "[OK] Human silhouette mapped in 2D."
                terminalLogs = terminalLogs + "[SYS] Running 3D depth field calculation..."
                delay(1000)
                terminalLogs = terminalLogs + "[OK] Depth field mapped. Initializing Liveness challenges."
                botProbabilityScore = 0.72
                delay(600)
                scanPhase = 2 // Advance to Blink challenge
            }
            4 -> {
                terminalLogs = terminalLogs + listOf(
                    "[SYS] Analyzing micro-facial muscle dynamics...",
                    "[DATA] Querying 1,024-node local mesh...",
                    "[ANTI-SPOOF] Checking for physical tissue light refraction...",
                    "[INFO] Running Adversarial Network Deepfake scan..."
                )
                delay(1500)
                terminalLogs = terminalLogs + "[PASSED] Deepfake presentation attack check: SAFE"
                terminalLogs = terminalLogs + "[SYS] Querying global biometric registry for clones..."
                delay(1200)
                terminalLogs = terminalLogs + "[PASSED] Uniqueness verified (0 database matches)."
                terminalLogs = terminalLogs + "[OK] Identity token finalized. Safe from bot-nets."
                botProbabilityScore = 0.01
                delay(500)
                scanPhase = 5 // Success!
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App Identity & Header Block
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(primaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Masti App Icon",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "masti time",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Text(
                        text = "CREATOR PORTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Process Indicator Step Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step 1: Details
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (onboardingStage == "DETAILS") Color(0xFFF77737) else Color(0xFF33334C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Step 1", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Profile", color = if (onboardingStage == "DETAILS") Color.White else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Divider Line
                Box(modifier = Modifier.height(2.dp).weight(0.8f).background(Color(0xFF33334C)))

                // Step 2: Biometric Facial Check
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (onboardingStage == "FACESCAN") cyberNeonCyan else Color(0xFF33334C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Face, contentDescription = "Step 2", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Face Scan", color = if (onboardingStage == "FACESCAN") Color.White else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Divider Line
                Box(modifier = Modifier.height(2.dp).weight(0.8f).background(Color(0xFF33334C)))

                // Step 3: Secure OTP
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (onboardingStage == "OTP") Color(0xFFFFDC80) else Color(0xFF33334C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = "Step 3", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("OTP Secure", color = if (onboardingStage == "OTP") Color.White else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stage Switching Container
            when (onboardingStage) {
                "DETAILS" -> {
                    // Profile Setup Screen
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Start Secure Onboarding 🛡️",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Masti Time enforces a clean, safe, and authentic community. To stop bot-nets, artificial view farms, and duplicate clones, we verify every single profile's real physical tissue via a mandatory 3D Facial Recognition scan.",
                                fontSize = 12.sp,
                                color = Color.LightGray,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // DISPLAY NAME
                            Text(
                                text = "Choose Public Profile Name:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = viewModel.usernameInput,
                                onValueChange = { viewModel.usernameInput = it },
                                placeholder = { Text("e.g. mastigamer_99", color = Color.DarkGray) },
                                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "User icon", tint = Color.LightGray) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFF77737),
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // PHONE/EMAIL
                            Text(
                                text = "Enter Mobile Number or Email:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = viewModel.phoneOrEmailInput,
                                onValueChange = { viewModel.phoneOrEmailInput = it },
                                placeholder = { Text("e.g. +91 9999988888 or write@masti.com", color = Color.DarkGray) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (viewModel.phoneOrEmailInput.contains("@")) Icons.Default.Email else Icons.Default.Phone,
                                        contentDescription = "Contact icon",
                                        tint = Color(0xFFF77737)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFF77737),
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_input_field")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // AGE SELECTION
                            Text(
                                text = "Confirm Your Age Category:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (viewModel.ageCategoryInput == "Adult (18+)") Color(0x33F77737) else Color(0xFF1F1F35))
                                        .clickable { viewModel.ageCategoryInput = "Adult (18+)" }
                                        .padding(10.dp)
                                ) {
                                    RadioButton(
                                        selected = (viewModel.ageCategoryInput == "Adult (18+)"),
                                        onClick = { viewModel.ageCategoryInput = "Adult (18+)" },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFF77737))
                                    )
                                    Text("Adult (18+)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (viewModel.ageCategoryInput == "Minor (Under 18)") Color(0x33F77737) else Color(0xFF1F1F35))
                                        .clickable { viewModel.ageCategoryInput = "Minor (Under 18)" }
                                        .padding(10.dp)
                                ) {
                                    RadioButton(
                                        selected = (viewModel.ageCategoryInput == "Minor (Under 18)"),
                                        onClick = { viewModel.ageCategoryInput = "Minor (Under 18)" },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFF77737))
                                    )
                                    Text("Minor (<18)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // ERROR CONTAINER
                            viewModel.loginError?.let { err ->
                                Text(
                                    text = err,
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }

                            // CONTINUE BUTTON
                            Button(
                                onClick = {
                                    if (viewModel.usernameInput.trim().isEmpty() || viewModel.phoneOrEmailInput.trim().isEmpty()) {
                                        viewModel.loginError = "Please enter both display name and phone/email details."
                                    } else {
                                        viewModel.loginError = null
                                        viewModel.verificationMethodInput = "Face Scan"
                                        onboardingStage = "FACESCAN"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77737)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Face, contentDescription = "Proceed icon")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Continue to Facial Biometrics", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }

                "FACESCAN" -> {
                    // Biometric Facial recognition screen
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Anti-Bot 3D Face Scanner",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = cyberNeonCyan,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Protects your payouts & blocks sybil duplicate profiles",
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // ADVANCED GRAPHICAL CAMERA SCANNING FRAME
                            Box(
                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(Color(0xFF0B0B14))
                                    .border(
                                        width = 2.dp,
                                        color = if (scanPhase == 5) cyberNeonGreen else cyberNeonCyan,
                                        shape = RoundedCornerShape(32.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Draw face mesh representation inside Canvas
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val width = size.width
                                    val height = size.height

                                    // Draw glowing circular scanning grid background
                                    drawCircle(
                                        color = Color(0x1100E5FF),
                                        radius = width * 0.4f,
                                        center = Offset(width / 2, height / 2)
                                    )

                                    // Draw Face Landmark Nodes & Lines if scanner active
                                    if (scanPhase in 1..4) {
                                        val points = listOf(
                                            Offset(width * 0.5f, height * 0.30f), // forehead
                                            Offset(width * 0.35f, height * 0.45f), // left eye
                                            Offset(width * 0.65f, height * 0.45f), // right eye
                                            Offset(width * 0.5f, height * 0.55f), // nose bridge
                                            Offset(width * 0.5f, height * 0.65f), // nose tip
                                            Offset(width * 0.38f, height * 0.72f), // left mouth corner
                                            Offset(width * 0.62f, height * 0.72f), // right mouth corner
                                            Offset(width * 0.5f, height * 0.76f), // chin center
                                            Offset(width * 0.22f, height * 0.55f), // left cheek
                                            Offset(width * 0.78f, height * 0.55f)  // right cheek
                                        )

                                        // Connect facial points with light lines mimicking mesh
                                        for (i in points.indices) {
                                            for (j in i + 1 until points.size) {
                                                // Only connect nearby points to look like a real mesh map
                                                val distSq = (points[i].x - points[j].x) * (points[i].x - points[j].x) + 
                                                             (points[i].y - points[j].y) * (points[i].y - points[j].y)
                                                if (distSq < (width * 0.35f) * (width * 0.35f)) {
                                                    drawLine(
                                                        color = Color(0x2200E5FF),
                                                        start = points[i],
                                                        end = points[j],
                                                        strokeWidth = 1.dp.toPx()
                                                    )
                                                }
                                            }
                                        }

                                        // Draw landmark dots
                                        points.forEach { pt ->
                                            drawCircle(
                                                color = cyberNeonCyan,
                                                radius = 3.5.dp.toPx(),
                                                center = pt
                                            )
                                        }
                                    }

                                    // Laser sweeping scan bar
                                    if (scanPhase in 1..4) {
                                        val laserY = height * laserProgress
                                        drawLine(
                                            color = cyberNeonCyan,
                                            start = Offset(0f, laserY),
                                            end = Offset(width, laserY),
                                            strokeWidth = 2.dp.toPx()
                                        )
                                    }
                                }

                                // Interactive face scanner core indicator icon
                                Box(
                                    modifier = Modifier
                                        .size(170.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.dp,
                                            color = if (scanPhase == 5) cyberNeonGreen.copy(alpha = 0.6f) else cyberNeonCyan.copy(alpha = 0.4f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (scanPhase) {
                                            0 -> Icons.Default.Face
                                            1 -> Icons.Default.Face
                                            2 -> Icons.Default.RemoveRedEye // blink
                                            3 -> Icons.Default.SentimentSatisfiedAlt // smile
                                            4 -> Icons.Default.Cached // matching
                                            5 -> Icons.Default.CheckCircle // success
                                            else -> Icons.Default.Face
                                        },
                                        contentDescription = "Face tracking state",
                                        tint = when (scanPhase) {
                                            5 -> cyberNeonGreen
                                            2, 3 -> Color(0xFFFFB300)
                                            else -> cyberNeonCyan
                                        },
                                        modifier = Modifier
                                            .size(80.dp)
                                            .align(Alignment.Center)
                                    )
                                }

                                // Scanning live text ticker overlays
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = when (scanPhase) {
                                            0 -> "READY TO SCAN"
                                            1 -> "CENTERING FACE"
                                            2 -> "ACTION: BLINK"
                                            3 -> "ACTION: SMILE"
                                            4 -> "DO NOT MOVE"
                                            5 -> "VERIFIED"
                                            else -> "SCANNER ON"
                                        },
                                        color = when (scanPhase) {
                                            5 -> cyberNeonGreen
                                            2, 3 -> Color(0xFFFFB300)
                                            else -> cyberNeonCyan
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .background(Color(0xFF0F0F1E), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // DYNAMIC USER INSTRUCTIONS
                            Text(
                                text = when (scanPhase) {
                                    0 -> "Step 1: Start Calibration"
                                    1 -> "Step 2: Center Face"
                                    2 -> "Step 3: Blink Twice"
                                    3 -> "Step 4: Smile widely"
                                    4 -> "Step 5: Matching Neural Hash..."
                                    5 -> "Scan Complete!"
                                    else -> "Facial Scan"
                                        },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (scanPhase) {
                                    0 -> "Align your camera and stand in a brightly lit room."
                                    1 -> "Searching depth anchors. Keep your eyes centered."
                                    2 -> "We check pupil contraction to verify physical, living human eyes."
                                    3 -> "Checking micro-expression timing values against standard static photo masks."
                                    4 -> "Calculating unique biome-key. Querying identity servers..."
                                    5 -> "Biometric profile generated successfully! Zero duplicate threat detected."
                                    else -> "Secure scanning"
                                },
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // BOT PROBABILITY & HUMAN AUTHENTICITY METER
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E38)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Anti-Bot Authenticity Meter:", fontSize = 11.sp, color = Color.LightGray, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                text = if (scanPhase == 5) "TRUST LEVEL: 100%" else "Trust: ${String.format("%.0f", (1.0 - botProbabilityScore) * 100)}%",
                                                fontSize = 11.sp,
                                                color = if (scanPhase == 5) cyberNeonGreen else Color(0xFFFFB300),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = (1.0f - botProbabilityScore.toFloat()).coerceIn(0f, 1f),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(CircleShape),
                                            color = if (scanPhase == 5) cyberNeonGreen else cyberNeonCyan,
                                            trackColor = Color.DarkGray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Bot Risk Factor: ${String.format("%.2f", botProbabilityScore * 100)}% (Target threshold: <0.1%)",
                                            fontSize = 9.sp,
                                            color = if (botProbabilityScore < 0.05) cyberNeonGreen else Color.Gray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // TELEMETRY SCROLLING CONSOLE OUTPUT (REAL-TIME ALGORITHMIC VERBAL READOUT)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF070711)),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF1C1C30))
                            ) {
                                Box(modifier = Modifier.padding(10.dp)) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                        modifier = Modifier.verticalScroll(rememberScrollState())
                                    ) {
                                        if (terminalLogs.isEmpty()) {
                                            Text("> Scanner ready. Awaiting secure ignition pipeline...", color = Color.Gray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        } else {
                                            terminalLogs.forEach { log ->
                                                Text("> $log", color = if (log.contains("[PASSED]") || log.contains("[OK]")) cyberNeonGreen else cyberNeonCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // INTERACTIVE ACTION BUTTONS
                            when (scanPhase) {
                                0 -> {
                                    Button(
                                        onClick = { scanPhase = 1 },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = cyberNeonCyan),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Ignite Secure Face Scan", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                    }
                                }
                                2 -> {
                                    // Eye blink simulation button
                                    Button(
                                        onClick = {
                                            terminalLogs = terminalLogs + "[LIVENESS] Pupil contract matched."
                                            terminalLogs = terminalLogs + "[LIVENESS] Dual-blink sequence positive."
                                            botProbabilityScore = 0.38
                                            scanPhase = 3
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.RemoveRedEye, contentDescription = "Blink", tint = Color.Black)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Simulate Eye Blink Action 😉", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                    }
                                }
                                3 -> {
                                    // Smile simulation button
                                    Button(
                                        onClick = {
                                            terminalLogs = terminalLogs + "[LIVENESS] Smile micro-expression verified."
                                            terminalLogs = terminalLogs + "[LIVENESS] Heatmap alignment confirmed."
                                            botProbabilityScore = 0.12
                                            scanPhase = 4
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.SentimentSatisfiedAlt, contentDescription = "Smile", tint = Color.Black)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Simulate Smile Action 😊", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                    }
                                }
                                5 -> {
                                    // Verification succeeded. Click to proceed to OTP stage
                                    Button(
                                        onClick = {
                                            viewModel.sendOtp()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("send_otp_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = cyberNeonGreen),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Complete Face scan", tint = Color.Black)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Finalize Biometrics & Request OTP", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                    }
                                }
                                else -> {
                                    // In transition (1 & 4)
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        CircularProgressIndicator(color = cyberNeonCyan, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Evaluating live tissues...", color = cyberNeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Reset button in case of failure or scan restart
                            if (scanPhase > 0) {
                                Text(
                                    text = "Restart Face Scanner",
                                    color = Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clickable {
                                            scanPhase = 0
                                            terminalLogs = emptyList()
                                            botProbabilityScore = 1.0
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }

                "OTP" -> {
                    // Safe verification / OTP validation card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "OTP Authentication",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Verify your contact access token to finalize secure identity registration.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Verified banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x1A00FF66))
                                    .border(1.dp, cyberNeonGreen, RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Passed", tint = cyberNeonGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "3D FACE VERIFICATION: COMPLETED",
                                        color = cyberNeonGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Simulated OTP Notification box
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0x33FFB300)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock key", tint = Color(0xFFFFB300))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Simulated Secure SMS/Email Carrier:", fontSize = 10.sp, color = Color.LightGray)
                                        Text(
                                            text = "Verification Token OTP: ${viewModel.simulatedOtpCode}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Enter Received OTP Code:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = viewModel.otpInput,
                                onValueChange = { viewModel.otpInput = it },
                                placeholder = { Text("4-digit code", color = Color.DarkGray) },
                                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "OTP Icon", tint = Color(0xFFFFDC80)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFDC80),
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input_field")
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // LOGIN ERROR CONTAINER
                            viewModel.loginError?.let { err ->
                                Text(
                                    text = err,
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }

                            Button(
                                onClick = { viewModel.verifyOtp() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("verify_otp_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF833AB4)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Complete Verification & Login", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Allow going back to adjust scan in case of timeout
                            Text(
                                text = "Go Back to Face Scanner",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.isOtpSent = false
                                        onboardingStage = "FACESCAN"
                                        scanPhase = 0
                                        botProbabilityScore = 1.0
                                        terminalLogs = emptyList()
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lower branding disclaimer on anti-bot safety
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Shield seal",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Anti-Bot Verification Node v3.4 Active",
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
