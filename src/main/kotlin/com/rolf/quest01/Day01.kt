package com.rolf.quest01

import com.rolf.Day
import com.rolf.util.splitLine

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val names = splitLine(lines[0], ",")
        val instructions = splitLine(lines[2], ",")
        var pointer = 0
        instructions.forEach {
            val direction = it[0]
            val amount = it.substring(1).toInt()
            when (direction) {
                'L' -> pointer = maxOf(0, pointer - amount)
                'R' -> pointer = minOf(names.size - 1, pointer + amount)
            }
        }
        println(names[pointer])
    }

    override fun solve2(lines: List<String>) {
        val names = splitLine(lines[0], ",")
        val instructions = splitLine(lines[2], ",")
        var pointer = 0
        instructions.forEach {
            val direction = it[0]
            val amount = it.substring(1).toInt()
            when (direction) {
                'L' -> pointer -= amount
                'R' -> pointer += amount
            }
            pointer = pointer.mod(names.size)
        }
        println(names[pointer])
    }

    override fun solve3(lines: List<String>) {
        val names = splitLine(lines[0], ",").toMutableList()
        val instructions = splitLine(lines[2], ",")
        val pointer1 = 0
        instructions.forEach {
            var pointer2 = 0
            val direction = it[0]
            val amount = it.substring(1).toInt()
            when (direction) {
                'L' -> pointer2 -= amount
                'R' -> pointer2 += amount
            }
            pointer2 = pointer2.mod(names.size)
            val tmpName = names[pointer1]
            names[pointer1] = names[pointer2]
            names[pointer2] = tmpName
        }
        println(names[pointer1])
    }
}
