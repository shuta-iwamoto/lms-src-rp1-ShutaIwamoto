package jp.co.sss.lms.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.sss.lms.dto.AttendanceManagementDto;
import jp.co.sss.lms.entity.TStudentAttendance;

@Mapper
public interface TStudentAttendanceMapper {
	List<TStudentAttendance> findByLmsUserId(@Param("lmsUserId") Integer lmsUserId, @Param("deleteFlg") Short deleteFlg);
	TStudentAttendance findByLmsUserIdAndTrainingDate(@Param("lmsUserId") Integer lmsUserId, @Param("trainingDate") Date trainingDate, @Param("deleteFlg") Short deleteFlg);
	List<AttendanceManagementDto> getAttendanceManagement(@Param("courseId") Integer courseId, @Param("lmsUserId") Integer lmsUserId, @Param("deleteFlg") Short deleteFlg);
	Boolean insert(TStudentAttendance tStudentAttendance);
	Boolean update(TStudentAttendance tStudentAttendance);
	int notEnterCount(@Param("lmsUserId") Integer lmsUserId, @Param("today") Date today);
}