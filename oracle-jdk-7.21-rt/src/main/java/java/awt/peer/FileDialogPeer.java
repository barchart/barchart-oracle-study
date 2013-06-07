package java.awt.peer;

import java.io.FilenameFilter;

public abstract interface FileDialogPeer extends DialogPeer
{
  public abstract void setFile(String paramString);

  public abstract void setDirectory(String paramString);

  public abstract void setFilenameFilter(FilenameFilter paramFilenameFilter);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.peer.FileDialogPeer
 * JD-Core Version:    0.6.2
 */