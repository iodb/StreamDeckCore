package de.rcblum.stream.deck.device.hid4java.listener;

import org.hid4java.HidDevice;

public interface InputReportListener {
	public void onInputReport(HidDevice source, byte reportID, byte[] reportData, int reportLength);
}
