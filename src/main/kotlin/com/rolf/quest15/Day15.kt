package com.rolf.quest15

import com.rolf.Day
import com.rolf.util.*
import kotlin.math.sign

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val matrix = MatrixString.buildDefault(50, 50, ".")
        val instructions = parseInstructions(lines)

        var location = matrix.center()
        var direction = Direction.NORTH
        matrix.set(location, "S")
        for (instruction in instructions) {
            direction = when (instruction.direction) {
                "R" -> direction.right()
                "L" -> direction.left()
                else -> throw IllegalStateException("Incorrect direction $instruction")
            }
            repeat(instruction.amount) {
                location = matrix.getForward(location, direction)!!
                matrix.set(location, "#")
            }
        }
        matrix.set(location, "E")

        println(
            matrix.findPathByValue(
                matrix.find("S").first(),
                matrix.find("E").first(),
                setOf("#")
            ).size - 1
        )
    }

    private fun parseInstructions(lines: List<String>): List<Instruction> {
        return lines.first().split(",").map {
            val direction = it.first().toString()
            val amount = it.substring(1).toInt()
            Instruction(direction, amount)
        }
    }

    override fun solve2(lines: List<String>) {
        val matrix = MatrixString.buildDefault(5000, 5000, ".")
        val instructions = parseInstructions(lines)

        var location = matrix.center()
        var direction = Direction.NORTH
        matrix.set(location, "S")
        for (instruction in instructions) {
            direction = when (instruction.direction) {
                "R" -> direction.right()
                "L" -> direction.left()
                else -> throw IllegalStateException("Incorrect direction $instruction")
            }
            repeat(instruction.amount) {
                location = matrix.getForward(location, direction)!!
                matrix.set(location, "#")
            }
        }
        matrix.set(location, "E")

        println(
            matrix.findPathByValue(
                matrix.find("S").first(),
                matrix.find("E").first(),
                setOf("#")
            ).size - 1
        )
    }

    override fun solve3(lines: List<String>) {
        val instructions = parseInstructions(lines)

        // Now we know that the first corner and the last corner are the start and end.
        val corners = findCorners(instructions)
        val walls = findWalls(corners)
        val interestingPoints = findInterestingPoints(corners)

        // Next up is to build a Graph where we add each corner
        val graph = Graph<Point>()
        for (point in interestingPoints) {
            graph.addVertex(Vertex(point.toString(), point))
        }
        // And with its connection to any other corner that is possible.
        for (interestingPoint in interestingPoints) {
            for (otherPoint in interestingPoints) {
                if (interestingPoint == otherPoint) continue

                if (canMove(interestingPoint, otherPoint, walls)) {
                    graph.addEdge(
                        interestingPoint.toString(),
                        otherPoint.toString(),
                        EdgeType.UNDIRECTED,
                        interestingPoint.distance(otherPoint).toDouble()
                    )
                }
            }
        }

        val path = graph.shortestPathAndWeight(
            corners.first().toString(),
            corners.last().toString()
        )
        println(path.second.toLong())
    }

    private fun findInterestingPoints(corners: List<Point>): List<Point> {
        val edges = mutableListOf<Point>()
        for (corner in corners) {
            edges.add(Point(corner.x, corner.y))
            edges.add(Point(corner.x, corner.y + 1))
            edges.add(Point(corner.x, corner.y - 1))
            edges.add(Point(corner.x + 1, corner.y))
            edges.add(Point(corner.x + 1, corner.y + 1))
            edges.add(Point(corner.x + 1, corner.y - 1))
            edges.add(Point(corner.x - 1, corner.y))
            edges.add(Point(corner.x - 1, corner.y + 1))
            edges.add(Point(corner.x - 1, corner.y - 1))
        }
        return edges
    }

    private fun findCorners(instructions: List<Instruction>): List<Point> {
        val corners = mutableListOf<Point>()
        var location = Point(0, 0)
        var direction = Direction.NORTH
        corners.add(location)
        instructions.forEach { instruction ->
            direction = getDirection(direction, instruction)
            location = getLocation(location, direction, instruction.amount)
            corners.add(location)
        }
        return corners
    }

    private fun findWalls(corners: List<Point>): List<Wall> {
        return corners.zipWithNext().map {
            val from = it.first
            val to = it.second
            // Shorten the walls with 1, so we don't block the start and end locations.
            if (from.x == to.x) {
                val diff = (to.y - from.y).sign
                val newFrom = from.copy(y = from.y + diff)
                val newTo = to.copy(y = to.y - diff)
                Wall(newFrom, newTo)
            } else {
                val diff = (to.x - from.x).sign
                val newFrom = from.copy(x = from.x + diff)
                val newTo = to.copy(x = to.x - diff)
                Wall(newFrom, newTo)
            }
        }
    }

    private fun getDirection(currentDirection: Direction, instruction: Instruction): Direction {
        return when (instruction.direction) {
            "R" -> currentDirection.right()
            "L" -> currentDirection.left()
            else -> throw IllegalStateException("Incorrect direction $instruction")
        }
    }

    private fun getLocation(currentLocation: Point, direction: Direction, amount: Int): Point {
        return when (direction) {
            Direction.NORTH -> Point(currentLocation.x, currentLocation.y - amount)
            Direction.SOUTH -> Point(currentLocation.x, currentLocation.y + amount)
            Direction.EAST -> Point(currentLocation.x + amount, currentLocation.y)
            Direction.WEST -> Point(currentLocation.x - amount, currentLocation.y)
        }
    }

    private fun canMove(from: Point, to: Point, walls: List<Wall>): Boolean {
        // Make a rectangular for the from and to
        val minX = minOf(from.x, to.x)
        val maxX = maxOf(from.x, to.x)
        val minY = minOf(from.y, to.y)
        val maxY = maxOf(from.y, to.y)

        for (wall in walls) {
            val wallMinX = minOf(wall.from.x, wall.to.x)
            val wallMaxX = maxOf(wall.from.x, wall.to.x)
            val wallMinY = minOf(wall.from.y, wall.to.y)
            val wallMaxY = maxOf(wall.from.y, wall.to.y)

            // Check if the rectangulars do not overlap, so this wall is not blocking
            if (maxX < wallMinX) continue
            if (minX > wallMaxX) continue
            if (maxY < wallMinY) continue
            if (minY > wallMaxY) continue
            return false
        }
        return true
    }
}

data class Instruction(val direction: String, val amount: Int)

data class Wall(val from: Point, val to: Point)
