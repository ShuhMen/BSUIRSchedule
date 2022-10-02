
data class Lesson (

	var day_of_week: Int,
	var auditories : List<String>,
	var endLessonTime : String?,
	var lessonTypeAbbrev : String?,
	var note : String?,
	var numSubgroup : Int?,
	var startLessonTime : String?,
	var studentGroups : List<StudentGroups>,
	var subject : String?,
	var subjectFullName : String?,
	var weekNumber : List<Int>?,
	var employees : List<Employees>?,
	var dateLesson : String?,
	var startLessonDate : String?,
	var endLessonDate : String?,
	var announcementStart : String?,
	var announcementEnd : String?,
	var announcement : Boolean?,
	var split : Boolean?
)