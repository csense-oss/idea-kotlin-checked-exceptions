class TryCatchTest : KotlinLightCodeInsightFixtureTestCase() {
    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun testTryCatch() {
        myFixture.configureByFiles("tryCatch.kt")
        myFixture.checkHighlighting(
            false,
            false,
            true,
            true
        );
    }
}