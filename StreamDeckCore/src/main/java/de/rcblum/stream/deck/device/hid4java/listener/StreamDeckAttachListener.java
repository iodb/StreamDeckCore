package de.rcblum.stream.deck.device.hid4java.listener;

import de.rcblum.stream.deck.device.hid4java.HidDeviceWrapper;

public interface StreamDeckAttachListener {
	
	public void streamDeckAttached(HidDeviceWrapper dev);
	
	public void streamDeckDetached(HidDeviceWrapper dev);

}
