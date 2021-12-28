package com.example.membot.models

import android.util.Log
import com.example.membot.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize){


    val cards: List<MemoryCard>
    var numPairsFound = 0

    private var numCardFlips = 0
    private var indexOfSingleSelectedCard: Int? = null
    private var _strikes = 0

    // Getter for strikes variable
    var strikes: Int = 0
        get() = _strikes

    init {
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map { MemoryCard(it) }
    }

    //Game Logic
    fun flipCard(position: Int): Boolean {
        numCardFlips++
    val card = cards[position]
        //Three cases
        //1. zero cards flipped over => flip over the selected card
        //2. exactly one card flipped over => flip over the selected card + check if the images match
        //3. exactly two cards flipped over => restore cards + flip over the selected card
        var foundMatch = false
        if (indexOfSingleSelectedCard == null){
            //0 or 2 cards flipped over
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            //exactly 1 card flipped over
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null

        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier != cards[position2].identifier){
            // Increment strike
              _strikes++
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true

    }

    private fun restoreCards() {
        for (card in cards){
            if (!card.isMatched){
                card.isFaceUp = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardFlips / 2
    }
}