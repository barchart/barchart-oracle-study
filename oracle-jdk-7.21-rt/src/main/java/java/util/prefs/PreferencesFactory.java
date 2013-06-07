package java.util.prefs;

public abstract interface PreferencesFactory
{
  public abstract Preferences systemRoot();

  public abstract Preferences userRoot();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.prefs.PreferencesFactory
 * JD-Core Version:    0.6.2
 */