/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XMSelection
/*     */ {
/*  58 */   private static PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XMSelection");
/*     */   String selectionName;
/*     */   Vector listeners;
/*     */   XAtom[] atoms;
/*     */   long[] owners;
/*     */   long eventMask;
/*     */   static int numScreens;
/*     */   static XAtom XA_MANAGER;
/*  93 */   static HashMap selectionMap = new HashMap();
/*     */ 
/*     */   static void initScreen(long paramLong, int paramInt)
/*     */   {
/*  97 */     XToolkit.awtLock();
/*     */     try {
/*  99 */       long l = XlibWrapper.RootWindow(paramLong, paramInt);
/* 100 */       XlibWrapper.XSelectInput(paramLong, l, 131072L);
/* 101 */       XToolkit.addEventDispatcher(l, new XEventDispatcher()
/*     */       {
/*     */         public void dispatchEvent(XEvent paramAnonymousXEvent) {
/* 104 */           XMSelection.processRootEvent(paramAnonymousXEvent, this.val$screen);
/*     */         }
/*     */       });
/*     */     }
/*     */     finally {
/* 109 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getNumberOfScreens()
/*     */   {
/* 115 */     return numScreens;
/*     */   }
/*     */ 
/*     */   void select(long paramLong) {
/* 119 */     this.eventMask = paramLong;
/* 120 */     for (int i = 0; i < numScreens; i++)
/* 121 */       selectPerScreen(i, paramLong);
/*     */   }
/*     */ 
/*     */   void resetOwner(long paramLong, final int paramInt)
/*     */   {
/* 126 */     XToolkit.awtLock();
/*     */     try {
/* 128 */       long l = XToolkit.getDisplay();
/* 129 */       synchronized (this) {
/* 130 */         setOwner(paramLong, paramInt);
/* 131 */         if (log.isLoggable(500)) log.fine("New Selection Owner for screen " + paramInt + " = " + paramLong);
/* 132 */         XlibWrapper.XSelectInput(l, paramLong, 0x20000 | this.eventMask);
/* 133 */         XToolkit.addEventDispatcher(paramLong, new XEventDispatcher()
/*     */         {
/*     */           public void dispatchEvent(XEvent paramAnonymousXEvent) {
/* 136 */             XMSelection.this.dispatchSelectionEvent(paramAnonymousXEvent, paramInt);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     finally {
/* 142 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   void selectPerScreen(final int paramInt, long paramLong) {
/* 147 */     XToolkit.awtLock();
/*     */     try {
/*     */       try {
/* 150 */         long l1 = XToolkit.getDisplay();
/* 151 */         if (log.isLoggable(500)) log.fine("Grabbing XServer");
/* 152 */         XlibWrapper.XGrabServer(l1);
/*     */ 
/* 154 */         synchronized (this) {
/* 155 */           String str = getName() + "_S" + paramInt;
/* 156 */           if (log.isLoggable(500)) log.fine("Screen = " + paramInt + " selection name = " + str);
/* 157 */           XAtom localXAtom = XAtom.get(str);
/* 158 */           selectionMap.put(Long.valueOf(localXAtom.getAtom()), this);
/* 159 */           setAtom(localXAtom, paramInt);
/* 160 */           long l2 = XlibWrapper.XGetSelectionOwner(l1, localXAtom.getAtom());
/* 161 */           if (l2 != 0L) {
/* 162 */             setOwner(l2, paramInt);
/* 163 */             if (log.isLoggable(500)) log.fine("Selection Owner for screen " + paramInt + " = " + l2);
/* 164 */             XlibWrapper.XSelectInput(l1, l2, 0x20000 | paramLong);
/* 165 */             XToolkit.addEventDispatcher(l2, new XEventDispatcher()
/*     */             {
/*     */               public void dispatchEvent(XEvent paramAnonymousXEvent) {
/* 168 */                 XMSelection.this.dispatchSelectionEvent(paramAnonymousXEvent, paramInt);
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception localException) {
/* 175 */         localException.printStackTrace();
/*     */       }
/*     */       finally {
/* 178 */         if (log.isLoggable(500)) log.fine("UnGrabbing XServer");
/* 179 */         XlibWrapper.XUngrabServer(XToolkit.getDisplay());
/*     */       }
/*     */     } finally {
/* 182 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean processClientMessage(XEvent paramXEvent, int paramInt)
/*     */   {
/* 188 */     XClientMessageEvent localXClientMessageEvent = paramXEvent.get_xclient();
/* 189 */     if (localXClientMessageEvent.get_message_type() == XA_MANAGER.getAtom()) {
/* 190 */       if (log.isLoggable(500)) log.fine("client messags = " + localXClientMessageEvent);
/* 191 */       long l1 = localXClientMessageEvent.get_data(0);
/* 192 */       long l2 = localXClientMessageEvent.get_data(1);
/* 193 */       long l3 = localXClientMessageEvent.get_data(2);
/* 194 */       long l4 = localXClientMessageEvent.get_data(3);
/*     */ 
/* 196 */       XMSelection localXMSelection = getInstance(l2);
/* 197 */       if (localXMSelection != null) {
/* 198 */         localXMSelection.resetOwner(l3, paramInt);
/* 199 */         localXMSelection.dispatchOwnerChangedEvent(paramXEvent, paramInt, l3, l4, l1);
/*     */       }
/*     */     }
/* 202 */     return false;
/*     */   }
/*     */ 
/*     */   static boolean processRootEvent(XEvent paramXEvent, int paramInt) {
/* 206 */     switch (paramXEvent.get_type()) {
/*     */     case 33:
/* 208 */       return processClientMessage(paramXEvent, paramInt);
/*     */     }
/*     */ 
/* 212 */     return false;
/*     */   }
/*     */ 
/*     */   static XMSelection getInstance(long paramLong)
/*     */   {
/* 218 */     return (XMSelection)selectionMap.get(Long.valueOf(paramLong));
/*     */   }
/*     */ 
/*     */   public XMSelection(String paramString)
/*     */   {
/* 227 */     this(paramString, 4194304L);
/*     */   }
/*     */ 
/*     */   public XMSelection(String paramString, long paramLong)
/*     */   {
/* 238 */     synchronized (this) {
/* 239 */       this.selectionName = paramString;
/* 240 */       this.atoms = new XAtom[getNumberOfScreens()];
/* 241 */       this.owners = new long[getNumberOfScreens()];
/*     */     }
/* 243 */     select(paramLong);
/*     */   }
/*     */ 
/*     */   public synchronized void addSelectionListener(XMSelectionListener paramXMSelectionListener)
/*     */   {
/* 249 */     if (this.listeners == null) {
/* 250 */       this.listeners = new Vector();
/*     */     }
/* 252 */     this.listeners.add(paramXMSelectionListener);
/*     */   }
/*     */ 
/*     */   public synchronized void removeSelectionListener(XMSelectionListener paramXMSelectionListener) {
/* 256 */     if (this.listeners != null)
/* 257 */       this.listeners.remove(paramXMSelectionListener);
/*     */   }
/*     */ 
/*     */   synchronized Collection getListeners()
/*     */   {
/* 262 */     return this.listeners;
/*     */   }
/*     */ 
/*     */   synchronized XAtom getAtom(int paramInt) {
/* 266 */     if (this.atoms != null) {
/* 267 */       return this.atoms[paramInt];
/*     */     }
/* 269 */     return null;
/*     */   }
/*     */ 
/*     */   synchronized void setAtom(XAtom paramXAtom, int paramInt) {
/* 273 */     if (this.atoms != null)
/* 274 */       this.atoms[paramInt] = paramXAtom;
/*     */   }
/*     */ 
/*     */   synchronized long getOwner(int paramInt)
/*     */   {
/* 279 */     if (this.owners != null) {
/* 280 */       return this.owners[paramInt];
/*     */     }
/* 282 */     return 0L;
/*     */   }
/*     */ 
/*     */   synchronized void setOwner(long paramLong, int paramInt) {
/* 286 */     if (this.owners != null)
/* 287 */       this.owners[paramInt] = paramLong;
/*     */   }
/*     */ 
/*     */   synchronized String getName()
/*     */   {
/* 292 */     return this.selectionName;
/*     */   }
/*     */ 
/*     */   synchronized void dispatchSelectionChanged(XPropertyEvent paramXPropertyEvent, int paramInt)
/*     */   {
/* 297 */     if (log.isLoggable(500)) log.fine("Selection Changed : Screen = " + paramInt + "Event =" + paramXPropertyEvent);
/* 298 */     if (this.listeners != null) {
/* 299 */       Iterator localIterator = this.listeners.iterator();
/* 300 */       while (localIterator.hasNext()) {
/* 301 */         XMSelectionListener localXMSelectionListener = (XMSelectionListener)localIterator.next();
/* 302 */         localXMSelectionListener.selectionChanged(paramInt, this, paramXPropertyEvent.get_window(), paramXPropertyEvent);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void dispatchOwnerDeath(XDestroyWindowEvent paramXDestroyWindowEvent, int paramInt) {
/* 308 */     if (log.isLoggable(500)) log.fine("Owner dead : Screen = " + paramInt + "Event =" + paramXDestroyWindowEvent);
/* 309 */     if (this.listeners != null) {
/* 310 */       Iterator localIterator = this.listeners.iterator();
/* 311 */       while (localIterator.hasNext()) {
/* 312 */         XMSelectionListener localXMSelectionListener = (XMSelectionListener)localIterator.next();
/* 313 */         localXMSelectionListener.ownerDeath(paramInt, this, paramXDestroyWindowEvent.get_window());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void dispatchSelectionEvent(XEvent paramXEvent, int paramInt)
/*     */   {
/* 320 */     if (log.isLoggable(500)) log.fine("Event =" + paramXEvent);
/*     */     Object localObject;
/* 321 */     if (paramXEvent.get_type() == 17) {
/* 322 */       localObject = paramXEvent.get_xdestroywindow();
/* 323 */       dispatchOwnerDeath((XDestroyWindowEvent)localObject, paramInt);
/*     */     }
/* 325 */     else if (paramXEvent.get_type() == 28) {
/* 326 */       localObject = paramXEvent.get_xproperty();
/* 327 */       dispatchSelectionChanged((XPropertyEvent)localObject, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void dispatchOwnerChangedEvent(XEvent paramXEvent, int paramInt, long paramLong1, long paramLong2, long paramLong3)
/*     */   {
/* 333 */     if (this.listeners != null) {
/* 334 */       Iterator localIterator = this.listeners.iterator();
/* 335 */       while (localIterator.hasNext()) {
/* 336 */         XMSelectionListener localXMSelectionListener = (XMSelectionListener)localIterator.next();
/* 337 */         localXMSelectionListener.ownerChanged(paramInt, this, paramLong1, paramLong2, paramLong3);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  81 */     long l = XToolkit.getDisplay();
/*  82 */     XToolkit.awtLock();
/*     */     try {
/*  84 */       numScreens = XlibWrapper.ScreenCount(l);
/*     */     } finally {
/*  86 */       XToolkit.awtUnlock();
/*     */     }
/*  88 */     XA_MANAGER = XAtom.get("MANAGER");
/*  89 */     for (int i = 0; i < numScreens; i++)
/*  90 */       initScreen(l, i);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XMSelection
 * JD-Core Version:    0.6.2
 */