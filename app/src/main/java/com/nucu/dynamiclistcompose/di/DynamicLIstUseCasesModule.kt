package com.nucu.dynamiclistcompose.di

import com.nucu.dynamiclistcompose.api.DynamicListUseCaseApi
import com.nucu.dynamiclistcompose.impl.useCases.DynamicListUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DynamicListUseCasesModule {

    @Binds
    @Singleton
    abstract fun bindUseCase(
        impl: DynamicListUseCaseImpl
    ): DynamicListUseCaseApi
}