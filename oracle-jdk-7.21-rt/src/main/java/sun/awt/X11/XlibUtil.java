/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import sun.awt.X11GraphicsEnvironment;
/*     */ 
/*     */ public class XlibUtil
/*     */ {
/* 375 */   static Boolean isShapingSupported = null;
/*     */ 
/*     */   public static long getRootWindow(int paramInt)
/*     */   {
/*  60 */     XToolkit.awtLock();
/*     */     try
/*     */     {
/*  63 */       X11GraphicsEnvironment localX11GraphicsEnvironment = (X11GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
/*     */       long l;
/*  65 */       if (localX11GraphicsEnvironment.runningXinerama())
/*     */       {
/*  68 */         return XlibWrapper.RootWindow(XToolkit.getDisplay(), 0L);
/*     */       }
/*     */ 
/*  72 */       return XlibWrapper.RootWindow(XToolkit.getDisplay(), paramInt);
/*     */     }
/*     */     finally
/*     */     {
/*  77 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean isRoot(long paramLong1, long paramLong2)
/*     */   {
/*  88 */     XToolkit.awtLock();
/*     */     long l;
/*     */     try
/*     */     {
/*  91 */       l = XlibWrapper.RootWindow(XToolkit.getDisplay(), paramLong2);
/*     */     }
/*     */     finally
/*     */     {
/*  96 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/*  99 */     return l == paramLong1;
/*     */   }
/*     */ 
/*     */   static Rectangle getWindowGeometry(long paramLong)
/*     */   {
/* 107 */     XToolkit.awtLock();
/*     */     try
/*     */     {
/* 110 */       int i = XlibWrapper.XGetGeometry(XToolkit.getDisplay(), paramLong, XlibWrapper.larg1, XlibWrapper.larg2, XlibWrapper.larg3, XlibWrapper.larg4, XlibWrapper.larg5, XlibWrapper.larg6, XlibWrapper.larg7);
/*     */ 
/* 119 */       if (i == 0)
/*     */       {
/* 121 */         return null;
/*     */       }
/*     */ 
/* 124 */       int j = Native.getInt(XlibWrapper.larg2);
/* 125 */       int k = Native.getInt(XlibWrapper.larg3);
/* 126 */       long l1 = Native.getUInt(XlibWrapper.larg4);
/* 127 */       long l2 = Native.getUInt(XlibWrapper.larg5);
/*     */ 
/* 129 */       return new Rectangle(j, k, (int)l1, (int)l2);
/*     */     }
/*     */     finally
/*     */     {
/* 133 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   static Point translateCoordinates(long paramLong1, long paramLong2, Point paramPoint)
/*     */   {
/* 143 */     Point localPoint = null;
/*     */ 
/* 145 */     XToolkit.awtLock();
/*     */     try
/*     */     {
/* 148 */       XTranslateCoordinates localXTranslateCoordinates = new XTranslateCoordinates(paramLong1, paramLong2, paramPoint.x, paramPoint.y);
/*     */       try
/*     */       {
/* 152 */         int i = localXTranslateCoordinates.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 153 */         if ((i != 0) && ((XToolkit.saved_error == null) || (XToolkit.saved_error.get_error_code() == 0)))
/*     */         {
/* 157 */           localPoint = new Point(localXTranslateCoordinates.get_dest_x(), localXTranslateCoordinates.get_dest_y());
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 162 */         localXTranslateCoordinates.dispose();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 167 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/* 170 */     return localPoint;
/*     */   }
/*     */ 
/*     */   static Rectangle translateCoordinates(long paramLong1, long paramLong2, Rectangle paramRectangle)
/*     */   {
/* 179 */     Point localPoint = translateCoordinates(paramLong1, paramLong2, paramRectangle.getLocation());
/* 180 */     if (localPoint == null)
/*     */     {
/* 182 */       return null;
/*     */     }
/*     */ 
/* 186 */     return new Rectangle(localPoint, paramRectangle.getSize());
/*     */   }
/*     */ 
/*     */   static long getParentWindow(long paramLong)
/*     */   {
/* 195 */     XToolkit.awtLock();
/*     */     try
/*     */     {
/* 198 */       XBaseWindow localXBaseWindow = XToolkit.windowToXWindow(paramLong);
/*     */       long l;
/* 199 */       if (localXBaseWindow != null)
/*     */       {
/* 201 */         localObject1 = localXBaseWindow.getParentWindow();
/* 202 */         if (localObject1 != null)
/*     */         {
/* 204 */           return ((XBaseWindow)localObject1).getWindow();
/*     */         }
/*     */       }
/*     */ 
/* 208 */       Object localObject1 = new XQueryTree(paramLong);
/*     */       try
/*     */       {
/* 211 */         if (((XQueryTree)localObject1).execute() == 0)
/*     */         {
/* 213 */           l = 0L;
/*     */ 
/* 222 */           ((XQueryTree)localObject1).dispose();
/*     */ 
/* 227 */           return l;
/*     */         }
/* 217 */         l = ((XQueryTree)localObject1).get_parent();
/*     */ 
/* 222 */         ((XQueryTree)localObject1).dispose();
/*     */ 
/* 227 */         return l;
/*     */       }
/*     */       finally
/*     */       {
/* 222 */         ((XQueryTree)localObject1).dispose();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 227 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   static Set<Long> getChildWindows(long paramLong)
/*     */   {
/* 236 */     XToolkit.awtLock();
/*     */     try
/*     */     {
/* 239 */       XBaseWindow localXBaseWindow = XToolkit.windowToXWindow(paramLong);
/* 240 */       if (localXBaseWindow != null)
/*     */       {
/* 242 */         return localXBaseWindow.getChildren();
/*     */       }
/*     */ 
/* 245 */       Object localObject1 = new XQueryTree(paramLong);
/*     */       try
/*     */       {
/* 248 */         int i = ((XQueryTree)localObject1).execute();
/* 249 */         if (i == 0)
/*     */         {
/* 251 */           Set localSet1 = Collections.emptySet();
/*     */ 
/* 273 */           ((XQueryTree)localObject1).dispose();
/*     */ 
/* 278 */           return localSet1;
/*     */         }
/* 254 */         long l = ((XQueryTree)localObject1).get_children();
/*     */ 
/* 256 */         if (l == 0L)
/*     */         {
/* 258 */           Set localSet2 = Collections.emptySet();
/*     */ 
/* 273 */           ((XQueryTree)localObject1).dispose();
/*     */ 
/* 278 */           return localSet2;
/*     */         }
/* 261 */         int j = ((XQueryTree)localObject1).get_nchildren();
/*     */ 
/* 263 */         HashSet localHashSet1 = new HashSet(j);
/* 264 */         for (int k = 0; k < j; k++)
/*     */         {
/* 266 */           localHashSet1.add(Long.valueOf(Native.getWindow(l, k)));
/*     */         }
/*     */ 
/* 269 */         HashSet localHashSet2 = localHashSet1;
/*     */ 
/* 273 */         ((XQueryTree)localObject1).dispose();
/*     */ 
/* 278 */         return localHashSet2;
/*     */       }
/*     */       finally
/*     */       {
/* 273 */         ((XQueryTree)localObject1).dispose();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 278 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean isXAWTToplevelWindow(long paramLong)
/*     */   {
/* 288 */     return XToolkit.windowToXWindow(paramLong) instanceof XWindowPeer;
/*     */   }
/*     */ 
/*     */   static boolean isToplevelWindow(long paramLong)
/*     */   {
/* 296 */     if ((XToolkit.windowToXWindow(paramLong) instanceof XDecoratedPeer))
/*     */     {
/* 298 */       return true;
/*     */     }
/*     */ 
/* 301 */     XToolkit.awtLock();
/*     */     try
/*     */     {
/* 304 */       WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, XWM.XA_WM_STATE, 0L, 1L, false, XWM.XA_WM_STATE);
/*     */       boolean bool;
/*     */       try
/*     */       {
/* 309 */         localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 310 */         if (localWindowPropertyGetter.getActualType() == XWM.XA_WM_STATE.getAtom())
/*     */         {
/* 312 */           bool = true;
/*     */ 
/* 317 */           localWindowPropertyGetter.dispose();
/*     */ 
/* 324 */           return bool;
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 317 */         localWindowPropertyGetter.dispose();
/*     */       }
/*     */ 
/* 320 */       return false;
/*     */     }
/*     */     finally
/*     */     {
/* 324 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean isTrueToplevelWindow(long paramLong)
/*     */   {
/* 334 */     if ((XToolkit.windowToXWindow(paramLong) instanceof XEmbeddedFramePeer))
/*     */     {
/* 336 */       return false;
/*     */     }
/*     */ 
/* 339 */     return isToplevelWindow(paramLong);
/*     */   }
/*     */ 
/*     */   static int getWindowMapState(long paramLong)
/*     */   {
/* 344 */     XToolkit.awtLock();
/* 345 */     XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/*     */     try
/*     */     {
/* 348 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 349 */       int i = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), paramLong, localXWindowAttributes.pData);
/*     */ 
/* 351 */       XToolkit.RESTORE_XERROR_HANDLER();
/* 352 */       if ((i != 0) && ((XToolkit.saved_error == null) || (XToolkit.saved_error.get_error_code() == 0)))
/*     */       {
/* 356 */         return localXWindowAttributes.get_map_state();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 361 */       localXWindowAttributes.dispose();
/* 362 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/* 365 */     return 0;
/*     */   }
/*     */ 
/*     */   static synchronized boolean isShapingSupported()
/*     */   {
/* 383 */     if (isShapingSupported == null) {
/* 384 */       XToolkit.awtLock();
/*     */       try {
/* 386 */         isShapingSupported = Boolean.valueOf(XlibWrapper.XShapeQueryExtension(XToolkit.getDisplay(), XlibWrapper.larg1, XlibWrapper.larg2));
/*     */       }
/*     */       finally
/*     */       {
/* 392 */         XToolkit.awtUnlock();
/*     */       }
/*     */     }
/*     */ 
/* 396 */     return isShapingSupported.booleanValue();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XlibUtil
 * JD-Core Version:    0.6.2
 */