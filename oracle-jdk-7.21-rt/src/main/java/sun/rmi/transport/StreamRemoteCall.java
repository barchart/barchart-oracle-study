/*     */ package sun.rmi.transport;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.io.OutputStream;
/*     */ import java.io.StreamCorruptedException;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.UnmarshalException;
/*     */ import java.rmi.server.ObjID;
/*     */ import java.rmi.server.RemoteCall;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.server.UnicastRef;
/*     */ import sun.rmi.transport.tcp.TCPEndpoint;
/*     */ 
/*     */ public class StreamRemoteCall
/*     */   implements RemoteCall
/*     */ {
/*  49 */   private ConnectionInputStream in = null;
/*  50 */   private ConnectionOutputStream out = null;
/*     */   private Connection conn;
/*  52 */   private boolean resultStarted = false;
/*  53 */   private Exception serverException = null;
/*     */ 
/*     */   public StreamRemoteCall(Connection paramConnection) {
/*  56 */     this.conn = paramConnection;
/*     */   }
/*     */ 
/*     */   public StreamRemoteCall(Connection paramConnection, ObjID paramObjID, int paramInt, long paramLong) throws RemoteException
/*     */   {
/*     */     try
/*     */     {
/*  63 */       this.conn = paramConnection;
/*  64 */       Transport.transportLog.log(Log.VERBOSE, "write remote call header...");
/*     */ 
/*  69 */       this.conn.getOutputStream().write(80);
/*  70 */       getOutputStream();
/*  71 */       paramObjID.write(this.out);
/*     */ 
/*  73 */       this.out.writeInt(paramInt);
/*  74 */       this.out.writeLong(paramLong);
/*     */     } catch (IOException localIOException) {
/*  76 */       throw new MarshalException("Error marshaling call header", localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Connection getConnection()
/*     */   {
/*  84 */     return this.conn;
/*     */   }
/*     */ 
/*     */   public ObjectOutput getOutputStream()
/*     */     throws IOException
/*     */   {
/*  92 */     return getOutputStream(false);
/*     */   }
/*     */ 
/*     */   private ObjectOutput getOutputStream(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  98 */     if (this.out == null) {
/*  99 */       Transport.transportLog.log(Log.VERBOSE, "getting output stream");
/*     */ 
/* 101 */       this.out = new ConnectionOutputStream(this.conn, paramBoolean);
/*     */     }
/* 103 */     return this.out;
/*     */   }
/*     */ 
/*     */   public void releaseOutputStream()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 112 */       if (this.out != null) {
/*     */         try {
/* 114 */           this.out.flush();
/*     */         } finally {
/* 116 */           this.out.done();
/*     */         }
/*     */       }
/* 119 */       this.conn.releaseOutputStream();
/*     */     } finally {
/* 121 */       this.out = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ObjectInput getInputStream()
/*     */     throws IOException
/*     */   {
/* 130 */     if (this.in == null) {
/* 131 */       Transport.transportLog.log(Log.VERBOSE, "getting input stream");
/*     */ 
/* 133 */       this.in = new ConnectionInputStream(this.conn.getInputStream());
/*     */     }
/* 135 */     return this.in;
/*     */   }
/*     */ 
/*     */   public void releaseInputStream()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 148 */       if (this.in != null)
/*     */       {
/*     */         try {
/* 151 */           this.in.done();
/*     */         }
/*     */         catch (RuntimeException localRuntimeException)
/*     */         {
/*     */         }
/* 156 */         this.in.registerRefs();
/*     */ 
/* 161 */         this.in.done(this.conn);
/*     */       }
/* 163 */       this.conn.releaseInputStream();
/*     */     } finally {
/* 165 */       this.in = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ObjectOutput getResultStream(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 180 */     if (this.resultStarted) {
/* 181 */       throw new StreamCorruptedException("result already in progress");
/*     */     }
/* 183 */     this.resultStarted = true;
/*     */ 
/* 187 */     DataOutputStream localDataOutputStream = new DataOutputStream(this.conn.getOutputStream());
/* 188 */     localDataOutputStream.writeByte(81);
/* 189 */     getOutputStream(true);
/*     */ 
/* 191 */     if (paramBoolean)
/* 192 */       this.out.writeByte(1);
/*     */     else
/* 194 */       this.out.writeByte(2);
/* 195 */     this.out.writeID();
/* 196 */     return this.out;
/*     */   }
/*     */ 
/*     */   public void executeCall()
/*     */     throws Exception
/*     */   {
/* 206 */     DGCAckHandler localDGCAckHandler = null;
/*     */     int i;
/*     */     try
/*     */     {
/* 208 */       if (this.out != null) {
/* 209 */         localDGCAckHandler = this.out.getDGCAckHandler();
/*     */       }
/* 211 */       releaseOutputStream();
/* 212 */       DataInputStream localDataInputStream = new DataInputStream(this.conn.getInputStream());
/* 213 */       int j = localDataInputStream.readByte();
/* 214 */       if (j != 81) {
/* 215 */         if (Transport.transportLog.isLoggable(Log.BRIEF)) {
/* 216 */           Transport.transportLog.log(Log.BRIEF, "transport return code invalid: " + j);
/*     */         }
/*     */ 
/* 219 */         throw new UnmarshalException("Transport return code invalid");
/*     */       }
/* 221 */       getInputStream();
/* 222 */       i = this.in.readByte();
/* 223 */       this.in.readID();
/*     */     } catch (UnmarshalException localUnmarshalException) {
/* 225 */       throw localUnmarshalException;
/*     */     } catch (IOException localIOException) {
/* 227 */       throw new UnmarshalException("Error unmarshaling return header", localIOException);
/*     */     }
/*     */     finally {
/* 230 */       if (localDGCAckHandler != null) {
/* 231 */         localDGCAckHandler.release();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 236 */     switch (i) {
/*     */     case 1:
/* 238 */       break;
/*     */     case 2:
/*     */       Object localObject1;
/*     */       try
/*     */       {
/* 243 */         localObject1 = this.in.readObject();
/*     */       } catch (Exception localException) {
/* 245 */         throw new UnmarshalException("Error unmarshaling return", localException);
/*     */       }
/*     */ 
/* 250 */       if ((localObject1 instanceof Exception))
/* 251 */         exceptionReceivedFromServer((Exception)localObject1);
/*     */       else
/* 253 */         throw new UnmarshalException("Return type not Exception");
/*     */       break;
/*     */     }
/* 256 */     if (Transport.transportLog.isLoggable(Log.BRIEF)) {
/* 257 */       Transport.transportLog.log(Log.BRIEF, "return code invalid: " + i);
/*     */     }
/*     */ 
/* 260 */     throw new UnmarshalException("Return code invalid");
/*     */   }
/*     */ 
/*     */   protected void exceptionReceivedFromServer(Exception paramException)
/*     */     throws Exception
/*     */   {
/* 270 */     this.serverException = paramException;
/*     */ 
/* 272 */     StackTraceElement[] arrayOfStackTraceElement1 = paramException.getStackTrace();
/* 273 */     StackTraceElement[] arrayOfStackTraceElement2 = new Throwable().getStackTrace();
/* 274 */     StackTraceElement[] arrayOfStackTraceElement3 = new StackTraceElement[arrayOfStackTraceElement1.length + arrayOfStackTraceElement2.length];
/*     */ 
/* 276 */     System.arraycopy(arrayOfStackTraceElement1, 0, arrayOfStackTraceElement3, 0, arrayOfStackTraceElement1.length);
/*     */ 
/* 278 */     System.arraycopy(arrayOfStackTraceElement2, 0, arrayOfStackTraceElement3, arrayOfStackTraceElement1.length, arrayOfStackTraceElement2.length);
/*     */ 
/* 280 */     paramException.setStackTrace(arrayOfStackTraceElement3);
/*     */ 
/* 286 */     if (UnicastRef.clientCallLog.isLoggable(Log.BRIEF))
/*     */     {
/* 288 */       TCPEndpoint localTCPEndpoint = (TCPEndpoint)this.conn.getChannel().getEndpoint();
/* 289 */       UnicastRef.clientCallLog.log(Log.BRIEF, "outbound call received exception: [" + localTCPEndpoint.getHost() + ":" + localTCPEndpoint.getPort() + "] exception: ", paramException);
/*     */     }
/*     */ 
/* 294 */     throw paramException;
/*     */   }
/*     */ 
/*     */   public Exception getServerException()
/*     */   {
/* 302 */     return this.serverException;
/*     */   }
/*     */ 
/*     */   public void done()
/*     */     throws IOException
/*     */   {
/* 310 */     releaseInputStream();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.StreamRemoteCall
 * JD-Core Version:    0.6.2
 */