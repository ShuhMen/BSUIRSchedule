import kotlinx.serialization.Serializable

@Serializable
data class Schedules (

	val Четверг : List<Pair>,
	val Пятница : List<Pair>,
	val Вторник : List<Pair>,
	val Понедельник : List<Pair>,
	val Среда : List<Pair>
)