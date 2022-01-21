package csense.idea.kotlin.checked

import csense.idea.kotlin.test.*
import org.junit.*


class ThrowsAnnotationsFixtures : KotlinLightCodeInsightFixtureTestCaseJunit4() {

    override fun getTestDataPath(): String = "src/test/testData/ThrowsAnnotations/"

    @Before
    fun setup() {
        myFixture.allowTreeAccessForAllFiles()
    }

//    @Test
//    fun custom() {
//        Settings.runtimeAsCheckedException = false
//        myFixture.testHighlighting("CustomException.kt")
//        Settings.runtimeAsCheckedException = true
//        myFixture.testHighlighting("CustomExceptionNonRuntime.kt")
//    }

    @Test
    fun throwable() {
        myFixture.testHighlighting("Throwable.kt")
    }

    @Test
    fun exception() {
        myFixture.testHighlighting("Exception.kt")
    }

    @Test
    fun runtimeException() {
        myFixture.testHighlighting("RuntimeException.kt")
    }

    @Test
    fun kotlinException() {
        myFixture.testHighlighting("KotlinException.kt")
    }

    @Test
    fun variable() {
        myFixture.testHighlighting("ThrowVariableAnnotator.kt")
    }

}

