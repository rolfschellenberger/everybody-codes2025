package com.rolf.quest09

import com.rolf.Day
import com.rolf.util.splitLine
import com.rolf.util.splitLines

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val scales = parseScales(lines)
        val cache = mutableSetOf<Set<Int>>()
        for (parent1 in scales) {
            for (parent2 in scales) {
                for (child in scales) {
                    if (child.hasParents(parent1, parent2)) {
                        if (cache.add(setOf(parent1.id, parent2.id, child.id))) {
                            val similarity1 = parent1.similarity(child)
                            val similarity2 = parent2.similarity(child)
                            println(similarity1 * similarity2)
                        }
                    }
                }
            }
        }
    }

    private fun parseScales(lines: List<String>): List<Scale> {
        return splitLines(lines, ":").map { line ->
            val (id, s) = line
            val sequence = splitLine(s).map { it.first() }
            Scale(id.toInt(), sequence)
        }
    }

    override fun solve2(lines: List<String>) {
        val scales = parseScales(lines)
        val cache = mutableSetOf<Set<Int>>()
        var sum = 0L
        for (parent1 in scales) {
            for (parent2 in scales) {
                for (child in scales) {
                    if (child.hasParents(parent1, parent2)) {
                        if (cache.add(setOf(parent1.id, parent2.id, child.id))) {
                            val similarity1 = parent1.similarity(child)
                            val similarity2 = parent2.similarity(child)
                            sum += similarity1 * similarity2
                        }
                    }
                }
            }
        }
        println(sum)
    }

    override fun solve3(lines: List<String>) {
        val scales = parseScales(lines)
        for (parent1 in scales) {
            for (parent2 in scales) {
                for (child in scales) {
                    child.hasParents(parent1, parent2)
                }
            }
        }
        val largestFamily = findLargestFamily(scales)
        println(largestFamily.sumOf { it.id })
    }

    private fun findLargestFamily(scales: List<Scale>): Set<Scale> {
        val e = scales.toMutableSet()
        val families = mutableListOf<Set<Scale>>()
        while (e.isNotEmpty()) {
            val scale = e.first()
            val family = findFamily(scale)
            families.add(family)
            e.removeAll(family)
        }
        return families.maxBy { family -> family.size }
    }

    private fun findFamily(scale: Scale, family: Set<Scale> = setOf()): Set<Scale> {
        if (family.contains(scale)) return emptySet()

        val result = mutableSetOf(scale)
        for (parent in scale.parents) {
            result += findFamily(parent, family + result)
        }
        for (child in scale.children) {
            result += findFamily(child, family + result)
        }
        return result
    }
}

data class Scale(
    val id: Int,
    val dna: List<Char>,
    val parents: MutableSet<Scale> = mutableSetOf(),
    val children: MutableSet<Scale> = mutableSetOf(),
) {
    fun similarity(child: Scale): Long {
        var score = 0L
        for (i in 0 until dna.size) {
            if (dna[i] == child.dna[i]) {
                score++
            }
        }
        return score
    }

    fun hasParents(parent1: Scale, parent2: Scale): Boolean {
        if (parent1 == this) return false
        if (parent2 == this) return false
        if (parent1 == parent2) return false

        for (i in 0 until dna.size) {
            val p1 = parent1.dna[i]
            val p2 = parent2.dna[i]
            val c = dna[i]
            if (p1 == p2 && c != p1) return false
            if (p1 != p2 && c != p1 && c != p2) return false
        }
        parents.add(parent1)
        parents.add(parent2)
        parent1.children.add(this)
        parent2.children.add(this)
        return true
    }

    override fun toString(): String {
        return "Scale(id=$id, parents=${parents.size}, children=${children.size})"
    }

    override fun hashCode(): Int {
        return id
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other) && (other as Scale).id == this.id
    }
}
