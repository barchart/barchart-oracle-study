/*     */ package com.sun.org.apache.xerces.internal.impl.dv.xs;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
/*     */ import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
/*     */ import com.sun.org.apache.xerces.internal.xs.datatypes.XSDecimal;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ public class DecimalDV extends TypeValidator
/*     */ {
/*     */   public final short getAllowedFacets()
/*     */   {
/*  42 */     return 4088; } 
/*     */   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException { // Byte code:
/*     */     //   0: new 43	com/sun/org/apache/xerces/internal/impl/dv/xs/DecimalDV$XDecimal
/*     */     //   3: dup
/*     */     //   4: aload_1
/*     */     //   5: invokespecial 64	com/sun/org/apache/xerces/internal/impl/dv/xs/DecimalDV$XDecimal:<init>	(Ljava/lang/String;)V
/*     */     //   8: areturn
/*     */     //   9: astore_3
/*     */     //   10: new 41	com/sun/org/apache/xerces/internal/impl/dv/InvalidDatatypeValueException
/*     */     //   13: dup
/*     */     //   14: ldc 1
/*     */     //   16: iconst_2
/*     */     //   17: anewarray 46	java/lang/Object
/*     */     //   20: dup
/*     */     //   21: iconst_0
/*     */     //   22: aload_1
/*     */     //   23: aastore
/*     */     //   24: dup
/*     */     //   25: iconst_1
/*     */     //   26: ldc 2
/*     */     //   28: aastore
/*     */     //   29: invokespecial 62	com/sun/org/apache/xerces/internal/impl/dv/InvalidDatatypeValueException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */     //   32: athrow
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	8	9	java/lang/NumberFormatException } 
/*  54 */   public final int compare(Object value1, Object value2) { return ((XDecimal)value1).compareTo((XDecimal)value2); }
/*     */ 
/*     */   public final int getTotalDigits(Object value)
/*     */   {
/*  58 */     return ((XDecimal)value).totalDigits;
/*     */   }
/*     */ 
/*     */   public final int getFractionDigits(Object value) {
/*  62 */     return ((XDecimal)value).fracDigits; } 
/*  68 */   static class XDecimal implements XSDecimal { int sign = 1;
/*     */ 
/*  70 */     int totalDigits = 0;
/*     */ 
/*  72 */     int intDigits = 0;
/*     */ 
/*  74 */     int fracDigits = 0;
/*     */ 
/*  76 */     String ivalue = "";
/*     */ 
/*  78 */     String fvalue = "";
/*     */ 
/*  80 */     boolean integer = false;
/*     */     private String canonical;
/*     */ 
/*  83 */     XDecimal(String content) throws NumberFormatException { initD(content); }
/*     */ 
/*     */     XDecimal(String content, boolean integer) throws NumberFormatException {
/*  86 */       if (integer)
/*  87 */         initI(content);
/*     */       else
/*  89 */         initD(content); 
/*     */     }
/*     */ 
/*  92 */     void initD(String content) throws NumberFormatException { int len = content.length();
/*  93 */       if (len == 0) {
/*  94 */         throw new NumberFormatException();
/*     */       }
/*     */ 
/*  98 */       int intStart = 0; int intEnd = 0; int fracStart = 0; int fracEnd = 0;
/*     */ 
/* 101 */       if (content.charAt(0) == '+')
/*     */       {
/* 103 */         intStart = 1;
/*     */       }
/* 105 */       else if (content.charAt(0) == '-')
/*     */       {
/* 107 */         intStart = 1;
/* 108 */         this.sign = -1;
/*     */       }
/*     */ 
/* 112 */       int actualIntStart = intStart;
/* 113 */       while ((actualIntStart < len) && (content.charAt(actualIntStart) == '0')) {
/* 114 */         actualIntStart++;
/*     */       }
/*     */ 
/* 118 */       intEnd = actualIntStart;
/* 119 */       while ((intEnd < len) && (TypeValidator.isDigit(content.charAt(intEnd)))) {
/* 120 */         intEnd++;
/*     */       }
/*     */ 
/* 123 */       if (intEnd < len)
/*     */       {
/* 125 */         if (content.charAt(intEnd) != '.') {
/* 126 */           throw new NumberFormatException();
/*     */         }
/*     */ 
/* 129 */         fracStart = intEnd + 1;
/* 130 */         fracEnd = len;
/*     */       }
/*     */ 
/* 134 */       if ((intStart == intEnd) && (fracStart == fracEnd)) {
/* 135 */         throw new NumberFormatException();
/*     */       }
/*     */ 
/* 138 */       while ((fracEnd > fracStart) && (content.charAt(fracEnd - 1) == '0')) {
/* 139 */         fracEnd--;
/*     */       }
/*     */ 
/* 143 */       for (int fracPos = fracStart; fracPos < fracEnd; fracPos++) {
/* 144 */         if (!TypeValidator.isDigit(content.charAt(fracPos))) {
/* 145 */           throw new NumberFormatException();
/*     */         }
/*     */       }
/* 148 */       this.intDigits = (intEnd - actualIntStart);
/* 149 */       this.fracDigits = (fracEnd - fracStart);
/* 150 */       this.totalDigits = (this.intDigits + this.fracDigits);
/*     */ 
/* 152 */       if (this.intDigits > 0) {
/* 153 */         this.ivalue = content.substring(actualIntStart, intEnd);
/* 154 */         if (this.fracDigits > 0) {
/* 155 */           this.fvalue = content.substring(fracStart, fracEnd);
/*     */         }
/*     */       }
/* 158 */       else if (this.fracDigits > 0) {
/* 159 */         this.fvalue = content.substring(fracStart, fracEnd);
/*     */       }
/*     */       else
/*     */       {
/* 163 */         this.sign = 0;
/*     */       } }
/*     */ 
/*     */     void initI(String content) throws NumberFormatException
/*     */     {
/* 168 */       int len = content.length();
/* 169 */       if (len == 0) {
/* 170 */         throw new NumberFormatException();
/*     */       }
/*     */ 
/* 173 */       int intStart = 0; int intEnd = 0;
/*     */ 
/* 176 */       if (content.charAt(0) == '+')
/*     */       {
/* 178 */         intStart = 1;
/*     */       }
/* 180 */       else if (content.charAt(0) == '-')
/*     */       {
/* 182 */         intStart = 1;
/* 183 */         this.sign = -1;
/*     */       }
/*     */ 
/* 187 */       int actualIntStart = intStart;
/* 188 */       while ((actualIntStart < len) && (content.charAt(actualIntStart) == '0')) {
/* 189 */         actualIntStart++;
/*     */       }
/*     */ 
/* 193 */       intEnd = actualIntStart;
/* 194 */       while ((intEnd < len) && (TypeValidator.isDigit(content.charAt(intEnd)))) {
/* 195 */         intEnd++;
/*     */       }
/*     */ 
/* 198 */       if (intEnd < len) {
/* 199 */         throw new NumberFormatException();
/*     */       }
/*     */ 
/* 202 */       if (intStart == intEnd) {
/* 203 */         throw new NumberFormatException();
/*     */       }
/* 205 */       this.intDigits = (intEnd - actualIntStart);
/* 206 */       this.fracDigits = 0;
/* 207 */       this.totalDigits = this.intDigits;
/*     */ 
/* 209 */       if (this.intDigits > 0) {
/* 210 */         this.ivalue = content.substring(actualIntStart, intEnd);
/*     */       }
/*     */       else
/*     */       {
/* 214 */         this.sign = 0;
/*     */       }
/*     */ 
/* 217 */       this.integer = true;
/*     */     }
/*     */     public boolean equals(Object val) {
/* 220 */       if (val == this) {
/* 221 */         return true;
/*     */       }
/* 223 */       if (!(val instanceof XDecimal))
/* 224 */         return false;
/* 225 */       XDecimal oval = (XDecimal)val;
/*     */ 
/* 227 */       if (this.sign != oval.sign)
/* 228 */         return false;
/* 229 */       if (this.sign == 0) {
/* 230 */         return true;
/*     */       }
/* 232 */       return (this.intDigits == oval.intDigits) && (this.fracDigits == oval.fracDigits) && (this.ivalue.equals(oval.ivalue)) && (this.fvalue.equals(oval.fvalue));
/*     */     }
/*     */ 
/*     */     public int compareTo(XDecimal val) {
/* 236 */       if (this.sign != val.sign)
/* 237 */         return this.sign > val.sign ? 1 : -1;
/* 238 */       if (this.sign == 0)
/* 239 */         return 0;
/* 240 */       return this.sign * intComp(val);
/*     */     }
/*     */     private int intComp(XDecimal val) {
/* 243 */       if (this.intDigits != val.intDigits)
/* 244 */         return this.intDigits > val.intDigits ? 1 : -1;
/* 245 */       int ret = this.ivalue.compareTo(val.ivalue);
/* 246 */       if (ret != 0)
/* 247 */         return ret > 0 ? 1 : -1;
/* 248 */       ret = this.fvalue.compareTo(val.fvalue);
/* 249 */       return ret > 0 ? 1 : ret == 0 ? 0 : -1;
/*     */     }
/*     */ 
/*     */     public synchronized String toString() {
/* 253 */       if (this.canonical == null) {
/* 254 */         makeCanonical();
/*     */       }
/* 256 */       return this.canonical;
/*     */     }
/*     */ 
/*     */     private void makeCanonical() {
/* 260 */       if (this.sign == 0) {
/* 261 */         if (this.integer)
/* 262 */           this.canonical = "0";
/*     */         else
/* 264 */           this.canonical = "0.0";
/* 265 */         return;
/*     */       }
/* 267 */       if ((this.integer) && (this.sign > 0)) {
/* 268 */         this.canonical = this.ivalue;
/* 269 */         return;
/*     */       }
/*     */ 
/* 272 */       StringBuffer buffer = new StringBuffer(this.totalDigits + 3);
/* 273 */       if (this.sign == -1)
/* 274 */         buffer.append('-');
/* 275 */       if (this.intDigits != 0)
/* 276 */         buffer.append(this.ivalue);
/*     */       else
/* 278 */         buffer.append('0');
/* 279 */       if (!this.integer) {
/* 280 */         buffer.append('.');
/* 281 */         if (this.fracDigits != 0) {
/* 282 */           buffer.append(this.fvalue);
/*     */         }
/*     */         else {
/* 285 */           buffer.append('0');
/*     */         }
/*     */       }
/* 288 */       this.canonical = buffer.toString();
/*     */     }
/*     */ 
/*     */     public BigDecimal getBigDecimal() {
/* 292 */       if (this.sign == 0) {
/* 293 */         return new BigDecimal(BigInteger.ZERO);
/*     */       }
/* 295 */       return new BigDecimal(toString());
/*     */     }
/*     */ 
/*     */     public BigInteger getBigInteger() throws NumberFormatException {
/* 299 */       if (this.fracDigits != 0) {
/* 300 */         throw new NumberFormatException();
/*     */       }
/* 302 */       if (this.sign == 0) {
/* 303 */         return BigInteger.ZERO;
/*     */       }
/* 305 */       if (this.sign == 1) {
/* 306 */         return new BigInteger(this.ivalue);
/*     */       }
/* 308 */       return new BigInteger("-" + this.ivalue);
/*     */     }
/*     */ 
/*     */     public long getLong() throws NumberFormatException {
/* 312 */       if (this.fracDigits != 0) {
/* 313 */         throw new NumberFormatException();
/*     */       }
/* 315 */       if (this.sign == 0) {
/* 316 */         return 0L;
/*     */       }
/* 318 */       if (this.sign == 1) {
/* 319 */         return Long.parseLong(this.ivalue);
/*     */       }
/* 321 */       return Long.parseLong("-" + this.ivalue);
/*     */     }
/*     */ 
/*     */     public int getInt() throws NumberFormatException {
/* 325 */       if (this.fracDigits != 0) {
/* 326 */         throw new NumberFormatException();
/*     */       }
/* 328 */       if (this.sign == 0) {
/* 329 */         return 0;
/*     */       }
/* 331 */       if (this.sign == 1) {
/* 332 */         return Integer.parseInt(this.ivalue);
/*     */       }
/* 334 */       return Integer.parseInt("-" + this.ivalue);
/*     */     }
/*     */ 
/*     */     public short getShort() throws NumberFormatException {
/* 338 */       if (this.fracDigits != 0) {
/* 339 */         throw new NumberFormatException();
/*     */       }
/* 341 */       if (this.sign == 0) {
/* 342 */         return 0;
/*     */       }
/* 344 */       if (this.sign == 1) {
/* 345 */         return Short.parseShort(this.ivalue);
/*     */       }
/* 347 */       return Short.parseShort("-" + this.ivalue);
/*     */     }
/*     */ 
/*     */     public byte getByte() throws NumberFormatException {
/* 351 */       if (this.fracDigits != 0) {
/* 352 */         throw new NumberFormatException();
/*     */       }
/* 354 */       if (this.sign == 0) {
/* 355 */         return 0;
/*     */       }
/* 357 */       if (this.sign == 1) {
/* 358 */         return Byte.parseByte(this.ivalue);
/*     */       }
/* 360 */       return Byte.parseByte("-" + this.ivalue);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.dv.xs.DecimalDV
 * JD-Core Version:    0.6.2
 */