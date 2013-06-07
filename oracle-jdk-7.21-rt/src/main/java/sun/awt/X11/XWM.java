/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.Insets;
/*      */ import java.awt.Rectangle;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import sun.misc.Unsafe;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ final class XWM
/*      */ {
/*   52 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XWM");
/*   53 */   private static final PlatformLogger insLog = PlatformLogger.getLogger("sun.awt.X11.insets.XWM");
/*   54 */   private static final PlatformLogger stateLog = PlatformLogger.getLogger("sun.awt.X11.states.XWM");
/*      */ 
/*   56 */   static final XAtom XA_MWM_HINTS = new XAtom();
/*      */ 
/*   58 */   private static Unsafe unsafe = XlibWrapper.unsafe;
/*      */ 
/*   62 */   static XAtom XA_WM_STATE = new XAtom();
/*      */ 
/*   65 */   XAtom XA_UTF8_STRING = XAtom.get("UTF8_STRING");
/*      */   static final int AWT_NET_N_KNOWN_STATES = 2;
/*   71 */   static final XAtom XA_E_FRAME_SIZE = new XAtom();
/*      */ 
/*   74 */   static final XAtom XA_KDE_NET_WM_FRAME_STRUT = new XAtom();
/*      */ 
/*   77 */   static final XAtom XA_KWM_WIN_ICONIFIED = new XAtom();
/*   78 */   static final XAtom XA_KWM_WIN_MAXIMIZED = new XAtom();
/*      */ 
/*   81 */   static final XAtom XA_OL_DECOR_DEL = new XAtom();
/*   82 */   static final XAtom XA_OL_DECOR_HEADER = new XAtom();
/*   83 */   static final XAtom XA_OL_DECOR_RESIZE = new XAtom();
/*   84 */   static final XAtom XA_OL_DECOR_PIN = new XAtom();
/*   85 */   static final XAtom XA_OL_DECOR_CLOSE = new XAtom();
/*      */ 
/*   88 */   static final XAtom XA_NET_FRAME_EXTENTS = new XAtom();
/*   89 */   static final XAtom XA_NET_REQUEST_FRAME_EXTENTS = new XAtom();
/*      */   static final int UNDETERMINED_WM = 1;
/*      */   static final int NO_WM = 2;
/*      */   static final int OTHER_WM = 3;
/*      */   static final int OPENLOOK_WM = 4;
/*      */   static final int MOTIF_WM = 5;
/*      */   static final int CDE_WM = 6;
/*      */   static final int ENLIGHTEN_WM = 7;
/*      */   static final int KDE2_WM = 8;
/*      */   static final int SAWFISH_WM = 9;
/*      */   static final int ICE_WM = 10;
/*      */   static final int METACITY_WM = 11;
/*      */   static final int COMPIZ_WM = 12;
/*      */   static final int LG3D_WM = 13;
/*      */   static final int CWM_WM = 14;
/*      */   static final int MUTTER_WM = 15;
/*      */   int WMID;
/*  145 */   static final Insets zeroInsets = new Insets(0, 0, 0, 0);
/*  146 */   static final Insets defaultInsets = new Insets(25, 5, 5, 5);
/*      */ 
/*  174 */   static XNETProtocol g_net_protocol = null;
/*  175 */   static XWINProtocol g_win_protocol = null;
/*      */ 
/*  313 */   static XAtom XA_ENLIGHTENMENT_COMMS = new XAtom("ENLIGHTENMENT_COMMS", false);
/*      */ 
/*  405 */   static final XAtom XA_DT_SM_WINDOW_INFO = new XAtom("_DT_SM_WINDOW_INFO", false);
/*  406 */   static final XAtom XA_DT_SM_STATE_INFO = new XAtom("_DT_SM_STATE_INFO", false);
/*      */ 
/*  478 */   static final XAtom XA_MOTIF_WM_INFO = new XAtom("_MOTIF_WM_INFO", false);
/*  479 */   static final XAtom XA_DT_WORKSPACE_CURRENT = new XAtom("_DT_WORKSPACE_CURRENT", false);
/*      */ 
/*  600 */   static final XAtom XA_ICEWM_WINOPTHINT = new XAtom("_ICEWM_WINOPTHINT", false);
/*  601 */   static final char[] opt = { 'A', 'W', 'T', '_', 'I', 'C', 'E', 'W', 'M', '_', 'T', 'E', 'S', 'T', '\000', 'a', 'l', 'l', 'W', 'o', 'r', 'k', 's', 'p', 'a', 'c', 'e', 's', '\000', '0', '\000' };
/*      */ 
/*  670 */   static final XAtom XA_SUN_WM_PROTOCOLS = new XAtom("_SUN_WM_PROTOCOLS", false);
/*      */ 
/*  684 */   private static boolean winmgr_running = false;
/*  685 */   private static XErrorHandler detectWMHandler = new XErrorHandler.XBaseErrorHandler()
/*      */   {
/*      */     public int handleError(long paramAnonymousLong, XErrorEvent paramAnonymousXErrorEvent) {
/*  688 */       if ((paramAnonymousXErrorEvent.get_request_code() == 2) && (paramAnonymousXErrorEvent.get_error_code() == 10))
/*      */       {
/*  691 */         XWM.access$002(true);
/*  692 */         return 0;
/*      */       }
/*  694 */       return super.handleError(paramAnonymousLong, paramAnonymousXErrorEvent);
/*      */     }
/*  685 */   };
/*      */ 
/*  702 */   static int awt_wmgr = 1;
/*      */   static XWM wm;
/* 1036 */   private HashMap<Class<?>, Collection<?>> protocolsMap = new HashMap();
/*      */ 
/* 1246 */   static boolean inited = false;
/*      */ 
/* 1285 */   HashMap storedInsets = new HashMap();
/*      */ 
/* 1319 */   static int awtWMStaticGravity = -1;
/*      */ 
/*      */   public String toString()
/*      */   {
/*  108 */     switch (this.WMID) {
/*      */     case 2:
/*  110 */       return "NO WM";
/*      */     case 3:
/*  112 */       return "Other WM";
/*      */     case 4:
/*  114 */       return "OPENLOOK";
/*      */     case 5:
/*  116 */       return "MWM";
/*      */     case 6:
/*  118 */       return "DTWM";
/*      */     case 7:
/*  120 */       return "Enlightenment";
/*      */     case 8:
/*  122 */       return "KWM2";
/*      */     case 9:
/*  124 */       return "Sawfish";
/*      */     case 10:
/*  126 */       return "IceWM";
/*      */     case 11:
/*  128 */       return "Metacity";
/*      */     case 12:
/*  130 */       return "Compiz";
/*      */     case 13:
/*  132 */       return "LookingGlass";
/*      */     case 14:
/*  134 */       return "CWM";
/*      */     case 15:
/*  136 */       return "Mutter";
/*      */     case 1:
/*      */     }
/*  139 */     return "Undetermined WM";
/*      */   }
/*      */ 
/*      */   XWM(int paramInt)
/*      */   {
/*  149 */     this.WMID = paramInt;
/*  150 */     initializeProtocols();
/*  151 */     if (log.isLoggable(500)) log.fine("Window manager: " + toString()); 
/*      */   }
/*      */ 
/*  154 */   int getID() { return this.WMID; }
/*      */ 
/*      */ 
/*      */   static Insets normalize(Insets paramInsets)
/*      */   {
/*  159 */     if ((paramInsets.top > 64) || (paramInsets.top < 0)) {
/*  160 */       paramInsets.top = 28;
/*      */     }
/*  162 */     if ((paramInsets.left > 32) || (paramInsets.left < 0)) {
/*  163 */       paramInsets.left = 6;
/*      */     }
/*  165 */     if ((paramInsets.right > 32) || (paramInsets.right < 0)) {
/*  166 */       paramInsets.right = 6;
/*      */     }
/*  168 */     if ((paramInsets.bottom > 32) || (paramInsets.bottom < 0)) {
/*  169 */       paramInsets.bottom = 6;
/*      */     }
/*  171 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   static boolean isNetWMName(String paramString)
/*      */   {
/*  177 */     if (g_net_protocol != null) {
/*  178 */       return g_net_protocol.isWMName(paramString);
/*      */     }
/*  180 */     return false;
/*      */   }
/*      */ 
/*      */   static void initAtoms()
/*      */   {
/*  185 */     Object[][] arrayOfObject; = { { XA_WM_STATE, "WM_STATE" }, { XA_KDE_NET_WM_FRAME_STRUT, "_KDE_NET_WM_FRAME_STRUT" }, { XA_E_FRAME_SIZE, "_E_FRAME_SIZE" }, { XA_KWM_WIN_ICONIFIED, "KWM_WIN_ICONIFIED" }, { XA_KWM_WIN_MAXIMIZED, "KWM_WIN_MAXIMIZED" }, { XA_OL_DECOR_DEL, "_OL_DECOR_DEL" }, { XA_OL_DECOR_HEADER, "_OL_DECOR_HEADER" }, { XA_OL_DECOR_RESIZE, "_OL_DECOR_RESIZE" }, { XA_OL_DECOR_PIN, "_OL_DECOR_PIN" }, { XA_OL_DECOR_CLOSE, "_OL_DECOR_CLOSE" }, { XA_MWM_HINTS, "_MOTIF_WM_HINTS" }, { XA_NET_FRAME_EXTENTS, "_NET_FRAME_EXTENTS" }, { XA_NET_REQUEST_FRAME_EXTENTS, "_NET_REQUEST_FRAME_EXTENTS" } };
/*      */ 
/*  205 */     String[] arrayOfString = new String[arrayOfObject;.length];
/*  206 */     for (int i = 0; i < arrayOfString.length; i++) {
/*  207 */       arrayOfString[i] = ((String)arrayOfObject;[i][1]);
/*      */     }
/*      */ 
/*  210 */     i = XAtom.getAtomSize();
/*  211 */     long l = unsafe.allocateMemory(arrayOfString.length * i);
/*  212 */     XToolkit.awtLock();
/*      */     try {
/*  214 */       int j = XlibWrapper.XInternAtoms(XToolkit.getDisplay(), arrayOfString, false, l);
/*  215 */       if (j == 0) {
/*      */         return;
/*      */       }
/*  218 */       int k = 0; for (int m = 0; k < arrayOfString.length; m += i) {
/*  219 */         ((XAtom)arrayOfObject;[k][0]).setValues(XToolkit.getDisplay(), arrayOfString[k], XAtom.getAtom(l + m));
/*      */ 
/*  218 */         k++;
/*      */       }
/*      */     }
/*      */     finally {
/*  222 */       XToolkit.awtUnlock();
/*  223 */       unsafe.freeMemory(l);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static boolean isNoWM()
/*      */   {
/*  248 */     String str1 = XlibWrapper.ServerVendor(XToolkit.getDisplay());
/*  249 */     if (str1.indexOf("eXcursion") != -1)
/*      */     {
/*  255 */       if (insLog.isLoggable(500)) {
/*  256 */         insLog.finer("eXcursion means NO_WM");
/*      */       }
/*  258 */       return true;
/*      */     }
/*      */ 
/*  261 */     XSetWindowAttributes localXSetWindowAttributes = new XSetWindowAttributes();
/*      */     try
/*      */     {
/*  266 */       long l1 = XlibWrapper.DefaultScreen(XToolkit.getDisplay());
/*      */ 
/*  268 */       String str2 = "WM_S" + l1;
/*      */ 
/*  270 */       long l2 = XlibWrapper.XGetSelectionOwner(XToolkit.getDisplay(), XAtom.get(str2).getAtom());
/*      */ 
/*  273 */       if (insLog.isLoggable(500))
/*  274 */         insLog.finer("selection owner of " + str2 + " is " + l2);
/*      */       boolean bool;
/*  278 */       if (l2 != 0L) {
/*  279 */         return false;
/*      */       }
/*      */ 
/*  282 */       winmgr_running = false;
/*  283 */       localXSetWindowAttributes.set_event_mask(1048576L);
/*      */ 
/*  285 */       XToolkit.WITH_XERROR_HANDLER(detectWMHandler);
/*  286 */       XlibWrapper.XChangeWindowAttributes(XToolkit.getDisplay(), XToolkit.getDefaultRootWindow(), 2048L, localXSetWindowAttributes.pData);
/*      */ 
/*  290 */       XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  296 */       if (!winmgr_running) {
/*  297 */         localXSetWindowAttributes.set_event_mask(0L);
/*  298 */         XlibWrapper.XChangeWindowAttributes(XToolkit.getDisplay(), XToolkit.getDefaultRootWindow(), 2048L, localXSetWindowAttributes.pData);
/*      */ 
/*  302 */         if (insLog.isLoggable(500)) {
/*  303 */           insLog.finer("It looks like there is no WM thus NO_WM");
/*      */         }
/*      */       }
/*      */ 
/*  307 */       return !winmgr_running;
/*      */     } finally {
/*  309 */       localXSetWindowAttributes.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   static long getECommsWindowIDProperty(long paramLong)
/*      */   {
/*  322 */     if (!XA_ENLIGHTENMENT_COMMS.isInterned()) {
/*  323 */       return 0L;
/*      */     }
/*      */ 
/*  326 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, XA_ENLIGHTENMENT_COMMS, 0L, 14L, false, 31L);
/*      */     try
/*      */     {
/*  330 */       int i = localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */       long l1;
/*  331 */       if ((i != 0) || (localWindowPropertyGetter.getData() == 0L)) {
/*  332 */         return 0L;
/*      */       }
/*      */ 
/*  335 */       if ((localWindowPropertyGetter.getActualType() != 31L) || (localWindowPropertyGetter.getActualFormat() != 8) || (localWindowPropertyGetter.getNumberOfItems() != 14) || (localWindowPropertyGetter.getBytesAfter() != 0L))
/*      */       {
/*  339 */         return 0L;
/*      */       }
/*      */ 
/*  343 */       byte[] arrayOfByte = XlibWrapper.getStringBytes(localWindowPropertyGetter.getData());
/*  344 */       String str1 = new String(arrayOfByte);
/*      */ 
/*  346 */       log.finer("ENLIGHTENMENT_COMMS is " + str1);
/*      */ 
/*  349 */       Pattern localPattern = Pattern.compile("WINID\\s+(\\p{XDigit}{0,8})");
/*      */       try {
/*  351 */         Matcher localMatcher = localPattern.matcher(str1);
/*  352 */         if (localMatcher.matches()) {
/*  353 */           log.finest("Match group count: " + localMatcher.groupCount());
/*  354 */           String str2 = localMatcher.group(1);
/*  355 */           log.finest("Match group 1 " + str2);
/*  356 */           long l3 = Long.parseLong(str2, 16);
/*  357 */           log.finer("Enlightenment communication window " + l3);
/*  358 */           return l3;
/*      */         }
/*  360 */         log.finer("ENLIGHTENMENT_COMMS has wrong format");
/*  361 */         return 0L;
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*      */         long l2;
/*  364 */         if (log.isLoggable(400)) {
/*  365 */           localException.printStackTrace();
/*      */         }
/*  367 */         return 0L;
/*      */       }
/*      */     } finally {
/*  370 */       localWindowPropertyGetter.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   static boolean isEnlightenment()
/*      */   {
/*  380 */     long l1 = getECommsWindowIDProperty(XToolkit.getDefaultRootWindow());
/*  381 */     if (l1 == 0L) {
/*  382 */       return false;
/*      */     }
/*      */ 
/*  385 */     long l2 = getECommsWindowIDProperty(l1);
/*  386 */     if (l2 != l1) {
/*  387 */       return false;
/*      */     }
/*      */ 
/*  390 */     return true;
/*      */   }
/*      */ 
/*      */   static boolean isCDE()
/*      */   {
/*  409 */     if (!XA_DT_SM_WINDOW_INFO.isInterned()) {
/*  410 */       log.finer("{0} is not interned", new Object[] { XA_DT_SM_WINDOW_INFO });
/*  411 */       return false;
/*      */     }
/*      */ 
/*  414 */     WindowPropertyGetter localWindowPropertyGetter1 = new WindowPropertyGetter(XToolkit.getDefaultRootWindow(), XA_DT_SM_WINDOW_INFO, 0L, 2L, false, XA_DT_SM_WINDOW_INFO);
/*      */     try
/*      */     {
/*  419 */       int i = localWindowPropertyGetter1.execute();
/*      */       boolean bool1;
/*  420 */       if ((i != 0) || (localWindowPropertyGetter1.getData() == 0L)) {
/*  421 */         log.finer("Getting of _DT_SM_WINDOW_INFO is not successfull");
/*  422 */         return false;
/*      */       }
/*  424 */       if ((localWindowPropertyGetter1.getActualType() != XA_DT_SM_WINDOW_INFO.getAtom()) || (localWindowPropertyGetter1.getActualFormat() != 32) || (localWindowPropertyGetter1.getNumberOfItems() != 2) || (localWindowPropertyGetter1.getBytesAfter() != 0L))
/*      */       {
/*  428 */         log.finer("Wrong format of _DT_SM_WINDOW_INFO");
/*  429 */         return false;
/*      */       }
/*      */ 
/*  432 */       long l = Native.getWindow(localWindowPropertyGetter1.getData(), 1);
/*      */       boolean bool2;
/*  434 */       if (l == 0L) {
/*  435 */         log.fine("WARNING: DT_SM_WINDOW_INFO exists but returns zero windows");
/*  436 */         return false;
/*      */       }
/*      */ 
/*  440 */       if (!XA_DT_SM_STATE_INFO.isInterned()) {
/*  441 */         log.finer("{0} is not interned", new Object[] { XA_DT_SM_STATE_INFO });
/*  442 */         return false;
/*      */       }
/*  444 */       WindowPropertyGetter localWindowPropertyGetter2 = new WindowPropertyGetter(l, XA_DT_SM_STATE_INFO, 0L, 1L, false, XA_DT_SM_STATE_INFO);
/*      */       try
/*      */       {
/*  448 */         i = localWindowPropertyGetter2.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */         boolean bool3;
/*  451 */         if ((i != 0) || (localWindowPropertyGetter2.getData() == 0L)) {
/*  452 */           log.finer("Getting of _DT_SM_STATE_INFO is not successfull");
/*  453 */           return false;
/*      */         }
/*  455 */         if ((localWindowPropertyGetter2.getActualType() != XA_DT_SM_STATE_INFO.getAtom()) || (localWindowPropertyGetter2.getActualFormat() != 32))
/*      */         {
/*  458 */           log.finer("Wrong format of _DT_SM_STATE_INFO");
/*  459 */           return false;
/*      */         }
/*      */ 
/*  462 */         return true;
/*      */       } finally {
/*      */       }
/*      */     }
/*      */     finally {
/*  467 */       localWindowPropertyGetter1.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   static boolean isMotif()
/*      */   {
/*  482 */     if (!XA_MOTIF_WM_INFO.isInterned()) {
/*  483 */       return false;
/*      */     }
/*      */ 
/*  486 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(XToolkit.getDefaultRootWindow(), XA_MOTIF_WM_INFO, 0L, 2L, false, XA_MOTIF_WM_INFO);
/*      */     try
/*      */     {
/*  492 */       int i = localWindowPropertyGetter.execute();
/*      */       boolean bool1;
/*  494 */       if ((i != 0) || (localWindowPropertyGetter.getData() == 0L)) {
/*  495 */         return false;
/*      */       }
/*      */ 
/*  498 */       if ((localWindowPropertyGetter.getActualType() != XA_MOTIF_WM_INFO.getAtom()) || (localWindowPropertyGetter.getActualFormat() != 32) || (localWindowPropertyGetter.getNumberOfItems() != 2) || (localWindowPropertyGetter.getBytesAfter() != 0L))
/*      */       {
/*  503 */         return false;
/*      */       }
/*      */ 
/*  506 */       long l = Native.getLong(localWindowPropertyGetter.getData(), 1);
/*  507 */       if (l != 0L)
/*      */       {
/*      */         boolean bool2;
/*  508 */         if (XA_DT_WORKSPACE_CURRENT.isInterned())
/*      */         {
/*  510 */           localObject1 = XA_DT_WORKSPACE_CURRENT.getAtomListProperty(l);
/*  511 */           if (localObject1.length == 0) {
/*  512 */             return false;
/*      */           }
/*  514 */           return true;
/*      */         }
/*      */ 
/*  519 */         Object localObject1 = new WindowPropertyGetter(l, XA_WM_STATE, 0L, 1L, false, XA_WM_STATE);
/*      */         try
/*      */         {
/*  525 */           if ((((WindowPropertyGetter)localObject1).execute() == 0) && (((WindowPropertyGetter)localObject1).getData() != 0L) && (((WindowPropertyGetter)localObject1).getActualType() == XA_WM_STATE.getAtom()))
/*      */           {
/*  529 */             bool2 = true;
/*      */ 
/*  532 */             ((WindowPropertyGetter)localObject1).dispose();
/*      */ 
/*  537 */             return bool2;
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  532 */           ((WindowPropertyGetter)localObject1).dispose();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  537 */       localWindowPropertyGetter.dispose();
/*      */     }
/*  539 */     return false;
/*      */   }
/*      */ 
/*      */   static boolean isSawfish()
/*      */   {
/*  546 */     return isNetWMName("Sawfish");
/*      */   }
/*      */ 
/*      */   static boolean isKDE2()
/*      */   {
/*  553 */     return isNetWMName("KWin");
/*      */   }
/*      */ 
/*      */   static boolean isCompiz() {
/*  557 */     return isNetWMName("compiz");
/*      */   }
/*      */ 
/*      */   static boolean isLookingGlass() {
/*  561 */     return isNetWMName("LG3D");
/*      */   }
/*      */ 
/*      */   static boolean isCWM() {
/*  565 */     return isNetWMName("CWM");
/*      */   }
/*      */ 
/*      */   static boolean isMetacity()
/*      */   {
/*  572 */     return isNetWMName("Metacity");
/*      */   }
/*      */ 
/*      */   static boolean isMutter()
/*      */   {
/*  580 */     return isNetWMName("Mutter");
/*      */   }
/*      */ 
/*      */   static boolean isNonReparentingWM() {
/*  584 */     return (getWMID() == 12) || (getWMID() == 13) || (getWMID() == 14);
/*      */   }
/*      */ 
/*      */   static boolean prepareIsIceWM()
/*      */   {
/*  612 */     if (!XA_ICEWM_WINOPTHINT.isInterned()) {
/*  613 */       log.finer("{0} is not interned", new Object[] { XA_ICEWM_WINOPTHINT });
/*  614 */       return false;
/*      */     }
/*      */ 
/*  617 */     XToolkit.awtLock();
/*      */     try {
/*  619 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/*  620 */       XlibWrapper.XChangePropertyS(XToolkit.getDisplay(), XToolkit.getDefaultRootWindow(), XA_ICEWM_WINOPTHINT.getAtom(), XA_ICEWM_WINOPTHINT.getAtom(), 8, 0, new String(opt));
/*      */ 
/*  625 */       XToolkit.RESTORE_XERROR_HANDLER();
/*      */       boolean bool;
/*  627 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0)) {
/*  628 */         log.finer("Erorr getting XA_ICEWM_WINOPTHINT property");
/*  629 */         return false;
/*      */       }
/*  631 */       log.finer("Prepared for IceWM detection");
/*  632 */       return true;
/*      */     } finally {
/*  634 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static boolean isIceWM()
/*      */   {
/*  645 */     if (!XA_ICEWM_WINOPTHINT.isInterned()) {
/*  646 */       log.finer("{0} is not interned", new Object[] { XA_ICEWM_WINOPTHINT });
/*  647 */       return false;
/*      */     }
/*      */ 
/*  650 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(XToolkit.getDefaultRootWindow(), XA_ICEWM_WINOPTHINT, 0L, 65535L, true, XA_ICEWM_WINOPTHINT);
/*      */     try
/*      */     {
/*  655 */       int i = localWindowPropertyGetter.execute();
/*  656 */       int j = (i == 0) && (localWindowPropertyGetter.getActualType() != 0L) ? 1 : 0;
/*  657 */       log.finer("Status getting XA_ICEWM_WINOPTHINT: " + (j == 0));
/*  658 */       return (j == 0) || (isNetWMName("IceWM"));
/*      */     } finally {
/*  660 */       localWindowPropertyGetter.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   static boolean isOpenLook()
/*      */   {
/*  672 */     if (!XA_SUN_WM_PROTOCOLS.isInterned()) {
/*  673 */       return false;
/*      */     }
/*      */ 
/*  676 */     XAtom[] arrayOfXAtom = XA_SUN_WM_PROTOCOLS.getAtomListProperty(XToolkit.getDefaultRootWindow());
/*  677 */     return arrayOfXAtom.length != 0;
/*      */   }
/*      */ 
/*      */   static XWM getWM()
/*      */   {
/*  705 */     if (wm == null) {
/*  706 */       wm = new XWM(XWM.awt_wmgr = getWMID());
/*      */     }
/*  708 */     return wm;
/*      */   }
/*      */   static int getWMID() {
/*  711 */     if (insLog.isLoggable(300)) {
/*  712 */       insLog.finest("awt_wmgr = " + awt_wmgr);
/*      */     }
/*      */ 
/*  719 */     if (awt_wmgr != 1) {
/*  720 */       return awt_wmgr;
/*      */     }
/*      */ 
/*  723 */     XSetWindowAttributes localXSetWindowAttributes = new XSetWindowAttributes();
/*  724 */     XToolkit.awtLock();
/*      */     try {
/*  726 */       if (isNoWM()) {
/*  727 */         awt_wmgr = 2;
/*  728 */         return awt_wmgr;
/*      */       }
/*      */ 
/*  733 */       XNETProtocol localXNETProtocol = XWM.g_net_protocol = new XNETProtocol();
/*  734 */       localXNETProtocol.detect();
/*  735 */       if ((log.isLoggable(500)) && (localXNETProtocol.active())) {
/*  736 */         log.fine("_NET_WM_NAME is " + localXNETProtocol.getWMName());
/*      */       }
/*  738 */       XWINProtocol localXWINProtocol = XWM.g_win_protocol = new XWINProtocol();
/*  739 */       localXWINProtocol.detect();
/*      */ 
/*  742 */       boolean bool = prepareIsIceWM();
/*      */ 
/*  748 */       if (isEnlightenment())
/*  749 */         awt_wmgr = 7;
/*  750 */       else if (isMetacity())
/*  751 */         awt_wmgr = 11;
/*  752 */       else if (isMutter())
/*  753 */         awt_wmgr = 15;
/*  754 */       else if (isSawfish())
/*  755 */         awt_wmgr = 9;
/*  756 */       else if (isKDE2())
/*  757 */         awt_wmgr = 8;
/*  758 */       else if (isCompiz())
/*  759 */         awt_wmgr = 12;
/*  760 */       else if (isLookingGlass())
/*  761 */         awt_wmgr = 13;
/*  762 */       else if (isCWM())
/*  763 */         awt_wmgr = 14;
/*  764 */       else if ((bool) && (isIceWM())) {
/*  765 */         awt_wmgr = 10;
/*      */       }
/*  771 */       else if (localXNETProtocol.active())
/*  772 */         awt_wmgr = 3;
/*  773 */       else if (localXWINProtocol.active()) {
/*  774 */         awt_wmgr = 3;
/*      */       }
/*  779 */       else if (isCDE())
/*  780 */         awt_wmgr = 6;
/*  781 */       else if (isMotif())
/*  782 */         awt_wmgr = 5;
/*  783 */       else if (isOpenLook())
/*  784 */         awt_wmgr = 4;
/*      */       else {
/*  786 */         awt_wmgr = 3;
/*      */       }
/*      */ 
/*  789 */       return awt_wmgr;
/*      */     } finally {
/*  791 */       XToolkit.awtUnlock();
/*  792 */       localXSetWindowAttributes.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   static void removeSizeHints(XDecoratedPeer paramXDecoratedPeer, long paramLong)
/*      */   {
/*  809 */     paramLong &= 48L;
/*      */ 
/*  811 */     XToolkit.awtLock();
/*      */     try {
/*  813 */       XSizeHints localXSizeHints = paramXDecoratedPeer.getHints();
/*  814 */       if ((localXSizeHints.get_flags() & paramLong) == 0L)
/*      */       {
/*      */         return;
/*      */       }
/*  818 */       localXSizeHints.set_flags(localXSizeHints.get_flags() & (paramLong ^ 0xFFFFFFFF));
/*  819 */       if (insLog.isLoggable(400)) insLog.finer("Setting hints, flags " + XlibWrapper.hintsToString(localXSizeHints.get_flags()));
/*  820 */       XlibWrapper.XSetWMNormalHints(XToolkit.getDisplay(), paramXDecoratedPeer.getWindow(), localXSizeHints.pData);
/*      */     }
/*      */     finally
/*      */     {
/*  824 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static int normalizeMotifDecor(int paramInt)
/*      */   {
/*  835 */     if ((paramInt & 0x1) == 0) {
/*  836 */       return paramInt;
/*      */     }
/*  838 */     int i = 126;
/*      */ 
/*  842 */     i &= (paramInt ^ 0xFFFFFFFF);
/*  843 */     return i;
/*      */   }
/*      */ 
/*      */   static int normalizeMotifFunc(int paramInt)
/*      */   {
/*  853 */     if ((paramInt & 0x1) == 0) {
/*  854 */       return paramInt;
/*      */     }
/*  856 */     int i = 62;
/*      */ 
/*  861 */     i &= (paramInt ^ 0xFFFFFFFF);
/*  862 */     return i;
/*      */   }
/*      */ 
/*      */   static void setOLDecor(XWindow paramXWindow, boolean paramBoolean, int paramInt)
/*      */   {
/*  870 */     if (paramXWindow == null) {
/*  871 */       return;
/*      */     }
/*      */ 
/*  874 */     XAtomList localXAtomList = new XAtomList();
/*  875 */     paramInt = normalizeMotifDecor(paramInt);
/*  876 */     if (insLog.isLoggable(400)) insLog.finer("Setting OL_DECOR to " + Integer.toBinaryString(paramInt));
/*  877 */     if ((paramInt & 0x8) == 0) {
/*  878 */       localXAtomList.add(XA_OL_DECOR_HEADER);
/*      */     }
/*  880 */     if ((paramInt & 0x44) == 0) {
/*  881 */       localXAtomList.add(XA_OL_DECOR_RESIZE);
/*      */     }
/*  883 */     if ((paramInt & 0x70) == 0)
/*      */     {
/*  887 */       localXAtomList.add(XA_OL_DECOR_CLOSE);
/*      */     }
/*  889 */     if (localXAtomList.size() == 0) {
/*  890 */       insLog.finer("Deleting OL_DECOR");
/*  891 */       XA_OL_DECOR_DEL.DeleteProperty(paramXWindow);
/*      */     } else {
/*  893 */       if (insLog.isLoggable(400)) insLog.finer("Setting OL_DECOR to " + localXAtomList);
/*  894 */       XA_OL_DECOR_DEL.setAtomListProperty(paramXWindow, localXAtomList);
/*      */     }
/*      */   }
/*      */ 
/*      */   static void setMotifDecor(XWindow paramXWindow, boolean paramBoolean, int paramInt1, int paramInt2)
/*      */   {
/*  903 */     if (((paramInt1 & 0x1) != 0) && (paramInt1 != 1))
/*      */     {
/*  906 */       paramInt1 = normalizeMotifDecor(paramInt1);
/*      */     }
/*  908 */     if (((paramInt2 & 0x1) != 0) && (paramInt2 != 1))
/*      */     {
/*  911 */       paramInt2 = normalizeMotifFunc(paramInt2);
/*      */     }
/*      */ 
/*  914 */     PropMwmHints localPropMwmHints = paramXWindow.getMWMHints();
/*  915 */     localPropMwmHints.set_flags(localPropMwmHints.get_flags() | 1L | 0x2);
/*      */ 
/*  918 */     localPropMwmHints.set_functions(paramInt2);
/*  919 */     localPropMwmHints.set_decorations(paramInt1);
/*      */ 
/*  921 */     if (stateLog.isLoggable(400)) stateLog.finer("Setting MWM_HINTS to " + localPropMwmHints);
/*  922 */     paramXWindow.setMWMHints(localPropMwmHints);
/*      */   }
/*      */ 
/*      */   static boolean needRemap(XDecoratedPeer paramXDecoratedPeer)
/*      */   {
/*  944 */     return !paramXDecoratedPeer.isEmbedded();
/*      */   }
/*      */ 
/*      */   static void setShellDecor(XDecoratedPeer paramXDecoratedPeer)
/*      */   {
/*  952 */     int i = paramXDecoratedPeer.getDecorations();
/*  953 */     int j = paramXDecoratedPeer.getFunctions();
/*  954 */     boolean bool = paramXDecoratedPeer.isResizable();
/*      */ 
/*  956 */     if (!bool) {
/*  957 */       if ((i & 0x1) != 0)
/*  958 */         i |= 68;
/*      */       else {
/*  960 */         i &= -69;
/*      */       }
/*      */     }
/*  963 */     setMotifDecor(paramXDecoratedPeer, bool, i, j);
/*  964 */     setOLDecor(paramXDecoratedPeer, bool, i);
/*      */ 
/*  967 */     if ((paramXDecoratedPeer.isShowing()) && (needRemap(paramXDecoratedPeer)))
/*      */     {
/*  973 */       paramXDecoratedPeer.xSetVisible(false);
/*  974 */       XToolkit.XSync();
/*  975 */       paramXDecoratedPeer.xSetVisible(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   static void setShellResizable(XDecoratedPeer paramXDecoratedPeer)
/*      */   {
/*  983 */     if (insLog.isLoggable(500)) insLog.fine("Setting shell resizable " + paramXDecoratedPeer);
/*  984 */     XToolkit.awtLock();
/*      */     try {
/*  986 */       Rectangle localRectangle = paramXDecoratedPeer.getShellBounds();
/*  987 */       localRectangle.translate(-paramXDecoratedPeer.currentInsets.left, -paramXDecoratedPeer.currentInsets.top);
/*  988 */       paramXDecoratedPeer.updateSizeHints(paramXDecoratedPeer.getDimensions());
/*  989 */       requestWMExtents(paramXDecoratedPeer.getWindow());
/*  990 */       XlibWrapper.XMoveResizeWindow(XToolkit.getDisplay(), paramXDecoratedPeer.getShell(), localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */ 
/*  995 */       removeSizeHints(paramXDecoratedPeer, 32L);
/*  996 */       paramXDecoratedPeer.updateMinimumSize();
/*      */ 
/*  999 */       setShellDecor(paramXDecoratedPeer);
/*      */     } finally {
/* 1001 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static void setShellNotResizable(XDecoratedPeer paramXDecoratedPeer, WindowDimensions paramWindowDimensions, Rectangle paramRectangle, boolean paramBoolean)
/*      */   {
/* 1013 */     if (insLog.isLoggable(500)) insLog.fine("Setting non-resizable shell " + paramXDecoratedPeer + ", dimensions " + paramWindowDimensions + ", shellBounds " + paramRectangle + ", just change size: " + paramBoolean);
/*      */ 
/* 1015 */     XToolkit.awtLock();
/*      */     try
/*      */     {
/* 1018 */       if (!paramRectangle.isEmpty()) {
/* 1019 */         paramXDecoratedPeer.updateSizeHints(paramWindowDimensions);
/* 1020 */         requestWMExtents(paramXDecoratedPeer.getWindow());
/* 1021 */         XToolkit.XSync();
/* 1022 */         XlibWrapper.XMoveResizeWindow(XToolkit.getDisplay(), paramXDecoratedPeer.getShell(), paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */       }
/*      */ 
/* 1025 */       if (!paramBoolean)
/* 1026 */         setShellDecor(paramXDecoratedPeer);
/*      */     }
/*      */     finally {
/* 1029 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   <T> Collection<T> getProtocols(Class<T> paramClass)
/*      */   {
/* 1041 */     Collection localCollection = (Collection)this.protocolsMap.get(paramClass);
/* 1042 */     if (localCollection != null) {
/* 1043 */       return localCollection;
/*      */     }
/* 1045 */     return new LinkedList();
/*      */   }
/*      */ 
/*      */   private <T> void addProtocol(Class<T> paramClass, T paramT)
/*      */   {
/* 1050 */     Collection localCollection = getProtocols(paramClass);
/* 1051 */     localCollection.add(paramT);
/* 1052 */     this.protocolsMap.put(paramClass, localCollection);
/*      */   }
/*      */ 
/*      */   boolean supportsDynamicLayout() {
/* 1056 */     int i = getWMID();
/* 1057 */     switch (i) {
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/* 1063 */       return true;
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/* 1067 */       return false;
/*      */     }
/* 1069 */     return false;
/*      */   }
/*      */ 
/*      */   boolean supportsExtendedState(int paramInt)
/*      */   {
/* 1082 */     switch (paramInt)
/*      */     {
/*      */     case 2:
/*      */     case 4:
/* 1089 */       if (getWMID() == 11)
/*      */       {
/* 1091 */         return false;
/*      */       }
/*      */ 
/*      */     case 6:
/* 1095 */       for (XStateProtocol localXStateProtocol : getProtocols(XStateProtocol.class))
/* 1096 */         if (localXStateProtocol.supportsState(paramInt))
/* 1097 */           return true;
/*      */     case 3:
/*      */     case 5:
/*      */     }
/* 1101 */     return false;
/*      */   }
/*      */ 
/*      */   int getExtendedState(XWindowPeer paramXWindowPeer)
/*      */   {
/* 1113 */     int i = 0;
/* 1114 */     for (XStateProtocol localXStateProtocol : getProtocols(XStateProtocol.class)) {
/* 1115 */       i |= localXStateProtocol.getState(paramXWindowPeer);
/*      */     }
/* 1117 */     if (i != 0) {
/* 1118 */       return i;
/*      */     }
/* 1120 */     return 0;
/*      */   }
/*      */ 
/*      */   boolean isStateChange(XDecoratedPeer paramXDecoratedPeer, XPropertyEvent paramXPropertyEvent)
/*      */   {
/* 1135 */     if (!paramXDecoratedPeer.isShowing()) {
/* 1136 */       stateLog.finer("Window is not showing");
/* 1137 */       return false;
/*      */     }
/*      */ 
/* 1140 */     int i = paramXDecoratedPeer.getWMState();
/* 1141 */     if (i == 0) {
/* 1142 */       stateLog.finer("WithdrawnState");
/* 1143 */       return false;
/*      */     }
/* 1145 */     stateLog.finer("Window WM_STATE is " + i);
/*      */ 
/* 1147 */     boolean bool = false;
/* 1148 */     if (paramXPropertyEvent.get_atom() == XA_WM_STATE.getAtom()) {
/* 1149 */       bool = true;
/*      */     }
/*      */ 
/* 1152 */     for (XStateProtocol localXStateProtocol : getProtocols(XStateProtocol.class)) {
/* 1153 */       bool |= localXStateProtocol.isStateChange(paramXPropertyEvent);
/* 1154 */       stateLog.finest(localXStateProtocol + ": is state changed = " + bool);
/*      */     }
/* 1156 */     return bool;
/*      */   }
/*      */ 
/*      */   int getState(XDecoratedPeer paramXDecoratedPeer)
/*      */   {
/* 1163 */     int i = 0;
/* 1164 */     int j = paramXDecoratedPeer.getWMState();
/* 1165 */     if (j == 3)
/* 1166 */       i = 1;
/*      */     else {
/* 1168 */       i = 0;
/*      */     }
/* 1170 */     i |= getExtendedState(paramXDecoratedPeer);
/* 1171 */     return i;
/*      */   }
/*      */ 
/*      */   void setLayer(XWindowPeer paramXWindowPeer, int paramInt)
/*      */   {
/* 1185 */     for (XLayerProtocol localXLayerProtocol : getProtocols(XLayerProtocol.class)) {
/* 1186 */       if (localXLayerProtocol.supportsLayer(paramInt)) {
/* 1187 */         localXLayerProtocol.setLayer(paramXWindowPeer, paramInt);
/*      */       }
/*      */     }
/* 1190 */     XToolkit.XSync();
/*      */   }
/*      */ 
/*      */   void setExtendedState(XWindowPeer paramXWindowPeer, int paramInt) {
/* 1194 */     for (XStateProtocol localXStateProtocol : getProtocols(XStateProtocol.class)) {
/* 1195 */       if (localXStateProtocol.supportsState(paramInt)) {
/* 1196 */         localXStateProtocol.setState(paramXWindowPeer, paramInt);
/* 1197 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 1201 */     if (!paramXWindowPeer.isShowing())
/*      */     {
/* 1206 */       XToolkit.awtLock();
/*      */       try {
/* 1208 */         XlibWrapper.XDeleteProperty(XToolkit.getDisplay(), paramXWindowPeer.getWindow(), XA_KWM_WIN_ICONIFIED.getAtom());
/*      */ 
/* 1211 */         XlibWrapper.XDeleteProperty(XToolkit.getDisplay(), paramXWindowPeer.getWindow(), XA_KWM_WIN_MAXIMIZED.getAtom());
/*      */       }
/*      */       finally
/*      */       {
/* 1216 */         XToolkit.awtUnlock();
/*      */       }
/*      */     }
/* 1219 */     XToolkit.XSync();
/*      */   }
/*      */ 
/*      */   void unshadeKludge(XDecoratedPeer paramXDecoratedPeer)
/*      */   {
/* 1238 */     assert (paramXDecoratedPeer.isShowing());
/*      */ 
/* 1240 */     for (XStateProtocol localXStateProtocol : getProtocols(XStateProtocol.class)) {
/* 1241 */       localXStateProtocol.unshadeKludge(paramXDecoratedPeer);
/*      */     }
/* 1243 */     XToolkit.XSync();
/*      */   }
/*      */ 
/*      */   static void init()
/*      */   {
/* 1248 */     if (inited) {
/* 1249 */       return;
/*      */     }
/*      */ 
/* 1252 */     initAtoms();
/* 1253 */     getWM();
/* 1254 */     inited = true;
/*      */   }
/*      */ 
/*      */   void initializeProtocols() {
/* 1258 */     XNETProtocol localXNETProtocol = g_net_protocol;
/* 1259 */     if (localXNETProtocol != null) {
/* 1260 */       if (!localXNETProtocol.active()) {
/* 1261 */         localXNETProtocol = null;
/*      */       } else {
/* 1263 */         if (localXNETProtocol.doStateProtocol()) {
/* 1264 */           addProtocol(XStateProtocol.class, localXNETProtocol);
/*      */         }
/* 1266 */         if (localXNETProtocol.doLayerProtocol()) {
/* 1267 */           addProtocol(XLayerProtocol.class, localXNETProtocol);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1272 */     XWINProtocol localXWINProtocol = g_win_protocol;
/* 1273 */     if ((localXWINProtocol != null) && 
/* 1274 */       (localXWINProtocol.active())) {
/* 1275 */       if (localXWINProtocol.doStateProtocol()) {
/* 1276 */         addProtocol(XStateProtocol.class, localXWINProtocol);
/*      */       }
/* 1278 */       if (localXWINProtocol.doLayerProtocol())
/* 1279 */         addProtocol(XLayerProtocol.class, localXWINProtocol);
/*      */     }
/*      */   }
/*      */ 
/*      */   Insets guessInsets(XDecoratedPeer paramXDecoratedPeer)
/*      */   {
/* 1287 */     Insets localInsets = (Insets)this.storedInsets.get(paramXDecoratedPeer.getClass());
/* 1288 */     if (localInsets == null) {
/* 1289 */       switch (this.WMID) {
/*      */       case 7:
/* 1291 */         localInsets = new Insets(19, 4, 4, 4);
/* 1292 */         break;
/*      */       case 6:
/* 1294 */         localInsets = new Insets(28, 6, 6, 6);
/* 1295 */         break;
/*      */       case 2:
/*      */       case 13:
/* 1298 */         localInsets = zeroInsets;
/* 1299 */         break;
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       default:
/* 1303 */         localInsets = defaultInsets;
/*      */       }
/*      */     }
/* 1306 */     if (insLog.isLoggable(300)) insLog.finest("WM guessed insets: " + localInsets);
/* 1307 */     return localInsets;
/*      */   }
/*      */ 
/*      */   static boolean configureGravityBuggy()
/*      */   {
/* 1322 */     if (awtWMStaticGravity == -1) {
/* 1323 */       awtWMStaticGravity = XToolkit.getEnv("_JAVA_AWT_WM_STATIC_GRAVITY") != null ? 1 : 0;
/*      */     }
/*      */ 
/* 1326 */     if (awtWMStaticGravity == 1) {
/* 1327 */       return true;
/*      */     }
/*      */ 
/* 1330 */     switch (getWMID())
/*      */     {
/*      */     case 10:
/* 1340 */       if (g_net_protocol != null) {
/* 1341 */         String str = g_net_protocol.getWMName();
/* 1342 */         Pattern localPattern = Pattern.compile("^IceWM (\\d+)\\.(\\d+)\\.(\\d+).*$");
/*      */         try {
/* 1344 */           Matcher localMatcher = localPattern.matcher(str);
/* 1345 */           if (localMatcher.matches()) {
/* 1346 */             int i = Integer.parseInt(localMatcher.group(1));
/* 1347 */             int j = Integer.parseInt(localMatcher.group(2));
/* 1348 */             int k = Integer.parseInt(localMatcher.group(3));
/* 1349 */             return (i <= 1) && ((i != 1) || ((j <= 2) && ((j != 2) || (k < 2))));
/*      */           }
/*      */         } catch (Exception localException) {
/* 1352 */           return true;
/*      */         }
/*      */       }
/* 1355 */       return true;
/*      */     case 7:
/* 1358 */       return true;
/*      */     }
/* 1360 */     return false;
/*      */   }
/*      */ 
/*      */   public static Insets getInsetsFromExtents(long paramLong)
/*      */   {
/* 1369 */     if (paramLong == 0L) {
/* 1370 */       return null;
/*      */     }
/* 1372 */     XNETProtocol localXNETProtocol = getWM().getNETProtocol();
/* 1373 */     if ((localXNETProtocol != null) && (localXNETProtocol.active())) {
/* 1374 */       Insets localInsets = getInsetsFromProp(paramLong, XA_NET_FRAME_EXTENTS);
/* 1375 */       insLog.fine("_NET_FRAME_EXTENTS: {0}", new Object[] { localInsets });
/*      */ 
/* 1377 */       if (localInsets != null) {
/* 1378 */         return localInsets;
/*      */       }
/*      */     }
/* 1381 */     switch (getWMID()) {
/*      */     case 8:
/* 1383 */       return getInsetsFromProp(paramLong, XA_KDE_NET_WM_FRAME_STRUT);
/*      */     case 7:
/* 1385 */       return getInsetsFromProp(paramLong, XA_E_FRAME_SIZE);
/*      */     }
/* 1387 */     return null;
/*      */   }
/*      */ 
/*      */   public static Insets getInsetsFromProp(long paramLong, XAtom paramXAtom)
/*      */   {
/* 1396 */     if (paramLong == 0L) {
/* 1397 */       return null;
/*      */     }
/*      */ 
/* 1400 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, paramXAtom, 0L, 4L, false, 6L);
/*      */     try
/*      */     {
/*      */       Insets localInsets;
/* 1404 */       if ((localWindowPropertyGetter.execute() != 0) || (localWindowPropertyGetter.getData() == 0L) || (localWindowPropertyGetter.getActualType() != 6L) || (localWindowPropertyGetter.getActualFormat() != 32))
/*      */       {
/* 1409 */         return null;
/*      */       }
/* 1411 */       return new Insets((int)Native.getCard32(localWindowPropertyGetter.getData(), 2), (int)Native.getCard32(localWindowPropertyGetter.getData(), 0), (int)Native.getCard32(localWindowPropertyGetter.getData(), 3), (int)Native.getCard32(localWindowPropertyGetter.getData(), 1));
/*      */     }
/*      */     finally
/*      */     {
/* 1417 */       localWindowPropertyGetter.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void requestWMExtents(long paramLong)
/*      */   {
/* 1425 */     if (paramLong == 0L) {
/* 1426 */       return;
/*      */     }
/*      */ 
/* 1429 */     log.fine("Requesting FRAME_EXTENTS");
/*      */ 
/* 1431 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/* 1432 */     localXClientMessageEvent.zero();
/* 1433 */     localXClientMessageEvent.set_type(33);
/* 1434 */     localXClientMessageEvent.set_display(XToolkit.getDisplay());
/* 1435 */     localXClientMessageEvent.set_window(paramLong);
/* 1436 */     localXClientMessageEvent.set_format(32);
/* 1437 */     XToolkit.awtLock();
/*      */     try {
/* 1439 */       XNETProtocol localXNETProtocol = getWM().getNETProtocol();
/* 1440 */       if ((localXNETProtocol != null) && (localXNETProtocol.active())) {
/* 1441 */         localXClientMessageEvent.set_message_type(XA_NET_REQUEST_FRAME_EXTENTS.getAtom());
/* 1442 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), XToolkit.getDefaultRootWindow(), false, 1572864L, localXClientMessageEvent.getPData());
/*      */       }
/*      */ 
/* 1447 */       if (getWMID() == 8) {
/* 1448 */         localXClientMessageEvent.set_message_type(XA_KDE_NET_WM_FRAME_STRUT.getAtom());
/* 1449 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), XToolkit.getDefaultRootWindow(), false, 1572864L, localXClientMessageEvent.getPData());
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 1456 */       XToolkit.awtUnlock();
/* 1457 */       localXClientMessageEvent.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean syncTopLevelPos(long paramLong, XWindowAttributes paramXWindowAttributes)
/*      */   {
/* 1471 */     int i = 0;
/* 1472 */     XToolkit.awtLock();
/*      */     try {
/*      */       do {
/* 1475 */         XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), paramLong, paramXWindowAttributes.pData);
/* 1476 */         if ((paramXWindowAttributes.get_x() != 0) || (paramXWindowAttributes.get_y() != 0)) {
/* 1477 */           return true;
/*      */         }
/* 1479 */         i++;
/* 1480 */         XToolkit.XSync();
/* 1481 */       }while (i < 50);
/*      */     }
/*      */     finally {
/* 1484 */       XToolkit.awtUnlock();
/*      */     }
/* 1486 */     return false;
/*      */   }
/*      */ 
/*      */   Insets getInsets(XDecoratedPeer paramXDecoratedPeer, long paramLong1, long paramLong2)
/*      */   {
/* 1515 */     Insets localInsets1 = getInsetsFromExtents(paramLong1);
/* 1516 */     insLog.finer("Got insets from property: {0}", new Object[] { localInsets1 });
/*      */ 
/* 1518 */     if (localInsets1 == null) {
/* 1519 */       localInsets1 = new Insets(0, 0, 0, 0);
/*      */ 
/* 1521 */       localInsets1.top = -1;
/* 1522 */       localInsets1.left = -1;
/*      */ 
/* 1524 */       XWindowAttributes localXWindowAttributes1 = new XWindowAttributes();
/* 1525 */       XWindowAttributes localXWindowAttributes2 = new XWindowAttributes();
/*      */       try {
/* 1527 */         switch (getWMID())
/*      */         {
/*      */         case 7:
/* 1531 */           syncTopLevelPos(paramLong2, localXWindowAttributes1);
/* 1532 */           localInsets1.left = localXWindowAttributes1.get_x();
/* 1533 */           localInsets1.top = localXWindowAttributes1.get_y();
/*      */ 
/* 1540 */           XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), XlibUtil.getParentWindow(paramLong2), localXWindowAttributes2.pData);
/*      */ 
/* 1543 */           localInsets1.right = (localXWindowAttributes2.get_width() - (localXWindowAttributes1.get_width() + localInsets1.left));
/*      */ 
/* 1545 */           localInsets1.bottom = (localXWindowAttributes2.get_height() - (localXWindowAttributes1.get_height() + localInsets1.top));
/*      */ 
/* 1548 */           break;
/*      */         case 5:
/*      */         case 6:
/*      */         case 8:
/*      */         case 10:
/* 1555 */           if (syncTopLevelPos(paramLong2, localXWindowAttributes1)) {
/* 1556 */             localInsets1.top = localXWindowAttributes1.get_y();
/* 1557 */             localInsets1.left = localXWindowAttributes1.get_x();
/* 1558 */             localInsets1.right = localInsets1.left;
/* 1559 */             localInsets1.bottom = localInsets1.left;
/*      */           } else {
/* 1561 */             return null;
/*      */           }
/*      */ 
/*      */           break;
/*      */         case 4:
/*      */         case 9:
/* 1568 */           syncTopLevelPos(paramLong1, localXWindowAttributes1);
/* 1569 */           localInsets1.top = localXWindowAttributes1.get_y();
/* 1570 */           localInsets1.left = localXWindowAttributes1.get_x();
/* 1571 */           localInsets1.right = localInsets1.left;
/* 1572 */           localInsets1.bottom = localInsets1.left;
/* 1573 */           break;
/*      */         case 3:
/*      */         default:
/* 1577 */           insLog.finest("Getting correct insets for OTHER_WM/default, parent: {0}", new Object[] { Long.valueOf(paramLong2) });
/* 1578 */           syncTopLevelPos(paramLong2, localXWindowAttributes1);
/* 1579 */           int i = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), paramLong1, localXWindowAttributes1.pData);
/*      */ 
/* 1581 */           i = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), paramLong2, localXWindowAttributes2.pData);
/*      */ 
/* 1583 */           if (localXWindowAttributes1.get_root() == paramLong2) {
/* 1584 */             insLog.finest("our parent is root so insets should be zero");
/* 1585 */             localInsets1 = new Insets(0, 0, 0, 0);
/*      */           }
/*      */           else
/*      */           {
/* 1600 */             if ((localXWindowAttributes1.get_x() == 0) && (localXWindowAttributes1.get_y() == 0) && (localXWindowAttributes1.get_width() + 2 * localXWindowAttributes1.get_border_width() == localXWindowAttributes2.get_width()) && (localXWindowAttributes1.get_height() + 2 * localXWindowAttributes1.get_border_width() == localXWindowAttributes2.get_height()))
/*      */             {
/* 1604 */               insLog.finest("Double reparenting detected, pattr({2})={0}, lwinAttr({3})={1}", new Object[] { localXWindowAttributes1, localXWindowAttributes2, Long.valueOf(paramLong2), Long.valueOf(paramLong1) });
/*      */ 
/* 1606 */               localXWindowAttributes1.set_x(localXWindowAttributes2.get_x());
/* 1607 */               localXWindowAttributes1.set_y(localXWindowAttributes2.get_y());
/* 1608 */               localXWindowAttributes1.set_border_width(localXWindowAttributes1.get_border_width() + localXWindowAttributes2.get_border_width());
/*      */ 
/* 1610 */               long l = XlibUtil.getParentWindow(paramLong2);
/*      */ 
/* 1612 */               if (l == localXWindowAttributes1.get_root())
/*      */               {
/* 1617 */                 return null;
/*      */               }
/* 1619 */               paramLong2 = l;
/* 1620 */               XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), paramLong2, localXWindowAttributes2.pData);
/*      */             }
/*      */ 
/* 1632 */             insLog.finest("Attrs before calculation: pattr({2})={0}, lwinAttr({3})={1}", new Object[] { localXWindowAttributes1, localXWindowAttributes2, Long.valueOf(paramLong2), Long.valueOf(paramLong1) });
/*      */ 
/* 1634 */             localInsets1 = new Insets(localXWindowAttributes1.get_y() + localXWindowAttributes1.get_border_width(), localXWindowAttributes1.get_x() + localXWindowAttributes1.get_border_width(), localXWindowAttributes2.get_height() - (localXWindowAttributes1.get_y() + localXWindowAttributes1.get_height() + 2 * localXWindowAttributes1.get_border_width()), localXWindowAttributes2.get_width() - (localXWindowAttributes1.get_x() + localXWindowAttributes1.get_width() + 2 * localXWindowAttributes1.get_border_width()));
/*      */           }
/*      */           break;
/*      */         }
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 1642 */         localXWindowAttributes1.dispose();
/* 1643 */         localXWindowAttributes2.dispose();
/*      */       }
/*      */     }
/* 1646 */     if (this.storedInsets.get(paramXDecoratedPeer.getClass()) == null) {
/* 1647 */       this.storedInsets.put(paramXDecoratedPeer.getClass(), localInsets1);
/*      */     }
/* 1649 */     return localInsets1;
/*      */   }
/*      */   boolean isDesktopWindow(long paramLong) {
/* 1652 */     if (g_net_protocol != null) {
/* 1653 */       XAtomList localXAtomList = XAtom.get("_NET_WM_WINDOW_TYPE").getAtomListPropertyList(paramLong);
/* 1654 */       return localXAtomList.contains(XAtom.get("_NET_WM_WINDOW_TYPE_DESKTOP"));
/*      */     }
/* 1656 */     return false;
/*      */   }
/*      */ 
/*      */   public XNETProtocol getNETProtocol()
/*      */   {
/* 1661 */     return g_net_protocol;
/*      */   }
/*      */ 
/*      */   public boolean setNetWMIcon(XWindowPeer paramXWindowPeer, List<XIconInfo> paramList)
/*      */   {
/* 1674 */     if ((g_net_protocol != null) && (g_net_protocol.active())) {
/* 1675 */       g_net_protocol.setWMIcons(paramXWindowPeer, paramList);
/* 1676 */       return getWMID() != 10;
/*      */     }
/* 1678 */     return false;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XWM
 * JD-Core Version:    0.6.2
 */