package io.github.junrdev.hiddengems.presentation.di

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.junrdev.hiddengems.data.local.HiddenGemDb
import io.github.junrdev.hiddengems.data.repo.GemsRepo
import io.github.junrdev.hiddengems.data.repo.ReviewsRepo
import io.github.junrdev.hiddengems.data.repo.ServingRepo
import io.github.junrdev.hiddengems.data.repo.UsersRepo
import io.github.junrdev.hiddengems.domain.repoimpl.GemsRepoImpl
import io.github.junrdev.hiddengems.domain.repoimpl.ReviewsRepoImpl
import io.github.junrdev.hiddengems.domain.repoimpl.ServingRepoImpl
import io.github.junrdev.hiddengems.domain.repoimpl.UsersRepoImpl
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import javax.inject.Singleton

@Module
@InstallIn(
    SingletonComponent::class
)
object AppModule {

    @Provides
    @Singleton
    fun providesContext(app: Application) = app as Context


    //app db
    @Provides
    @Singleton
    fun providesAppDb(context: Context): HiddenGemDb {
        return HiddenGemDb.getDb(context)
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
        return GemsRepoImpl(firestore, storage)
    }

    @Provides
    @Singleton
    fun providesUserRepo(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): UsersRepo {
        return UsersRepoImpl(firebaseAuth, firebaseFirestore)
    }

    @Provides
    @Singleton
    fun providesServingsRepo(firestore: FirebaseFirestore): ServingRepo {
        return ServingRepoImpl(firestore)
    }


    @Provides
    @Singleton
    fun providesReviewsRepo(firestore: FirebaseFirestore): ReviewsRepo {
        return ReviewsRepoImpl(firestore)
    }


    @Provides
    @Singleton
    fun providesAppDatastore(context: Context, firebaseAuth: FirebaseAuth): AppDatastore {
        return AppDatastore(context, firebaseAuth)
    }


}