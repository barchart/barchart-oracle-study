/*     */ package com.sun.org.apache.xerces.internal.impl.dv.xs;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
/*     */ import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
/*     */ 
/*     */ class PrecisionDecimalDV extends TypeValidator
/*     */ {
/*     */   public short getAllowedFacets()
/*     */   {
/* 329 */     return 4088; } 
/*     */   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException { // Byte code:
/*     */     //   0: new 45	com/sun/org/apache/xerces/internal/impl/dv/xs/PrecisionDecimalDV$XPrecisionDecimal
/*     */     //   3: dup
/*     */     //   4: aload_1
/*     */     //   5: invokespecial 69	com/sun/org/apache/xerces/internal/impl/dv/xs/PrecisionDecimalDV$XPrecisionDecimal:<init>	(Ljava/lang/String;)V
/*     */     //   8: areturn
/*     */     //   9: astore_3
/*     */     //   10: new 43	com/sun/org/apache/xerces/internal/impl/dv/InvalidDatatypeValueException
/*     */     //   13: dup
/*     */     //   14: ldc 1
/*     */     //   16: iconst_2
/*     */     //   17: anewarray 48	java/lang/Object
/*     */     //   20: dup
/*     */     //   21: iconst_0
/*     */     //   22: aload_1
/*     */     //   23: aastore
/*     */     //   24: dup
/*     */     //   25: iconst_1
/*     */     //   26: ldc 2
/*     */     //   28: aastore
/*     */     //   29: invokespecial 66	com/sun/org/apache/xerces/internal/impl/dv/InvalidDatatypeValueException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */     //   32: athrow
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	8	9	java/lang/NumberFormatException } 
/* 345 */   public int compare(Object value1, Object value2) { return ((XPrecisionDecimal)value1).compareTo((XPrecisionDecimal)value2); }
/*     */ 
/*     */   public int getFractionDigits(Object value)
/*     */   {
/* 349 */     return ((XPrecisionDecimal)value).fracDigits;
/*     */   }
/*     */ 
/*     */   public int getTotalDigits(Object value) {
/* 353 */     return ((XPrecisionDecimal)value).totalDigits;
/*     */   }
/*     */ 
/*     */   public boolean isIdentical(Object value1, Object value2) {
/* 357 */     if ((!(value2 instanceof XPrecisionDecimal)) || (!(value1 instanceof XPrecisionDecimal)))
/* 358 */       return false;
/* 359 */     return ((XPrecisionDecimal)value1).isIdentical((XPrecisionDecimal)value2);
/*     */   }
/*     */ 
/*     */   static class XPrecisionDecimal
/*     */   {
/*  38 */     int sign = 1;
/*     */ 
/*  40 */     int totalDigits = 0;
/*     */ 
/*  42 */     int intDigits = 0;
/*     */ 
/*  44 */     int fracDigits = 0;
/*     */ 
/*  48 */     String ivalue = "";
/*     */ 
/*  50 */     String fvalue = "";
/*     */ 
/*  52 */     int pvalue = 0;
/*     */     private String canonical;
/*     */ 
/*     */     XPrecisionDecimal(String content)
/*     */       throws NumberFormatException
/*     */     {
/*  56 */       if (content.equals("NaN")) {
/*  57 */         this.ivalue = content;
/*  58 */         this.sign = 0;
/*     */       }
/*  60 */       if ((content.equals("+INF")) || (content.equals("INF")) || (content.equals("-INF"))) {
/*  61 */         this.ivalue = (content.charAt(0) == '+' ? content.substring(1) : content);
/*  62 */         return;
/*     */       }
/*  64 */       initD(content);
/*     */     }
/*     */ 
/*     */     void initD(String content) throws NumberFormatException {
/*  68 */       int len = content.length();
/*  69 */       if (len == 0) {
/*  70 */         throw new NumberFormatException();
/*     */       }
/*     */ 
/*  74 */       int intStart = 0; int intEnd = 0; int fracStart = 0; int fracEnd = 0;
/*     */ 
/*  77 */       if (content.charAt(0) == '+')
/*     */       {
/*  79 */         intStart = 1;
/*     */       }
/*  81 */       else if (content.charAt(0) == '-') {
/*  82 */         intStart = 1;
/*  83 */         this.sign = -1;
/*     */       }
/*     */ 
/*  87 */       int actualIntStart = intStart;
/*  88 */       while ((actualIntStart < len) && (content.charAt(actualIntStart) == '0')) {
/*  89 */         actualIntStart++;
/*     */       }
/*     */ 
/*  93 */       for (intEnd = actualIntStart; (intEnd < len) && (TypeValidator.isDigit(content.charAt(intEnd))); intEnd++);
/*  96 */       if (intEnd < len)
/*     */       {
/*  98 */         if ((content.charAt(intEnd) != '.') && (content.charAt(intEnd) != 'E') && (content.charAt(intEnd) != 'e')) {
/*  99 */           throw new NumberFormatException();
/*     */         }
/* 101 */         if (content.charAt(intEnd) == '.')
/*     */         {
/* 103 */           fracStart = intEnd + 1;
/*     */ 
/* 107 */           fracEnd = fracStart;
/* 108 */           while ((fracEnd < len) && (TypeValidator.isDigit(content.charAt(fracEnd)))) {
/* 109 */             fracEnd++;
/*     */           }
/*     */         }
/* 112 */         this.pvalue = Integer.parseInt(content.substring(intEnd + 1, len));
/*     */       }
/*     */ 
/* 117 */       if ((intStart == intEnd) && (fracStart == fracEnd)) {
/* 118 */         throw new NumberFormatException();
/*     */       }
/*     */ 
/* 126 */       for (int fracPos = fracStart; fracPos < fracEnd; fracPos++) {
/* 127 */         if (!TypeValidator.isDigit(content.charAt(fracPos))) {
/* 128 */           throw new NumberFormatException();
/*     */         }
/*     */       }
/* 131 */       this.intDigits = (intEnd - actualIntStart);
/* 132 */       this.fracDigits = (fracEnd - fracStart);
/*     */ 
/* 134 */       if (this.intDigits > 0) {
/* 135 */         this.ivalue = content.substring(actualIntStart, intEnd);
/*     */       }
/*     */ 
/* 138 */       if (this.fracDigits > 0) {
/* 139 */         this.fvalue = content.substring(fracStart, fracEnd);
/* 140 */         if (fracEnd < len) {
/* 141 */           this.pvalue = Integer.parseInt(content.substring(fracEnd + 1, len));
/*     */         }
/*     */       }
/* 144 */       this.totalDigits = (this.intDigits + this.fracDigits);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object val)
/*     */     {
/* 149 */       if (val == this) {
/* 150 */         return true;
/*     */       }
/* 152 */       if (!(val instanceof XPrecisionDecimal))
/* 153 */         return false;
/* 154 */       XPrecisionDecimal oval = (XPrecisionDecimal)val;
/*     */ 
/* 156 */       return compareTo(oval) == 0;
/*     */     }
/*     */ 
/*     */     private int compareFractionalPart(XPrecisionDecimal oval)
/*     */     {
/* 163 */       if (this.fvalue.equals(oval.fvalue)) {
/* 164 */         return 0;
/*     */       }
/* 166 */       StringBuffer temp1 = new StringBuffer(this.fvalue);
/* 167 */       StringBuffer temp2 = new StringBuffer(oval.fvalue);
/*     */ 
/* 169 */       truncateTrailingZeros(temp1, temp2);
/* 170 */       return temp1.toString().compareTo(temp2.toString());
/*     */     }
/*     */ 
/*     */     private void truncateTrailingZeros(StringBuffer fValue, StringBuffer otherFValue) {
/* 174 */       for (int i = fValue.length() - 1; (i >= 0) && 
/* 175 */         (fValue.charAt(i) == '0'); i--)
/*     */       {
/* 176 */         fValue.deleteCharAt(i);
/*     */       }
/*     */ 
/* 180 */       for (int i = otherFValue.length() - 1; (i >= 0) && 
/* 181 */         (otherFValue.charAt(i) == '0'); i--)
/*     */       {
/* 182 */         otherFValue.deleteCharAt(i);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int compareTo(XPrecisionDecimal val)
/*     */     {
/* 190 */       if (this.sign == 0) {
/* 191 */         return 2;
/*     */       }
/*     */ 
/* 194 */       if ((this.ivalue.equals("INF")) || (val.ivalue.equals("INF"))) {
/* 195 */         if (this.ivalue.equals(val.ivalue))
/* 196 */           return 0;
/* 197 */         if (this.ivalue.equals("INF"))
/* 198 */           return 1;
/* 199 */         return -1;
/*     */       }
/*     */ 
/* 203 */       if ((this.ivalue.equals("-INF")) || (val.ivalue.equals("-INF"))) {
/* 204 */         if (this.ivalue.equals(val.ivalue))
/* 205 */           return 0;
/* 206 */         if (this.ivalue.equals("-INF"))
/* 207 */           return -1;
/* 208 */         return 1;
/*     */       }
/*     */ 
/* 211 */       if (this.sign != val.sign) {
/* 212 */         return this.sign > val.sign ? 1 : -1;
/*     */       }
/* 214 */       return this.sign * compare(val);
/*     */     }
/*     */ 
/*     */     private int compare(XPrecisionDecimal val)
/*     */     {
/* 221 */       if ((this.pvalue != 0) || (val.pvalue != 0)) {
/* 222 */         if (this.pvalue == val.pvalue) {
/* 223 */           return intComp(val);
/*     */         }
/*     */ 
/* 226 */         if (this.intDigits + this.pvalue != val.intDigits + val.pvalue) {
/* 227 */           return this.intDigits + this.pvalue > val.intDigits + val.pvalue ? 1 : -1;
/*     */         }
/*     */ 
/* 230 */         if (this.pvalue > val.pvalue) {
/* 231 */           int expDiff = this.pvalue - val.pvalue;
/* 232 */           StringBuffer buffer = new StringBuffer(this.ivalue);
/* 233 */           StringBuffer fbuffer = new StringBuffer(this.fvalue);
/* 234 */           for (int i = 0; i < expDiff; i++)
/* 235 */             if (i < this.fracDigits) {
/* 236 */               buffer.append(this.fvalue.charAt(i));
/* 237 */               fbuffer.deleteCharAt(i);
/*     */             }
/*     */             else {
/* 240 */               buffer.append('0');
/*     */             }
/* 242 */           return compareDecimal(buffer.toString(), val.ivalue, fbuffer.toString(), val.fvalue);
/*     */         }
/*     */ 
/* 245 */         int expDiff = val.pvalue - this.pvalue;
/* 246 */         StringBuffer buffer = new StringBuffer(val.ivalue);
/* 247 */         StringBuffer fbuffer = new StringBuffer(val.fvalue);
/* 248 */         for (int i = 0; i < expDiff; i++)
/* 249 */           if (i < val.fracDigits) {
/* 250 */             buffer.append(val.fvalue.charAt(i));
/* 251 */             fbuffer.deleteCharAt(i);
/*     */           }
/*     */           else {
/* 254 */             buffer.append('0');
/*     */           }
/* 256 */         return compareDecimal(this.ivalue, buffer.toString(), this.fvalue, fbuffer.toString());
/*     */       }
/*     */ 
/* 261 */       return intComp(val);
/*     */     }
/*     */ 
/*     */     private int intComp(XPrecisionDecimal val)
/*     */     {
/* 270 */       if (this.intDigits != val.intDigits) {
/* 271 */         return this.intDigits > val.intDigits ? 1 : -1;
/*     */       }
/* 273 */       return compareDecimal(this.ivalue, val.ivalue, this.fvalue, val.fvalue);
/*     */     }
/*     */ 
/*     */     private int compareDecimal(String iValue, String fValue, String otherIValue, String otherFValue)
/*     */     {
/* 281 */       int ret = iValue.compareTo(otherIValue);
/* 282 */       if (ret != 0) {
/* 283 */         return ret > 0 ? 1 : -1;
/*     */       }
/* 285 */       if (fValue.equals(otherFValue)) {
/* 286 */         return 0;
/*     */       }
/* 288 */       StringBuffer temp1 = new StringBuffer(fValue);
/* 289 */       StringBuffer temp2 = new StringBuffer(otherFValue);
/*     */ 
/* 291 */       truncateTrailingZeros(temp1, temp2);
/* 292 */       ret = temp1.toString().compareTo(temp2.toString());
/* 293 */       return ret > 0 ? 1 : ret == 0 ? 0 : -1;
/*     */     }
/*     */ 
/*     */     public synchronized String toString()
/*     */     {
/* 299 */       if (this.canonical == null) {
/* 300 */         makeCanonical();
/*     */       }
/* 302 */       return this.canonical;
/*     */     }
/*     */ 
/*     */     private void makeCanonical()
/*     */     {
/* 307 */       this.canonical = "TBD by Working Group";
/*     */     }
/*     */ 
/*     */     public boolean isIdentical(XPrecisionDecimal decimal)
/*     */     {
/* 315 */       if ((this.ivalue.equals(decimal.ivalue)) && ((this.ivalue.equals("INF")) || (this.ivalue.equals("-INF")) || (this.ivalue.equals("NaN")))) {
/* 316 */         return true;
/*     */       }
/* 318 */       if ((this.sign == decimal.sign) && (this.intDigits == decimal.intDigits) && (this.fracDigits == decimal.fracDigits) && (this.pvalue == decimal.pvalue) && (this.ivalue.equals(decimal.ivalue)) && (this.fvalue.equals(decimal.fvalue)))
/*     */       {
/* 320 */         return true;
/* 321 */       }return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.dv.xs.PrecisionDecimalDV
 * JD-Core Version:    0.6.2
 */