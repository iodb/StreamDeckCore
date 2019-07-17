package de.rcblum.stream.deck.device.hid4java;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.event.HidServicesEvent;

import de.rcblum.stream.deck.device.hid4java.listener.StreamDeckAttachListener;

/**
 * 
 * <br>
 * <br>
 * 
 * MIT License
 * 
 * Copyright (c) 2017 Roland von Werden
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * @author Roland von Werden
 * @version 1.0.0
 *
 */
public class StreamDeckDevices implements HidServicesListener {

	/**
	 * Flag for enabling the software stream deck GUI. <code>true</code> Stream Deck
	 * devices will be wrapped in a software SD, <code>false</code> the StreamDeck
	 * will be returned directly.
	 */
	private static boolean enableSoftwareStreamDeck = true;

	private static final Logger LOGGER = LogManager.getLogger(StreamDeckDevices.class);

	public static final short VENDOR_ID = 4057;

	public static final short PRODUCT_ID = 96;

	private static Map<HidDevice, HidDeviceWrapper> connectedDecks = new HashMap<>();

	private static List<StreamDeckAttachListener> listeners = new ArrayList<>(5);
	
	static {
		init();
	}

	private static void init() {
		// Get HID services
		HidServices hidServices = HidManager.getHidServices();
		hidServices.addHidServicesListener(new StreamDeckDevices());
		for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
			if (hidDevice.getVendorId() == VENDOR_ID && hidDevice.getProductId() == PRODUCT_ID) 
				connectedDecks.put(hidDevice, new HidDeviceWrapper(hidDevice));
		}
	}

	@Override
	public void hidDeviceAttached(HidServicesEvent arg0) {
		if (arg0.getHidDevice().getVendorId() == VENDOR_ID && arg0.getHidDevice().getProductId() == PRODUCT_ID) {
			HidDevice hid = arg0.getHidDevice();
			HidDeviceWrapper hidw = new HidDeviceWrapper(hid);
			connectedDecks.put(hid, hidw);
			fireStreamDeckAttached(hidw);
		}
	}

	@Override
	public void hidDeviceDetached(HidServicesEvent arg0) {
		if (arg0.getHidDevice().getVendorId() == VENDOR_ID && arg0.getHidDevice().getProductId() == PRODUCT_ID) {
			HidDevice hid = arg0.getHidDevice();
			HidDeviceWrapper hidw = connectedDecks.get(hid);
			fireStreamDeckDetached(hidw != null ? hidw : new HidDeviceWrapper(hid));
		}

	}
	
	public static List<HidDeviceWrapper> getStreamDecks() {
		return new ArrayList<>(connectedDecks.values());
	}

	@Override
	public void hidFailure(HidServicesEvent arg0) {
		if (arg0.getHidDevice().getVendorId() == VENDOR_ID && arg0.getHidDevice().getProductId() == PRODUCT_ID) {
			HidDevice hid = arg0.getHidDevice();
			HidDeviceWrapper hidw = connectedDecks.get(hid);
			fireStreamDeckDetached(hidw != null ? hidw : new HidDeviceWrapper(hid));
		}
	}

	private static void fireStreamDeckAttached(HidDeviceWrapper hidDev) {
		listeners.stream().forEach(l -> l.streamDeckAttached(hidDev));
	}

	private static void fireStreamDeckDetached(HidDeviceWrapper hidDev) {
		listeners.stream().forEach(l -> l.streamDeckDetached(hidDev));
	}

}