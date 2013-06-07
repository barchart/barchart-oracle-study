/*     */ package java.net;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import sun.net.ConnectionResetException;
/*     */ 
/*     */ class SocketInputStream extends FileInputStream
/*     */ {
/*     */   private boolean eof;
/*  50 */   private AbstractPlainSocketImpl impl = null;
/*     */   private byte[] temp;
/*  52 */   private Socket socket = null;
/*     */ 
/* 244 */   private boolean closing = false;
/*     */ 
/*     */   SocketInputStream(AbstractPlainSocketImpl paramAbstractPlainSocketImpl)
/*     */     throws IOException
/*     */   {
/*  61 */     super(paramAbstractPlainSocketImpl.getFileDescriptor());
/*  62 */     this.impl = paramAbstractPlainSocketImpl;
/*  63 */     this.socket = paramAbstractPlainSocketImpl.getSocket();
/*     */   }
/*     */ 
/*     */   public final FileChannel getChannel()
/*     */   {
/*  79 */     return null;
/*     */   }
/*     */ 
/*     */   private native int socketRead0(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException;
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 107 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 121 */     return read(paramArrayOfByte, paramInt1, paramInt2, this.impl.getTimeout());
/*     */   }
/*     */ 
/*     */   int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 128 */     if (this.eof) {
/* 129 */       return -1;
/*     */     }
/*     */ 
/* 133 */     if (this.impl.isConnectionReset()) {
/* 134 */       throw new SocketException("Connection reset");
/*     */     }
/*     */ 
/* 138 */     if ((paramInt2 <= 0) || (paramInt1 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length)) {
/* 139 */       if (paramInt2 == 0) {
/* 140 */         return 0;
/*     */       }
/* 142 */       throw new ArrayIndexOutOfBoundsException();
/* 145 */     }
/*     */ int i = 0;
/*     */ 
/* 148 */     FileDescriptor localFileDescriptor = this.impl.acquireFD();
/*     */     ConnectionResetException localConnectionResetException1;
/*     */     try { localConnectionResetException1 = socketRead0(localFileDescriptor, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
/* 151 */       if (localConnectionResetException1 > 0)
/* 152 */         return localConnectionResetException1;
/*     */     } catch (ConnectionResetException localConnectionResetException2)
/*     */     {
/* 155 */       i = 1;
/*     */     } finally {
/* 157 */       this.impl.releaseFD();
/*     */     }
/*     */ 
/* 164 */     if (i != 0) {
/* 165 */       this.impl.setConnectionResetPending();
/* 166 */       this.impl.acquireFD();
/*     */       try {
/* 168 */         localConnectionResetException1 = socketRead0(localFileDescriptor, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
/* 169 */         if (localConnectionResetException1 > 0)
/* 170 */           return localConnectionResetException1;
/*     */       } catch (ConnectionResetException localConnectionResetException3) {
/*     */       }
/*     */       finally {
/* 174 */         this.impl.releaseFD();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 182 */     if (this.impl.isClosedOrPending()) {
/* 183 */       throw new SocketException("Socket closed");
/*     */     }
/* 185 */     if (this.impl.isConnectionResetPending()) {
/* 186 */       this.impl.setConnectionReset();
/*     */     }
/* 188 */     if (this.impl.isConnectionReset()) {
/* 189 */       throw new SocketException("Connection reset");
/*     */     }
/* 191 */     this.eof = true;
/* 192 */     return -1;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/* 199 */     if (this.eof) {
/* 200 */       return -1;
/*     */     }
/* 202 */     this.temp = new byte[1];
/* 203 */     int i = read(this.temp, 0, 1);
/* 204 */     if (i <= 0) {
/* 205 */       return -1;
/*     */     }
/* 207 */     return this.temp[0] & 0xFF;
/*     */   }
/*     */ 
/*     */   public long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 217 */     if (paramLong <= 0L) {
/* 218 */       return 0L;
/*     */     }
/* 220 */     long l = paramLong;
/* 221 */     int i = (int)Math.min(1024L, l);
/* 222 */     byte[] arrayOfByte = new byte[i];
/* 223 */     while (l > 0L) {
/* 224 */       int j = read(arrayOfByte, 0, (int)Math.min(i, l));
/* 225 */       if (j < 0) {
/*     */         break;
/*     */       }
/* 228 */       l -= j;
/*     */     }
/* 230 */     return paramLong - l;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 238 */     return this.impl.available();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 247 */     if (this.closing)
/* 248 */       return;
/* 249 */     this.closing = true;
/* 250 */     if (this.socket != null) {
/* 251 */       if (!this.socket.isClosed())
/* 252 */         this.socket.close();
/*     */     }
/* 254 */     else this.impl.close();
/* 255 */     this.closing = false;
/*     */   }
/*     */ 
/*     */   void setEOF(boolean paramBoolean) {
/* 259 */     this.eof = paramBoolean;
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/*     */   }
/*     */ 
/*     */   private static native void init();
/*     */ 
/*     */   static
/*     */   {
/*  46 */     init();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.SocketInputStream
 * JD-Core Version:    0.6.2
 */