/*     */ package javax.swing.text;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.text.AttributedCharacterIterator;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.text.Format;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.NumberFormat.Field;
/*     */ import java.text.ParseException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.JFormattedTextField;
/*     */ 
/*     */ public class NumberFormatter extends InternationalFormatter
/*     */ {
/*     */   private String specialChars;
/*     */ 
/*     */   public NumberFormatter()
/*     */   {
/* 102 */     this(NumberFormat.getNumberInstance());
/*     */   }
/*     */ 
/*     */   public NumberFormatter(NumberFormat paramNumberFormat)
/*     */   {
/* 111 */     super(paramNumberFormat);
/* 112 */     setFormat(paramNumberFormat);
/* 113 */     setAllowsInvalid(true);
/* 114 */     setCommitsOnValidEdit(false);
/* 115 */     setOverwriteMode(false);
/*     */   }
/*     */ 
/*     */   public void setFormat(Format paramFormat)
/*     */   {
/* 129 */     super.setFormat(paramFormat);
/*     */ 
/* 131 */     DecimalFormatSymbols localDecimalFormatSymbols = getDecimalFormatSymbols();
/*     */ 
/* 133 */     if (localDecimalFormatSymbols != null) {
/* 134 */       StringBuilder localStringBuilder = new StringBuilder();
/*     */ 
/* 136 */       localStringBuilder.append(localDecimalFormatSymbols.getCurrencySymbol());
/* 137 */       localStringBuilder.append(localDecimalFormatSymbols.getDecimalSeparator());
/* 138 */       localStringBuilder.append(localDecimalFormatSymbols.getGroupingSeparator());
/* 139 */       localStringBuilder.append(localDecimalFormatSymbols.getInfinity());
/* 140 */       localStringBuilder.append(localDecimalFormatSymbols.getInternationalCurrencySymbol());
/* 141 */       localStringBuilder.append(localDecimalFormatSymbols.getMinusSign());
/* 142 */       localStringBuilder.append(localDecimalFormatSymbols.getMonetaryDecimalSeparator());
/* 143 */       localStringBuilder.append(localDecimalFormatSymbols.getNaN());
/* 144 */       localStringBuilder.append(localDecimalFormatSymbols.getPercent());
/* 145 */       localStringBuilder.append('+');
/* 146 */       this.specialChars = localStringBuilder.toString();
/*     */     }
/*     */     else {
/* 149 */       this.specialChars = "";
/*     */     }
/*     */   }
/*     */ 
/*     */   Object stringToValue(String paramString, Format paramFormat)
/*     */     throws ParseException
/*     */   {
/* 158 */     if (paramFormat == null) {
/* 159 */       return paramString;
/*     */     }
/* 161 */     Object localObject = paramFormat.parseObject(paramString);
/*     */ 
/* 163 */     return convertValueToValueClass(localObject, getValueClass());
/*     */   }
/*     */ 
/*     */   private Object convertValueToValueClass(Object paramObject, Class paramClass)
/*     */   {
/* 174 */     if ((paramClass != null) && ((paramObject instanceof Number))) {
/* 175 */       Number localNumber = (Number)paramObject;
/* 176 */       if (paramClass == Integer.class) {
/* 177 */         return Integer.valueOf(localNumber.intValue());
/*     */       }
/* 179 */       if (paramClass == Long.class) {
/* 180 */         return Long.valueOf(localNumber.longValue());
/*     */       }
/* 182 */       if (paramClass == Float.class) {
/* 183 */         return Float.valueOf(localNumber.floatValue());
/*     */       }
/* 185 */       if (paramClass == Double.class) {
/* 186 */         return Double.valueOf(localNumber.doubleValue());
/*     */       }
/* 188 */       if (paramClass == Byte.class) {
/* 189 */         return Byte.valueOf(localNumber.byteValue());
/*     */       }
/* 191 */       if (paramClass == Short.class) {
/* 192 */         return Short.valueOf(localNumber.shortValue());
/*     */       }
/*     */     }
/* 195 */     return paramObject;
/*     */   }
/*     */ 
/*     */   private char getPositiveSign()
/*     */   {
/* 202 */     return '+';
/*     */   }
/*     */ 
/*     */   private char getMinusSign()
/*     */   {
/* 209 */     DecimalFormatSymbols localDecimalFormatSymbols = getDecimalFormatSymbols();
/*     */ 
/* 211 */     if (localDecimalFormatSymbols != null) {
/* 212 */       return localDecimalFormatSymbols.getMinusSign();
/*     */     }
/* 214 */     return '-';
/*     */   }
/*     */ 
/*     */   private char getDecimalSeparator()
/*     */   {
/* 221 */     DecimalFormatSymbols localDecimalFormatSymbols = getDecimalFormatSymbols();
/*     */ 
/* 223 */     if (localDecimalFormatSymbols != null) {
/* 224 */       return localDecimalFormatSymbols.getDecimalSeparator();
/*     */     }
/* 226 */     return '.';
/*     */   }
/*     */ 
/*     */   private DecimalFormatSymbols getDecimalFormatSymbols()
/*     */   {
/* 233 */     Format localFormat = getFormat();
/*     */ 
/* 235 */     if ((localFormat instanceof DecimalFormat)) {
/* 236 */       return ((DecimalFormat)localFormat).getDecimalFormatSymbols();
/*     */     }
/* 238 */     return null;
/*     */   }
/*     */ 
/*     */   boolean isLegalInsertText(String paramString)
/*     */   {
/* 248 */     if (getAllowsInvalid()) {
/* 249 */       return true;
/*     */     }
/* 251 */     for (int i = paramString.length() - 1; i >= 0; i--) {
/* 252 */       char c = paramString.charAt(i);
/*     */ 
/* 254 */       if ((!Character.isDigit(c)) && (this.specialChars.indexOf(c) == -1))
/*     */       {
/* 256 */         return false;
/*     */       }
/*     */     }
/* 259 */     return true;
/*     */   }
/*     */ 
/*     */   boolean isLiteral(Map paramMap)
/*     */   {
/* 267 */     if (!super.isLiteral(paramMap)) {
/* 268 */       if (paramMap == null) {
/* 269 */         return false;
/*     */       }
/* 271 */       int i = paramMap.size();
/*     */ 
/* 273 */       if (paramMap.get(NumberFormat.Field.GROUPING_SEPARATOR) != null) {
/* 274 */         i--;
/* 275 */         if (paramMap.get(NumberFormat.Field.INTEGER) != null) {
/* 276 */           i--;
/*     */         }
/*     */       }
/* 279 */       if (paramMap.get(NumberFormat.Field.EXPONENT_SYMBOL) != null) {
/* 280 */         i--;
/*     */       }
/* 282 */       if (paramMap.get(NumberFormat.Field.PERCENT) != null) {
/* 283 */         i--;
/*     */       }
/* 285 */       if (paramMap.get(NumberFormat.Field.PERMILLE) != null) {
/* 286 */         i--;
/*     */       }
/* 288 */       if (paramMap.get(NumberFormat.Field.CURRENCY) != null) {
/* 289 */         i--;
/*     */       }
/* 291 */       if (paramMap.get(NumberFormat.Field.SIGN) != null) {
/* 292 */         i--;
/*     */       }
/* 294 */       return i == 0;
/*     */     }
/* 296 */     return true;
/*     */   }
/*     */ 
/*     */   boolean isNavigatable(int paramInt)
/*     */   {
/* 305 */     if (!super.isNavigatable(paramInt))
/*     */     {
/* 307 */       return getBufferedChar(paramInt) == getDecimalSeparator();
/*     */     }
/* 309 */     return true;
/*     */   }
/*     */ 
/*     */   private NumberFormat.Field getFieldFrom(int paramInt1, int paramInt2)
/*     */   {
/* 317 */     if (isValidMask()) {
/* 318 */       int i = getFormattedTextField().getDocument().getLength();
/* 319 */       AttributedCharacterIterator localAttributedCharacterIterator = getIterator();
/*     */ 
/* 321 */       if (paramInt1 >= i) {
/* 322 */         paramInt1 += paramInt2;
/*     */       }
/* 324 */       while ((paramInt1 >= 0) && (paramInt1 < i)) {
/* 325 */         localAttributedCharacterIterator.setIndex(paramInt1);
/*     */ 
/* 327 */         Map localMap = localAttributedCharacterIterator.getAttributes();
/*     */         Iterator localIterator;
/* 329 */         if ((localMap != null) && (localMap.size() > 0)) {
/* 330 */           for (localIterator = localMap.keySet().iterator(); localIterator.hasNext(); ) { Object localObject = localIterator.next();
/* 331 */             if ((localObject instanceof NumberFormat.Field)) {
/* 332 */               return (NumberFormat.Field)localObject;
/*     */             }
/*     */           }
/*     */         }
/* 336 */         paramInt1 += paramInt2;
/*     */       }
/*     */     }
/* 339 */     return null;
/*     */   }
/*     */ 
/*     */   void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
/*     */     throws BadLocationException
/*     */   {
/* 348 */     if ((!getAllowsInvalid()) && (paramInt2 == 0) && (paramString != null) && (paramString.length() == 1) && (toggleSignIfNecessary(paramFilterBypass, paramInt1, paramString.charAt(0))))
/*     */     {
/* 351 */       return;
/*     */     }
/* 353 */     super.replace(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
/*     */   }
/*     */ 
/*     */   private boolean toggleSignIfNecessary(DocumentFilter.FilterBypass paramFilterBypass, int paramInt, char paramChar)
/*     */     throws BadLocationException
/*     */   {
/* 364 */     if ((paramChar == getMinusSign()) || (paramChar == getPositiveSign())) {
/* 365 */       NumberFormat.Field localField = getFieldFrom(paramInt, -1);
/*     */       try
/*     */       {
/*     */         Object localObject;
/* 369 */         if ((localField == null) || ((localField != NumberFormat.Field.EXPONENT) && (localField != NumberFormat.Field.EXPONENT_SYMBOL) && (localField != NumberFormat.Field.EXPONENT_SIGN)))
/*     */         {
/* 373 */           localObject = toggleSign(paramChar == getPositiveSign());
/*     */         }
/*     */         else
/*     */         {
/* 377 */           localObject = toggleExponentSign(paramInt, paramChar);
/*     */         }
/* 379 */         if ((localObject != null) && (isValidValue(localObject, false))) {
/* 380 */           int i = getLiteralCountTo(paramInt);
/* 381 */           String str = valueToString(localObject);
/*     */ 
/* 383 */           paramFilterBypass.remove(0, paramFilterBypass.getDocument().getLength());
/* 384 */           paramFilterBypass.insertString(0, str, null);
/* 385 */           updateValue(localObject);
/* 386 */           repositionCursor(getLiteralCountTo(paramInt) - i + paramInt, 1);
/*     */ 
/* 388 */           return true;
/*     */         }
/*     */       } catch (ParseException localParseException) {
/* 391 */         invalidEdit();
/*     */       }
/*     */     }
/* 394 */     return false;
/*     */   }
/*     */ 
/*     */   private Object toggleSign(boolean paramBoolean)
/*     */     throws ParseException
/*     */   {
/* 402 */     Object localObject = stringToValue(getFormattedTextField().getText());
/*     */ 
/* 404 */     if (localObject != null)
/*     */     {
/* 407 */       String str = localObject.toString();
/*     */ 
/* 409 */       if ((str != null) && (str.length() > 0)) {
/* 410 */         if (paramBoolean) {
/* 411 */           if (str.charAt(0) == '-')
/* 412 */             str = str.substring(1);
/*     */         }
/*     */         else
/*     */         {
/* 416 */           if (str.charAt(0) == '+') {
/* 417 */             str = str.substring(1);
/*     */           }
/* 419 */           if ((str.length() > 0) && (str.charAt(0) != '-')) {
/* 420 */             str = "-" + str;
/*     */           }
/*     */         }
/* 423 */         if (str != null) {
/* 424 */           Class localClass = getValueClass();
/*     */ 
/* 426 */           if (localClass == null)
/* 427 */             localClass = localObject.getClass();
/*     */           try
/*     */           {
/* 430 */             Constructor localConstructor = localClass.getConstructor(new Class[] { String.class });
/*     */ 
/* 433 */             if (localConstructor != null)
/* 434 */               return localConstructor.newInstance(new Object[] { str });
/*     */           } catch (Throwable localThrowable) {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 440 */     return null;
/*     */   }
/*     */ 
/*     */   private Object toggleExponentSign(int paramInt, char paramChar)
/*     */     throws BadLocationException, ParseException
/*     */   {
/* 449 */     String str = getFormattedTextField().getText();
/* 450 */     int i = 0;
/* 451 */     int j = getAttributeStart(NumberFormat.Field.EXPONENT_SIGN);
/*     */ 
/* 453 */     if (j >= 0) {
/* 454 */       i = 1;
/* 455 */       paramInt = j;
/*     */     }
/* 457 */     if (paramChar == getPositiveSign()) {
/* 458 */       str = getReplaceString(paramInt, i, null);
/*     */     }
/*     */     else {
/* 461 */       str = getReplaceString(paramInt, i, new String(new char[] { paramChar }));
/*     */     }
/*     */ 
/* 464 */     return stringToValue(str);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.text.NumberFormatter
 * JD-Core Version:    0.6.2
 */