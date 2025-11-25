package com.rolf.quest16

import com.rolf.Day

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val numbers = lines[0].split(",").map { it.toLong() }
        println(getWallSize(90, numbers))
    }

    override fun solve2(lines: List<String>) {
        val wall = lines[0].split(",").map { it.toLong() }.toLongArray()
        val numbers = findSpell(wall)
        println(numbers.reduce { acc, i -> acc * i })
    }

    private fun findSpell(wall: LongArray): List<Long> {
        val numbers = mutableListOf<Long>()
        for (i in 1..wall.size) {
            if (subtract(i, wall)) {
                numbers.add(i.toLong())
            }
        }
        return numbers
    }

    private fun subtract(number: Int, wall: LongArray): Boolean {
        for (i in number - 1 until wall.size step number) {
            if (wall[i] <= 0) return false
        }

        for (i in number - 1 until wall.size step number) {
            wall[i] -= 1
        }
        return true
    }

    override fun solve3(lines: List<String>) {
        val wall = lines[0].split(",").map { it.toLong() }.toLongArray()
        val numbers = findSpell(wall)

        val blocks = 202520252025000
        var from = 0L
        var till = blocks

        while (from < till) {
            val mid = (from + till) / 2
            if (mid == from || mid == till) break
            val wallSize = getWallSize(mid, numbers)
            if (wallSize == blocks) break
            if (wallSize > blocks) {
                till = mid
            } else {
                from = mid
            }
        }
        println(from)
    }

    private fun getWallSize(columns: Long, numbers: List<Long>): Long {
        return numbers.sumOf {
            columns / it
        }
    }
}
