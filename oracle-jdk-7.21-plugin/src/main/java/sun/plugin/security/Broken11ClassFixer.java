package sun.plugin.security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;

public class Broken11ClassFixer
{
  private byte[] src;
  private int srcPos;
  private byte[] dest;
  private int destPos;
  private int numConstantPoolEntries;
  private boolean detectedBogusLocalVariableNameIndex;
  private static final int CODE_MASK = 1;
  private static final int LOCAL_VARIABLE_TABLE_MASK = 2;
  private byte[] constantPoolUtf8Entries;
  private static final int CONSTANT_CLASS_TAG = 7;
  private static final int CONSTANT_FIELD_REF_TAG = 9;
  private static final int CONSTANT_METHOD_REF_TAG = 10;
  private static final int CONSTANT_INTERFACE_METHOD_REF_TAG = 11;
  private static final int CONSTANT_STRING_TAG = 8;
  private static final int CONSTANT_INTEGER_TAG = 3;
  private static final int CONSTANT_FLOAT_TAG = 4;
  private static final int CONSTANT_LONG_TAG = 5;
  private static final int CONSTANT_DOUBLE_TAG = 6;
  private static final int CONSTANT_NAME_AND_TYPE_TAG = 12;
  private static final int CONSTANT_UTF8_TAG = 1;

  public void process(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws Exception
  {
    init(paramArrayOfByte, paramInt1, paramInt2);
    copyHeader();
    scanAndCopyConstantPool();
    copyAccessFlagsAndClassInformation();
    copyInterfaces();
    scanAndCopyFields();
    scanAndCopyMethods();
    copyAttributes();
  }

  public byte[] getProcessedData()
  {
    return this.dest;
  }

  public int getProcessedDataOffset()
  {
    return 0;
  }

  public int getProcessedDataLength()
  {
    return this.destPos;
  }

  private void init(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.src = paramArrayOfByte;
    this.srcPos = paramInt1;
    this.dest = new byte[paramInt2];
    this.destPos = 0;
    this.detectedBogusLocalVariableNameIndex = false;
  }

  private int srcPos()
  {
    return this.srcPos;
  }

  private int destPos()
  {
    return this.destPos;
  }

  private int readByte()
  {
    return this.src[(this.srcPos++)] & 0xFF;
  }

  private int readShort()
  {
    int i = readByte();
    int j = readByte();
    return i << 8 | j;
  }

  private int readInt()
  {
    int i = readShort();
    int j = readShort();
    return i << 16 | j;
  }

  private void writeByte(int paramInt)
  {
    this.dest[(this.destPos++)] = ((byte)paramInt);
  }

  private void writeShort(int paramInt)
  {
    writeByte(paramInt >> 8);
    writeByte(paramInt);
  }

  private void writeInt(int paramInt)
  {
    writeShort(paramInt >> 16);
    writeShort(paramInt);
  }

  private void writeByteAt(int paramInt1, int paramInt2)
  {
    this.dest[paramInt1] = ((byte)paramInt2);
  }

  private void writeShortAt(int paramInt1, int paramInt2)
  {
    writeByteAt(paramInt1, paramInt2 >> 8);
    writeByteAt(paramInt1 + 1, paramInt2);
  }

  private void writeIntAt(int paramInt1, int paramInt2)
  {
    writeShortAt(paramInt1, paramInt2 >> 16);
    writeShortAt(paramInt1 + 2, paramInt2);
  }

  private void copy(int paramInt)
  {
    for (int i = 0; i < paramInt; i++)
      this.dest[(this.destPos + i)] = this.src[(this.srcPos + i)];
    this.srcPos += paramInt;
    this.destPos += paramInt;
  }

  private int copyByte()
  {
    int i = readByte();
    writeByte(i);
    return i;
  }

  private int copyShort()
  {
    int i = readShort();
    writeShort(i);
    return i;
  }

  private int copyInt()
  {
    int i = readInt();
    writeInt(i);
    return i;
  }

  private void copyHeader()
  {
    copy(8);
  }

  private void scanAndCopyConstantPool()
  {
    this.numConstantPoolEntries = (copyShort() - 1);
    this.constantPoolUtf8Entries = new byte[this.numConstantPoolEntries];
    for (int i = 0; i < this.numConstantPoolEntries; i++)
    {
      int j = copyByte();
      switch (j)
      {
      case 7:
      case 8:
        copy(2);
        break;
      case 3:
      case 4:
      case 9:
      case 10:
      case 11:
      case 12:
        copy(4);
        break;
      case 5:
      case 6:
        copy(8);
        i++;
        break;
      case 1:
        scanAndCopyUtf8(i);
        break;
      case 2:
      default:
        throw new RuntimeException("Invalid constant pool tag " + j);
      }
    }
  }

  private void scanAndCopyUtf8(int paramInt)
  {
    int i = copyShort();
    String str1 = "Code";
    String str2 = "LocalVariableTable";
    if (i == str1.length())
    {
      if (copyUtf8LookingFor(str1))
        markCodeInCP(paramInt);
    }
    else if (i == str2.length())
    {
      if (copyUtf8LookingFor(str2))
        markLocalVariableTableInCP(paramInt);
    }
    else
      copy(i);
  }

  private boolean copyUtf8LookingFor(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      int k = copyByte();
      if (k != (paramString.charAt(j) & 0xFF))
      {
        copy(i - j - 1);
        return false;
      }
    }
    return true;
  }

  private void markCodeInCP(int paramInt)
  {
    int tmp5_4 = paramInt;
    byte[] tmp5_1 = this.constantPoolUtf8Entries;
    tmp5_1[tmp5_4] = ((byte)(tmp5_1[tmp5_4] | 0x1));
  }

  private void markLocalVariableTableInCP(int paramInt)
  {
    int tmp5_4 = paramInt;
    byte[] tmp5_1 = this.constantPoolUtf8Entries;
    tmp5_1[tmp5_4] = ((byte)(tmp5_1[tmp5_4] | 0x2));
  }

  private void copyAccessFlagsAndClassInformation()
  {
    copy(6);
  }

  private void copyInterfaces()
  {
    int i = copyShort();
    copy(2 * i);
  }

  private void scanAndCopyFields()
  {
    int i = copyShort();
    for (int j = 0; j < i; j++)
    {
      scanAndCopyAccessFlags();
      copy(4);
      copyAttributes();
    }
  }

  private void scanAndCopyAccessFlags()
  {
    int i = readShort();
    if (Modifier.isPublic(i))
      i &= -7;
    else if (Modifier.isProtected(i))
      i &= -4;
    else
      i &= -6;
    if (Modifier.isAbstract(i))
      i &= -2363;
    writeShort(i);
  }

  private boolean cpIdxIsLocalVariableTable(int paramInt)
  {
    return (this.constantPoolUtf8Entries[(paramInt - 1)] & 0x2) != 0;
  }

  private boolean cpIdxIsCode(int paramInt)
  {
    return (this.constantPoolUtf8Entries[(paramInt - 1)] & 0x1) != 0;
  }

  private boolean isValidConstantPoolIndex(int paramInt)
  {
    return (paramInt > 0) && (paramInt <= this.numConstantPoolEntries);
  }

  private boolean isValidStartAndLength(int paramInt1, int paramInt2, int paramInt3)
  {
    return (paramInt1 >= 0) && (paramInt1 + paramInt2 <= paramInt3);
  }

  private void copyAttributes()
  {
    int i = copyShort();
    for (int j = 0; j < i; j++)
      copyAttribute();
  }

  private void copyAttribute()
  {
    copy(2);
    copyRestOfAttribute();
  }

  private void copyRestOfAttribute()
  {
    copy(copyInt());
  }

  private void scanAndCopyMethods()
  {
    int i = copyShort();
    for (int j = 0; j < i; j++)
    {
      scanAndCopyAccessFlags();
      copy(4);
      int k = copyShort();
      for (int m = 0; m < k; m++)
      {
        int n = copyShort();
        if (cpIdxIsCode(n))
          processCodeAttribute();
        else
          copyRestOfAttribute();
      }
    }
  }

  private void processCodeAttribute()
  {
    int i = destPos();
    int j = copyInt();
    copy(4);
    int k = copyInt();
    copy(k);
    copy(8 * copyShort());
    int m = copyShort();
    for (int n = 0; n < m; n++)
    {
      int i1 = copyShort();
      if (cpIdxIsLocalVariableTable(i1))
        processLocalVariableTableAttribute(i, j, k);
      else
        copyRestOfAttribute();
    }
  }

  private void processLocalVariableTableAttribute(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = destPos();
    int j = copyInt();
    int k = destPos();
    int m = copyShort();
    int n = m;
    for (int i1 = 0; i1 < m; i1++)
    {
      int i2 = readShort();
      int i3 = readShort();
      int i4 = readShort();
      int i5 = readShort();
      int i6 = readShort();
      if ((isValidConstantPoolIndex(i4)) && (isValidStartAndLength(i2, i3, paramInt3)))
      {
        writeShort(i2);
        writeShort(i3);
        writeShort(i4);
        writeShort(i5);
        writeShort(i6);
      }
      else
      {
        paramInt2 -= 10;
        j -= 10;
        n--;
        writeIntAt(paramInt1, paramInt2);
        writeIntAt(i, j);
        writeShortAt(k, n);
        this.detectedBogusLocalVariableNameIndex = true;
      }
    }
  }

  private static void usage()
  {
    System.err.println("Usage: java Broken11ClassFixer [filename]");
    System.exit(1);
  }

  public static void main(String[] paramArrayOfString)
  {
    if (paramArrayOfString.length != 1)
      usage();
    try
    {
      File localFile = new File(paramArrayOfString[0]);
      int i = (int)localFile.length();
      byte[] arrayOfByte = new byte[i];
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(localFile));
      int j = 0;
      while (j < arrayOfByte.length)
        j += localBufferedInputStream.read(arrayOfByte, j, arrayOfByte.length - j);
      Broken11ClassFixer localBroken11ClassFixer = new Broken11ClassFixer();
      localBroken11ClassFixer.process(arrayOfByte, 0, arrayOfByte.length);
      if (localBroken11ClassFixer.detectedBogusLocalVariableNameIndex)
        System.err.println("Detected bogus local variable name index");
      if (localBroken11ClassFixer.srcPos < arrayOfByte.length)
        System.err.println("Detected extra bytes at the end of the class file");
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.security.Broken11ClassFixer
 * JD-Core Version:    0.6.2
 */