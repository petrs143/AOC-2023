package day05

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    val input = File("src/day05/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Long {
    val seeds = lines.first().removePrefix("seeds: ").split(" ").map { it.toLong() }
    val almanac = readAlmanac(lines)
    return seeds.minOf {
        almanac.apply(it)
    }
}

private fun part2(lines: List<String>): Long {
    val seeds = lines.first().removePrefix("seeds: ").split(" ").map { it.toLong() }.chunked(2).map { (from, length) ->
        SeedRange(from, length)
    }
    val almanac = readAlmanac(lines)
    return runBlocking(Dispatchers.Default) {
        seeds.map {
            it.getAll()
        }.map { range ->
            async {
                range.minOf { seed ->
                    almanac.apply(seed)
                }
            }
        }.awaitAll()
            .min()
    }
}

private fun readAlmanac(lines: List<String>): Almanac {
    val rules = mutableListOf<Rule>()

    lines.listIterator(1)
        .iterator()
        .also { iterator ->
            while (iterator.hasNext()) {
                val line = iterator.next()

                if (line.contains("map")) {
                    var line = iterator.next()
                    val ranges = mutableListOf<Range>()
                    while (iterator.hasNext() && line.isNotBlank() && !line.contains("map")) {
                        ranges += Range.of(line)
                        line = iterator.next()
                    }

                    rules += Rule(ranges)
                }
            }
        }

    return Almanac(rules)
}

data class Almanac(
    val rules: List<Rule>
) {
    
    fun apply(seed: Long): Long {
        return rules.fold(seed) { acc, rule ->
            rule.apply(acc)
        }
    }
}

data class Rule(
    val ranges: List<Range>
) {
    
    fun apply(from: Long): Long {
        ranges.forEach {
            if (it.mathes(from)) {
                return it.apply(from)
            }
        }
        
        return from
    }
}

data class Range(
    val destination: Long,
    val source: Long,
    val length: Long
) {
    
    fun mathes(target: Long): Boolean {
        return target in this.source until (source + length)
    }
    
    fun apply(target: Long): Long {
        val delta = target - source
        return destination + delta
    }
    
    companion object {
        
        fun of(line: String): Range {
            val items = line.split(" ")
            return Range(items[0].toLong(), items[1].toLong(), items[2].toLong())
        }
    }
}

data class SeedRange(
    val from: Long,
    val lenght: Long
)  {
    
    fun getAll(): LongRange {
        return (from until (from+lenght))
    }
}