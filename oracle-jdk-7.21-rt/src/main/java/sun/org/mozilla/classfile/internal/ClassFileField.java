/*      */ package sun.org.mozilla.classfile.internal;
/*      */ 
/*      */ final class ClassFileField
/*      */ {
/*      */   private short itsNameIndex;
/*      */   private short itsTypeIndex;
/*      */   private short itsFlags;
/*      */   private boolean itsHasAttributes;
/*      */   private short itsAttr1;
/*      */   private short itsAttr2;
/*      */   private short itsAttr3;
/*      */   private int itsIndex;
/*      */ 
/*      */   ClassFileField(short paramShort1, short paramShort2, short paramShort3)
/*      */   {
/* 4288 */     this.itsNameIndex = paramShort1;
/* 4289 */     this.itsTypeIndex = paramShort2;
/* 4290 */     this.itsFlags = paramShort3;
/* 4291 */     this.itsHasAttributes = false;
/*      */   }
/*      */ 
/*      */   void setAttributes(short paramShort1, short paramShort2, short paramShort3, int paramInt)
/*      */   {
/* 4296 */     this.itsHasAttributes = true;
/* 4297 */     this.itsAttr1 = paramShort1;
/* 4298 */     this.itsAttr2 = paramShort2;
/* 4299 */     this.itsAttr3 = paramShort3;
/* 4300 */     this.itsIndex = paramInt;
/*      */   }
/*      */ 
/*      */   int write(byte[] paramArrayOfByte, int paramInt)
/*      */   {
/* 4305 */     paramInt = ClassFileWriter.putInt16(this.itsFlags, paramArrayOfByte, paramInt);
/* 4306 */     paramInt = ClassFileWriter.putInt16(this.itsNameIndex, paramArrayOfByte, paramInt);
/* 4307 */     paramInt = ClassFileWriter.putInt16(this.itsTypeIndex, paramArrayOfByte, paramInt);
/* 4308 */     if (!this.itsHasAttributes)
/*      */     {
/* 4310 */       paramInt = ClassFileWriter.putInt16(0, paramArrayOfByte, paramInt);
/*      */     } else {
/* 4312 */       paramInt = ClassFileWriter.putInt16(1, paramArrayOfByte, paramInt);
/* 4313 */       paramInt = ClassFileWriter.putInt16(this.itsAttr1, paramArrayOfByte, paramInt);
/* 4314 */       paramInt = ClassFileWriter.putInt16(this.itsAttr2, paramArrayOfByte, paramInt);
/* 4315 */       paramInt = ClassFileWriter.putInt16(this.itsAttr3, paramArrayOfByte, paramInt);
/* 4316 */       paramInt = ClassFileWriter.putInt16(this.itsIndex, paramArrayOfByte, paramInt);
/*      */     }
/* 4318 */     return paramInt;
/*      */   }
/*      */ 
/*      */   int getWriteSize()
/*      */   {
/* 4323 */     int i = 6;
/* 4324 */     if (!this.itsHasAttributes)
/* 4325 */       i += 2;
/*      */     else {
/* 4327 */       i += 10;
/*      */     }
/* 4329 */     return i;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.classfile.internal.ClassFileField
 * JD-Core Version:    0.6.2
 */