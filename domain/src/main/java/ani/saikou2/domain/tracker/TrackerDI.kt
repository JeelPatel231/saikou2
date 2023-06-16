package ani.saikou2.domain.tracker

import ani.saikou2.data.tracker.AnilistData
import ani.saikou2.data.tracker.AnilistTrackerImpl
import ani.saikou2.data.tracker.Tracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackerDI {
    @Provides
    @Singleton
    fun providesStartLoginTrackerUseCase(
        anilistData: AnilistData
    ): StartLoginTrackerUseCase {
        return StartLoginTrackerUseCase(anilistData)
    }

    @Provides
    @Singleton
    fun provideTrackerUseCases(
        anilistTrackerImpl: Tracker
    ) : TrackerUseCases {
        return TrackerUseCases(
            LoginUserUseCase(anilistTrackerImpl),
            LogoutUserUseCase(anilistTrackerImpl),
            GetUserDataUseCase(anilistTrackerImpl),
        )
    }
}