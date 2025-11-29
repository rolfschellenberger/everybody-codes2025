package com.rolf.util

data class Block(val x: IntRange, val y: IntRange) {
    fun contains(point: Point): Boolean {
        return point.x in x && point.y in y
    }

    fun toPoints(): Set<Point> {
        val result = mutableSetOf<Point>()
        for (x in x.first..x.last) {
            for (y in y.first..y.last) {
                result.add(Point(x, y))
            }
        }
        return result
    }
}
