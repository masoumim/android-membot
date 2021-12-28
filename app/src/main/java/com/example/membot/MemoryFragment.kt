package com.example.membot

import android.animation.ArgbEvaluator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.membot.databinding.FragmentMemoryBinding
import com.example.membot.models.BoardSize
import com.example.membot.models.MemoryCard
import com.example.membot.models.MemoryGame
import com.example.membot.utils.DEFAULT_ICONS
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_memory.view.*
import java.io.*

class MemoryFragment : Fragment() {

    companion object {
        private const val TAG = "MemoryFragment"
    }


    private var _binding: FragmentMemoryBinding? = null
    private val binding get() = _binding!!


    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter
    private var boardSize: BoardSize = BoardSize.MEDIUM

    // Nav Controller Object
    private lateinit var navController: NavController

    // The save file for accuracy data
    val fileName = "memoryGameAccuracy.txt"

    // The save file for high score data
    val highScoreFile = "memoryGameHighScore.txt"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemoryBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clRoot = view.findViewById(R.id.clRoot)
        rvBoard = view.findViewById(R.id.rvBoard)
        tvNumMoves = view.findViewById(R.id.tvNumMoves)
        tvNumPairs = view.findViewById(R.id.tvNumPairs)

        // Set the retry and quit buttons to invisible
        view.quitBtn.isVisible = false
        view.retryBtn.isVisible = false

        setupBoard()

        // Handle the QUIT button
        view.quitBtn.setOnClickListener{
            navController = Navigation.findNavController(view)
            navController!!.navigate(R.id.action_memoryFragment_to_mainFragment)
        }

        // Handle the RETRY button:
        binding.retryBtn.setOnClickListener{
            // Set the retry and quit buttons to invisible
            view.quitBtn.isVisible = false
            view.retryBtn.isVisible = false

            // Re-setup the board
            setupBoard()
            // Refresh the strikes counter text view
            tvNumMoves.text = "Strikes: ${memoryGame.strikes}"
            // Refresh the pairs counter text view
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound}"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.memory_game_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mi_refresh -> {
                //setup the game again
                setupBoard()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBoard() {
        tvNumPairs.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_progress_none))
        memoryGame = MemoryGame(boardSize)

        adapter = MemoryBoardAdapter(requireContext(), boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }

        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(requireContext(), boardSize.getWidth())
    }

    private fun updateGameWithFlip(position: Int) {
        //Error Handling
        if(memoryGame.haveWonGame()){
            //alert user
                Snackbar.make(clRoot,"You already won!",Snackbar.LENGTH_LONG).show()
         return
        }
        if (memoryGame.isCardFaceUp(position)){
            //Alert the user of an invalid move
            Snackbar.make(clRoot,"Invalid move!",Snackbar.LENGTH_SHORT).show()
            return
        }
        //Flip over the card
       if (memoryGame.flipCard(position)){
           Log.i(TAG,"Found a match! Num pairs found: ${memoryGame.numPairsFound}")
           val color = ArgbEvaluator().evaluate(
               memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(requireContext(), R.color.color_progress_none),
                ContextCompat.getColor(requireContext(),R.color.color_progress_full)


           ) as Int
           tvNumPairs.setTextColor(color)
           tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound}"
           if (memoryGame.haveWonGame()){
               // Save user accuracy score
               saveUserAccuracyData()

               // Save high score
               saveUserHighScore()

               // Display Retry and Quit buttons
               requireView().quitBtn.isVisible = true
               requireView().retryBtn.isVisible = true
           }
       }
        tvNumMoves.text = "Strikes: ${memoryGame.strikes}"
        adapter.notifyDataSetChanged()
        // Check if user has reached 6 strikes, if so, end the game and display "retry" and "quit" buttons
        if(memoryGame.strikes == 6){
            // Save user accuracy score
            saveUserAccuracyData()

            // Save high score
            saveUserHighScore()

            // Display Retry and Quit buttons
            requireView().quitBtn.isVisible = true
            requireView().retryBtn.isVisible = true
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
            if(savedHighScoreString.toInt() < memoryGame.numPairsFound){
                writeHighScore(memoryGame.numPairsFound.toString())
            }
        }
        else{
            // if the file doesn't exist, just write the score this round to file.
            writeHighScore(memoryGame.numPairsFound.toString())
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
            updatedNumCorrectAns = numCorrectAns.toInt() + memoryGame.numPairsFound
            updatedNumQuestions = numQuestions.toInt() + memoryGame.numPairsFound + memoryGame.strikes
        }
        else{
            // 2. Update this saved score value with the scores from this play session / round.
            updatedNumCorrectAns = memoryGame.numPairsFound
            updatedNumQuestions = memoryGame.numPairsFound + memoryGame.strikes
        }

        // 3. Save the updated score values to the save file.
        val updatedScoreString = updatedNumCorrectAns.toString() + "," + updatedNumQuestions.toString()
        writeToFile(updatedScoreString)
    }
}