package com.sun.deploy.security;

import java.net.URL;
import sun.net.www.protocol.http.ntlm.NTLMAuthenticationCallback;

public class UnixDeployNTLMAuthCallback extends NTLMAuthenticationCallback
{
  public boolean isTrustedSite(URL paramURL)
  {
    return true;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.UnixDeployNTLMAuthCallback
 * JD-Core Version:    0.6.2
 */