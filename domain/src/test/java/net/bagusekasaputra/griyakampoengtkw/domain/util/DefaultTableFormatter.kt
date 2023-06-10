package net.bagusekasaputra.griyakampoengtkw.domain.util

class DefaultTableFormatter private constructor(
    private val columnHeaderItems: List<String>,
    private val rowHeaderItems: List<Int>,
    private val cellItems: List<List<String>>,
    private val whiteSpaces: Int,
    private val chPattern: String,
    private val rhPattern: String,
) : TableFormatter {

    companion object {
        const val NUM_WHITESPACES = 15
        const val UNIT_WORD_FORMAT = "s"
        const val UNIT_NUMBER_FORMAT = "d"
        const val FORMAT_COLUMN_HEADER = "%-15s%-15s%-15s%-15s\n"
        const val FORMAT_ROWS = "%-15d%-15s%-15s%-15s\n"
    }

    override fun print() {
        createColumnHeader()
        println(columnAndCellSeparator())
        createRows()
    }

    private fun createColumnHeader() {
        System.out.format(chPattern, *columnHeaderItems.toTypedArray())
    }

    private fun createRows() {
        repeat(cellItems.size) {
            val cell = cellItems[it].toTypedArray()
            val rowHeader = rowHeaderItems[it]

            System.out.format(rhPattern, rowHeader, *cell)
        }
    }

    private fun columnAndCellSeparator(): String {
        return buildString {
            repeat(whiteSpaces * columnHeaderItems.size + 5) {
                append("-")
            }
        }
    }

    class Builder {
        private var columnHeaderItems = emptyList<String>()
        private var rowHeaderItems = emptyList<Int>()
        private var cellItems = emptyList<List<String>>()
        private var numWhiteSpaces = 15

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

        fun setNumOfWhiteSpaces(num: Int): Builder {
            this.numWhiteSpaces = num

            return this
        }

        fun build(): DefaultTableFormatter {
            return DefaultTableFormatter(
                columnHeaderItems, rowHeaderItems, cellItems, numWhiteSpaces,
                getChPattern(columnHeaderItems.size),
                getRhPattern(columnHeaderItems.size)
            )
        }

        private fun getChPattern(chSize: Int): String {
            return buildString {
                repeat(chSize) {
                    append("%-${NUM_WHITESPACES}${UNIT_WORD_FORMAT}")
                }
                append("\n")
            }
        }

        private fun getRhPattern(chSize: Int): String {
            return buildString {
                repeat(chSize) {
                    if (it == 0) {
                        append("%-${NUM_WHITESPACES}${UNIT_NUMBER_FORMAT}")
                    } else {
                        append("%-${NUM_WHITESPACES}${UNIT_WORD_FORMAT}")
                    }
                }
                append("\n")
            }
        }


    }
}