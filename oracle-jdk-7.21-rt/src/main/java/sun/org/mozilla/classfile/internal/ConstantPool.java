/*      */ package sun.org.mozilla.classfile.internal;
/*      */ 
/*      */ import sun.org.mozilla.javascript.internal.ObjToIntMap;
/*      */ import sun.org.mozilla.javascript.internal.UintMap;
/*      */ 
/*      */ final class ConstantPool
/*      */ {
/*      */   private static final int ConstantPoolSize = 256;
/*      */   static final byte CONSTANT_Class = 7;
/*      */   static final byte CONSTANT_Fieldref = 9;
/*      */   static final byte CONSTANT_Methodref = 10;
/*      */   static final byte CONSTANT_InterfaceMethodref = 11;
/*      */   static final byte CONSTANT_String = 8;
/*      */   static final byte CONSTANT_Integer = 3;
/*      */   static final byte CONSTANT_Float = 4;
/*      */   static final byte CONSTANT_Long = 5;
/*      */   static final byte CONSTANT_Double = 6;
/*      */   static final byte CONSTANT_NameAndType = 12;
/*      */   static final byte CONSTANT_Utf8 = 1;
/*      */   private ClassFileWriter cfw;
/*      */   private static final int MAX_UTF_ENCODING_SIZE = 65535;
/* 4721 */   private UintMap itsStringConstHash = new UintMap();
/* 4722 */   private ObjToIntMap itsUtf8Hash = new ObjToIntMap();
/* 4723 */   private ObjToIntMap itsFieldRefHash = new ObjToIntMap();
/* 4724 */   private ObjToIntMap itsMethodRefHash = new ObjToIntMap();
/* 4725 */   private ObjToIntMap itsClassHash = new ObjToIntMap();
/*      */   private int itsTop;
/*      */   private int itsTopIndex;
/* 4729 */   private UintMap itsConstantData = new UintMap();
/* 4730 */   private UintMap itsPoolTypes = new UintMap();
/*      */   private byte[] itsPool;
/*      */ 
/*      */   ConstantPool(ClassFileWriter paramClassFileWriter)
/*      */   {
/* 4405 */     this.cfw = paramClassFileWriter;
/* 4406 */     this.itsTopIndex = 1;
/* 4407 */     this.itsPool = new byte[256];
/* 4408 */     this.itsTop = 0;
/*      */   }
/*      */ 
/*      */   int write(byte[] paramArrayOfByte, int paramInt)
/*      */   {
/* 4427 */     paramInt = ClassFileWriter.putInt16((short)this.itsTopIndex, paramArrayOfByte, paramInt);
/* 4428 */     System.arraycopy(this.itsPool, 0, paramArrayOfByte, paramInt, this.itsTop);
/* 4429 */     paramInt += this.itsTop;
/* 4430 */     return paramInt;
/*      */   }
/*      */ 
/*      */   int getWriteSize()
/*      */   {
/* 4435 */     return 2 + this.itsTop;
/*      */   }
/*      */ 
/*      */   int addConstant(int paramInt)
/*      */   {
/* 4440 */     ensure(5);
/* 4441 */     this.itsPool[(this.itsTop++)] = 3;
/* 4442 */     this.itsTop = ClassFileWriter.putInt32(paramInt, this.itsPool, this.itsTop);
/* 4443 */     this.itsPoolTypes.put(this.itsTopIndex, 3);
/* 4444 */     return (short)this.itsTopIndex++;
/*      */   }
/*      */ 
/*      */   int addConstant(long paramLong)
/*      */   {
/* 4449 */     ensure(9);
/* 4450 */     this.itsPool[(this.itsTop++)] = 5;
/* 4451 */     this.itsTop = ClassFileWriter.putInt64(paramLong, this.itsPool, this.itsTop);
/* 4452 */     int i = this.itsTopIndex;
/* 4453 */     this.itsTopIndex += 2;
/* 4454 */     this.itsPoolTypes.put(i, 5);
/* 4455 */     return i;
/*      */   }
/*      */ 
/*      */   int addConstant(float paramFloat)
/*      */   {
/* 4460 */     ensure(5);
/* 4461 */     this.itsPool[(this.itsTop++)] = 4;
/* 4462 */     int i = Float.floatToIntBits(paramFloat);
/* 4463 */     this.itsTop = ClassFileWriter.putInt32(i, this.itsPool, this.itsTop);
/* 4464 */     this.itsPoolTypes.put(this.itsTopIndex, 4);
/* 4465 */     return this.itsTopIndex++;
/*      */   }
/*      */ 
/*      */   int addConstant(double paramDouble)
/*      */   {
/* 4470 */     ensure(9);
/* 4471 */     this.itsPool[(this.itsTop++)] = 6;
/* 4472 */     long l = Double.doubleToLongBits(paramDouble);
/* 4473 */     this.itsTop = ClassFileWriter.putInt64(l, this.itsPool, this.itsTop);
/* 4474 */     int i = this.itsTopIndex;
/* 4475 */     this.itsTopIndex += 2;
/* 4476 */     this.itsPoolTypes.put(i, 6);
/* 4477 */     return i;
/*      */   }
/*      */ 
/*      */   int addConstant(String paramString)
/*      */   {
/* 4482 */     int i = 0xFFFF & addUtf8(paramString);
/* 4483 */     int j = this.itsStringConstHash.getInt(i, -1);
/* 4484 */     if (j == -1) {
/* 4485 */       j = this.itsTopIndex++;
/* 4486 */       ensure(3);
/* 4487 */       this.itsPool[(this.itsTop++)] = 8;
/* 4488 */       this.itsTop = ClassFileWriter.putInt16(i, this.itsPool, this.itsTop);
/* 4489 */       this.itsStringConstHash.put(i, j);
/*      */     }
/* 4491 */     this.itsPoolTypes.put(j, 8);
/* 4492 */     return j;
/*      */   }
/*      */ 
/*      */   boolean isUnderUtfEncodingLimit(String paramString)
/*      */   {
/* 4497 */     int i = paramString.length();
/* 4498 */     if (i * 3 <= 65535)
/* 4499 */       return true;
/* 4500 */     if (i > 65535) {
/* 4501 */       return false;
/*      */     }
/* 4503 */     return i == getUtfEncodingLimit(paramString, 0, i);
/*      */   }
/*      */ 
/*      */   int getUtfEncodingLimit(String paramString, int paramInt1, int paramInt2)
/*      */   {
/* 4512 */     if ((paramInt2 - paramInt1) * 3 <= 65535) {
/* 4513 */       return paramInt2;
/*      */     }
/* 4515 */     int i = 65535;
/* 4516 */     for (int j = paramInt1; j != paramInt2; j++) {
/* 4517 */       int k = paramString.charAt(j);
/* 4518 */       if ((0 != k) && (k <= 127))
/* 4519 */         i--;
/* 4520 */       else if (k < 2047)
/* 4521 */         i -= 2;
/*      */       else {
/* 4523 */         i -= 3;
/*      */       }
/* 4525 */       if (i < 0) {
/* 4526 */         return j;
/*      */       }
/*      */     }
/* 4529 */     return paramInt2;
/*      */   }
/*      */ 
/*      */   short addUtf8(String paramString)
/*      */   {
/* 4534 */     int i = this.itsUtf8Hash.get(paramString, -1);
/* 4535 */     if (i == -1) {
/* 4536 */       int j = paramString.length();
/*      */       int k;
/* 4538 */       if (j > 65535) {
/* 4539 */         k = 1;
/*      */       } else {
/* 4541 */         k = 0;
/*      */ 
/* 4544 */         ensure(3 + j * 3);
/* 4545 */         int m = this.itsTop;
/*      */ 
/* 4547 */         this.itsPool[(m++)] = 1;
/* 4548 */         m += 2;
/*      */ 
/* 4550 */         char[] arrayOfChar = this.cfw.getCharBuffer(j);
/* 4551 */         paramString.getChars(0, j, arrayOfChar, 0);
/*      */ 
/* 4553 */         for (int n = 0; n != j; n++) {
/* 4554 */           int i1 = arrayOfChar[n];
/* 4555 */           if ((i1 != 0) && (i1 <= 127)) {
/* 4556 */             this.itsPool[(m++)] = ((byte)i1);
/* 4557 */           } else if (i1 > 2047) {
/* 4558 */             this.itsPool[(m++)] = ((byte)(0xE0 | i1 >> 12));
/* 4559 */             this.itsPool[(m++)] = ((byte)(0x80 | i1 >> 6 & 0x3F));
/* 4560 */             this.itsPool[(m++)] = ((byte)(0x80 | i1 & 0x3F));
/*      */           } else {
/* 4562 */             this.itsPool[(m++)] = ((byte)(0xC0 | i1 >> 6));
/* 4563 */             this.itsPool[(m++)] = ((byte)(0x80 | i1 & 0x3F));
/*      */           }
/*      */         }
/*      */ 
/* 4567 */         n = m - (this.itsTop + 1 + 2);
/* 4568 */         if (n > 65535) {
/* 4569 */           k = 1;
/*      */         }
/*      */         else {
/* 4572 */           this.itsPool[(this.itsTop + 1)] = ((byte)(n >>> 8));
/* 4573 */           this.itsPool[(this.itsTop + 2)] = ((byte)n);
/*      */ 
/* 4575 */           this.itsTop = m;
/* 4576 */           i = this.itsTopIndex++;
/* 4577 */           this.itsUtf8Hash.put(paramString, i);
/*      */         }
/*      */       }
/* 4580 */       if (k != 0) {
/* 4581 */         throw new IllegalArgumentException("Too big string");
/*      */       }
/*      */     }
/* 4584 */     setConstantData(i, paramString);
/* 4585 */     this.itsPoolTypes.put(i, 1);
/* 4586 */     return (short)i;
/*      */   }
/*      */ 
/*      */   private short addNameAndType(String paramString1, String paramString2)
/*      */   {
/* 4591 */     int i = addUtf8(paramString1);
/* 4592 */     int j = addUtf8(paramString2);
/* 4593 */     ensure(5);
/* 4594 */     this.itsPool[(this.itsTop++)] = 12;
/* 4595 */     this.itsTop = ClassFileWriter.putInt16(i, this.itsPool, this.itsTop);
/* 4596 */     this.itsTop = ClassFileWriter.putInt16(j, this.itsPool, this.itsTop);
/* 4597 */     this.itsPoolTypes.put(this.itsTopIndex, 12);
/* 4598 */     return (short)this.itsTopIndex++;
/*      */   }
/*      */ 
/*      */   short addClass(String paramString)
/*      */   {
/* 4603 */     int i = this.itsClassHash.get(paramString, -1);
/* 4604 */     if (i == -1) {
/* 4605 */       String str = paramString;
/* 4606 */       if (paramString.indexOf('.') > 0) {
/* 4607 */         str = ClassFileWriter.getSlashedForm(paramString);
/* 4608 */         i = this.itsClassHash.get(str, -1);
/* 4609 */         if (i != -1) {
/* 4610 */           this.itsClassHash.put(paramString, i);
/*      */         }
/*      */       }
/* 4613 */       if (i == -1) {
/* 4614 */         int j = addUtf8(str);
/* 4615 */         ensure(3);
/* 4616 */         this.itsPool[(this.itsTop++)] = 7;
/* 4617 */         this.itsTop = ClassFileWriter.putInt16(j, this.itsPool, this.itsTop);
/* 4618 */         i = this.itsTopIndex++;
/* 4619 */         this.itsClassHash.put(str, i);
/* 4620 */         if (paramString != str) {
/* 4621 */           this.itsClassHash.put(paramString, i);
/*      */         }
/*      */       }
/*      */     }
/* 4625 */     setConstantData(i, paramString);
/* 4626 */     this.itsPoolTypes.put(i, 7);
/* 4627 */     return (short)i;
/*      */   }
/*      */ 
/*      */   short addFieldRef(String paramString1, String paramString2, String paramString3)
/*      */   {
/* 4632 */     FieldOrMethodRef localFieldOrMethodRef = new FieldOrMethodRef(paramString1, paramString2, paramString3);
/*      */ 
/* 4635 */     int i = this.itsFieldRefHash.get(localFieldOrMethodRef, -1);
/* 4636 */     if (i == -1) {
/* 4637 */       int j = addNameAndType(paramString2, paramString3);
/* 4638 */       int k = addClass(paramString1);
/* 4639 */       ensure(5);
/* 4640 */       this.itsPool[(this.itsTop++)] = 9;
/* 4641 */       this.itsTop = ClassFileWriter.putInt16(k, this.itsPool, this.itsTop);
/* 4642 */       this.itsTop = ClassFileWriter.putInt16(j, this.itsPool, this.itsTop);
/* 4643 */       i = this.itsTopIndex++;
/* 4644 */       this.itsFieldRefHash.put(localFieldOrMethodRef, i);
/*      */     }
/* 4646 */     setConstantData(i, localFieldOrMethodRef);
/* 4647 */     this.itsPoolTypes.put(i, 9);
/* 4648 */     return (short)i;
/*      */   }
/*      */ 
/*      */   short addMethodRef(String paramString1, String paramString2, String paramString3)
/*      */   {
/* 4654 */     FieldOrMethodRef localFieldOrMethodRef = new FieldOrMethodRef(paramString1, paramString2, paramString3);
/*      */ 
/* 4657 */     int i = this.itsMethodRefHash.get(localFieldOrMethodRef, -1);
/* 4658 */     if (i == -1) {
/* 4659 */       int j = addNameAndType(paramString2, paramString3);
/* 4660 */       int k = addClass(paramString1);
/* 4661 */       ensure(5);
/* 4662 */       this.itsPool[(this.itsTop++)] = 10;
/* 4663 */       this.itsTop = ClassFileWriter.putInt16(k, this.itsPool, this.itsTop);
/* 4664 */       this.itsTop = ClassFileWriter.putInt16(j, this.itsPool, this.itsTop);
/* 4665 */       i = this.itsTopIndex++;
/* 4666 */       this.itsMethodRefHash.put(localFieldOrMethodRef, i);
/*      */     }
/* 4668 */     setConstantData(i, localFieldOrMethodRef);
/* 4669 */     this.itsPoolTypes.put(i, 10);
/* 4670 */     return (short)i;
/*      */   }
/*      */ 
/*      */   short addInterfaceMethodRef(String paramString1, String paramString2, String paramString3)
/*      */   {
/* 4676 */     int i = addNameAndType(paramString2, paramString3);
/* 4677 */     int j = addClass(paramString1);
/* 4678 */     ensure(5);
/* 4679 */     this.itsPool[(this.itsTop++)] = 11;
/* 4680 */     this.itsTop = ClassFileWriter.putInt16(j, this.itsPool, this.itsTop);
/* 4681 */     this.itsTop = ClassFileWriter.putInt16(i, this.itsPool, this.itsTop);
/* 4682 */     FieldOrMethodRef localFieldOrMethodRef = new FieldOrMethodRef(paramString1, paramString2, paramString3);
/*      */ 
/* 4684 */     setConstantData(this.itsTopIndex, localFieldOrMethodRef);
/* 4685 */     this.itsPoolTypes.put(this.itsTopIndex, 11);
/* 4686 */     return (short)this.itsTopIndex++;
/*      */   }
/*      */ 
/*      */   Object getConstantData(int paramInt)
/*      */   {
/* 4691 */     return this.itsConstantData.getObject(paramInt);
/*      */   }
/*      */ 
/*      */   void setConstantData(int paramInt, Object paramObject)
/*      */   {
/* 4696 */     this.itsConstantData.put(paramInt, paramObject);
/*      */   }
/*      */ 
/*      */   byte getConstantType(int paramInt)
/*      */   {
/* 4701 */     return (byte)this.itsPoolTypes.getInt(paramInt, 0);
/*      */   }
/*      */ 
/*      */   void ensure(int paramInt)
/*      */   {
/* 4706 */     if (this.itsTop + paramInt > this.itsPool.length) {
/* 4707 */       int i = this.itsPool.length * 2;
/* 4708 */       if (this.itsTop + paramInt > i) {
/* 4709 */         i = this.itsTop + paramInt;
/*      */       }
/* 4711 */       byte[] arrayOfByte = new byte[i];
/* 4712 */       System.arraycopy(this.itsPool, 0, arrayOfByte, 0, this.itsTop);
/* 4713 */       this.itsPool = arrayOfByte;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.classfile.internal.ConstantPool
 * JD-Core Version:    0.6.2
 */