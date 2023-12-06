package day06

import java.io.File

fun main() {
    val input = File("src/day06/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Int {
    val times = lines.first().removePrefix("Time:")
        .split(" ")
        .mapNotNull { it.trim().toIntOrNull()}
    val distances = lines.last().removePrefix("Distance:")
        .split(" ")
        .mapNotNull { it.trim().toIntOrNull()}
    val races = times.zip(distances)
    
   return races.map { (time, distance) ->
        (0..time).count { chargeTime ->
            chargeTime * (time - chargeTime) > distance
        }
    }.reduce { acc, i -> acc * i }
}

private fun part2(lines: List<String>): Int {
    val time = lines.first().removePrefix("Time:")
        .replace(" ", "")
        .toLong()
    
    val distance = lines.last().removePrefix("Distance:")
        .replace(" ", "")
        .toLong()
    
    return (0..time).count { chargeTime ->
        chargeTime * (time - chargeTime) > distance
    }
}