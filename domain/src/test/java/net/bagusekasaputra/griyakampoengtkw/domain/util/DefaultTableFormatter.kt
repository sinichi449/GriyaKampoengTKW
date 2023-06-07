package net.bagusekasaputra.griyakampoengtkw.domain.util

class DefaultTableFormatter(
    private val columnHeaderItems: List<String>,
    private val rowHeaderItems: List<Int>,
    private val cellItems: List<List<String>>,
): TableFormatter {
    companion object {
        const val FORMAT_COLUMN_HEADER = "%-15s%-15s%-15s%-15s\n"
        const val FORMAT_ROWS = "%-15d%-15s%-15s%-15s\n"
    }

    override fun print() {
        createColumnHeader()
        createRows()
    }

    private fun createColumnHeader() {
        System.out.format(FORMAT_COLUMN_HEADER, *columnHeaderItems.toTypedArray())
    }

    private fun createRows() {
        repeat(cellItems.size) {
            val cell = cellItems[it].toTypedArray()
            val rowHeader = rowHeaderItems[it]

            System.out.format(FORMAT_ROWS, rowHeader, *cell)
        }
    }

    class Builder {
        private var columnHeaderItems = emptyList<String>()
        private var rowHeaderItems = emptyList<Int>()
        private var cellItems = emptyList<List<String>>()

        fun setColumnHeaders(columnHeaderItems: List<String>): Builder {
            this.columnHeaderItems = columnHeaderItems

            return this
        }

        fun setRowHeaders(rowHeaderItems: List<Int>): Builder {
            this.rowHeaderItems = rowHeaderItems

            return this
        }

        fun setCellItems(cellItems: List<List<String>>): Builder {
            this.cellItems = cellItems

            return this
        }

        fun build(): DefaultTableFormatter {
            return DefaultTableFormatter(columnHeaderItems, rowHeaderItems, cellItems)
        }
    }
}