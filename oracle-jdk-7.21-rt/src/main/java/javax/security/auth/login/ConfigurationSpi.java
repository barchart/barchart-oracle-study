package javax.security.auth.login;

public abstract class ConfigurationSpi
{
  protected abstract AppConfigurationEntry[] engineGetAppConfigurationEntry(String paramString);

  protected void engineRefresh()
  {
  }
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.login.ConfigurationSpi
 * JD-Core Version:    0.6.2
 */