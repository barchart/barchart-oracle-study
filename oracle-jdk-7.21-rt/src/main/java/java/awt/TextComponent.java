/*      */ package java.awt;
/*      */ 
/*      */ import java.awt.event.TextEvent;
/*      */ import java.awt.event.TextListener;
/*      */ import java.awt.im.InputMethodRequests;
/*      */ import java.awt.peer.TextComponentPeer;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.text.BreakIterator;
/*      */ import java.util.EventListener;
/*      */ import javax.accessibility.Accessible;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.accessibility.AccessibleRole;
/*      */ import javax.accessibility.AccessibleState;
/*      */ import javax.accessibility.AccessibleStateSet;
/*      */ import javax.accessibility.AccessibleText;
/*      */ import javax.swing.text.AttributeSet;
/*      */ import sun.awt.InputMethodSupport;
/*      */ 
/*      */ public class TextComponent extends Component
/*      */   implements Accessible
/*      */ {
/*      */   String text;
/*   81 */   boolean editable = true;
/*      */   int selectionStart;
/*      */   int selectionEnd;
/*  108 */   boolean backgroundSetByClientCode = false;
/*      */   protected transient TextListener textListener;
/*      */   private static final long serialVersionUID = -2214773872412987419L;
/*  741 */   private int textComponentSerializedDataVersion = 1;
/*      */ 
/* 1219 */   private boolean checkForEnableIM = true;
/*      */ 
/*      */   TextComponent(String paramString)
/*      */     throws HeadlessException
/*      */   {
/*  131 */     GraphicsEnvironment.checkHeadless();
/*  132 */     this.text = (paramString != null ? paramString : "");
/*  133 */     setCursor(Cursor.getPredefinedCursor(2));
/*      */   }
/*      */ 
/*      */   private void enableInputMethodsIfNecessary() {
/*  137 */     if (this.checkForEnableIM) {
/*  138 */       this.checkForEnableIM = false;
/*      */       try {
/*  140 */         Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*  141 */         boolean bool = false;
/*  142 */         if ((localToolkit instanceof InputMethodSupport)) {
/*  143 */           bool = ((InputMethodSupport)localToolkit).enableInputMethodsForTextComponent();
/*      */         }
/*      */ 
/*  146 */         enableInputMethods(bool);
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void enableInputMethods(boolean paramBoolean)
/*      */   {
/*  166 */     this.checkForEnableIM = false;
/*  167 */     super.enableInputMethods(paramBoolean);
/*      */   }
/*      */ 
/*      */   boolean areInputMethodsEnabled()
/*      */   {
/*  173 */     if (this.checkForEnableIM) {
/*  174 */       enableInputMethodsIfNecessary();
/*      */     }
/*      */ 
/*  179 */     return (this.eventMask & 0x1000) != 0L;
/*      */   }
/*      */ 
/*      */   public InputMethodRequests getInputMethodRequests() {
/*  183 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  184 */     if (localTextComponentPeer != null) return localTextComponentPeer.getInputMethodRequests();
/*  185 */     return null;
/*      */   }
/*      */ 
/*      */   public void addNotify()
/*      */   {
/*  198 */     super.addNotify();
/*  199 */     enableInputMethodsIfNecessary();
/*      */   }
/*      */ 
/*      */   public void removeNotify()
/*      */   {
/*  209 */     synchronized (getTreeLock()) {
/*  210 */       TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  211 */       if (localTextComponentPeer != null) {
/*  212 */         this.text = localTextComponentPeer.getText();
/*  213 */         this.selectionStart = localTextComponentPeer.getSelectionStart();
/*  214 */         this.selectionEnd = localTextComponentPeer.getSelectionEnd();
/*      */       }
/*  216 */       super.removeNotify();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setText(String paramString)
/*      */   {
/*  229 */     this.text = (paramString != null ? paramString : "");
/*  230 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  231 */     if (localTextComponentPeer != null)
/*  232 */       localTextComponentPeer.setText(this.text);
/*      */   }
/*      */ 
/*      */   public synchronized String getText()
/*      */   {
/*  244 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  245 */     if (localTextComponentPeer != null) {
/*  246 */       this.text = localTextComponentPeer.getText();
/*      */     }
/*  248 */     return this.text;
/*      */   }
/*      */ 
/*      */   public synchronized String getSelectedText()
/*      */   {
/*  258 */     return getText().substring(getSelectionStart(), getSelectionEnd());
/*      */   }
/*      */ 
/*      */   public boolean isEditable()
/*      */   {
/*  269 */     return this.editable;
/*      */   }
/*      */ 
/*      */   public synchronized void setEditable(boolean paramBoolean)
/*      */   {
/*  289 */     if (this.editable == paramBoolean) {
/*  290 */       return;
/*      */     }
/*      */ 
/*  293 */     this.editable = paramBoolean;
/*  294 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  295 */     if (localTextComponentPeer != null)
/*  296 */       localTextComponentPeer.setEditable(paramBoolean);
/*      */   }
/*      */ 
/*      */   public Color getBackground()
/*      */   {
/*  314 */     if ((!this.editable) && (!this.backgroundSetByClientCode)) {
/*  315 */       return SystemColor.control;
/*      */     }
/*      */ 
/*  318 */     return super.getBackground();
/*      */   }
/*      */ 
/*      */   public void setBackground(Color paramColor)
/*      */   {
/*  331 */     this.backgroundSetByClientCode = true;
/*  332 */     super.setBackground(paramColor);
/*      */   }
/*      */ 
/*      */   public synchronized int getSelectionStart()
/*      */   {
/*  343 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  344 */     if (localTextComponentPeer != null) {
/*  345 */       this.selectionStart = localTextComponentPeer.getSelectionStart();
/*      */     }
/*  347 */     return this.selectionStart;
/*      */   }
/*      */ 
/*      */   public synchronized void setSelectionStart(int paramInt)
/*      */   {
/*  369 */     select(paramInt, getSelectionEnd());
/*      */   }
/*      */ 
/*      */   public synchronized int getSelectionEnd()
/*      */   {
/*  380 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  381 */     if (localTextComponentPeer != null) {
/*  382 */       this.selectionEnd = localTextComponentPeer.getSelectionEnd();
/*      */     }
/*  384 */     return this.selectionEnd;
/*      */   }
/*      */ 
/*      */   public synchronized void setSelectionEnd(int paramInt)
/*      */   {
/*  405 */     select(getSelectionStart(), paramInt);
/*      */   }
/*      */ 
/*      */   public synchronized void select(int paramInt1, int paramInt2)
/*      */   {
/*  441 */     String str = getText();
/*  442 */     if (paramInt1 < 0) {
/*  443 */       paramInt1 = 0;
/*      */     }
/*  445 */     if (paramInt1 > str.length()) {
/*  446 */       paramInt1 = str.length();
/*      */     }
/*  448 */     if (paramInt2 > str.length()) {
/*  449 */       paramInt2 = str.length();
/*      */     }
/*  451 */     if (paramInt2 < paramInt1) {
/*  452 */       paramInt2 = paramInt1;
/*      */     }
/*      */ 
/*  455 */     this.selectionStart = paramInt1;
/*  456 */     this.selectionEnd = paramInt2;
/*      */ 
/*  458 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  459 */     if (localTextComponentPeer != null)
/*  460 */       localTextComponentPeer.select(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public synchronized void selectAll()
/*      */   {
/*  469 */     this.selectionStart = 0;
/*  470 */     this.selectionEnd = getText().length();
/*      */ 
/*  472 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  473 */     if (localTextComponentPeer != null)
/*  474 */       localTextComponentPeer.select(this.selectionStart, this.selectionEnd);
/*      */   }
/*      */ 
/*      */   public synchronized void setCaretPosition(int paramInt)
/*      */   {
/*  495 */     if (paramInt < 0) {
/*  496 */       throw new IllegalArgumentException("position less than zero.");
/*      */     }
/*      */ 
/*  499 */     int i = getText().length();
/*  500 */     if (paramInt > i) {
/*  501 */       paramInt = i;
/*      */     }
/*      */ 
/*  504 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  505 */     if (localTextComponentPeer != null)
/*  506 */       localTextComponentPeer.setCaretPosition(paramInt);
/*      */     else
/*  508 */       select(paramInt, paramInt);
/*      */   }
/*      */ 
/*      */   public synchronized int getCaretPosition()
/*      */   {
/*  524 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  525 */     int i = 0;
/*      */ 
/*  527 */     if (localTextComponentPeer != null)
/*  528 */       i = localTextComponentPeer.getCaretPosition();
/*      */     else {
/*  530 */       i = this.selectionStart;
/*      */     }
/*  532 */     int j = getText().length();
/*  533 */     if (i > j) {
/*  534 */       i = j;
/*      */     }
/*  536 */     return i;
/*      */   }
/*      */ 
/*      */   public synchronized void addTextListener(TextListener paramTextListener)
/*      */   {
/*  553 */     if (paramTextListener == null) {
/*  554 */       return;
/*      */     }
/*  556 */     this.textListener = AWTEventMulticaster.add(this.textListener, paramTextListener);
/*  557 */     this.newEventsOnly = true;
/*      */   }
/*      */ 
/*      */   public synchronized void removeTextListener(TextListener paramTextListener)
/*      */   {
/*  575 */     if (paramTextListener == null) {
/*  576 */       return;
/*      */     }
/*  578 */     this.textListener = AWTEventMulticaster.remove(this.textListener, paramTextListener);
/*      */   }
/*      */ 
/*      */   public synchronized TextListener[] getTextListeners()
/*      */   {
/*  595 */     return (TextListener[])getListeners(TextListener.class);
/*      */   }
/*      */ 
/*      */   public <T extends EventListener> T[] getListeners(Class<T> paramClass)
/*      */   {
/*  632 */     TextListener localTextListener = null;
/*  633 */     if (paramClass == TextListener.class)
/*  634 */       localTextListener = this.textListener;
/*      */     else {
/*  636 */       return super.getListeners(paramClass);
/*      */     }
/*  638 */     return AWTEventMulticaster.getListeners(localTextListener, paramClass);
/*      */   }
/*      */ 
/*      */   boolean eventEnabled(AWTEvent paramAWTEvent)
/*      */   {
/*  643 */     if (paramAWTEvent.id == 900) {
/*  644 */       if (((this.eventMask & 0x400) != 0L) || (this.textListener != null))
/*      */       {
/*  646 */         return true;
/*      */       }
/*  648 */       return false;
/*      */     }
/*  650 */     return super.eventEnabled(paramAWTEvent);
/*      */   }
/*      */ 
/*      */   protected void processEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  664 */     if ((paramAWTEvent instanceof TextEvent)) {
/*  665 */       processTextEvent((TextEvent)paramAWTEvent);
/*  666 */       return;
/*      */     }
/*  668 */     super.processEvent(paramAWTEvent);
/*      */   }
/*      */ 
/*      */   protected void processTextEvent(TextEvent paramTextEvent)
/*      */   {
/*  691 */     TextListener localTextListener = this.textListener;
/*  692 */     if (localTextListener != null) {
/*  693 */       int i = paramTextEvent.getID();
/*  694 */       switch (i) {
/*      */       case 900:
/*  696 */         localTextListener.textValueChanged(paramTextEvent);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String paramString()
/*      */   {
/*  713 */     String str = super.paramString() + ",text=" + getText();
/*  714 */     if (this.editable) {
/*  715 */       str = str + ",editable";
/*      */     }
/*  717 */     return str + ",selection=" + getSelectionStart() + "-" + getSelectionEnd();
/*      */   }
/*      */ 
/*      */   private boolean canAccessClipboard()
/*      */   {
/*  724 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  725 */     if (localSecurityManager == null) return true; try
/*      */     {
/*  727 */       localSecurityManager.checkSystemClipboardAccess();
/*  728 */       return true; } catch (SecurityException localSecurityException) {
/*      */     }
/*  730 */     return false;
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/*  764 */     TextComponentPeer localTextComponentPeer = (TextComponentPeer)this.peer;
/*  765 */     if (localTextComponentPeer != null) {
/*  766 */       this.text = localTextComponentPeer.getText();
/*  767 */       this.selectionStart = localTextComponentPeer.getSelectionStart();
/*  768 */       this.selectionEnd = localTextComponentPeer.getSelectionEnd();
/*      */     }
/*      */ 
/*  771 */     paramObjectOutputStream.defaultWriteObject();
/*      */ 
/*  773 */     AWTEventMulticaster.save(paramObjectOutputStream, "textL", this.textListener);
/*  774 */     paramObjectOutputStream.writeObject(null);
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws ClassNotFoundException, IOException, HeadlessException
/*      */   {
/*  793 */     GraphicsEnvironment.checkHeadless();
/*  794 */     paramObjectInputStream.defaultReadObject();
/*      */ 
/*  798 */     this.text = (this.text != null ? this.text : "");
/*  799 */     select(this.selectionStart, this.selectionEnd);
/*      */     Object localObject;
/*  802 */     while (null != (localObject = paramObjectInputStream.readObject())) {
/*  803 */       String str = ((String)localObject).intern();
/*      */ 
/*  805 */       if ("textL" == str) {
/*  806 */         addTextListener((TextListener)paramObjectInputStream.readObject());
/*      */       }
/*      */       else {
/*  809 */         paramObjectInputStream.readObject();
/*      */       }
/*      */     }
/*  812 */     enableInputMethodsIfNecessary();
/*      */   }
/*      */ 
/*      */   int getIndexAtPoint(Point paramPoint)
/*      */   {
/*  825 */     return -1;
/*      */   }
/*      */ 
/*      */   Rectangle getCharacterBounds(int paramInt)
/*      */   {
/*  840 */     return null;
/*      */   }
/*      */ 
/*      */   public AccessibleContext getAccessibleContext()
/*      */   {
/*  862 */     if (this.accessibleContext == null) {
/*  863 */       this.accessibleContext = new AccessibleAWTTextComponent();
/*      */     }
/*  865 */     return this.accessibleContext;
/*      */   }
/*      */ 
/*      */   protected class AccessibleAWTTextComponent extends Component.AccessibleAWTComponent
/*      */     implements AccessibleText, TextListener
/*      */   {
/*      */     private static final long serialVersionUID = 3631432373506317811L;
/*      */     private static final boolean NEXT = true;
/*      */     private static final boolean PREVIOUS = false;
/*      */ 
/*      */     public AccessibleAWTTextComponent()
/*      */     {
/*  887 */       super();
/*  888 */       TextComponent.this.addTextListener(this);
/*      */     }
/*      */ 
/*      */     public void textValueChanged(TextEvent paramTextEvent)
/*      */     {
/*  895 */       Integer localInteger = Integer.valueOf(TextComponent.this.getCaretPosition());
/*  896 */       firePropertyChange("AccessibleText", null, localInteger);
/*      */     }
/*      */ 
/*      */     public AccessibleStateSet getAccessibleStateSet()
/*      */     {
/*  913 */       AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
/*  914 */       if (TextComponent.this.isEditable()) {
/*  915 */         localAccessibleStateSet.add(AccessibleState.EDITABLE);
/*      */       }
/*  917 */       return localAccessibleStateSet;
/*      */     }
/*      */ 
/*      */     public AccessibleRole getAccessibleRole()
/*      */     {
/*  929 */       return AccessibleRole.TEXT;
/*      */     }
/*      */ 
/*      */     public AccessibleText getAccessibleText()
/*      */     {
/*  941 */       return this;
/*      */     }
/*      */ 
/*      */     public int getIndexAtPoint(Point paramPoint)
/*      */     {
/*  961 */       return TextComponent.this.getIndexAtPoint(paramPoint);
/*      */     }
/*      */ 
/*      */     public Rectangle getCharacterBounds(int paramInt)
/*      */     {
/*  974 */       return TextComponent.this.getCharacterBounds(paramInt);
/*      */     }
/*      */ 
/*      */     public int getCharCount()
/*      */     {
/*  983 */       return TextComponent.this.getText().length();
/*      */     }
/*      */ 
/*      */     public int getCaretPosition()
/*      */     {
/*  996 */       return TextComponent.this.getCaretPosition();
/*      */     }
/*      */ 
/*      */     public AttributeSet getCharacterAttribute(int paramInt)
/*      */     {
/* 1006 */       return null;
/*      */     }
/*      */ 
/*      */     public int getSelectionStart()
/*      */     {
/* 1019 */       return TextComponent.this.getSelectionStart();
/*      */     }
/*      */ 
/*      */     public int getSelectionEnd()
/*      */     {
/* 1032 */       return TextComponent.this.getSelectionEnd();
/*      */     }
/*      */ 
/*      */     public String getSelectedText()
/*      */     {
/* 1041 */       String str = TextComponent.this.getSelectedText();
/*      */ 
/* 1043 */       if ((str == null) || (str.equals(""))) {
/* 1044 */         return null;
/*      */       }
/* 1046 */       return str;
/*      */     }
/*      */ 
/*      */     public String getAtIndex(int paramInt1, int paramInt2)
/*      */     {
/* 1059 */       if ((paramInt2 < 0) || (paramInt2 >= TextComponent.this.getText().length()))
/* 1060 */         return null;
/*      */       String str;
/*      */       BreakIterator localBreakIterator;
/*      */       int i;
/* 1062 */       switch (paramInt1) {
/*      */       case 1:
/* 1064 */         return TextComponent.this.getText().substring(paramInt2, paramInt2 + 1);
/*      */       case 2:
/* 1066 */         str = TextComponent.this.getText();
/* 1067 */         localBreakIterator = BreakIterator.getWordInstance();
/* 1068 */         localBreakIterator.setText(str);
/* 1069 */         i = localBreakIterator.following(paramInt2);
/* 1070 */         return str.substring(localBreakIterator.previous(), i);
/*      */       case 3:
/* 1073 */         str = TextComponent.this.getText();
/* 1074 */         localBreakIterator = BreakIterator.getSentenceInstance();
/* 1075 */         localBreakIterator.setText(str);
/* 1076 */         i = localBreakIterator.following(paramInt2);
/* 1077 */         return str.substring(localBreakIterator.previous(), i);
/*      */       }
/*      */ 
/* 1080 */       return null;
/*      */     }
/*      */ 
/*      */     private int findWordLimit(int paramInt, BreakIterator paramBreakIterator, boolean paramBoolean, String paramString)
/*      */     {
/* 1098 */       int i = paramBoolean == true ? paramBreakIterator.following(paramInt) : paramBreakIterator.preceding(paramInt);
/*      */ 
/* 1100 */       int j = paramBoolean == true ? paramBreakIterator.next() : paramBreakIterator.previous();
/*      */ 
/* 1102 */       while (j != -1) {
/* 1103 */         for (int k = Math.min(i, j); k < Math.max(i, j); k++) {
/* 1104 */           if (Character.isLetter(paramString.charAt(k))) {
/* 1105 */             return i;
/*      */           }
/*      */         }
/* 1108 */         i = j;
/* 1109 */         j = paramBoolean == true ? paramBreakIterator.next() : paramBreakIterator.previous();
/*      */       }
/*      */ 
/* 1112 */       return -1;
/*      */     }
/*      */ 
/*      */     public String getAfterIndex(int paramInt1, int paramInt2)
/*      */     {
/* 1125 */       if ((paramInt2 < 0) || (paramInt2 >= TextComponent.this.getText().length()))
/* 1126 */         return null;
/*      */       String str;
/*      */       BreakIterator localBreakIterator;
/*      */       int i;
/*      */       int j;
/* 1128 */       switch (paramInt1) {
/*      */       case 1:
/* 1130 */         if (paramInt2 + 1 >= TextComponent.this.getText().length()) {
/* 1131 */           return null;
/*      */         }
/* 1133 */         return TextComponent.this.getText().substring(paramInt2 + 1, paramInt2 + 2);
/*      */       case 2:
/* 1135 */         str = TextComponent.this.getText();
/* 1136 */         localBreakIterator = BreakIterator.getWordInstance();
/* 1137 */         localBreakIterator.setText(str);
/* 1138 */         i = findWordLimit(paramInt2, localBreakIterator, true, str);
/* 1139 */         if ((i == -1) || (i >= str.length())) {
/* 1140 */           return null;
/*      */         }
/* 1142 */         j = localBreakIterator.following(i);
/* 1143 */         if ((j == -1) || (j >= str.length())) {
/* 1144 */           return null;
/*      */         }
/* 1146 */         return str.substring(i, j);
/*      */       case 3:
/* 1149 */         str = TextComponent.this.getText();
/* 1150 */         localBreakIterator = BreakIterator.getSentenceInstance();
/* 1151 */         localBreakIterator.setText(str);
/* 1152 */         i = localBreakIterator.following(paramInt2);
/* 1153 */         if ((i == -1) || (i >= str.length())) {
/* 1154 */           return null;
/*      */         }
/* 1156 */         j = localBreakIterator.following(i);
/* 1157 */         if ((j == -1) || (j >= str.length())) {
/* 1158 */           return null;
/*      */         }
/* 1160 */         return str.substring(i, j);
/*      */       }
/*      */ 
/* 1163 */       return null;
/*      */     }
/*      */ 
/*      */     public String getBeforeIndex(int paramInt1, int paramInt2)
/*      */     {
/* 1178 */       if ((paramInt2 < 0) || (paramInt2 > TextComponent.this.getText().length() - 1))
/* 1179 */         return null;
/*      */       String str;
/*      */       BreakIterator localBreakIterator;
/*      */       int i;
/*      */       int j;
/* 1181 */       switch (paramInt1) {
/*      */       case 1:
/* 1183 */         if (paramInt2 == 0) {
/* 1184 */           return null;
/*      */         }
/* 1186 */         return TextComponent.this.getText().substring(paramInt2 - 1, paramInt2);
/*      */       case 2:
/* 1188 */         str = TextComponent.this.getText();
/* 1189 */         localBreakIterator = BreakIterator.getWordInstance();
/* 1190 */         localBreakIterator.setText(str);
/* 1191 */         i = findWordLimit(paramInt2, localBreakIterator, false, str);
/* 1192 */         if (i == -1) {
/* 1193 */           return null;
/*      */         }
/* 1195 */         j = localBreakIterator.preceding(i);
/* 1196 */         if (j == -1) {
/* 1197 */           return null;
/*      */         }
/* 1199 */         return str.substring(j, i);
/*      */       case 3:
/* 1202 */         str = TextComponent.this.getText();
/* 1203 */         localBreakIterator = BreakIterator.getSentenceInstance();
/* 1204 */         localBreakIterator.setText(str);
/* 1205 */         i = localBreakIterator.following(paramInt2);
/* 1206 */         i = localBreakIterator.previous();
/* 1207 */         j = localBreakIterator.previous();
/* 1208 */         if (j == -1) {
/* 1209 */           return null;
/*      */         }
/* 1211 */         return str.substring(j, i);
/*      */       }
/*      */ 
/* 1214 */       return null;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.TextComponent
 * JD-Core Version:    0.6.2
 */