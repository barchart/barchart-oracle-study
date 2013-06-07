/*     */ package java.beans;
/*     */ 
/*     */ import java.applet.Applet;
/*     */ import java.applet.AppletContext;
/*     */ import java.applet.AudioClip;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.ImageProducer;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ 
/*     */ class BeansAppletContext
/*     */   implements AppletContext
/*     */ {
/*     */   Applet target;
/* 507 */   Hashtable imageCache = new Hashtable();
/*     */ 
/*     */   BeansAppletContext(Applet paramApplet) {
/* 510 */     this.target = paramApplet;
/*     */   }
/*     */ 
/*     */   public AudioClip getAudioClip(URL paramURL)
/*     */   {
/*     */     try
/*     */     {
/* 518 */       return (AudioClip)paramURL.getContent(); } catch (Exception localException) {
/*     */     }
/* 520 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized Image getImage(URL paramURL)
/*     */   {
/* 525 */     Object localObject = this.imageCache.get(paramURL);
/* 526 */     if (localObject != null)
/* 527 */       return (Image)localObject;
/*     */     try
/*     */     {
/* 530 */       localObject = paramURL.getContent();
/* 531 */       if (localObject == null) {
/* 532 */         return null;
/*     */       }
/* 534 */       if ((localObject instanceof Image)) {
/* 535 */         this.imageCache.put(paramURL, localObject);
/* 536 */         return (Image)localObject;
/*     */       }
/*     */ 
/* 539 */       Image localImage = this.target.createImage((ImageProducer)localObject);
/* 540 */       this.imageCache.put(paramURL, localImage);
/* 541 */       return localImage;
/*     */     } catch (Exception localException) {
/*     */     }
/* 544 */     return null;
/*     */   }
/*     */ 
/*     */   public Applet getApplet(String paramString)
/*     */   {
/* 549 */     return null;
/*     */   }
/*     */ 
/*     */   public Enumeration getApplets() {
/* 553 */     Vector localVector = new Vector();
/* 554 */     localVector.addElement(this.target);
/* 555 */     return localVector.elements();
/*     */   }
/*     */ 
/*     */   public void showDocument(URL paramURL)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void showDocument(URL paramURL, String paramString)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void showStatus(String paramString)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setStream(String paramString, InputStream paramInputStream) throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public InputStream getStream(String paramString)
/*     */   {
/* 576 */     return null;
/*     */   }
/*     */ 
/*     */   public Iterator getStreamKeys()
/*     */   {
/* 581 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.BeansAppletContext
 * JD-Core Version:    0.6.2
 */