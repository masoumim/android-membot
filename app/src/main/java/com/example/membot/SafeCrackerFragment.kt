package com.example.membot

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
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
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.membot.databinding.FragmentSafeCrackerBinding
import com.example.membot.ui.safecracker.SafeCrackerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import kotlin.math.absoluteValue

class SafeCrackerFragment : Fragment() {

    // Level Indicator
    private var levelNum = 1


    // SafeCracker ViewModel object
    private val viewModel: SafeCrackerViewModel by viewModels()

    // View Binding object
    private var _binding: FragmentSafeCrackerBinding? = null
    private val binding get() = _binding!!

    // Nav Controller Object
    private lateinit var navController: NavController

    // The save file for accuracy data
    val fileName = "safeCrackerAccuracy.txt"

    // The save file for accuracy data
    val highScoreFile = "safeCrackerHighScore.txt"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSafeCrackerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Game Logic Goes Here

        // Hide the RETRY button
        binding.retryButton.isVisible = false

        // Hide the QUIT button
        binding.quitButton.isVisible = false

        // Populate the list with 4 random digits (Level 1).
        setLevelNumber(levelNum)

        //Number listeners
        binding.button0.setOnClickListener { appendOnClick("0") }
        binding.button1.setOnClickListener { appendOnClick("1") }
        binding.button2.setOnClickListener { appendOnClick("2") }
        binding.button3.setOnClickListener { appendOnClick("3") }
        binding.button4.setOnClickListener { appendOnClick("4") }
        binding.button5.setOnClickListener { appendOnClick("5") }
        binding.button6.setOnClickListener { appendOnClick("6") }
        binding.button7.setOnClickListener { appendOnClick("7") }
        binding.button8.setOnClickListener { appendOnClick("8") }
        binding.button9.setOnClickListener { appendOnClick("9") }

        // Handle SUBMIT button
        binding.buttonSubmit.setOnClickListener {

            // Hide the keyboard
            hideSoftKeyboard(activity)

            // Check if submission is correct
            val userInputString = binding.inputTextView.text.toString() // get the user input as a string
            var userInputList = listOf<Int>()// variable to store the user input as a list of INT
            userInputList = userInputString.map { it.toString().toInt() } // convert user input string to a LIST of INT

            // Compare user input to the safe cracker query list
            if (userInputList == viewModel.safeCrackerList) {
                // Correct!
                Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
                // Update score
                viewModel.incrementScore()
                // Display updated score
                displayScore()

                setLevelNumber(++levelNum)

            } else {

                // Incorrect!
                Toast.makeText(context, "WRONG!", Toast.LENGTH_SHORT).show()

                // Update Strike count
                if (viewModel.strikes < 3) {
                    viewModel.incrementStrikes()
                    setLevelNumber(levelNum)
                }

                // Check for 3 strikes
                if (viewModel.strikes == 3) {

                    // Save the user accuracy score
                    saveUserAccuracyData()

                    // Save the user high score
                    saveUserHighScore()

                    // STOP THE GAME AND RESET THE LEVEL
                    viewModel.clearSafeCrackerList()

                    // Game Over
                    Toast.makeText(context, "GAME OVER!", Toast.LENGTH_SHORT).show()

                    // Hide the SUBMIT button
                    binding.buttonSubmit.isVisible = false

                    // Hide the CODE sequence
                    binding.numSequence.isVisible = false

                    // Show the RETRY button
                    binding.retryButton.isVisible = true

                    // Show the QUIT button
                    binding.quitButton.isVisible = true

                }

                displayStrikes()
            }

        }

        // Handle RETRY button
        binding.retryButton.setOnClickListener {
            viewModel.resetGame()
            setLevelNumber(1)
            displayScore()
            displayStrikes()
            binding.retryButton.isVisible = false
            binding.quitButton.isVisible = false
            binding.buttonSubmit.isVisible = true
        }

        // Handle the QUIT button
        binding.quitButton.setOnClickListener {
            navController = Navigation.findNavController(view)
            navController!!.navigate(R.id.action_safeCrackerFragment_to_mainFragment)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // METHODS / FUNCTIONS

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
            updatedNumCorrectAns = numCorrectAns.toInt() + viewModel.score
            updatedNumQuestions = numQuestions.toInt() + viewModel.score + viewModel.strikes
        }
        else{
            // 2. Update this saved score value with the scores from this play session / round.
            updatedNumCorrectAns = viewModel.score
            updatedNumQuestions = viewModel.score + viewModel.strikes
        }

        // 3. Save the updated score values to the save file.
        val updatedScoreString = updatedNumCorrectAns.toString() + "," + updatedNumQuestions.toString()
        writeToFile(updatedScoreString)
    }

    private fun appendOnClick(string: String) {
        binding.inputTextView.append(string)
    }

    // Set the level number
    private fun setLevelNumber(levelNum: Int){

        // Clear the buttons input
        binding.inputTextView.setText("")

        when (levelNum) {
            1 -> {
                viewModel.populateSafeCrackerList(3)
                playLevelSequence()
            }
            2 -> {
                viewModel.populateSafeCrackerList(4)
                playLevelSequence()
            }
            3 -> {
                viewModel.populateSafeCrackerList(5)
                playLevelSequence()
            }
            4 -> {
                viewModel.populateSafeCrackerList(6)
                playLevelSequence()
            }
            5 -> {
                viewModel.populateSafeCrackerList(7)
                playLevelSequence()
            }
            6 -> {
                viewModel.populateSafeCrackerList(8)
                playLevelSequence()
            }
            7 -> {
                viewModel.populateSafeCrackerList(9)
                playLevelSequence()
            }
            8 -> {
                viewModel.populateSafeCrackerList(10)
                playLevelSequence()
            }
            9 -> {
                viewModel.populateSafeCrackerList(11)
                playLevelSequence()
            }
            10 -> {
                viewModel.populateSafeCrackerList(12)
                playLevelSequence()
            }
        }

    }

    // METHODS / FUNCTIONS
    fun playLevelSequence() {
        GlobalScope.launch(context = Dispatchers.Main) {

            if(viewModel.safeCrackerList.isNotEmpty()) {

                disableTheButtons()

                for (number in viewModel.safeCrackerList) {
                    when (number) {
                        // set them to yellow
                        0 -> binding.button0.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                        1 -> binding.button1.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                        2 -> binding.button2.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                        3 -> binding.button3.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                        4 -> binding.button4.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                        5 -> binding.button5.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                        6 -> binding.button6.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                        7 -> binding.button7.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                        8 -> binding.button8.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                        9 -> binding.button9.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                        else -> Log.d("NO BUTTON", "")
                    }
                    // 1 Second delay before turning the button "off"
                    delay(1000)
                    //set them to blue
                    binding.button0.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    binding.button1.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    binding.button2.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    binding.button3.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    binding.button4.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    binding.button5.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    binding.button6.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    binding.button7.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    binding.button8.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    binding.button9.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    // 1 Second delay before turning back into the WHEN block
                    delay(1000)


                }

            }

            enableTheButtons()

        }
    }

    private fun disableTheButtons() {
        binding.button0.isEnabled = false
        binding.button1.isEnabled = false
        binding.button2.isEnabled = false
        binding.button3.isEnabled = false
        binding.button4.isEnabled = false
        binding.button5.isEnabled = false
        binding.button6.isEnabled = false
        binding.button7.isEnabled = false
        binding.button8.isEnabled = false
        binding.button9.isEnabled = false
    }

    private fun enableTheButtons() {
        binding.button0.isEnabled = true
        binding.button1.isEnabled = true
        binding.button2.isEnabled = true
        binding.button3.isEnabled = true
        binding.button4.isEnabled = true
        binding.button5.isEnabled = true
        binding.button6.isEnabled = true
        binding.button7.isEnabled = true
        binding.button8.isEnabled = true
        binding.button9.isEnabled = true
    }

    private fun displayScore() {
        binding.userScore.text = viewModel.score.toString()
    }

    private fun displayStrikes() {
        binding.userStrikes.text = viewModel.strikes.toString()
    }

    // Hide keyboard function
    fun hideSoftKeyboard(activity: FragmentActivity?) {
        if (activity != null) {
            if (activity.currentFocus == null) {
                return
            }
        }
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }


}