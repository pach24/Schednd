package com.schednd.domain.util

import java.security.SecureRandom

object EventCodeGenerator {
    private const val ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    private const val CODE_LENGTH = 6
    private val random = SecureRandom()

    fun generate(): String =
        (1..CODE_LENGTH)
            .map { ALPHABET[random.nextInt(ALPHABET.length)] }
            .joinToString("")

    fun isValid(code: String): Boolean =
        code.length == CODE_LENGTH && code.all { it in ALPHABET }
}
