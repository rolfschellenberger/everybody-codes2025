package com.rolf.quest08

import com.rolf.Day
import com.rolf.util.splitLine
import kotlin.math.absoluteValue

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val numbers = splitLine(lines.first(), ",").map { it.toInt() }
        val nails = numbers.max()
        var count = 0
        for ((number, next) in numbers.zipWithNext()) {
            if (isOpposite(number, next, nails)) {
                count++
            }
        }
        println(count)
    }

    private fun isOpposite(number: Int, next: Int, nails: Int): Boolean {
        val diff = (number - next).absoluteValue
        return diff == nails / 2
    }

    override fun solve2(lines: List<String>) {
        val numbers = splitLine(lines.first(), ",").map { it.toInt() }
        val nails = numbers.max()

        val ropes = mutableListOf<Rope>()
        var knots = 0
        for ((number, next) in numbers.zipWithNext()) {
            val rope = Rope(number, next)
            val (crossFrom, crossTo) = rope.crossRopes(nails)
            for (otherRope in ropes) {
                if (otherRope.isFrom(crossFrom, crossTo)) {
                    knots++
                }
            }
            ropes.add(rope)
        }
        println(knots)
    }

    override fun solve3(lines: List<String>) {
        val numbers = splitLine(lines.first(), ",").map { it.toInt() }
        val nails = numbers.max()

        val ropes = mutableListOf<Rope>()
        for ((number, next) in numbers.zipWithNext()) {
            ropes.add(Rope(number, next))
        }

        var knots = 0
        for (from in 1..nails) {
            for (to in 1..nails) {
                if (from != to) {
                    knots = maxOf(knots, countKnots(ropes, from, to, nails))
                }
            }
        }
        println(knots)
    }

    private fun countKnots(ropes: MutableList<Rope>, from: Int, to: Int, nails: Int): Int {
        val cut = Rope(from, to)
        val (crossFrom, crossTo) = cut.crossRopes(nails)

        var knots = 0
        for (otherRope in ropes) {
            if (otherRope.isFrom(crossFrom, crossTo)) {
                knots++
            } else if (otherRope == cut) {
                knots++
            }
        }
        return knots
    }
}

data class Rope(val from: Int, val to: Int) {
    fun isFrom(fromRange: Set<Int>, toRange: Set<Int>): Boolean {
        return (from in fromRange && to in toRange) ||
                (from in toRange && to in fromRange)
    }

    fun crossRopes(nails: Int): Pair<Set<Int>, Set<Int>> {
        val from = mutableSetOf<Int>()
        val to = mutableSetOf<Int>()
        var f = true
        for (i in 1..nails) {
            when (i) {
                this.from -> f = !f
                this.to -> f = !f
                else -> {
                    if (f) from.add(i)
                    else to.add(i)
                }
            }
        }
        return from to to
    }
}
