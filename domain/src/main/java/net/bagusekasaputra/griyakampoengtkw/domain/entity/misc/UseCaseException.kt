package net.bagusekasaputra.griyakampoengtkw.domain.entity.misc

sealed class UseCaseException(cause: Throwable): Throwable(cause) {

    class UnknownException(cause: Throwable): UseCaseException(cause)

    companion object {
        fun createFromThrowable(throwable: Throwable): UseCaseException {
            return if (throwable is UseCaseException) throwable
                else UnknownException(throwable)
        }
    }
}

class NullHargaKavlingException(
    message: String = "Error harga kavling masih kosong"
): Throwable(message)