package day15

import java.io.File

fun main() {
    val input = File("src/day15/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Long {
    return lines.first().split(",").sumOf {
        hash(it)
    }
}

private fun part2(lines: List<String>): Long {
    val commands = lines.first().split(",")

    val boxes = List(256) { Box() }

    commands.forEach { command ->
        if (command.contains("=")) {
            val (label, focalLength) = command.split("=")
            val boxIndex = hash(label)
            boxes[boxIndex.toInt()].add(Lens(label, focalLength.toInt()))
        } else {
            val label = command.removeSuffix("-")
            val boxIndex = hash(label)
            boxes[boxIndex.toInt()].remove(label)
        }
    }

    var boxNumber = 1
    return boxes.sumOf {
        it.power(boxNumber++)
    }
}

private fun hash(text: String): Long {
    var currentValue = 0L

    text.forEach {
        val asciiNumber = it.code
        currentValue += asciiNumber
        currentValue *= 17
        currentValue %= 256
    }

    return currentValue
}

class Box {

    private val lenses = mutableListOf<Lens>()

    fun remove(label: String) {
        lenses.removeIf {
            it.label == label
        }
    }

    fun add(lens: Lens) {
        val index = lenses.indexOfFirst { it.label == lens.label }.takeUnless { it == -1 }

        if (index != null) {
            lenses.removeAt(index)
            lenses.add(index, lens)
        } else {
            lenses.add(lens)
        }
    }

    fun power(boxNumber: Int): Long {
        var slot = 1
        return lenses.sumOf {
            boxNumber * slot++ * it.length.toLong()
        }
    }
}

data class Lens(
        val label: String,
        val length: Int,
)