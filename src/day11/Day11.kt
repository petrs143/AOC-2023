package day11

import java.io.File
import kotlin.math.abs

fun main() {
    val input = File("src/day11/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Long {
    val galaxies = parseGalaxies(lines, 2)
    
    var sum = 0L
    galaxies.forEachIndexed { index, galaxy ->
        sum += (index + 1 until galaxies.size).sumOf {
            galaxy.distaceFrom(galaxies[it])
        }
    }
    
    return sum
}

private fun part2(lines: List<String>): Long {
    val galaxies = parseGalaxies(lines, 1000000)

    var sum = 0L
    galaxies.forEachIndexed { index, galaxy ->
        sum += (index + 1 until galaxies.size).sumOf {
            galaxy.distaceFrom(galaxies[it])
        }
    }

    return sum
}

private fun parseGalaxies(lines: List<String>, expansion: Long): List<Galaxy> {
    val galaxies = mutableListOf<Galaxy>()
    var currentRow = 0L
    
    lines.forEach { line ->
        var currentColumn = 0L
        var isEmptyRow = true
        
        line.forEachIndexed { columnInInput, point ->
            val isEmptyColumn = lines.all { it[columnInInput] == '.' }
            if (isEmptyColumn) {
                currentColumn+= expansion 
            } else if (point == '#') {
                galaxies.add(Galaxy(currentRow, currentColumn))
                isEmptyRow = false
                currentColumn++
            } else {
                currentColumn++
            }
        }
        
        if (isEmptyRow) {
            currentRow+= expansion
        } else {
            currentRow++
        }
    }
    
    return galaxies
}

data class Galaxy(
    val row: Long,
    val column: Long
) {
    
    fun distaceFrom(galaxy: Galaxy): Long {
        return abs(row - galaxy.row) + abs(column - galaxy.column)
    }
}