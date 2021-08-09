package xyz.xenondevs.vetric.util

import java.io.Flushable

fun <T> T.flushClose() where T : Flushable, T : AutoCloseable {
    flush(); close()
}