/*     */ package javax.rmi;
/*     */ 
/*     */ import com.sun.corba.se.impl.orbutil.GetPropertyAction;
/*     */ import java.net.MalformedURLException;
/*     */ import java.rmi.NoSuchObjectException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.RMIClassLoader;
/*     */ import java.security.AccessController;
/*     */ import java.util.Properties;
/*     */ import javax.rmi.CORBA.PortableRemoteObjectDelegate;
/*     */ import org.omg.CORBA.INITIALIZE;
/*     */ 
/*     */ public class PortableRemoteObject
/*     */ {
/*  77 */   private static PortableRemoteObjectDelegate proDelegate = (PortableRemoteObjectDelegate)createDelegateIfSpecified("javax.rmi.CORBA.PortableRemoteObjectClass");
/*     */   private static final String PortableRemoteObjectClassKey = "javax.rmi.CORBA.PortableRemoteObjectClass";
/*     */   private static final String defaultPortableRemoteObjectImplName = "com.sun.corba.se.impl.javax.rmi.PortableRemoteObject";
/*     */ 
/*     */   protected PortableRemoteObject()
/*     */     throws RemoteException
/*     */   {
/*  86 */     if (proDelegate != null)
/*  87 */       exportObject((Remote)this);
/*     */   }
/*     */ 
/*     */   public static void exportObject(Remote paramRemote)
/*     */     throws RemoteException
/*     */   {
/* 102 */     if (proDelegate != null)
/* 103 */       proDelegate.exportObject(paramRemote);
/*     */   }
/*     */ 
/*     */   public static Remote toStub(Remote paramRemote)
/*     */     throws NoSuchObjectException
/*     */   {
/* 118 */     if (proDelegate != null) {
/* 119 */       return proDelegate.toStub(paramRemote);
/*     */     }
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   public static void unexportObject(Remote paramRemote)
/*     */     throws NoSuchObjectException
/*     */   {
/* 134 */     if (proDelegate != null)
/* 135 */       proDelegate.unexportObject(paramRemote);
/*     */   }
/*     */ 
/*     */   public static Object narrow(Object paramObject, Class paramClass)
/*     */     throws ClassCastException
/*     */   {
/* 152 */     if (proDelegate != null) {
/* 153 */       return proDelegate.narrow(paramObject, paramClass);
/*     */     }
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   public static void connect(Remote paramRemote1, Remote paramRemote2)
/*     */     throws RemoteException
/*     */   {
/* 174 */     if (proDelegate != null)
/* 175 */       proDelegate.connect(paramRemote1, paramRemote2);
/*     */   }
/*     */ 
/*     */   private static Object createDelegateIfSpecified(String paramString)
/*     */   {
/* 185 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
/*     */ 
/* 187 */     if (str == null) {
/* 188 */       Properties localProperties = getORBPropertiesFile();
/* 189 */       if (localProperties != null) {
/* 190 */         str = localProperties.getProperty(paramString);
/*     */       }
/*     */     }
/* 193 */     if (str == null)
/* 194 */       str = "com.sun.corba.se.impl.javax.rmi.PortableRemoteObject";
/*     */     INITIALIZE localINITIALIZE;
/*     */     try
/*     */     {
/* 198 */       return loadDelegateClass(str).newInstance();
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 200 */       localINITIALIZE = new INITIALIZE("Cannot instantiate " + str);
/* 201 */       localINITIALIZE.initCause(localClassNotFoundException);
/* 202 */       throw localINITIALIZE;
/*     */     } catch (Exception localException) {
/* 204 */       localINITIALIZE = new INITIALIZE("Error while instantiating" + str);
/* 205 */       localINITIALIZE.initCause(localException);
/* 206 */     }throw localINITIALIZE;
/*     */   }
/*     */ 
/*     */   private static Class loadDelegateClass(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 214 */       ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/* 215 */       return Class.forName(paramString, false, localClassLoader);
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException1)
/*     */     {
/*     */       try
/*     */       {
/* 221 */         return RMIClassLoader.loadClass(paramString);
/*     */       } catch (MalformedURLException localMalformedURLException) {
/* 223 */         String str = "Could not load " + paramString + ": " + localMalformedURLException.toString();
/* 224 */         ClassNotFoundException localClassNotFoundException2 = new ClassNotFoundException(str);
/* 225 */         throw localClassNotFoundException2;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Properties getORBPropertiesFile()
/*     */   {
/* 233 */     return (Properties)AccessController.doPrivileged(new GetORBPropertiesFileAction());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.rmi.PortableRemoteObject
 * JD-Core Version:    0.6.2
 */