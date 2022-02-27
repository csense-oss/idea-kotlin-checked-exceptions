
object ThrowVariable {

    fun simpleExample(){
        val ex = Exception()
        <warning descr="This call throws, so you should handle it with try catch, or declare that this method throws.
It throws the following types:Exception">throw ex</warning>
    }


}