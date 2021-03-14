package minesweeper

import java.util.*
import kotlin.random.Random

class Field(var mineCount: Int) {

    class Item(val id: Int, val x: Int, val y: Int) {

        var title: Char = '.'

        var lookMine = 0
            set(value) {
                field = value
                setTitle()
            }
        var isLook = false
            set(value) {
                field = value
                setTitle()
            }
        var showMine = false
            set(value) {
                field = value
                setTitle()
            }
        var isMine: Boolean = false
            set(value) {
                field = value
                setTitle()
            }
        var isMark: Boolean = false
            set(value) {
                field = value
                setTitle()
            }

        fun setTitle() {
            title = when {
                isMark -> '*'
                isMine && showMine -> 'X'
                isLook -> {
                    if (lookMine == 0) '/' else "$lookMine"[0]
                }
                else -> '.'
            }
        }

        fun setMark() {
            isMark = !isMark
        }

        override fun toString(): String {
            return "" + title
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Item

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id
        }


        companion object {
            var id = 0
            fun getItem(x: Int, y: Int): Item {
                return Item(id++, x, y)
            }

        }

    }


    val gameField = Array(9, { y -> Array(9, { x -> Item.getItem(x, y) }) })
    var count = 0

    init {
        repeat(mineCount) {
            setMines()
        }
        initField()
    }

    private fun setMines() {
        while (true) {
            val x = Random.nextInt(0, 9)
            val y = Random.nextInt(0, 9)
            if (!gameField[y][x].isMine) {
                gameField[y][x].isMine = true
                break
            }
        }
    }

    private fun initField() {
        for (y in 0..8) {
            for (x in 0..8) {
                if (gameField[y][x].isMine == false) {
                    gameField[y][x].lookMine = lookAround(x, y)
                }
            }
        }
    }

    private fun lookAround(x: Int, y: Int): Int {
        var count = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (getGameFieldElement(i + x, j + y) == true) {
                    count++
                }
            }
        }
        return count
    }

    private fun getGameFieldElement(x: Int, y: Int): Boolean {
        return try {
            gameField[y][x].isMine
        } catch (e: IndexOutOfBoundsException) {
            false
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
        for (y in 0..8) {
            print("${y + 1}|")
            for (x in 0..8) {
                print(gameField[y][x])
            }
            println("|")
        }
        println("-|---------|")
    }

    fun startGame() {
        while (!isWin()) {
            count++
            print("Set/unset mines marks or claim a cell as free: > ")
            val tmp = readLine()!!.split(" ")

            val (x, y) = tmp.subList(0, 2).map { it.toInt() - 1 }
            val s = tmp[2]
            when (s) {
                "free" -> {
                    var gameOver = testItem(x, y)
                    if (gameOver && count == 1) {
                        setMines()
                        gameField[x][y].isMine = false
                        initField()
                        gameOver = testItem(x, y)
                    }
                    if (gameOver) {
                        showMines()
                        println("You stepped on a mine and failed!")
                        break
                    } else {
                        printField()
                    }

                }
                "mine" -> {
                    gameField[y][x].setMark()
                    println()
                    printField()
                }
            }
        }
    }

    private fun showMines() {
        val list = gameField.flatMap { it -> it.toList() }.filter { i -> i.isMine }.forEach { i -> i.showMine = true }
        printField()
    }

    private fun testItem(x: Int, y: Int): Boolean {
        if (gameField[y][x].isMine) {
            return true
        } else if (gameField[y][x].lookMine > 0) {
            gameField[y][x].isLook = true
            return false
        } else {
            val queue: Queue<Item> = LinkedList<Item>()
            queue.add(gameField[y][x])
            while (!queue.isEmpty()) {
                val element = queue.remove()
                val listElem = arrayListOf<Item>()
                for (x1 in -1..1) {
                    for (y1 in -1..1) {
                        try {
                            if (x1 != 0 || y1 != 0) {
                                listElem.add(gameField[element.y + y1][element.x + x1])
                            }
                        } catch (e: IndexOutOfBoundsException) {
                        }
                    }
                }
                for (e in listElem) {
                    if (!e.isLook) {
                        e.isMark = false
                        if (e.lookMine == 0) {
                            e.isLook = true
                            queue.add(e)
                        } else if (e.lookMine > 0) {
                            e.isLook = true
                        }
                    }

                }
            }
        }
        return false
    }

    private fun isWin(): Boolean {
        val countOpen = gameField.flatMap { it -> it.toList() }.count { it -> it.isLook }
        if (countOpen + mineCount == 81) {
            println("Congratulations! You found all the mines!")
            return true
        }

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
