package csense.idea.kotlin.checked.exceptions.builtin.callthough

import csense.idea.base.bll.kotlin.*

object KotlinCallThough {

    fun contains(lookup: LambdaArgumentLookup): Boolean {
        val fqName: String = lookup.parentFunctionFqName ?: return false
        val parameterName: String = builtIn[fqName] ?: return false
        return lookup.parameterName == parameterName
    }

    private val builtIn: Map<String, String> = mapOf(

        //standard
        "kotlin.synchronized" to "block",

        //standard

        "kotlin.run" to "block",
        "kotlin.with" to "receiver",
        "kotlin.apply" to "block",

        "kotlin.also" to "block",

        "kotlin.let" to "block",
        "kotlin.takeIf" to "predicate",
        "kotlin.takeUnless" to "predicate",
        "kotlin.repeat" to "action",


        //collection https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/

        "kotlin.collections.all" to "predicate",
        "kotlin.collections.any" to "predicate",
        "kotlin.collections.associate" to "transform",
        "kotlin.collections.associateBy" to "keySelector",
        "kotlin.collections.associateBy" to "valueSelector",
        "kotlin.collections.associateByTo" to "keySelector",
        "kotlin.collections.associateByTo" to "valueSelector",
        "kotlin.collections.associateTo" to "keySelector",
        "kotlin.collections.associateTo" to "valueSelector",
        "kotlin.collections.associateWith" to "valueSelector",
        "kotlin.collections.associateWithTo" to "valueSelector",

        "kotlin.collections.binarySearch" to "comparison",
        "kotlin.collections.binarySearchBy" to "comparison",
        "kotlin.collections.buildList" to "builderAction",
        "kotlin.collections.buildSet" to "builderAction",
        "kotlin.collections.chunked" to "transform",
        "kotlin.collections.count" to "predicate",
        "kotlin.collections.distinctBy" to "selector",
        "kotlin.collections.dropLastWhile" to "predicate",
        "kotlin.collections.dropWhile" to "predicate",
        "kotlin.collections.elementAtOrElse" to "defaultValue",
        "kotlin.collections.filter" to "predicate",
        "kotlin.collections.filterIndexed" to "predicate",
        "kotlin.collections.filterNot" to "predicate",
        "kotlin.collections.filterNotTo" to "predicate",
        "kotlin.collections.filterTo" to "predicate",
        "kotlin.collections.filterValues" to "predicate",
        "kotlin.collections.find" to "predicate",
        "kotlin.collections.findLast" to "predicate",
        "kotlin.collections.first" to "predicate",
        "kotlin.collections.firstNotNullOf" to "transform",
        "kotlin.collections.firstNotNullOfOrNull" to "transform",
        "kotlin.collections.firstOrNull" to "predicate",
        "kotlin.collections.flatMap" to "transform",
        "kotlin.collections.flatMapIndexed" to "transform",
        "kotlin.collections.flatMapIndexedTo" to "transform",
        "kotlin.collections.flatMapTo" to "transform",
        "kotlin.collections.fold" to "operation",
        "kotlin.collections.foldIndexed" to "operation",
        "kotlin.collections.foldRightIndexed" to "operation",
        "kotlin.collections.foldTo" to "operation",
        "kotlin.collections.forEach" to "action",
        "kotlin.collections.forEachIndexed" to "action",
        "kotlin.collections.getOrElse" to "defaultValue",
        "kotlin.collections.getOrPut" to "defaultValue",
        "kotlin.collections.groupBy" to "keySelector",
        "kotlin.collections.groupBy" to "valueTransform",
        "kotlin.collections.groupByTo" to "keySelector",
        "kotlin.collections.groupByTo" to "valueTransform",
        "kotlin.collections.groupingBy" to "keySelector",
        "kotlin.collections.ifEmpty" to "defaultValue",
        "kotlin.collections.indexOfFirst" to "predicate",
        "kotlin.collections.indexOfLast" to "predicate",
        "kotlin.collections.iterable" to "iterator",
        "kotlin.collections.joinTo" to "transform",
        "kotlin.collections.joinToString" to "transform",
        "kotlin.collections.last" to "predicate",
        "kotlin.collections.List" to "init",
        "kotlin.collections.map" to "transform",
        "kotlin.collections.mapIndexed" to "transform",
        "kotlin.collections.mapIndexedNotNull" to "transform",
        "kotlin.collections.mapIndexedNotNullTo" to "transform",
        "kotlin.collections.mapIndexedTo" to "transform",
        "kotlin.collections.mapKeys" to "transform",
        "kotlin.collections.mapKeysTo" to "transform",
        "kotlin.collections.mapNotNull" to "transform",
        "kotlin.collections.mapNotNullTo" to "transform",
        "kotlin.collections.mapTo" to "transform",
        "kotlin.collections.mapValues" to "transform",
        "kotlin.collections.mapValuesTo" to "transform",
        "kotlin.collections.maxBy" to "selector",
        "kotlin.collections.maxByOrNull" to "selector",
        "kotlin.collections.maxOf" to "selector",
        "kotlin.collections.maxOfOrNull" to "selector",
        "kotlin.collections.maxOfOrWith" to "selector",
        "kotlin.collections.maxOfOrWithOrNull" to "selector",
        "kotlin.collections.maxOfOrWithOrNull" to "selector",
        "kotlin.collections.minBy" to "selector",
        "kotlin.collections.minByOrNull" to "selector",
        "kotlin.collections.minOf" to "selector",
        "kotlin.collections.minOfOrNull" to "selector",
        "kotlin.collections.minOfOrWith" to "selector",
        "kotlin.collections.minOfOrWithOrNull" to "selector",

        "kotlin.collections.MutableList" to "init",
        "kotlin.collections.none" to "predicate",
        "kotlin.collections.onEach" to "action",
        "kotlin.collections.onEachIndexed" to "action",
        "kotlin.collections.partition" to "predicate",
        "kotlin.collections.reduce" to "operation",
        "kotlin.collections.reduceIndexed" to "operation",
        "kotlin.collections.reduceIndexedOrNull" to "operation",
        "kotlin.collections.reduceRight" to "operation",
        "kotlin.collections.reduceRightIndexed" to "operation",
        "kotlin.collections.reduceRightIndexedOrNull" to "operation",
        "kotlin.collections.reduceRightOrNull" to "operation",
        "kotlin.collections.reduceTo" to "operation",
        "kotlin.collections.removeAll" to "predicate",
        "kotlin.collections.replaceAll" to "predicate",
        "kotlin.collections.retainAll" to "predicate",
        "kotlin.collections.runningFold" to "operation",
        "kotlin.collections.runningFoldIndexed" to "operation",
        "kotlin.collections.runningReduce" to "operation",
        "kotlin.collections.runningReduceIndexed" to "operation",
        "kotlin.collections.scan" to "operation",
        "kotlin.collections.scanIndexed" to "operation",
        "kotlin.collections.scanIndexed" to "operation",
        "kotlin.collections.single" to "predicate",
        "kotlin.collections.singleOrNull" to "predicate",
        "kotlin.collections.sort" to "comparison",
        "kotlin.collections.sortBy" to "selector",
        "kotlin.collections.sortByDescending" to "selector",
        "kotlin.collections.sortedBy" to "selector",
        "kotlin.collections.sortedByDescending" to "selector",
        "kotlin.collections.sumBy" to "selector",
        "kotlin.collections.sumByDouble" to "selector",
        "kotlin.collections.sumOf" to "selector",
        "kotlin.collections.takeLastWhile" to "predicate",
        "kotlin.collections.takeWhile" to "predicate",
        "kotlin.collections.windowed" to "transform",
        "kotlin.collections.withDefault" to "defaultValue",
        "kotlin.collections.zip" to "transform",
        "kotlin.collections.zipWithNext" to "transform",



        //test
        "kotlin.test.assertFailsWith" to "block",


//
//
//        "kotlin.Result.fold",
//        "kotlin.Result.getOrElse",
//        "kotlin.Result.map",
//        "kotlin.Result.onFailure",
//        "kotlin.Result.onSuccess",
//        "kotlin.Result.recover",
//
//        "kotlin.time.toComponents",
//        "kotlin.time.measureTime",
//        "kotlin.time.TimeSource.measureTime",
//        "kotlin.time.measureTimedValue",
//        "kotlin.time.TimeSource.measureTimedValue",
//
//        "kotlin.system.measureNanoTime",
//        "kotlin.system.measureTimeMillis",
//
//        "kotlin.text.StringBuilder.buildString",
//
//        "kotlin.io.use",
//
//        "kotlin.coroutines.suspendCoroutine",
//
//        "kotlin.collections.buildList",
//        "kotlin.collections.map",
//        "kotlin.collections.filter",
//
//        "kotlin.concurrent.ReentrantReadWriteLock.read",
//        "kotlin.concurrent.Lock.withLock",
//        "kotlin.concurrent.ReentrantReadWriteLock.write",
//
//        "kotlinx.coroutines.coroutineScope",
//        "kotlinx.coroutines.supervisorScope",
//        "kotlinx.coroutines.time.withTimeout",
//        "kotlinx.coroutines.runBlocking",
//        "kotlinx.coroutines.withTimeout",
//        "kotlinx.coroutines.channels.ChannelResult<T>.getOrElse",
//        "kotlinx.coroutines.channels.ChannelResult<T>.onClosed",
//        "kotlinx.coroutines.channels.ChannelResult<T>.onFailure",
//        "kotlinx.coroutines.channels.ChannelResult<T>.onSuccess",
//        "kotlinx.coroutines.channels.ReceiveChannel<E>.consume",
//        "kotlinx.coroutines.withContext",
//        "kotlinx.coroutines.sync.Mutex.withLock",
//        "kotlinx.coroutines.sync.Semaphore.withPermit",
//        "kotlinx.coroutines.selects.select"
    )
}