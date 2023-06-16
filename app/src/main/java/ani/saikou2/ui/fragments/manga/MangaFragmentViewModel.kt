package ani.saikou2.ui.fragments.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ani.saikou2.data.tracker.MediaCardData
import ani.saikou2.domain.animeclient.AnimeClientUseCases
import ani.saikou2.domain.mangaclient.MangaClientUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaFragmentViewModel @Inject constructor(
    private val mangaClientUseCases: MangaClientUseCases
): ViewModel() {
    private val _trendingManga: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val trendingManga = _trendingManga.asStateFlow()

    private val _popularManga: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val popularManga = _popularManga.asStateFlow()

    private val _trendingNovel: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val trendingNovel = _trendingNovel.asStateFlow()

    init {
        with(viewModelScope) {
            launch { _trendingManga.value = mangaClientUseCases.getTrendingManga() }
            launch { _popularManga.value = mangaClientUseCases.getPopularManga() }
            launch { _trendingNovel.value = mangaClientUseCases.getTrendingNovel() }
        }
    }
}