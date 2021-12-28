package com.example.membot

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.membot.databinding.FragmentMusicBinding
import com.example.membot.ui.music.MusicViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.launch as launch
import android.media.MediaPlayer
import java.io.*


class MusicFragment : Fragment() {

    // Music Game ViewModel object
    private val viewModel: MusicViewModel by viewModels()

    // View Binding object
    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    // Nav Controller Object
    private lateinit var navController: NavController

    var currentButtonIndex = 0
    var currentButton = 0
    var inputResult = 0

    var mMediaPlayer: MediaPlayer? = null

    // The save file for accuracy data
    val fileName = "musicAccuracy.txt"

    // The save file for high score data
    val highScoreFile = "musicHighScore.txt"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    // Game Logic goes here:
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the "retry" and "quit buttons when game starts:
        binding.retryButtonMusic.isVisible = false
        binding.quitButtonMusic.isVisible = false

        // Display the initial score and strikes (both 0)
        displayScore()
        displayStrikes()

        // Initialize the current button to be the first element in the button sequence
        currentButton = viewModel.buttonSequence[currentButtonIndex]

        // Play initial sequence (first note / button)
        playSequence()

        // Button Handlers:
        // Blue Button:
        binding.MusicButton1.setOnClickListener{
            mMediaPlayer = MediaPlayer.create(context, R.raw.simon_sound1)
            mMediaPlayer!!.start()
            viewModel.setInput(1)
            // Check user input
            inputResult = checkUserInput()
            // Process uer input result
            processUserInputResult(inputResult)
        }
        // Red Button:
        binding.MusicButton2.setOnClickListener {
            mMediaPlayer = MediaPlayer.create(context, R.raw.simon_sound2)
            mMediaPlayer!!.start()
            viewModel.setInput(2)
            // Check user input
            inputResult = checkUserInput()
            // Process uer input result
            processUserInputResult(inputResult)
        }
        // Green Button:
        binding.MusicButton3.setOnClickListener {
            mMediaPlayer = MediaPlayer.create(context, R.raw.simon_sound3)
            mMediaPlayer!!.start()
            viewModel.setInput(3)
            // Check user input
            inputResult = checkUserInput()
            // Process uer input result
            processUserInputResult(inputResult)
        }
        // Yellow Button:
        binding.MusicButton4.setOnClickListener {
            mMediaPlayer = MediaPlayer.create(context, R.raw.simon_sound4)
            mMediaPlayer!!.start()
            viewModel.setInput(4)
            // Check user input
            inputResult = checkUserInput()
            // Process uer input result
            processUserInputResult(inputResult)
        }
        // retry button:
        binding.retryButtonMusic.setOnClickListener{
            binding.retryButtonMusic.isVisible = false
            binding.quitButtonMusic.isVisible = false
            viewModel.resetGame()
            displayScore()
            displayStrikes()
            currentButtonIndex = 0
            currentButton = viewModel.buttonSequence[currentButtonIndex]
            playSequence()
        }
        // Handle the QUIT button
        binding.quitButtonMusic.setOnClickListener{
            navController = Navigation.findNavController(view)
            navController!!.navigate(R.id.action_musicFragment_to_mainFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // METHODS / FUNCTIONS
    fun playSequence() {
        setButtonsNotClickable()
        GlobalScope.launch(context = Dispatchers.Main) {
        // Add a short delay before playing a new sequence.
            delay(500)
            for(number in viewModel.buttonSequence){
            when (number) {
                1 -> {
                    binding.MusicButton1.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1Lit))
                    mMediaPlayer = MediaPlayer.create(context, R.raw.simon_sound1)
                    mMediaPlayer!!.start()
                }
                2 -> {
                    binding.MusicButton2.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton2Lit))
                    mMediaPlayer = MediaPlayer.create(context, R.raw.simon_sound2)
                    mMediaPlayer!!.start()
                }
                3 -> {
                    binding.MusicButton3.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton3Lit))
                    mMediaPlayer = MediaPlayer.create(context, R.raw.simon_sound3)
                    mMediaPlayer!!.start()
                }
                4 -> {
                    binding.MusicButton4.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4Lit))
                    mMediaPlayer = MediaPlayer.create(context, R.raw.simon_sound4)
                    mMediaPlayer!!.start()
                }
                else -> Log.d("NO BUTTON","")
            }
            // 1 Second delay before turning the button "off"
            delay(1000)
            binding.MusicButton1.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton1))
            binding.MusicButton2.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton2))
            binding.MusicButton3.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton3))
            binding.MusicButton4.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.musicButton4))
            // 1 Second delay before turning back into the WHEN block
            delay(1000)
        }
            setButtonsClickable()
        }
    }

    // Checks if the button clicked by user matches current button in sequence
    fun checkUserInput(): Int{
        if(viewModel.userInput == currentButton){
            Toast.makeText(context,"correct!",Toast.LENGTH_SHORT).show()
            return if((currentButtonIndex + 1) < viewModel.buttonSequence.size) {
                // Return value (correct input)
                0
            } else{
                Toast.makeText(context,"sequence correct!",Toast.LENGTH_SHORT).show()
                // Return value (correct sequence)
                1
            }
        }
        else{
            // Wrong - end round and increment strike counter
            Toast.makeText(context,"wrong!",Toast.LENGTH_SHORT).show()
        }
        // Default return (incorrect input)
        return 2
    }

    fun processUserInputResult(inputResult: Int) {
        when (this.inputResult) {
            // Input correct:
            0 -> {
                currentButtonIndex++
                currentButton = viewModel.buttonSequence[currentButtonIndex]
            }
            // Sequence correct:
            1 -> {
                // Add new button to sequence
                viewModel.addButtonToSequence()
                // Reset the current button to the first element in the sequence
                currentButtonIndex = 0
                currentButton = viewModel.buttonSequence[currentButtonIndex]
                // Increment score
                viewModel.incrementScore()
                // Display updated score
                displayScore()
                // Play updated sequence from beginning
                playSequence()
            }
            // Incorrect input:
            2 -> {
                // Increment strike counter
                viewModel.incrementStrikes()
                // Display updated strikes
                displayStrikes()
                // Check if game over (3 strikes)
                if(viewModel.strikes == 3){
                    // Save user accuracy score
                    saveUserAccuracyData()
                    // Save user high score data
                    saveUserHighScore()
                    // Display "retry" and "quit" buttons
                    binding.retryButtonMusic.isVisible = true
                    binding.quitButtonMusic.isVisible = true
                    setButtonsNotClickable()
                }
                else {
                    // Reset / clear the button sequence
                    viewModel.clearSequence()
                    // Start a new sequence by adding an initial button to it
                    viewModel.addButtonToSequence()
                    // Reset the current button to the first element in the sequence
                    currentButtonIndex = 0
                    currentButton = viewModel.buttonSequence[currentButtonIndex]
                    // Play the new sequence
                    playSequence()
                }
            }
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

    fun displayScore(){
        binding.userScore.text = viewModel.score.toString()
    }

    fun displayStrikes(){
        binding.userStrikes.text = viewModel.strikes.toString()
    }

    fun setButtonsClickable(){
        binding.MusicButton1.isEnabled = true
        binding.MusicButton2.isEnabled = true
        binding.MusicButton3.isEnabled = true
        binding.MusicButton4.isEnabled = true
    }

    fun setButtonsNotClickable(){
        binding.MusicButton1.isEnabled = false
        binding.MusicButton2.isEnabled = false
        binding.MusicButton3.isEnabled = false
        binding.MusicButton4.isEnabled = false
    }
}