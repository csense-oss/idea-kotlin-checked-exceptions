package csense.idea.kotlin.checked.exceptions.callthough

object InlineLambdaCallInbuilt {
    
    //some of the inbuilt constructs that uses a lambda argument as if it was called directly.
    //so for the analysis we are to simply continue if we encounter one of theses
    val inbuiltKotlinSdk = hashSetOf(
            //Synchronized.kt
            "kotlin.synchronized",
            //standard.kt
            "kotlin.run",
            "kotlin.run",
            "kotlin.with",
            "kotlin.apply",
            "kotlin.also",
            "kotlin.let",
            "kotlin.takeIf",
            "kotlin.takeUnless",
            //kotlin time - measureTime.kt
            "kotlin.time.measureTime",
            "kotlin.time.TimeSource",
            "kotlin.time.measureTimedValue",
            "kotlin.time.measureTimedValue",
            //kotlin system - Timing.kt
            "kotlin.system.measureTimeMillis",
            "kotlin.system.measureNanoTime",
            //io/Closeable.kt
            "kotlin.io.use",
            //collections/Sets.kt
            "kotlin.collections.buildSet",
            //kotlin/AutoCloseable.kt
            "kotlin.use",
            //more collections..
            "kotlin.collections.buildList",
            "kotlin.collections.buildMap"
    )
}