package com.benimradyom.app.engine

import com.benimradyom.app.data.model.PrayerSettings
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

/**
 * Namaz vakitlerini cihazda hesaplar - internet gerekmez.
 *
 * Standart gunes konumu formulleri (PrayTimes yontemi). Sonuc gun icindeki
 * dakika cinsinden doner; 1 dakikalik hata payi vardir, vakit duraklatmasi icin
 * fazlasiyla yeterli.
 */
object PrayerTimes {

    /** Vakitler, gun basindan itibaren dakika cinsinden. */
    data class Times(
        val fajr: Int,
        val sunrise: Int,
        val dhuhr: Int,
        val asr: Int,
        val maghrib: Int,
        val isha: Int
    ) {
        /** Sadece namaz vakitleri (gunes dogusu haric), sirayla. */
        val prayers: List<Pair<String, Int>>
            get() = listOf(
                "Sabah" to fajr,
                "Ogle" to dhuhr,
                "Ikindi" to asr,
                "Aksam" to maghrib,
                "Yatsi" to isha
            )
    }

    /** Yaygin sehirler - kullanici konum izni vermeden secebilsin diye. */
    val cities: List<Triple<String, Double, Double>> = listOf(
        Triple("Istanbul", 41.0082, 28.9784),
        Triple("Ankara", 39.9334, 32.8597),
        Triple("Izmir", 38.4237, 27.1428),
        Triple("Bursa", 40.1826, 29.0665),
        Triple("Antalya", 36.8969, 30.7133),
        Triple("Adana", 37.0000, 35.3213),
        Triple("Konya", 37.8746, 32.4932),
        Triple("Gaziantep", 37.0662, 37.3833),
        Triple("Kayseri", 38.7312, 35.4787),
        Triple("Diyarbakir", 37.9144, 40.2306),
        Triple("Trabzon", 41.0027, 39.7168),
        Triple("Erzurum", 39.9000, 41.2700),
        Triple("Samsun", 41.2867, 36.3300),
        Triple("Van", 38.4891, 43.4089),
        Triple("Sanliurfa", 37.1591, 38.7969),
        Triple("Malatya", 38.3552, 38.3095),
        Triple("Mersin", 36.8000, 34.6333),
        Triple("Eskisehir", 39.7767, 30.5206),
        Triple("Rize", 41.0201, 40.5234),
        Triple("Mardin", 37.3212, 40.7245)
    )

    fun forToday(s: PrayerSettings, calendar: Calendar = Calendar.getInstance()): Times {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val tzHours = TimeZone.getDefault().getOffset(calendar.timeInMillis) / 3_600_000.0
        return compute(year, month, day, s.latitude, s.longitude, tzHours, s.fajrAngle, s.ishaAngle)
    }

    fun compute(
        year: Int, month: Int, day: Int,
        latitude: Double, longitude: Double, timeZoneHours: Double,
        fajrAngle: Double, ishaAngle: Double
    ): Times {
        val jd = julianDay(year, month, day) - longitude / (15.0 * 24.0)

        // Gun icindeki kaba baslangic tahminleri (gun kesri olarak)
        var fajr = 5.0 / 24.0
        var sunrise = 6.0 / 24.0
        var dhuhr = 12.0 / 24.0
        var asr = 13.0 / 24.0
        var maghrib = 18.0 / 24.0
        var isha = 18.0 / 24.0

        // Iki tur yeterli yakinsama saglar.
        repeat(2) {
            dhuhr = midDay(jd, dhuhr)
            fajr = sunAngleTime(jd, fajr, fajrAngle, latitude, ccw = true)
            sunrise = sunAngleTime(jd, sunrise, riseSetAngle(), latitude, ccw = true)
            asr = asrTime(jd, asr, latitude)
            maghrib = sunAngleTime(jd, maghrib, riseSetAngle(), latitude, ccw = false)
            isha = sunAngleTime(jd, isha, ishaAngle, latitude, ccw = false)
        }

        val shift = timeZoneHours - longitude / 15.0
        fun toMinutes(hours: Double): Int {
            val h = fixHour(hours + shift)
            return ((h * 60.0).toInt()).coerceIn(0, 24 * 60 - 1)
        }

        return Times(
            fajr = toMinutes(fajr),
            sunrise = toMinutes(sunrise),
            dhuhr = toMinutes(dhuhr),
            asr = toMinutes(asr),
            maghrib = toMinutes(maghrib),
            isha = toMinutes(isha)
        )
    }

    // ── Hesap ayrintilari ───────────────────────────────────────────────────

    /** Gunesin ufuk altindaki gorunur yariçapi + kirilma duzeltmesi. */
    private fun riseSetAngle() = 0.833

    private fun julianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) { y -= 1; m += 12 }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    /** Gunes egimi (declination) ve zaman denklemi (equation of time). */
    private fun sunPosition(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sinDeg(g) + 0.020 * sinDeg(2 * g))
        val e = 23.439 - 0.00000036 * d
        val ra = fixHour(atan2Deg(cosDeg(e) * sinDeg(l), cosDeg(l)) / 15.0)
        val declination = asinDeg(sinDeg(e) * sinDeg(l))
        val equationOfTime = q / 15.0 - ra
        return declination to equationOfTime
    }

    private fun midDay(jd: Double, t: Double): Double {
        val eqt = sunPosition(jd + t).second
        return fixHour(12.0 - eqt)
    }

    private fun sunAngleTime(jd: Double, t: Double, angle: Double, lat: Double, ccw: Boolean): Double {
        val decl = sunPosition(jd + t).first
        val noon = midDay(jd, t)
        val numerator = -sinDeg(angle) - sinDeg(decl) * sinDeg(lat)
        val denominator = cosDeg(decl) * cosDeg(lat)
        val ratio = (numerator / denominator).coerceIn(-1.0, 1.0)
        val v = acosDeg(ratio) / 15.0
        return noon + if (ccw) -v else v
    }

    private fun asrTime(jd: Double, t: Double, lat: Double): Double {
        // Hanefi disindaki cogunlugun kullandigi golge carpani 1.
        val factor = 1.0
        val decl = sunPosition(jd + t).first
        val angle = -atanDeg(1.0 / (factor + tanDeg(abs(lat - decl))))
        return sunAngleTime(jd, t, angle, lat, ccw = false)
    }

    // ── Aci yardimcilari ────────────────────────────────────────────────────

    private fun rad(d: Double) = d * Math.PI / 180.0
    private fun deg(r: Double) = r * 180.0 / Math.PI
    private fun sinDeg(d: Double) = sin(rad(d))
    private fun cosDeg(d: Double) = cos(rad(d))
    private fun tanDeg(d: Double) = tan(rad(d))
    private fun asinDeg(x: Double) = deg(asin(x.coerceIn(-1.0, 1.0)))
    private fun acosDeg(x: Double) = deg(acos(x.coerceIn(-1.0, 1.0)))
    private fun atanDeg(x: Double) = deg(atan(x))
    private fun atan2Deg(y: Double, x: Double) = deg(atan2(y, x))

    private fun fixAngle(a: Double): Double {
        var v = a - 360.0 * floor(a / 360.0)
        if (v < 0) v += 360.0
        return v
    }

    private fun fixHour(a: Double): Double {
        var v = a - 24.0 * floor(a / 24.0)
        if (v < 0) v += 24.0
        return v
    }
}
