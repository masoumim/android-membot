package com.example.membot

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.membot.databinding.FragmentChimpBinding
import com.example.membot.databinding.FragmentSafeCrackerBinding
import com.example.membot.ui.chimptest.ChimpTestViewModel
import com.example.membot.ui.safecracker.SafeCrackerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import kotlin.random.Random


class ChimpFragment : Fragment() {

    // ViewModel object
    private val viewModel: ChimpTestViewModel by viewModels()

    // View Binding object
    private var _binding: FragmentChimpBinding? = null
    private val binding get() = _binding!!

    // Nav Controller Object
    private lateinit var navController: NavController

    // List of buttons
    private var buttonList = mutableListOf<Button>()

    // List of buttons that are currently being displayed
    private var displayedButtonsList = mutableListOf<Button>()

    // The index position of the number in the digit list we are currently checking
    var checkDigitListAtIndex = 0

    // The accumulated number of correct answers the user got before game over.
    // (Used for saving accuracy data)
    var totalCorrectAns = 0

    // The save file for accuracy data
    val fileName = "chimpAccuracy.txt"

    // The save file for high score data
    val highScoreFile = "chimpHighScore.txt"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChimpBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Game Logic Goes Here

        // Hide the RETRY button
        binding.retryChimpButton.isVisible = false

        // Hide the QUIT button
        binding.quitChimpButton.isVisible = false

        // Set the list of buttons
        setButtonList()

        // Start by hiding all the buttons
        hideAllButtons()

        // Display Buttons
        displayButtons()

        // Set the digit text for each button
        setTheButtonsText()

        //Number listeners
        binding.buttonChimp1.setOnClickListener { checkUserInput(binding.buttonChimp1.text.toString()) }
        binding.buttonChimp2.setOnClickListener { checkUserInput(binding.buttonChimp2.text.toString()) }
        binding.buttonChimp3.setOnClickListener { checkUserInput(binding.buttonChimp3.text.toString()) }
        binding.buttonChimp4.setOnClickListener { checkUserInput(binding.buttonChimp4.text.toString()) }
        binding.buttonChimp5.setOnClickListener { checkUserInput(binding.buttonChimp5.text.toString()) }
        binding.buttonChimp6.setOnClickListener { checkUserInput(binding.buttonChimp6.text.toString()) }
        binding.buttonChimp7.setOnClickListener { checkUserInput(binding.buttonChimp7.text.toString()) }
        binding.buttonChimp8.setOnClickListener { checkUserInput(binding.buttonChimp8.text.toString()) }
        binding.buttonChimp9.setOnClickListener { checkUserInput(binding.buttonChimp9.text.toString()) }
        binding.buttonChimp10.setOnClickListener { checkUserInput(binding.buttonChimp10.text.toString()) }
        binding.buttonChimp11.setOnClickListener { checkUserInput(binding.buttonChimp11.text.toString()) }
        binding.buttonChimp12.setOnClickListener { checkUserInput(binding.buttonChimp12.text.toString()) }
        binding.buttonChimp13.setOnClickListener { checkUserInput(binding.buttonChimp13.text.toString()) }
        binding.buttonChimp14.setOnClickListener { checkUserInput(binding.buttonChimp14.text.toString()) }
        binding.buttonChimp15.setOnClickListener { checkUserInput(binding.buttonChimp15.text.toString()) }
        binding.buttonChimp16.setOnClickListener { checkUserInput(binding.buttonChimp16.text.toString()) }
        binding.buttonChimp17.setOnClickListener { checkUserInput(binding.buttonChimp17.text.toString()) }
        binding.buttonChimp18.setOnClickListener { checkUserInput(binding.buttonChimp18.text.toString()) }
        binding.buttonChimp19.setOnClickListener { checkUserInput(binding.buttonChimp19.text.toString()) }
        binding.buttonChimp20.setOnClickListener { checkUserInput(binding.buttonChimp20.text.toString()) }


        // Handle RETRY button
        binding.retryChimpButton.setOnClickListener {
            restart()
            binding.retryChimpButton.isVisible = false
            binding.quitChimpButton.isVisible = false
            displayScore()
            displayStrikes()
        }

        // Handle the QUIT button
        binding.quitChimpButton.setOnClickListener {
            navController = Navigation.findNavController(view)
            navController!!.navigate(R.id.action_chimpFragment_to_mainFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // Methods

    // Create a list of buttons
    private fun setButtonList(){
        buttonList.add(binding.buttonChimp1)
        buttonList.add(binding.buttonChimp2)
        buttonList.add(binding.buttonChimp3)
        buttonList.add(binding.buttonChimp4)
        buttonList.add(binding.buttonChimp5)
        buttonList.add(binding.buttonChimp6)
        buttonList.add(binding.buttonChimp7)
        buttonList.add(binding.buttonChimp8)
        buttonList.add(binding.buttonChimp9)
        buttonList.add(binding.buttonChimp10)
        buttonList.add(binding.buttonChimp11)
        buttonList.add(binding.buttonChimp12)
        buttonList.add(binding.buttonChimp13)
        buttonList.add(binding.buttonChimp14)
        buttonList.add(binding.buttonChimp15)
        buttonList.add(binding.buttonChimp16)
        buttonList.add(binding.buttonChimp17)
        buttonList.add(binding.buttonChimp18)
        buttonList.add(binding.buttonChimp19)
        buttonList.add(binding.buttonChimp20)
    }

    private fun hideAllButtons() {
        for(button in buttonList){
            button.isVisible = false
        }
    }

    // Buttons are displayed randomly
    private fun displayButtons() {
        var randomNumberList = generateRandNumList()
        // Randomly display / make visible the buttons.
        for (i in 1..20) {
            for (num in randomNumberList) {
                if (i == num) {
                    buttonList[i-1].isVisible = true
                    displayedButtonsList.add(buttonList[i-1])
                }
            }
        }
    }

    private fun setTheButtonsText() {
        var randomNumberList = generateRandNumList()
        viewModel.digitListSet(randomNumberList)
        // Set the text of the buttons
        var i = 0
        for(button in displayedButtonsList){
            button.text = randomNumberList[i].toString()
            i++
        }
        // Sort the digit list to make user input comparison easier
        viewModel.sortDigitList()
    }

    // Crete a unique / distinct list of random numbers of size numOfButtons
    private fun generateRandNumList(): MutableList<Int>{
        val randomNumberList = mutableListOf<Int>()
        while (randomNumberList.size < viewModel.numOfButtons) {
            val randNum = (1..20).random()
            if (!randomNumberList.contains(randNum)) {
                randomNumberList.add(randNum)
            }
        }
        return randomNumberList
    }

    private fun checkUserInput(input: String){
        if(input == viewModel.digitList[checkDigitListAtIndex].toString()){
            // Check if this is the first button with the lowest digit. If so, hide text on the buttons.
            if(checkDigitListAtIndex == 0){
                hideButtonText()
            }
            checkDigitListAtIndex++
        }
        else{
            // Wrong!
            // Save user high score data
            saveUserHighScore()
            totalCorrectAns += viewModel.score
            reset()
            viewModel.incrementStrikes()
            displayStrikes()
            displayScore()
        }

        // Check if user pressed all the buttons in the correct order:
        if (checkDigitListAtIndex == viewModel.digitList.size) {
            viewModel.incrementScore()
            displayScore()
            displayNewButtons()
        }
        // Check for 3 strikes, if so end game.
        if(viewModel.strikes == 3){
            gameOver()
        }
    }

    // Reads the high score from the save file
    fun readHighScore(): String{
        var fileInputStream: FileInputStream? = null

        try {
            fileInputStream = requireContext().openFileInput(highScoreFile)
            var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            val text: List<String> = bufferedReader.readLines()

            // Read the text stored in the file.
            for(line in text){
                return line
            }
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }

        // Default return value.
        return ""
    }

    // Writes the high score to the save file
    fun writeHighScore(scoreString: String){
        // The format for high score is simply an int (string) stored in a text file.
        val fileBody = scoreString
        val fileOutputStream: FileOutputStream

        try {
            fileOutputStream = requireContext().openFileOutput(highScoreFile, Context.MODE_PRIVATE)
            fileOutputStream.write(fileBody.toByteArray())
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        }catch (e: NumberFormatException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    // Save high score file
    fun saveUserHighScore(){
        var savedHighScoreString = ""

        // Get the saved high score value from the save file.
        var file = File(context?.filesDir,highScoreFile)

        if(file.exists()){
            savedHighScoreString = readHighScore()
        }

        // If file exists already, update the saved high score if it is less
        // than the high score this round.
        if(savedHighScoreString != ""){
            if(savedHighScoreString.toInt() < viewModel.score){
                writeHighScore(viewModel.score.toString())
            }
        }
        else{
            // if the file doesn't exist, just write the score this round to file.
            writeHighScore(viewModel.score.toString())
        }
    }

    // Resets the game if the user gets the input order wrong.
    private fun reset(){
        viewModel.resetGame()
        displayNewButtons()
    }

    private fun restart(){
        viewModel.restartGame()
        displayNewButtons()
    }

    private fun gameOver(){
        // Save user accuracy data
        saveUserAccuracyData()
        // Reset the total correct answers
        totalCorrectAns = 0
        // Hide all the buttons
        hideAllButtons()
        // Display "Retry" and "Quit" buttons
        binding.retryChimpButton.isVisible = true
        binding.quitChimpButton.isVisible = true
    }

    // This method will save and update the user accuracy save file.
    // The format of the saved data is: "correct answers,total questions". Example: "2,5"
    fun saveUserAccuracyData(){
        var numCorrectAns = ""
        var numQuestions = ""
        var updatedNumCorrectAns = 0
        var updatedNumQuestions = 0
        var savedScoreString: String

        // 1. Get the saved score values from the save file.
        var file = File(context?.filesDir,fileName)

        if(file.exists()){
            savedScoreString = readFromFile()
        }else{
            savedScoreString = ""
        }

        if(savedScoreString != ""){
            // Extract the NUMBER OF CORRECT ANSWERS from the string:
            numCorrectAns = savedScoreString.substring(0,savedScoreString.indexOf(","))
            // Extract the NUMBER OF QUESTIONS ASKED from the string:
            numQuestions = savedScoreString.substring(savedScoreString.indexOf(",")+1,savedScoreString.length)

            // 2. Update this saved score value with the scores from this play session / round.
            updatedNumCorrectAns = numCorrectAns.toInt() + totalCorrectAns
            updatedNumQuestions = numQuestions.toInt() + totalCorrectAns + viewModel.strikes
        }
        else{
            // 2. Update this saved score value with the scores from this play session / round.
            updatedNumCorrectAns = totalCorrectAns
            updatedNumQuestions = totalCorrectAns + viewModel.strikes
        }

        // 3. Save the updated score values to the save file.
        val updatedScoreString = updatedNumCorrectAns.toString() + "," + updatedNumQuestions.toString()
        writeToFile(updatedScoreString)
    }

    // This will write ACCURACY data to Internal file
    fun writeToFile(scoreString: String){
        // The format of the saved data is: "correct answers,total questions". Example: "2,5"
        val fileBody = scoreString
        val fileOutputStream: FileOutputStream

        try {
            fileOutputStream = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(fileBody.toByteArray())
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        }catch (e: NumberFormatException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    // This will read the saved ACCURACY data from Internal file...
    // and return a string representing the saved data
    fun readFromFile(): String {
        var fileInputStream: FileInputStream? = null

        try {
            fileInputStream = requireContext().openFileInput(fileName)
            var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            val text: List<String> = bufferedReader.readLines()

            // Read the text stored in the file.
            for(line in text){
                return line
            }
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }

        // Default return value.
        return ""
    }

    private fun hideButtonText(){
        for(button in displayedButtonsList){
            button.textSize = 0F
        }
    }

    private fun unhideButtonText(){
        for(button in displayedButtonsList){
            button.textSize = 20F
        }
    }

    // Displays a new set of buttons
    private fun displayNewButtons(){
        // Reset the index of digit list so we check from the beginning
        checkDigitListAtIndex = 0
        // Clear the list of currently displayed buttons
        displayedButtonsList.clear()
        // Increment the number of buttons every 3 successful rounds
        if(viewModel.score % 3 == 0 && viewModel.score != 0 && viewModel.numOfButtons < 20){
            viewModel.incrementNumOfButtons()
        }
        hideAllButtons()
        displayButtons()
        setTheButtonsText()
        unhideButtonText()
    }

    private fun displayScore() {
        binding.userScore.text = viewModel.score.toString()
    }

    private fun displayStrikes() {
        binding.userStrikes.text = viewModel.strikes.toString()
    }
}