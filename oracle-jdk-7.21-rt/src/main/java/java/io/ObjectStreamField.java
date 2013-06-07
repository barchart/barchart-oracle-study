/*     */ package java.io;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ 
/*     */ public class ObjectStreamField
/*     */   implements Comparable<Object>
/*     */ {
/*     */   private final String name;
/*     */   private final String signature;
/*     */   private final Class<?> type;
/*     */   private final boolean unshared;
/*     */   private final Field field;
/*  54 */   private int offset = 0;
/*     */ 
/*     */   public ObjectStreamField(String paramString, Class<?> paramClass)
/*     */   {
/*  64 */     this(paramString, paramClass, false);
/*     */   }
/*     */ 
/*     */   public ObjectStreamField(String paramString, Class<?> paramClass, boolean paramBoolean)
/*     */   {
/*  85 */     if (paramString == null) {
/*  86 */       throw new NullPointerException();
/*     */     }
/*  88 */     this.name = paramString;
/*  89 */     this.type = paramClass;
/*  90 */     this.unshared = paramBoolean;
/*  91 */     this.signature = getClassSignature(paramClass).intern();
/*  92 */     this.field = null;
/*     */   }
/*     */ 
/*     */   ObjectStreamField(String paramString1, String paramString2, boolean paramBoolean)
/*     */   {
/* 100 */     if (paramString1 == null) {
/* 101 */       throw new NullPointerException();
/*     */     }
/* 103 */     this.name = paramString1;
/* 104 */     this.signature = paramString2.intern();
/* 105 */     this.unshared = paramBoolean;
/* 106 */     this.field = null;
/*     */ 
/* 108 */     switch (paramString2.charAt(0)) { case 'Z':
/* 109 */       this.type = Boolean.TYPE; break;
/*     */     case 'B':
/* 110 */       this.type = Byte.TYPE; break;
/*     */     case 'C':
/* 111 */       this.type = Character.TYPE; break;
/*     */     case 'S':
/* 112 */       this.type = Short.TYPE; break;
/*     */     case 'I':
/* 113 */       this.type = Integer.TYPE; break;
/*     */     case 'J':
/* 114 */       this.type = Long.TYPE; break;
/*     */     case 'F':
/* 115 */       this.type = Float.TYPE; break;
/*     */     case 'D':
/* 116 */       this.type = Double.TYPE; break;
/*     */     case 'L':
/*     */     case '[':
/* 118 */       this.type = Object.class; break;
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'K':
/*     */     case 'M':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'V':
/*     */     case 'W':
/*     */     case 'X':
/*     */     case 'Y':
/*     */     default:
/* 119 */       throw new IllegalArgumentException("illegal signature");
/*     */     }
/*     */   }
/*     */ 
/*     */   ObjectStreamField(Field paramField, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 132 */     this.field = paramField;
/* 133 */     this.unshared = paramBoolean1;
/* 134 */     this.name = paramField.getName();
/* 135 */     Class localClass = paramField.getType();
/* 136 */     this.type = ((paramBoolean2) || (localClass.isPrimitive()) ? localClass : Object.class);
/* 137 */     this.signature = getClassSignature(localClass).intern();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 147 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Class<?> getType()
/*     */   {
/* 161 */     return this.type;
/*     */   }
/*     */ 
/*     */   public char getTypeCode()
/*     */   {
/* 183 */     return this.signature.charAt(0);
/*     */   }
/*     */ 
/*     */   public String getTypeString()
/*     */   {
/* 193 */     return isPrimitive() ? null : this.signature;
/*     */   }
/*     */ 
/*     */   public int getOffset()
/*     */   {
/* 204 */     return this.offset;
/*     */   }
/*     */ 
/*     */   protected void setOffset(int paramInt)
/*     */   {
/* 215 */     this.offset = paramInt;
/*     */   }
/*     */ 
/*     */   public boolean isPrimitive()
/*     */   {
/* 225 */     int i = this.signature.charAt(0);
/* 226 */     return (i != 76) && (i != 91);
/*     */   }
/*     */ 
/*     */   public boolean isUnshared()
/*     */   {
/* 236 */     return this.unshared;
/*     */   }
/*     */ 
/*     */   public int compareTo(Object paramObject)
/*     */   {
/* 247 */     ObjectStreamField localObjectStreamField = (ObjectStreamField)paramObject;
/* 248 */     boolean bool = isPrimitive();
/* 249 */     if (bool != localObjectStreamField.isPrimitive()) {
/* 250 */       return bool ? -1 : 1;
/*     */     }
/* 252 */     return this.name.compareTo(localObjectStreamField.name);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 259 */     return this.signature + ' ' + this.name;
/*     */   }
/*     */ 
/*     */   Field getField()
/*     */   {
/* 267 */     return this.field;
/*     */   }
/*     */ 
/*     */   String getSignature()
/*     */   {
/* 275 */     return this.signature;
/*     */   }
/*     */ 
/*     */   private static String getClassSignature(Class<?> paramClass)
/*     */   {
/* 282 */     StringBuilder localStringBuilder = new StringBuilder();
/* 283 */     while (paramClass.isArray()) {
/* 284 */       localStringBuilder.append('[');
/* 285 */       paramClass = paramClass.getComponentType();
/*     */     }
/* 287 */     if (paramClass.isPrimitive()) {
/* 288 */       if (paramClass == Integer.TYPE)
/* 289 */         localStringBuilder.append('I');
/* 290 */       else if (paramClass == Byte.TYPE)
/* 291 */         localStringBuilder.append('B');
/* 292 */       else if (paramClass == Long.TYPE)
/* 293 */         localStringBuilder.append('J');
/* 294 */       else if (paramClass == Float.TYPE)
/* 295 */         localStringBuilder.append('F');
/* 296 */       else if (paramClass == Double.TYPE)
/* 297 */         localStringBuilder.append('D');
/* 298 */       else if (paramClass == Short.TYPE)
/* 299 */         localStringBuilder.append('S');
/* 300 */       else if (paramClass == Character.TYPE)
/* 301 */         localStringBuilder.append('C');
/* 302 */       else if (paramClass == Boolean.TYPE)
/* 303 */         localStringBuilder.append('Z');
/* 304 */       else if (paramClass == Void.TYPE)
/* 305 */         localStringBuilder.append('V');
/*     */       else
/* 307 */         throw new InternalError();
/*     */     }
/*     */     else {
/* 310 */       localStringBuilder.append('L' + paramClass.getName().replace('.', '/') + ';');
/*     */     }
/* 312 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.ObjectStreamField
 * JD-Core Version:    0.6.2
 */