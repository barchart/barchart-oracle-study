/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.SystemTray;
/*     */ import java.awt.TrayIcon;
/*     */ import java.awt.peer.SystemTrayPeer;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.SystemTrayAccessor;
/*     */ import sun.awt.AWTAccessor.TrayIconAccessor;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XSystemTrayPeer
/*     */   implements SystemTrayPeer, XMSelectionListener
/*     */ {
/*  36 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XSystemTrayPeer");
/*     */   SystemTray target;
/*     */   static XSystemTrayPeer peerInstance;
/*     */   private volatile boolean available;
/*  42 */   private final XMSelection selection = new XMSelection("_NET_SYSTEM_TRAY");
/*     */   private static final int SCREEN = 0;
/*     */   private static final String SYSTEM_TRAY_PROPERTY_NAME = "systemTray";
/*  46 */   private static final XAtom _NET_SYSTEM_TRAY = XAtom.get("_NET_SYSTEM_TRAY_S0");
/*  47 */   private static final XAtom _XEMBED_INFO = XAtom.get("_XEMBED_INFO");
/*  48 */   private static final XAtom _NET_SYSTEM_TRAY_OPCODE = XAtom.get("_NET_SYSTEM_TRAY_OPCODE");
/*  49 */   private static final XAtom _NET_WM_ICON = XAtom.get("_NET_WM_ICON");
/*     */   private static final long SYSTEM_TRAY_REQUEST_DOCK = 0L;
/*     */ 
/*     */   XSystemTrayPeer(SystemTray paramSystemTray)
/*     */   {
/*  53 */     this.target = paramSystemTray;
/*  54 */     peerInstance = this;
/*     */ 
/*  56 */     this.selection.addSelectionListener(this);
/*     */ 
/*  58 */     long l = this.selection.getOwner(0);
/*  59 */     this.available = (l != 0L);
/*     */ 
/*  61 */     log.fine(" check if system tray is available. selection owner: " + l);
/*     */   }
/*     */ 
/*     */   public void ownerChanged(int paramInt, XMSelection paramXMSelection, long paramLong1, long paramLong2, long paramLong3) {
/*  65 */     if (paramInt != 0) {
/*  66 */       return;
/*     */     }
/*  68 */     if (!this.available) {
/*  69 */       this.available = true;
/*  70 */       firePropertyChange("systemTray", null, this.target);
/*     */     } else {
/*  72 */       removeTrayPeers();
/*     */     }
/*  74 */     createTrayPeers();
/*     */   }
/*     */ 
/*     */   public void ownerDeath(int paramInt, XMSelection paramXMSelection, long paramLong) {
/*  78 */     if (paramInt != 0) {
/*  79 */       return;
/*     */     }
/*  81 */     if (this.available) {
/*  82 */       this.available = false;
/*  83 */       firePropertyChange("systemTray", this.target, null);
/*  84 */       removeTrayPeers();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void selectionChanged(int paramInt, XMSelection paramXMSelection, long paramLong, XPropertyEvent paramXPropertyEvent) {
/*     */   }
/*     */ 
/*     */   public Dimension getTrayIconSize() {
/*  92 */     return new Dimension(24, 24);
/*     */   }
/*     */ 
/*     */   boolean isAvailable() {
/*  96 */     return this.available;
/*     */   }
/*     */ 
/*     */   void dispose() {
/* 100 */     this.selection.removeSelectionListener(this);
/*     */   }
/*     */ 
/*     */   void addTrayIcon(XTrayIconPeer paramXTrayIconPeer)
/*     */     throws AWTException
/*     */   {
/* 107 */     long l1 = this.selection.getOwner(0);
/*     */ 
/* 109 */     log.fine(" send SYSTEM_TRAY_REQUEST_DOCK message to owner: " + l1);
/*     */ 
/* 111 */     if (l1 == 0L) {
/* 112 */       throw new AWTException("TrayIcon couldn't be displayed.");
/*     */     }
/*     */ 
/* 115 */     long l2 = paramXTrayIconPeer.getWindow();
/* 116 */     long[] arrayOfLong = { 0L, 1L };
/* 117 */     long l3 = Native.card32ToData(arrayOfLong);
/*     */ 
/* 119 */     _XEMBED_INFO.setAtomData(l2, l3, arrayOfLong.length);
/*     */ 
/* 121 */     sendMessage(l1, 0L, l2, 0L, 0L);
/*     */   }
/*     */ 
/*     */   void sendMessage(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) {
/* 125 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try
/*     */     {
/* 128 */       localXClientMessageEvent.set_type(33);
/* 129 */       localXClientMessageEvent.set_window(paramLong1);
/* 130 */       localXClientMessageEvent.set_format(32);
/* 131 */       localXClientMessageEvent.set_message_type(_NET_SYSTEM_TRAY_OPCODE.getAtom());
/* 132 */       localXClientMessageEvent.set_data(0, 0L);
/* 133 */       localXClientMessageEvent.set_data(1, paramLong2);
/* 134 */       localXClientMessageEvent.set_data(2, paramLong3);
/* 135 */       localXClientMessageEvent.set_data(3, paramLong4);
/* 136 */       localXClientMessageEvent.set_data(4, paramLong5);
/*     */ 
/* 138 */       XToolkit.awtLock();
/*     */       try {
/* 140 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), paramLong1, false, 0L, localXClientMessageEvent.pData);
/*     */       }
/*     */       finally {
/* 143 */         XToolkit.awtUnlock();
/*     */       }
/*     */     } finally {
/* 146 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   static XSystemTrayPeer getPeerInstance() {
/* 151 */     return peerInstance;
/*     */   }
/*     */ 
/*     */   private void firePropertyChange(final String paramString, final Object paramObject1, final Object paramObject2)
/*     */   {
/* 157 */     Runnable local1 = new Runnable() {
/*     */       public void run() {
/* 159 */         AWTAccessor.getSystemTrayAccessor().firePropertyChange(XSystemTrayPeer.this.target, paramString, paramObject1, paramObject2);
/*     */       }
/*     */     };
/* 163 */     invokeOnEachAppContext(local1);
/*     */   }
/*     */ 
/*     */   private void createTrayPeers() {
/* 167 */     Runnable local2 = new Runnable() {
/*     */       public void run() {
/* 169 */         TrayIcon[] arrayOfTrayIcon1 = XSystemTrayPeer.this.target.getTrayIcons();
/*     */         try {
/* 171 */           for (TrayIcon localTrayIcon : arrayOfTrayIcon1)
/* 172 */             AWTAccessor.getTrayIconAccessor().addNotify(localTrayIcon);
/*     */         }
/*     */         catch (AWTException localAWTException)
/*     */         {
/*     */         }
/*     */       }
/*     */     };
/* 178 */     invokeOnEachAppContext(local2);
/*     */   }
/*     */ 
/*     */   private void removeTrayPeers() {
/* 182 */     Runnable local3 = new Runnable() {
/*     */       public void run() {
/* 184 */         TrayIcon[] arrayOfTrayIcon1 = XSystemTrayPeer.this.target.getTrayIcons();
/* 185 */         for (TrayIcon localTrayIcon : arrayOfTrayIcon1)
/* 186 */           AWTAccessor.getTrayIconAccessor().removeNotify(localTrayIcon);
/*     */       }
/*     */     };
/* 190 */     invokeOnEachAppContext(local3);
/*     */   }
/*     */ 
/*     */   private void invokeOnEachAppContext(Runnable paramRunnable) {
/* 194 */     for (AppContext localAppContext : AppContext.getAppContexts())
/* 195 */       SunToolkit.invokeLaterOnAppContext(localAppContext, paramRunnable);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XSystemTrayPeer
 * JD-Core Version:    0.6.2
 */