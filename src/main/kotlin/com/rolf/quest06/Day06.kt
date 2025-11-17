package com.rolf.quest06

import com.rolf.Day
import com.rolf.util.splitLine

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val letters = splitLine(lines[0], "").map { it.first() }
        var mentors = 0L
        val mentorCount = mutableMapOf<Char, Long>()
        for (letter in letters) {
            val count = mentorCount.getOrDefault(letter, 0)
            when (letter.isUpperCase()) {
                true -> {
                    mentorCount[letter] = count + 1
                }

                false -> {
                    if (letter == 'a')
                        mentors += mentorCount.getValue(letter.uppercaseChar())
                }
            }
        }
        println(mentors)
    }

    override fun solve2(lines: List<String>) {
        val letters = splitLine(lines[0], "").map { it.first() }
        var mentors = 0L
        val mentorCount = mutableMapOf<Char, Long>()
        for (letter in letters) {
            val count = mentorCount.getOrDefault(letter, 0)
            when (letter.isUpperCase()) {
                true -> {
                    mentorCount[letter] = count + 1
                }

                false -> {
                    mentors += mentorCount.getValue(letter.uppercaseChar())
                }
            }
        }
        println(mentors)
    }

    override fun solve3(lines: List<String>) {
        val letters = splitLine(lines[0], "").map { it.first() }
        val distance = 1000
        val repeat = 1000
        val sequence = mutableListOf<Char>()
        repeat(repeat) {
            sequence.addAll(letters)
        }

        var mentors = 0L
        for ((index, letter) in sequence.withIndex()) {
            if (letter.isLowerCase()) {
                mentors += findMentors(sequence, index, distance)
            }
        }
        println(mentors)
    }

    private fun findMentors(letters: List<Char>, index: Int, distance: Int): Long {
        val range = maxOf(0, index - distance)..minOf(letters.size - 1, index + distance)
        val char = letters[index].uppercaseChar()
        var mentors = 0L
        for (i in range) {
            val mentor = letters[i]
            if (mentor == char) {
                mentors++
            }
        }
        return mentors
    }
}
