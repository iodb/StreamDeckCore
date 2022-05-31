package test.de.rcblum.stream.deck;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.vveird.stream.deck.StreamDeckController;
import io.github.vveird.stream.deck.device.general.IStreamDeck;
import io.github.vveird.stream.deck.device.hid4java.StreamDeckDevices;
import io.github.vveird.stream.deck.items.ExecutableItem;
import io.github.vveird.stream.deck.items.FolderItem;
import io.github.vveird.stream.deck.items.StreamItem;
import io.github.vveird.stream.deck.items.animation.AnimationStack;
import io.github.vveird.stream.deck.util.IconHelper;
import io.github.vveird.stream.deck.util.IconPackage;
import io.github.vveird.stream.deck.util.SDImage;

public class TestAnimationStack {
	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
		System.setProperty("log4j.configurationFile", TestAnimationStack.class.getResource("/resources/log4j.xml").getFile());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		AnimationStack as = new AnimationStack(AnimationStack.REPEAT_LOOPING, true, AnimationStack.FRAME_RATE_30, AnimationStack.TRIGGER_PRESSED, new SDImage[0]);
		System.out.println(gson.toJson(as));
		IconHelper.createIconPackage("resources" + File.separator + "icon.zip", "resources" + File.separator + "icon.png", "resources" + File.separator + "icon.gif", as);
		IconPackage ip = IconHelper.loadIconPackage("resources" + File.separator + "icon.zip");
		StreamItem[] items = new StreamItem[15];
		ExecutableItem item0 = new ExecutableItem(ip, "cmd /c dir");
		ExecutableItem item1 = item0;
		ip = new IconPackage(ip.icon, IconHelper.createRollingTextAnimation(ip.icon, "Rolling Text test", StreamItem.TEXT_POS_BOTTOM));
		ExecutableItem item2 = new ExecutableItem(ip, "cmd /c dir");
		
		items[0] = item1;
		items[4] = item1;
		items[12] = item2;
		FolderItem root = new FolderItem(null, null, items);
		IStreamDeck sd = StreamDeckDevices.getStreamDeck();
		sd.reset();
		sd.setBrightness(90);
		StreamDeckController sdc = new StreamDeckController(sd, root);
		sdc.pressButton(0);
		sdc.pressButton(4);
		sdc.pressButton(7);
		sdc.pressButton(10);
		sdc.pressButton(14);
		try {
			Thread.sleep(15_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		item0.setText("Hello");
		sdc.releaseButton(0);
		sdc.releaseButton(4);
		sdc.releaseButton(7);
		sdc.releaseButton(10);
		sdc.releaseButton(14);
		try {
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		item0.setTextPosition(StreamItem.TEXT_POS_TOP);
		try {
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		sd.setBrightness(50);
		item0.setTextPosition(StreamItem.TEXT_POS_CENTER);
		sdc.pressButton(0);
		sdc.pressButton(4);
		sdc.pressButton(7);
		sdc.pressButton(10);
		sdc.pressButton(14);
		try {
			Thread.sleep(90_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		sdc.releaseButton(7);
		sd.reset();
		sd.setBrightness(0);
		sdc.stop(true, true);
		sd.waitForCompletion();
		sd.reset();
		System.exit(0);
	}
}
