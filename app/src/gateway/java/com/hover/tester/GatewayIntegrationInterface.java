package com.hover.tester;

public interface GatewayIntegrationInterface {
	void savePin(final String pin);
	void pickAction(int serviceId);
	void addAction(int serviceId, int actionIdx);
}
