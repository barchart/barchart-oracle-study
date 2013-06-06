package com.sun.deploy.security;

import java.net.PasswordAuthentication;
import java.net.URL;

public abstract interface BrowserAuthenticator
{
  public abstract PasswordAuthentication getAuthentication(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, URL paramURL, boolean paramBoolean);
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.BrowserAuthenticator
 * JD-Core Version:    0.6.2
 */