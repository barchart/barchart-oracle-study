/*     */ package com.sun.jndi.ldap;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Socket;
/*     */ import javax.naming.CommunicationException;
/*     */ import javax.naming.InterruptedNamingException;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.ServiceUnavailableException;
/*     */ import javax.naming.ldap.Control;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ 
/*     */ public final class Connection
/*     */   implements Runnable
/*     */ {
/*     */   private static final boolean debug = false;
/*     */   private static final int dump = 0;
/*     */   private final Thread worker;
/* 118 */   private boolean v3 = true;
/*     */   public final String host;
/*     */   public final int port;
/* 125 */   private boolean bound = false;
/*     */ 
/* 128 */   private OutputStream traceFile = null;
/* 129 */   private String traceTagIn = null;
/* 130 */   private String traceTagOut = null;
/*     */   public InputStream inStream;
/*     */   public OutputStream outStream;
/*     */   public Socket sock;
/*     */   private final LdapClient parent;
/* 149 */   private int outMsgId = 0;
/*     */ 
/* 155 */   private LdapRequest pendingRequests = null;
/*     */ 
/* 157 */   volatile IOException closureReason = null;
/* 158 */   volatile boolean useable = true;
/*     */   private int readTimeout;
/* 775 */   private Object pauseLock = new Object();
/* 776 */   private boolean paused = false;
/*     */ 
/*     */   void setV3(boolean paramBoolean)
/*     */   {
/* 166 */     this.v3 = paramBoolean;
/*     */   }
/*     */ 
/*     */   void setBound()
/*     */   {
/* 174 */     this.bound = true;
/*     */   }
/*     */ 
/*     */   Connection(LdapClient paramLdapClient, String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream)
/*     */     throws NamingException
/*     */   {
/* 186 */     this.host = paramString1;
/* 187 */     this.port = paramInt1;
/* 188 */     this.parent = paramLdapClient;
/* 189 */     this.readTimeout = paramInt3;
/*     */ 
/* 191 */     if (paramOutputStream != null) {
/* 192 */       this.traceFile = paramOutputStream;
/* 193 */       this.traceTagIn = ("<- " + paramString1 + ":" + paramInt1 + "\n\n");
/* 194 */       this.traceTagOut = ("-> " + paramString1 + ":" + paramInt1 + "\n\n");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 201 */       this.sock = createSocket(paramString1, paramInt1, paramString2, paramInt2);
/*     */ 
/* 207 */       this.inStream = new BufferedInputStream(this.sock.getInputStream());
/* 208 */       this.outStream = new BufferedOutputStream(this.sock.getOutputStream());
/*     */     }
/*     */     catch (InvocationTargetException localInvocationTargetException) {
/* 211 */       localObject = localInvocationTargetException.getTargetException();
/*     */ 
/* 214 */       CommunicationException localCommunicationException = new CommunicationException(paramString1 + ":" + paramInt1);
/*     */ 
/* 216 */       localCommunicationException.setRootCause((Throwable)localObject);
/* 217 */       throw localCommunicationException;
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 224 */       Object localObject = new CommunicationException(paramString1 + ":" + paramInt1);
/*     */ 
/* 226 */       ((CommunicationException)localObject).setRootCause(localException);
/* 227 */       throw ((Throwable)localObject);
/*     */     }
/*     */ 
/* 230 */     this.worker = Obj.helper.createThread(this);
/* 231 */     this.worker.setDaemon(true);
/* 232 */     this.worker.start();
/*     */   }
/*     */ 
/*     */   private Object createInetSocketAddress(String paramString, int paramInt)
/*     */     throws NoSuchMethodException
/*     */   {
/*     */     try
/*     */     {
/* 242 */       Class localClass = Class.forName("java.net.InetSocketAddress");
/*     */ 
/* 245 */       Constructor localConstructor = localClass.getConstructor(new Class[] { String.class, Integer.TYPE });
/*     */ 
/* 249 */       return localConstructor.newInstance(new Object[] { paramString, new Integer(paramInt) });
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException)
/*     */     {
/* 253 */       throw new NoSuchMethodException();
/*     */     }
/*     */     catch (InstantiationException localInstantiationException) {
/* 256 */       throw new NoSuchMethodException();
/*     */     }
/*     */     catch (InvocationTargetException localInvocationTargetException) {
/* 259 */       throw new NoSuchMethodException();
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/*     */     }
/* 262 */     throw new NoSuchMethodException();
/*     */   }
/*     */ 
/*     */   private Socket createSocket(String paramString1, int paramInt1, String paramString2, int paramInt2)
/*     */     throws Exception
/*     */   {
/* 278 */     Socket localSocket = null;
/*     */     Object localObject1;
/*     */     Method localMethod1;
/*     */     Object localObject2;
/* 280 */     if (paramString2 != null)
/*     */     {
/* 284 */       localObject1 = Obj.helper.loadClass(paramString2);
/* 285 */       localMethod1 = ((Class)localObject1).getMethod("getDefault", new Class[0]);
/*     */ 
/* 287 */       localObject2 = localMethod1.invoke(null, new Object[0]);
/*     */ 
/* 291 */       Method localMethod2 = null;
/*     */ 
/* 293 */       if (paramInt2 > 0) {
/*     */         try
/*     */         {
/* 296 */           localMethod2 = ((Class)localObject1).getMethod("createSocket", new Class[0]);
/*     */ 
/* 299 */           Method localMethod3 = Socket.class.getMethod("connect", new Class[] { Class.forName("java.net.SocketAddress"), Integer.TYPE });
/*     */ 
/* 302 */           Object localObject3 = createInetSocketAddress(paramString1, paramInt1);
/*     */ 
/* 305 */           localSocket = (Socket)localMethod2.invoke(localObject2, new Object[0]);
/*     */ 
/* 314 */           localMethod3.invoke(localSocket, new Object[] { localObject3, new Integer(paramInt2) });
/*     */         }
/*     */         catch (NoSuchMethodException localNoSuchMethodException2)
/*     */         {
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 322 */       if (localSocket == null) {
/* 323 */         localMethod2 = ((Class)localObject1).getMethod("createSocket", new Class[] { String.class, Integer.TYPE });
/*     */ 
/* 331 */         localSocket = (Socket)localMethod2.invoke(localObject2, new Object[] { paramString1, new Integer(paramInt1) });
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 336 */       if (paramInt2 > 0) {
/*     */         try
/*     */         {
/* 339 */           localObject1 = Socket.class.getConstructor(new Class[0]);
/*     */ 
/* 342 */           localMethod1 = Socket.class.getMethod("connect", new Class[] { Class.forName("java.net.SocketAddress"), Integer.TYPE });
/*     */ 
/* 345 */           localObject2 = createInetSocketAddress(paramString1, paramInt1);
/*     */ 
/* 347 */           localSocket = (Socket)((Constructor)localObject1).newInstance(new Object[0]);
/*     */ 
/* 353 */           localMethod1.invoke(localSocket, new Object[] { localObject2, new Integer(paramInt2) });
/*     */         }
/*     */         catch (NoSuchMethodException localNoSuchMethodException1)
/*     */         {
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 361 */       if (localSocket == null)
/*     */       {
/* 366 */         localSocket = new Socket(paramString1, paramInt1);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 374 */     if ((paramInt2 > 0) && ((localSocket instanceof SSLSocket))) {
/* 375 */       SSLSocket localSSLSocket = (SSLSocket)localSocket;
/* 376 */       int i = localSSLSocket.getSoTimeout();
/*     */ 
/* 378 */       localSSLSocket.setSoTimeout(paramInt2);
/* 379 */       localSSLSocket.startHandshake();
/* 380 */       localSSLSocket.setSoTimeout(i);
/*     */     }
/*     */ 
/* 383 */     return localSocket;
/*     */   }
/*     */ 
/*     */   synchronized int getMsgId()
/*     */   {
/* 393 */     return ++this.outMsgId;
/*     */   }
/*     */ 
/*     */   LdapRequest writeRequest(BerEncoder paramBerEncoder, int paramInt) throws IOException {
/* 397 */     return writeRequest(paramBerEncoder, paramInt, false, -1);
/*     */   }
/*     */ 
/*     */   LdapRequest writeRequest(BerEncoder paramBerEncoder, int paramInt, boolean paramBoolean) throws IOException
/*     */   {
/* 402 */     return writeRequest(paramBerEncoder, paramInt, paramBoolean, -1);
/*     */   }
/*     */ 
/*     */   LdapRequest writeRequest(BerEncoder paramBerEncoder, int paramInt1, boolean paramBoolean, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 408 */     LdapRequest localLdapRequest = new LdapRequest(paramInt1, paramBoolean, paramInt2);
/*     */ 
/* 410 */     addRequest(localLdapRequest);
/*     */ 
/* 412 */     if (this.traceFile != null) {
/* 413 */       Ber.dumpBER(this.traceFile, this.traceTagOut, paramBerEncoder.getBuf(), 0, paramBerEncoder.getDataLen());
/*     */     }
/*     */ 
/* 420 */     unpauseReader();
/*     */     try
/*     */     {
/* 427 */       synchronized (this) {
/* 428 */         this.outStream.write(paramBerEncoder.getBuf(), 0, paramBerEncoder.getDataLen());
/* 429 */         this.outStream.flush();
/*     */       }
/*     */     } catch (IOException localIOException) {
/* 432 */       cleanup(null, true);
/* 433 */       throw (this.closureReason = localIOException);
/*     */     }
/*     */ 
/* 436 */     return localLdapRequest;
/*     */   }
/*     */ 
/*     */   BerDecoder readReply(LdapRequest paramLdapRequest)
/*     */     throws IOException, NamingException
/*     */   {
/* 445 */     int i = 0;
/*     */     BerDecoder localBerDecoder;
/* 447 */     while (((localBerDecoder = paramLdapRequest.getReplyBer()) == null) && (i == 0)) {
/*     */       try
/*     */       {
/* 450 */         synchronized (this) {
/* 451 */           if (this.sock == null) {
/* 452 */             throw new ServiceUnavailableException(this.host + ":" + this.port + "; socket closed");
/*     */           }
/*     */         }
/*     */ 
/* 456 */         synchronized (paramLdapRequest)
/*     */         {
/* 458 */           localBerDecoder = paramLdapRequest.getReplyBer();
/* 459 */           if (localBerDecoder == null) {
/* 460 */             if (this.readTimeout > 0)
/*     */             {
/* 464 */               paramLdapRequest.wait(this.readTimeout);
/* 465 */               i = 1;
/*     */             } else {
/* 467 */               paramLdapRequest.wait(15000L);
/*     */             }
/*     */           }
/* 470 */           else break; 
/*     */         }
/*     */       }
/*     */       catch (InterruptedException localInterruptedException)
/*     */       {
/* 474 */         throw new InterruptedNamingException("Interrupted during LDAP operation");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 479 */     if ((localBerDecoder == null) && (i != 0)) {
/* 480 */       removeRequest(paramLdapRequest);
/* 481 */       throw new NamingException("LDAP response read timed out, timeout used:" + this.readTimeout + "ms.");
/*     */     }
/*     */ 
/* 485 */     return localBerDecoder;
/*     */   }
/*     */ 
/*     */   private synchronized void addRequest(LdapRequest paramLdapRequest)
/*     */   {
/* 497 */     LdapRequest localLdapRequest = this.pendingRequests;
/* 498 */     if (localLdapRequest == null) {
/* 499 */       this.pendingRequests = paramLdapRequest;
/* 500 */       paramLdapRequest.next = null;
/*     */     } else {
/* 502 */       paramLdapRequest.next = this.pendingRequests;
/* 503 */       this.pendingRequests = paramLdapRequest;
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized LdapRequest findRequest(int paramInt)
/*     */   {
/* 509 */     LdapRequest localLdapRequest = this.pendingRequests;
/* 510 */     while (localLdapRequest != null) {
/* 511 */       if (localLdapRequest.msgId == paramInt) {
/* 512 */         return localLdapRequest;
/*     */       }
/* 514 */       localLdapRequest = localLdapRequest.next;
/*     */     }
/* 516 */     return null;
/*     */   }
/*     */ 
/*     */   synchronized void removeRequest(LdapRequest paramLdapRequest)
/*     */   {
/* 521 */     LdapRequest localLdapRequest1 = this.pendingRequests;
/* 522 */     LdapRequest localLdapRequest2 = null;
/*     */ 
/* 524 */     while (localLdapRequest1 != null) {
/* 525 */       if (localLdapRequest1 == paramLdapRequest) {
/* 526 */         localLdapRequest1.cancel();
/*     */ 
/* 528 */         if (localLdapRequest2 != null)
/* 529 */           localLdapRequest2.next = localLdapRequest1.next;
/*     */         else {
/* 531 */           this.pendingRequests = localLdapRequest1.next;
/*     */         }
/* 533 */         localLdapRequest1.next = null;
/*     */       }
/* 535 */       localLdapRequest2 = localLdapRequest1;
/* 536 */       localLdapRequest1 = localLdapRequest1.next;
/*     */     }
/*     */   }
/*     */ 
/*     */   void abandonRequest(LdapRequest paramLdapRequest, Control[] paramArrayOfControl)
/*     */   {
/* 542 */     removeRequest(paramLdapRequest);
/*     */ 
/* 544 */     BerEncoder localBerEncoder = new BerEncoder(256);
/* 545 */     int i = getMsgId();
/*     */     try
/*     */     {
/* 551 */       localBerEncoder.beginSeq(48);
/* 552 */       localBerEncoder.encodeInt(i);
/* 553 */       localBerEncoder.encodeInt(paramLdapRequest.msgId, 80);
/*     */ 
/* 555 */       if (this.v3) {
/* 556 */         LdapClient.encodeControls(localBerEncoder, paramArrayOfControl);
/*     */       }
/* 558 */       localBerEncoder.endSeq();
/*     */ 
/* 560 */       if (this.traceFile != null) {
/* 561 */         Ber.dumpBER(this.traceFile, this.traceTagOut, localBerEncoder.getBuf(), 0, localBerEncoder.getDataLen());
/*     */       }
/*     */ 
/* 565 */       synchronized (this) {
/* 566 */         this.outStream.write(localBerEncoder.getBuf(), 0, localBerEncoder.getDataLen());
/* 567 */         this.outStream.flush();
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void abandonOutstandingReqs(Control[] paramArrayOfControl)
/*     */   {
/* 578 */     LdapRequest localLdapRequest = this.pendingRequests;
/*     */ 
/* 580 */     while (localLdapRequest != null) {
/* 581 */       abandonRequest(localLdapRequest, paramArrayOfControl);
/* 582 */       this.pendingRequests = (localLdapRequest = localLdapRequest.next);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void ldapUnbind(Control[] paramArrayOfControl)
/*     */   {
/* 595 */     BerEncoder localBerEncoder = new BerEncoder(256);
/* 596 */     int i = getMsgId();
/*     */     try
/*     */     {
/* 604 */       localBerEncoder.beginSeq(48);
/* 605 */       localBerEncoder.encodeInt(i);
/*     */ 
/* 607 */       localBerEncoder.encodeByte(66);
/* 608 */       localBerEncoder.encodeByte(0);
/*     */ 
/* 610 */       if (this.v3) {
/* 611 */         LdapClient.encodeControls(localBerEncoder, paramArrayOfControl);
/*     */       }
/* 613 */       localBerEncoder.endSeq();
/*     */ 
/* 615 */       if (this.traceFile != null) {
/* 616 */         Ber.dumpBER(this.traceFile, this.traceTagOut, localBerEncoder.getBuf(), 0, localBerEncoder.getDataLen());
/*     */       }
/*     */ 
/* 620 */       synchronized (this) {
/* 621 */         this.outStream.write(localBerEncoder.getBuf(), 0, localBerEncoder.getDataLen());
/* 622 */         this.outStream.flush();
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   void cleanup(Control[] paramArrayOfControl, boolean paramBoolean)
/*     */   {
/* 643 */     boolean bool = false;
/*     */ 
/* 645 */     synchronized (this) {
/* 646 */       this.useable = false;
/*     */       LdapRequest localLdapRequest1;
/* 648 */       if (this.sock != null)
/*     */       {
/*     */         try
/*     */         {
/* 653 */           if (!paramBoolean) {
/* 654 */             abandonOutstandingReqs(paramArrayOfControl);
/*     */           }
/* 656 */           if (this.bound)
/* 657 */             ldapUnbind(paramArrayOfControl);
/*     */         }
/*     */         finally {
/*     */           try {
/* 661 */             this.outStream.flush();
/* 662 */             this.sock.close();
/* 663 */             unpauseReader();
/*     */           }
/*     */           catch (IOException localIOException2)
/*     */           {
/*     */           }
/* 668 */           if (!paramBoolean) {
/* 669 */             LdapRequest localLdapRequest2 = this.pendingRequests;
/* 670 */             while (localLdapRequest2 != null) {
/* 671 */               localLdapRequest2.cancel();
/* 672 */               localLdapRequest2 = localLdapRequest2.next;
/*     */             }
/*     */           }
/* 675 */           this.sock = null;
/*     */         }
/* 677 */         bool = paramBoolean;
/*     */       }
/* 679 */       if (bool) {
/* 680 */         localLdapRequest1 = this.pendingRequests;
/* 681 */         while (localLdapRequest1 != null)
/*     */         {
/* 683 */           synchronized (localLdapRequest1) {
/* 684 */             localLdapRequest1.notify();
/* 685 */             localLdapRequest1 = localLdapRequest1.next;
/*     */           }
/*     */         }
/* 688 */         this.parent.processConnectionClosure();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void replaceStreams(InputStream paramInputStream, OutputStream paramOutputStream)
/*     */   {
/* 704 */     this.inStream = paramInputStream;
/*     */     try
/*     */     {
/* 708 */       this.outStream.flush();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */ 
/* 715 */     this.outStream = paramOutputStream;
/*     */   }
/*     */ 
/*     */   private synchronized InputStream getInputStream()
/*     */   {
/* 724 */     return this.inStream;
/*     */   }
/*     */ 
/*     */   private void unpauseReader()
/*     */     throws IOException
/*     */   {
/* 782 */     synchronized (this.pauseLock) {
/* 783 */       if (this.paused)
/*     */       {
/* 788 */         this.paused = false;
/* 789 */         this.pauseLock.notify();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void pauseReader()
/*     */     throws IOException
/*     */   {
/* 804 */     this.paused = true;
/*     */     try {
/* 806 */       while (this.paused)
/* 807 */         this.pauseLock.wait();
/*     */     }
/*     */     catch (InterruptedException localInterruptedException) {
/* 810 */       throw new InterruptedIOException("Pause/unpause reader has problems.");
/*     */     }
/*     */   }
/*     */ 
/*     */   // ERROR //
/*     */   public void run()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aconst_null
/*     */     //   1: astore 10
/*     */     //   3: sipush 129
/*     */     //   6: newarray byte
/*     */     //   8: astore_1
/*     */     //   9: iconst_0
/*     */     //   10: istore 5
/*     */     //   12: iconst_0
/*     */     //   13: istore 6
/*     */     //   15: iconst_0
/*     */     //   16: istore 7
/*     */     //   18: aload_0
/*     */     //   19: invokespecial 405	com/sun/jndi/ldap/Connection:getInputStream	()Ljava/io/InputStream;
/*     */     //   22: astore 10
/*     */     //   24: aload 10
/*     */     //   26: aload_1
/*     */     //   27: iload 5
/*     */     //   29: iconst_1
/*     */     //   30: invokevirtual 424	java/io/InputStream:read	([BII)I
/*     */     //   33: istore_3
/*     */     //   34: iload_3
/*     */     //   35: ifge +18 -> 53
/*     */     //   38: aload 10
/*     */     //   40: aload_0
/*     */     //   41: invokespecial 405	com/sun/jndi/ldap/Connection:getInputStream	()Ljava/io/InputStream;
/*     */     //   44: if_acmpeq +6 -> 50
/*     */     //   47: goto -44 -> 3
/*     */     //   50: goto +354 -> 404
/*     */     //   53: aload_1
/*     */     //   54: iload 5
/*     */     //   56: iinc 5 1
/*     */     //   59: baload
/*     */     //   60: bipush 48
/*     */     //   62: if_icmpeq +6 -> 68
/*     */     //   65: goto -62 -> 3
/*     */     //   68: aload 10
/*     */     //   70: aload_1
/*     */     //   71: iload 5
/*     */     //   73: iconst_1
/*     */     //   74: invokevirtual 424	java/io/InputStream:read	([BII)I
/*     */     //   77: istore_3
/*     */     //   78: iload_3
/*     */     //   79: ifge +6 -> 85
/*     */     //   82: goto +322 -> 404
/*     */     //   85: aload_1
/*     */     //   86: iload 5
/*     */     //   88: iinc 5 1
/*     */     //   91: baload
/*     */     //   92: istore 6
/*     */     //   94: iload 6
/*     */     //   96: sipush 128
/*     */     //   99: iand
/*     */     //   100: sipush 128
/*     */     //   103: if_icmpne +108 -> 211
/*     */     //   106: iload 6
/*     */     //   108: bipush 127
/*     */     //   110: iand
/*     */     //   111: istore 7
/*     */     //   113: iconst_0
/*     */     //   114: istore_3
/*     */     //   115: iconst_0
/*     */     //   116: istore 8
/*     */     //   118: iload_3
/*     */     //   119: iload 7
/*     */     //   121: if_icmpge +38 -> 159
/*     */     //   124: aload 10
/*     */     //   126: aload_1
/*     */     //   127: iload 5
/*     */     //   129: iload_3
/*     */     //   130: iadd
/*     */     //   131: iload 7
/*     */     //   133: iload_3
/*     */     //   134: isub
/*     */     //   135: invokevirtual 424	java/io/InputStream:read	([BII)I
/*     */     //   138: istore 4
/*     */     //   140: iload 4
/*     */     //   142: ifge +9 -> 151
/*     */     //   145: iconst_1
/*     */     //   146: istore 8
/*     */     //   148: goto +11 -> 159
/*     */     //   151: iload_3
/*     */     //   152: iload 4
/*     */     //   154: iadd
/*     */     //   155: istore_3
/*     */     //   156: goto -38 -> 118
/*     */     //   159: iload 8
/*     */     //   161: ifeq +6 -> 167
/*     */     //   164: goto +240 -> 404
/*     */     //   167: iconst_0
/*     */     //   168: istore 6
/*     */     //   170: iconst_0
/*     */     //   171: istore 11
/*     */     //   173: iload 11
/*     */     //   175: iload 7
/*     */     //   177: if_icmpge +28 -> 205
/*     */     //   180: iload 6
/*     */     //   182: bipush 8
/*     */     //   184: ishl
/*     */     //   185: aload_1
/*     */     //   186: iload 5
/*     */     //   188: iload 11
/*     */     //   190: iadd
/*     */     //   191: baload
/*     */     //   192: sipush 255
/*     */     //   195: iand
/*     */     //   196: iadd
/*     */     //   197: istore 6
/*     */     //   199: iinc 11 1
/*     */     //   202: goto -29 -> 173
/*     */     //   205: iload 5
/*     */     //   207: iload_3
/*     */     //   208: iadd
/*     */     //   209: istore 5
/*     */     //   211: aload 10
/*     */     //   213: iload 6
/*     */     //   215: iconst_0
/*     */     //   216: invokestatic 460	sun/misc/IOUtils:readFully	(Ljava/io/InputStream;IZ)[B
/*     */     //   219: astore 11
/*     */     //   221: aload_1
/*     */     //   222: iload 5
/*     */     //   224: aload 11
/*     */     //   226: arraylength
/*     */     //   227: iadd
/*     */     //   228: invokestatic 451	java/util/Arrays:copyOf	([BI)[B
/*     */     //   231: astore_1
/*     */     //   232: aload 11
/*     */     //   234: iconst_0
/*     */     //   235: aload_1
/*     */     //   236: iload 5
/*     */     //   238: aload 11
/*     */     //   240: arraylength
/*     */     //   241: invokestatic 441	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   244: iload 5
/*     */     //   246: aload 11
/*     */     //   248: arraylength
/*     */     //   249: iadd
/*     */     //   250: istore 5
/*     */     //   252: new 196	com/sun/jndi/ldap/BerDecoder
/*     */     //   255: dup
/*     */     //   256: aload_1
/*     */     //   257: iconst_0
/*     */     //   258: iload 5
/*     */     //   260: invokespecial 389	com/sun/jndi/ldap/BerDecoder:<init>	([BII)V
/*     */     //   263: astore 9
/*     */     //   265: aload_0
/*     */     //   266: getfield 375	com/sun/jndi/ldap/Connection:traceFile	Ljava/io/OutputStream;
/*     */     //   269: ifnull +18 -> 287
/*     */     //   272: aload_0
/*     */     //   273: getfield 375	com/sun/jndi/ldap/Connection:traceFile	Ljava/io/OutputStream;
/*     */     //   276: aload_0
/*     */     //   277: getfield 378	com/sun/jndi/ldap/Connection:traceTagIn	Ljava/lang/String;
/*     */     //   280: aload_1
/*     */     //   281: iconst_0
/*     */     //   282: iload 5
/*     */     //   284: invokestatic 386	com/sun/jndi/ldap/Ber:dumpBER	(Ljava/io/OutputStream;Ljava/lang/String;[BII)V
/*     */     //   287: aload 9
/*     */     //   289: aconst_null
/*     */     //   290: invokevirtual 390	com/sun/jndi/ldap/BerDecoder:parseSeq	([I)I
/*     */     //   293: pop
/*     */     //   294: aload 9
/*     */     //   296: invokevirtual 387	com/sun/jndi/ldap/BerDecoder:parseInt	()I
/*     */     //   299: istore_2
/*     */     //   300: aload 9
/*     */     //   302: invokevirtual 388	com/sun/jndi/ldap/BerDecoder:reset	()V
/*     */     //   305: iconst_0
/*     */     //   306: istore 12
/*     */     //   308: iload_2
/*     */     //   309: ifne +15 -> 324
/*     */     //   312: aload_0
/*     */     //   313: getfield 370	com/sun/jndi/ldap/Connection:parent	Lcom/sun/jndi/ldap/LdapClient;
/*     */     //   316: aload 9
/*     */     //   318: invokevirtual 414	com/sun/jndi/ldap/LdapClient:processUnsolicited	(Lcom/sun/jndi/ldap/BerDecoder;)V
/*     */     //   321: goto +55 -> 376
/*     */     //   324: aload_0
/*     */     //   325: iload_2
/*     */     //   326: invokevirtual 402	com/sun/jndi/ldap/Connection:findRequest	(I)Lcom/sun/jndi/ldap/LdapRequest;
/*     */     //   329: astore 13
/*     */     //   331: aload 13
/*     */     //   333: ifnull +43 -> 376
/*     */     //   336: aload_0
/*     */     //   337: getfield 376	com/sun/jndi/ldap/Connection:pauseLock	Ljava/lang/Object;
/*     */     //   340: dup
/*     */     //   341: astore 14
/*     */     //   343: monitorenter
/*     */     //   344: aload 13
/*     */     //   346: aload 9
/*     */     //   348: invokevirtual 419	com/sun/jndi/ldap/LdapRequest:addReplyBer	(Lcom/sun/jndi/ldap/BerDecoder;)Z
/*     */     //   351: istore 12
/*     */     //   353: iload 12
/*     */     //   355: ifeq +7 -> 362
/*     */     //   358: aload_0
/*     */     //   359: invokespecial 400	com/sun/jndi/ldap/Connection:pauseReader	()V
/*     */     //   362: aload 14
/*     */     //   364: monitorexit
/*     */     //   365: goto +11 -> 376
/*     */     //   368: astore 15
/*     */     //   370: aload 14
/*     */     //   372: monitorexit
/*     */     //   373: aload 15
/*     */     //   375: athrow
/*     */     //   376: goto +5 -> 381
/*     */     //   379: astore 12
/*     */     //   381: goto -378 -> 3
/*     */     //   384: astore 11
/*     */     //   386: aload 10
/*     */     //   388: aload_0
/*     */     //   389: invokespecial 405	com/sun/jndi/ldap/Connection:getInputStream	()Ljava/io/InputStream;
/*     */     //   392: if_acmpeq +6 -> 398
/*     */     //   395: goto +6 -> 401
/*     */     //   398: aload 11
/*     */     //   400: athrow
/*     */     //   401: goto -398 -> 3
/*     */     //   404: aload_0
/*     */     //   405: aconst_null
/*     */     //   406: iconst_1
/*     */     //   407: invokevirtual 408	com/sun/jndi/ldap/Connection:cleanup	([Ljavax/naming/ldap/Control;Z)V
/*     */     //   410: goto +31 -> 441
/*     */     //   413: astore 11
/*     */     //   415: aload_0
/*     */     //   416: aload 11
/*     */     //   418: putfield 372	com/sun/jndi/ldap/Connection:closureReason	Ljava/io/IOException;
/*     */     //   421: aload_0
/*     */     //   422: aconst_null
/*     */     //   423: iconst_1
/*     */     //   424: invokevirtual 408	com/sun/jndi/ldap/Connection:cleanup	([Ljavax/naming/ldap/Control;Z)V
/*     */     //   427: goto +14 -> 441
/*     */     //   430: astore 16
/*     */     //   432: aload_0
/*     */     //   433: aconst_null
/*     */     //   434: iconst_1
/*     */     //   435: invokevirtual 408	com/sun/jndi/ldap/Connection:cleanup	([Ljavax/naming/ldap/Control;Z)V
/*     */     //   438: aload 16
/*     */     //   440: athrow
/*     */     //   441: return
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   344	365	368	finally
/*     */     //   368	373	368	finally
/*     */     //   252	376	379	com/sun/jndi/ldap/Ber$DecodeException
/*     */     //   3	47	384	java/io/IOException
/*     */     //   53	65	384	java/io/IOException
/*     */     //   68	82	384	java/io/IOException
/*     */     //   85	164	384	java/io/IOException
/*     */     //   167	381	384	java/io/IOException
/*     */     //   3	404	413	java/io/IOException
/*     */     //   3	404	430	finally
/*     */     //   413	421	430	finally
/*     */     //   430	432	430	finally
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.Connection
 * JD-Core Version:    0.6.2
 */