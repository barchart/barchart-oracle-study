/*      */ package sun.org.mozilla.classfile.internal;
/*      */ 
/*      */ import java.io.PrintStream;
/*      */ 
/*      */ final class TypeInfo
/*      */ {
/*      */   static final int TOP = 0;
/*      */   static final int INTEGER = 1;
/*      */   static final int FLOAT = 2;
/*      */   static final int DOUBLE = 3;
/*      */   static final int LONG = 4;
/*      */   static final int NULL = 5;
/*      */   static final int UNINITIALIZED_THIS = 6;
/*      */   static final int OBJECT_TAG = 7;
/*      */   static final int UNINITIALIZED_VAR_TAG = 8;
/*      */ 
/*      */   static final int OBJECT(int paramInt)
/*      */   {
/* 4963 */     return (paramInt & 0xFFFF) << 8 | 0x7;
/*      */   }
/*      */ 
/*      */   static final int OBJECT(String paramString, ConstantPool paramConstantPool) {
/* 4967 */     return OBJECT(paramConstantPool.addClass(paramString));
/*      */   }
/*      */ 
/*      */   static final int UNINITIALIZED_VARIABLE(int paramInt) {
/* 4971 */     return (paramInt & 0xFFFF) << 8 | 0x8;
/*      */   }
/*      */ 
/*      */   static final int getTag(int paramInt) {
/* 4975 */     return paramInt & 0xFF;
/*      */   }
/*      */ 
/*      */   static final int getPayload(int paramInt) {
/* 4979 */     return paramInt >>> 8;
/*      */   }
/*      */ 
/*      */   static final String getPayloadAsType(int paramInt, ConstantPool paramConstantPool)
/*      */   {
/* 4989 */     if (getTag(paramInt) == 7) {
/* 4990 */       return (String)paramConstantPool.getConstantData(getPayload(paramInt));
/*      */     }
/* 4992 */     throw new IllegalArgumentException("expecting object type");
/*      */   }
/*      */ 
/*      */   static final int fromType(String paramString, ConstantPool paramConstantPool)
/*      */   {
/* 4999 */     if (paramString.length() == 1) {
/* 5000 */       switch (paramString.charAt(0)) {
/*      */       case 'B':
/*      */       case 'C':
/*      */       case 'I':
/*      */       case 'S':
/*      */       case 'Z':
/* 5006 */         return 1;
/*      */       case 'D':
/* 5008 */         return 3;
/*      */       case 'F':
/* 5010 */         return 2;
/*      */       case 'J':
/* 5012 */         return 4;
/*      */       case 'E':
/*      */       case 'G':
/*      */       case 'H':
/*      */       case 'K':
/*      */       case 'L':
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
/* 5014 */       case 'Y': } throw new IllegalArgumentException("bad type");
/*      */     }
/*      */ 
/* 5017 */     return OBJECT(paramString, paramConstantPool);
/*      */   }
/*      */ 
/*      */   static boolean isTwoWords(int paramInt) {
/* 5021 */     return (paramInt == 3) || (paramInt == 4);
/*      */   }
/*      */ 
/*      */   static int merge(int paramInt1, int paramInt2, ConstantPool paramConstantPool)
/*      */   {
/* 5043 */     int i = getTag(paramInt1);
/* 5044 */     int j = getTag(paramInt2);
/* 5045 */     int k = i == 7 ? 1 : 0;
/* 5046 */     int m = j == 7 ? 1 : 0;
/*      */ 
/* 5048 */     if ((paramInt1 == paramInt2) || ((k != 0) && (paramInt2 == 5)))
/* 5049 */       return paramInt1;
/* 5050 */     if ((i == 0) || (j == 0))
/*      */     {
/* 5052 */       return 0;
/* 5053 */     }if ((paramInt1 == 5) && (m != 0))
/* 5054 */       return paramInt2;
/* 5055 */     if ((k != 0) && (m != 0)) {
/* 5056 */       Object localObject1 = getPayloadAsType(paramInt1, paramConstantPool);
/* 5057 */       Object localObject2 = getPayloadAsType(paramInt2, paramConstantPool);
/*      */ 
/* 5061 */       String str1 = (String)paramConstantPool.getConstantData(2);
/* 5062 */       String str2 = (String)paramConstantPool.getConstantData(4);
/*      */ 
/* 5069 */       if (((String)localObject1).equals(str1)) {
/* 5070 */         localObject1 = str2;
/*      */       }
/* 5072 */       if (((String)localObject2).equals(str1)) {
/* 5073 */         localObject2 = str2;
/*      */       }
/*      */ 
/* 5076 */       Class localClass1 = getClassFromInternalName((String)localObject1);
/* 5077 */       Class localClass2 = getClassFromInternalName((String)localObject2);
/*      */ 
/* 5079 */       if (localClass1.isAssignableFrom(localClass2))
/* 5080 */         return paramInt1;
/* 5081 */       if (localClass2.isAssignableFrom(localClass1))
/* 5082 */         return paramInt2;
/* 5083 */       if ((localClass2.isInterface()) || (localClass1.isInterface()))
/*      */       {
/* 5089 */         return OBJECT("java/lang/Object", paramConstantPool);
/*      */       }
/* 5091 */       Class localClass3 = localClass2.getSuperclass();
/* 5092 */       while (localClass3 != null) {
/* 5093 */         if (localClass3.isAssignableFrom(localClass1)) {
/* 5094 */           String str3 = localClass3.getName();
/* 5095 */           str3 = ClassFileWriter.getSlashedForm(str3);
/* 5096 */           return OBJECT(str3, paramConstantPool);
/*      */         }
/* 5098 */         localClass3 = localClass3.getSuperclass();
/*      */       }
/*      */     }
/*      */ 
/* 5102 */     throw new IllegalArgumentException("bad merge attempt between " + toString(paramInt1, paramConstantPool) + " and " + toString(paramInt2, paramConstantPool));
/*      */   }
/*      */ 
/*      */   static String toString(int paramInt, ConstantPool paramConstantPool)
/*      */   {
/* 5108 */     int i = getTag(paramInt);
/* 5109 */     switch (i) {
/*      */     case 0:
/* 5111 */       return "top";
/*      */     case 1:
/* 5113 */       return "int";
/*      */     case 2:
/* 5115 */       return "float";
/*      */     case 3:
/* 5117 */       return "double";
/*      */     case 4:
/* 5119 */       return "long";
/*      */     case 5:
/* 5121 */       return "null";
/*      */     case 6:
/* 5123 */       return "uninitialized_this";
/*      */     }
/* 5125 */     if (i == 7)
/* 5126 */       return getPayloadAsType(paramInt, paramConstantPool);
/* 5127 */     if (i == 8) {
/* 5128 */       return "uninitialized";
/*      */     }
/* 5130 */     throw new IllegalArgumentException("bad type");
/*      */   }
/*      */ 
/*      */   static Class getClassFromInternalName(String paramString)
/*      */   {
/*      */     try
/*      */     {
/* 5144 */       return Class.forName(paramString.replace('/', '.'));
/*      */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 5146 */       throw new RuntimeException(localClassNotFoundException);
/*      */     }
/*      */   }
/*      */ 
/*      */   static String toString(int[] paramArrayOfInt, ConstantPool paramConstantPool) {
/* 5151 */     return toString(paramArrayOfInt, paramArrayOfInt.length, paramConstantPool);
/*      */   }
/*      */ 
/*      */   static String toString(int[] paramArrayOfInt, int paramInt, ConstantPool paramConstantPool) {
/* 5155 */     StringBuilder localStringBuilder = new StringBuilder();
/* 5156 */     localStringBuilder.append("[");
/* 5157 */     for (int i = 0; i < paramInt; i++) {
/* 5158 */       if (i > 0) {
/* 5159 */         localStringBuilder.append(", ");
/*      */       }
/* 5161 */       localStringBuilder.append(toString(paramArrayOfInt[i], paramConstantPool));
/*      */     }
/* 5163 */     localStringBuilder.append("]");
/* 5164 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   static void print(int[] paramArrayOfInt1, int[] paramArrayOfInt2, ConstantPool paramConstantPool) {
/* 5168 */     print(paramArrayOfInt1, paramArrayOfInt1.length, paramArrayOfInt2, paramArrayOfInt2.length, paramConstantPool);
/*      */   }
/*      */ 
/*      */   static void print(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, ConstantPool paramConstantPool)
/*      */   {
/* 5173 */     System.out.print("locals: ");
/* 5174 */     System.out.println(toString(paramArrayOfInt1, paramInt1, paramConstantPool));
/* 5175 */     System.out.print("stack: ");
/* 5176 */     System.out.println(toString(paramArrayOfInt2, paramInt2, paramConstantPool));
/* 5177 */     System.out.println();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.classfile.internal.TypeInfo
 * JD-Core Version:    0.6.2
 */