import kotlinx.serialization.Serializable


@Serializable
data class StudentGroupDto(
    val name                                : String,
    val facultyId                           : Int,
    val facultyAbbrev                       : String,
    val specialityDepartmentEducationFormId : Int,
    val specialityName                      : String,
    val specialityAbbrev                    : String,
    val course                              : Int,
    val id                                  : Int,
    val calendarId                          : String,
    val educationDegree                     : Int
)