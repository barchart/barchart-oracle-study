/*     */ package javax.activation;
/*     */ 
/*     */ import java.io.File;
/*     */ 
/*     */ public abstract class FileTypeMap
/*     */ {
/*  50 */   private static FileTypeMap defaultMap = null;
/*     */ 
/*     */   public abstract String getContentType(File paramFile);
/*     */ 
/*     */   public abstract String getContentType(String paramString);
/*     */ 
/*     */   public static void setDefaultFileTypeMap(FileTypeMap map)
/*     */   {
/*  86 */     SecurityManager security = System.getSecurityManager();
/*  87 */     if (security != null)
/*     */       try
/*     */       {
/*  90 */         security.checkSetFactory();
/*     */       }
/*     */       catch (SecurityException ex)
/*     */       {
/*  95 */         if (FileTypeMap.class.getClassLoader() != map.getClass().getClassLoader())
/*     */         {
/*  97 */           throw ex;
/*     */         }
/*     */       }
/* 100 */     defaultMap = map;
/*     */   }
/*     */ 
/*     */   public static FileTypeMap getDefaultFileTypeMap()
/*     */   {
/* 114 */     if (defaultMap == null)
/* 115 */       defaultMap = new MimetypesFileTypeMap();
/* 116 */     return defaultMap;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activation.FileTypeMap
 * JD-Core Version:    0.6.2
 */