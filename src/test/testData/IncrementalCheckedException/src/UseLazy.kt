object UseLazy {

    val x: String by lazy {
        throw Exception()
    }

    fun useX() {
        //should highlight this is where an exception "might" be thrown
        <warning descr="This call throws, so you should handle it with try catch, or declare that this method throws.
        It throws the following types:Exception">x.toInt()</warning>
    }
}