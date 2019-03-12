package com.staa.games.orien.engine.game

import java.io.Serializable
import kotlin.math.abs

class GameBoard(private val n: Int) : Serializable, Cloneable
{
    private val grid = Array(n) {
        Array(n) { nul }
    }

    private val hors = arrayListOf<ArrayList<Point>>()
    private val vers = arrayListOf<ArrayList<Point>>()
    private val rgts = arrayListOf<ArrayList<Point>>()
    private val lfts = arrayListOf<ArrayList<Point>>()

    val tokens = mapOf(
            Pair(hor, hors),
            Pair(ver, vers),
            Pair(rgt, rgts),
            Pair(lft, lfts))

    operator fun set(point: Point, value: Int) = set(point.first, point.second, value)

    operator fun set(i: Int, j: Int, value: Int)
    {
        if (value == nul)
        {
            grid[i][j] = nul
            return reSync()
        }

        if (value !in 1..4)
        {
            throw UnsupportedOperationException("Unsupported state")
        }

        if (i !in 0 until n || j !in 0 until n)
        {
            throw UnsupportedOperationException("Grid point not available: ($i,$j)")
        }

        // prevent play into non-empty space
        if (grid[i][j] != nul)
        {
            return
        }

        grid[i][j] = value

        val point = Point(i, j)

        when (value)
        {
            hor ->
            {
                treatStraight(hors, point, { it.first }, { it.second })
            }
            ver ->
            {
                treatStraight(vers, point, { it.second }, { it.first })
            }
            rgt ->
            {
                treatSlant(rgts, point) { a, b ->
                    a.first + a.second == b.first + b.second
                }
            }
            lft ->
            {
                treatSlant(lfts, point) { a, b ->
                    abs((a.first + a.second) - (b.first + b.second)) == 2
                }
            }
        }
    }

    operator fun get(i: Int, j: Int) = grid[i][j]

    operator fun get(point: Point) = get(point.first, point.second)

    fun getEmptyPoints(): Array<Point>
    {
        val list = arrayListOf<Point>()

        for (r in 0 until n)
        {
            for (j in 0 until n)
            {
                if (grid[r][j] == nul)
                    list += Point(r, j)
            }
        }

        return list.toTypedArray()
    }

    private fun reSync()
    {
        val clone = Array(n) { i ->
            Array(n) { j ->
                val p = grid[i][j]
                grid[i][j] = nul
                p
            }
        }
        tokens.values.forEach { it.clear() }

        for (i in 0 until n)
        {
            for (j in 0 until n)
            {
                if (clone[i][j] != nul)
                {
                    this[i, j] = clone[i][j]
                }
            }
        }
    }


    fun isFilled() = !grid.any { it.contains(nul) }


    private inline fun treatStraight(line: ArrayList<ArrayList<Point>>,
                                     point: Point,
                                     crossinline selectSame: (Point) -> Int,
                                     crossinline selectOther: (Point) -> Int)
    {
        return treatLine(line, point) {
            selectSame(it) == selectSame(point) &&
                    abs(selectOther(it) - selectOther(point)) == 1
        }
    }


    private inline fun treatSlant(line: ArrayList<ArrayList<Point>>,
                                  point: Point,
                                  filter: (Point, Point) -> Boolean)
    {
        return treatLine(line, point) {
            filter(it, point) &&
                    abs(it.first - point.first) == abs(it.second - point.second) &&
                    abs(it.first - point.first) == 1
        }
    }


    private inline fun treatLine(line: ArrayList<ArrayList<Point>>, point: Point, filter: (Point) -> Boolean)
    {
        val lists = line.withIndex().filter { (_, it) ->
            it.any {
                filter(it)
            }
        }.toMutableList()

        if (lists.isEmpty())
        {
            line.add(arrayListOf(point))
        }
        else
        {
            when (lists.size)
            {
                1    ->
                {
                    line[lists[0].index].add(point)
                }
                2    ->
                {
                    line[lists[0].index].addAll(line[lists[1].index] + point)
                    line.removeAt(lists[1].index)
                }
                else ->
                {
                    error("Logic error!")
                }
            }
        }
    }


    override fun toString(): String
    {
        return grid.joinToString(separator = "\n",
                                 prefix = "{\n",
                                 postfix = "\n}")
        {
            "\t${it.contentDeepToString()}"
        }
    }
}