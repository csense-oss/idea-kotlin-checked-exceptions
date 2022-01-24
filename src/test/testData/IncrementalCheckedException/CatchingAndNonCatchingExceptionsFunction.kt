//object CatchingAndNonCatchingExceptionsFunction {
//    @Throws
//    fun iThrow() {
//        throw Exception("")
//    }
//
//    fun iDoNotCatchException() {
//        <warning descr="This call throws, so you should handle it with try catch, or declare that this method throws.
// It throws the following types:Throwable">iThrow()</warning>
//    }
//
//    @Throws
//    fun iDoRethrowTheException() {
//        iThrow()
//    }
//
//    fun iDoCatchException() {
//        try {
//            iThrow()
//        } catch (E: Throwable) {
//
//        }
//    }
//
//    fun iDoCatchWrongType() {
//        try {
//            <warning descr="This call throws, so you should handle it with try catch, or declare that this method throws.
// It throws the following types:Throwable">iThrow()</warning>
//        } catch (E: Exception) {
//
//        }
//    }
//}

object CatchingAndNonCatchingExceptionsFunction {
    @Throws
    fun iThrow() {
        throw Exception("")
    }

    fun iDoNotCatchException() {
        iThrow()//This call throws, so you should handle it with try catch, or declare that this method throws.
// It throws the following types:Throwable
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
            iThrow()//This call throws, so you should handle it with try catch, or declare that this method throws.
// It throws the following types:Throwable
        } catch (E: Exception) {

        }
    }
}
