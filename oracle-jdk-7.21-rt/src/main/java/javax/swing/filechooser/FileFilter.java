package javax.swing.filechooser;

import java.io.File;

public abstract class FileFilter
{
  public abstract boolean accept(File paramFile);

  public abstract String getDescription();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.filechooser.FileFilter
 * JD-Core Version:    0.6.2
 */