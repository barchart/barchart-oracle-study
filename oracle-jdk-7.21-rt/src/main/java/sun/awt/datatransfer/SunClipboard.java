/*     */ package sun.awt.datatransfer;
/*     */ 
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.datatransfer.Clipboard;
/*     */ import java.awt.datatransfer.ClipboardOwner;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.FlavorEvent;
/*     */ import java.awt.datatransfer.FlavorListener;
/*     */ import java.awt.datatransfer.FlavorTable;
/*     */ import java.awt.datatransfer.SystemFlavorMap;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.EventListenerAggregate;
/*     */ import sun.awt.PeerEvent;
/*     */ import sun.awt.SunToolkit;
/*     */ 
/*     */ public abstract class SunClipboard extends Clipboard
/*     */   implements PropertyChangeListener
/*     */ {
/*  67 */   public static final FlavorTable flavorMap = (FlavorTable)SystemFlavorMap.getDefaultFlavorMap();
/*     */ 
/*  70 */   private AppContext contentsContext = null;
/*     */   private final Object CLIPBOARD_FLAVOR_LISTENER_KEY;
/*  78 */   private volatile int numberOfFlavorListeners = 0;
/*     */   private volatile Set currentDataFlavors;
/*     */ 
/*     */   public SunClipboard(String paramString)
/*     */   {
/*  89 */     super(paramString);
/*  90 */     this.CLIPBOARD_FLAVOR_LISTENER_KEY = new StringBuffer(paramString + "_CLIPBOARD_FLAVOR_LISTENER_KEY");
/*     */   }
/*     */ 
/*     */   public synchronized void setContents(Transferable paramTransferable, ClipboardOwner paramClipboardOwner)
/*     */   {
/*  97 */     if (paramTransferable == null) {
/*  98 */       throw new NullPointerException("contents");
/*     */     }
/*     */ 
/* 101 */     initContext();
/*     */ 
/* 103 */     final ClipboardOwner localClipboardOwner = this.owner;
/* 104 */     final Transferable localTransferable = this.contents;
/*     */     try
/*     */     {
/* 107 */       this.owner = paramClipboardOwner;
/* 108 */       this.contents = new TransferableProxy(paramTransferable, true);
/*     */ 
/* 110 */       setContentsNative(paramTransferable);
/*     */ 
/* 112 */       if ((localClipboardOwner != null) && (localClipboardOwner != paramClipboardOwner))
/* 113 */         EventQueue.invokeLater(new Runnable() {
/*     */           public void run() {
/* 115 */             localClipboardOwner.lostOwnership(SunClipboard.this, localTransferable);
/*     */           }
/*     */         });
/*     */     }
/*     */     finally
/*     */     {
/* 112 */       if ((localClipboardOwner != null) && (localClipboardOwner != paramClipboardOwner))
/* 113 */         EventQueue.invokeLater(new Runnable() {
/*     */           public void run() {
/* 115 */             localClipboardOwner.lostOwnership(SunClipboard.this, localTransferable);
/*     */           }
/*     */         });
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void initContext()
/*     */   {
/* 123 */     AppContext localAppContext = AppContext.getAppContext();
/*     */ 
/* 125 */     if (this.contentsContext != localAppContext)
/*     */     {
/* 128 */       synchronized (localAppContext) {
/* 129 */         if (localAppContext.isDisposed()) {
/* 130 */           throw new IllegalStateException("Can't set contents from disposed AppContext");
/*     */         }
/* 132 */         localAppContext.addPropertyChangeListener("disposed", this);
/*     */       }
/*     */ 
/* 135 */       if (this.contentsContext != null) {
/* 136 */         this.contentsContext.removePropertyChangeListener("disposed", this);
/*     */       }
/*     */ 
/* 139 */       this.contentsContext = localAppContext;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized Transferable getContents(Object paramObject) {
/* 144 */     if (this.contents != null) {
/* 145 */       return this.contents;
/*     */     }
/* 147 */     return new ClipboardTransferable(this);
/*     */   }
/*     */ 
/*     */   private synchronized Transferable getContextContents()
/*     */   {
/* 157 */     AppContext localAppContext = AppContext.getAppContext();
/* 158 */     return localAppContext == this.contentsContext ? this.contents : null;
/*     */   }
/*     */ 
/*     */   public DataFlavor[] getAvailableDataFlavors()
/*     */   {
/* 167 */     Transferable localTransferable = getContextContents();
/* 168 */     if (localTransferable != null) {
/* 169 */       return localTransferable.getTransferDataFlavors();
/*     */     }
/*     */ 
/* 172 */     long[] arrayOfLong = getClipboardFormatsOpenClose();
/*     */ 
/* 174 */     return DataTransferer.getInstance().getFlavorsForFormatsAsArray(arrayOfLong, flavorMap);
/*     */   }
/*     */ 
/*     */   public boolean isDataFlavorAvailable(DataFlavor paramDataFlavor)
/*     */   {
/* 183 */     if (paramDataFlavor == null) {
/* 184 */       throw new NullPointerException("flavor");
/*     */     }
/*     */ 
/* 187 */     Transferable localTransferable = getContextContents();
/* 188 */     if (localTransferable != null) {
/* 189 */       return localTransferable.isDataFlavorSupported(paramDataFlavor);
/*     */     }
/*     */ 
/* 192 */     long[] arrayOfLong = getClipboardFormatsOpenClose();
/*     */ 
/* 194 */     return formatArrayAsDataFlavorSet(arrayOfLong).contains(paramDataFlavor);
/*     */   }
/*     */ 
/*     */   public Object getData(DataFlavor paramDataFlavor)
/*     */     throws UnsupportedFlavorException, IOException
/*     */   {
/* 203 */     if (paramDataFlavor == null) {
/* 204 */       throw new NullPointerException("flavor");
/*     */     }
/*     */ 
/* 207 */     Transferable localTransferable1 = getContextContents();
/* 208 */     if (localTransferable1 != null) {
/* 209 */       return localTransferable1.getTransferData(paramDataFlavor);
/*     */     }
/*     */ 
/* 212 */     long l = 0L;
/* 213 */     byte[] arrayOfByte = null;
/* 214 */     Transferable localTransferable2 = null;
/*     */     try
/*     */     {
/* 217 */       openClipboard(null);
/*     */ 
/* 219 */       long[] arrayOfLong = getClipboardFormats();
/* 220 */       Long localLong = (Long)DataTransferer.getInstance().getFlavorsForFormats(arrayOfLong, flavorMap).get(paramDataFlavor);
/*     */ 
/* 223 */       if (localLong == null) {
/* 224 */         throw new UnsupportedFlavorException(paramDataFlavor);
/*     */       }
/*     */ 
/* 227 */       l = localLong.longValue();
/* 228 */       arrayOfByte = getClipboardData(l);
/*     */ 
/* 230 */       if (DataTransferer.getInstance().isLocaleDependentTextFormat(l))
/* 231 */         localTransferable2 = createLocaleTransferable(arrayOfLong);
/*     */     }
/*     */     finally
/*     */     {
/* 235 */       closeClipboard();
/*     */     }
/*     */ 
/* 238 */     return DataTransferer.getInstance().translateBytes(arrayOfByte, paramDataFlavor, l, localTransferable2);
/*     */   }
/*     */ 
/*     */   protected Transferable createLocaleTransferable(long[] paramArrayOfLong)
/*     */     throws IOException
/*     */   {
/* 248 */     return null;
/*     */   }
/*     */ 
/*     */   public void openClipboard(SunClipboard paramSunClipboard) {
/*     */   }
/*     */ 
/*     */   public void closeClipboard() {
/*     */   }
/*     */ 
/*     */   public abstract long getID();
/*     */ 
/*     */   public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/* 260 */     if (("disposed".equals(paramPropertyChangeEvent.getPropertyName())) && (Boolean.TRUE.equals(paramPropertyChangeEvent.getNewValue())))
/*     */     {
/* 262 */       AppContext localAppContext = (AppContext)paramPropertyChangeEvent.getSource();
/* 263 */       lostOwnershipLater(localAppContext);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void lostOwnershipImpl() {
/* 268 */     lostOwnershipLater(null);
/*     */   }
/*     */ 
/*     */   protected void lostOwnershipLater(final AppContext paramAppContext)
/*     */   {
/* 282 */     AppContext localAppContext = this.contentsContext;
/* 283 */     if (localAppContext == null) {
/* 284 */       return;
/*     */     }
/*     */ 
/* 287 */     Runnable local2 = new Runnable() {
/*     */       public void run() {
/* 289 */         SunClipboard localSunClipboard = SunClipboard.this;
/* 290 */         ClipboardOwner localClipboardOwner = null;
/* 291 */         Transferable localTransferable = null;
/*     */ 
/* 293 */         synchronized (localSunClipboard) {
/* 294 */           AppContext localAppContext = localSunClipboard.contentsContext;
/*     */ 
/* 296 */           if (localAppContext == null) {
/* 297 */             return;
/*     */           }
/*     */ 
/* 300 */           if ((paramAppContext == null) || (localAppContext == paramAppContext)) {
/* 301 */             localClipboardOwner = localSunClipboard.owner;
/* 302 */             localTransferable = localSunClipboard.contents;
/* 303 */             localSunClipboard.contentsContext = null;
/* 304 */             localSunClipboard.owner = null;
/* 305 */             localSunClipboard.contents = null;
/* 306 */             localSunClipboard.clearNativeContext();
/* 307 */             localAppContext.removePropertyChangeListener("disposed", localSunClipboard);
/*     */           }
/*     */           else {
/* 310 */             return;
/*     */           }
/*     */         }
/* 313 */         if (localClipboardOwner != null)
/* 314 */           localClipboardOwner.lostOwnership(localSunClipboard, localTransferable);
/*     */       }
/*     */     };
/* 319 */     SunToolkit.postEvent(localAppContext, new PeerEvent(this, local2, 1L));
/*     */   }
/*     */ 
/*     */   protected abstract void clearNativeContext();
/*     */ 
/*     */   protected abstract void setContentsNative(Transferable paramTransferable);
/*     */ 
/*     */   protected long[] getClipboardFormatsOpenClose()
/*     */   {
/*     */     try
/*     */     {
/* 332 */       openClipboard(null);
/* 333 */       return getClipboardFormats();
/*     */     } finally {
/* 335 */       closeClipboard();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract long[] getClipboardFormats();
/*     */ 
/*     */   protected abstract byte[] getClipboardData(long paramLong)
/*     */     throws IOException;
/*     */ 
/*     */   private static Set formatArrayAsDataFlavorSet(long[] paramArrayOfLong)
/*     */   {
/* 350 */     return paramArrayOfLong == null ? null : DataTransferer.getInstance().getFlavorsForFormatsAsSet(paramArrayOfLong, flavorMap);
/*     */   }
/*     */ 
/*     */   public synchronized void addFlavorListener(FlavorListener paramFlavorListener)
/*     */   {
/* 357 */     if (paramFlavorListener == null) {
/* 358 */       return;
/*     */     }
/* 360 */     AppContext localAppContext = AppContext.getAppContext();
/* 361 */     EventListenerAggregate localEventListenerAggregate = (EventListenerAggregate)localAppContext.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
/*     */ 
/* 363 */     if (localEventListenerAggregate == null) {
/* 364 */       localEventListenerAggregate = new EventListenerAggregate(FlavorListener.class);
/* 365 */       localAppContext.put(this.CLIPBOARD_FLAVOR_LISTENER_KEY, localEventListenerAggregate);
/*     */     }
/* 367 */     localEventListenerAggregate.add(paramFlavorListener);
/*     */ 
/* 369 */     if (this.numberOfFlavorListeners++ == 0) {
/* 370 */       long[] arrayOfLong = null;
/*     */       try {
/* 372 */         openClipboard(null);
/* 373 */         arrayOfLong = getClipboardFormats();
/*     */       } catch (IllegalStateException localIllegalStateException) {
/*     */       } finally {
/* 376 */         closeClipboard();
/*     */       }
/* 378 */       this.currentDataFlavors = formatArrayAsDataFlavorSet(arrayOfLong);
/*     */ 
/* 380 */       registerClipboardViewerChecked();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void removeFlavorListener(FlavorListener paramFlavorListener) {
/* 385 */     if (paramFlavorListener == null) {
/* 386 */       return;
/*     */     }
/* 388 */     AppContext localAppContext = AppContext.getAppContext();
/* 389 */     EventListenerAggregate localEventListenerAggregate = (EventListenerAggregate)localAppContext.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
/*     */ 
/* 391 */     if (localEventListenerAggregate == null)
/*     */     {
/* 393 */       return;
/*     */     }
/* 395 */     if ((localEventListenerAggregate.remove(paramFlavorListener)) && (--this.numberOfFlavorListeners == 0))
/*     */     {
/* 397 */       unregisterClipboardViewerChecked();
/* 398 */       this.currentDataFlavors = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized FlavorListener[] getFlavorListeners() {
/* 403 */     EventListenerAggregate localEventListenerAggregate = (EventListenerAggregate)AppContext.getAppContext().get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
/*     */ 
/* 405 */     return localEventListenerAggregate == null ? new FlavorListener[0] : (FlavorListener[])localEventListenerAggregate.getListenersCopy();
/*     */   }
/*     */ 
/*     */   public boolean areFlavorListenersRegistered()
/*     */   {
/* 410 */     return this.numberOfFlavorListeners > 0;
/*     */   }
/*     */ 
/*     */   protected abstract void registerClipboardViewerChecked();
/*     */ 
/*     */   protected abstract void unregisterClipboardViewerChecked();
/*     */ 
/*     */   public void checkChange(long[] paramArrayOfLong)
/*     */   {
/* 428 */     Set localSet = this.currentDataFlavors;
/* 429 */     this.currentDataFlavors = formatArrayAsDataFlavorSet(paramArrayOfLong);
/*     */ 
/* 431 */     if ((localSet != null) && (this.currentDataFlavors != null) && (localSet.equals(this.currentDataFlavors)))
/*     */     {
/* 436 */       return;
/*     */     }
/*     */ 
/* 453 */     for (Iterator localIterator = AppContext.getAppContexts().iterator(); localIterator.hasNext(); ) {
/* 454 */       AppContext localAppContext = (AppContext)localIterator.next();
/* 455 */       if ((localAppContext != null) && (!localAppContext.isDisposed()))
/*     */       {
/* 458 */         EventListenerAggregate localEventListenerAggregate = (EventListenerAggregate)localAppContext.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
/*     */ 
/* 460 */         if (localEventListenerAggregate != null) {
/* 461 */           FlavorListener[] arrayOfFlavorListener = (FlavorListener[])localEventListenerAggregate.getListenersInternal();
/*     */ 
/* 463 */           for (int i = 0; i < arrayOfFlavorListener.length; i++)
/* 464 */             SunToolkit.postEvent(localAppContext, new PeerEvent(this, new Runnable()
/*     */             {
/*     */               private final FlavorListener flavorListener;
/*     */ 
/*     */               public void run()
/*     */               {
/* 447 */                 if (this.flavorListener != null)
/* 448 */                   this.flavorListener.flavorsChanged(new FlavorEvent(SunClipboard.this));
/*     */               }
/*     */             }
/*     */             , 1L));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.datatransfer.SunClipboard
 * JD-Core Version:    0.6.2
 */