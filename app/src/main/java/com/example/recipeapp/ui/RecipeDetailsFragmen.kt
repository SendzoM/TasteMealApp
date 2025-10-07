package com.example.recipeapp.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.model.Recipe

class RecipeDetailsFragment : Fragment() {

    private var recipe: Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipe = RecipeDetailsFragmentArgs.fromBundle(it).recipe
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_details, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Now populate views with recipe data
        recipe?.let {
            view.findViewById<TextView>(R.id.recipe_title).text = it.title

            // Use fromHtml to properly display the summary, which might contain HTML tags
            val summaryTextView = view.findViewById<TextView>(R.id.recipe_summary)
            summaryTextView.text = android.text.Html.fromHtml(it.summary, android.text.Html.FROM_HTML_MODE_COMPACT)

            // --- THIS IS THE FIX ---
            // Find the source URL TextView and set its text
            val sourceUrlTextView = view.findViewById<TextView>(R.id.recipe_source_url)
            sourceUrlTextView.text = it.sourceUrl

            val imageView = view.findViewById<ImageView>(R.id.recipe_image)
            Glide.with(requireContext())
                .load(it.image)
                .into(imageView)
        }
    }

    companion object {
        fun newInstance(recipe: Recipe): RecipeDetailsFragment {
            val fragment = RecipeDetailsFragment()
            val args = Bundle()
            args.putParcelable("recipe", recipe)
            fragment.arguments = args
            return fragment
        }
    }
}