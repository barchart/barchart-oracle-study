/*     */ package com.sun.java.util.jar.pack;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ 
/*     */ class Code extends Attribute.Holder
/*     */ {
/*     */   Package.Class.Method m;
/*  59 */   private static final ConstantPool.Entry[] noRefs = ConstantPool.noRefs;
/*     */   int max_stack;
/*     */   int max_locals;
/*  65 */   ConstantPool.Entry[] handler_class = noRefs;
/*  66 */   int[] handler_start = Constants.noInts;
/*  67 */   int[] handler_end = Constants.noInts;
/*  68 */   int[] handler_catch = Constants.noInts;
/*     */   byte[] bytes;
/*     */   Fixups fixups;
/*     */   Object insnMap;
/*     */   static final boolean shrinkMaps = true;
/*     */ 
/*     */   public Code(Package.Class.Method paramMethod)
/*     */   {
/*  42 */     this.m = paramMethod;
/*     */   }
/*     */ 
/*     */   public Package.Class.Method getMethod() {
/*  46 */     return this.m;
/*     */   }
/*     */   public Package.Class thisClass() {
/*  49 */     return this.m.thisClass();
/*     */   }
/*     */   public Package getPackage() {
/*  52 */     return this.m.thisClass().getPackage();
/*     */   }
/*     */ 
/*     */   public ConstantPool.Entry[] getCPMap() {
/*  56 */     return this.m.getCPMap();
/*     */   }
/*     */ 
/*     */   int getLength()
/*     */   {
/*  74 */     return this.bytes.length;
/*     */   }
/*     */   int getMaxStack() {
/*  77 */     return this.max_stack;
/*     */   }
/*     */   void setMaxStack(int paramInt) {
/*  80 */     this.max_stack = paramInt;
/*     */   }
/*     */ 
/*     */   int getMaxNALocals() {
/*  84 */     int i = this.m.getArgumentSize();
/*  85 */     return this.max_locals - i;
/*     */   }
/*     */   void setMaxNALocals(int paramInt) {
/*  88 */     int i = this.m.getArgumentSize();
/*  89 */     this.max_locals = (i + paramInt);
/*     */   }
/*     */ 
/*     */   int getHandlerCount() {
/*  93 */     assert (this.handler_class.length == this.handler_start.length);
/*  94 */     assert (this.handler_class.length == this.handler_end.length);
/*  95 */     assert (this.handler_class.length == this.handler_catch.length);
/*  96 */     return this.handler_class.length;
/*     */   }
/*     */   void setHandlerCount(int paramInt) {
/*  99 */     if (paramInt > 0) {
/* 100 */       this.handler_class = new ConstantPool.Entry[paramInt];
/* 101 */       this.handler_start = new int[paramInt];
/* 102 */       this.handler_end = new int[paramInt];
/* 103 */       this.handler_catch = new int[paramInt];
/*     */     }
/*     */   }
/*     */ 
/*     */   void setBytes(byte[] paramArrayOfByte)
/*     */   {
/* 109 */     this.bytes = paramArrayOfByte;
/* 110 */     if (this.fixups != null)
/* 111 */       this.fixups.setBytes(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   void setInstructionMap(int[] paramArrayOfInt, int paramInt)
/*     */   {
/* 117 */     this.insnMap = allocateInstructionMap(paramArrayOfInt, paramInt);
/*     */   }
/*     */ 
/*     */   void setInstructionMap(int[] paramArrayOfInt) {
/* 121 */     setInstructionMap(paramArrayOfInt, paramArrayOfInt.length);
/*     */   }
/*     */ 
/*     */   int[] getInstructionMap() {
/* 125 */     return expandInstructionMap(getInsnMap());
/*     */   }
/*     */ 
/*     */   void addFixups(Collection paramCollection) {
/* 129 */     if (this.fixups == null) {
/* 130 */       this.fixups = new Fixups(this.bytes);
/*     */     }
/* 132 */     assert (this.fixups.getBytes() == this.bytes);
/* 133 */     this.fixups.addAll(paramCollection);
/*     */   }
/*     */ 
/*     */   public void trimToSize() {
/* 137 */     if (this.fixups != null) {
/* 138 */       this.fixups.trimToSize();
/* 139 */       if (this.fixups.size() == 0)
/* 140 */         this.fixups = null;
/*     */     }
/* 142 */     super.trimToSize();
/*     */   }
/*     */ 
/*     */   protected void visitRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection) {
/* 146 */     int i = getPackage().verbose;
/* 147 */     if (i > 2)
/* 148 */       System.out.println("Reference scan " + this);
/* 149 */     Package.Class localClass = thisClass();
/* 150 */     paramCollection.addAll(Arrays.asList(this.handler_class));
/* 151 */     if (this.fixups != null) {
/* 152 */       this.fixups.visitRefs(paramCollection);
/*     */     }
/*     */     else {
/* 155 */       ConstantPool.Entry[] arrayOfEntry = getCPMap();
/* 156 */       for (Instruction localInstruction = instructionAt(0); localInstruction != null; localInstruction = localInstruction.next()) {
/* 157 */         if (i > 4)
/* 158 */           System.out.println(localInstruction);
/* 159 */         int j = localInstruction.getCPIndex();
/* 160 */         if (j >= 0) {
/* 161 */           paramCollection.add(arrayOfEntry[j]);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 166 */     super.visitRefs(paramInt, paramCollection);
/*     */   }
/*     */ 
/*     */   private Object allocateInstructionMap(int[] paramArrayOfInt, int paramInt)
/*     */   {
/* 181 */     int i = getLength();
/*     */     int j;
/* 182 */     if (i <= 255) {
/* 183 */       localObject = new byte[paramInt + 1];
/* 184 */       for (j = 0; j < paramInt; j++) {
/* 185 */         localObject[j] = ((byte)(paramArrayOfInt[j] + -128));
/*     */       }
/* 187 */       localObject[paramInt] = ((byte)(i + -128));
/* 188 */       return localObject;
/* 189 */     }if (i < 65535) {
/* 190 */       localObject = new short[paramInt + 1];
/* 191 */       for (j = 0; j < paramInt; j++) {
/* 192 */         localObject[j] = ((short)(paramArrayOfInt[j] + -32768));
/*     */       }
/* 194 */       localObject[paramInt] = ((short)(i + -32768));
/* 195 */       return localObject;
/*     */     }
/* 197 */     Object localObject = Arrays.copyOf(paramArrayOfInt, paramInt + 1);
/* 198 */     localObject[paramInt] = i;
/* 199 */     return localObject;
/*     */   }
/*     */ 
/*     */   private int[] expandInstructionMap(Object paramObject)
/*     */   {
/*     */     Object localObject;
/*     */     int[] arrayOfInt;
/*     */     int i;
/* 204 */     if ((paramObject instanceof byte[])) {
/* 205 */       localObject = (byte[])paramObject;
/* 206 */       arrayOfInt = new int[localObject.length - 1];
/* 207 */       for (i = 0; i < arrayOfInt.length; i++)
/* 208 */         localObject[i] -= -128;
/*     */     }
/* 210 */     else if ((paramObject instanceof short[])) {
/* 211 */       localObject = (short[])paramObject;
/* 212 */       arrayOfInt = new int[localObject.length - 1];
/* 213 */       for (i = 0; i < arrayOfInt.length; i++)
/* 214 */         localObject[i] -= -128;
/*     */     }
/*     */     else {
/* 217 */       localObject = (int[])paramObject;
/* 218 */       arrayOfInt = Arrays.copyOfRange((int[])localObject, 0, localObject.length - 1);
/*     */     }
/* 220 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   Object getInsnMap()
/*     */   {
/* 225 */     if (this.insnMap != null) {
/* 226 */       return this.insnMap;
/*     */     }
/* 228 */     int[] arrayOfInt = new int[getLength()];
/* 229 */     int i = 0;
/* 230 */     for (Instruction localInstruction = instructionAt(0); localInstruction != null; localInstruction = localInstruction.next()) {
/* 231 */       arrayOfInt[(i++)] = localInstruction.getPC();
/*     */     }
/*     */ 
/* 234 */     this.insnMap = allocateInstructionMap(arrayOfInt, i);
/*     */ 
/* 236 */     return this.insnMap;
/*     */   }
/*     */ 
/*     */   public int encodeBCI(int paramInt)
/*     */   {
/* 247 */     if ((paramInt <= 0) || (paramInt > getLength())) return paramInt;
/* 248 */     Object localObject1 = getInsnMap();
/*     */     Object localObject2;
/*     */     int j;
/*     */     int i;
/* 250 */     if ((localObject1 instanceof byte[])) {
/* 251 */       localObject2 = (byte[])localObject1;
/* 252 */       j = localObject2.length;
/* 253 */       i = Arrays.binarySearch((byte[])localObject2, (byte)(paramInt + -128));
/* 254 */     } else if ((localObject1 instanceof short[])) {
/* 255 */       localObject2 = (short[])localObject1;
/* 256 */       j = localObject2.length;
/* 257 */       i = Arrays.binarySearch((short[])localObject2, (short)(paramInt + -32768));
/*     */     } else {
/* 259 */       localObject2 = (int[])localObject1;
/* 260 */       j = localObject2.length;
/* 261 */       i = Arrays.binarySearch((int[])localObject2, paramInt);
/*     */     }
/* 263 */     assert (i != -1);
/* 264 */     assert (i != 0);
/* 265 */     assert (i != j);
/* 266 */     assert (i != -j - 1);
/* 267 */     return i >= 0 ? i : j + paramInt - (-i - 1);
/*     */   }
/*     */   public int decodeBCI(int paramInt) {
/* 270 */     if ((paramInt <= 0) || (paramInt > getLength())) return paramInt;
/* 271 */     Object localObject1 = getInsnMap();
/*     */     Object localObject2;
/*     */     int j;
/*     */     int i;
/*     */     int k;
/* 290 */     if ((localObject1 instanceof byte[])) {
/* 291 */       localObject2 = (byte[])localObject1;
/* 292 */       j = localObject2.length;
/* 293 */       if (paramInt < j)
/* 294 */         return localObject2[paramInt] - -128;
/* 295 */       i = Arrays.binarySearch((byte[])localObject2, (byte)(paramInt + -128));
/* 296 */       if (i < 0) i = -i - 1;
/* 297 */       k = paramInt - j + -128;
/*     */ 
/* 299 */       while (localObject2[(i - 1)] - (i - 1) > k) {
/* 298 */         i--;
/*     */       }
/*     */     }
/* 301 */     else if ((localObject1 instanceof short[])) {
/* 302 */       localObject2 = (short[])localObject1;
/* 303 */       j = localObject2.length;
/* 304 */       if (paramInt < j)
/* 305 */         return localObject2[paramInt] - -32768;
/* 306 */       i = Arrays.binarySearch((short[])localObject2, (short)(paramInt + -32768));
/* 307 */       if (i < 0) i = -i - 1;
/* 308 */       k = paramInt - j + -32768;
/*     */ 
/* 310 */       while (localObject2[(i - 1)] - (i - 1) > k)
/* 309 */         i--;
/*     */     }
/*     */     else
/*     */     {
/* 313 */       localObject2 = (int[])localObject1;
/* 314 */       j = localObject2.length;
/* 315 */       if (paramInt < j)
/* 316 */         return localObject2[paramInt];
/* 317 */       i = Arrays.binarySearch((int[])localObject2, paramInt);
/* 318 */       if (i < 0) i = -i - 1;
/* 319 */       k = paramInt - j;
/*     */ 
/* 321 */       while (localObject2[(i - 1)] - (i - 1) > k) {
/* 320 */         i--;
/*     */       }
/*     */     }
/*     */ 
/* 324 */     return paramInt - j + i;
/*     */   }
/*     */ 
/*     */   public void finishRefs(ConstantPool.Index paramIndex) {
/* 328 */     if (this.fixups != null) {
/* 329 */       this.fixups.finishRefs(paramIndex);
/* 330 */       this.fixups = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   Instruction instructionAt(int paramInt)
/*     */   {
/* 336 */     return Instruction.at(this.bytes, paramInt);
/*     */   }
/*     */ 
/*     */   static boolean flagsRequireCode(int paramInt)
/*     */   {
/* 342 */     return (paramInt & 0x500) == 0;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 346 */     return this.m + ".Code";
/*     */   }
/*     */ 
/*     */   public int getInt(int paramInt) {
/* 350 */     return Instruction.getInt(this.bytes, paramInt); } 
/* 351 */   public int getShort(int paramInt) { return Instruction.getShort(this.bytes, paramInt); } 
/* 352 */   public int getByte(int paramInt) { return Instruction.getByte(this.bytes, paramInt); } 
/* 353 */   void setInt(int paramInt1, int paramInt2) { Instruction.setInt(this.bytes, paramInt1, paramInt2); } 
/* 354 */   void setShort(int paramInt1, int paramInt2) { Instruction.setShort(this.bytes, paramInt1, paramInt2); } 
/* 355 */   void setByte(int paramInt1, int paramInt2) { Instruction.setByte(this.bytes, paramInt1, paramInt2); }
/*     */ 
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.util.jar.pack.Code
 * JD-Core Version:    0.6.2
 */