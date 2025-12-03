fun main() {
    fun parse(input: List<String>): List<List<Int>> {
        return input.map { it.toList().map{ c -> c.code - '0'.code } }
    }

    fun part1(input: List<String>): Int {
        val banks = parse(input)
        var joltage = 0
        for (bank in banks) {
            val maxValue = bank.dropLast(1).max()
            val maxIndex = bank.indexOf(maxValue)
            val secondValue = bank.drop(maxIndex + 1).max()
            joltage += maxValue*10 + secondValue
        }

        return joltage
    }

    // Returns Pair of max and the remainder of the bank after the max with at least 'remaining'
    fun findMax(bank: List<Int>, remaining: Int): Pair<Int, List<Int>> {
        val maxValue = bank.dropLast(remaining).max()
        val maxIndex = bank.indexOf(maxValue)
        return Pair(maxValue, bank.drop(maxIndex + 1))
    }

    fun part2(input: List<String>): Long {
        val banks = parse(input)
        var joltage = 0L
        for (bank in banks) {
            var computedJoltage = 0L
            var remainingBank = bank
            for(i in 1..12) {
                val result = findMax(remainingBank, 12-i)
                computedJoltage += result.first
                remainingBank = result.second
                if (i < 12) {
                    computedJoltage *= 10L
                }
            }

            joltage += computedJoltage
        }

        return joltage
    }

    val testInput = readInput("Day03_test")
    checkEquals(357, part1(testInput))
    checkEquals(3121910778619L, part2(testInput))

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
