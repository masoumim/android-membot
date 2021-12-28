package com.example.membot.ui.pi

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.security.AccessControlContext
import java.security.AccessController.getContext


class PiViewModel : ViewModel() {

    // Declare private mutable variable that can only be modified
    // within the class it is declared.
    private var _score = 0
    private var _piList = listOf<Int>()
    private var _piQueryList = mutableListOf<Int>()
    private var _currentDigit: Int = 4
    private var _strikes = 0

    // Declare another public immutable field and override its getter method.
    // Return the private property's value in the getter method.
    // When count is accessed, the get() function is called and
    // the value of _count is returned.
    var score: Int = 0
        get() = _score

    var strikes: Int = 0
        get() = _strikes

    val piList: List<Int>
        get() = _piList

    val piQueryList: List<Int>
        get() = _piQueryList


    // Methods
    fun populatePiList() {
        val digitString =
            "1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679"
        _piList = digitString.map { it.toString().toInt() }
    }

    fun setPiQueryList() {
        var index = 0
        while (index < _currentDigit) {
            _piQueryList.add(_piList[index])
            index++
        }
    }

    fun incrementScore() {
        _score++
    }

    fun incrementStrikes(){
        _strikes++
    }

    fun addDigitToPiQueryList() {
        _piQueryList.add(_piList[_currentDigit])
        _currentDigit++
    }

    fun resetGame(){
        _score = 0
        _strikes = 0
        _currentDigit = 4
        _piQueryList.clear()
        setPiQueryList()
    }
}
