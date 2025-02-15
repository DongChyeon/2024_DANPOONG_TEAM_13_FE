package com.daon.onjung.data.datasource

import com.daon.onjung.di.IoDispatcher
import com.daon.onjung.network.adapter.ApiResult
import com.daon.onjung.network.model.BaseResponse
import com.daon.onjung.network.model.request.DeviceTokenRequest
import com.daon.onjung.network.model.request.LoginRequest
import com.daon.onjung.network.model.response.NotificationAllowedResponse
import com.daon.onjung.network.model.response.ProfileResponse
import com.daon.onjung.network.model.response.UserTokenResponse
import com.daon.onjung.network.service.AuthService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val authService: AuthService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthDataSource {

    override suspend fun kakaoLogin(
        loginRequest: LoginRequest
    ): Flow<ApiResult<BaseResponse<UserTokenResponse>>> = flow {
        emit(authService.kakaoLogin(loginRequest))
    }.flowOn(ioDispatcher)

    override suspend fun patchDeviceToken(
        deviceTokenRequest: DeviceTokenRequest
    ): Flow<ApiResult<BaseResponse<Any>>> = flow {
        emit(authService.patchDeviceToken(deviceTokenRequest))
    }.flowOn(ioDispatcher)

    override suspend fun logout(
    ): Flow<ApiResult<BaseResponse<Any>>> = flow {
        emit(authService.logout())
    }.flowOn(ioDispatcher)

    override suspend fun deleteAccount(
    ): Flow<ApiResult<BaseResponse<Any>>> = flow {
        emit(authService.deleteAccount())
    }.flowOn(ioDispatcher)

    override suspend fun getUserProfile(): Flow<ApiResult<BaseResponse<ProfileResponse>>> = flow {
        emit(authService.getUserProfile())
    }.flowOn(ioDispatcher)

    override suspend fun patchNotificationAllowed(): Flow<ApiResult<BaseResponse<NotificationAllowedResponse>>> = flow {
        emit(authService.patchNotificationAllowed())
    }.flowOn(ioDispatcher)
}