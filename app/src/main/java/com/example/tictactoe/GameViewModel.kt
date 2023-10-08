package com.example.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var state by mutableStateOf(GameState())

    val boardItems: MutableMap<Int, BoardCellValue> = mutableMapOf(
        1 to BoardCellValue.NONE,
        2 to BoardCellValue.NONE,
        3 to BoardCellValue.NONE,
        4 to BoardCellValue.NONE,
        5 to BoardCellValue.NONE,
        6 to BoardCellValue.NONE,
        7 to BoardCellValue.NONE,
        8 to BoardCellValue.NONE,
        9 to BoardCellValue.NONE,
    )

    fun onAction(action: UserAction) {
        when (action) {
            is UserAction.BoardTapped -> {
                addValueToBoard(action.cellNo)
                if (state.currentTurn == BoardCellValue.CROSS) {
                    computerMove()
                }
            }

            UserAction.PlayAgainButtonClicked -> {
                gameReset()
            }
        }
    }

    private fun gameReset() {
        boardItems.forEach { (i, _) ->
            boardItems[i] = BoardCellValue.NONE
        }
        state = state.copy(
            hintText = if ((state.playerCircleCount + state.playerCrossCount + state.drawCount) % 2 == 0) "Player '0' turn" else "Player 'X' turn",
            currentTurn = if ((state.playerCircleCount + state.playerCrossCount + state.drawCount) % 2 == 0) BoardCellValue.CIRCLE else BoardCellValue.CROSS,
            victoryType = VictoryType.NONE,
            hasWon = false
        )

        if (state.currentTurn == BoardCellValue.CROSS) {
            computerMove()
        }
    }

    private fun addValueToBoard(cellNo: Int) {
        if (boardItems[cellNo] != BoardCellValue.NONE) {
            return
        }
        if (state.currentTurn == BoardCellValue.CIRCLE) {
            boardItems[cellNo] = BoardCellValue.CIRCLE
            state = if (checkForVictory(BoardCellValue.CIRCLE)) {
                state.copy(
                    hintText = "Player 'O' Won",
                    playerCircleCount = state.playerCircleCount + 1,
                    currentTurn = BoardCellValue.NONE,
                    hasWon = true
                )
            } else if (hasBoardFull()) {
                state.copy(
                    hintText = "Game Draw", drawCount = state.drawCount + 1
                )
            } else {
                state.copy(
                    hintText = "Player 'X' turn", currentTurn = BoardCellValue.CROSS
                )
            }
        } else if (state.currentTurn == BoardCellValue.CROSS) {
            boardItems[cellNo] = BoardCellValue.CROSS
            state = if (checkForVictory(BoardCellValue.CROSS)) {
                state.copy(
                    hintText = "Player 'X' Won",
                    playerCrossCount = state.playerCrossCount + 1,
                    currentTurn = BoardCellValue.NONE,
                    hasWon = true
                )
            } else if (hasBoardFull()) {
                state.copy(
                    hintText = "Game Draw", drawCount = state.drawCount + 1
                )
            } else {
                state.copy(
                    hintText = "Player 'O' turn", currentTurn = BoardCellValue.CIRCLE
                )
            }
        }
    }

    private fun checkForVictory(boardValue: BoardCellValue): Boolean {
        when {
            boardItems[1] == boardValue && boardItems[2] == boardValue && boardItems[3] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL1)
                return true
            }

            boardItems[4] == boardValue && boardItems[5] == boardValue && boardItems[6] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL2)
                return true
            }

            boardItems[7] == boardValue && boardItems[8] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL3)
                return true
            }

            boardItems[1] == boardValue && boardItems[4] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL1)
                return true
            }

            boardItems[2] == boardValue && boardItems[5] == boardValue && boardItems[8] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL2)
                return true
            }

            boardItems[3] == boardValue && boardItems[6] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL3)
                return true
            }

            boardItems[1] == boardValue && boardItems[5] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL1)
                return true
            }

            boardItems[3] == boardValue && boardItems[5] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL2)
                return true
            }

            else -> return false
        }
    }

    private fun hasBoardFull(): Boolean {
        return !boardItems.containsValue(BoardCellValue.NONE)
    }

    private fun computerMove() {
        if (boardItems.isEmpty()) {
            addValueToBoard(5)
        }
        val possibleWin = checkPossibleWin(BoardCellValue.CROSS)
        val possibleBlock = checkPossibleWin(BoardCellValue.CIRCLE)

        if (possibleWin.isEmpty() && possibleBlock.isEmpty()) {
            if (boardItems[5] == BoardCellValue.NONE) {
                addValueToBoard(5)
            } else {
                val blank = emptyList<Int>().toMutableList()
                boardItems.forEach { (boardNum, boardValue) ->
                    if (boardValue == BoardCellValue.NONE) {
                        blank.add(boardNum)
                    }
                }
                addValueToBoard(blank.random())
            }
            return
        }

        if (possibleWin.isNotEmpty()) {
            possibleWin.forEach { boardNums ->
                boardNums.forEach { boardNum ->
                    if (boardItems[boardNum] == BoardCellValue.NONE) {
                        addValueToBoard(boardNum)
                        return
                    }
                }
            }
        }

        if (possibleBlock.isNotEmpty()) {
            possibleBlock.forEach { boardNums ->
                boardNums.forEach { boardNum ->
                    if (boardItems[boardNum] == BoardCellValue.NONE) {
                        addValueToBoard(boardNum)
                        return
                    }
                }
            }
        }
    }

    private fun checkPossibleWin(player: BoardCellValue): MutableList<List<Int>> {
        val possibleVictory = mutableListOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            listOf(3, 6, 9),
            listOf(1, 5, 9),
            listOf(3, 5, 7)
        )
        val valid = mutableListOf<List<Int>>()
        possibleVictory.forEach { boardNums ->
            var count = 0
            for (boardNum in boardNums) {
                if (boardItems[boardNum] == BoardCellValue.NONE) {
                    continue
                }
                if (player == boardItems[boardNum]) {
                    count++
                }
            }
            if (count == 2) {
                if (boardNums.any { boardItems[it]!! == BoardCellValue.NONE }) valid.add(boardNums)
            }
        }
        return valid
    }


}