package br.com.renan.vinylcollection.di

import android.content.Context
import br.com.renan.vinylcollection.data.local.VinylCollectionDatabase
import br.com.renan.vinylcollection.data.local.dao.TaskDao
import br.com.renan.vinylcollection.data.local.dao.VinylRecordDao
import br.com.renan.vinylcollection.data.network.api.DiscogsApiService
import br.com.renan.vinylcollection.data.network.api.RetrofitClient
import br.com.renan.vinylcollection.data.repository.VinylRepository
import br.com.renan.vinylcollection.core.notifications.VinylNotificationManager

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDiscogsApiService(): DiscogsApiService {
        return RetrofitClient.apiService
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VinylCollectionDatabase {
        return VinylCollectionDatabase.getDatabase(context)
    }

    @Provides
    fun provideVinylRecordDao(database: VinylCollectionDatabase): VinylRecordDao {
        return database.vinylRecordDao()
    }

    @Provides
    fun provideTaskDao(database: VinylCollectionDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideVinylRepository(
        apiService: DiscogsApiService,
        vinylRecordDao: VinylRecordDao,
        taskDao: TaskDao
    ): VinylRepository {
        return VinylRepository(apiService, vinylRecordDao, taskDao)
    }

    @Provides
    @Singleton
    fun provideVinylNotificationManager(@ApplicationContext context: Context): VinylNotificationManager {
        return VinylNotificationManager(context)
    }
}