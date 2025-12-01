fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readInput("Day00_test")
    checkEquals(1, part1(testInput))

    val input = readInput("Day00")
    println(part1(input))
    println(part2(input))
}
