package sun.plugin2.os.windows;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import sun.plugin2.gluegen.runtime.BufferFactory;
import sun.plugin2.util.NativeLibLoader;

public class Windows
{
  public static final int ERROR_PIPE_CONNECTED = 535;
  public static final int STATUS_ABANDONED_WAIT_0 = 128;
  public static final int WAIT_ABANDONED = 128;
  public static final int STATUS_WAIT_0 = 0;
  public static final int WAIT_OBJECT_0 = 0;
  public static final int WAIT_TIMEOUT = 258;
  public static final long INFINITE = -1L;
  public static final int EVENT_ALL_ACCESS = 2031619;
  public static final int PIPE_ACCESS_INBOUND = 1;
  public static final int PIPE_ACCESS_OUTBOUND = 2;
  public static final int PIPE_ACCESS_DUPLEX = 3;
  public static final int PIPE_WAIT = 0;
  public static final int PIPE_NOWAIT = 1;
  public static final int PIPE_READMODE_BYTE = 0;
  public static final int PIPE_READMODE_MESSAGE = 2;
  public static final int PIPE_TYPE_BYTE = 0;
  public static final int PIPE_TYPE_MESSAGE = 4;
  public static final int PIPE_UNLIMITED_INSTANCES = 255;
  public static final int CREATE_NEW = 1;
  public static final int CREATE_ALWAYS = 2;
  public static final int OPEN_EXISTING = 3;
  public static final int OPEN_ALWAYS = 4;
  public static final int TRUNCATE_EXISTING = 5;
  public static final int FILE_SHARE_READ = 1;
  public static final int FILE_SHARE_WRITE = 2;
  public static final int FILE_SHARE_DELETE = 4;
  public static final int FILE_ATTRIBUTE_READONLY = 1;
  public static final int FILE_ATTRIBUTE_HIDDEN = 2;
  public static final int FILE_ATTRIBUTE_SYSTEM = 4;
  public static final int FILE_ATTRIBUTE_DIRECTORY = 16;
  public static final int FILE_ATTRIBUTE_ARCHIVE = 32;
  public static final int FILE_ATTRIBUTE_DEVICE = 64;
  public static final int FILE_ATTRIBUTE_NORMAL = 128;
  public static final int FILE_ATTRIBUTE_TEMPORARY = 256;
  public static final int FILE_ATTRIBUTE_SPARSE_FILE = 512;
  public static final int FILE_ATTRIBUTE_REPARSE_POINT = 1024;
  public static final int FILE_ATTRIBUTE_COMPRESSED = 2048;
  public static final int FILE_ATTRIBUTE_OFFLINE = 4096;
  public static final int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 8192;
  public static final int FILE_ATTRIBUTE_ENCRYPTED = 16384;
  public static final long FILE_FLAG_WRITE_THROUGH = -2147483648L;
  public static final int FILE_FLAG_OVERLAPPED = 1073741824;
  public static final int FILE_FLAG_NO_BUFFERING = 536870912;
  public static final int FILE_FLAG_RANDOM_ACCESS = 268435456;
  public static final int FILE_FLAG_SEQUENTIAL_SCAN = 134217728;
  public static final int FILE_FLAG_DELETE_ON_CLOSE = 67108864;
  public static final int FILE_FLAG_BACKUP_SEMANTICS = 33554432;
  public static final int FILE_FLAG_POSIX_SEMANTICS = 16777216;
  public static final int FILE_FLAG_OPEN_REPARSE_POINT = 2097152;
  public static final int FILE_FLAG_OPEN_NO_RECALL = 1048576;
  public static final int FILE_FLAG_FIRST_PIPE_INSTANCE = 524288;
  public static final long GENERIC_READ = -2147483648L;
  public static final int GENERIC_WRITE = 1073741824;
  public static final int GENERIC_EXECUTE = 536870912;
  public static final int GENERIC_ALL = 268435456;
  public static final int FLASHW_CAPTION = 1;
  public static final int MB_OK = 0;
  public static final int VER_PLATFORM_WIN32s = 0;
  public static final int VER_PLATFORM_WIN32_WINDOWS = 1;
  public static final int VER_PLATFORM_WIN32_NT = 2;
  public static final long INVALID_HANDLE_VALUE = -1L;

  public static native boolean CloseHandle(long paramLong);

  public static boolean ConnectNamedPipe(long paramLong, OVERLAPPED paramOVERLAPPED)
  {
    return ConnectNamedPipe0(paramLong, paramOVERLAPPED == null ? null : paramOVERLAPPED.getBuffer());
  }

  private static native boolean ConnectNamedPipe0(long paramLong, ByteBuffer paramByteBuffer);

  public static long CreateEventA(SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES, boolean paramBoolean1, boolean paramBoolean2, String paramString)
  {
    return CreateEventA0(paramSECURITY_ATTRIBUTES == null ? null : paramSECURITY_ATTRIBUTES.getBuffer(), paramBoolean1, paramBoolean2, paramString);
  }

  private static native long CreateEventA0(ByteBuffer paramByteBuffer, boolean paramBoolean1, boolean paramBoolean2, String paramString);

  public static long CreateFileA(String paramString, int paramInt1, int paramInt2, SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES, int paramInt3, int paramInt4, long paramLong)
  {
    return CreateFileA0(paramString, paramInt1, paramInt2, paramSECURITY_ATTRIBUTES == null ? null : paramSECURITY_ATTRIBUTES.getBuffer(), paramInt3, paramInt4, paramLong);
  }

  private static native long CreateFileA0(String paramString, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer, int paramInt3, int paramInt4, long paramLong);

  public static long CreateNamedPipeA(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES)
  {
    return CreateNamedPipeA0(paramString, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramSECURITY_ATTRIBUTES == null ? null : paramSECURITY_ATTRIBUTES.getBuffer());
  }

  private static native long CreateNamedPipeA0(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, ByteBuffer paramByteBuffer);

  public static native boolean DisconnectNamedPipe(long paramLong);

  public static boolean FlashWindowEx(FLASHWINFO paramFLASHWINFO)
  {
    return FlashWindowEx0(paramFLASHWINFO == null ? null : paramFLASHWINFO.getBuffer());
  }

  private static native boolean FlashWindowEx0(ByteBuffer paramByteBuffer);

  public static native int GetCurrentProcessId();

  public static native int GetLastError();

  public static boolean GetVersionExA(OSVERSIONINFOA paramOSVERSIONINFOA)
  {
    return GetVersionExA0(paramOSVERSIONINFOA == null ? null : paramOSVERSIONINFOA.getBuffer());
  }

  private static native boolean GetVersionExA0(ByteBuffer paramByteBuffer);

  public static native boolean MessageBeep(int paramInt);

  public static native long OpenEventA(int paramInt, boolean paramBoolean, String paramString);

  public static boolean ReadFile(long paramLong, Buffer paramBuffer, int paramInt, IntBuffer paramIntBuffer, OVERLAPPED paramOVERLAPPED)
  {
    if (!BufferFactory.isDirect(paramBuffer))
      throw new RuntimeException("Argument \"lpBuffer\" was not a direct buffer");
    if (!BufferFactory.isDirect(paramIntBuffer))
      throw new RuntimeException("Argument \"lpNumberOfBytesRead\" was not a direct buffer");
    return ReadFile0(paramLong, paramBuffer, BufferFactory.getDirectBufferByteOffset(paramBuffer), paramInt, paramIntBuffer, BufferFactory.getDirectBufferByteOffset(paramIntBuffer), paramOVERLAPPED == null ? null : paramOVERLAPPED.getBuffer());
  }

  private static native boolean ReadFile0(long paramLong, Object paramObject1, int paramInt1, int paramInt2, Object paramObject2, int paramInt3, ByteBuffer paramByteBuffer);

  public static native boolean ResetEvent(long paramLong);

  public static native boolean SetEvent(long paramLong);

  public static native int WaitForSingleObject(long paramLong, int paramInt);

  public static boolean WriteFile(long paramLong, Buffer paramBuffer, int paramInt, IntBuffer paramIntBuffer, OVERLAPPED paramOVERLAPPED)
  {
    if (!BufferFactory.isDirect(paramBuffer))
      throw new RuntimeException("Argument \"lpBuffer\" was not a direct buffer");
    if (!BufferFactory.isDirect(paramIntBuffer))
      throw new RuntimeException("Argument \"lpNumberOfBytesWritten\" was not a direct buffer");
    return WriteFile0(paramLong, paramBuffer, BufferFactory.getDirectBufferByteOffset(paramBuffer), paramInt, paramIntBuffer, BufferFactory.getDirectBufferByteOffset(paramIntBuffer), paramOVERLAPPED == null ? null : paramOVERLAPPED.getBuffer());
  }

  private static native boolean WriteFile0(long paramLong, Object paramObject1, int paramInt1, int paramInt2, Object paramObject2, int paramInt3, ByteBuffer paramByteBuffer);

  static
  {
    String str = System.getProperty("java.version");
    if ((str.startsWith("1.4")) || (str.startsWith("1.5")) || (str.startsWith("1.6")))
      NativeLibLoader.load(new String[] { "msvcr100" });
    NativeLibLoader.load(new String[] { "jp2native" });
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.os.windows.Windows
 * JD-Core Version:    0.6.2
 */