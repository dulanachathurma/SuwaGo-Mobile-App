package com.qualityapplied.vitacare.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

/** Gets the phone location dynamically using FusedLocationProviderClient with fast provider fallbacks. */
object LocationHelper {
    @Volatile
    private var cachedLocation: Pair<Double, Double>? = null

    fun clearCache() {
        cachedLocation = null
    }

    fun hasPermission(ctx: Context): Boolean =
        ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /** Returns latitude to longitude (Pair<Double, Double>), or null when permission / GPS is not available. */
    @SuppressLint("MissingPermission")
    suspend fun current(ctx: Context, forceFresh: Boolean = true): Pair<Double, Double>? {
        if (!hasPermission(ctx)) return null

        // 0) Memory cache hit (bypassed when forceFresh is true)
        if (!forceFresh) {
            cachedLocation?.let { return it }
        }

        val fusedClient = try {
            LocationServices.getFusedLocationProviderClient(ctx)
        } catch (_: Exception) {
            null
        }

        // 1) Primary: Request LIVE, FRESH real-time location from FusedLocationProviderClient
        if (fusedClient != null) {
            try {
                val cts = CancellationTokenSource()
                val freshLoc = withTimeoutOrNull(7_000L) {
                    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token).await()
                } ?: withTimeoutOrNull(5_000L) {
                    val cts2 = CancellationTokenSource()
                    fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts2.token).await()
                }
                if (freshLoc != null && isRecentAndValid(freshLoc)) {
                    val result = freshLoc.latitude to freshLoc.longitude
                    cachedLocation = result
                    return result
                }
            } catch (_: Exception) {
            }

            // 2) Fallback: Try fusedClient lastLocation if fresh request times out
            try {
                val lastLoc = fusedClient.lastLocation.await()
                if (lastLoc != null && isRecentAndValid(lastLoc)) {
                    val result = lastLoc.latitude to lastLoc.longitude
                    cachedLocation = result
                    return result
                }
            } catch (_: Exception) {
            }
        }

        // 3) Fallback to LocationManager (GPS, Network, Passive providers)
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (lm != null) {
            try {
                val providers = try { lm.getProviders(true) } catch (_: Exception) { emptyList() }
                var best: Location? = null
                for (p in providers) {
                    val l = try { lm.getLastKnownLocation(p) } catch (_: Exception) { null } ?: continue
                    if (isRecentAndValid(l)) {
                        val b = best
                        if (b == null || l.time > b.time) best = l
                    }
                }
                if (best != null) {
                    val result = best.latitude to best.longitude
                    cachedLocation = result
                    return result
                }
            } catch (_: Exception) {
            }
        }

        return null
    }

    private fun isRecentAndValid(loc: Location): Boolean {
        // Ensure coordinates are valid and non-zero
        return (loc.latitude != 0.0 || loc.longitude != 0.0) &&
            loc.latitude in -90.0..90.0 && loc.longitude in -180.0..180.0
    }
}
