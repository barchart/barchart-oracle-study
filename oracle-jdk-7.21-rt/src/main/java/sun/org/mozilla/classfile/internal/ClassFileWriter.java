/*      */ package sun.org.mozilla.classfile.internal;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.util.Arrays;
/*      */ import sun.org.mozilla.javascript.internal.ObjArray;
/*      */ import sun.org.mozilla.javascript.internal.UintMap;
/*      */ 
/*      */ public class ClassFileWriter
/*      */ {
/*      */   public static final short ACC_PUBLIC = 1;
/*      */   public static final short ACC_PRIVATE = 2;
/*      */   public static final short ACC_PROTECTED = 4;
/*      */   public static final short ACC_STATIC = 8;
/*      */   public static final short ACC_FINAL = 16;
/*      */   public static final short ACC_SUPER = 32;
/*      */   public static final short ACC_SYNCHRONIZED = 32;
/*      */   public static final short ACC_VOLATILE = 64;
/*      */   public static final short ACC_TRANSIENT = 128;
/*      */   public static final short ACC_NATIVE = 256;
/*      */   public static final short ACC_ABSTRACT = 1024;
/* 4156 */   private int[] itsSuperBlockStarts = null;
/* 4157 */   private int itsSuperBlockStartsTop = 0;
/*      */   private static final int SuperBlockStartsSize = 4;
/* 4164 */   private UintMap itsJumpFroms = null;
/*      */   private static final int LineNumberTableSize = 16;
/*      */   private static final int ExceptionTableSize = 4;
/* 4213 */   private static final int MajorVersion = 48;
/* 4214 */   private static final int MinorVersion = 0;
/* 4215 */   private static final boolean GenerateStackMap = false;
/*      */   private static final int FileHeaderConstant = -889275714;
/*      */   private static final boolean DEBUGSTACK = false;
/*      */   private static final boolean DEBUGLABELS = false;
/*      */   private static final boolean DEBUGCODE = false;
/*      */   private String generatedClassName;
/*      */   private ExceptionTableEntry[] itsExceptionTable;
/*      */   private int itsExceptionTableTop;
/*      */   private int[] itsLineNumberTable;
/*      */   private int itsLineNumberTableTop;
/* 4232 */   private byte[] itsCodeBuffer = new byte[256];
/*      */   private int itsCodeBufferTop;
/*      */   private ConstantPool itsConstantPool;
/*      */   private ClassFileMethod itsCurrentMethod;
/*      */   private short itsStackTop;
/*      */   private short itsMaxStack;
/*      */   private short itsMaxLocals;
/* 4243 */   private ObjArray itsMethods = new ObjArray();
/* 4244 */   private ObjArray itsFields = new ObjArray();
/* 4245 */   private ObjArray itsInterfaces = new ObjArray();
/*      */   private short itsFlags;
/*      */   private short itsThisClassIndex;
/*      */   private short itsSuperClassIndex;
/*      */   private short itsSourceFileNameIndex;
/*      */   private static final int MIN_LABEL_TABLE_SIZE = 32;
/*      */   private int[] itsLabelTable;
/*      */   private int itsLabelTableTop;
/*      */   private static final int MIN_FIXUP_TABLE_SIZE = 40;
/*      */   private long[] itsFixupTable;
/*      */   private int itsFixupTableTop;
/*      */   private ObjArray itsVarDescriptors;
/* 4262 */   private char[] tmpCharBuffer = new char[64];
/*      */ 
/*      */   public ClassFileWriter(String paramString1, String paramString2, String paramString3)
/*      */   {
/*   87 */     this.generatedClassName = paramString1;
/*   88 */     this.itsConstantPool = new ConstantPool(this);
/*   89 */     this.itsThisClassIndex = this.itsConstantPool.addClass(paramString1);
/*   90 */     this.itsSuperClassIndex = this.itsConstantPool.addClass(paramString2);
/*   91 */     if (paramString3 != null) {
/*   92 */       this.itsSourceFileNameIndex = this.itsConstantPool.addUtf8(paramString3);
/*      */     }
/*      */ 
/*   96 */     this.itsFlags = 33;
/*      */   }
/*      */ 
/*      */   public final String getClassName()
/*      */   {
/*  101 */     return this.generatedClassName;
/*      */   }
/*      */ 
/*      */   public void addInterface(String paramString)
/*      */   {
/*  115 */     short s = this.itsConstantPool.addClass(paramString);
/*  116 */     this.itsInterfaces.add(Short.valueOf(s));
/*      */   }
/*      */ 
/*      */   public void setFlags(short paramShort)
/*      */   {
/*  146 */     this.itsFlags = paramShort;
/*      */   }
/*      */ 
/*      */   static String getSlashedForm(String paramString)
/*      */   {
/*  151 */     return paramString.replace('.', '/');
/*      */   }
/*      */ 
/*      */   public static String classNameToSignature(String paramString)
/*      */   {
/*  161 */     int i = paramString.length();
/*  162 */     int j = 1 + i;
/*  163 */     char[] arrayOfChar = new char[j + 1];
/*  164 */     arrayOfChar[0] = 'L';
/*  165 */     arrayOfChar[j] = ';';
/*  166 */     paramString.getChars(0, i, arrayOfChar, 1);
/*  167 */     for (int k = 1; k != j; k++) {
/*  168 */       if (arrayOfChar[k] == '.') {
/*  169 */         arrayOfChar[k] = '/';
/*      */       }
/*      */     }
/*  172 */     return new String(arrayOfChar, 0, j + 1);
/*      */   }
/*      */ 
/*      */   public void addField(String paramString1, String paramString2, short paramShort)
/*      */   {
/*  184 */     short s1 = this.itsConstantPool.addUtf8(paramString1);
/*  185 */     short s2 = this.itsConstantPool.addUtf8(paramString2);
/*  186 */     this.itsFields.add(new ClassFileField(s1, s2, paramShort));
/*      */   }
/*      */ 
/*      */   public void addField(String paramString1, String paramString2, short paramShort, int paramInt)
/*      */   {
/*  201 */     short s1 = this.itsConstantPool.addUtf8(paramString1);
/*  202 */     short s2 = this.itsConstantPool.addUtf8(paramString2);
/*  203 */     ClassFileField localClassFileField = new ClassFileField(s1, s2, paramShort);
/*      */ 
/*  205 */     localClassFileField.setAttributes(this.itsConstantPool.addUtf8("ConstantValue"), (short)0, (short)0, this.itsConstantPool.addConstant(paramInt));
/*      */ 
/*  209 */     this.itsFields.add(localClassFileField);
/*      */   }
/*      */ 
/*      */   public void addField(String paramString1, String paramString2, short paramShort, long paramLong)
/*      */   {
/*  224 */     short s1 = this.itsConstantPool.addUtf8(paramString1);
/*  225 */     short s2 = this.itsConstantPool.addUtf8(paramString2);
/*  226 */     ClassFileField localClassFileField = new ClassFileField(s1, s2, paramShort);
/*      */ 
/*  228 */     localClassFileField.setAttributes(this.itsConstantPool.addUtf8("ConstantValue"), (short)0, (short)2, this.itsConstantPool.addConstant(paramLong));
/*      */ 
/*  232 */     this.itsFields.add(localClassFileField);
/*      */   }
/*      */ 
/*      */   public void addField(String paramString1, String paramString2, short paramShort, double paramDouble)
/*      */   {
/*  247 */     short s1 = this.itsConstantPool.addUtf8(paramString1);
/*  248 */     short s2 = this.itsConstantPool.addUtf8(paramString2);
/*  249 */     ClassFileField localClassFileField = new ClassFileField(s1, s2, paramShort);
/*      */ 
/*  251 */     localClassFileField.setAttributes(this.itsConstantPool.addUtf8("ConstantValue"), (short)0, (short)2, this.itsConstantPool.addConstant(paramDouble));
/*      */ 
/*  255 */     this.itsFields.add(localClassFileField);
/*      */   }
/*      */ 
/*      */   public void addVariableDescriptor(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*      */   {
/*  271 */     int i = this.itsConstantPool.addUtf8(paramString1);
/*  272 */     int j = this.itsConstantPool.addUtf8(paramString2);
/*  273 */     int[] arrayOfInt = { i, j, paramInt1, paramInt2 };
/*  274 */     if (this.itsVarDescriptors == null) {
/*  275 */       this.itsVarDescriptors = new ObjArray();
/*      */     }
/*  277 */     this.itsVarDescriptors.add(arrayOfInt);
/*      */   }
/*      */ 
/*      */   public void startMethod(String paramString1, String paramString2, short paramShort)
/*      */   {
/*  292 */     short s1 = this.itsConstantPool.addUtf8(paramString1);
/*  293 */     short s2 = this.itsConstantPool.addUtf8(paramString2);
/*  294 */     this.itsCurrentMethod = new ClassFileMethod(paramString1, s1, paramString2, s2, paramShort);
/*      */ 
/*  296 */     this.itsJumpFroms = new UintMap();
/*  297 */     this.itsMethods.add(this.itsCurrentMethod);
/*  298 */     addSuperBlockStart(0);
/*      */   }
/*      */ 
/*      */   public void stopMethod(short paramShort)
/*      */   {
/*  311 */     if (this.itsCurrentMethod == null) {
/*  312 */       throw new IllegalStateException("No method to stop");
/*      */     }
/*  314 */     fixLabelGotos();
/*      */ 
/*  316 */     this.itsMaxLocals = paramShort;
/*      */ 
/*  318 */     StackMapTable localStackMapTable = null;
/*  319 */     if (GenerateStackMap) {
/*  320 */       finalizeSuperBlockStarts();
/*  321 */       localStackMapTable = new StackMapTable();
/*  322 */       localStackMapTable.generate();
/*      */     }
/*      */ 
/*  325 */     int i = 0;
/*  326 */     if (this.itsLineNumberTable != null)
/*      */     {
/*  330 */       i = 8 + this.itsLineNumberTableTop * 4;
/*      */     }
/*      */ 
/*  333 */     int j = 0;
/*  334 */     if (this.itsVarDescriptors != null)
/*      */     {
/*  338 */       j = 8 + this.itsVarDescriptors.size() * 10;
/*      */     }
/*      */ 
/*  341 */     int k = 0;
/*  342 */     if (localStackMapTable != null) {
/*  343 */       m = localStackMapTable.computeWriteSize();
/*  344 */       if (m > 0) {
/*  345 */         k = 6 + m;
/*      */       }
/*      */     }
/*      */ 
/*  349 */     int m = 14 + this.itsCodeBufferTop + 2 + this.itsExceptionTableTop * 8 + 2 + i + j + k;
/*      */ 
/*  362 */     if (m > 65536)
/*      */     {
/*  366 */       throw new ClassFileFormatException("generated bytecode for method exceeds 64K limit.");
/*      */     }
/*      */ 
/*  369 */     byte[] arrayOfByte = new byte[m];
/*  370 */     int n = 0;
/*  371 */     int i1 = this.itsConstantPool.addUtf8("Code");
/*  372 */     n = putInt16(i1, arrayOfByte, n);
/*  373 */     m -= 6;
/*  374 */     n = putInt32(m, arrayOfByte, n);
/*  375 */     n = putInt16(this.itsMaxStack, arrayOfByte, n);
/*  376 */     n = putInt16(this.itsMaxLocals, arrayOfByte, n);
/*  377 */     n = putInt32(this.itsCodeBufferTop, arrayOfByte, n);
/*  378 */     System.arraycopy(this.itsCodeBuffer, 0, arrayOfByte, n, this.itsCodeBufferTop);
/*      */ 
/*  380 */     n += this.itsCodeBufferTop;
/*      */     int i4;
/*      */     int i5;
/*      */     int i6;
/*  382 */     if (this.itsExceptionTableTop > 0) {
/*  383 */       n = putInt16(this.itsExceptionTableTop, arrayOfByte, n);
/*  384 */       for (i2 = 0; i2 < this.itsExceptionTableTop; i2++) {
/*  385 */         ExceptionTableEntry localExceptionTableEntry = this.itsExceptionTable[i2];
/*  386 */         i4 = (short)getLabelPC(localExceptionTableEntry.itsStartLabel);
/*  387 */         i5 = (short)getLabelPC(localExceptionTableEntry.itsEndLabel);
/*  388 */         i6 = (short)getLabelPC(localExceptionTableEntry.itsHandlerLabel);
/*  389 */         int i7 = localExceptionTableEntry.itsCatchType;
/*  390 */         if (i4 == -1)
/*  391 */           throw new IllegalStateException("start label not defined");
/*  392 */         if (i5 == -1)
/*  393 */           throw new IllegalStateException("end label not defined");
/*  394 */         if (i6 == -1) {
/*  395 */           throw new IllegalStateException("handler label not defined");
/*      */         }
/*      */ 
/*  398 */         n = putInt16(i4, arrayOfByte, n);
/*  399 */         n = putInt16(i5, arrayOfByte, n);
/*  400 */         n = putInt16(i6, arrayOfByte, n);
/*  401 */         n = putInt16(i7, arrayOfByte, n);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  406 */       n = putInt16(0, arrayOfByte, n);
/*      */     }
/*      */ 
/*  409 */     int i2 = 0;
/*  410 */     if (this.itsLineNumberTable != null)
/*  411 */       i2++;
/*  412 */     if (this.itsVarDescriptors != null)
/*  413 */       i2++;
/*  414 */     if (k > 0) {
/*  415 */       i2++;
/*      */     }
/*  417 */     n = putInt16(i2, arrayOfByte, n);
/*      */     int i3;
/*  419 */     if (this.itsLineNumberTable != null) {
/*  420 */       i3 = this.itsConstantPool.addUtf8("LineNumberTable");
/*      */ 
/*  422 */       n = putInt16(i3, arrayOfByte, n);
/*  423 */       i4 = 2 + this.itsLineNumberTableTop * 4;
/*  424 */       n = putInt32(i4, arrayOfByte, n);
/*  425 */       n = putInt16(this.itsLineNumberTableTop, arrayOfByte, n);
/*  426 */       for (i5 = 0; i5 < this.itsLineNumberTableTop; i5++) {
/*  427 */         n = putInt32(this.itsLineNumberTable[i5], arrayOfByte, n);
/*      */       }
/*      */     }
/*      */ 
/*  431 */     if (this.itsVarDescriptors != null) {
/*  432 */       i3 = this.itsConstantPool.addUtf8("LocalVariableTable");
/*      */ 
/*  434 */       n = putInt16(i3, arrayOfByte, n);
/*  435 */       i4 = this.itsVarDescriptors.size();
/*  436 */       i5 = 2 + i4 * 10;
/*  437 */       n = putInt32(i5, arrayOfByte, n);
/*  438 */       n = putInt16(i4, arrayOfByte, n);
/*  439 */       for (i6 = 0; i6 < i4; i6++) {
/*  440 */         int[] arrayOfInt = (int[])this.itsVarDescriptors.get(i6);
/*  441 */         int i8 = arrayOfInt[0];
/*  442 */         int i9 = arrayOfInt[1];
/*  443 */         int i10 = arrayOfInt[2];
/*  444 */         int i11 = arrayOfInt[3];
/*  445 */         int i12 = this.itsCodeBufferTop - i10;
/*      */ 
/*  447 */         n = putInt16(i10, arrayOfByte, n);
/*  448 */         n = putInt16(i12, arrayOfByte, n);
/*  449 */         n = putInt16(i8, arrayOfByte, n);
/*  450 */         n = putInt16(i9, arrayOfByte, n);
/*  451 */         n = putInt16(i11, arrayOfByte, n);
/*      */       }
/*      */     }
/*      */ 
/*  455 */     if (k > 0) {
/*  456 */       i3 = this.itsConstantPool.addUtf8("StackMapTable");
/*      */ 
/*  458 */       i4 = n;
/*  459 */       n = putInt16(i3, arrayOfByte, n);
/*  460 */       n = localStackMapTable.write(arrayOfByte, n);
/*      */     }
/*      */ 
/*  463 */     this.itsCurrentMethod.setCodeAttribute(arrayOfByte);
/*      */ 
/*  465 */     this.itsExceptionTable = null;
/*  466 */     this.itsExceptionTableTop = 0;
/*  467 */     this.itsLineNumberTableTop = 0;
/*  468 */     this.itsCodeBufferTop = 0;
/*  469 */     this.itsCurrentMethod = null;
/*  470 */     this.itsMaxStack = 0;
/*  471 */     this.itsStackTop = 0;
/*  472 */     this.itsLabelTableTop = 0;
/*  473 */     this.itsFixupTableTop = 0;
/*  474 */     this.itsVarDescriptors = null;
/*  475 */     this.itsSuperBlockStarts = null;
/*  476 */     this.itsSuperBlockStartsTop = 0;
/*  477 */     this.itsJumpFroms = null;
/*      */   }
/*      */ 
/*      */   public void add(int paramInt)
/*      */   {
/*  486 */     if (opcodeCount(paramInt) != 0)
/*  487 */       throw new IllegalArgumentException("Unexpected operands");
/*  488 */     int i = this.itsStackTop + stackChange(paramInt);
/*  489 */     if ((i < 0) || (32767 < i)) badStack(i);
/*      */ 
/*  492 */     addToCodeBuffer(paramInt);
/*  493 */     this.itsStackTop = ((short)i);
/*  494 */     if (i > this.itsMaxStack) this.itsMaxStack = ((short)i);
/*      */ 
/*  499 */     if (paramInt == 191)
/*  500 */       addSuperBlockStart(this.itsCodeBufferTop);
/*      */   }
/*      */ 
/*      */   public void add(int paramInt1, int paramInt2)
/*      */   {
/*  515 */     int i = this.itsStackTop + stackChange(paramInt1);
/*  516 */     if ((i < 0) || (32767 < i)) badStack(i);
/*      */ 
/*  518 */     switch (paramInt1)
/*      */     {
/*      */     case 167:
/*  523 */       addSuperBlockStart(this.itsCodeBufferTop + 3);
/*      */     case 153:
/*      */     case 154:
/*      */     case 155:
/*      */     case 156:
/*      */     case 157:
/*      */     case 158:
/*      */     case 159:
/*      */     case 160:
/*      */     case 161:
/*      */     case 162:
/*      */     case 163:
/*      */     case 164:
/*      */     case 165:
/*      */     case 166:
/*      */     case 168:
/*      */     case 198:
/*      */     case 199:
/*  542 */       if (((paramInt2 & 0x80000000) != -2147483648) && (
/*  543 */         (paramInt2 < 0) || (paramInt2 > 65535))) {
/*  544 */         throw new IllegalArgumentException("Bad label for branch");
/*      */       }
/*      */ 
/*  547 */       int j = this.itsCodeBufferTop;
/*  548 */       addToCodeBuffer(paramInt1);
/*      */       int k;
/*  549 */       if ((paramInt2 & 0x80000000) != -2147483648)
/*      */       {
/*  551 */         addToCodeInt16(paramInt2);
/*  552 */         k = paramInt2 + j;
/*  553 */         addSuperBlockStart(k);
/*  554 */         this.itsJumpFroms.put(k, j);
/*      */       }
/*      */       else {
/*  557 */         k = getLabelPC(paramInt2);
/*      */ 
/*  564 */         if (k != -1) {
/*  565 */           int m = k - j;
/*  566 */           addToCodeInt16(m);
/*  567 */           addSuperBlockStart(k);
/*  568 */           this.itsJumpFroms.put(k, j);
/*      */         }
/*      */         else {
/*  571 */           addLabelFixup(paramInt2, j + 1);
/*  572 */           addToCodeInt16(0);
/*      */         }
/*      */       }
/*      */ 
/*  576 */       break;
/*      */     case 16:
/*  579 */       if ((byte)paramInt2 != paramInt2)
/*  580 */         throw new IllegalArgumentException("out of range byte");
/*  581 */       addToCodeBuffer(paramInt1);
/*  582 */       addToCodeBuffer((byte)paramInt2);
/*  583 */       break;
/*      */     case 17:
/*  586 */       if ((short)paramInt2 != paramInt2)
/*  587 */         throw new IllegalArgumentException("out of range short");
/*  588 */       addToCodeBuffer(paramInt1);
/*  589 */       addToCodeInt16(paramInt2);
/*  590 */       break;
/*      */     case 188:
/*  593 */       if ((0 > paramInt2) || (paramInt2 >= 256))
/*  594 */         throw new IllegalArgumentException("out of range index");
/*  595 */       addToCodeBuffer(paramInt1);
/*  596 */       addToCodeBuffer(paramInt2);
/*  597 */       break;
/*      */     case 180:
/*      */     case 181:
/*  601 */       if ((0 > paramInt2) || (paramInt2 >= 65536))
/*  602 */         throw new IllegalArgumentException("out of range field");
/*  603 */       addToCodeBuffer(paramInt1);
/*  604 */       addToCodeInt16(paramInt2);
/*  605 */       break;
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/*  610 */       if ((0 > paramInt2) || (paramInt2 >= 65536))
/*  611 */         throw new IllegalArgumentException("out of range index");
/*  612 */       if ((paramInt2 >= 256) || (paramInt1 == 19) || (paramInt1 == 20))
/*      */       {
/*  616 */         if (paramInt1 == 18)
/*  617 */           addToCodeBuffer(19);
/*      */         else {
/*  619 */           addToCodeBuffer(paramInt1);
/*      */         }
/*  621 */         addToCodeInt16(paramInt2);
/*      */       } else {
/*  623 */         addToCodeBuffer(paramInt1);
/*  624 */         addToCodeBuffer(paramInt2);
/*      */       }
/*  626 */       break;
/*      */     case 21:
/*      */     case 22:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     case 54:
/*      */     case 55:
/*      */     case 56:
/*      */     case 57:
/*      */     case 58:
/*      */     case 169:
/*  639 */       if ((0 > paramInt2) || (paramInt2 >= 65536))
/*  640 */         throw new ClassFileFormatException("out of range variable");
/*  641 */       if (paramInt2 >= 256) {
/*  642 */         addToCodeBuffer(196);
/*  643 */         addToCodeBuffer(paramInt1);
/*  644 */         addToCodeInt16(paramInt2);
/*      */       }
/*      */       else {
/*  647 */         addToCodeBuffer(paramInt1);
/*  648 */         addToCodeBuffer(paramInt2);
/*      */       }
/*  650 */       break;
/*      */     default:
/*  653 */       throw new IllegalArgumentException("Unexpected opcode for 1 operand");
/*      */     }
/*      */ 
/*  657 */     this.itsStackTop = ((short)i);
/*  658 */     if (i > this.itsMaxStack) this.itsMaxStack = ((short)i);
/*      */   }
/*      */ 
/*      */   public void addLoadConstant(int paramInt)
/*      */   {
/*  671 */     switch (paramInt) { case 0:
/*  672 */       add(3); break;
/*      */     case 1:
/*  673 */       add(4); break;
/*      */     case 2:
/*  674 */       add(5); break;
/*      */     case 3:
/*  675 */       add(6); break;
/*      */     case 4:
/*  676 */       add(7); break;
/*      */     case 5:
/*  677 */       add(8); break;
/*      */     default:
/*  679 */       add(18, this.itsConstantPool.addConstant(paramInt));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addLoadConstant(long paramLong)
/*      */   {
/*  690 */     add(20, this.itsConstantPool.addConstant(paramLong));
/*      */   }
/*      */ 
/*      */   public void addLoadConstant(float paramFloat)
/*      */   {
/*  699 */     add(18, this.itsConstantPool.addConstant(paramFloat));
/*      */   }
/*      */ 
/*      */   public void addLoadConstant(double paramDouble)
/*      */   {
/*  708 */     add(20, this.itsConstantPool.addConstant(paramDouble));
/*      */   }
/*      */ 
/*      */   public void addLoadConstant(String paramString)
/*      */   {
/*  717 */     add(18, this.itsConstantPool.addConstant(paramString));
/*      */   }
/*      */ 
/*      */   public void add(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  733 */     int i = this.itsStackTop + stackChange(paramInt1);
/*  734 */     if ((i < 0) || (32767 < i)) badStack(i);
/*      */ 
/*  736 */     if (paramInt1 == 132) {
/*  737 */       if ((0 > paramInt2) || (paramInt2 >= 65536))
/*  738 */         throw new ClassFileFormatException("out of range variable");
/*  739 */       if ((0 > paramInt3) || (paramInt3 >= 65536)) {
/*  740 */         throw new ClassFileFormatException("out of range increment");
/*      */       }
/*  742 */       if ((paramInt2 > 255) || (paramInt3 < -128) || (paramInt3 > 127)) {
/*  743 */         addToCodeBuffer(196);
/*  744 */         addToCodeBuffer(132);
/*  745 */         addToCodeInt16(paramInt2);
/*  746 */         addToCodeInt16(paramInt3);
/*      */       }
/*      */       else {
/*  749 */         addToCodeBuffer(132);
/*  750 */         addToCodeBuffer(paramInt2);
/*  751 */         addToCodeBuffer(paramInt3);
/*      */       }
/*      */     }
/*  754 */     else if (paramInt1 == 197) {
/*  755 */       if ((0 > paramInt2) || (paramInt2 >= 65536))
/*  756 */         throw new IllegalArgumentException("out of range index");
/*  757 */       if ((0 > paramInt3) || (paramInt3 >= 256)) {
/*  758 */         throw new IllegalArgumentException("out of range dimensions");
/*      */       }
/*  760 */       addToCodeBuffer(197);
/*  761 */       addToCodeInt16(paramInt2);
/*  762 */       addToCodeBuffer(paramInt3);
/*      */     }
/*      */     else {
/*  765 */       throw new IllegalArgumentException("Unexpected opcode for 2 operands");
/*      */     }
/*      */ 
/*  768 */     this.itsStackTop = ((short)i);
/*  769 */     if (i > this.itsMaxStack) this.itsMaxStack = ((short)i);
/*      */   }
/*      */ 
/*      */   public void add(int paramInt, String paramString)
/*      */   {
/*  782 */     int i = this.itsStackTop + stackChange(paramInt);
/*  783 */     if ((i < 0) || (32767 < i)) badStack(i);
/*  784 */     switch (paramInt) {
/*      */     case 187:
/*      */     case 189:
/*      */     case 192:
/*      */     case 193:
/*  789 */       int j = this.itsConstantPool.addClass(paramString);
/*  790 */       addToCodeBuffer(paramInt);
/*  791 */       addToCodeInt16(j);
/*      */ 
/*  793 */       break;
/*      */     case 188:
/*      */     case 190:
/*      */     case 191:
/*      */     default:
/*  796 */       throw new IllegalArgumentException("bad opcode for class reference");
/*      */     }
/*      */ 
/*  799 */     this.itsStackTop = ((short)i);
/*  800 */     if (i > this.itsMaxStack) this.itsMaxStack = ((short)i);
/*      */   }
/*      */ 
/*      */   public void add(int paramInt, String paramString1, String paramString2, String paramString3)
/*      */   {
/*  815 */     int i = this.itsStackTop + stackChange(paramInt);
/*  816 */     int j = paramString3.charAt(0);
/*  817 */     int k = (j == 74) || (j == 68) ? 2 : 1;
/*      */ 
/*  819 */     switch (paramInt) {
/*      */     case 178:
/*      */     case 180:
/*  822 */       i += k;
/*  823 */       break;
/*      */     case 179:
/*      */     case 181:
/*  826 */       i -= k;
/*  827 */       break;
/*      */     default:
/*  829 */       throw new IllegalArgumentException("bad opcode for field reference");
/*      */     }
/*      */ 
/*  832 */     if ((i < 0) || (32767 < i)) badStack(i);
/*  833 */     int m = this.itsConstantPool.addFieldRef(paramString1, paramString2, paramString3);
/*      */ 
/*  835 */     addToCodeBuffer(paramInt);
/*  836 */     addToCodeInt16(m);
/*      */ 
/*  838 */     this.itsStackTop = ((short)i);
/*  839 */     if (i > this.itsMaxStack) this.itsMaxStack = ((short)i);
/*      */   }
/*      */ 
/*      */   public void addInvoke(int paramInt, String paramString1, String paramString2, String paramString3)
/*      */   {
/*  854 */     int i = sizeOfParameters(paramString3);
/*  855 */     int j = i >>> 16;
/*  856 */     int k = (short)i;
/*      */ 
/*  858 */     int m = this.itsStackTop + k;
/*  859 */     m += stackChange(paramInt);
/*  860 */     if ((m < 0) || (32767 < m)) badStack(m);
/*      */ 
/*  862 */     switch (paramInt) {
/*      */     case 182:
/*      */     case 183:
/*      */     case 184:
/*      */     case 185:
/*  867 */       addToCodeBuffer(paramInt);
/*      */       int n;
/*  868 */       if (paramInt == 185) {
/*  869 */         n = this.itsConstantPool.addInterfaceMethodRef(paramString1, paramString2, paramString3);
/*      */ 
/*  873 */         addToCodeInt16(n);
/*  874 */         addToCodeBuffer(j + 1);
/*  875 */         addToCodeBuffer(0);
/*      */       }
/*      */       else {
/*  878 */         n = this.itsConstantPool.addMethodRef(paramString1, paramString2, paramString3);
/*      */ 
/*  881 */         addToCodeInt16(n);
/*      */       }
/*      */ 
/*  884 */       break;
/*      */     default:
/*  887 */       throw new IllegalArgumentException("bad opcode for method reference");
/*      */     }
/*      */ 
/*  890 */     this.itsStackTop = ((short)m);
/*  891 */     if (m > this.itsMaxStack) this.itsMaxStack = ((short)m);
/*      */   }
/*      */ 
/*      */   public void addPush(int paramInt)
/*      */   {
/*  905 */     if ((byte)paramInt == paramInt) {
/*  906 */       if (paramInt == -1)
/*  907 */         add(2);
/*  908 */       else if ((0 <= paramInt) && (paramInt <= 5))
/*  909 */         add((byte)(3 + paramInt));
/*      */       else
/*  911 */         add(16, (byte)paramInt);
/*      */     }
/*  913 */     else if ((short)paramInt == paramInt)
/*  914 */       add(17, (short)paramInt);
/*      */     else
/*  916 */       addLoadConstant(paramInt);
/*      */   }
/*      */ 
/*      */   public void addPush(boolean paramBoolean)
/*      */   {
/*  922 */     add(paramBoolean ? 4 : 3);
/*      */   }
/*      */ 
/*      */   public void addPush(long paramLong)
/*      */   {
/*  932 */     int i = (int)paramLong;
/*  933 */     if (i == paramLong) {
/*  934 */       addPush(i);
/*  935 */       add(133);
/*      */     } else {
/*  937 */       addLoadConstant(paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addPush(double paramDouble)
/*      */   {
/*  948 */     if (paramDouble == 0.0D)
/*      */     {
/*  950 */       add(14);
/*  951 */       if (1.0D / paramDouble < 0.0D)
/*      */       {
/*  953 */         add(119);
/*      */       }
/*  955 */     } else if ((paramDouble == 1.0D) || (paramDouble == -1.0D)) {
/*  956 */       add(15);
/*  957 */       if (paramDouble < 0.0D)
/*  958 */         add(119);
/*      */     }
/*      */     else {
/*  961 */       addLoadConstant(paramDouble);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addPush(String paramString)
/*      */   {
/*  972 */     int i = paramString.length();
/*  973 */     int j = this.itsConstantPool.getUtfEncodingLimit(paramString, 0, i);
/*  974 */     if (j == i) {
/*  975 */       addLoadConstant(paramString);
/*  976 */       return;
/*      */     }
/*      */ 
/*  985 */     add(187, "java/lang/StringBuffer");
/*  986 */     add(89);
/*  987 */     addPush(i);
/*  988 */     addInvoke(183, "java/lang/StringBuffer", "<init>", "(I)V");
/*  989 */     int k = 0;
/*      */     while (true) {
/*  991 */       add(89);
/*  992 */       String str = paramString.substring(k, j);
/*  993 */       addLoadConstant(str);
/*  994 */       addInvoke(182, "java/lang/StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;");
/*      */ 
/*  996 */       add(87);
/*  997 */       if (j == i) {
/*      */         break;
/*      */       }
/* 1000 */       k = j;
/* 1001 */       j = this.itsConstantPool.getUtfEncodingLimit(paramString, j, i);
/*      */     }
/* 1003 */     addInvoke(182, "java/lang/StringBuffer", "toString", "()Ljava/lang/String;");
/*      */   }
/*      */ 
/*      */   public boolean isUnderStringSizeLimit(String paramString)
/*      */   {
/* 1015 */     return this.itsConstantPool.isUnderUtfEncodingLimit(paramString);
/*      */   }
/*      */ 
/*      */   public void addIStore(int paramInt)
/*      */   {
/* 1025 */     xop(59, 54, paramInt);
/*      */   }
/*      */ 
/*      */   public void addLStore(int paramInt)
/*      */   {
/* 1035 */     xop(63, 55, paramInt);
/*      */   }
/*      */ 
/*      */   public void addFStore(int paramInt)
/*      */   {
/* 1045 */     xop(67, 56, paramInt);
/*      */   }
/*      */ 
/*      */   public void addDStore(int paramInt)
/*      */   {
/* 1055 */     xop(71, 57, paramInt);
/*      */   }
/*      */ 
/*      */   public void addAStore(int paramInt)
/*      */   {
/* 1065 */     xop(75, 58, paramInt);
/*      */   }
/*      */ 
/*      */   public void addILoad(int paramInt)
/*      */   {
/* 1075 */     xop(26, 21, paramInt);
/*      */   }
/*      */ 
/*      */   public void addLLoad(int paramInt)
/*      */   {
/* 1085 */     xop(30, 22, paramInt);
/*      */   }
/*      */ 
/*      */   public void addFLoad(int paramInt)
/*      */   {
/* 1095 */     xop(34, 23, paramInt);
/*      */   }
/*      */ 
/*      */   public void addDLoad(int paramInt)
/*      */   {
/* 1105 */     xop(38, 24, paramInt);
/*      */   }
/*      */ 
/*      */   public void addALoad(int paramInt)
/*      */   {
/* 1115 */     xop(42, 25, paramInt);
/*      */   }
/*      */ 
/*      */   public void addLoadThis()
/*      */   {
/* 1123 */     add(42);
/*      */   }
/*      */ 
/*      */   private void xop(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1128 */     switch (paramInt3) {
/*      */     case 0:
/* 1130 */       add(paramInt1);
/* 1131 */       break;
/*      */     case 1:
/* 1133 */       add(paramInt1 + 1);
/* 1134 */       break;
/*      */     case 2:
/* 1136 */       add(paramInt1 + 2);
/* 1137 */       break;
/*      */     case 3:
/* 1139 */       add(paramInt1 + 3);
/* 1140 */       break;
/*      */     default:
/* 1142 */       add(paramInt2, paramInt3);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int addTableSwitch(int paramInt1, int paramInt2)
/*      */   {
/* 1152 */     if (paramInt1 > paramInt2) {
/* 1153 */       throw new ClassFileFormatException("Bad bounds: " + paramInt1 + ' ' + paramInt2);
/*      */     }
/* 1155 */     int i = this.itsStackTop + stackChange(170);
/* 1156 */     if ((i < 0) || (32767 < i)) badStack(i);
/*      */ 
/* 1158 */     int j = paramInt2 - paramInt1 + 1;
/* 1159 */     int k = 0x3 & (this.itsCodeBufferTop ^ 0xFFFFFFFF);
/*      */ 
/* 1161 */     int m = addReservedCodeSpace(1 + k + 4 * (3 + j));
/* 1162 */     int n = m;
/* 1163 */     this.itsCodeBuffer[(m++)] = -86;
/* 1164 */     while (k != 0) {
/* 1165 */       this.itsCodeBuffer[(m++)] = 0;
/* 1166 */       k--;
/*      */     }
/* 1168 */     m += 4;
/* 1169 */     m = putInt32(paramInt1, this.itsCodeBuffer, m);
/* 1170 */     putInt32(paramInt2, this.itsCodeBuffer, m);
/*      */ 
/* 1172 */     this.itsStackTop = ((short)i);
/* 1173 */     if (i > this.itsMaxStack) this.itsMaxStack = ((short)i);
/*      */ 
/* 1179 */     return n;
/*      */   }
/*      */ 
/*      */   public final void markTableSwitchDefault(int paramInt)
/*      */   {
/* 1184 */     addSuperBlockStart(this.itsCodeBufferTop);
/* 1185 */     this.itsJumpFroms.put(this.itsCodeBufferTop, paramInt);
/* 1186 */     setTableSwitchJump(paramInt, -1, this.itsCodeBufferTop);
/*      */   }
/*      */ 
/*      */   public final void markTableSwitchCase(int paramInt1, int paramInt2)
/*      */   {
/* 1191 */     addSuperBlockStart(this.itsCodeBufferTop);
/* 1192 */     this.itsJumpFroms.put(this.itsCodeBufferTop, paramInt1);
/* 1193 */     setTableSwitchJump(paramInt1, paramInt2, this.itsCodeBufferTop);
/*      */   }
/*      */ 
/*      */   public final void markTableSwitchCase(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1199 */     if ((0 > paramInt3) || (paramInt3 > this.itsMaxStack))
/* 1200 */       throw new IllegalArgumentException("Bad stack index: " + paramInt3);
/* 1201 */     this.itsStackTop = ((short)paramInt3);
/* 1202 */     addSuperBlockStart(this.itsCodeBufferTop);
/* 1203 */     this.itsJumpFroms.put(this.itsCodeBufferTop, paramInt1);
/* 1204 */     setTableSwitchJump(paramInt1, paramInt2, this.itsCodeBufferTop);
/*      */   }
/*      */ 
/*      */   public void setTableSwitchJump(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1214 */     if ((0 > paramInt3) || (paramInt3 > this.itsCodeBufferTop))
/* 1215 */       throw new IllegalArgumentException("Bad jump target: " + paramInt3);
/* 1216 */     if (paramInt2 < -1) {
/* 1217 */       throw new IllegalArgumentException("Bad case index: " + paramInt2);
/*      */     }
/* 1219 */     int i = 0x3 & (paramInt1 ^ 0xFFFFFFFF);
/*      */     int j;
/* 1221 */     if (paramInt2 < 0)
/*      */     {
/* 1223 */       j = paramInt1 + 1 + i;
/*      */     }
/* 1225 */     else j = paramInt1 + 1 + i + 4 * (3 + paramInt2);
/*      */ 
/* 1227 */     if ((0 > paramInt1) || (paramInt1 > this.itsCodeBufferTop - 16 - i - 1))
/*      */     {
/* 1230 */       throw new IllegalArgumentException(paramInt1 + " is outside a possible range of tableswitch" + " in already generated code");
/*      */     }
/*      */ 
/* 1234 */     if ((0xFF & this.itsCodeBuffer[paramInt1]) != 170) {
/* 1235 */       throw new IllegalArgumentException(paramInt1 + " is not offset of tableswitch statement");
/*      */     }
/*      */ 
/* 1238 */     if ((0 > j) || (j + 4 > this.itsCodeBufferTop))
/*      */     {
/* 1241 */       throw new ClassFileFormatException("Too big case index: " + paramInt2);
/*      */     }
/*      */ 
/* 1245 */     putInt32(paramInt3 - paramInt1, this.itsCodeBuffer, j);
/*      */   }
/*      */ 
/*      */   public int acquireLabel()
/*      */   {
/* 1250 */     int i = this.itsLabelTableTop;
/* 1251 */     if ((this.itsLabelTable == null) || (i == this.itsLabelTable.length)) {
/* 1252 */       if (this.itsLabelTable == null) {
/* 1253 */         this.itsLabelTable = new int[32];
/*      */       } else {
/* 1255 */         int[] arrayOfInt = new int[this.itsLabelTable.length * 2];
/* 1256 */         System.arraycopy(this.itsLabelTable, 0, arrayOfInt, 0, i);
/* 1257 */         this.itsLabelTable = arrayOfInt;
/*      */       }
/*      */     }
/* 1260 */     this.itsLabelTableTop = (i + 1);
/* 1261 */     this.itsLabelTable[i] = -1;
/* 1262 */     return i | 0x80000000;
/*      */   }
/*      */ 
/*      */   public void markLabel(int paramInt)
/*      */   {
/* 1267 */     if (paramInt >= 0) {
/* 1268 */       throw new IllegalArgumentException("Bad label, no biscuit");
/*      */     }
/* 1270 */     paramInt &= 2147483647;
/* 1271 */     if (paramInt > this.itsLabelTableTop) {
/* 1272 */       throw new IllegalArgumentException("Bad label");
/*      */     }
/* 1274 */     if (this.itsLabelTable[paramInt] != -1) {
/* 1275 */       throw new IllegalStateException("Can only mark label once");
/*      */     }
/*      */ 
/* 1278 */     this.itsLabelTable[paramInt] = this.itsCodeBufferTop;
/*      */   }
/*      */ 
/*      */   public void markLabel(int paramInt, short paramShort)
/*      */   {
/* 1283 */     markLabel(paramInt);
/* 1284 */     this.itsStackTop = paramShort;
/*      */   }
/*      */ 
/*      */   public void markHandler(int paramInt) {
/* 1288 */     this.itsStackTop = 1;
/* 1289 */     markLabel(paramInt);
/*      */   }
/*      */ 
/*      */   private int getLabelPC(int paramInt)
/*      */   {
/* 1294 */     if (paramInt >= 0)
/* 1295 */       throw new IllegalArgumentException("Bad label, no biscuit");
/* 1296 */     paramInt &= 2147483647;
/* 1297 */     if (paramInt >= this.itsLabelTableTop)
/* 1298 */       throw new IllegalArgumentException("Bad label");
/* 1299 */     return this.itsLabelTable[paramInt];
/*      */   }
/*      */ 
/*      */   private void addLabelFixup(int paramInt1, int paramInt2)
/*      */   {
/* 1304 */     if (paramInt1 >= 0)
/* 1305 */       throw new IllegalArgumentException("Bad label, no biscuit");
/* 1306 */     paramInt1 &= 2147483647;
/* 1307 */     if (paramInt1 >= this.itsLabelTableTop)
/* 1308 */       throw new IllegalArgumentException("Bad label");
/* 1309 */     int i = this.itsFixupTableTop;
/* 1310 */     if ((this.itsFixupTable == null) || (i == this.itsFixupTable.length)) {
/* 1311 */       if (this.itsFixupTable == null) {
/* 1312 */         this.itsFixupTable = new long[40];
/*      */       } else {
/* 1314 */         long[] arrayOfLong = new long[this.itsFixupTable.length * 2];
/* 1315 */         System.arraycopy(this.itsFixupTable, 0, arrayOfLong, 0, i);
/* 1316 */         this.itsFixupTable = arrayOfLong;
/*      */       }
/*      */     }
/* 1319 */     this.itsFixupTableTop = (i + 1);
/* 1320 */     this.itsFixupTable[i] = (paramInt1 << 32 | paramInt2);
/*      */   }
/*      */ 
/*      */   private void fixLabelGotos()
/*      */   {
/* 1325 */     byte[] arrayOfByte = this.itsCodeBuffer;
/* 1326 */     for (int i = 0; i < this.itsFixupTableTop; i++) {
/* 1327 */       long l = this.itsFixupTable[i];
/* 1328 */       int j = (int)(l >> 32);
/* 1329 */       int k = (int)l;
/* 1330 */       int m = this.itsLabelTable[j];
/* 1331 */       if (m == -1)
/*      */       {
/* 1333 */         throw new RuntimeException();
/*      */       }
/*      */ 
/* 1336 */       addSuperBlockStart(m);
/* 1337 */       this.itsJumpFroms.put(m, k - 1);
/* 1338 */       int n = m - (k - 1);
/* 1339 */       if ((short)n != n) {
/* 1340 */         throw new ClassFileFormatException("Program too complex: too big jump offset");
/*      */       }
/*      */ 
/* 1343 */       arrayOfByte[k] = ((byte)(n >> 8));
/* 1344 */       arrayOfByte[(k + 1)] = ((byte)n);
/*      */     }
/* 1346 */     this.itsFixupTableTop = 0;
/*      */   }
/*      */ 
/*      */   public int getCurrentCodeOffset()
/*      */   {
/* 1355 */     return this.itsCodeBufferTop;
/*      */   }
/*      */ 
/*      */   public short getStackTop() {
/* 1359 */     return this.itsStackTop;
/*      */   }
/*      */ 
/*      */   public void setStackTop(short paramShort) {
/* 1363 */     this.itsStackTop = paramShort;
/*      */   }
/*      */ 
/*      */   public void adjustStackTop(int paramInt) {
/* 1367 */     int i = this.itsStackTop + paramInt;
/* 1368 */     if ((i < 0) || (32767 < i)) badStack(i);
/* 1369 */     this.itsStackTop = ((short)i);
/* 1370 */     if (i > this.itsMaxStack) this.itsMaxStack = ((short)i);
/*      */   }
/*      */ 
/*      */   private void addToCodeBuffer(int paramInt)
/*      */   {
/* 1379 */     int i = addReservedCodeSpace(1);
/* 1380 */     this.itsCodeBuffer[i] = ((byte)paramInt);
/*      */   }
/*      */ 
/*      */   private void addToCodeInt16(int paramInt)
/*      */   {
/* 1385 */     int i = addReservedCodeSpace(2);
/* 1386 */     putInt16(paramInt, this.itsCodeBuffer, i);
/*      */   }
/*      */ 
/*      */   private int addReservedCodeSpace(int paramInt)
/*      */   {
/* 1391 */     if (this.itsCurrentMethod == null)
/* 1392 */       throw new IllegalArgumentException("No method to add to");
/* 1393 */     int i = this.itsCodeBufferTop;
/* 1394 */     int j = i + paramInt;
/* 1395 */     if (j > this.itsCodeBuffer.length) {
/* 1396 */       int k = this.itsCodeBuffer.length * 2;
/* 1397 */       if (j > k) k = j;
/* 1398 */       byte[] arrayOfByte = new byte[k];
/* 1399 */       System.arraycopy(this.itsCodeBuffer, 0, arrayOfByte, 0, i);
/* 1400 */       this.itsCodeBuffer = arrayOfByte;
/*      */     }
/* 1402 */     this.itsCodeBufferTop = j;
/* 1403 */     return i;
/*      */   }
/*      */ 
/*      */   public void addExceptionHandler(int paramInt1, int paramInt2, int paramInt3, String paramString)
/*      */   {
/* 1409 */     if ((paramInt1 & 0x80000000) != -2147483648)
/* 1410 */       throw new IllegalArgumentException("Bad startLabel");
/* 1411 */     if ((paramInt2 & 0x80000000) != -2147483648)
/* 1412 */       throw new IllegalArgumentException("Bad endLabel");
/* 1413 */     if ((paramInt3 & 0x80000000) != -2147483648) {
/* 1414 */       throw new IllegalArgumentException("Bad handlerLabel");
/*      */     }
/*      */ 
/* 1421 */     short s = paramString == null ? 0 : this.itsConstantPool.addClass(paramString);
/*      */ 
/* 1424 */     ExceptionTableEntry localExceptionTableEntry = new ExceptionTableEntry(paramInt1, paramInt2, paramInt3, s);
/*      */ 
/* 1429 */     int i = this.itsExceptionTableTop;
/* 1430 */     if (i == 0) {
/* 1431 */       this.itsExceptionTable = new ExceptionTableEntry[4];
/* 1432 */     } else if (i == this.itsExceptionTable.length) {
/* 1433 */       ExceptionTableEntry[] arrayOfExceptionTableEntry = new ExceptionTableEntry[i * 2];
/* 1434 */       System.arraycopy(this.itsExceptionTable, 0, arrayOfExceptionTableEntry, 0, i);
/* 1435 */       this.itsExceptionTable = arrayOfExceptionTableEntry;
/*      */     }
/* 1437 */     this.itsExceptionTable[i] = localExceptionTableEntry;
/* 1438 */     this.itsExceptionTableTop = (i + 1);
/*      */   }
/*      */ 
/*      */   public void addLineNumberEntry(short paramShort)
/*      */   {
/* 1443 */     if (this.itsCurrentMethod == null)
/* 1444 */       throw new IllegalArgumentException("No method to stop");
/* 1445 */     int i = this.itsLineNumberTableTop;
/* 1446 */     if (i == 0) {
/* 1447 */       this.itsLineNumberTable = new int[16];
/* 1448 */     } else if (i == this.itsLineNumberTable.length) {
/* 1449 */       int[] arrayOfInt = new int[i * 2];
/* 1450 */       System.arraycopy(this.itsLineNumberTable, 0, arrayOfInt, 0, i);
/* 1451 */       this.itsLineNumberTable = arrayOfInt;
/*      */     }
/* 1453 */     this.itsLineNumberTable[i] = ((this.itsCodeBufferTop << 16) + paramShort);
/* 1454 */     this.itsLineNumberTableTop = (i + 1);
/*      */   }
/*      */ 
/*      */   private static char arrayTypeToName(int paramInt)
/*      */   {
/* 2600 */     switch (paramInt) {
/*      */     case 4:
/* 2602 */       return 'Z';
/*      */     case 5:
/* 2604 */       return 'C';
/*      */     case 6:
/* 2606 */       return 'F';
/*      */     case 7:
/* 2608 */       return 'D';
/*      */     case 8:
/* 2610 */       return 'B';
/*      */     case 9:
/* 2612 */       return 'S';
/*      */     case 10:
/* 2614 */       return 'I';
/*      */     case 11:
/* 2616 */       return 'J';
/*      */     }
/* 2618 */     throw new IllegalArgumentException("bad operand");
/*      */   }
/*      */ 
/*      */   private static String classDescriptorToInternalName(String paramString)
/*      */   {
/* 2628 */     return paramString.substring(1, paramString.length() - 1);
/*      */   }
/*      */ 
/*      */   private static String descriptorToInternalName(String paramString)
/*      */   {
/* 2637 */     switch (paramString.charAt(0)) {
/*      */     case 'B':
/*      */     case 'C':
/*      */     case 'D':
/*      */     case 'F':
/*      */     case 'I':
/*      */     case 'J':
/*      */     case 'S':
/*      */     case 'V':
/*      */     case 'Z':
/*      */     case '[':
/* 2648 */       return paramString;
/*      */     case 'L':
/* 2650 */       return classDescriptorToInternalName(paramString);
/*      */     case 'E':
/*      */     case 'G':
/*      */     case 'H':
/*      */     case 'K':
/*      */     case 'M':
/*      */     case 'N':
/*      */     case 'O':
/*      */     case 'P':
/*      */     case 'Q':
/*      */     case 'R':
/*      */     case 'T':
/*      */     case 'U':
/*      */     case 'W':
/*      */     case 'X':
/* 2652 */     case 'Y': } throw new IllegalArgumentException("bad descriptor:" + paramString);
/*      */   }
/*      */ 
/*      */   private int[] createInitialLocals()
/*      */   {
/* 2664 */     int[] arrayOfInt = new int[this.itsMaxLocals];
/* 2665 */     int i = 0;
/*      */ 
/* 2670 */     if ((this.itsCurrentMethod.getFlags() & 0x8) == 0) {
/* 2671 */       if ("<init>".equals(this.itsCurrentMethod.getName()))
/* 2672 */         arrayOfInt[(i++)] = 6;
/*      */       else {
/* 2674 */         arrayOfInt[(i++)] = TypeInfo.OBJECT(this.itsThisClassIndex);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2679 */     String str1 = this.itsCurrentMethod.getType();
/* 2680 */     int j = str1.indexOf('(');
/* 2681 */     int k = str1.indexOf(')');
/* 2682 */     if ((j != 0) || (k < 0)) {
/* 2683 */       throw new IllegalArgumentException("bad method type");
/*      */     }
/* 2685 */     int m = j + 1;
/* 2686 */     StringBuilder localStringBuilder = new StringBuilder();
/* 2687 */     while (m < k)
/* 2688 */       switch (str1.charAt(m)) {
/*      */       case 'B':
/*      */       case 'C':
/*      */       case 'D':
/*      */       case 'F':
/*      */       case 'I':
/*      */       case 'J':
/*      */       case 'S':
/*      */       case 'Z':
/* 2697 */         localStringBuilder.append(str1.charAt(m));
/* 2698 */         m++;
/* 2699 */         break;
/*      */       case 'L':
/* 2701 */         int n = str1.indexOf(';', m) + 1;
/* 2702 */         String str3 = str1.substring(m, n);
/* 2703 */         localStringBuilder.append(str3);
/* 2704 */         m = n;
/* 2705 */         break;
/*      */       case '[':
/* 2707 */         localStringBuilder.append('[');
/* 2708 */         m++;
/* 2709 */         break;
/*      */       case 'E':
/*      */       case 'G':
/*      */       case 'H':
/*      */       case 'K':
/*      */       case 'M':
/*      */       case 'N':
/*      */       case 'O':
/*      */       case 'P':
/*      */       case 'Q':
/*      */       case 'R':
/*      */       case 'T':
/*      */       case 'U':
/*      */       case 'V':
/*      */       case 'W':
/*      */       case 'X':
/*      */       case 'Y':
/*      */       default:
/* 2711 */         String str2 = descriptorToInternalName(localStringBuilder.toString());
/*      */ 
/* 2713 */         int i1 = TypeInfo.fromType(str2, this.itsConstantPool);
/* 2714 */         arrayOfInt[(i++)] = i1;
/* 2715 */         if (TypeInfo.isTwoWords(i1)) {
/* 2716 */           i++;
/*      */         }
/* 2718 */         localStringBuilder.setLength(0);
/*      */       }
/* 2720 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   public void write(OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/* 2732 */     byte[] arrayOfByte = toByteArray();
/* 2733 */     paramOutputStream.write(arrayOfByte);
/*      */   }
/*      */ 
/*      */   private int getWriteSize()
/*      */   {
/* 2738 */     int i = 0;
/*      */ 
/* 2740 */     if (this.itsSourceFileNameIndex != 0) {
/* 2741 */       this.itsConstantPool.addUtf8("SourceFile");
/*      */     }
/*      */ 
/* 2744 */     i += 8;
/* 2745 */     i += this.itsConstantPool.getWriteSize();
/* 2746 */     i += 2;
/* 2747 */     i += 2;
/* 2748 */     i += 2;
/* 2749 */     i += 2;
/* 2750 */     i += 2 * this.itsInterfaces.size();
/*      */ 
/* 2752 */     i += 2;
/* 2753 */     for (int j = 0; j < this.itsFields.size(); j++) {
/* 2754 */       i += ((ClassFileField)this.itsFields.get(j)).getWriteSize();
/*      */     }
/*      */ 
/* 2757 */     i += 2;
/* 2758 */     for (j = 0; j < this.itsMethods.size(); j++) {
/* 2759 */       i += ((ClassFileMethod)this.itsMethods.get(j)).getWriteSize();
/*      */     }
/*      */ 
/* 2762 */     if (this.itsSourceFileNameIndex != 0) {
/* 2763 */       i += 2;
/* 2764 */       i += 2;
/* 2765 */       i += 4;
/* 2766 */       i += 2;
/*      */     } else {
/* 2768 */       i += 2;
/*      */     }
/*      */ 
/* 2771 */     return i;
/*      */   }
/*      */ 
/*      */   public byte[] toByteArray()
/*      */   {
/* 2779 */     int i = getWriteSize();
/* 2780 */     byte[] arrayOfByte = new byte[i];
/* 2781 */     int j = 0;
/*      */ 
/* 2783 */     int k = 0;
/* 2784 */     if (this.itsSourceFileNameIndex != 0) {
/* 2785 */       k = this.itsConstantPool.addUtf8("SourceFile");
/*      */     }
/*      */ 
/* 2789 */     j = putInt32(-889275714, arrayOfByte, j);
/* 2790 */     j = putInt16(MinorVersion, arrayOfByte, j);
/* 2791 */     j = putInt16(MajorVersion, arrayOfByte, j);
/* 2792 */     j = this.itsConstantPool.write(arrayOfByte, j);
/* 2793 */     j = putInt16(this.itsFlags, arrayOfByte, j);
/* 2794 */     j = putInt16(this.itsThisClassIndex, arrayOfByte, j);
/* 2795 */     j = putInt16(this.itsSuperClassIndex, arrayOfByte, j);
/* 2796 */     j = putInt16(this.itsInterfaces.size(), arrayOfByte, j);
/* 2797 */     for (int m = 0; m < this.itsInterfaces.size(); m++) {
/* 2798 */       int n = ((Short)this.itsInterfaces.get(m)).shortValue();
/* 2799 */       j = putInt16(n, arrayOfByte, j);
/*      */     }
/* 2801 */     j = putInt16(this.itsFields.size(), arrayOfByte, j);
/*      */     Object localObject;
/* 2802 */     for (m = 0; m < this.itsFields.size(); m++) {
/* 2803 */       localObject = (ClassFileField)this.itsFields.get(m);
/* 2804 */       j = ((ClassFileField)localObject).write(arrayOfByte, j);
/*      */     }
/* 2806 */     j = putInt16(this.itsMethods.size(), arrayOfByte, j);
/* 2807 */     for (m = 0; m < this.itsMethods.size(); m++) {
/* 2808 */       localObject = (ClassFileMethod)this.itsMethods.get(m);
/* 2809 */       j = ((ClassFileMethod)localObject).write(arrayOfByte, j);
/*      */     }
/* 2811 */     if (this.itsSourceFileNameIndex != 0) {
/* 2812 */       j = putInt16(1, arrayOfByte, j);
/* 2813 */       j = putInt16(k, arrayOfByte, j);
/* 2814 */       j = putInt32(2, arrayOfByte, j);
/* 2815 */       j = putInt16(this.itsSourceFileNameIndex, arrayOfByte, j);
/*      */     } else {
/* 2817 */       j = putInt16(0, arrayOfByte, j);
/*      */     }
/*      */ 
/* 2820 */     if (j != i)
/*      */     {
/* 2822 */       throw new RuntimeException();
/*      */     }
/*      */ 
/* 2825 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   static int putInt64(long paramLong, byte[] paramArrayOfByte, int paramInt)
/*      */   {
/* 2830 */     paramInt = putInt32((int)(paramLong >>> 32), paramArrayOfByte, paramInt);
/* 2831 */     return putInt32((int)paramLong, paramArrayOfByte, paramInt);
/*      */   }
/*      */ 
/*      */   private static void badStack(int paramInt)
/*      */   {
/* 2837 */     String str;
/* 2837 */     if (paramInt < 0) str = "Stack underflow: " + paramInt; else
/* 2838 */       str = "Too big stack: " + paramInt;
/* 2839 */     throw new IllegalStateException(str);
/*      */   }
/*      */ 
/*      */   private static int sizeOfParameters(String paramString)
/*      */   {
/* 2852 */     int i = paramString.length();
/* 2853 */     int j = paramString.lastIndexOf(')');
/* 2854 */     if ((3 <= i) && (paramString.charAt(0) == '(') && (1 <= j) && (j + 1 < i))
/*      */     {
/* 2858 */       int k = 1;
/* 2859 */       int m = 1;
/* 2860 */       int n = 0;
/* 2861 */       int i1 = 0;
/*      */ 
/* 2863 */       while (m != j) {
/* 2864 */         switch (paramString.charAt(m)) { case 'E':
/*      */         case 'G':
/*      */         case 'H':
/*      */         case 'K':
/*      */         case 'M':
/*      */         case 'N':
/*      */         case 'O':
/*      */         case 'P':
/*      */         case 'Q':
/*      */         case 'R':
/*      */         case 'T':
/*      */         case 'U':
/*      */         case 'V':
/*      */         case 'W':
/*      */         case 'X':
/*      */         case 'Y':
/*      */         default:
/* 2866 */           k = 0;
/* 2867 */           break;
/*      */         case 'D':
/*      */         case 'J':
/* 2870 */           n--;
/*      */         case 'B':
/*      */         case 'C':
/*      */         case 'F':
/*      */         case 'I':
/*      */         case 'S':
/*      */         case 'Z':
/* 2878 */           n--;
/* 2879 */           i1++;
/* 2880 */           m++;
/* 2881 */           break;
/*      */         case '[':
/* 2883 */           m++;
/* 2884 */           int i2 = paramString.charAt(m);
/* 2885 */           while (i2 == 91) {
/* 2886 */             m++;
/* 2887 */             i2 = paramString.charAt(m);
/*      */           }
/* 2889 */           switch (i2) { case 69:
/*      */           case 71:
/*      */           case 72:
/*      */           case 75:
/*      */           case 77:
/*      */           case 78:
/*      */           case 79:
/*      */           case 80:
/*      */           case 81:
/*      */           case 82:
/*      */           case 84:
/*      */           case 85:
/*      */           case 86:
/*      */           case 87:
/*      */           case 88:
/*      */           case 89:
/*      */           default:
/* 2891 */             k = 0;
/* 2892 */             break;
/*      */           case 66:
/*      */           case 67:
/*      */           case 68:
/*      */           case 70:
/*      */           case 73:
/*      */           case 74:
/*      */           case 83:
/*      */           case 90:
/* 2901 */             n--;
/* 2902 */             i1++;
/* 2903 */             m++;
/* 2904 */           case 76: } break;
/*      */         case 'L':
/* 2910 */           n--;
/* 2911 */           i1++;
/* 2912 */           m++;
/* 2913 */           int i3 = paramString.indexOf(';', m);
/* 2914 */           if ((m + 1 > i3) || (i3 >= j))
/*      */           {
/* 2917 */             k = 0;
/* 2918 */             break label413;
/*      */           }
/* 2920 */           m = i3 + 1;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2925 */       label413: if (k != 0) {
/* 2926 */         switch (paramString.charAt(j + 1)) { case 'E':
/*      */         case 'G':
/*      */         case 'H':
/*      */         case 'K':
/*      */         case 'M':
/*      */         case 'N':
/*      */         case 'O':
/*      */         case 'P':
/*      */         case 'Q':
/*      */         case 'R':
/*      */         case 'T':
/*      */         case 'U':
/*      */         case 'W':
/*      */         case 'X':
/*      */         case 'Y':
/*      */         default:
/* 2928 */           k = 0;
/* 2929 */           break;
/*      */         case 'D':
/*      */         case 'J':
/* 2932 */           n++;
/*      */         case 'B':
/*      */         case 'C':
/*      */         case 'F':
/*      */         case 'I':
/*      */         case 'L':
/*      */         case 'S':
/*      */         case 'Z':
/*      */         case '[':
/* 2942 */           n++;
/*      */         case 'V':
/*      */         }
/*      */ 
/* 2947 */         if (k != 0) {
/* 2948 */           return i1 << 16 | 0xFFFF & n;
/*      */         }
/*      */       }
/*      */     }
/* 2952 */     throw new IllegalArgumentException("Bad parameter signature: " + paramString);
/*      */   }
/*      */ 
/*      */   static int putInt16(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*      */   {
/* 2958 */     paramArrayOfByte[(paramInt2 + 0)] = ((byte)(paramInt1 >>> 8));
/* 2959 */     paramArrayOfByte[(paramInt2 + 1)] = ((byte)paramInt1);
/* 2960 */     return paramInt2 + 2;
/*      */   }
/*      */ 
/*      */   static int putInt32(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*      */   {
/* 2965 */     paramArrayOfByte[(paramInt2 + 0)] = ((byte)(paramInt1 >>> 24));
/* 2966 */     paramArrayOfByte[(paramInt2 + 1)] = ((byte)(paramInt1 >>> 16));
/* 2967 */     paramArrayOfByte[(paramInt2 + 2)] = ((byte)(paramInt1 >>> 8));
/* 2968 */     paramArrayOfByte[(paramInt2 + 3)] = ((byte)paramInt1);
/* 2969 */     return paramInt2 + 4;
/*      */   }
/*      */ 
/*      */   static int opcodeLength(int paramInt)
/*      */   {
/* 2979 */     switch (paramInt) {
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 14:
/*      */     case 15:
/*      */     case 26:
/*      */     case 27:
/*      */     case 28:
/*      */     case 29:
/*      */     case 30:
/*      */     case 31:
/*      */     case 32:
/*      */     case 33:
/*      */     case 34:
/*      */     case 35:
/*      */     case 36:
/*      */     case 37:
/*      */     case 38:
/*      */     case 39:
/*      */     case 40:
/*      */     case 41:
/*      */     case 42:
/*      */     case 43:
/*      */     case 44:
/*      */     case 45:
/*      */     case 46:
/*      */     case 47:
/*      */     case 48:
/*      */     case 49:
/*      */     case 50:
/*      */     case 51:
/*      */     case 52:
/*      */     case 53:
/*      */     case 59:
/*      */     case 60:
/*      */     case 61:
/*      */     case 62:
/*      */     case 63:
/*      */     case 64:
/*      */     case 65:
/*      */     case 66:
/*      */     case 67:
/*      */     case 68:
/*      */     case 69:
/*      */     case 70:
/*      */     case 71:
/*      */     case 72:
/*      */     case 73:
/*      */     case 74:
/*      */     case 75:
/*      */     case 76:
/*      */     case 77:
/*      */     case 78:
/*      */     case 79:
/*      */     case 80:
/*      */     case 81:
/*      */     case 82:
/*      */     case 83:
/*      */     case 84:
/*      */     case 85:
/*      */     case 86:
/*      */     case 87:
/*      */     case 88:
/*      */     case 89:
/*      */     case 90:
/*      */     case 91:
/*      */     case 92:
/*      */     case 93:
/*      */     case 94:
/*      */     case 95:
/*      */     case 96:
/*      */     case 97:
/*      */     case 98:
/*      */     case 99:
/*      */     case 100:
/*      */     case 101:
/*      */     case 102:
/*      */     case 103:
/*      */     case 104:
/*      */     case 105:
/*      */     case 106:
/*      */     case 107:
/*      */     case 108:
/*      */     case 109:
/*      */     case 110:
/*      */     case 111:
/*      */     case 112:
/*      */     case 113:
/*      */     case 114:
/*      */     case 115:
/*      */     case 116:
/*      */     case 117:
/*      */     case 118:
/*      */     case 119:
/*      */     case 120:
/*      */     case 121:
/*      */     case 122:
/*      */     case 123:
/*      */     case 124:
/*      */     case 125:
/*      */     case 126:
/*      */     case 127:
/*      */     case 128:
/*      */     case 129:
/*      */     case 130:
/*      */     case 131:
/*      */     case 133:
/*      */     case 134:
/*      */     case 135:
/*      */     case 136:
/*      */     case 137:
/*      */     case 138:
/*      */     case 139:
/*      */     case 140:
/*      */     case 141:
/*      */     case 142:
/*      */     case 143:
/*      */     case 144:
/*      */     case 145:
/*      */     case 146:
/*      */     case 147:
/*      */     case 148:
/*      */     case 149:
/*      */     case 150:
/*      */     case 151:
/*      */     case 152:
/*      */     case 172:
/*      */     case 173:
/*      */     case 174:
/*      */     case 175:
/*      */     case 176:
/*      */     case 177:
/*      */     case 190:
/*      */     case 191:
/*      */     case 194:
/*      */     case 195:
/*      */     case 196:
/*      */     case 202:
/*      */     case 254:
/*      */     case 255:
/* 3131 */       return 1;
/*      */     case 16:
/*      */     case 18:
/*      */     case 21:
/*      */     case 22:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     case 54:
/*      */     case 55:
/*      */     case 56:
/*      */     case 57:
/*      */     case 58:
/*      */     case 169:
/*      */     case 188:
/* 3146 */       return 2;
/*      */     case 17:
/*      */     case 19:
/*      */     case 20:
/*      */     case 132:
/*      */     case 153:
/*      */     case 154:
/*      */     case 155:
/*      */     case 156:
/*      */     case 157:
/*      */     case 158:
/*      */     case 159:
/*      */     case 160:
/*      */     case 161:
/*      */     case 162:
/*      */     case 163:
/*      */     case 164:
/*      */     case 165:
/*      */     case 166:
/*      */     case 167:
/*      */     case 168:
/*      */     case 178:
/*      */     case 179:
/*      */     case 180:
/*      */     case 181:
/*      */     case 182:
/*      */     case 183:
/*      */     case 184:
/*      */     case 187:
/*      */     case 189:
/*      */     case 192:
/*      */     case 193:
/*      */     case 198:
/*      */     case 199:
/* 3181 */       return 3;
/*      */     case 197:
/* 3184 */       return 4;
/*      */     case 185:
/*      */     case 200:
/*      */     case 201:
/* 3189 */       return 5;
/*      */     case 170:
/*      */     case 171:
/*      */     case 186:
/*      */     case 203:
/*      */     case 204:
/*      */     case 205:
/*      */     case 206:
/*      */     case 207:
/*      */     case 208:
/*      */     case 209:
/*      */     case 210:
/*      */     case 211:
/*      */     case 212:
/*      */     case 213:
/*      */     case 214:
/*      */     case 215:
/*      */     case 216:
/*      */     case 217:
/*      */     case 218:
/*      */     case 219:
/*      */     case 220:
/*      */     case 221:
/*      */     case 222:
/*      */     case 223:
/*      */     case 224:
/*      */     case 225:
/*      */     case 226:
/*      */     case 227:
/*      */     case 228:
/*      */     case 229:
/*      */     case 230:
/*      */     case 231:
/*      */     case 232:
/*      */     case 233:
/*      */     case 234:
/*      */     case 235:
/*      */     case 236:
/*      */     case 237:
/*      */     case 238:
/*      */     case 239:
/*      */     case 240:
/*      */     case 241:
/*      */     case 242:
/*      */     case 243:
/*      */     case 244:
/*      */     case 245:
/*      */     case 246:
/*      */     case 247:
/*      */     case 248:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/* 3197 */     case 253: } throw new IllegalArgumentException("Bad opcode: " + paramInt);
/*      */   }
/*      */ 
/*      */   static int opcodeCount(int paramInt)
/*      */   {
/* 3205 */     switch (paramInt) {
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 14:
/*      */     case 15:
/*      */     case 26:
/*      */     case 27:
/*      */     case 28:
/*      */     case 29:
/*      */     case 30:
/*      */     case 31:
/*      */     case 32:
/*      */     case 33:
/*      */     case 34:
/*      */     case 35:
/*      */     case 36:
/*      */     case 37:
/*      */     case 38:
/*      */     case 39:
/*      */     case 40:
/*      */     case 41:
/*      */     case 42:
/*      */     case 43:
/*      */     case 44:
/*      */     case 45:
/*      */     case 46:
/*      */     case 47:
/*      */     case 48:
/*      */     case 49:
/*      */     case 50:
/*      */     case 51:
/*      */     case 52:
/*      */     case 53:
/*      */     case 59:
/*      */     case 60:
/*      */     case 61:
/*      */     case 62:
/*      */     case 63:
/*      */     case 64:
/*      */     case 65:
/*      */     case 66:
/*      */     case 67:
/*      */     case 68:
/*      */     case 69:
/*      */     case 70:
/*      */     case 71:
/*      */     case 72:
/*      */     case 73:
/*      */     case 74:
/*      */     case 75:
/*      */     case 76:
/*      */     case 77:
/*      */     case 78:
/*      */     case 79:
/*      */     case 80:
/*      */     case 81:
/*      */     case 82:
/*      */     case 83:
/*      */     case 84:
/*      */     case 85:
/*      */     case 86:
/*      */     case 87:
/*      */     case 88:
/*      */     case 89:
/*      */     case 90:
/*      */     case 91:
/*      */     case 92:
/*      */     case 93:
/*      */     case 94:
/*      */     case 95:
/*      */     case 96:
/*      */     case 97:
/*      */     case 98:
/*      */     case 99:
/*      */     case 100:
/*      */     case 101:
/*      */     case 102:
/*      */     case 103:
/*      */     case 104:
/*      */     case 105:
/*      */     case 106:
/*      */     case 107:
/*      */     case 108:
/*      */     case 109:
/*      */     case 110:
/*      */     case 111:
/*      */     case 112:
/*      */     case 113:
/*      */     case 114:
/*      */     case 115:
/*      */     case 116:
/*      */     case 117:
/*      */     case 118:
/*      */     case 119:
/*      */     case 120:
/*      */     case 121:
/*      */     case 122:
/*      */     case 123:
/*      */     case 124:
/*      */     case 125:
/*      */     case 126:
/*      */     case 127:
/*      */     case 128:
/*      */     case 129:
/*      */     case 130:
/*      */     case 131:
/*      */     case 133:
/*      */     case 134:
/*      */     case 135:
/*      */     case 136:
/*      */     case 137:
/*      */     case 138:
/*      */     case 139:
/*      */     case 140:
/*      */     case 141:
/*      */     case 142:
/*      */     case 143:
/*      */     case 144:
/*      */     case 145:
/*      */     case 146:
/*      */     case 147:
/*      */     case 148:
/*      */     case 149:
/*      */     case 150:
/*      */     case 151:
/*      */     case 152:
/*      */     case 172:
/*      */     case 173:
/*      */     case 174:
/*      */     case 175:
/*      */     case 176:
/*      */     case 177:
/*      */     case 190:
/*      */     case 191:
/*      */     case 194:
/*      */     case 195:
/*      */     case 196:
/*      */     case 202:
/*      */     case 254:
/*      */     case 255:
/* 3357 */       return 0;
/*      */     case 16:
/*      */     case 17:
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/*      */     case 21:
/*      */     case 22:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     case 54:
/*      */     case 55:
/*      */     case 56:
/*      */     case 57:
/*      */     case 58:
/*      */     case 153:
/*      */     case 154:
/*      */     case 155:
/*      */     case 156:
/*      */     case 157:
/*      */     case 158:
/*      */     case 159:
/*      */     case 160:
/*      */     case 161:
/*      */     case 162:
/*      */     case 163:
/*      */     case 164:
/*      */     case 165:
/*      */     case 166:
/*      */     case 167:
/*      */     case 168:
/*      */     case 169:
/*      */     case 178:
/*      */     case 179:
/*      */     case 180:
/*      */     case 181:
/*      */     case 182:
/*      */     case 183:
/*      */     case 184:
/*      */     case 185:
/*      */     case 187:
/*      */     case 188:
/*      */     case 189:
/*      */     case 192:
/*      */     case 193:
/*      */     case 198:
/*      */     case 199:
/*      */     case 200:
/*      */     case 201:
/* 3407 */       return 1;
/*      */     case 132:
/*      */     case 197:
/* 3411 */       return 2;
/*      */     case 170:
/*      */     case 171:
/* 3415 */       return -1;
/*      */     case 186:
/*      */     case 203:
/*      */     case 204:
/*      */     case 205:
/*      */     case 206:
/*      */     case 207:
/*      */     case 208:
/*      */     case 209:
/*      */     case 210:
/*      */     case 211:
/*      */     case 212:
/*      */     case 213:
/*      */     case 214:
/*      */     case 215:
/*      */     case 216:
/*      */     case 217:
/*      */     case 218:
/*      */     case 219:
/*      */     case 220:
/*      */     case 221:
/*      */     case 222:
/*      */     case 223:
/*      */     case 224:
/*      */     case 225:
/*      */     case 226:
/*      */     case 227:
/*      */     case 228:
/*      */     case 229:
/*      */     case 230:
/*      */     case 231:
/*      */     case 232:
/*      */     case 233:
/*      */     case 234:
/*      */     case 235:
/*      */     case 236:
/*      */     case 237:
/*      */     case 238:
/*      */     case 239:
/*      */     case 240:
/*      */     case 241:
/*      */     case 242:
/*      */     case 243:
/*      */     case 244:
/*      */     case 245:
/*      */     case 246:
/*      */     case 247:
/*      */     case 248:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/* 3417 */     case 253: } throw new IllegalArgumentException("Bad opcode: " + paramInt);
/*      */   }
/*      */ 
/*      */   static int stackChange(int paramInt)
/*      */   {
/* 3427 */     switch (paramInt) {
/*      */     case 80:
/*      */     case 82:
/* 3430 */       return -4;
/*      */     case 79:
/*      */     case 81:
/*      */     case 83:
/*      */     case 84:
/*      */     case 85:
/*      */     case 86:
/*      */     case 148:
/*      */     case 151:
/*      */     case 152:
/* 3441 */       return -3;
/*      */     case 55:
/*      */     case 57:
/*      */     case 63:
/*      */     case 64:
/*      */     case 65:
/*      */     case 66:
/*      */     case 71:
/*      */     case 72:
/*      */     case 73:
/*      */     case 74:
/*      */     case 88:
/*      */     case 97:
/*      */     case 99:
/*      */     case 101:
/*      */     case 103:
/*      */     case 105:
/*      */     case 107:
/*      */     case 109:
/*      */     case 111:
/*      */     case 113:
/*      */     case 115:
/*      */     case 127:
/*      */     case 129:
/*      */     case 131:
/*      */     case 159:
/*      */     case 160:
/*      */     case 161:
/*      */     case 162:
/*      */     case 163:
/*      */     case 164:
/*      */     case 165:
/*      */     case 166:
/*      */     case 173:
/*      */     case 175:
/* 3477 */       return -2;
/*      */     case 46:
/*      */     case 48:
/*      */     case 50:
/*      */     case 51:
/*      */     case 52:
/*      */     case 53:
/*      */     case 54:
/*      */     case 56:
/*      */     case 58:
/*      */     case 59:
/*      */     case 60:
/*      */     case 61:
/*      */     case 62:
/*      */     case 67:
/*      */     case 68:
/*      */     case 69:
/*      */     case 70:
/*      */     case 75:
/*      */     case 76:
/*      */     case 77:
/*      */     case 78:
/*      */     case 87:
/*      */     case 96:
/*      */     case 98:
/*      */     case 100:
/*      */     case 102:
/*      */     case 104:
/*      */     case 106:
/*      */     case 108:
/*      */     case 110:
/*      */     case 112:
/*      */     case 114:
/*      */     case 120:
/*      */     case 121:
/*      */     case 122:
/*      */     case 123:
/*      */     case 124:
/*      */     case 125:
/*      */     case 126:
/*      */     case 128:
/*      */     case 130:
/*      */     case 136:
/*      */     case 137:
/*      */     case 142:
/*      */     case 144:
/*      */     case 149:
/*      */     case 150:
/*      */     case 153:
/*      */     case 154:
/*      */     case 155:
/*      */     case 156:
/*      */     case 157:
/*      */     case 158:
/*      */     case 170:
/*      */     case 171:
/*      */     case 172:
/*      */     case 174:
/*      */     case 176:
/*      */     case 180:
/*      */     case 181:
/*      */     case 182:
/*      */     case 183:
/*      */     case 185:
/*      */     case 191:
/*      */     case 194:
/*      */     case 195:
/*      */     case 198:
/*      */     case 199:
/* 3547 */       return -1;
/*      */     case 0:
/*      */     case 47:
/*      */     case 49:
/*      */     case 95:
/*      */     case 116:
/*      */     case 117:
/*      */     case 118:
/*      */     case 119:
/*      */     case 132:
/*      */     case 134:
/*      */     case 138:
/*      */     case 139:
/*      */     case 143:
/*      */     case 145:
/*      */     case 146:
/*      */     case 147:
/*      */     case 167:
/*      */     case 169:
/*      */     case 177:
/*      */     case 178:
/*      */     case 179:
/*      */     case 184:
/*      */     case 188:
/*      */     case 189:
/*      */     case 190:
/*      */     case 192:
/*      */     case 193:
/*      */     case 196:
/*      */     case 200:
/*      */     case 202:
/*      */     case 254:
/*      */     case 255:
/* 3581 */       return 0;
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 16:
/*      */     case 17:
/*      */     case 18:
/*      */     case 19:
/*      */     case 21:
/*      */     case 23:
/*      */     case 25:
/*      */     case 26:
/*      */     case 27:
/*      */     case 28:
/*      */     case 29:
/*      */     case 34:
/*      */     case 35:
/*      */     case 36:
/*      */     case 37:
/*      */     case 42:
/*      */     case 43:
/*      */     case 44:
/*      */     case 45:
/*      */     case 89:
/*      */     case 90:
/*      */     case 91:
/*      */     case 133:
/*      */     case 135:
/*      */     case 140:
/*      */     case 141:
/*      */     case 168:
/*      */     case 187:
/*      */     case 197:
/*      */     case 201:
/* 3624 */       return 1;
/*      */     case 9:
/*      */     case 10:
/*      */     case 14:
/*      */     case 15:
/*      */     case 20:
/*      */     case 22:
/*      */     case 24:
/*      */     case 30:
/*      */     case 31:
/*      */     case 32:
/*      */     case 33:
/*      */     case 38:
/*      */     case 39:
/*      */     case 40:
/*      */     case 41:
/*      */     case 92:
/*      */     case 93:
/*      */     case 94:
/* 3644 */       return 2;
/*      */     case 186:
/*      */     case 203:
/*      */     case 204:
/*      */     case 205:
/*      */     case 206:
/*      */     case 207:
/*      */     case 208:
/*      */     case 209:
/*      */     case 210:
/*      */     case 211:
/*      */     case 212:
/*      */     case 213:
/*      */     case 214:
/*      */     case 215:
/*      */     case 216:
/*      */     case 217:
/*      */     case 218:
/*      */     case 219:
/*      */     case 220:
/*      */     case 221:
/*      */     case 222:
/*      */     case 223:
/*      */     case 224:
/*      */     case 225:
/*      */     case 226:
/*      */     case 227:
/*      */     case 228:
/*      */     case 229:
/*      */     case 230:
/*      */     case 231:
/*      */     case 232:
/*      */     case 233:
/*      */     case 234:
/*      */     case 235:
/*      */     case 236:
/*      */     case 237:
/*      */     case 238:
/*      */     case 239:
/*      */     case 240:
/*      */     case 241:
/*      */     case 242:
/*      */     case 243:
/*      */     case 244:
/*      */     case 245:
/*      */     case 246:
/*      */     case 247:
/*      */     case 248:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/* 3646 */     case 253: } throw new IllegalArgumentException("Bad opcode: " + paramInt);
/*      */   }
/*      */ 
/*      */   private static String bytecodeStr(int paramInt)
/*      */   {
/* 4087 */     return "";
/*      */   }
/*      */ 
/*      */   final char[] getCharBuffer(int paramInt)
/*      */   {
/* 4092 */     if (paramInt > this.tmpCharBuffer.length) {
/* 4093 */       int i = this.tmpCharBuffer.length * 2;
/* 4094 */       if (paramInt > i) i = paramInt;
/* 4095 */       this.tmpCharBuffer = new char[i];
/*      */     }
/* 4097 */     return this.tmpCharBuffer;
/*      */   }
/*      */ 
/*      */   private void addSuperBlockStart(int paramInt)
/*      */   {
/* 4110 */     if (GenerateStackMap) {
/* 4111 */       if (this.itsSuperBlockStarts == null) {
/* 4112 */         this.itsSuperBlockStarts = new int[4];
/* 4113 */       } else if (this.itsSuperBlockStarts.length == this.itsSuperBlockStartsTop) {
/* 4114 */         int[] arrayOfInt = new int[this.itsSuperBlockStartsTop * 2];
/* 4115 */         System.arraycopy(this.itsSuperBlockStarts, 0, arrayOfInt, 0, this.itsSuperBlockStartsTop);
/*      */ 
/* 4117 */         this.itsSuperBlockStarts = arrayOfInt;
/*      */       }
/* 4119 */       this.itsSuperBlockStarts[(this.itsSuperBlockStartsTop++)] = paramInt;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void finalizeSuperBlockStarts()
/*      */   {
/* 4130 */     if (GenerateStackMap) {
/* 4131 */       for (int i = 0; i < this.itsExceptionTableTop; i++) {
/* 4132 */         ExceptionTableEntry localExceptionTableEntry = this.itsExceptionTable[i];
/* 4133 */         k = (short)getLabelPC(localExceptionTableEntry.itsHandlerLabel);
/* 4134 */         addSuperBlockStart(k);
/*      */       }
/* 4136 */       Arrays.sort(this.itsSuperBlockStarts, 0, this.itsSuperBlockStartsTop);
/* 4137 */       i = this.itsSuperBlockStarts[0];
/* 4138 */       int j = 1;
/* 4139 */       for (int k = 1; k < this.itsSuperBlockStartsTop; k++) {
/* 4140 */         int m = this.itsSuperBlockStarts[k];
/* 4141 */         if (i != m) {
/* 4142 */           if (j != k) {
/* 4143 */             this.itsSuperBlockStarts[j] = m;
/*      */           }
/* 4145 */           j++;
/* 4146 */           i = m;
/*      */         }
/*      */       }
/* 4149 */       this.itsSuperBlockStartsTop = j;
/* 4150 */       if (this.itsSuperBlockStarts[(j - 1)] == this.itsCodeBufferTop)
/* 4151 */         this.itsSuperBlockStartsTop -= 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class ClassFileFormatException extends RuntimeException
/*      */   {
/*      */     private static final long serialVersionUID = 1263998431033790599L;
/*      */ 
/*      */     ClassFileFormatException(String paramString)
/*      */     {
/*   69 */       super();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class StackMapTable
/*      */   {
/*      */     private int[] locals;
/*      */     private int localsTop;
/*      */     private int[] stack;
/*      */     private int stackTop;
/*      */     private SuperBlock[] workList;
/*      */     private int workListTop;
/*      */     private SuperBlock[] superBlocks;
/*      */     private SuperBlock[] superBlockDeps;
/*      */     private byte[] rawStackMap;
/*      */     private int rawStackMapTop;
/*      */     static final boolean DEBUGSTACKMAP = false;
/*      */ 
/*      */     StackMapTable()
/*      */     {
/* 1466 */       this.superBlocks = null;
/* 1467 */       this.locals = (this.stack = null);
/* 1468 */       this.workList = null;
/* 1469 */       this.rawStackMap = null;
/* 1470 */       this.localsTop = 0;
/* 1471 */       this.stackTop = 0;
/* 1472 */       this.workListTop = 0;
/* 1473 */       this.rawStackMapTop = 0;
/*      */     }
/*      */ 
/*      */     void generate() {
/* 1477 */       this.superBlocks = new SuperBlock[ClassFileWriter.this.itsSuperBlockStartsTop];
/* 1478 */       int[] arrayOfInt = ClassFileWriter.this.createInitialLocals();
/*      */ 
/* 1480 */       for (int i = 0; i < ClassFileWriter.this.itsSuperBlockStartsTop; i++) {
/* 1481 */         int j = ClassFileWriter.this.itsSuperBlockStarts[i];
/*      */         int k;
/* 1483 */         if (i == ClassFileWriter.this.itsSuperBlockStartsTop - 1)
/* 1484 */           k = ClassFileWriter.this.itsCodeBufferTop;
/*      */         else {
/* 1486 */           k = ClassFileWriter.this.itsSuperBlockStarts[(i + 1)];
/*      */         }
/* 1488 */         this.superBlocks[i] = new SuperBlock(i, j, k, arrayOfInt);
/*      */       }
/*      */ 
/* 1501 */       this.superBlockDeps = getSuperBlockDependencies();
/*      */ 
/* 1503 */       verify();
/*      */     }
/*      */ 
/*      */     private SuperBlock getSuperBlockFromOffset(int paramInt)
/*      */     {
/* 1517 */       for (int i = 0; i < this.superBlocks.length; i++) {
/* 1518 */         SuperBlock localSuperBlock = this.superBlocks[i];
/* 1519 */         if (localSuperBlock == null)
/*      */           break;
/* 1521 */         if ((paramInt >= localSuperBlock.getStart()) && (paramInt < localSuperBlock.getEnd())) {
/* 1522 */           return localSuperBlock;
/*      */         }
/*      */       }
/* 1525 */       throw new IllegalArgumentException("bad offset: " + paramInt);
/*      */     }
/*      */ 
/*      */     private boolean isSuperBlockEnd(int paramInt)
/*      */     {
/* 1533 */       switch (paramInt) {
/*      */       case 167:
/*      */       case 170:
/*      */       case 171:
/*      */       case 172:
/*      */       case 173:
/*      */       case 174:
/*      */       case 176:
/*      */       case 177:
/*      */       case 191:
/*      */       case 200:
/* 1544 */         return true;
/*      */       case 168:
/*      */       case 169:
/*      */       case 175:
/*      */       case 178:
/*      */       case 179:
/*      */       case 180:
/*      */       case 181:
/*      */       case 182:
/*      */       case 183:
/*      */       case 184:
/*      */       case 185:
/*      */       case 186:
/*      */       case 187:
/*      */       case 188:
/*      */       case 189:
/*      */       case 190:
/*      */       case 192:
/*      */       case 193:
/*      */       case 194:
/*      */       case 195:
/*      */       case 196:
/*      */       case 197:
/*      */       case 198:
/* 1546 */       case 199: } return false;
/*      */     }
/*      */ 
/*      */     private SuperBlock[] getSuperBlockDependencies()
/*      */     {
/* 1557 */       SuperBlock[] arrayOfSuperBlock = new SuperBlock[this.superBlocks.length];
/*      */       int k;
/*      */       int m;
/*      */       SuperBlock localSuperBlock1;
/*      */       SuperBlock localSuperBlock2;
/* 1559 */       for (int i = 0; i < ClassFileWriter.this.itsExceptionTableTop; i++) {
/* 1560 */         ExceptionTableEntry localExceptionTableEntry = ClassFileWriter.this.itsExceptionTable[i];
/* 1561 */         k = (short)ClassFileWriter.this.getLabelPC(localExceptionTableEntry.itsStartLabel);
/* 1562 */         m = (short)ClassFileWriter.this.getLabelPC(localExceptionTableEntry.itsHandlerLabel);
/* 1563 */         localSuperBlock1 = getSuperBlockFromOffset(m);
/* 1564 */         localSuperBlock2 = getSuperBlockFromOffset(k);
/* 1565 */         arrayOfSuperBlock[localSuperBlock1.getIndex()] = localSuperBlock2;
/*      */       }
/* 1567 */       int[] arrayOfInt = ClassFileWriter.this.itsJumpFroms.getKeys();
/* 1568 */       for (int j = 0; j < arrayOfInt.length; j++) {
/* 1569 */         k = arrayOfInt[j];
/* 1570 */         m = ClassFileWriter.this.itsJumpFroms.getInt(k, -1);
/* 1571 */         localSuperBlock1 = getSuperBlockFromOffset(m);
/* 1572 */         localSuperBlock2 = getSuperBlockFromOffset(k);
/* 1573 */         arrayOfSuperBlock[localSuperBlock2.getIndex()] = localSuperBlock1;
/*      */       }
/*      */ 
/* 1576 */       return arrayOfSuperBlock;
/*      */     }
/*      */ 
/*      */     private SuperBlock getBranchTarget(int paramInt)
/*      */     {
/*      */       int i;
/* 1586 */       if ((ClassFileWriter.this.itsCodeBuffer[paramInt] & 0xFF) == 200)
/* 1587 */         i = paramInt + getOperand(paramInt + 1, 4);
/*      */       else {
/* 1589 */         i = paramInt + (short)getOperand(paramInt + 1, 2);
/*      */       }
/* 1591 */       return getSuperBlockFromOffset(i);
/*      */     }
/*      */ 
/*      */     private boolean isBranch(int paramInt)
/*      */     {
/* 1599 */       switch (paramInt) {
/*      */       case 153:
/*      */       case 154:
/*      */       case 155:
/*      */       case 156:
/*      */       case 157:
/*      */       case 158:
/*      */       case 159:
/*      */       case 160:
/*      */       case 161:
/*      */       case 162:
/*      */       case 163:
/*      */       case 164:
/*      */       case 165:
/*      */       case 166:
/*      */       case 167:
/*      */       case 198:
/*      */       case 199:
/*      */       case 200:
/* 1618 */         return true;
/*      */       case 168:
/*      */       case 169:
/*      */       case 170:
/*      */       case 171:
/*      */       case 172:
/*      */       case 173:
/*      */       case 174:
/*      */       case 175:
/*      */       case 176:
/*      */       case 177:
/*      */       case 178:
/*      */       case 179:
/*      */       case 180:
/*      */       case 181:
/*      */       case 182:
/*      */       case 183:
/*      */       case 184:
/*      */       case 185:
/*      */       case 186:
/*      */       case 187:
/*      */       case 188:
/*      */       case 189:
/*      */       case 190:
/*      */       case 191:
/*      */       case 192:
/*      */       case 193:
/*      */       case 194:
/*      */       case 195:
/*      */       case 196:
/* 1620 */       case 197: } return false;
/*      */     }
/*      */ 
/*      */     private int getOperand(int paramInt)
/*      */     {
/* 1625 */       return getOperand(paramInt, 1);
/*      */     }
/*      */ 
/*      */     private int getOperand(int paramInt1, int paramInt2)
/*      */     {
/* 1634 */       int i = 0;
/* 1635 */       if (paramInt2 > 4) {
/* 1636 */         throw new IllegalArgumentException("bad operand size");
/*      */       }
/* 1638 */       for (int j = 0; j < paramInt2; j++) {
/* 1639 */         i = i << 8 | ClassFileWriter.this.itsCodeBuffer[(paramInt1 + j)] & 0xFF;
/*      */       }
/* 1641 */       return i;
/*      */     }
/*      */ 
/*      */     private void verify()
/*      */     {
/* 1649 */       int[] arrayOfInt = ClassFileWriter.this.createInitialLocals();
/* 1650 */       this.superBlocks[0].merge(arrayOfInt, arrayOfInt.length, new int[0], 0, ClassFileWriter.this.itsConstantPool);
/*      */ 
/* 1655 */       this.workList = new SuperBlock[] { this.superBlocks[0] };
/* 1656 */       this.workListTop = 1;
/* 1657 */       executeWorkList();
/*      */ 
/* 1660 */       for (int i = 0; i < this.superBlocks.length; i++) {
/* 1661 */         SuperBlock localSuperBlock = this.superBlocks[i];
/* 1662 */         if (!localSuperBlock.isInitialized()) {
/* 1663 */           killSuperBlock(localSuperBlock);
/*      */         }
/*      */       }
/* 1666 */       executeWorkList();
/*      */     }
/*      */ 
/*      */     private void killSuperBlock(SuperBlock paramSuperBlock)
/*      */     {
/* 1685 */       int[] arrayOfInt1 = new int[0];
/* 1686 */       int[] arrayOfInt2 = { TypeInfo.OBJECT("java/lang/Throwable", ClassFileWriter.this.itsConstantPool) };
/*      */ 
/* 1688 */       for (int i = 0; i < ClassFileWriter.this.itsExceptionTableTop; i++) {
/* 1689 */         ExceptionTableEntry localExceptionTableEntry = ClassFileWriter.this.itsExceptionTable[i];
/* 1690 */         int k = ClassFileWriter.this.getLabelPC(localExceptionTableEntry.itsStartLabel);
/* 1691 */         int m = ClassFileWriter.this.getLabelPC(localExceptionTableEntry.itsEndLabel);
/* 1692 */         if (((paramSuperBlock.getStart() >= k) && (paramSuperBlock.getStart() < m)) || ((k >= paramSuperBlock.getStart()) && (k < paramSuperBlock.getEnd())))
/*      */         {
/* 1694 */           int n = ClassFileWriter.this.getLabelPC(localExceptionTableEntry.itsHandlerLabel);
/* 1695 */           SuperBlock localSuperBlock = getSuperBlockFromOffset(n);
/* 1696 */           arrayOfInt1 = localSuperBlock.getLocals();
/* 1697 */           break;
/*      */         }
/*      */       }
/*      */ 
/* 1701 */       paramSuperBlock.merge(arrayOfInt1, arrayOfInt1.length, arrayOfInt2, arrayOfInt2.length, ClassFileWriter.this.itsConstantPool);
/*      */ 
/* 1704 */       i = paramSuperBlock.getEnd() - 1;
/* 1705 */       ClassFileWriter.this.itsCodeBuffer[i] = -65;
/* 1706 */       for (int j = paramSuperBlock.getStart(); j < i; j++)
/* 1707 */         ClassFileWriter.this.itsCodeBuffer[j] = 0;
/*      */     }
/*      */ 
/*      */     private void executeWorkList()
/*      */     {
/* 1712 */       while (this.workListTop > 0) {
/* 1713 */         SuperBlock localSuperBlock = this.workList[(--this.workListTop)];
/* 1714 */         localSuperBlock.setInQueue(false);
/* 1715 */         this.locals = localSuperBlock.getLocals();
/* 1716 */         this.stack = localSuperBlock.getStack();
/* 1717 */         this.localsTop = this.locals.length;
/* 1718 */         this.stackTop = this.stack.length;
/* 1719 */         executeBlock(localSuperBlock);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void executeBlock(SuperBlock paramSuperBlock)
/*      */     {
/* 1727 */       int i = 0;
/* 1728 */       int j = 0;
/*      */ 
/* 1737 */       for (int k = paramSuperBlock.getStart(); k < paramSuperBlock.getEnd(); k += j) {
/* 1738 */         i = ClassFileWriter.this.itsCodeBuffer[k] & 0xFF;
/* 1739 */         j = execute(k);
/*      */         int i2;
/*      */         int i3;
/*      */         int i5;
/* 1746 */         if (isBranch(i)) {
/* 1747 */           SuperBlock localSuperBlock1 = getBranchTarget(k);
/*      */ 
/* 1758 */           flowInto(localSuperBlock1);
/*      */         }
/* 1765 */         else if (i == 170) {
/* 1766 */           m = k + 1 + (0x3 & (k ^ 0xFFFFFFFF));
/* 1767 */           int n = getOperand(m, 4);
/* 1768 */           SuperBlock localSuperBlock2 = getSuperBlockFromOffset(k + n);
/*      */ 
/* 1774 */           flowInto(localSuperBlock2);
/* 1775 */           i2 = getOperand(m + 4, 4);
/* 1776 */           i3 = getOperand(m + 8, 4);
/* 1777 */           int i4 = i3 - i2 + 1;
/* 1778 */           i5 = m + 12;
/* 1779 */           for (int i6 = 0; i6 < i4; i6++) {
/* 1780 */             int i7 = k + getOperand(i5 + 4 * i6, 4);
/* 1781 */             localSuperBlock2 = getSuperBlockFromOffset(i7);
/*      */ 
/* 1787 */             flowInto(localSuperBlock2);
/*      */           }
/*      */         }
/*      */ 
/* 1791 */         for (int m = 0; m < ClassFileWriter.this.itsExceptionTableTop; m++) {
/* 1792 */           ExceptionTableEntry localExceptionTableEntry = ClassFileWriter.this.itsExceptionTable[m];
/* 1793 */           int i1 = (short)ClassFileWriter.this.getLabelPC(localExceptionTableEntry.itsStartLabel);
/* 1794 */           i2 = (short)ClassFileWriter.this.getLabelPC(localExceptionTableEntry.itsEndLabel);
/* 1795 */           if ((k >= i1) && (k < i2))
/*      */           {
/* 1798 */             i3 = (short)ClassFileWriter.this.getLabelPC(localExceptionTableEntry.itsHandlerLabel);
/*      */ 
/* 1800 */             SuperBlock localSuperBlock3 = getSuperBlockFromOffset(i3);
/*      */ 
/* 1803 */             if (localExceptionTableEntry.itsCatchType == 0) {
/* 1804 */               i5 = TypeInfo.OBJECT(ClassFileWriter.this.itsConstantPool.addClass("java/lang/Throwable"));
/*      */             }
/*      */             else {
/* 1807 */               i5 = TypeInfo.OBJECT(localExceptionTableEntry.itsCatchType);
/*      */             }
/* 1809 */             localSuperBlock3.merge(this.locals, this.localsTop, new int[] { i5 }, 1, ClassFileWriter.this.itsConstantPool);
/*      */ 
/* 1811 */             addToWorkList(localSuperBlock3);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1824 */       if (!isSuperBlockEnd(i)) {
/* 1825 */         k = paramSuperBlock.getIndex() + 1;
/* 1826 */         if (k < this.superBlocks.length)
/*      */         {
/* 1832 */           flowInto(this.superBlocks[k]);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void flowInto(SuperBlock paramSuperBlock)
/*      */     {
/* 1842 */       if (paramSuperBlock.merge(this.locals, this.localsTop, this.stack, this.stackTop, ClassFileWriter.this.itsConstantPool))
/* 1843 */         addToWorkList(paramSuperBlock);
/*      */     }
/*      */ 
/*      */     private void addToWorkList(SuperBlock paramSuperBlock)
/*      */     {
/* 1848 */       if (!paramSuperBlock.isInQueue()) {
/* 1849 */         paramSuperBlock.setInQueue(true);
/* 1850 */         paramSuperBlock.setInitialized(true);
/* 1851 */         if (this.workListTop == this.workList.length) {
/* 1852 */           SuperBlock[] arrayOfSuperBlock = new SuperBlock[this.workListTop * 2];
/* 1853 */           System.arraycopy(this.workList, 0, arrayOfSuperBlock, 0, this.workListTop);
/* 1854 */           this.workList = arrayOfSuperBlock;
/*      */         }
/* 1856 */         this.workList[(this.workListTop++)] = paramSuperBlock;
/*      */       }
/*      */     }
/*      */ 
/*      */     private int execute(int paramInt)
/*      */     {
/* 1867 */       int i = ClassFileWriter.this.itsCodeBuffer[paramInt] & 0xFF;
/*      */ 
/* 1869 */       int n = 0;
/*      */       int j;
/*      */       int k;
/*      */       int m;
/*      */       String str1;
/*      */       long l1;
/* 1873 */       switch (i)
/*      */       {
/*      */       case 0:
/*      */       case 132:
/*      */       case 167:
/*      */       case 200:
/* 1879 */         break;
/*      */       case 192:
/* 1881 */         pop();
/* 1882 */         push(TypeInfo.OBJECT(getOperand(paramInt + 1, 2)));
/* 1883 */         break;
/*      */       case 79:
/*      */       case 80:
/*      */       case 81:
/*      */       case 82:
/*      */       case 83:
/*      */       case 84:
/*      */       case 85:
/*      */       case 86:
/* 1892 */         pop();
/*      */       case 159:
/*      */       case 160:
/*      */       case 161:
/*      */       case 162:
/*      */       case 163:
/*      */       case 164:
/*      */       case 165:
/*      */       case 166:
/*      */       case 181:
/* 1902 */         pop();
/*      */       case 87:
/*      */       case 153:
/*      */       case 154:
/*      */       case 155:
/*      */       case 156:
/*      */       case 157:
/*      */       case 158:
/*      */       case 179:
/*      */       case 194:
/*      */       case 195:
/*      */       case 198:
/*      */       case 199:
/* 1915 */         pop();
/* 1916 */         break;
/*      */       case 88:
/* 1918 */         pop2();
/* 1919 */         break;
/*      */       case 1:
/* 1921 */         push(5);
/* 1922 */         break;
/*      */       case 46:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 96:
/*      */       case 100:
/*      */       case 104:
/*      */       case 108:
/*      */       case 112:
/*      */       case 120:
/*      */       case 122:
/*      */       case 124:
/*      */       case 126:
/*      */       case 128:
/*      */       case 130:
/*      */       case 148:
/*      */       case 149:
/*      */       case 150:
/*      */       case 151:
/*      */       case 152:
/* 1943 */         pop();
/*      */       case 116:
/*      */       case 136:
/*      */       case 139:
/*      */       case 142:
/*      */       case 145:
/*      */       case 146:
/*      */       case 147:
/*      */       case 190:
/*      */       case 193:
/* 1953 */         pop();
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 16:
/*      */       case 17:
/*      */       case 21:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/* 1968 */         push(1);
/* 1969 */         break;
/*      */       case 47:
/*      */       case 97:
/*      */       case 101:
/*      */       case 105:
/*      */       case 109:
/*      */       case 113:
/*      */       case 121:
/*      */       case 123:
/*      */       case 125:
/*      */       case 127:
/*      */       case 129:
/*      */       case 131:
/* 1982 */         pop();
/*      */       case 117:
/*      */       case 133:
/*      */       case 140:
/*      */       case 143:
/* 1987 */         pop();
/*      */       case 9:
/*      */       case 10:
/*      */       case 22:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/* 1995 */         push(4);
/* 1996 */         break;
/*      */       case 48:
/*      */       case 98:
/*      */       case 102:
/*      */       case 106:
/*      */       case 110:
/*      */       case 114:
/* 2003 */         pop();
/*      */       case 118:
/*      */       case 134:
/*      */       case 137:
/*      */       case 144:
/* 2008 */         pop();
/*      */       case 11:
/*      */       case 12:
/*      */       case 13:
/*      */       case 23:
/*      */       case 34:
/*      */       case 35:
/*      */       case 36:
/*      */       case 37:
/* 2017 */         push(2);
/* 2018 */         break;
/*      */       case 49:
/*      */       case 99:
/*      */       case 103:
/*      */       case 107:
/*      */       case 111:
/*      */       case 115:
/* 2025 */         pop();
/*      */       case 119:
/*      */       case 135:
/*      */       case 138:
/*      */       case 141:
/* 2030 */         pop();
/*      */       case 14:
/*      */       case 15:
/*      */       case 24:
/*      */       case 38:
/*      */       case 39:
/*      */       case 40:
/*      */       case 41:
/* 2038 */         push(3);
/* 2039 */         break;
/*      */       case 54:
/* 2041 */         executeStore(getOperand(paramInt + 1), 1);
/* 2042 */         break;
/*      */       case 59:
/*      */       case 60:
/*      */       case 61:
/*      */       case 62:
/* 2047 */         executeStore(i - 59, 1);
/* 2048 */         break;
/*      */       case 55:
/* 2050 */         executeStore(getOperand(paramInt + 1), 4);
/* 2051 */         break;
/*      */       case 63:
/*      */       case 64:
/*      */       case 65:
/*      */       case 66:
/* 2056 */         executeStore(i - 63, 4);
/* 2057 */         break;
/*      */       case 56:
/* 2059 */         executeStore(getOperand(paramInt + 1), 2);
/* 2060 */         break;
/*      */       case 67:
/*      */       case 68:
/*      */       case 69:
/*      */       case 70:
/* 2065 */         executeStore(getOperand(paramInt + 1), 2);
/* 2066 */         break;
/*      */       case 57:
/* 2068 */         executeStore(getOperand(paramInt + 1), 3);
/* 2069 */         break;
/*      */       case 71:
/*      */       case 72:
/*      */       case 73:
/*      */       case 74:
/* 2074 */         executeStore(i - 71, 3);
/* 2075 */         break;
/*      */       case 25:
/* 2077 */         executeALoad(getOperand(paramInt + 1));
/* 2078 */         break;
/*      */       case 42:
/*      */       case 43:
/*      */       case 44:
/*      */       case 45:
/* 2083 */         executeALoad(i - 42);
/* 2084 */         break;
/*      */       case 58:
/* 2086 */         executeAStore(getOperand(paramInt + 1));
/* 2087 */         break;
/*      */       case 75:
/*      */       case 76:
/*      */       case 77:
/*      */       case 78:
/* 2092 */         executeAStore(i - 75);
/* 2093 */         break;
/*      */       case 172:
/*      */       case 173:
/*      */       case 174:
/*      */       case 175:
/*      */       case 176:
/*      */       case 177:
/* 2100 */         clearStack();
/* 2101 */         break;
/*      */       case 191:
/* 2103 */         j = pop();
/* 2104 */         clearStack();
/* 2105 */         push(j);
/* 2106 */         break;
/*      */       case 95:
/* 2108 */         j = pop();
/* 2109 */         k = pop();
/* 2110 */         push(j);
/* 2111 */         push(k);
/* 2112 */         break;
/*      */       case 18:
/*      */       case 19:
/*      */       case 20:
/* 2116 */         if (i == 18)
/* 2117 */           m = getOperand(paramInt + 1);
/*      */         else {
/* 2119 */           m = getOperand(paramInt + 1, 2);
/*      */         }
/* 2121 */         int i1 = ClassFileWriter.this.itsConstantPool.getConstantType(m);
/* 2122 */         switch (i1) {
/*      */         case 6:
/* 2124 */           push(3);
/* 2125 */           break;
/*      */         case 4:
/* 2127 */           push(2);
/* 2128 */           break;
/*      */         case 5:
/* 2130 */           push(4);
/* 2131 */           break;
/*      */         case 3:
/* 2133 */           push(1);
/* 2134 */           break;
/*      */         case 8:
/* 2136 */           push(TypeInfo.OBJECT("java/lang/String", ClassFileWriter.this.itsConstantPool));
/*      */ 
/* 2138 */           break;
/*      */         case 7:
/*      */         default:
/* 2140 */           throw new IllegalArgumentException("bad const type " + i1);
/*      */         }
/*      */ 
/*      */         break;
/*      */       case 187:
/* 2145 */         push(TypeInfo.UNINITIALIZED_VARIABLE(paramInt));
/* 2146 */         break;
/*      */       case 188:
/* 2148 */         pop();
/* 2149 */         char c = ClassFileWriter.arrayTypeToName(ClassFileWriter.this.itsCodeBuffer[(paramInt + 1)]);
/*      */ 
/* 2151 */         m = ClassFileWriter.this.itsConstantPool.addClass("[" + c);
/* 2152 */         push(TypeInfo.OBJECT((short)m));
/* 2153 */         break;
/*      */       case 189:
/* 2155 */         m = getOperand(paramInt + 1, 2);
/* 2156 */         str1 = (String)ClassFileWriter.this.itsConstantPool.getConstantData(m);
/* 2157 */         pop();
/* 2158 */         push(TypeInfo.OBJECT("[L" + str1 + ';', ClassFileWriter.this.itsConstantPool));
/*      */ 
/* 2160 */         break;
/*      */       case 182:
/*      */       case 183:
/*      */       case 184:
/*      */       case 185:
/* 2165 */         m = getOperand(paramInt + 1, 2);
/* 2166 */         FieldOrMethodRef localFieldOrMethodRef1 = (FieldOrMethodRef)ClassFileWriter.this.itsConstantPool.getConstantData(m);
/*      */ 
/* 2168 */         String str2 = localFieldOrMethodRef1.getType();
/* 2169 */         String str3 = localFieldOrMethodRef1.getName();
/* 2170 */         int i2 = ClassFileWriter.sizeOfParameters(str2) >>> 16;
/* 2171 */         for (int i3 = 0; i3 < i2; i3++) {
/* 2172 */           pop();
/*      */         }
/* 2174 */         if (i != 184) {
/* 2175 */           i3 = pop();
/* 2176 */           int i4 = TypeInfo.getTag(i3);
/* 2177 */           if ((i4 == TypeInfo.UNINITIALIZED_VARIABLE(0)) || (i4 == 6))
/*      */           {
/* 2179 */             if ("<init>".equals(str3)) {
/* 2180 */               int i5 = TypeInfo.OBJECT(ClassFileWriter.this.itsThisClassIndex);
/*      */ 
/* 2182 */               initializeTypeInfo(i3, i5);
/*      */             } else {
/* 2184 */               throw new IllegalStateException("bad instance");
/*      */             }
/*      */           }
/*      */         }
/* 2188 */         i3 = str2.indexOf(')');
/* 2189 */         String str4 = str2.substring(i3 + 1);
/* 2190 */         str4 = ClassFileWriter.descriptorToInternalName(str4);
/* 2191 */         if (!str4.equals("V"))
/* 2192 */           push(TypeInfo.fromType(str4, ClassFileWriter.this.itsConstantPool)); break;
/*      */       case 180:
/* 2196 */         pop();
/*      */       case 178:
/* 2198 */         m = getOperand(paramInt + 1, 2);
/* 2199 */         FieldOrMethodRef localFieldOrMethodRef2 = (FieldOrMethodRef)ClassFileWriter.this.itsConstantPool.getConstantData(m);
/*      */ 
/* 2201 */         String str5 = ClassFileWriter.descriptorToInternalName(localFieldOrMethodRef2.getType());
/* 2202 */         push(TypeInfo.fromType(str5, ClassFileWriter.this.itsConstantPool));
/* 2203 */         break;
/*      */       case 89:
/* 2205 */         j = pop();
/* 2206 */         push(j);
/* 2207 */         push(j);
/* 2208 */         break;
/*      */       case 90:
/* 2210 */         j = pop();
/* 2211 */         k = pop();
/* 2212 */         push(j);
/* 2213 */         push(k);
/* 2214 */         push(j);
/* 2215 */         break;
/*      */       case 91:
/* 2217 */         j = pop();
/* 2218 */         l1 = pop2();
/* 2219 */         push(j);
/* 2220 */         push2(l1);
/* 2221 */         push(j);
/* 2222 */         break;
/*      */       case 92:
/* 2224 */         l1 = pop2();
/* 2225 */         push2(l1);
/* 2226 */         push2(l1);
/* 2227 */         break;
/*      */       case 93:
/* 2229 */         l1 = pop2();
/* 2230 */         j = pop();
/* 2231 */         push2(l1);
/* 2232 */         push(j);
/* 2233 */         push2(l1);
/* 2234 */         break;
/*      */       case 94:
/* 2236 */         l1 = pop2();
/* 2237 */         long l2 = pop2();
/* 2238 */         push2(l1);
/* 2239 */         push2(l2);
/* 2240 */         push2(l1);
/* 2241 */         break;
/*      */       case 170:
/* 2243 */         int i6 = paramInt + 1 + (0x3 & (paramInt ^ 0xFFFFFFFF));
/* 2244 */         int i7 = getOperand(i6 + 4, 4);
/* 2245 */         int i8 = getOperand(i6 + 8, 4);
/* 2246 */         n = 4 * (i8 - i7 + 4) + i6 - paramInt;
/* 2247 */         pop();
/* 2248 */         break;
/*      */       case 50:
/* 2250 */         pop();
/* 2251 */         int i9 = pop() >>> 8;
/* 2252 */         str1 = (String)ClassFileWriter.this.itsConstantPool.getConstantData(i9);
/*      */ 
/* 2254 */         String str6 = str1;
/* 2255 */         if (str6.charAt(0) != '[') {
/* 2256 */           throw new IllegalStateException("bad array type");
/*      */         }
/* 2258 */         String str7 = str6.substring(1);
/* 2259 */         String str8 = ClassFileWriter.descriptorToInternalName(str7);
/* 2260 */         i9 = ClassFileWriter.this.itsConstantPool.addClass(str8);
/* 2261 */         push(TypeInfo.OBJECT(i9));
/* 2262 */         break;
/*      */       case 168:
/*      */       case 169:
/*      */       case 171:
/*      */       case 186:
/*      */       case 196:
/*      */       case 197:
/*      */       case 201:
/*      */       default:
/* 2271 */         throw new IllegalArgumentException("bad opcode");
/*      */       }
/*      */ 
/* 2274 */       if (n == 0) {
/* 2275 */         n = ClassFileWriter.opcodeLength(i);
/*      */       }
/* 2277 */       return n;
/*      */     }
/*      */ 
/*      */     private void executeALoad(int paramInt) {
/* 2281 */       int i = getLocal(paramInt);
/* 2282 */       int j = TypeInfo.getTag(i);
/* 2283 */       if ((j == 7) || (j == 6) || (j == 8) || (j == 5))
/*      */       {
/* 2287 */         push(i);
/*      */       }
/* 2289 */       else throw new IllegalStateException("bad local variable type: " + i + " at index: " + paramInt);
/*      */     }
/*      */ 
/*      */     private void executeAStore(int paramInt)
/*      */     {
/* 2296 */       setLocal(paramInt, pop());
/*      */     }
/*      */ 
/*      */     private void executeStore(int paramInt1, int paramInt2) {
/* 2300 */       pop();
/* 2301 */       setLocal(paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     private void initializeTypeInfo(int paramInt1, int paramInt2)
/*      */     {
/* 2310 */       initializeTypeInfo(paramInt1, paramInt2, this.locals, this.localsTop);
/* 2311 */       initializeTypeInfo(paramInt1, paramInt2, this.stack, this.stackTop);
/*      */     }
/*      */ 
/*      */     private void initializeTypeInfo(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3)
/*      */     {
/* 2316 */       for (int i = 0; i < paramInt3; i++)
/* 2317 */         if (paramArrayOfInt[i] == paramInt1)
/* 2318 */           paramArrayOfInt[i] = paramInt2;
/*      */     }
/*      */ 
/*      */     private int getLocal(int paramInt)
/*      */     {
/* 2324 */       if (paramInt < this.localsTop) {
/* 2325 */         return this.locals[paramInt];
/*      */       }
/* 2327 */       return 0;
/*      */     }
/*      */ 
/*      */     private void setLocal(int paramInt1, int paramInt2)
/*      */     {
/* 2332 */       if (paramInt1 >= this.localsTop) {
/* 2333 */         int[] arrayOfInt = new int[paramInt1 + 1];
/* 2334 */         System.arraycopy(this.locals, 0, arrayOfInt, 0, this.localsTop);
/* 2335 */         this.locals = arrayOfInt;
/* 2336 */         this.localsTop = (paramInt1 + 1);
/*      */       }
/* 2338 */       this.locals[paramInt1] = paramInt2;
/*      */     }
/*      */ 
/*      */     private void push(int paramInt) {
/* 2342 */       if (this.stackTop == this.stack.length) {
/* 2343 */         int[] arrayOfInt = new int[Math.max(this.stackTop * 2, 4)];
/* 2344 */         System.arraycopy(this.stack, 0, arrayOfInt, 0, this.stackTop);
/* 2345 */         this.stack = arrayOfInt;
/*      */       }
/* 2347 */       this.stack[(this.stackTop++)] = paramInt;
/*      */     }
/*      */ 
/*      */     private int pop() {
/* 2351 */       return this.stack[(--this.stackTop)];
/*      */     }
/*      */ 
/*      */     private void push2(long paramLong)
/*      */     {
/* 2361 */       push((int)(paramLong & 0xFFFFFF));
/* 2362 */       paramLong >>>= 32;
/* 2363 */       if (paramLong != 0L)
/* 2364 */         push((int)(paramLong & 0xFFFFFF));
/*      */     }
/*      */ 
/*      */     private long pop2()
/*      */     {
/* 2377 */       long l = pop();
/* 2378 */       if (TypeInfo.isTwoWords((int)l)) {
/* 2379 */         return l;
/*      */       }
/* 2381 */       return l << 32 | pop() & 0xFFFFFF;
/*      */     }
/*      */ 
/*      */     private void clearStack()
/*      */     {
/* 2386 */       this.stackTop = 0;
/*      */     }
/*      */ 
/*      */     int computeWriteSize()
/*      */     {
/* 2400 */       int i = getWorstCaseWriteSize();
/* 2401 */       this.rawStackMap = new byte[i];
/* 2402 */       computeRawStackMap();
/* 2403 */       return this.rawStackMapTop + 2;
/*      */     }
/*      */ 
/*      */     int write(byte[] paramArrayOfByte, int paramInt) {
/* 2407 */       paramInt = ClassFileWriter.putInt32(this.rawStackMapTop + 2, paramArrayOfByte, paramInt);
/* 2408 */       paramInt = ClassFileWriter.putInt16(this.superBlocks.length - 1, paramArrayOfByte, paramInt);
/* 2409 */       System.arraycopy(this.rawStackMap, 0, paramArrayOfByte, paramInt, this.rawStackMapTop);
/* 2410 */       return paramInt + this.rawStackMapTop;
/*      */     }
/*      */ 
/*      */     private void computeRawStackMap()
/*      */     {
/* 2417 */       Object localObject1 = this.superBlocks[0];
/* 2418 */       Object localObject2 = ((SuperBlock)localObject1).getTrimmedLocals();
/* 2419 */       int i = -1;
/* 2420 */       for (int j = 1; j < this.superBlocks.length; j++) {
/* 2421 */         SuperBlock localSuperBlock = this.superBlocks[j];
/* 2422 */         int[] arrayOfInt1 = localSuperBlock.getTrimmedLocals();
/* 2423 */         int[] arrayOfInt2 = localSuperBlock.getStack();
/* 2424 */         int k = localSuperBlock.getStart() - i - 1;
/*      */ 
/* 2426 */         if (arrayOfInt2.length == 0) {
/* 2427 */           int m = localObject2.length > arrayOfInt1.length ? arrayOfInt1.length : localObject2.length;
/*      */ 
/* 2429 */           int n = Math.abs(localObject2.length - arrayOfInt1.length);
/*      */ 
/* 2434 */           for (int i1 = 0; (i1 < m) && 
/* 2435 */             (localObject2[i1] == arrayOfInt1[i1]); i1++);
/* 2439 */           if ((i1 == arrayOfInt1.length) && (n == 0))
/*      */           {
/* 2442 */             writeSameFrame(arrayOfInt1, k);
/* 2443 */           } else if ((i1 == arrayOfInt1.length) && (n <= 3))
/*      */           {
/* 2446 */             writeChopFrame(n, k);
/* 2447 */           } else if ((i1 == localObject2.length) && (n <= 3))
/*      */           {
/* 2450 */             writeAppendFrame(arrayOfInt1, n, k);
/*      */           }
/*      */           else
/*      */           {
/* 2454 */             writeFullFrame(arrayOfInt1, arrayOfInt2, k);
/*      */           }
/*      */         }
/* 2457 */         else if (arrayOfInt2.length == 1) {
/* 2458 */           if (Arrays.equals((int[])localObject2, arrayOfInt1)) {
/* 2459 */             writeSameLocalsOneStackItemFrame(arrayOfInt1, arrayOfInt2, k);
/*      */           }
/*      */           else
/*      */           {
/* 2465 */             writeFullFrame(arrayOfInt1, arrayOfInt2, k);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 2472 */           writeFullFrame(arrayOfInt1, arrayOfInt2, k);
/*      */         }
/*      */ 
/* 2475 */         localObject1 = localSuperBlock;
/* 2476 */         localObject2 = arrayOfInt1;
/* 2477 */         i = localSuperBlock.getStart();
/*      */       }
/*      */     }
/*      */ 
/*      */     private int getWorstCaseWriteSize()
/*      */     {
/* 2489 */       return (this.superBlocks.length - 1) * (7 + ClassFileWriter.this.itsMaxLocals * 3 + ClassFileWriter.this.itsMaxStack * 3);
/*      */     }
/*      */ 
/*      */     private void writeSameFrame(int[] paramArrayOfInt, int paramInt)
/*      */     {
/* 2494 */       if (paramInt <= 63)
/*      */       {
/* 2498 */         this.rawStackMap[(this.rawStackMapTop++)] = ((byte)paramInt);
/*      */       }
/*      */       else
/*      */       {
/* 2502 */         this.rawStackMap[(this.rawStackMapTop++)] = -5;
/* 2503 */         this.rawStackMapTop = ClassFileWriter.putInt16(paramInt, this.rawStackMap, this.rawStackMapTop);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void writeSameLocalsOneStackItemFrame(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*      */     {
/* 2511 */       if (paramInt <= 63)
/*      */       {
/* 2515 */         this.rawStackMap[(this.rawStackMapTop++)] = ((byte)(64 + paramInt));
/*      */       }
/*      */       else
/*      */       {
/* 2520 */         this.rawStackMap[(this.rawStackMapTop++)] = -9;
/* 2521 */         this.rawStackMapTop = ClassFileWriter.putInt16(paramInt, this.rawStackMap, this.rawStackMapTop);
/*      */       }
/*      */ 
/* 2524 */       writeType(paramArrayOfInt2[0]);
/*      */     }
/*      */ 
/*      */     private void writeFullFrame(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*      */     {
/* 2529 */       this.rawStackMap[(this.rawStackMapTop++)] = -1;
/* 2530 */       this.rawStackMapTop = ClassFileWriter.putInt16(paramInt, this.rawStackMap, this.rawStackMapTop);
/* 2531 */       this.rawStackMapTop = ClassFileWriter.putInt16(paramArrayOfInt1.length, this.rawStackMap, this.rawStackMapTop);
/*      */ 
/* 2533 */       this.rawStackMapTop = writeTypes(paramArrayOfInt1);
/* 2534 */       this.rawStackMapTop = ClassFileWriter.putInt16(paramArrayOfInt2.length, this.rawStackMap, this.rawStackMapTop);
/*      */ 
/* 2536 */       this.rawStackMapTop = writeTypes(paramArrayOfInt2);
/*      */     }
/*      */ 
/*      */     private void writeAppendFrame(int[] paramArrayOfInt, int paramInt1, int paramInt2)
/*      */     {
/* 2541 */       int i = paramArrayOfInt.length - paramInt1;
/* 2542 */       this.rawStackMap[(this.rawStackMapTop++)] = ((byte)(251 + paramInt1));
/* 2543 */       this.rawStackMapTop = ClassFileWriter.putInt16(paramInt2, this.rawStackMap, this.rawStackMapTop);
/* 2544 */       this.rawStackMapTop = writeTypes(paramArrayOfInt, i);
/*      */     }
/*      */ 
/*      */     private void writeChopFrame(int paramInt1, int paramInt2) {
/* 2548 */       this.rawStackMap[(this.rawStackMapTop++)] = ((byte)(251 - paramInt1));
/* 2549 */       this.rawStackMapTop = ClassFileWriter.putInt16(paramInt2, this.rawStackMap, this.rawStackMapTop);
/*      */     }
/*      */ 
/*      */     private int writeTypes(int[] paramArrayOfInt) {
/* 2553 */       return writeTypes(paramArrayOfInt, 0);
/*      */     }
/*      */ 
/*      */     private int writeTypes(int[] paramArrayOfInt, int paramInt) {
/* 2557 */       int i = this.rawStackMapTop;
/* 2558 */       for (int j = paramInt; j < paramArrayOfInt.length; j++) {
/* 2559 */         this.rawStackMapTop = writeType(paramArrayOfInt[j]);
/*      */       }
/* 2561 */       return this.rawStackMapTop;
/*      */     }
/*      */ 
/*      */     private int writeType(int paramInt) {
/* 2565 */       int i = paramInt & 0xFF;
/* 2566 */       this.rawStackMap[(this.rawStackMapTop++)] = ((byte)i);
/* 2567 */       if ((i == 7) || (i == 8))
/*      */       {
/* 2569 */         this.rawStackMapTop = ClassFileWriter.putInt16(paramInt >>> 8, this.rawStackMap, this.rawStackMapTop);
/*      */       }
/*      */ 
/* 2572 */       return this.rawStackMapTop;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.classfile.internal.ClassFileWriter
 * JD-Core Version:    0.6.2
 */