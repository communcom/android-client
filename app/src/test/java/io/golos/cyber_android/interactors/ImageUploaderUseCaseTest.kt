//package io.golos.cyber_android.interactors
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import io.golos.cyber_android.imageUploadRepo
//import io.golos.domain.interactors.images.ImageUploadUseCase
//import io.golos.domain.requestmodel.CompressionParams
//import io.golos.domain.requestmodel.QueryResult
//import junit.framework.Assert.assertNotSame
//import junit.framework.Assert.assertTrue
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.runBlocking
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import java.io.File
//
///**
// * Created by yuri yurivladdurain@gmail.com on 2019-04-29.
// */
//class ImageUploaderUseCaseTest {
//    @Rule
//    @JvmField
//    public val rule = InstantTaskExecutorRule()
//
//    private lateinit var imagesUseCase: ImageUploadUseCase
//
//    @Before
//    fun before() {
//        imagesUseCase = ImageUploadUseCase(imageUploadRepo)
//    }
//
//    @Test
//    fun test() = runBlocking {
//        val firstImage = File(this::class.java.getResource("/test.jpeg")!!.file)!!
//        val secondImage = File(this::class.java.getResource("/test2.jpg")!!.file)!!
//
//        imagesUseCase.subscribe()
//        imagesUseCase.unsubscribe()
//        imagesUseCase.subscribe()
//
//        var uploadedImages = imagesUseCase.getAsLiveData.value
//        imagesUseCase.getAsLiveData.observeForever {
//            uploadedImages = it
//        }
//
//        imagesUseCase.submitImageForUpload(firstImage.absolutePath, CompressionParams.DirectCompressionParams)
//        delay(50)
//        assertTrue(uploadedImages!![firstImage.absolutePath] is QueryResult.Success)
//        val firstImageUrl = (uploadedImages!![firstImage.absolutePath] as QueryResult.Success).originalQuery.url
//
//        imagesUseCase.submitImageForUpload(secondImage.absolutePath, CompressionParams.DirectCompressionParams)
//        delay(50)
//        assertTrue(uploadedImages!![secondImage.absolutePath] is QueryResult.Success)
//        val secondImageUrl = (uploadedImages!![secondImage.absolutePath] as QueryResult.Success).originalQuery.url
//
//        assertNotSame(firstImageUrl, secondImageUrl)
//
//    }
//}