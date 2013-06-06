package com.sun.jnlp;

import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.javaws.util.JNLPUtils;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.util.ArrayList;

public final class PreverificationClassLoader extends URLClassLoader
{
  private ArrayList _jarsInURLClassLoader = new ArrayList();
  private ArrayList _jarsNotInURLClassLoader = new ArrayList();

  public PreverificationClassLoader(ClassLoader paramClassLoader)
  {
    super(new URL[0], paramClassLoader);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
      localSecurityManager.checkCreateClassLoader();
  }

  protected PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
    localPermissionCollection.add(new AllPermission());
    return localPermissionCollection;
  }

  public void preverifyJARs()
  {
    if (!ResourceProvider.get().canCache(null))
      return;
    long l1 = System.currentTimeMillis();
    for (int i = 0; i < this._jarsInURLClassLoader.size(); i++)
    {
      JARDesc localJARDesc = (JARDesc)this._jarsInURLClassLoader.get(i);
      ResourceProvider.get().preverifyCachedJar(localJARDesc.getLocation(), localJARDesc.getVersion(), this);
    }
    long l2 = System.currentTimeMillis();
    Trace.println("PreverificationCL, Cached JAR preverification took (ms): " + (l2 - l1), TraceLevel.CACHE);
  }

  public void initialize(LaunchDesc paramLaunchDesc)
  {
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (localResourcesDesc != null)
    {
      JNLPUtils.sortResourcesForClasspath(localResourcesDesc, this._jarsInURLClassLoader, this._jarsNotInURLClassLoader);
      for (int i = 0; i < this._jarsInURLClassLoader.size(); i++)
      {
        JARDesc localJARDesc = (JARDesc)this._jarsInURLClassLoader.get(i);
        addURL(localJARDesc.getLocation());
      }
    }
  }

  private void addLoadedJarsEntry(JARDesc paramJARDesc)
  {
    if (!this._jarsInURLClassLoader.contains(paramJARDesc))
      this._jarsInURLClassLoader.add(paramJARDesc);
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.jnlp.PreverificationClassLoader
 * JD-Core Version:    0.6.2
 */