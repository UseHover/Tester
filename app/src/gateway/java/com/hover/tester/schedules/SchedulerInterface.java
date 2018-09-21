package com.hover.tester.schedules;

public interface SchedulerInterface {
	void addSchedule(String actionId);
	void setType(int schedule);
	void chooseTime(int day);
	void setTime(int hour, int min);
	void saveSchedule();
}
