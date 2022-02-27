class NonRuntimeException2 : Throwable("")

fun throwsUncheckedException2() {
    //when all exceptions are registered as runtime exception, then this should be highligted
    <warning descr="Throws \"NonRuntimeException2\"">throw NonRuntimeException2()</warning>
}