package net.bagusekasaputra.griyakampoengtkw.domain.misc

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

class InvalidTimeFrameBaselinePembayaranException(
    message: String = "Timeframe pembayaran tidak valid!! Timeframe harus lebih besar dari 0 bulan."
): Exception(message)

class NotTheSameKeyIdException(
    message: String = "KeyId tidak sama!"
): Exception(message)

class ChangesNotDetectedException(
    message: String = "Tidak ada perubahan pada data!"
): Exception(message)