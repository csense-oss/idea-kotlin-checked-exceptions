package csense.idea.kotlin.checked

import csense.idea.kotlin.test.*
import org.junit.*


class ThrowsFunctionAnnotatorFixture : KotlinLightCodeInsightFixtureTestCaseJunit4() {

    override fun getTestDataPath(): String = "src/test/testData/ThrowsFunctionAnnotator/"

    @Before
    fun setup() {
        myFixture.allowTreeAccessForAllFiles()
    }

    @Test
    fun kotlinException() {
        myFixture.testHighlighting("FunctionNothing.kt")
    }

}

