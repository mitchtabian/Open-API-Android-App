package com.codingwithmitch.openapi.ui


data class DataState<T>(
    var error: Event<StateError>? = null,
    var loading: Loading = Loading(false),
    var data: Data<T>? = null
) {

    companion object {

        fun <T> error(
            errorMessage: String,
            useDialog: Boolean
        ): DataState<T> {
            return DataState(
                error = Event(
                    StateError(
                        errorMessage,
                        useDialog
                    )
                ),
                loading = Loading(false),
                data = null
            )
        }

        fun <T> loading(
            isLoading: Boolean,
            cachedData: T? = null
        ): DataState<T> {
            return DataState(
                error = null,
                loading = Loading(isLoading),
                data = Data(
                    Event.dataEvent(
                        cachedData
                    ), null
                )
            )
        }

        fun <T> data(
            data: T? = null,
            response: Response? = null
        ): DataState<T> {
            return DataState(
                error = null,
                loading = Loading(false),
                data = Data(
                    Event.dataEvent(data),
                    Event.responseEvent(response)
                )
            )
        }
    }
}