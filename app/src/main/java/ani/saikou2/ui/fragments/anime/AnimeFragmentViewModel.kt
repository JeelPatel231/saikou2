package ani.saikou2.ui.fragments.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ani.saikou2.data.tracker.MediaCardData
import ani.saikou2.domain.animeclient.AnimeClientUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeFragmentViewModel @Inject constructor(
    private val animeClientUseCases: AnimeClientUseCases
): ViewModel() {
    private val _trendingAnime: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val trendingAnime = _trendingAnime.asStateFlow()

    private val _popularAnime: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val popularAnime = _popularAnime.asStateFlow()

    private val _recentlyUpdated: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val recentlyUpdated = _recentlyUpdated.asStateFlow()

    init {
        with(viewModelScope) {
            launch { _trendingAnime.value = animeClientUseCases.getTrendingAnime() }
            launch { _popularAnime.value = animeClientUseCases.getPopularAnime() }
            launch { _recentlyUpdated.value = animeClientUseCases.getRecentlyUpdatedAnime() }
        }
    }
}