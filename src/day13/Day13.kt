package day13

import java.io.File
import kotlin.math.min

fun main() {
    val input = File("src/day13/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Int {
    val maps = parseMaps(lines)

    return maps.sumOf { map ->
        val horizontal = map.data.indices.zipWithNext()
                .firstOrNull { map.isHorizontalReflection(it.first, it.second, 0) }
                ?.first
                ?.plus(1) ?: 0

        val vertical = map.data.first().indices.zipWithNext()
                .firstOrNull { map.isVerticalReflection(it.first, it.second, 0) }
                ?.first
                ?.plus(1) ?: 0

        100 * horizontal + vertical
    }
}

private fun part2(lines: List<String>): Int {
    val maps = parseMaps(lines)

    return maps.sumOf { map ->
        val horizontal = map.data.indices.zipWithNext()
                .firstOrNull { map.isHorizontalReflection(it.first, it.second, 1) }
                ?.first
                ?.plus(1) ?: 0

        val vertical = map.data.first().indices.zipWithNext()
                .firstOrNull { map.isVerticalReflection(it.first, it.second, 1) }
                ?.first
                ?.plus(1) ?: 0

        100 * horizontal + vertical
    }
}

private fun parseMaps(lines: List<String>): List<Map> {
    val results = mutableListOf<Map>()

    var currentData = mutableListOf<List<Pattern>>()

    lines.forEach { line ->
        if (line.isEmpty()) {
            results.add(Map(currentData.map { it.toTypedArray() }.toTypedArray()))
            currentData = mutableListOf()
        } else {
            currentData.add(line.map { Pattern.of(it) })
        }
    }

    results.add(Map(currentData.map { it.toTypedArray() }.toTypedArray()))
    return results
}

class Map(
        val data: Array<Array<Pattern>>
) {

    fun isHorizontalReflection(startIndex: Int, endIndex: Int, tolerance: Int): Boolean {
        val length = min(startIndex, data.lastIndex - endIndex)
        return (0..length).sumOf { rowIndex ->
            var index = 0
            data[startIndex - rowIndex].count { pattern -> pattern != data[rowIndex + endIndex][index++] }
        } == tolerance
    }

    fun isVerticalReflection(startIndex: Int, endIndex: Int, tolerance: Int): Boolean {
        val length = min(startIndex, data.first().lastIndex - endIndex)
        return data.sumOf { row ->
            (0..length).count {
                row[startIndex - it] != row[it + endIndex]
            }
        } == tolerance
    }
}

enum class Pattern {
    Ash, Rock;

    fun revert(): Pattern {
        return when (this) {
            Ash -> Rock
            Rock -> Ash
        }
    }

    companion object {

        fun of(char: Char): Pattern {
            return when (char) {
                '.' -> Ash
                '#' -> Rock
                else -> error("Unknown pattern $char")
            }
        }
    }
}