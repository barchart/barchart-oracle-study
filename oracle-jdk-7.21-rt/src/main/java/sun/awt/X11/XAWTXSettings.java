/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.Toolkit;
/*     */ import java.util.Map;
/*     */ import sun.awt.XSettings;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ class XAWTXSettings extends XSettings
/*     */   implements XMSelectionListener
/*     */ {
/*  43 */   private final XAtom xSettingsPropertyAtom = XAtom.get("_XSETTINGS_SETTINGS");
/*     */ 
/*  45 */   private static PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XAWTXSettings");
/*     */   public static final long MAX_LENGTH = 1000000L;
/*     */   XMSelection settings;
/*     */ 
/*     */   public XAWTXSettings()
/*     */   {
/*  53 */     initXSettings();
/*     */   }
/*     */ 
/*     */   void initXSettings()
/*     */   {
/*  58 */     if (log.isLoggable(500)) log.fine("Initializing XAWT XSettings");
/*  59 */     this.settings = new XMSelection("_XSETTINGS");
/*  60 */     this.settings.addSelectionListener(this);
/*  61 */     initPerScreenXSettings();
/*     */   }
/*     */ 
/*     */   void dispose() {
/*  65 */     this.settings.removeSelectionListener(this);
/*     */   }
/*     */ 
/*     */   public void ownerDeath(int paramInt, XMSelection paramXMSelection, long paramLong) {
/*  69 */     if (log.isLoggable(500)) log.fine("Owner " + paramLong + " died for selection " + paramXMSelection + " screen " + paramInt);
/*     */   }
/*     */ 
/*     */   public void ownerChanged(int paramInt, XMSelection paramXMSelection, long paramLong1, long paramLong2, long paramLong3)
/*     */   {
/*  74 */     if (log.isLoggable(500)) log.fine("New Owner " + paramLong1 + " for selection = " + paramXMSelection + " screen " + paramInt); 
/*     */   }
/*     */ 
/*     */   public void selectionChanged(int paramInt, XMSelection paramXMSelection, long paramLong, XPropertyEvent paramXPropertyEvent)
/*     */   {
/*  78 */     log.fine("Selection changed on sel " + paramXMSelection + " screen = " + paramInt + " owner = " + paramLong + " event = " + paramXPropertyEvent);
/*  79 */     updateXSettings(paramInt, paramLong);
/*     */   }
/*     */ 
/*     */   void initPerScreenXSettings() {
/*  83 */     if (log.isLoggable(500)) log.fine("Updating Per XSettings changes");
/*     */ 
/*  91 */     Map localMap = null;
/*  92 */     XToolkit.awtLock();
/*     */     try {
/*  94 */       long l = XToolkit.getDisplay();
/*  95 */       int i = (int)XlibWrapper.DefaultScreen(l);
/*  96 */       localMap = getUpdatedSettings(this.settings.getOwner(i));
/*     */     } finally {
/*  98 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/* 101 */     ((XToolkit)Toolkit.getDefaultToolkit()).parseXSettings(0, localMap);
/*     */   }
/*     */ 
/*     */   private void updateXSettings(int paramInt, long paramLong) {
/* 105 */     final Map localMap = getUpdatedSettings(paramLong);
/*     */ 
/* 109 */     EventQueue.invokeLater(new Runnable() {
/*     */       public void run() {
/* 111 */         ((XToolkit)Toolkit.getDefaultToolkit()).parseXSettings(0, localMap);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private Map getUpdatedSettings(long paramLong) {
/* 117 */     if (log.isLoggable(500)) log.fine("owner =" + paramLong);
/* 118 */     if (0L == paramLong) {
/* 119 */       return null;
/*     */     }
/*     */ 
/* 122 */     Map localMap = null;
/*     */     try {
/* 124 */       WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, this.xSettingsPropertyAtom, 0L, 1000000L, false, this.xSettingsPropertyAtom.getAtom());
/*     */       try
/*     */       {
/* 128 */         int i = localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*     */ 
/* 130 */         if ((i != 0) || (localWindowPropertyGetter.getData() == 0L)) {
/* 131 */           if (log.isLoggable(500)) log.fine("OH OH : getter failed  status = " + i);
/* 132 */           localMap = null;
/*     */         }
/*     */ 
/* 135 */         long l = localWindowPropertyGetter.getData();
/*     */ 
/* 137 */         if (log.isLoggable(500)) log.fine("noItems = " + localWindowPropertyGetter.getNumberOfItems());
/* 138 */         byte[] arrayOfByte = Native.toBytes(l, localWindowPropertyGetter.getNumberOfItems());
/* 139 */         if (arrayOfByte != null)
/* 140 */           localMap = update(arrayOfByte);
/*     */       }
/*     */       finally {
/* 143 */         localWindowPropertyGetter.dispose();
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {
/* 147 */       localException.printStackTrace();
/*     */     }
/* 149 */     return localMap;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XAWTXSettings
 * JD-Core Version:    0.6.2
 */