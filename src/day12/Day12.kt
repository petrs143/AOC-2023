package day12

import java.io.File

fun main() {
    val input = File("src/day12/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Long {
    return lines.sumOf { line ->
        val (r, d) = line.split(" ")
        val records = r.toCharArray()
        val groups = d.split(",").map { it.toInt() }

        memory = Array(records.size) {
            Array(groups.size) { -1 }
        }

        calculateArrangements(records, groups, 0, 0, "")
    }
}

private fun part2(lines: List<String>): Long {
    return lines.sumOf { line ->
        val repeat = 5
        val (r, d) = line.split(" ")
        val records = r.toCharArray().let { arr ->
            buildList {
                repeat(5) {
                    addAll(arr.toList())
                    add('?')
                }
            }.dropLast(1).toCharArray()
        }
        val groups = d.split(",").map { it.toInt() }.let { list ->
            List(list.size * repeat) { index ->
                list[index % list.size]
            }
        }

        memory = Array(records.size) {
            Array(groups.size) { -1 }
        }

        calculateArrangements(records, groups, 0, 0, "")
    }
}

lateinit var memory: Array<Array<Long>>

private fun calculateArrangements(
    records: CharArray,
    groups: List<Int>,
    recordIndex: Int,
    groupIndex: Int,
    currentGroup: String,
): Long {
    fun useDot(): Long {
        var newGroupIndex = groupIndex
        if (currentGroup.isNotEmpty()) {
            if (currentGroup.length == groups[groupIndex]) {
                newGroupIndex++
            } else {
                return 0
            }
        }

        return calculateArrangements(records, groups, recordIndex + 1, newGroupIndex, currentGroup = "")
    }

    fun useBroken(): Long {
        return calculateArrangements(records, groups, recordIndex + 1, groupIndex, currentGroup = currentGroup + "#")
    }

    if (groupIndex >= groups.size) {
        if (currentGroup.isEmpty() && records.drop(recordIndex).all { it == '.' || it == '?' }) {
            return 1
        } else {
            return 0
        }
    }

    if (currentGroup.length > groups[groupIndex]) {
        return 0
    }

    if (recordIndex >= records.size) {
        if (groupIndex < groups.lastIndex) {
            return 0
        } else if (groupIndex == groups.lastIndex && currentGroup.length == groups[groupIndex]) {
            return 1
        } else {
            return 0
        }
    }

    if (currentGroup.isEmpty() && memory[recordIndex][groupIndex] != -1L) {
        return memory[recordIndex][groupIndex]
    }

    val currentSpring = records[recordIndex]
    val result = when (currentSpring) {
        '.' -> useDot()
        '#' -> useBroken()
        '?' -> useBroken() + useDot()
        else -> error("Unknown char $currentSpring")
    }

    if (currentGroup.isEmpty()) {
        memory[recordIndex][groupIndex] = result
    }

    return result
}