package net.bagusekasaputra.griyakampoengtkw.idk

interface IDataLamaManager {

    fun createDataLamaFolderIfNotExist()

    fun getFiles(): List<String>

    fun extract(pathToFile: String?)


}