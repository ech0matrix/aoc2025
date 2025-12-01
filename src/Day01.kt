fun main() {
    fun part1(input: List<String>): Int {
        return input.map {
            it.replace("R", "")
                .replace("L", "-")
                .toInt()
        }.fold(Pair(50, 0)) { (dial, zeroCount), n ->
            val dialNext = (dial + n + 100) % 100
            val zeroCountNext = zeroCount + if(dialNext == 0) 1 else 0
            Pair(dialNext, zeroCountNext)
        }.second
    }

    fun part2(input: List<String>): Int {
        val nums = input.map {
            it.replace("R", "")
                .replace("L", "-")
                .toInt()
        }

        var dial = 50
        var zeroCount = 0

        for(num in nums) {
            if(num > 0) {
                repeat(num) {
                    dial++
                    if(dial == 100) {
                        dial = 0
                        zeroCount++
                    }
                }
            } else {
                repeat(num*-1) {
                    dial--
                    if(dial == 0) {
                        zeroCount++
                    }
                    if(dial == -1) {
                        dial = 99
                    }
                }
            }
        }

        return zeroCount
    }

    val testInput = readInput("Day01_test")
    checkEquals(3, part1(testInput))
    checkEquals(6, part2(testInput))

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
