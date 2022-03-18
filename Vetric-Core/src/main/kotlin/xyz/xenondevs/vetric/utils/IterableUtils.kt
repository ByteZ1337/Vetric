package xyz.xenondevs.vetric.utils

@Suppress("UNCHECKED_CAST")
inline fun <reified A, reified B> Iterable<*>.filterTypeSub(crossinline mapper: (A) -> Any): Iterable<A> =
    filter { it is A && mapper(it) is B } as Iterable<A>

@Suppress("UNCHECKED_CAST")
inline fun <reified A, reified B> Sequence<*>.filterTypeSub(crossinline mapper: (A) -> Any): Sequence<A> =
    filter { it is A && mapper(it) is B } as Sequence<A>