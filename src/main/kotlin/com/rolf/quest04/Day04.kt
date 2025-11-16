package com.rolf.quest04

import com.rolf.Day
import com.rolf.util.splitLines
import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val gears = splitLines(lines, "\n").flatten().map { it.toDouble() }
        val ratio = findRatio(gears)
        println((ratio * 2025).toInt())
    }

    private fun findRatio(gears: List<Double>): Double {
        var ratio = 1.0
        for (i in 1 until gears.size) {
            val gearRatio = gears[i - 1] / gears[i]
            ratio *= gearRatio
        }
        return ratio
    }

    override fun solve2(lines: List<String>) {
        val gears = splitLines(lines, "\n").flatten().map { it.toDouble() }
        val ratio = findRatio(gears)
        // Now that we know the ratio, we need to divide 10000000000000 by this,
        // because that would be the rotation for the first gear
        println(ceil(10000000000000 / ratio).toLong())
    }

    override fun solve3(lines: List<String>) {
        val gears = parseGears(lines)
        val ratio = findRatios(gears)
        println(floor(ratio * 100).toLong())
    }

    private fun parseGears(lines: List<String>): List<Gear> {
        return lines.map { line ->
            when (line.contains("|")) {
                true -> {
                    val (left, right) = line.split("|").map { it.toDouble() }
                    Gear(left, right)
                }

                false -> {
                    val value = line.toDouble()
                    Gear(value, value)
                }
            }
        }
    }

    private fun findRatios(gears: List<Gear>): Double {
        var ratio = 1.0
        for (i in 1 until gears.size) {
            val gearRatio = gears[i - 1].second / gears[i].first
            ratio *= gearRatio
        }
        return ratio
    }

    private data class Gear(val first: Double, val second: Double)
}
