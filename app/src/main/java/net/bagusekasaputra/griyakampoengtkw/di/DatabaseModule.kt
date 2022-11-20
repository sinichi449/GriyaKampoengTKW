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
        return Room.databaseBuilder(
            appContext, MyRoomDatabase::class.java, "griya_kampoeng_tkw.db"
        )
            .addMigrations(MIGRATION_2_3)
            .build()
    }

    @Provides
    fun providesFirebaseDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance(firebaseUrl).reference
    }

}