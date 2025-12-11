data class DevicePath(
    val device: String,
    val hasVisitedDAC: Boolean,
    val hasVisitedFFT: Boolean
)

fun main() {
    fun getPathCountPart1(current: String, devices: Map<String, List<String>>, cache: MutableMap<String, Int>): Int {
        if (current == "out") {
            return 1
        }
        if (cache.contains(current)) {
            return cache[current]!!
        }

        return devices[current]!!
            .sumOf { next -> getPathCountPart1(next, devices, cache) }
            .also { pathCount -> cache[current] = pathCount }
    }

    fun getPathCountPart2(current: DevicePath, devices: Map<String, List<String>>, cache: MutableMap<DevicePath, Long>): Long {
        if (current.device == "out") {
            return if(current.hasVisitedDAC && current.hasVisitedFFT) 1L else 0L
        }
        if (cache.contains(current)) {
            return cache[current]!!
        }

        return devices[current.device]!!
            .sumOf { nextDevice ->
                val nextPath = DevicePath(
                    nextDevice,
                    current.hasVisitedDAC || nextDevice == "dac",
                    current.hasVisitedFFT || nextDevice == "fft",
                )
                getPathCountPart2(nextPath, devices, cache)
            }
            .also { pathCount -> cache[current] = pathCount }
    }

    fun parse(input: List<String>): Map<String, List<String>> {
        return input.associate { line ->
            val d = line.split(" ", ": ")
            val input = d.first()
            val outputs = d.drop(1)
            Pair(input, outputs)
        }
    }

    fun part1(input: List<String>): Int {
        val devices = parse(input)
        val cache = mutableMapOf<String, Int>()

        return getPathCountPart1("you", devices, cache)
    }

    fun part2(input: List<String>): Long {
        val devices = parse(input)
        val cache = mutableMapOf<DevicePath, Long>()

        val start = DevicePath("svr", false, false)
        return getPathCountPart2(start, devices, cache)
    }

    val testInput = readInput("Day11_test")
    checkEquals(5, part1(testInput))
    val testInput2 = readInput("Day11_test2")
    checkEquals(2, part2(testInput2))

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
