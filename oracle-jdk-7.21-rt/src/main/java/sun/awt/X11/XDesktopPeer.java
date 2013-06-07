/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Desktop.Action;
/*     */ import java.awt.peer.DesktopPeer;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ 
/*     */ public class XDesktopPeer
/*     */   implements DesktopPeer
/*     */ {
/*  46 */   private static boolean nativeLibraryLoaded = false;
/*  47 */   private static boolean initExecuted = false;
/*     */ 
/*     */   private static void initWithLock() {
/*  50 */     XToolkit.awtLock();
/*     */     try {
/*  52 */       if (!initExecuted)
/*  53 */         nativeLibraryLoaded = init();
/*     */     }
/*     */     finally {
/*  56 */       initExecuted = true;
/*  57 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   XDesktopPeer()
/*     */   {
/*  63 */     initWithLock();
/*     */   }
/*     */ 
/*     */   static boolean isDesktopSupported() {
/*  67 */     initWithLock();
/*  68 */     return nativeLibraryLoaded;
/*     */   }
/*     */ 
/*     */   public boolean isSupported(Desktop.Action paramAction) {
/*  72 */     return (paramAction != Desktop.Action.PRINT) && (paramAction != Desktop.Action.EDIT);
/*     */   }
/*     */ 
/*     */   public void open(File paramFile) throws IOException {
/*     */     try {
/*  77 */       launch(paramFile.toURI());
/*     */     } catch (MalformedURLException localMalformedURLException) {
/*  79 */       throw new IOException(paramFile.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void edit(File paramFile) throws IOException {
/*  84 */     throw new UnsupportedOperationException("The current platform doesn't support the EDIT action.");
/*     */   }
/*     */ 
/*     */   public void print(File paramFile) throws IOException
/*     */   {
/*  89 */     throw new UnsupportedOperationException("The current platform doesn't support the PRINT action.");
/*     */   }
/*     */ 
/*     */   public void mail(URI paramURI) throws IOException
/*     */   {
/*  94 */     launch(paramURI);
/*     */   }
/*     */ 
/*     */   public void browse(URI paramURI) throws IOException {
/*  98 */     launch(paramURI);
/*     */   }
/*     */ 
/*     */   private void launch(URI paramURI) throws IOException {
/* 102 */     byte[] arrayOfByte = (paramURI.toString() + '\000').getBytes();
/* 103 */     boolean bool = false;
/* 104 */     XToolkit.awtLock();
/*     */     try {
/* 106 */       if (!nativeLibraryLoaded) {
/* 107 */         throw new IOException("Failed to load native libraries.");
/*     */       }
/* 109 */       bool = gnome_url_show(arrayOfByte);
/*     */     } finally {
/* 111 */       XToolkit.awtUnlock();
/*     */     }
/* 113 */     if (!bool)
/* 114 */       throw new IOException("Failed to show URI:" + paramURI);
/*     */   }
/*     */ 
/*     */   private native boolean gnome_url_show(byte[] paramArrayOfByte);
/*     */ 
/*     */   private static native boolean init();
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDesktopPeer
 * JD-Core Version:    0.6.2
 */