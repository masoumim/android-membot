package com.example.membot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.membot.databinding.FragmentGraphBinding
import com.example.membot.databinding.FragmentPiBinding
import kotlinx.android.synthetic.main.fragment_graph.view.*
import java.io.*


class GraphFragment : Fragment() {

    // View Binding object
    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    // Nav Controller Object
    private lateinit var navController: NavController

    // The name of the save file used to save accuracy scores.
    val piAccuracyFile = "piAccuracy.txt"
    val musicAccuracyFile = "musicAccuracy.txt"
    val safeCrackerAccuracyFile = "safeCrackerAccuracy.txt"
    val memoryAccuracyFile = "memoryGameAccuracy.txt"
    val chimpAccuracyFile = "chimpAccuracy.txt"

    // The name of the save file used to save high score.
    val piHighScoreFile = "piHighScore.txt"
    val musicHighScoreFile = "musicHighScore.txt"
    val safeCrackerHighScoreFile = "safeCrackerHighScore.txt"
    val memoryHighScoreFile = "memoryGameHighScore.txt"
    val chimpHighScoreFile = "chimpHighScore.txt"




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the score text views

        // Pi game
        view.piAccuracyTextView.text = getAccuracyScore(piAccuracyFile)
        view.piHighScoreTextView.text = readFromFile(piHighScoreFile)

        // Music game
        view.musicAccuracyTextView.text = getAccuracyScore(musicAccuracyFile)
        view.musicHighScoreTextView.text = readFromFile(musicHighScoreFile)

        // Safe Cracker
        view.safeCrackerAccuracyTextView.text = getAccuracyScore(safeCrackerAccuracyFile)
        view.safeHighScoreTextView.text = readFromFile(safeCrackerHighScoreFile)

        // Memory Game
        view.memoryAccuracyTextView.text = getAccuracyScore(memoryAccuracyFile)
        view.memoryHighScoreTextView.text = readFromFile(memoryHighScoreFile)

        // Chimp Test
        view.chimpAccuracyTextView.text = getAccuracyScore(chimpAccuracyFile)
        view.chimpHighScoreTextView.text = readFromFile(chimpHighScoreFile)

        // Handle the BACK button
        view.backBtn.setOnClickListener{
            navController = Navigation.findNavController(view)
            navController.navigate(R.id.action_graphFragment_to_mainFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun readFromFile(fileName: String): String {
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

    fun getAccuracyScore(fileName: String): String{
        var savedScoreString = ""

        var file = File(context?.filesDir,fileName)

        if(file.exists()){
            savedScoreString = readFromFile(fileName)

            // Extract the NUMBER OF CORRECT ANSWERS from the string:
            val numCorrectAns = savedScoreString.substring(0,savedScoreString.indexOf(","))

            // Extract the NUMBER OF QUESTIONS ASKED from the string:
            val numQuestions = savedScoreString.substring(savedScoreString.indexOf(",")+1,savedScoreString.length)

            val accuracy = (numCorrectAns.toDouble() / numQuestions.toDouble()) * 100

            val accuracyRounded = String.format("%.1f", accuracy).toDouble()

            savedScoreString = accuracyRounded.toString()

        }else{
            savedScoreString = "no data"
        }

        return savedScoreString
    }

}