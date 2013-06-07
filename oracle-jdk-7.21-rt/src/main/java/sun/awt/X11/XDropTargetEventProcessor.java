/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ 
/*     */ final class XDropTargetEventProcessor
/*     */ {
/*  36 */   private static final XDropTargetEventProcessor theInstance = new XDropTargetEventProcessor();
/*     */ 
/*  38 */   private static boolean active = false;
/*     */ 
/*  41 */   private XDropTargetProtocol protocol = null;
/*     */ 
/*     */   private boolean doProcessEvent(XEvent paramXEvent)
/*     */   {
/*  46 */     if ((paramXEvent.get_type() == 17) && (this.protocol != null) && (paramXEvent.get_xany().get_window() == this.protocol.getSourceWindow()))
/*     */     {
/*  49 */       this.protocol.cleanup();
/*  50 */       this.protocol = null;
/*  51 */       return false;
/*     */     }
/*     */ 
/*  54 */     if (paramXEvent.get_type() == 28) {
/*  55 */       XPropertyEvent localXPropertyEvent = paramXEvent.get_xproperty();
/*  56 */       if (localXPropertyEvent.get_atom() == MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO.getAtom())
/*     */       {
/*  59 */         XDropTargetRegistry.getRegistry().updateEmbedderDropSite(localXPropertyEvent.get_window());
/*     */       }
/*     */     }
/*     */ 
/*  63 */     if (paramXEvent.get_type() != 33) {
/*  64 */       return false;
/*     */     }
/*     */ 
/*  67 */     boolean bool = false;
/*  68 */     XClientMessageEvent localXClientMessageEvent = paramXEvent.get_xclient();
/*     */ 
/*  70 */     XDropTargetProtocol localXDropTargetProtocol1 = this.protocol;
/*     */ 
/*  72 */     if (this.protocol != null) {
/*  73 */       if (this.protocol.getMessageType(localXClientMessageEvent) != 0)
/*     */       {
/*  75 */         bool = this.protocol.processClientMessage(localXClientMessageEvent);
/*     */       }
/*  77 */       else this.protocol = null;
/*     */ 
/*     */     }
/*     */ 
/*  81 */     if (this.protocol == null) {
/*  82 */       Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/*  85 */       while (localIterator.hasNext()) {
/*  86 */         XDropTargetProtocol localXDropTargetProtocol2 = (XDropTargetProtocol)localIterator.next();
/*     */ 
/*  89 */         if ((localXDropTargetProtocol2 != localXDropTargetProtocol1) && 
/*  93 */           (localXDropTargetProtocol2.getMessageType(localXClientMessageEvent) != 0))
/*     */         {
/*  98 */           this.protocol = localXDropTargetProtocol2;
/*  99 */           bool = this.protocol.processClientMessage(localXClientMessageEvent);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 104 */     return bool;
/*     */   }
/*     */ 
/*     */   static void reset() {
/* 108 */     theInstance.protocol = null;
/*     */   }
/*     */ 
/*     */   static void activate() {
/* 112 */     active = true;
/*     */   }
/*     */ 
/*     */   static boolean processEvent(XEvent paramXEvent)
/*     */   {
/* 119 */     return active ? theInstance.doProcessEvent(paramXEvent) : false;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDropTargetEventProcessor
 * JD-Core Version:    0.6.2
 */