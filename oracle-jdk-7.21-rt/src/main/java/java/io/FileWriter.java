/*     */ package java.io;
/*     */ 
/*     */ public class FileWriter extends OutputStreamWriter
/*     */ {
/*     */   public FileWriter(String paramString)
/*     */     throws IOException
/*     */   {
/*  63 */     super(new FileOutputStream(paramString));
/*     */   }
/*     */ 
/*     */   public FileWriter(String paramString, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  78 */     super(new FileOutputStream(paramString, paramBoolean));
/*     */   }
/*     */ 
/*     */   public FileWriter(File paramFile)
/*     */     throws IOException
/*     */   {
/*  90 */     super(new FileOutputStream(paramFile));
/*     */   }
/*     */ 
/*     */   public FileWriter(File paramFile, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 107 */     super(new FileOutputStream(paramFile, paramBoolean));
/*     */   }
/*     */ 
/*     */   public FileWriter(FileDescriptor paramFileDescriptor)
/*     */   {
/* 116 */     super(new FileOutputStream(paramFileDescriptor));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.FileWriter
 * JD-Core Version:    0.6.2
 */