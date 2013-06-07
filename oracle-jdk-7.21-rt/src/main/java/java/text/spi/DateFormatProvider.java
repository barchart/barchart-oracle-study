package java.text.spi;

import java.text.DateFormat;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DateFormatProvider extends LocaleServiceProvider
{
  public abstract DateFormat getTimeInstance(int paramInt, Locale paramLocale);

  public abstract DateFormat getDateInstance(int paramInt, Locale paramLocale);

  public abstract DateFormat getDateTimeInstance(int paramInt1, int paramInt2, Locale paramLocale);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.text.spi.DateFormatProvider
 * JD-Core Version:    0.6.2
 */