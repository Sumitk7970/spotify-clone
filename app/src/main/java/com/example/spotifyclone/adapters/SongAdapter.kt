package com.example.spotifyclone.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ItemListBinding
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseSongAdapter(R.layout.item_list) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        val binding = ItemListBinding.bind(holder.itemView)
        binding.apply {
            tvTitle.text = song.title
            tvSubtitle.text = song.artist
            glide.load(song.imageUrl).into(ivIcon)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(song)
            }
        }
    }
}