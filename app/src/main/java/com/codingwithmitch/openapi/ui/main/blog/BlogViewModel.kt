package com.codingwithmitch.openapi.ui.main.blog

import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.util.AbsentLiveData
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(

): BaseViewModel<BlogStateEvent, BlogViewState>(){
    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when(stateEvent){

            is BlogSearchEvent ->{
                return AbsentLiveData.create()
            }

            is None ->{
                return AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }


}