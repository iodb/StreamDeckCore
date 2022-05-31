package test.de.rcblum.stream.deck;

import io.github.vveird.stream.deck.StreamDeckController;
import io.github.vveird.stream.deck.device.general.IStreamDeck;
import io.github.vveird.stream.deck.device.hid4java.StreamDeckDevices;
import io.github.vveird.stream.deck.items.ExecutableItem;
import io.github.vveird.stream.deck.items.FolderItem;
import io.github.vveird.stream.deck.items.StreamItem;
import io.github.vveird.stream.deck.util.IconHelper;
import io.github.vveird.stream.deck.util.SDImage;

import java.io.File;
import java.io.IOException;

public class TestFolderInFolder {
	public static void main(String[] args) throws IOException {
		System.setProperty("log4j.configurationFile", TestAnimationStack.class.getResource("/resources/log4j.xml").getFile());
		IStreamDeck sd = StreamDeckDevices.getStreamDeck();
		sd.init();
		sd.reset();
		sd.setBrightness(50);
		// Level 2
		StreamItem[] items = new StreamItem[15];
		for (int i = 0; i < items.length; i++) {
			SDImage icon = IconHelper.loadImage("resources" + File.separator + "icon" + (i+1) + ".png");
			ExecutableItem eI = new ExecutableItem(icon, "uplay://launch/635/0");
			items[i] = eI;
		}
		FolderItem dir = new FolderItem("Folder Level 2", null, items);
		
		// Level 1
		items = new StreamItem[15];
		items[0] = dir;
		for (int i = 1; i < items.length; i++) {
			SDImage icon = IconHelper.loadImage("resources" + File.separator + "icon" + (i+1) + ".png");
			ExecutableItem eI = new ExecutableItem(icon, "uplay://launch/635/0");
			items[i] = eI;
		}
		dir = new FolderItem("Folder Level 1", null, items);
		// Root
		StreamItem[] rootDirs = new StreamItem[15];
		rootDirs[0] = dir;
		rootDirs[4] = dir;
		rootDirs[6] = dir;
		rootDirs[8] = dir;
		rootDirs[12] = dir;
		FolderItem root = new FolderItem(null, null, rootDirs);
		
		StreamDeckController controller = new StreamDeckController(sd, root);
		try {
			Thread.sleep(60_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		controller.stop(true, true);
		System.exit(0);
	}
}
