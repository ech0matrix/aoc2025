import kotlin.math.abs

fun main() {
    fun parse(input: List<String>): List<LongCoordinates> {
        return input.map {
            val position = it.split(',').map(String::toLong)
            checkEquals(2, position.size)
            LongCoordinates(position[1],position[0])
        }
    }
    fun part1(input: List<String>): Long {
        val tiles = parse(input)

        var maxArea = 0L
        for(i in tiles.indices) {
            for(j in (i+1)..<tiles.size) {
                val height = abs(tiles[i].row - tiles[j].row) + 1L
                val width = abs(tiles[i].col - tiles[j].col) + 1L
                val area = height * width
                if (area > maxArea) {
                    maxArea = area
                }
            }
        }

        return maxArea
    }

    fun connectTiles(t1: LongCoordinates, t2: LongCoordinates, allTiles: MutableSet<LongCoordinates>) {
        if (t1.col == t2.col) {
            val min = minOf(t1.row, t2.row)
            val max = maxOf(t1.row, t2.row)
            for(r in min..max) {
                allTiles.add(LongCoordinates(r, t1.col))
            }
        } else if (t1.row == t2.row) {
            val min = minOf(t1.col, t2.col)
            val max = maxOf(t1.col, t2.col)
            for(c in min..max) {
                allTiles.add(LongCoordinates(t1.row, c))
            }
        } else {
            throw IllegalStateException("Expected straight line between tiles")
        }
    }

    fun insideShape(
        pos: LongCoordinates,
        minCorner: LongCoordinates,
        maxCorner: LongCoordinates,
        allTiles: MutableSet<LongCoordinates>,
        minBounds: LongCoordinates,
        maxBounds: LongCoordinates
    ): Boolean {
        var position = pos
        while(true) {
            if (position.row <= minBounds.row) {
                return false
            }
            if(allTiles.contains(position)) {
                if (position.row > minCorner.row) {
                    return false
                } else {
                    break
                }
            }
            position = position.add(Direction.NORTH)
        }
        position = pos
        while(true) {
            if (position.row >= maxBounds.row) {
                return false
            }
            if(allTiles.contains(position)) {
                if (position.row < maxCorner.row) {
                    return false
                } else {
                    break
                }
            }
            position = position.add(Direction.SOUTH)
        }
        position = pos
        while(true) {
            if (position.col <= minBounds.col) {
                return false
            }
            if(allTiles.contains(position)) {
                if (position.col > minCorner.col) {
                    return false
                } else {
                    break
                }
            }
            position = position.add(Direction.WEST)
        }
        position = pos
        while(true) {
            if (position.col >= maxBounds.col) {
                return false
            }
            if(allTiles.contains(position)) {
                if (position.col < maxCorner.col) {
                    return false
                } else {
                    break
                }
            }
            position = position.add(Direction.EAST)
        }

        return true
    }

    fun part2(input: List<String>): Long {
        val redTiles = parse(input)
        val allTiles = mutableSetOf<LongCoordinates>()
        for(i in 0..<redTiles.size-1) {
            connectTiles(redTiles[i], redTiles[i+1], allTiles)
        }
        connectTiles(redTiles.last(), redTiles.first(), allTiles) // Wrap around

        // Compute boundaries
        val minBounds = LongCoordinates(
            redTiles.minOf { it.row } - 1,
            redTiles.minOf { it.col } - 1
        )
        val maxBounds = LongCoordinates(
            redTiles.maxOf { it.row } + 1,
            redTiles.maxOf { it.col } + 1
        )

        var maxArea = 0L
        for(i in redTiles.indices) {
            // i+2, since we know i+1 is in the same row or col
            for(j in (i+2)..<redTiles.size) {
                val height = abs(redTiles[i].row - redTiles[j].row) + 1L
                val width = abs(redTiles[i].col - redTiles[j].col) + 1L
                val area = height * width

                // Disqualify checking unless it at least has some height and width
                // so that we can pick a centroid inside the rectangle and not on an edge
                if (area > maxArea && height>=3 && width>=3) {
                    val minCorner = LongCoordinates(
                        minOf(redTiles[i].row, redTiles[j].row),
                        minOf(redTiles[i].col, redTiles[j].col)
                    )
                    val maxCorner = LongCoordinates(
                        maxOf(redTiles[i].row, redTiles[j].row),
                        maxOf(redTiles[i].col, redTiles[j].col)
                    )

                    // Draw straight lines out from centroid to check that it is inside the shape
                    val centroid = LongCoordinates(
                        (redTiles[i].row + redTiles[j].row) / 2L,
                        (redTiles[i].col + redTiles[j].col) / 2L
                    )

                    // Check spots along edges too (check inside corners)
                    val checkPositions = listOf(
                        centroid,
                        LongCoordinates(minCorner.row+1, minCorner.col+1),
                        LongCoordinates(maxCorner.row-1, minCorner.col+1),
                        LongCoordinates(minCorner.row+1, maxCorner.col-1),
                        LongCoordinates(maxCorner.row-1, maxCorner.col-1),
                    )

                    if (checkPositions.all{ insideShape(it, minCorner, maxCorner, allTiles, minBounds, maxBounds) }) {
                        maxArea = area
                    }
                }
            }
            if (i % 10 == 0) {
                println("$i of ${redTiles.size}")
            }
        }

//        for(r in 0L..8L) {
//            for(c in 0L..13L) {
//                val position = LongCoordinates(r, c)
//                if (redTiles.contains(position)) {
//                    print("#")
//                } else if (allTiles.contains(position)) {
//                    print("X")
//                } else {
//                    print(".")
//                }
//            }
//            println()
//        }

        return maxArea
    }

    val testInput = readInput("Day09_test")
    checkEquals(50L, part1(testInput))
    checkEquals(24L, part2(testInput))

    val input = readInput("Day09")
    println(part1(input))
    println(timeIt{part2(input)})
}
