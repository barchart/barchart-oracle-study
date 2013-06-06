package com.sun.deploy.net.protocol;

public abstract interface ProtocolType
{
  public static final int HTTP = 1;
  public static final int HTTPS = 2;
  public static final int FTP = 4;
  public static final int GOPHER = 8;
  public static final int SOCKS = 16;
  public static final int JAR = 32;
  public static final int JAVASCRIPT = 64;
  public static final int RMI = 128;
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.protocol.ProtocolType
 * JD-Core Version:    0.6.2
 */