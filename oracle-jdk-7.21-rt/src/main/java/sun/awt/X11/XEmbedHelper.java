/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTKeyStroke;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XEmbedHelper
/*     */ {
/*  40 */   private static final PlatformLogger xembedLog = PlatformLogger.getLogger("sun.awt.X11.xembed");
/*  41 */   static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */   static final int XEMBED_VERSION = 0;
/*     */   static final int XEMBED_MAPPED = 1;
/*     */   static final int XEMBED_EMBEDDED_NOTIFY = 0;
/*     */   static final int XEMBED_WINDOW_ACTIVATE = 1;
/*     */   static final int XEMBED_WINDOW_DEACTIVATE = 2;
/*     */   static final int XEMBED_REQUEST_FOCUS = 3;
/*     */   static final int XEMBED_FOCUS_IN = 4;
/*     */   static final int XEMBED_FOCUS_OUT = 5;
/*     */   static final int XEMBED_FOCUS_NEXT = 6;
/*     */   static final int XEMBED_FOCUS_PREV = 7;
/*     */   static final int XEMBED_GRAB_KEY = 8;
/*     */   static final int XEMBED_UNGRAB_KEY = 9;
/*     */   static final int XEMBED_MODALITY_ON = 10;
/*     */   static final int XEMBED_MODALITY_OFF = 11;
/*     */   static final int XEMBED_REGISTER_ACCELERATOR = 12;
/*     */   static final int XEMBED_UNREGISTER_ACCELERATOR = 13;
/*     */   static final int XEMBED_ACTIVATE_ACCELERATOR = 14;
/*     */   static final int NON_STANDARD_XEMBED_GTK_GRAB_KEY = 108;
/*     */   static final int NON_STANDARD_XEMBED_GTK_UNGRAB_KEY = 109;
/*     */   static final int XEMBED_FOCUS_CURRENT = 0;
/*     */   static final int XEMBED_FOCUS_FIRST = 1;
/*     */   static final int XEMBED_FOCUS_LAST = 2;
/*     */   static final int XEMBED_MODIFIER_SHIFT = 1;
/*     */   static final int XEMBED_MODIFIER_CONTROL = 2;
/*     */   static final int XEMBED_MODIFIER_ALT = 4;
/*     */   static final int XEMBED_MODIFIER_SUPER = 8;
/*     */   static final int XEMBED_MODIFIER_HYPER = 16;
/*     */   static XAtom XEmbedInfo;
/*     */   static XAtom XEmbed;
/*     */ 
/*     */   XEmbedHelper()
/*     */   {
/*  83 */     if (XEmbed == null) {
/*  84 */       XEmbed = XAtom.get("_XEMBED");
/*  85 */       if (xembedLog.isLoggable(400)) xembedLog.finer("Created atom " + XEmbed.toString());
/*     */     }
/*  87 */     if (XEmbedInfo == null) {
/*  88 */       XEmbedInfo = XAtom.get("_XEMBED_INFO");
/*  89 */       if (xembedLog.isLoggable(400)) xembedLog.finer("Created atom " + XEmbedInfo.toString()); 
/*     */     }
/*     */   }
/*     */ 
/*     */   void sendMessage(long paramLong, int paramInt)
/*     */   {
/*  94 */     sendMessage(paramLong, paramInt, 0L, 0L, 0L);
/*     */   }
/*     */   void sendMessage(long paramLong1, int paramInt, long paramLong2, long paramLong3, long paramLong4) {
/*  97 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*  98 */     localXClientMessageEvent.set_type(33);
/*  99 */     localXClientMessageEvent.set_window(paramLong1);
/* 100 */     localXClientMessageEvent.set_message_type(XEmbed.getAtom());
/* 101 */     localXClientMessageEvent.set_format(32);
/* 102 */     localXClientMessageEvent.set_data(0, XToolkit.getCurrentServerTime());
/* 103 */     localXClientMessageEvent.set_data(1, paramInt);
/* 104 */     localXClientMessageEvent.set_data(2, paramLong2);
/* 105 */     localXClientMessageEvent.set_data(3, paramLong3);
/* 106 */     localXClientMessageEvent.set_data(4, paramLong4);
/* 107 */     XToolkit.awtLock();
/*     */     try {
/* 109 */       if (xembedLog.isLoggable(500)) xembedLog.fine("Sending " + XEmbedMessageToString(localXClientMessageEvent));
/* 110 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), paramLong1, false, 0L, localXClientMessageEvent.pData);
/*     */     }
/*     */     finally {
/* 113 */       XToolkit.awtUnlock();
/*     */     }
/* 115 */     localXClientMessageEvent.dispose();
/*     */   }
/*     */ 
/*     */   static String msgidToString(int paramInt) {
/* 119 */     switch (paramInt) {
/*     */     case 0:
/* 121 */       return "XEMBED_EMBEDDED_NOTIFY";
/*     */     case 1:
/* 123 */       return "XEMBED_WINDOW_ACTIVATE";
/*     */     case 2:
/* 125 */       return "XEMBED_WINDOW_DEACTIVATE";
/*     */     case 4:
/* 127 */       return "XEMBED_FOCUS_IN";
/*     */     case 5:
/* 129 */       return "XEMBED_FOCUS_OUT";
/*     */     case 3:
/* 131 */       return "XEMBED_REQUEST_FOCUS";
/*     */     case 6:
/* 133 */       return "XEMBED_FOCUS_NEXT";
/*     */     case 7:
/* 135 */       return "XEMBED_FOCUS_PREV";
/*     */     case 10:
/* 137 */       return "XEMBED_MODALITY_ON";
/*     */     case 11:
/* 139 */       return "XEMBED_MODALITY_OFF";
/*     */     case 12:
/* 141 */       return "XEMBED_REGISTER_ACCELERATOR";
/*     */     case 13:
/* 143 */       return "XEMBED_UNREGISTER_ACCELERATOR";
/*     */     case 14:
/* 145 */       return "XEMBED_ACTIVATE_ACCELERATOR";
/*     */     case 8:
/* 147 */       return "XEMBED_GRAB_KEY";
/*     */     case 9:
/* 149 */       return "XEMBED_UNGRAB_KEY";
/*     */     case 109:
/* 151 */       return "NON_STANDARD_XEMBED_GTK_UNGRAB_KEY";
/*     */     case 108:
/* 153 */       return "NON_STANDARD_XEMBED_GTK_GRAB_KEY";
/*     */     case 32770:
/* 155 */       return "KeyPress";
/*     */     case 32787:
/* 157 */       return "MapNotify";
/*     */     case 32796:
/* 159 */       return "PropertyNotify";
/*     */     }
/* 161 */     return "unknown XEMBED id " + paramInt;
/*     */   }
/*     */ 
/*     */   static String focusIdToString(int paramInt)
/*     */   {
/* 166 */     switch (paramInt) {
/*     */     case 0:
/* 168 */       return "XEMBED_FOCUS_CURRENT";
/*     */     case 1:
/* 170 */       return "XEMBED_FOCUS_FIRST";
/*     */     case 2:
/* 172 */       return "XEMBED_FOCUS_LAST";
/*     */     }
/* 174 */     return "unknown focus id " + paramInt;
/*     */   }
/*     */ 
/*     */   static String XEmbedMessageToString(XClientMessageEvent paramXClientMessageEvent)
/*     */   {
/* 179 */     return "XEmbed message to " + Long.toHexString(paramXClientMessageEvent.get_window()) + ": " + msgidToString((int)paramXClientMessageEvent.get_data(1)) + ", detail: " + paramXClientMessageEvent.get_data(2) + ", data:[" + paramXClientMessageEvent.get_data(3) + "," + paramXClientMessageEvent.get_data(4) + "]";
/*     */   }
/*     */ 
/*     */   int getModifiers(int paramInt)
/*     */   {
/* 190 */     int i = 0;
/* 191 */     if ((paramInt & 0x1) != 0) {
/* 192 */       i |= 64;
/*     */     }
/* 194 */     if ((paramInt & 0x2) != 0) {
/* 195 */       i |= 128;
/*     */     }
/* 197 */     if ((paramInt & 0x4) != 0) {
/* 198 */       i |= 512;
/*     */     }
/*     */ 
/* 202 */     if ((paramInt & 0x8) != 0) {
/* 203 */       i |= 512;
/*     */     }
/*     */ 
/* 208 */     return i;
/*     */   }
/*     */ 
/*     */   AWTKeyStroke getKeyStrokeForKeySym(long paramLong1, long paramLong2) {
/* 213 */     XBaseWindow.checkSecurity();
/*     */ 
/* 217 */     XToolkit.awtLock();
/*     */     int i;
/*     */     try {
/* 219 */       XKeysym.Keysym2JavaKeycode localKeysym2JavaKeycode = XKeysym.getJavaKeycode(paramLong1);
/* 220 */       if (localKeysym2JavaKeycode == null)
/* 221 */         i = 0;
/*     */       else
/* 223 */         i = localKeysym2JavaKeycode.getJavaKeycode();
/*     */     }
/*     */     finally {
/* 226 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/* 229 */     int j = getModifiers((int)paramLong2);
/* 230 */     return AWTKeyStroke.getAWTKeyStroke(i, j);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XEmbedHelper
 * JD-Core Version:    0.6.2
 */