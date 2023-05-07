
object ThrowVariable {
    fun simpleExample(){
        val ex = Exception()
        <warning descr="Uncaught exceptions kotlin.Exception">throw ex</warning>
    }
}