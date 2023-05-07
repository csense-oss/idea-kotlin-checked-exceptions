@file:Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
object UseLazy {

    val x: String by lazy {
        throw Exception()
    }
    fun useX() {
        //should highlight this is where an exception "might" be thrown
        x.<warning descr="Uncaught exceptions kotlin.NumberFormatException">toInt()</warning>
    }
}