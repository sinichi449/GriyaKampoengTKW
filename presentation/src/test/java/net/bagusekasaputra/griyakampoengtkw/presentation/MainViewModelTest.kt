package net.bagusekasaputra.griyakampoengtkw.presentation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.block.GetAllBlocksAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingAndProgressStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingByBlockAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetProgressKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.promotion.GetPromotionMessageAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.appupdate.GetUpdateInformationUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.block.AddNewBlockUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.AddKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.EditKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.RemoveKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val getAllBlocksUseCase = mock<GetAllBlocksAsyncUseCase>()
    private val addNewBlockUseCase = mock<AddNewBlockUseCase>()
    private val getKavlingByBlockUseCase = mock<GetKavlingByBlockAsyncUseCase>()
    private val addKavlingUseCase = mock<AddKavlingUseCase>()
    private val editKavlingUseCase = mock<EditKavlingUseCase>()
    private val removeKavlingUseCase = mock<RemoveKavlingUseCase>()
    private val getProgresKavlingUseCase = mock<GetProgressKavlingAsyncUseCase>()
    private val getAppUpdateInformationUseCase = mock<GetUpdateInformationUseCase>()
    private val getPromotionMessageUseCase = mock<GetPromotionMessageAsyncUseCase>()

    private val getKavlingAndProgressStreamUseCase = mock<GetKavlingAndProgressStreamAsyncUseCase>()

    private val dispatcher = StandardTestDispatcher()
    private val mainViewModel = MainViewModel(
        getAllBlocksUseCase = getAllBlocksUseCase,
        addNewBlockUseCase = addNewBlockUseCase,
        getKavlingByBlockUseCase = getKavlingByBlockUseCase,
        addKavlingUseCase = addKavlingUseCase,
        editKavlingUseCase = editKavlingUseCase,
        removeKavlingUseCase = removeKavlingUseCase,
        getProgressKavlingUseCase = getProgresKavlingUseCase,
        getKavlingAndProgressStreamUseCase = getKavlingAndProgressStreamUseCase,
        getAppUpdateInformationUseCase = getAppUpdateInformationUseCase,
        getPromotionMessageUseCase = getPromotionMessageUseCase,
        dispatchers = dispatcher,
    )

    private val sizeProgressKavling = 20
    private val progressKosongNums = listOf(5, 10, 15)


}