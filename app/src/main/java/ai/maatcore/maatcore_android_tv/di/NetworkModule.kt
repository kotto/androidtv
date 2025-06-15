package ai.maatcore.maatcore_android_tv.di

import android.content.Context
import ai.maatcore.maatcore_android_tv.data.remote.openai.OpenAiClient
import ai.maatcore.maatcore_android_tv.data.remote.openai.OpenAiService
import ai.maatcore.maatcore_android_tv.data.remote.maatclass.MaatClassService
import ai.maatcore.maatcore_android_tv.data.remote.maattube.MaatTubeService
import ai.maatcore.maatcore_android_tv.data.remote.maattv.MaatTVService
import ai.maatcore.maatcore_android_tv.data.remote.maatfoot.MaatFootService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ai.maatcore.maatcore_android_tv.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOpenAiService(context: Context): OpenAiService {
        val apiKey: String = BuildConfig.OPENAI_API_KEY // Ajoutez cette field dans buildConfigField
        return OpenAiClient.create(context, apiKey)
    }

    @Provides
    @Singleton
    fun provideMaatClassService(): MaatClassService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.maatcore.com/") // TODO externalize
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
        return retrofit.create(MaatClassService::class.java)
    }

    @Provides
    @Singleton
    fun provideMaatTubeService(): MaatTubeService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.maatcore.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
        return retrofit.create(MaatTubeService::class.java)
    }

    @Provides
    @Singleton
    fun provideMaatTVService(): MaatTVService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.maatcore.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
        return retrofit.create(MaatTVService::class.java)
    }

    @Provides
    @Singleton
    fun provideMaatFootService(): MaatFootService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.maatcore.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
        return retrofit.create(MaatFootService::class.java)
    }
}
