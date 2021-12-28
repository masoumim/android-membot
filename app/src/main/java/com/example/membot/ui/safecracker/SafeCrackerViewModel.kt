package com.example.membot.ui.safecracker

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class SafeCrackerViewModel: ViewModel() {

    // Declare Private Variables
    private var _safeCrackerList = listOf<Int>()
    private var _score = 0
    private var _strikes = 0

    // Getters
    val safeCrackerList: List<Int>
        get() = _safeCrackerList

    var score: Int = 0
        get() = _score

    var strikes: Int = 0
        get() = _strikes

    // Methods
    fun getRandomString(length:Int) : String {
        val allowedChars = ('0'..'9') //to generate random from 0 to 9
        return (0..length) // length
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun populateSafeCrackerList(length:Int) {
        val digitString = getRandomString(length)
        _safeCrackerList = digitString.map { it.toString().toInt() }
    }

    fun clearSafeCrackerList() {
        _safeCrackerList = emptyList()
    }


    fun incrementScore() {
        _score++
    }

    fun incrementStrikes(){
        _strikes++
    }

    fun resetGame(){
        _score = 0
        _strikes = 0
        _safeCrackerList = emptyList()
    }
}
