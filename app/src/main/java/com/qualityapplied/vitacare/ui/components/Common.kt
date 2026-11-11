package com.qualityapplied.vitacare.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.qualityapplied.vitacare.R
import com.qualityapplied.vitacare.data.LocaleManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.zIndex
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.qualityapplied.vitacare.data.ApiClient
import com.qualityapplied.vitacare.ui.decodeDataUrl
import com.qualityapplied.vitacare.ui.tr
import com.qualityapplied.vitacare.ui.theme.*
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

// ---------- Inputs & buttons ----------

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Slate400, fontSize = 15.sp) },
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (password && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (password) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(if (passwordVisible) R.drawable.ic_eye_visible else R.drawable.ic_eye_off),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Slate500,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Rose500 else Teal600,
                unfocusedBorderColor = if (isError) Rose500 else Slate200,
                errorBorderColor = Rose500,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                cursorColor = Teal600,
            ),
        )
        if (isError && !errorMessage.isNullOrEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = Rose500,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Teal600, contentColor = Color.White),
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
fun SmallPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Teal600,
    textColor: Color = Color.White,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = background,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@Composable
fun ErrorText(code: String) {
    if (code.isEmpty()) return
    val text = when {
        code.startsWith("err.") -> {
            val key = code.removePrefix("err.")
            val translated = tr("err.$key")
            if (translated != "err.$key") translated else key
        }
        else -> {
            val translated = tr("err.$code")
            if (translated != "err.$code") translated else code
        }
    }
    Text(
        text = text,
        color = Rose600,
        fontSize = 12.sp,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Rose50).padding(12.dp),
    )
}

@Composable
fun SuccessText(text: String) {
    Text(
        text = text,
        color = Emerald600,
        fontSize = 12.sp,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Emerald50).padding(12.dp),
    )
}

// ---------- Cards & pills ----------

@Composable
fun SectionCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        tonalElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

/** Background / text colours for a status or BMI category key. */
fun statusColors(key: String): Pair<Color, Color> = when (key) {
    "low", "underweight" -> Sky50 to Sky600
    "normal" -> Emerald50 to Emerald600
    "elevated", "overweight" -> Amber50 to Amber600
    else -> Rose50 to Rose600 // high, obese
}

@Composable
fun StatusPill(text: String, key: String) {
    val (bg, fg) = statusColors(key)
    Text(
        text = text,
        color = fg,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 12.dp, vertical = 4.dp),
    )
}

@Composable
fun ChoiceChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Teal600 else Color.White)
            .border(1.dp, if (selected) Teal600 else Slate200, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(text, color = if (selected) Color.White else Slate600, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

// ---------- Avatar ----------

@Composable
fun Avatar(avatarUrl: String, name: String, size: Dp) {
    val bitmap by produceState<ImageBitmap?>(null, avatarUrl) {
        value = try {
            when {
                avatarUrl.startsWith("data:image") -> decodeDataUrl(avatarUrl)?.asImageBitmap()
                avatarUrl.startsWith("http") -> ApiClient.fetchBytes(avatarUrl)?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap()
                }
                else -> null
            }
        } catch (_: Throwable) {
            null
        }
    }
    val bmp = bitmap
    if (bmp != null) {
        Image(
            bitmap = bmp,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size).clip(CircleShape),
        )
    } else {
        Box(
            modifier = Modifier.size(size).clip(CircleShape).background(Color(0xFFCCFBF1)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.trim().take(1).uppercase().ifEmpty { "?" },
                color = Teal700,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value / 2.6f).sp,
            )
        }
    }
}

// ---------- Language switcher capsule (EN / සිං / ත) ----------

/**
 * Compact language selector capsule for the Top App Bar.
 * Uses a DropdownMenu and isolated z-index to prevent overflowing header action icons.
 */
@Composable
fun LanguageSwitcher(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val current = LocaleManager.currentLocale
    var expanded by remember { mutableStateOf(false) }

    val languages = remember {
        listOf(
            "en" to "EN",
            "si" to "සිං",
            "ta" to "ත",
        )
    }
    val currentLabel = languages.firstOrNull { it.first == current }?.second ?: "EN"

    // Isolated Box container with zIndex to prevent overlapping header icons
    Box(modifier = modifier.zIndex(10f)) {
        // Compact Language Toggle Capsule
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.9f),
            shadowElevation = 2.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { expanded = !expanded },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = currentLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                )
                Text(
                    text = if (expanded) "▲" else "▼",
                    fontSize = 8.sp,
                    color = Slate600,
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .clip(RoundedCornerShape(12.dp)),
        ) {
            languages.forEach { (code, label) ->
                val selected = code == current
                DropdownMenuItem(
                    text = {
                        Text(
                            text = when (code) {
                                "en" -> "EN (English)"
                                "si" -> "සිං (සිංහල)"
                                "ta" -> "ත (தமிழ்)"
                                else -> label
                            },
                            fontSize = 12.5.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) Teal700 else Slate800,
                        )
                    },
                    onClick = {
                        expanded = false
                        if (!selected) {
                            LocaleManager.setLocale(context, code)
                        }
                    },
                )
            }
        }
    }
}

// ---------- Line chart (drawn with Canvas, no chart library needed) ----------

data class ChartSeries(
    val label: String,
    val values: List<Float>,
    val color: Color,
    val dashed: Boolean = false,
    val showPoints: Boolean = true,
    val fill: Boolean = false,
)

@Composable
fun LineChart(
    labels: List<String>,
    series: List<ChartSeries>,
    modifier: Modifier = Modifier,
    minY: Float? = null,
    maxY: Float? = null,
    height: Dp = 200.dp,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val all = series.flatMap { it.values }
            if (all.isEmpty() || labels.isEmpty()) return@Canvas

            var lo = minOf(all.min(), minY ?: all.min())
            var hi = maxOf(all.max(), maxY ?: all.max())
            if (hi - lo < 1f) {
                hi += 1f
                lo -= 1f
            }
            val pad = (hi - lo) * 0.06f
            lo -= pad
            hi += pad

            val left = 36.dp.toPx()
            val right = 10.dp.toPx()
            val top = 8.dp.toPx()
            val bottom = 22.dp.toPx()
            val w = size.width - left - right
            val h = size.height - top - bottom
            val n = labels.size

            fun xAt(i: Int): Float = if (n <= 1) left + w / 2f else left + w * i / (n - 1).toFloat()
            fun yAt(v: Float): Float = top + h * (1f - (v - lo) / (hi - lo))

            val textPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = 10.sp.toPx()
            }

            // Horizontal grid + y labels
            for (k in 0..3) {
                val v = lo + (hi - lo) * k / 3f
                val y = yAt(v)
                drawLine(Slate200, Offset(left, y), Offset(left + w, y), strokeWidth = 1.dp.toPx())
                val text = if (hi - lo < 10f) String.format(Locale.US, "%.1f", v) else v.roundToInt().toString()
                textPaint.textAlign = android.graphics.Paint.Align.LEFT
                drawContext.canvas.nativeCanvas.drawText(text, 0f, y + 4.dp.toPx(), textPaint)
            }

            // X labels (at most ~5)
            val step = max(1, ceil(n / 5.0).toInt())
            textPaint.textAlign = android.graphics.Paint.Align.CENTER
            var i = 0
            while (i < n) {
                drawContext.canvas.nativeCanvas.drawText(labels[i], xAt(i), size.height - 4.dp.toPx(), textPaint)
                i += step
            }

            // Series
            series.forEach { s ->
                val count = minOf(s.values.size, n)
                if (count == 0) return@forEach
                val path = Path()
                for (idx in 0 until count) {
                    val x = xAt(idx)
                    val y = yAt(s.values[idx])
                    if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                if (s.fill && count > 1) {
                    val area = Path().apply {
                        addPath(path)
                        lineTo(xAt(count - 1), top + h)
                        lineTo(xAt(0), top + h)
                        close()
                    }
                    drawPath(area, s.color.copy(alpha = 0.15f))
                }
                if (count > 1) {
                    drawPath(
                        path = path,
                        color = s.color,
                        style = Stroke(
                            width = (if (s.dashed) 1.5.dp else 2.5.dp).toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = if (s.dashed) PathEffect.dashPathEffect(floatArrayOf(14f, 12f)) else null,
                        ),
                    )
                }
                if (s.showPoints) {
                    for (idx in 0 until count) {
                        drawCircle(s.color, radius = 4.dp.toPx(), center = Offset(xAt(idx), yAt(s.values[idx])))
                    }
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            series.filter { it.label.isNotBlank() }.forEach { s ->
                Box(Modifier.padding(top = 4.dp).size(10.dp).clip(CircleShape).background(s.color))
                Text(
                    s.label,
                    fontSize = 11.sp,
                    color = Slate600,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 4.dp, end = 12.dp),
                )
            }
        }
    }
}
