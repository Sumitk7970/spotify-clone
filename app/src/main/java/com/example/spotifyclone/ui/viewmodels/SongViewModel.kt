package com.example.spotifyclone.ui.viewmodels

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.exoplayer.MusicService
import com.example.spotifyclone.exoplayer.MusicServiceConnection
import com.example.spotifyclone.exoplayer.currentPlaybackPosition
import com.example.spotifyclone.other.Constants
import com.example.spotifyclone.other.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val playbackState = musicServiceConnection.playbackState

    private var _currSongDuration =  MutableLiveData<Long>()
    val currSongDuration: LiveData<Long> = _currSongDuration

    private var _currPlayerPosition =  MutableLiveData<Long>()
    val currPlayerPosition: LiveData<Long> = _currPlayerPosition

    init {
        updateCurrentPlayingPosition()
    }

    private fun updateCurrentPlayingPosition() {
        viewModelScope.launch {
            val position = playbackState.value?.currentPlaybackPosition
            if (currPlayerPosition.value != position) {
                _currPlayerPosition.postValue(position ?: 0L)
                _currSongDuration.postValue(MusicService.currentSongDuration)
            }
            delay(UPDATE_PLAYER_POSITION_INTERVAL)
        }
    }
}