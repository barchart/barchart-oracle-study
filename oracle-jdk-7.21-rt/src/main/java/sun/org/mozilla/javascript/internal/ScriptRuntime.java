/*      */ package sun.org.mozilla.javascript.internal;
/*      */ 
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.Locale;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import sun.org.mozilla.javascript.internal.xml.XMLLib;
/*      */ import sun.org.mozilla.javascript.internal.xml.XMLLib.Factory;
/*      */ import sun.org.mozilla.javascript.internal.xml.XMLObject;
/*      */ 
/*      */ public class ScriptRuntime
/*      */ {
/*   99 */   private static BaseFunction THROW_TYPE_ERROR = null;
/*      */ 
/*  141 */   public static final Class<?> BooleanClass = Kit.classOrNull("java.lang.Boolean");
/*  142 */   public static final Class<?> ByteClass = Kit.classOrNull("java.lang.Byte");
/*  143 */   public static final Class<?> CharacterClass = Kit.classOrNull("java.lang.Character");
/*  144 */   public static final Class<?> ClassClass = Kit.classOrNull("java.lang.Class");
/*  145 */   public static final Class<?> DoubleClass = Kit.classOrNull("java.lang.Double");
/*  146 */   public static final Class<?> FloatClass = Kit.classOrNull("java.lang.Float");
/*  147 */   public static final Class<?> IntegerClass = Kit.classOrNull("java.lang.Integer");
/*  148 */   public static final Class<?> LongClass = Kit.classOrNull("java.lang.Long");
/*  149 */   public static final Class<?> NumberClass = Kit.classOrNull("java.lang.Number");
/*  150 */   public static final Class<?> ObjectClass = Kit.classOrNull("java.lang.Object");
/*  151 */   public static final Class<?> ShortClass = Kit.classOrNull("java.lang.Short");
/*  152 */   public static final Class<?> StringClass = Kit.classOrNull("java.lang.String");
/*  153 */   public static final Class<?> DateClass = Kit.classOrNull("java.util.Date");
/*      */ 
/*  156 */   public static final Class<?> ContextClass = Kit.classOrNull("sun.org.mozilla.javascript.internal.Context");
/*      */ 
/*  158 */   public static final Class<?> ContextFactoryClass = Kit.classOrNull("sun.org.mozilla.javascript.internal.ContextFactory");
/*      */ 
/*  160 */   public static final Class<?> FunctionClass = Kit.classOrNull("sun.org.mozilla.javascript.internal.Function");
/*      */ 
/*  162 */   public static final Class<?> ScriptableObjectClass = Kit.classOrNull("sun.org.mozilla.javascript.internal.ScriptableObject");
/*      */ 
/*  164 */   public static final Class<Scriptable> ScriptableClass = Scriptable.class;
/*      */ 
/*  167 */   private static final String[] lazilyNames = { "RegExp", "sun.org.mozilla.javascript.internal.regexp.NativeRegExp", "Packages", "sun.org.mozilla.javascript.internal.NativeJavaTopPackage", "java", "sun.org.mozilla.javascript.internal.NativeJavaTopPackage", "javax", "sun.org.mozilla.javascript.internal.NativeJavaTopPackage", "org", "sun.org.mozilla.javascript.internal.NativeJavaTopPackage", "com", "sun.org.mozilla.javascript.internal.NativeJavaTopPackage", "edu", "sun.org.mozilla.javascript.internal.NativeJavaTopPackage", "net", "sun.org.mozilla.javascript.internal.NativeJavaTopPackage", "getClass", "sun.org.mozilla.javascript.internal.NativeJavaTopPackage", "JavaAdapter", "sun.org.mozilla.javascript.internal.JavaAdapter", "JavaImporter", "sun.org.mozilla.javascript.internal.ImporterTopLevel", "Continuation", "sun.org.mozilla.javascript.internal.NativeContinuation", "XML", "(xml)", "XMLList", "(xml)", "Namespace", "(xml)", "QName", "(xml)" };
/*      */ 
/*  189 */   public static Locale ROOT_LOCALE = new Locale("");
/*      */ 
/*  191 */   private static final Object LIBRARY_SCOPE_KEY = "LIBRARY_SCOPE";
/*      */ 
/*  421 */   public static final double NaN = Double.longBitsToDouble(9221120237041090560L);
/*      */ 
/*  425 */   public static final double negativeZero = Double.longBitsToDouble(-9223372036854775808L);
/*      */ 
/*  427 */   public static final Double NaNobj = new Double(NaN);
/*      */   private static final boolean MSJVM_BUG_WORKAROUNDS = true;
/*      */   private static final String DEFAULT_NS_TAG = "__default_namespace__";
/*      */   public static final int ENUMERATE_KEYS = 0;
/*      */   public static final int ENUMERATE_VALUES = 1;
/*      */   public static final int ENUMERATE_ARRAY = 2;
/*      */   public static final int ENUMERATE_KEYS_NO_ITERATOR = 3;
/*      */   public static final int ENUMERATE_VALUES_NO_ITERATOR = 4;
/*      */   public static final int ENUMERATE_ARRAY_NO_ITERATOR = 5;
/* 3695 */   public static MessageProvider messageProvider = new DefaultMessageProvider(null);
/*      */ 
/* 4062 */   public static final Object[] emptyArgs = new Object[0];
/* 4063 */   public static final String[] emptyStrings = new String[0];
/*      */ 
/*      */   public static BaseFunction typeErrorThrower()
/*      */   {
/*   83 */     if (THROW_TYPE_ERROR == null) {
/*   84 */       BaseFunction local1 = new BaseFunction()
/*      */       {
/*      */         public Object call(Context paramAnonymousContext, Scriptable paramAnonymousScriptable1, Scriptable paramAnonymousScriptable2, Object[] paramAnonymousArrayOfObject) {
/*   87 */           throw ScriptRuntime.typeError0("msg.op.not.allowed");
/*      */         }
/*      */ 
/*      */         public int getLength() {
/*   91 */           return 0;
/*      */         }
/*      */       };
/*   94 */       local1.preventExtensions();
/*   95 */       THROW_TYPE_ERROR = local1;
/*      */     }
/*   97 */     return THROW_TYPE_ERROR;
/*      */   }
/*      */ 
/*      */   public static boolean isRhinoRuntimeType(Class<?> paramClass)
/*      */   {
/*  195 */     if (paramClass.isPrimitive()) {
/*  196 */       return paramClass != Character.TYPE;
/*      */     }
/*  198 */     return (paramClass == StringClass) || (paramClass == BooleanClass) || (NumberClass.isAssignableFrom(paramClass)) || (ScriptableClass.isAssignableFrom(paramClass));
/*      */   }
/*      */ 
/*      */   public static ScriptableObject initStandardObjects(Context paramContext, ScriptableObject paramScriptableObject, boolean paramBoolean)
/*      */   {
/*  208 */     if (paramScriptableObject == null) {
/*  209 */       paramScriptableObject = new NativeObject();
/*      */     }
/*  211 */     paramScriptableObject.associateValue(LIBRARY_SCOPE_KEY, paramScriptableObject);
/*  212 */     new ClassCache().associate(paramScriptableObject);
/*      */ 
/*  214 */     BaseFunction.init(paramScriptableObject, paramBoolean);
/*  215 */     NativeObject.init(paramScriptableObject, paramBoolean);
/*      */ 
/*  217 */     Scriptable localScriptable1 = ScriptableObject.getObjectPrototype(paramScriptableObject);
/*      */ 
/*  220 */     Scriptable localScriptable2 = ScriptableObject.getFunctionPrototype(paramScriptableObject);
/*  221 */     localScriptable2.setPrototype(localScriptable1);
/*      */ 
/*  224 */     if (paramScriptableObject.getPrototype() == null) {
/*  225 */       paramScriptableObject.setPrototype(localScriptable1);
/*      */     }
/*      */ 
/*  228 */     NativeError.init(paramScriptableObject, paramBoolean);
/*  229 */     NativeGlobal.init(paramContext, paramScriptableObject, paramBoolean);
/*      */ 
/*  231 */     NativeArray.init(paramScriptableObject, paramBoolean);
/*  232 */     if (paramContext.getOptimizationLevel() > 0)
/*      */     {
/*  236 */       NativeArray.setMaximumInitialCapacity(200000);
/*      */     }
/*  238 */     NativeString.init(paramScriptableObject, paramBoolean);
/*  239 */     NativeBoolean.init(paramScriptableObject, paramBoolean);
/*  240 */     NativeNumber.init(paramScriptableObject, paramBoolean);
/*  241 */     NativeDate.init(paramContext, paramScriptableObject, paramBoolean);
/*  242 */     NativeMath.init(paramScriptableObject, paramBoolean);
/*  243 */     NativeJSON.init(paramScriptableObject, paramBoolean);
/*      */ 
/*  245 */     NativeWith.init(paramScriptableObject, paramBoolean);
/*  246 */     NativeCall.init(paramScriptableObject, paramBoolean);
/*  247 */     NativeScript.init(paramScriptableObject, paramBoolean);
/*      */ 
/*  249 */     NativeIterator.init(paramScriptableObject, paramBoolean);
/*      */ 
/*  251 */     int i = (paramContext.hasFeature(6)) && (paramContext.getE4xImplementationFactory() != null) ? 1 : 0;
/*      */ 
/*  254 */     for (int j = 0; j != lazilyNames.length; j += 2) {
/*  255 */       String str1 = lazilyNames[j];
/*  256 */       String str2 = lazilyNames[(j + 1)];
/*  257 */       if ((i != 0) || (!str2.equals("(xml)")))
/*      */       {
/*  259 */         if ((i != 0) && (str2.equals("(xml)"))) {
/*  260 */           str2 = paramContext.getE4xImplementationFactory().getImplementationClassName();
/*      */         }
/*      */ 
/*  263 */         new LazilyLoadedCtor(paramScriptableObject, str1, str2, paramBoolean, true);
/*      */       }
/*      */     }
/*  266 */     return paramScriptableObject;
/*      */   }
/*      */ 
/*      */   public static ScriptableObject getLibraryScopeOrNull(Scriptable paramScriptable)
/*      */   {
/*  272 */     ScriptableObject localScriptableObject = (ScriptableObject)ScriptableObject.getTopScopeValue(paramScriptable, LIBRARY_SCOPE_KEY);
/*      */ 
/*  274 */     return localScriptableObject;
/*      */   }
/*      */ 
/*      */   public static boolean isJSLineTerminator(int paramInt)
/*      */   {
/*  282 */     if ((paramInt & 0xDFD0) != 0) {
/*  283 */       return false;
/*      */     }
/*  285 */     return (paramInt == 10) || (paramInt == 13) || (paramInt == 8232) || (paramInt == 8233);
/*      */   }
/*      */ 
/*      */   public static boolean isJSWhitespaceOrLineTerminator(int paramInt) {
/*  289 */     return (isStrWhiteSpaceChar(paramInt)) || (isJSLineTerminator(paramInt));
/*      */   }
/*      */ 
/*      */   static boolean isStrWhiteSpaceChar(int paramInt)
/*      */   {
/*  309 */     switch (paramInt) {
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 32:
/*      */     case 160:
/*      */     case 8232:
/*      */     case 8233:
/*      */     case 65279:
/*  320 */       return true;
/*      */     }
/*  322 */     return Character.getType(paramInt) == 12;
/*      */   }
/*      */ 
/*      */   public static Boolean wrapBoolean(boolean paramBoolean)
/*      */   {
/*  328 */     return paramBoolean ? Boolean.TRUE : Boolean.FALSE;
/*      */   }
/*      */ 
/*      */   public static Integer wrapInt(int paramInt)
/*      */   {
/*  333 */     return Integer.valueOf(paramInt);
/*      */   }
/*      */ 
/*      */   public static Number wrapNumber(double paramDouble)
/*      */   {
/*  338 */     if (paramDouble != paramDouble) {
/*  339 */       return NaNobj;
/*      */     }
/*  341 */     return new Double(paramDouble);
/*      */   }
/*      */ 
/*      */   public static boolean toBoolean(Object paramObject)
/*      */   {
/*      */     do
/*      */     {
/*  352 */       if ((paramObject instanceof Boolean))
/*  353 */         return ((Boolean)paramObject).booleanValue();
/*  354 */       if ((paramObject == null) || (paramObject == Undefined.instance))
/*  355 */         return false;
/*  356 */       if ((paramObject instanceof String))
/*  357 */         return ((String)paramObject).length() != 0;
/*  358 */       if ((paramObject instanceof Number)) {
/*  359 */         double d = ((Number)paramObject).doubleValue();
/*  360 */         return (d == d) && (d != 0.0D);
/*      */       }
/*  362 */       if (!(paramObject instanceof Scriptable)) break;
/*  363 */       if (((paramObject instanceof ScriptableObject)) && (((ScriptableObject)paramObject).avoidObjectDetection()))
/*      */       {
/*  366 */         return false;
/*      */       }
/*  368 */       if (Context.getContext().isVersionECMA1())
/*      */       {
/*  370 */         return true;
/*      */       }
/*      */ 
/*  373 */       paramObject = ((Scriptable)paramObject).getDefaultValue(BooleanClass);
/*  374 */     }while (!(paramObject instanceof Scriptable));
/*  375 */     throw errorWithClassName("msg.primitive.expected", paramObject);
/*      */ 
/*  378 */     warnAboutNonJSObject(paramObject);
/*  379 */     return true;
/*      */   }
/*      */ 
/*      */   public static double toNumber(Object paramObject)
/*      */   {
/*      */     do
/*      */     {
/*  391 */       if ((paramObject instanceof Number))
/*  392 */         return ((Number)paramObject).doubleValue();
/*  393 */       if (paramObject == null)
/*  394 */         return 0.0D;
/*  395 */       if (paramObject == Undefined.instance)
/*  396 */         return NaN;
/*  397 */       if ((paramObject instanceof String))
/*  398 */         return toNumber((String)paramObject);
/*  399 */       if ((paramObject instanceof Boolean))
/*  400 */         return ((Boolean)paramObject).booleanValue() ? 1.0D : 0.0D;
/*  401 */       if (!(paramObject instanceof Scriptable)) break;
/*  402 */       paramObject = ((Scriptable)paramObject).getDefaultValue(NumberClass);
/*  403 */     }while (!(paramObject instanceof Scriptable));
/*  404 */     throw errorWithClassName("msg.primitive.expected", paramObject);
/*      */ 
/*  407 */     warnAboutNonJSObject(paramObject);
/*  408 */     return NaN;
/*      */   }
/*      */ 
/*      */   public static double toNumber(Object[] paramArrayOfObject, int paramInt)
/*      */   {
/*  413 */     return paramInt < paramArrayOfObject.length ? toNumber(paramArrayOfObject[paramInt]) : NaN;
/*      */   }
/*      */ 
/*      */   static double stringToNumber(String paramString, int paramInt1, int paramInt2)
/*      */   {
/*  433 */     int i = 57;
/*  434 */     int j = 97;
/*  435 */     int k = 65;
/*  436 */     int m = paramString.length();
/*  437 */     if (paramInt2 < 10) {
/*  438 */       i = (char)(48 + paramInt2 - 1);
/*      */     }
/*  440 */     if (paramInt2 > 10) {
/*  441 */       j = (char)(97 + paramInt2 - 10);
/*  442 */       k = (char)(65 + paramInt2 - 10);
/*      */     }
/*      */ 
/*  445 */     double d1 = 0.0D;
/*      */     int i3;
/*  446 */     for (int n = paramInt1; n < m; n++) {
/*  447 */       int i1 = paramString.charAt(n);
/*      */ 
/*  449 */       if ((48 <= i1) && (i1 <= i)) {
/*  450 */         i3 = i1 - 48;
/*  451 */       } else if ((97 <= i1) && (i1 < j)) {
/*  452 */         i3 = i1 - 97 + 10; } else {
/*  453 */         if ((65 > i1) || (i1 >= k)) break;
/*  454 */         i3 = i1 - 65 + 10;
/*      */       }
/*      */ 
/*  457 */       d1 = d1 * paramInt2 + i3;
/*      */     }
/*  459 */     if (paramInt1 == n) {
/*  460 */       return NaN;
/*      */     }
/*  462 */     if (d1 >= 9007199254740992.0D) {
/*  463 */       if (paramInt2 == 10)
/*      */       {
/*      */         try
/*      */         {
/*  470 */           return Double.valueOf(paramString.substring(paramInt1, n)).doubleValue();
/*      */         } catch (NumberFormatException localNumberFormatException) {
/*  472 */           return NaN;
/*      */         }
/*      */       }
/*  474 */       if ((paramInt2 == 2) || (paramInt2 == 4) || (paramInt2 == 8) || (paramInt2 == 16) || (paramInt2 == 32))
/*      */       {
/*  487 */         int i2 = 1;
/*  488 */         i3 = 0;
/*      */ 
/*  496 */         int i4 = 0;
/*  497 */         int i5 = 53;
/*  498 */         double d2 = 0.0D;
/*  499 */         int i6 = 0;
/*      */ 
/*  501 */         int i7 = 0;
/*      */         while (true)
/*      */         {
/*  504 */           if (i2 == 1) {
/*  505 */             if (paramInt1 == n)
/*      */               break;
/*  507 */             i3 = paramString.charAt(paramInt1++);
/*  508 */             if ((48 <= i3) && (i3 <= 57))
/*  509 */               i3 -= 48;
/*  510 */             else if ((97 <= i3) && (i3 <= 122))
/*  511 */               i3 -= 87;
/*      */             else
/*  513 */               i3 -= 55;
/*  514 */             i2 = paramInt2;
/*      */           }
/*  516 */           i2 >>= 1;
/*  517 */           int i8 = (i3 & i2) != 0 ? 1 : 0;
/*      */ 
/*  519 */           switch (i4) {
/*      */           case 0:
/*  521 */             if (i8 != 0) {
/*  522 */               i5--;
/*  523 */               d1 = 1.0D;
/*  524 */               i4 = 1; } break;
/*      */           case 1:
/*  528 */             d1 *= 2.0D;
/*  529 */             if (i8 != 0)
/*  530 */               d1 += 1.0D;
/*  531 */             i5--;
/*  532 */             if (i5 == 0) {
/*  533 */               i6 = i8;
/*  534 */               i4 = 2; } break;
/*      */           case 2:
/*  538 */             i7 = i8;
/*  539 */             d2 = 2.0D;
/*  540 */             i4 = 3;
/*  541 */             break;
/*      */           case 3:
/*  543 */             if (i8 != 0) {
/*  544 */               i4 = 4;
/*      */             }
/*      */ 
/*      */           case 4:
/*  548 */             d2 *= 2.0D;
/*      */           }
/*      */         }
/*      */ 
/*  552 */         switch (i4) {
/*      */         case 0:
/*  554 */           d1 = 0.0D;
/*  555 */           break;
/*      */         case 1:
/*      */         case 2:
/*  559 */           break;
/*      */         case 3:
/*  563 */           if ((i7 & i6) != 0)
/*  564 */             d1 += 1.0D;
/*  565 */           d1 *= d2;
/*  566 */           break;
/*      */         case 4:
/*  570 */           if (i7 != 0)
/*  571 */             d1 += 1.0D;
/*  572 */           d1 *= d2;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  578 */     return d1;
/*      */   }
/*      */ 
/*      */   public static double toNumber(String paramString)
/*      */   {
/*  588 */     int i = paramString.length();
/*  589 */     int j = 0;
/*      */     int k;
/*      */     while (true)
/*      */     {
/*  592 */       if (j == i)
/*      */       {
/*  594 */         return 0.0D;
/*      */       }
/*  596 */       k = paramString.charAt(j);
/*  597 */       if (!isStrWhiteSpaceChar(k))
/*      */         break;
/*  599 */       j++;
/*      */     }
/*      */ 
/*  602 */     if (k == 48) {
/*  603 */       if (j + 2 < i) {
/*  604 */         m = paramString.charAt(j + 1);
/*  605 */         if ((m == 120) || (m == 88))
/*      */         {
/*  607 */           return stringToNumber(paramString, j + 2, 16);
/*      */         }
/*      */       }
/*  610 */     } else if (((k == 43) || (k == 45)) && 
/*  611 */       (j + 3 < i) && (paramString.charAt(j + 1) == '0')) {
/*  612 */       m = paramString.charAt(j + 2);
/*  613 */       if ((m == 120) || (m == 88))
/*      */       {
/*  615 */         double d = stringToNumber(paramString, j + 3, 16);
/*  616 */         return k == 45 ? -d : d;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  621 */     int m = i - 1;
/*      */     int n;
/*  623 */     while (isStrWhiteSpaceChar(n = paramString.charAt(m)))
/*  624 */       m--;
/*  625 */     if (n == 121)
/*      */     {
/*  627 */       if ((k == 43) || (k == 45))
/*  628 */         j++;
/*  629 */       if ((j + 7 == m) && (paramString.regionMatches(j, "Infinity", 0, 8))) {
/*  630 */         return k == 45 ? (-1.0D / 0.0D) : (1.0D / 0.0D);
/*      */       }
/*      */ 
/*  633 */       return NaN;
/*      */     }
/*      */ 
/*  637 */     String str = paramString.substring(j, m + 1);
/*      */ 
/*  642 */     for (int i1 = str.length() - 1; i1 >= 0; i1--) {
/*  643 */       int i2 = str.charAt(i1);
/*  644 */       if (((48 > i2) || (i2 > 57)) && (i2 != 46) && (i2 != 101) && (i2 != 69) && (i2 != 43) && (i2 != 45))
/*      */       {
/*  648 */         return NaN;
/*      */       }
/*      */     }
/*      */     try {
/*  652 */       return Double.valueOf(str).doubleValue(); } catch (NumberFormatException localNumberFormatException) {
/*      */     }
/*  654 */     return NaN;
/*      */   }
/*      */ 
/*      */   public static Object[] padArguments(Object[] paramArrayOfObject, int paramInt)
/*      */   {
/*  665 */     if (paramInt < paramArrayOfObject.length) {
/*  666 */       return paramArrayOfObject;
/*      */     }
/*      */ 
/*  669 */     Object[] arrayOfObject = new Object[paramInt];
/*  670 */     for (int i = 0; i < paramArrayOfObject.length; i++) {
/*  671 */       arrayOfObject[i] = paramArrayOfObject[i];
/*      */     }
/*      */ 
/*  674 */     for (; i < paramInt; i++) {
/*  675 */       arrayOfObject[i] = Undefined.instance;
/*      */     }
/*      */ 
/*  678 */     return arrayOfObject;
/*      */   }
/*      */ 
/*      */   public static String escapeString(String paramString)
/*      */   {
/*  686 */     return escapeString(paramString, '"');
/*      */   }
/*      */ 
/*      */   public static String escapeString(String paramString, char paramChar)
/*      */   {
/*  695 */     if ((paramChar != '"') && (paramChar != '\'')) Kit.codeBug();
/*  696 */     StringBuffer localStringBuffer = null;
/*      */ 
/*  698 */     int i = 0; for (int j = paramString.length(); i != j; i++) {
/*  699 */       char c1 = paramString.charAt(i);
/*      */ 
/*  701 */       if ((' ' <= c1) && (c1 <= '~') && (c1 != paramChar) && (c1 != '\\'))
/*      */       {
/*  704 */         if (localStringBuffer != null)
/*  705 */           localStringBuffer.append((char)c1);
/*      */       }
/*      */       else
/*      */       {
/*  709 */         if (localStringBuffer == null) {
/*  710 */           localStringBuffer = new StringBuffer(j + 3);
/*  711 */           localStringBuffer.append(paramString);
/*  712 */           localStringBuffer.setLength(i);
/*      */         }
/*      */ 
/*  715 */         int k = -1;
/*  716 */         switch (c1) { case '\b':
/*  717 */           k = 98; break;
/*      */         case '\f':
/*  718 */           k = 102; break;
/*      */         case '\n':
/*  719 */           k = 110; break;
/*      */         case '\r':
/*  720 */           k = 114; break;
/*      */         case '\t':
/*  721 */           k = 116; break;
/*      */         case '\013':
/*  722 */           k = 118; break;
/*      */         case ' ':
/*  723 */           k = 32; break;
/*      */         case '\\':
/*  724 */           k = 92;
/*      */         }
/*  726 */         if (k >= 0)
/*      */         {
/*  728 */           localStringBuffer.append('\\');
/*  729 */           localStringBuffer.append((char)k);
/*  730 */         } else if (c1 == paramChar) {
/*  731 */           localStringBuffer.append('\\');
/*  732 */           localStringBuffer.append(paramChar);
/*      */         }
/*      */         else
/*      */         {
/*      */           int m;
/*  735 */           if (c1 < 'Ā')
/*      */           {
/*  737 */             localStringBuffer.append("\\x");
/*  738 */             m = 2;
/*      */           }
/*      */           else {
/*  741 */             localStringBuffer.append("\\u");
/*  742 */             m = 4;
/*      */           }
/*      */ 
/*  745 */           for (char c2 = (m - 1) * 4; c2 >= 0; c2 -= 4) {
/*  746 */             int n = 0xF & c1 >> c2;
/*  747 */             int i1 = n < 10 ? 48 + n : 87 + n;
/*  748 */             localStringBuffer.append((char)i1);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  752 */     return localStringBuffer == null ? paramString : localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   static boolean isValidIdentifierName(String paramString)
/*      */   {
/*  757 */     int i = paramString.length();
/*  758 */     if (i == 0)
/*  759 */       return false;
/*  760 */     if (!Character.isJavaIdentifierStart(paramString.charAt(0)))
/*  761 */       return false;
/*  762 */     for (int j = 1; j != i; j++) {
/*  763 */       if (!Character.isJavaIdentifierPart(paramString.charAt(j)))
/*  764 */         return false;
/*      */     }
/*  766 */     return !TokenStream.isKeyword(paramString);
/*      */   }
/*      */ 
/*      */   public static String toString(Object paramObject)
/*      */   {
/*      */     do
/*      */     {
/*  776 */       if (paramObject == null) {
/*  777 */         return "null";
/*      */       }
/*  779 */       if (paramObject == Undefined.instance) {
/*  780 */         return "undefined";
/*      */       }
/*  782 */       if ((paramObject instanceof String)) {
/*  783 */         return (String)paramObject;
/*      */       }
/*  785 */       if ((paramObject instanceof Number))
/*      */       {
/*  788 */         return numberToString(((Number)paramObject).doubleValue(), 10);
/*      */       }
/*  790 */       if (!(paramObject instanceof Scriptable)) break;
/*  791 */       paramObject = ((Scriptable)paramObject).getDefaultValue(StringClass);
/*  792 */     }while (!(paramObject instanceof Scriptable));
/*  793 */     throw errorWithClassName("msg.primitive.expected", paramObject);
/*      */ 
/*  797 */     return paramObject.toString();
/*      */   }
/*      */ 
/*      */   static String defaultObjectToString(Scriptable paramScriptable)
/*      */   {
/*  803 */     return "[object " + paramScriptable.getClassName() + ']';
/*      */   }
/*      */ 
/*      */   public static String toString(Object[] paramArrayOfObject, int paramInt)
/*      */   {
/*  808 */     return paramInt < paramArrayOfObject.length ? toString(paramArrayOfObject[paramInt]) : "undefined";
/*      */   }
/*      */ 
/*      */   public static String toString(double paramDouble)
/*      */   {
/*  815 */     return numberToString(paramDouble, 10);
/*      */   }
/*      */ 
/*      */   public static String numberToString(double paramDouble, int paramInt) {
/*  819 */     if (paramDouble != paramDouble)
/*  820 */       return "NaN";
/*  821 */     if (paramDouble == (1.0D / 0.0D))
/*  822 */       return "Infinity";
/*  823 */     if (paramDouble == (-1.0D / 0.0D))
/*  824 */       return "-Infinity";
/*  825 */     if (paramDouble == 0.0D) {
/*  826 */       return "0";
/*      */     }
/*  828 */     if ((paramInt < 2) || (paramInt > 36)) {
/*  829 */       throw Context.reportRuntimeError1("msg.bad.radix", Integer.toString(paramInt));
/*      */     }
/*      */ 
/*  833 */     if (paramInt != 10) {
/*  834 */       return DToA.JS_dtobasestr(paramInt, paramDouble);
/*      */     }
/*  836 */     StringBuffer localStringBuffer = new StringBuffer();
/*  837 */     DToA.JS_dtostr(localStringBuffer, 0, 0, paramDouble);
/*  838 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   static String uneval(Context paramContext, Scriptable paramScriptable, Object paramObject)
/*      */   {
/*  845 */     if (paramObject == null) {
/*  846 */       return "null";
/*      */     }
/*  848 */     if (paramObject == Undefined.instance)
/*  849 */       return "undefined";
/*      */     Object localObject;
/*  851 */     if ((paramObject instanceof String)) {
/*  852 */       String str = escapeString((String)paramObject);
/*  853 */       localObject = new StringBuffer(str.length() + 2);
/*  854 */       ((StringBuffer)localObject).append('"');
/*  855 */       ((StringBuffer)localObject).append(str);
/*  856 */       ((StringBuffer)localObject).append('"');
/*  857 */       return ((StringBuffer)localObject).toString();
/*      */     }
/*  859 */     if ((paramObject instanceof Number)) {
/*  860 */       double d = ((Number)paramObject).doubleValue();
/*  861 */       if ((d == 0.0D) && (1.0D / d < 0.0D)) {
/*  862 */         return "-0";
/*      */       }
/*  864 */       return toString(d);
/*      */     }
/*  866 */     if ((paramObject instanceof Boolean)) {
/*  867 */       return toString(paramObject);
/*      */     }
/*  869 */     if ((paramObject instanceof Scriptable)) {
/*  870 */       Scriptable localScriptable = (Scriptable)paramObject;
/*      */ 
/*  873 */       if (ScriptableObject.hasProperty(localScriptable, "toSource")) {
/*  874 */         localObject = ScriptableObject.getProperty(localScriptable, "toSource");
/*  875 */         if ((localObject instanceof Function)) {
/*  876 */           Function localFunction = (Function)localObject;
/*  877 */           return toString(localFunction.call(paramContext, paramScriptable, localScriptable, emptyArgs));
/*      */         }
/*      */       }
/*  880 */       return toString(paramObject);
/*      */     }
/*  882 */     warnAboutNonJSObject(paramObject);
/*  883 */     return paramObject.toString();
/*      */   }
/*      */ 
/*      */   static String defaultObjectToSource(Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject)
/*      */   {
/*      */     int i;
/*      */     boolean bool;
/*  890 */     if (paramContext.iterating == null) {
/*  891 */       i = 1;
/*  892 */       bool = false;
/*  893 */       paramContext.iterating = new ObjToIntMap(31);
/*      */     } else {
/*  895 */       i = 0;
/*  896 */       bool = paramContext.iterating.has(paramScriptable2);
/*      */     }
/*      */ 
/*  899 */     StringBuffer localStringBuffer = new StringBuffer(128);
/*  900 */     if (i != 0) {
/*  901 */       localStringBuffer.append("(");
/*      */     }
/*  903 */     localStringBuffer.append('{');
/*      */     try
/*      */     {
/*  908 */       if (!bool) {
/*  909 */         paramContext.iterating.intern(paramScriptable2);
/*  910 */         Object[] arrayOfObject = paramScriptable2.getIds();
/*  911 */         for (int j = 0; j < arrayOfObject.length; j++) {
/*  912 */           Object localObject1 = arrayOfObject[j];
/*      */           Object localObject2;
/*  914 */           if ((localObject1 instanceof Integer)) {
/*  915 */             int k = ((Integer)localObject1).intValue();
/*  916 */             localObject2 = paramScriptable2.get(k, paramScriptable2);
/*  917 */             if (localObject2 == Scriptable.NOT_FOUND)
/*      */               continue;
/*  919 */             if (j > 0)
/*  920 */               localStringBuffer.append(", ");
/*  921 */             localStringBuffer.append(k);
/*      */           } else {
/*  923 */             String str = (String)localObject1;
/*  924 */             localObject2 = paramScriptable2.get(str, paramScriptable2);
/*  925 */             if (localObject2 == Scriptable.NOT_FOUND)
/*      */               continue;
/*  927 */             if (j > 0)
/*  928 */               localStringBuffer.append(", ");
/*  929 */             if (isValidIdentifierName(str)) {
/*  930 */               localStringBuffer.append(str);
/*      */             } else {
/*  932 */               localStringBuffer.append('\'');
/*  933 */               localStringBuffer.append(escapeString(str, '\''));
/*      */ 
/*  935 */               localStringBuffer.append('\'');
/*      */             }
/*      */           }
/*  938 */           localStringBuffer.append(':');
/*  939 */           localStringBuffer.append(uneval(paramContext, paramScriptable1, localObject2));
/*      */         }
/*      */       }
/*      */     } finally {
/*  943 */       if (i != 0) {
/*  944 */         paramContext.iterating = null;
/*      */       }
/*      */     }
/*      */ 
/*  948 */     localStringBuffer.append('}');
/*  949 */     if (i != 0) {
/*  950 */       localStringBuffer.append(')');
/*      */     }
/*  952 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   public static Scriptable toObject(Scriptable paramScriptable, Object paramObject)
/*      */   {
/*  957 */     if ((paramObject instanceof Scriptable)) {
/*  958 */       return (Scriptable)paramObject;
/*      */     }
/*  960 */     return toObject(Context.getContext(), paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   public static Scriptable toObjectOrNull(Context paramContext, Object paramObject)
/*      */   {
/*  968 */     if ((paramObject instanceof Scriptable))
/*  969 */       return (Scriptable)paramObject;
/*  970 */     if ((paramObject != null) && (paramObject != Undefined.instance)) {
/*  971 */       return toObject(paramContext, getTopCallScope(paramContext), paramObject);
/*      */     }
/*  973 */     return null;
/*      */   }
/*      */ 
/*      */   public static Scriptable toObjectOrNull(Context paramContext, Object paramObject, Scriptable paramScriptable)
/*      */   {
/*  982 */     if ((paramObject instanceof Scriptable))
/*  983 */       return (Scriptable)paramObject;
/*  984 */     if ((paramObject != null) && (paramObject != Undefined.instance)) {
/*  985 */       return toObject(paramContext, paramScriptable, paramObject);
/*      */     }
/*  987 */     return null;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Scriptable toObject(Scriptable paramScriptable, Object paramObject, Class<?> paramClass)
/*      */   {
/*  996 */     if ((paramObject instanceof Scriptable)) {
/*  997 */       return (Scriptable)paramObject;
/*      */     }
/*  999 */     return toObject(Context.getContext(), paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   public static Scriptable toObject(Context paramContext, Scriptable paramScriptable, Object paramObject)
/*      */   {
/* 1009 */     if ((paramObject instanceof Scriptable)) {
/* 1010 */       return (Scriptable)paramObject;
/*      */     }
/* 1012 */     if (paramObject == null) {
/* 1013 */       throw typeError0("msg.null.to.object");
/*      */     }
/* 1015 */     if (paramObject == Undefined.instance) {
/* 1016 */       throw typeError0("msg.undef.to.object");
/*      */     }
/* 1018 */     String str = (paramObject instanceof Boolean) ? "Boolean" : (paramObject instanceof Number) ? "Number" : (paramObject instanceof String) ? "String" : null;
/*      */ 
/* 1022 */     if (str != null) {
/* 1023 */       localObject = new Object[] { paramObject };
/* 1024 */       paramScriptable = ScriptableObject.getTopLevelScope(paramScriptable);
/* 1025 */       return newObject(paramContext, paramScriptable, str, (Object[])localObject);
/*      */     }
/*      */ 
/* 1029 */     Object localObject = paramContext.getWrapFactory().wrap(paramContext, paramScriptable, paramObject, null);
/* 1030 */     if ((localObject instanceof Scriptable))
/* 1031 */       return (Scriptable)localObject;
/* 1032 */     throw errorWithClassName("msg.invalid.type", paramObject);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Scriptable toObject(Context paramContext, Scriptable paramScriptable, Object paramObject, Class<?> paramClass)
/*      */   {
/* 1041 */     return toObject(paramContext, paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Object call(Context paramContext, Object paramObject1, Object paramObject2, Object[] paramArrayOfObject, Scriptable paramScriptable)
/*      */   {
/* 1050 */     if (!(paramObject1 instanceof Function)) {
/* 1051 */       throw notFunctionError(toString(paramObject1));
/*      */     }
/* 1053 */     Function localFunction = (Function)paramObject1;
/* 1054 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject2);
/* 1055 */     if (localScriptable == null) {
/* 1056 */       throw undefCallError(localScriptable, "function");
/*      */     }
/* 1058 */     return localFunction.call(paramContext, paramScriptable, localScriptable, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public static Scriptable newObject(Context paramContext, Scriptable paramScriptable, String paramString, Object[] paramArrayOfObject)
/*      */   {
/* 1064 */     paramScriptable = ScriptableObject.getTopLevelScope(paramScriptable);
/* 1065 */     Function localFunction = getExistingCtor(paramContext, paramScriptable, paramString);
/* 1066 */     if (paramArrayOfObject == null) paramArrayOfObject = emptyArgs;
/* 1067 */     return localFunction.construct(paramContext, paramScriptable, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public static double toInteger(Object paramObject)
/*      */   {
/* 1075 */     return toInteger(toNumber(paramObject));
/*      */   }
/*      */ 
/*      */   public static double toInteger(double paramDouble)
/*      */   {
/* 1081 */     if (paramDouble != paramDouble) {
/* 1082 */       return 0.0D;
/*      */     }
/* 1084 */     if ((paramDouble == 0.0D) || (paramDouble == (1.0D / 0.0D)) || (paramDouble == (-1.0D / 0.0D)))
/*      */     {
/* 1087 */       return paramDouble;
/*      */     }
/* 1089 */     if (paramDouble > 0.0D) {
/* 1090 */       return Math.floor(paramDouble);
/*      */     }
/* 1092 */     return Math.ceil(paramDouble);
/*      */   }
/*      */ 
/*      */   public static double toInteger(Object[] paramArrayOfObject, int paramInt) {
/* 1096 */     return paramInt < paramArrayOfObject.length ? toInteger(paramArrayOfObject[paramInt]) : 0.0D;
/*      */   }
/*      */ 
/*      */   public static int toInt32(Object paramObject)
/*      */   {
/* 1106 */     if ((paramObject instanceof Integer)) {
/* 1107 */       return ((Integer)paramObject).intValue();
/*      */     }
/* 1109 */     return toInt32(toNumber(paramObject));
/*      */   }
/*      */ 
/*      */   public static int toInt32(Object[] paramArrayOfObject, int paramInt) {
/* 1113 */     return paramInt < paramArrayOfObject.length ? toInt32(paramArrayOfObject[paramInt]) : 0;
/*      */   }
/*      */ 
/*      */   public static int toInt32(double paramDouble) {
/* 1117 */     int i = (int)paramDouble;
/* 1118 */     if (i == paramDouble)
/*      */     {
/* 1120 */       return i;
/*      */     }
/*      */ 
/* 1123 */     if ((paramDouble != paramDouble) || (paramDouble == (1.0D / 0.0D)) || (paramDouble == (-1.0D / 0.0D)))
/*      */     {
/* 1127 */       return 0;
/*      */     }
/*      */ 
/* 1130 */     paramDouble = paramDouble >= 0.0D ? Math.floor(paramDouble) : Math.ceil(paramDouble);
/*      */ 
/* 1132 */     double d = 4294967296.0D;
/* 1133 */     paramDouble = Math.IEEEremainder(paramDouble, d);
/*      */ 
/* 1136 */     long l = ()paramDouble;
/*      */ 
/* 1139 */     return (int)l;
/*      */   }
/*      */ 
/*      */   public static long toUint32(double paramDouble)
/*      */   {
/* 1147 */     long l = ()paramDouble;
/* 1148 */     if (l == paramDouble)
/*      */     {
/* 1150 */       return l & 0xFFFFFFFF;
/*      */     }
/*      */ 
/* 1153 */     if ((paramDouble != paramDouble) || (paramDouble == (1.0D / 0.0D)) || (paramDouble == (-1.0D / 0.0D)))
/*      */     {
/* 1157 */       return 0L;
/*      */     }
/*      */ 
/* 1160 */     paramDouble = paramDouble >= 0.0D ? Math.floor(paramDouble) : Math.ceil(paramDouble);
/*      */ 
/* 1163 */     double d = 4294967296.0D;
/* 1164 */     l = ()Math.IEEEremainder(paramDouble, d);
/*      */ 
/* 1166 */     return l & 0xFFFFFFFF;
/*      */   }
/*      */ 
/*      */   public static long toUint32(Object paramObject) {
/* 1170 */     return toUint32(toNumber(paramObject));
/*      */   }
/*      */ 
/*      */   public static char toUint16(Object paramObject)
/*      */   {
/* 1178 */     double d = toNumber(paramObject);
/*      */ 
/* 1180 */     int i = (int)d;
/* 1181 */     if (i == d) {
/* 1182 */       return (char)i;
/*      */     }
/*      */ 
/* 1185 */     if ((d != d) || (d == (1.0D / 0.0D)) || (d == (-1.0D / 0.0D)))
/*      */     {
/* 1189 */       return '\000';
/*      */     }
/*      */ 
/* 1192 */     d = d >= 0.0D ? Math.floor(d) : Math.ceil(d);
/*      */ 
/* 1194 */     int j = 65536;
/* 1195 */     i = (int)Math.IEEEremainder(d, j);
/*      */ 
/* 1197 */     return (char)i;
/*      */   }
/*      */ 
/*      */   public static Object setDefaultNamespace(Object paramObject, Context paramContext)
/*      */   {
/* 1206 */     Object localObject1 = paramContext.currentActivationCall;
/* 1207 */     if (localObject1 == null) {
/* 1208 */       localObject1 = getTopCallScope(paramContext);
/*      */     }
/*      */ 
/* 1211 */     XMLLib localXMLLib = currentXMLLib(paramContext);
/* 1212 */     Object localObject2 = localXMLLib.toDefaultXmlNamespace(paramContext, paramObject);
/*      */ 
/* 1215 */     if (!((Scriptable)localObject1).has("__default_namespace__", (Scriptable)localObject1))
/*      */     {
/* 1217 */       ScriptableObject.defineProperty((Scriptable)localObject1, "__default_namespace__", localObject2, 6);
/*      */     }
/*      */     else
/*      */     {
/* 1221 */       ((Scriptable)localObject1).put("__default_namespace__", (Scriptable)localObject1, localObject2);
/*      */     }
/*      */ 
/* 1224 */     return Undefined.instance;
/*      */   }
/*      */ 
/*      */   public static Object searchDefaultNamespace(Context paramContext)
/*      */   {
/* 1229 */     Object localObject1 = paramContext.currentActivationCall;
/* 1230 */     if (localObject1 == null)
/* 1231 */       localObject1 = getTopCallScope(paramContext);
/*      */     Object localObject2;
/*      */     while (true)
/*      */     {
/* 1235 */       Scriptable localScriptable = ((Scriptable)localObject1).getParentScope();
/* 1236 */       if (localScriptable == null) {
/* 1237 */         localObject2 = ScriptableObject.getProperty((Scriptable)localObject1, "__default_namespace__");
/* 1238 */         if (localObject2 != Scriptable.NOT_FOUND) break;
/* 1239 */         return null;
/*      */       }
/*      */ 
/* 1243 */       localObject2 = ((Scriptable)localObject1).get("__default_namespace__", (Scriptable)localObject1);
/* 1244 */       if (localObject2 != Scriptable.NOT_FOUND) {
/*      */         break;
/*      */       }
/* 1247 */       localObject1 = localScriptable;
/*      */     }
/* 1249 */     return localObject2;
/*      */   }
/*      */ 
/*      */   public static Object getTopLevelProp(Scriptable paramScriptable, String paramString) {
/* 1253 */     paramScriptable = ScriptableObject.getTopLevelScope(paramScriptable);
/* 1254 */     return ScriptableObject.getProperty(paramScriptable, paramString);
/*      */   }
/*      */ 
/*      */   static Function getExistingCtor(Context paramContext, Scriptable paramScriptable, String paramString)
/*      */   {
/* 1260 */     Object localObject = ScriptableObject.getProperty(paramScriptable, paramString);
/* 1261 */     if ((localObject instanceof Function)) {
/* 1262 */       return (Function)localObject;
/*      */     }
/* 1264 */     if (localObject == Scriptable.NOT_FOUND) {
/* 1265 */       throw Context.reportRuntimeError1("msg.ctor.not.found", paramString);
/*      */     }
/*      */ 
/* 1268 */     throw Context.reportRuntimeError1("msg.not.ctor", paramString);
/*      */   }
/*      */ 
/*      */   private static long indexFromString(String paramString)
/*      */   {
/* 1283 */     int i = paramString.length();
/* 1284 */     if (i > 0) {
/* 1285 */       int j = 0;
/* 1286 */       int k = 0;
/* 1287 */       int m = paramString.charAt(0);
/* 1288 */       if ((m == 45) && 
/* 1289 */         (i > 1)) {
/* 1290 */         m = paramString.charAt(1);
/* 1291 */         j = 1;
/* 1292 */         k = 1;
/*      */       }
/*      */ 
/* 1295 */       m -= 48;
/* 1296 */       if ((0 <= m) && (m <= 9)) if (i <= (k != 0 ? 11 : 10))
/*      */         {
/* 1302 */           int n = -m;
/* 1303 */           int i1 = 0;
/* 1304 */           j++;
/* 1305 */           if (n != 0)
/*      */           {
/* 1307 */             while ((j != i) && (0 <= (m = paramString.charAt(j) - '0')) && (m <= 9))
/*      */             {
/* 1309 */               i1 = n;
/* 1310 */               n = 10 * n - m;
/* 1311 */               j++;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1316 */           if (j == i) if (i1 <= -214748364) { if (i1 == -214748364)
/* 1316 */                 if (m > (k != 0 ? 8 : 7));
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 1322 */               return 0xFFFFFFFF & (k != 0 ? n : -n);
/*      */             }
/*      */         }
/*      */     }
/* 1326 */     return -1L;
/*      */   }
/*      */ 
/*      */   public static long testUint32String(String paramString)
/*      */   {
/* 1339 */     int i = paramString.length();
/* 1340 */     if ((1 <= i) && (i <= 10)) {
/* 1341 */       int j = paramString.charAt(0);
/* 1342 */       j -= 48;
/* 1343 */       if (j == 0)
/*      */       {
/* 1345 */         return i == 1 ? 0L : -1L;
/*      */       }
/* 1347 */       if ((1 <= j) && (j <= 9)) {
/* 1348 */         long l = j;
/* 1349 */         for (int k = 1; k != i; k++) {
/* 1350 */           j = paramString.charAt(k) - '0';
/* 1351 */           if ((0 > j) || (j > 9)) {
/* 1352 */             return -1L;
/*      */           }
/* 1354 */           l = 10L * l + j;
/*      */         }
/*      */ 
/* 1357 */         if (l >>> 32 == 0L) {
/* 1358 */           return l;
/*      */         }
/*      */       }
/*      */     }
/* 1362 */     return -1L;
/*      */   }
/*      */ 
/*      */   static Object getIndexObject(String paramString)
/*      */   {
/* 1371 */     long l = indexFromString(paramString);
/* 1372 */     if (l >= 0L) {
/* 1373 */       return Integer.valueOf((int)l);
/*      */     }
/* 1375 */     return paramString;
/*      */   }
/*      */ 
/*      */   static Object getIndexObject(double paramDouble)
/*      */   {
/* 1384 */     int i = (int)paramDouble;
/* 1385 */     if (i == paramDouble) {
/* 1386 */       return Integer.valueOf(i);
/*      */     }
/* 1388 */     return toString(paramDouble);
/*      */   }
/*      */ 
/*      */   static String toStringIdOrIndex(Context paramContext, Object paramObject)
/*      */   {
/* 1398 */     if ((paramObject instanceof Number)) {
/* 1399 */       double d = ((Number)paramObject).doubleValue();
/* 1400 */       int i = (int)d;
/* 1401 */       if (i == d) {
/* 1402 */         storeIndexResult(paramContext, i);
/* 1403 */         return null;
/*      */       }
/* 1405 */       return toString(paramObject);
/*      */     }
/*      */     String str;
/* 1408 */     if ((paramObject instanceof String))
/* 1409 */       str = (String)paramObject;
/*      */     else {
/* 1411 */       str = toString(paramObject);
/*      */     }
/* 1413 */     long l = indexFromString(str);
/* 1414 */     if (l >= 0L) {
/* 1415 */       storeIndexResult(paramContext, (int)l);
/* 1416 */       return null;
/*      */     }
/* 1418 */     return str;
/*      */   }
/*      */ 
/*      */   public static Object getObjectElem(Object paramObject1, Object paramObject2, Context paramContext)
/*      */   {
/* 1427 */     return getObjectElem(paramObject1, paramObject2, paramContext, getTopCallScope(paramContext));
/*      */   }
/*      */ 
/*      */   public static Object getObjectElem(Object paramObject1, Object paramObject2, Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 1435 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject1, paramScriptable);
/* 1436 */     if (localScriptable == null) {
/* 1437 */       throw undefReadError(paramObject1, paramObject2);
/*      */     }
/* 1439 */     return getObjectElem(localScriptable, paramObject2, paramContext);
/*      */   }
/*      */ 
/*      */   public static Object getObjectElem(Scriptable paramScriptable, Object paramObject, Context paramContext)
/*      */   {
/*      */     Object localObject;
/* 1445 */     if ((paramScriptable instanceof XMLObject)) {
/* 1446 */       localObject = (XMLObject)paramScriptable;
/* 1447 */       return ((XMLObject)localObject).ecmaGet(paramContext, paramObject);
/*      */     }
/*      */ 
/* 1452 */     String str = toStringIdOrIndex(paramContext, paramObject);
/* 1453 */     if (str == null) {
/* 1454 */       int i = lastIndexResult(paramContext);
/* 1455 */       localObject = ScriptableObject.getProperty(paramScriptable, i);
/*      */     } else {
/* 1457 */       localObject = ScriptableObject.getProperty(paramScriptable, str);
/*      */     }
/*      */ 
/* 1460 */     if (localObject == Scriptable.NOT_FOUND) {
/* 1461 */       localObject = Undefined.instance;
/*      */     }
/*      */ 
/* 1464 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static Object getObjectProp(Object paramObject, String paramString, Context paramContext)
/*      */   {
/* 1473 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject);
/* 1474 */     if (localScriptable == null) {
/* 1475 */       throw undefReadError(paramObject, paramString);
/*      */     }
/* 1477 */     return getObjectProp(localScriptable, paramString, paramContext);
/*      */   }
/*      */ 
/*      */   public static Object getObjectProp(Object paramObject, String paramString, Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 1486 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject, paramScriptable);
/* 1487 */     if (localScriptable == null) {
/* 1488 */       throw undefReadError(paramObject, paramString);
/*      */     }
/* 1490 */     return getObjectProp(localScriptable, paramString, paramContext);
/*      */   }
/*      */ 
/*      */   public static Object getObjectProp(Scriptable paramScriptable, String paramString, Context paramContext)
/*      */   {
/* 1496 */     if ((paramScriptable instanceof XMLObject))
/*      */     {
/* 1500 */       localObject = (XMLObject)paramScriptable;
/* 1501 */       return ((XMLObject)localObject).ecmaGet(paramContext, paramString);
/*      */     }
/*      */ 
/* 1504 */     Object localObject = ScriptableObject.getProperty(paramScriptable, paramString);
/* 1505 */     if (localObject == Scriptable.NOT_FOUND) {
/* 1506 */       if (paramContext.hasFeature(11)) {
/* 1507 */         Context.reportWarning(getMessage1("msg.ref.undefined.prop", paramString));
/*      */       }
/*      */ 
/* 1510 */       localObject = Undefined.instance;
/*      */     }
/*      */ 
/* 1513 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static Object getObjectPropNoWarn(Object paramObject, String paramString, Context paramContext)
/*      */   {
/* 1519 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject);
/* 1520 */     if (localScriptable == null) {
/* 1521 */       throw undefReadError(paramObject, paramString);
/*      */     }
/* 1523 */     if ((paramObject instanceof XMLObject))
/*      */     {
/* 1525 */       getObjectProp(localScriptable, paramString, paramContext);
/*      */     }
/* 1527 */     Object localObject = ScriptableObject.getProperty(localScriptable, paramString);
/* 1528 */     if (localObject == Scriptable.NOT_FOUND) {
/* 1529 */       return Undefined.instance;
/*      */     }
/* 1531 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static Object getObjectIndex(Object paramObject, double paramDouble, Context paramContext)
/*      */   {
/* 1541 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject);
/* 1542 */     if (localScriptable == null) {
/* 1543 */       throw undefReadError(paramObject, toString(paramDouble));
/*      */     }
/*      */ 
/* 1546 */     int i = (int)paramDouble;
/* 1547 */     if (i == paramDouble) {
/* 1548 */       return getObjectIndex(localScriptable, i, paramContext);
/*      */     }
/* 1550 */     String str = toString(paramDouble);
/* 1551 */     return getObjectProp(localScriptable, str, paramContext);
/*      */   }
/*      */ 
/*      */   public static Object getObjectIndex(Scriptable paramScriptable, int paramInt, Context paramContext)
/*      */   {
/* 1558 */     if ((paramScriptable instanceof XMLObject)) {
/* 1559 */       localObject = (XMLObject)paramScriptable;
/* 1560 */       return ((XMLObject)localObject).ecmaGet(paramContext, Integer.valueOf(paramInt));
/*      */     }
/*      */ 
/* 1563 */     Object localObject = ScriptableObject.getProperty(paramScriptable, paramInt);
/* 1564 */     if (localObject == Scriptable.NOT_FOUND) {
/* 1565 */       localObject = Undefined.instance;
/*      */     }
/*      */ 
/* 1568 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static Object setObjectElem(Object paramObject1, Object paramObject2, Object paramObject3, Context paramContext)
/*      */   {
/* 1577 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject1);
/* 1578 */     if (localScriptable == null) {
/* 1579 */       throw undefWriteError(paramObject1, paramObject2, paramObject3);
/*      */     }
/* 1581 */     return setObjectElem(localScriptable, paramObject2, paramObject3, paramContext);
/*      */   }
/*      */ 
/*      */   public static Object setObjectElem(Scriptable paramScriptable, Object paramObject1, Object paramObject2, Context paramContext)
/*      */   {
/* 1587 */     if ((paramScriptable instanceof XMLObject)) {
/* 1588 */       localObject = (XMLObject)paramScriptable;
/* 1589 */       ((XMLObject)localObject).ecmaPut(paramContext, paramObject1, paramObject2);
/* 1590 */       return paramObject2;
/*      */     }
/*      */ 
/* 1593 */     Object localObject = toStringIdOrIndex(paramContext, paramObject1);
/* 1594 */     if (localObject == null) {
/* 1595 */       int i = lastIndexResult(paramContext);
/* 1596 */       ScriptableObject.putProperty(paramScriptable, i, paramObject2);
/*      */     } else {
/* 1598 */       ScriptableObject.putProperty(paramScriptable, (String)localObject, paramObject2);
/*      */     }
/*      */ 
/* 1601 */     return paramObject2;
/*      */   }
/*      */ 
/*      */   public static Object setObjectProp(Object paramObject1, String paramString, Object paramObject2, Context paramContext)
/*      */   {
/* 1610 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject1);
/* 1611 */     if (localScriptable == null) {
/* 1612 */       throw undefWriteError(paramObject1, paramString, paramObject2);
/*      */     }
/* 1614 */     return setObjectProp(localScriptable, paramString, paramObject2, paramContext);
/*      */   }
/*      */ 
/*      */   public static Object setObjectProp(Scriptable paramScriptable, String paramString, Object paramObject, Context paramContext)
/*      */   {
/* 1620 */     if ((paramScriptable instanceof XMLObject)) {
/* 1621 */       XMLObject localXMLObject = (XMLObject)paramScriptable;
/* 1622 */       localXMLObject.ecmaPut(paramContext, paramString, paramObject);
/*      */     } else {
/* 1624 */       ScriptableObject.putProperty(paramScriptable, paramString, paramObject);
/*      */     }
/* 1626 */     return paramObject;
/*      */   }
/*      */ 
/*      */   public static Object setObjectIndex(Object paramObject1, double paramDouble, Object paramObject2, Context paramContext)
/*      */   {
/* 1636 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject1);
/* 1637 */     if (localScriptable == null) {
/* 1638 */       throw undefWriteError(paramObject1, String.valueOf(paramDouble), paramObject2);
/*      */     }
/*      */ 
/* 1641 */     int i = (int)paramDouble;
/* 1642 */     if (i == paramDouble) {
/* 1643 */       return setObjectIndex(localScriptable, i, paramObject2, paramContext);
/*      */     }
/* 1645 */     String str = toString(paramDouble);
/* 1646 */     return setObjectProp(localScriptable, str, paramObject2, paramContext);
/*      */   }
/*      */ 
/*      */   public static Object setObjectIndex(Scriptable paramScriptable, int paramInt, Object paramObject, Context paramContext)
/*      */   {
/* 1653 */     if ((paramScriptable instanceof XMLObject)) {
/* 1654 */       XMLObject localXMLObject = (XMLObject)paramScriptable;
/* 1655 */       localXMLObject.ecmaPut(paramContext, Integer.valueOf(paramInt), paramObject);
/*      */     } else {
/* 1657 */       ScriptableObject.putProperty(paramScriptable, paramInt, paramObject);
/*      */     }
/* 1659 */     return paramObject;
/*      */   }
/*      */ 
/*      */   public static boolean deleteObjectElem(Scriptable paramScriptable, Object paramObject, Context paramContext)
/*      */   {
/*      */     Object localObject;
/*      */     boolean bool;
/* 1666 */     if ((paramScriptable instanceof XMLObject)) {
/* 1667 */       localObject = (XMLObject)paramScriptable;
/* 1668 */       bool = ((XMLObject)localObject).ecmaDelete(paramContext, paramObject);
/*      */     } else {
/* 1670 */       localObject = toStringIdOrIndex(paramContext, paramObject);
/* 1671 */       if (localObject == null) {
/* 1672 */         int i = lastIndexResult(paramContext);
/* 1673 */         paramScriptable.delete(i);
/* 1674 */         return !paramScriptable.has(i, paramScriptable);
/*      */       }
/* 1676 */       paramScriptable.delete((String)localObject);
/* 1677 */       return !paramScriptable.has((String)localObject, paramScriptable);
/*      */     }
/*      */ 
/* 1680 */     return bool;
/*      */   }
/*      */ 
/*      */   public static boolean hasObjectElem(Scriptable paramScriptable, Object paramObject, Context paramContext)
/*      */   {
/*      */     Object localObject;
/*      */     boolean bool;
/* 1688 */     if ((paramScriptable instanceof XMLObject)) {
/* 1689 */       localObject = (XMLObject)paramScriptable;
/* 1690 */       bool = ((XMLObject)localObject).ecmaHas(paramContext, paramObject);
/*      */     } else {
/* 1692 */       localObject = toStringIdOrIndex(paramContext, paramObject);
/* 1693 */       if (localObject == null) {
/* 1694 */         int i = lastIndexResult(paramContext);
/* 1695 */         bool = ScriptableObject.hasProperty(paramScriptable, i);
/*      */       } else {
/* 1697 */         bool = ScriptableObject.hasProperty(paramScriptable, (String)localObject);
/*      */       }
/*      */     }
/*      */ 
/* 1701 */     return bool;
/*      */   }
/*      */ 
/*      */   public static Object refGet(Ref paramRef, Context paramContext)
/*      */   {
/* 1706 */     return paramRef.get(paramContext);
/*      */   }
/*      */ 
/*      */   public static Object refSet(Ref paramRef, Object paramObject, Context paramContext)
/*      */   {
/* 1711 */     return paramRef.set(paramContext, paramObject);
/*      */   }
/*      */ 
/*      */   public static Object refDel(Ref paramRef, Context paramContext)
/*      */   {
/* 1716 */     return wrapBoolean(paramRef.delete(paramContext));
/*      */   }
/*      */ 
/*      */   static boolean isSpecialProperty(String paramString)
/*      */   {
/* 1721 */     return (paramString.equals("__proto__")) || (paramString.equals("__parent__"));
/*      */   }
/*      */ 
/*      */   public static Ref specialRef(Object paramObject, String paramString, Context paramContext)
/*      */   {
/* 1727 */     return SpecialRef.createSpecial(paramContext, paramObject, paramString);
/*      */   }
/*      */ 
/*      */   public static Object delete(Object paramObject1, Object paramObject2, Context paramContext)
/*      */   {
/* 1743 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject1);
/* 1744 */     if (localScriptable == null) {
/* 1745 */       String str = paramObject2 == null ? "null" : paramObject2.toString();
/* 1746 */       throw typeError2("msg.undef.prop.delete", toString(paramObject1), str);
/*      */     }
/* 1748 */     boolean bool = deleteObjectElem(localScriptable, paramObject2, paramContext);
/* 1749 */     return wrapBoolean(bool);
/*      */   }
/*      */ 
/*      */   public static Object name(Context paramContext, Scriptable paramScriptable, String paramString)
/*      */   {
/* 1757 */     Scriptable localScriptable = paramScriptable.getParentScope();
/* 1758 */     if (localScriptable == null) {
/* 1759 */       Object localObject = topScopeName(paramContext, paramScriptable, paramString);
/* 1760 */       if (localObject == Scriptable.NOT_FOUND) {
/* 1761 */         throw notFoundError(paramScriptable, paramString);
/*      */       }
/* 1763 */       return localObject;
/*      */     }
/*      */ 
/* 1766 */     return nameOrFunction(paramContext, paramScriptable, localScriptable, paramString, false);
/*      */   }
/*      */ 
/*      */   private static Object nameOrFunction(Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, String paramString, boolean paramBoolean)
/*      */   {
/* 1774 */     Object localObject2 = paramScriptable1;
/*      */ 
/* 1776 */     Object localObject3 = null;
/*      */     do {
/* 1778 */       if ((paramScriptable1 instanceof NativeWith)) {
/* 1779 */         Scriptable localScriptable = paramScriptable1.getPrototype();
/* 1780 */         if ((localScriptable instanceof XMLObject)) {
/* 1781 */           XMLObject localXMLObject = (XMLObject)localScriptable;
/* 1782 */           if (localXMLObject.ecmaHas(paramContext, paramString))
/*      */           {
/* 1784 */             localObject2 = localXMLObject;
/* 1785 */             localObject1 = localXMLObject.ecmaGet(paramContext, paramString);
/* 1786 */             break;
/*      */           }
/* 1788 */           if (localObject3 == null)
/* 1789 */             localObject3 = localXMLObject;
/*      */         }
/*      */         else {
/* 1792 */           localObject1 = ScriptableObject.getProperty(localScriptable, paramString);
/* 1793 */           if (localObject1 != Scriptable.NOT_FOUND)
/*      */           {
/* 1795 */             localObject2 = localScriptable;
/* 1796 */             break;
/*      */           }
/*      */         }
/* 1799 */       } else if ((paramScriptable1 instanceof NativeCall))
/*      */       {
/* 1802 */         localObject1 = paramScriptable1.get(paramString, paramScriptable1);
/* 1803 */         if (localObject1 != Scriptable.NOT_FOUND) {
/* 1804 */           if (!paramBoolean) {
/*      */             break;
/*      */           }
/* 1807 */           localObject2 = ScriptableObject.getTopLevelScope(paramScriptable2); break;
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1815 */         localObject1 = ScriptableObject.getProperty(paramScriptable1, paramString);
/* 1816 */         if (localObject1 != Scriptable.NOT_FOUND) {
/* 1817 */           localObject2 = paramScriptable1;
/* 1818 */           break;
/*      */         }
/*      */       }
/* 1821 */       paramScriptable1 = paramScriptable2;
/* 1822 */       paramScriptable2 = paramScriptable2.getParentScope();
/* 1823 */     }while (paramScriptable2 != null);
/* 1824 */     Object localObject1 = topScopeName(paramContext, paramScriptable1, paramString);
/* 1825 */     if (localObject1 == Scriptable.NOT_FOUND) {
/* 1826 */       if ((localObject3 == null) || (paramBoolean)) {
/* 1827 */         throw notFoundError(paramScriptable1, paramString);
/*      */       }
/*      */ 
/* 1833 */       localObject1 = localObject3.ecmaGet(paramContext, paramString);
/*      */     }
/*      */ 
/* 1836 */     localObject2 = paramScriptable1;
/*      */ 
/* 1841 */     if (paramBoolean) {
/* 1842 */       if (!(localObject1 instanceof Callable)) {
/* 1843 */         throw notFunctionError(localObject1, paramString);
/*      */       }
/* 1845 */       storeScriptable(paramContext, (Scriptable)localObject2);
/*      */     }
/*      */ 
/* 1848 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private static Object topScopeName(Context paramContext, Scriptable paramScriptable, String paramString)
/*      */   {
/* 1854 */     if (paramContext.useDynamicScope) {
/* 1855 */       paramScriptable = checkDynamicScope(paramContext.topCallScope, paramScriptable);
/*      */     }
/* 1857 */     return ScriptableObject.getProperty(paramScriptable, paramString);
/*      */   }
/*      */ 
/*      */   public static Scriptable bind(Context paramContext, Scriptable paramScriptable, String paramString)
/*      */   {
/* 1876 */     Object localObject = null;
/* 1877 */     Scriptable localScriptable1 = paramScriptable.getParentScope();
/* 1878 */     if (localScriptable1 != null)
/*      */     {
/* 1880 */       while ((paramScriptable instanceof NativeWith)) {
/* 1881 */         Scriptable localScriptable2 = paramScriptable.getPrototype();
/* 1882 */         if ((localScriptable2 instanceof XMLObject)) {
/* 1883 */           XMLObject localXMLObject = (XMLObject)localScriptable2;
/* 1884 */           if (localXMLObject.ecmaHas(paramContext, paramString)) {
/* 1885 */             return localXMLObject;
/*      */           }
/* 1887 */           if (localObject == null) {
/* 1888 */             localObject = localXMLObject;
/*      */           }
/*      */         }
/* 1891 */         else if (ScriptableObject.hasProperty(localScriptable2, paramString)) {
/* 1892 */           return localScriptable2;
/*      */         }
/*      */ 
/* 1895 */         paramScriptable = localScriptable1;
/* 1896 */         localScriptable1 = localScriptable1.getParentScope();
/* 1897 */         if (localScriptable1 == null)
/*      */           break label133;
/*      */       }
/*      */       while (true)
/*      */       {
/* 1902 */         if (ScriptableObject.hasProperty(paramScriptable, paramString)) {
/* 1903 */           return paramScriptable;
/*      */         }
/* 1905 */         paramScriptable = localScriptable1;
/* 1906 */         localScriptable1 = localScriptable1.getParentScope();
/* 1907 */         if (localScriptable1 == null) {
/* 1908 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1913 */     label133: if (paramContext.useDynamicScope) {
/* 1914 */       paramScriptable = checkDynamicScope(paramContext.topCallScope, paramScriptable);
/*      */     }
/* 1916 */     if (ScriptableObject.hasProperty(paramScriptable, paramString)) {
/* 1917 */       return paramScriptable;
/*      */     }
/*      */ 
/* 1921 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static Object setName(Scriptable paramScriptable1, Object paramObject, Context paramContext, Scriptable paramScriptable2, String paramString)
/*      */   {
/* 1927 */     if (paramScriptable1 != null) {
/* 1928 */       if ((paramScriptable1 instanceof XMLObject)) {
/* 1929 */         XMLObject localXMLObject = (XMLObject)paramScriptable1;
/* 1930 */         localXMLObject.ecmaPut(paramContext, paramString, paramObject);
/*      */       } else {
/* 1932 */         ScriptableObject.putProperty(paramScriptable1, paramString, paramObject);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1938 */       if ((paramContext.hasFeature(11)) || (paramContext.hasFeature(8)))
/*      */       {
/* 1941 */         Context.reportWarning(getMessage1("msg.assn.create.strict", paramString));
/*      */       }
/*      */ 
/* 1945 */       paramScriptable1 = ScriptableObject.getTopLevelScope(paramScriptable2);
/* 1946 */       if (paramContext.useDynamicScope) {
/* 1947 */         paramScriptable1 = checkDynamicScope(paramContext.topCallScope, paramScriptable1);
/*      */       }
/* 1949 */       paramScriptable1.put(paramString, paramScriptable1, paramObject);
/*      */     }
/* 1951 */     return paramObject;
/*      */   }
/*      */ 
/*      */   public static Object strictSetName(Scriptable paramScriptable1, Object paramObject, Context paramContext, Scriptable paramScriptable2, String paramString)
/*      */   {
/*      */     Object localObject;
/* 1956 */     if (paramScriptable1 != null)
/*      */     {
/* 1963 */       if ((paramScriptable1 instanceof XMLObject)) {
/* 1964 */         localObject = (XMLObject)paramScriptable1;
/* 1965 */         ((XMLObject)localObject).ecmaPut(paramContext, paramString, paramObject);
/*      */       } else {
/* 1967 */         ScriptableObject.putProperty(paramScriptable1, paramString, paramObject);
/*      */       }
/*      */     }
/*      */     else {
/* 1971 */       localObject = new int[1];
/* 1972 */       String str = Context.getSourcePositionFromStack((int[])localObject);
/* 1973 */       throw new JavaScriptException(paramContext.newObject(paramScriptable2, "ReferenceError", new Object[] { paramString }), str, localObject[0]);
/*      */     }
/*      */ 
/* 1978 */     return paramObject;
/*      */   }
/*      */ 
/*      */   public static Object setConst(Scriptable paramScriptable, Object paramObject, Context paramContext, String paramString)
/*      */   {
/* 1984 */     if ((paramScriptable instanceof XMLObject)) {
/* 1985 */       XMLObject localXMLObject = (XMLObject)paramScriptable;
/* 1986 */       localXMLObject.ecmaPut(paramContext, paramString, paramObject);
/*      */     } else {
/* 1988 */       ScriptableObject.putConstProperty(paramScriptable, paramString, paramObject);
/*      */     }
/* 1990 */     return paramObject;
/*      */   }
/*      */ 
/*      */   public static Scriptable toIterator(Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, boolean paramBoolean)
/*      */   {
/* 2027 */     if (ScriptableObject.hasProperty(paramScriptable2, "__iterator__"))
/*      */     {
/* 2030 */       Object localObject = ScriptableObject.getProperty(paramScriptable2, "__iterator__");
/*      */ 
/* 2032 */       if (!(localObject instanceof Callable)) {
/* 2033 */         throw typeError0("msg.invalid.iterator");
/*      */       }
/* 2035 */       Callable localCallable = (Callable)localObject;
/* 2036 */       Object[] arrayOfObject = { paramBoolean ? Boolean.TRUE : Boolean.FALSE };
/*      */ 
/* 2038 */       localObject = localCallable.call(paramContext, paramScriptable1, paramScriptable2, arrayOfObject);
/* 2039 */       if (!(localObject instanceof Scriptable)) {
/* 2040 */         throw typeError0("msg.iterator.primitive");
/*      */       }
/* 2042 */       return (Scriptable)localObject;
/*      */     }
/* 2044 */     return null;
/*      */   }
/*      */ 
/*      */   public static Object enumInit(Object paramObject, Context paramContext, boolean paramBoolean)
/*      */   {
/* 2050 */     return enumInit(paramObject, paramContext, paramBoolean ? 1 : 0);
/*      */   }
/*      */ 
/*      */   public static Object enumInit(Object paramObject, Context paramContext, int paramInt)
/*      */   {
/* 2063 */     IdEnumeration localIdEnumeration = new IdEnumeration(null);
/* 2064 */     localIdEnumeration.obj = toObjectOrNull(paramContext, paramObject);
/* 2065 */     if (localIdEnumeration.obj == null)
/*      */     {
/* 2068 */       return localIdEnumeration;
/*      */     }
/* 2070 */     localIdEnumeration.enumType = paramInt;
/* 2071 */     localIdEnumeration.iterator = null;
/* 2072 */     if ((paramInt != 3) && (paramInt != 4) && (paramInt != 5))
/*      */     {
/* 2076 */       localIdEnumeration.iterator = toIterator(paramContext, localIdEnumeration.obj.getParentScope(), localIdEnumeration.obj, paramInt == 0);
/*      */     }
/*      */ 
/* 2079 */     if (localIdEnumeration.iterator == null)
/*      */     {
/* 2082 */       enumChangeObject(localIdEnumeration);
/*      */     }
/*      */ 
/* 2085 */     return localIdEnumeration;
/*      */   }
/*      */ 
/*      */   public static void setEnumNumbers(Object paramObject, boolean paramBoolean) {
/* 2089 */     ((IdEnumeration)paramObject).enumNumbers = paramBoolean;
/*      */   }
/*      */ 
/*      */   public static Boolean enumNext(Object paramObject)
/*      */   {
/* 2094 */     IdEnumeration localIdEnumeration = (IdEnumeration)paramObject;
/*      */     Object localObject1;
/*      */     Object localObject2;
/* 2095 */     if (localIdEnumeration.iterator != null) {
/* 2096 */       localObject1 = ScriptableObject.getProperty(localIdEnumeration.iterator, "next");
/* 2097 */       if (!(localObject1 instanceof Callable))
/* 2098 */         return Boolean.FALSE;
/* 2099 */       localObject2 = (Callable)localObject1;
/* 2100 */       Context localContext = Context.getContext();
/*      */       try {
/* 2102 */         localIdEnumeration.currentId = ((Callable)localObject2).call(localContext, localIdEnumeration.iterator.getParentScope(), localIdEnumeration.iterator, emptyArgs);
/*      */ 
/* 2104 */         return Boolean.TRUE;
/*      */       } catch (JavaScriptException localJavaScriptException) {
/* 2106 */         if ((localJavaScriptException.getValue() instanceof NativeIterator.StopIteration)) {
/* 2107 */           return Boolean.FALSE;
/*      */         }
/* 2109 */         throw localJavaScriptException;
/*      */       }
/*      */     }int i;
/*      */     do {
/*      */       do { do { while (true) { if (localIdEnumeration.obj == null) {
/* 2114 */               return Boolean.FALSE;
/*      */             }
/* 2116 */             if (localIdEnumeration.index != localIdEnumeration.ids.length) break;
/* 2117 */             localIdEnumeration.obj = localIdEnumeration.obj.getPrototype();
/* 2118 */             enumChangeObject(localIdEnumeration);
/*      */           }
/*      */ 
/* 2121 */           localObject1 = localIdEnumeration.ids[(localIdEnumeration.index++)]; }
/* 2122 */         while ((localIdEnumeration.used != null) && (localIdEnumeration.used.has(localObject1)));
/*      */ 
/* 2125 */         if (!(localObject1 instanceof String)) break;
/* 2126 */         localObject2 = (String)localObject1; }
/* 2127 */       while (!localIdEnumeration.obj.has((String)localObject2, localIdEnumeration.obj));
/*      */ 
/* 2129 */       localIdEnumeration.currentId = localObject2;
/* 2130 */       break;
/* 2131 */       i = ((Number)localObject1).intValue();
/* 2132 */     }while (!localIdEnumeration.obj.has(i, localIdEnumeration.obj));
/*      */ 
/* 2134 */     localIdEnumeration.currentId = (localIdEnumeration.enumNumbers ? Integer.valueOf(i) : String.valueOf(i));
/*      */ 
/* 2137 */     return Boolean.TRUE;
/*      */   }
/*      */ 
/*      */   public static Object enumId(Object paramObject, Context paramContext)
/*      */   {
/* 2143 */     IdEnumeration localIdEnumeration = (IdEnumeration)paramObject;
/* 2144 */     if (localIdEnumeration.iterator != null) {
/* 2145 */       return localIdEnumeration.currentId;
/*      */     }
/* 2147 */     switch (localIdEnumeration.enumType) {
/*      */     case 0:
/*      */     case 3:
/* 2150 */       return localIdEnumeration.currentId;
/*      */     case 1:
/*      */     case 4:
/* 2153 */       return enumValue(paramObject, paramContext);
/*      */     case 2:
/*      */     case 5:
/* 2156 */       Object[] arrayOfObject = { localIdEnumeration.currentId, enumValue(paramObject, paramContext) };
/* 2157 */       return paramContext.newArray(ScriptableObject.getTopLevelScope(localIdEnumeration.obj), arrayOfObject);
/*      */     }
/* 2159 */     throw Kit.codeBug();
/*      */   }
/*      */ 
/*      */   public static Object enumValue(Object paramObject, Context paramContext)
/*      */   {
/* 2164 */     IdEnumeration localIdEnumeration = (IdEnumeration)paramObject;
/*      */ 
/* 2168 */     String str = toStringIdOrIndex(paramContext, localIdEnumeration.currentId);
/*      */     Object localObject;
/* 2169 */     if (str == null) {
/* 2170 */       int i = lastIndexResult(paramContext);
/* 2171 */       localObject = localIdEnumeration.obj.get(i, localIdEnumeration.obj);
/*      */     } else {
/* 2173 */       localObject = localIdEnumeration.obj.get(str, localIdEnumeration.obj);
/*      */     }
/*      */ 
/* 2176 */     return localObject;
/*      */   }
/*      */ 
/*      */   private static void enumChangeObject(IdEnumeration paramIdEnumeration)
/*      */   {
/* 2181 */     Object[] arrayOfObject1 = null;
/* 2182 */     while (paramIdEnumeration.obj != null) {
/* 2183 */       arrayOfObject1 = paramIdEnumeration.obj.getIds();
/* 2184 */       if (arrayOfObject1.length != 0) {
/*      */         break;
/*      */       }
/* 2187 */       paramIdEnumeration.obj = paramIdEnumeration.obj.getPrototype();
/*      */     }
/* 2189 */     if ((paramIdEnumeration.obj != null) && (paramIdEnumeration.ids != null)) {
/* 2190 */       Object[] arrayOfObject2 = paramIdEnumeration.ids;
/* 2191 */       int i = arrayOfObject2.length;
/* 2192 */       if (paramIdEnumeration.used == null) {
/* 2193 */         paramIdEnumeration.used = new ObjToIntMap(i);
/*      */       }
/* 2195 */       for (int j = 0; j != i; j++) {
/* 2196 */         paramIdEnumeration.used.intern(arrayOfObject2[j]);
/*      */       }
/*      */     }
/* 2199 */     paramIdEnumeration.ids = arrayOfObject1;
/* 2200 */     paramIdEnumeration.index = 0;
/*      */   }
/*      */ 
/*      */   public static Callable getNameFunctionAndThis(String paramString, Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 2214 */     Scriptable localScriptable1 = paramScriptable.getParentScope();
/* 2215 */     if (localScriptable1 == null) {
/* 2216 */       Object localObject = topScopeName(paramContext, paramScriptable, paramString);
/* 2217 */       if (!(localObject instanceof Callable)) {
/* 2218 */         if (localObject == Scriptable.NOT_FOUND) {
/* 2219 */           throw notFoundError(paramScriptable, paramString);
/*      */         }
/* 2221 */         throw notFunctionError(localObject, paramString);
/*      */       }
/*      */ 
/* 2225 */       Scriptable localScriptable2 = paramScriptable;
/* 2226 */       storeScriptable(paramContext, localScriptable2);
/* 2227 */       return (Callable)localObject;
/*      */     }
/*      */ 
/* 2231 */     return (Callable)nameOrFunction(paramContext, paramScriptable, localScriptable1, paramString, true);
/*      */   }
/*      */ 
/*      */   public static Callable getElemFunctionAndThis(Object paramObject1, Object paramObject2, Context paramContext)
/*      */   {
/* 2245 */     String str = toStringIdOrIndex(paramContext, paramObject2);
/* 2246 */     if (str != null) {
/* 2247 */       return getPropFunctionAndThis(paramObject1, str, paramContext);
/*      */     }
/* 2249 */     int i = lastIndexResult(paramContext);
/*      */ 
/* 2251 */     Object localObject1 = toObjectOrNull(paramContext, paramObject1);
/* 2252 */     if (localObject1 == null) {
/* 2253 */       throw undefCallError(paramObject1, String.valueOf(i));
/*      */     }
/*      */ 
/*      */     Object localObject2;
/*      */     while (true)
/*      */     {
/* 2259 */       localObject2 = ScriptableObject.getProperty((Scriptable)localObject1, i);
/* 2260 */       if (localObject2 != Scriptable.NOT_FOUND) {
/*      */         break;
/*      */       }
/* 2263 */       if (!(localObject1 instanceof XMLObject)) {
/*      */         break;
/*      */       }
/* 2266 */       XMLObject localXMLObject = (XMLObject)localObject1;
/* 2267 */       Scriptable localScriptable = localXMLObject.getExtraMethodSource(paramContext);
/* 2268 */       if (localScriptable == null) {
/*      */         break;
/*      */       }
/* 2271 */       localObject1 = localScriptable;
/*      */     }
/* 2273 */     if (!(localObject2 instanceof Callable)) {
/* 2274 */       throw notFunctionError(localObject2, paramObject2);
/*      */     }
/*      */ 
/* 2277 */     storeScriptable(paramContext, (Scriptable)localObject1);
/* 2278 */     return (Callable)localObject2;
/*      */   }
/*      */ 
/*      */   public static Callable getPropFunctionAndThis(Object paramObject, String paramString, Context paramContext)
/*      */   {
/* 2294 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject);
/* 2295 */     return getPropFunctionAndThisHelper(paramObject, paramString, paramContext, localScriptable);
/*      */   }
/*      */ 
/*      */   public static Callable getPropFunctionAndThis(Object paramObject, String paramString, Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 2309 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject, paramScriptable);
/* 2310 */     return getPropFunctionAndThisHelper(paramObject, paramString, paramContext, localScriptable);
/*      */   }
/*      */ 
/*      */   private static Callable getPropFunctionAndThisHelper(Object paramObject, String paramString, Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 2316 */     if (paramScriptable == null)
/* 2317 */       throw undefCallError(paramObject, paramString);
/*      */     Object localObject1;
/*      */     Object localObject2;
/*      */     while (true)
/*      */     {
/* 2323 */       localObject1 = ScriptableObject.getProperty(paramScriptable, paramString);
/* 2324 */       if (localObject1 != Scriptable.NOT_FOUND) {
/*      */         break;
/*      */       }
/* 2327 */       if (!(paramScriptable instanceof XMLObject)) {
/*      */         break;
/*      */       }
/* 2330 */       localObject2 = (XMLObject)paramScriptable;
/* 2331 */       Scriptable localScriptable = ((XMLObject)localObject2).getExtraMethodSource(paramContext);
/* 2332 */       if (localScriptable == null) {
/*      */         break;
/*      */       }
/* 2335 */       paramScriptable = localScriptable;
/*      */     }
/*      */ 
/* 2338 */     if (!(localObject1 instanceof Callable)) {
/* 2339 */       localObject2 = ScriptableObject.getProperty(paramScriptable, "__noSuchMethod__");
/* 2340 */       if ((localObject2 instanceof Callable))
/* 2341 */         localObject1 = new NoSuchMethodShim((Callable)localObject2, paramString);
/*      */       else {
/* 2343 */         throw notFunctionError(paramScriptable, localObject1, paramString);
/*      */       }
/*      */     }
/* 2346 */     storeScriptable(paramContext, paramScriptable);
/* 2347 */     return (Callable)localObject1;
/*      */   }
/*      */ 
/*      */   public static Callable getValueFunctionAndThis(Object paramObject, Context paramContext)
/*      */   {
/* 2359 */     if (!(paramObject instanceof Callable)) {
/* 2360 */       throw notFunctionError(paramObject);
/*      */     }
/*      */ 
/* 2363 */     Callable localCallable = (Callable)paramObject;
/* 2364 */     Scriptable localScriptable = null;
/* 2365 */     if ((localCallable instanceof Scriptable)) {
/* 2366 */       localScriptable = ((Scriptable)localCallable).getParentScope();
/*      */     }
/* 2368 */     if (localScriptable == null) {
/* 2369 */       if (paramContext.topCallScope == null) throw new IllegalStateException();
/* 2370 */       localScriptable = paramContext.topCallScope;
/*      */     }
/* 2372 */     if ((localScriptable.getParentScope() != null) && 
/* 2373 */       (!(localScriptable instanceof NativeWith)))
/*      */     {
/* 2376 */       if ((localScriptable instanceof NativeCall))
/*      */       {
/* 2378 */         localScriptable = ScriptableObject.getTopLevelScope(localScriptable);
/*      */       }
/*      */     }
/* 2381 */     storeScriptable(paramContext, localScriptable);
/* 2382 */     return localCallable;
/*      */   }
/*      */ 
/*      */   public static Ref callRef(Callable paramCallable, Scriptable paramScriptable, Object[] paramArrayOfObject, Context paramContext)
/*      */   {
/* 2397 */     if ((paramCallable instanceof RefCallable)) {
/* 2398 */       localObject = (RefCallable)paramCallable;
/* 2399 */       Ref localRef = ((RefCallable)localObject).refCall(paramContext, paramScriptable, paramArrayOfObject);
/* 2400 */       if (localRef == null) {
/* 2401 */         throw new IllegalStateException(localObject.getClass().getName() + ".refCall() returned null");
/*      */       }
/* 2403 */       return localRef;
/*      */     }
/*      */ 
/* 2406 */     Object localObject = getMessage1("msg.no.ref.from.function", toString(paramCallable));
/*      */ 
/* 2408 */     throw constructError("ReferenceError", (String)localObject);
/*      */   }
/*      */ 
/*      */   public static Scriptable newObject(Object paramObject, Context paramContext, Scriptable paramScriptable, Object[] paramArrayOfObject)
/*      */   {
/* 2419 */     if (!(paramObject instanceof Function)) {
/* 2420 */       throw notFunctionError(paramObject);
/*      */     }
/* 2422 */     Function localFunction = (Function)paramObject;
/* 2423 */     return localFunction.construct(paramContext, paramScriptable, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public static Object callSpecial(Context paramContext, Callable paramCallable, Scriptable paramScriptable1, Object[] paramArrayOfObject, Scriptable paramScriptable2, Scriptable paramScriptable3, int paramInt1, String paramString, int paramInt2)
/*      */   {
/* 2432 */     if (paramInt1 == 1) {
/* 2433 */       if ((paramScriptable1.getParentScope() == null) && (NativeGlobal.isEvalFunction(paramCallable))) {
/* 2434 */         return evalSpecial(paramContext, paramScriptable2, paramScriptable3, paramArrayOfObject, paramString, paramInt2);
/*      */       }
/*      */     }
/* 2437 */     else if (paramInt1 == 2) {
/* 2438 */       if (NativeWith.isWithFunction(paramCallable)) {
/* 2439 */         throw Context.reportRuntimeError1("msg.only.from.new", "With");
/*      */       }
/*      */     }
/*      */     else {
/* 2443 */       throw Kit.codeBug();
/*      */     }
/*      */ 
/* 2446 */     return paramCallable.call(paramContext, paramScriptable2, paramScriptable1, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public static Object newSpecial(Context paramContext, Object paramObject, Object[] paramArrayOfObject, Scriptable paramScriptable, int paramInt)
/*      */   {
/* 2453 */     if (paramInt == 1) {
/* 2454 */       if (NativeGlobal.isEvalFunction(paramObject))
/* 2455 */         throw typeError1("msg.not.ctor", "eval");
/*      */     }
/* 2457 */     else if (paramInt == 2) {
/* 2458 */       if (NativeWith.isWithFunction(paramObject))
/* 2459 */         return NativeWith.newWithSpecial(paramContext, paramScriptable, paramArrayOfObject);
/*      */     }
/*      */     else {
/* 2462 */       throw Kit.codeBug();
/*      */     }
/*      */ 
/* 2465 */     return newObject(paramObject, paramContext, paramScriptable, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public static Object applyOrCall(boolean paramBoolean, Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject)
/*      */   {
/* 2477 */     int i = paramArrayOfObject.length;
/* 2478 */     Callable localCallable = getCallable(paramScriptable2);
/*      */ 
/* 2480 */     Scriptable localScriptable = null;
/* 2481 */     if (i != 0) {
/* 2482 */       localScriptable = toObjectOrNull(paramContext, paramArrayOfObject[0]);
/*      */     }
/* 2484 */     if (localScriptable == null)
/*      */     {
/* 2486 */       localScriptable = getTopCallScope(paramContext);
/*      */     }
/*      */     Object[] arrayOfObject;
/* 2490 */     if (paramBoolean)
/*      */     {
/* 2492 */       arrayOfObject = i <= 1 ? emptyArgs : getApplyArguments(paramContext, paramArrayOfObject[1]);
/*      */     }
/* 2496 */     else if (i <= 1) {
/* 2497 */       arrayOfObject = emptyArgs;
/*      */     } else {
/* 2499 */       arrayOfObject = new Object[i - 1];
/* 2500 */       System.arraycopy(paramArrayOfObject, 1, arrayOfObject, 0, i - 1);
/*      */     }
/*      */ 
/* 2504 */     return localCallable.call(paramContext, paramScriptable1, localScriptable, arrayOfObject);
/*      */   }
/*      */ 
/*      */   static Object[] getApplyArguments(Context paramContext, Object paramObject)
/*      */   {
/* 2509 */     if ((paramObject == null) || (paramObject == Undefined.instance))
/* 2510 */       return emptyArgs;
/* 2511 */     if (((paramObject instanceof NativeArray)) || ((paramObject instanceof Arguments))) {
/* 2512 */       return paramContext.getElements((Scriptable)paramObject);
/*      */     }
/* 2514 */     throw typeError0("msg.arg.isnt.array");
/*      */   }
/*      */ 
/*      */   static Callable getCallable(Scriptable paramScriptable)
/*      */   {
/*      */     Callable localCallable;
/* 2521 */     if ((paramScriptable instanceof Callable)) {
/* 2522 */       localCallable = (Callable)paramScriptable;
/*      */     } else {
/* 2524 */       Object localObject = paramScriptable.getDefaultValue(FunctionClass);
/* 2525 */       if (!(localObject instanceof Callable)) {
/* 2526 */         throw notFunctionError(localObject, paramScriptable);
/*      */       }
/* 2528 */       localCallable = (Callable)localObject;
/*      */     }
/* 2530 */     return localCallable;
/*      */   }
/*      */ 
/*      */   public static Object evalSpecial(Context paramContext, Scriptable paramScriptable, Object paramObject, Object[] paramArrayOfObject, String paramString, int paramInt)
/*      */   {
/* 2542 */     if (paramArrayOfObject.length < 1)
/* 2543 */       return Undefined.instance;
/* 2544 */     Object localObject1 = paramArrayOfObject[0];
/* 2545 */     if (!(localObject1 instanceof String)) {
/* 2546 */       if ((paramContext.hasFeature(11)) || (paramContext.hasFeature(9)))
/*      */       {
/* 2549 */         throw Context.reportRuntimeError0("msg.eval.nonstring.strict");
/*      */       }
/* 2551 */       localObject2 = getMessage0("msg.eval.nonstring");
/* 2552 */       Context.reportWarning((String)localObject2);
/* 2553 */       return localObject1;
/*      */     }
/* 2555 */     if (paramString == null) {
/* 2556 */       localObject2 = new int[1];
/* 2557 */       paramString = Context.getSourcePositionFromStack((int[])localObject2);
/* 2558 */       if (paramString != null)
/* 2559 */         paramInt = localObject2[0];
/*      */       else {
/* 2561 */         paramString = "";
/*      */       }
/*      */     }
/* 2564 */     Object localObject2 = makeUrlForGeneratedScript(true, paramString, paramInt);
/*      */ 
/* 2568 */     ErrorReporter localErrorReporter = DefaultErrorReporter.forEval(paramContext.getErrorReporter());
/*      */ 
/* 2570 */     Evaluator localEvaluator = Context.createInterpreter();
/* 2571 */     if (localEvaluator == null) {
/* 2572 */       throw new JavaScriptException("Interpreter not present", paramString, paramInt);
/*      */     }
/*      */ 
/* 2578 */     Script localScript = paramContext.compileString((String)localObject1, localEvaluator, localErrorReporter, (String)localObject2, 1, null);
/*      */ 
/* 2580 */     localEvaluator.setEvalScriptFlag(localScript);
/* 2581 */     Callable localCallable = (Callable)localScript;
/* 2582 */     return localCallable.call(paramContext, paramScriptable, (Scriptable)paramObject, emptyArgs);
/*      */   }
/*      */ 
/*      */   public static String typeof(Object paramObject)
/*      */   {
/* 2590 */     if (paramObject == null)
/* 2591 */       return "object";
/* 2592 */     if (paramObject == Undefined.instance)
/* 2593 */       return "undefined";
/* 2594 */     if ((paramObject instanceof ScriptableObject))
/* 2595 */       return ((ScriptableObject)paramObject).getTypeOf();
/* 2596 */     if ((paramObject instanceof Scriptable))
/* 2597 */       return (paramObject instanceof Callable) ? "function" : "object";
/* 2598 */     if ((paramObject instanceof String))
/* 2599 */       return "string";
/* 2600 */     if ((paramObject instanceof Number))
/* 2601 */       return "number";
/* 2602 */     if ((paramObject instanceof Boolean))
/* 2603 */       return "boolean";
/* 2604 */     throw errorWithClassName("msg.invalid.type", paramObject);
/*      */   }
/*      */ 
/*      */   public static String typeofName(Scriptable paramScriptable, String paramString)
/*      */   {
/* 2612 */     Context localContext = Context.getContext();
/* 2613 */     Scriptable localScriptable = bind(localContext, paramScriptable, paramString);
/* 2614 */     if (localScriptable == null)
/* 2615 */       return "undefined";
/* 2616 */     return typeof(getObjectProp(localScriptable, paramString, localContext));
/*      */   }
/*      */ 
/*      */   public static Object add(Object paramObject1, Object paramObject2, Context paramContext)
/*      */   {
/* 2633 */     if (((paramObject1 instanceof Number)) && ((paramObject2 instanceof Number)))
/* 2634 */       return wrapNumber(((Number)paramObject1).doubleValue() + ((Number)paramObject2).doubleValue());
/*      */     Object localObject;
/* 2637 */     if ((paramObject1 instanceof XMLObject)) {
/* 2638 */       localObject = ((XMLObject)paramObject1).addValues(paramContext, true, paramObject2);
/* 2639 */       if (localObject != Scriptable.NOT_FOUND) {
/* 2640 */         return localObject;
/*      */       }
/*      */     }
/* 2643 */     if ((paramObject2 instanceof XMLObject)) {
/* 2644 */       localObject = ((XMLObject)paramObject2).addValues(paramContext, false, paramObject1);
/* 2645 */       if (localObject != Scriptable.NOT_FOUND) {
/* 2646 */         return localObject;
/*      */       }
/*      */     }
/* 2649 */     if ((paramObject1 instanceof Scriptable))
/* 2650 */       paramObject1 = ((Scriptable)paramObject1).getDefaultValue(null);
/* 2651 */     if ((paramObject2 instanceof Scriptable))
/* 2652 */       paramObject2 = ((Scriptable)paramObject2).getDefaultValue(null);
/* 2653 */     if ((!(paramObject1 instanceof String)) && (!(paramObject2 instanceof String))) {
/* 2654 */       if (((paramObject1 instanceof Number)) && ((paramObject2 instanceof Number))) {
/* 2655 */         return wrapNumber(((Number)paramObject1).doubleValue() + ((Number)paramObject2).doubleValue());
/*      */       }
/*      */ 
/* 2658 */       return wrapNumber(toNumber(paramObject1) + toNumber(paramObject2));
/* 2659 */     }return toString(paramObject1).concat(toString(paramObject2));
/*      */   }
/*      */ 
/*      */   public static String add(String paramString, Object paramObject) {
/* 2663 */     return paramString.concat(toString(paramObject));
/*      */   }
/*      */ 
/*      */   public static String add(Object paramObject, String paramString) {
/* 2667 */     return toString(paramObject).concat(paramString);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Object nameIncrDecr(Scriptable paramScriptable, String paramString, int paramInt)
/*      */   {
/* 2676 */     return nameIncrDecr(paramScriptable, paramString, Context.getContext(), paramInt);
/*      */   }
/*      */ 
/*      */   public static Object nameIncrDecr(Scriptable paramScriptable, String paramString, Context paramContext, int paramInt)
/*      */   {
/*      */     Scriptable localScriptable;
/*      */     Object localObject;
/*      */     do
/*      */     {
/* 2686 */       if ((paramContext.useDynamicScope) && (paramScriptable.getParentScope() == null)) {
/* 2687 */         paramScriptable = checkDynamicScope(paramContext.topCallScope, paramScriptable);
/*      */       }
/* 2689 */       localScriptable = paramScriptable;
/*      */       do {
/* 2691 */         localObject = localScriptable.get(paramString, paramScriptable);
/* 2692 */         if (localObject != Scriptable.NOT_FOUND) {
/*      */           break;
/*      */         }
/* 2695 */         localScriptable = localScriptable.getPrototype();
/* 2696 */       }while (localScriptable != null);
/* 2697 */       paramScriptable = paramScriptable.getParentScope();
/* 2698 */     }while (paramScriptable != null);
/* 2699 */     throw notFoundError(paramScriptable, paramString);
/*      */ 
/* 2701 */     return doScriptableIncrDecr(localScriptable, paramString, paramScriptable, localObject, paramInt);
/*      */   }
/*      */ 
/*      */   public static Object propIncrDecr(Object paramObject, String paramString, Context paramContext, int paramInt)
/*      */   {
/* 2708 */     Scriptable localScriptable1 = toObjectOrNull(paramContext, paramObject);
/* 2709 */     if (localScriptable1 == null) {
/* 2710 */       throw undefReadError(paramObject, paramString);
/*      */     }
/*      */ 
/* 2713 */     Scriptable localScriptable2 = localScriptable1;
/*      */     Object localObject;
/*      */     do {
/* 2717 */       localObject = localScriptable2.get(paramString, localScriptable1);
/* 2718 */       if (localObject != Scriptable.NOT_FOUND) {
/*      */         break;
/*      */       }
/* 2721 */       localScriptable2 = localScriptable2.getPrototype();
/* 2722 */     }while (localScriptable2 != null);
/* 2723 */     localScriptable1.put(paramString, localScriptable1, NaNobj);
/* 2724 */     return NaNobj;
/*      */ 
/* 2726 */     return doScriptableIncrDecr(localScriptable2, paramString, localScriptable1, localObject, paramInt);
/*      */   }
/*      */ 
/*      */   private static Object doScriptableIncrDecr(Scriptable paramScriptable1, String paramString, Scriptable paramScriptable2, Object paramObject, int paramInt)
/*      */   {
/* 2736 */     int i = (paramInt & 0x2) != 0 ? 1 : 0;
/*      */     double d;
/* 2738 */     if ((paramObject instanceof Number)) {
/* 2739 */       d = ((Number)paramObject).doubleValue();
/*      */     } else {
/* 2741 */       d = toNumber(paramObject);
/* 2742 */       if (i != 0)
/*      */       {
/* 2744 */         paramObject = wrapNumber(d);
/*      */       }
/*      */     }
/* 2747 */     if ((paramInt & 0x1) == 0)
/* 2748 */       d += 1.0D;
/*      */     else {
/* 2750 */       d -= 1.0D;
/*      */     }
/* 2752 */     Number localNumber = wrapNumber(d);
/* 2753 */     paramScriptable1.put(paramString, paramScriptable2, localNumber);
/* 2754 */     if (i != 0) {
/* 2755 */       return paramObject;
/*      */     }
/* 2757 */     return localNumber;
/*      */   }
/*      */ 
/*      */   public static Object elemIncrDecr(Object paramObject1, Object paramObject2, Context paramContext, int paramInt)
/*      */   {
/* 2764 */     Object localObject = getObjectElem(paramObject1, paramObject2, paramContext);
/* 2765 */     int i = (paramInt & 0x2) != 0 ? 1 : 0;
/*      */     double d;
/* 2767 */     if ((localObject instanceof Number)) {
/* 2768 */       d = ((Number)localObject).doubleValue();
/*      */     } else {
/* 2770 */       d = toNumber(localObject);
/* 2771 */       if (i != 0)
/*      */       {
/* 2773 */         localObject = wrapNumber(d);
/*      */       }
/*      */     }
/* 2776 */     if ((paramInt & 0x1) == 0)
/* 2777 */       d += 1.0D;
/*      */     else {
/* 2779 */       d -= 1.0D;
/*      */     }
/* 2781 */     Number localNumber = wrapNumber(d);
/* 2782 */     setObjectElem(paramObject1, paramObject2, localNumber, paramContext);
/* 2783 */     if (i != 0) {
/* 2784 */       return localObject;
/*      */     }
/* 2786 */     return localNumber;
/*      */   }
/*      */ 
/*      */   public static Object refIncrDecr(Ref paramRef, Context paramContext, int paramInt)
/*      */   {
/* 2792 */     Object localObject = paramRef.get(paramContext);
/* 2793 */     int i = (paramInt & 0x2) != 0 ? 1 : 0;
/*      */     double d;
/* 2795 */     if ((localObject instanceof Number)) {
/* 2796 */       d = ((Number)localObject).doubleValue();
/*      */     } else {
/* 2798 */       d = toNumber(localObject);
/* 2799 */       if (i != 0)
/*      */       {
/* 2801 */         localObject = wrapNumber(d);
/*      */       }
/*      */     }
/* 2804 */     if ((paramInt & 0x1) == 0)
/* 2805 */       d += 1.0D;
/*      */     else {
/* 2807 */       d -= 1.0D;
/*      */     }
/* 2809 */     Number localNumber = wrapNumber(d);
/* 2810 */     paramRef.set(paramContext, localNumber);
/* 2811 */     if (i != 0) {
/* 2812 */       return localObject;
/*      */     }
/* 2814 */     return localNumber;
/*      */   }
/*      */ 
/*      */   public static Object toPrimitive(Object paramObject)
/*      */   {
/* 2819 */     return toPrimitive(paramObject, null);
/*      */   }
/*      */ 
/*      */   public static Object toPrimitive(Object paramObject, Class<?> paramClass)
/*      */   {
/* 2824 */     if (!(paramObject instanceof Scriptable)) {
/* 2825 */       return paramObject;
/*      */     }
/* 2827 */     Scriptable localScriptable = (Scriptable)paramObject;
/* 2828 */     Object localObject = localScriptable.getDefaultValue(paramClass);
/* 2829 */     if ((localObject instanceof Scriptable))
/* 2830 */       throw typeError0("msg.bad.default.value");
/* 2831 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static boolean eq(Object paramObject1, Object paramObject2)
/*      */   {
/* 2841 */     if ((paramObject1 == null) || (paramObject1 == Undefined.instance)) {
/* 2842 */       if ((paramObject2 == null) || (paramObject2 == Undefined.instance)) {
/* 2843 */         return true;
/*      */       }
/* 2845 */       if ((paramObject2 instanceof ScriptableObject)) {
/* 2846 */         Object localObject1 = ((ScriptableObject)paramObject2).equivalentValues(paramObject1);
/* 2847 */         if (localObject1 != Scriptable.NOT_FOUND) {
/* 2848 */           return ((Boolean)localObject1).booleanValue();
/*      */         }
/*      */       }
/* 2851 */       return false;
/* 2852 */     }if ((paramObject1 instanceof Number))
/* 2853 */       return eqNumber(((Number)paramObject1).doubleValue(), paramObject2);
/* 2854 */     if ((paramObject1 instanceof String))
/* 2855 */       return eqString((String)paramObject1, paramObject2);
/*      */     Object localObject3;
/* 2856 */     if ((paramObject1 instanceof Boolean)) {
/* 2857 */       boolean bool = ((Boolean)paramObject1).booleanValue();
/* 2858 */       if ((paramObject2 instanceof Boolean)) {
/* 2859 */         return bool == ((Boolean)paramObject2).booleanValue();
/*      */       }
/* 2861 */       if ((paramObject2 instanceof ScriptableObject)) {
/* 2862 */         localObject3 = ((ScriptableObject)paramObject2).equivalentValues(paramObject1);
/* 2863 */         if (localObject3 != Scriptable.NOT_FOUND) {
/* 2864 */           return ((Boolean)localObject3).booleanValue();
/*      */         }
/*      */       }
/* 2867 */       return eqNumber(bool ? 1.0D : 0.0D, paramObject2);
/* 2868 */     }if ((paramObject1 instanceof Scriptable))
/*      */     {
/*      */       Object localObject2;
/* 2869 */       if ((paramObject2 instanceof Scriptable)) {
/* 2870 */         if (paramObject1 == paramObject2) {
/* 2871 */           return true;
/*      */         }
/* 2873 */         if ((paramObject1 instanceof ScriptableObject)) {
/* 2874 */           localObject2 = ((ScriptableObject)paramObject1).equivalentValues(paramObject2);
/* 2875 */           if (localObject2 != Scriptable.NOT_FOUND) {
/* 2876 */             return ((Boolean)localObject2).booleanValue();
/*      */           }
/*      */         }
/* 2879 */         if ((paramObject2 instanceof ScriptableObject)) {
/* 2880 */           localObject2 = ((ScriptableObject)paramObject2).equivalentValues(paramObject1);
/* 2881 */           if (localObject2 != Scriptable.NOT_FOUND) {
/* 2882 */             return ((Boolean)localObject2).booleanValue();
/*      */           }
/*      */         }
/* 2885 */         if (((paramObject1 instanceof Wrapper)) && ((paramObject2 instanceof Wrapper)))
/*      */         {
/* 2888 */           localObject2 = ((Wrapper)paramObject1).unwrap();
/* 2889 */           localObject3 = ((Wrapper)paramObject2).unwrap();
/* 2890 */           return (localObject2 == localObject3) || ((isPrimitive(localObject2)) && (isPrimitive(localObject3)) && (eq(localObject2, localObject3)));
/*      */         }
/*      */ 
/* 2895 */         return false;
/* 2896 */       }if ((paramObject2 instanceof Boolean)) {
/* 2897 */         if ((paramObject1 instanceof ScriptableObject)) {
/* 2898 */           localObject2 = ((ScriptableObject)paramObject1).equivalentValues(paramObject2);
/* 2899 */           if (localObject2 != Scriptable.NOT_FOUND) {
/* 2900 */             return ((Boolean)localObject2).booleanValue();
/*      */           }
/*      */         }
/* 2903 */         double d = ((Boolean)paramObject2).booleanValue() ? 1.0D : 0.0D;
/* 2904 */         return eqNumber(d, paramObject1);
/* 2905 */       }if ((paramObject2 instanceof Number))
/* 2906 */         return eqNumber(((Number)paramObject2).doubleValue(), paramObject1);
/* 2907 */       if ((paramObject2 instanceof String)) {
/* 2908 */         return eqString((String)paramObject2, paramObject1);
/*      */       }
/*      */ 
/* 2911 */       return false;
/*      */     }
/* 2913 */     warnAboutNonJSObject(paramObject1);
/* 2914 */     return paramObject1 == paramObject2;
/*      */   }
/*      */ 
/*      */   public static boolean isPrimitive(Object paramObject)
/*      */   {
/* 2919 */     return (paramObject == null) || (paramObject == Undefined.instance) || ((paramObject instanceof Number)) || ((paramObject instanceof String)) || ((paramObject instanceof Boolean));
/*      */   }
/*      */ 
/*      */   static boolean eqNumber(double paramDouble, Object paramObject)
/*      */   {
/*      */     while (true)
/*      */     {
/* 2927 */       if ((paramObject == null) || (paramObject == Undefined.instance))
/* 2928 */         return false;
/* 2929 */       if ((paramObject instanceof Number))
/* 2930 */         return paramDouble == ((Number)paramObject).doubleValue();
/* 2931 */       if ((paramObject instanceof String))
/* 2932 */         return paramDouble == toNumber(paramObject);
/* 2933 */       if ((paramObject instanceof Boolean))
/* 2934 */         return paramDouble == (((Boolean)paramObject).booleanValue() ? 1.0D : 0.0D);
/* 2935 */       if (!(paramObject instanceof Scriptable)) break;
/* 2936 */       if ((paramObject instanceof ScriptableObject)) {
/* 2937 */         Number localNumber = wrapNumber(paramDouble);
/* 2938 */         Object localObject = ((ScriptableObject)paramObject).equivalentValues(localNumber);
/* 2939 */         if (localObject != Scriptable.NOT_FOUND) {
/* 2940 */           return ((Boolean)localObject).booleanValue();
/*      */         }
/*      */       }
/* 2943 */       paramObject = toPrimitive(paramObject);
/*      */     }
/* 2945 */     warnAboutNonJSObject(paramObject);
/* 2946 */     return false;
/*      */   }
/*      */ 
/*      */   private static boolean eqString(String paramString, Object paramObject)
/*      */   {
/*      */     while (true)
/*      */     {
/* 2954 */       if ((paramObject == null) || (paramObject == Undefined.instance))
/* 2955 */         return false;
/* 2956 */       if ((paramObject instanceof String))
/* 2957 */         return paramString.equals(paramObject);
/* 2958 */       if ((paramObject instanceof Number))
/* 2959 */         return toNumber(paramString) == ((Number)paramObject).doubleValue();
/* 2960 */       if ((paramObject instanceof Boolean))
/* 2961 */         return toNumber(paramString) == (((Boolean)paramObject).booleanValue() ? 1.0D : 0.0D);
/* 2962 */       if (!(paramObject instanceof Scriptable)) break;
/* 2963 */       if ((paramObject instanceof ScriptableObject)) {
/* 2964 */         Object localObject = ((ScriptableObject)paramObject).equivalentValues(paramString);
/* 2965 */         if (localObject != Scriptable.NOT_FOUND) {
/* 2966 */           return ((Boolean)localObject).booleanValue();
/*      */         }
/*      */       }
/* 2969 */       paramObject = toPrimitive(paramObject);
/*      */     }
/*      */ 
/* 2972 */     warnAboutNonJSObject(paramObject);
/* 2973 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean shallowEq(Object paramObject1, Object paramObject2)
/*      */   {
/* 2979 */     if (paramObject1 == paramObject2) {
/* 2980 */       if (!(paramObject1 instanceof Number)) {
/* 2981 */         return true;
/*      */       }
/*      */ 
/* 2984 */       double d = ((Number)paramObject1).doubleValue();
/* 2985 */       return d == d;
/*      */     }
/* 2987 */     if ((paramObject1 == null) || (paramObject1 == Undefined.instance))
/* 2988 */       return false;
/* 2989 */     if ((paramObject1 instanceof Number)) {
/* 2990 */       if ((paramObject2 instanceof Number))
/* 2991 */         return ((Number)paramObject1).doubleValue() == ((Number)paramObject2).doubleValue();
/*      */     }
/* 2993 */     else if ((paramObject1 instanceof String)) {
/* 2994 */       if ((paramObject2 instanceof String))
/* 2995 */         return paramObject1.equals(paramObject2);
/*      */     }
/* 2997 */     else if ((paramObject1 instanceof Boolean)) {
/* 2998 */       if ((paramObject2 instanceof Boolean))
/* 2999 */         return paramObject1.equals(paramObject2);
/*      */     }
/* 3001 */     else if ((paramObject1 instanceof Scriptable)) {
/* 3002 */       if (((paramObject1 instanceof Wrapper)) && ((paramObject2 instanceof Wrapper)))
/* 3003 */         return ((Wrapper)paramObject1).unwrap() == ((Wrapper)paramObject2).unwrap();
/*      */     }
/*      */     else {
/* 3006 */       warnAboutNonJSObject(paramObject1);
/* 3007 */       return paramObject1 == paramObject2;
/*      */     }
/* 3009 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean instanceOf(Object paramObject1, Object paramObject2, Context paramContext)
/*      */   {
/* 3020 */     if (!(paramObject2 instanceof Scriptable)) {
/* 3021 */       throw typeError0("msg.instanceof.not.object");
/*      */     }
/*      */ 
/* 3025 */     if (!(paramObject1 instanceof Scriptable)) {
/* 3026 */       return false;
/*      */     }
/* 3028 */     return ((Scriptable)paramObject2).hasInstance((Scriptable)paramObject1);
/*      */   }
/*      */ 
/*      */   public static boolean jsDelegatesTo(Scriptable paramScriptable1, Scriptable paramScriptable2)
/*      */   {
/* 3037 */     Scriptable localScriptable = paramScriptable1.getPrototype();
/*      */ 
/* 3039 */     while (localScriptable != null) {
/* 3040 */       if (localScriptable.equals(paramScriptable2)) return true;
/* 3041 */       localScriptable = localScriptable.getPrototype();
/*      */     }
/*      */ 
/* 3044 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean in(Object paramObject1, Object paramObject2, Context paramContext)
/*      */   {
/* 3063 */     if (!(paramObject2 instanceof Scriptable)) {
/* 3064 */       throw typeError0("msg.instanceof.not.object");
/*      */     }
/*      */ 
/* 3067 */     return hasObjectElem((Scriptable)paramObject2, paramObject1, paramContext);
/*      */   }
/*      */ 
/*      */   public static boolean cmp_LT(Object paramObject1, Object paramObject2)
/*      */   {
/*      */     double d1;
/*      */     double d2;
/* 3073 */     if (((paramObject1 instanceof Number)) && ((paramObject2 instanceof Number))) {
/* 3074 */       d1 = ((Number)paramObject1).doubleValue();
/* 3075 */       d2 = ((Number)paramObject2).doubleValue();
/*      */     } else {
/* 3077 */       if ((paramObject1 instanceof Scriptable))
/* 3078 */         paramObject1 = ((Scriptable)paramObject1).getDefaultValue(NumberClass);
/* 3079 */       if ((paramObject2 instanceof Scriptable))
/* 3080 */         paramObject2 = ((Scriptable)paramObject2).getDefaultValue(NumberClass);
/* 3081 */       if (((paramObject1 instanceof String)) && ((paramObject2 instanceof String))) {
/* 3082 */         return ((String)paramObject1).compareTo((String)paramObject2) < 0;
/*      */       }
/* 3084 */       d1 = toNumber(paramObject1);
/* 3085 */       d2 = toNumber(paramObject2);
/*      */     }
/* 3087 */     return d1 < d2;
/*      */   }
/*      */ 
/*      */   public static boolean cmp_LE(Object paramObject1, Object paramObject2)
/*      */   {
/*      */     double d1;
/*      */     double d2;
/* 3093 */     if (((paramObject1 instanceof Number)) && ((paramObject2 instanceof Number))) {
/* 3094 */       d1 = ((Number)paramObject1).doubleValue();
/* 3095 */       d2 = ((Number)paramObject2).doubleValue();
/*      */     } else {
/* 3097 */       if ((paramObject1 instanceof Scriptable))
/* 3098 */         paramObject1 = ((Scriptable)paramObject1).getDefaultValue(NumberClass);
/* 3099 */       if ((paramObject2 instanceof Scriptable))
/* 3100 */         paramObject2 = ((Scriptable)paramObject2).getDefaultValue(NumberClass);
/* 3101 */       if (((paramObject1 instanceof String)) && ((paramObject2 instanceof String))) {
/* 3102 */         return ((String)paramObject1).compareTo((String)paramObject2) <= 0;
/*      */       }
/* 3104 */       d1 = toNumber(paramObject1);
/* 3105 */       d2 = toNumber(paramObject2);
/*      */     }
/* 3107 */     return d1 <= d2;
/*      */   }
/*      */ 
/*      */   public static ScriptableObject getGlobal(Context paramContext)
/*      */   {
/* 3116 */     Class localClass = Kit.classOrNull("sun.org.mozilla.javascript.internal.tools.shell.Global");
/* 3117 */     if (localClass != null) {
/*      */       try {
/* 3119 */         Class[] arrayOfClass = { ContextClass };
/* 3120 */         Constructor localConstructor = localClass.getConstructor(arrayOfClass);
/* 3121 */         Object[] arrayOfObject = { paramContext };
/* 3122 */         return (ScriptableObject)localConstructor.newInstance(arrayOfObject);
/*      */       }
/*      */       catch (RuntimeException localRuntimeException) {
/* 3125 */         throw localRuntimeException;
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*      */       }
/*      */     }
/* 3131 */     return new ImporterTopLevel(paramContext);
/*      */   }
/*      */ 
/*      */   public static boolean hasTopCall(Context paramContext)
/*      */   {
/* 3136 */     return paramContext.topCallScope != null;
/*      */   }
/*      */ 
/*      */   public static Scriptable getTopCallScope(Context paramContext)
/*      */   {
/* 3141 */     Scriptable localScriptable = paramContext.topCallScope;
/* 3142 */     if (localScriptable == null) {
/* 3143 */       throw new IllegalStateException();
/*      */     }
/* 3145 */     return localScriptable;
/*      */   }
/*      */ 
/*      */   public static Object doTopCall(Callable paramCallable, Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject)
/*      */   {
/* 3152 */     if (paramScriptable1 == null)
/* 3153 */       throw new IllegalArgumentException();
/* 3154 */     if (paramContext.topCallScope != null) throw new IllegalStateException(); 
/*      */ paramContext.topCallScope = ScriptableObject.getTopLevelScope(paramScriptable1);
/* 3158 */     paramContext.useDynamicScope = paramContext.hasFeature(7);
/* 3159 */     ContextFactory localContextFactory = paramContext.getFactory();
/*      */     Object localObject1;
/*      */     try { localObject1 = localContextFactory.doTopCall(paramCallable, paramContext, paramScriptable1, paramScriptable2, paramArrayOfObject);
/*      */     } finally {
/* 3163 */       paramContext.topCallScope = null;
/*      */ 
/* 3165 */       paramContext.cachedXMLLib = null;
/*      */ 
/* 3167 */       if (paramContext.currentActivationCall != null)
/*      */       {
/* 3170 */         throw new IllegalStateException();
/*      */       }
/*      */     }
/* 3173 */     return localObject1;
/*      */   }
/*      */ 
/*      */   static Scriptable checkDynamicScope(Scriptable paramScriptable1, Scriptable paramScriptable2)
/*      */   {
/* 3186 */     if (paramScriptable1 == paramScriptable2) {
/* 3187 */       return paramScriptable1;
/*      */     }
/* 3189 */     Scriptable localScriptable = paramScriptable1;
/*      */     do {
/* 3191 */       localScriptable = localScriptable.getPrototype();
/* 3192 */       if (localScriptable == paramScriptable2)
/* 3193 */         return paramScriptable1;
/*      */     }
/* 3195 */     while (localScriptable != null);
/* 3196 */     return paramScriptable2;
/*      */   }
/*      */ 
/*      */   public static void addInstructionCount(Context paramContext, int paramInt)
/*      */   {
/* 3203 */     paramContext.instructionCount += paramInt;
/* 3204 */     if (paramContext.instructionCount > paramContext.instructionThreshold)
/*      */     {
/* 3206 */       paramContext.observeInstructionCount(paramContext.instructionCount);
/* 3207 */       paramContext.instructionCount = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void initScript(NativeFunction paramNativeFunction, Scriptable paramScriptable1, Context paramContext, Scriptable paramScriptable2, boolean paramBoolean)
/*      */   {
/* 3215 */     if (paramContext.topCallScope == null) {
/* 3216 */       throw new IllegalStateException();
/*      */     }
/* 3218 */     int i = paramNativeFunction.getParamAndVarCount();
/*      */     Scriptable localScriptable;
/*      */     int j;
/* 3219 */     if (i != 0)
/*      */     {
/* 3221 */       localScriptable = paramScriptable2;
/*      */ 
/* 3224 */       while ((localScriptable instanceof NativeWith)) {
/* 3225 */         localScriptable = localScriptable.getParentScope();
/*      */       }
/*      */ 
/* 3228 */       for (j = i; j-- != 0; ) {
/* 3229 */         String str = paramNativeFunction.getParamOrVarName(j);
/* 3230 */         boolean bool = paramNativeFunction.getParamOrVarConst(j);
/*      */ 
/* 3233 */         if (!ScriptableObject.hasProperty(paramScriptable2, str)) {
/* 3234 */           if (!paramBoolean)
/*      */           {
/* 3236 */             if (bool)
/* 3237 */               ScriptableObject.defineConstProperty(localScriptable, str);
/*      */             else {
/* 3239 */               ScriptableObject.defineProperty(localScriptable, str, Undefined.instance, 4);
/*      */             }
/*      */           }
/*      */           else
/* 3243 */             localScriptable.put(str, localScriptable, Undefined.instance);
/*      */         }
/*      */         else
/* 3246 */           ScriptableObject.redefineProperty(paramScriptable2, str, bool);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Scriptable createFunctionActivation(NativeFunction paramNativeFunction, Scriptable paramScriptable, Object[] paramArrayOfObject)
/*      */   {
/* 3256 */     return new NativeCall(paramNativeFunction, paramScriptable, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public static void enterActivationFunction(Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 3263 */     if (paramContext.topCallScope == null)
/* 3264 */       throw new IllegalStateException();
/* 3265 */     NativeCall localNativeCall = (NativeCall)paramScriptable;
/* 3266 */     localNativeCall.parentActivationCall = paramContext.currentActivationCall;
/* 3267 */     paramContext.currentActivationCall = localNativeCall;
/*      */   }
/*      */ 
/*      */   public static void exitActivationFunction(Context paramContext)
/*      */   {
/* 3272 */     NativeCall localNativeCall = paramContext.currentActivationCall;
/* 3273 */     paramContext.currentActivationCall = localNativeCall.parentActivationCall;
/* 3274 */     localNativeCall.parentActivationCall = null;
/*      */   }
/*      */ 
/*      */   static NativeCall findFunctionActivation(Context paramContext, Function paramFunction)
/*      */   {
/* 3279 */     NativeCall localNativeCall = paramContext.currentActivationCall;
/* 3280 */     while (localNativeCall != null) {
/* 3281 */       if (localNativeCall.function == paramFunction)
/* 3282 */         return localNativeCall;
/* 3283 */       localNativeCall = localNativeCall.parentActivationCall;
/*      */     }
/* 3285 */     return null;
/*      */   }
/*      */ 
/*      */   public static Scriptable newCatchScope(Throwable paramThrowable, Scriptable paramScriptable1, String paramString, Context paramContext, Scriptable paramScriptable2)
/*      */   {
/*      */     int i;
/*      */     Object localObject1;
/* 3297 */     if ((paramThrowable instanceof JavaScriptException)) {
/* 3298 */       i = 0;
/* 3299 */       localObject1 = ((JavaScriptException)paramThrowable).getValue();
/*      */     } else {
/* 3301 */       i = 1;
/*      */ 
/* 3306 */       if (paramScriptable1 != null) {
/* 3307 */         localObject2 = (NativeObject)paramScriptable1;
/* 3308 */         localObject1 = ((NativeObject)localObject2).getAssociatedValue(paramThrowable);
/* 3309 */         if (localObject1 == null) Kit.codeBug();
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 3316 */         Throwable localThrowable = null;
/*      */         String str1;
/*      */         String str2;
/* 3318 */         if ((paramThrowable instanceof EcmaError)) {
/* 3319 */           localObject3 = (EcmaError)paramThrowable;
/* 3320 */           localObject2 = localObject3;
/* 3321 */           str1 = ((EcmaError)localObject3).getName();
/* 3322 */           str2 = ((EcmaError)localObject3).getErrorMessage();
/* 3323 */         } else if ((paramThrowable instanceof WrappedException)) {
/* 3324 */           localObject3 = (WrappedException)paramThrowable;
/* 3325 */           localObject2 = localObject3;
/* 3326 */           localThrowable = ((WrappedException)localObject3).getWrappedException();
/* 3327 */           str1 = "JavaException";
/* 3328 */           str2 = localThrowable.getClass().getName() + ": " + localThrowable.getMessage();
/*      */         }
/* 3330 */         else if ((paramThrowable instanceof EvaluatorException))
/*      */         {
/* 3332 */           localObject3 = (EvaluatorException)paramThrowable;
/* 3333 */           localObject2 = localObject3;
/* 3334 */           str1 = "InternalError";
/* 3335 */           str2 = ((EvaluatorException)localObject3).getMessage();
/* 3336 */         } else if (paramContext.hasFeature(13))
/*      */         {
/* 3339 */           localObject2 = new WrappedException(paramThrowable);
/* 3340 */           str1 = "JavaException";
/* 3341 */           str2 = paramThrowable.toString();
/*      */         }
/*      */         else
/*      */         {
/* 3345 */           throw Kit.codeBug();
/*      */         }
/*      */ 
/* 3348 */         Object localObject3 = ((RhinoException)localObject2).sourceName();
/* 3349 */         if (localObject3 == null) {
/* 3350 */           localObject3 = "";
/*      */         }
/* 3352 */         int j = ((RhinoException)localObject2).lineNumber();
/*      */         Object[] arrayOfObject;
/* 3354 */         if (j > 0)
/* 3355 */           arrayOfObject = new Object[] { str2, localObject3, Integer.valueOf(j) };
/*      */         else {
/* 3357 */           arrayOfObject = new Object[] { str2, localObject3 };
/*      */         }
/*      */ 
/* 3360 */         Scriptable localScriptable = paramContext.newObject(paramScriptable2, str1, arrayOfObject);
/* 3361 */         ScriptableObject.putProperty(localScriptable, "name", str1);
/*      */ 
/* 3363 */         if ((localScriptable instanceof NativeError))
/* 3364 */           ((NativeError)localScriptable).setStackProvider((RhinoException)localObject2);
/*      */         Object localObject4;
/* 3367 */         if ((localThrowable != null) && (isVisible(paramContext, localThrowable))) {
/* 3368 */           localObject4 = paramContext.getWrapFactory().wrap(paramContext, paramScriptable2, localThrowable, null);
/*      */ 
/* 3370 */           ScriptableObject.defineProperty(localScriptable, "javaException", localObject4, 5);
/*      */         }
/*      */ 
/* 3374 */         if (isVisible(paramContext, localObject2)) {
/* 3375 */           localObject4 = paramContext.getWrapFactory().wrap(paramContext, paramScriptable2, localObject2, null);
/* 3376 */           ScriptableObject.defineProperty(localScriptable, "rhinoException", localObject4, 5);
/*      */         }
/*      */ 
/* 3380 */         localObject1 = localScriptable;
/*      */       }
/*      */     }
/* 3383 */     Object localObject2 = new NativeObject();
/*      */ 
/* 3385 */     ((NativeObject)localObject2).defineProperty(paramString, localObject1, 4);
/*      */ 
/* 3388 */     if (isVisible(paramContext, paramThrowable))
/*      */     {
/* 3392 */       ((NativeObject)localObject2).defineProperty("__exception__", Context.javaToJS(paramThrowable, paramScriptable2), 6);
/*      */     }
/*      */ 
/* 3397 */     if (i != 0) {
/* 3398 */       ((NativeObject)localObject2).associateValue(paramThrowable, localObject1);
/*      */     }
/* 3400 */     return localObject2;
/*      */   }
/*      */ 
/*      */   private static boolean isVisible(Context paramContext, Object paramObject) {
/* 3404 */     ClassShutter localClassShutter = paramContext.getClassShutter();
/* 3405 */     return (localClassShutter == null) || (localClassShutter.visibleToScripts(paramObject.getClass().getName()));
/*      */   }
/*      */ 
/*      */   public static Scriptable enterWith(Object paramObject, Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 3412 */     Scriptable localScriptable = toObjectOrNull(paramContext, paramObject);
/* 3413 */     if (localScriptable == null) {
/* 3414 */       throw typeError1("msg.undef.with", toString(paramObject));
/*      */     }
/* 3416 */     if ((localScriptable instanceof XMLObject)) {
/* 3417 */       XMLObject localXMLObject = (XMLObject)localScriptable;
/* 3418 */       return localXMLObject.enterWith(paramScriptable);
/*      */     }
/* 3420 */     return new NativeWith(paramScriptable, localScriptable);
/*      */   }
/*      */ 
/*      */   public static Scriptable leaveWith(Scriptable paramScriptable)
/*      */   {
/* 3425 */     NativeWith localNativeWith = (NativeWith)paramScriptable;
/* 3426 */     return localNativeWith.getParentScope();
/*      */   }
/*      */ 
/*      */   public static Scriptable enterDotQuery(Object paramObject, Scriptable paramScriptable)
/*      */   {
/* 3431 */     if (!(paramObject instanceof XMLObject)) {
/* 3432 */       throw notXmlError(paramObject);
/*      */     }
/* 3434 */     XMLObject localXMLObject = (XMLObject)paramObject;
/* 3435 */     return localXMLObject.enterDotQuery(paramScriptable);
/*      */   }
/*      */ 
/*      */   public static Object updateDotQuery(boolean paramBoolean, Scriptable paramScriptable)
/*      */   {
/* 3441 */     NativeWith localNativeWith = (NativeWith)paramScriptable;
/* 3442 */     return localNativeWith.updateDotQuery(paramBoolean);
/*      */   }
/*      */ 
/*      */   public static Scriptable leaveDotQuery(Scriptable paramScriptable)
/*      */   {
/* 3447 */     NativeWith localNativeWith = (NativeWith)paramScriptable;
/* 3448 */     return localNativeWith.getParentScope();
/*      */   }
/*      */ 
/*      */   public static void setFunctionProtoAndParent(BaseFunction paramBaseFunction, Scriptable paramScriptable)
/*      */   {
/* 3454 */     paramBaseFunction.setParentScope(paramScriptable);
/* 3455 */     paramBaseFunction.setPrototype(ScriptableObject.getFunctionPrototype(paramScriptable));
/*      */   }
/*      */ 
/*      */   public static void setObjectProtoAndParent(ScriptableObject paramScriptableObject, Scriptable paramScriptable)
/*      */   {
/* 3462 */     paramScriptable = ScriptableObject.getTopLevelScope(paramScriptable);
/* 3463 */     paramScriptableObject.setParentScope(paramScriptable);
/* 3464 */     Scriptable localScriptable = ScriptableObject.getClassPrototype(paramScriptable, paramScriptableObject.getClassName());
/*      */ 
/* 3466 */     paramScriptableObject.setPrototype(localScriptable);
/*      */   }
/*      */ 
/*      */   public static void initFunction(Context paramContext, Scriptable paramScriptable, NativeFunction paramNativeFunction, int paramInt, boolean paramBoolean)
/*      */   {
/*      */     String str;
/* 3473 */     if (paramInt == 1) {
/* 3474 */       str = paramNativeFunction.getFunctionName();
/* 3475 */       if ((str != null) && (str.length() != 0)) {
/* 3476 */         if (!paramBoolean)
/*      */         {
/* 3479 */           ScriptableObject.defineProperty(paramScriptable, str, paramNativeFunction, 4);
/*      */         }
/*      */         else
/* 3482 */           paramScriptable.put(str, paramScriptable, paramNativeFunction);
/*      */       }
/*      */     }
/* 3485 */     else if (paramInt == 3) {
/* 3486 */       str = paramNativeFunction.getFunctionName();
/* 3487 */       if ((str != null) && (str.length() != 0))
/*      */       {
/* 3491 */         while ((paramScriptable instanceof NativeWith)) {
/* 3492 */           paramScriptable = paramScriptable.getParentScope();
/*      */         }
/* 3494 */         paramScriptable.put(str, paramScriptable, paramNativeFunction);
/*      */       }
/*      */     } else {
/* 3497 */       throw Kit.codeBug();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Scriptable newArrayLiteral(Object[] paramArrayOfObject, int[] paramArrayOfInt, Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 3506 */     int i = paramArrayOfObject.length;
/* 3507 */     int j = 0;
/* 3508 */     if (paramArrayOfInt != null) {
/* 3509 */       j = paramArrayOfInt.length;
/*      */     }
/* 3511 */     int k = i + j;
/* 3512 */     if ((k > 1) && (j * 2 < k))
/*      */     {
/* 3515 */       if (j == 0) {
/* 3516 */         localObject = paramArrayOfObject;
/*      */       } else {
/* 3518 */         localObject = new Object[k];
/* 3519 */         int m = 0;
/* 3520 */         i1 = 0; for (i2 = 0; i1 != k; i1++)
/* 3521 */           if ((m != j) && (paramArrayOfInt[m] == i1)) {
/* 3522 */             localObject[i1] = Scriptable.NOT_FOUND;
/* 3523 */             m++;
/*      */           }
/*      */           else {
/* 3526 */             localObject[i1] = paramArrayOfObject[i2];
/* 3527 */             i2++;
/*      */           }
/*      */       }
/* 3530 */       NativeArray localNativeArray = new NativeArray((Object[])localObject);
/* 3531 */       setObjectProtoAndParent(localNativeArray, paramScriptable);
/* 3532 */       return localNativeArray;
/*      */     }
/*      */ 
/* 3535 */     Object localObject = new NativeArray(k);
/* 3536 */     setObjectProtoAndParent((ScriptableObject)localObject, paramScriptable);
/*      */ 
/* 3538 */     int n = 0;
/* 3539 */     int i1 = 0; for (int i2 = 0; i1 != k; i1++)
/* 3540 */       if ((n != j) && (paramArrayOfInt[n] == i1)) {
/* 3541 */         n++;
/*      */       }
/*      */       else {
/* 3544 */         ScriptableObject.putProperty((Scriptable)localObject, i1, paramArrayOfObject[i2]);
/* 3545 */         i2++;
/*      */       }
/* 3547 */     return localObject;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Scriptable newObjectLiteral(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 3562 */     int[] arrayOfInt = new int[paramArrayOfObject1.length];
/* 3563 */     return newObjectLiteral(paramArrayOfObject1, paramArrayOfObject2, arrayOfInt, paramContext, paramScriptable);
/*      */   }
/*      */ 
/*      */   public static Scriptable newObjectLiteral(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, int[] paramArrayOfInt, Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 3572 */     Scriptable localScriptable = paramContext.newObject(paramScriptable);
/* 3573 */     int i = 0; for (int j = paramArrayOfObject1.length; i != j; i++) {
/* 3574 */       Object localObject1 = paramArrayOfObject1[i];
/* 3575 */       int k = paramArrayOfInt[i];
/* 3576 */       Object localObject2 = paramArrayOfObject2[i];
/* 3577 */       if ((localObject1 instanceof String)) {
/* 3578 */         if (k == 0) {
/* 3579 */           if (isSpecialProperty((String)localObject1))
/* 3580 */             specialRef(localScriptable, (String)localObject1, paramContext).set(paramContext, localObject2);
/*      */           else
/* 3582 */             ScriptableObject.putProperty(localScriptable, (String)localObject1, localObject2);
/*      */         }
/*      */         else
/*      */         {
/*      */           String str;
/* 3587 */           if (k < 0)
/* 3588 */             str = "__defineGetter__";
/*      */           else
/* 3590 */             str = "__defineSetter__";
/* 3591 */           Callable localCallable = getPropFunctionAndThis(localScriptable, str, paramContext);
/*      */ 
/* 3593 */           lastStoredScriptable(paramContext);
/* 3594 */           Object[] arrayOfObject = new Object[2];
/* 3595 */           arrayOfObject[0] = localObject1;
/* 3596 */           arrayOfObject[1] = localObject2;
/* 3597 */           localCallable.call(paramContext, paramScriptable, localScriptable, arrayOfObject);
/*      */         }
/*      */       } else {
/* 3600 */         int m = ((Integer)localObject1).intValue();
/* 3601 */         ScriptableObject.putProperty(localScriptable, m, localObject2);
/*      */       }
/*      */     }
/* 3604 */     return localScriptable;
/*      */   }
/*      */ 
/*      */   public static boolean isArrayObject(Object paramObject)
/*      */   {
/* 3609 */     return ((paramObject instanceof NativeArray)) || ((paramObject instanceof Arguments));
/*      */   }
/*      */ 
/*      */   public static Object[] getArrayElements(Scriptable paramScriptable)
/*      */   {
/* 3614 */     Context localContext = Context.getContext();
/* 3615 */     long l = NativeArray.getLengthProperty(localContext, paramScriptable);
/* 3616 */     if (l > 2147483647L)
/*      */     {
/* 3618 */       throw new IllegalArgumentException();
/*      */     }
/* 3620 */     int i = (int)l;
/* 3621 */     if (i == 0) {
/* 3622 */       return emptyArgs;
/*      */     }
/* 3624 */     Object[] arrayOfObject = new Object[i];
/* 3625 */     for (int j = 0; j < i; j++) {
/* 3626 */       Object localObject = ScriptableObject.getProperty(paramScriptable, j);
/* 3627 */       arrayOfObject[j] = (localObject == Scriptable.NOT_FOUND ? Undefined.instance : localObject);
/*      */     }
/*      */ 
/* 3630 */     return arrayOfObject;
/*      */   }
/*      */ 
/*      */   static void checkDeprecated(Context paramContext, String paramString)
/*      */   {
/* 3635 */     int i = paramContext.getLanguageVersion();
/* 3636 */     if ((i >= 140) || (i == 0)) {
/* 3637 */       String str = getMessage1("msg.deprec.ctor", paramString);
/* 3638 */       if (i == 0)
/* 3639 */         Context.reportWarning(str);
/*      */       else
/* 3641 */         throw Context.reportRuntimeError(str);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static String getMessage0(String paramString)
/*      */   {
/* 3647 */     return getMessage(paramString, null);
/*      */   }
/*      */ 
/*      */   public static String getMessage1(String paramString, Object paramObject)
/*      */   {
/* 3652 */     Object[] arrayOfObject = { paramObject };
/* 3653 */     return getMessage(paramString, arrayOfObject);
/*      */   }
/*      */ 
/*      */   public static String getMessage2(String paramString, Object paramObject1, Object paramObject2)
/*      */   {
/* 3659 */     Object[] arrayOfObject = { paramObject1, paramObject2 };
/* 3660 */     return getMessage(paramString, arrayOfObject);
/*      */   }
/*      */ 
/*      */   public static String getMessage3(String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
/*      */   {
/* 3666 */     Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3 };
/* 3667 */     return getMessage(paramString, arrayOfObject);
/*      */   }
/*      */ 
/*      */   public static String getMessage4(String paramString, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
/*      */   {
/* 3673 */     Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4 };
/* 3674 */     return getMessage(paramString, arrayOfObject);
/*      */   }
/*      */ 
/*      */   public static String getMessage(String paramString, Object[] paramArrayOfObject)
/*      */   {
/* 3699 */     return messageProvider.getMessage(paramString, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public static EcmaError constructError(String paramString1, String paramString2)
/*      */   {
/* 3747 */     int[] arrayOfInt = new int[1];
/* 3748 */     String str = Context.getSourcePositionFromStack(arrayOfInt);
/* 3749 */     return constructError(paramString1, paramString2, str, arrayOfInt[0], null, 0);
/*      */   }
/*      */ 
/*      */   public static EcmaError constructError(String paramString1, String paramString2, int paramInt)
/*      */   {
/* 3756 */     int[] arrayOfInt = new int[1];
/* 3757 */     String str = Context.getSourcePositionFromStack(arrayOfInt);
/* 3758 */     if (arrayOfInt[0] != 0) {
/* 3759 */       arrayOfInt[0] += paramInt;
/*      */     }
/* 3761 */     return constructError(paramString1, paramString2, str, arrayOfInt[0], null, 0);
/*      */   }
/*      */ 
/*      */   public static EcmaError constructError(String paramString1, String paramString2, String paramString3, int paramInt1, String paramString4, int paramInt2)
/*      */   {
/* 3771 */     return new EcmaError(paramString1, paramString2, paramString3, paramInt1, paramString4, paramInt2);
/*      */   }
/*      */ 
/*      */   public static EcmaError typeError(String paramString)
/*      */   {
/* 3777 */     return constructError("TypeError", paramString);
/*      */   }
/*      */ 
/*      */   public static EcmaError typeError0(String paramString)
/*      */   {
/* 3782 */     String str = getMessage0(paramString);
/* 3783 */     return typeError(str);
/*      */   }
/*      */ 
/*      */   public static EcmaError typeError1(String paramString1, String paramString2)
/*      */   {
/* 3788 */     String str = getMessage1(paramString1, paramString2);
/* 3789 */     return typeError(str);
/*      */   }
/*      */ 
/*      */   public static EcmaError typeError2(String paramString1, String paramString2, String paramString3)
/*      */   {
/* 3795 */     String str = getMessage2(paramString1, paramString2, paramString3);
/* 3796 */     return typeError(str);
/*      */   }
/*      */ 
/*      */   public static EcmaError typeError3(String paramString1, String paramString2, String paramString3, String paramString4)
/*      */   {
/* 3802 */     String str = getMessage3(paramString1, paramString2, paramString3, paramString4);
/* 3803 */     return typeError(str);
/*      */   }
/*      */ 
/*      */   public static RuntimeException undefReadError(Object paramObject1, Object paramObject2)
/*      */   {
/* 3808 */     String str = paramObject2 == null ? "null" : paramObject2.toString();
/* 3809 */     return typeError2("msg.undef.prop.read", toString(paramObject1), str);
/*      */   }
/*      */ 
/*      */   public static RuntimeException undefCallError(Object paramObject1, Object paramObject2)
/*      */   {
/* 3814 */     String str = paramObject2 == null ? "null" : paramObject2.toString();
/* 3815 */     return typeError2("msg.undef.method.call", toString(paramObject1), str);
/*      */   }
/*      */ 
/*      */   public static RuntimeException undefWriteError(Object paramObject1, Object paramObject2, Object paramObject3)
/*      */   {
/* 3822 */     String str1 = paramObject2 == null ? "null" : paramObject2.toString();
/* 3823 */     String str2 = (paramObject3 instanceof Scriptable) ? paramObject3.toString() : toString(paramObject3);
/*      */ 
/* 3825 */     return typeError3("msg.undef.prop.write", toString(paramObject1), str1, str2);
/*      */   }
/*      */ 
/*      */   public static RuntimeException notFoundError(Scriptable paramScriptable, String paramString)
/*      */   {
/* 3833 */     String str = getMessage1("msg.is.not.defined", paramString);
/* 3834 */     throw constructError("ReferenceError", str);
/*      */   }
/*      */ 
/*      */   public static RuntimeException notFunctionError(Object paramObject)
/*      */   {
/* 3839 */     return notFunctionError(paramObject, paramObject);
/*      */   }
/*      */ 
/*      */   public static RuntimeException notFunctionError(Object paramObject1, Object paramObject2)
/*      */   {
/* 3846 */     String str = paramObject2 == null ? "null" : paramObject2.toString();
/*      */ 
/* 3848 */     if (paramObject1 == Scriptable.NOT_FOUND) {
/* 3849 */       return typeError1("msg.function.not.found", str);
/*      */     }
/* 3851 */     return typeError2("msg.isnt.function", str, typeof(paramObject1));
/*      */   }
/*      */ 
/*      */   public static RuntimeException notFunctionError(Object paramObject1, Object paramObject2, String paramString)
/*      */   {
/* 3858 */     String str = toString(paramObject1);
/* 3859 */     if ((paramObject1 instanceof NativeFunction))
/*      */     {
/* 3861 */       int i = str.indexOf('{');
/* 3862 */       if (i > -1) {
/* 3863 */         str = str.substring(0, i + 1) + "...}";
/*      */       }
/*      */     }
/* 3866 */     if (paramObject2 == Scriptable.NOT_FOUND) {
/* 3867 */       return typeError2("msg.function.not.found.in", paramString, str);
/*      */     }
/*      */ 
/* 3870 */     return typeError3("msg.isnt.function.in", paramString, str, typeof(paramObject2));
/*      */   }
/*      */ 
/*      */   private static RuntimeException notXmlError(Object paramObject)
/*      */   {
/* 3876 */     throw typeError1("msg.isnt.xml.object", toString(paramObject));
/*      */   }
/*      */ 
/*      */   private static void warnAboutNonJSObject(Object paramObject)
/*      */   {
/* 3881 */     String str = "RHINO USAGE WARNING: Missed Context.javaToJS() conversion:\nRhino runtime detected object " + paramObject + " of class " + paramObject.getClass().getName() + " where it expected String, Number, Boolean or Scriptable instance. Please check your code for missing Context.javaToJS() call.";
/*      */ 
/* 3884 */     Context.reportWarning(str);
/*      */ 
/* 3886 */     System.err.println(str);
/*      */   }
/*      */ 
/*      */   public static RegExpProxy getRegExpProxy(Context paramContext)
/*      */   {
/* 3891 */     return paramContext.getRegExpProxy();
/*      */   }
/*      */ 
/*      */   public static void setRegExpProxy(Context paramContext, RegExpProxy paramRegExpProxy)
/*      */   {
/* 3896 */     if (paramRegExpProxy == null) throw new IllegalArgumentException();
/* 3897 */     paramContext.regExpProxy = paramRegExpProxy;
/*      */   }
/*      */ 
/*      */   public static RegExpProxy checkRegExpProxy(Context paramContext)
/*      */   {
/* 3902 */     RegExpProxy localRegExpProxy = getRegExpProxy(paramContext);
/* 3903 */     if (localRegExpProxy == null) {
/* 3904 */       throw Context.reportRuntimeError0("msg.no.regexp");
/*      */     }
/* 3906 */     return localRegExpProxy;
/*      */   }
/*      */ 
/*      */   private static XMLLib currentXMLLib(Context paramContext)
/*      */   {
/* 3912 */     if (paramContext.topCallScope == null) {
/* 3913 */       throw new IllegalStateException();
/*      */     }
/* 3915 */     XMLLib localXMLLib = paramContext.cachedXMLLib;
/* 3916 */     if (localXMLLib == null) {
/* 3917 */       localXMLLib = XMLLib.extractFromScope(paramContext.topCallScope);
/* 3918 */       if (localXMLLib == null)
/* 3919 */         throw new IllegalStateException();
/* 3920 */       paramContext.cachedXMLLib = localXMLLib;
/*      */     }
/*      */ 
/* 3923 */     return localXMLLib;
/*      */   }
/*      */ 
/*      */   public static String escapeAttributeValue(Object paramObject, Context paramContext)
/*      */   {
/* 3934 */     XMLLib localXMLLib = currentXMLLib(paramContext);
/* 3935 */     return localXMLLib.escapeAttributeValue(paramObject);
/*      */   }
/*      */ 
/*      */   public static String escapeTextValue(Object paramObject, Context paramContext)
/*      */   {
/* 3946 */     XMLLib localXMLLib = currentXMLLib(paramContext);
/* 3947 */     return localXMLLib.escapeTextValue(paramObject);
/*      */   }
/*      */ 
/*      */   public static Ref memberRef(Object paramObject1, Object paramObject2, Context paramContext, int paramInt)
/*      */   {
/* 3953 */     if (!(paramObject1 instanceof XMLObject)) {
/* 3954 */       throw notXmlError(paramObject1);
/*      */     }
/* 3956 */     XMLObject localXMLObject = (XMLObject)paramObject1;
/* 3957 */     return localXMLObject.memberRef(paramContext, paramObject2, paramInt);
/*      */   }
/*      */ 
/*      */   public static Ref memberRef(Object paramObject1, Object paramObject2, Object paramObject3, Context paramContext, int paramInt)
/*      */   {
/* 3963 */     if (!(paramObject1 instanceof XMLObject)) {
/* 3964 */       throw notXmlError(paramObject1);
/*      */     }
/* 3966 */     XMLObject localXMLObject = (XMLObject)paramObject1;
/* 3967 */     return localXMLObject.memberRef(paramContext, paramObject2, paramObject3, paramInt);
/*      */   }
/*      */ 
/*      */   public static Ref nameRef(Object paramObject, Context paramContext, Scriptable paramScriptable, int paramInt)
/*      */   {
/* 3973 */     XMLLib localXMLLib = currentXMLLib(paramContext);
/* 3974 */     return localXMLLib.nameRef(paramContext, paramObject, paramScriptable, paramInt);
/*      */   }
/*      */ 
/*      */   public static Ref nameRef(Object paramObject1, Object paramObject2, Context paramContext, Scriptable paramScriptable, int paramInt)
/*      */   {
/* 3980 */     XMLLib localXMLLib = currentXMLLib(paramContext);
/* 3981 */     return localXMLLib.nameRef(paramContext, paramObject1, paramObject2, paramScriptable, paramInt);
/*      */   }
/*      */ 
/*      */   private static void storeIndexResult(Context paramContext, int paramInt)
/*      */   {
/* 3986 */     paramContext.scratchIndex = paramInt;
/*      */   }
/*      */ 
/*      */   static int lastIndexResult(Context paramContext)
/*      */   {
/* 3991 */     return paramContext.scratchIndex;
/*      */   }
/*      */ 
/*      */   public static void storeUint32Result(Context paramContext, long paramLong)
/*      */   {
/* 3996 */     if (paramLong >>> 32 != 0L)
/* 3997 */       throw new IllegalArgumentException();
/* 3998 */     paramContext.scratchUint32 = paramLong;
/*      */   }
/*      */ 
/*      */   public static long lastUint32Result(Context paramContext)
/*      */   {
/* 4003 */     long l = paramContext.scratchUint32;
/* 4004 */     if (l >>> 32 != 0L)
/* 4005 */       throw new IllegalStateException();
/* 4006 */     return l;
/*      */   }
/*      */ 
/*      */   private static void storeScriptable(Context paramContext, Scriptable paramScriptable)
/*      */   {
/* 4012 */     if (paramContext.scratchScriptable != null)
/* 4013 */       throw new IllegalStateException();
/* 4014 */     paramContext.scratchScriptable = paramScriptable;
/*      */   }
/*      */ 
/*      */   public static Scriptable lastStoredScriptable(Context paramContext)
/*      */   {
/* 4019 */     Scriptable localScriptable = paramContext.scratchScriptable;
/* 4020 */     paramContext.scratchScriptable = null;
/* 4021 */     return localScriptable;
/*      */   }
/*      */ 
/*      */   static String makeUrlForGeneratedScript(boolean paramBoolean, String paramString, int paramInt)
/*      */   {
/* 4027 */     if (paramBoolean) {
/* 4028 */       return paramString + '#' + paramInt + "(eval)";
/*      */     }
/* 4030 */     return paramString + '#' + paramInt + "(Function)";
/*      */   }
/*      */ 
/*      */   static boolean isGeneratedScript(String paramString)
/*      */   {
/* 4037 */     return (paramString.indexOf("(eval)") >= 0) || (paramString.indexOf("(Function)") >= 0);
/*      */   }
/*      */ 
/*      */   private static RuntimeException errorWithClassName(String paramString, Object paramObject)
/*      */   {
/* 4043 */     return Context.reportRuntimeError1(paramString, paramObject.getClass().getName());
/*      */   }
/*      */ 
/*      */   public static JavaScriptException throwError(Context paramContext, Scriptable paramScriptable, String paramString)
/*      */   {
/* 4055 */     Scriptable localScriptable = paramContext.newObject(paramScriptable, "Error", new Object[] { paramString });
/*      */ 
/* 4057 */     return new JavaScriptException(localScriptable, (String)ScriptableObject.getTypedProperty(localScriptable, "fileName", String.class), ((Number)ScriptableObject.getTypedProperty(localScriptable, "lineNumber", Number.class)).intValue());
/*      */   }
/*      */ 
/*      */   private static class DefaultMessageProvider
/*      */     implements ScriptRuntime.MessageProvider
/*      */   {
/*      */     public String getMessage(String paramString, Object[] paramArrayOfObject)
/*      */     {
/* 3711 */       Context localContext = Context.getCurrentContext();
/* 3712 */       final Locale localLocale = localContext != null ? localContext.getLocale() : Locale.getDefault();
/*      */ 
/* 3720 */       ResourceBundle localResourceBundle = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public ResourceBundle run() {
/* 3723 */           return ResourceBundle.getBundle("sun.org.mozilla.javascript.internal.resources.Messages", localLocale);
/*      */         }
/*      */       });
/*      */       String str;
/*      */       try {
/* 3729 */         str = localResourceBundle.getString(paramString);
/*      */       } catch (MissingResourceException localMissingResourceException) {
/* 3731 */         throw new RuntimeException("no message resource found for message property " + paramString);
/*      */       }
/*      */ 
/* 3740 */       MessageFormat localMessageFormat = new MessageFormat(str);
/* 3741 */       return localMessageFormat.format(paramArrayOfObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class IdEnumeration
/*      */   {
/*      */     Scriptable obj;
/*      */     Object[] ids;
/*      */     int index;
/*      */     ObjToIntMap used;
/*      */     Object currentId;
/*      */     int enumType;
/*      */     boolean enumNumbers;
/*      */     Scriptable iterator;
/*      */   }
/*      */ 
/*      */   public static abstract interface MessageProvider
/*      */   {
/*      */     public abstract String getMessage(String paramString, Object[] paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   static class NoSuchMethodShim
/*      */     implements Callable
/*      */   {
/*      */     String methodName;
/*      */     Callable noSuchMethodMethod;
/*      */ 
/*      */     NoSuchMethodShim(Callable paramCallable, String paramString)
/*      */     {
/*  107 */       this.noSuchMethodMethod = paramCallable;
/*  108 */       this.methodName = paramString;
/*      */     }
/*      */ 
/*      */     public Object call(Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject)
/*      */     {
/*  122 */       Object[] arrayOfObject = new Object[2];
/*      */ 
/*  124 */       arrayOfObject[0] = this.methodName;
/*  125 */       arrayOfObject[1] = ScriptRuntime.newArrayLiteral(paramArrayOfObject, null, paramContext, paramScriptable1);
/*  126 */       return this.noSuchMethodMethod.call(paramContext, paramScriptable1, paramScriptable2, arrayOfObject);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.ScriptRuntime
 * JD-Core Version:    0.6.2
 */