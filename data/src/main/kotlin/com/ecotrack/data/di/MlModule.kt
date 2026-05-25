package com.ecotrack.data.di

import com.ecotrack.core.ml.ImageLabelingClient
import com.ecotrack.core.ml.TextRecognitionClient
import com.ecotrack.core.ml.gemini.GeminiNanoClassifier
import com.ecotrack.core.ml.gemini.NoOpGeminiNanoClassifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MlModule {

    @Provides
    @Singleton
    fun provideImageLabelingClient(): ImageLabelingClient = ImageLabelingClient()

    @Provides
    @Singleton
    fun provideTextRecognitionClient(): TextRecognitionClient = TextRecognitionClient()

    @Provides
    @Singleton
    fun provideGeminiNanoClassifier(): GeminiNanoClassifier = NoOpGeminiNanoClassifier()
}
