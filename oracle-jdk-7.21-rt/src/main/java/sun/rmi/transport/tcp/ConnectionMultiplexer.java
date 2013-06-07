/*     */ package sun.rmi.transport.tcp;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.rmi.server.LogStream;
/*     */ import java.security.AccessController;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.transport.Connection;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ final class ConnectionMultiplexer
/*     */ {
/*  49 */   static int logLevel = LogStream.parseLevel(getLogLevel());
/*     */ 
/*  57 */   static final Log multiplexLog = Log.getLog("sun.rmi.transport.tcp.multiplex", "multiplex", logLevel);
/*     */   private static final int OPEN = 225;
/*     */   private static final int CLOSE = 226;
/*     */   private static final int CLOSEACK = 227;
/*     */   private static final int REQUEST = 228;
/*     */   private static final int TRANSMIT = 229;
/*     */   private TCPChannel channel;
/*     */   private InputStream in;
/*     */   private OutputStream out;
/*     */   private boolean orig;
/*     */   private DataInputStream dataIn;
/*     */   private DataOutputStream dataOut;
/*  88 */   private Hashtable connectionTable = new Hashtable(7);
/*     */ 
/*  91 */   private int numConnections = 0;
/*     */   private static final int maxConnections = 256;
/*  97 */   private int lastID = 4097;
/*     */ 
/* 100 */   private boolean alive = true;
/*     */ 
/*     */   private static String getLogLevel()
/*     */   {
/*  52 */     return (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.tcp.multiplex.logLevel"));
/*     */   }
/*     */ 
/*     */   public ConnectionMultiplexer(TCPChannel paramTCPChannel, InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean)
/*     */   {
/* 118 */     this.channel = paramTCPChannel;
/* 119 */     this.in = paramInputStream;
/* 120 */     this.out = paramOutputStream;
/* 121 */     this.orig = paramBoolean;
/*     */ 
/* 123 */     this.dataIn = new DataInputStream(paramInputStream);
/* 124 */     this.dataOut = new DataOutputStream(paramOutputStream);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 140 */       int i = this.dataIn.readUnsignedByte();
/*     */       int j;
/*     */       Integer localInteger;
/*     */       MultiplexConnectionInfo localMultiplexConnectionInfo;
/*     */       int k;
/* 141 */       switch (i)
/*     */       {
/*     */       case 225:
/* 145 */         j = this.dataIn.readUnsignedShort();
/*     */ 
/* 147 */         if (multiplexLog.isLoggable(Log.VERBOSE)) {
/* 148 */           multiplexLog.log(Log.VERBOSE, "operation  OPEN " + j);
/*     */         }
/*     */ 
/* 151 */         localInteger = new Integer(j);
/* 152 */         localMultiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(localInteger);
/*     */ 
/* 154 */         if (localMultiplexConnectionInfo != null) {
/* 155 */           throw new IOException("OPEN: Connection ID already exists");
/*     */         }
/* 157 */         localMultiplexConnectionInfo = new MultiplexConnectionInfo(j);
/* 158 */         localMultiplexConnectionInfo.in = new MultiplexInputStream(this, localMultiplexConnectionInfo, 2048);
/* 159 */         localMultiplexConnectionInfo.out = new MultiplexOutputStream(this, localMultiplexConnectionInfo, 2048);
/* 160 */         synchronized (this.connectionTable) {
/* 161 */           this.connectionTable.put(localInteger, localMultiplexConnectionInfo);
/* 162 */           this.numConnections += 1;
/*     */         }
/*     */ 
/* 165 */         ??? = new TCPConnection(this.channel, localMultiplexConnectionInfo.in, localMultiplexConnectionInfo.out);
/* 166 */         this.channel.acceptMultiplexConnection((Connection)???);
/* 167 */         break;
/*     */       case 226:
/* 171 */         j = this.dataIn.readUnsignedShort();
/*     */ 
/* 173 */         if (multiplexLog.isLoggable(Log.VERBOSE)) {
/* 174 */           multiplexLog.log(Log.VERBOSE, "operation  CLOSE " + j);
/*     */         }
/*     */ 
/* 177 */         localInteger = new Integer(j);
/* 178 */         localMultiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(localInteger);
/*     */ 
/* 180 */         if (localMultiplexConnectionInfo == null) {
/* 181 */           throw new IOException("CLOSE: Invalid connection ID");
/*     */         }
/* 183 */         localMultiplexConnectionInfo.in.disconnect();
/* 184 */         localMultiplexConnectionInfo.out.disconnect();
/* 185 */         if (!localMultiplexConnectionInfo.closed)
/* 186 */           sendCloseAck(localMultiplexConnectionInfo);
/* 187 */         synchronized (this.connectionTable) {
/* 188 */           this.connectionTable.remove(localInteger);
/* 189 */           this.numConnections -= 1;
/*     */         }
/* 191 */         break;
/*     */       case 227:
/* 195 */         j = this.dataIn.readUnsignedShort();
/*     */ 
/* 197 */         if (multiplexLog.isLoggable(Log.VERBOSE)) {
/* 198 */           multiplexLog.log(Log.VERBOSE, "operation  CLOSEACK " + j);
/*     */         }
/*     */ 
/* 202 */         localInteger = new Integer(j);
/* 203 */         localMultiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(localInteger);
/*     */ 
/* 205 */         if (localMultiplexConnectionInfo == null) {
/* 206 */           throw new IOException("CLOSEACK: Invalid connection ID");
/*     */         }
/* 208 */         if (!localMultiplexConnectionInfo.closed) {
/* 209 */           throw new IOException("CLOSEACK: Connection not closed");
/*     */         }
/* 211 */         localMultiplexConnectionInfo.in.disconnect();
/* 212 */         localMultiplexConnectionInfo.out.disconnect();
/* 213 */         synchronized (this.connectionTable) {
/* 214 */           this.connectionTable.remove(localInteger);
/* 215 */           this.numConnections -= 1;
/*     */         }
/* 217 */         break;
/*     */       case 228:
/* 221 */         j = this.dataIn.readUnsignedShort();
/* 222 */         localInteger = new Integer(j);
/* 223 */         localMultiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(localInteger);
/*     */ 
/* 225 */         if (localMultiplexConnectionInfo == null) {
/* 226 */           throw new IOException("REQUEST: Invalid connection ID");
/*     */         }
/* 228 */         k = this.dataIn.readInt();
/*     */ 
/* 230 */         if (multiplexLog.isLoggable(Log.VERBOSE)) {
/* 231 */           multiplexLog.log(Log.VERBOSE, "operation  REQUEST " + j + ": " + k);
/*     */         }
/*     */ 
/* 235 */         localMultiplexConnectionInfo.out.request(k);
/* 236 */         break;
/*     */       case 229:
/* 240 */         j = this.dataIn.readUnsignedShort();
/* 241 */         localInteger = new Integer(j);
/* 242 */         localMultiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(localInteger);
/*     */ 
/* 244 */         if (localMultiplexConnectionInfo == null)
/* 245 */           throw new IOException("SEND: Invalid connection ID");
/* 246 */         k = this.dataIn.readInt();
/*     */ 
/* 248 */         if (multiplexLog.isLoggable(Log.VERBOSE)) {
/* 249 */           multiplexLog.log(Log.VERBOSE, "operation  TRANSMIT " + j + ": " + k);
/*     */         }
/*     */ 
/* 253 */         localMultiplexConnectionInfo.in.receive(k, this.dataIn);
/* 254 */         break;
/*     */       default:
/* 257 */         throw new IOException("Invalid operation: " + Integer.toHexString(i));
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 262 */       shutDown();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized TCPConnection openConnection()
/*     */     throws IOException
/*     */   {
/*     */     int i;
/*     */     Integer localInteger;
/*     */     do
/*     */     {
/* 278 */       this.lastID = (++this.lastID & 0x7FFF);
/* 279 */       i = this.lastID;
/*     */ 
/* 284 */       if (this.orig)
/* 285 */         i |= 32768;
/* 286 */       localInteger = new Integer(i);
/* 287 */     }while (this.connectionTable.get(localInteger) != null);
/*     */ 
/* 290 */     MultiplexConnectionInfo localMultiplexConnectionInfo = new MultiplexConnectionInfo(i);
/* 291 */     localMultiplexConnectionInfo.in = new MultiplexInputStream(this, localMultiplexConnectionInfo, 2048);
/* 292 */     localMultiplexConnectionInfo.out = new MultiplexOutputStream(this, localMultiplexConnectionInfo, 2048);
/*     */ 
/* 295 */     synchronized (this.connectionTable) {
/* 296 */       if (!this.alive)
/* 297 */         throw new IOException("Multiplexer connection dead");
/* 298 */       if (this.numConnections >= 256) {
/* 299 */         throw new IOException("Cannot exceed 256 simultaneous multiplexed connections");
/*     */       }
/* 301 */       this.connectionTable.put(localInteger, localMultiplexConnectionInfo);
/* 302 */       this.numConnections += 1;
/*     */     }
/*     */ 
/* 306 */     synchronized (this.dataOut) {
/*     */       try {
/* 308 */         this.dataOut.writeByte(225);
/* 309 */         this.dataOut.writeShort(i);
/* 310 */         this.dataOut.flush();
/*     */       } catch (IOException localIOException) {
/* 312 */         multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
/*     */ 
/* 314 */         shutDown();
/* 315 */         throw localIOException;
/*     */       }
/*     */     }
/*     */ 
/* 319 */     return new TCPConnection(this.channel, localMultiplexConnectionInfo.in, localMultiplexConnectionInfo.out);
/*     */   }
/*     */ 
/*     */   public void shutDown()
/*     */   {
/* 328 */     synchronized (this.connectionTable)
/*     */     {
/* 330 */       if (!this.alive)
/* 331 */         return;
/* 332 */       this.alive = false;
/*     */ 
/* 334 */       Enumeration localEnumeration = this.connectionTable.elements();
/* 335 */       while (localEnumeration.hasMoreElements()) {
/* 336 */         MultiplexConnectionInfo localMultiplexConnectionInfo = (MultiplexConnectionInfo)localEnumeration.nextElement();
/*     */ 
/* 338 */         localMultiplexConnectionInfo.in.disconnect();
/* 339 */         localMultiplexConnectionInfo.out.disconnect();
/*     */       }
/* 341 */       this.connectionTable.clear();
/* 342 */       this.numConnections = 0;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 347 */       this.in.close();
/*     */     } catch (IOException localIOException1) {
/*     */     }
/*     */     try {
/* 351 */       this.out.close();
/*     */     }
/*     */     catch (IOException localIOException2)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   void sendRequest(MultiplexConnectionInfo paramMultiplexConnectionInfo, int paramInt)
/*     */     throws IOException
/*     */   {
/* 363 */     synchronized (this.dataOut) {
/* 364 */       if ((this.alive) && (!paramMultiplexConnectionInfo.closed))
/*     */         try {
/* 366 */           this.dataOut.writeByte(228);
/* 367 */           this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
/* 368 */           this.dataOut.writeInt(paramInt);
/* 369 */           this.dataOut.flush();
/*     */         } catch (IOException localIOException) {
/* 371 */           multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
/*     */ 
/* 373 */           shutDown();
/* 374 */           throw localIOException;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   void sendTransmit(MultiplexConnectionInfo paramMultiplexConnectionInfo, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 389 */     synchronized (this.dataOut) {
/* 390 */       if ((this.alive) && (!paramMultiplexConnectionInfo.closed))
/*     */         try {
/* 392 */           this.dataOut.writeByte(229);
/* 393 */           this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
/* 394 */           this.dataOut.writeInt(paramInt2);
/* 395 */           this.dataOut.write(paramArrayOfByte, paramInt1, paramInt2);
/* 396 */           this.dataOut.flush();
/*     */         } catch (IOException localIOException) {
/* 398 */           multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
/*     */ 
/* 400 */           shutDown();
/* 401 */           throw localIOException;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   void sendClose(MultiplexConnectionInfo paramMultiplexConnectionInfo)
/*     */     throws IOException
/*     */   {
/* 412 */     paramMultiplexConnectionInfo.out.disconnect();
/* 413 */     synchronized (this.dataOut) {
/* 414 */       if ((this.alive) && (!paramMultiplexConnectionInfo.closed))
/*     */         try {
/* 416 */           this.dataOut.writeByte(226);
/* 417 */           this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
/* 418 */           this.dataOut.flush();
/* 419 */           paramMultiplexConnectionInfo.closed = true;
/*     */         } catch (IOException localIOException) {
/* 421 */           multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
/*     */ 
/* 423 */           shutDown();
/* 424 */           throw localIOException;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   void sendCloseAck(MultiplexConnectionInfo paramMultiplexConnectionInfo)
/*     */     throws IOException
/*     */   {
/* 435 */     synchronized (this.dataOut) {
/* 436 */       if ((this.alive) && (!paramMultiplexConnectionInfo.closed))
/*     */         try {
/* 438 */           this.dataOut.writeByte(227);
/* 439 */           this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
/* 440 */           this.dataOut.flush();
/* 441 */           paramMultiplexConnectionInfo.closed = true;
/*     */         } catch (IOException localIOException) {
/* 443 */           multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
/*     */ 
/* 445 */           shutDown();
/* 446 */           throw localIOException;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/* 456 */     super.finalize();
/* 457 */     shutDown();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.tcp.ConnectionMultiplexer
 * JD-Core Version:    0.6.2
 */