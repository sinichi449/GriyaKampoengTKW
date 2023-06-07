package net.bagusekasaputra.griyakampoengtkw.domain.util

interface TableFormatter {

    fun print()

    companion object {
        fun createRowHeader(dataSize: Int): List<Int> {
            return buildList {
                repeat(dataSize) {
                    add(it + 1)
                }
            }
        }

        fun <T> createCells(
            collection: Collection<T>,
            actions: List<(T) -> String>,
        ): List<List<String>> {
            return buildList {
                collection.forEach {
                    val cell = mutableListOf<String>()
                    actions.forEach { action ->
                        cell.add(action(it))
                    }
                    add(cell)
                }
            }
        }
    }
}