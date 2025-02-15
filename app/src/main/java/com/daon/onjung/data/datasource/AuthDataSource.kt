package com.daon.onjung.data.datasource

import com.daon.onjung.network.adapter.ApiResult
import com.daon.onjung.network.model.BaseResponse
import com.daon.onjung.network.model.request.DeviceTokenRequest
import com.daon.onjung.network.model.request.LoginRequest
import com.daon.onjung.network.model.response.NotificationAllowedResponse
import com.daon.onjung.network.model.response.ProfileResponse
import com.daon.onjung.network.model.response.UserTokenResponse
import kotlinx.coroutines.flow.Flow

interface AuthDataSource {

    suspend fun kakaoLogin(
        loginRequest: LoginRequest
    ): Flow<ApiResult<BaseResponse<UserTokenResponse>>>

    suspend fun patchDeviceToken(
        deviceTokenRequest: DeviceTokenRequest
    ): Flow<ApiResult<BaseResponse<Any>>>

    suspend fun logout(): Flow<ApiResult<BaseResponse<Any>>>

    suspend fun deleteAccount(): Flow<ApiResult<BaseResponse<Any>>>

    suspend fun getUserProfile(): Flow<ApiResult<BaseResponse<ProfileResponse>>>

    suspend fun patchNotificationAllowed(): Flow<ApiResult<BaseResponse<NotificationAllowedResponse>>>
}