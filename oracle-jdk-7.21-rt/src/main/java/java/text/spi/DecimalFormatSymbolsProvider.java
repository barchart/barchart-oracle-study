package java.text.spi;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DecimalFormatSymbolsProvider extends LocaleServiceProvider
{
  public abstract DecimalFormatSymbols getInstance(Locale paramLocale);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.text.spi.DecimalFormatSymbolsProvider
 * JD-Core Version:    0.6.2
 */