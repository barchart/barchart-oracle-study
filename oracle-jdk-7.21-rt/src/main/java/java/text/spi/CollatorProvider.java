package java.text.spi;

import java.text.Collator;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class CollatorProvider extends LocaleServiceProvider
{
  public abstract Collator getInstance(Locale paramLocale);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.text.spi.CollatorProvider
 * JD-Core Version:    0.6.2
 */