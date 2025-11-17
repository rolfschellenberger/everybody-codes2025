package com.rolf.quest07

import com.rolf.Day
import com.rolf.util.groupLines

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val groups = groupLines(lines, "")
        val names = groups.first().first().split(",")
        val rules = parseRules(groups[1])

        for (name in names) {
            if (isMatch(name, rules)) {
                println(name)
            }
        }
    }

    private fun isMatch(name: String, rules: List<Rule>): Boolean {
        for (part in name.windowed(2)) {
            val (from, to) = part.chunked(1)
            if (rules.none { rule -> rule.isMatch(from, to) }) {
                return false
            }
        }
        return true
    }

    private fun parseRules(lines: List<String>): List<Rule> {
        return lines.map { line ->
            val (from, to) = line.split(" > ")
            val toList = to.split(",").toSet()
            Rule(from, toList)
        }
    }

    override fun solve2(lines: List<String>) {
        val groups = groupLines(lines, "")
        val names = groups.first().first().split(",")
        val rules = parseRules(groups[1])

        var sum = 0
        for ((index, name) in names.withIndex()) {
            if (isMatch(name, rules)) {
                sum += index + 1
            }
        }
        println(sum)
    }

    override fun solve3(lines: List<String>) {
        val groups = groupLines(lines, "")
        val names = groups.first().first().split(",")
        val rules = parseRules(groups[1])

        val prefixes = mutableSetOf<String>()
        for (name in names) {
            if (isMatch(name, rules)) {
                prefixes += findPrefixes(name, rules, 7, 11)
            }
        }
        println(prefixes.size)
    }

    private fun findPrefixes(name: String, rules: List<Rule>, minLength: Int, maxLength: Int): Set<String> {
        if (name.length > maxLength) {
            return emptySet()
        }

        val last = name.last().toString()
        val options = rules.filter { rule ->
            rule.from == last
        }.map { rule ->
            rule.to
        }.flatten().toSet()

        val prefixes = mutableSetOf<String>()
        for (option in options) {
            val newName = "$name$option"
            if (newName.length in minLength..maxLength) {
                prefixes += newName
            }
            prefixes += findPrefixes("$name$option", rules, minLength, maxLength)
        }
        return prefixes
    }
}

data class Rule(val from: String, val to: Set<String>) {
    fun isMatch(a: String, b: String): Boolean {
        return from == a && to.contains(b)
    }
}
