package xyz.xenondevs.vetric.util

import kotlin.random.Random
import kotlin.random.nextInt

fun repeatRandom(range: IntRange, block: (Int) -> Unit) = repeat(Random.nextInt(range), block)

fun Random.nextFloat(range: IntRange) = range.first + (range.last - range.first) * this.nextFloat()

fun Random.nextDouble(range: LongRange) = range.first + (range.last - range.first) * this.nextDouble()