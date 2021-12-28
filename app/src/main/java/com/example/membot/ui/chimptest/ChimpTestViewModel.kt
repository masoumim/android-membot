package com.example.membot.ui.chimptest

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class ChimpTestViewModel: ViewModel() {

    // Declare Private Variables
    // The list of digits that will be placed in the buttons. Game always starts with 3 digits / buttons.
    private var _digitList = mutableListOf<Int>()
    private var _score = 0
    private var _strikes = 0
    // The number of digit buttons to display. Game always starts with 3 buttons.
    private var _numOfButtons = 3
    private var _highestScore = 0

    // Getters
    var digitList: List<Int> = mutableListOf()
        get() = _digitList

    var highestscore: Int = 0
        get() = _highestScore

    var strikes: Int = 0
        get() = _strikes

    var score: Int = 0
        get() = _score

    var numOfButtons = 3
        get() = _numOfButtons

    // Methods
    fun digitListSet(list: MutableList<Int>){
        _digitList = list
    }

    fun sortDigitList(){
        _digitList.sort()
    }

    fun addDigitToList(){
        _digitList.add(Random.nextInt(1,9))
    }

    fun clearChimpList() {
        _digitList.clear()
    }

    fun setHighestScore(highestScore:Int) {
        _highestScore = highestScore
    }

    fun incrementStrikes(){
        _strikes++
    }

    fun incrementScore(){
        _score++
    }

    fun incrementNumOfButtons(){
        _numOfButtons++
    }

    // Resets the game if the user gets the input order wrong.
    fun resetGame(){
        _numOfButtons = 3
        _score = 0
        _digitList.clear()
    }

    // Restarts the game if user gets 3 strikes
    fun restartGame(){
        _score = 0
        _strikes = 0
        _numOfButtons = 3
        _digitList.clear()
    }

}