package java.util.prefs;

import java.util.EventListener;

public abstract interface PreferenceChangeListener extends EventListener
{
  public abstract void preferenceChange(PreferenceChangeEvent paramPreferenceChangeEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.prefs.PreferenceChangeListener
 * JD-Core Version:    0.6.2
 */