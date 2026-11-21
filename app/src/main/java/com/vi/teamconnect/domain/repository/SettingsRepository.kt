package com.vi.teamconnect.domain.repository

import com.vi.teamconnect.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getAppSettings(): Flow<AppSettings>
    suspend fun updateAppSettings(settings: AppSettings): Result<Unit>
}
