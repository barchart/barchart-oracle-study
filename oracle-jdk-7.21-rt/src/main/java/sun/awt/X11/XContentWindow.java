/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.ComponentAccessor;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public final class XContentWindow extends XWindow
/*     */ {
/*  46 */   private static PlatformLogger insLog = PlatformLogger.getLogger("sun.awt.X11.insets.XContentWindow");
/*     */   private final XDecoratedPeer parentFrame;
/*  68 */   private final List<SavedExposeEvent> iconifiedExposeEvents = new ArrayList();
/*     */ 
/*     */   static XContentWindow createContent(XDecoratedPeer paramXDecoratedPeer)
/*     */   {
/*  49 */     WindowDimensions localWindowDimensions = paramXDecoratedPeer.getDimensions();
/*  50 */     Rectangle localRectangle = localWindowDimensions.getBounds();
/*     */ 
/*  52 */     Insets localInsets = localWindowDimensions.getInsets();
/*  53 */     if (localInsets != null) {
/*  54 */       localRectangle.x = (-localInsets.left);
/*  55 */       localRectangle.y = (-localInsets.top);
/*     */     } else {
/*  57 */       localRectangle.x = 0;
/*  58 */       localRectangle.y = 0;
/*     */     }
/*  60 */     XContentWindow localXContentWindow = new XContentWindow(paramXDecoratedPeer, localRectangle);
/*  61 */     localXContentWindow.xSetVisible(true);
/*  62 */     return localXContentWindow;
/*     */   }
/*     */ 
/*     */   private XContentWindow(XDecoratedPeer paramXDecoratedPeer, Rectangle paramRectangle)
/*     */   {
/*  72 */     super((Component)paramXDecoratedPeer.getTarget(), paramXDecoratedPeer.getShell(), paramRectangle);
/*  73 */     this.parentFrame = paramXDecoratedPeer;
/*     */   }
/*     */ 
/*     */   void preInit(XCreateWindowParams paramXCreateWindowParams) {
/*  77 */     super.preInit(paramXCreateWindowParams);
/*  78 */     paramXCreateWindowParams.putIfNull("bit gravity", Integer.valueOf(1));
/*  79 */     Long localLong = (Long)paramXCreateWindowParams.get("event mask");
/*  80 */     if (localLong != null) {
/*  81 */       localLong = Long.valueOf(localLong.longValue() & 0xFFFDFFFF);
/*  82 */       paramXCreateWindowParams.put("event mask", localLong);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getWMName() {
/*  87 */     return "Content window";
/*     */   }
/*     */   protected boolean isEventDisabled(XEvent paramXEvent) {
/*  90 */     switch (paramXEvent.get_type())
/*     */     {
/*     */     case 7:
/*     */     case 8:
/*  94 */       return false;
/*     */     case 22:
/*  97 */       return true;
/*     */     case 18:
/*     */     case 19:
/* 101 */       return true;
/*     */     }
/* 103 */     return (super.isEventDisabled(paramXEvent)) || (this.parentFrame.isEventDisabled(paramXEvent));
/*     */   }
/*     */ 
/*     */   void setContentBounds(WindowDimensions paramWindowDimensions)
/*     */   {
/* 109 */     XToolkit.awtLock();
/*     */     try
/*     */     {
/* 113 */       Rectangle localRectangle = paramWindowDimensions.getBounds();
/* 114 */       Insets localInsets = paramWindowDimensions.getInsets();
/* 115 */       if (localInsets != null) {
/* 116 */         localRectangle.setLocation(-localInsets.left, -localInsets.top);
/*     */       }
/* 118 */       if (insLog.isLoggable(500)) insLog.fine("Setting content bounds {0}, old bounds {1}", new Object[] { localRectangle, getBounds() });
/*     */ 
/* 123 */       int i = !localRectangle.equals(getBounds()) ? 1 : 0;
/* 124 */       reshape(localRectangle);
/* 125 */       if (i != 0) {
/* 126 */         insLog.fine("Sending RESIZED");
/* 127 */         handleResize(localRectangle);
/*     */       }
/*     */     } finally {
/* 130 */       XToolkit.awtUnlock();
/*     */     }
/* 132 */     validateSurface();
/*     */   }
/*     */ 
/*     */   public void handleResize(Rectangle paramRectangle)
/*     */   {
/* 138 */     AWTAccessor.getComponentAccessor().setSize(this.target, paramRectangle.width, paramRectangle.height);
/* 139 */     postEvent(new ComponentEvent(this.target, 101));
/*     */   }
/*     */ 
/*     */   public void handleExposeEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 154 */     if (((this.parentFrame instanceof XFramePeer)) && ((((XFramePeer)this.parentFrame).getState() & 0x1) != 0))
/*     */     {
/* 158 */       this.iconifiedExposeEvents.add(new SavedExposeEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4));
/*     */     }
/*     */     else
/* 161 */       super.handleExposeEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */   }
/*     */ 
/*     */   void purgeIconifiedExposeEvents()
/*     */   {
/* 166 */     for (SavedExposeEvent localSavedExposeEvent : this.iconifiedExposeEvents) {
/* 167 */       super.handleExposeEvent(localSavedExposeEvent.target, localSavedExposeEvent.x, localSavedExposeEvent.y, localSavedExposeEvent.w, localSavedExposeEvent.h);
/*     */     }
/* 169 */     this.iconifiedExposeEvents.clear();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 185 */     return getClass().getName() + "[" + getBounds() + "]";
/*     */   }
/*     */ 
/*     */   private static class SavedExposeEvent
/*     */   {
/*     */     Component target;
/*     */     int x;
/*     */     int y;
/*     */     int w;
/*     */     int h;
/*     */ 
/*     */     SavedExposeEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 176 */       this.target = paramComponent;
/* 177 */       this.x = paramInt1;
/* 178 */       this.y = paramInt2;
/* 179 */       this.w = paramInt3;
/* 180 */       this.h = paramInt4;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XContentWindow
 * JD-Core Version:    0.6.2
 */