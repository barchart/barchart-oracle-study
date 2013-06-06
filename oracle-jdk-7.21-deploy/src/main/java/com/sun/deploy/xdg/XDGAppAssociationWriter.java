package com.sun.deploy.xdg;

import com.sun.deploy.association.Association;
import com.sun.deploy.association.AssociationAlreadyRegisteredException;
import com.sun.deploy.association.AssociationNotRegisteredException;
import com.sun.deploy.association.RegisterFailedException;
import com.sun.deploy.association.utility.AppAssociationWriter;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

public class XDGAppAssociationWriter
  implements AppAssociationWriter
{
  public XDGAppAssociationWriter(LocalApplicationProperties paramLocalApplicationProperties)
  {
  }

  public void checkAssociationValidForRegistration(Association paramAssociation)
    throws IllegalArgumentException
  {
    if ((paramAssociation.getName() == null) || (paramAssociation.getMimeType() == null))
      throw new IllegalArgumentException("The given association is invalid. It should specify both the name and mimeType fields to perform this operation.");
  }

  public void checkAssociationValidForUnregistration(Association paramAssociation)
    throws IllegalArgumentException
  {
    if (paramAssociation.getName() == null)
      throw new IllegalArgumentException("The given association is invalid. It should specify the name field to perform this operation.");
  }

  public boolean isAssociationExist(Association paramAssociation, int paramInt)
  {
    Associations localAssociations = Associations.getInstance();
    return localAssociations.isAssociationExist(paramAssociation, paramInt);
  }

  public void registerAssociation(Association paramAssociation, int paramInt)
    throws AssociationAlreadyRegisteredException, RegisterFailedException
  {
    Trace.println("XDGAppAssociationWriter.registerAssociation " + paramAssociation.getMimeType(), TraceLevel.TEMP);
    writeMimetype(paramAssociation, paramInt);
    updateMimeDatabase(paramInt);
    makeApplicationDefault(paramAssociation, paramInt);
    updateDesktopDatabase(paramInt);
  }

  private void writeMimetype(Association paramAssociation, int paramInt)
    throws RegisterFailedException
  {
    File localFile = Associations.getMimeTypeFile(paramAssociation, paramInt);
    Trace.println("XDGAppAssociationWriter.writeMimetype " + localFile, TraceLevel.TEMP);
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localFileOutputStream);
      BufferedWriter localBufferedWriter = new BufferedWriter(localOutputStreamWriter);
      localBufferedWriter.write("<?xml version=\"1.0\"?>\n");
      localBufferedWriter.write("<mime-info xmlns='http://www.freedesktop.org/standards/shared-mime-info'>\n");
      localBufferedWriter.write("<mime-type type=\"" + paramAssociation.getMimeType() + "\">\n");
      localBufferedWriter.write("<comment>" + paramAssociation.getDescription() + "</comment>\n");
      Iterator localIterator = paramAssociation.getFileExtList().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localBufferedWriter.write("<glob pattern=\"*" + str + "\"/>");
      }
      localBufferedWriter.write("\n");
      localBufferedWriter.write("</mime-type>\n");
      localBufferedWriter.write("</mime-info>\n");
      localBufferedWriter.close();
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Trace.ignored(localFileNotFoundException);
      throw new RegisterFailedException();
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
      throw new RegisterFailedException();
    }
  }

  private void updateMimeDatabase(int paramInt)
    throws RegisterFailedException
  {
    Trace.println("XDGAppAssociationWriter.updateMimeDatabase ", TraceLevel.TEMP);
    try
    {
      String[] arrayOfString1 = Associations.getMimeBasePaths(paramInt);
      for (int i = 0; i < arrayOfString1.length; i++)
      {
        String[] arrayOfString2 = { "update-mime-database", arrayOfString1[i] };
        Process localProcess = Runtime.getRuntime().exec(arrayOfString2);
        localProcess.waitFor();
      }
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
      throw new RegisterFailedException();
    }
  }

  private void updateDesktopDatabase(int paramInt)
    throws RegisterFailedException
  {
    Trace.println("XDGAppAssociationWriter.updateDesktopDatabase ", TraceLevel.TEMP);
    try
    {
      String[] arrayOfString1 = Associations.getAppBasePaths(paramInt);
      for (int i = 0; i < arrayOfString1.length; i++)
      {
        String[] arrayOfString2 = { "update-desktop-database", arrayOfString1[i] };
        Process localProcess = Runtime.getRuntime().exec(arrayOfString2);
        localProcess.waitFor();
      }
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
      throw new RegisterFailedException();
    }
  }

  private void makeApplicationDefault(Association paramAssociation, int paramInt)
    throws RegisterFailedException
  {
    Trace.println("XDGAppAssociationWriter.makeApplicationDefault ", TraceLevel.TEMP);
    try
    {
      File localFile = Associations.getDesktopEntryFile(paramAssociation, paramInt);
      String[] arrayOfString = { "xdg-mime", "default", localFile.getName(), paramAssociation.getMimeType() };
      Process localProcess = Runtime.getRuntime().exec(arrayOfString);
      int i = localProcess.waitFor();
      if (i != 0)
      {
        Trace.println(" xdg-mime default fails, exitValue == " + i);
        throw new RegisterFailedException();
      }
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
      throw new RegisterFailedException();
    }
  }

  public void unregisterAssociation(Association paramAssociation, int paramInt)
    throws AssociationNotRegisteredException, RegisterFailedException
  {
    Trace.println("XDGAppAssociationWriter.unregisterAssociation " + paramAssociation.getMimeType(), TraceLevel.TEMP);
    File localFile = Associations.getMimeTypeFile(paramAssociation, paramInt);
    if (!localFile.exists())
      Trace.print("mime-info file doesn't exist, path == " + localFile.getAbsolutePath(), TraceLevel.TEMP);
    else
      localFile.delete();
    updateMimeDatabase(paramInt);
    updateDesktopDatabase(paramInt);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.xdg.XDGAppAssociationWriter
 * JD-Core Version:    0.6.2
 */