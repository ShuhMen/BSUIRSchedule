data class Employees(

    var type: Int,
    var id: Int,
    var firstName: String?,
    var middleName: String?,
    var lastName: String,
    var photoLink: String?,
    var photo: ByteArray?,
    var urlId: String?
    /*	val degree       : String?,
        val degreeAbbrev : String?,
        val rank         : String?,
        val email        : String?,
        val department   : String?,
        val calendarId   : String?,
        val jobPositions : String?*/
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Employees

        if (type != other.type) return false
        if (id != other.id) return false
        if (firstName != other.firstName) return false
        if (middleName != other.middleName) return false
        if (lastName != other.lastName) return false
        if (photoLink != other.photoLink) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false
        return urlId == other.urlId
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + id
        result = 31 * result + (firstName?.hashCode() ?: 0)
        result = 31 * result + (middleName?.hashCode() ?: 0)
        result = 31 * result + lastName.hashCode()
        result = 31 * result + photoLink.hashCode()
        result = 31 * result + (photo?.contentHashCode() ?: 0)
        result = 31 * result + (urlId?.hashCode() ?: 0)
        return result
    }
}