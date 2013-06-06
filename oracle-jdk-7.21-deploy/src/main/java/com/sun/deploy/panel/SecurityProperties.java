package com.sun.deploy.panel;

import com.sun.deploy.config.Platform;
import com.sun.deploy.config.Platform.WebJavaSwitch;
import com.sun.deploy.config.Platform.WebJavaSwitch.WebJavaState;
import com.sun.deploy.config.SecuritySettings;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.ui.UIFactory;
import java.util.EventListener;
import java.util.Properties;
import javax.swing.event.EventListenerList;

class SecurityProperties
{
  private static EventListenerList javaEnabledListeners = new EventListenerList();
  private static Boolean enableJavaInBrowserDesiredState = null;
  private static SecurityLevel currentSliderSecuritySetting = null;
  private static SecurityLevel proposedSliderSecuritySetting = null;
  private static final Properties properties = new Properties();

  static void addJavaEnableListener(JavaEnableListener paramJavaEnableListener)
  {
    javaEnabledListeners.add(JavaEnableListener.class, paramJavaEnableListener);
  }

  static void removeMyEventListener(JavaEnableListener paramJavaEnableListener)
  {
    javaEnabledListeners.remove(JavaEnableListener.class, paramJavaEnableListener);
  }

  private static void fireMyEvent(JavaEnableEvent paramJavaEnableEvent)
  {
    Object[] arrayOfObject = javaEnabledListeners.getListenerList();
    for (int i = 0; i < arrayOfObject.length; i += 2)
      if (arrayOfObject[i] == JavaEnableListener.class)
        ((JavaEnableListener)arrayOfObject[(i + 1)]).javaEnableChanged(paramJavaEnableEvent);
  }

  static void enableJavaInBrowser(boolean paramBoolean)
  {
    enableJavaInBrowserDesiredState = Boolean.valueOf(paramBoolean);
    ControlPanel.propertyHasChanged();
  }

  public static void saveLevelProperites()
  {
    if (proposedSliderSecuritySetting != null)
    {
      currentSliderSecuritySetting = proposedSliderSecuritySetting;
      proposedSliderSecuritySetting = null;
      SecuritySettings.setSecurityLevel(currentSliderSecuritySetting.getConfigKey());
    }
  }

  public static JavaEnableManager prepareToSaveJavaEnabled()
  {
    Platform.WebJavaSwitch localWebJavaSwitch = Platform.get().getWebJavaSwitch();
    Platform.WebJavaSwitch.WebJavaState localWebJavaState = localWebJavaSwitch.queryWebJavaState();
    JavaEnableManager localJavaEnableManager = new JavaEnableManager(localWebJavaState, enableJavaInBrowserDesiredState);
    enableJavaInBrowserDesiredState = null;
    return localJavaEnableManager;
  }

  static boolean isJavaInBrowserEnabled()
  {
    boolean bool = Platform.get().getWebJavaSwitch().isWebJavaEnabled();
    return bool;
  }

  static boolean isJavaInBrowserDisabledForUser()
  {
    return Platform.get().getWebJavaSwitch().queryWebJavaState() == Platform.WebJavaSwitch.WebJavaState.USER_DISABLED;
  }

  static SecurityLevel getProposedSecurityLevel()
  {
    if (proposedSliderSecuritySetting != null)
      return proposedSliderSecuritySetting;
    return getCurrentSecurityLevel();
  }

  static SecurityLevel getCurrentSecurityLevel()
  {
    if (currentSliderSecuritySetting == null)
    {
      String str = SecuritySettings.getSecurityLevel();
      currentSliderSecuritySetting = SecurityLevel.getLevel(str);
    }
    return currentSliderSecuritySetting;
  }

  static void setProposedSecurityLevel(int paramInt)
  {
    proposedSliderSecuritySetting = SecurityLevel.getSliderSetting(paramInt);
    if (proposedSliderSecuritySetting != currentSliderSecuritySetting)
      ControlPanel.propertyHasChanged();
  }

  static class JavaEnableEvent
  {
    boolean enabled;

    public JavaEnableEvent(boolean paramBoolean)
    {
      this.enabled = paramBoolean;
    }

    public boolean isEnabled()
    {
      return this.enabled;
    }
  }

  static abstract interface JavaEnableListener extends EventListener
  {
    public abstract void javaEnableChanged(SecurityProperties.JavaEnableEvent paramJavaEnableEvent);
  }

  static class JavaEnableManager
  {
    private final Platform.WebJavaSwitch.WebJavaState previousState;
    private final Boolean desiredState;
    final Platform.WebJavaSwitch webJavaSwitch = Platform.get().getWebJavaSwitch();

    JavaEnableManager(Platform.WebJavaSwitch.WebJavaState paramWebJavaState, Boolean paramBoolean)
    {
      this.previousState = paramWebJavaState;
      this.desiredState = paramBoolean;
    }

    public boolean isJavaEnableChanging()
    {
      if (this.desiredState != null);
      return this.desiredState.booleanValue() != (this.previousState == Platform.WebJavaSwitch.WebJavaState.ENABLED);
    }

    public void saveIfEnabling()
    {
      if (isJavaBeingEnabled())
        doSave();
    }

    public void saveIfDisabling()
    {
      if (isJavaBeingDisabled())
        doSave();
    }

    private void showPluginChangedDialog()
    {
      UIFactory.showInformationDialog(null, getMessage("deployment.java.change.success.masthead"), getMessage("deployment.java.change.success.message"), getMessage("deployment.java.change.success.title"));
    }

    private void showDisabledForUserDialog()
    {
      UIFactory.showInformationDialog(null, getMessage("deployment.java.change.useronly.masthead"), getMessage("deployment.java.change.useronly.message"), getMessage("deployment.java.change.useronly.title"));
    }

    private String getMessage(String paramString)
    {
      return ResourceManager.getMessage(paramString);
    }

    private void showAppropriateDialog()
    {
      Platform.WebJavaSwitch.WebJavaState localWebJavaState = this.webJavaSwitch.queryWebJavaState();
      if (this.previousState == Platform.WebJavaSwitch.WebJavaState.ENABLED)
      {
        if (localWebJavaState == Platform.WebJavaSwitch.WebJavaState.DISABLED)
          showPluginChangedDialog();
        else if (localWebJavaState == Platform.WebJavaSwitch.WebJavaState.USER_DISABLED)
          showDisabledForUserDialog();
      }
      else if ((this.previousState == Platform.WebJavaSwitch.WebJavaState.DISABLED) && (localWebJavaState == Platform.WebJavaSwitch.WebJavaState.ENABLED))
        showPluginChangedDialog();
    }

    private boolean isJavaBeingEnabled()
    {
      return (isJavaEnableChanging()) && (this.desiredState != null) && (this.desiredState.booleanValue() == true);
    }

    private boolean isJavaBeingDisabled()
    {
      return (isJavaEnableChanging()) && (this.desiredState != null) && (!this.desiredState.booleanValue());
    }

    private void doSave()
    {
      if (this.desiredState != null)
      {
        this.webJavaSwitch.setWebJavaEnabled(this.desiredState.booleanValue());
        showAppropriateDialog();
        SecurityProperties.fireMyEvent(new SecurityProperties.JavaEnableEvent(this.webJavaSwitch.isWebJavaEnabled()));
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.SecurityProperties
 * JD-Core Version:    0.6.2
 */