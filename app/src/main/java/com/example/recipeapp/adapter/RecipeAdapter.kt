package com.example.recipeapp.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.model.Recipe
import com.example.recipeapp.ui.RecipeListFragmentDirections

class RecipeAdapter(private val recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

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
            val direction = RecipeListFragmentDirections.actionRecipeListFragmentToRecipeDetailsFragment(currentRecipe)
            holder.itemView.findNavController().navigate(direction)
        }
    }

    override fun getItemCount() = recipes.size
}