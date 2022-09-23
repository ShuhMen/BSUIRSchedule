import kotlinx.serialization.Serializable

@Serializable
data class StudentGroups (

	val specialityName : String,
	val specialityCode : String,
	val numberOfStudents : Int,
	val name : Int,
	val educationDegree : Int
)