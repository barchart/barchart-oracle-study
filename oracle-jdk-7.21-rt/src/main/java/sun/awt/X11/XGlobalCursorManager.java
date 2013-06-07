/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Point;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import java.lang.ref.WeakReference;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.ComponentAccessor;
/*     */ import sun.awt.AWTAccessor.CursorAccessor;
/*     */ import sun.awt.GlobalCursorManager;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ public final class XGlobalCursorManager extends GlobalCursorManager
/*     */ {
/*     */   private WeakReference<Component> nativeContainer;
/*     */   private static XGlobalCursorManager manager;
/*     */ 
/*     */   static GlobalCursorManager getCursorManager()
/*     */   {
/*  49 */     if (manager == null) {
/*  50 */       manager = new XGlobalCursorManager();
/*     */     }
/*  52 */     return manager;
/*     */   }
/*     */ 
/*     */   static void nativeUpdateCursor(Component paramComponent)
/*     */   {
/*  60 */     getCursorManager().updateCursorLater(paramComponent);
/*     */   }
/*     */ 
/*     */   protected void setCursor(Component paramComponent, Cursor paramCursor, boolean paramBoolean)
/*     */   {
/*  65 */     if (paramComponent == null) {
/*  66 */       return;
/*     */     }
/*     */ 
/*  69 */     Cursor localCursor = paramBoolean ? paramCursor : getCapableCursor(paramComponent);
/*     */ 
/*  71 */     Component localComponent = null;
/*  72 */     if (paramBoolean)
/*  73 */       synchronized (this) {
/*  74 */         localComponent = (Component)this.nativeContainer.get();
/*     */       }
/*     */     else {
/*  77 */       localComponent = SunToolkit.getHeavyweightComponent(paramComponent);
/*     */     }
/*     */ 
/*  80 */     if (localComponent != null) {
/*  81 */       ??? = AWTAccessor.getComponentAccessor().getPeer(localComponent);
/*  82 */       if ((??? instanceof XComponentPeer)) {
/*  83 */         synchronized (this) {
/*  84 */           this.nativeContainer = new WeakReference(localComponent);
/*     */         }
/*     */ 
/*  89 */         ((XComponentPeer)???).pSetCursor(localCursor, false);
/*     */ 
/*  94 */         updateGrabbedCursor(localCursor);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void updateGrabbedCursor(Cursor paramCursor)
/*     */   {
/* 104 */     XBaseWindow localXBaseWindow = XAwtState.getGrabWindow();
/* 105 */     if ((localXBaseWindow instanceof XWindowPeer)) {
/* 106 */       XWindowPeer localXWindowPeer = (XWindowPeer)localXBaseWindow;
/* 107 */       localXWindowPeer.pSetCursor(paramCursor);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void updateCursorOutOfJava()
/*     */   {
/* 115 */     updateGrabbedCursor(Cursor.getPredefinedCursor(0));
/*     */   }
/*     */ 
/*     */   protected void getCursorPos(Point paramPoint)
/*     */   {
/* 120 */     if (!((XToolkit)Toolkit.getDefaultToolkit()).getLastCursorPos(paramPoint)) {
/* 121 */       XToolkit.awtLock();
/*     */       try {
/* 123 */         long l1 = XToolkit.getDisplay();
/* 124 */         long l2 = XlibWrapper.RootWindow(l1, XlibWrapper.DefaultScreen(l1));
/*     */ 
/* 127 */         XlibWrapper.XQueryPointer(l1, l2, XlibWrapper.larg1, XlibWrapper.larg2, XlibWrapper.larg3, XlibWrapper.larg4, XlibWrapper.larg5, XlibWrapper.larg6, XlibWrapper.larg7);
/*     */ 
/* 136 */         paramPoint.x = XlibWrapper.unsafe.getInt(XlibWrapper.larg3);
/* 137 */         paramPoint.y = XlibWrapper.unsafe.getInt(XlibWrapper.larg4);
/*     */       } finally {
/* 139 */         XToolkit.awtUnlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 144 */   protected Component findHeavyweightUnderCursor() { return XAwtState.getComponentMouseEntered(); }
/*     */ 
/*     */ 
/*     */   protected Component findComponentAt(Container paramContainer, int paramInt1, int paramInt2)
/*     */   {
/* 152 */     return paramContainer.findComponentAt(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   protected Point getLocationOnScreen(Component paramComponent) {
/* 156 */     return paramComponent.getLocationOnScreen();
/*     */   }
/*     */ 
/*     */   protected Component findHeavyweightUnderCursor(boolean paramBoolean) {
/* 160 */     return findHeavyweightUnderCursor();
/*     */   }
/*     */ 
/*     */   private Cursor getCapableCursor(Component paramComponent) {
/* 164 */     AWTAccessor.ComponentAccessor localComponentAccessor = AWTAccessor.getComponentAccessor();
/*     */ 
/* 166 */     Object localObject = paramComponent;
/*     */ 
/* 170 */     while ((localObject != null) && (!(localObject instanceof Window)) && (localComponentAccessor.isEnabled((Component)localObject)) && (localComponentAccessor.isVisible((Component)localObject)) && (localComponentAccessor.isDisplayable((Component)localObject)))
/*     */     {
/* 172 */       localObject = localComponentAccessor.getParent((Component)localObject);
/*     */     }
/* 174 */     if ((localObject instanceof Window)) {
/* 175 */       return (localComponentAccessor.isEnabled((Component)localObject)) && (localComponentAccessor.isVisible((Component)localObject)) && (localComponentAccessor.isDisplayable((Component)localObject)) && (localComponentAccessor.isEnabled(paramComponent)) ? localComponentAccessor.getCursor(paramComponent) : Cursor.getPredefinedCursor(0);
/*     */     }
/*     */ 
/* 183 */     if (localObject == null) {
/* 184 */       return null;
/*     */     }
/* 186 */     return getCapableCursor(localComponentAccessor.getParent((Component)localObject));
/*     */   }
/*     */ 
/*     */   static long getCursor(Cursor paramCursor)
/*     */   {
/* 193 */     long l = 0L;
/* 194 */     int i = 0;
/*     */     try {
/* 196 */       l = AWTAccessor.getCursorAccessor().getPData(paramCursor);
/* 197 */       i = AWTAccessor.getCursorAccessor().getType(paramCursor);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 201 */       localException.printStackTrace();
/*     */     }
/*     */ 
/* 204 */     if (l != 0L) return l;
/*     */ 
/* 206 */     int j = 0;
/* 207 */     switch (i) {
/*     */     case 0:
/* 209 */       j = 68;
/* 210 */       break;
/*     */     case 1:
/* 212 */       j = 34;
/* 213 */       break;
/*     */     case 2:
/* 215 */       j = 152;
/* 216 */       break;
/*     */     case 3:
/* 218 */       j = 150;
/* 219 */       break;
/*     */     case 4:
/* 221 */       j = 12;
/* 222 */       break;
/*     */     case 6:
/* 224 */       j = 134;
/* 225 */       break;
/*     */     case 5:
/* 227 */       j = 14;
/* 228 */       break;
/*     */     case 7:
/* 230 */       j = 136;
/* 231 */       break;
/*     */     case 9:
/* 233 */       j = 16;
/* 234 */       break;
/*     */     case 8:
/* 236 */       j = 138;
/* 237 */       break;
/*     */     case 10:
/* 239 */       j = 70;
/* 240 */       break;
/*     */     case 11:
/* 242 */       j = 96;
/* 243 */       break;
/*     */     case 12:
/* 245 */       j = 60;
/* 246 */       break;
/*     */     case 13:
/* 248 */       j = 52;
/*     */     }
/*     */ 
/* 252 */     XToolkit.awtLock();
/*     */     try {
/* 254 */       l = XlibWrapper.XCreateFontCursor(XToolkit.getDisplay(), j);
/*     */     }
/*     */     finally {
/* 257 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/* 260 */     setPData(paramCursor, l);
/* 261 */     return l;
/*     */   }
/*     */ 
/*     */   static void setPData(Cursor paramCursor, long paramLong)
/*     */   {
/*     */     try {
/* 267 */       AWTAccessor.getCursorAccessor().setPData(paramCursor, paramLong);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 271 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XGlobalCursorManager
 * JD-Core Version:    0.6.2
 */