/*    */ package sun.nio.fs;
/*    */ 
/*    */ import java.nio.file.spi.FileSystemProvider;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ import sun.security.action.GetPropertyAction;
/*    */ 
/*    */ public class DefaultFileSystemProvider
/*    */ {
/*    */   private static FileSystemProvider createProvider(String paramString)
/*    */   {
/* 42 */     return (FileSystemProvider)AccessController.doPrivileged(new PrivilegedAction()
/*    */     {
/*    */       public FileSystemProvider run() {
/*    */         Class localClass;
/*    */         try {
/* 47 */           localClass = Class.forName(this.val$cn, true, null);
/*    */         } catch (ClassNotFoundException localClassNotFoundException) {
/* 49 */           throw new AssertionError(localClassNotFoundException);
/*    */         }
/*    */         try {
/* 52 */           return (FileSystemProvider)localClass.newInstance();
/*    */         } catch (IllegalAccessException localIllegalAccessException) {
/* 54 */           throw new AssertionError(localIllegalAccessException);
/*    */         } catch (InstantiationException localInstantiationException) {
/* 56 */           throw new AssertionError(localInstantiationException);
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public static FileSystemProvider create()
/*    */   {
/* 65 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
/*    */ 
/* 67 */     if (str.equals("SunOS"))
/* 68 */       return createProvider("sun.nio.fs.SolarisFileSystemProvider");
/* 69 */     if (str.equals("Linux"))
/* 70 */       return createProvider("sun.nio.fs.LinuxFileSystemProvider");
/* 71 */     if ((str.equals("Darwin")) || (str.contains("OS X")))
/* 72 */       return createProvider("sun.nio.fs.BsdFileSystemProvider");
/* 73 */     throw new AssertionError("Platform not recognized");
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.DefaultFileSystemProvider
 * JD-Core Version:    0.6.2
 */