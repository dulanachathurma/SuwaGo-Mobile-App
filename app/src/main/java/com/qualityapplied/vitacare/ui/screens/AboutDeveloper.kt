package com.qualityapplied.vitacare.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.qualityapplied.vitacare.ui.components.PrimaryButton
import com.qualityapplied.vitacare.ui.components.SectionCard
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate50
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate600
import com.qualityapplied.vitacare.ui.theme.Slate700
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal100
import com.qualityapplied.vitacare.ui.theme.Teal50
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700
import com.qualityapplied.vitacare.ui.tr

const val DEV_FULL_NAME = "Dulana Chathurma"
const val DEV_SHORT_NAME = "Dulana Chathurma"
const val DEV_ROLE = "AI-Integrated Full-Stack & UI/UX Developer"
const val DEV_BRAND = "Quality Applied / SuwaGo Mobile"
const val LINKEDIN_URL = "https://www.linkedin.com/in/dulana-chathurma/"
const val GITHUB_URL = "https://github.com/dulanachathurma"
const val PORTFOLIO_URL = "https://dulanaportfolio.vercel.app/"
const val DEV_EMAIL = "dulanachathurma99@gmail.com"
const val APP_VERSION = "v1.1.0"
const val APP_DESCRIPTION = "SuwaGo is an AI-integrated smart digital healthcare management platform designed for daily health tracking, medicine reminders, lab report vault, emergency services, and smart doctor discovery."

private fun open(context: Context, intent: Intent) {
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
    }
}

/** Developer card shown at the bottom of the Profile screen. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeveloperCard() {
    val context = LocalContext.current
    SectionCard {
        Text(tr("dev.title"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate400)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.dev_photo),
                contentDescription = DEV_FULL_NAME,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(72.dp).clip(CircleShape),
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(DEV_SHORT_NAME, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate800)
                Text(DEV_BRAND, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Teal700)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(DEV_ROLE, fontSize = 12.sp, color = Slate500, lineHeight = 17.sp)
        Spacer(Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SmallPill("✉  " + tr("dev.email"), onClick = {
                open(context, Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$DEV_EMAIL")))
            })
            SmallPill("in  " + tr("dev.linkedin"), onClick = {
                open(context, Intent(Intent.ACTION_VIEW, Uri.parse(LINKEDIN_URL)))
            }, background = Color(0xFF0A66C2))
            SmallPill("🌐  Portfolio", onClick = {
                open(context, Intent(Intent.ACTION_VIEW, Uri.parse(PORTFOLIO_URL)))
            }, background = Teal700)
        }
    }
}

/** Small credit line for the login / register screens. */
@Composable
fun DeveloperFooter() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(tr("dev.madeBy") + " " + DEV_SHORT_NAME, fontSize = 11.sp, color = Slate400, textAlign = TextAlign.Center)
        Text(DEV_BRAND, fontSize = 11.sp, color = Slate400, textAlign = TextAlign.Center)
    }
}

/**
 * Developer & App Details Dialog triggered by clicking the Info button in the header.
 */
@Composable
fun DeveloperAppDetailsDialog(onDismiss: () -> Unit) {
    DeveloperInfoDialog(onDismiss = onDismiss)
}
