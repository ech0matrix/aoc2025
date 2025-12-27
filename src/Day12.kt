
data class Shape(val index: Int, val coordinates: Set<Coordinates>) {
    val minBounds = Coordinates(
        coordinates.minOf { it.row },
        coordinates.minOf { it.col },
    )
    val maxBounds = Coordinates(
        coordinates.maxOf { it.row },
        coordinates.maxOf { it.col },
    )

    companion object {
        fun buildShape(lines: List<String>): Shape {
            val coordinatesBuilder = mutableSetOf<Coordinates>()
            val shapeLines = lines.drop(1)
            for(row in 0..<shapeLines.size) {
                for(col in 0..<shapeLines[row].length) {
                    if (shapeLines[row][col] == '#') {
                        coordinatesBuilder.add(Coordinates(row, col))
                    }
                }
            }

            return Shape(
                lines[0].dropLast(1).toInt(),
                coordinatesBuilder.toSet()
            )
        }
    }

    fun print() {
        println("$index:")
        println("  Rows: ${minBounds.row} to ${maxBounds.row}")
        println("  Cols: ${minBounds.col} to ${maxBounds.col}")
        for(row in minBounds.row..maxBounds.row) {
            for(col in minBounds.col..maxBounds.col) {
                val c = Coordinates(row, col)
                if (coordinates.contains(c)) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println("")
        }
    }

    fun moveTo(moveBy: Coordinates): Shape {
        val translatedCoordinates = coordinates.map {
            it.add(moveBy)
        }.toSet()
        return Shape(index, translatedCoordinates)
    }

    // Move to 0,0
    fun moveToOrigin(): Shape {
        val originTranslation = Coordinates(-minBounds.row, -minBounds.col)
        return moveTo(originTranslation)
    }

    // Rotates 90-degrees clockwise
    fun rotate(): Shape {
        val rotatedCoordinates = coordinates.map {
            Coordinates(it.col, -it.row)
        }.toSet()
        return Shape(index, rotatedCoordinates).moveToOrigin()
    }

    fun reflectHorizontal(): Shape {
        val flippedCoordinates = coordinates.map {
            Coordinates(it.row, -it.col)
        }.toSet()
        return Shape(index, flippedCoordinates).moveToOrigin()
    }

    fun reflectVertical(): Shape {
        val flippedCoordinates = coordinates.map {
            Coordinates(-it.row, it.col)
        }.toSet()
        return Shape(index, flippedCoordinates).moveToOrigin()
    }

    fun getAllVariants(): List<Shape> {
        val variants = mutableSetOf<Shape>()
        var current = this
        repeat(4) {
            variants.add(current)
            variants.add(current.reflectVertical())
            variants.add(current.reflectHorizontal())
            current = current.rotate()
        }
        return variants.toList()
    }
}

data class TreeRegion(val size: Coordinates, val requirements: List<Int>) {
    companion object {
        fun buildRegion(line: String): TreeRegion {
            val parts = line.split(": ")
            checkEquals(2, parts.size)

            val dimensions = parts[0].split("x").map { it.toInt() }
            checkEquals(2, dimensions.size)
            val size = Coordinates(
                dimensions[1],
                dimensions[0]
            )

            return TreeRegion(
                size,
                parts[1].split(" ").map { it.toInt() }
            )
        }
    }
}

fun main() {
    val FITS = "Fits"
    val DOESNT_FIT = "Doesn't fit"
    val MAYBE_FITS = "Maybe"

    fun part1(input: List<String>): Int {
        val chunks = mutableListOf<List<String>>()
        var chunk = mutableListOf<String>()
        for (line in input) {
            if (line.isEmpty()) {
                chunks.add(chunk.toList())
                chunk = mutableListOf()
            } else {
                chunk.add(line)
            }
        }
        chunks.add(chunk.toList())

        val shapes = chunks.dropLast(1).map { Shape.buildShape(it) }.associateBy { it.index }
        val regions = chunks.last().map { TreeRegion.buildRegion(it) }

        val results = regions.map { region ->
            val area = region.size.row * region.size.col
            val minArea = region.requirements.mapIndexed { index, num ->
                num * shapes[index]!!.coordinates.size
            }.sum()

            val lazyShapeFitCount = region.size.row/3 * region.size.col/3
            val numShapes = region.requirements.sum()

            if (minArea > area) {
                DOESNT_FIT
            } else if (lazyShapeFitCount >= numShapes) {
                FITS
            } else {
                MAYBE_FITS
            }
        }

        val fitCount = results.count { it == FITS }
        val doesntFitCount = results.count { it == DOESNT_FIT }
        val maybeCount = results.count { it == MAYBE_FITS }

        println("Fit: $fitCount")
        println("Doesn't Fit: $doesntFitCount")
        println("Maybe: $maybeCount")

        return fitCount
    }

    val testInput = readInput("Day12_test")
    println(part1(testInput))
    //checkEquals(2, part1(testInput))

    val input = readInput("Day12")
    println(part1(input))
}
