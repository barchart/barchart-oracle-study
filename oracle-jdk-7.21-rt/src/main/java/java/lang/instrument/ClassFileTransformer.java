package java.lang.instrument;

import java.security.ProtectionDomain;

public abstract interface ClassFileTransformer
{
  public abstract byte[] transform(ClassLoader paramClassLoader, String paramString, Class<?> paramClass, ProtectionDomain paramProtectionDomain, byte[] paramArrayOfByte)
    throws IllegalClassFormatException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.instrument.ClassFileTransformer
 * JD-Core Version:    0.6.2
 */