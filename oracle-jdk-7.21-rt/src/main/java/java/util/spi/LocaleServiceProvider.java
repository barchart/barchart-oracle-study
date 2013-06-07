package java.util.spi;

import java.util.Locale;

public abstract class LocaleServiceProvider
{
  public abstract Locale[] getAvailableLocales();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.spi.LocaleServiceProvider
 * JD-Core Version:    0.6.2
 */