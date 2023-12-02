package day01

import java.io.File

fun main() {
    val input = File("src/day01/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.map { line ->
        line.filter { it.isDigit() }
            .let { "${it.first()}${it.last()}" }
    }.sumOf { it.toIntOrNull() ?: 0 }
}


private fun part2(input: List<String>): Int {
    return input.map { line ->
        line.mapDigits()
    }.sum()
}

private val digitMap = mapOf(
    "one" to "1",
    "two" to "2",
    "three" to "3",
    "four" to "4",
    "five" to "5",
    "six" to "6",
    "seven" to "7",
    "eight" to "8",
    "nine" to "9",
)

private fun String.mapDigits(): Int {
    val (fromWordIndex, fromWordValue) = findAnyOf(digitMap.keys) ?: (Int.MAX_VALUE to "")
    val (fromDigitIndex, fromDigitValue) = findAnyOf(digitMap.values) ?: (Int.MAX_VALUE to "")
    
    val (toWordIndex, toWordValue) = findLastAnyOf(digitMap.keys) ?: (Int.MIN_VALUE to "")
    val (toDigitIndex, toDigitValue) = findLastAnyOf(digitMap.values) ?: (Int.MIN_VALUE to "")
    
    val firstDigit = if(fromWordIndex < fromDigitIndex) {
        digitMap[fromWordValue]!!
    } else {
        fromDigitValue
    }
    
    val lastDigit = if(toWordIndex > toDigitIndex) {
        digitMap[toWordValue]!!
    } else {
        toDigitValue
    }
    
    return "$firstDigit$lastDigit".toInt()
}