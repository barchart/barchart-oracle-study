package com.sun.deploy.panel;

import com.sun.deploy.config.Config;
import com.sun.deploy.config.Platform;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

class AdvancedProperties
{
  private static EventListenerList newPluginEnabledListeners = new EventListenerList();

  static void addStatusChangedListener(NewPluginEnableListener paramNewPluginEnableListener)
  {
    newPluginEnabledListeners.add(NewPluginEnableListener.class, paramNewPluginEnableListener);
  }

  static void removeStatusChangedListener(NewPluginEnableListener paramNewPluginEnableListener)
  {
    newPluginEnabledListeners.remove(NewPluginEnableListener.class, paramNewPluginEnableListener);
  }

  private static void fireEvent(NewPluginEnableEvent paramNewPluginEnableEvent)
  {
    Object[] arrayOfObject = newPluginEnabledListeners.getListenerList();
    for (int i = 0; i < arrayOfObject.length; i += 2)
      if (arrayOfObject[i] == NewPluginEnableListener.class)
        ((NewPluginEnableListener)arrayOfObject[(i + 1)]).newPluginEnableChanged(paramNewPluginEnableEvent);
  }

  static String getOldPluginSliderDescription()
  {
    return ResourceManager.getString("deployment.security.slider.oldplugin");
  }

  static NewPluginEnableManager prepareToSaveNewPluginEnabled()
  {
    boolean bool1 = Platform.get().getJavaPluginSettings();
    Config.get().storeIfNeeded();
    boolean bool2 = Config.getBooleanProperty("deployment.jpi.mode.new");
    NewPluginEnableManager localNewPluginEnableManager = new NewPluginEnableManager(bool1, bool2);
    return localNewPluginEnableManager;
  }

  static class NewPluginEnableEvent
  {
    boolean enabled;

    public NewPluginEnableEvent(boolean paramBoolean)
    {
      this.enabled = paramBoolean;
    }

    public boolean isEnabled()
    {
      return this.enabled;
    }
  }

  static abstract interface NewPluginEnableListener extends EventListener
  {
    public abstract void newPluginEnableChanged(AdvancedProperties.NewPluginEnableEvent paramNewPluginEnableEvent);
  }

  static class NewPluginEnableManager
  {
    private final boolean previousState;
    private final boolean desiredState;
    private boolean isDialogShown;

    public NewPluginEnableManager(boolean paramBoolean1, boolean paramBoolean2)
    {
      this.previousState = paramBoolean1;
      this.desiredState = paramBoolean2;
      this.isDialogShown = false;
    }

    public boolean isDialogShown()
    {
      return this.isDialogShown;
    }

    public void saveIfChanging(boolean paramBoolean)
    {
      if (this.desiredState != this.previousState)
      {
        int i = Platform.get().setJavaPluginSettings(this.desiredState);
        if (!paramBoolean)
        {
          if (i == 1)
            showPluginSwitchFailDialog();
          else if (i != 2)
            showPluginSwitchSuccessDialog();
          this.isDialogShown = true;
        }
        AdvancedProperties.fireEvent(new AdvancedProperties.NewPluginEnableEvent(this.desiredState));
      }
    }

    private void showPluginSwitchFailDialog()
    {
      String str1 = ResourceManager.getString("common.ok_btn");
      String str2 = ResourceManager.getString("common.detail.button");
      ToolkitStore.getUI().showMessageDialog(null, null, 0, ResourceManager.getMessage("jpi.settings.fail.caption"), ResourceManager.getMessage("jpi.settings.fail.masthead"), ResourceManager.getMessage("jpi.settings.fail.text"), null, str1, str2, null);
    }

    private void showPluginSwitchSuccessDialog()
    {
      ToolkitStore.getUI().showMessageDialog(null, null, 1, ResourceManager.getMessage("jpi.settings.success.caption"), ResourceManager.getMessage("jpi.settings.success.masthead"), ResourceManager.getMessage("jpi.settings.success.text"), null, null, null, null);
    }
  }

  static class NewPluginOptionListener
    implements TreeModelListener
  {
    public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
    {
      TreePath localTreePath = paramTreeModelEvent.getTreePath();
      Object localObject = localTreePath.getLastPathComponent();
      if ((localObject instanceof IProperty))
      {
        IProperty localIProperty = (IProperty)localObject;
        if ((localIProperty.getPropertyName().equals("deployment.jpi.mode.new")) && (localIProperty.getValue().equals("false")) && (showNewPluginDisabledWarning() != 0))
          localIProperty.setValue("true");
      }
    }

    public void treeNodesInserted(TreeModelEvent paramTreeModelEvent)
    {
    }

    public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent)
    {
    }

    public void treeStructureChanged(TreeModelEvent paramTreeModelEvent)
    {
    }

    private static int showNewPluginDisabledWarning()
    {
      return ToolkitStore.getUI().showMessageDialog(null, null, 2, ResourceManager.getString("security.dialog.caption"), ResourceManager.getString("deployment.securiry.oldplugin.warning.masthead"), ResourceManager.getString("deployment.securiry.oldplugin.warning.message"), null, ResourceManager.getString("deployment.securiry.oldplugin.warning.button.disable"), ResourceManager.getString("deployment.securiry.oldplugin.warning.button.cancel"), null, null, null, 1);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.AdvancedProperties
 * JD-Core Version:    0.6.2
 */