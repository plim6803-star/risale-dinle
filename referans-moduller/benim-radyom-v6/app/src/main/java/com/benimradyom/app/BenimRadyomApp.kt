package com.benimradyom.app

import android.app.Application
import com.benimradyom.app.service.AlarmScheduler

class BenimRadyomApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Zamanlayicilar her acilista tazelenir; boylece kacirilan ya da
        // sistem tarafindan temizlenen alarmlar geri gelir.
        AlarmScheduler.rescheduleAll(this)
    }
}
