package csense.idea.kotlin.checked

import csense.idea.kotlin.checked.exceptions.inspections.*
import csense.idea.kotlin.test.*
import org.junit.*

class IncrementalCheckedExceptionInspectionTest : KotlinLightCodeInsightFixtureTestCaseJunit4() {
    override fun getTestDataPath(): String = "src/test/testData/IncrementalCheckedException/"

    @Test
    fun functionDoesNotCatchException() {
        myFixture.configureByFile("CatchingAndNonCatchingExceptionsFunction.kt")
        myFixture.enableInspections(IncrementalCheckedExceptionInspection())
        myFixture.checkHighlighting(true, false, false, false)
    }

}