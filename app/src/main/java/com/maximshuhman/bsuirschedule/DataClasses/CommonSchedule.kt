import kotlinx.serialization.Serializable

@Serializable
data class CommonSchedule(

    val startDate: String,
    val endDate: String,
    val startExamsDate: String,
    val endExamsDate: String,
    val lastBuild: String?,
)