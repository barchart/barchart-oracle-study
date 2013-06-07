/*     */ package javax.rmi.CORBA;
/*     */ 
/*     */ import com.sun.corba.se.impl.orbutil.GetPropertyAction;
/*     */ import java.net.MalformedURLException;
/*     */ import java.rmi.NoSuchObjectException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.RMIClassLoader;
/*     */ import java.security.AccessController;
/*     */ import java.util.Properties;
/*     */ import org.omg.CORBA.INITIALIZE;
/*     */ import org.omg.CORBA.ORB;
/*     */ import org.omg.CORBA.SystemException;
/*     */ import org.omg.CORBA.portable.InputStream;
/*     */ import org.omg.CORBA.portable.OutputStream;
/*     */ 
/*     */ public class Util
/*     */ {
/*  69 */   private static UtilDelegate utilDelegate = (UtilDelegate)createDelegateIfSpecified("javax.rmi.CORBA.UtilClass", "com.sun.corba.se.impl.javax.rmi.CORBA.Util");
/*     */   private static final String UtilClassKey = "javax.rmi.CORBA.UtilClass";
/*     */   private static final String defaultUtilImplName = "com.sun.corba.se.impl.javax.rmi.CORBA.Util";
/*     */ 
/*     */   public static RemoteException mapSystemException(SystemException paramSystemException)
/*     */   {
/*  82 */     if (utilDelegate != null) {
/*  83 */       return utilDelegate.mapSystemException(paramSystemException);
/*     */     }
/*  85 */     return null;
/*     */   }
/*     */ 
/*     */   public static void writeAny(OutputStream paramOutputStream, Object paramObject)
/*     */   {
/*  95 */     if (utilDelegate != null)
/*  96 */       utilDelegate.writeAny(paramOutputStream, paramObject);
/*     */   }
/*     */ 
/*     */   public static Object readAny(InputStream paramInputStream)
/*     */   {
/* 107 */     if (utilDelegate != null) {
/* 108 */       return utilDelegate.readAny(paramInputStream);
/*     */     }
/* 110 */     return null;
/*     */   }
/*     */ 
/*     */   public static void writeRemoteObject(OutputStream paramOutputStream, Object paramObject)
/*     */   {
/* 126 */     if (utilDelegate != null)
/* 127 */       utilDelegate.writeRemoteObject(paramOutputStream, paramObject);
/*     */   }
/*     */ 
/*     */   public static void writeAbstractObject(OutputStream paramOutputStream, Object paramObject)
/*     */   {
/* 146 */     if (utilDelegate != null)
/* 147 */       utilDelegate.writeAbstractObject(paramOutputStream, paramObject);
/*     */   }
/*     */ 
/*     */   public static void registerTarget(Tie paramTie, Remote paramRemote)
/*     */   {
/* 160 */     if (utilDelegate != null)
/* 161 */       utilDelegate.registerTarget(paramTie, paramRemote);
/*     */   }
/*     */ 
/*     */   public static void unexportObject(Remote paramRemote)
/*     */     throws NoSuchObjectException
/*     */   {
/* 176 */     if (utilDelegate != null)
/* 177 */       utilDelegate.unexportObject(paramRemote);
/*     */   }
/*     */ 
/*     */   public static Tie getTie(Remote paramRemote)
/*     */   {
/* 188 */     if (utilDelegate != null) {
/* 189 */       return utilDelegate.getTie(paramRemote);
/*     */     }
/* 191 */     return null;
/*     */   }
/*     */ 
/*     */   public static ValueHandler createValueHandler()
/*     */   {
/* 202 */     if (utilDelegate != null) {
/* 203 */       return utilDelegate.createValueHandler();
/*     */     }
/* 205 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getCodebase(Class paramClass)
/*     */   {
/* 214 */     if (utilDelegate != null) {
/* 215 */       return utilDelegate.getCodebase(paramClass);
/*     */     }
/* 217 */     return null;
/*     */   }
/*     */ 
/*     */   public static Class loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader)
/*     */     throws ClassNotFoundException
/*     */   {
/* 250 */     if (utilDelegate != null) {
/* 251 */       return utilDelegate.loadClass(paramString1, paramString2, paramClassLoader);
/*     */     }
/* 253 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isLocal(Stub paramStub)
/*     */     throws RemoteException
/*     */   {
/* 279 */     if (utilDelegate != null) {
/* 280 */       return utilDelegate.isLocal(paramStub);
/*     */     }
/*     */ 
/* 283 */     return false;
/*     */   }
/*     */ 
/*     */   public static RemoteException wrapException(Throwable paramThrowable)
/*     */   {
/* 294 */     if (utilDelegate != null) {
/* 295 */       return utilDelegate.wrapException(paramThrowable);
/*     */     }
/*     */ 
/* 298 */     return null;
/*     */   }
/*     */ 
/*     */   public static Object[] copyObjects(Object[] paramArrayOfObject, ORB paramORB)
/*     */     throws RemoteException
/*     */   {
/* 313 */     if (utilDelegate != null) {
/* 314 */       return utilDelegate.copyObjects(paramArrayOfObject, paramORB);
/*     */     }
/*     */ 
/* 317 */     return null;
/*     */   }
/*     */ 
/*     */   public static Object copyObject(Object paramObject, ORB paramORB)
/*     */     throws RemoteException
/*     */   {
/* 331 */     if (utilDelegate != null) {
/* 332 */       return utilDelegate.copyObject(paramObject, paramORB);
/*     */     }
/* 334 */     return null;
/*     */   }
/*     */ 
/*     */   private static Object createDelegateIfSpecified(String paramString1, String paramString2)
/*     */   {
/* 344 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString1));
/*     */ 
/* 346 */     if (str == null) {
/* 347 */       Properties localProperties = getORBPropertiesFile();
/* 348 */       if (localProperties != null) {
/* 349 */         str = localProperties.getProperty(paramString1);
/*     */       }
/*     */     }
/*     */ 
/* 353 */     if (str == null)
/* 354 */       str = paramString2;
/*     */     INITIALIZE localINITIALIZE;
/*     */     try
/*     */     {
/* 358 */       return loadDelegateClass(str).newInstance();
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 360 */       localINITIALIZE = new INITIALIZE("Cannot instantiate " + str);
/* 361 */       localINITIALIZE.initCause(localClassNotFoundException);
/* 362 */       throw localINITIALIZE;
/*     */     } catch (Exception localException) {
/* 364 */       localINITIALIZE = new INITIALIZE("Error while instantiating" + str);
/* 365 */       localINITIALIZE.initCause(localException);
/* 366 */     }throw localINITIALIZE;
/*     */   }
/*     */ 
/*     */   private static Class loadDelegateClass(String paramString) throws ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 373 */       ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/* 374 */       return Class.forName(paramString, false, localClassLoader);
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException1)
/*     */     {
/*     */       try
/*     */       {
/* 380 */         return RMIClassLoader.loadClass(paramString);
/*     */       } catch (MalformedURLException localMalformedURLException) {
/* 382 */         String str = "Could not load " + paramString + ": " + localMalformedURLException.toString();
/* 383 */         ClassNotFoundException localClassNotFoundException2 = new ClassNotFoundException(str);
/* 384 */         throw localClassNotFoundException2;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Properties getORBPropertiesFile()
/*     */   {
/* 392 */     return (Properties)AccessController.doPrivileged(new GetORBPropertiesFileAction());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.rmi.CORBA.Util
 * JD-Core Version:    0.6.2
 */