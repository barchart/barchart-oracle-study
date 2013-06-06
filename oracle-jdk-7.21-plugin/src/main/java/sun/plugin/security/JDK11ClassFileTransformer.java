package sun.plugin.security;

import sun.misc.ClassFileTransformer;

public class JDK11ClassFileTransformer
{
  public static synchronized void init()
  {
    ClassFileTransformer.add(new Broken11Transformer_0(null));
    ClassFileTransformer.add(new Broken11Transformer_1(null));
  }

  private static void ensureClassFileVersion(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ClassFormatError
  {
    if (paramInt2 < 8)
      throw new ClassFormatError();
    int i = readShort(paramArrayOfByte, paramInt1 + 6);
    if (i >= 46)
      throw new ClassFormatError();
  }

  private static int readByte(byte paramByte)
  {
    return paramByte & 0xFF;
  }

  private static int readShort(byte[] paramArrayOfByte, int paramInt)
  {
    int i = readByte(paramArrayOfByte[paramInt]);
    int j = readByte(paramArrayOfByte[(paramInt + 1)]);
    return i << 8 | j;
  }

  private static class Broken11Transformer_0 extends ClassFileTransformer
  {
    private Broken11Transformer_0()
    {
    }

    public byte[] transform(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws ClassFormatError
    {
      JDK11ClassFileTransformer.ensureClassFileVersion(paramArrayOfByte, paramInt1, paramInt2);
      try
      {
        Broken11ClassFixer localBroken11ClassFixer = new Broken11ClassFixer();
        localBroken11ClassFixer.process(paramArrayOfByte, paramInt1, paramInt2);
        byte[] arrayOfByte1 = localBroken11ClassFixer.getProcessedData();
        int i = localBroken11ClassFixer.getProcessedDataOffset();
        int j = localBroken11ClassFixer.getProcessedDataLength();
        if ((i == 0) && (j == arrayOfByte1.length))
          return arrayOfByte1;
        byte[] arrayOfByte2 = new byte[j];
        System.arraycopy(arrayOfByte1, i, arrayOfByte2, 0, j);
        return arrayOfByte2;
      }
      catch (ThreadDeath localThreadDeath)
      {
        throw localThreadDeath;
      }
      catch (Throwable localThrowable)
      {
      }
      throw new ClassFormatError();
    }

    Broken11Transformer_0(JDK11ClassFileTransformer.1 param1)
    {
      this();
    }
  }

  private static class Broken11Transformer_1 extends ClassFileTransformer
  {
    private Broken11Transformer_1()
    {
    }

    public byte[] transform(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws ClassFormatError
    {
      JDK11ClassFileTransformer.ensureClassFileVersion(paramArrayOfByte, paramInt1, paramInt2);
      try
      {
        StripClassFile localStripClassFile = new StripClassFile();
        return localStripClassFile.strip(paramArrayOfByte);
      }
      catch (ThreadDeath localThreadDeath)
      {
        throw localThreadDeath;
      }
      catch (Throwable localThrowable)
      {
      }
      throw new ClassFormatError();
    }

    Broken11Transformer_1(JDK11ClassFileTransformer.1 param1)
    {
      this();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.security.JDK11ClassFileTransformer
 * JD-Core Version:    0.6.2
 */