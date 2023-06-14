package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

interface OnResultListener {

    fun onLoading()

    fun onCompleted()

    fun onFailure(failMsg: String?)

}