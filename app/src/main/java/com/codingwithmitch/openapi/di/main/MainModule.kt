package com.codingwithmitch.openapi.di.main

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.persistence.AppDatabase
import com.codingwithmitch.openapi.persistence.BlogPostDao
import com.codingwithmitch.openapi.repository.main.AccountRepository
import com.codingwithmitch.openapi.repository.main.AccountRepositoryImpl
import com.codingwithmitch.openapi.repository.main.BlogRepositoryImpl
import com.codingwithmitch.openapi.repository.main.CreateBlogRepositoryImpl
import com.codingwithmitch.openapi.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import javax.inject.Singleton

@FlowPreview
@Module
@InstallIn(SingletonComponent::class)
object MainModule {

	@Singleton
	@Provides
	fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
		return retrofitBuilder
			.build()
			.create(OpenApiMainService::class.java)
	}

	@Singleton
	@Provides
	fun provideAccountRepository(
		openApiMainService: OpenApiMainService,
		accountPropertiesDao: AccountPropertiesDao,
		sessionManager: SessionManager
	): AccountRepository {
		return AccountRepositoryImpl(openApiMainService, accountPropertiesDao, sessionManager)
	}

	@Singleton
	@Provides
	fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
		return db.getBlogPostDao()
	}

	@Singleton
	@Provides
	fun provideBlogRepository(
		openApiMainService: OpenApiMainService,
		blogPostDao: BlogPostDao,
		sessionManager: SessionManager
	): BlogRepositoryImpl {
		return BlogRepositoryImpl(openApiMainService, blogPostDao, sessionManager)
	}

	@Singleton
	@Provides
	fun provideCreateBlogRepository(
		openApiMainService: OpenApiMainService,
		blogPostDao: BlogPostDao,
		sessionManager: SessionManager
	): CreateBlogRepositoryImpl {
		return CreateBlogRepositoryImpl(openApiMainService, blogPostDao, sessionManager)
	}

}

















