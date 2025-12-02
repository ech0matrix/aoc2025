fun main() {
    fun parse(input: List<String>): List<InclusiveRange<Long>> {
        checkEquals(1, input.size)
        return input[0].split(',')
            .map {
                val boundaries = it.split('-')
                checkEquals(2, boundaries.size)
                Pair(boundaries[0], boundaries[1])
            }.map { (start, end) ->
                InclusiveRange(start.toLong(), end.toLong())
            }
    }
    fun part1(input: List<String>): Long {
        val ranges = parse(input)
        var total = 0L

        for(range in ranges) {
            var i = range.x
            while (range.contains(i)) {
                val str = i.toString()
                if (str.length % 2 != 0) {
                    var nextStr = "1"
                    repeat(str.length) {
                        nextStr += "0"
                    }
                    i = nextStr.toLong()
                } else {
                    val subStr = str.take(str.length/2)
                    val halfNum = subStr.toLong()
                    val fullNum = (subStr + subStr).toLong()
                    if (range.contains(fullNum)) {
                        total += fullNum
                    }
                    val nextHalf = (halfNum+1).toString()
                    i = (nextHalf + nextHalf).toLong()
                }
            }
        }

        return total
    }

    fun hasRepeats(num: Long): Boolean {
        val numStr = num.toString()
        for(d in 1..numStr.length/2) {
            //println("Testing $d digit(s)")
            if (numStr.length % d == 0) {
                val subStr = numStr.take(d)
                //println("  with pattern $subStr")
                var repeatedStr = ""
                repeat(numStr.length/d) {
                    repeatedStr += subStr
                }
                //println("  Constructed $repeatedStr")
                if (repeatedStr == numStr) {
                    //println("  Matches! ($num)")
                    return true
                } else {
                    //println("  No match.")
                }
            } else {
                //println("  Not divisible. Skipping.")
            }
        }

        return false
    }

    fun part2(input: List<String>): Long {
        val ranges = parse(input)
        var total = 0L

        for(range in ranges) {
            //print("$range has invalids: ")
            for (i in range.x..range.y) {
                if (hasRepeats(i)) {
                    //print("$i ")
                    total += i
                }
            }
            //println()
        }

        return total
    }

    //println(hasRepeats(2121212121L))

    val testInput = readInput("Day02_test")
    checkEquals(1227775554L, part1(testInput))
    checkEquals(4174379265L, part2(testInput))

    val input = readInput("Day02")
    println(part1(input))
    println(timeIt { part2(input) })
}
