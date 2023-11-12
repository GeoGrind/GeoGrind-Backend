package com.geogrind.geogrindbackend.repositories.courses

import com.geogrind.geogrindbackend.models.courses.Courses
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CoursesRepository : JpaRepository<Courses, UUID> {
    fun findAllByCourseCode(courseCode: String): Array<Courses>?
}