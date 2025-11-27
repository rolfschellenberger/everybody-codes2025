package com.rolf.quest18

import com.rolf.Day
import com.rolf.util.groupLines
import com.rolf.util.isNumeric
import com.rolf.util.splitLine
import java.util.regex.Pattern

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val plants = parsePlants(lines)
        println(plants.values.last().getEnergy(plants))
    }

    private fun parsePlants(lines: List<String>): Map<Int, Plant> {
        val groups = groupLines(lines, "")
        return groups.associate {
            val plant = parsePlant(it)
            plant.id to plant
        }
    }

    private fun parsePlant(lines: List<String>): Plant {
        val plantInfo = lines.first()
        val (id, thickness) = parseIdAndThickness(plantInfo)
        val plant = Plant(id, thickness)

        // Plant with branches connected to other plants
        for (line in lines.subList(1, lines.size)) {
            if (line.contains("free")) continue
            val (id, thickness) = parseIdAndThickness(line)
            plant.branches.add(
                Branch(
                    id, thickness
                )
            )
        }
        return plant
    }

    private fun parseIdAndThickness(line: String): Pair<Int, Int> {
        val parts = splitLine(line, pattern = Pattern.compile("[^\\d-]"))
            .filter { it.isNumeric() }
            .map { it.toInt() }
        return parts[0] to parts[1]
    }

    override fun solve2(lines: List<String>) {
        val (plantConfig, testCases) = ungroup(lines)
        val plants = parsePlants(plantConfig)
        println(
            testCases.sumOf { testCase ->
                val testValues = testCase.split(" ").map { it.toInt() }
                plants.values.last().getEnergy(plants, testValues)
            }
        )
    }

    private fun ungroup(lines: List<String>): List<List<String>> {
        val separator = " ### "
        val separator3 = separator + separator + separator
        val all = lines.joinToString(separator)
        val plantConfig = all.take(all.lastIndexOf(separator3))
        val testCases = all.substring(all.lastIndexOf(separator3) + separator3.length)
        return listOf(
            plantConfig.split(separator),
            testCases.split(separator),
        )
    }

    override fun solve3(lines: List<String>) {
        val (plantConfig, testCases) = ungroup(lines)
        val plants = parsePlants(plantConfig)

        // Now we are looking for each starting plant that has a positive connection to its connecting plants
        val startingPlants = plants.filter { it.value.branches.isEmpty() }
        val otherPlants = plants.filterNot { it.value.branches.isEmpty() }

        val positivePlants = startingPlants.map { it.key }.toMutableSet()
        for (otherPlant in otherPlants) {
            for (branch in otherPlant.value.branches) {
                if (branch.thickness < 0) positivePlants.remove(branch.to)
            }
        }

        // Now we know what are positive (and negative), we can set up the test case for the maximum score
        val testCaseBestScore = List(startingPlants.size) { 0 }.toMutableList()
        positivePlants.forEach { id -> testCaseBestScore[id - 1] = 1 }

        val maxValue = plants.values.last().getEnergy(plants, testCaseBestScore)
        var sum = 0L
        for (testCase in testCases) {
            val testValues = testCase.split(" ").map { it.toInt() }
            val value = plants.values.last().getEnergy(plants, testValues)
            if (value > 0) {
                sum += (maxValue - value)
            }
        }
        println(sum)
    }
}

data class Plant(val id: Int, val thickness: Int, val branches: MutableList<Branch> = mutableListOf()) {
    fun getEnergy(plants: Map<Int, Plant>, testValues: List<Int> = emptyList()): Long {
        val incoming = getIncomingEnergy(plants, testValues)
        return if (incoming >= thickness) incoming else 0
    }

    private fun getIncomingEnergy(plants: Map<Int, Plant>, testValues: List<Int> = emptyList()): Long {
        if (branches.isEmpty()) {
            if (testValues.isEmpty()) return 1
            return testValues[this.id - 1].toLong()
        }
        val value = branches.sumOf { branch ->
            branch.thickness * plants.getValue(branch.to).getEnergy(plants, testValues)
        }
        return value
    }
}

data class Branch(val to: Int, val thickness: Int)
