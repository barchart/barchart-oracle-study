/*     */ package java.beans;
/*     */ 
/*     */ import java.applet.Applet;
/*     */ import java.applet.AppletContext;
/*     */ import java.applet.AppletStub;
/*     */ import java.net.URL;
/*     */ 
/*     */ class BeansAppletStub
/*     */   implements AppletStub
/*     */ {
/*     */   transient boolean active;
/*     */   transient Applet target;
/*     */   transient AppletContext context;
/*     */   transient URL codeBase;
/*     */   transient URL docBase;
/*     */ 
/*     */   BeansAppletStub(Applet paramApplet, AppletContext paramAppletContext, URL paramURL1, URL paramURL2)
/*     */   {
/* 599 */     this.target = paramApplet;
/* 600 */     this.context = paramAppletContext;
/* 601 */     this.codeBase = paramURL1;
/* 602 */     this.docBase = paramURL2;
/*     */   }
/*     */ 
/*     */   public boolean isActive() {
/* 606 */     return this.active;
/*     */   }
/*     */ 
/*     */   public URL getDocumentBase()
/*     */   {
/* 611 */     return this.docBase;
/*     */   }
/*     */ 
/*     */   public URL getCodeBase()
/*     */   {
/* 616 */     return this.codeBase;
/*     */   }
/*     */ 
/*     */   public String getParameter(String paramString) {
/* 620 */     return null;
/*     */   }
/*     */ 
/*     */   public AppletContext getAppletContext() {
/* 624 */     return this.context;
/*     */   }
/*     */ 
/*     */   public void appletResize(int paramInt1, int paramInt2)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.BeansAppletStub
 * JD-Core Version:    0.6.2
 */