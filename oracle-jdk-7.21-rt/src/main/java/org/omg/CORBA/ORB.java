/*      */ package org.omg.CORBA;
/*      */ 
/*      */ import java.applet.Applet;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Properties;
/*      */ import org.omg.CORBA.ORBPackage.InconsistentTypeCode;
/*      */ import org.omg.CORBA.ORBPackage.InvalidName;
/*      */ import org.omg.CORBA.portable.OutputStream;
/*      */ 
/*      */ public abstract class ORB
/*      */ {
/*      */   private static final String ORBClassKey = "org.omg.CORBA.ORBClass";
/*      */   private static final String ORBSingletonClassKey = "org.omg.CORBA.ORBSingletonClass";
/*      */   private static final String defaultORB = "com.sun.corba.se.impl.orb.ORBImpl";
/*      */   private static final String defaultORBSingleton = "com.sun.corba.se.impl.orb.ORBSingleton";
/*      */   private static ORB singleton;
/*      */ 
/*      */   private static String getSystemProperty(String paramString)
/*      */   {
/*  200 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public java.lang.Object run() {
/*  203 */         return System.getProperty(this.val$name);
/*      */       }
/*      */     });
/*  208 */     return str;
/*      */   }
/*      */ 
/*      */   private static String getPropertyFromFile(String paramString)
/*      */   {
/*  217 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       private Properties getFileProperties(String paramAnonymousString) {
/*      */         try {
/*  221 */           File localFile = new File(paramAnonymousString);
/*  222 */           if (!localFile.exists()) {
/*  223 */             return null;
/*      */           }
/*  225 */           Properties localProperties = new Properties();
/*  226 */           FileInputStream localFileInputStream = new FileInputStream(localFile);
/*      */           try {
/*  228 */             localProperties.load(localFileInputStream);
/*      */           } finally {
/*  230 */             localFileInputStream.close();
/*      */           }
/*      */ 
/*  233 */           return localProperties; } catch (Exception localException) {
/*      */         }
/*  235 */         return null;
/*      */       }
/*      */ 
/*      */       public java.lang.Object run()
/*      */       {
/*  240 */         String str1 = System.getProperty("user.home");
/*  241 */         String str2 = str1 + File.separator + "orb.properties";
/*      */ 
/*  243 */         Properties localProperties = getFileProperties(str2);
/*      */ 
/*  245 */         if (localProperties != null) {
/*  246 */           str3 = localProperties.getProperty(this.val$name);
/*  247 */           if (str3 != null) {
/*  248 */             return str3;
/*      */           }
/*      */         }
/*  251 */         String str3 = System.getProperty("java.home");
/*  252 */         str2 = str3 + File.separator + "lib" + File.separator + "orb.properties";
/*      */ 
/*  254 */         localProperties = getFileProperties(str2);
/*      */ 
/*  256 */         if (localProperties == null) {
/*  257 */           return null;
/*      */         }
/*  259 */         return localProperties.getProperty(this.val$name);
/*      */       }
/*      */     });
/*  264 */     return str;
/*      */   }
/*      */ 
/*      */   public static synchronized ORB init()
/*      */   {
/*  293 */     if (singleton == null) {
/*  294 */       String str = getSystemProperty("org.omg.CORBA.ORBSingletonClass");
/*  295 */       if (str == null)
/*  296 */         str = getPropertyFromFile("org.omg.CORBA.ORBSingletonClass");
/*  297 */       if (str == null) {
/*  298 */         str = "com.sun.corba.se.impl.orb.ORBSingleton";
/*      */       }
/*  300 */       singleton = create_impl(str);
/*      */     }
/*  302 */     return singleton;
/*      */   }
/*      */ 
/*      */   private static ORB create_impl(String paramString)
/*      */   {
/*  307 */     ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/*  308 */     if (localClassLoader == null)
/*  309 */       localClassLoader = ClassLoader.getSystemClassLoader();
/*      */     try
/*      */     {
/*  312 */       return (ORB)Class.forName(paramString, true, localClassLoader).newInstance();
/*      */     } catch (Throwable localThrowable) {
/*  314 */       INITIALIZE localINITIALIZE = new INITIALIZE("can't instantiate default ORB implementation " + paramString);
/*      */ 
/*  316 */       localINITIALIZE.initCause(localThrowable);
/*  317 */       throw localINITIALIZE;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static ORB init(String[] paramArrayOfString, Properties paramProperties)
/*      */   {
/*  341 */     String str = null;
/*      */ 
/*  344 */     if (paramProperties != null)
/*  345 */       str = paramProperties.getProperty("org.omg.CORBA.ORBClass");
/*  346 */     if (str == null)
/*  347 */       str = getSystemProperty("org.omg.CORBA.ORBClass");
/*  348 */     if (str == null)
/*  349 */       str = getPropertyFromFile("org.omg.CORBA.ORBClass");
/*  350 */     if (str == null) {
/*  351 */       str = "com.sun.corba.se.impl.orb.ORBImpl";
/*      */     }
/*  353 */     ORB localORB = create_impl(str);
/*  354 */     localORB.set_parameters(paramArrayOfString, paramProperties);
/*  355 */     return localORB;
/*      */   }
/*      */ 
/*      */   public static ORB init(Applet paramApplet, Properties paramProperties)
/*      */   {
/*  371 */     String str = paramApplet.getParameter("org.omg.CORBA.ORBClass");
/*  372 */     if ((str == null) && (paramProperties != null))
/*  373 */       str = paramProperties.getProperty("org.omg.CORBA.ORBClass");
/*  374 */     if (str == null)
/*  375 */       str = getSystemProperty("org.omg.CORBA.ORBClass");
/*  376 */     if (str == null)
/*  377 */       str = getPropertyFromFile("org.omg.CORBA.ORBClass");
/*  378 */     if (str == null) {
/*  379 */       str = "com.sun.corba.se.impl.orb.ORBImpl";
/*      */     }
/*  381 */     ORB localORB = create_impl(str);
/*  382 */     localORB.set_parameters(paramApplet, paramProperties);
/*  383 */     return localORB;
/*      */   }
/*      */ 
/*      */   protected abstract void set_parameters(String[] paramArrayOfString, Properties paramProperties);
/*      */ 
/*      */   protected abstract void set_parameters(Applet paramApplet, Properties paramProperties);
/*      */ 
/*      */   public void connect(Object paramObject)
/*      */   {
/*  431 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public void destroy()
/*      */   {
/*  453 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public void disconnect(Object paramObject)
/*      */   {
/*  475 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public abstract String[] list_initial_services();
/*      */ 
/*      */   public abstract Object resolve_initial_references(String paramString)
/*      */     throws InvalidName;
/*      */ 
/*      */   public abstract String object_to_string(Object paramObject);
/*      */ 
/*      */   public abstract Object string_to_object(String paramString);
/*      */ 
/*      */   public abstract NVList create_list(int paramInt);
/*      */ 
/*      */   public NVList create_operation_list(Object paramObject)
/*      */   {
/*      */     try
/*      */     {
/*  576 */       String str = "org.omg.CORBA.OperationDef";
/*  577 */       localObject = null;
/*      */ 
/*  579 */       ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/*  580 */       if (localClassLoader == null) {
/*  581 */         localClassLoader = ClassLoader.getSystemClassLoader();
/*      */       }
/*  583 */       localObject = Class.forName(str, true, localClassLoader);
/*      */ 
/*  587 */       Class[] arrayOfClass = { localObject };
/*  588 */       Method localMethod = getClass().getMethod("create_operation_list", arrayOfClass);
/*      */ 
/*  592 */       java.lang.Object[] arrayOfObject = { paramObject };
/*  593 */       return (NVList)localMethod.invoke(this, arrayOfObject);
/*      */     }
/*      */     catch (InvocationTargetException localInvocationTargetException) {
/*  596 */       java.lang.Object localObject = localInvocationTargetException.getTargetException();
/*  597 */       if ((localObject instanceof Error)) {
/*  598 */         throw ((Error)localObject);
/*      */       }
/*  600 */       if ((localObject instanceof RuntimeException)) {
/*  601 */         throw ((RuntimeException)localObject);
/*      */       }
/*      */ 
/*  604 */       throw new NO_IMPLEMENT();
/*      */     }
/*      */     catch (RuntimeException localRuntimeException)
/*      */     {
/*  608 */       throw localRuntimeException;
/*      */     } catch (Exception localException) {
/*      */     }
/*  611 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public abstract NamedValue create_named_value(String paramString, Any paramAny, int paramInt);
/*      */ 
/*      */   public abstract ExceptionList create_exception_list();
/*      */ 
/*      */   public abstract ContextList create_context_list();
/*      */ 
/*      */   public abstract Context get_default_context();
/*      */ 
/*      */   public abstract Environment create_environment();
/*      */ 
/*      */   public abstract OutputStream create_output_stream();
/*      */ 
/*      */   public abstract void send_multiple_requests_oneway(Request[] paramArrayOfRequest);
/*      */ 
/*      */   public abstract void send_multiple_requests_deferred(Request[] paramArrayOfRequest);
/*      */ 
/*      */   public abstract boolean poll_next_response();
/*      */ 
/*      */   public abstract Request get_next_response()
/*      */     throws WrongTransaction;
/*      */ 
/*      */   public abstract TypeCode get_primitive_tc(TCKind paramTCKind);
/*      */ 
/*      */   public abstract TypeCode create_struct_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember);
/*      */ 
/*      */   public abstract TypeCode create_union_tc(String paramString1, String paramString2, TypeCode paramTypeCode, UnionMember[] paramArrayOfUnionMember);
/*      */ 
/*      */   public abstract TypeCode create_enum_tc(String paramString1, String paramString2, String[] paramArrayOfString);
/*      */ 
/*      */   public abstract TypeCode create_alias_tc(String paramString1, String paramString2, TypeCode paramTypeCode);
/*      */ 
/*      */   public abstract TypeCode create_exception_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember);
/*      */ 
/*      */   public abstract TypeCode create_interface_tc(String paramString1, String paramString2);
/*      */ 
/*      */   public abstract TypeCode create_string_tc(int paramInt);
/*      */ 
/*      */   public abstract TypeCode create_wstring_tc(int paramInt);
/*      */ 
/*      */   public abstract TypeCode create_sequence_tc(int paramInt, TypeCode paramTypeCode);
/*      */ 
/*      */   @Deprecated
/*      */   public abstract TypeCode create_recursive_sequence_tc(int paramInt1, int paramInt2);
/*      */ 
/*      */   public abstract TypeCode create_array_tc(int paramInt, TypeCode paramTypeCode);
/*      */ 
/*      */   public TypeCode create_native_tc(String paramString1, String paramString2)
/*      */   {
/*  901 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public TypeCode create_abstract_interface_tc(String paramString1, String paramString2)
/*      */   {
/*  915 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public TypeCode create_fixed_tc(short paramShort1, short paramShort2)
/*      */   {
/*  929 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public TypeCode create_value_tc(String paramString1, String paramString2, short paramShort, TypeCode paramTypeCode, ValueMember[] paramArrayOfValueMember)
/*      */   {
/*  958 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public TypeCode create_recursive_tc(String paramString)
/*      */   {
/* 1002 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public TypeCode create_value_box_tc(String paramString1, String paramString2, TypeCode paramTypeCode)
/*      */   {
/* 1018 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public abstract Any create_any();
/*      */ 
/*      */   @Deprecated
/*      */   public Current get_current()
/*      */   {
/* 1049 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/* 1061 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public void shutdown(boolean paramBoolean)
/*      */   {
/* 1095 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public boolean work_pending()
/*      */   {
/* 1111 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public void perform_work()
/*      */   {
/* 1125 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public boolean get_service_information(short paramShort, ServiceInformationHolder paramServiceInformationHolder)
/*      */   {
/* 1155 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public DynAny create_dyn_any(Any paramAny)
/*      */   {
/* 1175 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public DynAny create_basic_dyn_any(TypeCode paramTypeCode)
/*      */     throws InconsistentTypeCode
/*      */   {
/* 1195 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public DynStruct create_dyn_struct(TypeCode paramTypeCode)
/*      */     throws InconsistentTypeCode
/*      */   {
/* 1215 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public DynSequence create_dyn_sequence(TypeCode paramTypeCode)
/*      */     throws InconsistentTypeCode
/*      */   {
/* 1235 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public DynArray create_dyn_array(TypeCode paramTypeCode)
/*      */     throws InconsistentTypeCode
/*      */   {
/* 1256 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public DynUnion create_dyn_union(TypeCode paramTypeCode)
/*      */     throws InconsistentTypeCode
/*      */   {
/* 1276 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public DynEnum create_dyn_enum(TypeCode paramTypeCode)
/*      */     throws InconsistentTypeCode
/*      */   {
/* 1296 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ 
/*      */   public Policy create_policy(int paramInt, Any paramAny)
/*      */     throws PolicyError
/*      */   {
/* 1322 */     throw new NO_IMPLEMENT();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.ORB
 * JD-Core Version:    0.6.2
 */