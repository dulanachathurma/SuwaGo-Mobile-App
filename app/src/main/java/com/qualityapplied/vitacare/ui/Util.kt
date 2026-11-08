package com.qualityapplied.vitacare.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Looks up a translated string by its web-app key, e.g. tr("fd.c.fever") -> R.string.fd_c_fever.
 * Mirrors the t('...') helper of the website, so keys stay identical in EN / SI / TA.
 */
@Composable
fun tr(key: String): String {
    val context = LocalContext.current
    LocalConfiguration.current // re-read when the language changes
    val name = key.replace('.', '_')
    val id = context.resources.getIdentifier(name, "string", context.packageName)
    return if (id != 0) context.getString(id) else key
}

@Composable
fun tr(key: String, vararg args: Any): String {
    val raw = tr(key)
    return try {
        if (args.isNotEmpty()) String.format(raw, *args) else raw
    } catch (_: Exception) {
        raw
    }
}

@Composable
fun currentLocale(): Locale {
    val config = LocalConfiguration.current
    return config.locales[0]
}

@Composable
fun formatDate(date: Date): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, currentLocale()).format(date)

@Composable
fun formatShortDate(date: Date): String =
    SimpleDateFormat("MMM d", currentLocale()).format(date)

/** Crops to a centred square, shrinks to [size] px and returns a base64 JPEG data URL (same as the web app). */
fun uriToAvatarDataUrl(context: Context, uri: Uri, size: Int = 256): String? {
    return try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / sample > 1024 || bounds.outHeight / sample > 1024) sample *= 2

        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        var bmp = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opts)
        } ?: return null

        // Respect the camera rotation
        val rotation = context.contentResolver.openInputStream(uri)?.use {
            when (ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f
        if (rotation != 0f) {
            val m = Matrix().apply { postRotate(rotation) }
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
        }

        val side = minOf(bmp.width, bmp.height)
        val square = Bitmap.createBitmap(bmp, (bmp.width - side) / 2, (bmp.height - side) / 2, side, side)
        val scaled = Bitmap.createScaledBitmap(square, size, size, true)
        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, out)
        "data:image/jpeg;base64," + Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    } catch (_: Exception) {
        null
    }
}

fun decodeDataUrl(dataUrl: String): Bitmap? = try {
    val raw = Base64.decode(dataUrl.substringAfter("base64,"), Base64.DEFAULT)
    BitmapFactory.decodeByteArray(raw, 0, raw.size)
} catch (_: Exception) {
    null
}
