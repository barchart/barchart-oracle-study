package sun.plugin.cache;

import com.sun.deploy.resources.ResourceManager;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class FileVersion
{
  private String strVersion;
  private long longVersion;
  private static final int VERSION_DIGITS = 4;
  private static final int VERSION_DIGITS_BITSIZE = 16;
  private static final int VERSION_DIGITS_BYTESIZE = 4;
  private static final int VERSION_DIGITS_RADIX = 16;
  public static final String defStrVersion = "x.x.x.x";
  public static final int defIntVersion = 0;
  public static final String regEx = "\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}";

  public FileVersion()
  {
    this.strVersion = "x.x.x.x";
    this.longVersion = 0L;
  }

  public FileVersion(String paramString)
    throws JarCacheVersionException
  {
    this.strVersion = paramString;
    this.longVersion = convertToLong(paramString);
  }

  public FileVersion(long paramLong)
  {
    this.strVersion = convertToString(paramLong);
    this.longVersion = paramLong;
  }

  public void setVersion(long paramLong)
  {
    if (paramLong > 0L)
    {
      this.longVersion = paramLong;
      this.strVersion = convertToString(paramLong);
    }
  }

  public void setVersion(String paramString)
    throws JarCacheVersionException
  {
    if (paramString != null)
    {
      this.strVersion = paramString;
      this.longVersion = convertToLong(paramString);
    }
  }

  public long getVersionAsLong()
  {
    return this.longVersion;
  }

  public String getVersionAsString()
  {
    return this.strVersion;
  }

  public boolean isUpToDate(FileVersion paramFileVersion)
  {
    return (!this.strVersion.equals("x.x.x.x")) && (this.longVersion >= paramFileVersion.longVersion);
  }

  public static long convertToLong(String paramString)
    throws JarCacheVersionException
  {
    long l = 0L;
    if (!Pattern.matches("\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}", paramString))
    {
      localObject = new MessageFormat(ResourceManager.getMessage("cache.version_format_error"));
      throw new JarCacheVersionException(((MessageFormat)localObject).format(new Object[] { paramString }));
    }
    Object localObject = new StringTokenizer(paramString, ".", false);
    while (((StringTokenizer)localObject).hasMoreTokens())
    {
      String str = ((StringTokenizer)localObject).nextToken().trim();
      l <<= 16;
      l += Integer.parseInt(str, 16);
    }
    return l;
  }

  public static String convertToString(long paramLong)
  {
    String str = "";
    long l1 = paramLong;
    for (int i = 0; i < 4; i++)
    {
      long l2 = l1 >> 48 & 0xFFFF;
      l1 <<= 16;
      str = str + Long.toString(l2, 16);
      str = str + (i != 3 ? "." : "");
    }
    return str;
  }

  public static String getMessage(String paramString)
  {
    return ResourceManager.getMessage(paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.cache.FileVersion
 * JD-Core Version:    0.6.2
 */