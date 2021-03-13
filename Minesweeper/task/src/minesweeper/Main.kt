package minesweeper

import kotlin.random.Random

class Field( var mineCount: Int) {

    class Item(var title: Char = '.') {
        var isMine: Boolean = false
        var isMark: Boolean = false
        override fun toString(): String {
            return "" + title
        }

        fun setMark() {
            isMark = !isMark
            title = if (isMark) '*' else '.'
        }

    }


    val gameField = Array(9, { Array(9, { Item() }) })

    init {
        repeat(mineCount) {
            while (true) {
                val x = Random.nextInt(0, 9)
                val y = Random.nextInt(0, 9)
                if (gameField[x][y].isMine == false) {
                    gameField[x][y].isMine = true
                    break
                }
            }
        }
        for (y in 0..8) {
            for (x in 0..8) {
                if (gameField[x][y].isMine == false) {
                    gameField[x][y] = lookAround(x, y)
                }
            }
        }
    }

    private fun lookAround(x: Int, y: Int): Item {
        var count = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (getGameFieldElement(i + x, j + y).isMine == true) {
                    count++
                }
            }
        }
        return if (count > 0) Item("$count"[0]) else Item()
    }

    private fun getGameFieldElement(x: Int, y: Int): Item {
        return try {
            gameField[x][y]
        } catch (e: IndexOutOfBoundsException) {
            Item()
        }
    }

    fun printField() {

        print(
            """
             |123456789|
            -|---------|
        """.trimIndent()
        )
        println()
        for (x in 0..8) {
            print("${x + 1}|")
            for (y in 0..8) {
                print(gameField[x][y])
            }
            println("|")
        }
        println("-|---------|")
    }

    fun startGame() {
        while (!isWin()) {
            print("Set/delete mines marks (x and y coordinates): > ")
            val (x, y) = readLine()!!.split(" ").map { it.toInt() - 1 }
            if (gameField[y][x].title.isDigit()) {
                println("There is a number here!")
                continue
            }
            gameField[y][x].setMark()
            println()
            printField()
        }
    }

    private fun isWin(): Boolean {
        val markMine = gameField.flatMap { it -> it.toList() }.filter { i -> i.isMark }.count { i -> i.isMine }
        val mark = gameField.flatMap { it -> it.toList() }.filter { i -> i.isMark }.count()
        if (markMine == mineCount && mark == mineCount) {
            println("Congratulations! You found all the mines!")
            return true
        }
        return false
    }


}

fun main() {
    print("How many mines do you want on the field? > ")
    val count = readLine()!!.toInt()
    val game = Field(count)
    game.printField()
    game.startGame()


}
