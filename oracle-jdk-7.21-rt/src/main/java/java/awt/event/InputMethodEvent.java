/*     */ package java.awt.event;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Component;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.font.TextHitInfo;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.text.AttributedCharacterIterator;
/*     */ 
/*     */ public class InputMethodEvent extends AWTEvent
/*     */ {
/*     */   private static final long serialVersionUID = 4727190874778922661L;
/*     */   public static final int INPUT_METHOD_FIRST = 1100;
/*     */   public static final int INPUT_METHOD_TEXT_CHANGED = 1100;
/*     */   public static final int CARET_POSITION_CHANGED = 1101;
/*     */   public static final int INPUT_METHOD_LAST = 1101;
/*     */   long when;
/*     */   private transient AttributedCharacterIterator text;
/*     */   private transient int committedCharacterCount;
/*     */   private transient TextHitInfo caret;
/*     */   private transient TextHitInfo visiblePosition;
/*     */ 
/*     */   public InputMethodEvent(Component paramComponent, int paramInt1, long paramLong, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt2, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2)
/*     */   {
/* 150 */     super(paramComponent, paramInt1);
/* 151 */     if ((paramInt1 < 1100) || (paramInt1 > 1101)) {
/* 152 */       throw new IllegalArgumentException("id outside of valid range");
/*     */     }
/*     */ 
/* 155 */     if ((paramInt1 == 1101) && (paramAttributedCharacterIterator != null)) {
/* 156 */       throw new IllegalArgumentException("text must be null for CARET_POSITION_CHANGED");
/*     */     }
/*     */ 
/* 159 */     this.when = paramLong;
/* 160 */     this.text = paramAttributedCharacterIterator;
/* 161 */     int i = 0;
/* 162 */     if (paramAttributedCharacterIterator != null) {
/* 163 */       i = paramAttributedCharacterIterator.getEndIndex() - paramAttributedCharacterIterator.getBeginIndex();
/*     */     }
/*     */ 
/* 166 */     if ((paramInt2 < 0) || (paramInt2 > i)) {
/* 167 */       throw new IllegalArgumentException("committedCharacterCount outside of valid range");
/*     */     }
/* 169 */     this.committedCharacterCount = paramInt2;
/*     */ 
/* 171 */     this.caret = paramTextHitInfo1;
/* 172 */     this.visiblePosition = paramTextHitInfo2;
/*     */   }
/*     */ 
/*     */   public InputMethodEvent(Component paramComponent, int paramInt1, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt2, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2)
/*     */   {
/* 220 */     this(paramComponent, paramInt1, EventQueue.getMostRecentEventTime(), paramAttributedCharacterIterator, paramInt2, paramTextHitInfo1, paramTextHitInfo2);
/*     */   }
/*     */ 
/*     */   public InputMethodEvent(Component paramComponent, int paramInt, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2)
/*     */   {
/* 261 */     this(paramComponent, paramInt, EventQueue.getMostRecentEventTime(), null, 0, paramTextHitInfo1, paramTextHitInfo2);
/*     */   }
/*     */ 
/*     */   public AttributedCharacterIterator getText()
/*     */   {
/* 275 */     return this.text;
/*     */   }
/*     */ 
/*     */   public int getCommittedCharacterCount()
/*     */   {
/* 282 */     return this.committedCharacterCount;
/*     */   }
/*     */ 
/*     */   public TextHitInfo getCaret()
/*     */   {
/* 298 */     return this.caret;
/*     */   }
/*     */ 
/*     */   public TextHitInfo getVisiblePosition()
/*     */   {
/* 314 */     return this.visiblePosition;
/*     */   }
/*     */ 
/*     */   public void consume()
/*     */   {
/* 322 */     this.consumed = true;
/*     */   }
/*     */ 
/*     */   public boolean isConsumed()
/*     */   {
/* 330 */     return this.consumed;
/*     */   }
/*     */ 
/*     */   public long getWhen()
/*     */   {
/* 340 */     return this.when;
/*     */   }
/*     */ 
/*     */   public String paramString()
/*     */   {
/*     */     String str1;
/* 355 */     switch (this.id) {
/*     */     case 1100:
/* 357 */       str1 = "INPUT_METHOD_TEXT_CHANGED";
/* 358 */       break;
/*     */     case 1101:
/* 360 */       str1 = "CARET_POSITION_CHANGED";
/* 361 */       break;
/*     */     default:
/* 363 */       str1 = "unknown type";
/*     */     }
/*     */     String str2;
/* 367 */     if (this.text == null) {
/* 368 */       str2 = "no text";
/*     */     } else {
/* 370 */       localObject = new StringBuilder("\"");
/* 371 */       int i = this.committedCharacterCount;
/* 372 */       int j = this.text.first();
/* 373 */       while (i-- > 0) {
/* 374 */         ((StringBuilder)localObject).append(j);
/* 375 */         j = this.text.next();
/*     */       }
/* 377 */       ((StringBuilder)localObject).append("\" + \"");
/* 378 */       while (j != 65535) {
/* 379 */         ((StringBuilder)localObject).append(j);
/* 380 */         int k = this.text.next();
/*     */       }
/* 382 */       ((StringBuilder)localObject).append("\"");
/* 383 */       str2 = ((StringBuilder)localObject).toString();
/*     */     }
/*     */ 
/* 386 */     Object localObject = this.committedCharacterCount + " characters committed";
/*     */     String str3;
/* 389 */     if (this.caret == null)
/* 390 */       str3 = "no caret";
/*     */     else
/* 392 */       str3 = "caret: " + this.caret.toString();
/*     */     String str4;
/* 396 */     if (this.visiblePosition == null)
/* 397 */       str4 = "no visible position";
/*     */     else {
/* 399 */       str4 = "visible position: " + this.visiblePosition.toString();
/*     */     }
/*     */ 
/* 402 */     return str1 + ", " + str2 + ", " + (String)localObject + ", " + str3 + ", " + str4;
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 411 */     paramObjectInputStream.defaultReadObject();
/* 412 */     if (this.when == 0L)
/* 413 */       this.when = EventQueue.getMostRecentEventTime();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.event.InputMethodEvent
 * JD-Core Version:    0.6.2
 */