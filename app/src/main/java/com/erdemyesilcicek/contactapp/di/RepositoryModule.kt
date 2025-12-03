package com.erdemyesilcicek.contactapp.di

import com.erdemyesilcicek.contactapp.data.repository.ApiContactRepository
import com.erdemyesilcicek.contactapp.data.repository.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindContactRepository(
        apiContactRepository: ApiContactRepository
    ): ContactRepository
}
