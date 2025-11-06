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

    }
}
