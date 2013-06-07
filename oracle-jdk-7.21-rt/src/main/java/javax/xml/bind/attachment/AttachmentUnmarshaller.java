/*     */ package javax.xml.bind.attachment;
/*     */ 
/*     */ import javax.activation.DataHandler;
/*     */ 
/*     */ public abstract class AttachmentUnmarshaller
/*     */ {
/*     */   public abstract DataHandler getAttachmentAsDataHandler(String paramString);
/*     */ 
/*     */   public abstract byte[] getAttachmentAsByteArray(String paramString);
/*     */ 
/*     */   public boolean isXOPPackage()
/*     */   {
/* 138 */     return false;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.bind.attachment.AttachmentUnmarshaller
 * JD-Core Version:    0.6.2
 */