package csense.idea.kotlin.checked.exceptions.lineMarkers

import com.intellij.codeInsight.daemon.*
import com.intellij.lang.*
import com.intellij.testFramework.*
import com.intellij.testFramework.fixtures.*
import csense.idea.kotlin.test.*
import csense.kotlin.tests.assertions.*
import org.junit.*
import org.junit.runner.*
import org.junit.runners.*
//TODO base test module
@RunWith(JUnit4::class)
abstract class BaseLineMarkerTestFixture : BasePlatformTestCase() {
    private val language: Language by lazy {
        Language.findLanguageByID("kotlin")!!
    }

    abstract val providerToTest: LineMarkerProvider
    private var counter = 0

    @Before
    fun setup() {
        myFixture.allowTreeAccessForAllFiles()
        cleanAndSetProvider(language, providerToTest)
        counter += 1
    }

    fun cleanAndSetProvider(language: Language, provider: LineMarkerProvider) {
        LineMarkerProviders.getInstance().clearCache(language)
        LineMarkerProviders.getInstance().addExplicitExtension(language, provider)
    }

    fun testSingleGutter(
        code: String,
        expectedTooltipText: String
    ) {
        myFixture.configureByText(
            /* fileName = */ "test$counter.kt",
            /* text = */ code
        )
        myFixture.doHighlighting()
        val gutters: MutableList<GutterMark> = myFixture.findAllGutters()
        gutters.assertSingle { it: GutterMark ->
            it.tooltipText.assert(expectedTooltipText)
        }
    }


    override fun getProjectDescriptor(): LightProjectDescriptor {
        return KotlinWithJdkAndRuntimeLightProjectDescriptor.INSTANCE_FULL_JDK
    }
}