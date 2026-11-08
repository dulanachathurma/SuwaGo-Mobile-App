package com.qualityapplied.vitacare.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.data.ApiClient
import com.qualityapplied.vitacare.data.LabReportItem
import com.qualityapplied.vitacare.ui.components.AppTextField
import com.qualityapplied.vitacare.ui.components.ChoiceChip
import com.qualityapplied.vitacare.ui.components.ErrorText
import com.qualityapplied.vitacare.ui.components.PrimaryButton
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.decodeDataUrl
import com.qualityapplied.vitacare.ui.theme.Rose50
import com.qualityapplied.vitacare.ui.theme.Rose700
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate600
import com.qualityapplied.vitacare.ui.theme.Slate700
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal50
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700
import com.qualityapplied.vitacare.ui.tr
import com.qualityapplied.vitacare.ui.uriToAvatarDataUrl
import kotlinx.coroutines.launch

@Composable
fun LabReportVaultScreen() {
    val scope = rememberCoroutineScope()
    var reports by remember { mutableStateOf<List<LabReportItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("All") }
    var previewReport by remember { mutableStateOf<LabReportItem?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val categories = listOf("All", "Blood Test", "X-Ray", "Prescription", "General")

    fun loadReports() {
        scope.launch {
            loading = true
            try {
                reports = ApiClient.getReports()
            } catch (e: Exception) {
                error = e.message ?: "Failed to load lab reports"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadReports() }

    val filteredList = reports.filter {
        selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("📂 Lab Report Vault & Storage", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800)
                Text("Store, view and preview medical reports", fontSize = 12.sp, color = Slate500)
            }
            Spacer(Modifier.width(8.dp))
            SmallPill("+ Upload Report", onClick = { showUploadDialog = true }, background = Teal600, textColor = Color.White)
        }

        // Category Filter Chips
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            categories.forEach { cat ->
                ChoiceChip(cat, selected = selectedCategory == cat, onClick = { selectedCategory = cat })
            }
        }

        ErrorText(error)

        if (loading) {
            Text(tr("common.loading"), fontSize = 14.sp, color = Slate500)
        } else if (filteredList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("📄", fontSize = 38.sp)
                    Text("No Medical Reports Found", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate800, modifier = Modifier.padding(top = 8.dp))
                    Text("Upload your lab reports, prescriptions, or X-rays to store them securely.", fontSize = 12.sp, color = Slate500, modifier = Modifier.padding(top = 4.dp))
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredList.forEach { report ->
                    LabReportCard(
                        report = report,
                        onPreview = { previewReport = report },
                        onDelete = {
                            scope.launch {
                                try {
                                    ApiClient.deleteReport(report.id)
                                    loadReports()
                                } catch (_: Exception) {}
                            }
                        },
                    )
                }
            }
        }

        // Upload Dialog
        if (showUploadDialog) {
            UploadReportDialog(
                onDismiss = { showUploadDialog = false },
                onUploaded = {
                    showUploadDialog = false
                    loadReports()
                },
            )
        }

        // Preview Dialog
        previewReport?.let { report ->
            ReportPreviewDialog(report = report, onDismiss = { previewReport = null })
        }
    }
}

@Composable
private fun LabReportCard(
    report: LabReportItem,
    onPreview: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, Slate100),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(report.reportTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate800, modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Teal50)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(report.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Teal700)
                }
            }

            if (report.notes.isNotBlank()) {
                Text(report.notes, fontSize = 12.sp, color = Slate600, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SmallPill("👁️ Preview Report", onClick = onPreview, background = Teal600, textColor = Color.White)
                SmallPill("Delete", onClick = onDelete, background = Rose50, textColor = Rose700)
            }
        }
    }
}

@Composable
private fun UploadReportDialog(
    onDismiss: () -> Unit,
    onUploaded: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Blood Test") }
    var fileDataUrl by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val categories = listOf("Blood Test", "X-Ray", "Prescription", "General")

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            val dataUrl = uriToAvatarDataUrl(context, uri, size = 800)
            if (dataUrl != null) {
                fileDataUrl = dataUrl
                error = ""
            } else {
                error = "Could not process selected image/document."
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Medical Report", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppTextField(title, { title = it }, "Report Title (e.g. Hemoglobin Test)")

                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        ChoiceChip(cat, selected = category == cat, onClick = { category = cat })
                    }
                }

                AppTextField(notes, { notes = it }, "Doctor / Notes (Optional)")

                // File Select button
                SmallPill(
                    text = if (fileDataUrl != null) "✓ Document Attached" else "📷 Pick Image / Document",
                    onClick = { filePicker.launch("image/*") },
                    background = if (fileDataUrl != null) Teal50 else Slate100,
                    textColor = if (fileDataUrl != null) Teal700 else Slate700,
                )

                ErrorText(error)
            }
        },
        confirmButton = {
            PrimaryButton(
                text = if (loading) "Uploading..." else "Upload Vault Report",
                enabled = !loading && title.isNotBlank() && fileDataUrl != null,
                onClick = {
                    val url = fileDataUrl ?: return@PrimaryButton
                    loading = true
                    error = ""
                    scope.launch {
                        try {
                            ApiClient.uploadReport(
                                reportTitle = title,
                                fileUrl = url,
                                category = category,
                                notes = notes,
                            )
                            onUploaded()
                        } catch (e: Exception) {
                            error = e.message ?: "Upload failed"
                        } finally {
                            loading = false
                        }
                    }
                },
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate600) }
        },
    )
}

@Composable
private fun ReportPreviewDialog(
    report: LabReportItem,
    onDismiss: () -> Unit,
) {
    val bitmap = remember(report.fileUrl) { decodeDataUrl(report.fileUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(report.reportTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate800)
                Text("Category: ${report.category}", fontSize = 12.sp, color = Teal700)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = report.reportTitle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate100),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Preview Unavailable / Document Saved", fontSize = 13.sp, color = Slate500)
                    }
                }
                if (report.notes.isNotBlank()) {
                    Text("Notes: ${report.notes}", fontSize = 12.sp, color = Slate600)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", fontWeight = FontWeight.Bold, color = Teal700) }
        },
    )
}
