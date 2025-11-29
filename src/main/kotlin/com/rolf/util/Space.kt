package com.rolf.util

import java.util.*
import kotlin.math.abs

open class Space<T>(internal val input: MutableMap<Point, T> = mutableMapOf()) {

    fun allElements(): List<T> {
        return input.values.toList()
    }

    fun allPoints(): List<Point> {
        return input.keys.toList()
    }

    fun get(x: Int, y: Int): T? {
        return get(Point(x, y))
    }

    fun get(point: Point): T? {
        return input[point]
    }

    fun set(x: Int, y: Int, value: T) {
        set(Point(x, y), value)
    }

    fun set(point: Point, value: T) {
        input[point] = value
    }

    fun remove(x: Int, y: Int): T? {
        return remove(Point(x, y))
    }

    fun remove(point: Point): T? {
        return input.remove(point)
    }

    fun count(value: T): Int {
        return count(setOf(value))
    }

    fun count(values: Set<T>): Int {
        return allElements().count { it in values }
    }

    fun find(value: T): List<Point> {
        return allPoints().filter { get(it) == value }
    }

    fun find(values: Set<T>): List<Point> {
        return allPoints().filter { values.contains(get(it)) }
    }

    fun getLeft(point: Point): Point? {
        return move(point, -1, 0)
    }

    fun getRight(point: Point): Point? {
        return move(point, 1, 0)
    }

    fun getUp(point: Point): Point? {
        return move(point, 0, -1)
    }

    fun getDown(point: Point): Point? {
        return move(point, 0, 1)
    }

    fun getLeftUp(point: Point): Point? {
        return move(point, -1, -1)
    }

    fun getLeftDown(point: Point): Point? {
        return move(point, -1, 1)
    }

    fun getRightUp(point: Point): Point? {
        return move(point, 1, -1)
    }

    fun getRightDown(point: Point): Point? {
        return move(point, 1, 1)
    }

    fun getForward(point: Point, direction: Direction): Point? {
        return when (direction) {
            Direction.NORTH -> getUp(point)
            Direction.EAST -> getRight(point)
            Direction.SOUTH -> getDown(point)
            Direction.WEST -> getLeft(point)
        }
    }

    fun getAllDirections(start: Point): List<Point> {
        return allPoints().filterNot { it == start }.map {
            val gcd = abs(greatestCommonDivisor(start.x - it.x, start.y - it.y))
            Point((it.x - start.x) / gcd, (it.y - start.y) / gcd)
        }.distinct()
    }

    fun getNextDirection(point: Point, direction: Point): Point {
        return Point(point.x + direction.x, point.y + direction.y)
    }

    private fun move(point: Point, xDelta: Int, yDelta: Int): Point? {
        return Point(point.x + xDelta, point.y + yDelta)
    }

    fun getNeighbours(
        point: Point,
        horizontal: Boolean = true,
        vertical: Boolean = true,
        diagonal: Boolean = true,
        includeOwn: Boolean = false,
        wrap: Boolean = false,
    ): Set<Point> {
        val result = mutableSetOf<Point>()
        if (includeOwn) result.add(point)
        if (horizontal) {
            getLeft(point)?.let { result.add(it) }
            getRight(point)?.let { result.add(it) }
        }
        if (vertical) {
            getUp(point)?.let { result.add(it) }
            getDown(point)?.let { result.add(it) }
        }
        if (diagonal) {
            getLeftUp(point)?.let { result.add(it) }
            getLeftDown(point)?.let { result.add(it) }
            getRightUp(point)?.let { result.add(it) }
            getRightDown(point)?.let { result.add(it) }
        }
        return result
    }

    fun getArea(topLeftInclusive: Point, bottomRightInclusive: Point): List<Point> {
        val points = mutableListOf<Point>()
        for (y in topLeftInclusive.y..bottomRightInclusive.y) {
            for (x in topLeftInclusive.x..bottomRightInclusive.x) {
                points.add(Point(x, y))
            }
        }
        return points
    }

    fun getOppositePointOverX(x: Int, y: Int, centerX: Int): Point {
        val diff = abs(centerX - x)
        return if (centerX > x) {
            Point(centerX + diff, y)
        } else {
            Point(centerX - diff, y)
        }
    }

    fun getOppositePointOverY(x: Int, y: Int, centerY: Int): Point {
        val diff = abs(centerY - y)
        return if (centerY > y) {
            Point(x, centerY + diff)
        } else {
            Point(x, centerY - diff)
        }
    }

    fun subSpace(topLeftInclusive: Point, bottomRightInclusive: Point): Space<T> {
        val newInput = input.filter { (point, value) ->
            point.x in topLeftInclusive.x..bottomRightInclusive.x &&
                    point.y in topLeftInclusive.y..bottomRightInclusive.y
        }.toMutableMap()
        return Space(newInput)
    }

    fun replace(map: Map<T, T>) {
        for (point in allPoints()) {
            val value = get(point)
            if (map.containsKey(value)) {
                set(point, map[value]!!)
            }
        }
    }

    fun findPath(
        from: Point,
        to: Set<Point>,
        notAllowedLocations: Set<Block> = emptySet(),
        diagonal: Boolean = false,
        customAllowedFunction: (grid: Space<T>, from: Point, to: Point, path: Path) -> Boolean = { _, _, _, _ -> true },
        customScoreFunction: (grid: Space<T>, from: Point, to: Point, path: Path) -> Int = { _, _, _, _ -> 1 },
    ): Path {
        val paths = PriorityQueue<Path>()
        val seen: MutableSet<Point> = mutableSetOf()

        // Function to filter allowed locations
        fun isAllowed(from: Point, to: Point, path: Path): Boolean {
            if (seen.contains(to)) return false
            if (notAllowedLocations.any { block -> block.contains(to) }) return false
            if (!customAllowedFunction(this, from, to, path)) return false
            return true
        }

        fun getNeighbours(point: Point, path: Path): List<Point> {
            return getNeighbours(point, diagonal = diagonal).filter { isAllowed(point, it, path) }.sorted()
        }

        // Start with the neighbours of the starting point that are allowed to visit.
        paths.add(Path(listOf(from)))

        while (paths.isNotEmpty()) {
            val path = paths.poll()
            val pathEnd: Point = path.locations.last()

            // Arrived at destination?
            if (to.contains(pathEnd)) {
                return path
            }

            // Continue only new locations
            if (pathEnd !in seen) {
                seen.add(pathEnd)

                for (neighbour in getNeighbours(pathEnd, path)) {
                    val score = customScoreFunction(this, pathEnd, neighbour, path)
                    paths.add(Path(path.locations + neighbour, path.score + score))
                }
            }
        }
        return Path(emptyList())
    }

    // Note to self: When this function is not working, maybe it can be solved faster with a graph of fixed paths?
    fun findAllPaths(
        from: Point,
        to: Point,
        notAllowedLocations: Set<Point> = emptySet(),
        diagonal: Boolean = false,
        customAllowedFunction: (grid: Space<T>, from: Point, to: Point, path: Path) -> Boolean = { _, _, _, _ -> true },
        customScoreFunction: (grid: Space<T>, from: Point, to: Point, path: Path) -> Int = { _, _, _, _ -> 1 },
    ): List<Path> {
        val seen: MutableSet<Point> = notAllowedLocations.toMutableSet()

        // Function to filter allowed locations
        fun isAllowed(from: Point, to: Point, path: Path): Boolean {
            if (seen.contains(to)) return false
            if (notAllowedLocations.contains(to)) return false
            if (!customAllowedFunction(this, from, to, path)) return false
            return true
        }

        fun getNeighbours(point: Point, path: Path): List<Point> {
            return getNeighbours(point, diagonal = diagonal).filter { isAllowed(point, it, path) }.sorted()
        }

        fun dfs(from: Point, paths: MutableList<Path>, path: Path = Path(listOf(from))) {
            seen.add(from)

            if (from == to) {
                paths.add(path)
            } else {
                for (neighbour in getNeighbours(from, path)) {
                    val score = customScoreFunction(this, from, neighbour, path)
                    val p = Path(path.locations + neighbour, path.score + score)
                    dfs(neighbour, paths, p)
                }
            }

            // Backtrack
            seen.remove(from)
        }

        val paths = mutableListOf<Path>()
        dfs(from, paths)
        return paths
    }

    fun waterFill(
        start: Point,
        notAllowedValues: Set<T>,
        horizontal: Boolean = true,
        vertical: Boolean = true,
        diagonal: Boolean = true,
    ): Set<Point> {
        val watered = mutableSetOf<Point>(start)
        val inspect = mutableSetOf(start)
        val notAllowed = find(notAllowedValues).toSet()
        while (inspect.isNotEmpty()) {
            val neighbours = inspect.map {
                getNeighbours(it, horizontal = horizontal, vertical = vertical, diagonal = diagonal)
            }.flatten().toSet() - watered - notAllowed
            watered += neighbours

            inspect.clear()
            inspect.addAll(neighbours)
        }
        return watered
    }

    open fun copy(): Space<T> {
        val inputCopy = input.toMutableMap()
        return Space(inputCopy)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Space<*>

        if (input != other.input) return false

        return true
    }

    override fun hashCode(): Int {
        return input.hashCode()
    }
}
