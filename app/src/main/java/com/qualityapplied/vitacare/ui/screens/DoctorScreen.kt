package com.qualityapplied.vitacare.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.data.ApiClient
import com.qualityapplied.vitacare.data.BookingItem
import com.qualityapplied.vitacare.data.NotificationCenter
import com.qualityapplied.vitacare.data.CONDITIONS
import com.qualityapplied.vitacare.data.Condition
import com.qualityapplied.vitacare.data.DoctorReview
import com.qualityapplied.vitacare.data.SPECIALTIES
import com.qualityapplied.vitacare.ui.components.AppTextField
import com.qualityapplied.vitacare.ui.components.ChoiceChip
import com.qualityapplied.vitacare.ui.components.ErrorText
import com.qualityapplied.vitacare.ui.components.PrimaryButton
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.theme.Amber400
import com.qualityapplied.vitacare.ui.theme.Emerald400
import com.qualityapplied.vitacare.ui.theme.Rose200
import com.qualityapplied.vitacare.ui.theme.Rose50
import com.qualityapplied.vitacare.ui.theme.Rose700
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate600
import com.qualityapplied.vitacare.ui.theme.Slate700
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal200
import com.qualityapplied.vitacare.ui.theme.Teal50
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700
import com.qualityapplied.vitacare.ui.tr
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DoctorScreen(initialSpec: String?) {
    var mapSpec by rememberSaveable { mutableStateOf<String?>(null) }
    val openMap = mapSpec
    if (openMap != null) {
        NearbyMapScreen(specId = openMap, onBack = { mapSpec = null })
    } else {
        DoctorBrowser(initialSpec, onOpenMap = { mapSpec = it })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DoctorBrowser(initialSpec: String?, onOpenMap: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    var mode by rememberSaveable { mutableIntStateOf(0) } // 0 = by illness, 1 = by symptoms, 2 = my bookings
    var query by rememberSaveable { mutableStateOf("") }
    var spec by rememberSaveable(initialSpec) { mutableStateOf(initialSpec ?: "all") }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }

    // Booking & Review Dialog states
    var showBookingDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var bookings by remember { mutableStateOf<List<BookingItem>>(emptyList()) }
    var reviews by remember { mutableStateOf<List<DoctorReview>>(emptyList()) }
    var avgRating by remember { mutableStateOf(4.8) }
    var reviewsLoading by remember { mutableStateOf(false) }

    val selected: Condition? = CONDITIONS.firstOrNull { it.id == selectedId }
    val needle = query.trim().lowercase()
    val list = CONDITIONS.filter { c ->
        (spec == "all" || c.specialty == spec) &&
            (needle.isEmpty() || tr("fd.c." + c.id).lowercase().contains(needle))
    }

    val activeSpecId = selected?.specialty ?: if (spec != "all") spec else null
    val activeSpec = SPECIALTIES.firstOrNull { it.id == activeSpecId }
    val displaySpec = activeSpec ?: SPECIALTIES.first()
    val currentDoctorId = displaySpec.id
    val doctorName = "Dr. " + displaySpec.id.replaceFirstChar { it.uppercase() } + " Specialist"

    // Load user bookings
    fun loadUserBookings() {
        scope.launch {
            try {
                bookings = ApiClient.getBookings()
            } catch (_: Exception) {}
        }
    }

    // Load doctor reviews
    fun loadDoctorReviews(docId: String) {
        reviewsLoading = true
        scope.launch {
            try {
                val res = ApiClient.getDoctorReviews(docId)
                reviews = res.reviews
                if (res.avgRating > 0) avgRating = res.avgRating
            } catch (_: Exception) {
            } finally {
                reviewsLoading = false
            }
        }
    }

    LaunchedEffect(currentDoctorId) {
        loadDoctorReviews(currentDoctorId)
        loadUserBookings()
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text(tr("fd.title"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800)
            Text(tr("fd.intro"), fontSize = 12.sp, color = Slate500)
        }

        // Mode switch with horizontalScroll to prevent chip clipping on right edge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ChoiceChip("🩺 " + tr("fd.tab.illness"), selected = mode == 0, onClick = { mode = 0 })
            ChoiceChip("✅ " + tr("fd.tab.symptoms"), selected = mode == 1, onClick = { mode = 1 })
            ChoiceChip("📅 My Bookings", selected = mode == 2, onClick = {
                mode = 2
                loadUserBookings()
            })
        }

        if (mode == 2) {
            // My Bookings view
            MyBookingsSection(
                bookings = bookings,
                onRefresh = { loadUserBookings() },
                onNewBooking = { mode = 0 },
            )
        } else if (mode == 1) {
            SymptomChecker(onFindOnMap = onOpenMap)
        } else {
            AppTextField(query, { query = it }, tr("fd.search"))

            // Specialist filter
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ChoiceChip("✨ " + tr("fd.all"), selected = spec == "all", onClick = {
                    selectedId = null
                    spec = "all"
                })
                SPECIALTIES.forEach { s ->
                    ChoiceChip(s.icon + " " + tr("fd.s." + s.id), selected = spec == s.id, onClick = {
                        selectedId = null
                        spec = s.id
                    })
                }
            }

            // Featured doctor card with Booking & Reviews (Always visible)
            val urgent = selected?.urgent == true
            val shape = RoundedCornerShape(16.dp)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                colors = CardDefaults.cardColors(containerColor = if (urgent) Rose50 else Teal50),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, if (urgent) Rose200 else Teal200),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (selected != null) {
                        Text(selected.icon + " " + tr("fd.c." + selected.id), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate500)
                    }
                    if (urgent) {
                        Text("🚨 " + tr("fd.urgent"), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Rose700, modifier = Modifier.padding(vertical = 4.dp))
                    }
                    Text(tr("fd.specialist"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate500, modifier = Modifier.padding(top = 4.dp))
                    Text(doctorName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate800)
                    Text(displaySpec.icon + " " + tr("fd.s." + displaySpec.id), fontSize = 13.sp, color = Teal700, fontWeight = FontWeight.Medium)

                    // Rating badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp, bottom = 12.dp),
                    ) {
                        Text("⭐ $avgRating", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Amber400)
                        Spacer(Modifier.width(4.dp))
                        Text("(${reviews.size} patient reviews)", fontSize = 11.sp, color = Slate500)
                    }

                    // Action Buttons: Book Appointment, Map, Write Review
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SmallPill("📅 " + tr("fd.bookChanneling"), onClick = { showBookingDialog = true }, background = Teal600, textColor = Color.White)
                        SmallPill("📍 " + tr("fd.findNearby"), onClick = { onOpenMap(displaySpec.id) })
                        SmallPill("✍️ " + tr("fd.writeReview"), onClick = { showReviewDialog = true }, background = Slate100, textColor = Slate700)
                    }

                    // Patient Reviews preview list
                    if (reviews.isNotEmpty()) {
                        Spacer(Modifier.height(14.dp))
                        Text("Patient Feedback & Ratings", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate800)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            reviews.take(3).forEach { r ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(r.userName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                                            Text("⭐ ${r.rating.toInt()}/5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Amber400)
                                        }
                                        Text(r.reviewText, fontSize = 12.sp, color = Slate600, modifier = Modifier.padding(top = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Illness grid (2 columns)
            if (list.isEmpty()) {
                Text(tr("fd.noResults"), fontSize = 14.sp, color = Slate500)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    list.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { c ->
                                val isSelected = selectedId == c.id
                                val shape = RoundedCornerShape(16.dp)
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedId = c.id },
                                    shape = shape,
                                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Teal50 else Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                    border = BorderStroke(1.dp, if (isSelected) Teal600 else Slate100),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Text(c.icon, fontSize = 20.sp)
                                        Text(
                                            tr("fd.c." + c.id),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) Teal700 else Slate700,
                                        )
                                    }
                                }
                            }
                            if (row.size == 1) Box(Modifier.weight(1f))
                        }
                    }
                }
            }

            Text(tr("fd.disclaimer"), fontSize = 11.sp, color = Slate400)
        }
        Spacer(Modifier.height(4.dp))

        // Booking Modal Dialog
        if (showBookingDialog) {
            BookingDialog(
                doctorId = currentDoctorId,
                doctorName = doctorName,
                doctorSpecialty = tr("fd.s." + displaySpec.id),
                onDismiss = { showBookingDialog = false },
                onBooked = {
                    showBookingDialog = false
                    loadUserBookings()
                    mode = 2 // switch to My Bookings tab
                },
            )
        }

        // Review Modal Dialog
        if (showReviewDialog) {
            WriteReviewDialog(
                doctorId = currentDoctorId,
                doctorName = doctorName,
                onDismiss = { showReviewDialog = false },
                onSubmitted = {
                    showReviewDialog = false
                    loadDoctorReviews(currentDoctorId)
                },
            )
        }
    }
}

// ---------- Booking Dialog ----------

@Composable
private fun BookingDialog(
    doctorId: String,
    doctorName: String,
    doctorSpecialty: String,
    onDismiss: () -> Unit,
    onBooked: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val calendar = remember { Calendar.getInstance() }

    var selectedDate by remember { mutableStateOf(sdf.format(calendar.time)) }
    var selectedSlot by remember { mutableStateOf("09:00 AM") }
    var notes by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                selectedDate = sdf.format(cal.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        )
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val amPm = if (hourOfDay >= 12) "PM" else "AM"
                val hour12 = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                selectedSlot = String.format(Locale.US, "%02d:%02d %s", hour12, minute, amPm)
            },
            9,
            0,
            false,
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book Appointment", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("$doctorName ($doctorSpecialty)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Teal700)

                // Native Calendar Date Picker
                Text("Selected Date: $selectedDate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                SmallPill("📅 Pick Date (Calendar)", onClick = { datePickerDialog.show() }, background = Teal50, textColor = Teal700)

                // Native Clock Time Picker
                Text("Selected Time: $selectedSlot", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                SmallPill("⏰ Pick Time (Clock)", onClick = { timePickerDialog.show() }, background = Teal50, textColor = Teal700)

                AppTextField(notes, { notes = it }, "Notes / Symptoms (Optional)")
                ErrorText(error)
            }
        },
        confirmButton = {
            PrimaryButton(
                text = if (loading) "Booking..." else "Confirm Booking",
                enabled = !loading,
                onClick = {
                    loading = true
                    error = ""
                    scope.launch {
                        try {
                            ApiClient.createBooking(
                                doctorId = doctorId,
                                doctorName = doctorName,
                                doctorSpecialty = doctorSpecialty,
                                appointmentDate = selectedDate,
                                timeSlot = selectedSlot,
                                notes = notes,
                            )
                            NotificationCenter.add(
                                context,
                                "🩺 Channeling Confirmed: $doctorName",
                                "Appointment scheduled for $selectedDate at $selectedSlot ($doctorSpecialty).",
                                showOnPhone = true,
                            )
                            onBooked()
                        } catch (e: Exception) {
                            error = e.message ?: "Booking failed"
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

// ---------- Review Dialog ----------

@Composable
private fun WriteReviewDialog(
    doctorId: String,
    doctorName: String,
    onDismiss: () -> Unit,
    onSubmitted: (Int) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var rating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate & Review Doctor", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(doctorName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Teal700)

                // 5-Star selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    (1..5).forEach { star ->
                        Text(
                            text = if (star <= rating) "★" else "☆",
                            fontSize = 28.sp,
                            color = if (star <= rating) Amber400 else Slate400,
                            modifier = Modifier.clickable { rating = star },
                        )
                    }
                }

                AppTextField(reviewText, { reviewText = it }, "Write your experience...")
                ErrorText(error)
            }
        },
        confirmButton = {
            PrimaryButton(
                text = if (loading) "Submitting..." else "Submit Review",
                enabled = !loading && reviewText.isNotBlank(),
                onClick = {
                    loading = true
                    error = ""
                    scope.launch {
                        try {
                            ApiClient.createReview(doctorId, rating.toDouble(), reviewText)
                            NotificationCenter.add(
                                context,
                                "⭐ Review Submitted for $doctorName",
                                "Thank you for rating $rating/5 stars!",
                                showOnPhone = false,
                            )
                            onSubmitted(rating)
                        } catch (e: Exception) {
                            error = e.message ?: "Submission failed"
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

// ---------- My Bookings Section ----------

@Composable
private fun MyBookingsSection(
    bookings: List<BookingItem>,
    onRefresh: () -> Unit,
    onNewBooking: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Your Scheduled Appointments",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.width(8.dp))
            SmallPill("+ New Booking", onClick = onNewBooking, background = Teal600, textColor = Color.White)
        }

        if (bookings.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📅", fontSize = 36.sp)
                    Text("No Appointments Found", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate800, modifier = Modifier.padding(top = 8.dp))
                    Text("You don't have any booked doctor channelings yet.", fontSize = 12.sp, color = Slate500, modifier = Modifier.padding(top = 4.dp))
                }
            }
        } else {
            bookings.forEach { b ->
                BookingCardItem(b, onRefresh)
            }
        }
    }
}

@Composable
private fun BookingCardItem(booking: BookingItem, onRefresh: () -> Unit) {
    val scope = rememberCoroutineScope()
    var showRatingModal by remember { mutableStateOf(false) }
    var submittedRating by remember { mutableStateOf<Int?>(null) }

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
                Text(booking.doctorName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate800)
                StatusBadge(booking.status)
            }
            Text(booking.doctorSpecialty, fontSize = 12.sp, color = Teal700, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("📆 " + booking.appointmentDate, fontSize = 12.sp, color = Slate600)
                Text("⏰ " + booking.timeSlot, fontSize = 12.sp, color = Slate600)
            }
            if (booking.notes.isNotBlank()) {
                Text("Note: " + booking.notes, fontSize = 11.sp, color = Slate500, modifier = Modifier.padding(top = 6.dp))
            }
            Spacer(Modifier.height(10.dp))

            if (booking.status == "Completed" || submittedRating != null) {
                // Post-rating card state with green checkmark tick & star rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text("✔ Completed", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    if (submittedRating != null) {
                        Text("★ ${submittedRating}.0 Rated", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Amber400)
                    }
                }
            } else if (booking.status == "Confirmed" || booking.status == "Pending") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallPill("Complete & Rate", onClick = {
                        showRatingModal = true
                    }, background = Emerald400, textColor = Color.White)

                    SmallPill("Cancel", onClick = {
                        scope.launch {
                            try {
                                ApiClient.deleteBooking(booking.id)
                                onRefresh()
                            } catch (_: Exception) {}
                        }
                    }, background = Rose50, textColor = Rose700)
                }
            }
        }
    }

    if (showRatingModal) {
        WriteReviewDialog(
            doctorId = booking.doctorId,
            doctorName = booking.doctorName,
            onDismiss = { showRatingModal = false },
            onSubmitted = { ratingVal ->
                showRatingModal = false
                submittedRating = ratingVal
                scope.launch {
                    try {
                        ApiClient.updateBookingStatus(booking.id, "Completed")
                        onRefresh()
                    } catch (_: Exception) {}
                }
            },
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (bg, textCol) = when (status) {
        "Confirmed" -> Teal50 to Teal700
        "Completed" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "Cancelled" -> Rose50 to Rose700
        else -> Slate100 to Slate700
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textCol)
    }
}
