/*     */ package com.sun.corba.se.spi.orb;
/*     */ 
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.impl.orbutil.ORBClassLoader;
/*     */ import java.lang.reflect.Array;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Arrays;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public abstract class OperationFactory
/*     */ {
/* 181 */   private static Operation suffixActionImpl = new SuffixAction(null);
/*     */ 
/* 193 */   private static Operation valueActionImpl = new ValueAction(null);
/*     */ 
/* 205 */   private static Operation identityActionImpl = new IdentityAction(null);
/*     */ 
/* 217 */   private static Operation booleanActionImpl = new BooleanAction(null);
/*     */ 
/* 229 */   private static Operation integerActionImpl = new IntegerAction(null);
/*     */ 
/* 241 */   private static Operation stringActionImpl = new StringAction(null);
/*     */ 
/* 262 */   private static Operation classActionImpl = new ClassAction(null);
/*     */ 
/* 274 */   private static Operation setFlagActionImpl = new SetFlagAction(null);
/*     */ 
/* 293 */   private static Operation URLActionImpl = new URLAction(null);
/*     */ 
/* 559 */   private static Operation convertIntegerToShortImpl = new ConvertIntegerToShort(null);
/*     */ 
/*     */   private static String getString(Object paramObject)
/*     */   {
/*  75 */     if ((paramObject instanceof String)) {
/*  76 */       return (String)paramObject;
/*     */     }
/*  78 */     throw new Error("String expected");
/*     */   }
/*     */ 
/*     */   private static Object[] getObjectArray(Object paramObject)
/*     */   {
/*  83 */     if ((paramObject instanceof Object[])) {
/*  84 */       return (Object[])paramObject;
/*     */     }
/*  86 */     throw new Error("Object[] expected");
/*     */   }
/*     */ 
/*     */   private static StringPair getStringPair(Object paramObject)
/*     */   {
/*  91 */     if ((paramObject instanceof StringPair)) {
/*  92 */       return (StringPair)paramObject;
/*     */     }
/*  94 */     throw new Error("StringPair expected");
/*     */   }
/*     */ 
/*     */   public static Operation maskErrorAction(Operation paramOperation)
/*     */   {
/* 143 */     return new MaskErrorAction(paramOperation);
/*     */   }
/*     */ 
/*     */   public static Operation indexAction(int paramInt)
/*     */   {
/* 168 */     return new IndexAction(paramInt);
/*     */   }
/*     */ 
/*     */   public static Operation identityAction()
/*     */   {
/* 297 */     return identityActionImpl;
/*     */   }
/*     */ 
/*     */   public static Operation suffixAction()
/*     */   {
/* 302 */     return suffixActionImpl;
/*     */   }
/*     */ 
/*     */   public static Operation valueAction()
/*     */   {
/* 307 */     return valueActionImpl;
/*     */   }
/*     */ 
/*     */   public static Operation booleanAction()
/*     */   {
/* 312 */     return booleanActionImpl;
/*     */   }
/*     */ 
/*     */   public static Operation integerAction()
/*     */   {
/* 317 */     return integerActionImpl;
/*     */   }
/*     */ 
/*     */   public static Operation stringAction()
/*     */   {
/* 322 */     return stringActionImpl;
/*     */   }
/*     */ 
/*     */   public static Operation classAction()
/*     */   {
/* 327 */     return classActionImpl;
/*     */   }
/*     */ 
/*     */   public static Operation setFlagAction()
/*     */   {
/* 332 */     return setFlagActionImpl;
/*     */   }
/*     */ 
/*     */   public static Operation URLAction()
/*     */   {
/* 337 */     return URLActionImpl;
/*     */   }
/*     */ 
/*     */   public static Operation integerRangeAction(int paramInt1, int paramInt2)
/*     */   {
/* 369 */     return new IntegerRangeAction(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public static Operation listAction(String paramString, Operation paramOperation)
/*     */   {
/* 412 */     return new ListAction(paramString, paramOperation);
/*     */   }
/*     */ 
/*     */   public static Operation sequenceAction(String paramString, Operation[] paramArrayOfOperation)
/*     */   {
/* 457 */     return new SequenceAction(paramString, paramArrayOfOperation);
/*     */   }
/*     */ 
/*     */   public static Operation compose(Operation paramOperation1, Operation paramOperation2)
/*     */   {
/* 483 */     return new ComposeAction(paramOperation1, paramOperation2);
/*     */   }
/*     */ 
/*     */   public static Operation mapAction(Operation paramOperation)
/*     */   {
/* 511 */     return new MapAction(paramOperation);
/*     */   }
/*     */ 
/*     */   public static Operation mapSequenceAction(Operation[] paramArrayOfOperation)
/*     */   {
/* 543 */     return new MapSequenceAction(paramArrayOfOperation);
/*     */   }
/*     */ 
/*     */   public static Operation convertIntegerToShort()
/*     */   {
/* 563 */     return convertIntegerToShortImpl;
/*     */   }
/*     */ 
/*     */   private static class BooleanAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private BooleanAction()
/*     */     {
/* 207 */       super();
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject) {
/* 211 */       return new Boolean(OperationFactory.getString(paramObject));
/*     */     }
/*     */     public String toString() {
/* 214 */       return "booleanAction";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ClassAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private ClassAction()
/*     */     {
/* 243 */       super();
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject) {
/* 247 */       String str = OperationFactory.getString(paramObject);
/*     */       try
/*     */       {
/* 250 */         return ORBClassLoader.loadClass(str);
/*     */       }
/*     */       catch (Exception localException) {
/* 253 */         ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get("orb.lifecycle");
/*     */ 
/* 255 */         throw localORBUtilSystemException.couldNotLoadClass(localException, str);
/*     */       }
/*     */     }
/*     */ 
/* 259 */     public String toString() { return "classAction"; }
/*     */ 
/*     */   }
/*     */ 
/*     */   private static class ComposeAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private Operation op1;
/*     */     private Operation op2;
/*     */ 
/*     */     ComposeAction(Operation paramOperation1, Operation paramOperation2)
/*     */     {
/* 466 */       super();
/* 467 */       this.op1 = paramOperation1;
/* 468 */       this.op2 = paramOperation2;
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject)
/*     */     {
/* 473 */       return this.op2.operate(this.op1.operate(paramObject));
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 477 */       return "composition(" + this.op1 + "," + this.op2 + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ConvertIntegerToShort extends OperationFactory.OperationBase
/*     */   {
/*     */     private ConvertIntegerToShort()
/*     */     {
/* 546 */       super();
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject) {
/* 550 */       Integer localInteger = (Integer)paramObject;
/* 551 */       return new Short(localInteger.shortValue());
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 555 */       return "ConvertIntegerToShort";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class IdentityAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private IdentityAction()
/*     */     {
/* 195 */       super();
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject) {
/* 199 */       return paramObject;
/*     */     }
/*     */     public String toString() {
/* 202 */       return "identityAction";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class IndexAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private int index;
/*     */ 
/*     */     public IndexAction(int paramInt)
/*     */     {
/* 151 */       super();
/* 152 */       this.index = paramInt;
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject)
/*     */     {
/* 157 */       return OperationFactory.getObjectArray(paramObject)[this.index];
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 162 */       return "indexAction(" + this.index + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class IntegerAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private IntegerAction()
/*     */     {
/* 219 */       super();
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject) {
/* 223 */       return new Integer(OperationFactory.getString(paramObject));
/*     */     }
/*     */     public String toString() {
/* 226 */       return "integerAction";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class IntegerRangeAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private int min;
/*     */     private int max;
/*     */ 
/*     */     IntegerRangeAction(int paramInt1, int paramInt2)
/*     */     {
/* 346 */       super();
/* 347 */       this.min = paramInt1;
/* 348 */       this.max = paramInt2;
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject)
/*     */     {
/* 353 */       int i = Integer.parseInt(OperationFactory.getString(paramObject));
/* 354 */       if ((i >= this.min) && (i <= this.max)) {
/* 355 */         return new Integer(i);
/*     */       }
/* 357 */       throw new IllegalArgumentException("Property value " + i + " is not in the range " + this.min + " to " + this.max);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 363 */       return "integerRangeAction(" + this.min + "," + this.max + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ListAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private String sep;
/*     */     private Operation act;
/*     */ 
/*     */     ListAction(String paramString, Operation paramOperation)
/*     */     {
/* 377 */       super();
/* 378 */       this.sep = paramString;
/* 379 */       this.act = paramOperation;
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject)
/*     */     {
/* 388 */       StringTokenizer localStringTokenizer = new StringTokenizer(OperationFactory.getString(paramObject), this.sep);
/*     */ 
/* 390 */       int i = localStringTokenizer.countTokens();
/* 391 */       Object localObject1 = null;
/* 392 */       int j = 0;
/* 393 */       while (localStringTokenizer.hasMoreTokens()) {
/* 394 */         String str = localStringTokenizer.nextToken();
/* 395 */         Object localObject2 = this.act.operate(str);
/* 396 */         if (localObject1 == null)
/* 397 */           localObject1 = Array.newInstance(localObject2.getClass(), i);
/* 398 */         Array.set(localObject1, j++, localObject2);
/*     */       }
/*     */ 
/* 401 */       return localObject1;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 405 */       return "listAction(separator=\"" + this.sep + "\",action=" + this.act + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MapAction extends OperationFactory.OperationBase
/*     */   {
/*     */     Operation op;
/*     */ 
/*     */     MapAction(Operation paramOperation)
/*     */     {
/* 491 */       super();
/* 492 */       this.op = paramOperation;
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject)
/*     */     {
/* 497 */       Object[] arrayOfObject1 = (Object[])paramObject;
/* 498 */       Object[] arrayOfObject2 = new Object[arrayOfObject1.length];
/* 499 */       for (int i = 0; i < arrayOfObject1.length; i++)
/* 500 */         arrayOfObject2[i] = this.op.operate(arrayOfObject1[i]);
/* 501 */       return arrayOfObject2;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 505 */       return "mapAction(" + this.op + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MapSequenceAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private Operation[] op;
/*     */ 
/*     */     public MapSequenceAction(Operation[] paramArrayOfOperation)
/*     */     {
/* 519 */       super();
/* 520 */       this.op = paramArrayOfOperation;
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject)
/*     */     {
/* 528 */       Object[] arrayOfObject1 = (Object[])paramObject;
/* 529 */       Object[] arrayOfObject2 = new Object[arrayOfObject1.length];
/* 530 */       for (int i = 0; i < arrayOfObject1.length; i++)
/* 531 */         arrayOfObject2[i] = this.op[i].operate(arrayOfObject1[i]);
/* 532 */       return arrayOfObject2;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 536 */       return "mapSequenceAction(" + Arrays.toString(this.op) + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MaskErrorAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private Operation op;
/*     */ 
/*     */     public MaskErrorAction(Operation paramOperation)
/*     */     {
/* 122 */       super();
/* 123 */       this.op = paramOperation;
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject)
/*     */     {
/*     */       try {
/* 129 */         return this.op.operate(paramObject); } catch (Exception localException) {
/*     */       }
/* 131 */       return null;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 137 */       return "maskErrorAction(" + this.op + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class OperationBase
/*     */     implements Operation
/*     */   {
/*     */     public boolean equals(Object paramObject)
/*     */     {
/* 100 */       if (this == paramObject) {
/* 101 */         return true;
/*     */       }
/* 103 */       if (!(paramObject instanceof OperationBase)) {
/* 104 */         return false;
/*     */       }
/* 106 */       OperationBase localOperationBase = (OperationBase)paramObject;
/*     */ 
/* 108 */       return toString().equals(localOperationBase.toString());
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 113 */       return toString().hashCode();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SequenceAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private String sep;
/*     */     private Operation[] actions;
/*     */ 
/*     */     SequenceAction(String paramString, Operation[] paramArrayOfOperation)
/*     */     {
/* 421 */       super();
/* 422 */       this.sep = paramString;
/* 423 */       this.actions = paramArrayOfOperation;
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject)
/*     */     {
/* 428 */       StringTokenizer localStringTokenizer = new StringTokenizer(OperationFactory.getString(paramObject), this.sep);
/*     */ 
/* 431 */       int i = localStringTokenizer.countTokens();
/* 432 */       if (i != this.actions.length) {
/* 433 */         throw new Error("Number of tokens and number of actions do not match");
/*     */       }
/*     */ 
/* 436 */       int j = 0;
/* 437 */       Object[] arrayOfObject = new Object[i];
/* 438 */       while (localStringTokenizer.hasMoreTokens()) {
/* 439 */         Operation localOperation = this.actions[j];
/* 440 */         String str = localStringTokenizer.nextToken();
/* 441 */         arrayOfObject[(j++)] = localOperation.operate(str);
/*     */       }
/*     */ 
/* 444 */       return arrayOfObject;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 448 */       return "sequenceAction(separator=\"" + this.sep + "\",actions=" + Arrays.toString(this.actions) + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SetFlagAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private SetFlagAction()
/*     */     {
/* 264 */       super();
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject) {
/* 268 */       return Boolean.TRUE;
/*     */     }
/*     */     public String toString() {
/* 271 */       return "setFlagAction";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class StringAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private StringAction()
/*     */     {
/* 231 */       super();
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject) {
/* 235 */       return paramObject;
/*     */     }
/*     */     public String toString() {
/* 238 */       return "stringAction";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SuffixAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private SuffixAction()
/*     */     {
/* 171 */       super();
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject) {
/* 175 */       return OperationFactory.getStringPair(paramObject).getFirst();
/*     */     }
/*     */     public String toString() {
/* 178 */       return "suffixAction";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class URLAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private URLAction()
/*     */     {
/* 276 */       super();
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject) {
/* 280 */       String str = (String)paramObject;
/*     */       try {
/* 282 */         return new URL(str);
/*     */       } catch (MalformedURLException localMalformedURLException) {
/* 284 */         ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get("orb.lifecycle");
/*     */ 
/* 286 */         throw localORBUtilSystemException.badUrl(localMalformedURLException, str);
/*     */       }
/*     */     }
/*     */ 
/* 290 */     public String toString() { return "URLAction"; }
/*     */ 
/*     */   }
/*     */ 
/*     */   private static class ValueAction extends OperationFactory.OperationBase
/*     */   {
/*     */     private ValueAction()
/*     */     {
/* 183 */       super();
/*     */     }
/*     */ 
/*     */     public Object operate(Object paramObject) {
/* 187 */       return OperationFactory.getStringPair(paramObject).getSecond();
/*     */     }
/*     */     public String toString() {
/* 190 */       return "valueAction";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orb.OperationFactory
 * JD-Core Version:    0.6.2
 */