fun main() {
    fun part1(input: List<String>): Int {
        val lasers = mutableSetOf<Int>()
        lasers.add(input[0].indexOf('S'))
        checkEquals(1, lasers.size)

        var splits = 0

        for(row in input) {
            val splitters = row.withIndex().filter { it.value == '^' }.map { it.index }
            for(splitter in splitters) {
                if (lasers.contains(splitter)) {
                    lasers.remove(splitter)
                    lasers.add(splitter+1)
                    lasers.add(splitter-1)
                    splits++
                }
            }
        }

        return splits
    }

    fun part2(input: List<String>): Long {
        val lasers = mutableMapOf<Int, Long>()
        lasers[input[0].indexOf('S')] = 1L

        for(row in input) {
            val splitters = row.withIndex().filter { it.value == '^' }.map { it.index }
            for(splitter in splitters) {
                if (lasers.contains(splitter)) {
                    val count = lasers.remove(splitter)!!

                    if (lasers.contains(splitter+1)) {
                        lasers[splitter + 1] = lasers[splitter + 1]!! + count
                    } else {
                        lasers[splitter + 1] = count
                    }

                    if (lasers.contains(splitter-1)) {
                        lasers[splitter - 1] = lasers[splitter - 1]!! + count
                    } else {
                        lasers[splitter - 1] = count
                    }
                }
            }
        }

        return lasers.values.sum()
    }

    val testInput = readInput("Day07_test")
    checkEquals(21, part1(testInput))
    checkEquals(40, part2(testInput))

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
