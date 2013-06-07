/*    */ package sun.awt.image;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ 
/*    */ public class FileImageSource extends InputStreamImageSource
/*    */ {
/*    */   String imagefile;
/*    */ 
/*    */   public FileImageSource(String paramString)
/*    */   {
/* 37 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 38 */     if (localSecurityManager != null) {
/* 39 */       localSecurityManager.checkRead(paramString);
/*    */     }
/* 41 */     this.imagefile = paramString;
/*    */   }
/*    */ 
/*    */   final boolean checkSecurity(Object paramObject, boolean paramBoolean)
/*    */   {
/* 47 */     return true;
/*    */   }
/*    */ 
/*    */   protected ImageDecoder getDecoder() {
/*    */     BufferedInputStream localBufferedInputStream;
/*    */     try {
/* 53 */       localBufferedInputStream = new BufferedInputStream(new FileInputStream(this.imagefile));
/*    */     } catch (FileNotFoundException localFileNotFoundException) {
/* 55 */       return null;
/*    */     }
/*    */ 
/* 73 */     return getDecoder(localBufferedInputStream);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.image.FileImageSource
 * JD-Core Version:    0.6.2
 */