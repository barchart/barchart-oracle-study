/*     */ package java.beans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class XMLEncoder extends Encoder
/*     */   implements AutoCloseable
/*     */ {
/*     */   private final CharsetEncoder encoder;
/*     */   private final String charset;
/*     */   private final boolean declaration;
/*     */   private OutputStreamWriter out;
/*     */   private Object owner;
/* 215 */   private int indentation = 0;
/* 216 */   private boolean internal = false;
/*     */   private Map<Object, ValueData> valueToExpression;
/*     */   private Map<Object, List<Statement>> targetToStatementList;
/* 219 */   private boolean preambleWritten = false;
/*     */   private NameGenerator nameGenerator;
/*     */ 
/*     */   public XMLEncoder(OutputStream paramOutputStream)
/*     */   {
/* 242 */     this(paramOutputStream, "UTF-8", true, 0);
/*     */   }
/*     */ 
/*     */   public XMLEncoder(OutputStream paramOutputStream, String paramString, boolean paramBoolean, int paramInt)
/*     */   {
/* 278 */     if (paramOutputStream == null) {
/* 279 */       throw new IllegalArgumentException("the output stream cannot be null");
/*     */     }
/* 281 */     if (paramInt < 0) {
/* 282 */       throw new IllegalArgumentException("the indentation must be >= 0");
/*     */     }
/* 284 */     Charset localCharset = Charset.forName(paramString);
/* 285 */     this.encoder = localCharset.newEncoder();
/* 286 */     this.charset = paramString;
/* 287 */     this.declaration = paramBoolean;
/* 288 */     this.indentation = paramInt;
/* 289 */     this.out = new OutputStreamWriter(paramOutputStream, localCharset.newEncoder());
/* 290 */     this.valueToExpression = new IdentityHashMap();
/* 291 */     this.targetToStatementList = new IdentityHashMap();
/* 292 */     this.nameGenerator = new NameGenerator();
/*     */   }
/*     */ 
/*     */   public void setOwner(Object paramObject)
/*     */   {
/* 303 */     this.owner = paramObject;
/* 304 */     writeExpression(new Expression(this, "getOwner", new Object[0]));
/*     */   }
/*     */ 
/*     */   public Object getOwner()
/*     */   {
/* 315 */     return this.owner;
/*     */   }
/*     */ 
/*     */   public void writeObject(Object paramObject)
/*     */   {
/* 326 */     if (this.internal) {
/* 327 */       super.writeObject(paramObject);
/*     */     }
/*     */     else
/* 330 */       writeStatement(new Statement(this, "writeObject", new Object[] { paramObject }));
/*     */   }
/*     */ 
/*     */   private List<Statement> statementList(Object paramObject)
/*     */   {
/* 335 */     Object localObject = (List)this.targetToStatementList.get(paramObject);
/* 336 */     if (localObject == null) {
/* 337 */       localObject = new ArrayList();
/* 338 */       this.targetToStatementList.put(paramObject, localObject);
/*     */     }
/* 340 */     return localObject;
/*     */   }
/*     */ 
/*     */   private void mark(Object paramObject, boolean paramBoolean)
/*     */   {
/* 345 */     if ((paramObject == null) || (paramObject == this)) {
/* 346 */       return;
/*     */     }
/* 348 */     ValueData localValueData = getValueData(paramObject);
/* 349 */     Expression localExpression = localValueData.exp;
/*     */ 
/* 352 */     if ((paramObject.getClass() == String.class) && (localExpression == null)) {
/* 353 */       return;
/*     */     }
/*     */ 
/* 357 */     if (paramBoolean) {
/* 358 */       localValueData.refs += 1;
/*     */     }
/* 360 */     if (localValueData.marked) {
/* 361 */       return;
/*     */     }
/* 363 */     localValueData.marked = true;
/* 364 */     Object localObject = localExpression.getTarget();
/* 365 */     mark(localExpression);
/* 366 */     if (!(localObject instanceof Class)) {
/* 367 */       statementList(localObject).add(localExpression);
/*     */ 
/* 370 */       localValueData.refs += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void mark(Statement paramStatement) {
/* 375 */     Object[] arrayOfObject = paramStatement.getArguments();
/* 376 */     for (int i = 0; i < arrayOfObject.length; i++) {
/* 377 */       Object localObject = arrayOfObject[i];
/* 378 */       mark(localObject, true);
/*     */     }
/* 380 */     mark(paramStatement.getTarget(), false);
/*     */   }
/*     */ 
/*     */   public void writeStatement(Statement paramStatement)
/*     */   {
/* 397 */     boolean bool = this.internal;
/* 398 */     this.internal = true;
/*     */     try {
/* 400 */       super.writeStatement(paramStatement);
/*     */ 
/* 409 */       mark(paramStatement);
/* 410 */       Object localObject = paramStatement.getTarget();
/* 411 */       if ((localObject instanceof Field)) {
/* 412 */         String str = paramStatement.getMethodName();
/* 413 */         Object[] arrayOfObject = paramStatement.getArguments();
/* 414 */         if ((str != null) && (arrayOfObject != null))
/*     */         {
/* 416 */           if ((str.equals("get")) && (arrayOfObject.length == 1)) {
/* 417 */             localObject = arrayOfObject[0];
/*     */           }
/* 419 */           else if ((str.equals("set")) && (arrayOfObject.length == 2))
/* 420 */             localObject = arrayOfObject[0];
/*     */         }
/*     */       }
/* 423 */       statementList(localObject).add(paramStatement);
/*     */     }
/*     */     catch (Exception localException) {
/* 426 */       getExceptionListener().exceptionThrown(new Exception("XMLEncoder: discarding statement " + paramStatement, localException));
/*     */     }
/* 428 */     this.internal = bool;
/*     */   }
/*     */ 
/*     */   public void writeExpression(Expression paramExpression)
/*     */   {
/* 449 */     boolean bool = this.internal;
/* 450 */     this.internal = true;
/* 451 */     Object localObject = getValue(paramExpression);
/* 452 */     if ((get(localObject) == null) || (((localObject instanceof String)) && (!bool))) {
/* 453 */       getValueData(localObject).exp = paramExpression;
/* 454 */       super.writeExpression(paramExpression);
/*     */     }
/* 456 */     this.internal = bool;
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */   {
/* 468 */     if (!this.preambleWritten) {
/* 469 */       if (this.declaration) {
/* 470 */         writeln("<?xml version=" + quote("1.0") + " encoding=" + quote(this.charset) + "?>");
/*     */       }
/*     */ 
/* 473 */       writeln("<java version=" + quote(System.getProperty("java.version")) + " class=" + quote(XMLDecoder.class.getName()) + ">");
/*     */ 
/* 475 */       this.preambleWritten = true;
/*     */     }
/* 477 */     this.indentation += 1;
/* 478 */     List localList = statementList(this);
/* 479 */     while (!localList.isEmpty()) {
/* 480 */       Statement localStatement = (Statement)localList.remove(0);
/* 481 */       if ("writeObject".equals(localStatement.getMethodName())) {
/* 482 */         outputValue(localStatement.getArguments()[0], this, true);
/*     */       }
/*     */       else {
/* 485 */         outputStatement(localStatement, this, false);
/*     */       }
/*     */     }
/* 488 */     this.indentation -= 1;
/*     */     try
/*     */     {
/* 491 */       this.out.flush();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 494 */       getExceptionListener().exceptionThrown(localIOException);
/*     */     }
/* 496 */     clear();
/*     */   }
/*     */ 
/*     */   void clear() {
/* 500 */     super.clear();
/* 501 */     this.nameGenerator.clear();
/* 502 */     this.valueToExpression.clear();
/* 503 */     this.targetToStatementList.clear();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 513 */     flush();
/* 514 */     writeln("</java>");
/*     */     try {
/* 516 */       this.out.close();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 519 */       getExceptionListener().exceptionThrown(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String quote(String paramString) {
/* 524 */     return "\"" + paramString + "\"";
/*     */   }
/*     */ 
/*     */   private ValueData getValueData(Object paramObject) {
/* 528 */     ValueData localValueData = (ValueData)this.valueToExpression.get(paramObject);
/* 529 */     if (localValueData == null) {
/* 530 */       localValueData = new ValueData(null);
/* 531 */       this.valueToExpression.put(paramObject, localValueData);
/*     */     }
/* 533 */     return localValueData;
/*     */   }
/*     */ 
/*     */   private static boolean isValidCharCode(int paramInt)
/*     */   {
/* 556 */     return ((32 <= paramInt) && (paramInt <= 55295)) || (10 == paramInt) || (9 == paramInt) || (13 == paramInt) || ((57344 <= paramInt) && (paramInt <= 65533)) || ((65536 <= paramInt) && (paramInt <= 1114111));
/*     */   }
/*     */ 
/*     */   private void writeln(String paramString)
/*     */   {
/*     */     try
/*     */     {
/* 566 */       StringBuilder localStringBuilder = new StringBuilder();
/* 567 */       for (int i = 0; i < this.indentation; i++) {
/* 568 */         localStringBuilder.append(' ');
/*     */       }
/* 570 */       localStringBuilder.append(paramString);
/* 571 */       localStringBuilder.append('\n');
/* 572 */       this.out.write(localStringBuilder.toString());
/*     */     }
/*     */     catch (IOException localIOException) {
/* 575 */       getExceptionListener().exceptionThrown(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void outputValue(Object paramObject1, Object paramObject2, boolean paramBoolean) {
/* 580 */     if (paramObject1 == null) {
/* 581 */       writeln("<null/>");
/* 582 */       return;
/*     */     }
/*     */ 
/* 585 */     if ((paramObject1 instanceof Class)) {
/* 586 */       writeln("<class>" + ((Class)paramObject1).getName() + "</class>");
/* 587 */       return;
/*     */     }
/*     */ 
/* 590 */     ValueData localValueData = getValueData(paramObject1);
/* 591 */     if (localValueData.exp != null) {
/* 592 */       Object localObject1 = localValueData.exp.getTarget();
/* 593 */       String str1 = localValueData.exp.getMethodName();
/*     */ 
/* 595 */       if ((localObject1 == null) || (str1 == null)) {
/* 596 */         throw new NullPointerException((localObject1 == null ? "target" : "methodName") + " should not be null");
/*     */       }
/*     */ 
/* 600 */       if (((localObject1 instanceof Field)) && (str1.equals("get"))) {
/* 601 */         localObject2 = (Field)localObject1;
/* 602 */         writeln("<object class=" + quote(((Field)localObject2).getDeclaringClass().getName()) + " field=" + quote(((Field)localObject2).getName()) + "/>");
/*     */ 
/* 604 */         return;
/*     */       }
/*     */ 
/* 607 */       Object localObject2 = ReflectionUtils.primitiveTypeFor(paramObject1.getClass());
/* 608 */       if ((localObject2 != null) && (localObject1 == paramObject1.getClass()) && (str1.equals("new")))
/*     */       {
/* 610 */         String str2 = ((Class)localObject2).getName();
/*     */ 
/* 612 */         if (localObject2 == Character.TYPE) {
/* 613 */           char c = ((Character)paramObject1).charValue();
/* 614 */           if (!isValidCharCode(c)) {
/* 615 */             writeln(createString(c));
/* 616 */             return;
/*     */           }
/* 618 */           paramObject1 = quoteCharCode(c);
/* 619 */           if (paramObject1 == null) {
/* 620 */             paramObject1 = Character.valueOf(c);
/*     */           }
/*     */         }
/* 623 */         writeln("<" + str2 + ">" + paramObject1 + "</" + str2 + ">");
/*     */ 
/* 625 */         return;
/*     */       }
/*     */     }
/* 628 */     else if ((paramObject1 instanceof String)) {
/* 629 */       writeln(createString((String)paramObject1));
/* 630 */       return;
/*     */     }
/*     */ 
/* 633 */     if (localValueData.name != null) {
/* 634 */       if (paramBoolean) {
/* 635 */         writeln("<object idref=" + quote(localValueData.name) + "/>");
/*     */       }
/*     */       else {
/* 638 */         outputXML("void", " idref=" + quote(localValueData.name), paramObject1, new Object[0]);
/*     */       }
/*     */     }
/* 641 */     else if (localValueData.exp != null)
/* 642 */       outputStatement(localValueData.exp, paramObject2, paramBoolean);
/*     */   }
/*     */ 
/*     */   private static String quoteCharCode(int paramInt)
/*     */   {
/* 647 */     switch (paramInt) { case 38:
/* 648 */       return "&amp;";
/*     */     case 60:
/* 649 */       return "&lt;";
/*     */     case 62:
/* 650 */       return "&gt;";
/*     */     case 34:
/* 651 */       return "&quot;";
/*     */     case 39:
/* 652 */       return "&apos;";
/*     */     case 13:
/* 653 */       return "&#13;"; }
/* 654 */     return null;
/*     */   }
/*     */ 
/*     */   private static String createString(int paramInt)
/*     */   {
/* 659 */     return "<char code=\"#" + Integer.toString(paramInt, 16) + "\"/>";
/*     */   }
/*     */ 
/*     */   private String createString(String paramString) {
/* 663 */     StringBuilder localStringBuilder = new StringBuilder();
/* 664 */     localStringBuilder.append("<string>");
/* 665 */     int i = 0;
/* 666 */     while (i < paramString.length()) {
/* 667 */       int j = paramString.codePointAt(i);
/* 668 */       int k = Character.charCount(j);
/*     */ 
/* 670 */       if ((isValidCharCode(j)) && (this.encoder.canEncode(paramString.substring(i, i + k)))) {
/* 671 */         String str = quoteCharCode(j);
/* 672 */         if (str != null)
/* 673 */           localStringBuilder.append(str);
/*     */         else {
/* 675 */           localStringBuilder.appendCodePoint(j);
/*     */         }
/* 677 */         i += k;
/*     */       } else {
/* 679 */         localStringBuilder.append(createString(paramString.charAt(i)));
/* 680 */         i++;
/*     */       }
/*     */     }
/* 683 */     localStringBuilder.append("</string>");
/* 684 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   private void outputStatement(Statement paramStatement, Object paramObject, boolean paramBoolean) {
/* 688 */     Object localObject1 = paramStatement.getTarget();
/* 689 */     String str1 = paramStatement.getMethodName();
/*     */ 
/* 691 */     if ((localObject1 == null) || (str1 == null)) {
/* 692 */       throw new NullPointerException((localObject1 == null ? "target" : "methodName") + " should not be null");
/*     */     }
/*     */ 
/* 696 */     Object[] arrayOfObject = paramStatement.getArguments();
/* 697 */     int i = paramStatement.getClass() == Expression.class ? 1 : 0;
/* 698 */     Object localObject2 = i != 0 ? getValue((Expression)paramStatement) : null;
/*     */ 
/* 700 */     String str2 = (i != 0) && (paramBoolean) ? "object" : "void";
/* 701 */     String str3 = "";
/* 702 */     ValueData localValueData = getValueData(localObject2);
/*     */     Object localObject3;
/* 705 */     if (localObject1 != paramObject)
/*     */     {
/* 707 */       if ((localObject1 == Array.class) && (str1.equals("newInstance"))) {
/* 708 */         str2 = "array";
/* 709 */         str3 = str3 + " class=" + quote(((Class)arrayOfObject[0]).getName());
/* 710 */         str3 = str3 + " length=" + quote(arrayOfObject[1].toString());
/* 711 */         arrayOfObject = new Object[0];
/*     */       }
/* 713 */       else if (localObject1.getClass() == Class.class) {
/* 714 */         str3 = str3 + " class=" + quote(((Class)localObject1).getName());
/*     */       }
/*     */       else {
/* 717 */         localValueData.refs = 2;
/* 718 */         if (localValueData.name == null) {
/* 719 */           getValueData(localObject1).refs += 1;
/* 720 */           localObject3 = statementList(localObject1);
/* 721 */           if (!((List)localObject3).contains(paramStatement)) {
/* 722 */             ((List)localObject3).add(paramStatement);
/*     */           }
/* 724 */           outputValue(localObject1, paramObject, false);
/*     */         }
/* 726 */         if (i != 0) {
/* 727 */           outputValue(localObject2, paramObject, paramBoolean);
/*     */         }
/* 729 */         return;
/*     */       }
/*     */     }
/* 731 */     if ((i != 0) && (localValueData.refs > 1)) {
/* 732 */       localObject3 = this.nameGenerator.instanceName(localObject2);
/* 733 */       localValueData.name = ((String)localObject3);
/* 734 */       str3 = str3 + " id=" + quote((String)localObject3);
/*     */     }
/*     */ 
/* 738 */     if (((i == 0) && (str1.equals("set")) && (arrayOfObject.length == 2) && ((arrayOfObject[0] instanceof Integer))) || ((i != 0) && (str1.equals("get")) && (arrayOfObject.length == 1) && ((arrayOfObject[0] instanceof Integer))))
/*     */     {
/* 742 */       str3 = str3 + " index=" + quote(arrayOfObject[0].toString());
/* 743 */       arrayOfObject = new Object[] { arrayOfObject.length == 1 ? new Object[0] : arrayOfObject[1] };
/*     */     }
/* 745 */     else if (((i == 0) && (str1.startsWith("set")) && (arrayOfObject.length == 1)) || ((i != 0) && (str1.startsWith("get")) && (arrayOfObject.length == 0)))
/*     */     {
/* 747 */       if (3 < str1.length()) {
/* 748 */         str3 = str3 + " property=" + quote(Introspector.decapitalize(str1.substring(3)));
/*     */       }
/*     */ 
/*     */     }
/* 752 */     else if ((!str1.equals("new")) && (!str1.equals("newInstance"))) {
/* 753 */       str3 = str3 + " method=" + quote(str1);
/*     */     }
/* 755 */     outputXML(str2, str3, localObject2, arrayOfObject);
/*     */   }
/*     */ 
/*     */   private void outputXML(String paramString1, String paramString2, Object paramObject, Object[] paramArrayOfObject) {
/* 759 */     List localList = statementList(paramObject);
/*     */ 
/* 761 */     if ((paramArrayOfObject.length == 0) && (localList.size() == 0)) {
/* 762 */       writeln("<" + paramString1 + paramString2 + "/>");
/* 763 */       return;
/*     */     }
/*     */ 
/* 766 */     writeln("<" + paramString1 + paramString2 + ">");
/* 767 */     this.indentation += 1;
/*     */ 
/* 769 */     for (int i = 0; i < paramArrayOfObject.length; i++) {
/* 770 */       outputValue(paramArrayOfObject[i], null, true);
/*     */     }
/*     */ 
/* 773 */     while (!localList.isEmpty()) {
/* 774 */       Statement localStatement = (Statement)localList.remove(0);
/* 775 */       outputStatement(localStatement, paramObject, false);
/*     */     }
/*     */ 
/* 778 */     this.indentation -= 1;
/* 779 */     writeln("</" + paramString1 + ">");
/*     */   }
/*     */ 
/*     */   private class ValueData
/*     */   {
/* 223 */     public int refs = 0;
/* 224 */     public boolean marked = false;
/* 225 */     public String name = null;
/* 226 */     public Expression exp = null;
/*     */ 
/*     */     private ValueData()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.XMLEncoder
 * JD-Core Version:    0.6.2
 */