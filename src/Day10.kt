import java.util.PriorityQueue
import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver

data class LightsButtonPressState(
    val currentLights: List<Boolean>,
    val buttonsPressed: Set<Set<Int>>
)

data class JoltageButtonPressState(
    val currentJoltage: List<Int>,
    val buttonsPressed: Map<Set<Int>, Int>,
    val totalPresses: Int
)

fun main() {
    Loader.loadNativeLibraries()


    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val tokens = line.split(' ')

            val goalLights = tokens.first().drop(1).dropLast(1).map { it == '#' }

            val buttons = tokens.drop(1).dropLast(1).map { buttonStr ->
                buttonStr.drop(1).dropLast(1).split(',').map(String::toInt).toSet()
            }.toSet()

            val presses = PriorityQueue<LightsButtonPressState>{ s1, s2 -> s1.buttonsPressed.size.compareTo(s2.buttonsPressed.size) }
            presses.add(LightsButtonPressState(
                List(goalLights.size) { false },
                emptySet()
            ))
            while(presses.isNotEmpty()) {
                val state = presses.poll()

                if (state.currentLights == goalLights) {
                    return@sumOf state.buttonsPressed.size
                }

                val options = buttons.minus(state.buttonsPressed)
                for(option in options) {
                    val nextLights = state.currentLights.toMutableList()
                    for(light in option) {
                        nextLights[light] = !nextLights[light]
                    }

                    val nextButtonsPressed = state.buttonsPressed.plus(setOf(option))

                    presses.add(LightsButtonPressState(
                        nextLights.toList(),
                        nextButtonsPressed
                    ))
                }
            }

            throw IllegalStateException("Expected solution by now")
        }
    }

    fun part2BFS(input: List<String>): Int {
        return input.sumOf { line ->
            val tokens = line.split(' ')

            val goalJoltage = tokens.last().drop(1).dropLast(1).split(',').map(String::toInt)

            val buttons = tokens.drop(1).dropLast(1).map { buttonStr ->
                buttonStr.drop(1).dropLast(1).split(',').map(String::toInt).toSet()
            }.toSet()

            val presses = PriorityQueue<JoltageButtonPressState>{ s1, s2 ->
                s1.totalPresses.compareTo(s2.totalPresses)
            }
            presses.add(JoltageButtonPressState(
                List(goalJoltage.size) { 0 },
                buttons.associateWith { 0 },
                0
            ))

            val visited = mutableSetOf<Map<Set<Int>, Int>>()

            while(presses.isNotEmpty()) {
                val state = presses.poll()

                if (state.currentJoltage == goalJoltage) {
                    return@sumOf state.totalPresses
                }

                for(option in buttons) {
                    val workingNextButtonsPressed = state.buttonsPressed.toMutableMap()
                    workingNextButtonsPressed[option] = workingNextButtonsPressed[option]!! + 1
                    val nextButtonsPressed = workingNextButtonsPressed.toMap()

                    if (visited.contains(nextButtonsPressed)) {
                        continue
                    }
                    visited.add(nextButtonsPressed)

                    val nextCounters = state.currentJoltage.toMutableList()

                    var isTooManyPresses = false
                    for(counter in option) {
                        if (nextCounters[counter] == goalJoltage[counter]) {
                            // Don't over count
                            isTooManyPresses = true
                            break
                        }
                        nextCounters[counter]++
                    }
                    if (isTooManyPresses) {
                        continue
                    }

                    presses.add(JoltageButtonPressState(
                        nextCounters.toList(),
                        nextButtonsPressed,
                        state.totalPresses + 1
                    ))
                }
            }

            throw IllegalStateException("Expected solution by now")
        }
    }

    fun part2solver(input: List<String>): Int {
        return input.sumOf { line ->

            val tokens = line.split(' ')

            val goalJoltage = tokens.last().drop(1).dropLast(1).split(',').map(String::toInt)

            val buttons = tokens.drop(1).dropLast(1).map { buttonStr ->
                buttonStr.drop(1).dropLast(1).split(',').map(String::toInt).toSet()
            }

            val equations = mutableListOf<Pair<Set<String>, Int>>()
            goalJoltage.forEachIndexed { i, joltage ->
                val variables = mutableSetOf<String>()
                buttons.forEachIndexed { j, buttonSet ->
                    if (buttonSet.contains(i)) {
                        variables.add(('a' + j).toString())
                    }
                }

                equations.add(Pair(variables.toSet(), joltage))
            }

            val solver = MPSolver.createSolver("CBC")

            // Define variables (one for each button)
            val variables = buttons.indices.map { ('a' + it).toString() }.associateWith { varName ->
                val maxBound = equations.maxOf { (button, sum) ->
                    if (button.contains(varName)) sum else 0
                }
                solver.makeIntVar(0.0, maxBound.toDouble(), varName)
            }

            // Add equations
            equations.forEachIndexed { i, (variableNames, sum) ->
                val constraint = solver.makeConstraint(sum.toDouble(), sum.toDouble(), "eq$i")
                for (variableName in variableNames) {
                    constraint.setCoefficient(variables[variableName], 1.0)
                }
            }

            // Set objective: minimize total button presses
            val objective = solver.objective()
            for (variable in variables.values) {
                objective.setCoefficient(variable, 1.0)
            }
            objective.setMinimization()

            val resultStatus = solver.solve()

            if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
                throw IllegalStateException("Expected solution to be found")
            }

            return@sumOf objective.value().toInt()
        }
    }

    val testInput = readInput("Day10_test")
    checkEquals(7, part1(testInput))
    checkEquals(33, part2BFS(testInput))
    checkEquals(33, part2solver(testInput))

    val input = readInput("Day10")
    println(timeIt{part1(input)})
    println(timeIt{part2solver(input)})
}
