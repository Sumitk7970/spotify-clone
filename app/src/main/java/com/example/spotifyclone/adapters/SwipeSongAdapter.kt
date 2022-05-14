package com.example.spotifyclone.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ItemViewpagerBottomBinding

class SwipeSongAdapter : BaseSongAdapter(R.layout.item_viewpager_bottom) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        val binding = ItemViewpagerBottomBinding.bind(holder.itemView)
        binding.apply {
            tvTitle.text = song.title
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(song)
            }
        }
    }
}