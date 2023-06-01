package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriIndenBooking
import org.junit.Test

class ImageDataDiriIndenBookingTest {

    @Test
    fun filename_correct() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val correctFileName = "3053d174-4b9b-437c-96aa-68fd44fa0fef.png"

        val imageDataDiri = ImageDataDiriIndenBooking(keyId, "")

        assert(imageDataDiri.getFilename() == correctFileName)
    }

    @Test
    fun folder_path_correct() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val correctFolderPath = "inden_booking_images/data_diri_images/" +
                "3053d174-4b9b-437c-96aa-68fd44fa0fef.png"

        val imageDataDiri = ImageDataDiriIndenBooking(keyId, "")
        val folderPath = "${imageDataDiri.getFolderPath()}/${imageDataDiri.getFilename()}"

        assert(folderPath == correctFolderPath)
    }
}