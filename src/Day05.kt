fun main() {
    fun parse(input: List<String>): Pair<Int, List<InclusiveRange<Long>>> {
        val splitIndex = input.indexOfFirst(String::isEmpty)
        val ranges = input.subList(0, splitIndex).map {
            val nums = it.split('-').map(String::toLong)
            checkEquals(2, nums.size)
            InclusiveRange(nums[0], nums[1])
        }
        return Pair(splitIndex, ranges)
    }

    fun part1(input: List<String>): Int {
        val (splitIndex, ranges) = parse(input)
        val ids = input.subList(splitIndex+1, input.size).map(String::toLong)
        return ids.count { id -> ranges.any{ range -> range.contains(id) } }
    }

    fun part2(input: List<String>): Long {
        val (_, parsedRanges) = parse(input)
        val ranges = parsedRanges.toMutableList()
        var hadMerge = true
        while (hadMerge) {
            hadMerge = false

            for(i in ranges.indices) {
                for(j in i+1..<ranges.size) {
                    if(ranges[i].overlaps(ranges[j])) {
                        ranges.add(ranges[i].merge(ranges[j]))
                        ranges.removeAt(j)
                        ranges.removeAt(i)
                        hadMerge = true
                        break
                    }
                }
                if(hadMerge) {
                    break
                }
            }
        }

        return ranges.sumOf { it.y - it.x + 1L }
    }

    val testInput = readInput("Day05_test")
    checkEquals(3, part1(testInput))
    checkEquals(14L, part2(testInput))

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
