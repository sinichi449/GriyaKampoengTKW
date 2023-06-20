package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

interface ViewModelListener {
    fun onProgress()

    fun onCompleted()

    fun onFailed(failMsg: String?)
}