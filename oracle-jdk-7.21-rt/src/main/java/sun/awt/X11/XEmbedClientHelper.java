/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTKeyStroke;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.FocusTraversalPolicy;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.X11GraphicsConfig;
/*     */ import sun.awt.X11GraphicsDevice;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XEmbedClientHelper extends XEmbedHelper
/*     */   implements XEventDispatcher
/*     */ {
/*  43 */   private static final PlatformLogger xembedLog = PlatformLogger.getLogger("sun.awt.X11.xembed.XEmbedClientHelper");
/*     */   private XEmbeddedFramePeer embedded;
/*     */   private long server;
/*     */   private boolean active;
/*     */   private boolean applicationActive;
/*     */ 
/*     */   void setClient(XEmbeddedFramePeer paramXEmbeddedFramePeer)
/*     */   {
/*  56 */     if (xembedLog.isLoggable(500)) {
/*  57 */       xembedLog.fine("XEmbed client: " + paramXEmbeddedFramePeer);
/*     */     }
/*  59 */     if (this.embedded != null) {
/*  60 */       XToolkit.removeEventDispatcher(this.embedded.getWindow(), this);
/*  61 */       this.active = false;
/*     */     }
/*  63 */     this.embedded = paramXEmbeddedFramePeer;
/*  64 */     if (this.embedded != null)
/*  65 */       XToolkit.addEventDispatcher(this.embedded.getWindow(), this);
/*     */   }
/*     */ 
/*     */   void install()
/*     */   {
/*  70 */     if (xembedLog.isLoggable(500)) {
/*  71 */       xembedLog.fine("Installing xembedder on " + this.embedded);
/*     */     }
/*  73 */     long[] arrayOfLong = { 0L, 1L };
/*  74 */     long l1 = Native.card32ToData(arrayOfLong);
/*     */     try {
/*  76 */       XEmbedInfo.setAtomData(this.embedded.getWindow(), l1, 2);
/*     */     } finally {
/*  78 */       unsafe.freeMemory(l1);
/*     */     }
/*     */ 
/*  82 */     long l2 = this.embedded.getParentWindowHandle();
/*  83 */     if (l2 != 0L) {
/*  84 */       XToolkit.awtLock();
/*     */       try {
/*  86 */         XlibWrapper.XReparentWindow(XToolkit.getDisplay(), this.embedded.getWindow(), l2, 0, 0);
/*     */       }
/*     */       finally
/*     */       {
/*  91 */         XToolkit.awtUnlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void handleClientMessage(XEvent paramXEvent) {
/*  97 */     XClientMessageEvent localXClientMessageEvent = paramXEvent.get_xclient();
/*  98 */     if (xembedLog.isLoggable(500)) xembedLog.fine(localXClientMessageEvent.toString());
/*  99 */     if (localXClientMessageEvent.get_message_type() == XEmbed.getAtom()) {
/* 100 */       if (xembedLog.isLoggable(500)) xembedLog.fine("Embedded message: " + msgidToString((int)localXClientMessageEvent.get_data(1)));
/* 101 */       switch ((int)localXClientMessageEvent.get_data(1)) {
/*     */       case 0:
/* 103 */         this.active = true;
/* 104 */         this.server = getEmbedder(this.embedded, localXClientMessageEvent);
/*     */ 
/* 107 */         if (!this.embedded.isReparented()) {
/* 108 */           this.embedded.setReparented(true);
/* 109 */           this.embedded.updateSizeHints();
/*     */         }
/* 111 */         this.embedded.notifyStarted();
/* 112 */         break;
/*     */       case 1:
/* 114 */         this.applicationActive = true;
/* 115 */         break;
/*     */       case 2:
/* 117 */         if (this.applicationActive) {
/* 118 */           this.applicationActive = false;
/* 119 */           handleWindowFocusOut(); } break;
/*     */       case 4:
/* 124 */         handleFocusIn((int)localXClientMessageEvent.get_data(2));
/* 125 */         break;
/*     */       case 5:
/* 127 */         if (this.applicationActive)
/* 128 */           handleWindowFocusOut(); break;
/*     */       case 3:
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void handleFocusIn(int paramInt) {
/* 135 */     if (this.embedded.focusAllowedFor()) {
/* 136 */       this.embedded.handleWindowFocusIn(0L);
/*     */     }
/* 138 */     switch (paramInt)
/*     */     {
/*     */     case 0:
/* 141 */       break;
/*     */     case 1:
/* 143 */       SunToolkit.executeOnEventHandlerThread(this.embedded.target, new Runnable() {
/*     */         public void run() {
/* 145 */           Component localComponent = ((Container)XEmbedClientHelper.this.embedded.target).getFocusTraversalPolicy().getFirstComponent((Container)XEmbedClientHelper.this.embedded.target);
/* 146 */           if (localComponent != null)
/* 147 */             localComponent.requestFocusInWindow();
/*     */         }
/*     */       });
/* 150 */       break;
/*     */     case 2:
/* 152 */       SunToolkit.executeOnEventHandlerThread(this.embedded.target, new Runnable() {
/*     */         public void run() {
/* 154 */           Component localComponent = ((Container)XEmbedClientHelper.this.embedded.target).getFocusTraversalPolicy().getLastComponent((Container)XEmbedClientHelper.this.embedded.target);
/* 155 */           if (localComponent != null)
/* 156 */             localComponent.requestFocusInWindow();
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   public void dispatchEvent(XEvent paramXEvent)
/*     */   {
/* 164 */     switch (paramXEvent.get_type()) {
/*     */     case 33:
/* 166 */       handleClientMessage(paramXEvent);
/* 167 */       break;
/*     */     case 21:
/* 169 */       handleReparentNotify(paramXEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handleReparentNotify(XEvent paramXEvent) {
/* 174 */     XReparentEvent localXReparentEvent = paramXEvent.get_xreparent();
/* 175 */     long l = localXReparentEvent.get_parent();
/* 176 */     if (this.active)
/*     */     {
/* 178 */       this.embedded.notifyStopped();
/*     */ 
/* 180 */       X11GraphicsConfig localX11GraphicsConfig = (X11GraphicsConfig)this.embedded.getGraphicsConfiguration();
/* 181 */       X11GraphicsDevice localX11GraphicsDevice = (X11GraphicsDevice)localX11GraphicsConfig.getDevice();
/* 182 */       if ((l == XlibUtil.getRootWindow(localX11GraphicsDevice.getScreen())) || (l == XToolkit.getDefaultRootWindow()))
/*     */       {
/* 186 */         this.active = false;
/*     */       }
/*     */       else {
/* 189 */         this.server = l;
/* 190 */         this.embedded.notifyStarted();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 195 */   boolean requestFocus() { if ((this.active) && (this.embedded.focusAllowedFor())) {
/* 196 */       sendMessage(this.server, 3);
/* 197 */       return true;
/*     */     }
/* 199 */     return false;
/*     */   }
/*     */ 
/*     */   void handleWindowFocusOut()
/*     */   {
/* 207 */     if (XKeyboardFocusManagerPeer.getCurrentNativeFocusedWindow() == this.embedded.target)
/* 208 */       this.embedded.handleWindowFocusOut(null, 0L);
/*     */   }
/*     */ 
/*     */   long getEmbedder(XWindowPeer paramXWindowPeer, XClientMessageEvent paramXClientMessageEvent)
/*     */   {
/* 214 */     return XlibUtil.getParentWindow(paramXWindowPeer.getWindow());
/*     */   }
/*     */ 
/*     */   boolean isApplicationActive() {
/* 218 */     return this.applicationActive;
/*     */   }
/*     */ 
/*     */   boolean isActive() {
/* 222 */     return this.active;
/*     */   }
/*     */ 
/*     */   void traverseOutForward() {
/* 226 */     if (this.active)
/* 227 */       sendMessage(this.server, 6);
/*     */   }
/*     */ 
/*     */   void traverseOutBackward()
/*     */   {
/* 232 */     if (this.active)
/* 233 */       sendMessage(this.server, 7);
/*     */   }
/*     */ 
/*     */   void registerAccelerator(AWTKeyStroke paramAWTKeyStroke, int paramInt)
/*     */   {
/* 238 */     if (this.active) {
/* 239 */       long l1 = getX11KeySym(paramAWTKeyStroke);
/* 240 */       long l2 = getX11Mods(paramAWTKeyStroke);
/* 241 */       sendMessage(this.server, 12, paramInt, l1, l2);
/*     */     }
/*     */   }
/*     */ 
/* 245 */   void unregisterAccelerator(int paramInt) { if (this.active)
/* 246 */       sendMessage(this.server, 13, paramInt, 0L, 0L);
/*     */   }
/*     */ 
/*     */   long getX11KeySym(AWTKeyStroke paramAWTKeyStroke)
/*     */   {
/* 251 */     XToolkit.awtLock();
/*     */     try {
/* 253 */       return XWindow.getKeySymForAWTKeyCode(paramAWTKeyStroke.getKeyCode());
/*     */     } finally {
/* 255 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   long getX11Mods(AWTKeyStroke paramAWTKeyStroke) {
/* 260 */     return XWindow.getXModifiers(paramAWTKeyStroke);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XEmbedClientHelper
 * JD-Core Version:    0.6.2
 */