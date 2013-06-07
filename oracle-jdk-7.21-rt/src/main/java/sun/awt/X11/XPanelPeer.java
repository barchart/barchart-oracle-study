/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.awt.peer.LightweightPeer;
/*     */ import java.awt.peer.PanelPeer;
/*     */ import sun.awt.SunGraphicsCallback.PrintHeavyweightComponentsCallback;
/*     */ 
/*     */ public class XPanelPeer extends XCanvasPeer
/*     */   implements PanelPeer
/*     */ {
/*  34 */   XEmbeddingContainer embedder = null;
/*     */ 
/*     */   public void xembed(long paramLong)
/*     */   {
/*  39 */     if (this.embedder != null)
/*  40 */       this.embedder.add(paramLong); 
/*     */   }
/*     */ 
/*     */   XPanelPeer() {
/*     */   }
/*     */ 
/*  46 */   XPanelPeer(XCreateWindowParams paramXCreateWindowParams) { super(paramXCreateWindowParams); }
/*     */ 
/*     */   XPanelPeer(Component paramComponent)
/*     */   {
/*  50 */     super(paramComponent);
/*     */   }
/*     */ 
/*     */   void postInit(XCreateWindowParams paramXCreateWindowParams) {
/*  54 */     super.postInit(paramXCreateWindowParams);
/*  55 */     if (this.embedder != null)
/*  56 */       this.embedder.install(this);
/*     */   }
/*     */ 
/*     */   public Insets getInsets()
/*     */   {
/*  61 */     return new Insets(0, 0, 0, 0);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics) {
/*  65 */     super.paint(paramGraphics);
/*     */   }
/*     */ 
/*     */   public void print(Graphics paramGraphics)
/*     */   {
/*  72 */     super.print(paramGraphics);
/*  73 */     SunGraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().runComponents(((Container)this.target).getComponents(), paramGraphics, 3);
/*     */   }
/*     */ 
/*     */   public void setBackground(Color paramColor)
/*     */   {
/*  84 */     Container localContainer = (Container)this.target;
/*  85 */     synchronized (this.target.getTreeLock()) {
/*  86 */       int j = localContainer.getComponentCount();
/*  87 */       for (int i = 0; i < j; i++) {
/*  88 */         Component localComponent = localContainer.getComponent(i);
/*  89 */         ComponentPeer localComponentPeer = localComponent.getPeer();
/*  90 */         if (localComponentPeer != null) {
/*  91 */           Color localColor = localComponent.getBackground();
/*  92 */           if ((localColor == null) || (localColor.equals(paramColor))) {
/*  93 */             localComponentPeer.setBackground(paramColor);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  98 */     super.setBackground(paramColor);
/*     */   }
/*     */ 
/*     */   public void setForeground(Color paramColor) {
/* 102 */     setForegroundForHierarchy((Container)this.target, paramColor);
/*     */   }
/*     */ 
/*     */   private void setForegroundForHierarchy(Container paramContainer, Color paramColor) {
/* 106 */     synchronized (this.target.getTreeLock()) {
/* 107 */       int i = paramContainer.getComponentCount();
/* 108 */       for (int j = 0; j < i; j++) {
/* 109 */         Component localComponent = paramContainer.getComponent(j);
/* 110 */         Color localColor = localComponent.getForeground();
/* 111 */         if ((localColor == null) || (localColor.equals(paramColor))) {
/* 112 */           ComponentPeer localComponentPeer = localComponent.getPeer();
/* 113 */           if (localComponentPeer != null) {
/* 114 */             localComponentPeer.setForeground(paramColor);
/*     */           }
/* 116 */           if (((localComponentPeer instanceof LightweightPeer)) && ((localComponent instanceof Container)))
/*     */           {
/* 119 */             setForegroundForHierarchy((Container)localComponent, paramColor);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Insets insets()
/*     */   {
/* 130 */     return getInsets();
/*     */   }
/*     */ 
/*     */   public void dispose() {
/* 134 */     if (this.embedder != null) {
/* 135 */       this.embedder.deinstall();
/*     */     }
/* 137 */     super.dispose();
/*     */   }
/*     */ 
/*     */   protected boolean shouldFocusOnClick()
/*     */   {
/* 143 */     return ((Container)this.target).getComponentCount() == 0;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XPanelPeer
 * JD-Core Version:    0.6.2
 */