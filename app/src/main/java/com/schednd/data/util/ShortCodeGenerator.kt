package com.schednd.data.util

import java.security.SecureRandom

object ShortCodeGenerator {
    private const val ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    private const val CODE_LENGTH = 6
    private val random = SecureRandom()

    fun generate(): String {
        return (1..CODE_LENGTH)
            .map { ALPHABET[random.nextInt(ALPHABET.length)] }
            .joinToString("")
    }

    fun isValid(code: String): Boolean {
        return code.length == CODE_LENGTH && code.all { it in ALPHABET }
    }
}
