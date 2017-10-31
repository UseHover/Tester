package com.hover.tester.schedules;

public interface SchedulerInterface {
	void addSchedule(int actionId);
	void setType(int schedule);
	void chooseTime(int day);
	void setTime(int hour, int min);
	void saveSchedule();
}
