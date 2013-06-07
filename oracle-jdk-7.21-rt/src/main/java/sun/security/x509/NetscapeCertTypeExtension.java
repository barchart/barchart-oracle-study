/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import sun.security.util.BitArray;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ 
/*     */ public class NetscapeCertTypeExtension extends Extension
/*     */   implements CertAttrSet<String>
/*     */ {
/*     */   public static final String IDENT = "x509.info.extensions.NetscapeCertType";
/*     */   public static final String NAME = "NetscapeCertType";
/*     */   public static final String SSL_CLIENT = "ssl_client";
/*     */   public static final String SSL_SERVER = "ssl_server";
/*     */   public static final String S_MIME = "s_mime";
/*     */   public static final String OBJECT_SIGNING = "object_signing";
/*     */   public static final String SSL_CA = "ssl_ca";
/*     */   public static final String S_MIME_CA = "s_mime_ca";
/*     */   public static final String OBJECT_SIGNING_CA = "object_signing_ca";
/*  72 */   private static final int[] CertType_data = { 2, 16, 840, 1, 113730, 1, 1 };
/*     */   public static ObjectIdentifier NetscapeCertType_Id;
/*     */   private boolean[] bitString;
/*     */   private static MapEntry[] mMapData;
/*     */   private static final Vector<String> mAttributeNames;
/*     */ 
/*     */   private static int getPosition(String paramString)
/*     */     throws IOException
/*     */   {
/* 118 */     for (int i = 0; i < mMapData.length; i++) {
/* 119 */       if (paramString.equalsIgnoreCase(mMapData[i].mName))
/* 120 */         return mMapData[i].mPosition;
/*     */     }
/* 122 */     throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:NetscapeCertType.");
/*     */   }
/*     */ 
/*     */   private void encodeThis()
/*     */     throws IOException
/*     */   {
/* 128 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 129 */     localDerOutputStream.putTruncatedUnalignedBitString(new BitArray(this.bitString));
/* 130 */     this.extensionValue = localDerOutputStream.toByteArray();
/*     */   }
/*     */ 
/*     */   private boolean isSet(int paramInt)
/*     */   {
/* 139 */     return this.bitString[paramInt];
/*     */   }
/*     */ 
/*     */   private void set(int paramInt, boolean paramBoolean)
/*     */   {
/* 147 */     if (paramInt >= this.bitString.length) {
/* 148 */       boolean[] arrayOfBoolean = new boolean[paramInt + 1];
/* 149 */       System.arraycopy(this.bitString, 0, arrayOfBoolean, 0, this.bitString.length);
/* 150 */       this.bitString = arrayOfBoolean;
/*     */     }
/* 152 */     this.bitString[paramInt] = paramBoolean;
/*     */   }
/*     */ 
/*     */   public NetscapeCertTypeExtension(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 162 */     this.bitString = new BitArray(paramArrayOfByte.length * 8, paramArrayOfByte).toBooleanArray();
/*     */ 
/* 164 */     this.extensionId = NetscapeCertType_Id;
/* 165 */     this.critical = true;
/* 166 */     encodeThis();
/*     */   }
/*     */ 
/*     */   public NetscapeCertTypeExtension(boolean[] paramArrayOfBoolean)
/*     */     throws IOException
/*     */   {
/* 176 */     this.bitString = paramArrayOfBoolean;
/* 177 */     this.extensionId = NetscapeCertType_Id;
/* 178 */     this.critical = true;
/* 179 */     encodeThis();
/*     */   }
/*     */ 
/*     */   public NetscapeCertTypeExtension(Boolean paramBoolean, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 192 */     this.extensionId = NetscapeCertType_Id;
/* 193 */     this.critical = paramBoolean.booleanValue();
/* 194 */     this.extensionValue = ((byte[])paramObject);
/* 195 */     DerValue localDerValue = new DerValue(this.extensionValue);
/* 196 */     this.bitString = localDerValue.getUnalignedBitString().toBooleanArray();
/*     */   }
/*     */ 
/*     */   public NetscapeCertTypeExtension()
/*     */   {
/* 203 */     this.extensionId = NetscapeCertType_Id;
/* 204 */     this.critical = true;
/* 205 */     this.bitString = new boolean[0];
/*     */   }
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 212 */     if (!(paramObject instanceof Boolean)) {
/* 213 */       throw new IOException("Attribute must be of type Boolean.");
/*     */     }
/* 215 */     boolean bool = ((Boolean)paramObject).booleanValue();
/* 216 */     set(getPosition(paramString), bool);
/* 217 */     encodeThis();
/*     */   }
/*     */ 
/*     */   public Object get(String paramString)
/*     */     throws IOException
/*     */   {
/* 224 */     return Boolean.valueOf(isSet(getPosition(paramString)));
/*     */   }
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 231 */     set(getPosition(paramString), false);
/* 232 */     encodeThis();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 239 */     String str = super.toString() + "NetscapeCertType [\n";
/*     */     try
/*     */     {
/* 242 */       if (isSet(getPosition("ssl_client")))
/* 243 */         str = str + "   SSL client\n";
/* 244 */       if (isSet(getPosition("ssl_server")))
/* 245 */         str = str + "   SSL server\n";
/* 246 */       if (isSet(getPosition("s_mime")))
/* 247 */         str = str + "   S/MIME\n";
/* 248 */       if (isSet(getPosition("object_signing")))
/* 249 */         str = str + "   Object Signing\n";
/* 250 */       if (isSet(getPosition("ssl_ca")))
/* 251 */         str = str + "   SSL CA\n";
/* 252 */       if (isSet(getPosition("s_mime_ca")))
/* 253 */         str = str + "   S/MIME CA\n";
/* 254 */       if (isSet(getPosition("object_signing_ca")))
/* 255 */         str = str + "   Object Signing CA";
/*     */     } catch (Exception localException) {
/*     */     }
/* 258 */     str = str + "]\n";
/* 259 */     return str;
/*     */   }
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 269 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */ 
/* 271 */     if (this.extensionValue == null) {
/* 272 */       this.extensionId = NetscapeCertType_Id;
/* 273 */       this.critical = true;
/* 274 */       encodeThis();
/*     */     }
/* 276 */     super.encode(localDerOutputStream);
/* 277 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 285 */     return mAttributeNames.elements();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 292 */     return "NetscapeCertType";
/*     */   }
/*     */ 
/*     */   public boolean[] getKeyUsageMappedBits()
/*     */   {
/* 302 */     KeyUsageExtension localKeyUsageExtension = new KeyUsageExtension();
/* 303 */     Boolean localBoolean = Boolean.TRUE;
/*     */     try
/*     */     {
/* 306 */       if ((isSet(getPosition("ssl_client"))) || (isSet(getPosition("s_mime"))) || (isSet(getPosition("object_signing"))))
/*     */       {
/* 309 */         localKeyUsageExtension.set("digital_signature", localBoolean);
/*     */       }
/* 311 */       if (isSet(getPosition("ssl_server"))) {
/* 312 */         localKeyUsageExtension.set("key_encipherment", localBoolean);
/*     */       }
/* 314 */       if ((isSet(getPosition("ssl_ca"))) || (isSet(getPosition("s_mime_ca"))) || (isSet(getPosition("object_signing_ca"))))
/*     */       {
/* 317 */         localKeyUsageExtension.set("key_certsign", localBoolean);
/*     */       } } catch (IOException localIOException) {  }
/*     */ 
/* 319 */     return localKeyUsageExtension.getBits();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  81 */       NetscapeCertType_Id = new ObjectIdentifier(CertType_data);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */ 
/*  99 */     mMapData = new MapEntry[] { new MapEntry("ssl_client", 0), new MapEntry("ssl_server", 1), new MapEntry("s_mime", 2), new MapEntry("object_signing", 3), new MapEntry("ssl_ca", 5), new MapEntry("s_mime_ca", 6), new MapEntry("object_signing_ca", 7) };
/*     */ 
/* 110 */     mAttributeNames = new Vector();
/*     */ 
/* 112 */     for (MapEntry localMapEntry : mMapData)
/* 113 */       mAttributeNames.add(localMapEntry.mName);
/*     */   }
/*     */ 
/*     */   private static class MapEntry
/*     */   {
/*     */     String mName;
/*     */     int mPosition;
/*     */ 
/*     */     MapEntry(String paramString, int paramInt)
/*     */     {
/*  94 */       this.mName = paramString;
/*  95 */       this.mPosition = paramInt;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.x509.NetscapeCertTypeExtension
 * JD-Core Version:    0.6.2
 */