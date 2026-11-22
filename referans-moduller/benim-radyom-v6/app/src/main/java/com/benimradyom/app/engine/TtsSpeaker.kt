package com.benimradyom.app.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong

/**
 * Metin anonslarini seslendirir.
 *
 * Kendi ses kaydin varsa bu hic kullanilmaz - kayit dogrudan calar. Bu sinif
 * sadece "yazip gectigin" anonslar icindir.
 */
class TtsSpeaker(ctx: Context) {

    private var tts: TextToSpeech? = null
    @Volatile private var ready = false
    private val counter = AtomicLong(0)

    /** Su an konusulan metnin bittiginde calisacak is. */
    @Volatile private var onFinished: (() -> Unit)? = null

    init {
        tts = TextToSpeech(ctx.applicationContext) { status ->
            ready = status == TextToSpeech.SUCCESS
            if (ready) {
                val tr = Locale("tr", "TR")
                val result = tts?.setLanguage(tr)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.getDefault())
                }
            }
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) = finish()
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) = finish()
            override fun onError(utteranceId: String?, errorCode: Int) = finish()
        })
    }

    val isReady: Boolean get() = ready

    private fun finish() {
        val cb = onFinished
        onFinished = null
        cb?.invoke()
    }

    /**
     * [text] metnini seslendirir; bitince [onDone] cagrilir.
     * Hazir degilse [onDone] hemen cagrilir ki radyo takilmasin.
     */
    fun speak(text: String, speed: Float, pitch: Float, onDone: () -> Unit) {
        val engine = tts
        if (!ready || engine == null || text.isBlank()) { onDone(); return }
        onFinished = onDone
        engine.setSpeechRate(speed.coerceIn(0.5f, 2.0f))
        engine.setPitch(pitch.coerceIn(0.5f, 2.0f))
        val id = "ann_" + counter.incrementAndGet()
        val result = engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
        if (result != TextToSpeech.SUCCESS) finish()
    }

    /** Ayarlari denemek icin; bitince bir sey yapmaz. */
    fun preview(text: String, speed: Float, pitch: Float) = speak(text, speed, pitch) {}

    fun stop() {
        onFinished = null
        tts?.stop()
    }

    fun release() {
        onFinished = null
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }
}
