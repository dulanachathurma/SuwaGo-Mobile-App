package com.qualityapplied.vitacare.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.qualityapplied.vitacare.R
import com.qualityapplied.vitacare.ui.SuwagoLogo
import com.qualityapplied.vitacare.ui.theme.Teal100
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700

const val DEV_NAME = "Dulana Chathurma"
const val DEV_PRIMARY_SUBTITLE = "AI-Integrated Full-Stack & UI/UX Developer"
const val DEV_SECONDARY_SUBTITLE = "BSc (Hons) Software Engineering Undergraduate @ University of Kelaniya"

private fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (_: ActivityNotFoundException) {
    }
}

private fun openEmail(context: Context, email: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")))
    } catch (_: ActivityNotFoundException) {
    }
}

/**
 * Developer & App Info Dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperInfoDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(16.dp),
        content = {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 10.dp,
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Header bar with Suwago App logo & Version badge + Close (X) icon button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.vitacare_logo),
                                contentDescription = "Suwago Logo",
                                modifier = Modifier.size(28.dp).clip(CircleShape),
                            )
                            Spacer(Modifier.width(8.dp))
                            SuwagoLogo(fontSize = 18.sp, showTagline = false, showCircleLogoAbove = false)
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Teal100,
                            ) {
                                Text(
                                    text = APP_VERSION,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Teal700,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                )
                            }
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Text(
                                "✕",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Developer Card / Inner Container (24.dp rounded corners)
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(R.drawable.dev_photo),
                                    contentDescription = DEV_NAME,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(84.dp)
                                        .clip(CircleShape)
                                        .border(2.5.dp, Teal600, CircleShape),
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = DEV_NAME,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = DEV_PRIMARY_SUBTITLE,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Teal700,
                                textAlign = TextAlign.Center,
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = DEV_SECONDARY_SUBTITLE,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp,
                            )

                            Spacer(Modifier.height(18.dp))

                            // 3 Action Buttons: Email & LinkedIn side-by-side, Portfolio centered beneath
                            // 16.dp rounded corners for action buttons
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    // Email Button: #EA4335 (Gmail Red)
                                    ActionButton(
                                        text = "✉ Email",
                                        backgroundColor = Color(0xFFEA4335),
                                        modifier = Modifier.weight(1f),
                                        onClick = { openEmail(context, DEV_EMAIL) },
                                    )
                                    // LinkedIn Button: #0A66C2 (LinkedIn Blue)
                                    ActionButton(
                                        text = "💼 LinkedIn",
                                        backgroundColor = Color(0xFF0A66C2),
                                        modifier = Modifier.weight(1f),
                                        onClick = { openUrl(context, LINKEDIN_URL) },
                                    )
                                }
                                // Portfolio Button: #0D9488 (Emerald Teal) - centered beneath
                                ActionButton(
                                    text = "🌐 Portfolio",
                                    backgroundColor = Color(0xFF0D9488),
                                    modifier = Modifier.fillMaxWidth(0.85f),
                                    onClick = { openUrl(context, PORTFOLIO_URL) },
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ActionButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}
