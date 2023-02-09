package csense.idea.kotlin.checked

import csense.idea.kotlin.test.*
import org.junit.*

class IncrementalCheckedExceptionInspectionTest : KotlinLightCodeInsightFixtureTestCaseJunit4() {
    override fun getTestDataPath(): String = "src/test/testData/IncrementalCheckedException/"


    @Before
    fun setup() {
        myFixture.allowTreeAccessForAllFiles()
//        myFixture.enableInspections(IncrementalCheckedExceptionInspection())
    }

    @Test
    fun functionDoesNotCatchException() {
        myFixture.configureByFile("CatchingAndNonCatchingExceptionsFunction.kt")
        //TODO improve test with actually knowing the type of the wrongly not caught exceptions etc.
//        myFixture.checkHighlighting()
//        val x = myFixture.doHighlighting()
//        val matchingInspector = x.filter {
//            it.inspectionToolId == "CheckedExceptionsKotlin"
//        }

        myFixture.checkHighlighting(true, false, false, true)
    }

    @Test
    fun throwVariable() {
        myFixture.configureByFile("ThrowVariable.kt")
        myFixture.checkHighlighting(true, false, false, true)
    }

    @Test
    fun useLazy() {
        myFixture.configureByFile("UseLazy.kt")
        myFixture.checkHighlighting(true, false, false, true)
    }


}