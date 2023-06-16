package ani.saikou2.ui.fragments.mangadetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ani.saikou2.data.tracker.MediaDetailsFull
import ani.saikou2.domain.mangaclient.GetMangaDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    mangaDetailsUseCase: GetMangaDetailsUseCase,
): ViewModel() {
    val navArgs = MangaDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _mangaDetails: MutableStateFlow<MediaDetailsFull?> = MutableStateFlow(null)
    val mangaDetails = _mangaDetails.asStateFlow()

    init {
        viewModelScope.launch {
            _mangaDetails.value = mangaDetailsUseCase(navArgs.id)
        }
    }
}