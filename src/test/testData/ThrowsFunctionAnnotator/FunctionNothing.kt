fun shouldBeHighlighted(): Nothing {
    <warning descr="Throws \"Exception\"">throw Exception()</warning>
}

fun useShouldBeHighligted() {
    <warning descr="Throws inside of function">shouldBeHighlighted()</warning>
}