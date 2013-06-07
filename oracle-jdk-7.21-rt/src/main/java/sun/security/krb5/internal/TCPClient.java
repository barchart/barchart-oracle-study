/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ 
/*     */ class TCPClient extends NetClient
/*     */ {
/*     */   private Socket tcpSocket;
/*     */   private BufferedOutputStream out;
/*     */   private BufferedInputStream in;
/*     */ 
/*     */   TCPClient(String paramString, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  62 */     this.tcpSocket = new Socket();
/*  63 */     this.tcpSocket.connect(new InetSocketAddress(paramString, paramInt1), paramInt2);
/*  64 */     this.out = new BufferedOutputStream(this.tcpSocket.getOutputStream());
/*  65 */     this.in = new BufferedInputStream(this.tcpSocket.getInputStream());
/*  66 */     this.tcpSocket.setSoTimeout(paramInt2);
/*     */   }
/*     */ 
/*     */   public void send(byte[] paramArrayOfByte) throws IOException
/*     */   {
/*  71 */     byte[] arrayOfByte = new byte[4];
/*  72 */     intToNetworkByteOrder(paramArrayOfByte.length, arrayOfByte, 0, 4);
/*  73 */     this.out.write(arrayOfByte);
/*     */ 
/*  75 */     this.out.write(paramArrayOfByte);
/*  76 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   public byte[] receive() throws IOException
/*     */   {
/*  81 */     byte[] arrayOfByte1 = new byte[4];
/*  82 */     int i = readFully(arrayOfByte1, 4);
/*     */ 
/*  84 */     if (i != 4) {
/*  85 */       if (Krb5.DEBUG) {
/*  86 */         System.out.println(">>>DEBUG: TCPClient could not read length field");
/*     */       }
/*     */ 
/*  89 */       return null;
/*     */     }
/*     */ 
/*  92 */     int j = networkByteOrderToInt(arrayOfByte1, 0, 4);
/*  93 */     if (Krb5.DEBUG) {
/*  94 */       System.out.println(">>>DEBUG: TCPClient reading " + j + " bytes");
/*     */     }
/*     */ 
/*  97 */     if (j <= 0) {
/*  98 */       if (Krb5.DEBUG) {
/*  99 */         System.out.println(">>>DEBUG: TCPClient zero or negative length field: " + j);
/*     */       }
/*     */ 
/* 102 */       return null;
/*     */     }
/*     */ 
/* 105 */     byte[] arrayOfByte2 = new byte[j];
/* 106 */     i = readFully(arrayOfByte2, j);
/* 107 */     if (i != j) {
/* 108 */       if (Krb5.DEBUG) {
/* 109 */         System.out.println(">>>DEBUG: TCPClient could not read complete packet (" + j + "/" + i + ")");
/*     */       }
/*     */ 
/* 113 */       return null;
/*     */     }
/* 115 */     return arrayOfByte2;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 121 */     this.tcpSocket.close();
/*     */   }
/*     */ 
/*     */   private int readFully(byte[] paramArrayOfByte, int paramInt)
/*     */     throws IOException
/*     */   {
/* 129 */     int j = 0;
/*     */ 
/* 131 */     while (paramInt > 0) {
/* 132 */       int i = this.in.read(paramArrayOfByte, j, paramInt);
/*     */ 
/* 134 */       if (i == -1) {
/* 135 */         return j == 0 ? -1 : j;
/*     */       }
/* 137 */       j += i;
/* 138 */       paramInt -= i;
/*     */     }
/* 140 */     return j;
/*     */   }
/*     */ 
/*     */   private static int networkByteOrderToInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 148 */     if (paramInt2 > 4) {
/* 149 */       throw new IllegalArgumentException("Cannot handle more than 4 bytes");
/*     */     }
/*     */ 
/* 153 */     int i = 0;
/*     */ 
/* 155 */     for (int j = 0; j < paramInt2; j++) {
/* 156 */       i <<= 8;
/* 157 */       i |= paramArrayOfByte[(paramInt1 + j)] & 0xFF;
/*     */     }
/* 159 */     return i;
/*     */   }
/*     */ 
/*     */   private static void intToNetworkByteOrder(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
/*     */   {
/* 168 */     if (paramInt3 > 4) {
/* 169 */       throw new IllegalArgumentException("Cannot handle more than 4 bytes");
/*     */     }
/*     */ 
/* 173 */     for (int i = paramInt3 - 1; i >= 0; i--) {
/* 174 */       paramArrayOfByte[(paramInt2 + i)] = ((byte)(paramInt1 & 0xFF));
/* 175 */       paramInt1 >>>= 8;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.internal.TCPClient
 * JD-Core Version:    0.6.2
 */