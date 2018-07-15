package org.carbon.handson

import java.util.*

/**
 * @author garden 2018/07/12~7/14.
 */

// fun with body
fun sum(a: Int, b: Int): Int {
    return a + b
}

// fun with expression with inferred
fun sumByExp(a: Int, b: Int) = a + b

// unit fun
fun noReturn(): Unit {
    println("just print function")
}

fun noReturnOmitReturnType(): Unit {
    println("just print function omitting return type")
}

fun defImmutableVariable() {
    // immediate assignment
    val a: Int = 123

    // 'Int' type is inferred
    val b = 123

    // type is required if no initial value provided
    val c: Int

    // deferred assignment
    c = 123
}

fun defMutableVariable() {
    var a = 123
    a++
    a += 123
}

/*
   block comment
    /*
        nested block comment
    */
 */

/**
 * doc <br/>
 * /**
 *  nested doc
 * */
 */
val filed = ""

// ===================================================================================
//                                                                     string template
//                                                                          ==========
// simple use
val a = 123
val simpleStrTemplate = "val a is ($a)"
// expression use
val expStrTemplate = "val a ^ 2 is ${a * a}"


// ===================================================================================
//                                                                          conditions
//                                                                          ==========
fun checkNumber(x: Int): String {
    if (x > 0) {
        return "probably natural number"
    } else {
        return "arg x is less than or equal 0"
    }
}

val conditionalExpression = if (Random().nextInt() % 2 == 0) "even" else "odd"

// cf. try expression
val tryExpression = { str: String ->
    try {
        Integer.parseInt(str)
    } catch (e: NumberFormatException) {
        null
    }
}

// ===================================================================================
//                                                                          null check
//                                                                          ==========
fun nullHandling(): Int {
    val valX = tryExpression("123")
    val valY = tryExpression("hoge")

    // compile error
    // println(valX * valY);

    return if (valX != null && valY != null) valX * valY else 0
}

// ===================================================================================
//                                                                          Pattern Matching
//                                                                          ==========
fun matchPattern(obj: Any) = when (obj) {
    "hello" -> "English"
    "こんちには" -> "Japanese"
    is Int -> "obj is int"
    is String -> "obj is int"
    else -> "unknown..."
}

// ===================================================================================
//                                                                          Range
//                                                                          ==========
val end = 100
val checkIn1to100 = { x: Int -> (if (x in 1..end) "x is in" else "x is not in") + "1 to 100" }
fun range() {
    println(checkIn1to100(50))
    println(checkIn1to100(123))
}

// range and progression
fun progression() {
    println("for(x in 1..end step 3)")
    for (x in 1..end step 3) {
        println(x)
    }

    println("for (x in end downTo 1 step 2 )")
    for (x in end downTo 1 step 2) {
        println(x)
    }

    println("for (x in (1..end).reversed() ) {")
    for (x in (1..end).reversed()) {
        println(x)
    }
}

// ===================================================================================
//                                                                          Extension
//                                                                          ==========
// extend fun
fun <T> List<T>.indexOfInverse(value: T): Int = size - indexOf(value)
// extend property
val <T> List<T>.lastIndex: Int
    get() = size - 1

// ===================================================================================
//                                                                          Collection
//                                                                          ==========
val list = listOf("a", "b", "c", "d")

// simple use
fun useSimpleList() {
    for (x in list) {
        println(x)
    }
}

// pattern matching
fun usePatterMatcing(): String {
    return when {
        "a" in list -> "a is in list"
        "f" in list -> "f is in list"
        else -> "list doesn't contain a or f"
    }
}

// lambda
fun useLambda() {
    val fruits = listOf("apple" , "banana", "lemon", "grape")
    fruits
        .filter { it.contains("a") }
        .sortedBy { it }
        .map { it.toUpperCase() }
        .forEach { println(it) }

    val a: Int.(a: Int) -> Int = {a: Int -> 1}
}

// -----------------------------------------------------
//                                     [misc] scala like
//                                               -------
class ImplicitUnderScore<out T>(private val val1: T, private val val2: T) {
    private var refCnt: Int = 0
    val _u:  T // oops... Cannot use _ as var name
        get() = when(refCnt++) {
                0 -> val1
                1 -> val2
                else -> throw IllegalStateException()
            }
}

fun <T> List<T>.implicitReduce(implicitUnderScore: ImplicitUnderScore<T>.() -> T): T =
    reduce { acc, t ->  ImplicitUnderScore(acc, t).implicitUnderScore()}

fun main(args: Array<String>) {

    println("hello world !")

    sum(10, 20)

    sumByExp(10, 20)

    noReturn()

    noReturnOmitReturnType()
}