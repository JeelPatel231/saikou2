package ani.saikou2.ui.fragments.animedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import ani.saikou2.data.tracker.MediaDetailsFull
import ani.saikou2.domain.animeclient.GetAnimeDetailsUseCase
import ani.saikou2.usecases.CreateMediaSourceFromUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val exoPlayer: ExoPlayer,
    val createMediaSourceFromUri: CreateMediaSourceFromUri,
    animeDetailsUseCase: GetAnimeDetailsUseCase,
): ViewModel() {
    val navArgs = AnimeDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _animeDetails: MutableStateFlow<MediaDetailsFull?> = MutableStateFlow(null)
    val animeDetails = _animeDetails.asStateFlow()

    init {
        viewModelScope.launch {
            _animeDetails.value = animeDetailsUseCase(navArgs.id)
        }
    }
}