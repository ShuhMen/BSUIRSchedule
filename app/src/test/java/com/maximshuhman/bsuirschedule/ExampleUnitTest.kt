package com.maximshuhman.bsuirschedule

import com.maximshuhman.bsuirschedule.data.DataModule
import com.maximshuhman.bsuirschedule.data.dto.CommonSchedule
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.repositories.ScheduleNetworkSourceImpl
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun getGroupsList_test() {
        runBlocking {

            val service = DataModule.provideApiService(DataModule.provideRetrofit())

            val source = ScheduleNetworkSourceImpl(service)

            val list = source.getGroupsList()

            assert(list is AppResult.Success)
        }
    }

    @Test
    fun getGroupSchedule_test() {
        runBlocking {
            val service = DataModule.provideApiService(DataModule.provideRetrofit())
            val source = ScheduleNetworkSourceImpl(service)
            val list = source.getGroupSchedule("220601")

            assert(list is AppResult.Success<CommonSchedule>)
        }
    }

    @Test
    fun getEmployeeList_test() {
        runBlocking {
            val service = DataModule.provideApiService(DataModule.provideRetrofit())
            val source = ScheduleNetworkSourceImpl(service)
            val list = source.getEmployeesList()

            assert(list is AppResult.Success<List<Employee>>)
            assert((list as AppResult.Success<List<Employee>>).data.isNotEmpty()){
                "Список преподавателей пуст"
            }

        }
    }
}