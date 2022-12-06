
data class Lesson (

	var day_of_week      : Int,
	var auditories       : String,
	var endLessonTime    : String?,
	var lessonTypeAbbrev : String?,
	var note             : String?,
	var numSubgroup      : Int?,
	var startLessonTime  : String?,
	var subject          : String?,
	var subjectFullName  : String?,
	var weekNumber       : String,
	var employees        : Employees,
	var startLessonDate  : String?,
	var endLessonDate    : String?
)