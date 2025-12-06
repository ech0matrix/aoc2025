fun main() {
    fun part1(input: List<String>): Long {
        val operators = input.last().split(" ").filterNot(String::isEmpty)
        val numbers = input.dropLast(1).map { row ->
            row.split(" ")
                .filterNot(String::isEmpty)
                .map(String::toLong)
        }
        check(numbers.all { it.size == operators.size })

        var total = 0L
        for(i in operators.indices) {
            val operator: (Long, Long) -> Long = if (operators[i] == "*") {
                { a, b -> a * b }
            } else {
                { a, b -> a + b }
            }
            val operands = numbers.map { it[i] }
            total += operands.reduce(operator)
        }

        return total
    }

    fun part2(input: List<String>): Long {
        var total = 0L
        var i = 0
        while (i < input.last().length) {
            var nextOperator = input.last().indexOfAny(listOf("*", "+"), i+1)
            if (nextOperator == -1) {
                nextOperator = input.last().length
            }

            val operands = mutableListOf<Long>()
            for (j in (nextOperator-1) downTo i) {
                val numString = input.dropLast(1)
                    .map { it[j] }
                    .joinToString("")
                    .trim()
                if (numString.isNotEmpty()) {
                    operands.add(numString.toLong())
                }
            }

            val operator: (Long, Long) -> Long = if (input.last()[i] == '*') {
                { a, b -> a * b }
            } else if (input.last()[i] == '+') {
                { a, b -> a + b }
            } else {
                throw IllegalStateException("Expected operator at index $i")
            }
            total += operands.reduce(operator)

            i = nextOperator
        }

        return total
    }

    val testInput = readInputNoTrim("Day06_test")
    checkEquals(4277556L, part1(testInput))
    checkEquals(3263827L, part2(testInput))

    val input = readInputNoTrim("Day06")
    println(part1(input))
    println(part2(input))
}
