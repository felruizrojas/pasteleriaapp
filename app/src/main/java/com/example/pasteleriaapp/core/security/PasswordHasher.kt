package com.example.pasteleriaapp.core.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    private const val ITERATIONS = 65_536
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16
    private const val DELIMITER = ":"
    private const val ALGORITHM = "PBKDF2WithHmacSHA1"

    private val secureRandom = SecureRandom()

    fun hash(password: String): String {
        val salt = ByteArray(SALT_LENGTH).also { secureRandom.nextBytes(it) }
        val hash = deriveKey(password, salt)
        val saltEncoded = salt.toBase64()
        val hashEncoded = hash.toBase64()
        return listOf(saltEncoded, hashEncoded).joinToString(DELIMITER)
    }

    fun verify(rawPassword: String, storedHash: String): Boolean {
        val parts = storedHash.split(DELIMITER)
        if (parts.size != 2) {
            return storedHash == rawPassword
        }
        val salt = parts[0].fromBase64()
        val expectedHash = parts[1].fromBase64()
        val derivedHash = deriveKey(rawPassword, salt)
        return MessageDigest.isEqual(derivedHash, expectedHash)
    }

    private fun deriveKey(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
    }

    private fun ByteArray.toBase64(): String =
        Base64.encodeToString(this, Base64.NO_WRAP)

    private fun String.fromBase64(): ByteArray =
        Base64.decode(this, Base64.NO_WRAP)
}
