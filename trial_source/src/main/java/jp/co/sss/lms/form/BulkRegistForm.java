package jp.co.sss.lms.form;

import java.util.List;

/**
 * 勤怠一括登録フォーム
 * 
 * @author 東京ITスクール
 */
public class BulkRegistForm {

	/** 会場ID */
	private Integer placeId;

	/** 会場名（表示用） */
	private String placeName;

	/** 検索期間（From） */
	private String searchPeriodFrom;

	/** 検索期間（To） */
	private String searchPeriodTo;

	/** 受講生勤怠情報入力リスト（グリッド形式） */
	private List<DailyAttendanceForm> attendanceList;

	// --- ゲッター・セッター ---

	public Integer getPlaceId() {
		return placeId;
	}

	public void setPlaceId(Integer placeId) {
		this.placeId = placeId;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getSearchPeriodFrom() {
		return searchPeriodFrom;
	}

	public void setSearchPeriodFrom(String searchPeriodFrom) {
		this.searchPeriodFrom = searchPeriodFrom;
	}

	public String getSearchPeriodTo() {
		return searchPeriodTo;
	}

	public void setSearchPeriodTo(String searchPeriodTo) {
		this.searchPeriodTo = searchPeriodTo;
	}

	public List<DailyAttendanceForm> getAttendanceList() {
		return attendanceList;
	}

	public void setAttendanceList(List<DailyAttendanceForm> attendanceList) {
		this.attendanceList = attendanceList;
	}
}