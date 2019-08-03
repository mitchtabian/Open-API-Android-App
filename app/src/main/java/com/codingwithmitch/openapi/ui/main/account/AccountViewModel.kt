package com.codingwithmitch.openapi.ui.main.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.api.main.network_responses.AccountPropertiesResponse
import com.codingwithmitch.openapi.session.SessionManager
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.Callback
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import java.lang.Exception
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val openApiMainService: OpenApiMainService
)
    : ViewModel()
{

    private val TAG: String = "AppDebug"

    init {
        getAccountProperties()
    }

    private fun getAccountProperties(){

        sessionManager.observeSession().value?.authToken?.token?.let {
            viewModelScope.launch {

                val token = "Token 0333ec24e34766df2e1e413874664723d3c62172"
                Log.d(TAG, ": ${token}")
                openApiMainService.getAccountProperties2(token).enqueue(object: retrofit2.Callback<AccountPropertiesResponse> {

                    override fun onFailure(call: Call<AccountPropertiesResponse>, t: Throwable) {
                        Log.e(TAG, "response: ${t.message}")
                    }

                    override fun onResponse(call: Call<AccountPropertiesResponse>, response: retrofit2.Response<AccountPropertiesResponse>) {
                        if(response.code() == 200){
                            Log.d(TAG, "response code: ${response.code()}")
                            Log.d(TAG, "response body: ${response.body().toString()}")
                            Log.d(TAG, "response body: ${call.request().body().toString()}")
                            Log.d(TAG, "response headers: ${response.headers()}")
                        }
                        else{
                            Log.d(TAG, "response code: ${response.code()}")
                            Log.d(TAG, "response body: ${response.body().toString()}")
                            Log.d(TAG, "response body: ${call.request().body().toString()}")
                            Log.d(TAG, "response headers: ${response.headers()}")
                        }
                    }
                })

//                try{
//                    val token = "Token ${it}"
//                    Log.d(TAG, "token: ${token}")
//                    val accountResponse = openApiMainService.getAccountProperties(token)
//
//                    Log.d(TAG, "getAccountProperties: ${accountResponse}")
//                }catch (e: Exception){
//                    Log.e(TAG, "getAccountProperties: Exception: ${e.message}")
//                }
            }
        }
    }

    fun logout(){
        sessionManager.logout()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}