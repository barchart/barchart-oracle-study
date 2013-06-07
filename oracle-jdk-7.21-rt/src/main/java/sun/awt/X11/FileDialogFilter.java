/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ 
/*     */ class FileDialogFilter
/*     */   implements FilenameFilter
/*     */ {
/*     */   String filter;
/*     */ 
/*     */   public FileDialogFilter(String paramString)
/*     */   {
/* 900 */     this.filter = paramString;
/*     */   }
/*     */ 
/*     */   public boolean accept(File paramFile, String paramString)
/*     */   {
/* 908 */     File localFile = new File(paramFile, paramString);
/*     */ 
/* 910 */     if (localFile.isDirectory()) {
/* 911 */       return true;
/*     */     }
/* 913 */     return matches(paramString, this.filter);
/*     */   }
/*     */ 
/*     */   private boolean matches(String paramString1, String paramString2)
/*     */   {
/* 921 */     String str = convert(paramString2);
/* 922 */     return paramString1.matches(str);
/*     */   }
/*     */ 
/*     */   private String convert(String paramString)
/*     */   {
/* 929 */     String str = "^" + paramString + "$";
/* 930 */     str = str.replaceAll("\\.", "\\\\.");
/* 931 */     str = str.replaceAll("\\?", ".");
/* 932 */     str = str.replaceAll("\\*", ".*");
/* 933 */     return str;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.FileDialogFilter
 * JD-Core Version:    0.6.2
 */