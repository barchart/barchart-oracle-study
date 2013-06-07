/*      */ package java.io;
/*      */ 
/*      */ import java.nio.channels.FileChannel;
/*      */ import sun.nio.ch.FileChannelImpl;
/*      */ 
/*      */ public class RandomAccessFile
/*      */   implements DataOutput, DataInput, Closeable
/*      */ {
/*      */   private FileDescriptor fd;
/*   62 */   private FileChannel channel = null;
/*      */   private boolean rw;
/*   65 */   private Object closeLock = new Object();
/*   66 */   private volatile boolean closed = false;
/*      */   private static final int O_RDONLY = 1;
/*      */   private static final int O_RDWR = 2;
/*      */   private static final int O_SYNC = 4;
/*      */   private static final int O_DSYNC = 8;
/*      */ 
/*      */   public RandomAccessFile(String paramString1, String paramString2)
/*      */     throws FileNotFoundException
/*      */   {
/*  118 */     this(paramString1 != null ? new File(paramString1) : null, paramString2);
/*      */   }
/*      */ 
/*      */   public RandomAccessFile(File paramFile, String paramString)
/*      */     throws FileNotFoundException
/*      */   {
/*  200 */     String str = paramFile != null ? paramFile.getPath() : null;
/*  201 */     int i = -1;
/*  202 */     if (paramString.equals("r")) {
/*  203 */       i = 1;
/*  204 */     } else if (paramString.startsWith("rw")) {
/*  205 */       i = 2;
/*  206 */       this.rw = true;
/*  207 */       if (paramString.length() > 2) {
/*  208 */         if (paramString.equals("rws"))
/*  209 */           i |= 4;
/*  210 */         else if (paramString.equals("rwd"))
/*  211 */           i |= 8;
/*      */         else
/*  213 */           i = -1;
/*      */       }
/*      */     }
/*  216 */     if (i < 0) {
/*  217 */       throw new IllegalArgumentException("Illegal mode \"" + paramString + "\" must be one of " + "\"r\", \"rw\", \"rws\"," + " or \"rwd\"");
/*      */     }
/*      */ 
/*  221 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  222 */     if (localSecurityManager != null) {
/*  223 */       localSecurityManager.checkRead(str);
/*  224 */       if (this.rw) {
/*  225 */         localSecurityManager.checkWrite(str);
/*      */       }
/*      */     }
/*  228 */     if (str == null) {
/*  229 */       throw new NullPointerException();
/*      */     }
/*  231 */     this.fd = new FileDescriptor();
/*  232 */     this.fd.incrementAndGetUseCount();
/*  233 */     open(str, i);
/*      */   }
/*      */ 
/*      */   public final FileDescriptor getFD()
/*      */     throws IOException
/*      */   {
/*  245 */     if (this.fd != null) return this.fd;
/*  246 */     throw new IOException();
/*      */   }
/*      */ 
/*      */   public final FileChannel getChannel()
/*      */   {
/*  268 */     synchronized (this) {
/*  269 */       if (this.channel == null) {
/*  270 */         this.channel = FileChannelImpl.open(this.fd, true, this.rw, this);
/*      */ 
/*  281 */         this.fd.incrementAndGetUseCount();
/*      */       }
/*  283 */       return this.channel;
/*      */     }
/*      */   }
/*      */ 
/*      */   private native void open(String paramString, int paramInt)
/*      */     throws FileNotFoundException;
/*      */ 
/*      */   public native int read()
/*      */     throws IOException;
/*      */ 
/*      */   private native int readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException;
/*      */ 
/*      */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  355 */     return readBytes(paramArrayOfByte, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public int read(byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/*  378 */     return readBytes(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */   }
/*      */ 
/*      */   public final void readFully(byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/*  394 */     readFully(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */   }
/*      */ 
/*      */   public final void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  412 */     int i = 0;
/*      */     do {
/*  414 */       int j = read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
/*  415 */       if (j < 0)
/*  416 */         throw new EOFException();
/*  417 */       i += j;
/*  418 */     }while (i < paramInt2);
/*      */   }
/*      */ 
/*      */   public int skipBytes(int paramInt)
/*      */     throws IOException
/*      */   {
/*  442 */     if (paramInt <= 0) {
/*  443 */       return 0;
/*      */     }
/*  445 */     long l1 = getFilePointer();
/*  446 */     long l2 = length();
/*  447 */     long l3 = l1 + paramInt;
/*  448 */     if (l3 > l2) {
/*  449 */       l3 = l2;
/*      */     }
/*  451 */     seek(l3);
/*      */ 
/*  454 */     return (int)(l3 - l1);
/*      */   }
/*      */ 
/*      */   public native void write(int paramInt)
/*      */     throws IOException;
/*      */ 
/*      */   private native void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException;
/*      */ 
/*      */   public void write(byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/*  486 */     writeBytes(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */   }
/*      */ 
/*      */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  499 */     writeBytes(paramArrayOfByte, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public native long getFilePointer()
/*      */     throws IOException;
/*      */ 
/*      */   public native void seek(long paramLong)
/*      */     throws IOException;
/*      */ 
/*      */   public native long length()
/*      */     throws IOException;
/*      */ 
/*      */   public native void setLength(long paramLong)
/*      */     throws IOException;
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  573 */     synchronized (this.closeLock) {
/*  574 */       if (this.closed) {
/*  575 */         return;
/*      */       }
/*  577 */       this.closed = true;
/*      */     }
/*  579 */     if (this.channel != null)
/*      */     {
/*  585 */       this.fd.decrementAndGetUseCount();
/*  586 */       this.channel.close();
/*      */     }
/*      */ 
/*  593 */     this.fd.decrementAndGetUseCount();
/*  594 */     close0();
/*      */   }
/*      */ 
/*      */   public final boolean readBoolean()
/*      */     throws IOException
/*      */   {
/*  615 */     int i = read();
/*  616 */     if (i < 0)
/*  617 */       throw new EOFException();
/*  618 */     return i != 0;
/*      */   }
/*      */ 
/*      */   public final byte readByte()
/*      */     throws IOException
/*      */   {
/*  640 */     int i = read();
/*  641 */     if (i < 0)
/*  642 */       throw new EOFException();
/*  643 */     return (byte)i;
/*      */   }
/*      */ 
/*      */   public final int readUnsignedByte()
/*      */     throws IOException
/*      */   {
/*  660 */     int i = read();
/*  661 */     if (i < 0)
/*  662 */       throw new EOFException();
/*  663 */     return i;
/*      */   }
/*      */ 
/*      */   public final short readShort()
/*      */     throws IOException
/*      */   {
/*  687 */     int i = read();
/*  688 */     int j = read();
/*  689 */     if ((i | j) < 0)
/*  690 */       throw new EOFException();
/*  691 */     return (short)((i << 8) + (j << 0));
/*      */   }
/*      */ 
/*      */   public final int readUnsignedShort()
/*      */     throws IOException
/*      */   {
/*  715 */     int i = read();
/*  716 */     int j = read();
/*  717 */     if ((i | j) < 0)
/*  718 */       throw new EOFException();
/*  719 */     return (i << 8) + (j << 0);
/*      */   }
/*      */ 
/*      */   public final char readChar()
/*      */     throws IOException
/*      */   {
/*  743 */     int i = read();
/*  744 */     int j = read();
/*  745 */     if ((i | j) < 0)
/*  746 */       throw new EOFException();
/*  747 */     return (char)((i << 8) + (j << 0));
/*      */   }
/*      */ 
/*      */   public final int readInt()
/*      */     throws IOException
/*      */   {
/*  771 */     int i = read();
/*  772 */     int j = read();
/*  773 */     int k = read();
/*  774 */     int m = read();
/*  775 */     if ((i | j | k | m) < 0)
/*  776 */       throw new EOFException();
/*  777 */     return (i << 24) + (j << 16) + (k << 8) + (m << 0);
/*      */   }
/*      */ 
/*      */   public final long readLong()
/*      */     throws IOException
/*      */   {
/*  809 */     return (readInt() << 32) + (readInt() & 0xFFFFFFFF);
/*      */   }
/*      */ 
/*      */   public final float readFloat()
/*      */     throws IOException
/*      */   {
/*  832 */     return Float.intBitsToFloat(readInt());
/*      */   }
/*      */ 
/*      */   public final double readDouble()
/*      */     throws IOException
/*      */   {
/*  855 */     return Double.longBitsToDouble(readLong());
/*      */   }
/*      */ 
/*      */   public final String readLine()
/*      */     throws IOException
/*      */   {
/*  883 */     StringBuffer localStringBuffer = new StringBuffer();
/*  884 */     int i = -1;
/*  885 */     int j = 0;
/*      */ 
/*  887 */     while (j == 0) {
/*  888 */       switch (i = read()) {
/*      */       case -1:
/*      */       case 10:
/*  891 */         j = 1;
/*  892 */         break;
/*      */       case 13:
/*  894 */         j = 1;
/*  895 */         long l = getFilePointer();
/*  896 */         if (read() != 10)
/*  897 */           seek(l); break;
/*      */       default:
/*  901 */         localStringBuffer.append((char)i);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  906 */     if ((i == -1) && (localStringBuffer.length() == 0)) {
/*  907 */       return null;
/*      */     }
/*  909 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   public final String readUTF()
/*      */     throws IOException
/*      */   {
/*  938 */     return DataInputStream.readUTF(this);
/*      */   }
/*      */ 
/*      */   public final void writeBoolean(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  952 */     write(paramBoolean ? 1 : 0);
/*      */   }
/*      */ 
/*      */   public final void writeByte(int paramInt)
/*      */     throws IOException
/*      */   {
/*  964 */     write(paramInt);
/*      */   }
/*      */ 
/*      */   public final void writeShort(int paramInt)
/*      */     throws IOException
/*      */   {
/*  976 */     write(paramInt >>> 8 & 0xFF);
/*  977 */     write(paramInt >>> 0 & 0xFF);
/*      */   }
/*      */ 
/*      */   public final void writeChar(int paramInt)
/*      */     throws IOException
/*      */   {
/*  990 */     write(paramInt >>> 8 & 0xFF);
/*  991 */     write(paramInt >>> 0 & 0xFF);
/*      */   }
/*      */ 
/*      */   public final void writeInt(int paramInt)
/*      */     throws IOException
/*      */   {
/* 1003 */     write(paramInt >>> 24 & 0xFF);
/* 1004 */     write(paramInt >>> 16 & 0xFF);
/* 1005 */     write(paramInt >>> 8 & 0xFF);
/* 1006 */     write(paramInt >>> 0 & 0xFF);
/*      */   }
/*      */ 
/*      */   public final void writeLong(long paramLong)
/*      */     throws IOException
/*      */   {
/* 1018 */     write((int)(paramLong >>> 56) & 0xFF);
/* 1019 */     write((int)(paramLong >>> 48) & 0xFF);
/* 1020 */     write((int)(paramLong >>> 40) & 0xFF);
/* 1021 */     write((int)(paramLong >>> 32) & 0xFF);
/* 1022 */     write((int)(paramLong >>> 24) & 0xFF);
/* 1023 */     write((int)(paramLong >>> 16) & 0xFF);
/* 1024 */     write((int)(paramLong >>> 8) & 0xFF);
/* 1025 */     write((int)(paramLong >>> 0) & 0xFF);
/*      */   }
/*      */ 
/*      */   public final void writeFloat(float paramFloat)
/*      */     throws IOException
/*      */   {
/* 1041 */     writeInt(Float.floatToIntBits(paramFloat));
/*      */   }
/*      */ 
/*      */   public final void writeDouble(double paramDouble)
/*      */     throws IOException
/*      */   {
/* 1056 */     writeLong(Double.doubleToLongBits(paramDouble));
/*      */   }
/*      */ 
/*      */   public final void writeBytes(String paramString)
/*      */     throws IOException
/*      */   {
/* 1069 */     int i = paramString.length();
/* 1070 */     byte[] arrayOfByte = new byte[i];
/* 1071 */     paramString.getBytes(0, i, arrayOfByte, 0);
/* 1072 */     writeBytes(arrayOfByte, 0, i);
/*      */   }
/*      */ 
/*      */   public final void writeChars(String paramString)
/*      */     throws IOException
/*      */   {
/* 1086 */     int i = paramString.length();
/* 1087 */     int j = 2 * i;
/* 1088 */     byte[] arrayOfByte = new byte[j];
/* 1089 */     char[] arrayOfChar = new char[i];
/* 1090 */     paramString.getChars(0, i, arrayOfChar, 0);
/* 1091 */     int k = 0; for (int m = 0; k < i; k++) {
/* 1092 */       arrayOfByte[(m++)] = ((byte)(arrayOfChar[k] >>> '\b'));
/* 1093 */       arrayOfByte[(m++)] = ((byte)(arrayOfChar[k] >>> '\000'));
/*      */     }
/* 1095 */     writeBytes(arrayOfByte, 0, j);
/*      */   }
/*      */ 
/*      */   public final void writeUTF(String paramString)
/*      */     throws IOException
/*      */   {
/* 1115 */     DataOutputStream.writeUTF(paramString, this);
/*      */   }
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   private native void close0() throws IOException;
/*      */ 
/*      */   static {
/* 1123 */     initIDs();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.RandomAccessFile
 * JD-Core Version:    0.6.2
 */