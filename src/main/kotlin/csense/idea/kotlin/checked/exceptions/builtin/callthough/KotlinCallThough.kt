package csense.idea.kotlin.checked.exceptions.builtin.callthough

import csense.idea.base.bll.kotlin.*
import org.jetbrains.kotlin.psi.*

object KotlinCallThough {

    fun contains(ktLambdaExpression: KtLambdaExpression): Boolean {
        val fqName: String = ktLambdaExpression.resolveParameterFunction()?.fqName ?: return false
        return fqName in builtIn

    }

    private val builtIn: Set<String> = setOf(

        "kotlin.synchronized",

        "kotlin.apply",
        "kotlin.run",
        "kotlin.with",
        "kotlin.also",
        "kotlin.let",
        "kotlin.takeIf",
        "kotlin.takeUnless",
        "kotlin.repeat",


        "kotlin.Result.fold",
        "kotlin.Result.getOrElse",
        "kotlin.Result.map",
        "kotlin.Result.onFailure",
        "kotlin.Result.onSuccess",
        "kotlin.Result.recover",

        "kotlin.time.toComponents",
        "kotlin.time.measureTime",
        "kotlin.time.TimeSource.measureTime",
        "kotlin.time.measureTimedValue",
        "kotlin.time.TimeSource.measureTimedValue",

        "kotlin.system.measureNanoTime",
        "kotlin.system.measureTimeMillis",

        "kotlin.text.StringBuilder.buildString",

        "kotlin.io.use",

        "kotlin.coroutines.suspendCoroutine",

        "kotlin.collections.buildList",

        "kotlin.concurrent.ReentrantReadWriteLock.read",
        "kotlin.concurrent.Lock.withLock",
        "kotlin.concurrent.ReentrantReadWriteLock.write",

        "kotlinx.coroutines.coroutineScope",
        "kotlinx.coroutines.supervisorScope",
        "kotlinx.coroutines.time.withTimeout",
        "kotlinx.coroutines.runBlocking",
        "kotlinx.coroutines.withTimeout",
        "kotlinx.coroutines.channels.ChannelResult<T>.getOrElse",
        "kotlinx.coroutines.channels.ChannelResult<T>.onClosed",
        "kotlinx.coroutines.channels.ChannelResult<T>.onFailure",
        "kotlinx.coroutines.channels.ChannelResult<T>.onSuccess",
        "kotlinx.coroutines.channels.ReceiveChannel<E>.consume",
        "kotlinx.coroutines.withContext",
        "kotlinx.coroutines.sync.Mutex.withLock",
        "kotlinx.coroutines.sync.Semaphore.withPermit",
        "kotlinx.coroutines.selects.select"
    )
}