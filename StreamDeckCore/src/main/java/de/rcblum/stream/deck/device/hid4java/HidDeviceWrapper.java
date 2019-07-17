package de.rcblum.stream.deck.device.hid4java;

import java.util.LinkedList;
import java.util.List;

import org.hid4java.HidDevice;

import de.rcblum.stream.deck.device.hid4java.listener.InputReportListener;

public class HidDeviceWrapper  {

	private HidDevice device = null;
	
	private List<InputReportListener> reportListener = new LinkedList<InputReportListener>();
	
	private Thread inputReportThread = null;

	public HidDeviceWrapper(HidDevice device) {
		super();
		this.device = device;
		if (!this.device.isOpen())
			this.device.open();
		if (this.device.isOpen()) {
			inputReportThread = new Thread(new InputReportPuller());
			inputReportThread.setDaemon(true);
			inputReportThread.start();
		}
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
			if (inputReportThread.isAlive())
				inputReportThread.interrupt();
			inputReportThread = new Thread(new InputReportPuller());
		}
		return op;
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
			while (running) {
				if ( HidDeviceWrapper.this.device.isOpen()) {
					int read = HidDeviceWrapper.this.device.getFeatureReport(data, (byte) 0x01);
					for (InputReportListener inputReportListener : reportListener) {
						inputReportListener.onInputReport(device, (byte) 0x01, data, read);
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
