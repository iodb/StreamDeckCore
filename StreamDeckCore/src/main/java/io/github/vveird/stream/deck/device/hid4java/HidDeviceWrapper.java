package io.github.vveird.stream.deck.device.hid4java;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hid4java.HidDevice;

import io.github.vveird.stream.deck.device.hid4java.listener.InputReportListener;
/**
 * MIT License
 * 
 * Copyright (c) 2017-2019 Roland von Werden
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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
 *
 */
public class HidDeviceWrapper {
	
	private static final Logger LOGGER = LogManager.getLogger(HidDeviceWrapper.class);

	private HidDevice device = null;
	
	private List<InputReportListener> reportListener = new LinkedList<InputReportListener>();
	
	private Thread inputReportThread = null;

	public HidDeviceWrapper(HidDevice device) {
		super();
		this.device = device;
		init();
	}
	

	public boolean init() {
		if(!this.isOpen()) {
			boolean r = this.open();
			inputReportThread = new Thread(new InputReportPuller());
			inputReportThread.setDaemon(true);
			inputReportThread.start();
		}
		return this.isOpen();
	}
	
	
	public int getFeatureReport(byte[] data, byte reportId) {
		// TODO Auto-generated method stub
		return device.getFeatureReport(data, reportId);
	}

	
	public String getId() {
		// TODO Auto-generated method stub
		return device.getId();
	}

	
	public String getIndexedString(int index) {
		// TODO Auto-generated method stub
		return device.getIndexedString(index);
	}

	
	public int getInterfaceNumber() {
		// TODO Auto-generated method stub
		return device.getInterfaceNumber();
	}

	
	public String getLastErrorMessage() {
		// TODO Auto-generated method stub
		return device.getLastErrorMessage();
	}

	
	public String getManufacturer() {
		// TODO Auto-generated method stub
		return device.getManufacturer();
	}

	
	public String getPath() {
		// TODO Auto-generated method stub
		return device.getPath();
	}

	
	public String getProduct() {
		// TODO Auto-generated method stub
		return device.getProduct();
	}

	
	public short getProductId() {
		// TODO Auto-generated method stub
		return device.getProductId();
	}

	
	public int getReleaseNumber() {
		// TODO Auto-generated method stub
		return device.getReleaseNumber();
	}

	
	public String getSerialNumber() {
		// TODO Auto-generated method stub
		return device.getSerialNumber();
	}

	
	public int getUsage() {
		// TODO Auto-generated method stub
		return device.getUsage();
	}

	
	public int getUsagePage() {
		// TODO Auto-generated method stub
		return device.getUsagePage();
	}

	
	public short getVendorId() {
		// TODO Auto-generated method stub
		return device.getVendorId();
	}

	
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return device.isOpen();
	}

	
	public boolean isVidPidSerial(int vendorId, int productId, String serialNumber) {
		// TODO Auto-generated method stub
		return device.isVidPidSerial(vendorId, productId, serialNumber);
	}

	
	public boolean open() {
		// TODO Auto-generated method stub
		boolean op = device.open();
		if(op) {
			if (inputReportThread != null && inputReportThread.isAlive())
				inputReportThread.interrupt();
			inputReportThread = new Thread(new InputReportPuller());
		}
		return op;
	}
	
	public void close() {
		device.close();
	}

	
	public int read(byte[] bytes, int timeoutMillis) {
		// TODO Auto-generated method stub
		return device.read(bytes, timeoutMillis);
	}

	
	public int read(byte[] data) {
		// TODO Auto-generated method stub
		return device.read(data);
	}

	
	public int sendFeatureReport(byte[] data, byte reportId) {
		// TODO Auto-generated method stub
		return device.sendFeatureReport(data, reportId);
	}

	
	public void setNonBlocking(boolean nonBlocking) {
		// TODO Auto-generated method stub
		device.setNonBlocking(nonBlocking);
	}

	
	public String toString() {
		// TODO Auto-generated method stub
		return device.toString();
	}
	
	public int write(byte[] message, int packetLength, byte reportId) {
		// TODO Auto-generated method stub
		return device.write(message, packetLength, reportId);
	}
	
	public void addInputReportListener(InputReportListener listener) {
		this.reportListener.add(listener);
	}
	
	public void removeInputReportListener(InputReportListener listener) {
		this.reportListener.remove(listener);
	}
	
	private class InputReportPuller implements Runnable {
		
		boolean running = true;

		@Override
		public void run() {
			byte[] data = new byte[10_000];
			byte[] report = new byte[15];
			while (running) {
				if (HidDeviceWrapper.this.device.isOpen()) {
					int read = HidDeviceWrapper.this.device.read(data);
					if (read > 0) {
						byte repNo = data[0];
						System.arraycopy(data, 1, report, 0, 15);
						System.out.println("Report Data: " + Arrays.toString(report));
						for (InputReportListener inputReportListener : reportListener) {
							inputReportListener.onInputReport(device, repNo, report, read);
						}
					}
				}
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		
	}
}
