package com.qualityapplied.vitacare.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qualityapplied.vitacare.R
import com.qualityapplied.vitacare.data.NotificationCenter
import com.qualityapplied.vitacare.ui.components.LanguageSwitcher
import com.qualityapplied.vitacare.ui.screens.AiAssistantScreen
import com.qualityapplied.vitacare.ui.screens.DashboardScreen
import com.qualityapplied.vitacare.ui.screens.DeveloperAppDetailsDialog
import com.qualityapplied.vitacare.ui.screens.DoctorScreen
import com.qualityapplied.vitacare.ui.screens.LabReportVaultScreen
import com.qualityapplied.vitacare.ui.screens.LoginScreen
import com.qualityapplied.vitacare.ui.screens.MetricsScreen
import com.qualityapplied.vitacare.ui.screens.NotificationsScreen
import com.qualityapplied.vitacare.ui.screens.ProfileScreen
import com.qualityapplied.vitacare.ui.screens.RemindersScreen
import com.qualityapplied.vitacare.ui.screens.RegisterScreen
import com.qualityapplied.vitacare.ui.theme.Emerald600
import com.qualityapplied.vitacare.ui.theme.Rose500
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate50
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal50
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AppRoot() {
    val vm: AppViewModel = viewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showNotifications by rememberSaveable { mutableStateOf(false) }
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var showAppInfoDialog by rememberSaveable { mutableStateOf(false) }
    val loggedIn = vm.user != null

    LaunchedEffect(Unit) {
        delay(2500) // SuwaGo Splash Screen for 2.5 seconds
        showSplash = false
    }

    if (showSplash) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.splash_bg),
                contentDescription = "SuwaGo Splash Screen",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
        return
    }

    // Pick up notifications that arrived while the app was in the background
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) NotificationCenter.load(context)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(loggedIn) { if (!loggedIn) showNotifications = false }

    // Logged in (saved session) -> straight to main flow with Scaffold. Logged out -> header + auth flow.
    if (!loggedIn) {
        Column(modifier = Modifier.fillMaxSize().background(Slate50)) {
            SuwagoModernHeader(
                showBell = false,
                unread = 0,
                onBell = {},
                onInfoClick = { showAppInfoDialog = true },
                userName = null,
            )
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AuthFlow(vm)
            }
        }
    } else {
        MainFlow(
            vm = vm,
            showNotifications = showNotifications,
            onShowNotifications = { showNotifications = it },
            onInfoClick = { showAppInfoDialog = true },
        )
    }

    if (showAppInfoDialog) {
        DeveloperAppDetailsDialog(onDismiss = { showAppInfoDialog = false })
    }
}

@Composable
fun SuwagoLogo(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 22.sp,
    showTagline: Boolean = true,
    showCircleLogoAbove: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (showCircleLogoAbove) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        if (showCircleLogoAbove) {
            Image(
                painter = painterResource(R.drawable.vitacare_logo),
                contentDescription = "Suwago Logo",
                modifier = Modifier.size((fontSize.value * 1.6).dp).clip(CircleShape),
            )
            Spacer(Modifier.height(2.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "suwa",
                        fontSize = fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0946B2),
                        letterSpacing = (-0.5).sp,
                    )
                    Text(
                        text = "g",
                        fontSize = fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFE50914),
                        letterSpacing = (-0.5).sp,
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 1.dp)
                            .size((fontSize.value * 0.95).dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE50914)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size((fontSize.value * 0.72).dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "🩺",
                                fontSize = (fontSize.value * 0.48).sp,
                            )
                        }
                    }
                }
                Text(
                    text = "🍃",
                    fontSize = (fontSize.value * 0.45).sp,
                    modifier = Modifier.offset(x = (fontSize.value * 1.35).dp, y = (-fontSize.value * 0.35).dp),
                )
            }
        }
        if (showTagline) {
            Spacer(Modifier.height(1.dp))
            Text(
                text = "නිරෝගී ජීවිතයකට",
                fontSize = (fontSize.value * 0.45).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                letterSpacing = 0.2.sp,
            )
        }
    }
}

val CurvedHeaderShape = GenericShape { size, _ ->
    val curveHeight = 32f
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height - curveHeight)
    quadraticBezierTo(
        size.width / 2f,
        size.height + curveHeight,
        0f,
        size.height - curveHeight,
    )
    close()
}

@Composable
fun SuwagoModernHeader(
    showBell: Boolean,
    unread: Int,
    onBell: () -> Unit,
    onInfoClick: () -> Unit,
    userName: String? = null,
) {
    val topBarGradient = remember {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFFFB703), // Vibrant Amber Gold
                Color(0xFF10B981), // Bright Emerald Green
                Color(0xFF00897B), // Deep Vibrant Teal
            ),
        )
    }

    Surface(
        color = Color.Transparent,
        shape = CurvedHeaderShape,
        shadowElevation = 6.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(topBarGradient)
                .statusBarsPadding()
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.vitacare_logo),
                    contentDescription = "Suwago Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                )
                Spacer(Modifier.width(8.dp))
                SuwagoLogo(
                    fontSize = 22.sp,
                    showTagline = false,
                    showCircleLogoAbove = false,
                )
                Spacer(Modifier.weight(1f))

                // Info Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable(onClick = onInfoClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("ℹ️", fontSize = 18.sp)
                }
                Spacer(Modifier.width(6.dp))

                if (showBell) {
                    // Bell with unread badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                            .clickable(onClick = onBell),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("🔔", fontSize = 18.sp)
                        if (unread > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Rose500),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    if (unread > 9) "9+" else unread.toString(),
                                    fontSize = 8.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(6.dp))
                }
                LanguageSwitcher()
            }
        }
    }
}

@Composable
private fun Header(showBell: Boolean, unread: Int, onBell: () -> Unit, userName: String?) {
    SuwagoModernHeader(
        showBell = showBell,
        unread = unread,
        onBell = onBell,
        onInfoClick = {},
        userName = userName,
    )
}

@Composable
private fun AuthFlow(vm: AppViewModel) {
    var showRegister by rememberSaveable { mutableStateOf(false) }
    if (showRegister) {
        RegisterScreen(vm, onGoLogin = { showRegister = false })
    } else {
        LoginScreen(vm, onGoRegister = { showRegister = true })
    }
}

private data class NavItem(val icon: String, val labelKey: String)

private val NAV_ITEMS = listOf(
    NavItem("🏠", "nav.home"),
    NavItem("🩺", "nav.doctors"),
    NavItem("🤖", "nav.ai"),
    NavItem("👤", "nav.profile"),
)

@Composable
private fun MainFlow(
    vm: AppViewModel,
    showNotifications: Boolean,
    onShowNotifications: (Boolean) -> Unit,
    onInfoClick: () -> Unit,
) {
    var tab by rememberSaveable(vm.user?.id) { mutableIntStateOf(vm.startTab) }
    var doctorSpec by rememberSaveable(vm.user?.id) { mutableStateOf<String?>(null) }
    var activeOverlay by rememberSaveable(vm.user?.id) { mutableStateOf<String?>(null) } // "metrics", "reminders", "vault"
    var confirmLogout by remember { mutableStateOf(false) }

    BackHandler(enabled = (tab != 0 || activeOverlay != null) && !showNotifications) {
        if (activeOverlay != null) {
            activeOverlay = null
        } else {
            tab = 0
        }
    }

    Scaffold(
        topBar = {
            SuwagoModernHeader(
                showBell = true,
                unread = NotificationCenter.unread,
                onBell = { onShowNotifications(true) },
                onInfoClick = onInfoClick,
                userName = vm.user?.name,
            )
        },
        bottomBar = {
            val navSystemBarGradient = remember {
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFFB703), // Vibrant Amber Gold
                        Color(0xFF10B981), // Bright Emerald Green
                        Color(0xFF00897B), // Deep Vibrant Teal
                    ),
                )
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                // Icon Buttons Bar with Solid Green Background
                Surface(
                    color = Color(0xFF00897B),
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NAV_ITEMS.forEachIndexed { index, item ->
                            BottomItem(
                                index = index,
                                icon = item.icon,
                                label = tr(item.labelKey),
                                active = tab == index && !showNotifications && activeOverlay == null,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    activeOverlay = null
                                    if (index == 1) doctorSpec = null
                                    onShowNotifications(false)
                                    tab = index
                                },
                            )
                        }
                    }
                }

                // Dedicated Bright Gradient Scrim for ONLY the System Navigation Bar / Back Button Section Below
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsBottomHeight(WindowInsets.navigationBars)
                        .background(navSystemBarGradient),
                )
            }
        },
        containerColor = Slate50,
    ) { innerPadding ->
        // Apply Scaffold innerPadding to handle top bar, bottom bar, and system insets gracefully
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (showNotifications) {
                NotificationsScreen(onBack = { onShowNotifications(false) })
            } else if (activeOverlay != null) {
                when (activeOverlay) {
                    "metrics" -> MetricsScreen(vm, onFindDoctor = { spec ->
                        doctorSpec = spec
                        activeOverlay = null
                        tab = 1
                    })
                    "reminders" -> RemindersScreen()
                    "vault" -> LabReportVaultScreen()
                }
            } else {
                when (tab) {
                    0 -> DashboardScreen(
                        vm = vm,
                        onOpenMetrics = { activeOverlay = "metrics" },
                        onOpenReminders = { activeOverlay = "reminders" },
                        onOpenVault = { activeOverlay = "vault" },
                    )
                    1 -> DoctorScreen(doctorSpec)
                    2 -> AiAssistantScreen(vm)
                    else -> ProfileScreen(
                        vm,
                        onOpenNotifications = { onShowNotifications(true) },
                        onLogout = { confirmLogout = true },
                    )
                }
            }
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text(tr("nav.logout")) },
            text = { Text(tr("nav.logout.confirm")) },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    vm.logout()
                }) { Text(tr("common.yes")) }
            },
            dismissButton = {
                TextButton(onClick = { confirmLogout = false }) { Text(tr("common.cancel")) }
            },
        )
    }
}

@Composable
private fun BottomItem(index: Int, icon: String, label: String, active: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) Color.White.copy(alpha = 0.95f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (index == 3 || icon == "👤") {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = if (active) Color(0xFF00897B) else Color.White,
            )
        } else {
            Text(icon, fontSize = 18.sp)
        }
        Spacer(Modifier.height(1.dp))
        Text(
            label,
            fontSize = 10.5.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
            color = if (active) Color(0xFF00897B) else Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
