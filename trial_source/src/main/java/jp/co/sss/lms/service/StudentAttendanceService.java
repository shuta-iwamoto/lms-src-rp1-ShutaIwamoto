package jp.co.sss.lms.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import jp.co.sss.lms.dto.AttendanceManagementDto;
import jp.co.sss.lms.dto.LoginUserDto;
import jp.co.sss.lms.entity.TStudentAttendance;
import jp.co.sss.lms.enums.AttendanceStatusEnum;
import jp.co.sss.lms.form.AttendanceForm;
import jp.co.sss.lms.form.DailyAttendanceForm;
import jp.co.sss.lms.mapper.TStudentAttendanceMapper;
import jp.co.sss.lms.util.AttendanceUtil;
import jp.co.sss.lms.util.Constants;
import jp.co.sss.lms.util.DateUtil;
import jp.co.sss.lms.util.MessageUtil;
import jp.co.sss.lms.util.TrainingTime;

@Service
public class StudentAttendanceService {

	@Autowired
	private DateUtil dateUtil;
	@Autowired
	private AttendanceUtil attendanceUtil;
	@Autowired
	private MessageUtil messageUtil;
	@Autowired
	private LoginUserDto loginUserDto;
	@Autowired
	private TStudentAttendanceMapper tStudentAttendanceMapper;

	/**
	 * 概要：コースIDとユーザIDから勤怠情報を検索する。中抜け時間と勤怠ステータスを表示用に加工し、DTOに詰める。
	 */
	public List<AttendanceManagementDto> getAttendanceManagement(Integer courseId, Integer lmsUserId) {
		List<AttendanceManagementDto> attendanceList = tStudentAttendanceMapper.getAttendanceManagement(courseId, lmsUserId, (short) 0);
		
		if (attendanceList != null) {
			for (AttendanceManagementDto dto : attendanceList) {
				TStudentAttendance attendance = tStudentAttendanceMapper.findByLmsUserIdAndTrainingDate(
						lmsUserId, dto.getTrainingDate(), (short) 0);
				
				if (attendance != null) {
					dto.setStudentAttendanceId(attendance.getStudentAttendanceId());
					dto.setStatus(attendance.getStatus());
					dto.setNote(attendance.getNote());
					dto.setBlankTime(attendance.getBlankTime());
					
					// 出退勤時間の有無をチェック
					boolean hasStartTime = (attendance.getTrainingStartTime() != null && !attendance.getTrainingStartTime().isEmpty());
					boolean hasEndTime = (attendance.getTrainingEndTime() != null && !attendance.getTrainingEndTime().isEmpty());
					
					// 1. 備考（note）の内容をチェックしてステータス表示名を決定
					String note = attendance.getNote();
					String statusName = "";
					
					if (note != null && !note.isEmpty()) {
						if (note.contains("遅刻")) {
							statusName = "遅刻";
						} else if (note.contains("早退")) {
							statusName = "早退";
						} else if (note.contains("欠席")) {
							if (!hasStartTime && !hasEndTime) {
								statusName = "欠席";
							}
						}
					}
					
					// 2. 備考にキーワードがない場合は、DBのステータスに応じて日本語名に変換（NONEなどの英語を露出させない）
					if (statusName.isEmpty() && attendance.getStatus() != null) {
						AttendanceStatusEnum statusEnum = AttendanceStatusEnum.getEnum(attendance.getStatus());
						if (statusEnum != null) {
							String enumName = statusEnum.name();
							// ステータスに応じた日本語変換
							if ("NONE".equals(enumName) || "ATTENDANCE".equals(enumName)) {
								statusName = ""; // 通常出席の場合は表示なし
							} else if (enumName.contains("TARDY") || enumName.contains("遅刻")) {
								statusName = "遅刻";
							} else if (enumName.contains("EARLY") || enumName.contains("早退")) {
								statusName = "早退";
							} else if (enumName.contains("ABSENT") || enumName.contains("欠席")) {
								statusName = "欠席";
							} else {
								statusName = "";
							}
						}
					}
					
					dto.setStatusDispName(statusName);
					
					// 中抜け時間がある場合の値設定
					if (attendance.getBlankTime() != null) {
						Map<Integer, String> blankTimesMap = attendanceUtil.setBlankTime();
						if (blankTimesMap != null) {
							dto.setBlankTimeValue(blankTimesMap.get(attendance.getBlankTime()));
						}
					}
				} else {
					dto.setStatus(null);
					dto.setStatusDispName("");
					dto.setNote("");
					dto.setBlankTime(null);
					dto.setBlankTimeValue("");
				}
			}
		}
		return attendanceList;
	}

	public boolean hasUnenteredPastDate(Integer lmsUserId) {
		Date today = attendanceUtil.getTrainingDate();
		Integer count = tStudentAttendanceMapper.notEnterCount(lmsUserId, (short) 0, today);
		return count != null && count > 0;
	}

	public String punchCheck(short codeVal) {
		Date today = attendanceUtil.getTrainingDate();
		TStudentAttendance attendance = tStudentAttendanceMapper.findByLmsUserIdAndTrainingDate(
				loginUserDto.getLmsUserId(), today, (short) 0);
		if (codeVal == Constants.CODE_VAL_ATWORK) {
			if (attendance != null && attendance.getTrainingStartTime() != null) return messageUtil.getMessage("attendance.already.punchIn");
		} else if (codeVal == Constants.CODE_VAL_LEAVING) {
			if (attendance == null || attendance.getTrainingStartTime() == null) return messageUtil.getMessage("attendance.not.punchIn");
			if (attendance.getTrainingEndTime() != null) return messageUtil.getMessage("attendance.already.punchOut");
		}
		return null;
	}

	public String setPunchIn() {
		Date today = attendanceUtil.getTrainingDate();
		TStudentAttendance attendance = tStudentAttendanceMapper.findByLmsUserIdAndTrainingDate(loginUserDto.getLmsUserId(), today, (short) 0);
		String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
		if (attendance == null) {
			attendance = new TStudentAttendance();
			attendance.setLmsUserId(loginUserDto.getLmsUserId());
			attendance.setTrainingDate(today);
			attendance.setTrainingStartTime(currentTime);
			attendance.setDeleteFlg((short) 0);
			tStudentAttendanceMapper.insert(attendance);
		} else {
			attendance.setTrainingStartTime(currentTime);
			tStudentAttendanceMapper.update(attendance);
		}
		return messageUtil.getMessage("attendance.punchIn.success");
	}

	public String setPunchOut() {
		Date today = attendanceUtil.getTrainingDate();
		TStudentAttendance attendance = tStudentAttendanceMapper.findByLmsUserIdAndTrainingDate(loginUserDto.getLmsUserId(), today, (short) 0);
		String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
		if (attendance != null) {
			attendance.setTrainingEndTime(currentTime);
			tStudentAttendanceMapper.update(attendance);
		}
		return messageUtil.getMessage("attendance.punchOut.success");
	}

	public AttendanceForm setAttendanceForm(List<AttendanceManagementDto> attendanceManagementDtoList) {
		AttendanceForm attendanceForm = new AttendanceForm();
		List<DailyAttendanceForm> dailyList = new java.util.ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		
		for (AttendanceManagementDto dto : attendanceManagementDtoList) {
			DailyAttendanceForm dailyForm = new DailyAttendanceForm();
			dailyForm.setSectionName(dto.getSectionName());
			if (dto.getTrainingDate() != null) {
				String dateStr = sdf.format(dto.getTrainingDate());
				dailyForm.setTrainingDate(dateStr);
				dailyForm.setDispTrainingDate(dateStr);
			}
			
			TStudentAttendance attendance = tStudentAttendanceMapper.findByLmsUserIdAndTrainingDate(
					loginUserDto.getLmsUserId(), dto.getTrainingDate(), (short) 0);
			
			if (attendance != null) {
				dailyForm.setStudentAttendanceId(attendance.getStudentAttendanceId());
				
				if (attendance.getTrainingStartTime() != null && !attendance.getTrainingStartTime().isEmpty()) {
					String[] startTimes = attendance.getTrainingStartTime().split(":");
					if (startTimes.length >= 2 && !startTimes[0].isEmpty() && !startTimes[1].isEmpty()) {
						dailyForm.setTrainingStartTimeHour(Integer.parseInt(startTimes[0]));
						dailyForm.setTrainingStartTimeMinute(Integer.parseInt(startTimes[1]));
					}
				}
				if (attendance.getTrainingEndTime() != null && !attendance.getTrainingEndTime().isEmpty()) {
					String[] endTimes = attendance.getTrainingEndTime().split(":");
					if (endTimes.length >= 2 && !endTimes[0].isEmpty() && !endTimes[1].isEmpty()) {
						dailyForm.setTrainingEndTimeHour(Integer.parseInt(endTimes[0]));
						dailyForm.setTrainingEndTimeMinute(Integer.parseInt(endTimes[1]));
					}
				}
				
				dailyForm.setBlankTime(attendance.getBlankTime());
				
				if (attendance.getStatus() != null) {
					dailyForm.setStatus(String.valueOf(attendance.getStatus()));
					AttendanceStatusEnum statusEnum = AttendanceStatusEnum.getEnum(attendance.getStatus());
					if (statusEnum != null) {
						String enumName = statusEnum.name();
						if ("NONE".equals(enumName) || "ATTENDANCE".equals(enumName)) {
							dailyForm.setStatusDispName("");
						} else if (enumName.contains("TARDY") || enumName.contains("遅刻")) {
							dailyForm.setStatusDispName("遅刻");
						} else if (enumName.contains("EARLY") || enumName.contains("早退")) {
							dailyForm.setStatusDispName("早退");
						} else if (enumName.contains("ABSENT") || enumName.contains("欠席")) {
							dailyForm.setStatusDispName("欠席");
						} else {
							dailyForm.setStatusDispName("");
						}
					} else {
						dailyForm.setStatusDispName("");
					}
				} else {
					dailyForm.setStatus(null);
					dailyForm.setStatusDispName("");
				}
				
				dailyForm.setNote(attendance.getNote());
			} else {
				dailyForm.setStatusDispName("");
			}
			
			dailyList.add(dailyForm);
		}
		attendanceForm.setAttendanceList(dailyList);
		attendanceForm.setBlankTimes(attendanceUtil.setBlankTime());
		return attendanceForm;
	}

	public String update(AttendanceForm attendanceForm) {
		if (attendanceForm.getAttendanceList() != null) {
			List<AttendanceManagementDto> dtoList = tStudentAttendanceMapper.getAttendanceManagement(
					loginUserDto.getCourseId(), loginUserDto.getLmsUserId(), (short) 0);
			
			if (dtoList == null) return "更新に失敗しました。";

			for (int i = 0; i < attendanceForm.getAttendanceList().size(); i++) {
				if (dtoList.size() <= i) break;
				DailyAttendanceForm dailyForm = attendanceForm.getAttendanceList().get(i);
				
				Date targetDate = dtoList.get(i).getTrainingDate();
				if (targetDate == null) continue;

				TStudentAttendance attendance = tStudentAttendanceMapper.findByLmsUserIdAndTrainingDate(
						loginUserDto.getLmsUserId(), targetDate, (short) 0);
				
				Integer startHour = dailyForm.getTrainingStartTimeHour();
				Integer startMin = dailyForm.getTrainingStartTimeMinute();
				Integer endHour = dailyForm.getTrainingEndTimeHour();
				Integer endMin = dailyForm.getTrainingEndTimeMinute();
				
				String startTime = (startHour != null && startMin != null) ? String.format("%02d:%02d", startHour, startMin) : null;
				String endTime = (endHour != null && endMin != null) ? String.format("%02d:%02d", endHour, endMin) : null;
				
				Short status = null;
				if (dailyForm.getStatus() != null && !dailyForm.getStatus().isEmpty()) {
					try {
						status = Short.valueOf(dailyForm.getStatus());
					} catch (NumberFormatException e) {
						status = null;
					}
				}
				
				if (attendance == null) {
					if (startTime != null || endTime != null || dailyForm.getBlankTime() != null || (dailyForm.getNote() != null && !dailyForm.getNote().isEmpty()) || status != null) {
						attendance = new TStudentAttendance();
						attendance.setLmsUserId(loginUserDto.getLmsUserId());
						attendance.setTrainingDate(targetDate);
						attendance.setTrainingStartTime(startTime);
						attendance.setTrainingEndTime(endTime);
						attendance.setBlankTime(dailyForm.getBlankTime());
						attendance.setStatus(status);
						attendance.setNote(dailyForm.getNote());
						attendance.setDeleteFlg((short) 0);
						tStudentAttendanceMapper.insert(attendance);
					}
				} else {
					attendance.setTrainingStartTime(startTime);
					attendance.setTrainingEndTime(endTime);
					attendance.setBlankTime(dailyForm.getBlankTime());
					attendance.setStatus(status);
					attendance.setNote(dailyForm.getNote());
					tStudentAttendanceMapper.update(attendance);
				}
			}
		}
		return "勤怠情報を更新しました。";
	}

	public void updateInputCheck(AttendanceForm attendanceForm, BindingResult result) {
		if (attendanceForm.getAttendanceList() == null) return;
		
		boolean isStartTimeInvalidRecorded = false;
		boolean isEndTimeInvalidRecorded = false;
		boolean isPunchInEmptyRecorded = false;
		boolean isRangeRecorded = false;
		boolean isBlankTimeRecorded = false;
		boolean isMaxlengthRecorded = false;
		
		for (int i = 0; i < attendanceForm.getAttendanceList().size(); i++) {
			DailyAttendanceForm dailyForm = attendanceForm.getAttendanceList().get(i);
			
			if (dailyForm.getNote() != null && dailyForm.getNote().length() > 100) {
				if (!isMaxlengthRecorded) {
					result.reject("maxlength", new Object[] { "備考", "100" }, "備考は100文字以内で入力してください。");
					isMaxlengthRecorded = true;
				}
				continue;
			}
			
			Integer startHour = dailyForm.getTrainingStartTimeHour();
			Integer startMin = dailyForm.getTrainingStartTimeMinute();
			Integer endHour = dailyForm.getTrainingEndTimeHour();
			Integer endMin = dailyForm.getTrainingEndTimeMinute();
			
			boolean isStartInvalid = (startHour != null && startMin == null) || (startHour == null && startMin != null);
			boolean isEndInvalid = (endHour != null && endMin == null) || (endHour == null && endMin != null);
			
			if (isStartInvalid) {
				if (!isStartTimeInvalidRecorded) {
					result.reject("attendance.startTime.invalid", "出勤時間が正しく入力されていません。");
					isStartTimeInvalidRecorded = true;
				}
				continue;
			}
			if (isEndInvalid) {
				if (!isEndTimeInvalidRecorded) {
					result.reject("attendance.endTime.invalid", "退勤時間が正しく入力されていません。");
					isEndTimeInvalidRecorded = true;
				}
				continue;
			}
			
			boolean isStartEmpty = (startHour == null && startMin == null);
			boolean isEndExist = (endHour != null || endMin != null);
			
			if (isStartEmpty && isEndExist) {
				if (!isPunchInEmptyRecorded) {
					result.reject("attendance.punchInEmpty", "出勤時間が入力されていません。");
					isPunchInEmptyRecorded = true;
				}
				continue;
			}
			
			if (!isStartEmpty && (endHour != null && endMin != null)) {
				TrainingTime trainingStartTime = new TrainingTime(String.format("%02d:%02d", startHour, startMin));
				TrainingTime trainingEndTime = new TrainingTime(String.format("%02d:%02d", endHour, endMin));
				
				if (trainingStartTime.compareTo(trainingEndTime) > 0) {
					if (!isRangeRecorded) {
						result.reject("attendance.trainingTimerRange", new Object[] { String.valueOf(i + 1) }, "退勤時間は出勤時間より後に入力してください。");
						isRangeRecorded = true;
					}
					continue;
				}
				if (dailyForm.getBlankTime() != null) {
					TrainingTime blankTime = attendanceUtil.calcBlankTime(dailyForm.getBlankTime());
					if (blankTime != null && trainingEndTime.subtract(trainingStartTime).compareTo(blankTime) < 0) {
						if (!isBlankTimeRecorded) {
							result.reject("attendance.blankTimeError", "中抜け時間が長すぎます。");
							isBlankTimeRecorded = true;
						}
						continue;
					}
				}
			}
		}
	}
}