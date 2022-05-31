package io.github.vveird.stream.deck.device.hid4java;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.event.HidServicesEvent;

import io.github.vveird.stream.deck.device.general.IStreamDeck;
import io.github.vveird.stream.deck.device.general.SoftStreamDeck;
import io.github.vveird.stream.deck.device.general.StreamDeck;
import io.github.vveird.stream.deck.device.hid4java.listener.StreamDeckAttachListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(StreamDeckDevices.class);

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
			if (hidDevice.getVendorId() == VENDOR_ID && hidDevice.getProductId() == PRODUCT_ID) {
				HidDeviceWrapper hdw = new HidDeviceWrapper(hidDevice);
				if(hdw.isOpen())
					connectedDecks.put(hidDevice, hdw);
				else
					LOGGER.error(String.format("Could not open device [Vendor=%d,ProductId=%d,Path=%s]", hidDevice.getVendorId(), hidDevice.getProductId(), hidDevice.getPath()));
			}
				
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				deinit();
			}
		});
	}
	private static void deinit() {
		for (HidDeviceWrapper hidW : connectedDecks.values()) {
			if (hidW.isOpen()) {
				StreamDeck sd = new StreamDeck(hidW, 0, 15);
				sd.reset();
				sd.setBrightness(0);
				sd.waitForCompletion();
				hidW.close();
			}
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
	
	public static List<IStreamDeck> getStreamDecks() {
		return connectedDecks.values().stream().map(h -> (IStreamDeck)new SoftStreamDeck(h.getId() , new StreamDeck(h, 90, 15), enableSoftwareStreamDeck)).collect(Collectors.toList());
	}
	
	public static IStreamDeck getStreamDeck() {
		HidDeviceWrapper hidW = connectedDecks.values().stream().findFirst().orElse(null);
		return hidW != null ? new SoftStreamDeck(hidW.getId(), new StreamDeck(hidW, 90, 15), enableSoftwareStreamDeck) : null;
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

	public static void enableSoftwareStreamDeck() {
		enableSoftwareStreamDeck = true;
	}

	public static void disableSoftwareStreamDeck() {
		enableSoftwareStreamDeck = false;
	}

}