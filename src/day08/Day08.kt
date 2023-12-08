package day08

import java.io.File

fun main() {
    val input = File("src/day08/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Int {
    val instructions = lines.first().map(Instruction::of)
    val network = Network.of(lines.drop(2))
    
    var current = Network.Begining
    var instructionIndex = 0
    var steps = 0
    while(current != Network.End) {
        current = network.navigate(current, instructions[instructionIndex])
        instructionIndex = (instructionIndex + 1) % instructions.size
        steps++
    }
    
    return steps
}

private fun part2(lines: List<String>): Long {
    val instructions = lines.first().map(Instruction::of)
    val network = Network.of(lines.drop(2))
    
    val ends = mutableMapOf<String, List<Int>>()
    
    var current = network.starts
    var instructionIndex = 0
    var steps = 0
    while(current.isNotEmpty()) {
        current = current.mapIndexedNotNull { index, code ->
            network.navigate(code, instructions[instructionIndex]).let {
                if (it.endsWith('Z')) {
                    val start = network.starts[index]
                    val currentSteps = steps + 1
                    val isAlreadyInMap = ends[start]?.any { it * 2 == currentSteps } ?: false
                    
                    if (isAlreadyInMap) {
                       null 
                    } else {
                        ends[start] = ends[start].orEmpty() + (steps + 1)
                        it
                    }
                } else {
                    it
                }
            }
        }
        instructionIndex = (instructionIndex + 1) % instructions.size
        steps++
    }
    
    return ends.values.map { it.first().toLong() }.lcm()
}

fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun List<Long>.lcm(): Long {
    return fold(1L) { acc, i -> findLCM(acc, i) }
}

enum class Instruction {
    Left, Right;
    
    companion object {
        
        fun of(char: Char): Instruction {
            return when(char) {
                'L' -> Left
                'R' -> Right
                else -> error("Invalid instruction $char")
            }
        }
    }
}

data class Network(
    private val map: Map<String, Pair<String, String>>
) {
    
    val starts by lazy { map.keys.filter { it.endsWith('A') } }
    
    fun navigate(from: String, instruction: Instruction): String {
        return when(instruction) {
            Instruction.Left -> map[from]!!.first
            Instruction.Right -> map[from]!!.second
        }
    }
    
    companion object {
        
        const val Begining = "AAA"
        const val End = "ZZZ"
        
        fun of(lines: List<String>): Network {
            return lines.associate {
                val (from, to) = it.split(" = ")
                val (left, right) = to.removeSurrounding("(", ")")
                    .split(", ")
                    .map { it.trim() }
                val destination = left to right
                from to destination
            }.let(::Network)
        }
    }
}