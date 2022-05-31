package tutorial.de.rcblum.stream.deck;

import io.github.vveird.stream.deck.device.general.IStreamDeck;
import io.github.vveird.stream.deck.device.hid4java.StreamDeckDevices;
import io.github.vveird.stream.deck.event.KeyEvent;
import io.github.vveird.stream.deck.event.StreamKeyListener;

import java.io.IOException;

public class Example2_Receiving_key_events {
	public static void main(String[] args) throws IOException {
		// Get the first connected (or software) ESD:
		IStreamDeck streamDeck = StreamDeckDevices.getStreamDeck();
		// Init
		streamDeck.init();
		// Reset the ESD so we can display our icon on it:
		// streamDeck.reset();
		// Set the brightness to 75%
		streamDeck.setBrightness(75);
		// Add the Listener to the stream deck:
		streamDeck.addKeyListener(new ExampleListener());
		// Wait 30 seconds before shutting down
		try {
			Thread.sleep(30_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		// Reset the stream deck to display nothing
		streamDeck.reset();
		// Set the brightness to 0%
		streamDeck.setBrightness(0);
		// Tell the device to shutdown
		streamDeck.stop();
		// wait for the device to shutdown
		streamDeck.waitForCompletion();
	}
	
	public static class ExampleListener implements StreamKeyListener {
		public void onKeyEvent(KeyEvent event) {
			switch(event.getType()) {
			case OFF_DISPLAY :
				System.out.println(event.getKeyId() + ": taken off display");
				break;
			case ON_DISPLAY:
				System.out.println(event.getKeyId() + ": put on display");
				break;
			case PRESSED:
				System.out.println(event.getKeyId() + ": pressed");
				break;
			case RELEASED_CLICKED:
				System.out.println(event.getKeyId() + ": released/clicked");
				break;
			default:
				break;
			}
		}
	}
}
