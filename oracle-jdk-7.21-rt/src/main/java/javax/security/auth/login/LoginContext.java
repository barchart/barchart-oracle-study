/*     */ package javax.security.auth.login;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.Security;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.security.auth.AuthPermission;
/*     */ import javax.security.auth.Subject;
/*     */ import javax.security.auth.callback.Callback;
/*     */ import javax.security.auth.callback.CallbackHandler;
/*     */ import javax.security.auth.callback.UnsupportedCallbackException;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.PendingException;
/*     */ import sun.security.util.ResourcesMgr;
/*     */ 
/*     */ public class LoginContext
/*     */ {
/*     */   private static final String INIT_METHOD = "initialize";
/*     */   private static final String LOGIN_METHOD = "login";
/*     */   private static final String COMMIT_METHOD = "commit";
/*     */   private static final String ABORT_METHOD = "abort";
/*     */   private static final String LOGOUT_METHOD = "logout";
/*     */   private static final String OTHER = "other";
/*     */   private static final String DEFAULT_HANDLER = "auth.login.defaultCallbackHandler";
/* 213 */   private Subject subject = null;
/* 214 */   private boolean subjectProvided = false;
/* 215 */   private boolean loginSucceeded = false;
/*     */   private CallbackHandler callbackHandler;
/* 217 */   private Map state = new HashMap();
/*     */   private Configuration config;
/* 220 */   private boolean configProvided = false;
/* 221 */   private AccessControlContext creatorAcc = null;
/*     */   private ModuleInfo[] moduleStack;
/* 223 */   private ClassLoader contextClassLoader = null;
/* 224 */   private static final Class[] PARAMS = new Class[0];
/*     */ 
/* 229 */   private int moduleIndex = 0;
/* 230 */   private LoginException firstError = null;
/* 231 */   private LoginException firstRequiredError = null;
/* 232 */   private boolean success = false;
/*     */ 
/* 234 */   private static final Debug debug = Debug.getInstance("logincontext", "\t[LoginContext]");
/*     */ 
/*     */   private void init(String paramString)
/*     */     throws LoginException
/*     */   {
/* 239 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 240 */     if ((localSecurityManager != null) && (!this.configProvided)) {
/* 241 */       localSecurityManager.checkPermission(new AuthPermission("createLoginContext." + paramString));
/*     */     }
/*     */ 
/* 245 */     if (paramString == null) {
/* 246 */       throw new LoginException(ResourcesMgr.getString("Invalid.null.input.name"));
/*     */     }
/*     */ 
/* 250 */     if (this.config == null) {
/* 251 */       this.config = ((Configuration)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Configuration run() {
/* 254 */           return Configuration.getConfiguration();
/*     */         }
/*     */ 
/*     */       }));
/*     */     }
/*     */ 
/* 260 */     AppConfigurationEntry[] arrayOfAppConfigurationEntry = this.config.getAppConfigurationEntry(paramString);
/* 261 */     if (arrayOfAppConfigurationEntry == null)
/*     */     {
/* 263 */       if ((localSecurityManager != null) && (!this.configProvided)) {
/* 264 */         localSecurityManager.checkPermission(new AuthPermission("createLoginContext.other"));
/*     */       }
/*     */ 
/* 268 */       arrayOfAppConfigurationEntry = this.config.getAppConfigurationEntry("other");
/* 269 */       if (arrayOfAppConfigurationEntry == null) {
/* 270 */         MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("No.LoginModules.configured.for.name"));
/*     */ 
/* 272 */         Object[] arrayOfObject = { paramString };
/* 273 */         throw new LoginException(localMessageFormat.format(arrayOfObject));
/*     */       }
/*     */     }
/* 276 */     this.moduleStack = new ModuleInfo[arrayOfAppConfigurationEntry.length];
/* 277 */     for (int i = 0; i < arrayOfAppConfigurationEntry.length; i++)
/*     */     {
/* 279 */       this.moduleStack[i] = new ModuleInfo(new AppConfigurationEntry(arrayOfAppConfigurationEntry[i].getLoginModuleName(), arrayOfAppConfigurationEntry[i].getControlFlag(), arrayOfAppConfigurationEntry[i].getOptions()), null);
/*     */     }
/*     */ 
/* 287 */     this.contextClassLoader = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public ClassLoader run() {
/* 290 */         return Thread.currentThread().getContextClassLoader();
/*     */       }
/*     */     }));
/*     */   }
/*     */ 
/*     */   private void loadDefaultCallbackHandler()
/*     */     throws LoginException
/*     */   {
/*     */     try
/*     */     {
/* 300 */       final ClassLoader localClassLoader = this.contextClassLoader;
/*     */ 
/* 302 */       this.callbackHandler = ((CallbackHandler)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public CallbackHandler run() throws Exception {
/* 305 */           String str = Security.getProperty("auth.login.defaultCallbackHandler");
/*     */ 
/* 307 */           if ((str == null) || (str.length() == 0))
/* 308 */             return null;
/* 309 */           Class localClass = Class.forName(str, true, localClassLoader);
/*     */ 
/* 312 */           return (CallbackHandler)localClass.newInstance();
/*     */         } } ));
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException) {
/* 316 */       throw new LoginException(localPrivilegedActionException.getException().toString());
/*     */     }
/*     */ 
/* 320 */     if ((this.callbackHandler != null) && (!this.configProvided))
/* 321 */       this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), this.callbackHandler);
/*     */   }
/*     */ 
/*     */   public LoginContext(String paramString)
/*     */     throws LoginException
/*     */   {
/* 349 */     init(paramString);
/* 350 */     loadDefaultCallbackHandler();
/*     */   }
/*     */ 
/*     */   public LoginContext(String paramString, Subject paramSubject)
/*     */     throws LoginException
/*     */   {
/* 382 */     init(paramString);
/* 383 */     if (paramSubject == null) {
/* 384 */       throw new LoginException(ResourcesMgr.getString("invalid.null.Subject.provided"));
/*     */     }
/* 386 */     this.subject = paramSubject;
/* 387 */     this.subjectProvided = true;
/* 388 */     loadDefaultCallbackHandler();
/*     */   }
/*     */ 
/*     */   public LoginContext(String paramString, CallbackHandler paramCallbackHandler)
/*     */     throws LoginException
/*     */   {
/* 418 */     init(paramString);
/* 419 */     if (paramCallbackHandler == null) {
/* 420 */       throw new LoginException(ResourcesMgr.getString("invalid.null.CallbackHandler.provided"));
/*     */     }
/* 422 */     this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), paramCallbackHandler);
/*     */   }
/*     */ 
/*     */   public LoginContext(String paramString, Subject paramSubject, CallbackHandler paramCallbackHandler)
/*     */     throws LoginException
/*     */   {
/* 459 */     this(paramString, paramSubject);
/* 460 */     if (paramCallbackHandler == null) {
/* 461 */       throw new LoginException(ResourcesMgr.getString("invalid.null.CallbackHandler.provided"));
/*     */     }
/* 463 */     this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), paramCallbackHandler);
/*     */   }
/*     */ 
/*     */   public LoginContext(String paramString, Subject paramSubject, CallbackHandler paramCallbackHandler, Configuration paramConfiguration)
/*     */     throws LoginException
/*     */   {
/* 508 */     this.config = paramConfiguration;
/* 509 */     this.configProvided = (paramConfiguration != null);
/* 510 */     if (this.configProvided) {
/* 511 */       this.creatorAcc = AccessController.getContext();
/*     */     }
/*     */ 
/* 514 */     init(paramString);
/* 515 */     if (paramSubject != null) {
/* 516 */       this.subject = paramSubject;
/* 517 */       this.subjectProvided = true;
/*     */     }
/* 519 */     if (paramCallbackHandler == null)
/* 520 */       loadDefaultCallbackHandler();
/* 521 */     else if (!this.configProvided) {
/* 522 */       this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), paramCallbackHandler);
/*     */     }
/*     */     else
/*     */     {
/* 526 */       this.callbackHandler = paramCallbackHandler;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void login()
/*     */     throws LoginException
/*     */   {
/* 581 */     this.loginSucceeded = false;
/*     */ 
/* 583 */     if (this.subject == null) {
/* 584 */       this.subject = new Subject();
/*     */     }
/*     */     try
/*     */     {
/* 588 */       if (this.configProvided)
/*     */       {
/* 590 */         invokeCreatorPriv("login");
/* 591 */         invokeCreatorPriv("commit");
/*     */       }
/*     */       else {
/* 594 */         invokePriv("login");
/* 595 */         invokePriv("commit");
/*     */       }
/* 597 */       this.loginSucceeded = true;
/*     */     } catch (LoginException localLoginException1) {
/*     */       try {
/* 600 */         if (this.configProvided)
/* 601 */           invokeCreatorPriv("abort");
/*     */         else
/* 603 */           invokePriv("abort");
/*     */       }
/*     */       catch (LoginException localLoginException2) {
/* 606 */         throw localLoginException1;
/*     */       }
/* 608 */       throw localLoginException1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void logout()
/*     */     throws LoginException
/*     */   {
/* 634 */     if (this.subject == null) {
/* 635 */       throw new LoginException(ResourcesMgr.getString("null.subject.logout.called.before.login"));
/*     */     }
/*     */ 
/* 639 */     if (this.configProvided)
/*     */     {
/* 641 */       invokeCreatorPriv("logout");
/*     */     }
/*     */     else
/* 644 */       invokePriv("logout");
/*     */   }
/*     */ 
/*     */   public Subject getSubject()
/*     */   {
/* 663 */     if ((!this.loginSucceeded) && (!this.subjectProvided))
/* 664 */       return null;
/* 665 */     return this.subject;
/*     */   }
/*     */ 
/*     */   private void clearState() {
/* 669 */     this.moduleIndex = 0;
/* 670 */     this.firstError = null;
/* 671 */     this.firstRequiredError = null;
/* 672 */     this.success = false;
/*     */   }
/*     */ 
/*     */   private void throwException(LoginException paramLoginException1, LoginException paramLoginException2)
/*     */     throws LoginException
/*     */   {
/* 679 */     clearState();
/*     */ 
/* 682 */     LoginException localLoginException = paramLoginException1 != null ? paramLoginException1 : paramLoginException2;
/* 683 */     throw localLoginException;
/*     */   }
/*     */ 
/*     */   private void invokePriv(final String paramString)
/*     */     throws LoginException
/*     */   {
/*     */     try
/*     */     {
/* 695 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Void run() throws LoginException {
/* 698 */           LoginContext.this.invoke(paramString);
/* 699 */           return null;
/*     */         } } );
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException) {
/* 703 */       throw ((LoginException)localPrivilegedActionException.getException());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void invokeCreatorPriv(final String paramString)
/*     */     throws LoginException
/*     */   {
/*     */     try
/*     */     {
/* 718 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Void run() throws LoginException {
/* 721 */           LoginContext.this.invoke(paramString);
/* 722 */           return null;
/*     */         }
/*     */       }
/*     */       , this.creatorAcc);
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException)
/*     */     {
/* 726 */       throw ((LoginException)localPrivilegedActionException.getException());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void invoke(String paramString)
/*     */     throws LoginException
/*     */   {
/* 735 */     for (int i = this.moduleIndex; i < this.moduleStack.length; this.moduleIndex += 1)
/*     */     {
/*     */       try {
/* 738 */         int j = 0;
/* 739 */         localObject1 = null;
/*     */ 
/* 741 */         if (this.moduleStack[i].module != null) {
/* 742 */           localObject1 = this.moduleStack[i].module.getClass().getMethods();
/*     */         }
/*     */         else
/*     */         {
/* 746 */           localObject2 = Class.forName(this.moduleStack[i].entry.getLoginModuleName(), true, this.contextClassLoader);
/*     */ 
/* 751 */           Constructor localConstructor = ((Class)localObject2).getConstructor(PARAMS);
/* 752 */           Object[] arrayOfObject1 = new Object[0];
/*     */ 
/* 756 */           this.moduleStack[i].module = localConstructor.newInstance(arrayOfObject1);
/*     */ 
/* 758 */           localObject1 = this.moduleStack[i].module.getClass().getMethods();
/*     */ 
/* 761 */           for (j = 0; (j < localObject1.length) && 
/* 762 */             (!localObject1[j].getName().equals("initialize")); j++);
/* 766 */           Object[] arrayOfObject2 = { this.subject, this.callbackHandler, this.state, this.moduleStack[i].entry.getOptions() };
/*     */ 
/* 771 */           localObject1[j].invoke(this.moduleStack[i].module, arrayOfObject2);
/*     */         }
/*     */ 
/* 775 */         for (j = 0; (j < localObject1.length) && 
/* 776 */           (!localObject1[j].getName().equals(paramString)); j++);
/* 781 */         localObject2 = new Object[0];
/*     */ 
/* 784 */         boolean bool = ((Boolean)localObject1[j].invoke(this.moduleStack[i].module, (Object[])localObject2)).booleanValue();
/*     */ 
/* 787 */         if (bool == true)
/*     */         {
/* 790 */           if ((!paramString.equals("abort")) && (!paramString.equals("logout")) && (this.moduleStack[i].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT) && (this.firstRequiredError == null))
/*     */           {
/* 797 */             clearState();
/*     */ 
/* 799 */             if (debug != null)
/* 800 */               debug.println(paramString + " SUFFICIENT success");
/* 801 */             return;
/*     */           }
/*     */ 
/* 804 */           if (debug != null)
/* 805 */             debug.println(paramString + " success");
/* 806 */           this.success = true;
/*     */         }
/* 808 */         else if (debug != null) {
/* 809 */           debug.println(paramString + " ignored");
/*     */         }
/*     */       }
/*     */       catch (NoSuchMethodException localNoSuchMethodException) {
/* 813 */         localObject1 = new MessageFormat(ResourcesMgr.getString("unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor"));
/*     */ 
/* 815 */         localObject2 = new Object[] { this.moduleStack[i].entry.getLoginModuleName() };
/* 816 */         throwException(null, new LoginException(((MessageFormat)localObject1).format(localObject2)));
/*     */       } catch (InstantiationException localInstantiationException) {
/* 818 */         throwException(null, new LoginException(ResourcesMgr.getString("unable.to.instantiate.LoginModule.") + localInstantiationException.getMessage()));
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException)
/*     */       {
/* 822 */         throwException(null, new LoginException(ResourcesMgr.getString("unable.to.find.LoginModule.class.") + localClassNotFoundException.getMessage()));
/*     */       }
/*     */       catch (IllegalAccessException localIllegalAccessException)
/*     */       {
/* 826 */         throwException(null, new LoginException(ResourcesMgr.getString("unable.to.access.LoginModule.") + localIllegalAccessException.getMessage()));
/*     */       }
/*     */       catch (InvocationTargetException localInvocationTargetException)
/*     */       {
/*     */         Object localObject1;
/*     */         Object localObject2;
/* 835 */         if (((localInvocationTargetException.getCause() instanceof PendingException)) && (paramString.equals("login")))
/*     */         {
/* 859 */           throw ((PendingException)localInvocationTargetException.getCause());
/*     */         }
/* 861 */         if ((localInvocationTargetException.getCause() instanceof LoginException))
/*     */         {
/* 863 */           localObject1 = (LoginException)localInvocationTargetException.getCause();
/*     */         }
/* 865 */         else if ((localInvocationTargetException.getCause() instanceof SecurityException))
/*     */         {
/* 870 */           localObject1 = new LoginException("Security Exception");
/* 871 */           ((LoginException)localObject1).initCause(new SecurityException());
/* 872 */           if (debug != null) {
/* 873 */             debug.println("original security exception with detail msg replaced by new exception with empty detail msg");
/*     */ 
/* 876 */             debug.println("original security exception: " + localInvocationTargetException.getCause().toString());
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 882 */           localObject2 = new StringWriter();
/* 883 */           localInvocationTargetException.getCause().printStackTrace(new PrintWriter((Writer)localObject2));
/*     */ 
/* 885 */           ((StringWriter)localObject2).flush();
/* 886 */           localObject1 = new LoginException(((StringWriter)localObject2).toString());
/*     */         }
/*     */ 
/* 889 */         if (this.moduleStack[i].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE)
/*     */         {
/* 892 */           if (debug != null) {
/* 893 */             debug.println(paramString + " REQUISITE failure");
/*     */           }
/*     */ 
/* 896 */           if ((paramString.equals("abort")) || (paramString.equals("logout")))
/*     */           {
/* 898 */             if (this.firstRequiredError == null)
/* 899 */               this.firstRequiredError = ((LoginException)localObject1);
/*     */           }
/* 901 */           else throwException(this.firstRequiredError, (LoginException)localObject1);
/*     */ 
/*     */         }
/* 904 */         else if (this.moduleStack[i].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED)
/*     */         {
/* 907 */           if (debug != null) {
/* 908 */             debug.println(paramString + " REQUIRED failure");
/*     */           }
/*     */ 
/* 911 */           if (this.firstRequiredError == null)
/* 912 */             this.firstRequiredError = ((LoginException)localObject1);
/*     */         }
/*     */         else
/*     */         {
/* 916 */           if (debug != null) {
/* 917 */             debug.println(paramString + " OPTIONAL failure");
/*     */           }
/*     */ 
/* 920 */           if (this.firstError == null)
/* 921 */             this.firstError = ((LoginException)localObject1);
/*     */         }
/*     */       }
/* 735 */       i++;
/*     */     }
/*     */ 
/* 927 */     if (this.firstRequiredError != null)
/*     */     {
/* 929 */       throwException(this.firstRequiredError, null);
/* 930 */     } else if ((!this.success) && (this.firstError != null))
/*     */     {
/* 932 */       throwException(this.firstError, null);
/* 933 */     } else if (!this.success)
/*     */     {
/* 935 */       throwException(new LoginException(ResourcesMgr.getString("Login.Failure.all.modules.ignored")), null);
/*     */     }
/*     */     else
/*     */     {
/* 941 */       clearState();
/* 942 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ModuleInfo
/*     */   {
/*     */     AppConfigurationEntry entry;
/*     */     Object module;
/*     */ 
/*     */     ModuleInfo(AppConfigurationEntry paramAppConfigurationEntry, Object paramObject)
/*     */     {
/* 992 */       this.entry = paramAppConfigurationEntry;
/* 993 */       this.module = paramObject;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SecureCallbackHandler
/*     */     implements CallbackHandler
/*     */   {
/*     */     private final AccessControlContext acc;
/*     */     private final CallbackHandler ch;
/*     */ 
/*     */     SecureCallbackHandler(AccessControlContext paramAccessControlContext, CallbackHandler paramCallbackHandler)
/*     */     {
/* 958 */       this.acc = paramAccessControlContext;
/* 959 */       this.ch = paramCallbackHandler;
/*     */     }
/*     */ 
/*     */     public void handle(final Callback[] paramArrayOfCallback) throws IOException, UnsupportedCallbackException
/*     */     {
/*     */       try {
/* 965 */         AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */         {
/*     */           public Void run() throws IOException, UnsupportedCallbackException
/*     */           {
/* 969 */             LoginContext.SecureCallbackHandler.this.ch.handle(paramArrayOfCallback);
/* 970 */             return null;
/*     */           }
/*     */         }
/*     */         , this.acc);
/*     */       }
/*     */       catch (PrivilegedActionException localPrivilegedActionException)
/*     */       {
/* 974 */         if ((localPrivilegedActionException.getException() instanceof IOException)) {
/* 975 */           throw ((IOException)localPrivilegedActionException.getException());
/*     */         }
/* 977 */         throw ((UnsupportedCallbackException)localPrivilegedActionException.getException());
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.login.LoginContext
 * JD-Core Version:    0.6.2
 */