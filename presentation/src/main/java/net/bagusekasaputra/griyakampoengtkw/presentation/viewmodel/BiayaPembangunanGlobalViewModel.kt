package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BiayaPembangunanGlobalViewModel @Inject constructor(

): ViewModel() {

    /* Current ViewPager page */
    private val _currentViewPagerPage = MutableStateFlow(0)
    val currentViewPagerPage = _currentViewPagerPage

    fun setViewPagerPage(page: Int) {
        _currentViewPagerPage.update { page }
    }

}