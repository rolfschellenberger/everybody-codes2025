package com.rolf.quest13

import com.rolf.Day
import kotlin.math.absoluteValue

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val wheel = Wheel.parse(lines)
        wheel.turn(2025)
        println(wheel.numbers[wheel.pointer])
    }

    override fun solve2(lines: List<String>) {
        val wheel = Wheel2.parse(lines)
        println(wheel.turn(20252025))
    }

    override fun solve3(lines: List<String>) {
        val wheel = Wheel2.parse(lines)
        println(wheel.turn(202520252025))
    }
}

data class Wheel(val numbers: MutableList<Int> = mutableListOf(1), var pointer: Int = 0) {
    fun add(numbers: List<Int>) {
        for ((index, number) in numbers.withIndex()) {
            if (index % 2 == 0) {
                this.numbers.addLast(number)
            } else {
                this.numbers.addFirst(number)
                this.pointer++
            }
        }
    }

    fun turn(steps: Int) {
        pointer = (pointer + steps) % this.numbers.size
    }

    companion object {
        fun parse(lines: List<String>): Wheel {
            val wheel = Wheel()
            val numbers = lines.map { line ->
                line.toInt()
            }
            wheel.add(numbers)
            return wheel
        }
    }
}

data class Wheel2(
    val rangesLeft: MutableList<IntProgression> = mutableListOf(1..1),
    val rangesRight: MutableList<IntProgression> = mutableListOf(),
) {
    fun add(range: IntRange) {
        if (rangesRight.size < rangesLeft.size) {
            rangesRight.add(range)
        } else {
            rangesLeft.addFirst(range.last downTo range.first)
        }
    }

    fun turn(steps: Long): Int {
        // Calculate the wheel size first
        val rightSize = size(rangesRight)
        val leftSize = size(rangesLeft)
        val size = rightSize + leftSize
        // Reduce the steps
        var stepsRemaining = steps % size
        for (range in rangesRight + rangesLeft) {
            if (range.size() >= stepsRemaining) {
                return range.elementAt(stepsRemaining.toInt() - 1)
            }

            // Otherwise reduce the steps for the next range
            stepsRemaining -= range.size()
        }
        throw IllegalStateException("Expected to find a value while turning")
    }

    private fun size(range: MutableList<IntProgression>): Int {
        return range.sumOf { it.size() }
    }

    companion object {
        fun parse(lines: List<String>): Wheel2 {
            val wheel = Wheel2()
            lines.forEach { line ->
                val (a, b) = line.split("-")
                val range = a.toInt()..b.toInt()
                wheel.add(range)
            }
            return wheel
        }
    }
}

fun IntProgression.size(): Int {
    return (this.last - this.first).absoluteValue + 1
}
