package com.rolf.quest02

import com.rolf.Day

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val a = Complex.parse(lines[0])
        var r = Complex(0, 0)
        (0 until 3).forEach { i ->
            r = computation1(r, a)
        }
        println(r)
    }

    fun computation1(r: Complex, a: Complex): Complex {
        val s = Complex(10, 10)
        return r.multiply(r).divide(s).add(a)
    }

    override fun solve2(lines: List<String>) {
        val a = Complex.parse(lines[0])
        val b = a.add(Complex(1000, 1000))

        var count = 0
        for (y in a.y..b.y step 10) {
            for (x in a.x..b.x step 10) {
                val point = Complex(x, y)
                if (isEngraved(point) != null) count++
            }
        }
        println(count)
    }

    private fun isEngraved(c: Complex): Complex? {
        val range = -1_000_000..1_000_000
        val s = Complex(100_000, 100_000)
        var r = Complex(0, 0)
        repeat(100) {
            r = r.multiply(r).divide(s).add(c)
            if (r.x !in range || r.y !in range) {
                return null
            }
        }
        return r
    }

    override fun solve3(lines: List<String>) {
        val a = Complex.parse(lines[0])
        val b = a.add(Complex(1000, 1000))

        var count = 0
        for (y in a.y..b.y) {
            for (x in a.x..b.x) {
                val point = Complex(x, y)
                if (isEngraved(point) != null) count++
            }
        }
        println(count)
    }
}

data class Complex(val x: Long, val y: Long) {
    fun multiply(a: Complex): Complex {
        return Complex(
            x * a.x - y * a.y,
            x * a.y + y * a.x
        )
    }

    fun divide(a: Complex): Complex {
        return Complex(
            x / a.x,
            y / a.y
        )
    }

    fun add(a: Complex): Complex {
        return Complex(
            x + a.x,
            y + a.y
        )
    }

    override fun toString(): String {
        return "Complex[$x,$y]"
    }

    companion object {
        fun parse(line: String): Complex {
            val a = line.indexOf('[')
            val b = line.indexOf(',')
            val c = line.indexOf(']')
            return Complex(
                line.substring(a + 1, b).toLong(),
                line.substring(b + 1, c).toLong()
            )
        }
    }
}
