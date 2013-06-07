/*     */ package sun.awt.X11;
/*     */ 
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XEvent extends XWrapperBase
/*     */ {
/*   9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*     */   private final boolean should_free_memory;
/*     */   long pData;
/*     */ 
/*     */   public static int getSize()
/*     */   {
/*  11 */     return 96; } 
/*  12 */   public int getDataSize() { return getSize(); }
/*     */ 
/*     */   public long getPData()
/*     */   {
/*  16 */     return this.pData;
/*     */   }
/*     */ 
/*     */   public XEvent(long paramLong) {
/*  20 */     log.finest("Creating");
/*  21 */     this.pData = paramLong;
/*  22 */     this.should_free_memory = false;
/*     */   }
/*     */ 
/*     */   public XEvent()
/*     */   {
/*  27 */     log.finest("Creating");
/*  28 */     this.pData = this.unsafe.allocateMemory(getSize());
/*  29 */     this.should_free_memory = true;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/*  34 */     log.finest("Disposing");
/*  35 */     if (this.should_free_memory) {
/*  36 */       log.finest("freeing memory");
/*  37 */       this.unsafe.freeMemory(this.pData);
/*     */     }
/*     */   }
/*  40 */   public int get_type() { log.finest(""); return Native.getInt(this.pData + 0L); } 
/*  41 */   public void set_type(int paramInt) { log.finest(""); Native.putInt(this.pData + 0L, paramInt); } 
/*  42 */   public XAnyEvent get_xany() { log.finest(""); return new XAnyEvent(this.pData + 0L); } 
/*  43 */   public XKeyEvent get_xkey() { log.finest(""); return new XKeyEvent(this.pData + 0L); } 
/*  44 */   public XButtonEvent get_xbutton() { log.finest(""); return new XButtonEvent(this.pData + 0L); } 
/*  45 */   public XMotionEvent get_xmotion() { log.finest(""); return new XMotionEvent(this.pData + 0L); } 
/*  46 */   public XCrossingEvent get_xcrossing() { log.finest(""); return new XCrossingEvent(this.pData + 0L); } 
/*  47 */   public XFocusChangeEvent get_xfocus() { log.finest(""); return new XFocusChangeEvent(this.pData + 0L); } 
/*  48 */   public XExposeEvent get_xexpose() { log.finest(""); return new XExposeEvent(this.pData + 0L); } 
/*  49 */   public XGraphicsExposeEvent get_xgraphicsexpose() { log.finest(""); return new XGraphicsExposeEvent(this.pData + 0L); } 
/*  50 */   public XNoExposeEvent get_xnoexpose() { log.finest(""); return new XNoExposeEvent(this.pData + 0L); } 
/*  51 */   public XVisibilityEvent get_xvisibility() { log.finest(""); return new XVisibilityEvent(this.pData + 0L); } 
/*  52 */   public XCreateWindowEvent get_xcreatewindow() { log.finest(""); return new XCreateWindowEvent(this.pData + 0L); } 
/*  53 */   public XDestroyWindowEvent get_xdestroywindow() { log.finest(""); return new XDestroyWindowEvent(this.pData + 0L); } 
/*  54 */   public XUnmapEvent get_xunmap() { log.finest(""); return new XUnmapEvent(this.pData + 0L); } 
/*  55 */   public XMapEvent get_xmap() { log.finest(""); return new XMapEvent(this.pData + 0L); } 
/*  56 */   public XMapRequestEvent get_xmaprequest() { log.finest(""); return new XMapRequestEvent(this.pData + 0L); } 
/*  57 */   public XReparentEvent get_xreparent() { log.finest(""); return new XReparentEvent(this.pData + 0L); } 
/*  58 */   public XConfigureEvent get_xconfigure() { log.finest(""); return new XConfigureEvent(this.pData + 0L); } 
/*  59 */   public XGravityEvent get_xgravity() { log.finest(""); return new XGravityEvent(this.pData + 0L); } 
/*  60 */   public XResizeRequestEvent get_xresizerequest() { log.finest(""); return new XResizeRequestEvent(this.pData + 0L); } 
/*  61 */   public XConfigureRequestEvent get_xconfigurerequest() { log.finest(""); return new XConfigureRequestEvent(this.pData + 0L); } 
/*  62 */   public XCirculateEvent get_xcirculate() { log.finest(""); return new XCirculateEvent(this.pData + 0L); } 
/*  63 */   public XCirculateRequestEvent get_xcirculaterequest() { log.finest(""); return new XCirculateRequestEvent(this.pData + 0L); } 
/*  64 */   public XPropertyEvent get_xproperty() { log.finest(""); return new XPropertyEvent(this.pData + 0L); } 
/*  65 */   public XSelectionClearEvent get_xselectionclear() { log.finest(""); return new XSelectionClearEvent(this.pData + 0L); } 
/*  66 */   public XSelectionRequestEvent get_xselectionrequest() { log.finest(""); return new XSelectionRequestEvent(this.pData + 0L); } 
/*  67 */   public XSelectionEvent get_xselection() { log.finest(""); return new XSelectionEvent(this.pData + 0L); } 
/*  68 */   public XColormapEvent get_xcolormap() { log.finest(""); return new XColormapEvent(this.pData + 0L); } 
/*  69 */   public XClientMessageEvent get_xclient() { log.finest(""); return new XClientMessageEvent(this.pData + 0L); } 
/*  70 */   public XMappingEvent get_xmapping() { log.finest(""); return new XMappingEvent(this.pData + 0L); } 
/*  71 */   public XErrorEvent get_xerror() { log.finest(""); return new XErrorEvent(this.pData + 0L); } 
/*  72 */   public XKeymapEvent get_xkeymap() { log.finest(""); return new XKeymapEvent(this.pData + 0L); } 
/*  73 */   public long get_pad(int paramInt) { log.finest(""); return Native.getLong(this.pData + 0L + paramInt * Native.getLongSize()); } 
/*  74 */   public void set_pad(int paramInt, long paramLong) { log.finest(""); Native.putLong(this.pData + 0L + paramInt * Native.getLongSize(), paramLong); } 
/*  75 */   public long get_pad() { log.finest(""); return this.pData + 0L; }
/*     */ 
/*     */   String getName()
/*     */   {
/*  79 */     return "XEvent";
/*     */   }
/*     */ 
/*     */   String getFieldsAsString()
/*     */   {
/*  84 */     StringBuilder localStringBuilder = new StringBuilder(1320);
/*     */ 
/*  86 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/*  87 */     localStringBuilder.append("xany = ").append(get_xany()).append(", ");
/*  88 */     localStringBuilder.append("xkey = ").append(get_xkey()).append(", ");
/*  89 */     localStringBuilder.append("xbutton = ").append(get_xbutton()).append(", ");
/*  90 */     localStringBuilder.append("xmotion = ").append(get_xmotion()).append(", ");
/*  91 */     localStringBuilder.append("xcrossing = ").append(get_xcrossing()).append(", ");
/*  92 */     localStringBuilder.append("xfocus = ").append(get_xfocus()).append(", ");
/*  93 */     localStringBuilder.append("xexpose = ").append(get_xexpose()).append(", ");
/*  94 */     localStringBuilder.append("xgraphicsexpose = ").append(get_xgraphicsexpose()).append(", ");
/*  95 */     localStringBuilder.append("xnoexpose = ").append(get_xnoexpose()).append(", ");
/*  96 */     localStringBuilder.append("xvisibility = ").append(get_xvisibility()).append(", ");
/*  97 */     localStringBuilder.append("xcreatewindow = ").append(get_xcreatewindow()).append(", ");
/*  98 */     localStringBuilder.append("xdestroywindow = ").append(get_xdestroywindow()).append(", ");
/*  99 */     localStringBuilder.append("xunmap = ").append(get_xunmap()).append(", ");
/* 100 */     localStringBuilder.append("xmap = ").append(get_xmap()).append(", ");
/* 101 */     localStringBuilder.append("xmaprequest = ").append(get_xmaprequest()).append(", ");
/* 102 */     localStringBuilder.append("xreparent = ").append(get_xreparent()).append(", ");
/* 103 */     localStringBuilder.append("xconfigure = ").append(get_xconfigure()).append(", ");
/* 104 */     localStringBuilder.append("xgravity = ").append(get_xgravity()).append(", ");
/* 105 */     localStringBuilder.append("xresizerequest = ").append(get_xresizerequest()).append(", ");
/* 106 */     localStringBuilder.append("xconfigurerequest = ").append(get_xconfigurerequest()).append(", ");
/* 107 */     localStringBuilder.append("xcirculate = ").append(get_xcirculate()).append(", ");
/* 108 */     localStringBuilder.append("xcirculaterequest = ").append(get_xcirculaterequest()).append(", ");
/* 109 */     localStringBuilder.append("xproperty = ").append(get_xproperty()).append(", ");
/* 110 */     localStringBuilder.append("xselectionclear = ").append(get_xselectionclear()).append(", ");
/* 111 */     localStringBuilder.append("xselectionrequest = ").append(get_xselectionrequest()).append(", ");
/* 112 */     localStringBuilder.append("xselection = ").append(get_xselection()).append(", ");
/* 113 */     localStringBuilder.append("xcolormap = ").append(get_xcolormap()).append(", ");
/* 114 */     localStringBuilder.append("xclient = ").append(get_xclient()).append(", ");
/* 115 */     localStringBuilder.append("xmapping = ").append(get_xmapping()).append(", ");
/* 116 */     localStringBuilder.append("xerror = ").append(get_xerror()).append(", ");
/* 117 */     localStringBuilder.append("xkeymap = ").append(get_xkeymap()).append(", ");
/* 118 */     localStringBuilder.append("{").append(get_pad(0)).append(" ").append(get_pad(1)).append(" ").append(get_pad(2)).append(" ").append(get_pad(3)).append(" ").append(get_pad(4)).append(" ").append(get_pad(5)).append(" ").append(get_pad(6)).append(" ").append(get_pad(7)).append(" ").append(get_pad(8)).append(" ").append(get_pad(9)).append(" ").append(get_pad(10)).append(" ").append(get_pad(11)).append(" ").append(get_pad(12)).append(" ").append(get_pad(13)).append(" ").append(get_pad(14)).append(" ").append(get_pad(15)).append(" ").append(get_pad(16)).append(" ").append(get_pad(17)).append(" ").append(get_pad(18)).append(" ").append(get_pad(19)).append(" ").append(get_pad(20)).append(" ").append(get_pad(21)).append(" ").append(get_pad(22)).append(" ").append(get_pad(23)).append(" ").append("}");
/*     */ 
/* 143 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XEvent
 * JD-Core Version:    0.6.2
 */