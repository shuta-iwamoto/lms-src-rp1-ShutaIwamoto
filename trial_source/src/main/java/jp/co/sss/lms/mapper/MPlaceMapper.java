package jp.co.sss.lms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MPlaceMapper {

	/**
	 * 特定会場・期間の受講生勤怠情報を取得する
	 * 
	 * @param placeId 会場ID
	 * @param startDate 開始日
	 * @param endDate 終了日
	 * @return 勤怠情報DTOなど
	 */
	Object getUserAttendanceDto(
			@Param("placeId") Integer placeId,
			@Param("startDate") String startDate,
			@Param("endDate") String endDate);

}