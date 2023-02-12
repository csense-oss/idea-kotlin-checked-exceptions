package csense.idea.kotlin.checked.exceptions.bll.callthough


import csense.idea.base.bll.psi.*
import csense.idea.base.uastKtPsi.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.psi.*

object CallthoughInMemory {

    private val isEnabled: Boolean
        get() = Settings.useCallThoughFile

    fun isArgumentMarkedAsCallthough(main: KtFunction, parameterName: String): Boolean {
        val mainFqName = main.getKotlinFqNameString() ?: return false
        if (isKnownKotlinFunction(mainFqName, parameterName)) {
            return true
        }
        if (!isEnabled) {
            return false
        }
        return CallthoughStorage.contains(mainFqName, parameterName, main.project)
    }


    fun isKnownKotlinFunction(fqName: String, paramName: String): Boolean {
        return knownKotlinFunctions[fqName] == paramName
    }

    val knownKotlinFunctions = hashMapOf(
        //standard kotlin
        Pair("kotlin.run", "block"),
        Pair("kotlin.with", "block"),
        Pair("kotlin.apply", "block"),
        Pair("kotlin.also", "block"),
        Pair("kotlin.let", "block"),
        Pair("kotlin.takeIf", "predicate"),
        Pair("kotlin.takeUnless", "predicate"),
        Pair("kotlin.repeat", "action"),
        Pair("kotlin.use", "block"),
        Pair("kotlin.synchronized", "block"),
        // Kotlin io
        Pair("kotlin.io.use", "block"),
        //Kotlin time
        Pair("kotlin.time.measureTime", "block"),
        Pair("kotlin.time.measureTimedValue", "block"),
        //kotlin system
        Pair("kotlin.system.measureNanoTime", "block"),
        Pair("kotlin.system.measureTimeMicros", "block"),
        Pair("kotlin.system.measureTimeMillis", "block"),
        // kotlin comparisons https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.comparisons/
        Pair("kotlin.comparisons.compareBy", "selectors"),
        Pair("kotlin.comparisons.compareBy", "selector"),
        Pair("kotlin.comparisons.compareByDescending", "selector"),
        Pair("kotlin.comparisons.compareValuesBy", "selectors"),
        Pair("kotlin.comparisons.compareValuesBy", "selector"),
        Pair("kotlin.comparisons.thenBy", "selector"),
        Pair("kotlin.comparisons.thenByDescending", "selector"),
        Pair("kotlin.comparisons.thenComparator", "comparison"),
        Pair("kotlin.comparisons.thenDescending", "comparator"),
        // kotlin collections
        Pair("kotlin.collections.aggregate", "operation"),
        Pair("kotlin.collections.aggregateTo", "operation"),
        Pair("kotlin.collections.all", "predicate"),
        Pair("kotlin.collections.any", "predicate"),
        Pair("kotlin.collections.associate", "transform"),
        Pair("kotlin.collections.associateBy", "keySelector"),
        Pair("kotlin.collections.associateBy", "valueTransform"),
        Pair("kotlin.collections.associateByTo", "valueTransform"),
        Pair("kotlin.collections.associateByTo", "keySelector"),
        Pair("kotlin.collections.associateTo", "transform"),
        Pair("kotlin.collections.associateWith", "valueSelector"),
        Pair("kotlin.collections.associateWithTo", "valueSelector"),
        Pair("kotlin.collections.binarySearch", "comparison"),
        Pair("kotlin.collections.binarySearchBy", "selector"),
        Pair("kotlin.collections.buildList", "builderAction"),
        Pair("kotlin.collections.buildMap", "builderAction"),
        Pair("kotlin.collections.buildSet", "builderAction"),
        Pair("kotlin.collections.chunked", "transform"),
        Pair("kotlin.collections.count", "predicate"),
        Pair("kotlin.collections.distinctBy", "selector"),
        Pair("kotlin.collections.dropLastWhile", "predicate"),
        Pair("kotlin.collections.dropWhile", "predicate"),
        Pair("kotlin.collections.elementAtOrElse", "defaultValue"),
        Pair("kotlin.collections.filter", "predicate"),
        Pair("kotlin.collections.filterIndexed", "predicate"),
        Pair("kotlin.collections.filterIndexedTo", "predicate"),
        Pair("kotlin.collections.filterKeys", "predicate"),
        Pair("kotlin.collections.filterNot", "predicate"),
        Pair("kotlin.collections.filterNotTo", "predicate"),
        Pair("kotlin.collections.filterTo", "predicate"),
        Pair("kotlin.collections.filterValues", "predicate"),
        Pair("kotlin.collections.find", "predicate"),
        Pair("kotlin.collections.findLast", "predicate"),
        Pair("kotlin.collections.first", "predicate"),
        Pair("kotlin.collections.firstNotNullOf", "transform"),
        Pair("kotlin.collections.firstNotNullOfOrNull", "transform"),
        Pair("kotlin.collections.firstOrNull", "predicate"),
        Pair("kotlin.collections.flatMap", "transform"),
        Pair("kotlin.collections.flatMapIndexed", "transform"),
        Pair("kotlin.collections.flatMapIndexedTo", "transform"),
        Pair("kotlin.collections.flatMapTo", "transform"),
        Pair("kotlin.collections.fold", "operation"),
        Pair("kotlin.collections.fold", "initialValueSelector"),
        Pair("kotlin.collections.foldIndexed", "operation"),
        Pair("kotlin.collections.foldRight", "operation"),
        Pair("kotlin.collections.foldRightIndexed", "operation"),
        Pair("kotlin.collections.foldTo", "operation"),
        Pair("kotlin.collections.foldTo", "initialValueSelector"),
        Pair("kotlin.collections.forEach", "action"),
        Pair("kotlin.collections.forEachIndexed", "action"),
        Pair("kotlin.collections.getOrElse", "defaultValue"),
        Pair("kotlin.collections.getOrPut", "defaultValue"),
        Pair("kotlin.collections.groupBy", "keySelector"),
        Pair("kotlin.collections.groupBy", "valueTransform"),
        Pair("kotlin.collections.groupByTo", "keySelector"),
        Pair("kotlin.collections.groupByTo", "valueTransform"),
        Pair("kotlin.collections.groupingBy", "keySelector"),
        Pair("kotlin.collections.ifEmpty", "defaultValue"),
        Pair("kotlin.collections.indexOfFirst", "predicate"),
        Pair("kotlin.collections.indexOfLast", "predicate"),
        Pair("kotlin.collections.iterable", "iterator"),
        Pair("kotlin.collections.joinTo", "transform"),
        Pair("kotlin.collections.joinToString", "transform"),
        Pair("kotlin.collections.last", "predicate"),
        Pair("kotlin.collections.lastOrNull", "predicate"),
        Pair("kotlin.collections.List", "init"),
        Pair("kotlin.collections.Map", "transform"),
        Pair("kotlin.collections.MapIndexed", "transform"),
        Pair("kotlin.collections.MapIndexedNotNull", "transform"),
        Pair("kotlin.collections.MapIndexedNotNullTo", "transform"),
        Pair("kotlin.collections.mapKeys", "transform"),
        Pair("kotlin.collections.mapKeysTo", "transform"),
        Pair("kotlin.collections.mapNotNull", "transform"),
        Pair("kotlin.collections.mapNotNullTo", "transform"),
        Pair("kotlin.collections.mapTo", "transform"),
        Pair("kotlin.collections.mapValues", "transform"),
        Pair("kotlin.collections.mapValuesTo", "transform"),
        Pair("kotlin.collections.maxBy", "selector"),
        Pair("kotlin.collections.maxByOrNull", "selector"),
        Pair("kotlin.collections.maxOf", "selector"),
        Pair("kotlin.collections.maxOfOrNull", "selector"),
        Pair("kotlin.collections.maxOfWith", "selector"),
        Pair("kotlin.collections.maxOfWithOrNull", "selector"),
        Pair("kotlin.collections.minBy", "selector"),
        Pair("kotlin.collections.minByOrNull", "selector"),
        Pair("kotlin.collections.minOf", "selector"),
        Pair("kotlin.collections.minOfOrNull", "selector"),
        Pair("kotlin.collections.minOfOrWith", "selector"),
        Pair("kotlin.collections.minOfOrWithOrNull", "selector"),
        Pair("kotlin.collections.MutableList", "init"),
        Pair("kotlin.collections.none", "predicate"),
        Pair("kotlin.collections.onEach", "action"),
        Pair("kotlin.collections.onEachIndexed", "action"),
        Pair("kotlin.collections.partition", "predicate"),
        Pair("kotlin.collections.reduce", "operation"),
        Pair("kotlin.collections.reduceIndexed", "operation"),
        Pair("kotlin.collections.reduceIndexedOrNull", "operation"),
        Pair("kotlin.collections.reduceOrNull", "operation"),
        Pair("kotlin.collections.reduceRight", "operation"),
        Pair("kotlin.collections.reduceRightIndexed", "operation"),
        Pair("kotlin.collections.reduceRightIndexedOrNull", "operation"),
        Pair("kotlin.collections.reduceRightOrNull", "operation"),
        Pair("kotlin.collections.reduceTo", "operation"),
        Pair("kotlin.collections.removeAll", "predicate"),
        Pair("kotlin.collections.replaceAll", "transformation"),
        Pair("kotlin.collections.retainAll", "predicate"),
        Pair("kotlin.collections.runningFold", "operation"),
        Pair("kotlin.collections.runningFoldIndexed", "operation"),
        Pair("kotlin.collections.runningReduce", "operation"),
        Pair("kotlin.collections.runningReduceIndexed", "operation"),
        Pair("kotlin.collections.scan", "operation"),
        Pair("kotlin.collections.scanIndexed", "operation"),
        Pair("kotlin.collections.single", "predicate"),
        Pair("kotlin.collections.singleOrNull", "predicate"),
        Pair("kotlin.collections.sort", "comparison"),
        Pair("kotlin.collections.sortBy", "selector"),
        Pair("kotlin.collections.sortByDescending", "selector"),
        Pair("kotlin.collections.sortedBy", "selector"),
        Pair("kotlin.collections.sortedByDescending", "selector"),
        Pair("kotlin.collections.sumBy", "selector"),
        Pair("kotlin.collections.sumByDouble", "selector"),
        Pair("kotlin.collections.sumOf", "selector"),
        Pair("kotlin.collections.takeLastWhile", "predicate"),
        Pair("kotlin.collections.takeWhile", "predicate"),
        Pair("kotlin.collections.windowed", "transform"),
        Pair("kotlin.collections.withDefault", "defaultValue"),
        Pair("kotlin.collections.zip", "transform"),
        Pair("kotlin.collections.zipWithNext", "transform"),
        // kotlin sequence https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/
        Pair("kotlin.sequences.all", "predicate"),
        Pair("kotlin.sequences.any", "predicate"),
        Pair("kotlin.sequences.associate", "transform"),
        Pair("kotlin.sequences.associateBy", "transform"),
        Pair("kotlin.sequences.associateBy", "keySelector"),
        Pair("kotlin.sequences.associateBy", "valueSelector"),
        Pair("kotlin.sequences.associateByTo", "valueSelector"),
        Pair("kotlin.sequences.associateByTo", "keySelector"),
        Pair("kotlin.sequences.associateTo", "transform"),
        Pair("kotlin.sequences.associateWith", "valueSelector"),
        Pair("kotlin.sequences.associateWithTo", "valueSelector"),
        Pair("kotlin.sequences.buildIterator", "builderAction"),
        Pair("kotlin.sequences.buildSequence", "builderAction"),
        Pair("kotlin.sequences.chunked", "transform"),
        Pair("kotlin.sequences.count", "predicate"),
        Pair("kotlin.sequences.distinctBy", "selector"),
        Pair("kotlin.sequences.dropWhile", "predicate"),
        Pair("kotlin.sequences.elementAtOrElse", "defaultValue"),
        Pair("kotlin.sequences.filter", "predicate"),
        Pair("kotlin.sequences.filterIndexed", "predicate"),
        Pair("kotlin.sequences.filterIndexedTo", "predicate"),
        Pair("kotlin.sequences.filterNot", "predicate"),
        Pair("kotlin.sequences.filterNotTo", "predicate"),
        Pair("kotlin.sequences.filterTo", "predicate"),
        Pair("kotlin.sequences.find", "predicate"),
        Pair("kotlin.sequences.findLast", "predicate"),
        Pair("kotlin.sequences.first", "predicate"),
        Pair("kotlin.sequences.firstNotNullOf", "predicate"),
        Pair("kotlin.sequences.firstNotNullOfOrNull", "predicate"),
        Pair("kotlin.sequences.flatMap", "transform"),
        Pair("kotlin.sequences.flatMapIndexed", "transform"),
        Pair("kotlin.sequences.flatMapIndexedTo", "transform"),
        Pair("kotlin.sequences.flatMapTo", "transform"),
        Pair("kotlin.sequences.fold", "operation"),
        Pair("kotlin.sequences.foldIndexed", "operation"),
        Pair("kotlin.sequences.forEach", "action"),
        Pair("kotlin.sequences.forEachIndexed", "action"),
        Pair("kotlin.sequences.generateSequence", "nextFunction"),
        Pair("kotlin.sequences.groupBy", "keySelector"),
        Pair("kotlin.sequences.groupBy", "valueTransform"),
        Pair("kotlin.sequences.groupByTo", "keySelector"),
        Pair("kotlin.sequences.groupByTo", "valueTransform"),
        Pair("kotlin.sequences.groupingBy", "keySelector"),
        Pair("kotlin.sequences.ifEmpty", "defaultValue"),
        Pair("kotlin.sequences.indexOfFirst", "predicate"),
        Pair("kotlin.sequences.indexOfLast", "predicate"),
        Pair("kotlin.sequences.joinTo", "transform"),
        Pair("kotlin.sequences.joinToString", "transform"),
        Pair("kotlin.sequences.last", "predicate"),
        Pair("kotlin.sequences.lastOrNull", "predicate"),
        Pair("kotlin.sequences.map", "transform"),
        Pair("kotlin.sequences.mapIndexed", "transform"),
        Pair("kotlin.sequences.mapIndexedNotNull", "transform"),
        Pair("kotlin.sequences.mapIndexedNotNullTo", "transform"),
        Pair("kotlin.sequences.mapIndexedTo", "transform"),
        Pair("kotlin.sequences.mapNotNull", "transform"),
        Pair("kotlin.sequences.mapNotNullTo", "transform"),
        Pair("kotlin.sequences.mapTo", "transform"),
        Pair("kotlin.sequences.maxBy", "selector"),
        Pair("kotlin.sequences.maxByOrNull", "selector"),
        Pair("kotlin.sequences.maxOf", "selector"),
        Pair("kotlin.sequences.maxOfOrNull", "selector"),
        Pair("kotlin.sequences.maxOfWith", "selector"),
        Pair("kotlin.sequences.maxOfWithOrNull", "selector"),
        Pair("kotlin.sequences.minBy", "selector"),
        Pair("kotlin.sequences.minByOrNull", "selector"),
        Pair("kotlin.sequences.minOf", "selector"),
        Pair("kotlin.sequences.minOfOrNull", "selector"),
        Pair("kotlin.sequences.minOfWith", "selector"),
        Pair("kotlin.sequences.minOfWithOrNull", "selector"),
        Pair("kotlin.sequences.none", "predicate"),
        Pair("kotlin.sequences.onEach", "action"),
        Pair("kotlin.sequences.onEachIndexed", "action"),
        Pair("kotlin.sequences.partition", "predicate"),
        Pair("kotlin.sequences.reduce", "operation"),
        Pair("kotlin.sequences.reduceIndexed", "operation"),
        Pair("kotlin.sequences.reduceIndexedOrNull", "operation"),
        Pair("kotlin.sequences.reduceOrNull", "operation"),
        Pair("kotlin.sequences.runningFold", "operation"),
        Pair("kotlin.sequences.runningFoldIndexed", "operation"),
        Pair("kotlin.sequences.runningReduce", "operation"),
        Pair("kotlin.sequences.runningReduceIndexed", "operation"),
        Pair("kotlin.sequences.scan", "operation"),
        Pair("kotlin.sequences.scanIndexed", "operation"),
        Pair("kotlin.sequences.Sequence", "iterator"),
        Pair("kotlin.sequences.single", "predicate"),
        Pair("kotlin.sequences.singleOrNull", "predicate"),
        Pair("kotlin.sequences.sortedBy", "selector"),
        Pair("kotlin.sequences.sortedByDescending", "selector"),
        Pair("kotlin.sequences.sumBy", "selector"),
        Pair("kotlin.sequences.sumByDouble", "selector"),
        Pair("kotlin.sequences.sumOf", "selector"),
        Pair("kotlin.sequences.takeWhile", "predicate"),
        Pair("kotlin.sequences.windowed", "transform"),
        Pair("kotlin.sequences.zip", "transform"),
        Pair("kotlin.sequences.zipWithNext", "transform"),
        // kotlin text https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/
        Pair("kotlin.text.all", "predicate"),
        Pair("kotlin.text.any", "predicate"),
        Pair("kotlin.text.associate", "transform"),
        Pair("kotlin.text.associateBy", "keySelector"),
        Pair("kotlin.text.associateBy", "valueTransform"),
        Pair("kotlin.text.associateByTo", "keySelector"),
        Pair("kotlin.text.associateByTo", "valueTransform"),
        Pair("kotlin.text.associateTo", "transform"),
        Pair("kotlin.text.associateWith", "valueSelector"),
        Pair("kotlin.text.associateWithTo", "valueSelector"),
        Pair("kotlin.text.buildString", "builderAction"),
        Pair("kotlin.text.chunked", "transform"),
        Pair("kotlin.text.chunkedSequence", "transform"),
        Pair("kotlin.text.count", "predicate"),
        Pair("kotlin.text.dropLastWhile", "predicate"),
        Pair("kotlin.text.dropWhile", "predicate"),
        Pair("kotlin.text.filter", "predicate"),
        Pair("kotlin.text.filterIndexed", "predicate"),
        Pair("kotlin.text.filterIndexedTo", "predicate"),
        Pair("kotlin.text.filterNot", "predicate"),
        Pair("kotlin.text.filterNotTo", "predicate"),
        Pair("kotlin.text.filterTo", "predicate"),
        Pair("kotlin.text.find", "predicate"),
        Pair("kotlin.text.findLast", "predicate"),
        Pair("kotlin.text.first", "predicate"),
        Pair("kotlin.text.firstNotNullOf", "predicate"),
        Pair("kotlin.text.firstNotNullOfOrNull", "predicate"),
        Pair("kotlin.text.firstOrNull", "predicate"),
        Pair("kotlin.text.flatMap", "transform"),
        Pair("kotlin.text.flatMapIndexed", "transform"),
        Pair("kotlin.text.flatMapIndexedTo", "transform"),
        Pair("kotlin.text.flatMapTo", "transform"),
        Pair("kotlin.text.fold", "operation"),
        Pair("kotlin.text.foldIndexed", "operation"),
        Pair("kotlin.text.foldRight", "operation"),
        Pair("kotlin.text.foldRightIndexed", "operation"),
        Pair("kotlin.text.forEach", "action"),
        Pair("kotlin.text.forEachIndexed", "action"),
        Pair("kotlin.text.getOrElse", "defaultValue"),
        Pair("kotlin.text.groupBy", "keySelector"),
        Pair("kotlin.text.groupBy", "valueTransformer"),
        Pair("kotlin.text.groupByTo", "valueTransformer"),
        Pair("kotlin.text.groupByTo", "keySelector"),
        Pair("kotlin.text.groupingBy", "keySelector"),
        Pair("kotlin.text.ifBlank", "defaultValue"),
        Pair("kotlin.text.ifEmpty", "defaultValue"),
        Pair("kotlin.text.indexOfFirst", "predicate"),
        Pair("kotlin.text.indexOfLast", "predicate"),
        Pair("kotlin.text.last", "predicate"),
        Pair("kotlin.text.lastOrNull", "predicate"),
        Pair("kotlin.text.map", "transform"),
        Pair("kotlin.text.mapIndexed", "transform"),
        Pair("kotlin.text.mapIndexedNotNull", "transform"),
        Pair("kotlin.text.mapIndexedNotNullTo", "transform"),
        Pair("kotlin.text.mapIndexedTo", "transform"),
        Pair("kotlin.text.mapNotNull", "transform"),
        Pair("kotlin.text.mapNotNullTo", "transform"),
        Pair("kotlin.text.mapTo", "transform"),
        Pair("kotlin.text.maxBy", "selector"),
        Pair("kotlin.text.maxByOrNull", "selector"),
        Pair("kotlin.text.maxOf", "selector"),
        Pair("kotlin.text.maxOfOrNull", "selector"),
        Pair("kotlin.text.maxOfOrWith", "selector"),
        Pair("kotlin.text.maxOfOrWithOrNull", "selector"),
        Pair("kotlin.text.minBy", "selector"),
        Pair("kotlin.text.minByOrNull", "selector"),
        Pair("kotlin.text.minOf", "selector"),
        Pair("kotlin.text.minOfOrNull", "selector"),
        Pair("kotlin.text.minOfWith", "selector"),
        Pair("kotlin.text.minOfWithOrNull", "selector"),
        Pair("kotlin.text.none", "selector"),
        Pair("kotlin.text.onEach", "action"),
        Pair("kotlin.text.onEachIndexed", "action"),
        Pair("kotlin.text.partition", "predicate"),
        Pair("kotlin.text.reduce", "operation"),
        Pair("kotlin.text.reduceIndexed", "operation"),
        Pair("kotlin.text.reduceIndexedOrNull", "operation"),
        Pair("kotlin.text.reduceOrNull", "operation"),
        Pair("kotlin.text.reduceRight", "operation"),
        Pair("kotlin.text.reduceRightIndexed", "operation"),
        Pair("kotlin.text.reduceRightIndexedOrNull", "operation"),
        Pair("kotlin.text.replace", "transform"),
        Pair("kotlin.text.replaceFirstChar", "transform"),
        Pair("kotlin.text.runningFold", "operation"),
        Pair("kotlin.text.runningFoldIndexed", "operation"),
        Pair("kotlin.text.runningReduce", "operation"),
        Pair("kotlin.text.runningReduceIndexed", "operation"),
        Pair("kotlin.text.scan", "operation"),
        Pair("kotlin.text.scanIndexed", "operation"),
        Pair("kotlin.text.single", "predicate"),
        Pair("kotlin.text.singleOrNull", "predicate"),
        Pair("kotlin.text.sumBy", "selector"),
        Pair("kotlin.text.sumByDouble", "selector"),
        Pair("kotlin.text.sumOf", "selector"),
        Pair("kotlin.text.takeLastWhile", "predicate"),
        Pair("kotlin.text.takeWhile", "predicate"),
        Pair("kotlin.text.trim", "predicate"),
        Pair("kotlin.text.trimEnd", "predicate"),
        Pair("kotlin.text.trimStart", "predicate"),
        Pair("kotlin.text.windowed", "transform"),
        Pair("kotlin.text.windowedSequence", "transform"),
        Pair("kotlin.text.zip", "transform"),
        Pair("kotlin.text.zipWithNext", "transform"),


        )
}