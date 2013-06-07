/*     */ package java.beans;
/*     */ 
/*     */ import com.sun.beans.finder.ClassFinder;
/*     */ import java.applet.Applet;
/*     */ import java.applet.AppletContext;
/*     */ import java.applet.AppletStub;
/*     */ import java.beans.beancontext.BeanContext;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ public class Beans
/*     */ {
/*     */   public static Object instantiate(ClassLoader paramClassLoader, String paramString)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/*  78 */     return instantiate(paramClassLoader, paramString, null, null);
/*     */   }
/*     */ 
/*     */   public static Object instantiate(ClassLoader paramClassLoader, String paramString, BeanContext paramBeanContext)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/*  99 */     return instantiate(paramClassLoader, paramString, paramBeanContext, null);
/*     */   }
/*     */ 
/*     */   public static Object instantiate(ClassLoader paramClassLoader, String paramString, BeanContext paramBeanContext, AppletInitializer paramAppletInitializer)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 157 */     Object localObject1 = null;
/* 158 */     Object localObject2 = null;
/* 159 */     int i = 0;
/* 160 */     Object localObject3 = null;
/*     */ 
/* 167 */     if (paramClassLoader == null) {
/*     */       try {
/* 169 */         paramClassLoader = ClassLoader.getSystemClassLoader();
/*     */       }
/*     */       catch (SecurityException localSecurityException)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 177 */     final String str1 = paramString.replace('.', '/').concat(".ser");
/* 178 */     ClassLoader localClassLoader1 = paramClassLoader;
/* 179 */     InputStream localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 182 */         if (this.val$loader == null) {
/* 183 */           return ClassLoader.getSystemResourceAsStream(str1);
/*     */         }
/* 185 */         return this.val$loader.getResourceAsStream(str1);
/*     */       }
/*     */     });
/* 188 */     if (localInputStream != null)
/*     */       try {
/* 190 */         if (paramClassLoader == null)
/* 191 */           localObject1 = new ObjectInputStream(localInputStream);
/*     */         else {
/* 193 */           localObject1 = new ObjectInputStreamWithLoader(localInputStream, paramClassLoader);
/*     */         }
/* 195 */         localObject2 = ((ObjectInputStream)localObject1).readObject();
/* 196 */         i = 1;
/* 197 */         ((ObjectInputStream)localObject1).close();
/*     */       } catch (IOException localIOException) {
/* 199 */         localInputStream.close();
/*     */ 
/* 202 */         localObject3 = localIOException;
/*     */       } catch (ClassNotFoundException localClassNotFoundException1) {
/* 204 */         localInputStream.close();
/* 205 */         throw localClassNotFoundException1;
/*     */       }
/*     */     Object localObject4;
/* 209 */     if (localObject2 == null)
/*     */     {
/*     */       try
/*     */       {
/* 214 */         localObject4 = ClassFinder.findClass(paramString, paramClassLoader);
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException2)
/*     */       {
/* 219 */         if (localObject3 != null) {
/* 220 */           throw localObject3;
/*     */         }
/* 222 */         throw localClassNotFoundException2;
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 230 */         localObject2 = ((Class)localObject4).newInstance();
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 234 */         throw new ClassNotFoundException("" + localObject4 + " : " + localException, localException);
/*     */       }
/*     */     }
/*     */ 
/* 238 */     if (localObject2 != null)
/*     */     {
/* 242 */       localObject4 = null;
/*     */ 
/* 244 */       if ((localObject2 instanceof Applet)) {
/* 245 */         Applet localApplet = (Applet)localObject2;
/* 246 */         int j = paramAppletInitializer == null ? 1 : 0;
/*     */ 
/* 248 */         if (j != 0)
/*     */         {
/*     */           final String str2;
/* 261 */           if (i != 0)
/*     */           {
/* 263 */             str2 = paramString.replace('.', '/').concat(".ser");
/*     */           }
/*     */           else {
/* 266 */             str2 = paramString.replace('.', '/').concat(".class");
/*     */           }
/*     */ 
/* 269 */           URL localURL1 = null;
/* 270 */           URL localURL2 = null;
/* 271 */           URL localURL3 = null;
/*     */ 
/* 275 */           ClassLoader localClassLoader2 = paramClassLoader;
/* 276 */           localURL1 = (URL)AccessController.doPrivileged(new PrivilegedAction()
/*     */           {
/*     */             public Object run()
/*     */             {
/* 280 */               if (this.val$cloader == null) {
/* 281 */                 return ClassLoader.getSystemResource(str2);
/*     */               }
/*     */ 
/* 284 */               return this.val$cloader.getResource(str2);
/*     */             }
/*     */           });
/* 296 */           if (localURL1 != null) {
/* 297 */             localObject5 = localURL1.toExternalForm();
/*     */ 
/* 299 */             if (((String)localObject5).endsWith(str2)) {
/* 300 */               int k = ((String)localObject5).length() - str2.length();
/* 301 */               localURL2 = new URL(((String)localObject5).substring(0, k));
/* 302 */               localURL3 = localURL2;
/*     */ 
/* 304 */               k = ((String)localObject5).lastIndexOf('/');
/*     */ 
/* 306 */               if (k >= 0) {
/* 307 */                 localURL3 = new URL(((String)localObject5).substring(0, k + 1));
/*     */               }
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 313 */           Object localObject5 = new BeansAppletContext(localApplet);
/*     */ 
/* 315 */           localObject4 = new BeansAppletStub(localApplet, (AppletContext)localObject5, localURL2, localURL3);
/* 316 */           localApplet.setStub((AppletStub)localObject4);
/*     */         } else {
/* 318 */           paramAppletInitializer.initialize(localApplet, paramBeanContext);
/*     */         }
/*     */ 
/* 323 */         if (paramBeanContext != null) {
/* 324 */           paramBeanContext.add(localObject2);
/*     */         }
/*     */ 
/* 330 */         if (i == 0)
/*     */         {
/* 334 */           localApplet.setSize(100, 100);
/* 335 */           localApplet.init();
/*     */         }
/*     */ 
/* 338 */         if (j != 0)
/* 339 */           ((BeansAppletStub)localObject4).active = true;
/* 340 */         else paramAppletInitializer.activate(localApplet);
/*     */       }
/* 342 */       else if (paramBeanContext != null) { paramBeanContext.add(localObject2); }
/*     */ 
/*     */     }
/* 345 */     return localObject2;
/*     */   }
/*     */ 
/*     */   public static Object getInstanceOf(Object paramObject, Class<?> paramClass)
/*     */   {
/* 365 */     return paramObject;
/*     */   }
/*     */ 
/*     */   public static boolean isInstanceOf(Object paramObject, Class<?> paramClass)
/*     */   {
/* 380 */     return Introspector.isSubclass(paramObject.getClass(), paramClass);
/*     */   }
/*     */ 
/*     */   public static boolean isDesignTime()
/*     */   {
/* 393 */     return ThreadGroupContext.getContext().isDesignTime();
/*     */   }
/*     */ 
/*     */   public static boolean isGuiAvailable()
/*     */   {
/* 410 */     return ThreadGroupContext.getContext().isGuiAvailable();
/*     */   }
/*     */ 
/*     */   public static void setDesignTime(boolean paramBoolean)
/*     */     throws SecurityException
/*     */   {
/* 432 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 433 */     if (localSecurityManager != null) {
/* 434 */       localSecurityManager.checkPropertiesAccess();
/*     */     }
/* 436 */     ThreadGroupContext.getContext().setDesignTime(paramBoolean);
/*     */   }
/*     */ 
/*     */   public static void setGuiAvailable(boolean paramBoolean)
/*     */     throws SecurityException
/*     */   {
/* 458 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 459 */     if (localSecurityManager != null) {
/* 460 */       localSecurityManager.checkPropertiesAccess();
/*     */     }
/* 462 */     ThreadGroupContext.getContext().setGuiAvailable(paramBoolean);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.Beans
 * JD-Core Version:    0.6.2
 */