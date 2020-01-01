package io.github.vveird.stream.deck.items.animation;

import io.github.vveird.stream.deck.event.KeyEvent.Type;

public interface AnimationTrigger {

	default boolean isTriggered(Type keyEventType) {return false;}

}
