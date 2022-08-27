package xyz.xenondevs.vetric.utils

@Suppress("UNCHECKED_CAST")
inline fun <reified A, reified B> Iterable<*>.filterTypeSub(crossinline mapper: (A) -> Any): Iterable<A> =
    filter { it is A && mapper(it) is B } as Iterable<A>

@Suppress("UNCHECKED_CAST")
inline fun <reified A, reified B> Sequence<*>.filterTypeSub(crossinline mapper: (A) -> Any): Sequence<A> =
    filter { it is A && mapper(it) is B } as Sequence<A>

@Suppress("UNCHECKED_CAST")
inline fun <reified A> Iterable<*>.filterTypeAnd(crossinline filter: (A) -> Boolean): Iterable<A> =
    filter { it is A && filter(it) } as Iterable<A>

@Suppress("UNCHECKED_CAST")
inline fun <reified A> Sequence<*>.filterTypeAnd(crossinline filter: (A) -> Boolean): Sequence<A> =
    filter { it is A && filter(it) } as Sequence<A>
    