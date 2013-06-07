package java.text.spi;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class BreakIteratorProvider extends LocaleServiceProvider
{
  public abstract BreakIterator getWordInstance(Locale paramLocale);

  public abstract BreakIterator getLineInstance(Locale paramLocale);

  public abstract BreakIterator getCharacterInstance(Locale paramLocale);

  public abstract BreakIterator getSentenceInstance(Locale paramLocale);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.text.spi.BreakIteratorProvider
 * JD-Core Version:    0.6.2
 */