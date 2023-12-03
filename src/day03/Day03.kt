package day03

import java.io.File

fun main() {
    val input = File("src/day03/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Long {
    val matrix = Array(lines.size) { rowIndex ->
        lines[rowIndex].toCharArray()
    }
    
    val numbers = mutableListOf<Number>()
    
    matrix.forEachIndexed { rowIndex, row ->
        var currentNumber = ""
        
        row.forEachIndexed { columnIndex, char ->
            if (char.isDigit()) {
                currentNumber+= char
            } else {
                getNumber(currentNumber, matrix, columnIndex, rowIndex)?.also { numbers.add(it) }
                currentNumber = ""
            }
        }
        
        getNumber(currentNumber, matrix, row.size, rowIndex)?.also { numbers.add(it) }
    }
    
    return numbers.filter { it.hasAdjectedSymbol }
        .sumOf { it.value }
}

private fun getNumber(currentNumber: String, matrix: Array<CharArray>, columnIndex: Int, rowIndex: Int): Number? {
    return if (currentNumber.isNotEmpty()) {
        val startColumnIndex = (columnIndex - currentNumber.length - 1).coerceAtLeast(0)
        val row = matrix[rowIndex]
        val endColumnIndex = columnIndex.coerceAtMost(row.lastIndex)

        val topRow = matrix.getOrNull(rowIndex - 1)?.copyOfRange(startColumnIndex, endColumnIndex + 1)?.toList().orEmpty()
        val middleRow = listOf(row[startColumnIndex], row[endColumnIndex])
        val bottomRow = matrix.getOrNull(rowIndex + 1)?.copyOfRange(startColumnIndex, endColumnIndex + 1)?.toList().orEmpty()
        
        val topGears = topRow.mapIndexedNotNull { index, c ->
            c.checkGear(rowIndex - 1, startColumnIndex + index)
        }
        val middleGears = listOfNotNull(
            row[startColumnIndex].checkGear(rowIndex, startColumnIndex),
            row[endColumnIndex].checkGear(rowIndex, endColumnIndex),
        )
        val bottomGears = bottomRow.mapIndexedNotNull { index, c ->
            c.checkGear(rowIndex + 1, startColumnIndex + index)
        }
        
        Number(currentNumber.toLong(), topRow + middleRow + bottomRow, topGears + middleGears + bottomGears)
    } else {
        null
    }
}

private fun Char.checkGear(rowIndex: Int, columnIndex: Int): Position? {
   return  if (this == '*') {
        Position(rowIndex, columnIndex)
    } else {
        null
    }
}

private fun part2(lines: List<String>): Long {
    val matrix = Array(lines.size) { rowIndex ->
        lines[rowIndex].toCharArray()
    }

    val numbers = mutableListOf<Number>()

    matrix.forEachIndexed { rowIndex, row ->
        var currentNumber = ""

        row.forEachIndexed { columnIndex, char ->
            if (char.isDigit()) {
                currentNumber+= char
            } else {
                getNumber(currentNumber, matrix, columnIndex, rowIndex)?.also { numbers.add(it) }
                currentNumber = ""
            }
        }

        getNumber(currentNumber, matrix, row.size, rowIndex)?.also { numbers.add(it) }
    }

    return numbers.groupBy { it.gearPositions }
        .filterKeys { it.isNotEmpty() }
        .values.sumOf { numbers ->
            if (numbers.size == 2) {
                numbers.first().value * numbers.last().value
            } else {
                0
            }
        }
}

data class Number(
    val value: Long,
    val neighbours: List<Char>,
    val gearPositions: List<Position>,
) {
    
    val hasAdjectedSymbol = neighbours.any { it != '.' && !it.isDigit() }
}

data class Position(val row: Int, val column: Int)