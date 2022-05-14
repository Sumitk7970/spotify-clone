package com.example.spotifyclone.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.example.spotifyclone.R
import com.example.spotifyclone.data.entities.Song
import com.example.spotifyclone.databinding.FragmentSongBinding
import com.example.spotifyclone.exoplayer.isPlaying
import com.example.spotifyclone.exoplayer.toSong
import com.example.spotifyclone.other.Status.SUCCESS
import com.example.spotifyclone.ui.viewmodels.MainViewModel
import com.example.spotifyclone.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var binding: FragmentSongBinding

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel: SongViewModel by viewModels()

    private var currPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekbar = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        subscribeToObservers()

        binding.ivPlayButton.setOnClickListener {
            currPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }
        binding.ivPrevButton.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }
        binding.ivNextButton.setOnClickListener {
            mainViewModel.skipToNextSong()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    setCurrPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
                seekbar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }
        })
    }

    private fun updateTitleAndSongImage(song: Song) {
        binding.apply {
            tvTitle.text = song.title
            tvSubtitle.text = song.artist
            glide.load(song.imageUrl).into(ivSongIcon)
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                SUCCESS -> {
                    result.data?.let { songs ->
                        if (currPlayingSong == null && songs.isNotEmpty()) {
                            currPlayingSong = songs[0]
                            updateTitleAndSongImage(songs[0])
                        }
                    }
                }
                else -> Unit
            }
        }
        mainViewModel.currPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            currPlayingSong = it.toSong()
            updateTitleAndSongImage(currPlayingSong!!)
        }
        mainViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            binding.ivPlayButton.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause_with_bg
                else R.drawable.ic_play_with_bg
            )
            binding.seekBar.progress = it?.position?.toInt() ?: 0
        }
        songViewModel.currPlayerPosition.observe(viewLifecycleOwner) {
            if (shouldUpdateSeekbar) {
                binding.seekBar.progress = it.toInt()
                setCurrPlayerTimeToTextView(it)
            }
        }
        songViewModel.currSongDuration.observe(viewLifecycleOwner) {
            binding.seekBar.max = it.toInt()
            val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
            binding.tvTotalTime.text = formatter.format(it)
        }
    }

    private fun setCurrPlayerTimeToTextView(millis: Long?) {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        binding.tvCurrTime.text = formatter.format(millis)
    }
}