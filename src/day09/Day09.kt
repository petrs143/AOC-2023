package day09

import java.io.File
import kotlin.math.abs

fun main() {
    val input = File("src/day09/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Long {
    return lines.sumOf {
        var values = it.split(" ").map { it.toLong() }
        val lastNumbers = mutableListOf(values.last())

        while(!values.isCompleted()) {
            values = values.process()
            lastNumbers.add(values.last())
        }
        
        lastNumbers.sum()
    }
}

private fun part2(lines: List<String>): Long {
    return lines.sumOf {
        var values = it.split(" ").map { it.toLong() }.reversed()
        val lastNumbers = mutableListOf(values.last())

        while(!values.isCompleted()) {
            values = values.process()
            lastNumbers.add(values.last())
        }

        lastNumbers.sum()
    }
}

private fun List<Long>.process(): List<Long> {
    return dropLast(1).mapIndexed { index, l -> this[index + 1] - l }
}

private fun List<Long>.isCompleted() = all { it == 0L }