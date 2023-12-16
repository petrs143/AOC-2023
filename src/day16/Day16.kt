package day16

import java.io.File
import java.util.LinkedList
import java.util.Queue

fun main() {
    val input = File("src/day16/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Int {
    val map = Map.of(lines)

    return getEnergizedCount(map, 0, 0, Beam.State.Right)
}

private fun part2(lines: List<String>): Int {
    val map = Map.of(lines)
    var max = map.data.first().indices.maxOf { columnIndex ->
        getEnergizedCount(map, 0, columnIndex, Beam.State.Down)
    }

    max = map.data.last().indices.maxOf { columnIndex ->
        getEnergizedCount(map, map.data.last().lastIndex, columnIndex, Beam.State.Up)

    }.coerceAtLeast(max)

    max = map.data.indices.maxOf { rowIndex ->
        getEnergizedCount(map, rowIndex, 0, Beam.State.Right)
    }.coerceAtLeast(max)

    max = map.data.indices.maxOf { rowIndex ->
        getEnergizedCount(map, rowIndex, map.data.first().lastIndex, Beam.State.Left)
    }.coerceAtLeast(max)

    return max
}

private fun getEnergizedCount(map: Map, startRow: Int, startColumn: Int, state: Beam.State): Int {
    val visited = Array(map.data.size) { row ->
        Array(map.data[row].size) {
            mutableSetOf<Beam.State>()
        }
    }

    val beams: Queue<Beam> = LinkedList()
    beams.add(Beam(startRow, startColumn, state))

    while (beams.isNotEmpty()) {
        val beam = beams.poll()
        if (beam.row in 0..map.data.lastIndex &&
                beam.column in 0..map.data[beam.row].lastIndex &&
                !visited[beam.row][beam.column].contains(beam.state)) {
            visited[beam.row][beam.column] += beam.state
            val newBeams = beam.apply(map.data[beam.row][beam.column])
            beams.addAll(newBeams)
        }
    }

    return visited.sumOf {
        it.count { it.isNotEmpty() }
    }
}

data class Map(
        val data: List<List<Type>>
) {


    companion object {

        fun of(lines: List<String>): Map {
            return lines.map { line ->
                line.map {
                    Type.of(it)
                }
            }.let(::Map)
        }
    }
}

enum class Type(val char: Char) {
    Empty('.'),
    Mirror('/'),
    BackMirror('\\'),
    HorizontalSplitter('-'),
    VerticalSplitter('|');

    companion object {

        fun of(char: Char): Type {
            return entries.first { it.char == char }
        }
    }
}

data class Beam(
        val row: Int,
        val column: Int,
        val state: State,
) {

    fun apply(type: Type) = state.apply(this, type)

    sealed interface State {

        fun apply(beam: Beam, type: Type): List<Beam>

        data object Up : State {
            override fun apply(beam: Beam, type: Type): List<Beam> {
                return when (type) {
                    Type.Mirror -> listOf(beam.copy(column = beam.column + 1, state = Right))
                    Type.BackMirror -> listOf(beam.copy(column = beam.column - 1, state = Left))
                    Type.HorizontalSplitter -> listOf(
                            beam.copy(column = beam.column - 1, state = Left),
                            beam.copy(column = beam.column + 1, state = Right))

                    Type.VerticalSplitter, Type.Empty -> listOf(beam.copy(row = beam.row - 1))
                }
            }
        }

        data object Down : State {

            override fun apply(beam: Beam, type: Type): List<Beam> {
                return when (type) {
                    Type.Mirror -> listOf(beam.copy(column = beam.column - 1, state = Left))
                    Type.BackMirror -> listOf(beam.copy(column = beam.column + 1, state = Right))
                    Type.HorizontalSplitter -> listOf(
                            beam.copy(column = beam.column - 1, state = Left),
                            beam.copy(column = beam.column + 1, state = Right))

                    Type.VerticalSplitter, Type.Empty -> listOf(beam.copy(row = beam.row + 1))
                }
            }
        }

        data object Left : State {
            override fun apply(beam: Beam, type: Type): List<Beam> {
                return when (type) {
                    Type.Mirror -> listOf(beam.copy(row = beam.row + 1, state = Down))
                    Type.BackMirror -> listOf(beam.copy(row = beam.row - 1, state = Up))
                    Type.VerticalSplitter -> listOf(
                            beam.copy(row = beam.row - 1, state = Up),
                            beam.copy(row = beam.row + 1, state = Down)
                    )

                    Type.HorizontalSplitter, Type.Empty -> listOf(beam.copy(column = beam.column - 1))
                }
            }
        }

        data object Right : State {
            override fun apply(beam: Beam, type: Type): List<Beam> {
                return when (type) {
                    Type.Mirror -> listOf(beam.copy(row = beam.row - 1, state = Up))
                    Type.BackMirror -> listOf(beam.copy(row = beam.row + 1, state = Down))
                    Type.VerticalSplitter -> listOf(
                            beam.copy(row = beam.row - 1, state = Up),
                            beam.copy(row = beam.row + 1, state = Down)
                    )

                    Type.HorizontalSplitter, Type.Empty -> listOf(beam.copy(column = beam.column + 1))
                }
            }
        }
    }
}