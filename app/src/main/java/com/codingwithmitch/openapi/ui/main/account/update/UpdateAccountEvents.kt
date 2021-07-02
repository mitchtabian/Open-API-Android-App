package com.codingwithmitch.openapi.ui.main.account.update

sealed class UpdateAccountEvents{

    data class GetAccountFromCache(
        val pk: Int
    ): UpdateAccountEvents()

    data class Update(
        val email: String,
        val username: String
    ): UpdateAccountEvents()

    data class OnUpdateEmail(
        val email: String
    ): UpdateAccountEvents()

    data class OnUpdateUsername(
        val username: String
    ): UpdateAccountEvents()
}
