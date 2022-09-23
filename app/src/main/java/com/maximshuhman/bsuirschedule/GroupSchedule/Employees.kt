import kotlinx.serialization.Serializable


@Serializable
data class Employees (

	val id : Int,
	val firstName : String,
	val middleName : String,
	val lastName : String,
	val photoLink : String,
	val degree : String,
	val degreeAbbrev : String,
	val rank : String,
	val email : String,
	val department : String,
	val urlId : String,
	val calendarId : String,
	val jobPositions : String
)