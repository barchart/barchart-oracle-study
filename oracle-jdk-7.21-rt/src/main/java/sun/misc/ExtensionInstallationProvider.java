package sun.misc;

public abstract interface ExtensionInstallationProvider
{
  public abstract boolean installExtension(ExtensionInfo paramExtensionInfo1, ExtensionInfo paramExtensionInfo2)
    throws ExtensionInstallationException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.ExtensionInstallationProvider
 * JD-Core Version:    0.6.2
 */