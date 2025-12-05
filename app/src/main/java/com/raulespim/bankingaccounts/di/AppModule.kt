package com.raulespim.bankingaccounts.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.raulespim.bankingaccounts.BuildConfig
import com.raulespim.bankingaccounts.core.common.BankingAccountsDatabase
import com.raulespim.bankingaccounts.features.accounts.data.local.AccountDao
import com.raulespim.bankingaccounts.features.accounts.data.remote.AccountApi
import com.raulespim.bankingaccounts.features.accounts.data.repository.AccountRepositoryImpl
import com.raulespim.bankingaccounts.features.accounts.domain.repository.AccountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val USERNAME = "Advantage"
    private const val PASSWORD = "mobileAssignment"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Authorization", Credentials.basic(USERNAME, PASSWORD))
                chain.proceed(requestBuilder.build())
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAccountApi(retrofit: Retrofit): AccountApi =
        retrofit.create(AccountApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BankingAccountsDatabase =
        Room.databaseBuilder(
            context,
            BankingAccountsDatabase::class.java,
            "banking_accounts_db"
        ).build()

    @Provides
    @Singleton
    fun provideAccountDao(db: BankingAccountsDatabase) = db.accountDao

    @Provides
    @Singleton
    fun provideAccountRepository(
        api: AccountApi,
        dao: AccountDao
    ): AccountRepository = AccountRepositoryImpl(api, dao)
}