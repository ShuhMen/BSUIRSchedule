
import com.maximshuhman.bsuirschedule.data.dto.Employee
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val auditories       : ArrayList<String>?,
    val endLessonTime    : String,
    val lessonTypeAbbrev : String?,
    val note             : String?,
    val numSubgroup      : Int,
    val startLessonTime  : String,
    val studentGroups    : ArrayList<StudentGroups>,
    val subject          : String?,
    val subjectFullName  : String?,
    val weekNumber       : ArrayList<Int>?,
    val employees        : ArrayList<Employee>,
    val dateLesson       : String?,
    val startLessonDate  : String?,
    val endLessonDate    : String?,
    val announcement     : Boolean?,
    val split            : Boolean?,
)

