/*      */ package sun.awt;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.AWTException;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.InputMethodEvent;
/*      */ import java.awt.font.TextAttribute;
/*      */ import java.awt.font.TextHitInfo;
/*      */ import java.awt.im.InputMethodHighlight;
/*      */ import java.awt.im.spi.InputMethodContext;
/*      */ import java.awt.peer.ComponentPeer;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.FileReader;
/*      */ import java.io.IOException;
/*      */ import java.text.AttributedCharacterIterator;
/*      */ import java.text.AttributedString;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.regex.Pattern;
/*      */ import sun.awt.im.InputMethodAdapter;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public abstract class X11InputMethod extends InputMethodAdapter
/*      */ {
/*   71 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11InputMethod");
/*      */   private static final int XIMReverse = 1;
/*      */   private static final int XIMUnderline = 2;
/*      */   private static final int XIMHighlight = 4;
/*      */   private static final int XIMPrimary = 32;
/*      */   private static final int XIMSecondary = 64;
/*      */   private static final int XIMTertiary = 128;
/*      */   private static final int XIMVisibleToForward = 256;
/*      */   private static final int XIMVisibleToBackward = 512;
/*      */   private static final int XIMVisibleCenter = 1024;
/*      */   private static final int XIMVisibleMask = 1792;
/*      */   private Locale locale;
/*   94 */   private static boolean isXIMOpened = false;
/*   95 */   protected Container clientComponentWindow = null;
/*   96 */   private Component awtFocussedComponent = null;
/*   97 */   private Component lastXICFocussedComponent = null;
/*   98 */   private boolean isLastXICActive = false;
/*   99 */   private boolean isLastTemporary = false;
/*  100 */   private boolean isActive = false;
/*  101 */   private boolean isActiveClient = false;
/*      */   private static Map[] highlightStyles;
/*  103 */   private boolean disposed = false;
/*      */ 
/*  106 */   private boolean needResetXIC = false;
/*  107 */   private Component needResetXICClient = null;
/*      */ 
/*  114 */   private boolean compositionEnableSupported = true;
/*      */ 
/*  123 */   private boolean savedCompositionState = false;
/*      */ 
/*  127 */   private String committedText = null;
/*  128 */   private StringBuffer composedText = null;
/*      */   private IntBuffer rawFeedbacks;
/*  134 */   private transient long pData = 0L;
/*      */   private static final int INITIAL_SIZE = 64;
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   public X11InputMethod()
/*      */     throws AWTException
/*      */   {
/*  186 */     this.locale = X11InputMethodDescriptor.getSupportedLocale();
/*  187 */     if (!initXIM())
/*  188 */       throw new AWTException("Cannot open X Input Method");
/*      */   }
/*      */ 
/*      */   protected void finalize() throws Throwable
/*      */   {
/*  193 */     dispose();
/*  194 */     super.finalize();
/*      */   }
/*      */ 
/*      */   private synchronized boolean initXIM()
/*      */   {
/*  202 */     if (!isXIMOpened)
/*  203 */       isXIMOpened = openXIM();
/*  204 */     return isXIMOpened;
/*      */   }
/*      */ 
/*      */   protected abstract boolean openXIM();
/*      */ 
/*      */   protected boolean isDisposed() {
/*  210 */     return this.disposed;
/*      */   }
/*      */ 
/*      */   protected abstract void setXICFocus(ComponentPeer paramComponentPeer, boolean paramBoolean1, boolean paramBoolean2);
/*      */ 
/*      */   public void setInputMethodContext(InputMethodContext paramInputMethodContext)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean setLocale(Locale paramLocale)
/*      */   {
/*  232 */     if (paramLocale.equals(this.locale)) {
/*  233 */       return true;
/*      */     }
/*      */ 
/*  236 */     if (((this.locale.equals(Locale.JAPAN)) && (paramLocale.equals(Locale.JAPANESE))) || ((this.locale.equals(Locale.KOREA)) && (paramLocale.equals(Locale.KOREAN))))
/*      */     {
/*  238 */       return true;
/*      */     }
/*  240 */     return false;
/*      */   }
/*      */ 
/*      */   public Locale getLocale()
/*      */   {
/*  247 */     return this.locale;
/*      */   }
/*      */ 
/*      */   public void setCharacterSubsets(Character.Subset[] paramArrayOfSubset)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void dispatchEvent(AWTEvent paramAWTEvent)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected final void resetXICifneeded()
/*      */   {
/*  274 */     if ((this.needResetXIC) && (haveActiveClient()) && (getClientComponent() != this.needResetXICClient))
/*      */     {
/*  276 */       resetXIC();
/*      */ 
/*  279 */       this.lastXICFocussedComponent = null;
/*  280 */       this.isLastXICActive = false;
/*      */ 
/*  282 */       this.needResetXICClient = null;
/*  283 */       this.needResetXIC = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void resetCompositionState()
/*      */   {
/*  291 */     if (this.compositionEnableSupported)
/*      */     {
/*      */       try
/*      */       {
/*  295 */         setCompositionEnabled(this.savedCompositionState);
/*      */       } catch (UnsupportedOperationException localUnsupportedOperationException) {
/*  297 */         this.compositionEnableSupported = false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean getCompositionState()
/*      */   {
/*  308 */     boolean bool = false;
/*  309 */     if (this.compositionEnableSupported) {
/*      */       try {
/*  311 */         bool = isCompositionEnabled();
/*      */       } catch (UnsupportedOperationException localUnsupportedOperationException) {
/*  313 */         this.compositionEnableSupported = false;
/*      */       }
/*      */     }
/*  316 */     return bool;
/*      */   }
/*      */ 
/*      */   public synchronized void activate()
/*      */   {
/*  323 */     this.clientComponentWindow = getClientComponentWindow();
/*  324 */     if (this.clientComponentWindow == null) {
/*  325 */       return;
/*      */     }
/*  327 */     if ((this.lastXICFocussedComponent != null) && 
/*  328 */       (log.isLoggable(500))) log.fine("XICFocused {0}, AWTFocused {1}", new Object[] { this.lastXICFocussedComponent, this.awtFocussedComponent });
/*      */ 
/*  332 */     if (this.pData == 0L) {
/*  333 */       if (!createXIC()) {
/*  334 */         return;
/*      */       }
/*  336 */       this.disposed = false;
/*      */     }
/*      */ 
/*  341 */     resetXICifneeded();
/*  342 */     ComponentPeer localComponentPeer1 = null;
/*  343 */     ComponentPeer localComponentPeer2 = getPeer(this.awtFocussedComponent);
/*      */ 
/*  345 */     if (this.lastXICFocussedComponent != null) {
/*  346 */       localComponentPeer1 = getPeer(this.lastXICFocussedComponent);
/*      */     }
/*      */ 
/*  353 */     if ((this.isLastTemporary) || (localComponentPeer1 != localComponentPeer2) || (this.isLastXICActive != haveActiveClient()))
/*      */     {
/*  355 */       if (localComponentPeer1 != null) {
/*  356 */         setXICFocus(localComponentPeer1, false, this.isLastXICActive);
/*      */       }
/*  358 */       if (localComponentPeer2 != null) {
/*  359 */         setXICFocus(localComponentPeer2, true, haveActiveClient());
/*      */       }
/*  361 */       this.lastXICFocussedComponent = this.awtFocussedComponent;
/*  362 */       this.isLastXICActive = haveActiveClient();
/*      */     }
/*  364 */     resetCompositionState();
/*  365 */     this.isActive = true;
/*      */   }
/*      */ 
/*      */   protected abstract boolean createXIC();
/*      */ 
/*      */   public synchronized void deactivate(boolean paramBoolean)
/*      */   {
/*  374 */     boolean bool = haveActiveClient();
/*      */ 
/*  393 */     this.savedCompositionState = getCompositionState();
/*      */ 
/*  395 */     if (paramBoolean)
/*      */     {
/*  397 */       turnoffStatusWindow();
/*      */     }
/*      */ 
/*  403 */     this.lastXICFocussedComponent = this.awtFocussedComponent;
/*  404 */     this.isLastXICActive = bool;
/*  405 */     this.isLastTemporary = paramBoolean;
/*  406 */     this.isActive = false;
/*      */   }
/*      */ 
/*      */   public void disableInputMethod()
/*      */   {
/*  414 */     if (this.lastXICFocussedComponent != null) {
/*  415 */       setXICFocus(getPeer(this.lastXICFocussedComponent), false, this.isLastXICActive);
/*  416 */       this.lastXICFocussedComponent = null;
/*  417 */       this.isLastXICActive = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void hideWindows()
/*      */   {
/*      */   }
/*      */ 
/*      */   public static Map mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight)
/*      */   {
/*  431 */     int j = paramInputMethodHighlight.getState();
/*      */     int i;
/*  432 */     if (j == 0)
/*  433 */       i = 0;
/*  434 */     else if (j == 1)
/*  435 */       i = 2;
/*      */     else {
/*  437 */       return null;
/*      */     }
/*  439 */     if (paramInputMethodHighlight.isSelected()) {
/*  440 */       i++;
/*      */     }
/*  442 */     return highlightStyles[i];
/*      */   }
/*      */ 
/*      */   protected void setAWTFocussedComponent(Component paramComponent)
/*      */   {
/*  449 */     if (paramComponent == null) {
/*  450 */       return;
/*      */     }
/*  452 */     if (this.isActive)
/*      */     {
/*  455 */       boolean bool = haveActiveClient();
/*  456 */       setXICFocus(getPeer(this.awtFocussedComponent), false, bool);
/*  457 */       setXICFocus(getPeer(paramComponent), true, bool);
/*      */     }
/*  459 */     this.awtFocussedComponent = paramComponent;
/*      */   }
/*      */ 
/*      */   protected void stopListening()
/*      */   {
/*  471 */     endComposition();
/*      */ 
/*  474 */     disableInputMethod();
/*  475 */     if (this.needResetXIC) {
/*  476 */       resetXIC();
/*  477 */       this.needResetXICClient = null;
/*  478 */       this.needResetXIC = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private Window getClientComponentWindow()
/*      */   {
/*  489 */     Component localComponent = getClientComponent();
/*      */     Container localContainer;
/*  492 */     if ((localComponent instanceof Container))
/*  493 */       localContainer = (Container)localComponent;
/*      */     else {
/*  495 */       localContainer = getParent(localComponent);
/*      */     }
/*      */ 
/*  498 */     while ((localContainer != null) && (!(localContainer instanceof Window))) {
/*  499 */       localContainer = getParent(localContainer);
/*      */     }
/*  501 */     return (Window)localContainer;
/*      */   }
/*      */ 
/*      */   protected abstract Container getParent(Component paramComponent);
/*      */ 
/*      */   protected abstract ComponentPeer getPeer(Component paramComponent);
/*      */ 
/*      */   protected abstract void awtLock();
/*      */ 
/*      */   protected abstract void awtUnlock();
/*      */ 
/*      */   private void postInputMethodEvent(int paramInt1, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt2, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2, long paramLong)
/*      */   {
/*  531 */     Component localComponent = getClientComponent();
/*  532 */     if (localComponent != null) {
/*  533 */       InputMethodEvent localInputMethodEvent = new InputMethodEvent(localComponent, paramInt1, paramLong, paramAttributedCharacterIterator, paramInt2, paramTextHitInfo1, paramTextHitInfo2);
/*      */ 
/*  535 */       SunToolkit.postEvent(SunToolkit.targetToAppContext(localComponent), localInputMethodEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void postInputMethodEvent(int paramInt1, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt2, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2)
/*      */   {
/*  544 */     postInputMethodEvent(paramInt1, paramAttributedCharacterIterator, paramInt2, paramTextHitInfo1, paramTextHitInfo2, EventQueue.getMostRecentEventTime());
/*      */   }
/*      */ 
/*      */   void dispatchCommittedText(String paramString, long paramLong)
/*      */   {
/*  560 */     if (paramString == null) {
/*  561 */       return;
/*      */     }
/*  563 */     if (this.composedText == null) {
/*  564 */       AttributedString localAttributedString = new AttributedString(paramString);
/*  565 */       postInputMethodEvent(1100, localAttributedString.getIterator(), paramString.length(), null, null, paramLong);
/*      */     }
/*      */     else
/*      */     {
/*  574 */       this.committedText = paramString;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void dispatchCommittedText(String paramString) {
/*  579 */     dispatchCommittedText(paramString, EventQueue.getMostRecentEventTime());
/*      */   }
/*      */ 
/*      */   void dispatchComposedText(String paramString, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, long paramLong)
/*      */   {
/*  601 */     if (this.disposed) {
/*  602 */       return;
/*      */     }
/*      */ 
/*  606 */     if ((paramString == null) && (paramArrayOfInt == null) && (paramInt1 == 0) && (paramInt2 == 0) && (paramInt3 == 0) && (this.composedText == null) && (this.committedText == null))
/*      */     {
/*  613 */       return;
/*      */     }
/*  615 */     if (this.composedText == null)
/*      */     {
/*  617 */       this.composedText = new StringBuffer(64);
/*  618 */       this.rawFeedbacks = new IntBuffer(64);
/*      */     }
/*  620 */     if (paramInt2 > 0) {
/*  621 */       if ((paramString == null) && (paramArrayOfInt != null)) {
/*  622 */         this.rawFeedbacks.replace(paramInt1, paramArrayOfInt);
/*      */       }
/*  624 */       else if (paramInt2 == this.composedText.length())
/*      */       {
/*  627 */         this.composedText = new StringBuffer(64);
/*  628 */         this.rawFeedbacks = new IntBuffer(64);
/*      */       }
/*  630 */       else if (this.composedText.length() > 0) {
/*  631 */         if (paramInt1 + paramInt2 < this.composedText.length())
/*      */         {
/*  633 */           String str = this.composedText.toString().substring(paramInt1 + paramInt2, this.composedText.length());
/*      */ 
/*  635 */           this.composedText.setLength(paramInt1);
/*  636 */           this.composedText.append(str);
/*      */         }
/*      */         else
/*      */         {
/*  640 */           this.composedText.setLength(paramInt1);
/*      */         }
/*  642 */         this.rawFeedbacks.remove(paramInt1, paramInt2);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  647 */     if (paramString != null) {
/*  648 */       this.composedText.insert(paramInt1, paramString);
/*  649 */       if (paramArrayOfInt != null) {
/*  650 */         this.rawFeedbacks.insert(paramInt1, paramArrayOfInt);
/*      */       }
/*      */     }
/*  653 */     if (this.composedText.length() == 0) {
/*  654 */       this.composedText = null;
/*  655 */       this.rawFeedbacks = null;
/*      */ 
/*  660 */       if (this.committedText != null) {
/*  661 */         dispatchCommittedText(this.committedText, paramLong);
/*  662 */         this.committedText = null;
/*  663 */         return;
/*      */       }
/*      */ 
/*  668 */       postInputMethodEvent(1100, null, 0, null, null, paramLong);
/*      */       return;
/*      */     }
/*      */     int i;
/*      */     AttributedString localAttributedString;
/*  684 */     if (this.committedText != null) {
/*  685 */       i = this.committedText.length();
/*  686 */       localAttributedString = new AttributedString(this.committedText + this.composedText);
/*  687 */       this.committedText = null;
/*      */     } else {
/*  689 */       i = 0;
/*  690 */       localAttributedString = new AttributedString(this.composedText.toString());
/*      */     }
/*      */ 
/*  695 */     int m = 0;
/*      */ 
/*  697 */     int i1 = 0;
/*  698 */     TextHitInfo localTextHitInfo = null;
/*      */ 
/*  700 */     this.rawFeedbacks.rewind();
/*  701 */     int j = this.rawFeedbacks.getNext();
/*  702 */     this.rawFeedbacks.unget();
/*      */     int k;
/*  703 */     while ((k = this.rawFeedbacks.getNext()) != -1) {
/*  704 */       if (i1 == 0) {
/*  705 */         i1 = k & 0x700;
/*  706 */         if (i1 != 0) {
/*  707 */           int i2 = this.rawFeedbacks.getOffset() - 1;
/*      */ 
/*  709 */           if (i1 == 512)
/*  710 */             localTextHitInfo = TextHitInfo.leading(i2);
/*      */           else
/*  712 */             localTextHitInfo = TextHitInfo.trailing(i2);
/*      */         }
/*      */       }
/*  715 */       k &= -1793;
/*  716 */       if (j != k) {
/*  717 */         this.rawFeedbacks.unget();
/*  718 */         n = this.rawFeedbacks.getOffset();
/*  719 */         localAttributedString.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, convertVisualFeedbackToHighlight(j), i + m, i + n);
/*      */ 
/*  723 */         m = n;
/*  724 */         j = k;
/*      */       }
/*      */     }
/*  727 */     int n = this.rawFeedbacks.getOffset();
/*  728 */     if (n >= 0) {
/*  729 */       localAttributedString.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, convertVisualFeedbackToHighlight(j), i + m, i + n);
/*      */     }
/*      */ 
/*  735 */     postInputMethodEvent(1100, localAttributedString.getIterator(), i, TextHitInfo.leading(paramInt3), localTextHitInfo, paramLong);
/*      */   }
/*      */ 
/*      */   void flushText()
/*      */   {
/*  753 */     String str = this.committedText != null ? this.committedText : "";
/*  754 */     if (this.composedText != null) {
/*  755 */       str = str + this.composedText.toString();
/*      */     }
/*      */ 
/*  758 */     if (!str.equals("")) {
/*  759 */       AttributedString localAttributedString = new AttributedString(str);
/*  760 */       postInputMethodEvent(1100, localAttributedString.getIterator(), str.length(), null, null, EventQueue.getMostRecentEventTime());
/*      */ 
/*  766 */       this.composedText = null;
/*  767 */       this.committedText = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected synchronized void disposeImpl()
/*      */   {
/*  776 */     disposeXIC();
/*  777 */     awtLock();
/*  778 */     this.composedText = null;
/*  779 */     this.committedText = null;
/*  780 */     this.rawFeedbacks = null;
/*  781 */     awtUnlock();
/*  782 */     this.awtFocussedComponent = null;
/*  783 */     this.lastXICFocussedComponent = null;
/*      */   }
/*      */ 
/*      */   public final void dispose()
/*      */   {
/*  792 */     int i = 0;
/*      */ 
/*  794 */     if (!this.disposed) {
/*  795 */       synchronized (this) {
/*  796 */         if (!this.disposed) {
/*  797 */           this.disposed = (i = 1);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  802 */     if (i != 0)
/*  803 */       disposeImpl();
/*      */   }
/*      */ 
/*      */   public Object getControlObject()
/*      */   {
/*  813 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized void removeNotify()
/*      */   {
/*  820 */     dispose();
/*      */   }
/*      */ 
/*      */   public void setCompositionEnabled(boolean paramBoolean)
/*      */   {
/*  835 */     if (setCompositionEnabledNative(paramBoolean))
/*  836 */       this.savedCompositionState = paramBoolean;
/*      */   }
/*      */ 
/*      */   public boolean isCompositionEnabled()
/*      */   {
/*  849 */     return isCompositionEnabledNative();
/*      */   }
/*      */ 
/*      */   public void endComposition()
/*      */   {
/*  866 */     if (this.disposed) {
/*  867 */       return;
/*      */     }
/*      */ 
/*  872 */     this.savedCompositionState = getCompositionState();
/*  873 */     boolean bool = haveActiveClient();
/*  874 */     if ((bool) && (this.composedText == null) && (this.committedText == null)) {
/*  875 */       this.needResetXIC = true;
/*  876 */       this.needResetXICClient = getClientComponent();
/*  877 */       return;
/*      */     }
/*      */ 
/*  880 */     String str = resetXIC();
/*      */ 
/*  883 */     if (bool) {
/*  884 */       this.needResetXIC = false;
/*      */     }
/*      */ 
/*  893 */     awtLock();
/*  894 */     this.composedText = null;
/*  895 */     postInputMethodEvent(1100, null, 0, null, null);
/*      */ 
/*  901 */     if ((str != null) && (str.length() > 0)) {
/*  902 */       dispatchCommittedText(str);
/*      */     }
/*  904 */     awtUnlock();
/*      */ 
/*  907 */     if (this.savedCompositionState)
/*  908 */       resetCompositionState();
/*      */   }
/*      */ 
/*      */   public String getNativeInputMethodInfo()
/*      */   {
/*  929 */     String str1 = System.getenv("XMODIFIERS");
/*  930 */     String str2 = null;
/*      */ 
/*  933 */     if (str1 != null) {
/*  934 */       int i = str1.indexOf("@im=");
/*  935 */       if (i != -1)
/*  936 */         str2 = str1.substring(i + 4);
/*      */     }
/*  938 */     else if (System.getProperty("os.name").startsWith("SunOS")) {
/*  939 */       File localFile = new File(System.getProperty("user.home") + "/.dtprofile");
/*      */ 
/*  941 */       String str3 = null;
/*      */       try {
/*  943 */         BufferedReader localBufferedReader = new BufferedReader(new FileReader(localFile));
/*  944 */         String str4 = null;
/*      */ 
/*  946 */         while ((str3 == null) && ((str4 = localBufferedReader.readLine()) != null)) {
/*  947 */           if ((str4.contains("atok")) || (str4.contains("wnn"))) {
/*  948 */             StringTokenizer localStringTokenizer = new StringTokenizer(str4);
/*  949 */             while (localStringTokenizer.hasMoreTokens()) {
/*  950 */               String str5 = localStringTokenizer.nextToken();
/*  951 */               if ((Pattern.matches("atok.*setup", str5)) || (Pattern.matches("wnn.*setup", str5)))
/*      */               {
/*  953 */                 str3 = str5.substring(0, str5.indexOf("setup"));
/*  954 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  960 */         localBufferedReader.close();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  964 */         localIOException.printStackTrace();
/*      */       }
/*      */ 
/*  967 */       str2 = "htt " + str3;
/*      */     }
/*      */ 
/*  970 */     return str2;
/*      */   }
/*      */ 
/*      */   private InputMethodHighlight convertVisualFeedbackToHighlight(int paramInt)
/*      */   {
/*      */     InputMethodHighlight localInputMethodHighlight;
/*  981 */     switch (paramInt) {
/*      */     case 2:
/*  983 */       localInputMethodHighlight = InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT;
/*  984 */       break;
/*      */     case 1:
/*  986 */       localInputMethodHighlight = InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT;
/*  987 */       break;
/*      */     case 4:
/*  989 */       localInputMethodHighlight = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
/*  990 */       break;
/*      */     case 32:
/*  992 */       localInputMethodHighlight = InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT;
/*  993 */       break;
/*      */     case 64:
/*  995 */       localInputMethodHighlight = InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT;
/*  996 */       break;
/*      */     case 128:
/*  998 */       localInputMethodHighlight = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
/*  999 */       break;
/*      */     default:
/* 1001 */       localInputMethodHighlight = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
/*      */     }
/*      */ 
/* 1004 */     return localInputMethodHighlight;
/*      */   }
/*      */ 
/*      */   protected native String resetXIC();
/*      */ 
/*      */   private native void disposeXIC();
/*      */ 
/*      */   private native boolean setCompositionEnabledNative(boolean paramBoolean);
/*      */ 
/*      */   private native boolean isCompositionEnabledNative();
/*      */ 
/*      */   private native void turnoffStatusWindow();
/*      */ 
/*      */   static
/*      */   {
/*  138 */     Map[] arrayOfMap = new Map[4];
/*      */ 
/*  142 */     HashMap localHashMap = new HashMap(1);
/*  143 */     localHashMap.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
/*      */ 
/*  145 */     arrayOfMap[0] = Collections.unmodifiableMap(localHashMap);
/*      */ 
/*  148 */     localHashMap = new HashMap(1);
/*  149 */     localHashMap.put(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
/*      */ 
/*  151 */     arrayOfMap[1] = Collections.unmodifiableMap(localHashMap);
/*      */ 
/*  154 */     localHashMap = new HashMap(1);
/*  155 */     localHashMap.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
/*      */ 
/*  157 */     arrayOfMap[2] = Collections.unmodifiableMap(localHashMap);
/*      */ 
/*  160 */     localHashMap = new HashMap(1);
/*  161 */     localHashMap.put(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
/*      */ 
/*  163 */     arrayOfMap[3] = Collections.unmodifiableMap(localHashMap);
/*      */ 
/*  165 */     highlightStyles = arrayOfMap;
/*      */ 
/*  169 */     initIDs();
/*      */   }
/*      */ 
/*      */   private final class IntBuffer
/*      */   {
/*      */     private int[] intArray;
/*      */     private int size;
/*      */     private int index;
/*      */ 
/*      */     IntBuffer(int arg2)
/*      */     {
/*      */       int i;
/* 1022 */       this.intArray = new int[i];
/* 1023 */       this.size = 0;
/* 1024 */       this.index = 0;
/*      */     }
/*      */ 
/*      */     void insert(int paramInt, int[] paramArrayOfInt) {
/* 1028 */       int i = this.size + paramArrayOfInt.length;
/* 1029 */       if (this.intArray.length < i) {
/* 1030 */         int[] arrayOfInt = new int[i * 2];
/* 1031 */         System.arraycopy(this.intArray, 0, arrayOfInt, 0, this.size);
/* 1032 */         this.intArray = arrayOfInt;
/*      */       }
/* 1034 */       System.arraycopy(this.intArray, paramInt, this.intArray, paramInt + paramArrayOfInt.length, this.size - paramInt);
/*      */ 
/* 1036 */       System.arraycopy(paramArrayOfInt, 0, this.intArray, paramInt, paramArrayOfInt.length);
/* 1037 */       this.size += paramArrayOfInt.length;
/* 1038 */       if (this.index > paramInt)
/* 1039 */         this.index = paramInt;
/*      */     }
/*      */ 
/*      */     void remove(int paramInt1, int paramInt2) {
/* 1043 */       if (paramInt1 + paramInt2 != this.size) {
/* 1044 */         System.arraycopy(this.intArray, paramInt1 + paramInt2, this.intArray, paramInt1, this.size - paramInt1 - paramInt2);
/*      */       }
/* 1046 */       this.size -= paramInt2;
/* 1047 */       if (this.index > paramInt1)
/* 1048 */         this.index = paramInt1;
/*      */     }
/*      */ 
/*      */     void replace(int paramInt, int[] paramArrayOfInt) {
/* 1052 */       System.arraycopy(paramArrayOfInt, 0, this.intArray, paramInt, paramArrayOfInt.length);
/*      */     }
/*      */ 
/*      */     void removeAll() {
/* 1056 */       this.size = 0;
/* 1057 */       this.index = 0;
/*      */     }
/*      */ 
/*      */     void rewind() {
/* 1061 */       this.index = 0;
/*      */     }
/*      */ 
/*      */     int getNext() {
/* 1065 */       if (this.index == this.size)
/* 1066 */         return -1;
/* 1067 */       return this.intArray[(this.index++)];
/*      */     }
/*      */ 
/*      */     void unget() {
/* 1071 */       if (this.index != 0)
/* 1072 */         this.index -= 1;
/*      */     }
/*      */ 
/*      */     int getOffset() {
/* 1076 */       return this.index;
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 1080 */       StringBuffer localStringBuffer = new StringBuffer();
/* 1081 */       for (int i = 0; i < this.size; ) {
/* 1082 */         localStringBuffer.append(this.intArray[(i++)]);
/* 1083 */         if (i < this.size)
/* 1084 */           localStringBuffer.append(",");
/*      */       }
/* 1086 */       return localStringBuffer.toString();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11InputMethod
 * JD-Core Version:    0.6.2
 */