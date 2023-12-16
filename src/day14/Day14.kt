package day14

import java.io.File

fun main() {
    val input = File("src/day14/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Long {
    val map = parse(lines)

    map.first().indices.forEach { columnIndex ->
        var movableStartIndex = -1
        var movableSize = 0

        map.forEachIndexed { rowIndex, row ->
            val current = row[columnIndex]

            when (current) {
                Type.CubeRock -> {
                    if (movableStartIndex != -1) {
                        map.sortVerticalRange(columnIndex, movableStartIndex..(movableStartIndex + movableSize))
                    }

                    movableStartIndex = -1
                    movableSize = 0
                }

                else -> {
                    if (movableStartIndex == -1) {
                        movableStartIndex = rowIndex
                    } else {
                        movableSize++
                    }
                }
            }
        }

        if (movableStartIndex != -1) {
            map.sortVerticalRange(columnIndex, movableStartIndex..(movableStartIndex + movableSize))
        }
    }

    var sum = 0L
    map.forEachIndexed { rowIndex, row ->
        val currentLoad = map.size - rowIndex
        sum += row.sumOf {
            if (it == Type.RoundRock) {
                currentLoad
            } else {
                0
            }
        }
    }

    return sum
}

private fun part2(lines: List<String>): Long {
    val map = parse(lines)
    val cycles = 1000000000

    val horizontalRanges = map.getHorizontalRanges()
    val verticalRanges = map.getVerticalRanges()

    val sums = mutableSetOf<Long>()
    repeat(cycles) {
        println("Cycle: $it - ${it / cycles.toFloat() * 100} %")

        Tilt.entries.forEach { tilt ->
            when (tilt) {
                Tilt.North -> verticalRanges.forEach { (columnIndex, ranges) ->
                    ranges.forEach { range ->
                        map.sortVerticalRange(columnIndex, range)
                    }
                }

                Tilt.West -> horizontalRanges.forEach { (rowIndex, ranges) ->
                    ranges.forEach { range ->
                        map.sortHorizontalRange(rowIndex, range)
                    }
                }

                Tilt.South -> verticalRanges.forEach { (columnIndex, ranges) ->
                    ranges.forEach { range ->
                        map.sortVerticalRange(columnIndex, range, reversed = true)
                    }
                }

                Tilt.East -> horizontalRanges.forEach { (rowIndex, ranges) ->
                    ranges.forEach { range ->
                        map.sortHorizontalRange(rowIndex, range, reversed = true)
                    }
                }
            }
        }

        var sum = 0L
        map.forEachIndexed { rowIndex, row ->
            val currentLoad = map.size - rowIndex
            sum += row.sumOf {
                if (it == Type.RoundRock) {
                    currentLoad
                } else {
                    0
                }
            }
        }

        if (it > 5000) {
            sums.add(sum)
        }

        println(sums)
    }



    map.forEach {
        it.forEach {
            print("${it.char}")
        }
        println()
    }

    return 0L
}

private fun Array<Array<Type>>.getHorizontalRanges(): Map<Int, List<IntRange>> {
    val result = mutableMapOf<Int, List<IntRange>>()

    forEachIndexed { rowIndex, row ->
        var movableStartIndex = -1
        var movableSize = 0

        row.forEachIndexed { columnIndex, type ->
            when (type) {
                Type.CubeRock -> {
                    if (movableStartIndex != -1) {
                        val newRange = movableStartIndex.rangeTo((movableStartIndex + movableSize))
                        result[rowIndex] = result[rowIndex].orEmpty() + listOf(newRange)
                    }

                    movableStartIndex = -1
                    movableSize = 0
                }

                else -> {
                    if (movableStartIndex == -1) {
                        movableStartIndex = columnIndex
                    } else {
                        movableSize++
                    }
                }
            }
        }


        if (movableStartIndex != -1) {
            val newRange = movableStartIndex.rangeTo((movableStartIndex + movableSize))
            result[rowIndex] = result[rowIndex].orEmpty() + listOf(newRange)
        }
    }

    return result
}

private fun Array<Array<Type>>.getVerticalRanges(): Map<Int, List<IntRange>> {
    val result = mutableMapOf<Int, List<IntRange>>()

    first().indices.forEach { columnIndex ->
        var movableStartIndex = -1
        var movableSize = 0

        forEachIndexed { rowIndex, row ->
            val current = row[columnIndex]

            when (current) {
                Type.CubeRock -> {
                    if (movableStartIndex != -1) {
                        val newRange = movableStartIndex..(movableStartIndex + movableSize)
                        result[columnIndex] = result[columnIndex].orEmpty() + listOf(newRange)
                    }

                    movableStartIndex = -1
                    movableSize = 0
                }

                else -> {
                    if (movableStartIndex == -1) {
                        movableStartIndex = rowIndex
                    } else {
                        movableSize++
                    }
                }
            }
        }

        if (movableStartIndex != -1) {
            val newRange = movableStartIndex..(movableStartIndex + movableSize)
            result[columnIndex] = result[columnIndex].orEmpty() + listOf(newRange)
        }
    }

    return result
}

private fun Array<Array<Type>>.sortVerticalRange(columnIndex: Int, range: IntRange, reversed: Boolean = false) {
    val sortedRange = mapIndexedNotNull { rowIndex, row ->
        if (rowIndex in range) {
            row[columnIndex]
        } else {
            null
        }
    }.let {
        if (reversed) {
            it.sortedDescending()
        } else {
            it.sorted()
        }
    }

    sortedRange.forEachIndexed { index, type ->
        this[index + range.first][columnIndex] = type
    }
}

private fun Array<Array<Type>>.sortHorizontalRange(rowIndex: Int, range: IntRange, reversed: Boolean = false) {
    val sortedRange = this[rowIndex].mapIndexedNotNull { columnIndex, type ->
        if (columnIndex in range) {
            type
        } else {
            null
        }
    }.let {
        if (reversed) {
            it.sortedDescending()
        } else {
            it.sorted()
        }
    }

    sortedRange.forEachIndexed { index, type ->
        this[rowIndex][index + range.first] = type
    }
}


private fun parse(lines: List<String>): Array<Array<Type>> {
    return Array(lines.size) { row ->
        Array(lines[row].length) { column ->
            Type.of(lines[row][column])
        }
    }
}

enum class Type(val char: Char) {

    RoundRock('O'), CubeRock('#'), Empty('.');

    companion object {

        fun of(char: Char): Type {
            return entries.first { it.char == char }
        }
    }
}

enum class Tilt {
    North, West, South, East
}