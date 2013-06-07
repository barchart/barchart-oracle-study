/*     */ package com.sun.corba.se.impl.encoding;
/*     */ 
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.CORBA.portable.InputStream;
/*     */ 
/*     */ public final class TypeCodeOutputStream extends EncapsOutputStream
/*     */ {
/*  66 */   private org.omg.CORBA_2_3.portable.OutputStream enclosure = null;
/*  67 */   private Map typeMap = null;
/*  68 */   private boolean isEncapsulation = false;
/*     */ 
/*     */   public TypeCodeOutputStream(com.sun.corba.se.spi.orb.ORB paramORB) {
/*  71 */     super(paramORB, false);
/*     */   }
/*     */ 
/*     */   public TypeCodeOutputStream(com.sun.corba.se.spi.orb.ORB paramORB, boolean paramBoolean) {
/*  75 */     super(paramORB, paramBoolean);
/*     */   }
/*     */ 
/*     */   public InputStream create_input_stream()
/*     */   {
/*  81 */     TypeCodeInputStream localTypeCodeInputStream = new TypeCodeInputStream((com.sun.corba.se.spi.orb.ORB)orb(), getByteBuffer(), getIndex(), isLittleEndian(), getGIOPVersion());
/*     */ 
/*  87 */     return localTypeCodeInputStream;
/*     */   }
/*     */ 
/*     */   public void setEnclosingOutputStream(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream) {
/*  91 */     this.enclosure = paramOutputStream;
/*     */   }
/*     */ 
/*     */   public TypeCodeOutputStream getTopLevelStream()
/*     */   {
/* 108 */     if (this.enclosure == null)
/* 109 */       return this;
/* 110 */     if ((this.enclosure instanceof TypeCodeOutputStream))
/* 111 */       return ((TypeCodeOutputStream)this.enclosure).getTopLevelStream();
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */   public int getTopLevelPosition() {
/* 116 */     if ((this.enclosure != null) && ((this.enclosure instanceof TypeCodeOutputStream))) {
/* 117 */       int i = ((TypeCodeOutputStream)this.enclosure).getTopLevelPosition() + getPosition();
/*     */ 
/* 120 */       if (this.isEncapsulation) i += 4;
/*     */ 
/* 128 */       return i;
/*     */     }
/*     */ 
/* 134 */     return getPosition();
/*     */   }
/*     */ 
/*     */   public void addIDAtPosition(String paramString, int paramInt) {
/* 138 */     if (this.typeMap == null) {
/* 139 */       this.typeMap = new HashMap(16);
/*     */     }
/* 141 */     this.typeMap.put(paramString, new Integer(paramInt));
/*     */   }
/*     */ 
/*     */   public int getPositionForID(String paramString) {
/* 145 */     if (this.typeMap == null) {
/* 146 */       throw this.wrapper.refTypeIndirType(CompletionStatus.COMPLETED_NO);
/*     */     }
/*     */ 
/* 149 */     return ((Integer)this.typeMap.get(paramString)).intValue();
/*     */   }
/*     */ 
/*     */   public void writeRawBuffer(org.omg.CORBA.portable.OutputStream paramOutputStream, int paramInt)
/*     */   {
/* 169 */     paramOutputStream.write_long(paramInt);
/*     */ 
/* 175 */     ByteBuffer localByteBuffer = getByteBuffer();
/* 176 */     if (localByteBuffer.hasArray())
/*     */     {
/* 178 */       paramOutputStream.write_octet_array(localByteBuffer.array(), 4, getIndex() - 4);
/*     */     }
/*     */     else
/*     */     {
/* 186 */       byte[] arrayOfByte = new byte[localByteBuffer.limit()];
/* 187 */       for (int i = 0; i < arrayOfByte.length; i++)
/* 188 */         arrayOfByte[i] = localByteBuffer.get(i);
/* 189 */       paramOutputStream.write_octet_array(arrayOfByte, 4, getIndex() - 4);
/*     */     }
/*     */   }
/*     */ 
/*     */   public TypeCodeOutputStream createEncapsulation(org.omg.CORBA.ORB paramORB)
/*     */   {
/* 199 */     TypeCodeOutputStream localTypeCodeOutputStream = new TypeCodeOutputStream((com.sun.corba.se.spi.orb.ORB)paramORB, isLittleEndian());
/* 200 */     localTypeCodeOutputStream.setEnclosingOutputStream(this);
/* 201 */     localTypeCodeOutputStream.makeEncapsulation();
/*     */ 
/* 203 */     return localTypeCodeOutputStream;
/*     */   }
/*     */ 
/*     */   protected void makeEncapsulation()
/*     */   {
/* 208 */     putEndian();
/* 209 */     this.isEncapsulation = true;
/*     */   }
/*     */ 
/*     */   public static TypeCodeOutputStream wrapOutputStream(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream) {
/* 213 */     boolean bool = (paramOutputStream instanceof CDROutputStream) ? ((CDROutputStream)paramOutputStream).isLittleEndian() : false;
/* 214 */     TypeCodeOutputStream localTypeCodeOutputStream = new TypeCodeOutputStream((com.sun.corba.se.spi.orb.ORB)paramOutputStream.orb(), bool);
/* 215 */     localTypeCodeOutputStream.setEnclosingOutputStream(paramOutputStream);
/*     */ 
/* 217 */     return localTypeCodeOutputStream;
/*     */   }
/*     */ 
/*     */   public int getPosition() {
/* 221 */     return getIndex();
/*     */   }
/*     */ 
/*     */   public int getRealIndex(int paramInt) {
/* 225 */     int i = getTopLevelPosition();
/*     */ 
/* 228 */     return i;
/*     */   }
/*     */ 
/*     */   public byte[] getTypeCodeBuffer()
/*     */   {
/* 238 */     ByteBuffer localByteBuffer = getByteBuffer();
/*     */ 
/* 240 */     byte[] arrayOfByte = new byte[getIndex() - 4];
/*     */ 
/* 245 */     for (int i = 0; i < arrayOfByte.length; i++)
/* 246 */       arrayOfByte[i] = localByteBuffer.get(i + 4);
/* 247 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public void printTypeMap() {
/* 251 */     System.out.println("typeMap = {");
/* 252 */     Iterator localIterator = this.typeMap.keySet().iterator();
/* 253 */     while (localIterator.hasNext()) {
/* 254 */       String str = (String)localIterator.next();
/* 255 */       Integer localInteger = (Integer)this.typeMap.get(str);
/* 256 */       System.out.println("  key = " + str + ", value = " + localInteger);
/*     */     }
/* 258 */     System.out.println("}");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.encoding.TypeCodeOutputStream
 * JD-Core Version:    0.6.2
 */