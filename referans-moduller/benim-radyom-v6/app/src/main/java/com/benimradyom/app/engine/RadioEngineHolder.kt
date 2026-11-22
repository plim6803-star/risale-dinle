package com.benimradyom.app.engine

import android.content.Context
import com.benimradyom.app.data.repo.RadioRepository

/**
 * Motorun tek ornegini uygulama omru boyunca tutar.
 *
 * Eski surumde her ViewModel yeni bir oynatici olusturup singleton'i eziyordu;
 * ekran kapaninca radyo susuyordu. Burada motor bir tane ve arayuzden bagimsiz.
 */
object RadioEngineHolder {

    @Volatile private var engine: RadioEngine? = null

    fun get(context: Context): RadioEngine {
        engine?.let { return it }
        return synchronized(this) {
            engine ?: RadioEngine(
                context.applicationContext,
                RadioRepository(context.applicationContext)
            ).also { engine = it }
        }
    }

    /** Sadece surec kapanirken; normal kullanimda cagrilmaz. */
    fun release() {
        synchronized(this) {
            engine?.release()
            engine = null
        }
    }
}
