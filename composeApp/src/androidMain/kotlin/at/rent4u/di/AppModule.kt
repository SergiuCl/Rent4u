package at.rent4u.di

import at.rent4u.data.BookingRepository
import at.rent4u.data.ToolRepository
import at.rent4u.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideToolRepository(): ToolRepository {
        return ToolRepository()
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore, auth: FirebaseAuth): UserRepository {
        return UserRepository(firestore, auth)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(firestore: FirebaseFirestore, auth: FirebaseAuth): BookingRepository {
        return BookingRepository(firestore, auth)
    }
}
