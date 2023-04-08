package net.bagusekasaputra.griyakampoengtkw.idk

interface IDataLamaManager {

    fun createDataLamaFolderIfNotExist()

    fun extract(pathToFile: String?)

}