package sun.plugin.security;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StripClassFile
{
  private static final int CONSTANT_Class = 7;
  private static final int CONSTANT_Fieldref = 9;
  private static final int CONSTANT_Methodref = 10;
  private static final int CONSTANT_InterfaceMethodref = 11;
  private static final int CONSTANT_String = 8;
  private static final int CONSTANT_Integer = 3;
  private static final int CONSTANT_Float = 4;
  private static final int CONSTANT_Long = 5;
  private static final int CONSTANT_Double = 6;
  private static final int CONSTANT_NameAndType = 12;
  private static final int CONSTANT_Utf8 = 1;
  private static final String[] requiredTypes = { "Code", "ConstantValue", "Exceptions", "InnerClasses", "Synthetic" };
  private ByteStream bs;
  private DataInputStream dis;
  int magic;
  short minor_version;
  short major_version;
  int constant_pool_count;
  ConstantPool[] constant_pool;

  public byte[] strip(byte[] paramArrayOfByte)
    throws Exception
  {
    int i = paramArrayOfByte.length;
    byte[] arrayOfByte1 = new byte[i];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 0, i);
    try
    {
      this.bs = new ByteStream(arrayOfByte1);
      this.dis = new DataInputStream(this.bs);
      stripClassFile();
    }
    catch (Exception localException)
    {
      throw localException;
    }
    int j = this.bs.getOffset();
    byte[] arrayOfByte2 = new byte[j];
    arrayOfByte1 = this.bs.getArray();
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, j);
    return arrayOfByte2;
  }

  private boolean validateClassName(int paramInt)
    throws IOException
  {
    ConstantPool localConstantPool = this.constant_pool[(paramInt - 1)];
    paramInt = localConstantPool.nameIndex;
    return validateName(paramInt);
  }

  private static boolean validJavaCharacter(char paramChar, boolean paramBoolean)
  {
    if ((paramChar == '/') || (paramChar == '(') || (paramChar == ')') || (paramChar == ';') || (paramChar == '[') || (paramChar == '<'))
      return true;
    return paramBoolean ? Character.isJavaIdentifierStart(paramChar) : Character.isJavaIdentifierPart(paramChar);
  }

  private boolean validateName(int paramInt)
    throws IOException
  {
    ConstantPool localConstantPool = this.constant_pool[(paramInt - 1)];
    if ((localConstantPool.string == null) || (localConstantPool.string.equals("<init>")) || (localConstantPool.string.equals("<clinit>")))
      return true;
    for (int i = 0; i < localConstantPool.string.length(); i++)
    {
      char c = localConstantPool.string.charAt(i);
      if (!validJavaCharacter(c, i == 0))
      {
        localConstantPool.invalid = true;
        return false;
      }
    }
    return true;
  }

  static void charToUnicode(char paramChar, StringBuffer paramStringBuffer)
  {
    paramStringBuffer.append("U");
    String str = Integer.toHexString(paramChar);
    for (int i = str.length(); i < 4; i++)
      paramStringBuffer.append('0');
    paramStringBuffer.append(str);
  }

  static String makeValid(String paramString)
  {
    int i = paramString.length();
    if (i == 0)
      return "";
    StringBuffer localStringBuffer = new StringBuffer(i * 5);
    char[] arrayOfChar = paramString.toCharArray();
    for (int j = 0; j < i; j++)
    {
      char c = arrayOfChar[j];
      if (validJavaCharacter(c, j == 0))
        localStringBuffer.append(arrayOfChar[j]);
      else
        charToUnicode(arrayOfChar[j], localStringBuffer);
    }
    return localStringBuffer.toString();
  }

  private void stripClassFile()
    throws IOException
  {
    this.magic = this.dis.readInt();
    this.minor_version = this.dis.readShort();
    this.major_version = this.dis.readShort();
    this.constant_pool_count = this.dis.readUnsignedShort();
    this.constant_pool = new ConstantPool[this.constant_pool_count - 1];
    int i = this.bs.getOffset();
    int n;
    for (int j = 0; j < 2; j++)
    {
      k = j == 0 ? 1 : 0;
      m = j != 0 ? 1 : 0;
      this.bs.setOffset(i);
      ConstantPool localConstantPool;
      for (n = 0; n < this.constant_pool_count - 1; n++)
      {
        localConstantPool = this.constant_pool[n];
        localConstantPool = new ConstantPool((m != 0) && (localConstantPool.invalid));
        this.constant_pool[n] = localConstantPool;
        if ((localConstantPool.tag == 5) || (localConstantPool.tag == 6))
          n++;
      }
      for (n = 0; n < this.constant_pool_count - 1; n++)
      {
        localConstantPool = this.constant_pool[n];
        localConstantPool.verify(this.constant_pool);
        if ((localConstantPool.tag == 5) || (localConstantPool.tag == 6))
          n++;
      }
      this.dis.skip(2L);
      n = this.dis.readUnsignedShort();
      int i1 = this.dis.readUnsignedShort();
      int i2 = this.dis.readShort();
      for (int i3 = 0; i3 < i2; i3++)
        i4 = this.dis.readUnsignedShort();
      i3 = this.dis.readShort();
      for (int i4 = 0; i4 < i3; i4++)
        parseFieldInfo();
      i4 = this.dis.readShort();
      for (int i5 = 0; i5 < i4; i5++)
        parseMethodInfo();
    }
    j = this.bs.getOffset();
    int k = this.dis.readShort();
    for (int m = 0; m < k; m++)
    {
      n = this.bs.getOffset();
      if (!parseAttributeInfo())
      {
        this.bs.decrementCount(j);
        this.bs.removeBytes(n, this.bs.getOffset());
        this.bs.setOffset(n);
      }
    }
  }

  private void parseFieldInfo()
    throws IOException
  {
    int i = this.dis.readUnsignedShort();
    int j = this.dis.readUnsignedShort();
    validateName(j);
    int k = this.dis.readUnsignedShort();
    validateName(k);
    int m = this.dis.readUnsignedShort();
    for (int n = 0; n < m; n++)
      parseAttributeInfo();
  }

  private void parseMethodInfo()
    throws IOException
  {
    this.dis.skip(2L);
    int i = this.dis.readUnsignedShort();
    validateName(i);
    int j = this.dis.readUnsignedShort();
    validateName(j);
    int k = this.dis.readUnsignedShort();
    for (int m = 0; m < k; m++)
      parseAttributeInfo();
  }

  private boolean parseAttributeInfo()
    throws IOException
  {
    int i = this.dis.readUnsignedShort();
    ConstantPool localConstantPool = this.constant_pool[(i - 1)];
    String str = localConstantPool.string;
    if (str.equals("Code"))
    {
      parseCodeAttribute();
      return true;
    }
    int j = this.dis.readInt();
    this.dis.skip(j);
    return attributeRequired(str);
  }

  boolean attributeRequired(String paramString)
  {
    for (int i = 0; i < requiredTypes.length; i++)
      if (paramString.equals(requiredTypes[i]))
        return true;
    return false;
  }

  private void parseCodeAttribute()
    throws IOException
  {
    int i = this.bs.getOffset();
    int j = this.dis.readInt();
    this.dis.skip(2L);
    this.dis.skip(2L);
    int k = this.dis.readInt();
    this.dis.skip(k);
    int m = this.dis.readUnsignedShort();
    this.dis.skip(8 * m);
    int n = this.bs.getOffset();
    int i1 = this.dis.readUnsignedShort();
    for (int i2 = 0; i2 < i1; i2++)
    {
      int i3 = this.bs.getOffset();
      if (!parseAttributeInfo())
      {
        int i4 = this.bs.getOffset() - i3;
        this.bs.decrementCount(n);
        this.bs.removeBytes(i3, this.bs.getOffset());
        this.bs.setOffset(i3);
        this.bs.decrementLength(i, i4);
      }
    }
  }

  private class ByteStream extends InputStream
  {
    private byte[] array;
    private int offset;
    private int length;

    ByteStream(byte[] arg2)
    {
      Object localObject;
      this.array = localObject;
      this.offset = 0;
      this.length = localObject.length;
    }

    public byte[] getArray()
    {
      return this.array;
    }

    public int read()
      throws IOException
    {
      if (this.offset == this.length)
        throw new IOException();
      int i = this.array[(this.offset++)] & 0xFF;
      return i;
    }

    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      if (this.offset + paramArrayOfByte.length > this.length)
        throw new IOException();
      for (int i = 0; i < paramArrayOfByte.length; i++)
        this.array[(this.offset++)] = paramArrayOfByte[i];
    }

    public int getOffset()
    {
      return this.offset;
    }

    public void setOffset(int paramInt)
    {
      this.offset = paramInt;
    }

    public void setByte(int paramInt1, int paramInt2)
    {
      this.array[paramInt1] = ((byte)paramInt2);
    }

    public void decrementCount(int paramInt)
    {
      int i = this.array[paramInt] & 0xFF;
      int j = this.array[(paramInt + 1)] & 0xFF;
      int k = (i << 8) + (j << 0) - 1;
      this.array[paramInt] = ((byte)(k >>> 8 & 0xFF));
      this.array[(paramInt + 1)] = ((byte)(k >>> 0 & 0xFF));
    }

    public void decrementLength(int paramInt1, int paramInt2)
    {
      int i = this.array[paramInt1] & 0xFF;
      int j = this.array[(paramInt1 + 1)] & 0xFF;
      int k = this.array[(paramInt1 + 2)] & 0xFF;
      int m = this.array[(paramInt1 + 3)] & 0xFF;
      int n = (i << 24) + (j << 16) + (k << 8) + (m << 0);
      n -= paramInt2;
      this.array[paramInt1] = ((byte)(n >>> 24 & 0xFF));
      this.array[(paramInt1 + 1)] = ((byte)(n >>> 16 & 0xFF));
      this.array[(paramInt1 + 2)] = ((byte)(n >>> 8 & 0xFF));
      this.array[(paramInt1 + 3)] = ((byte)(n >>> 0 & 0xFF));
    }

    public void removeBytes(int paramInt1, int paramInt2)
    {
      System.arraycopy(this.array, paramInt2, this.array, paramInt1, this.length - paramInt2);
      this.length -= paramInt2 - paramInt1;
    }

    public void addBytes(int paramInt1, int paramInt2)
    {
      byte[] arrayOfByte = new byte[this.length + paramInt2];
      System.arraycopy(this.array, 0, arrayOfByte, 0, paramInt1);
      System.arraycopy(this.array, paramInt1, arrayOfByte, paramInt1 + paramInt2, this.length - paramInt1);
      this.array = arrayOfByte;
      this.length += paramInt2;
    }
  }

  private class ConstantPool
  {
    byte tag = (byte)StripClassFile.this.dis.read();
    int nameIndex;
    String string;
    int stringOffset;
    int descriptorIndex;
    boolean invalid;

    ConstantPool(boolean arg2)
      throws IOException
    {
      switch (this.tag)
      {
      case 7:
      case 8:
        this.nameIndex = StripClassFile.this.dis.readUnsignedShort();
        break;
      case 9:
      case 10:
      case 11:
        this.nameIndex = StripClassFile.this.dis.readUnsignedShort();
        this.descriptorIndex = StripClassFile.this.dis.readUnsignedShort();
        break;
      case 3:
      case 4:
        StripClassFile.this.dis.skip(4L);
        break;
      case 12:
        this.nameIndex = StripClassFile.this.dis.readUnsignedShort();
        this.descriptorIndex = StripClassFile.this.dis.readUnsignedShort();
        break;
      case 5:
      case 6:
        StripClassFile.this.dis.skip(8L);
        break;
      case 1:
        this.stringOffset = StripClassFile.this.bs.getOffset();
        this.string = StripClassFile.this.dis.readUTF();
        int i;
        if (i != 0)
        {
          int j = StripClassFile.this.bs.getOffset() - this.stringOffset;
          ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
          DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
          this.string = StripClassFile.makeValid(this.string);
          localDataOutputStream.writeUTF(this.string);
          byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
          localByteArrayOutputStream = null;
          StripClassFile.this.bs.addBytes(this.stringOffset, arrayOfByte.length - j);
          StripClassFile.this.bs.setOffset(this.stringOffset);
          StripClassFile.this.bs.write(arrayOfByte);
        }
        break;
      case 2:
      default:
        throw new IOException();
      }
    }

    void verify(ConstantPool[] paramArrayOfConstantPool)
      throws IOException
    {
      switch (this.tag)
      {
      case 1:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
        break;
      case 9:
        StripClassFile.this.validateClassName(this.nameIndex);
        break;
      case 10:
        StripClassFile.this.validateClassName(this.nameIndex);
        break;
      case 11:
        StripClassFile.this.validateClassName(this.nameIndex);
        break;
      case 12:
        StripClassFile.this.validateName(this.nameIndex);
        StripClassFile.this.validateName(this.descriptorIndex);
        break;
      case 2:
      default:
        throw new IOException();
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.security.StripClassFile
 * JD-Core Version:    0.6.2
 */