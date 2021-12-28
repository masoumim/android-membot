package com.example.membot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.navigation.Navigation


class MainFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        // Buttons
        view.findViewById<ImageView>(R.id.piGameIcon).setOnClickListener(this)
        view.findViewById<ImageView>(R.id.musicGameIcon).setOnClickListener(this)
        view.findViewById<ImageView>(R.id.chimpGameIcon).setOnClickListener(this)
        view.findViewById<ImageView>(R.id.safeCrackerGameIcon).setOnClickListener(this)
        view.findViewById<ImageView>(R.id.memoryGameIcon).setOnClickListener(this)
        view.findViewById<Button>(R.id.button_graph).setOnClickListener(this)
    }


    // On Click Listeners
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button_graph -> navController!!.navigate(R.id.action_mainFragment_to_graphFragment)
            R.id.piGameIcon -> navController!!.navigate(R.id.action_mainFragment_to_piFragment)
            R.id.musicGameIcon -> navController!!.navigate(R.id.action_mainFragment_to_musicFragment)
            R.id.chimpGameIcon -> navController!!.navigate(R.id.action_mainFragment_to_chimpFragment)
            R.id.safeCrackerGameIcon -> navController!!.navigate(R.id.action_mainFragment_to_safeCrackerFragment)
            R.id.memoryGameIcon -> navController!!.navigate(R.id.action_mainFragment_to_memoryFragment)
        }
    }
}