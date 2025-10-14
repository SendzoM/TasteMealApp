package com.example.recipeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        val title = view.findViewById<TextView>(R.id.about_title)
        val description = view.findViewById<TextView>(R.id.about_description)
        val mission = view.findViewById<TextView>(R.id.about_mission)
        val contact = view.findViewById<TextView>(R.id.about_contact)

        title.text = getString(R.string.about_title)
        description.text = getString(R.string.about_description)
        mission.text = getString(R.string.about_mission)

        // Get version name dynamically
        val versionName = requireContext()
            .packageManager
            .getPackageInfo(requireContext().packageName, 0)
            .versionName

        // Use string resource with placeholder
        contact.text = getString(R.string.about_contact, versionName)

        return view
    }

}