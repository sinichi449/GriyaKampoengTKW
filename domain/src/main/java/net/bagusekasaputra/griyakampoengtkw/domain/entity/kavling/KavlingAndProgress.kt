package net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling

data class KavlingAndProgress(
    val blok: String,
    val kavling: Kavling,
    val progress: ProgressKavlingLegacy,
) {

    companion object {
        fun List<KavlingAndProgress>.containsMultipleBloks(blok: String): Boolean {
            var result = false
            for (item in this) {
                if (item.blok != blok) {
                    result = true
                    break
                }
            }

            return result
        }

        fun List<KavlingAndProgress>.sortByKavling(
            blok: String,
            sorter: KavlingSorter = SingleBlockKavlingSorter()
        ): List<KavlingAndProgress> {
            if (this.containsMultipleBloks(blok)) {
                throw UnsupportedOperationException("Belum mendukung pengurutan Kavling yang terdiri dari blok yang berbeda!")
            } else if (this.isEmpty()) {
                return emptyList()
            } else {
                val map = buildMap {
                    this@sortByKavling.forEach { item ->
                        put(item.kavling, item)
                    }
                }
                val kavlingsOnly = buildList {
                    this@sortByKavling.forEach { item ->
                        add(item.kavling)
                    }
                }
                val sortedKavlings = Kavling.sortKavling(kavlingsOnly, sorter)

                val result = buildList {
                    sortedKavlings.forEach {
                        val kavlingAndProgress = map[it]!!
                        add(kavlingAndProgress)
                    }
                }
                return result
            }
        }
    }

}