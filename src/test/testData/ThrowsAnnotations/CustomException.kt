class CustomException : kotlin.RuntimeException("")

fun throwFun() {
    <warning descr="Throws \"CustomException\"">throw CustomException()</warning>
}

class NonRuntimeException : Throwable("")

fun throwsUncheckedException() {
    //by default non runtime exception should not be highligted
    throw NonRuntimeException()
}