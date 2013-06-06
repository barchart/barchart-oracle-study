package com.sun.deploy.util;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.ui.ImageLoader;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class IcoEncoder
  implements IconEncoder
{
  private static final boolean DEBUG = false;

  public void convert(File[] paramArrayOfFile, int[] paramArrayOfInt, int paramInt, String paramString)
  {
    IcoImageEncoder[] arrayOfIcoImageEncoder = new IcoImageEncoder[paramInt];
    int i = 0;
    try
    {
      for (int j = 0; j < paramInt; j++)
      {
        PerfLogger.setTime("before ico creation for " + paramString);
        Image localImage = ImageLoader.getInstance().loadImage(paramArrayOfFile[j].getPath());
        if (localImage != null)
        {
          arrayOfIcoImageEncoder[i] = new IcoImageEncoder(localImage, paramArrayOfInt[j]);
          arrayOfIcoImageEncoder[i].createBitmaps();
          i++;
        }
        PerfLogger.setTime("after ico creation for " + paramString);
      }
    }
    catch (IOException localIOException1)
    {
      Trace.ignoredException(localIOException1);
    }
    paramInt = i;
    int[] arrayOfInt = new int[6];
    arrayOfInt[0] = (6 + 16 * paramInt);
    for (int k = 1; k < paramInt; k++)
      arrayOfInt[k] = (arrayOfInt[(k - 1)] + 40 + arrayOfIcoImageEncoder[(k - 1)].getXorData().length + arrayOfIcoImageEncoder[(k - 1)].getAndData().length);
    BufferedOutputStream localBufferedOutputStream = null;
    try
    {
      localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(paramString)));
      IcoStreamWriter localIcoStreamWriter = getIcoStreamWriter(localBufferedOutputStream);
      localIcoStreamWriter.writeIcoHeader(paramInt);
      for (int m = 0; m < paramInt; m++)
        writeIconDirEntry(localIcoStreamWriter, arrayOfIcoImageEncoder[m].getSize(), arrayOfInt[m]);
      for (m = 0; m < paramInt; m++)
      {
        writeInfoHeader(localIcoStreamWriter, arrayOfIcoImageEncoder[m].getSize());
        localBufferedOutputStream.write(arrayOfIcoImageEncoder[m].getXorData());
        localBufferedOutputStream.write(arrayOfIcoImageEncoder[m].getAndData());
      }
      localBufferedOutputStream.flush();
    }
    catch (IOException localIOException2)
    {
      Trace.ignoredException(localIOException2);
    }
    finally
    {
      if (localBufferedOutputStream != null)
        try
        {
          localBufferedOutputStream.close();
        }
        catch (Exception localException)
        {
          Trace.ignoredException(localException);
        }
    }
  }

  private static void writeInfoHeader(IcoStreamWriter paramIcoStreamWriter, int paramInt)
    throws IOException
  {
    paramIcoStreamWriter.writeDWord(40);
    paramIcoStreamWriter.writeDWord(paramInt);
    paramIcoStreamWriter.writeDWord(2 * paramInt);
    paramIcoStreamWriter.writeWord(1);
    paramIcoStreamWriter.writeWord(24);
    paramIcoStreamWriter.writeDWord(0);
    paramIcoStreamWriter.writeDWord(0);
    paramIcoStreamWriter.writeDWord(0);
    paramIcoStreamWriter.writeDWord(0);
    paramIcoStreamWriter.writeDWord(0);
    paramIcoStreamWriter.writeDWord(0);
  }

  private static void writeIconDirEntry(IcoStreamWriter paramIcoStreamWriter, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = getAndScanSize(paramInt1);
    int j = getXorScanSize(paramInt1);
    try
    {
      paramIcoStreamWriter.write(paramInt1);
      paramIcoStreamWriter.write(paramInt1);
      paramIcoStreamWriter.write(0);
      paramIcoStreamWriter.write(0);
      paramIcoStreamWriter.writeWord(1);
      paramIcoStreamWriter.writeWord(24);
      int k = paramInt1 * j + paramInt1 * i + 40;
      paramIcoStreamWriter.writeDWord(k);
      paramIcoStreamWriter.writeDWord(paramInt2);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  private static int getAndScanSize(int paramInt)
  {
    int i = (paramInt + 7) / 8;
    int j = 4 * ((i + 3) / 4);
    return j;
  }

  private static int getXorScanSize(int paramInt)
  {
    int i = paramInt * 3;
    int j = 4 * ((i + 3) / 4);
    return j;
  }

  private static IcoStreamWriter getIcoStreamWriter(BufferedOutputStream paramBufferedOutputStream)
  {
    return new IcoStreamWriter(paramBufferedOutputStream, null);
  }

  public static void printIconFile(File paramFile)
  {
    Trace.println("Icon: " + paramFile);
    try
    {
      FileInputStream localFileInputStream = new FileInputStream(paramFile);
      byte[] arrayOfByte1 = new byte[16];
      byte[] arrayOfByte2 = new byte[6];
      localFileInputStream.read(arrayOfByte2);
      Trace.println("header: " + arrayOfByte2[0] + ", " + arrayOfByte2[1] + ", " + arrayOfByte2[2] + ", " + arrayOfByte2[3] + ", " + arrayOfByte2[4] + ", " + arrayOfByte2[5]);
      int j = arrayOfByte2[4];
      for (int k = 0; k < j; k++)
      {
        localFileInputStream.read(arrayOfByte1);
        Trace.println("Dir entry " + k + ": " + arrayOfByte1[0] + ", " + arrayOfByte1[1] + ", " + arrayOfByte1[2] + ", " + arrayOfByte1[3] + ", " + arrayOfByte1[4] + ", " + arrayOfByte1[5] + ", " + arrayOfByte1[6] + ", " + arrayOfByte1[7] + ", " + arrayOfByte1[8] + ", " + arrayOfByte1[9] + ", " + arrayOfByte1[10] + ", " + arrayOfByte1[11] + ", " + arrayOfByte1[12] + ", " + arrayOfByte1[13] + ", " + arrayOfByte1[14] + ", " + arrayOfByte1[15]);
      }
      k = 0;
      Trace.println("InfoHeader: ");
      byte[] arrayOfByte3 = new byte[40];
      localFileInputStream.read(arrayOfByte3);
      for (k = 0; k < 40; k++)
        Trace.print(arrayOfByte3[k] + ",");
      Trace.println("\n");
      Trace.println("the rest: ");
      int i;
      while ((i = localFileInputStream.read(arrayOfByte1)) > 0)
        Trace.println(" line " + k++ + " : " + arrayOfByte1[0] + ", " + arrayOfByte1[1] + ", " + arrayOfByte1[2] + ", " + arrayOfByte1[3] + ", " + arrayOfByte1[4] + ", " + arrayOfByte1[5] + ", " + arrayOfByte1[6] + ", " + arrayOfByte1[7] + ", " + arrayOfByte1[8] + ", " + arrayOfByte1[9] + ", " + arrayOfByte1[10] + ", " + arrayOfByte1[11] + ", " + arrayOfByte1[12] + ", " + arrayOfByte1[13] + ", " + arrayOfByte1[14] + ", " + arrayOfByte1[15]);
    }
    catch (Exception localException)
    {
      Trace.ignoredException(localException);
    }
  }

  private static class IcoImageEncoder
  {
    Image _awtImage;
    int _size;
    byte[] _andData;
    byte[] _xorData;

    public IcoImageEncoder(Image paramImage, int paramInt)
    {
      this._size = paramInt;
      this._awtImage = paramImage;
      this._andData = new byte[this._size * IcoEncoder.getAndScanSize(this._size)];
      this._xorData = new byte[this._size * IcoEncoder.getXorScanSize(this._size)];
    }

    private int getSize()
    {
      return this._size;
    }

    private byte[] getXorData()
    {
      return this._xorData;
    }

    private byte[] getAndData()
    {
      return this._andData;
    }

    private void createBitmaps()
      throws IOException
    {
      int i = this._size;
      int j = this._size;
      int k = IcoEncoder.getXorScanSize(this._size);
      int m = IcoEncoder.getAndScanSize(this._size);
      byte[] arrayOfByte1 = new byte[j * k];
      byte[] arrayOfByte2 = new byte[j * m];
      BufferedImage localBufferedImage = new BufferedImage(i, j, 2);
      Graphics2D localGraphics2D = localBufferedImage.createGraphics();
      localGraphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      localGraphics2D.drawImage(this._awtImage, 0, 0, i, j, 0, 0, this._awtImage.getWidth(null), this._awtImage.getHeight(null), null);
      localGraphics2D.dispose();
      DataBuffer localDataBuffer = localBufferedImage.getRaster().getDataBuffer();
      int[] arrayOfInt = ((DataBufferInt)localDataBuffer).getData();
      int i1;
      int i3;
      for (int n = 0; n < j; n++)
      {
        i1 = n * m;
        i2 = 0;
        i3 = n * k;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        for (int i7 = 0; i7 < i; i7++)
        {
          int i8 = n * i + i7;
          int i9 = arrayOfInt[i8] >> 24 & 0xFF;
          int i10 = arrayOfInt[i8] >> 16 & 0xFF;
          int i11 = arrayOfInt[i8] >> 8 & 0xFF;
          int i12 = arrayOfInt[i8] & 0xFF;
          if (i9 == 0)
            i5 = (byte)(i5 | 128 >> i6);
          i6++;
          if ((i6 == 8) || (i7 == i - 1))
          {
            arrayOfByte2[(i1 + i2++)] = i5;
            i5 = 0;
            i6 = 0;
          }
          arrayOfByte1[(i3 + i4++)] = ((byte)i12);
          arrayOfByte1[(i3 + i4++)] = ((byte)i11);
          arrayOfByte1[(i3 + i4++)] = ((byte)i10);
        }
        while (i2 < m)
          arrayOfByte2[(i1 + i2++)] = 0;
        while (i4 < k)
          arrayOfByte1[(i3 + i4++)] = 0;
      }
      for (int i2 = 0; i2 < j; i2++)
      {
        i1 = i2 * k;
        n = (j - i2 - 1) * k;
        for (i3 = 0; i3 < k; i3++)
          this._xorData[(i1 + i3)] = arrayOfByte1[(n + i3)];
        i1 = i2 * m;
        n = (j - i2 - 1) * m;
        for (i3 = 0; i3 < m; i3++)
          this._andData[(i1 + i3)] = arrayOfByte2[(n + i3)];
      }
    }
  }

  private static class IcoStreamWriter
  {
    BufferedOutputStream _bos;

    private IcoStreamWriter(BufferedOutputStream paramBufferedOutputStream)
    {
      this._bos = paramBufferedOutputStream;
    }

    private void writeIcoHeader(int paramInt)
      throws IOException
    {
      try
      {
        writeWord(0);
        writeWord(1);
        writeWord(paramInt);
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }

    public void write(int paramInt)
      throws IOException
    {
      this._bos.write(paramInt);
    }

    public void writeWord(int paramInt)
      throws IOException
    {
      this._bos.write(paramInt & 0xFF);
      this._bos.write((paramInt & 0xFF00) >> 8);
    }

    public void writeDWord(int paramInt)
      throws IOException
    {
      this._bos.write(paramInt & 0xFF);
      this._bos.write((paramInt & 0xFF00) >> 8);
      this._bos.write((paramInt & 0xFF0000) >> 16);
      this._bos.write((paramInt & 0xFF000000) >> 24);
    }

    IcoStreamWriter(BufferedOutputStream paramBufferedOutputStream, IcoEncoder.1 param1)
    {
      this(paramBufferedOutputStream);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.IcoEncoder
 * JD-Core Version:    0.6.2
 */