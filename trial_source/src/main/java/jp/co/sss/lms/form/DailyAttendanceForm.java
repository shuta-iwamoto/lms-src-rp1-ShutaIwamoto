package jp.co.sss.lms.form;

import lombok.Data;

@Data
public class DailyAttendanceForm {
    private Integer studentAttendanceId;
    private String leaveDate;
    private String trainingDate;
    private String trainingStartTime;
    private String trainingEndTime;
    private Integer blankTime;
    private String blankTimeValue;
    private String status;
    private String note;
    private String sectionName;
    private Boolean isToday;
    private Boolean isError;
    private String dispTrainingDate;
    private String statusDispName;
    private String lmsUserId;
    private String userName;
    private String courseName;
    private String index;

    // 時間・分入力用フィールド（Integer型）
    private Integer trainingStartTimeHour;
    private Integer trainingStartTimeMinute;
    private Integer trainingEndTimeHour;
    private Integer trainingEndTimeMinute;
	public void setTrainingStartTimeHour(String string) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	public void setTrainingStartTimeMinute(String string) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	public void setTrainingEndTimeHour(String string) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	public void setTrainingEndTimeMinute(String string) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}