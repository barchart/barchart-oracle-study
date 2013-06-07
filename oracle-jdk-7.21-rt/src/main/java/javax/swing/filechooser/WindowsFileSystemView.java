/*     */ package javax.swing.filechooser;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.text.MessageFormat;
/*     */ import javax.swing.UIManager;
/*     */ import sun.awt.shell.ShellFolder;
/*     */ 
/*     */ class WindowsFileSystemView extends FileSystemView
/*     */ {
/* 672 */   private static final String newFolderString = UIManager.getString("FileChooser.win32.newFolder");
/*     */ 
/* 674 */   private static final String newFolderNextString = UIManager.getString("FileChooser.win32.newFolder.subsequent");
/*     */ 
/*     */   public Boolean isTraversable(File paramFile)
/*     */   {
/* 678 */     return Boolean.valueOf((isFileSystemRoot(paramFile)) || (isComputerNode(paramFile)) || (paramFile.isDirectory()));
/*     */   }
/*     */ 
/*     */   public File getChild(File paramFile, String paramString) {
/* 682 */     if ((paramString.startsWith("\\")) && (!paramString.startsWith("\\\\")) && (isFileSystem(paramFile)))
/*     */     {
/* 687 */       String str = paramFile.getAbsolutePath();
/* 688 */       if ((str.length() >= 2) && (str.charAt(1) == ':') && (Character.isLetter(str.charAt(0))))
/*     */       {
/* 692 */         return createFileObject(str.substring(0, 2) + paramString);
/*     */       }
/*     */     }
/* 695 */     return super.getChild(paramFile, paramString);
/*     */   }
/*     */ 
/*     */   public String getSystemTypeDescription(File paramFile)
/*     */   {
/* 706 */     if (paramFile == null) {
/* 707 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 711 */       return getShellFolder(paramFile).getFolderType(); } catch (FileNotFoundException localFileNotFoundException) {
/*     */     }
/* 713 */     return null;
/*     */   }
/*     */ 
/*     */   public File getHomeDirectory()
/*     */   {
/* 721 */     return getRoots()[0];
/*     */   }
/*     */ 
/*     */   public File createNewFolder(File paramFile)
/*     */     throws IOException
/*     */   {
/* 728 */     if (paramFile == null) {
/* 729 */       throw new IOException("Containing directory is null:");
/*     */     }
/*     */ 
/* 732 */     File localFile = createFileObject(paramFile, newFolderString);
/* 733 */     int i = 2;
/* 734 */     while ((localFile.exists()) && (i < 100)) {
/* 735 */       localFile = createFileObject(paramFile, MessageFormat.format(newFolderNextString, new Object[] { new Integer(i) }));
/*     */ 
/* 737 */       i++;
/*     */     }
/*     */ 
/* 740 */     if (localFile.exists()) {
/* 741 */       throw new IOException("Directory already exists:" + localFile.getAbsolutePath());
/*     */     }
/* 743 */     localFile.mkdirs();
/*     */ 
/* 746 */     return localFile;
/*     */   }
/*     */ 
/*     */   public boolean isDrive(File paramFile) {
/* 750 */     return isFileSystemRoot(paramFile);
/*     */   }
/*     */ 
/*     */   public boolean isFloppyDrive(final File paramFile) {
/* 754 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public String run() {
/* 756 */         return paramFile.getAbsolutePath();
/*     */       }
/*     */     });
/* 760 */     return (str != null) && ((str.equals("A:\\")) || (str.equals("B:\\")));
/*     */   }
/*     */ 
/*     */   public File createFileObject(String paramString)
/*     */   {
/* 768 */     if ((paramString.length() >= 2) && (paramString.charAt(1) == ':') && (Character.isLetter(paramString.charAt(0)))) {
/* 769 */       if (paramString.length() == 2)
/* 770 */         paramString = paramString + "\\";
/* 771 */       else if (paramString.charAt(2) != '\\') {
/* 772 */         paramString = paramString.substring(0, 2) + "\\" + paramString.substring(2);
/*     */       }
/*     */     }
/* 775 */     return super.createFileObject(paramString);
/*     */   }
/*     */ 
/*     */   protected File createFileSystemRoot(File paramFile)
/*     */   {
/* 781 */     return new FileSystemView.FileSystemRoot(paramFile) {
/*     */       public boolean exists() {
/* 783 */         return true;
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.filechooser.WindowsFileSystemView
 * JD-Core Version:    0.6.2
 */