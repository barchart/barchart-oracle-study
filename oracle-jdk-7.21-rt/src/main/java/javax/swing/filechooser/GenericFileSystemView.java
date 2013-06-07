/*     */ package javax.swing.filechooser;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import javax.swing.UIManager;
/*     */ 
/*     */ class GenericFileSystemView extends FileSystemView
/*     */ {
/* 795 */   private static final String newFolderString = UIManager.getString("FileChooser.other.newFolder");
/*     */ 
/*     */   public File createNewFolder(File paramFile)
/*     */     throws IOException
/*     */   {
/* 802 */     if (paramFile == null) {
/* 803 */       throw new IOException("Containing directory is null:");
/*     */     }
/*     */ 
/* 806 */     File localFile = createFileObject(paramFile, newFolderString);
/*     */ 
/* 808 */     if (localFile.exists()) {
/* 809 */       throw new IOException("Directory already exists:" + localFile.getAbsolutePath());
/*     */     }
/* 811 */     localFile.mkdirs();
/*     */ 
/* 814 */     return localFile;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.filechooser.GenericFileSystemView
 * JD-Core Version:    0.6.2
 */