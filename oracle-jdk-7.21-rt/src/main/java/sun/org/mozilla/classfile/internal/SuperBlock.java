/*      */ package sun.org.mozilla.classfile.internal;
/*      */ 
/*      */ final class SuperBlock
/*      */ {
/*      */   private int index;
/*      */   private int start;
/*      */   private int end;
/*      */   private int[] locals;
/*      */   private int[] stack;
/*      */   private boolean isInitialized;
/*      */   private boolean isInQueue;
/*      */ 
/*      */   SuperBlock(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
/*      */   {
/* 4795 */     this.index = paramInt1;
/* 4796 */     this.start = paramInt2;
/* 4797 */     this.end = paramInt3;
/* 4798 */     this.locals = new int[paramArrayOfInt.length];
/* 4799 */     System.arraycopy(paramArrayOfInt, 0, this.locals, 0, paramArrayOfInt.length);
/* 4800 */     this.stack = new int[0];
/* 4801 */     this.isInitialized = false;
/* 4802 */     this.isInQueue = false;
/*      */   }
/*      */ 
/*      */   int getIndex() {
/* 4806 */     return this.index;
/*      */   }
/*      */ 
/*      */   int[] getLocals() {
/* 4810 */     int[] arrayOfInt = new int[this.locals.length];
/* 4811 */     System.arraycopy(this.locals, 0, arrayOfInt, 0, this.locals.length);
/* 4812 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   int[] getTrimmedLocals()
/*      */   {
/* 4825 */     int i = this.locals.length - 1;
/*      */ 
/* 4827 */     while ((i >= 0) && (this.locals[i] == 0) && (!TypeInfo.isTwoWords(this.locals[(i - 1)])))
/*      */     {
/* 4829 */       i--;
/*      */     }
/* 4831 */     i++;
/*      */ 
/* 4833 */     int j = i;
/* 4834 */     for (int k = 0; k < i; k++) {
/* 4835 */       if (TypeInfo.isTwoWords(this.locals[k])) {
/* 4836 */         j--;
/*      */       }
/*      */     }
/* 4839 */     int[] arrayOfInt = new int[j];
/* 4840 */     int m = 0; for (int n = 0; m < j; n++) {
/* 4841 */       arrayOfInt[m] = this.locals[n];
/* 4842 */       if (TypeInfo.isTwoWords(this.locals[n]))
/* 4843 */         n++;
/* 4840 */       m++;
/*      */     }
/*      */ 
/* 4846 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   int[] getStack() {
/* 4850 */     int[] arrayOfInt = new int[this.stack.length];
/* 4851 */     System.arraycopy(this.stack, 0, arrayOfInt, 0, this.stack.length);
/* 4852 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   boolean merge(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, ConstantPool paramConstantPool)
/*      */   {
/* 4857 */     if (!this.isInitialized) {
/* 4858 */       System.arraycopy(paramArrayOfInt1, 0, this.locals, 0, paramInt1);
/* 4859 */       this.stack = new int[paramInt2];
/* 4860 */       System.arraycopy(paramArrayOfInt2, 0, this.stack, 0, paramInt2);
/* 4861 */       this.isInitialized = true;
/* 4862 */       return true;
/* 4863 */     }if ((this.locals.length == paramInt1) && (this.stack.length == paramInt2))
/*      */     {
/* 4865 */       boolean bool1 = mergeState(this.locals, paramArrayOfInt1, paramInt1, paramConstantPool);
/*      */ 
/* 4867 */       boolean bool2 = mergeState(this.stack, paramArrayOfInt2, paramInt2, paramConstantPool);
/*      */ 
/* 4869 */       return (bool1) || (bool2);
/*      */     }
/*      */ 
/* 4878 */     throw new IllegalArgumentException("bad merge attempt");
/*      */   }
/*      */ 
/*      */   private boolean mergeState(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt, ConstantPool paramConstantPool)
/*      */   {
/* 4892 */     boolean bool = false;
/* 4893 */     for (int i = 0; i < paramInt; i++) {
/* 4894 */       int j = paramArrayOfInt1[i];
/*      */ 
/* 4896 */       paramArrayOfInt1[i] = TypeInfo.merge(paramArrayOfInt1[i], paramArrayOfInt2[i], paramConstantPool);
/* 4897 */       if (j != paramArrayOfInt1[i]) {
/* 4898 */         bool = true;
/*      */       }
/*      */     }
/* 4901 */     return bool;
/*      */   }
/*      */ 
/*      */   int getStart() {
/* 4905 */     return this.start;
/*      */   }
/*      */ 
/*      */   int getEnd() {
/* 4909 */     return this.end;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 4914 */     return "sb " + this.index;
/*      */   }
/*      */ 
/*      */   boolean isInitialized() {
/* 4918 */     return this.isInitialized;
/*      */   }
/*      */ 
/*      */   void setInitialized(boolean paramBoolean) {
/* 4922 */     this.isInitialized = paramBoolean;
/*      */   }
/*      */ 
/*      */   boolean isInQueue() {
/* 4926 */     return this.isInQueue;
/*      */   }
/*      */ 
/*      */   void setInQueue(boolean paramBoolean) {
/* 4930 */     this.isInQueue = paramBoolean;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.classfile.internal.SuperBlock
 * JD-Core Version:    0.6.2
 */