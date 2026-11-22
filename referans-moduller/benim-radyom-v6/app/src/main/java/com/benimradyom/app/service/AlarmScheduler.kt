package com.benimradyom.app.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.benimradyom.app.data.model.Program
import com.benimradyom.app.data.repo.RadioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/** Programlarin otomatik baslama ve bitis alarmlari. */
object AlarmScheduler {
    const val ACTION_START = "com.benimradyom.app.PROGRAM_START"
    const val ACTION_STOP = "com.benimradyom.app.PROGRAM_STOP"
    const val EXTRA_ID = "program_id"

    private const val START_ID_OFFSET = 1_000_000
    private const val STOP_ID_OFFSET = 1_500_000

    fun schedule(ctx: Context, program: Program) {
        cancel(ctx, program.id)
        if (!program.isEnabled || !program.autoStart || program.id <= 0L) return
        if (program.startHour !in 0..23) return

        val startAt = nextOccurrence(
            program.startHour,
            program.startMinute,
            program.days
        ) ?: return
        val manager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        setAlarm(
            manager,
            startAt,
            pendingIntent(ctx, requestCode(program.id, START_ID_OFFSET), ACTION_START, program.id)
        )

        if (program.endHour in 0..23) {
            val startMinute = program.startHour * 60 + program.startMinute.coerceIn(0, 59)
            val endMinute = program.endHour * 60 + program.endMinute.coerceIn(0, 59)
            var durationMinutes = (endMinute - startMinute + 24 * 60) % (24 * 60)
            if (durationMinutes == 0) durationMinutes = 24 * 60
            setAlarm(
                manager,
                startAt + durationMinutes * 60_000L,
                pendingIntent(ctx, requestCode(program.id, STOP_ID_OFFSET), ACTION_STOP, program.id)
            )
        }
    }

    fun cancel(ctx: Context, id: Long) {
        if (id <= 0L) return
        val manager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pendingIntent(ctx, requestCode(id, START_ID_OFFSET), ACTION_START, id))
        manager.cancel(pendingIntent(ctx, requestCode(id, STOP_ID_OFFSET), ACTION_STOP, id))
    }

    fun rescheduleAll(ctx: Context) {
        val app = ctx.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            RadioRepository(app).programs().forEach { program ->
                if (program.autoStart && program.isEnabled) schedule(app, program)
                else cancel(app, program.id)
            }
        }
    }

    private fun setAlarm(manager: AlarmManager, atMillis: Long, intent: PendingIntent) {
        val exact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            manager.canScheduleExactAlarms()
        if (exact) manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, intent)
        else manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, intent)
    }

    private fun nextOccurrence(hour: Int, minute: Int, days: String): Long? {
        if (hour !in 0..23) return null
        val selectedDays = days.padEnd(7, '0').take(7)
        if ('1' !in selectedDays) return null
        val now = Calendar.getInstance()
        for (offset in 0..7) {
            val candidate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, offset)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute.coerceIn(0, 59))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (candidate.timeInMillis <= now.timeInMillis) continue
            val dayIndex = (candidate.get(Calendar.DAY_OF_WEEK) + 5) % 7
            if (selectedDays[dayIndex] == '1') return candidate.timeInMillis
        }
        return null
    }

    private fun requestCode(id: Long, offset: Int): Int =
        offset + (id % 400_000L).toInt()

    private fun pendingIntent(
        ctx: Context,
        requestCode: Int,
        action: String,
        id: Long
    ): PendingIntent = PendingIntent.getBroadcast(
        ctx,
        requestCode,
        Intent(ctx, ScheduleReceiver::class.java)
            .setAction(action)
            .putExtra(EXTRA_ID, id),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

/** Alarm geldiginde ilgili programi acar/kapatir ve gelecek alarmi yeniden kurar. */
class ScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val id = intent.getLongExtra(AlarmScheduler.EXTRA_ID, -1L)
        if (id <= 0L) return
        val app = ctx.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = RadioRepository(app)
                val program = repo.program(id)
                when (intent.action) {
                    AlarmScheduler.ACTION_START -> {
                        if (program?.isEnabled == true && program.autoStart) {
                            repo.activateProgram(program.id)
                            RadioService.send(app, RadioService.ACTION_START)
                            AlarmScheduler.schedule(app, program)
                        }
                    }
                    AlarmScheduler.ACTION_STOP -> {
                        val next = repo.currentProgram()
                        if (next != null && next.id != id && next.autoStart && next.isEnabled) {
                            repo.activateProgram(next.id)
                            RadioService.send(app, RadioService.ACTION_START)
                        } else {
                            RadioService.send(app, RadioService.ACTION_STOP)
                        }
                        program?.let { AlarmScheduler.schedule(app, it) }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

/** Telefon yeniden basladiginda Android alarmlari siler; burada geri kurulur. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val action = intent.action ?: return
        if (
            action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            AlarmScheduler.rescheduleAll(ctx.applicationContext)
        }
    }
}
