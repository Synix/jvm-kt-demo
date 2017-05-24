package io.github.synix.main

fun main(args: Array<String>) {
    println("Hello, Kotlin!")
    args.forEachIndexed { index, arg ->
        println("args[$index] is $arg")
    }
}
