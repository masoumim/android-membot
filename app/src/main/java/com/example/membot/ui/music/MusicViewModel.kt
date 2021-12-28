package com.example.membot.ui.music

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class MusicViewModel : ViewModel() {

    // Private members
    private var _score = 0
    private var _strikes = 0
    private var _buttonSequence = MutableList(1){ Random.nextInt(1,4)}
    private var _gameOver = false
    private var _userInput = 0

    // Public getters
    var score: Int = 0
        get() = _score

    var strikes: Int = 0
        get() = _strikes

    val buttonSequence: MutableList<Int>
        get() = _buttonSequence

    val gameOver: Boolean
        get() = _gameOver

    var userInput: Int = 0
        get() = _userInput

    // Methods
    fun incrementScore() {
        _score++
    }

    fun incrementStrikes() {
        _strikes++
    }

    fun addButtonToSequence(){
        _buttonSequence.add(Random.nextInt(1,4))
    }

    fun setInput(input: Int){
        _userInput = input
    }

    fun clearSequence(){
        _buttonSequence.clear()
    }

    fun resetGame(){
        _score = 0
        _strikes = 0
        _userInput = 0
        _buttonSequence.clear()
        _buttonSequence = MutableList(1){ Random.nextInt(1,4)}
    }
}