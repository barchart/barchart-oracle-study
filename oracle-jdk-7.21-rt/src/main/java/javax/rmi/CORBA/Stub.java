/*     */ package javax.rmi.CORBA;
/*     */ 
/*     */ import com.sun.corba.se.impl.orbutil.GetPropertyAction;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.net.MalformedURLException;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.RMIClassLoader;
/*     */ import java.security.AccessController;
/*     */ import java.util.Properties;
/*     */ import org.omg.CORBA.INITIALIZE;
/*     */ import org.omg.CORBA.ORB;
/*     */ import org.omg.CORBA_2_3.portable.ObjectImpl;
/*     */ 
/*     */ public abstract class Stub extends ObjectImpl
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1087775603798577179L;
/*  61 */   private transient StubDelegate stubDelegate = null;
/*  62 */   private static Class stubDelegateClass = null;
/*     */   private static final String StubClassKey = "javax.rmi.CORBA.StubClass";
/*     */   private static final String defaultStubImplName = "com.sun.corba.se.impl.javax.rmi.CORBA.StubDelegateImpl";
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  81 */     if (this.stubDelegate == null) {
/*  82 */       setDefaultDelegate();
/*     */     }
/*     */ 
/*  85 */     if (this.stubDelegate != null) {
/*  86 */       return this.stubDelegate.hashCode(this);
/*     */     }
/*     */ 
/*  89 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 101 */     if (this.stubDelegate == null) {
/* 102 */       setDefaultDelegate();
/*     */     }
/*     */ 
/* 105 */     if (this.stubDelegate != null) {
/* 106 */       return this.stubDelegate.equals(this, paramObject);
/*     */     }
/*     */ 
/* 109 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 120 */     if (this.stubDelegate == null) {
/* 121 */       setDefaultDelegate();
/*     */     }
/*     */ 
/* 125 */     if (this.stubDelegate != null) {
/* 126 */       String str = this.stubDelegate.toString(this);
/* 127 */       if (str == null) {
/* 128 */         return super.toString();
/*     */       }
/* 130 */       return str;
/*     */     }
/*     */ 
/* 133 */     return super.toString();
/*     */   }
/*     */ 
/*     */   public void connect(ORB paramORB)
/*     */     throws RemoteException
/*     */   {
/* 149 */     if (this.stubDelegate == null) {
/* 150 */       setDefaultDelegate();
/*     */     }
/*     */ 
/* 153 */     if (this.stubDelegate != null)
/* 154 */       this.stubDelegate.connect(this, paramORB);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 165 */     if (this.stubDelegate == null) {
/* 166 */       setDefaultDelegate();
/*     */     }
/*     */ 
/* 169 */     if (this.stubDelegate != null)
/* 170 */       this.stubDelegate.readObject(this, paramObjectInputStream);
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 185 */     if (this.stubDelegate == null) {
/* 186 */       setDefaultDelegate();
/*     */     }
/*     */ 
/* 189 */     if (this.stubDelegate != null)
/* 190 */       this.stubDelegate.writeObject(this, paramObjectOutputStream);
/*     */   }
/*     */ 
/*     */   private void setDefaultDelegate()
/*     */   {
/* 195 */     if (stubDelegateClass != null)
/*     */       try {
/* 197 */         this.stubDelegate = ((StubDelegate)stubDelegateClass.newInstance());
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   private static Object createDelegateIfSpecified(String paramString1, String paramString2)
/*     */   {
/* 211 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString1));
/*     */ 
/* 213 */     if (str == null) {
/* 214 */       Properties localProperties = getORBPropertiesFile();
/* 215 */       if (localProperties != null) {
/* 216 */         str = localProperties.getProperty(paramString1);
/*     */       }
/*     */     }
/*     */ 
/* 220 */     if (str == null)
/* 221 */       str = paramString2;
/*     */     INITIALIZE localINITIALIZE;
/*     */     try
/*     */     {
/* 225 */       return loadDelegateClass(str).newInstance();
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 227 */       localINITIALIZE = new INITIALIZE("Cannot instantiate " + str);
/* 228 */       localINITIALIZE.initCause(localClassNotFoundException);
/* 229 */       throw localINITIALIZE;
/*     */     } catch (Exception localException) {
/* 231 */       localINITIALIZE = new INITIALIZE("Error while instantiating" + str);
/* 232 */       localINITIALIZE.initCause(localException);
/* 233 */     }throw localINITIALIZE;
/*     */   }
/*     */ 
/*     */   private static Class loadDelegateClass(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 241 */       ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/* 242 */       return Class.forName(paramString, false, localClassLoader);
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException1)
/*     */     {
/*     */       try
/*     */       {
/* 248 */         return RMIClassLoader.loadClass(paramString);
/*     */       } catch (MalformedURLException localMalformedURLException) {
/* 250 */         String str = "Could not load " + paramString + ": " + localMalformedURLException.toString();
/* 251 */         ClassNotFoundException localClassNotFoundException2 = new ClassNotFoundException(str);
/* 252 */         throw localClassNotFoundException2;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Properties getORBPropertiesFile()
/*     */   {
/* 260 */     return (Properties)AccessController.doPrivileged(new GetORBPropertiesFileAction());
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  67 */     Object localObject = createDelegateIfSpecified("javax.rmi.CORBA.StubClass", "com.sun.corba.se.impl.javax.rmi.CORBA.StubDelegateImpl");
/*  68 */     if (localObject != null)
/*  69 */       stubDelegateClass = localObject.getClass();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.rmi.CORBA.Stub
 * JD-Core Version:    0.6.2
 */