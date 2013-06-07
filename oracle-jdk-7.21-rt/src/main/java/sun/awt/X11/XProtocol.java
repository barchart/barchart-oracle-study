/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ class XProtocol
/*    */ {
/* 33 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XProtocol");
/*    */ 
/* 35 */   private Map<XAtom, XAtomList> atomToList = new HashMap();
/* 36 */   private Map<XAtom, Long> atomToAnchor = new HashMap();
/*    */ 
/* 38 */   volatile boolean firstCheck = true;
/*    */ 
/*    */   boolean checkProtocol(XAtom paramXAtom1, XAtom paramXAtom2)
/*    */   {
/* 44 */     XAtomList localXAtomList = (XAtomList)this.atomToList.get(paramXAtom1);
/*    */ 
/* 46 */     if (localXAtomList != null) {
/* 47 */       return localXAtomList.contains(paramXAtom2);
/*    */     }
/*    */ 
/* 50 */     localXAtomList = paramXAtom1.getAtomListPropertyList(XToolkit.getDefaultRootWindow());
/* 51 */     this.atomToList.put(paramXAtom1, localXAtomList);
/*    */     try {
/* 53 */       return localXAtomList.contains(paramXAtom2);
/*    */     } finally {
/* 55 */       if (this.firstCheck) {
/* 56 */         this.firstCheck = false;
/* 57 */         log.fine("{0}:{1} supports {2}", new Object[] { this, paramXAtom1, localXAtomList });
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   long checkAnchorImpl(XAtom paramXAtom, long paramLong)
/*    */   {
/* 73 */     XToolkit.awtLock();
/*    */     long l1;
/*    */     try
/*    */     {
/* 75 */       l1 = paramXAtom.get32Property(XToolkit.getDefaultRootWindow(), paramLong);
/*    */     }
/*    */     finally {
/* 78 */       XToolkit.awtUnlock();
/*    */     }
/* 80 */     if (l1 == 0L) {
/* 81 */       return 0L;
/*    */     }
/* 83 */     long l2 = paramXAtom.get32Property(l1, paramLong);
/* 84 */     if (l2 != l1) {
/* 85 */       return 0L;
/*    */     }
/* 87 */     return l2;
/*    */   }
/*    */   public long checkAnchor(XAtom paramXAtom, long paramLong) {
/* 90 */     Long localLong = (Long)this.atomToAnchor.get(paramXAtom);
/* 91 */     if (localLong != null) {
/* 92 */       return localLong.longValue();
/*    */     }
/* 94 */     long l = checkAnchorImpl(paramXAtom, paramLong);
/* 95 */     this.atomToAnchor.put(paramXAtom, Long.valueOf(l));
/* 96 */     return l;
/*    */   }
/*    */   public long checkAnchor(XAtom paramXAtom1, XAtom paramXAtom2) {
/* 99 */     return checkAnchor(paramXAtom1, paramXAtom2.getAtom());
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XProtocol
 * JD-Core Version:    0.6.2
 */