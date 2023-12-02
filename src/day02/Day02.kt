package day02

import java.io.File

fun main() {
    val input = File("src/day02/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

val rules = mapOf(
    "red" to 12,
    "green" to 13,
    "blue" to 14,
)

fun part1(inputs: List<String>): Int {
    return inputs.mapNotNull { input ->
        val (game, record) = input.split(":")
        val sets = record.split(";")
        val valid = sets.all {
            it.split(",").all {
                val (count, color) = it.trim().split(" ")
                count.toInt() <= rules[color]!!
            }
        }
        game.removePrefix("Game ").toInt().takeIf { valid }
    }.sum()
}

fun part2(inputs: List<String>): Int {
     return inputs.map { input ->
        val (_, record) = input.split(":")
        record
            .split(";")
            .flatMap { it.split(",") }
            .map { it.trim().split(" ") }
            .groupBy { (_, color)  -> color }
            .mapValues {
                it.value.maxOf { (count, _) -> count.toInt() }
            }
            .values
            .reduce { acc, i -> acc * i }
    }.sum()
}
