package com.staa.games.orien.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.MotionEvent
import android.widget.FrameLayout
import com.staa.games.orien.R
import com.staa.games.orien.engine.game.*
import com.staa.games.orien.engine.game.Point
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min

class GameView(context: Context) : FrameLayout(context)
{
    private lateinit var game: Game
    private var initGame = false
    private var boardStartX = 0f
    private var boardEndX = width.toFloat()
    private var boardSidePadding = width / 20f
    private var boardStartY = 0f
    private var boardEndY = height.toFloat()
    private var boardTopPadding = height / 10f
    private var boxSize = 60f
    private var reservedUnderSpace = height / 10f
    private var dividerSize = 2
    private var boxRadius = 0.2f * boxSize
    private val playerScoreMap = hashMapOf<String, Int>()


    private fun resetDimensions(width: Int, height: Int)
    {
        boardSidePadding = width / 20f
        boardTopPadding = height / 20f
        reservedUnderSpace = height / 6f
        boardStartX = boardSidePadding
        boardEndX = width - boardSidePadding
        boardStartY = boardTopPadding

        val contentWidth = boardEndX - boardStartX

        boxSize = min(contentWidth,
                      height - 2 * boardTopPadding - reservedUnderSpace - dividerSize * game.settings.rowCount) / game.settings.rowCount
        boxRadius = 0.2f * boxSize

        boardEndY = boardStartY + (boxSize + dividerSize) * game.settings.rowCount
    }

    fun setGame(game: Game)
    {
        if (!initGame)
        {
            this.game = game
            initGame = true
            for (p in game.players)
            {
                playerScoreMap[p.name] = 0
            }
        }
    }

    override fun draw(canvas: Canvas?)
    {
        super.draw(canvas)
        if (canvas != null && initGame)
        {
            drawBoxes(canvas)
            drawPlayerScores(canvas)
        }
    }

    private fun drawBoxes(canvas: Canvas)
    {
        for (r in 0 until game.settings.rowCount)
        {
            for (c in 0 until game.settings.rowCount)
            {
                // get startX, endX = startX+boxSize. same for startY and endY
                val startX = (dividerSize + boxSize) * c + boardStartX + 1
                val startY = (dividerSize + boxSize) * r + boardStartY + 1
                val endX = startX + boxSize - 2
                val endY = startY + boxSize - 2


                // draw the box
                val rect = RectF(startX, startY, endX, endY)
                canvas.drawRoundRect(rect, boxRadius, boxRadius, boxPaint)

                // draw tokens as required

                val tokenRectStartX = startX + abs(startX - endX) * 0.45f
                val tokenRectEndX = startX + abs(startX - endX) * 0.55f
                val tokenRectStartY = startY + abs(startY - endY) * 0.125f
                val tokenRectEndY = endY - abs(startY - endY) * 0.125f

                when (game.board[r, c])
                {
                    ver ->
                    {
                        val path = drawRoundedRect(tokenRectStartX,
                                                   tokenRectStartY,
                                                   tokenRectEndX,
                                                   tokenRectEndY,
                                                   boxRadius,
                                                   boxRadius,
                                                   0f)
                        canvas.drawPath(path, verPaint)
                    }

                    hor ->
                    {
                        val path = drawRoundedRect(tokenRectStartX,
                                                   tokenRectStartY,
                                                   tokenRectEndX,
                                                   tokenRectEndY,
                                                   boxRadius,
                                                   boxRadius,
                                                   90f)
                        canvas.drawPath(path, horPaint)
                    }

                    rgt ->
                    {
                        val path = drawRoundedRect(tokenRectStartX,
                                                   tokenRectStartY,
                                                   tokenRectEndX,
                                                   tokenRectEndY,
                                                   boxRadius,
                                                   boxRadius,
                                                   45f)
                        canvas.drawPath(path, rgtPaint)
                    }

                    lft ->
                    {
                        val path = drawRoundedRect(tokenRectStartX,
                                                   tokenRectStartY,
                                                   tokenRectEndX,
                                                   tokenRectEndY,
                                                   boxRadius,
                                                   boxRadius,
                                                   -45f)
                        canvas.drawPath(path, lftPaint)
                    }
                }
            }
        }
    }

    private fun drawPlayerScores(canvas: Canvas)
    {
        val startY = boardEndY + 40

        var index = 0

        for ((name, score) in playerScoreMap)
        {
            val (x, y) = when (index)
            {
                0    -> Pair(boardStartX + 5, startY)
                1    -> Pair(boardStartX + (boardEndX - boardStartX) / 2 + 5, startY)
                2    -> Pair(boardStartX + 5, startY + reservedUnderSpace / 2)
                else -> Pair(boardStartX + (boardEndX - boardStartX) / 2 + 5,
                             startY + reservedUnderSpace / 2)
            }

            canvas.drawText("$name: $score",
                            x,
                            y,
                            when (game.players.find { it.name == name }!!.token)
                            {
                                ver  -> verPaint
                                hor  -> horPaint
                                rgt  -> rgtPaint
                                else -> lftPaint
                            })
            index++
        }
    }

    override fun onAttachedToWindow()
    {
        super.onAttachedToWindow()
        background =
                if (Build.VERSION.SDK_INT < 21)
                {
                    resources.getDrawable(R.drawable.bg)
                }
                else
                {
                    resources.getDrawable(R.drawable.bg, resources.newTheme())
                }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    {
        super.onSizeChanged(w, h, oldw, oldh)
        resetDimensions(w, h)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        if (event != null && event.action == MotionEvent.ACTION_DOWN)
        {
            val x = event.x
            val y = event.y

            val c = floor((x - boardStartX - 1) / (dividerSize + boxSize)).toInt()
            val r = floor((y - boardStartY - 1) / (dividerSize + boxSize)).toInt()

            val n = game.settings.rowCount
            if (c >= 0 && r >= 0 && r < n && c < n)
            {
                game.move(Point(r, c))
                invalidate()

                if (game.state == GameState.FinishedDraw || game.state == GameState.FinishedWin)
                {
                    fun dismiss()
                    {
                        game = game.newGame()
                        invalidate()
                    }

                    AlertDialog
                            .Builder(context)
                            .setNeutralButton("OK")
                            { _, _ ->
                                dismiss()
                            }
                            .setMessage(if (game.state == GameState.FinishedWin)
                                        {
                                            val name = game.players[game.winnerIndex].name
                                            playerScoreMap[name] = playerScoreMap[name]!! + 1
                                            "$name wins"
                                        }
                                        else
                                        {
                                            "Draw!"
                                        })
                            .setOnDismissListener {
                                dismiss()
                            }
                            .create()
                            .show()
                }
            }
        }
        return super.onTouchEvent(event)
    }


    companion object
    {
        private val boxPaint = Paint()
        private val tokenPaint = Paint()
        private val verPaint: Paint
        private val horPaint: Paint
        private val rgtPaint: Paint
        private val lftPaint: Paint

        init
        {
            boxPaint.style = Paint.Style.STROKE
            boxPaint.color = Color.CYAN
            boxPaint.isAntiAlias = true
            boxPaint.strokeWidth = 1f

            tokenPaint.style = Paint.Style.FILL
            tokenPaint.isAntiAlias = true
            tokenPaint.isFakeBoldText = true
            tokenPaint.isLinearText = true
            tokenPaint.isSubpixelText = true
            tokenPaint.textSize = 40f
            tokenPaint.typeface = Typeface.SANS_SERIF

            verPaint = Paint(tokenPaint)
            verPaint.color = Color.parseColor("#FF26A69A")

            horPaint = Paint(tokenPaint)
            horPaint.color = Color.parseColor("#FF448AFF")

            rgtPaint = Paint(tokenPaint)
            rgtPaint.color = Color.parseColor("#FFFF4081")

            lftPaint = Paint(tokenPaint)
            lftPaint.color = Color.parseColor("#FFE65100")
        }

        fun drawRoundedRect(left: Float,
                            top: Float,
                            right: Float,
                            bottom: Float,
                            rxArg: Float,
                            ryArg: Float,
                            angle: Float): Path
        {
            var rx = rxArg
            var ry = ryArg
            val path = Path()
            if (rx < 0) rx = 0f
            if (ry < 0) ry = 0f
            val width = right - left
            val height = bottom - top
            if (rx > width / 2) rx = width / 2
            if (ry > height / 2) ry = height / 2
            val widthMinusCorners = width - 2 * rx
            val heightMinusCorners = height - 2 * ry

            path.moveTo(right, top + ry)
            path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
            path.rLineTo(-widthMinusCorners, 0f)
            path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
            path.rLineTo(0f, heightMinusCorners)

            path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
            path.rLineTo(widthMinusCorners, 0f)
            path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner

            path.rLineTo(0f, -heightMinusCorners)

            path.close() //Given close, last lineTo can be removed.

            val mMatrix = Matrix()
            val bounds = RectF()
            path.computeBounds(bounds, true)
            mMatrix.postRotate(angle, bounds.centerX(), bounds.centerY())
            path.transform(mMatrix)

            return path
        }
    }
}