package com.staa.games.orien.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.widget.FrameLayout
import com.staa.games.orien.R
import com.staa.games.orien.engine.game.*
import com.staa.games.orien.engine.game.Point
import com.staa.games.orien.engine.game.players.AI
import com.staa.games.orien.util.GameSoundState
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min

class GameView(context: Context) : FrameLayout(context), IGameDisplay
{
    private var canUndo = true
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
    private var notifying = false
    private var boardTopBarSize = 50f
    private var resetGameBtnStartX = 50f
    private var resetGameBtnEndX = 150f
    private var buttonBarStartY = 10f
    private var buttonBarEndY = 110f
    private var undoLastMoveBtnStartX = 170f
    private var undoLastMoveBtnEndX = 270f
    private var musicStateBtnStartX = 290f
    private var musicStateBtnEndX = 390f

    private fun resetDimensions(width: Int, height: Int)
    {
        boardSidePadding = width / 20f
        boardTopPadding = height / 20f
        reservedUnderSpace = height / 6f
        boardStartX = boardSidePadding
        boardEndX = width - boardSidePadding
        boardStartY = boardTopPadding + boardTopBarSize

        resetGameBtnStartX = boardStartX
        resetGameBtnEndX = resetGameBtnStartX + 100

        undoLastMoveBtnStartX = resetGameBtnEndX + 20
        undoLastMoveBtnEndX = undoLastMoveBtnStartX + 100

        musicStateBtnEndX = boardEndX
        musicStateBtnStartX = musicStateBtnEndX - 100

        val contentWidth = boardEndX - boardStartX

        boxSize = min(contentWidth,
                      height - 2 * boardTopPadding - reservedUnderSpace - dividerSize * game.settings.rowCount) / game.settings.rowCount
        boxRadius = 0.2f * boxSize

        boardEndY = boardStartY + (boxSize + dividerSize) * game.settings.rowCount
    }

    fun setGame(game: Game, canUndo: Boolean)
    {
        if (!initGame)
        {
            this.game = game
            this.canUndo = canUndo
            initGame = true
            for (p in game.players)
            {
                playerScoreMap[p.name] = 0
            }

            game.notifyCurrentPlayerAsync()
        }
    }

    override fun draw(canvas: Canvas?)
    {
        super.draw(canvas)
        if (canvas != null && initGame)
        {
            drawGameButtons(canvas)
            drawBoxes(canvas)
            drawPlayerScores(canvas)
        }

        if (notifying)
        {
            notifying = false
            game.notifyCurrentPlayerAsync()
        }
    }

    private fun drawGameButtons(canvas: Canvas)
    {
        val resetBtnIcon = ContextCompat.getDrawable(context, R.drawable.refresh)!!
        resetBtnIcon.setBounds(resetGameBtnStartX.toInt(),
                               buttonBarStartY.toInt(),
                               resetGameBtnEndX.toInt(),
                               buttonBarEndY.toInt())
        resetBtnIcon.draw(canvas)

        if (GameSoundState.isPlaying)
        {
            val musicPlayingBtnIcon = ContextCompat.getDrawable(context, R.drawable.ic_volume_up)!!
            musicPlayingBtnIcon.setBounds(musicStateBtnStartX.toInt(),
                                          buttonBarStartY.toInt(),
                                          musicStateBtnEndX.toInt(),
                                          buttonBarEndY.toInt())
            musicPlayingBtnIcon.draw(canvas)
        }
        else
        {
            val musicPlayingBtnIcon = ContextCompat.getDrawable(context, R.drawable.ic_volume_off)!!
            musicPlayingBtnIcon.setBounds(musicStateBtnStartX.toInt(),
                                          buttonBarStartY.toInt(),
                                          musicStateBtnEndX.toInt(),
                                          buttonBarEndY.toInt())
            musicPlayingBtnIcon.draw(canvas)
        }

        if (canUndo)
        {
            val undoBtnIcon = ContextCompat.getDrawable(context, R.drawable.ic_undo)!!
            resetBtnIcon.setBounds(undoLastMoveBtnStartX.toInt(),
                                   buttonBarStartY.toInt(),
                                   undoLastMoveBtnEndX.toInt(),
                                   buttonBarEndY.toInt())
            undoBtnIcon.draw(canvas)
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

                var angle = 0f
                var paint = verPaint
                var shouldDraw = true

                when (game.board[r, c])
                {
                    ver  ->
                    {
                        angle = 0f
                        paint = verPaint
                    }

                    hor  ->
                    {
                        angle = 90f
                        paint = horPaint
                    }

                    rgt  ->
                    {
                        angle = 45f
                        paint = rgtPaint
                    }

                    lft  ->
                    {
                        angle = -45f
                        paint = lftPaint
                    }

                    else -> shouldDraw = false
                }

                if (shouldDraw)
                {
                    val path = drawRoundedRect(tokenRectStartX,
                                               tokenRectStartY,
                                               tokenRectEndX,
                                               tokenRectEndY,
                                               boxRadius,
                                               boxRadius,
                                               angle)
                    canvas.drawPath(path, paint)
                }
            }
        }
    }

    override fun refresh()
    {
        invalidate()

        if (game.state == GameState.Running && game.getCurrentPlayer() is AI)
        {
            notifying = true
        }

        if (game.state == GameState.FinishedDraw || game.state == GameState.FinishedWin)
        {
            notifying = false
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
                            }.apply { isUnderlineText = game.getCurrentPlayer().name == name })
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

    private fun resetGame()
    {
        game = game.newGame()
        invalidate()
    }

    private fun undoLastMove()
    {
        game.undoLastMove()
        invalidate()
    }

    private fun toggleMusic()
    {
        if (GameSoundState.isPlaying)
        {
            GameSoundState.player.pause()
            GameSoundState.isPlaying = false
        }
        else
        {
            GameSoundState.player.start()
            GameSoundState.isPlaying = true
        }

        invalidate()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        if ((PlayNotifier.job == null || (PlayNotifier.job != null && PlayNotifier.job!!.isCompleted)) && event != null && event.action == MotionEvent.ACTION_DOWN)
        {
            val x = event.x
            val y = event.y

            if (y !in buttonBarStartY..buttonBarEndY)
            {

                val c = floor((x - boardStartX - 1) / (dividerSize + boxSize)).toInt()
                val r = floor((y - boardStartY - 1) / (dividerSize + boxSize)).toInt()

                val n = game.settings.rowCount
                if (c >= 0 && r >= 0 && r < n && c < n)
                {
                    game.move(Point(r, c))
                    refresh()
                }
            }
            else
            {
                if (x in resetGameBtnStartX..resetGameBtnEndX)
                {
                    resetGame()
                }
                else if (canUndo && x in undoLastMoveBtnStartX..undoLastMoveBtnEndX)
                {
                    undoLastMove()
                }
                else if (x in musicStateBtnStartX..musicStateBtnEndX)
                {
                    toggleMusic()
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
            verPaint.color = Color.parseColor("#00e5ff")

            horPaint = Paint(tokenPaint)
            horPaint.color = Color.parseColor("#64dd17")

            rgtPaint = Paint(tokenPaint)
            rgtPaint.color = Color.parseColor("#e65100")

            lftPaint = Paint(tokenPaint)
            lftPaint.color = Color.parseColor("#f50057")
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