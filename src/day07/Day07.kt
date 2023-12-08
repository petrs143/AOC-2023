package day07

import java.io.File

fun main() {
    val input = File("src/day07/input.txt").readLines()
    println(part1(input))
    println(part2(input))
}

private fun part1(lines: List<String>): Long {
    val players = lines.map { line ->
        val (hand, bet) = line.split(" ")
        Player.of(hand, bet.toInt())
    }
    
    return players.sortedDescending().mapIndexed { index, player ->
        (index + 1) * player.bet.toLong()
    }.sum()
}

private fun part2(lines: List<String>): Long {
    val players = lines.map { line ->
        val (hand, bet) = line.split(" ")
        Player.ofPart2(hand, bet.toInt())
    }

    return players.sortedDescending().mapIndexed { index, player ->
        (index + 1) * player.bet.toLong()
    }.sum()
}

enum class Combination(val rating: Int) {
    FIVE_KIND(1),
    FOUR_KIND(2),
    FULL_HOUSE(3),
    THREE_KIND(4),
    TWO_PAIR(5),
    PAIR(6),
    HIGH_CARD(7);
}

data class Player(
    val hand: List<Card>,
    val bet: Int,
    val combination: Combination,
): Comparable<Player> {

    override fun compareTo(other: Player): Int {
        val combinationComparasion =  combination.rating.compareTo(other.combination.rating)
        return if (combinationComparasion == 0) {
            hand.forEachIndexed { index, card ->
               val cardComparasion = card.rankPart2.compareTo(other.hand[index].rankPart2)
                
                if (cardComparasion != 0) {
                    return cardComparasion
                }
            }
            
            0
        } else {
            combinationComparasion
        }
    }
    
    companion object {
        
        fun of(hand: String, bet: Int): Player {
            val groupedCards = hand.groupBy { it }.values
            val combination = when {
                groupedCards.any { it.size == 5 } -> Combination.FIVE_KIND
                groupedCards.any { it.size == 4 } -> Combination.FOUR_KIND
                groupedCards.any { it.size == 3 } && groupedCards.any { it.size == 2 } -> Combination.FULL_HOUSE
                groupedCards.any { it.size == 3 } -> Combination.THREE_KIND
                groupedCards.count { it.size == 2 } >= 2 -> Combination.TWO_PAIR
                groupedCards.any { it.size == 2 } -> Combination.PAIR
                else -> Combination.HIGH_CARD
            }
            
            return Player(
                hand.map { Card.of(it) }, bet, combination
            )
        }
        
        fun ofPart2(hand: String, bet: Int): Player {
            val cards = hand.map { Card.of(it) }
            val combination = Card.entries.map { card ->
                val replacedHand = cards.map { if (it == Card.J) card else it }
                val groupedCards = replacedHand.groupBy { it }.values
                when {
                    groupedCards.any { it.size == 5 } -> Combination.FIVE_KIND
                    groupedCards.any { it.size == 4 } -> Combination.FOUR_KIND
                    groupedCards.any { it.size == 3 } && groupedCards.any { it.size == 2 } -> Combination.FULL_HOUSE
                    groupedCards.any { it.size == 3 } -> Combination.THREE_KIND
                    groupedCards.count { it.size == 2 } >= 2 -> Combination.TWO_PAIR
                    groupedCards.any { it.size == 2 } -> Combination.PAIR
                    else -> Combination.HIGH_CARD
                }
            }.minBy { it.rating }

            return Player(
                cards, bet, combination
            )
        }
    }
}


enum class Card(val rank: Int, val rankPart2: Int) {
    A(1, 1),
    K(2, 2),
    Q(3, 3),
    J(4, 13),
    TEN(5, 4),
    NINE(6, 5),
    EIGHT(7, 6),
    SEVEN(8, 7),
    SIX(9, 8),
    FIVE(10, 9),
    FOUR(11, 10),
    THREE(12, 11),
    TWO(13, 12);
    
    companion object {
     
        fun of(value: Char): Card {
            return when(value) {
                'A' -> A
                'K' -> K
                'Q' -> Q
                'J' -> J
                'T' -> TEN
                '9' -> NINE
                '8' -> EIGHT
                '7' -> SEVEN
                '6' -> SIX
                '5' -> FIVE
                '4' -> FOUR
                '3' -> THREE
                '2' -> TWO
                else -> A
            }
        }
    }
}
