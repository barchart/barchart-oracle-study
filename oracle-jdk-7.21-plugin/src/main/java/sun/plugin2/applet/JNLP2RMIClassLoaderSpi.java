package sun.plugin2.applet;

import com.sun.deploy.util.DeployRMIClassLoaderSpi;

public final class JNLP2RMIClassLoaderSpi extends DeployRMIClassLoaderSpi
{
  protected boolean useRMIServerCodebaseForClass(Class paramClass)
  {
    return (paramClass != null) && ((paramClass.getClassLoader() instanceof JNLP2ClassLoader));
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.JNLP2RMIClassLoaderSpi
 * JD-Core Version:    0.6.2
 */