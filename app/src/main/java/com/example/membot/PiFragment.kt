package com.example.membot

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.membot.databinding.FragmentPiBinding
import com.example.membot.ui.pi.PiViewModel
import java.io.*


class PiFragment : Fragment() {

    // Pi ViewModel object
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val viewModel: PiViewModel by viewModels()

    // View Binding object
    private var _binding: FragmentPiBinding? = null
    private val binding get() = _binding!!

    // Nav Controller Object
    private lateinit var navController: NavController

    // The name of the save file used to save accuracy scores.
    val fileName = "piAccuracy.txt"

    // The name of the save file used to save high score.
    val highScoreFile = "piHighScore.txt"

    // The highest score the player got this round.
    var highScore = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPiBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display the initial score (0)
        displayScore()

        // Display the initial strikes (0)
        displayStrikes()

        // Hide the RETRY button
        binding.retryButton.isVisible = false

        // Hide the QUIT button
        binding.quitButton.isVisible = false

        // Set the pi digits textView to selected to enable scrolling.
        binding.piDigitsTextView.isSelected = true

        // Populate the list with 100 pi digits.
        viewModel.populatePiList()

        // Initialize the pi query list.
        viewModel.setPiQueryList()

        // Display the pi query list of digits.
        showPiQueryList()

        // Hide the Pi Digits when user clicks editText box.
        binding.textInputEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.piDigitsTextView.isVisible = false
            }
        }

        // Handle SUBMIT button
        binding.submitButton.setOnClickListener{

            // Hide the keyboard
            hideSoftKeyboard(activity)

            // Remove focus from input field
            binding.textInputEditText.clearFocus()

            // Check if submission is correct
            val userInputString = binding.textInputEditText.text.toString() // get the user input as a string
            var userInputList = listOf<Int>() // variable to store the user input as a list of INT
            userInputList = userInputString.map { it.toString().toInt() } // convert user input string to a LIST of INT

            // Compare user input to the pi query list
            if(userInputList == viewModel.piQueryList){
                // Correct!
                Toast.makeText(context,"correct!",Toast.LENGTH_SHORT).show()
                // Update score
                viewModel.incrementScore()
                // Display updated score
                displayScore()
                // Add a new digit to the list
                viewModel.addDigitToPiQueryList()
                // Update the list to show the new digit
                showPiQueryList()
            }
            else{
                // Incorrect!
                Toast.makeText(context,"WRONG!",Toast.LENGTH_SHORT).show()

                // Update Strike count
                if(viewModel.strikes < 3){
                    viewModel.incrementStrikes()
                }

                // Check for 3 strikes
                if(viewModel.strikes == 3){
                    // Game Over
                    Toast.makeText(context,"GAME OVER!",Toast.LENGTH_SHORT).show()

                    // Hide the SUBMIT button
                    binding.submitButton.isVisible = false

                    // Show the RETRY button
                    binding.retryButton.isVisible = true

                    // Show the QUIT button
                    binding.quitButton.isVisible = true

                    // Save user accuracy score
                    saveUserAccuracyData()

                    // Save user high score
                    highScore = viewModel.score
                    saveUserHighScore()
                }
                displayStrikes()
            }

            // Reveal the hidden pi digits
            binding.piDigitsTextView.isVisible = true

            // Clear the input field
            binding.textInputEditText.text?.clear()
        }


        // Handle RETRY button
        binding.retryButton.setOnClickListener{
            viewModel.resetGame()
            showPiQueryList()
            displayScore()
            displayStrikes()
            binding.retryButton.isVisible = false
            binding.quitButton.isVisible = false
            binding.submitButton.isVisible = true
            highScore = 0
        }

        // Handle the QUIT button
        binding.quitButton.setOnClickListener{
            navController = Navigation.findNavController(view)
            navController!!.navigate(R.id.action_piFragment_to_mainFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // METHODS / FUNCTIONS

    private fun showPiQueryList() {
        binding.piDigitsTextView.text = viewModel.piQueryList.toString()
    }

    private fun displayScore(){
        binding.userScore.text = viewModel.score.toString()
    }

    private fun displayStrikes(){
        binding.userStrikes.text = viewModel.strikes.toString()
    }

    // Hide keyboard function
    fun hideSoftKeyboard(activity: FragmentActivity?) {
        if (activity != null) {
            if (activity.currentFocus == null){
                return
            }
        }
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
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
            if(savedHighScoreString.toInt() < highScore){
                writeHighScore(highScore.toString())
            }
        }
        else{
            // if the file doesn't exist, just write the high score this round to file.
            writeHighScore(highScore.toString())
        }
    }
}