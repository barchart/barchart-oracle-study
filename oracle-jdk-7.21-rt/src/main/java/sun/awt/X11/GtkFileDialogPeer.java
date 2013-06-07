/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.peer.FileDialogPeer;
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.FileDialogAccessor;
/*     */ 
/*     */ class GtkFileDialogPeer extends XDialogPeer
/*     */   implements FileDialogPeer
/*     */ {
/*     */   private FileDialog fd;
/*  44 */   private volatile long widget = 0L;
/*     */ 
/*     */   public GtkFileDialogPeer(FileDialog paramFileDialog) {
/*  47 */     super(paramFileDialog);
/*  48 */     this.fd = paramFileDialog;
/*     */   }
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   private native void run(String paramString1, int paramInt1, String paramString2, String paramString3, FilenameFilter paramFilenameFilter, boolean paramBoolean, int paramInt2, int paramInt3);
/*     */ 
/*     */   private native void quit();
/*     */ 
/*     */   public native void toFront();
/*     */ 
/*     */   public native void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
/*     */ 
/*     */   private void setFileInternal(String paramString, String[] paramArrayOfString)
/*     */   {
/*  70 */     AWTAccessor.FileDialogAccessor localFileDialogAccessor = AWTAccessor.getFileDialogAccessor();
/*     */ 
/*  73 */     if (paramArrayOfString == null) {
/*  74 */       localFileDialogAccessor.setDirectory(this.fd, null);
/*  75 */       localFileDialogAccessor.setFile(this.fd, null);
/*  76 */       localFileDialogAccessor.setFiles(this.fd, null);
/*     */     }
/*     */     else {
/*  79 */       localFileDialogAccessor.setDirectory(this.fd, paramString + (paramString.endsWith(File.separator) ? "" : File.separator));
/*     */ 
/*  82 */       localFileDialogAccessor.setFile(this.fd, paramArrayOfString[0]);
/*     */ 
/*  84 */       int i = paramArrayOfString != null ? paramArrayOfString.length : 0;
/*  85 */       File[] arrayOfFile = new File[i];
/*  86 */       for (int j = 0; j < i; j++) {
/*  87 */         arrayOfFile[j] = new File(paramString, paramArrayOfString[j]);
/*     */       }
/*  89 */       localFileDialogAccessor.setFiles(this.fd, arrayOfFile);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean filenameFilterCallback(String paramString)
/*     */   {
/*  97 */     if (this.fd.getFilenameFilter() == null)
/*     */     {
/*  99 */       return true;
/*     */     }
/*     */ 
/* 102 */     File localFile = new File(paramString);
/* 103 */     return this.fd.getFilenameFilter().accept(new File(localFile.getParent()), localFile.getName());
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean paramBoolean)
/*     */   {
/* 109 */     XToolkit.awtLock();
/*     */     try {
/* 111 */       if (paramBoolean) {
/* 112 */         Thread local1 = new Thread() {
/*     */           public void run() {
/* 114 */             GtkFileDialogPeer.this.showNativeDialog();
/* 115 */             GtkFileDialogPeer.this.fd.setVisible(false);
/*     */           }
/*     */         };
/* 118 */         local1.start();
/*     */       } else {
/* 120 */         quit();
/* 121 */         this.fd.setVisible(false);
/*     */       }
/*     */     } finally {
/* 124 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 130 */     quit();
/* 131 */     super.dispose();
/*     */   }
/*     */ 
/*     */   public void setDirectory(String paramString)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setFile(String paramString)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setFilenameFilter(FilenameFilter paramFilenameFilter)
/*     */   {
/*     */   }
/*     */ 
/*     */   private void showNativeDialog()
/*     */   {
/* 153 */     String str1 = this.fd.getDirectory();
/*     */ 
/* 155 */     String str2 = this.fd.getFile();
/* 156 */     if (str2 != null) {
/* 157 */       File localFile = new File(str2);
/* 158 */       if ((this.fd.getMode() == 0) && (str1 != null) && (localFile.getParent() == null))
/*     */       {
/* 162 */         str2 = str1 + (str1.endsWith(File.separator) ? "" : File.separator) + str2;
/*     */       }
/*     */ 
/* 165 */       if ((this.fd.getMode() == 1) && (localFile.getParent() != null))
/*     */       {
/* 167 */         str2 = localFile.getName();
/*     */ 
/* 169 */         str1 = localFile.getParent();
/*     */       }
/*     */     }
/* 172 */     run(this.fd.getTitle(), this.fd.getMode(), str1, str2, this.fd.getFilenameFilter(), this.fd.isMultipleMode(), this.fd.getX(), this.fd.getY());
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  53 */     initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.GtkFileDialogPeer
 * JD-Core Version:    0.6.2
 */