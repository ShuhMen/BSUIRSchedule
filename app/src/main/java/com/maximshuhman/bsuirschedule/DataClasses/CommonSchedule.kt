import com.maximshuhman.bsuirschedule.DataClasses.Schedules
import kotlinx.serialization.Serializable

@Serializable
data class CommonSchedule(
    val startDate         : String?,
    val endDate           : String?,
    val startExamsDate    : String,
    val endExamsDate      : String,
    val employeeDto       : String?,
    val studentGroupDto   : StudentGroupDto,
    val schedules         : Schedules,
    val previousSchedules : String?,
    val currentTerm       : String,
    val previousTerm      : String?,
    val exams             : ArrayList<String>? = null,
    val currentPeriod     : String,
)