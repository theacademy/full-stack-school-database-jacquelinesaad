package mthree.com.fullstackschool.dao;

import mthree.com.fullstackschool.dao.mappers.CourseMapper;
import mthree.com.fullstackschool.model.Course;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CourseDaoImpl implements CourseDao {

    private final JdbcTemplate jdbcTemplate;

    public CourseDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Course createNewCourse(Course course) {
        //YOUR CODE STARTS HERE

        String sql = "INSERT INTO course (courseCode, courseDesc, teacherId) VALUES (?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, course.getCourseName());
            ps.setString(2, course.getCourseDesc());
            ps.setObject(3, course.getTeacherId(), java.sql.Types.INTEGER);
            return ps;
        }, keyHolder);

        // Handle null keys with exception
        Number key = keyHolder.getKey();
        if (key != null) {
            course.setCourseId(key.intValue());
        } else {
            throw new RuntimeException("Failed to retrieve course ID.");
        }

        return course;

        //YOUR CODE ENDS HERE
    }

    @Override
    public List<Course> getAllCourses() {
        //YOUR CODE STARTS HERE

        String sql = "SELECT * FROM course";
        return jdbcTemplate.query(sql, new CourseMapper());

        //YOUR CODE ENDS HERE
    }

    @Override
    public Course findCourseById(int id) {
        //YOUR CODE STARTS HERE

        String sql = "SELECT * FROM course WHERE cid = ?";
        return jdbcTemplate.queryForObject(sql, new CourseMapper(), id);

        //YOUR CODE ENDS HERE
    }

    @Override
    public void updateCourse(Course course) {
        //YOUR CODE STARTS HERE

        String sql = "UPDATE course SET courseCode = ?, courseDesc = ?, teacherId = ? WHERE cid = ?";
        int rowsAffected = jdbcTemplate.update(sql, course.getCourseName(), course.getCourseDesc(), course.getTeacherId(), course.getCourseId());

        //YOUR CODE ENDS HERE
    }

    @Override
    public void deleteCourse(int id) {
        //YOUR CODE STARTS HERE

        // First, remove students from course_student
        String sqlDeleteStudents = "DELETE FROM course_student WHERE course_id = ?";
        jdbcTemplate.update(sqlDeleteStudents, id);

        // Then delete the course
        String sql = "DELETE FROM course WHERE cid = ?";
        jdbcTemplate.update(sql, id);

        //YOUR CODE ENDS HERE
    }

    @Override
    public void deleteAllStudentsFromCourse(int courseId) {
        //YOUR CODE STARTS HERE

        String sql = "DELETE FROM course_student WHERE course_id = ?";
        jdbcTemplate.update(sql, courseId);

        //YOUR CODE ENDS HERE
    }
}
