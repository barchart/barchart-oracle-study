package sun.awt;

import java.awt.KeyboardFocusManager;
import java.awt.peer.KeyboardFocusManagerPeer;

public abstract interface KeyboardFocusManagerPeerProvider
{
  public abstract KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager paramKeyboardFocusManager);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.KeyboardFocusManagerPeerProvider
 * JD-Core Version:    0.6.2
 */