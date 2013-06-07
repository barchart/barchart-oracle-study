/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ final class XDragAndDropProtocols
/*     */ {
/*  69 */   private static final List dragProtocols = Collections.unmodifiableList(localArrayList1);
/*  70 */   private static final List dropProtocols = Collections.unmodifiableList(localArrayList2);
/*     */   public static final String XDnD = "XDnD";
/*     */   public static final String MotifDnD = "MotifDnD";
/*     */ 
/*     */   static Iterator getDragSourceProtocols()
/*     */   {
/*  74 */     return dragProtocols.iterator();
/*     */   }
/*     */ 
/*     */   static Iterator getDropTargetProtocols() {
/*  78 */     return dropProtocols.iterator();
/*     */   }
/*     */ 
/*     */   public static XDragSourceProtocol getDragSourceProtocol(String paramString)
/*     */   {
/*  87 */     if (paramString == null) {
/*  88 */       return null;
/*     */     }
/*     */ 
/*  91 */     Iterator localIterator = getDragSourceProtocols();
/*  92 */     while (localIterator.hasNext()) {
/*  93 */       XDragSourceProtocol localXDragSourceProtocol = (XDragSourceProtocol)localIterator.next();
/*     */ 
/*  95 */       if (localXDragSourceProtocol.getProtocolName().equals(paramString)) {
/*  96 */         return localXDragSourceProtocol;
/*     */       }
/*     */     }
/*     */ 
/* 100 */     return null;
/*     */   }
/*     */ 
/*     */   public static XDropTargetProtocol getDropTargetProtocol(String paramString)
/*     */   {
/* 109 */     if (paramString == null) {
/* 110 */       return null;
/*     */     }
/*     */ 
/* 113 */     Iterator localIterator = getDropTargetProtocols();
/* 114 */     while (localIterator.hasNext()) {
/* 115 */       XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 117 */       if (localXDropTargetProtocol.getProtocolName().equals(paramString)) {
/* 118 */         return localXDropTargetProtocol;
/*     */       }
/*     */     }
/*     */ 
/* 122 */     return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  47 */     XDragSourceProtocolListener localXDragSourceProtocolListener = XDragSourceContextPeer.getXDragSourceProtocolListener();
/*     */ 
/*  50 */     XDropTargetProtocolListener localXDropTargetProtocolListener = XDropTargetContextPeer.getXDropTargetProtocolListener();
/*     */ 
/*  53 */     ArrayList localArrayList1 = new ArrayList();
/*  54 */     XDragSourceProtocol localXDragSourceProtocol1 = XDnDDragSourceProtocol.createInstance(localXDragSourceProtocolListener);
/*     */ 
/*  56 */     localArrayList1.add(localXDragSourceProtocol1);
/*  57 */     XDragSourceProtocol localXDragSourceProtocol2 = MotifDnDDragSourceProtocol.createInstance(localXDragSourceProtocolListener);
/*     */ 
/*  59 */     localArrayList1.add(localXDragSourceProtocol2);
/*     */ 
/*  61 */     ArrayList localArrayList2 = new ArrayList();
/*  62 */     XDropTargetProtocol localXDropTargetProtocol1 = XDnDDropTargetProtocol.createInstance(localXDropTargetProtocolListener);
/*     */ 
/*  64 */     localArrayList2.add(localXDropTargetProtocol1);
/*  65 */     XDropTargetProtocol localXDropTargetProtocol2 = MotifDnDDropTargetProtocol.createInstance(localXDropTargetProtocolListener);
/*     */ 
/*  67 */     localArrayList2.add(localXDropTargetProtocol2);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDragAndDropProtocols
 * JD-Core Version:    0.6.2
 */