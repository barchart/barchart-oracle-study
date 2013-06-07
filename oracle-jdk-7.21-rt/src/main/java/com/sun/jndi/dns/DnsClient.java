/*     */ package com.sun.jndi.dns;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.naming.CommunicationException;
/*     */ import javax.naming.ConfigurationException;
/*     */ import javax.naming.NameNotFoundException;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.OperationNotSupportedException;
/*     */ import javax.naming.ServiceUnavailableException;
/*     */ 
/*     */ public class DnsClient
/*     */ {
/*     */   private static final int IDENT_OFFSET = 0;
/*     */   private static final int FLAGS_OFFSET = 2;
/*     */   private static final int NUMQ_OFFSET = 4;
/*     */   private static final int NUMANS_OFFSET = 6;
/*     */   private static final int NUMAUTH_OFFSET = 8;
/*     */   private static final int NUMADD_OFFSET = 10;
/*     */   private static final int DNS_HDR_SIZE = 12;
/*     */   private static final int NO_ERROR = 0;
/*     */   private static final int FORMAT_ERROR = 1;
/*     */   private static final int SERVER_FAILURE = 2;
/*     */   private static final int NAME_ERROR = 3;
/*     */   private static final int NOT_IMPL = 4;
/*     */   private static final int REFUSED = 5;
/*  70 */   private static final String[] rcodeDescription = { "No error", "DNS format error", "DNS server failure", "DNS name not found", "DNS operation not supported", "DNS service refused" };
/*     */   private static final int DEFAULT_PORT = 53;
/*     */   private InetAddress[] servers;
/*     */   private int[] serverPorts;
/*     */   private int timeout;
/*     */   private int retries;
/*     */   private DatagramSocket udpSocket;
/*     */   private Set<Integer> reqs;
/*     */   private Map<Integer, byte[]> resps;
/* 146 */   private Object queuesLock = new Object();
/*     */ 
/* 157 */   private int ident = 0;
/* 158 */   private Object identLock = new Object();
/*     */   private static final boolean debug = false;
/*     */ 
/*     */   public DnsClient(String[] paramArrayOfString, int paramInt1, int paramInt2)
/*     */     throws NamingException
/*     */   {
/* 103 */     this.timeout = paramInt1;
/* 104 */     this.retries = paramInt2;
/*     */     try {
/* 106 */       this.udpSocket = new DatagramSocket();
/*     */     } catch (SocketException localSocketException) {
/* 108 */       ConfigurationException localConfigurationException1 = new ConfigurationException();
/* 109 */       localConfigurationException1.setRootCause(localSocketException);
/* 110 */       throw localConfigurationException1;
/*     */     }
/*     */ 
/* 113 */     this.servers = new InetAddress[paramArrayOfString.length];
/* 114 */     this.serverPorts = new int[paramArrayOfString.length];
/*     */ 
/* 116 */     for (int i = 0; i < paramArrayOfString.length; i++)
/*     */     {
/* 119 */       int j = paramArrayOfString[i].indexOf(':', paramArrayOfString[i].indexOf(93) + 1);
/*     */ 
/* 122 */       this.serverPorts[i] = (j < 0 ? 53 : Integer.parseInt(paramArrayOfString[i].substring(j + 1)));
/*     */ 
/* 125 */       String str = j < 0 ? paramArrayOfString[i] : paramArrayOfString[i].substring(0, j);
/*     */       try
/*     */       {
/* 129 */         this.servers[i] = InetAddress.getByName(str);
/*     */       } catch (UnknownHostException localUnknownHostException) {
/* 131 */         ConfigurationException localConfigurationException2 = new ConfigurationException("Unknown DNS server: " + str);
/*     */ 
/* 133 */         localConfigurationException2.setRootCause(localUnknownHostException);
/* 134 */         throw localConfigurationException2;
/*     */       }
/*     */     }
/* 137 */     this.reqs = Collections.synchronizedSet(new HashSet());
/* 138 */     this.resps = Collections.synchronizedMap(new HashMap());
/*     */   }
/*     */ 
/*     */   protected void finalize() {
/* 142 */     close();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 149 */     this.udpSocket.close();
/* 150 */     synchronized (this.queuesLock) {
/* 151 */       this.reqs.clear();
/* 152 */       this.resps.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   ResourceRecords query(DnsName paramDnsName, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
/*     */     throws NamingException
/*     */   {
/*     */     int i;
/* 170 */     synchronized (this.identLock) {
/* 171 */       this.ident = (0xFFFF & this.ident + 1);
/* 172 */       i = this.ident;
/*     */     }
/*     */ 
/* 176 */     this.reqs.add(Integer.valueOf(i));
/*     */ 
/* 178 */     ??? = makeQueryPacket(paramDnsName, i, paramInt1, paramInt2, paramBoolean1);
/*     */ 
/* 180 */     Object localObject2 = null;
/* 181 */     boolean[] arrayOfBoolean = new boolean[this.servers.length];
/*     */ 
/* 188 */     for (int j = 0; j < this.retries; j++)
/*     */     {
/* 191 */       for (int k = 0; k < this.servers.length; k++) {
/* 192 */         if (arrayOfBoolean[k] == 0)
/*     */         {
/*     */           try
/*     */           {
/* 202 */             Object localObject3 = null;
/* 203 */             localObject3 = doUdpQuery((Packet)???, this.servers[k], this.serverPorts[k], j, i);
/*     */ 
/* 211 */             if (localObject3 == null) {
/* 212 */               if (this.resps.size() > 0) {
/* 213 */                 localObject3 = lookupResponse(Integer.valueOf(i));
/*     */               }
/* 215 */               if (localObject3 == null);
/*     */             }
/*     */             else
/*     */             {
/* 219 */               Object localObject4 = new Header((byte[])localObject3, localObject3.length);
/*     */ 
/* 221 */               if ((paramBoolean2) && (!((Header)localObject4).authoritative)) {
/* 222 */                 localObject2 = new NameNotFoundException("DNS response not authoritative");
/*     */ 
/* 224 */                 arrayOfBoolean[k] = true;
/*     */               }
/*     */               else {
/* 227 */                 if (((Header)localObject4).truncated)
/*     */                 {
/* 231 */                   for (int m = 0; m < this.servers.length; m++) {
/* 232 */                     int n = (k + m) % this.servers.length;
/* 233 */                     if (arrayOfBoolean[n] == 0)
/*     */                     {
/*     */                       try
/*     */                       {
/* 237 */                         Tcp localTcp = new Tcp(this.servers[n], this.serverPorts[n]);
/*     */                         byte[] arrayOfByte;
/*     */                         try {
/* 241 */                           arrayOfByte = doTcpQuery(localTcp, (Packet)???);
/*     */                         } finally {
/* 243 */                           localTcp.close();
/*     */                         }
/* 245 */                         Header localHeader = new Header(arrayOfByte, arrayOfByte.length);
/* 246 */                         if (localHeader.query) {
/* 247 */                           throw new CommunicationException("DNS error: expecting response");
/*     */                         }
/*     */ 
/* 250 */                         checkResponseCode(localHeader);
/*     */ 
/* 252 */                         if ((!paramBoolean2) || (localHeader.authoritative))
/*     */                         {
/* 254 */                           localObject4 = localHeader;
/* 255 */                           localObject3 = arrayOfByte;
/* 256 */                           break;
/*     */                         }
/* 258 */                         arrayOfBoolean[n] = true;
/*     */                       }
/*     */                       catch (Exception localException) {
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/* 265 */                 return new ResourceRecords((byte[])localObject3, localObject3.length, (Header)localObject4, false);
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (IOException localIOException)
/*     */           {
/* 271 */             if (localObject2 == null) {
/* 272 */               localObject2 = localIOException;
/*     */             }
/*     */ 
/* 276 */             if (localIOException.getClass().getName().equals("java.net.PortUnreachableException"))
/*     */             {
/* 278 */               arrayOfBoolean[k] = true;
/*     */             }
/*     */           } catch (NameNotFoundException localNameNotFoundException) {
/* 281 */             throw localNameNotFoundException;
/*     */           } catch (CommunicationException localCommunicationException2) {
/* 283 */             if (localObject2 == null)
/* 284 */               localObject2 = localCommunicationException2;
/*     */           }
/*     */           catch (NamingException localNamingException) {
/* 287 */             if (localObject2 == null) {
/* 288 */               localObject2 = localNamingException;
/*     */             }
/* 290 */             arrayOfBoolean[k] = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 295 */     this.reqs.remove(Integer.valueOf(i));
/* 296 */     if ((localObject2 instanceof NamingException)) {
/* 297 */       throw ((NamingException)localObject2);
/*     */     }
/*     */ 
/* 300 */     CommunicationException localCommunicationException1 = new CommunicationException("DNS error");
/* 301 */     localCommunicationException1.setRootCause((Throwable)localObject2);
/* 302 */     throw localCommunicationException1;
/*     */   }
/*     */ 
/*     */   ResourceRecords queryZone(DnsName paramDnsName, int paramInt, boolean paramBoolean)
/*     */     throws NamingException
/*     */   {
/*     */     int i;
/* 309 */     synchronized (this.identLock) {
/* 310 */       this.ident = (0xFFFF & this.ident + 1);
/* 311 */       i = this.ident;
/*     */     }
/* 313 */     ??? = makeQueryPacket(paramDnsName, i, paramInt, 252, paramBoolean);
/*     */ 
/* 315 */     Object localObject2 = null;
/*     */ 
/* 318 */     for (int j = 0; j < this.servers.length; j++) {
/*     */       try {
/* 320 */         Tcp localTcp = new Tcp(this.servers[j], this.serverPorts[j]);
/*     */         try
/*     */         {
/* 323 */           byte[] arrayOfByte = doTcpQuery(localTcp, (Packet)???);
/* 324 */           Header localHeader = new Header(arrayOfByte, arrayOfByte.length);
/*     */ 
/* 327 */           checkResponseCode(localHeader);
/* 328 */           ResourceRecords localResourceRecords1 = new ResourceRecords(arrayOfByte, arrayOfByte.length, localHeader, true);
/*     */ 
/* 330 */           if (localResourceRecords1.getFirstAnsType() != 6) {
/* 331 */             throw new CommunicationException("DNS error: zone xfer doesn't begin with SOA");
/*     */           }
/*     */ 
/* 335 */           if ((localResourceRecords1.answer.size() == 1) || (localResourceRecords1.getLastAnsType() != 6))
/*     */           {
/*     */             do
/*     */             {
/* 339 */               arrayOfByte = continueTcpQuery(localTcp);
/* 340 */               if (arrayOfByte == null) {
/* 341 */                 throw new CommunicationException("DNS error: incomplete zone transfer");
/*     */               }
/*     */ 
/* 344 */               localHeader = new Header(arrayOfByte, arrayOfByte.length);
/* 345 */               checkResponseCode(localHeader);
/* 346 */               localResourceRecords1.add(arrayOfByte, arrayOfByte.length, localHeader);
/* 347 */             }while (localResourceRecords1.getLastAnsType() != 6);
/*     */           }
/*     */ 
/* 352 */           localResourceRecords1.answer.removeElementAt(localResourceRecords1.answer.size() - 1);
/* 353 */           return localResourceRecords1;
/*     */         }
/*     */         finally {
/* 356 */           localTcp.close();
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException) {
/* 360 */         localObject2 = localIOException;
/*     */       } catch (NameNotFoundException localNameNotFoundException) {
/* 362 */         throw localNameNotFoundException;
/*     */       } catch (NamingException localNamingException) {
/* 364 */         localObject2 = localNamingException;
/*     */       }
/*     */     }
/* 367 */     if ((localObject2 instanceof NamingException)) {
/* 368 */       throw ((NamingException)localObject2);
/*     */     }
/* 370 */     CommunicationException localCommunicationException = new CommunicationException("DNS error during zone transfer");
/*     */ 
/* 372 */     localCommunicationException.setRootCause(localObject2);
/* 373 */     throw localCommunicationException;
/*     */   }
/*     */ 
/*     */   private byte[] doUdpQuery(Packet paramPacket, InetAddress paramInetAddress, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException, NamingException
/*     */   {
/* 387 */     int i = 50;
/*     */ 
/* 389 */     synchronized (this.udpSocket) {
/* 390 */       DatagramPacket localDatagramPacket1 = new DatagramPacket(paramPacket.getData(), paramPacket.length(), paramInetAddress, paramInt1);
/*     */ 
/* 392 */       DatagramPacket localDatagramPacket2 = new DatagramPacket(new byte[8000], 8000);
/* 393 */       this.udpSocket.connect(paramInetAddress, paramInt1);
/* 394 */       int j = this.timeout * (1 << paramInt2);
/*     */       try {
/* 396 */         this.udpSocket.send(localDatagramPacket1);
/*     */ 
/* 399 */         int k = j;
/* 400 */         int m = 0;
/*     */         do
/*     */         {
/* 409 */           this.udpSocket.setSoTimeout(k);
/* 410 */           long l1 = System.currentTimeMillis();
/* 411 */           this.udpSocket.receive(localDatagramPacket2);
/* 412 */           long l2 = System.currentTimeMillis();
/*     */ 
/* 414 */           byte[] arrayOfByte1 = new byte[localDatagramPacket2.getLength()];
/* 415 */           arrayOfByte1 = localDatagramPacket2.getData();
/* 416 */           if (isMatchResponse(arrayOfByte1, paramInt3)) {
/* 417 */             byte[] arrayOfByte2 = arrayOfByte1;
/*     */ 
/* 423 */             this.udpSocket.disconnect(); return arrayOfByte2;
/*     */           }
/* 419 */           k = j - (int)(l2 - l1);
/* 420 */         }while (k > i);
/*     */       }
/*     */       finally {
/* 423 */         this.udpSocket.disconnect();
/*     */       }
/* 425 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private byte[] doTcpQuery(Tcp paramTcp, Packet paramPacket)
/*     */     throws IOException
/*     */   {
/* 434 */     int i = paramPacket.length();
/*     */ 
/* 436 */     paramTcp.out.write(i >> 8);
/* 437 */     paramTcp.out.write(i);
/* 438 */     paramTcp.out.write(paramPacket.getData(), 0, i);
/* 439 */     paramTcp.out.flush();
/*     */ 
/* 441 */     byte[] arrayOfByte = continueTcpQuery(paramTcp);
/* 442 */     if (arrayOfByte == null) {
/* 443 */       throw new IOException("DNS error: no response");
/*     */     }
/* 445 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   private byte[] continueTcpQuery(Tcp paramTcp)
/*     */     throws IOException
/*     */   {
/* 453 */     int i = paramTcp.in.read();
/* 454 */     if (i == -1) {
/* 455 */       return null;
/*     */     }
/* 457 */     int j = paramTcp.in.read();
/* 458 */     if (j == -1) {
/* 459 */       throw new IOException("Corrupted DNS response: bad length");
/*     */     }
/* 461 */     int k = i << 8 | j;
/* 462 */     byte[] arrayOfByte = new byte[k];
/* 463 */     int m = 0;
/* 464 */     while (k > 0) {
/* 465 */       int n = paramTcp.in.read(arrayOfByte, m, k);
/* 466 */       if (n == -1) {
/* 467 */         throw new IOException("Corrupted DNS response: too little data");
/*     */       }
/*     */ 
/* 470 */       k -= n;
/* 471 */       m += n;
/*     */     }
/* 473 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   private Packet makeQueryPacket(DnsName paramDnsName, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
/*     */   {
/* 478 */     int i = paramDnsName.getOctets();
/* 479 */     int j = 12 + i + 4;
/* 480 */     Packet localPacket = new Packet(j);
/*     */ 
/* 482 */     int k = paramBoolean ? 256 : 0;
/*     */ 
/* 484 */     localPacket.putShort(paramInt1, 0);
/* 485 */     localPacket.putShort(k, 2);
/* 486 */     localPacket.putShort(1, 4);
/* 487 */     localPacket.putShort(0, 6);
/* 488 */     localPacket.putInt(0, 8);
/*     */ 
/* 490 */     makeQueryName(paramDnsName, localPacket, 12);
/* 491 */     localPacket.putShort(paramInt3, 12 + i);
/* 492 */     localPacket.putShort(paramInt2, 12 + i + 2);
/*     */ 
/* 494 */     return localPacket;
/*     */   }
/*     */ 
/*     */   private void makeQueryName(DnsName paramDnsName, Packet paramPacket, int paramInt)
/*     */   {
/* 501 */     for (int i = paramDnsName.size() - 1; i >= 0; i--) {
/* 502 */       String str = paramDnsName.get(i);
/* 503 */       int j = str.length();
/*     */ 
/* 505 */       paramPacket.putByte(j, paramInt++);
/* 506 */       for (int k = 0; k < j; k++) {
/* 507 */         paramPacket.putByte(str.charAt(k), paramInt++);
/*     */       }
/*     */     }
/* 510 */     if (!paramDnsName.hasRootLabel())
/* 511 */       paramPacket.putByte(0, paramInt);
/*     */   }
/*     */ 
/*     */   private byte[] lookupResponse(Integer paramInteger)
/*     */     throws NamingException
/*     */   {
/*     */     byte[] arrayOfByte;
/* 527 */     if ((arrayOfByte = (byte[])this.resps.get(paramInteger)) != null) {
/* 528 */       checkResponseCode(new Header(arrayOfByte, arrayOfByte.length));
/* 529 */       synchronized (this.queuesLock) {
/* 530 */         this.resps.remove(paramInteger);
/* 531 */         this.reqs.remove(paramInteger);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 539 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   private boolean isMatchResponse(byte[] paramArrayOfByte, int paramInt)
/*     */     throws NamingException
/*     */   {
/* 550 */     Header localHeader = new Header(paramArrayOfByte, paramArrayOfByte.length);
/* 551 */     if (localHeader.query) {
/* 552 */       throw new CommunicationException("DNS error: expecting response");
/*     */     }
/*     */ 
/* 555 */     if (!this.reqs.contains(Integer.valueOf(paramInt))) {
/* 556 */       return false;
/*     */     }
/*     */ 
/* 560 */     if (localHeader.xid == paramInt)
/*     */     {
/* 565 */       checkResponseCode(localHeader);
/*     */ 
/* 567 */       synchronized (this.queuesLock) {
/* 568 */         this.resps.remove(Integer.valueOf(paramInt));
/* 569 */         this.reqs.remove(Integer.valueOf(paramInt));
/*     */       }
/* 571 */       return true;
/*     */     }
/*     */ 
/* 579 */     synchronized (this.queuesLock) {
/* 580 */       if (this.reqs.contains(Integer.valueOf(localHeader.xid))) {
/* 581 */         this.resps.put(Integer.valueOf(localHeader.xid), paramArrayOfByte);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 591 */     return false;
/*     */   }
/*     */ 
/*     */   private void checkResponseCode(Header paramHeader)
/*     */     throws NamingException
/*     */   {
/* 600 */     int i = paramHeader.rcode;
/* 601 */     if (i == 0) {
/* 602 */       return;
/*     */     }
/* 604 */     String str = i < rcodeDescription.length ? rcodeDescription[i] : "DNS error";
/*     */ 
/* 607 */     str = str + " [response code " + i + "]";
/*     */ 
/* 609 */     switch (i) {
/*     */     case 2:
/* 611 */       throw new ServiceUnavailableException(str);
/*     */     case 3:
/* 613 */       throw new NameNotFoundException(str);
/*     */     case 4:
/*     */     case 5:
/* 616 */       throw new OperationNotSupportedException(str);
/*     */     case 1:
/*     */     }
/* 619 */     throw new NamingException(str);
/*     */   }
/*     */ 
/*     */   private static void dprint(String paramString)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.dns.DnsClient
 * JD-Core Version:    0.6.2
 */