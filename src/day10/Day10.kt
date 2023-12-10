package day10

import java.io.File
import java.io.PrintWriter
import java.util.LinkedList
import java.util.Queue

fun main() {
    val input = File("src/day10/input.txt").readLines()
    solve(input)
}

private fun solve(lines: List<String>) {
    val map = Map.of(lines)
    
    val loopDistances = mutableListOf<Int>()
    
    val positionQueue: Queue<Position> = LinkedList()
    positionQueue.add(map.startPosition)
    map.pipes[map.startPosition.row][map.startPosition.column] = map.pipes[map.startPosition.row][map.startPosition.column].changeVisitedAt(0)
    while(positionQueue.isNotEmpty()) {
        val currenPosition = positionQueue.poll()
        val currentSteps = map.pipes[currenPosition.row][currenPosition.column].visitedAt + 1
        
        val newPositions = map.pipes[currenPosition.row][currenPosition.column]
            .apply(currenPosition)
            .filter { map.isValid(it) }
            .filter { position ->
                map.pipes[position.row][position.column].apply(position).contains(currenPosition)
            }
            .filter { position ->
                if (map.pipes[position.row][position.column].visitedAt == currentSteps) {
                    loopDistances.add(currentSteps)
                }

                map.pipes[position.row][position.column].visitedAt == -1 || map.pipes[position.row][position.column].visitedAt >= currentSteps
            }
            
        newPositions.forEach { position ->
            map.pipes[position.row][position.column] = map.pipes[position.row][position.column].changeVisitedAt(currentSteps)
        }
        
        positionQueue.addAll(newPositions)
    }
    
    var inLoopCount = 0
    map.pipes.forEachIndexed { rowIndex, row ->
        var intersections = 0
        row.forEachIndexed { columnIndex, pipe ->
            if (pipe.visitedAt != -1) {
                if (pipe is Pipe.Vertical || pipe is Pipe.F || pipe is Pipe.Seven || pipe is Pipe.Start) {
                    intersections++
                }
            } else {
                if (intersections % 2 == 1) {
                    inLoopCount++
                    map.pipes[rowIndex][columnIndex] = pipe.setAsInLoop()
                } 
            }
        }
    }
    
    val writer = PrintWriter("src/day10/map.txt")
    map.pipes.forEach { row ->
        row.forEach {
            if (it.visitedAt != -1) {
                writer.print(".")
            } else if (it.inLoop){
                writer.print("#")
            } else {
                writer.print(" ")
            }
        }
        writer.println()
    }
    writer.close()
    
    println(loopDistances.max())
    println(inLoopCount)
}


data class Map(
    val pipes: Array<Array<Pipe>>,
    val startPosition: Position,
) {
    
    fun isValid(position: Position): Boolean {
        return position.row in pipes.indices &&
               position.column in pipes[position.row].indices
    }
    
    companion object {
        
        fun of(lines: List<String>): Map {
            val pipes = lines.map {
                it.toCharArray().map { Pipe.of(it) }.toTypedArray()
            }.toTypedArray()
            
            var currenPosition: Position = Position(-1, -1)
            pipes.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { columnIndex, pipe ->
                    if (pipe is Pipe.Start) {
                        currenPosition = Position(rowIndex, columnIndex)
                    }
                }
            }
            
            return Map(pipes, currenPosition)
        }
    }
}

data class Position(
    val row: Int,
    val column: Int,
)

sealed interface Pipe {
    
    val visitedAt: Int
    val inLoop: Boolean
    
    fun changeVisitedAt(visitedAt: Int): Pipe
    
    fun setAsInLoop(): Pipe
    
    fun apply(position: Position): List<Position>
    
    data class Vertical(
        override val visitedAt: Int,
        override val inLoop: Boolean,
    ): Pipe {

        override fun apply(position: Position): List<Position> {
            return listOf(
                position.copy(row = position.row - 1),
                position.copy(row = position.row + 1),
            )
        }

        override fun changeVisitedAt(visitedAt: Int): Pipe {
            return copy(visitedAt = visitedAt)
        }

        override fun setAsInLoop(): Pipe {
            return copy(inLoop = true)
        }
    }
    
    data class Horizontal(
        override val visitedAt: Int,
        override val inLoop: Boolean,
    ): Pipe {
        
        override fun apply(position: Position): List<Position> {
            return listOf(
                position.copy(column = position.column - 1),
                position.copy(column = position.column + 1),
            )
        }
        
        override fun changeVisitedAt(visitedAt: Int): Pipe {
            return copy(visitedAt = visitedAt)
        }
        
        override fun setAsInLoop(): Pipe {
            return copy(inLoop = true)
        }
    }
    
    data class L(
        override val visitedAt: Int,
        override val inLoop: Boolean,
    ): Pipe {
        override fun apply(position: Position): List<Position> {
            return listOf(
                position.copy(row = position.row - 1),
                position.copy(column = position.column + 1),
            )
        }
        
        override fun changeVisitedAt(visitedAt: Int): Pipe {
            return copy(visitedAt = visitedAt)
        }

        override fun setAsInLoop(): Pipe {
            return copy(inLoop = true)
        }
    }
    
    data class J(
        override val visitedAt: Int,
        override val inLoop: Boolean,
    ): Pipe {
        override fun apply(position: Position): List<Position> {
            return listOf(
                position.copy(row = position.row - 1),
                position.copy(column = position.column - 1),
            )
        }
        
        override fun changeVisitedAt(visitedAt: Int): Pipe {
            return copy(visitedAt = visitedAt)
        }
        
        override fun setAsInLoop(): Pipe {
            return copy(inLoop = true)
        }
    }
    
    data class Seven(
        override val visitedAt: Int,
        override val inLoop: Boolean,
    ): Pipe {
        
        override fun apply(position: Position): List<Position> {
            return listOf(
                position.copy(row = position.row + 1),
                position.copy(column = position.column - 1),
            )
        }
        
        override fun changeVisitedAt(visitedAt: Int): Pipe {
            return copy(visitedAt = visitedAt)
        }

        override fun setAsInLoop(): Pipe {
            return copy(inLoop = true)
        }
    }
    
    data class F(
        override val visitedAt: Int,
        override val inLoop: Boolean,
    ): Pipe {
        
        override fun apply(position: Position): List<Position> {
            return listOf(
                position.copy(row = position.row + 1),
                position.copy(column = position.column + 1),
            )
        }
        
        override fun changeVisitedAt(visitedAt: Int): Pipe {
            return copy(visitedAt = visitedAt)
        }

        override fun setAsInLoop(): Pipe {
            return copy(inLoop = true)
        }
    }
    
    data class Ground(
        override val visitedAt: Int,
        override val inLoop: Boolean,
    ): Pipe {
        
        override fun apply(position: Position): List<Position> {
            return emptyList()
        }
        
        override fun changeVisitedAt(visitedAt: Int): Pipe {
            return copy(visitedAt = visitedAt)
        }

        override fun setAsInLoop(): Pipe {
            return copy(inLoop = true)
        }
    }
    
    data class Start(
        override val visitedAt: Int,
        override val inLoop: Boolean,
    ): Pipe {
        
        override fun apply(position: Position): List<Position> {
            return listOf(
                position.copy(row = position.row + 1),
                position.copy(row = position.row - 1),
                position.copy(column = position.column + 1),
                position.copy(column = position.column - 1),
            )
        }
        
        override fun changeVisitedAt(visitedAt: Int): Pipe {
            return copy(visitedAt = visitedAt)
        }

        override fun setAsInLoop(): Pipe {
            return copy(inLoop = true)
        }
    }
    
    companion object {
        
        fun of(char: Char): Pipe {
            return when(char) {
                '|' -> Vertical(-1, false)
                '-' -> Horizontal(-1, false)
                'L' -> L(-1, false)
                'J' -> J(-1, false)
                '7' -> Seven(-1, false)
                'F' -> F(-1, false)
                '.' -> Ground(-1, false)
                'S' -> Start(-1, false)
                else -> error("invalid map char $char")
            }
        }
    }
}