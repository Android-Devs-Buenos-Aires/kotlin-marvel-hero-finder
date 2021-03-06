package pe.devpicon.android.marvelheroes.presentation.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pe.androidperu.marvelheroes.databinding.ItemHeroBinding
import pe.devpicon.android.marvelheroes.domain.Hero
import pe.devpicon.android.marvelheroes.presentation.home.ComicCharacterListAdapter.ViewHolder

class ComicCharacterListAdapter(val items: List<Hero>, val itemClick: (Hero) -> Unit) :
        RecyclerView.Adapter<ViewHolder>() {

    // Cuando una función cuenta con una sola línea, esta se puede simplificar
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemHeroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindCharacter(items[position])
    }

    class ViewHolder(private val itemBinding: ItemHeroBinding, val itemClick: (Hero) -> Unit) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindCharacter(character: Hero) {
            with(character) {
                Log.d(javaClass.simpleName, thumbnailUrl)
                Picasso.get().load(thumbnailUrl)
                        .resize(48, 48)
                        .centerCrop()
                        .into(itemBinding.heroThumbnailImageView)
                itemBinding.heroNameTextView.text = name
                itemBinding.heroOverviewTextView.text = description
                itemBinding.root.setOnClickListener { itemClick(this) }
            }
        }
    }

    /*interface OnItemClickListener {
        operator fun invoke(comicCharacter: Hero)
    }*/

}