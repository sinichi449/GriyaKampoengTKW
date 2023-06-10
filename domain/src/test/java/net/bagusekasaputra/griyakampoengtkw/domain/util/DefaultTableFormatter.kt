package net.bagusekasaputra.griyakampoengtkw.domain.util

/**
 * Convert your data into a console-style table.
 *
 * @param columnHeaderItems specify your Column Header in a [List] of [String].
 * @param rowHeaderItems specify your Row Header content in a [List] of [Int].
 * Note that currently this class only support for numeric Row Header.
 * @param cellItems convert your class and its attribute into a [List] of [String], where
 * each element of the [List] would correspond to, for example, your whole class properties/attributes.
 * @param whiteSpaces the number of whitespaces to separate each column. See [Builder.getChPattern]
 * for more information about [whiteSpaces].
 * @param chPattern the pattern which would be followed to produce the Column Header. See [Builder.getChPattern]
 * for more information about [chPattern].
 * @param rhPattern the pattern which would be followed to produce the Row Header. See [Builder.getChPattern]
 * for more information about [rhPattern].
 *
 */
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
    }

    override fun print() {
        println()
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
            repeat(whiteSpaces * (columnHeaderItems.size + 1)) {
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

        /**
         * A single unit of a Column Header follows below formula:
         *
         * **`%-{W}{F}`**
         *
         * where **{W}** is the number of whitespaces to separate the columns, and **{F}** is data format at
         * that column. The **{F}** could be a numeric format, thus corresponds to regex pattern **`d`**
         * _(digit)_, or whitespaces format **`s`**, which denotes for regex of, for example, a _tabs_  or a
         * _space_ keystroke.
         *
         * This doesn't constrained to just a single pattern. You can concatenate them.
         *
         * **For example**: **%-13d** would means _create a digit format and add 13 whitespaces after it_.
         * A bit more complex example would be **%-13d%-13s%-13s\n** would means _create a row with three
         * columns, where for each columns add 13 whitespaces after each of it, and print a new line **(\n)**
         * at the end of this row_.
         *
         * @param chSize pass your [Collection] subclass and call [Collection.size]. This is where I
         * would create a pattern for your [chSize] of columns.
         *
         */
        private fun getChPattern(chSize: Int): String {
            return buildString {
                repeat(chSize) {
                    append("%-${NUM_WHITESPACES}${UNIT_WORD_FORMAT}")
                }
                append("\n")
            }
        }

        /**
         * Row Header pattern follows exactly the same principle as Column Header.
         *
         * @see getChPattern
         */
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