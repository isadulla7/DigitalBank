package uz.fido.universaldigital.ui.utils.snowflake

import java.security.SecureRandom
import kotlin.math.abs

internal class Randomizer {

    private val random = SecureRandom()

    fun randomDouble(max: Int): Double {
        return random.nextDouble() * (max + 1)
    }

    fun randomInt(min: Int, max: Int, gaussian: Boolean = false): Int {
        return randomInt(max - min, gaussian) + min
    }

    private fun randomInt(max: Int, gaussian: Boolean = false): Int {
        return if (gaussian) {
            (abs(randomGaussian()) * (max + 1)).toInt()
        } else {
            random.nextInt(max + 1)
        }
    }

    private fun randomGaussian(): Double {
        val gaussian = random.nextGaussian() / 3
        return if (gaussian > -1 && gaussian < 1) gaussian else randomGaussian()
    }

    fun randomSignedNum(): Int {
        return if (random.nextBoolean()) 1 else -1
    }
}