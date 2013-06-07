/*      */ package sun.org.mozilla.classfile.internal;
/*      */ 
/*      */ final class ClassFileMethod
/*      */ {
/*      */   private String itsName;
/*      */   private String itsType;
/*      */   private short itsNameIndex;
/*      */   private short itsTypeIndex;
/*      */   private short itsFlags;
/*      */   private byte[] itsCodeAttribute;
/*      */ 
/*      */   ClassFileMethod(String paramString1, short paramShort1, String paramString2, short paramShort2, short paramShort3)
/*      */   {
/* 4346 */     this.itsName = paramString1;
/* 4347 */     this.itsNameIndex = paramShort1;
/* 4348 */     this.itsType = paramString2;
/* 4349 */     this.itsTypeIndex = paramShort2;
/* 4350 */     this.itsFlags = paramShort3;
/*      */   }
/*      */ 
/*      */   void setCodeAttribute(byte[] paramArrayOfByte)
/*      */   {
/* 4355 */     this.itsCodeAttribute = paramArrayOfByte;
/*      */   }
/*      */ 
/*      */   int write(byte[] paramArrayOfByte, int paramInt)
/*      */   {
/* 4360 */     paramInt = ClassFileWriter.putInt16(this.itsFlags, paramArrayOfByte, paramInt);
/* 4361 */     paramInt = ClassFileWriter.putInt16(this.itsNameIndex, paramArrayOfByte, paramInt);
/* 4362 */     paramInt = ClassFileWriter.putInt16(this.itsTypeIndex, paramArrayOfByte, paramInt);
/*      */ 
/* 4364 */     paramInt = ClassFileWriter.putInt16(1, paramArrayOfByte, paramInt);
/* 4365 */     System.arraycopy(this.itsCodeAttribute, 0, paramArrayOfByte, paramInt, this.itsCodeAttribute.length);
/*      */ 
/* 4367 */     paramInt += this.itsCodeAttribute.length;
/* 4368 */     return paramInt;
/*      */   }
/*      */ 
/*      */   int getWriteSize()
/*      */   {
/* 4373 */     return 8 + this.itsCodeAttribute.length;
/*      */   }
/*      */ 
/*      */   String getName()
/*      */   {
/* 4378 */     return this.itsName;
/*      */   }
/*      */ 
/*      */   String getType()
/*      */   {
/* 4383 */     return this.itsType;
/*      */   }
/*      */ 
/*      */   short getFlags()
/*      */   {
/* 4388 */     return this.itsFlags;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.classfile.internal.ClassFileMethod
 * JD-Core Version:    0.6.2
 */