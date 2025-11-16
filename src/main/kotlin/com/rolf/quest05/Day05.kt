package com.rolf.quest05

import com.rolf.Day
import com.rolf.util.splitLine

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val (id, numbers) = parseLine(lines[0])
        val spine = Spine(id)
        for (number in numbers) {
            spine.add(number)
        }
        println(spine.quality())
    }

    override fun solve2(lines: List<String>) {
        val qualities = mutableListOf<Long>()
        for (line in lines) {
            val (id, numbers) = parseLine(line)
            val spine = Spine(id)
            for (number in numbers) {
                spine.add(number)
            }
            qualities.add(spine.quality())
        }
        println(qualities.max() - qualities.min())
    }

    private fun parseLine(line: String): Pair<Long, List<Long>> {
        val (id, numbersList) = splitLine(line, ":")
        val numbers = splitLine(numbersList, ",").map { it.toLong() }
        return id.toLong() to numbers
    }

    override fun solve3(lines: List<String>) {
        val spines = mutableListOf<Spine>()
        for (line in lines) {
            val (id, numbers) = parseLine(line)
            val spine = Spine(id)
            for (number in numbers) {
                spine.add(number)
            }
            spines.add(spine)
        }
        val sorted = spines.sorted().reversed()
        println(sorted.mapIndexed { index, spine ->
            (index + 1) * spine.id
        }.sum())
    }
}

data class Spine(val id: Long, val values: MutableList<Segment> = mutableListOf()) : Comparable<Spine> {
    fun add(it: Long) {
        for (value in values) {
            if (value.add(it)) {
                return
            }
        }

        values.add(Segment(it))
    }

    fun quality(): Long {
        return values.map { it.value }.joinToString("").toLong()
    }

    override fun compareTo(other: Spine): Int {
        val quality = quality()
        val otherQuality = other.quality()
        if (quality != otherQuality) {
            return quality.compareTo(otherQuality)
        }

        // Compare the levels
        for (i in 0 until values.size) {
            val q = values[i].quality()
            val qOther = other.values[i].quality()
            if (q != qOther) {
                return q.compareTo(qOther)
            }
        }

        return id.compareTo(other.id)
    }
}

data class Segment(val value: Long, var left: Long? = null, var right: Long? = null) {
    fun add(it: Long): Boolean {
        if (it < value && left == null) {
            left = it
            return true
        }
        if (it > value && right == null) {
            right = it
            return true
        }
        return false
    }

    fun quality(): Long {
        val list = mutableListOf<Long>()
        if (left != null) {
            list.add(left!!)
        }
        list.add(value)
        if (right != null) {
            list.add(right!!)
        }
        return list.joinToString("").toLong()
    }

    override fun toString(): String {
        return "$left-$value-$right"
    }
}
