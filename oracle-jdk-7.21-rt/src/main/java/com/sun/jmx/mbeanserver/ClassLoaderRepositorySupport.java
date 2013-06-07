/*     */ package com.sun.jmx.mbeanserver;
/*     */ 
/*     */ import com.sun.jmx.defaults.JmxProperties;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.loading.PrivateClassLoader;
/*     */ import sun.reflect.misc.ReflectUtil;
/*     */ 
/*     */ final class ClassLoaderRepositorySupport
/*     */   implements ModifiableClassLoaderRepository
/*     */ {
/*  67 */   private static final LoaderEntry[] EMPTY_LOADER_ARRAY = new LoaderEntry[0];
/*     */ 
/*  79 */   private LoaderEntry[] loaders = EMPTY_LOADER_ARRAY;
/*     */ 
/* 130 */   private final Map<String, List<ClassLoader>> search = new Hashtable(10);
/*     */ 
/* 136 */   private final Map<ObjectName, ClassLoader> loadersWithNames = new Hashtable(10);
/*     */ 
/*     */   private synchronized boolean add(ObjectName paramObjectName, ClassLoader paramClassLoader)
/*     */   {
/*  87 */     ArrayList localArrayList = new ArrayList(Arrays.asList(this.loaders));
/*     */ 
/*  89 */     localArrayList.add(new LoaderEntry(paramObjectName, paramClassLoader));
/*  90 */     this.loaders = ((LoaderEntry[])localArrayList.toArray(EMPTY_LOADER_ARRAY));
/*  91 */     return true;
/*     */   }
/*     */ 
/*     */   private synchronized boolean remove(ObjectName paramObjectName, ClassLoader paramClassLoader)
/*     */   {
/* 107 */     int i = this.loaders.length;
/* 108 */     for (int j = 0; j < i; j++) {
/* 109 */       LoaderEntry localLoaderEntry = this.loaders[j];
/* 110 */       boolean bool = paramObjectName == null ? false : paramClassLoader == localLoaderEntry.loader ? true : paramObjectName.equals(localLoaderEntry.name);
/*     */ 
/* 114 */       if (bool) {
/* 115 */         LoaderEntry[] arrayOfLoaderEntry = new LoaderEntry[i - 1];
/* 116 */         System.arraycopy(this.loaders, 0, arrayOfLoaderEntry, 0, j);
/* 117 */         System.arraycopy(this.loaders, j + 1, arrayOfLoaderEntry, j, i - 1 - j);
/*     */ 
/* 119 */         this.loaders = arrayOfLoaderEntry;
/* 120 */         return true;
/*     */       }
/*     */     }
/* 123 */     return false;
/*     */   }
/*     */ 
/*     */   public final Class<?> loadClass(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/* 142 */     return loadClass(this.loaders, paramString, null, null);
/*     */   }
/*     */ 
/*     */   public final Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/* 149 */     if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
/* 150 */       JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClassWithout", paramString + " without " + paramClassLoader);
/*     */     }
/*     */ 
/* 157 */     if (paramClassLoader == null) {
/* 158 */       return loadClass(this.loaders, paramString, null, null);
/*     */     }
/*     */ 
/* 162 */     startValidSearch(paramClassLoader, paramString);
/*     */     try {
/* 164 */       return loadClass(this.loaders, paramString, paramClassLoader, null);
/*     */     } finally {
/* 166 */       stopValidSearch(paramClassLoader, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Class<?> loadClassBefore(ClassLoader paramClassLoader, String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/* 173 */     if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
/* 174 */       JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClassBefore", paramString + " before " + paramClassLoader);
/*     */     }
/*     */ 
/* 179 */     if (paramClassLoader == null) {
/* 180 */       return loadClass(this.loaders, paramString, null, null);
/*     */     }
/* 182 */     startValidSearch(paramClassLoader, paramString);
/*     */     try {
/* 184 */       return loadClass(this.loaders, paramString, null, paramClassLoader);
/*     */     } finally {
/* 186 */       stopValidSearch(paramClassLoader, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Class<?> loadClass(LoaderEntry[] paramArrayOfLoaderEntry, String paramString, ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
/*     */     throws ClassNotFoundException
/*     */   {
/* 196 */     ReflectUtil.checkPackageAccess(paramString);
/* 197 */     int i = paramArrayOfLoaderEntry.length;
/* 198 */     for (int j = 0; j < i; j++)
/*     */       try {
/* 200 */         ClassLoader localClassLoader = paramArrayOfLoaderEntry[j].loader;
/* 201 */         if (localClassLoader == null)
/* 202 */           return Class.forName(paramString, false, null);
/* 203 */         if (localClassLoader != paramClassLoader1)
/*     */         {
/* 205 */           if (localClassLoader == paramClassLoader2)
/*     */             break;
/* 207 */           if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
/* 208 */             JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClass", "Trying loader = " + localClassLoader);
/*     */           }
/*     */ 
/* 224 */           return Class.forName(paramString, false, localClassLoader);
/*     */         }
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException)
/*     */       {
/*     */       }
/* 230 */     throw new ClassNotFoundException(paramString);
/*     */   }
/*     */ 
/*     */   private synchronized void startValidSearch(ClassLoader paramClassLoader, String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/* 238 */     Object localObject = (List)this.search.get(paramString);
/* 239 */     if ((localObject != null) && (((List)localObject).contains(paramClassLoader))) {
/* 240 */       if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
/* 241 */         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "startValidSearch", "Already requested loader = " + paramClassLoader + " class = " + paramString);
/*     */       }
/*     */ 
/* 246 */       throw new ClassNotFoundException(paramString);
/*     */     }
/*     */ 
/* 251 */     if (localObject == null) {
/* 252 */       localObject = new ArrayList(1);
/* 253 */       this.search.put(paramString, localObject);
/*     */     }
/* 255 */     ((List)localObject).add(paramClassLoader);
/* 256 */     if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
/* 257 */       JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "startValidSearch", "loader = " + paramClassLoader + " class = " + paramString);
/*     */   }
/*     */ 
/*     */   private synchronized void stopValidSearch(ClassLoader paramClassLoader, String paramString)
/*     */   {
/* 269 */     List localList = (List)this.search.get(paramString);
/* 270 */     if (localList != null) {
/* 271 */       localList.remove(paramClassLoader);
/* 272 */       if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
/* 273 */         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "stopValidSearch", "loader = " + paramClassLoader + " class = " + paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void addClassLoader(ClassLoader paramClassLoader)
/*     */   {
/* 282 */     add(null, paramClassLoader);
/*     */   }
/*     */ 
/*     */   public final void removeClassLoader(ClassLoader paramClassLoader) {
/* 286 */     remove(null, paramClassLoader);
/*     */   }
/*     */ 
/*     */   public final synchronized void addClassLoader(ObjectName paramObjectName, ClassLoader paramClassLoader)
/*     */   {
/* 291 */     this.loadersWithNames.put(paramObjectName, paramClassLoader);
/* 292 */     if (!(paramClassLoader instanceof PrivateClassLoader))
/* 293 */       add(paramObjectName, paramClassLoader);
/*     */   }
/*     */ 
/*     */   public final synchronized void removeClassLoader(ObjectName paramObjectName) {
/* 297 */     ClassLoader localClassLoader = (ClassLoader)this.loadersWithNames.remove(paramObjectName);
/* 298 */     if (!(localClassLoader instanceof PrivateClassLoader))
/* 299 */       remove(paramObjectName, localClassLoader);
/*     */   }
/*     */ 
/*     */   public final ClassLoader getClassLoader(ObjectName paramObjectName) {
/* 303 */     return (ClassLoader)this.loadersWithNames.get(paramObjectName);
/*     */   }
/*     */ 
/*     */   private static class LoaderEntry
/*     */   {
/*     */     ObjectName name;
/*     */     ClassLoader loader;
/*     */ 
/*     */     LoaderEntry(ObjectName paramObjectName, ClassLoader paramClassLoader)
/*     */     {
/*  62 */       this.name = paramObjectName;
/*  63 */       this.loader = paramClassLoader;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.ClassLoaderRepositorySupport
 * JD-Core Version:    0.6.2
 */