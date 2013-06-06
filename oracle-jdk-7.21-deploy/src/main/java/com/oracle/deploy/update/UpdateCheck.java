package com.oracle.deploy.update;

import java.util.Calendar;
import javax.swing.SwingUtilities;

public abstract class UpdateCheck
{
  private static UpdateCheck sInstance = UpdateCheckFactory.getInstance();
  private UpdateCheckListener mUpdateListener;
  private UpdateInfo mUpdateInfo;
  private int mCurrentState;

  public static UpdateCheck getInstance()
  {
    return sInstance;
  }

  public synchronized boolean startUpdateCheck(UpdateCheckListener paramUpdateCheckListener)
  {
    if (paramUpdateCheckListener == null)
      throw new IllegalArgumentException("listener cannot be null");
    if (this.mUpdateListener != null)
      return false;
    this.mUpdateInfo = null;
    this.mUpdateListener = paramUpdateCheckListener;
    if (!checkForUpdate())
    {
      this.mUpdateListener = null;
      return false;
    }
    return true;
  }

  public int getCurrentState()
  {
    return this.mCurrentState;
  }

  public UpdateInfo getUpdateInfo()
  {
    return this.mUpdateInfo;
  }

  private void updateStateChange(int paramInt)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      private final int val$newState;

      public void run()
      {
        UpdateCheck.this.mUpdateListener.updateCheckStateChanged(this.val$newState);
        UpdateCheck.this.mCurrentState = this.val$newState;
        if (this.val$newState == 6)
        {
          UpdateCheck.this.mUpdateListener = null;
          UpdateCheck.this.mCurrentState = 1;
        }
      }
    });
  }

  private void updateAvailable(String paramString1, String paramString2, String paramString3)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      private final String val$version;
      private final String val$size;
      private final String val$type;

      public void run()
      {
        UpdateCheck.this.mUpdateInfo = new UpdateInfo(this.val$version, this.val$size, this.val$type);
      }
    });
  }

  public abstract Calendar getLastUpdateCheck();

  abstract boolean checkForUpdate();

  public final class UpdateCheckState
  {
    public static final int IDLE = 1;
    public static final int CONNECTING = 2;
    public static final int CONNECTED = 3;
    public static final int FAILED_TO_CONNECT = 4;
    public static final int UPDATE_INFO_READY = 5;
    public static final int DONE = 6;

    public UpdateCheckState()
    {
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.oracle.deploy.update.UpdateCheck
 * JD-Core Version:    0.6.2
 */