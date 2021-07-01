package com.codingwithmitch.openapi.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.bumptech.glide.request.RequestOptions
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.persistence.AppDatabase
import com.codingwithmitch.openapi.persistence.AppDatabase.Companion.DATABASE_NAME
import com.codingwithmitch.openapi.persistence.AuthTokenDao
import com.codingwithmitch.openapi.util.Constants
import com.codingwithmitch.openapi.util.PreferenceKeys
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Singleton
	@Provides
	fun provideSharedPreferences(
		application: Application
	): SharedPreferences {
		return application
			.getSharedPreferences(
				PreferenceKeys.APP_PREFERENCES,
				Context.MODE_PRIVATE
			)
	}

	@Singleton
	@Provides
	fun provideSharedPrefsEditor(
		sharedPreferences: SharedPreferences
	): SharedPreferences.Editor {
		return sharedPreferences.edit()
	}

	@Singleton
	@Provides
	fun provideGsonBuilder(): Gson {
		return GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.create()
	}

	@Singleton
	@Provides
	fun provideRetrofitBuilder(gsonBuilder: Gson): Retrofit.Builder {
		return Retrofit.Builder()
			.baseUrl(Constants.BASE_URL)
			.addConverterFactory(GsonConverterFactory.create(gsonBuilder))
	}

	@Singleton
	@Provides
	fun provideAppDb(app: Application): AppDatabase {
		return Room
			.databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
			.fallbackToDestructiveMigration() // get correct db version if schema changed
			.build()
	}

	@Singleton
	@Provides
	fun provideAuthTokenDao(db: AppDatabase): AuthTokenDao {
		return db.getAuthTokenDao()
	}

	@Singleton
	@Provides
	fun provideAccountPropertiesDao(db: AppDatabase): AccountPropertiesDao {
		return db.getAccountPropertiesDao()
	}

	@Singleton
	@Provides
	fun provideRequestOptions(): RequestOptions {
		return RequestOptions
			.placeholderOf(R.drawable.default_image)
			.error(R.drawable.default_image)
	}

}