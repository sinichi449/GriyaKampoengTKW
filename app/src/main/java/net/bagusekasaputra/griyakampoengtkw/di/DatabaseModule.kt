package net.bagusekasaputra.griyakampoengtkw.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes.Companion.firebaseUrl

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideMyRoomDatabase(@ApplicationContext appContext: Context): MyRoomDatabase {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE \"image_spr\" (\n" +
                        "\t\"id\"\tINTEGER,\n" +
                        "\t\"kavling_kode\"\tTEXT NOT NULL,\n" +
                        "\t\"uri\"\tTEXT NOT NULL,\n" +
                        "\tPRIMARY KEY(\"id\")\n" +
                        ");")
                database.execSQL("CREATE UNIQUE INDEX index_image_spr_kavling_kode ON image_spr ( kavling_kode ASC )")
            }

        }
        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS \"biaya_marketing\";")
                database.execSQL("CREATE TABLE IF NOT EXISTS `biaya_marketing_v2` (" +
                        "`id` INTEGER, " +
                        "`kavling_kode` TEXT NOT NULL, " +
                        "`tanggal` TEXT NOT NULL, " +
                        "`jenis_biaya` TEXT NOT NULL, " +
                        "`harga` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))" +
                        "")
            }

        }
        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `pengingat` (" +
                        "`id` INTEGER, " +
                        "`title` TEXT NOT NULL, " +
                        "`content` TEXT NOT NULL, " +
                        "`date` TEXT NOT NULL, " +
                        "`time` TEXT NOT NULL, " +
                        "`is_active` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`)"  +
                        ")")
            }
        }

        return Room.databaseBuilder(
            appContext, MyRoomDatabase::class.java, "griya_kampoeng_tkw.db"
        )
            .addMigrations(MIGRATION_2_3, MIGRATION_9_10, MIGRATION_10_11)
            .build()
    }

    @Provides
    fun providesFirebaseDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance(firebaseUrl).reference
    }

}