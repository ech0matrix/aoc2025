import Coordinates.Companion.NORTH
import Coordinates.Companion.SOUTH
import Coordinates.Companion.EAST
import Coordinates.Companion.WEST

fun main() {
    fun parse(input: List<String>): MutableSet<Coordinates> {
        val rolls = mutableSetOf<Coordinates>()
        for(row in input.indices) {
            for(col in input[row].indices) {
                if(input[row][col] == '@') {
                    rolls.add(Coordinates(row, col))
                }
            }
        }
        return rolls
    }

    fun isRemovable(roll: Coordinates, rolls: Set<Coordinates>): Boolean {
        val adjacentPositions = listOf(
            roll.add(NORTH),
            roll.add(SOUTH),
            roll.add(EAST),
            roll.add(WEST),
            roll.add(NORTH).add(WEST),
            roll.add(NORTH).add(EAST),
            roll.add(SOUTH).add(WEST),
            roll.add(SOUTH).add(EAST)
        )
        val adjacentCount = adjacentPositions.count { rolls.contains(it) }
        return adjacentCount < 4
    }

    fun part1(input: List<String>): Int {
        val rolls = parse(input)

        var totalCount = 0
        for(roll in rolls) {
            if (isRemovable(roll, rolls)) {
                totalCount++
            }
        }

        return totalCount
    }

    fun part2(input: List<String>): Int {
        val rolls = parse(input)
        val originalCount = rolls.size

        var removed = true
        while(removed) {
            removed = false

            val removals = mutableSetOf<Coordinates>()
            for(roll in rolls) {
                if (isRemovable(roll, rolls)) {
                    removals.add(roll)
                }
            }

            if (removals.isNotEmpty()) {
                removed = true
                rolls.removeAll(removals)
            }
        }

        return originalCount - rolls.size
    }

    val testInput = readInput("Day04_test")
    checkEquals(13, part1(testInput))
    checkEquals(43, part2(testInput))

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
