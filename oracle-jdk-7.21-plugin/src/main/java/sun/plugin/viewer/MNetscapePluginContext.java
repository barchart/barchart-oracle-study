package sun.plugin.viewer;

import com.sun.deploy.perf.DeployPerfUtil;

public class MNetscapePluginContext
{
  private static String PLUGIN_UNIQUE_ID = "A8F70EB5-AAEF-11d6-95A4-0050BAAC8BD3";

  public static MNetscapePluginObject createPluginObject(boolean paramBoolean, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt)
  {
    DeployPerfUtil.put("START - Java   - ENV - create browser plugin object (Mozilla:Unix)");
    MNetscapePluginObject localMNetscapePluginObject = new MNetscapePluginObject(paramInt, paramBoolean, LifeCycleManager.getIdentifier(paramArrayOfString1, paramArrayOfString2));
    for (int i = 0; i < paramArrayOfString1.length; i++)
      if ((paramArrayOfString1[i] != null) && (!PLUGIN_UNIQUE_ID.equals(paramArrayOfString1[i])))
        localMNetscapePluginObject.setParameter(paramArrayOfString1[i], paramArrayOfString2[i]);
    localMNetscapePluginObject.setBoxColors();
    DeployPerfUtil.put("END   - Java   - ENV - create browser plugin object (Mozilla:Unix)");
    return localMNetscapePluginObject;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.viewer.MNetscapePluginContext
 * JD-Core Version:    0.6.2
 */