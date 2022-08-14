package com.example.weatherapp.localDataSource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.model.OpenWeatherApi
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: WeatherDatabase
    private lateinit var dao :WeatherDao

    @Before
    fun setup(){
        database= Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        dao=database.weatherDao()
    }

    @After
    fun teardown(){
        database.close()
    }

    @Test
    fun insertWeatherItem() = runBlocking {
        val weatherItem = OpenWeatherApi(1,false,32.25,30.21,"cairo",5,
            null, listOf(), listOf(), listOf())
        dao.insertWeather(weatherItem)
        val allWeatherItems = dao.getAllWeather().getOrAwaitValue()

        assertThat(allWeatherItems).contains(weatherItem)
    }

    @Test
    fun deleteWeatherItem() = runBlocking {
        val weatherItem = OpenWeatherApi(1,false,32.25,30.21,"cairo",5,
            null, listOf(), listOf(), listOf())
        dao.insertWeather(weatherItem)
        dao.deleteWeather(weatherItem)
        val allWeatherItems = dao.getAllWeather().getOrAwaitValue()

        assertThat(allWeatherItems).doesNotContain(weatherItem)
    }

    @Test
    fun deleteCurrentWeatherItem() = runBlocking {
        val weatherItem = OpenWeatherApi(1,false,32.25,30.21,"cairo",5,
            null, listOf(), listOf(), listOf())
        dao.insertWeather(weatherItem)
        dao.deleteCurrentWeather()
        val allWeatherItems = dao.getAllWeather().getOrAwaitValue()

        assertThat(allWeatherItems).doesNotContain(weatherItem)
    }

    @Test
    fun deleteFavouriteWeatherItem() = runBlocking {
        val weatherItem = OpenWeatherApi(1,true,32.25,30.21,"cairo",5,
            null, listOf(), listOf(), listOf())
        dao.insertWeather(weatherItem)
        dao.deleteFavoriteWeather(1)
        val allWeatherItems = dao.getAllWeather().getOrAwaitValue()

        assertThat(allWeatherItems).doesNotContain(weatherItem)
    }

    @Test
    fun getCurrentWeatherItem() = runBlocking {
        val weatherItem = OpenWeatherApi(1,false,32.25,30.21,"cairo",5,
            null, listOf(), listOf(), listOf())
        dao.insertWeather(weatherItem)
        val weatherObj=dao.getCurrentWeather()

        assertThat(weatherItem).isEqualTo(weatherObj)
    }
}