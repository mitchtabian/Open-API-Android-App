package com.codingwithmitch.openapi.interactors.auth

import com.codingwithmitch.openapi.business.datasource.network.auth.OpenApiAuthService
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling
import com.codingwithmitch.openapi.business.domain.util.MessageType
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.interactors.auth.Register
import com.codingwithmitch.openapi.datasource.cache.AccountDaoFake
import com.codingwithmitch.openapi.datasource.cache.AppDatabaseFake
import com.codingwithmitch.openapi.datasource.cache.AuthTokenDaoFake
import com.codingwithmitch.openapi.datasource.datastore.AppDataStoreManagerFake
import com.codingwithmitch.openapi.datasource.network.auth.RegisterResponses
import com.codingwithmitch.openapi.presentation.util.DataStoreKeys
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
 * 1. Register Success
 * 2. Register Failure (Email already in use)
 * 3. Register Failure (Username already in use)
 * 4. Register Failure (Passwords must match)
 * 6. Register Failure (Missing Email)
 * 7. Register Failure (Missing Username)
 * 8. Register Failure (Missing password)
 * 9. Register Failure (Missing password2)
 */
class RegisterTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var register: Register

    // dependencies
    private lateinit var service: OpenApiAuthService
    private lateinit var accountDao: AccountDaoFake
    private lateinit var authTokenDao: AuthTokenDaoFake
    private lateinit var dataStore: AppDataStoreManagerFake

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        baseUrl = mockWebServer.url("/api/recipe/")
        service = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(OpenApiAuthService::class.java)

        accountDao = AccountDaoFake(appDatabase)
        authTokenDao = AuthTokenDaoFake(appDatabase)
        dataStore = AppDataStoreManagerFake()

        // instantiate the system in test
        register = Register(
            service = service,
            accountDao = accountDao,
            authTokenDao = authTokenDao,
            appDataStoreManager = dataStore
        )
    }

    @Test
    fun registerSuccess() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(RegisterResponses.registerSuccess)
        )

        // User Information
        val pk = RegisterResponses.pk
        val email = RegisterResponses.email
        val username = RegisterResponses.username
        val password = RegisterResponses.password
        val password2 = RegisterResponses.password
        val token = RegisterResponses.token

        // confirm no AuthToken is stored in cache
        var cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm no Account is stored in cache
        var cachedAccount = accountDao.searchByEmail(email)
        assert(cachedAccount == null)

        // confirm no email is stored in DataStore
        var storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        val emissions = register.execute(
            email = email,
            username = username,
            password = password,
            confirmPassword = password2
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm AuthToken is cached
        cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken?.account_pk == pk)
        assert(cachedToken?.token == token)

        // confirm Account is cached
        cachedAccount = accountDao.searchByPk(pk)
        assert(cachedAccount?.email == email)
        assert(cachedAccount?.username == username)
        assert(cachedAccount?.pk == pk)

        // confirm email is saved to DataStore
        storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == email)

        // confirm second emission is the cached AuthToken
        assert(emissions[1].data?.accountPk == pk)
        assert(emissions[1].data?.token == token)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun registerFail_emailInUse() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(RegisterResponses.registerFail_emailInUse)
        )

        // User Information
        val pk = RegisterResponses.pk
        val email = RegisterResponses.email
        val username = RegisterResponses.username
        val password = RegisterResponses.password
        val password2 = RegisterResponses.password

        // confirm no AuthToken is stored in cache
        var cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm no Account is stored in cache
        var cachedAccount = accountDao.searchByEmail(email)
        assert(cachedAccount == null)

        // confirm no email is stored in DataStore
        var storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        val emissions = register.execute(
            email = email,
            username = username,
            password = password,
            confirmPassword = password2
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm AuthToken is NOT cached
        cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm Account is NOT cached
        cachedAccount = accountDao.searchByPk(pk)
        assert(cachedAccount == null)

        // confirm email is NOT saved to DataStore
        storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_EMAIL_IN_USE)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun registerFail_usernameInUse() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(RegisterResponses.registerFail_usernameInUse)
        )

        // User Information
        val pk = RegisterResponses.pk
        val email = RegisterResponses.email
        val username = RegisterResponses.username
        val password = RegisterResponses.password
        val password2 = RegisterResponses.password

        // confirm no AuthToken is stored in cache
        var cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm no Account is stored in cache
        var cachedAccount = accountDao.searchByEmail(email)
        assert(cachedAccount == null)

        // confirm no email is stored in DataStore
        var storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        val emissions = register.execute(
            email = email,
            username = username,
            password = password,
            confirmPassword = password2
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm AuthToken is NOT cached
        cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm Account is NOT cached
        cachedAccount = accountDao.searchByPk(pk)
        assert(cachedAccount == null)

        // confirm email is NOT saved to DataStore
        storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_USERNAME_IN_USE)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun registerFail_passwordsMustMatch() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(RegisterResponses.registerFail_passwordsMustMatch)
        )

        // User Information
        val pk = RegisterResponses.pk
        val email = RegisterResponses.email
        val username = RegisterResponses.username
        val password = RegisterResponses.password
        val password2 = RegisterResponses.password

        // confirm no AuthToken is stored in cache
        var cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm no Account is stored in cache
        var cachedAccount = accountDao.searchByEmail(email)
        assert(cachedAccount == null)

        // confirm no email is stored in DataStore
        var storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        val emissions = register.execute(
            email = email,
            username = username,
            password = password,
            confirmPassword = password2
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm AuthToken is NOT cached
        cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm Account is NOT cached
        cachedAccount = accountDao.searchByPk(pk)
        assert(cachedAccount == null)

        // confirm email is NOT saved to DataStore
        storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_PASSWORDS_MUST_MATCH)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun registerFail_emailMissing() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(RegisterResponses.registerFail_emailMissing)
        )

        // User Information
        val pk = RegisterResponses.pk
        val email = RegisterResponses.email
        val username = RegisterResponses.username
        val password = RegisterResponses.password
        val password2 = RegisterResponses.password

        // confirm no AuthToken is stored in cache
        var cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm no Account is stored in cache
        var cachedAccount = accountDao.searchByEmail(email)
        assert(cachedAccount == null)

        // confirm no email is stored in DataStore
        var storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        val emissions = register.execute(
            email = email,
            username = username,
            password = password,
            confirmPassword = password2
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm AuthToken is NOT cached
        cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm Account is NOT cached
        cachedAccount = accountDao.searchByPk(pk)
        assert(cachedAccount == null)

        // confirm email is NOT saved to DataStore
        storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_EMAIL_BLANK_FIELD)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun registerFail_usernameMissing() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(RegisterResponses.registerFail_usernameMissing)
        )

        // User Information
        val pk = RegisterResponses.pk
        val email = RegisterResponses.email
        val username = RegisterResponses.username
        val password = RegisterResponses.password
        val password2 = RegisterResponses.password

        // confirm no AuthToken is stored in cache
        var cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm no Account is stored in cache
        var cachedAccount = accountDao.searchByEmail(email)
        assert(cachedAccount == null)

        // confirm no email is stored in DataStore
        var storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        val emissions = register.execute(
            email = email,
            username = username,
            password = password,
            confirmPassword = password2
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm AuthToken is NOT cached
        cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm Account is NOT cached
        cachedAccount = accountDao.searchByPk(pk)
        assert(cachedAccount == null)

        // confirm email is NOT saved to DataStore
        storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_USERNAME_BLANK_FIELD)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun registerFail_passwordMissing() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(RegisterResponses.registerFail_passwordMissing)
        )

        // User Information
        val pk = RegisterResponses.pk
        val email = RegisterResponses.email
        val username = RegisterResponses.username
        val password = RegisterResponses.password
        val password2 = RegisterResponses.password

        // confirm no AuthToken is stored in cache
        var cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm no Account is stored in cache
        var cachedAccount = accountDao.searchByEmail(email)
        assert(cachedAccount == null)

        // confirm no email is stored in DataStore
        var storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        val emissions = register.execute(
            email = email,
            username = username,
            password = password,
            confirmPassword = password2
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm AuthToken is NOT cached
        cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm Account is NOT cached
        cachedAccount = accountDao.searchByPk(pk)
        assert(cachedAccount == null)

        // confirm email is NOT saved to DataStore
        storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_PASSWORD_BLANK_FIELD)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun registerFail_password2Missing() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(RegisterResponses.registerFail_password2Missing)
        )

        // User Information
        val pk = RegisterResponses.pk
        val email = RegisterResponses.email
        val username = RegisterResponses.username
        val password = RegisterResponses.password
        val password2 = RegisterResponses.password

        // confirm no AuthToken is stored in cache
        var cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm no Account is stored in cache
        var cachedAccount = accountDao.searchByEmail(email)
        assert(cachedAccount == null)

        // confirm no email is stored in DataStore
        var storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        val emissions = register.execute(
            email = email,
            username = username,
            password = password,
            confirmPassword = password2
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm AuthToken is NOT cached
        cachedToken = authTokenDao.searchByPk(pk)
        assert(cachedToken == null)

        // confirm Account is NOT cached
        cachedAccount = accountDao.searchByPk(pk)
        assert(cachedAccount == null)

        // confirm email is NOT saved to DataStore
        storedEmail = dataStore.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
        assert(storedEmail == null)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_PASSWORD2_BLANK_FIELD)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }
}
















