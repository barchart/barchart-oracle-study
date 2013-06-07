/*     */ package sun.awt.X11;
/*     */ 
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ class XWINProtocol extends XProtocol
/*     */   implements XStateProtocol, XLayerProtocol
/*     */ {
/*  33 */   static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XWINProtocol");
/*     */ 
/*  36 */   XAtom XA_WIN_SUPPORTING_WM_CHECK = XAtom.get("_WIN_SUPPORTING_WM_CHECK");
/*  37 */   XAtom XA_WIN_PROTOCOLS = XAtom.get("_WIN_PROTOCOLS");
/*  38 */   XAtom XA_WIN_STATE = XAtom.get("_WIN_STATE");
/*     */ 
/* 179 */   XAtom XA_WIN_LAYER = XAtom.get("_WIN_LAYER");
/*     */   static final int WIN_STATE_STICKY = 1;
/*     */   static final int WIN_STATE_MINIMIZED = 2;
/*     */   static final int WIN_STATE_MAXIMIZED_VERT = 4;
/*     */   static final int WIN_STATE_MAXIMIZED_HORIZ = 8;
/*     */   static final int WIN_STATE_HIDDEN = 16;
/*     */   static final int WIN_STATE_SHADED = 32;
/*     */   static final int WIN_LAYER_ONTOP = 6;
/*     */   static final int WIN_LAYER_NORMAL = 4;
/* 192 */   long WinWindow = 0L;
/* 193 */   boolean supportChecked = false;
/*     */ 
/*     */   public boolean supportsState(int paramInt)
/*     */   {
/*  41 */     return doStateProtocol();
/*     */   }
/*     */ 
/*     */   public void setState(XWindowPeer paramXWindowPeer, int paramInt)
/*     */   {
/*     */     long l1;
/*  45 */     if (paramXWindowPeer.isShowing())
/*     */     {
/*  50 */       l1 = 0L;
/*     */ 
/*  52 */       if ((paramInt & 0x4) != 0) {
/*  53 */         l1 |= 4L;
/*     */       }
/*  55 */       if ((paramInt & 0x2) != 0) {
/*  56 */         l1 |= 8L;
/*     */       }
/*     */ 
/*  59 */       XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*  60 */       localXClientMessageEvent.set_type(33);
/*  61 */       localXClientMessageEvent.set_window(paramXWindowPeer.getWindow());
/*  62 */       localXClientMessageEvent.set_message_type(this.XA_WIN_STATE.getAtom());
/*  63 */       localXClientMessageEvent.set_format(32);
/*  64 */       localXClientMessageEvent.set_data(0, 12L);
/*  65 */       localXClientMessageEvent.set_data(1, l1);
/*  66 */       if (log.isLoggable(500)) log.fine("Sending WIN_STATE to root to change the state to " + l1); try
/*     */       {
/*  68 */         XToolkit.awtLock();
/*  69 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), XlibWrapper.RootWindow(XToolkit.getDisplay(), paramXWindowPeer.getScreenNumber()), false, 1572864L, localXClientMessageEvent.pData);
/*     */       }
/*     */       finally
/*     */       {
/*  77 */         XToolkit.awtUnlock();
/*     */       }
/*  79 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */     else
/*     */     {
/*  87 */       l1 = this.XA_WIN_STATE.getCard32Property(paramXWindowPeer);
/*  88 */       long l2 = l1;
/*     */ 
/*  96 */       if ((paramInt & 0x1) != 0)
/*  97 */         l1 |= 2L;
/*     */       else {
/*  99 */         l1 &= -3L;
/*     */       }
/*     */ 
/* 102 */       if ((paramInt & 0x4) != 0)
/* 103 */         l1 |= 4L;
/*     */       else {
/* 105 */         l1 &= -5L;
/*     */       }
/*     */ 
/* 108 */       if ((paramInt & 0x2) != 0)
/* 109 */         l1 |= 8L;
/*     */       else {
/* 111 */         l1 &= -9L;
/*     */       }
/* 113 */       if ((l2 ^ l1) != 0L) {
/* 114 */         if (log.isLoggable(500)) log.fine("Setting WIN_STATE on " + paramXWindowPeer + " to change the state to " + l1);
/* 115 */         this.XA_WIN_STATE.setCard32Property(paramXWindowPeer, l1);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getState(XWindowPeer paramXWindowPeer) {
/* 121 */     long l = this.XA_WIN_STATE.getCard32Property(paramXWindowPeer);
/* 122 */     int i = 0;
/* 123 */     if ((l & 0x4) != 0L) {
/* 124 */       i |= 4;
/*     */     }
/* 126 */     if ((l & 0x8) != 0L) {
/* 127 */       i |= 2;
/*     */     }
/* 129 */     return i;
/*     */   }
/*     */ 
/*     */   public boolean isStateChange(XPropertyEvent paramXPropertyEvent) {
/* 133 */     return (doStateProtocol()) && (paramXPropertyEvent.get_atom() == this.XA_WIN_STATE.getAtom());
/*     */   }
/*     */ 
/*     */   public void unshadeKludge(XWindowPeer paramXWindowPeer) {
/* 137 */     long l = this.XA_WIN_STATE.getCard32Property(paramXWindowPeer);
/* 138 */     if ((l & 0x20) == 0L) {
/* 139 */       return;
/*     */     }
/* 141 */     l &= -33L;
/* 142 */     this.XA_WIN_STATE.setCard32Property(paramXWindowPeer, l);
/*     */   }
/*     */ 
/*     */   public boolean supportsLayer(int paramInt) {
/* 146 */     return ((paramInt == 1) || (paramInt == 0)) && (doLayerProtocol());
/*     */   }
/*     */ 
/*     */   public void setLayer(XWindowPeer paramXWindowPeer, int paramInt) {
/* 150 */     if (paramXWindowPeer.isShowing()) {
/* 151 */       XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/* 152 */       localXClientMessageEvent.set_type(33);
/* 153 */       localXClientMessageEvent.set_window(paramXWindowPeer.getWindow());
/* 154 */       localXClientMessageEvent.set_message_type(this.XA_WIN_LAYER.getAtom());
/* 155 */       localXClientMessageEvent.set_format(32);
/* 156 */       localXClientMessageEvent.set_data(0, paramInt == 0 ? 4L : 6L);
/* 157 */       localXClientMessageEvent.set_data(1, 0L);
/* 158 */       localXClientMessageEvent.set_data(2, 0L);
/* 159 */       if (log.isLoggable(500)) log.fine("Setting layer " + paramInt + " by root message : " + localXClientMessageEvent);
/* 160 */       XToolkit.awtLock();
/*     */       try {
/* 162 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), XlibWrapper.RootWindow(XToolkit.getDisplay(), paramXWindowPeer.getScreenNumber()), false, 524288L, localXClientMessageEvent.pData);
/*     */       }
/*     */       finally
/*     */       {
/* 170 */         XToolkit.awtUnlock();
/*     */       }
/* 172 */       localXClientMessageEvent.dispose();
/*     */     } else {
/* 174 */       if (log.isLoggable(500)) log.fine("Setting layer property to " + paramInt);
/* 175 */       this.XA_WIN_LAYER.setCard32Property(paramXWindowPeer, paramInt == 0 ? 4L : 6L);
/*     */     }
/*     */   }
/*     */ 
/*     */   void detect()
/*     */   {
/* 195 */     if (this.supportChecked) {
/* 196 */       return;
/*     */     }
/* 198 */     this.WinWindow = checkAnchor(this.XA_WIN_SUPPORTING_WM_CHECK, 6L);
/* 199 */     this.supportChecked = true;
/* 200 */     if (log.isLoggable(500)) log.fine("### " + this + " is active: " + (this.WinWindow != 0L)); 
/*     */   }
/*     */ 
/*     */   boolean active()
/*     */   {
/* 204 */     detect();
/* 205 */     return this.WinWindow != 0L;
/*     */   }
/*     */   boolean doStateProtocol() {
/* 208 */     boolean bool = (active()) && (checkProtocol(this.XA_WIN_PROTOCOLS, this.XA_WIN_STATE));
/* 209 */     if (log.isLoggable(500)) log.fine("### " + this + " supports state: " + bool);
/* 210 */     return bool;
/*     */   }
/*     */ 
/*     */   boolean doLayerProtocol() {
/* 214 */     boolean bool = (active()) && (checkProtocol(this.XA_WIN_PROTOCOLS, this.XA_WIN_LAYER));
/* 215 */     if (log.isLoggable(500)) log.fine("### " + this + " supports layer: " + bool);
/* 216 */     return bool;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XWINProtocol
 * JD-Core Version:    0.6.2
 */