object CatchingAndNonCatchingExceptionsFunction {
    @Throws
    fun iThrow() {
        throw Exception("")
    }

    fun iDoNotCatchException() {
        <warning descr="Uncaught exceptions kotlin.Throwable">iThrow()</warning>
    }

    @Throws
    fun iDoRethrowTheException() {
        iThrow()
    }

    fun iDoCatchException() {
        try {
            iThrow()
        } catch (E: Throwable) {

        }
    }

    fun iDoCatchWrongType() {
        try {
            <warning descr="Uncaught exceptions kotlin.Throwable">iThrow()</warning>
        } catch (E: Exception) {

        }
    }
}
