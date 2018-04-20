package com.hover.tester.main;

public interface GatewayIntegrationInterface {
	void savePin(final String pin);
	void pickAction(int serviceId);
	void addAction(int serviceId, int actionIdx);
}
