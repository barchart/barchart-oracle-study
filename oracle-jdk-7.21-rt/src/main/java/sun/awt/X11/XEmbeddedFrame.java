/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTKeyStroke;
/*     */ import java.awt.Toolkit;
/*     */ import java.util.logging.Logger;
/*     */ import sun.awt.EmbeddedFrame;
/*     */ 
/*     */ public class XEmbeddedFrame extends EmbeddedFrame
/*     */ {
/*  35 */   private static final Logger log = Logger.getLogger(XEmbeddedFrame.class.getName());
/*     */   long handle;
/*     */ 
/*     */   public XEmbeddedFrame()
/*     */   {
/*     */   }
/*     */ 
/*     */   public XEmbeddedFrame(long paramLong)
/*     */   {
/*  43 */     this(paramLong, false);
/*     */   }
/*     */ 
/*     */   public XEmbeddedFrame(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/*  48 */     super(paramLong, paramBoolean1);
/*     */ 
/*  50 */     if (paramBoolean2) {
/*  51 */       XTrayIconPeer.suppressWarningString(this);
/*     */     }
/*     */ 
/*  54 */     this.handle = paramLong;
/*  55 */     if (paramLong != 0L) {
/*  56 */       addNotify();
/*  57 */       if (!paramBoolean2)
/*  58 */         show();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addNotify()
/*     */   {
/*  65 */     if (getPeer() == null) {
/*  66 */       XToolkit localXToolkit = (XToolkit)Toolkit.getDefaultToolkit();
/*  67 */       setPeer(localXToolkit.createEmbeddedFrame(this));
/*     */     }
/*  69 */     super.addNotify();
/*     */   }
/*     */ 
/*     */   public XEmbeddedFrame(long paramLong, boolean paramBoolean) {
/*  73 */     this(paramLong, paramBoolean, false);
/*     */   }
/*     */ 
/*     */   public boolean traverseIn(boolean paramBoolean)
/*     */   {
/*  80 */     XEmbeddedFramePeer localXEmbeddedFramePeer = (XEmbeddedFramePeer)getPeer();
/*  81 */     if (localXEmbeddedFramePeer != null) {
/*  82 */       if ((localXEmbeddedFramePeer.supportsXEmbed()) && (localXEmbeddedFramePeer.isXEmbedActive()))
/*  83 */         log.fine("The method shouldn't be called when XEmbed is active!");
/*     */       else {
/*  85 */         return super.traverseIn(paramBoolean);
/*     */       }
/*     */     }
/*  88 */     return false;
/*     */   }
/*     */ 
/*     */   protected boolean traverseOut(boolean paramBoolean) {
/*  92 */     XEmbeddedFramePeer localXEmbeddedFramePeer = (XEmbeddedFramePeer)getPeer();
/*  93 */     if (paramBoolean == true) {
/*  94 */       localXEmbeddedFramePeer.traverseOutForward();
/*     */     }
/*     */     else {
/*  97 */       localXEmbeddedFramePeer.traverseOutBackward();
/*     */     }
/*  99 */     return true;
/*     */   }
/*     */ 
/*     */   public void synthesizeWindowActivation(boolean paramBoolean)
/*     */   {
/* 106 */     XEmbeddedFramePeer localXEmbeddedFramePeer = (XEmbeddedFramePeer)getPeer();
/* 107 */     if (localXEmbeddedFramePeer != null)
/* 108 */       if ((localXEmbeddedFramePeer.supportsXEmbed()) && (localXEmbeddedFramePeer.isXEmbedActive()))
/* 109 */         log.fine("The method shouldn't be called when XEmbed is active!");
/*     */       else
/* 111 */         localXEmbeddedFramePeer.synthesizeFocusInOut(paramBoolean);
/*     */   }
/*     */ 
/*     */   public void registerAccelerator(AWTKeyStroke paramAWTKeyStroke)
/*     */   {
/* 117 */     XEmbeddedFramePeer localXEmbeddedFramePeer = (XEmbeddedFramePeer)getPeer();
/* 118 */     if (localXEmbeddedFramePeer != null)
/* 119 */       localXEmbeddedFramePeer.registerAccelerator(paramAWTKeyStroke);
/*     */   }
/*     */ 
/*     */   public void unregisterAccelerator(AWTKeyStroke paramAWTKeyStroke) {
/* 123 */     XEmbeddedFramePeer localXEmbeddedFramePeer = (XEmbeddedFramePeer)getPeer();
/* 124 */     if (localXEmbeddedFramePeer != null)
/* 125 */       localXEmbeddedFramePeer.unregisterAccelerator(paramAWTKeyStroke);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XEmbeddedFrame
 * JD-Core Version:    0.6.2
 */