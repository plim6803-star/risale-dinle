package com.benimradyom.app.data.ai

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** API anahtarini Android Keystore ile sifreleyerek cihazda saklar. */
class ApiKeyStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun hasKey(): Boolean = load().isNotBlank()

    fun save(value: String) {
        val clean = value.trim()
        if (clean.isBlank()) { clear(); return }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(clean.toByteArray(Charsets.UTF_8))
        prefs.edit()
            .putString(KEY_DATA, Base64.encodeToString(encrypted, Base64.NO_WRAP))
            .putString(KEY_IV, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .apply()
    }

    fun load(): String = runCatching {
        val data = prefs.getString(KEY_DATA, null) ?: return ""
        val iv = prefs.getString(KEY_IV, null) ?: return ""
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateKey(),
            GCMParameterSpec(128, Base64.decode(iv, Base64.NO_WRAP))
        )
        String(cipher.doFinal(Base64.decode(data, Base64.NO_WRAP)), Charsets.UTF_8)
    }.getOrElse {
        // Anahtar sistemi tarafindan gecersiz kilindiysa bozuk sifreli veriyi tutma.
        clear()
        ""
    }

    fun clear() {
        prefs.edit().remove(KEY_DATA).remove(KEY_IV).apply()
    }

    private fun getOrCreateKey(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    private companion object {
        const val PREFS = "secure_ai"
        const val KEY_DATA = "key_data"
        const val KEY_IV = "key_iv"
        const val ALIAS = "benim_radyom_ai_key_v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
