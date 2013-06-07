/*      */ package javax.swing;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Insets;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Vector;
/*      */ import javax.swing.border.Border;
/*      */ import javax.swing.event.SwingPropertyChangeSupport;
/*      */ import javax.swing.plaf.ColorUIResource;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import sun.reflect.misc.MethodUtil;
/*      */ import sun.reflect.misc.ReflectUtil;
/*      */ import sun.util.CoreResourceBundleControl;
/*      */ 
/*      */ public class UIDefaults extends Hashtable<Object, Object>
/*      */ {
/*   76 */   private static final Object PENDING = "Pending";
/*      */   private SwingPropertyChangeSupport changeSupport;
/*      */   private Vector<String> resourceBundles;
/*   82 */   private Locale defaultLocale = Locale.getDefault();
/*      */   private Map<Locale, Map<String, Object>> resourceCache;
/*      */ 
/*      */   public UIDefaults()
/*      */   {
/*   96 */     this(700, 0.75F);
/*      */   }
/*      */ 
/*      */   public UIDefaults(int paramInt, float paramFloat)
/*      */   {
/*  109 */     super(paramInt, paramFloat);
/*  110 */     this.resourceCache = new HashMap();
/*      */   }
/*      */ 
/*      */   public UIDefaults(Object[] paramArrayOfObject)
/*      */   {
/*  129 */     super(paramArrayOfObject.length / 2);
/*  130 */     for (int i = 0; i < paramArrayOfObject.length; i += 2)
/*  131 */       super.put(paramArrayOfObject[i], paramArrayOfObject[(i + 1)]);
/*      */   }
/*      */ 
/*      */   public Object get(Object paramObject)
/*      */   {
/*  162 */     Object localObject = getFromHashtable(paramObject);
/*  163 */     return localObject != null ? localObject : getFromResourceBundle(paramObject, null);
/*      */   }
/*      */ 
/*      */   private Object getFromHashtable(Object paramObject)
/*      */   {
/*  174 */     Object localObject1 = super.get(paramObject);
/*  175 */     if ((localObject1 != PENDING) && (!(localObject1 instanceof ActiveValue)) && (!(localObject1 instanceof LazyValue)))
/*      */     {
/*  178 */       return localObject1;
/*      */     }
/*      */ 
/*  187 */     synchronized (this) {
/*  188 */       localObject1 = super.get(paramObject);
/*  189 */       if (localObject1 == PENDING) {
/*      */         do {
/*      */           try {
/*  192 */             wait();
/*      */           }
/*      */           catch (InterruptedException localInterruptedException) {
/*      */           }
/*  196 */           localObject1 = super.get(paramObject);
/*      */         }
/*  198 */         while (localObject1 == PENDING);
/*  199 */         return localObject1;
/*      */       }
/*  201 */       if ((localObject1 instanceof LazyValue)) {
/*  202 */         super.put(paramObject, PENDING);
/*      */       }
/*  204 */       else if (!(localObject1 instanceof ActiveValue)) {
/*  205 */         return localObject1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  212 */     if ((localObject1 instanceof LazyValue))
/*      */     {
/*      */       try
/*      */       {
/*  217 */         localObject1 = ((LazyValue)localObject1).createValue(this);
/*      */       }
/*      */       finally {
/*  220 */         synchronized (this) {
/*  221 */           if (localObject1 == null) {
/*  222 */             super.remove(paramObject);
/*      */           }
/*      */           else {
/*  225 */             super.put(paramObject, localObject1);
/*      */           }
/*  227 */           notifyAll();
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  232 */       localObject1 = ((ActiveValue)localObject1).createValue(this);
/*      */     }
/*      */ 
/*  235 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public Object get(Object paramObject, Locale paramLocale)
/*      */   {
/*  265 */     Object localObject = getFromHashtable(paramObject);
/*  266 */     return localObject != null ? localObject : getFromResourceBundle(paramObject, paramLocale);
/*      */   }
/*      */ 
/*      */   private Object getFromResourceBundle(Object paramObject, Locale paramLocale)
/*      */   {
/*  274 */     if ((this.resourceBundles == null) || (this.resourceBundles.isEmpty()) || (!(paramObject instanceof String)))
/*      */     {
/*  277 */       return null;
/*      */     }
/*      */ 
/*  281 */     if (paramLocale == null) {
/*  282 */       if (this.defaultLocale == null) {
/*  283 */         return null;
/*      */       }
/*  285 */       paramLocale = this.defaultLocale;
/*      */     }
/*      */ 
/*  288 */     synchronized (this) {
/*  289 */       return getResourceCache(paramLocale).get(paramObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Map<String, Object> getResourceCache(Locale paramLocale)
/*      */   {
/*  297 */     Object localObject1 = (Map)this.resourceCache.get(paramLocale);
/*      */ 
/*  299 */     if (localObject1 == null) {
/*  300 */       localObject1 = new TextAndMnemonicHashMap(null);
/*  301 */       for (int i = this.resourceBundles.size() - 1; i >= 0; i--) {
/*  302 */         String str1 = (String)this.resourceBundles.get(i);
/*      */         try {
/*  304 */           CoreResourceBundleControl localCoreResourceBundleControl = CoreResourceBundleControl.getRBControlInstance(str1);
/*      */           ResourceBundle localResourceBundle;
/*  306 */           if (localCoreResourceBundleControl != null)
/*  307 */             localResourceBundle = ResourceBundle.getBundle(str1, paramLocale, localCoreResourceBundleControl);
/*      */           else {
/*  309 */             localResourceBundle = ResourceBundle.getBundle(str1, paramLocale);
/*      */           }
/*  311 */           Enumeration localEnumeration = localResourceBundle.getKeys();
/*      */ 
/*  313 */           while (localEnumeration.hasMoreElements()) {
/*  314 */             String str2 = (String)localEnumeration.nextElement();
/*      */ 
/*  316 */             if (((Map)localObject1).get(str2) == null) {
/*  317 */               Object localObject2 = localResourceBundle.getObject(str2);
/*      */ 
/*  319 */               ((Map)localObject1).put(str2, localObject2);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (MissingResourceException localMissingResourceException) {
/*      */         }
/*      */       }
/*  326 */       this.resourceCache.put(paramLocale, localObject1);
/*      */     }
/*  328 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public Object put(Object paramObject1, Object paramObject2)
/*      */   {
/*  346 */     Object localObject = paramObject2 == null ? super.remove(paramObject1) : super.put(paramObject1, paramObject2);
/*  347 */     if ((paramObject1 instanceof String)) {
/*  348 */       firePropertyChange((String)paramObject1, localObject, paramObject2);
/*      */     }
/*  350 */     return localObject;
/*      */   }
/*      */ 
/*      */   public void putDefaults(Object[] paramArrayOfObject)
/*      */   {
/*  366 */     int i = 0; for (int j = paramArrayOfObject.length; i < j; i += 2) {
/*  367 */       Object localObject = paramArrayOfObject[(i + 1)];
/*  368 */       if (localObject == null) {
/*  369 */         super.remove(paramArrayOfObject[i]);
/*      */       }
/*      */       else {
/*  372 */         super.put(paramArrayOfObject[i], localObject);
/*      */       }
/*      */     }
/*  375 */     firePropertyChange("UIDefaults", null, null);
/*      */   }
/*      */ 
/*      */   public Font getFont(Object paramObject)
/*      */   {
/*  388 */     Object localObject = get(paramObject);
/*  389 */     return (localObject instanceof Font) ? (Font)localObject : null;
/*      */   }
/*      */ 
/*      */   public Font getFont(Object paramObject, Locale paramLocale)
/*      */   {
/*  405 */     Object localObject = get(paramObject, paramLocale);
/*  406 */     return (localObject instanceof Font) ? (Font)localObject : null;
/*      */   }
/*      */ 
/*      */   public Color getColor(Object paramObject)
/*      */   {
/*  418 */     Object localObject = get(paramObject);
/*  419 */     return (localObject instanceof Color) ? (Color)localObject : null;
/*      */   }
/*      */ 
/*      */   public Color getColor(Object paramObject, Locale paramLocale)
/*      */   {
/*  435 */     Object localObject = get(paramObject, paramLocale);
/*  436 */     return (localObject instanceof Color) ? (Color)localObject : null;
/*      */   }
/*      */ 
/*      */   public Icon getIcon(Object paramObject)
/*      */   {
/*  449 */     Object localObject = get(paramObject);
/*  450 */     return (localObject instanceof Icon) ? (Icon)localObject : null;
/*      */   }
/*      */ 
/*      */   public Icon getIcon(Object paramObject, Locale paramLocale)
/*      */   {
/*  466 */     Object localObject = get(paramObject, paramLocale);
/*  467 */     return (localObject instanceof Icon) ? (Icon)localObject : null;
/*      */   }
/*      */ 
/*      */   public Border getBorder(Object paramObject)
/*      */   {
/*  480 */     Object localObject = get(paramObject);
/*  481 */     return (localObject instanceof Border) ? (Border)localObject : null;
/*      */   }
/*      */ 
/*      */   public Border getBorder(Object paramObject, Locale paramLocale)
/*      */   {
/*  497 */     Object localObject = get(paramObject, paramLocale);
/*  498 */     return (localObject instanceof Border) ? (Border)localObject : null;
/*      */   }
/*      */ 
/*      */   public String getString(Object paramObject)
/*      */   {
/*  511 */     Object localObject = get(paramObject);
/*  512 */     return (localObject instanceof String) ? (String)localObject : null;
/*      */   }
/*      */ 
/*      */   public String getString(Object paramObject, Locale paramLocale)
/*      */   {
/*  527 */     Object localObject = get(paramObject, paramLocale);
/*  528 */     return (localObject instanceof String) ? (String)localObject : null;
/*      */   }
/*      */ 
/*      */   public int getInt(Object paramObject)
/*      */   {
/*  539 */     Object localObject = get(paramObject);
/*  540 */     return (localObject instanceof Integer) ? ((Integer)localObject).intValue() : 0;
/*      */   }
/*      */ 
/*      */   public int getInt(Object paramObject, Locale paramLocale)
/*      */   {
/*  555 */     Object localObject = get(paramObject, paramLocale);
/*  556 */     return (localObject instanceof Integer) ? ((Integer)localObject).intValue() : 0;
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(Object paramObject)
/*      */   {
/*  570 */     Object localObject = get(paramObject);
/*  571 */     return (localObject instanceof Boolean) ? ((Boolean)localObject).booleanValue() : false;
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(Object paramObject, Locale paramLocale)
/*      */   {
/*  587 */     Object localObject = get(paramObject, paramLocale);
/*  588 */     return (localObject instanceof Boolean) ? ((Boolean)localObject).booleanValue() : false;
/*      */   }
/*      */ 
/*      */   public Insets getInsets(Object paramObject)
/*      */   {
/*  601 */     Object localObject = get(paramObject);
/*  602 */     return (localObject instanceof Insets) ? (Insets)localObject : null;
/*      */   }
/*      */ 
/*      */   public Insets getInsets(Object paramObject, Locale paramLocale)
/*      */   {
/*  618 */     Object localObject = get(paramObject, paramLocale);
/*  619 */     return (localObject instanceof Insets) ? (Insets)localObject : null;
/*      */   }
/*      */ 
/*      */   public Dimension getDimension(Object paramObject)
/*      */   {
/*  632 */     Object localObject = get(paramObject);
/*  633 */     return (localObject instanceof Dimension) ? (Dimension)localObject : null;
/*      */   }
/*      */ 
/*      */   public Dimension getDimension(Object paramObject, Locale paramLocale)
/*      */   {
/*  649 */     Object localObject = get(paramObject, paramLocale);
/*  650 */     return (localObject instanceof Dimension) ? (Dimension)localObject : null;
/*      */   }
/*      */ 
/*      */   public Class<? extends ComponentUI> getUIClass(String paramString, ClassLoader paramClassLoader)
/*      */   {
/*      */     try
/*      */     {
/*  678 */       String str = (String)get(paramString);
/*  679 */       if (str != null) {
/*  680 */         ReflectUtil.checkPackageAccess(str);
/*      */ 
/*  682 */         Class localClass = (Class)get(str);
/*  683 */         if (localClass == null) {
/*  684 */           if (paramClassLoader == null) {
/*  685 */             localClass = SwingUtilities.loadSystemClass(str);
/*      */           }
/*      */           else {
/*  688 */             localClass = paramClassLoader.loadClass(str);
/*      */           }
/*  690 */           if (localClass != null)
/*      */           {
/*  692 */             put(str, localClass);
/*      */           }
/*      */         }
/*  695 */         return localClass;
/*      */       }
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException) {
/*  699 */       return null;
/*      */     }
/*      */     catch (ClassCastException localClassCastException) {
/*  702 */       return null;
/*      */     }
/*  704 */     return null;
/*      */   }
/*      */ 
/*      */   public Class<? extends ComponentUI> getUIClass(String paramString)
/*      */   {
/*  716 */     return getUIClass(paramString, null);
/*      */   }
/*      */ 
/*      */   protected void getUIError(String paramString)
/*      */   {
/*  729 */     System.err.println("UIDefaults.getUI() failed: " + paramString);
/*      */     try {
/*  731 */       throw new Error();
/*      */     }
/*      */     catch (Throwable localThrowable) {
/*  734 */       localThrowable.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ComponentUI getUI(JComponent paramJComponent)
/*      */   {
/*  754 */     Object localObject1 = get("ClassLoader");
/*  755 */     ClassLoader localClassLoader = localObject1 != null ? (ClassLoader)localObject1 : paramJComponent.getClass().getClassLoader();
/*      */ 
/*  757 */     Class localClass = getUIClass(paramJComponent.getUIClassID(), localClassLoader);
/*  758 */     Object localObject2 = null;
/*      */ 
/*  760 */     if (localClass == null)
/*  761 */       getUIError("no ComponentUI class for: " + paramJComponent);
/*      */     else {
/*      */       try
/*      */       {
/*  765 */         Method localMethod = (Method)get(localClass);
/*  766 */         if (localMethod == null) {
/*  767 */           localMethod = localClass.getMethod("createUI", new Class[] { JComponent.class });
/*  768 */           put(localClass, localMethod);
/*      */         }
/*  770 */         localObject2 = MethodUtil.invoke(localMethod, null, new Object[] { paramJComponent });
/*      */       }
/*      */       catch (NoSuchMethodException localNoSuchMethodException) {
/*  773 */         getUIError("static createUI() method not found in " + localClass);
/*      */       }
/*      */       catch (Exception localException) {
/*  776 */         getUIError("createUI() failed for " + paramJComponent + " " + localException);
/*      */       }
/*      */     }
/*      */ 
/*  780 */     return (ComponentUI)localObject2;
/*      */   }
/*      */ 
/*      */   public synchronized void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */   {
/*  794 */     if (this.changeSupport == null) {
/*  795 */       this.changeSupport = new SwingPropertyChangeSupport(this);
/*      */     }
/*  797 */     this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   public synchronized void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */   {
/*  810 */     if (this.changeSupport != null)
/*  811 */       this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   public synchronized PropertyChangeListener[] getPropertyChangeListeners()
/*      */   {
/*  825 */     if (this.changeSupport == null) {
/*  826 */       return new PropertyChangeListener[0];
/*      */     }
/*  828 */     return this.changeSupport.getPropertyChangeListeners();
/*      */   }
/*      */ 
/*      */   protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
/*      */   {
/*  845 */     if (this.changeSupport != null)
/*  846 */       this.changeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   public synchronized void addResourceBundle(String paramString)
/*      */   {
/*  863 */     if (paramString == null) {
/*  864 */       return;
/*      */     }
/*  866 */     if (this.resourceBundles == null) {
/*  867 */       this.resourceBundles = new Vector(5);
/*      */     }
/*  869 */     if (!this.resourceBundles.contains(paramString)) {
/*  870 */       this.resourceBundles.add(paramString);
/*  871 */       this.resourceCache.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void removeResourceBundle(String paramString)
/*      */   {
/*  886 */     if (this.resourceBundles != null) {
/*  887 */       this.resourceBundles.remove(paramString);
/*      */     }
/*  889 */     this.resourceCache.clear();
/*      */   }
/*      */ 
/*      */   public void setDefaultLocale(Locale paramLocale)
/*      */   {
/*  907 */     this.defaultLocale = paramLocale;
/*      */   }
/*      */ 
/*      */   public Locale getDefaultLocale()
/*      */   {
/*  925 */     return this.defaultLocale;
/*      */   }
/*      */ 
/*      */   public static abstract interface ActiveValue
/*      */   {
/*      */     public abstract Object createValue(UIDefaults paramUIDefaults);
/*      */   }
/*      */ 
/*      */   public static class LazyInputMap
/*      */     implements UIDefaults.LazyValue
/*      */   {
/*      */     private Object[] bindings;
/*      */ 
/*      */     public LazyInputMap(Object[] paramArrayOfObject)
/*      */     {
/* 1202 */       this.bindings = paramArrayOfObject;
/*      */     }
/*      */ 
/*      */     public Object createValue(UIDefaults paramUIDefaults)
/*      */     {
/* 1213 */       if (this.bindings != null) {
/* 1214 */         InputMap localInputMap = LookAndFeel.makeInputMap(this.bindings);
/* 1215 */         return localInputMap;
/*      */       }
/* 1217 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface LazyValue
/*      */   {
/*      */     public abstract Object createValue(UIDefaults paramUIDefaults);
/*      */   }
/*      */ 
/*      */   public static class ProxyLazyValue
/*      */     implements UIDefaults.LazyValue
/*      */   {
/*      */     private AccessControlContext acc;
/*      */     private String className;
/*      */     private String methodName;
/*      */     private Object[] args;
/*      */ 
/*      */     public ProxyLazyValue(String paramString)
/*      */     {
/* 1023 */       this(paramString, (String)null);
/*      */     }
/*      */ 
/*      */     public ProxyLazyValue(String paramString1, String paramString2)
/*      */     {
/* 1037 */       this(paramString1, paramString2, null);
/*      */     }
/*      */ 
/*      */     public ProxyLazyValue(String paramString, Object[] paramArrayOfObject)
/*      */     {
/* 1049 */       this(paramString, null, paramArrayOfObject);
/*      */     }
/*      */ 
/*      */     public ProxyLazyValue(String paramString1, String paramString2, Object[] paramArrayOfObject)
/*      */     {
/* 1065 */       this.acc = AccessController.getContext();
/* 1066 */       this.className = paramString1;
/* 1067 */       this.methodName = paramString2;
/* 1068 */       if (paramArrayOfObject != null)
/* 1069 */         this.args = ((Object[])paramArrayOfObject.clone());
/*      */     }
/*      */ 
/*      */     public Object createValue(final UIDefaults paramUIDefaults)
/*      */     {
/* 1084 */       if ((this.acc == null) && (System.getSecurityManager() != null)) {
/* 1085 */         throw new SecurityException("null AccessControlContext");
/*      */       }
/* 1087 */       return AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run()
/*      */         {
/*      */           try
/*      */           {
/*      */             Object localObject1;
/* 1093 */             if ((paramUIDefaults == null) || (!((localObject1 = paramUIDefaults.get("ClassLoader")) instanceof ClassLoader)))
/*      */             {
/* 1095 */               localObject1 = Thread.currentThread().getContextClassLoader();
/*      */ 
/* 1097 */               if (localObject1 == null)
/*      */               {
/* 1099 */                 localObject1 = ClassLoader.getSystemClassLoader();
/*      */               }
/*      */             }
/* 1102 */             ReflectUtil.checkPackageAccess(UIDefaults.ProxyLazyValue.this.className);
/* 1103 */             Class localClass = Class.forName(UIDefaults.ProxyLazyValue.this.className, true, (ClassLoader)localObject1);
/* 1104 */             UIDefaults.ProxyLazyValue.this.checkAccess(localClass.getModifiers());
/* 1105 */             if (UIDefaults.ProxyLazyValue.this.methodName != null) {
/* 1106 */               arrayOfClass = UIDefaults.ProxyLazyValue.this.getClassArray(UIDefaults.ProxyLazyValue.this.args);
/* 1107 */               localObject2 = localClass.getMethod(UIDefaults.ProxyLazyValue.this.methodName, arrayOfClass);
/* 1108 */               return MethodUtil.invoke((Method)localObject2, localClass, UIDefaults.ProxyLazyValue.this.args);
/*      */             }
/* 1110 */             Class[] arrayOfClass = UIDefaults.ProxyLazyValue.this.getClassArray(UIDefaults.ProxyLazyValue.this.args);
/* 1111 */             Object localObject2 = localClass.getConstructor(arrayOfClass);
/* 1112 */             UIDefaults.ProxyLazyValue.this.checkAccess(((Constructor)localObject2).getModifiers());
/* 1113 */             return ((Constructor)localObject2).newInstance(UIDefaults.ProxyLazyValue.this.args);
/*      */           }
/*      */           catch (Exception localException)
/*      */           {
/*      */           }
/*      */ 
/* 1122 */           return null;
/*      */         }
/*      */       }
/*      */       , this.acc);
/*      */     }
/*      */ 
/*      */     private void checkAccess(int paramInt)
/*      */     {
/* 1128 */       if ((System.getSecurityManager() != null) && (!Modifier.isPublic(paramInt)))
/*      */       {
/* 1130 */         throw new SecurityException("Resource is not accessible");
/*      */       }
/*      */     }
/*      */ 
/*      */     private Class[] getClassArray(Object[] paramArrayOfObject)
/*      */     {
/* 1142 */       Class[] arrayOfClass = null;
/* 1143 */       if (paramArrayOfObject != null) {
/* 1144 */         arrayOfClass = new Class[paramArrayOfObject.length];
/* 1145 */         for (int i = 0; i < paramArrayOfObject.length; i++)
/*      */         {
/* 1149 */           if ((paramArrayOfObject[i] instanceof Integer))
/* 1150 */             arrayOfClass[i] = Integer.TYPE;
/* 1151 */           else if ((paramArrayOfObject[i] instanceof Boolean))
/* 1152 */             arrayOfClass[i] = Boolean.TYPE;
/* 1153 */           else if ((paramArrayOfObject[i] instanceof ColorUIResource))
/*      */           {
/* 1162 */             arrayOfClass[i] = Color.class;
/*      */           }
/* 1164 */           else arrayOfClass[i] = paramArrayOfObject[i].getClass();
/*      */         }
/*      */       }
/*      */ 
/* 1168 */       return arrayOfClass;
/*      */     }
/*      */ 
/*      */     private String printArgs(Object[] paramArrayOfObject) {
/* 1172 */       String str = "{";
/* 1173 */       if (paramArrayOfObject != null) {
/* 1174 */         for (int i = 0; i < paramArrayOfObject.length - 1; i++) {
/* 1175 */           str = str.concat(paramArrayOfObject[i] + ",");
/*      */         }
/* 1177 */         str = str.concat(paramArrayOfObject[(paramArrayOfObject.length - 1)] + "}");
/*      */       } else {
/* 1179 */         str = str.concat("}");
/*      */       }
/* 1181 */       return str;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class TextAndMnemonicHashMap extends HashMap<String, Object>
/*      */   {
/*      */     static final String AND_MNEMONIC = "AndMnemonic";
/*      */     static final String TITLE_SUFFIX = ".titleAndMnemonic";
/*      */     static final String TEXT_SUFFIX = ".textAndMnemonic";
/*      */ 
/*      */     public Object get(Object paramObject)
/*      */     {
/* 1252 */       Object localObject = super.get(paramObject);
/*      */ 
/* 1254 */       if (localObject == null)
/*      */       {
/* 1256 */         int i = 0;
/*      */ 
/* 1258 */         String str1 = paramObject.toString();
/* 1259 */         String str2 = null;
/*      */ 
/* 1261 */         if (str1.endsWith("AndMnemonic")) {
/* 1262 */           return null;
/*      */         }
/*      */ 
/* 1265 */         if (str1.endsWith(".mnemonic")) {
/* 1266 */           str2 = composeKey(str1, 9, ".textAndMnemonic");
/* 1267 */         } else if (str1.endsWith("NameMnemonic")) {
/* 1268 */           str2 = composeKey(str1, 12, ".textAndMnemonic");
/* 1269 */         } else if (str1.endsWith("Mnemonic")) {
/* 1270 */           str2 = composeKey(str1, 8, ".textAndMnemonic");
/* 1271 */           i = 1;
/*      */         }
/*      */ 
/* 1274 */         if (str2 != null) {
/* 1275 */           localObject = super.get(str2);
/* 1276 */           if ((localObject == null) && (i != 0)) {
/* 1277 */             str2 = composeKey(str1, 8, ".titleAndMnemonic");
/* 1278 */             localObject = super.get(str2);
/*      */           }
/*      */ 
/* 1281 */           return localObject == null ? null : getMnemonicFromProperty(localObject.toString());
/*      */         }
/*      */ 
/* 1284 */         if (str1.endsWith("NameText"))
/* 1285 */           str2 = composeKey(str1, 8, ".textAndMnemonic");
/* 1286 */         else if (str1.endsWith(".nameText"))
/* 1287 */           str2 = composeKey(str1, 9, ".textAndMnemonic");
/* 1288 */         else if (str1.endsWith("Text"))
/* 1289 */           str2 = composeKey(str1, 4, ".textAndMnemonic");
/* 1290 */         else if (str1.endsWith("Title")) {
/* 1291 */           str2 = composeKey(str1, 5, ".titleAndMnemonic");
/*      */         }
/*      */ 
/* 1294 */         if (str2 != null) {
/* 1295 */           localObject = super.get(str2);
/* 1296 */           return localObject == null ? null : getTextFromProperty(localObject.toString());
/*      */         }
/*      */ 
/* 1299 */         if (str1.endsWith("DisplayedMnemonicIndex")) {
/* 1300 */           str2 = composeKey(str1, 22, ".textAndMnemonic");
/* 1301 */           localObject = super.get(str2);
/* 1302 */           if (localObject == null) {
/* 1303 */             str2 = composeKey(str1, 22, ".titleAndMnemonic");
/* 1304 */             localObject = super.get(str2);
/*      */           }
/* 1306 */           return localObject == null ? null : getIndexFromProperty(localObject.toString());
/*      */         }
/*      */       }
/*      */ 
/* 1310 */       return localObject;
/*      */     }
/*      */ 
/*      */     String composeKey(String paramString1, int paramInt, String paramString2) {
/* 1314 */       return paramString1.substring(0, paramString1.length() - paramInt) + paramString2;
/*      */     }
/*      */ 
/*      */     String getTextFromProperty(String paramString) {
/* 1318 */       return paramString.replace("&", "");
/*      */     }
/*      */ 
/*      */     String getMnemonicFromProperty(String paramString) {
/* 1322 */       int i = paramString.indexOf('&');
/* 1323 */       if ((0 <= i) && (i < paramString.length() - 1)) {
/* 1324 */         char c = paramString.charAt(i + 1);
/* 1325 */         return Integer.toString(Character.toUpperCase(c));
/*      */       }
/* 1327 */       return null;
/*      */     }
/*      */ 
/*      */     String getIndexFromProperty(String paramString) {
/* 1331 */       int i = paramString.indexOf('&');
/* 1332 */       return i == -1 ? null : Integer.toString(i);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.UIDefaults
 * JD-Core Version:    0.6.2
 */