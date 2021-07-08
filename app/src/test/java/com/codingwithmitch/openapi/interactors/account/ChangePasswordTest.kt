package com.codingwithmitch.openapi.interactors.account

import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling
import com.codingwithmitch.openapi.business.domain.util.MessageType
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.interactors.account.UpdatePassword
import com.codingwithmitch.openapi.datasource.network.account.AccountResponses
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

/**
 * 1. Update password success
 * 2. Update password failure (incorrect password)
 * 3. Update password failure (passwords must match)
 * 4. Update password failure (Random malformed unknown error is returned)
 * 5. Update password failure (blank field)
 */
class ChangePasswordTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var updatePassword: UpdatePassword

    // dependencies
    private lateinit var service: OpenApiMainService

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        baseUrl = mockWebServer.url("/api/recipe/")
        service = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(OpenApiMainService::class.java)

        // instantiate the system in test
        updatePassword = UpdatePassword(
            service = service,
        )
    }

    @Test
    fun updatePasswordSuccess() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.updatePasswordSuccess)
        )

        // User Information
        val pk = AccountResponses.pk
        val currentPassword = AccountResponses.password
        val newPassword = AccountResponses.newPassword
        val confirmNewPassword = AccountResponses.newPassword
        val token = AccountResponses.token
        val authToken = AuthToken(
            accountPk = pk,
            token = token,
        )

        val emissions = updatePassword.execute(
            authToken = authToken,
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmNewPassword = confirmNewPassword
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is a success response
        assert(emissions[1].data?.message == SuccessHandling.SUCCESS_PASSWORD_UPDATED)
        assert(emissions[1].data?.uiComponentType is UIComponentType.None)
        assert(emissions[1].data?.messageType is MessageType.Success)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updatePasswordFail_incorrectPassword() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.updatePasswordFail_incorrectPassword)
        )

        // User Information
        val pk = AccountResponses.pk
        val currentPassword = AccountResponses.password
        val newPassword = AccountResponses.newPassword
        val confirmNewPassword = AccountResponses.newPassword
        val token = AccountResponses.token
        val authToken = AuthToken(
            accountPk = pk,
            token = token,
        )

        val emissions = updatePassword.execute(
            authToken = authToken,
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmNewPassword = confirmNewPassword
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_INCORRECT_PASSWORD)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updatePasswordFail_passwordsMustMatch() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.updatePasswordFail_passwordsMustMatch)
        )

        // User Information
        val pk = AccountResponses.pk
        val currentPassword = AccountResponses.password
        val newPassword = AccountResponses.newPassword
        val confirmNewPassword = AccountResponses.newPassword
        val token = AccountResponses.token
        val authToken = AuthToken(
            accountPk = pk,
            token = token,
        )

        val emissions = updatePassword.execute(
            authToken = authToken,
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmNewPassword = confirmNewPassword
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_PASSWORDS_MUST_MATCH)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updatePasswordFail_random() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.updatePasswordFail_random)
        )

        // User Information
        val pk = AccountResponses.pk
        val currentPassword = AccountResponses.password
        val newPassword = AccountResponses.newPassword
        val confirmNewPassword = AccountResponses.newPassword
        val token = AccountResponses.token
        val authToken = AuthToken(
            accountPk = pk,
            token = token,
        )

        val emissions = updatePassword.execute(
            authToken = authToken,
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmNewPassword = confirmNewPassword
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_UPDATE_PASSWORD)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updatePasswordFail_blankField() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.updatePasswordFail_blankField)
        )

        // User Information
        val pk = AccountResponses.pk
        val currentPassword = AccountResponses.password
        val newPassword = AccountResponses.newPassword
        val confirmNewPassword = AccountResponses.newPassword
        val token = AccountResponses.token
        val authToken = AuthToken(
            accountPk = pk,
            token = token,
        )

        val emissions = updatePassword.execute(
            authToken = authToken,
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmNewPassword = confirmNewPassword
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_BLANK_FIELD)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }
}
























