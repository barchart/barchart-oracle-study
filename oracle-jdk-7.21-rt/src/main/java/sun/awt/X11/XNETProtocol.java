/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.List;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ final class XNETProtocol extends XProtocol
/*     */   implements XStateProtocol, XLayerProtocol
/*     */ {
/*  34 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XNETProtocol");
/*  35 */   private static final PlatformLogger iconLog = PlatformLogger.getLogger("sun.awt.X11.icon.XNETProtocol");
/*  36 */   private static PlatformLogger stateLog = PlatformLogger.getLogger("sun.awt.X11.states.XNETProtocol");
/*     */ 
/* 231 */   XAtom XA_UTF8_STRING = XAtom.get("UTF8_STRING");
/* 232 */   XAtom XA_NET_SUPPORTING_WM_CHECK = XAtom.get("_NET_SUPPORTING_WM_CHECK");
/* 233 */   XAtom XA_NET_SUPPORTED = XAtom.get("_NET_SUPPORTED");
/* 234 */   XAtom XA_NET_WM_NAME = XAtom.get("_NET_WM_NAME");
/* 235 */   XAtom XA_NET_WM_STATE = XAtom.get("_NET_WM_STATE");
/*     */ 
/* 244 */   XAtom XA_NET_WM_STATE_MAXIMIZED_HORZ = XAtom.get("_NET_WM_STATE_MAXIMIZED_HORZ");
/* 245 */   XAtom XA_NET_WM_STATE_MAXIMIZED_VERT = XAtom.get("_NET_WM_STATE_MAXIMIZED_VERT");
/* 246 */   XAtom XA_NET_WM_STATE_SHADED = XAtom.get("_NET_WM_STATE_SHADED");
/* 247 */   XAtom XA_NET_WM_STATE_ABOVE = XAtom.get("_NET_WM_STATE_ABOVE");
/* 248 */   XAtom XA_NET_WM_STATE_MODAL = XAtom.get("_NET_WM_STATE_MODAL");
/* 249 */   XAtom XA_NET_WM_STATE_FULLSCREEN = XAtom.get("_NET_WM_STATE_FULLSCREEN");
/* 250 */   XAtom XA_NET_WM_STATE_BELOW = XAtom.get("_NET_WM_STATE_BELOW");
/* 251 */   XAtom XA_NET_WM_STATE_HIDDEN = XAtom.get("_NET_WM_STATE_HIDDEN");
/* 252 */   XAtom XA_NET_WM_STATE_SKIP_TASKBAR = XAtom.get("_NET_WM_STATE_SKIP_TASKBAR");
/* 253 */   XAtom XA_NET_WM_STATE_SKIP_PAGER = XAtom.get("_NET_WM_STATE_SKIP_PAGER");
/*     */ 
/* 255 */   public final XAtom XA_NET_WM_WINDOW_TYPE = XAtom.get("_NET_WM_WINDOW_TYPE");
/* 256 */   public final XAtom XA_NET_WM_WINDOW_TYPE_NORMAL = XAtom.get("_NET_WM_WINDOW_TYPE_NORMAL");
/* 257 */   public final XAtom XA_NET_WM_WINDOW_TYPE_DIALOG = XAtom.get("_NET_WM_WINDOW_TYPE_DIALOG");
/* 258 */   public final XAtom XA_NET_WM_WINDOW_TYPE_UTILITY = XAtom.get("_NET_WM_WINDOW_TYPE_UTILITY");
/* 259 */   public final XAtom XA_NET_WM_WINDOW_TYPE_POPUP_MENU = XAtom.get("_NET_WM_WINDOW_TYPE_POPUP_MENU");
/*     */ 
/* 261 */   XAtom XA_NET_WM_WINDOW_OPACITY = XAtom.get("_NET_WM_WINDOW_OPACITY");
/*     */   static final int _NET_WM_STATE_REMOVE = 0;
/*     */   static final int _NET_WM_STATE_ADD = 1;
/*     */   static final int _NET_WM_STATE_TOGGLE = 2;
/* 268 */   boolean supportChecked = false;
/* 269 */   long NetWindow = 0L;
/*     */   String net_wm_name_cache;
/*     */ 
/*     */   public boolean supportsState(int paramInt)
/*     */   {
/*  42 */     return doStateProtocol();
/*     */   }
/*     */ 
/*     */   public void setState(XWindowPeer paramXWindowPeer, int paramInt) {
/*  46 */     if (log.isLoggable(500)) log.fine("Setting state of " + paramXWindowPeer + " to " + paramInt);
/*  47 */     if (paramXWindowPeer.isShowing())
/*  48 */       requestState(paramXWindowPeer, paramInt);
/*     */     else
/*  50 */       setInitialState(paramXWindowPeer, paramInt);
/*     */   }
/*     */ 
/*     */   private void setInitialState(XWindowPeer paramXWindowPeer, int paramInt)
/*     */   {
/*  55 */     XAtomList localXAtomList = paramXWindowPeer.getNETWMState();
/*  56 */     log.fine("Current state of the window {0} is {1}", new Object[] { paramXWindowPeer, localXAtomList });
/*  57 */     if ((paramInt & 0x4) != 0)
/*  58 */       localXAtomList.add(this.XA_NET_WM_STATE_MAXIMIZED_VERT);
/*     */     else {
/*  60 */       localXAtomList.remove(this.XA_NET_WM_STATE_MAXIMIZED_VERT);
/*     */     }
/*  62 */     if ((paramInt & 0x2) != 0)
/*  63 */       localXAtomList.add(this.XA_NET_WM_STATE_MAXIMIZED_HORZ);
/*     */     else {
/*  65 */       localXAtomList.remove(this.XA_NET_WM_STATE_MAXIMIZED_HORZ);
/*     */     }
/*  67 */     log.fine("Setting initial state of the window {0} to {1}", new Object[] { paramXWindowPeer, localXAtomList });
/*  68 */     paramXWindowPeer.setNETWMState(localXAtomList);
/*     */   }
/*     */ 
/*     */   private void requestState(XWindowPeer paramXWindowPeer, int paramInt)
/*     */   {
/*  77 */     int i = getState(paramXWindowPeer);
/*  78 */     int j = (paramInt ^ i) & 0x6;
/*     */ 
/*  80 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try {
/*  82 */       switch (j) {
/*     */       case 0:
/*     */         return;
/*     */       case 2:
/*  86 */         localXClientMessageEvent.set_data(1, this.XA_NET_WM_STATE_MAXIMIZED_HORZ.getAtom());
/*  87 */         localXClientMessageEvent.set_data(2, 0L);
/*  88 */         break;
/*     */       case 4:
/*  90 */         localXClientMessageEvent.set_data(1, this.XA_NET_WM_STATE_MAXIMIZED_VERT.getAtom());
/*  91 */         localXClientMessageEvent.set_data(2, 0L);
/*  92 */         break;
/*     */       case 6:
/*  94 */         localXClientMessageEvent.set_data(1, this.XA_NET_WM_STATE_MAXIMIZED_HORZ.getAtom());
/*  95 */         localXClientMessageEvent.set_data(2, this.XA_NET_WM_STATE_MAXIMIZED_VERT.getAtom());
/*  96 */         break;
/*     */       case 1:
/*     */       case 3:
/*     */       case 5:
/*     */       default:
/* 100 */         return; } if (log.isLoggable(500)) log.fine("Requesting state on " + paramXWindowPeer + " for " + paramInt);
/* 101 */       localXClientMessageEvent.set_type(33);
/* 102 */       localXClientMessageEvent.set_window(paramXWindowPeer.getWindow());
/* 103 */       localXClientMessageEvent.set_message_type(this.XA_NET_WM_STATE.getAtom());
/* 104 */       localXClientMessageEvent.set_format(32);
/* 105 */       localXClientMessageEvent.set_data(0, 2L);
/* 106 */       XToolkit.awtLock();
/*     */       try {
/* 108 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), XlibWrapper.RootWindow(XToolkit.getDisplay(), paramXWindowPeer.getScreenNumber()), false, 1572864L, localXClientMessageEvent.pData);
/*     */       }
/*     */       finally
/*     */       {
/* 115 */         XToolkit.awtUnlock();
/*     */       }
/*     */     } finally {
/* 118 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getState(XWindowPeer paramXWindowPeer) {
/* 123 */     return getStateImpl(paramXWindowPeer);
/*     */   }
/*     */ 
/*     */   int getStateImpl(XWindowPeer paramXWindowPeer)
/*     */   {
/* 130 */     XAtomList localXAtomList = paramXWindowPeer.getNETWMState();
/* 131 */     if (localXAtomList.size() == 0) {
/* 132 */       return 0;
/*     */     }
/* 134 */     int i = 0;
/* 135 */     if (localXAtomList.contains(this.XA_NET_WM_STATE_MAXIMIZED_VERT)) {
/* 136 */       i |= 4;
/*     */     }
/* 138 */     if (localXAtomList.contains(this.XA_NET_WM_STATE_MAXIMIZED_HORZ)) {
/* 139 */       i |= 2;
/*     */     }
/* 141 */     return i;
/*     */   }
/*     */ 
/*     */   public boolean isStateChange(XPropertyEvent paramXPropertyEvent) {
/* 145 */     boolean bool = (doStateProtocol()) && (paramXPropertyEvent.get_atom() == this.XA_NET_WM_STATE.getAtom());
/*     */ 
/* 147 */     if (bool)
/*     */     {
/* 149 */       XWindowPeer localXWindowPeer = (XWindowPeer)XToolkit.windowToXWindow(paramXPropertyEvent.get_window());
/* 150 */       localXWindowPeer.setNETWMState(null);
/*     */     }
/* 152 */     return bool;
/*     */   }
/*     */ 
/*     */   public void unshadeKludge(XWindowPeer paramXWindowPeer)
/*     */   {
/* 159 */     XAtomList localXAtomList = paramXWindowPeer.getNETWMState();
/* 160 */     localXAtomList.remove(this.XA_NET_WM_STATE_SHADED);
/* 161 */     paramXWindowPeer.setNETWMState(localXAtomList);
/*     */   }
/*     */ 
/*     */   public boolean supportsLayer(int paramInt)
/*     */   {
/* 168 */     return ((paramInt == 1) || (paramInt == 0)) && (doLayerProtocol());
/*     */   }
/*     */ 
/*     */   public void requestState(XWindow paramXWindow, XAtom paramXAtom, boolean paramBoolean) {
/* 172 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try {
/* 174 */       localXClientMessageEvent.set_type(33);
/* 175 */       localXClientMessageEvent.set_window(paramXWindow.getWindow());
/* 176 */       localXClientMessageEvent.set_message_type(this.XA_NET_WM_STATE.getAtom());
/* 177 */       localXClientMessageEvent.set_format(32);
/* 178 */       localXClientMessageEvent.set_data(0, paramBoolean ? 1L : 0L);
/* 179 */       localXClientMessageEvent.set_data(1, paramXAtom.getAtom());
/*     */ 
/* 181 */       localXClientMessageEvent.set_data(2, 0L);
/* 182 */       log.fine("Setting _NET_STATE atom {0} on {1} for {2}", new Object[] { paramXAtom, paramXWindow, Boolean.valueOf(paramBoolean) });
/* 183 */       XToolkit.awtLock();
/*     */       try {
/* 185 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), XlibWrapper.RootWindow(XToolkit.getDisplay(), paramXWindow.getScreenNumber()), false, 1572864L, localXClientMessageEvent.pData);
/*     */       }
/*     */       finally
/*     */       {
/* 192 */         XToolkit.awtUnlock();
/*     */       }
/*     */     } finally {
/* 195 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setStateHelper(XWindowPeer paramXWindowPeer, XAtom paramXAtom, boolean paramBoolean)
/*     */   {
/* 207 */     log.finer("Window visibility is: withdrawn={0}, visible={1}, mapped={2} showing={3}", new Object[] { Boolean.valueOf(paramXWindowPeer.isWithdrawn()), Boolean.valueOf(paramXWindowPeer.isVisible()), Boolean.valueOf(paramXWindowPeer.isMapped()), Boolean.valueOf(paramXWindowPeer.isShowing()) });
/*     */ 
/* 210 */     if (paramXWindowPeer.isShowing()) {
/* 211 */       requestState(paramXWindowPeer, paramXAtom, paramBoolean);
/*     */     } else {
/* 213 */       XAtomList localXAtomList = paramXWindowPeer.getNETWMState();
/* 214 */       log.finer("Current state on {0} is {1}", new Object[] { paramXWindowPeer, localXAtomList });
/* 215 */       if (!paramBoolean)
/* 216 */         localXAtomList.remove(paramXAtom);
/*     */       else {
/* 218 */         localXAtomList.add(paramXAtom);
/*     */       }
/* 220 */       log.fine("Setting states on {0} to {1}", new Object[] { paramXWindowPeer, localXAtomList });
/* 221 */       paramXWindowPeer.setNETWMState(localXAtomList);
/*     */     }
/* 223 */     XToolkit.XSync();
/*     */   }
/*     */ 
/*     */   public void setLayer(XWindowPeer paramXWindowPeer, int paramInt) {
/* 227 */     setStateHelper(paramXWindowPeer, this.XA_NET_WM_STATE_ABOVE, paramInt == 1);
/*     */   }
/*     */ 
/*     */   void detect()
/*     */   {
/* 271 */     if (this.supportChecked)
/*     */     {
/* 273 */       return;
/*     */     }
/* 275 */     this.NetWindow = checkAnchor(this.XA_NET_SUPPORTING_WM_CHECK, 33L);
/* 276 */     this.supportChecked = true;
/* 277 */     if (log.isLoggable(500)) log.fine("### " + this + " is active: " + (this.NetWindow != 0L)); 
/*     */   }
/*     */ 
/*     */   boolean active()
/*     */   {
/* 281 */     detect();
/* 282 */     return this.NetWindow != 0L;
/*     */   }
/*     */ 
/*     */   boolean doStateProtocol() {
/* 286 */     boolean bool = (active()) && (checkProtocol(this.XA_NET_SUPPORTED, this.XA_NET_WM_STATE));
/* 287 */     stateLog.finer("doStateProtocol() returns " + bool);
/* 288 */     return bool;
/*     */   }
/*     */ 
/*     */   boolean doLayerProtocol() {
/* 292 */     boolean bool = (active()) && (checkProtocol(this.XA_NET_SUPPORTED, this.XA_NET_WM_STATE_ABOVE));
/* 293 */     return bool;
/*     */   }
/*     */ 
/*     */   boolean doModalityProtocol() {
/* 297 */     boolean bool = (active()) && (checkProtocol(this.XA_NET_SUPPORTED, this.XA_NET_WM_STATE_MODAL));
/* 298 */     return bool;
/*     */   }
/*     */ 
/*     */   boolean doOpacityProtocol() {
/* 302 */     boolean bool = (active()) && (checkProtocol(this.XA_NET_SUPPORTED, this.XA_NET_WM_WINDOW_OPACITY));
/* 303 */     return bool;
/*     */   }
/*     */ 
/*     */   boolean isWMName(String paramString) {
/* 307 */     if (!active()) {
/* 308 */       return false;
/*     */     }
/* 310 */     String str = getWMName();
/* 311 */     if (str == null) {
/* 312 */       return false;
/*     */     }
/* 314 */     if (log.isLoggable(500)) log.fine("### WM_NAME = " + str);
/* 315 */     return str.startsWith(paramString);
/*     */   }
/*     */ 
/*     */   public String getWMName()
/*     */   {
/* 320 */     if (!active()) {
/* 321 */       return null;
/*     */     }
/*     */ 
/* 324 */     if (this.net_wm_name_cache != null) {
/* 325 */       return this.net_wm_name_cache;
/*     */     }
/*     */ 
/* 334 */     String str = "UTF8";
/* 335 */     byte[] arrayOfByte = this.XA_NET_WM_NAME.getByteArrayProperty(this.NetWindow, this.XA_UTF8_STRING.getAtom());
/* 336 */     if (arrayOfByte == null) {
/* 337 */       arrayOfByte = this.XA_NET_WM_NAME.getByteArrayProperty(this.NetWindow, 31L);
/* 338 */       str = "ASCII";
/*     */     }
/*     */ 
/* 341 */     if (arrayOfByte == null)
/* 342 */       return null;
/*     */     try
/*     */     {
/* 345 */       this.net_wm_name_cache = new String(arrayOfByte, str);
/* 346 */       return this.net_wm_name_cache; } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*     */     }
/* 348 */     return null;
/*     */   }
/*     */ 
/*     */   public void setWMIcons(XWindowPeer paramXWindowPeer, List<XIconInfo> paramList)
/*     */   {
/* 357 */     if (paramXWindowPeer == null) return;
/*     */ 
/* 359 */     XAtom localXAtom = XAtom.get("_NET_WM_ICON");
/* 360 */     if (paramList == null) {
/* 361 */       localXAtom.DeleteProperty(paramXWindowPeer);
/* 362 */       return;
/*     */     }
/*     */ 
/* 365 */     int i = 0;
/* 366 */     for (XIconInfo localXIconInfo1 : paramList) {
/* 367 */       i += localXIconInfo1.getRawLength();
/*     */     }
/* 369 */     int j = XlibWrapper.dataModel == 32 ? 4 : 8;
/* 370 */     int k = i * j;
/*     */ 
/* 372 */     if (k != 0) {
/* 373 */       long l1 = XlibWrapper.unsafe.allocateMemory(k);
/*     */       try {
/* 375 */         long l2 = l1;
/* 376 */         for (XIconInfo localXIconInfo2 : paramList) {
/* 377 */           int m = localXIconInfo2.getRawLength() * j;
/* 378 */           if (XlibWrapper.dataModel == 32)
/* 379 */             XlibWrapper.copyIntArray(l2, localXIconInfo2.getIntData(), m);
/*     */           else {
/* 381 */             XlibWrapper.copyLongArray(l2, localXIconInfo2.getLongData(), m);
/*     */           }
/* 383 */           l2 += m;
/*     */         }
/* 385 */         localXAtom.setAtomData(paramXWindowPeer.getWindow(), 6L, l1, k / Native.getCard32Size());
/*     */       } finally {
/* 387 */         XlibWrapper.unsafe.freeMemory(l1);
/*     */       }
/*     */     } else {
/* 390 */       localXAtom.DeleteProperty(paramXWindowPeer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isWMStateNetHidden(XWindowPeer paramXWindowPeer) {
/* 395 */     if (!doStateProtocol()) {
/* 396 */       return false;
/*     */     }
/* 398 */     XAtomList localXAtomList = paramXWindowPeer.getNETWMState();
/* 399 */     return (localXAtomList != null) && (localXAtomList.size() != 0) && (localXAtomList.contains(this.XA_NET_WM_STATE_HIDDEN));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XNETProtocol
 * JD-Core Version:    0.6.2
 */