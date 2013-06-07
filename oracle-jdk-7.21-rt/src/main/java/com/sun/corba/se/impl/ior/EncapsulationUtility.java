/*     */ package com.sun.corba.se.impl.ior;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.CDROutputStream;
/*     */ import com.sun.corba.se.impl.encoding.EncapsInputStream;
/*     */ import com.sun.corba.se.impl.encoding.EncapsOutputStream;
/*     */ import com.sun.corba.se.spi.ior.Identifiable;
/*     */ import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
/*     */ import com.sun.corba.se.spi.ior.WriteContents;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.omg.CORBA_2_3.portable.InputStream;
/*     */ import org.omg.CORBA_2_3.portable.OutputStream;
/*     */ 
/*     */ public class EncapsulationUtility
/*     */ {
/*     */   public static void readIdentifiableSequence(List paramList, IdentifiableFactoryFinder paramIdentifiableFactoryFinder, InputStream paramInputStream)
/*     */   {
/*  68 */     int i = paramInputStream.read_long();
/*  69 */     for (int j = 0; j < i; j++) {
/*  70 */       int k = paramInputStream.read_long();
/*  71 */       Identifiable localIdentifiable = paramIdentifiableFactoryFinder.create(k, paramInputStream);
/*  72 */       paramList.add(localIdentifiable);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeIdentifiableSequence(List paramList, OutputStream paramOutputStream)
/*     */   {
/*  81 */     paramOutputStream.write_long(paramList.size());
/*  82 */     Iterator localIterator = paramList.iterator();
/*  83 */     while (localIterator.hasNext()) {
/*  84 */       Identifiable localIdentifiable = (Identifiable)localIterator.next();
/*  85 */       paramOutputStream.write_long(localIdentifiable.getId());
/*  86 */       localIdentifiable.write(paramOutputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeOutputStream(OutputStream paramOutputStream1, OutputStream paramOutputStream2)
/*     */   {
/*  97 */     byte[] arrayOfByte = ((CDROutputStream)paramOutputStream1).toByteArray();
/*  98 */     paramOutputStream2.write_long(arrayOfByte.length);
/*  99 */     paramOutputStream2.write_octet_array(arrayOfByte, 0, arrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public static InputStream getEncapsulationStream(InputStream paramInputStream)
/*     */   {
/* 110 */     byte[] arrayOfByte = readOctets(paramInputStream);
/* 111 */     EncapsInputStream localEncapsInputStream = new EncapsInputStream(paramInputStream.orb(), arrayOfByte, arrayOfByte.length);
/*     */ 
/* 113 */     localEncapsInputStream.consumeEndian();
/* 114 */     return localEncapsInputStream;
/*     */   }
/*     */ 
/*     */   public static byte[] readOctets(InputStream paramInputStream)
/*     */   {
/* 122 */     int i = paramInputStream.read_ulong();
/* 123 */     byte[] arrayOfByte = new byte[i];
/* 124 */     paramInputStream.read_octet_array(arrayOfByte, 0, i);
/* 125 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public static void writeEncapsulation(WriteContents paramWriteContents, OutputStream paramOutputStream)
/*     */   {
/* 131 */     EncapsOutputStream localEncapsOutputStream = new EncapsOutputStream((ORB)paramOutputStream.orb());
/*     */ 
/* 133 */     localEncapsOutputStream.putEndian();
/*     */ 
/* 135 */     paramWriteContents.writeContents(localEncapsOutputStream);
/*     */ 
/* 137 */     writeOutputStream(localEncapsOutputStream, paramOutputStream);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.ior.EncapsulationUtility
 * JD-Core Version:    0.6.2
 */