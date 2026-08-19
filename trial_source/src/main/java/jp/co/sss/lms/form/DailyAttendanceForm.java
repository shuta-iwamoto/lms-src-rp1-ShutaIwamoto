package jp.co.sss.lms.form;

import lombok.Data;

@Data
public class DailyAttendanceForm {
    
	/** 勤怠情報ID（主キー） */
	private Integer studentAttendanceId;
   
	/** 退校日 */
	private String leaveDate;
    
	/** 研修日 */
	private String trainingDate;
	/** 出勤時刻 */
	private String trainingStartTime;
    
	/** 退勤時刻 */
	private String trainingEndTime;
    
	/** 中抜け時間 */
	private Integer blankTime;
   
	/** 中抜け時間 */
	private String blankTimeValue;
   
	/** ステータスコード */
	private String status;
    
	/** 備考 */
	private String note;
    
	/** セクション名 */
	private String sectionName;
   
	/** 本日判定フラグ */
	private Boolean isToday;
    
	/** エラー判定フラグ */
	private Boolean isError;
    
	/** 研修日 */
	private String dispTrainingDate;
    
	/** ステータス表示名 */
	private String statusDispName;
    
	/** LMSユーザーID */
	private String lmsUserId;
    
	/** ユーザー名 */
	private String userName;
    
	/** コース名 */
	private String courseName;
   
	/** インデックス番号 */
	private String index;

	
    // 時間・分入力用フィールド（Integer型）
    
	/** 出勤時刻（時） */
	private Integer trainingStartTimeHour;
    
	/** 出勤時刻（分） */
	private Integer trainingStartTimeMinute;
    
	/** 退勤時刻（時） */
	private Integer trainingEndTimeHour;
    
	/** 退勤時刻（分） */
	private Integer trainingEndTimeMinute;

}