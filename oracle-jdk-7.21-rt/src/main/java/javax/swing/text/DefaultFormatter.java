/*     */ package javax.swing.text;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.text.ParseException;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JFormattedTextField.AbstractFormatter;
/*     */ import javax.swing.plaf.TextUI;
/*     */ import sun.reflect.misc.ConstructorUtil;
/*     */ 
/*     */ public class DefaultFormatter extends JFormattedTextField.AbstractFormatter
/*     */   implements Cloneable, Serializable
/*     */ {
/*     */   private boolean allowsInvalid;
/*     */   private boolean overwriteMode;
/*     */   private boolean commitOnEdit;
/*     */   private Class<?> valueClass;
/*     */   private NavigationFilter navigationFilter;
/*     */   private DocumentFilter documentFilter;
/*     */   transient ReplaceHolder replaceHolder;
/*     */ 
/*     */   public DefaultFormatter()
/*     */   {
/*  89 */     this.overwriteMode = true;
/*  90 */     this.allowsInvalid = true;
/*     */   }
/*     */ 
/*     */   public void install(JFormattedTextField paramJFormattedTextField)
/*     */   {
/* 124 */     super.install(paramJFormattedTextField);
/* 125 */     positionCursorAtInitialLocation();
/*     */   }
/*     */ 
/*     */   public void setCommitsOnValidEdit(boolean paramBoolean)
/*     */   {
/* 143 */     this.commitOnEdit = paramBoolean;
/*     */   }
/*     */ 
/*     */   public boolean getCommitsOnValidEdit()
/*     */   {
/* 153 */     return this.commitOnEdit;
/*     */   }
/*     */ 
/*     */   public void setOverwriteMode(boolean paramBoolean)
/*     */   {
/* 164 */     this.overwriteMode = paramBoolean;
/*     */   }
/*     */ 
/*     */   public boolean getOverwriteMode()
/*     */   {
/* 173 */     return this.overwriteMode;
/*     */   }
/*     */ 
/*     */   public void setAllowsInvalid(boolean paramBoolean)
/*     */   {
/* 187 */     this.allowsInvalid = paramBoolean;
/*     */   }
/*     */ 
/*     */   public boolean getAllowsInvalid()
/*     */   {
/* 197 */     return this.allowsInvalid;
/*     */   }
/*     */ 
/*     */   public void setValueClass(Class<?> paramClass)
/*     */   {
/* 209 */     this.valueClass = paramClass;
/*     */   }
/*     */ 
/*     */   public Class<?> getValueClass()
/*     */   {
/* 218 */     return this.valueClass;
/*     */   }
/*     */ 
/*     */   public Object stringToValue(String paramString)
/*     */     throws ParseException
/*     */   {
/* 236 */     Class localClass = getValueClass();
/* 237 */     JFormattedTextField localJFormattedTextField = getFormattedTextField();
/*     */     Object localObject;
/* 239 */     if ((localClass == null) && (localJFormattedTextField != null)) {
/* 240 */       localObject = localJFormattedTextField.getValue();
/*     */ 
/* 242 */       if (localObject != null) {
/* 243 */         localClass = localObject.getClass();
/*     */       }
/*     */     }
/* 246 */     if (localClass != null)
/*     */     {
/*     */       try
/*     */       {
/* 250 */         localObject = ConstructorUtil.getConstructor(localClass, new Class[] { String.class });
/*     */       }
/*     */       catch (NoSuchMethodException localNoSuchMethodException) {
/* 253 */         localObject = null;
/*     */       }
/*     */ 
/* 256 */       if (localObject != null) {
/*     */         try {
/* 258 */           return ((Constructor)localObject).newInstance(new Object[] { paramString });
/*     */         } catch (Throwable localThrowable) {
/* 260 */           throw new ParseException("Error creating instance", 0);
/*     */         }
/*     */       }
/*     */     }
/* 264 */     return paramString;
/*     */   }
/*     */ 
/*     */   public String valueToString(Object paramObject)
/*     */     throws ParseException
/*     */   {
/* 276 */     if (paramObject == null) {
/* 277 */       return "";
/*     */     }
/* 279 */     return paramObject.toString();
/*     */   }
/*     */ 
/*     */   protected DocumentFilter getDocumentFilter()
/*     */   {
/* 289 */     if (this.documentFilter == null) {
/* 290 */       this.documentFilter = new DefaultDocumentFilter(null);
/*     */     }
/* 292 */     return this.documentFilter;
/*     */   }
/*     */ 
/*     */   protected NavigationFilter getNavigationFilter()
/*     */   {
/* 302 */     if (this.navigationFilter == null) {
/* 303 */       this.navigationFilter = new DefaultNavigationFilter(null);
/*     */     }
/* 305 */     return this.navigationFilter;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */     throws CloneNotSupportedException
/*     */   {
/* 314 */     DefaultFormatter localDefaultFormatter = (DefaultFormatter)super.clone();
/*     */ 
/* 316 */     localDefaultFormatter.navigationFilter = null;
/* 317 */     localDefaultFormatter.documentFilter = null;
/* 318 */     localDefaultFormatter.replaceHolder = null;
/* 319 */     return localDefaultFormatter;
/*     */   }
/*     */ 
/*     */   void positionCursorAtInitialLocation()
/*     */   {
/* 327 */     JFormattedTextField localJFormattedTextField = getFormattedTextField();
/* 328 */     if (localJFormattedTextField != null)
/* 329 */       localJFormattedTextField.setCaretPosition(getInitialVisualPosition());
/*     */   }
/*     */ 
/*     */   int getInitialVisualPosition()
/*     */   {
/* 338 */     return getNextNavigatableChar(0, 1);
/*     */   }
/*     */ 
/*     */   boolean isNavigatable(int paramInt)
/*     */   {
/* 348 */     return true;
/*     */   }
/*     */ 
/*     */   boolean isLegalInsertText(String paramString)
/*     */   {
/* 357 */     return true;
/*     */   }
/*     */ 
/*     */   private int getNextNavigatableChar(int paramInt1, int paramInt2)
/*     */   {
/* 365 */     int i = getFormattedTextField().getDocument().getLength();
/*     */ 
/* 367 */     while ((paramInt1 >= 0) && (paramInt1 < i)) {
/* 368 */       if (isNavigatable(paramInt1)) {
/* 369 */         return paramInt1;
/*     */       }
/* 371 */       paramInt1 += paramInt2;
/*     */     }
/* 373 */     return paramInt1;
/*     */   }
/*     */ 
/*     */   String getReplaceString(int paramInt1, int paramInt2, String paramString)
/*     */   {
/* 384 */     String str1 = getFormattedTextField().getText();
/*     */ 
/* 387 */     String str2 = str1.substring(0, paramInt1);
/* 388 */     if (paramString != null) {
/* 389 */       str2 = str2 + paramString;
/*     */     }
/* 391 */     if (paramInt1 + paramInt2 < str1.length()) {
/* 392 */       str2 = str2 + str1.substring(paramInt1 + paramInt2);
/*     */     }
/* 394 */     return str2;
/*     */   }
/*     */ 
/*     */   boolean isValidEdit(ReplaceHolder paramReplaceHolder)
/*     */   {
/* 403 */     if (!getAllowsInvalid()) {
/* 404 */       String str = getReplaceString(paramReplaceHolder.offset, paramReplaceHolder.length, paramReplaceHolder.text);
/*     */       try
/*     */       {
/* 407 */         paramReplaceHolder.value = stringToValue(str);
/*     */ 
/* 409 */         return true;
/*     */       } catch (ParseException localParseException) {
/* 411 */         return false;
/*     */       }
/*     */     }
/* 414 */     return true;
/*     */   }
/*     */ 
/*     */   void commitEdit()
/*     */     throws ParseException
/*     */   {
/* 421 */     JFormattedTextField localJFormattedTextField = getFormattedTextField();
/*     */ 
/* 423 */     if (localJFormattedTextField != null)
/* 424 */       localJFormattedTextField.commitEdit();
/*     */   }
/*     */ 
/*     */   void updateValue()
/*     */   {
/* 434 */     updateValue(null);
/*     */   }
/*     */ 
/*     */   void updateValue(Object paramObject)
/*     */   {
/*     */     try
/*     */     {
/* 444 */       if (paramObject == null) {
/* 445 */         String str = getFormattedTextField().getText();
/*     */ 
/* 447 */         paramObject = stringToValue(str);
/*     */       }
/*     */ 
/* 450 */       if (getCommitsOnValidEdit()) {
/* 451 */         commitEdit();
/*     */       }
/* 453 */       setEditValid(true);
/*     */     } catch (ParseException localParseException) {
/* 455 */       setEditValid(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   int getNextCursorPosition(int paramInt1, int paramInt2)
/*     */   {
/* 466 */     int i = getNextNavigatableChar(paramInt1, paramInt2);
/* 467 */     int j = getFormattedTextField().getDocument().getLength();
/*     */ 
/* 469 */     if (!getAllowsInvalid()) {
/* 470 */       if ((paramInt2 == -1) && (paramInt1 == i))
/*     */       {
/* 473 */         i = getNextNavigatableChar(i, 1);
/* 474 */         if (i >= j) {
/* 475 */           i = paramInt1;
/*     */         }
/*     */       }
/* 478 */       else if ((paramInt2 == 1) && (i >= j))
/*     */       {
/* 480 */         i = getNextNavigatableChar(j - 1, -1);
/* 481 */         if (i < j) {
/* 482 */           i++;
/*     */         }
/*     */       }
/*     */     }
/* 486 */     return i;
/*     */   }
/*     */ 
/*     */   void repositionCursor(int paramInt1, int paramInt2)
/*     */   {
/* 493 */     getFormattedTextField().getCaret().setDot(getNextCursorPosition(paramInt1, paramInt2));
/*     */   }
/*     */ 
/*     */   int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias)
/*     */     throws BadLocationException
/*     */   {
/* 505 */     int i = paramJTextComponent.getUI().getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
/*     */ 
/* 508 */     if (i == -1) {
/* 509 */       return -1;
/*     */     }
/* 511 */     if ((!getAllowsInvalid()) && ((paramInt2 == 3) || (paramInt2 == 7)))
/*     */     {
/* 513 */       int j = -1;
/*     */ 
/* 515 */       while ((!isNavigatable(i)) && (i != j)) {
/* 516 */         j = i;
/* 517 */         i = paramJTextComponent.getUI().getNextVisualPositionFrom(paramJTextComponent, i, paramBias, paramInt2, paramArrayOfBias);
/*     */       }
/*     */ 
/* 520 */       int k = getFormattedTextField().getDocument().getLength();
/* 521 */       if ((j == i) || (i == k)) {
/* 522 */         if (i == 0) {
/* 523 */           paramArrayOfBias[0] = Position.Bias.Forward;
/* 524 */           i = getInitialVisualPosition();
/*     */         }
/* 526 */         if ((i >= k) && (k > 0))
/*     */         {
/* 528 */           paramArrayOfBias[0] = Position.Bias.Forward;
/* 529 */           i = getNextNavigatableChar(k - 1, -1) + 1;
/*     */         }
/*     */       }
/*     */     }
/* 533 */     return i;
/*     */   }
/*     */ 
/*     */   boolean canReplace(ReplaceHolder paramReplaceHolder)
/*     */   {
/* 541 */     return isValidEdit(paramReplaceHolder);
/*     */   }
/*     */ 
/*     */   void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
/*     */     throws BadLocationException
/*     */   {
/* 550 */     ReplaceHolder localReplaceHolder = getReplaceHolder(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
/*     */ 
/* 552 */     replace(localReplaceHolder);
/*     */   }
/*     */ 
/*     */   boolean replace(ReplaceHolder paramReplaceHolder)
/*     */     throws BadLocationException
/*     */   {
/* 566 */     int i = 1;
/* 567 */     int j = 1;
/*     */ 
/* 569 */     if ((paramReplaceHolder.length > 0) && ((paramReplaceHolder.text == null) || (paramReplaceHolder.text.length() == 0)) && ((getFormattedTextField().getSelectionStart() != paramReplaceHolder.offset) || (paramReplaceHolder.length > 1)))
/*     */     {
/* 572 */       j = -1;
/*     */     }
/*     */ 
/* 575 */     if ((getOverwriteMode()) && (paramReplaceHolder.text != null) && (getFormattedTextField().getSelectedText() == null))
/*     */     {
/* 578 */       paramReplaceHolder.length = Math.min(Math.max(paramReplaceHolder.length, paramReplaceHolder.text.length()), paramReplaceHolder.fb.getDocument().getLength() - paramReplaceHolder.offset);
/*     */     }
/*     */ 
/* 581 */     if (((paramReplaceHolder.text != null) && (!isLegalInsertText(paramReplaceHolder.text))) || (!canReplace(paramReplaceHolder)) || ((paramReplaceHolder.length == 0) && ((paramReplaceHolder.text == null) || (paramReplaceHolder.text.length() == 0))))
/*     */     {
/* 584 */       i = 0;
/*     */     }
/* 586 */     if (i != 0) {
/* 587 */       int k = paramReplaceHolder.cursorPosition;
/*     */ 
/* 589 */       paramReplaceHolder.fb.replace(paramReplaceHolder.offset, paramReplaceHolder.length, paramReplaceHolder.text, paramReplaceHolder.attrs);
/* 590 */       if (k == -1) {
/* 591 */         k = paramReplaceHolder.offset;
/* 592 */         if ((j == 1) && (paramReplaceHolder.text != null)) {
/* 593 */           k = paramReplaceHolder.offset + paramReplaceHolder.text.length();
/*     */         }
/*     */       }
/* 596 */       updateValue(paramReplaceHolder.value);
/* 597 */       repositionCursor(k, j);
/* 598 */       return true;
/*     */     }
/*     */ 
/* 601 */     invalidEdit();
/*     */ 
/* 603 */     return false;
/*     */   }
/*     */ 
/*     */   void setDot(NavigationFilter.FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias)
/*     */   {
/* 611 */     paramFilterBypass.setDot(paramInt, paramBias);
/*     */   }
/*     */ 
/*     */   void moveDot(NavigationFilter.FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias)
/*     */   {
/* 620 */     paramFilterBypass.moveDot(paramInt, paramBias);
/*     */   }
/*     */ 
/*     */   ReplaceHolder getReplaceHolder(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
/*     */   {
/* 631 */     if (this.replaceHolder == null) {
/* 632 */       this.replaceHolder = new ReplaceHolder();
/*     */     }
/* 634 */     this.replaceHolder.reset(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
/* 635 */     return this.replaceHolder;
/*     */   }
/*     */ 
/*     */   private class DefaultDocumentFilter extends DocumentFilter
/*     */     implements Serializable
/*     */   {
/*     */     private DefaultDocumentFilter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void remove(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2)
/*     */       throws BadLocationException
/*     */     {
/* 727 */       JFormattedTextField localJFormattedTextField = DefaultFormatter.this.getFormattedTextField();
/* 728 */       if (localJFormattedTextField.composedTextExists())
/*     */       {
/* 730 */         paramFilterBypass.remove(paramInt1, paramInt2);
/*     */       }
/* 732 */       else DefaultFormatter.this.replace(paramFilterBypass, paramInt1, paramInt2, null, null);
/*     */     }
/*     */ 
/*     */     public void insertString(DocumentFilter.FilterBypass paramFilterBypass, int paramInt, String paramString, AttributeSet paramAttributeSet)
/*     */       throws BadLocationException
/*     */     {
/* 739 */       JFormattedTextField localJFormattedTextField = DefaultFormatter.this.getFormattedTextField();
/* 740 */       if ((localJFormattedTextField.composedTextExists()) || (Utilities.isComposedTextAttributeDefined(paramAttributeSet)))
/*     */       {
/* 743 */         paramFilterBypass.insertString(paramInt, paramString, paramAttributeSet);
/*     */       }
/* 745 */       else DefaultFormatter.this.replace(paramFilterBypass, paramInt, 0, paramString, paramAttributeSet);
/*     */     }
/*     */ 
/*     */     public void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
/*     */       throws BadLocationException
/*     */     {
/* 752 */       JFormattedTextField localJFormattedTextField = DefaultFormatter.this.getFormattedTextField();
/* 753 */       if ((localJFormattedTextField.composedTextExists()) || (Utilities.isComposedTextAttributeDefined(paramAttributeSet)))
/*     */       {
/* 756 */         paramFilterBypass.replace(paramInt1, paramInt2, paramString, paramAttributeSet);
/*     */       }
/* 758 */       else DefaultFormatter.this.replace(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class DefaultNavigationFilter extends NavigationFilter
/*     */     implements Serializable
/*     */   {
/*     */     private DefaultNavigationFilter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setDot(NavigationFilter.FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias)
/*     */     {
/* 683 */       JFormattedTextField localJFormattedTextField = DefaultFormatter.this.getFormattedTextField();
/* 684 */       if (localJFormattedTextField.composedTextExists())
/*     */       {
/* 686 */         paramFilterBypass.setDot(paramInt, paramBias);
/*     */       }
/* 688 */       else DefaultFormatter.this.setDot(paramFilterBypass, paramInt, paramBias);
/*     */     }
/*     */ 
/*     */     public void moveDot(NavigationFilter.FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias)
/*     */     {
/* 693 */       JFormattedTextField localJFormattedTextField = DefaultFormatter.this.getFormattedTextField();
/* 694 */       if (localJFormattedTextField.composedTextExists())
/*     */       {
/* 696 */         paramFilterBypass.moveDot(paramInt, paramBias);
/*     */       }
/* 698 */       else DefaultFormatter.this.moveDot(paramFilterBypass, paramInt, paramBias);
/*     */     }
/*     */ 
/*     */     public int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias)
/*     */       throws BadLocationException
/*     */     {
/* 707 */       if (paramJTextComponent.composedTextExists())
/*     */       {
/* 709 */         return paramJTextComponent.getUI().getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
/*     */       }
/*     */ 
/* 712 */       return DefaultFormatter.this.getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ReplaceHolder
/*     */   {
/*     */     DocumentFilter.FilterBypass fb;
/*     */     int offset;
/*     */     int length;
/*     */     String text;
/*     */     AttributeSet attrs;
/*     */     Object value;
/*     */     int cursorPosition;
/*     */ 
/*     */     void reset(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
/*     */     {
/* 665 */       this.fb = paramFilterBypass;
/* 666 */       this.offset = paramInt1;
/* 667 */       this.length = paramInt2;
/* 668 */       this.text = paramString;
/* 669 */       this.attrs = paramAttributeSet;
/* 670 */       this.value = null;
/* 671 */       this.cursorPosition = -1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.text.DefaultFormatter
 * JD-Core Version:    0.6.2
 */