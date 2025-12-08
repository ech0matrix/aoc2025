import kotlin.math.sqrt
import kotlin.math.pow

data class Coordinates3D(
    val x: Long,
    val y: Long,
    val z: Long
) {
    companion object {
        fun fromString(input: String): Coordinates3D {
            val nums = input.split(',').map(String::toLong)
            checkEquals(3, nums.size)
            return Coordinates3D(nums[0], nums[1], nums[2])
        }
    }

    fun distance(other: Coordinates3D): Double {
        return sqrt(
            (x-other.x).toDouble().pow(2)
            + (y-other.y).toDouble().pow(2)
            + (z-other.z).toDouble().pow(2)
        )
    }
}

data class Circuit(
    val coordinates: Set<Coordinates3D>
) {
    fun merge(other: Circuit): Circuit {
        return Circuit(this.coordinates.union(other.coordinates))
    }
}

fun main() {
    // Returns coordinates, distances map, and circuits
    fun parse(input: List<String>): Triple<List<Coordinates3D>, MutableMap<Set<Coordinates3D>, Double>, MutableMap<Coordinates3D, Circuit>> {
        val coordinates = input.map(Coordinates3D::fromString) // List of all the coordinates from the input

        // Build a map of coordinate-pairs to straight line distances
        val distances = mutableMapOf<Set<Coordinates3D>, Double>()
        for(i in 0..<coordinates.size) {
            for(j in (i+1)..<coordinates.size) {
                distances[setOf(coordinates[i], coordinates[j])] = coordinates[i].distance(coordinates[j])
            }
        }
        // Start with a circuit for every coordinate
        val circuits = coordinates.associateWith { Circuit(setOf(it)) }.toMutableMap()

        return Triple(coordinates, distances, circuits)
    }

    fun part1(input: List<String>, numConnections: Int): Long {
        val (_, distances, circuits) = parse(input)

        val links = distances.keys.sortedBy { distances[it] }.take(numConnections)
        for(link in links) {
            val coord1 = link.first()
            val coord2 = link.last()

            val circuit1 = circuits[coord1]!!
            val circuit2 = circuits[coord2]!!

            if (circuit1 != circuit2) {
                val newCircuit = circuit1.merge(circuit2)
                for (coord in newCircuit.coordinates) {
                    circuits[coord] = newCircuit
                }
            }
        }

        val distinctCircuits = circuits.values.toSet().sortedBy { it.coordinates.size }.reversed()

        return distinctCircuits[0].coordinates.size.toLong() *
                distinctCircuits[1].coordinates.size.toLong() *
                distinctCircuits[2].coordinates.size.toLong()
    }

    fun part2(input: List<String>): Long {
        val (coordinates, distances, circuits) = parse(input)

        val links = distances.keys.sortedBy { distances[it] }
        for(link in links) {
            val coord1 = link.first()
            val coord2 = link.last()

            val circuit1 = circuits[coord1]!!
            val circuit2 = circuits[coord2]!!

            if (circuit1 != circuit2) {
                val newCircuit = circuit1.merge(circuit2)
                for (coord in newCircuit.coordinates) {
                    circuits[coord] = newCircuit
                }

                if (newCircuit.coordinates.size == coordinates.size) {
                    // New circuit links all the coordinates!
                    return coord1.x * coord2.x
                }
            }
        }

        throw IllegalStateException("Expected to have linked all circuits")
    }

    val testInput = readInput("Day08_test")
    checkEquals(40L, part1(testInput, 10))
    checkEquals(25272L, part2(testInput))

    val input = readInput("Day08")
    println(timeIt{part1(input, 1000)})
    println(timeIt{part2(input)})
}
