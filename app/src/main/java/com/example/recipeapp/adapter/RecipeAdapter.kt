package com.example.recipeapp.adapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.HomeFragmentDirections
import com.example.recipeapp.R
import com.example.recipeapp.model.Recipe

class RecipeAdapter(private var recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.recipe_image)
        val recipeTitle: TextView = itemView.findViewById(R.id.recipe_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.recipeTitle.text = currentRecipe.title

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(currentRecipe.image)
            .into(holder.recipeImage)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("recipe", currentRecipe)
            }
            holder.itemView.findNavController().navigate(R.id.action_global_recipeDetailsFragment, bundle)
        }
    }

    override fun getItemCount() = recipes.size

    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        // Notify the adapter that the data set has changed.
        notifyDataSetChanged()
    }
}