package org.carbon.handson.ref1

import java.time.LocalDateTime

/**
 * @author Soda 2018/07/16.
 */

// ===================================================================================
//                                                                          Create DTO
//                                                                          ==========
data class Customer(val name: String, val hobby: String)

fun useDTO() {
    val Mary = Customer("Mary", "watching movies")

    // `data` provides following functionality
    Mary.hashCode()
    Mary.toString()
    val Tim = Mary.copy(name = "Tim")
    Mary.equals(Tim)
    val name = Mary.component1()
    val hobby = Mary.component2()
}

// ===================================================================================
//                                                                   Default Parameter
//                                                                          ==========
fun repeatText(text: String = "", times: Int = 10) = text.repeat(times)

// ===================================================================================
//                                                                          Filtering
//                                                                          ==========
val countries = listOf("US", "Japan", "German", "France")

fun filterIdiom() {
    countries
            .filter { it.contains("a") }
            .forEach { println(it) }
}

// ===================================================================================
//                                                                String Interpolation
//                                                                          ==========
val name = "Shota Oda"

fun interpolation() {
    println("my name is $name")
}

// ===================================================================================
//                                                                      Instance Check
//                                                                          ==========
fun checkInstance(obj: Any): String {
    return when (obj) {
        is String -> "obj is character"
        is Int -> "obj is number"
        else -> "obj is unknown type"
    }
}

// ===================================================================================
//                                                                          Traversing
//                                                                          ==========
fun traversing() {
    val worldCups = mapOf(
            "German" to 2006,
            "Republic of South Africa" to 2010,
            "Brazil" to 2014,
            "Russia" to 2018
    )

    for ((country, year) in worldCups) {
        println("$country World cup was be held in $year")
    }
}

// ===================================================================================
//                                                                         Using Range
//                                                                          ==========
fun useRange() {
    for (x in 1..100) {}      // closed range, include 100
    for (x in 1 until 100) {} // half open range, doesn't include 100
    for (x in 1..100 step 2) {}
    for (x in 100 downTo 1) {}
    if (3 in 1..10) {}
}

// ===================================================================================
//                                                                          Read-Only
//                                                                          ==========
val readOnlyLists = listOf(1, 2, 3, 4, 5)
val readOnlyMaps = mapOf("a" to 1, "b" to 2, "c" to 3)

// ===================================================================================
//                                                                          Access Map
//                                                                          ==========
fun accessMap() {
    val indexOfAInAlphabets = readOnlyMaps["a"]
}

// ===================================================================================
//                                                                       Lazy Property
//                                                                          ==========
fun lazyTestSuccess() {
    val lazyVal: String by lazy {
        throw RuntimeException()
    }
}

fun lazyTestFail() {
    val lazyVal: String by lazy {
        throw RuntimeException()
    }
    println(lazyVal)
}

fun useLazyProperty() {
    lazyTestSuccess()

    try {
        lazyTestFail()
    } catch (e: Throwable) {
        println("[lazy]: lazyTestFail() -> throw RuntimeException")
    }
}

// ===================================================================================
//                                                                          Extension
//                                                                          ==========
// todo[Soda] mmm... this logic is not good enough. Want better solution
fun String.spaceToCamelCase(): String {
    fun String.toUpperCaseLast(): String = this.dropLast(1) + this.last().toUpperCase()
    val regSpaceCamel = "[a-z] [a-z]".toRegex()
    var result = this
    regSpaceCamel.findAll(this).forEach {
        val replacement = it.value.toUpperCaseLast()
        result = result.replaceRange(it.range, replacement)
    }
    return result
}

fun runExtension() {
    println("""[String.spaceToCamelCase]: Kotlin `"This is an apple".spaceToCamelCase() -> `${"This is an apple".spaceToCamelCase()}""")
}

fun main(args: Array<String>) {

    useDTO()

    filterIdiom()

    interpolation()

    checkInstance("")
    checkInstance(LocalDateTime.now())

    traversing()

    useRange()

    useLazyProperty()

    runExtension()

    misc()
}

// ===================================================================================
//                                                                          MISC
//                                                                          ==========
// int coerce
infix fun Int.minusGuaranteeNaturalNumber(value: Int): Int = this.minus(value).coerceAtLeast(0)
fun misc() {
    println("[Int.minusGuaranteeNaturalNumber]: Kotlin `40 minusGuaranteeNaturalNumber 100` -> ${40 minusGuaranteeNaturalNumber  100}")
}
