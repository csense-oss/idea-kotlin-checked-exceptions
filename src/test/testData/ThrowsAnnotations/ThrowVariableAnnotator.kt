
object ThrowVariableAnnotator {

    fun simpleExample(){
        val ex = Exception()
        <warning descr="Throws \"Exception\"">throw ex<warning>
    }

    fun fromClz(){
        <warning descr="Throws \"RuntimeException\"">throw myError</warning>
    }

    val myError: RuntimeException = RuntimeException()

}