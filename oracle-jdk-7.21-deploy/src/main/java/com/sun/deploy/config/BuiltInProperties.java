package com.sun.deploy.config;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class BuiltInProperties
{
  public static final String JRE_EXPIRATION_DATE = "07/18/2013";
  public static final String BASELINE_VERSION_131 = "1.3.1_21";
  public static final String BASELINE_VERSION_142 = "1.4.2_43";
  public static final String BASELINE_VERSION_150 = "1.5.0_45";
  public static final String BASELINE_VERSION_160 = "1.6.0_45";
  public static final String BASELINE_VERSION_170 = "1.7.0_21";
  public static final String BASELINE_VERSION_180 = "1.8.0";
  public static final String CURRENT_VERSION = "1.7.0_21";
  public static final String CURRENT_NODOT_VERSION = "170";
  public static final String DEPLOY_VERSION = "10.21.2.11";
  public static final String DEPLOY_NOBUILD_VERSION = "10.21.2";
  public static final String DEPLOY_NODOT_VERSION = "10212";
  public static final String JAVAWS_NAME = "javaws-10.21.2.11";
  public static final String JAVAWS_VERSION = "10.21.2.11";
  public static final long expirationTime = l;

  static
  {
    long l = 0L;
    try
    {
      DateFormat localDateFormat = DateFormat.getDateInstance(3, Locale.US);
      l = localDateFormat.parse("07/18/2013").getTime();
    }
    catch (Exception localException)
    {
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.config.BuiltInProperties
 * JD-Core Version:    0.6.2
 */