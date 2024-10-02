package io.github.junrdev.hiddengems.presentation.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.junrdev.hiddengems.HiddenGemsApp
import io.github.junrdev.hiddengems.data.repo.GemsRepo
import io.github.junrdev.hiddengems.data.repo.UsersRepo
import io.github.junrdev.hiddengems.domain.repoimpl.GemsRepoImpl
import io.github.junrdev.hiddengems.domain.repoimpl.UsersRepoImpl
import javax.inject.Singleton

@Module
@InstallIn(
    SingletonComponent::class
)
object AppModule {

    @Provides
    @Singleton
    fun providesApplication(context: Context): HiddenGemsApp {
        return context as HiddenGemsApp
    }


    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }


    //repos
    @Provides
    @Singleton
    fun providesGemsRepo(firestore: FirebaseFirestore, storage: FirebaseStorage): GemsRepo {
        return GemsRepoImpl(firestore,storage)
    }

    @Provides
    @Singleton
    fun providesUserRepo(firebaseAuth: FirebaseAuth): UsersRepo {
        return UsersRepoImpl(firebaseAuth)
    }

}