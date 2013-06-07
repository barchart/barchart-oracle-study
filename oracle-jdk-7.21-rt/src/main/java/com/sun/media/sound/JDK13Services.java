/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import javax.sound.midi.Receiver;
/*     */ import javax.sound.midi.Sequencer;
/*     */ import javax.sound.midi.Synthesizer;
/*     */ import javax.sound.midi.Transmitter;
/*     */ import javax.sound.sampled.Clip;
/*     */ import javax.sound.sampled.Port;
/*     */ import javax.sound.sampled.SourceDataLine;
/*     */ import javax.sound.sampled.TargetDataLine;
/*     */ 
/*     */ public class JDK13Services
/*     */ {
/*     */   private static final long DEFAULT_CACHING_PERIOD = 60000L;
/*     */   private static final String PROPERTIES_FILENAME = "sound.properties";
/*  83 */   private static Map providersCacheMap = new HashMap();
/*     */ 
/*  89 */   private static long cachingPeriod = 60000L;
/*     */   private static Properties properties;
/*     */ 
/*     */   public static void setCachingPeriod(int paramInt)
/*     */   {
/* 107 */     cachingPeriod = paramInt * 1000L;
/*     */   }
/*     */ 
/*     */   public static synchronized List getProviders(Class paramClass)
/*     */   {
/* 127 */     ProviderCache localProviderCache = (ProviderCache)providersCacheMap.get(paramClass);
/* 128 */     if (localProviderCache == null) {
/* 129 */       localProviderCache = new ProviderCache(null);
/* 130 */       providersCacheMap.put(paramClass, localProviderCache);
/*     */     }
/* 132 */     if ((localProviderCache.providers == null) || (System.currentTimeMillis() > localProviderCache.lastUpdate + cachingPeriod))
/*     */     {
/* 134 */       localProviderCache.providers = Collections.unmodifiableList(JSSecurityManager.getProviders(paramClass));
/* 135 */       localProviderCache.lastUpdate = System.currentTimeMillis();
/*     */     }
/* 137 */     return localProviderCache.providers;
/*     */   }
/*     */ 
/*     */   public static synchronized String getDefaultProviderClassName(Class paramClass)
/*     */   {
/* 151 */     Object localObject = null;
/* 152 */     String str = getDefaultProvider(paramClass);
/* 153 */     if (str != null) {
/* 154 */       int i = str.indexOf('#');
/* 155 */       if (i != 0)
/*     */       {
/* 157 */         if (i > 0)
/* 158 */           localObject = str.substring(0, i);
/*     */         else
/* 160 */           localObject = str;
/*     */       }
/*     */     }
/* 163 */     return localObject;
/*     */   }
/*     */ 
/*     */   public static synchronized String getDefaultInstanceName(Class paramClass)
/*     */   {
/* 177 */     String str1 = null;
/* 178 */     String str2 = getDefaultProvider(paramClass);
/* 179 */     if (str2 != null) {
/* 180 */       int i = str2.indexOf('#');
/* 181 */       if ((i >= 0) && (i < str2.length() - 1)) {
/* 182 */         str1 = str2.substring(i + 1);
/*     */       }
/*     */     }
/* 185 */     return str1;
/*     */   }
/*     */ 
/*     */   private static synchronized String getDefaultProvider(Class paramClass)
/*     */   {
/* 198 */     if ((!SourceDataLine.class.equals(paramClass)) && (!TargetDataLine.class.equals(paramClass)) && (!Clip.class.equals(paramClass)) && (!Port.class.equals(paramClass)) && (!Receiver.class.equals(paramClass)) && (!Transmitter.class.equals(paramClass)) && (!Synthesizer.class.equals(paramClass)) && (!Sequencer.class.equals(paramClass)))
/*     */     {
/* 206 */       return null;
/*     */     }
/*     */ 
/* 209 */     String str2 = paramClass.getName();
/* 210 */     String str1 = JSSecurityManager.getProperty(str2);
/* 211 */     if (str1 == null) {
/* 212 */       str1 = getProperties().getProperty(str2);
/*     */     }
/* 214 */     if ("".equals(str1)) {
/* 215 */       str1 = null;
/*     */     }
/* 217 */     return str1;
/*     */   }
/*     */ 
/*     */   private static synchronized Properties getProperties()
/*     */   {
/* 226 */     if (properties == null) {
/* 227 */       properties = new Properties();
/* 228 */       JSSecurityManager.loadProperties(properties, "sound.properties");
/*     */     }
/* 230 */     return properties;
/*     */   }
/*     */ 
/*     */   private static class ProviderCache
/*     */   {
/*     */     public long lastUpdate;
/*     */     public List providers;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.JDK13Services
 * JD-Core Version:    0.6.2
 */