package jp.co.sss.lms.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 勤怠管理画面用DTO
 * 
 * @author 東京ITスクール
 */
@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceManagementDto extends StudentAttendanceDto {

	/** 当日フラグ */
	private Boolean isToday;
	/** 中抜け時間（文字列） */
	private String blankTimeValue;
	/** セクション名 */
	private String sectionName;

	// --- 以下、HTML側で参照されている不足フィールドを追加 ---
	/** 日付 */
	private Date trainingDate;
	/** 出勤時刻 */
	private String trainingStartTime;
	/** 退勤時刻 */
	private String trainingEndTime;
	/** 中抜け開始 */
	private String blankStartTime;
	/** 中抜け終了 */
	private String blankEndTime;
	/** ステータス表示名 */
	private String statusDispName;
	/** 備考 */
	private String note;
}