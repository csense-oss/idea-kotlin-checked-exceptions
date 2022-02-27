object ThrowsFunction {
    fun iThrow(){
        <warning descr="Throws \"Exception\"">throw iCreateException()</warning>
    }

    fun iCreateException(): Exception{
        return Exception()
    }
}