/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.dnd.SunDropTargetContextPeer;
/*     */ import sun.awt.dnd.SunDropTargetEvent;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ final class XDropTargetContextPeer extends SunDropTargetContextPeer
/*     */ {
/*  52 */   private static final PlatformLogger logger = PlatformLogger.getLogger("sun.awt.X11.xembed.xdnd.XDropTargetContextPeer");
/*     */ 
/*  55 */   private static final Unsafe unsafe = XlibWrapper.unsafe;
/*     */ 
/*  60 */   private static final Object DTCP_KEY = "DropTargetContextPeer";
/*     */ 
/*     */   static XDropTargetContextPeer getPeer(AppContext paramAppContext)
/*     */   {
/*  65 */     synchronized (_globalLock) {
/*  66 */       XDropTargetContextPeer localXDropTargetContextPeer = (XDropTargetContextPeer)paramAppContext.get(DTCP_KEY);
/*     */ 
/*  68 */       if (localXDropTargetContextPeer == null) {
/*  69 */         localXDropTargetContextPeer = new XDropTargetContextPeer();
/*  70 */         paramAppContext.put(DTCP_KEY, localXDropTargetContextPeer);
/*     */       }
/*     */ 
/*  73 */       return localXDropTargetContextPeer;
/*     */     }
/*     */   }
/*     */ 
/*     */   static XDropTargetProtocolListener getXDropTargetProtocolListener() {
/*  78 */     return XDropTargetProtocolListenerImpl.getInstance();
/*     */   }
/*     */ 
/*     */   protected void eventProcessed(SunDropTargetEvent paramSunDropTargetEvent, int paramInt, boolean paramBoolean)
/*     */   {
/*  88 */     long l = getNativeDragContext();
/*     */     try
/*     */     {
/*  91 */       if ((l != 0L) && (!paramSunDropTargetEvent.isConsumed())) {
/*  92 */         Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/*  95 */         while (localIterator.hasNext()) {
/*  96 */           XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/*  98 */           if (localXDropTargetProtocol.sendResponse(l, paramSunDropTargetEvent.getID(), paramInt))
/*     */           {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 105 */       if ((paramBoolean) && (l != 0L))
/* 106 */         unsafe.freeMemory(l);
/*     */     }
/*     */     finally
/*     */     {
/* 105 */       if ((paramBoolean) && (l != 0L))
/* 106 */         unsafe.freeMemory(l);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doDropDone(boolean paramBoolean1, int paramInt, boolean paramBoolean2)
/*     */   {
/* 115 */     long l = getNativeDragContext();
/*     */ 
/* 117 */     if (l != 0L)
/*     */       try {
/* 119 */         Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 122 */         while (localIterator.hasNext()) {
/* 123 */           XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 125 */           if (localXDropTargetProtocol.sendDropDone(l, paramBoolean1, paramInt))
/*     */             break;
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 131 */         unsafe.freeMemory(l);
/*     */       }
/*     */   }
/*     */ 
/*     */   protected Object getNativeData(long paramLong)
/*     */     throws IOException
/*     */   {
/* 140 */     long l = getNativeDragContext();
/*     */ 
/* 142 */     if (l != 0L) {
/* 143 */       Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 146 */       while (localIterator.hasNext()) {
/* 147 */         XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */         try
/*     */         {
/* 151 */           return localXDropTargetProtocol.getData(l, paramLong);
/*     */         }
/*     */         catch (IllegalArgumentException localIllegalArgumentException) {
/*     */         }
/*     */       }
/*     */     }
/* 157 */     return null;
/*     */   }
/*     */ 
/*     */   private void cleanup() {
/*     */   }
/*     */ 
/*     */   protected void processEnterMessage(SunDropTargetEvent paramSunDropTargetEvent) {
/* 164 */     if (!processSunDropTargetEvent(paramSunDropTargetEvent))
/* 165 */       super.processEnterMessage(paramSunDropTargetEvent);
/*     */   }
/*     */ 
/*     */   protected void processExitMessage(SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/* 170 */     if (!processSunDropTargetEvent(paramSunDropTargetEvent))
/* 171 */       super.processExitMessage(paramSunDropTargetEvent);
/*     */   }
/*     */ 
/*     */   protected void processMotionMessage(SunDropTargetEvent paramSunDropTargetEvent, boolean paramBoolean)
/*     */   {
/* 177 */     if (!processSunDropTargetEvent(paramSunDropTargetEvent))
/* 178 */       super.processMotionMessage(paramSunDropTargetEvent, paramBoolean);
/*     */   }
/*     */ 
/*     */   protected void processDropMessage(SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/* 183 */     if (!processSunDropTargetEvent(paramSunDropTargetEvent))
/* 184 */       super.processDropMessage(paramSunDropTargetEvent);
/*     */   }
/*     */ 
/*     */   private boolean processSunDropTargetEvent(SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/* 192 */     Object localObject = paramSunDropTargetEvent.getSource();
/*     */ 
/* 194 */     if ((localObject instanceof Component)) {
/* 195 */       ComponentPeer localComponentPeer = ((Component)localObject).getPeer();
/* 196 */       if ((localComponentPeer instanceof XEmbedCanvasPeer)) {
/* 197 */         XEmbedCanvasPeer localXEmbedCanvasPeer = (XEmbedCanvasPeer)localComponentPeer;
/*     */ 
/* 200 */         long l = getNativeDragContext();
/*     */ 
/* 202 */         if (logger.isLoggable(400)) {
/* 203 */           logger.finer("        processing " + paramSunDropTargetEvent + " ctxt=" + l + " consumed=" + paramSunDropTargetEvent.isConsumed());
/*     */         }
/*     */ 
/* 208 */         if (!paramSunDropTargetEvent.isConsumed())
/*     */         {
/* 210 */           if (localXEmbedCanvasPeer.processXEmbedDnDEvent(l, paramSunDropTargetEvent.getID()))
/*     */           {
/* 212 */             paramSunDropTargetEvent.consume();
/* 213 */             return true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 219 */     return false;
/*     */   }
/*     */ 
/*     */   public void forwardEventToEmbedded(long paramLong1, long paramLong2, int paramInt)
/*     */   {
/* 224 */     Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 227 */     while (localIterator.hasNext()) {
/* 228 */       XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 230 */       if (localXDropTargetProtocol.forwardEventToEmbedded(paramLong1, paramLong2, paramInt))
/*     */         break;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class XDropTargetProtocolListenerImpl
/*     */     implements XDropTargetProtocolListener
/*     */   {
/* 240 */     private static final XDropTargetProtocolListener theInstance = new XDropTargetProtocolListenerImpl();
/*     */ 
/*     */     static XDropTargetProtocolListener getInstance()
/*     */     {
/* 246 */       return theInstance;
/*     */     }
/*     */ 
/*     */     public void handleDropTargetNotification(XWindow paramXWindow, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong, int paramInt5)
/*     */     {
/* 253 */       Object localObject = paramXWindow.getTarget();
/*     */ 
/* 256 */       assert ((localObject instanceof Component));
/*     */ 
/* 258 */       Component localComponent = (Component)localObject;
/*     */ 
/* 260 */       AppContext localAppContext = SunToolkit.targetToAppContext(localObject);
/*     */ 
/* 263 */       assert (localAppContext != null);
/*     */ 
/* 265 */       XDropTargetContextPeer localXDropTargetContextPeer = XDropTargetContextPeer.getPeer(localAppContext);
/*     */ 
/* 267 */       localXDropTargetContextPeer.postDropTargetEvent(localComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, paramInt5, false);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDropTargetContextPeer
 * JD-Core Version:    0.6.2
 */