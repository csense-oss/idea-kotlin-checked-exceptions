package csense.idea.kotlin.checked.exceptions.lineMarkers

import org.junit.*


class ThrowsExceptionLineMarkerProviderFixture : BaseLineMarkerTestFixture() {

    override val providerToTest = ThrowsExceptionLineMarkerProvider()


    @Test
    fun worksForJavaThrowable(): Unit = testThrowsGutterType(
        //language=kotlin
        code =
        """
          fun throwFun() {
            throw java.lang.Throwable()
          }
         """,
        exceptionTypes = "java.lang.Throwable"
    )

    @Test
    fun worksForKotlinThrowable(): Unit = testThrowsGutterType(
        //language=kotlin
        code =
        """
          fun throwFun() {
            throw kotlin.Throwable()
          }
         """,
        exceptionTypes = "kotlin.Throwable"
    )

    @Test
    fun customExceptionRuntimeException(): Unit = testThrowsGutterType(
        //language=kotlin
        code = """
            class CustomException : kotlin.RuntimeException("")
            fun throwFun() {
                throw CustomException()
            }
        """,
        exceptionTypes = "CustomException"
    )

    @Test
    fun customExceptionNonRuntime(): Unit = testThrowsGutterType(
        //language=kotlin
        code = """
        class NonRuntimeException2 : Throwable("")
        fun throwsUncheckedException2() {
            throw NonRuntimeException2()
        }
        """,
        exceptionTypes = "NonRuntimeException2"
    )


    @Test
    fun throwsFunction(): Unit = testSingleGutter(
        //language=kotlin
        code =
        """
            fun iCreateException(): RuntimeException{
                return Exception()
            }

            fun iThrow(){
               throw iCreateException()
            }
        """,
        //language=html
        expectedTooltipText = "<html>Throwing exception <b style=\"color:#EDA200\">java.lang.RuntimeException</b>(subtype of <i style=\"color:#EDA200\">RuntimeException</i>)</html>"
    )

    @Test
    fun throwsVariable(): Unit = testThrowsGutterType(
        //language=kotlin
        code =
        """
            val myError: kotlin.Exception = kotlin.Exception()
            fun fromClz(){
                throw myError
            }
        """,
        exceptionTypes = "java.lang.Exception"
    )

    private fun testThrowsGutterType(
        code: String,
        exceptionTypes: String
    ) {
        testSingleGutter(
            code = code,
            //language=html
            expectedTooltipText = "<html>Throwing exception <b style=\"color:#EDA200\">$exceptionTypes</b></html>"
        )
    }
}

