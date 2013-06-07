/*     */ package com.sun.corba.se.impl.encoding;
/*     */ 
/*     */ import com.sun.corba.se.impl.corba.TypeCodeImpl;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.omg.CORBA_2_3.portable.InputStream;
/*     */ 
/*     */ public class TypeCodeInputStream extends EncapsInputStream
/*     */   implements TypeCodeReader
/*     */ {
/*  69 */   private Map typeMap = null;
/*  70 */   private InputStream enclosure = null;
/*  71 */   private boolean isEncapsulation = false;
/*     */ 
/*     */   public TypeCodeInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt) {
/*  74 */     super(paramORB, paramArrayOfByte, paramInt);
/*     */   }
/*     */ 
/*     */   public TypeCodeInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion)
/*     */   {
/*  82 */     super(paramORB, paramArrayOfByte, paramInt, paramBoolean, paramGIOPVersion);
/*     */   }
/*     */ 
/*     */   public TypeCodeInputStream(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion)
/*     */   {
/*  90 */     super(paramORB, paramByteBuffer, paramInt, paramBoolean, paramGIOPVersion);
/*     */   }
/*     */ 
/*     */   public void addTypeCodeAtPosition(TypeCodeImpl paramTypeCodeImpl, int paramInt) {
/*  94 */     if (this.typeMap == null)
/*     */     {
/*  96 */       this.typeMap = new HashMap(16);
/*     */     }
/*     */ 
/*  99 */     this.typeMap.put(new Integer(paramInt), paramTypeCodeImpl);
/*     */   }
/*     */ 
/*     */   public TypeCodeImpl getTypeCodeAtPosition(int paramInt) {
/* 103 */     if (this.typeMap == null) {
/* 104 */       return null;
/*     */     }
/*     */ 
/* 109 */     return (TypeCodeImpl)this.typeMap.get(new Integer(paramInt));
/*     */   }
/*     */ 
/*     */   public void setEnclosingInputStream(InputStream paramInputStream) {
/* 113 */     this.enclosure = paramInputStream;
/*     */   }
/*     */ 
/*     */   public TypeCodeReader getTopLevelStream() {
/* 117 */     if (this.enclosure == null)
/* 118 */       return this;
/* 119 */     if ((this.enclosure instanceof TypeCodeReader))
/* 120 */       return ((TypeCodeReader)this.enclosure).getTopLevelStream();
/* 121 */     return this;
/*     */   }
/*     */ 
/*     */   public int getTopLevelPosition() {
/* 125 */     if ((this.enclosure != null) && ((this.enclosure instanceof TypeCodeReader)))
/*     */     {
/* 129 */       int i = ((TypeCodeReader)this.enclosure).getTopLevelPosition();
/*     */ 
/* 132 */       int j = i - getBufferLength() + getPosition();
/*     */ 
/* 139 */       return j;
/*     */     }
/*     */ 
/* 145 */     return getPosition();
/*     */   }
/*     */ 
/*     */   public static TypeCodeInputStream readEncapsulation(InputStream paramInputStream, org.omg.CORBA.ORB paramORB)
/*     */   {
/* 152 */     int i = paramInputStream.read_long();
/*     */ 
/* 155 */     byte[] arrayOfByte = new byte[i];
/* 156 */     paramInputStream.read_octet_array(arrayOfByte, 0, arrayOfByte.length);
/*     */     TypeCodeInputStream localTypeCodeInputStream;
/* 159 */     if ((paramInputStream instanceof CDRInputStream)) {
/* 160 */       localTypeCodeInputStream = new TypeCodeInputStream((com.sun.corba.se.spi.orb.ORB)paramORB, arrayOfByte, arrayOfByte.length, ((CDRInputStream)paramInputStream).isLittleEndian(), ((CDRInputStream)paramInputStream).getGIOPVersion());
/*     */     }
/*     */     else
/*     */     {
/* 164 */       localTypeCodeInputStream = new TypeCodeInputStream((com.sun.corba.se.spi.orb.ORB)paramORB, arrayOfByte, arrayOfByte.length);
/*     */     }
/* 166 */     localTypeCodeInputStream.setEnclosingInputStream(paramInputStream);
/* 167 */     localTypeCodeInputStream.makeEncapsulation();
/*     */ 
/* 172 */     return localTypeCodeInputStream;
/*     */   }
/*     */ 
/*     */   protected void makeEncapsulation()
/*     */   {
/* 177 */     consumeEndian();
/* 178 */     this.isEncapsulation = true;
/*     */   }
/*     */ 
/*     */   public void printTypeMap() {
/* 182 */     System.out.println("typeMap = {");
/* 183 */     Iterator localIterator = this.typeMap.keySet().iterator();
/* 184 */     while (localIterator.hasNext()) {
/* 185 */       Integer localInteger = (Integer)localIterator.next();
/* 186 */       TypeCodeImpl localTypeCodeImpl = (TypeCodeImpl)this.typeMap.get(localInteger);
/* 187 */       System.out.println("  key = " + localInteger.intValue() + ", value = " + localTypeCodeImpl.description());
/*     */     }
/* 189 */     System.out.println("}");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.encoding.TypeCodeInputStream
 * JD-Core Version:    0.6.2
 */