/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.SortedMap;
/*     */ import sun.awt.UNIXToolkit;
/*     */ import sun.awt.datatransfer.ClipboardTransferable;
/*     */ import sun.awt.datatransfer.DataTransferer;
/*     */ import sun.awt.datatransfer.SunClipboard;
/*     */ import sun.security.action.GetIntegerAction;
/*     */ 
/*     */ public final class XClipboard extends SunClipboard
/*     */   implements OwnershipListener
/*     */ {
/*     */   private final XSelection selection;
/*     */   private long convertSelectionTime;
/*     */   private volatile boolean isSelectionNotifyProcessed;
/*     */   private volatile XAtom targetsPropertyAtom;
/*  56 */   private static final Object classLock = new Object();
/*     */   private static final int defaultPollInterval = 200;
/*     */   private static int pollInterval;
/*     */   private static Map<Long, XClipboard> targetsAtom2Clipboard;
/*     */ 
/*     */   public XClipboard(String paramString1, String paramString2)
/*     */   {
/*  68 */     super(paramString1);
/*  69 */     this.selection = new XSelection(XAtom.get(paramString2));
/*  70 */     this.selection.registerOwershipListener(this);
/*     */   }
/*     */ 
/*     */   public void ownershipChanged(boolean paramBoolean)
/*     */   {
/*  78 */     if (paramBoolean)
/*  79 */       checkChangeHere(this.contents);
/*     */     else
/*  81 */       lostOwnershipImpl();
/*     */   }
/*     */ 
/*     */   protected synchronized void setContentsNative(Transferable paramTransferable)
/*     */   {
/*  86 */     SortedMap localSortedMap = DataTransferer.getInstance().getFormatsForTransferable(paramTransferable, DataTransferer.adaptFlavorMap(flavorMap));
/*     */ 
/*  88 */     long[] arrayOfLong = DataTransferer.keysToLongArray(localSortedMap);
/*     */ 
/*  90 */     if (!this.selection.setOwner(paramTransferable, localSortedMap, arrayOfLong, XToolkit.getCurrentServerTime()))
/*     */     {
/*  92 */       this.owner = null;
/*  93 */       this.contents = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getID() {
/*  98 */     return this.selection.getSelectionAtom().getAtom();
/*     */   }
/*     */ 
/*     */   public synchronized Transferable getContents(Object paramObject)
/*     */   {
/* 103 */     if (this.contents != null) {
/* 104 */       return this.contents;
/*     */     }
/* 106 */     return new ClipboardTransferable(this);
/*     */   }
/*     */ 
/*     */   protected void clearNativeContext()
/*     */   {
/* 111 */     this.selection.reset();
/*     */   }
/*     */ 
/*     */   protected long[] getClipboardFormats()
/*     */   {
/* 116 */     return this.selection.getTargets(XToolkit.getCurrentServerTime());
/*     */   }
/*     */ 
/*     */   protected byte[] getClipboardData(long paramLong) throws IOException {
/* 120 */     return this.selection.getData(paramLong, XToolkit.getCurrentServerTime());
/*     */   }
/*     */ 
/*     */   private void checkChangeHere(Transferable paramTransferable) {
/* 124 */     if (areFlavorListenersRegistered())
/* 125 */       checkChange(DataTransferer.getInstance().getFormatsForTransferableAsArray(paramTransferable, flavorMap));
/*     */   }
/*     */ 
/*     */   private static int getPollInterval()
/*     */   {
/* 131 */     synchronized (classLock) {
/* 132 */       if (pollInterval <= 0) {
/* 133 */         pollInterval = ((Integer)AccessController.doPrivileged(new GetIntegerAction("awt.datatransfer.clipboard.poll.interval", 200))).intValue();
/*     */ 
/* 136 */         if (pollInterval <= 0) {
/* 137 */           pollInterval = 200;
/*     */         }
/*     */       }
/* 140 */       return pollInterval;
/*     */     }
/*     */   }
/*     */ 
/*     */   private XAtom getTargetsPropertyAtom() {
/* 145 */     if (null == this.targetsPropertyAtom) {
/* 146 */       this.targetsPropertyAtom = XAtom.get("XAWT_TARGETS_OF_SELECTION:" + this.selection.getSelectionAtom().getName());
/*     */     }
/*     */ 
/* 149 */     return this.targetsPropertyAtom;
/*     */   }
/*     */ 
/*     */   protected void registerClipboardViewerChecked()
/*     */   {
/* 154 */     this.isSelectionNotifyProcessed = true;
/*     */ 
/* 156 */     boolean bool = false;
/* 157 */     synchronized (classLock) {
/* 158 */       if (targetsAtom2Clipboard == null) {
/* 159 */         targetsAtom2Clipboard = new HashMap(2);
/*     */       }
/* 161 */       bool = targetsAtom2Clipboard.isEmpty();
/* 162 */       targetsAtom2Clipboard.put(Long.valueOf(getTargetsPropertyAtom().getAtom()), this);
/* 163 */       if (bool) {
/* 164 */         XToolkit.addEventDispatcher(XWindow.getXAWTRootWindow().getWindow(), new SelectionNotifyHandler(null));
/*     */       }
/*     */     }
/*     */ 
/* 168 */     if (bool)
/* 169 */       XToolkit.schedule(new CheckChangeTimerTask(null), getPollInterval());
/*     */   }
/*     */ 
/*     */   protected void unregisterClipboardViewerChecked()
/*     */   {
/* 207 */     this.isSelectionNotifyProcessed = false;
/* 208 */     synchronized (classLock) {
/* 209 */       targetsAtom2Clipboard.remove(Long.valueOf(getTargetsPropertyAtom().getAtom()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void getTargetsDelayed()
/*     */   {
/* 215 */     XToolkit.awtLock();
/*     */     try {
/* 217 */       long l = System.currentTimeMillis();
/* 218 */       if ((this.isSelectionNotifyProcessed) || (l >= this.convertSelectionTime + UNIXToolkit.getDatatransferTimeout()))
/*     */       {
/* 220 */         this.convertSelectionTime = l;
/* 221 */         XlibWrapper.XConvertSelection(XToolkit.getDisplay(), this.selection.getSelectionAtom().getAtom(), XDataTransferer.TARGETS_ATOM.getAtom(), getTargetsPropertyAtom().getAtom(), XWindow.getXAWTRootWindow().getWindow(), 0L);
/*     */ 
/* 227 */         this.isSelectionNotifyProcessed = false;
/*     */       }
/*     */     } finally {
/* 230 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkChange(XSelectionEvent paramXSelectionEvent)
/*     */   {
/* 240 */     long l = paramXSelectionEvent.get_property();
/* 241 */     if (l != getTargetsPropertyAtom().getAtom())
/*     */     {
/* 243 */       return;
/*     */     }
/*     */ 
/* 246 */     XAtom localXAtom = XAtom.get(paramXSelectionEvent.get_selection());
/* 247 */     XSelection localXSelection = XSelection.getSelection(localXAtom);
/*     */ 
/* 249 */     if ((null == localXSelection) || (localXSelection != this.selection))
/*     */     {
/* 251 */       return;
/*     */     }
/*     */ 
/* 254 */     this.isSelectionNotifyProcessed = true;
/*     */ 
/* 256 */     if (this.selection.isOwner())
/*     */     {
/* 258 */       return;
/*     */     }
/*     */ 
/* 261 */     long[] arrayOfLong = null;
/*     */ 
/* 263 */     if (l == 0L)
/*     */     {
/* 265 */       arrayOfLong = new long[0];
/*     */     } else {
/* 267 */       WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(XWindow.getXAWTRootWindow().getWindow(), XAtom.get(l), 0L, 1000000L, true, 0L);
/*     */       try
/*     */       {
/* 273 */         localWindowPropertyGetter.execute();
/* 274 */         arrayOfLong = XSelection.getFormats(localWindowPropertyGetter);
/*     */       } finally {
/* 276 */         localWindowPropertyGetter.dispose();
/*     */       }
/*     */     }
/*     */ 
/* 280 */     checkChange(arrayOfLong);
/*     */   }
/*     */ 
/*     */   private static class CheckChangeTimerTask
/*     */     implements Runnable
/*     */   {
/*     */     public void run()
/*     */     {
/* 175 */       for (XClipboard localXClipboard : XClipboard.targetsAtom2Clipboard.values()) {
/* 176 */         localXClipboard.getTargetsDelayed();
/*     */       }
/* 178 */       synchronized (XClipboard.classLock) {
/* 179 */         if ((XClipboard.targetsAtom2Clipboard != null) && (!XClipboard.targetsAtom2Clipboard.isEmpty()))
/* 180 */           XToolkit.schedule(this, XClipboard.access$500());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SelectionNotifyHandler implements XEventDispatcher
/*     */   {
/*     */     public void dispatchEvent(XEvent paramXEvent) {
/* 188 */       if (paramXEvent.get_type() == 31) {
/* 189 */         XSelectionEvent localXSelectionEvent = paramXEvent.get_xselection();
/* 190 */         XClipboard localXClipboard = null;
/* 191 */         synchronized (XClipboard.classLock) {
/* 192 */           if ((XClipboard.targetsAtom2Clipboard != null) && (!XClipboard.targetsAtom2Clipboard.isEmpty())) {
/* 193 */             XToolkit.removeEventDispatcher(XWindow.getXAWTRootWindow().getWindow(), this);
/* 194 */             return;
/*     */           }
/* 196 */           long l = localXSelectionEvent.get_property();
/* 197 */           localXClipboard = (XClipboard)XClipboard.targetsAtom2Clipboard.get(Long.valueOf(l));
/*     */         }
/* 199 */         if (null != localXClipboard)
/* 200 */           localXClipboard.checkChange(localXSelectionEvent);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XClipboard
 * JD-Core Version:    0.6.2
 */