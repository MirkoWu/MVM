package com.mirkowu.lib_network.state

import com.mirkowu.lib_network.ErrorBean


class ResponseData<T> {
    var state: RequestState = RequestState.LOADING
    var data: T? = null
    lateinit var errorBean: ErrorBean

    constructor() {
    }

    constructor(state: RequestState) {
        this.state = state
    }

    constructor(data: T?) {
        this.state = RequestState.SUCCESS
        this.data = data
    }

    constructor(errorBean: ErrorBean) {
        this.state = RequestState.FAILURE
        this.errorBean = errorBean
    }

    fun isSuccess(): Boolean {
        return state == RequestState.SUCCESS
    }

    fun isFailure(): Boolean {
        return state == RequestState.FAILURE
    }
}