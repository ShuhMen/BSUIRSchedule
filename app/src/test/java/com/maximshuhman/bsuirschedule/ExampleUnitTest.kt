package com.maximshuhman.bsuirschedule

import com.maximshuhman.bsuirschedule.data.DataModule
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.repositories.ScheduleNetworkSourceImpl
import com.maximshuhman.bsuirschedule.domain.models.GroupReadySchedule
import com.maximshuhman.bsuirschedule.domain.useCases.GetEmployeeListUseCase
import com.maximshuhman.bsuirschedule.domain.useCases.GetGroupListUseCase
import com.maximshuhman.bsuirschedule.domain.useCases.GetGroupScheduleUseCase
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

            val list = GetGroupListUseCase(source)()

            assert(list is AppResult.Success<List<Group>>)
            assert((list as AppResult.Success<List<Group>>).data.isNotEmpty())
        }
    }

    @Test
    fun getGroupSchedule_test() {
        runBlocking {
            val service = DataModule.provideApiService(DataModule.provideRetrofit())
            val source = ScheduleNetworkSourceImpl(service)
            val list = GetGroupScheduleUseCase(source, DataModule.provideUserDao())(23811)

            assert(list is AppResult.Success<GroupReadySchedule>)
        }
    }

    @Test
    fun getEmployeeList_test() {
        runBlocking {
            val service = DataModule.provideApiService(DataModule.provideRetrofit())
            val source = ScheduleNetworkSourceImpl(service)
            val list = GetEmployeeListUseCase(source)()

            assert(list is AppResult.Success<List<Employee>>)
            assert((list as AppResult.Success<List<Employee>>).data.isNotEmpty()){
                "Список преподавателей пуст"
            }

        }
    }
}