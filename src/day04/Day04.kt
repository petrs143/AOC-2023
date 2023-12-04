package day04

import java.io.File
import kotlin.math.pow

fun main() {
    val input = File("src/day04/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Int {
    return lines.sumOf { line ->
        val (winningNumbers, ourNumbers) = line.split(":")
            .last()
            .split("|")
            .map { it.trim().split(" ").mapNotNull { it.toIntOrNull() } }
        
        val count = ourNumbers.count { it in winningNumbers }
        
        2.0.pow(count - 1).toInt()
    }
}

private fun part2(lines: List<String>): Int {
    val copies = mutableMapOf<Int, Int>()
    
    lines.forEach { line ->
        val (card, numbers) = line.split(":")
        val cardNumber = card.removePrefix("Card ").trim().toInt()
        copies[cardNumber] = copies.getOrDefault(cardNumber, 0) + 1
        val (winningNumbers, ourNumbers) = numbers.split("|")
            .map { it.trim().split(" ").mapNotNull { it.toIntOrNull() } }
        
        val count = ourNumbers.count { it in winningNumbers }
        
        (1..count).forEach { index ->
            copies[cardNumber + index] = copies.getOrDefault(cardNumber + index, 0) + copies[cardNumber]!!
        }
    }
    
    return copies.values.sum()
}