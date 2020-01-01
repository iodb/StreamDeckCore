package io.github.vveird.stream.deck.device.hid4java.listener;

import io.github.vveird.stream.deck.device.hid4java.HidDeviceWrapper;

public interface StreamDeckAttachListener {
	
	public void streamDeckAttached(HidDeviceWrapper dev);
	
	public void streamDeckDetached(HidDeviceWrapper dev);

}
