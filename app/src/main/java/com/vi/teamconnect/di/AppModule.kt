package com.vi.teamconnect.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vi.teamconnect.data.local.UserPreferences
import com.vi.teamconnect.data.repository.AuthRepositoryImpl
import com.vi.teamconnect.data.repository.BranchRepositoryImpl
import com.vi.teamconnect.data.repository.CustomerRepositoryImpl
import com.vi.teamconnect.data.repository.EmployeeRepositoryImpl
import com.vi.teamconnect.data.repository.SettingsRepositoryImpl
import com.vi.teamconnect.domain.repository.AuthRepository
import com.vi.teamconnect.domain.repository.BranchRepository
import com.vi.teamconnect.domain.repository.CustomerRepository
import com.vi.teamconnect.domain.repository.EmployeeRepository
import com.vi.teamconnect.domain.repository.SettingsRepository
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
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository = impl

    @Provides
    @Singleton
    fun provideEmployeeRepository(impl: EmployeeRepositoryImpl): EmployeeRepository = impl

    @Provides
    @Singleton
    fun provideBranchRepository(impl: BranchRepositoryImpl): BranchRepository = impl

    @Provides
    @Singleton
    fun provideSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository = impl
}
