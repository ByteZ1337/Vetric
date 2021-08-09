package xyz.xenondevs.vetric.util

fun Int.hasMask(mask: Int) = this and mask == mask

fun Int.setMask(mask: Int, value: Boolean) = if (value) this or mask else this and mask.inv()

fun UInt.toByteArray() =
    byteArrayOf(
        (this shr 24).toByte(),
        (this shr 16).toByte(),
        (this shr 8).toByte(),
        this.toByte()
    )