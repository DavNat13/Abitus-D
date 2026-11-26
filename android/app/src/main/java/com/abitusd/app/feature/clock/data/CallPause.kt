package com.abitusd.app.feature.clock.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.abitusd.app.feature.clock.domain.ClockEngine

/**
 * Pauses the running clock while a call rings or is active and resumes it afterwards, only when the
 * athlete turned it on and granted READ_PHONE_STATE (11e). Lives inside [ClockService].
 */
class CallPause(
    private val context: Context,
    private val controller: ClockController,
) {
    private val telephony = context.getSystemService(TelephonyManager::class.java)
    private var callback: Any? = null

    fun start() {
        val store = controller.store
        val allowed =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED
        val wanted = store.pauseOnCall && allowed
        if (callback != null || !wanted || telephony == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val cb =
                object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                    override fun onCallStateChanged(state: Int) = onState(state)
                }
            telephony.registerTelephonyCallback(ContextCompat.getMainExecutor(context), cb)
            callback = cb
        } else {
            @Suppress("DEPRECATION")
            val listener =
                object : PhoneStateListener() {
                    @Deprecated("Replaced by TelephonyCallback on Android 12")
                    override fun onCallStateChanged(
                        state: Int,
                        phoneNumber: String?,
                    ) = onState(state)
                }
            @Suppress("DEPRECATION")
            telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
            callback = listener
        }
    }

    fun stop() {
        val cb = callback ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && cb is TelephonyCallback) {
            telephony?.unregisterTelephonyCallback(cb)
        } else if (cb is PhoneStateListener) {
            @Suppress("DEPRECATION")
            telephony?.listen(cb, PhoneStateListener.LISTEN_NONE)
        }
        callback = null
    }

    private fun onState(state: Int) {
        val store = controller.store
        val run = store.refresh() ?: return
        if (state != TelephonyManager.CALL_STATE_IDLE && run.pausedAt == null) {
            store.pausedByCall = true
            controller.update(ClockEngine::pause)
        } else if (state == TelephonyManager.CALL_STATE_IDLE && store.pausedByCall) {
            store.pausedByCall = false
            controller.update(ClockEngine::resume)
        }
    }
}
