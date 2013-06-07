/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import javax.sound.sampled.Control;
/*     */ import javax.sound.sampled.Line;
/*     */ import javax.sound.sampled.Line.Info;
/*     */ import javax.sound.sampled.LineUnavailableException;
/*     */ import javax.sound.sampled.Mixer;
/*     */ import javax.sound.sampled.Mixer.Info;
/*     */ 
/*     */ abstract class AbstractMixer extends AbstractLine
/*     */   implements Mixer
/*     */ {
/*     */   protected static final int PCM = 0;
/*     */   protected static final int ULAW = 1;
/*     */   protected static final int ALAW = 2;
/*     */   private final Mixer.Info mixerInfo;
/*     */   protected Line.Info[] sourceLineInfo;
/*     */   protected Line.Info[] targetLineInfo;
/*  75 */   private boolean started = false;
/*     */ 
/*  82 */   private boolean manuallyOpened = false;
/*     */ 
/*  98 */   protected Vector sourceLines = new Vector();
/*     */ 
/* 104 */   protected Vector targetLines = new Vector();
/*     */ 
/*     */   protected AbstractMixer(Mixer.Info paramInfo, Control[] paramArrayOfControl, Line.Info[] paramArrayOfInfo1, Line.Info[] paramArrayOfInfo2)
/*     */   {
/* 118 */     super(new Line.Info(Mixer.class), null, paramArrayOfControl);
/*     */ 
/* 121 */     this.mixer = this;
/* 122 */     if (paramArrayOfControl == null) {
/* 123 */       paramArrayOfControl = new Control[0];
/*     */     }
/*     */ 
/* 127 */     this.mixerInfo = paramInfo;
/* 128 */     this.sourceLineInfo = paramArrayOfInfo1;
/* 129 */     this.targetLineInfo = paramArrayOfInfo2;
/*     */   }
/*     */ 
/*     */   public Mixer.Info getMixerInfo()
/*     */   {
/* 137 */     return this.mixerInfo;
/*     */   }
/*     */ 
/*     */   public Line.Info[] getSourceLineInfo()
/*     */   {
/* 142 */     Line.Info[] arrayOfInfo = new Line.Info[this.sourceLineInfo.length];
/* 143 */     System.arraycopy(this.sourceLineInfo, 0, arrayOfInfo, 0, this.sourceLineInfo.length);
/* 144 */     return arrayOfInfo;
/*     */   }
/*     */ 
/*     */   public Line.Info[] getTargetLineInfo()
/*     */   {
/* 150 */     Line.Info[] arrayOfInfo = new Line.Info[this.targetLineInfo.length];
/* 151 */     System.arraycopy(this.targetLineInfo, 0, arrayOfInfo, 0, this.targetLineInfo.length);
/* 152 */     return arrayOfInfo;
/*     */   }
/*     */ 
/*     */   public Line.Info[] getSourceLineInfo(Line.Info paramInfo)
/*     */   {
/* 159 */     Vector localVector = new Vector();
/*     */ 
/* 161 */     for (int i = 0; i < this.sourceLineInfo.length; i++)
/*     */     {
/* 163 */       if (paramInfo.matches(this.sourceLineInfo[i])) {
/* 164 */         localVector.addElement(this.sourceLineInfo[i]);
/*     */       }
/*     */     }
/*     */ 
/* 168 */     Line.Info[] arrayOfInfo = new Line.Info[localVector.size()];
/* 169 */     for (i = 0; i < arrayOfInfo.length; i++) {
/* 170 */       arrayOfInfo[i] = ((Line.Info)localVector.elementAt(i));
/*     */     }
/*     */ 
/* 173 */     return arrayOfInfo;
/*     */   }
/*     */ 
/*     */   public Line.Info[] getTargetLineInfo(Line.Info paramInfo)
/*     */   {
/* 180 */     Vector localVector = new Vector();
/*     */ 
/* 182 */     for (int i = 0; i < this.targetLineInfo.length; i++)
/*     */     {
/* 184 */       if (paramInfo.matches(this.targetLineInfo[i])) {
/* 185 */         localVector.addElement(this.targetLineInfo[i]);
/*     */       }
/*     */     }
/*     */ 
/* 189 */     Line.Info[] arrayOfInfo = new Line.Info[localVector.size()];
/* 190 */     for (i = 0; i < arrayOfInfo.length; i++) {
/* 191 */       arrayOfInfo[i] = ((Line.Info)localVector.elementAt(i));
/*     */     }
/*     */ 
/* 194 */     return arrayOfInfo;
/*     */   }
/*     */ 
/*     */   public boolean isLineSupported(Line.Info paramInfo)
/*     */   {
/* 202 */     for (int i = 0; i < this.sourceLineInfo.length; i++)
/*     */     {
/* 204 */       if (paramInfo.matches(this.sourceLineInfo[i])) {
/* 205 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 209 */     for (i = 0; i < this.targetLineInfo.length; i++)
/*     */     {
/* 211 */       if (paramInfo.matches(this.targetLineInfo[i])) {
/* 212 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 216 */     return false;
/*     */   }
/*     */ 
/*     */   public abstract Line getLine(Line.Info paramInfo)
/*     */     throws LineUnavailableException;
/*     */ 
/*     */   public abstract int getMaxLines(Line.Info paramInfo);
/*     */ 
/*     */   protected abstract void implOpen()
/*     */     throws LineUnavailableException;
/*     */ 
/*     */   protected abstract void implStart();
/*     */ 
/*     */   protected abstract void implStop();
/*     */ 
/*     */   protected abstract void implClose();
/*     */ 
/*     */   public Line[] getSourceLines()
/*     */   {
/*     */     Line[] arrayOfLine;
/* 234 */     synchronized (this.sourceLines)
/*     */     {
/* 236 */       arrayOfLine = new Line[this.sourceLines.size()];
/*     */ 
/* 238 */       for (int i = 0; i < arrayOfLine.length; i++) {
/* 239 */         arrayOfLine[i] = ((Line)this.sourceLines.elementAt(i));
/*     */       }
/*     */     }
/*     */ 
/* 243 */     return arrayOfLine;
/*     */   }
/*     */ 
/*     */   public Line[] getTargetLines()
/*     */   {
/*     */     Line[] arrayOfLine;
/* 251 */     synchronized (this.targetLines)
/*     */     {
/* 253 */       arrayOfLine = new Line[this.targetLines.size()];
/*     */ 
/* 255 */       for (int i = 0; i < arrayOfLine.length; i++) {
/* 256 */         arrayOfLine[i] = ((Line)this.targetLines.elementAt(i));
/*     */       }
/*     */     }
/*     */ 
/* 260 */     return arrayOfLine;
/*     */   }
/*     */ 
/*     */   public void synchronize(Line[] paramArrayOfLine, boolean paramBoolean)
/*     */   {
/* 268 */     throw new IllegalArgumentException("Synchronization not supported by this mixer.");
/*     */   }
/*     */ 
/*     */   public void unsynchronize(Line[] paramArrayOfLine)
/*     */   {
/* 276 */     throw new IllegalArgumentException("Synchronization not supported by this mixer.");
/*     */   }
/*     */ 
/*     */   public boolean isSynchronizationSupported(Line[] paramArrayOfLine, boolean paramBoolean)
/*     */   {
/* 284 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized void open()
/*     */     throws LineUnavailableException
/*     */   {
/* 294 */     open(true);
/*     */   }
/*     */ 
/*     */   protected synchronized void open(boolean paramBoolean)
/*     */     throws LineUnavailableException
/*     */   {
/* 302 */     if (!isOpen()) {
/* 303 */       implOpen();
/*     */ 
/* 305 */       setOpen(true);
/* 306 */       if (paramBoolean)
/* 307 */         this.manuallyOpened = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void open(Line paramLine)
/*     */     throws LineUnavailableException
/*     */   {
/* 330 */     if (equals(paramLine))
/*     */     {
/* 332 */       return;
/*     */     }
/*     */ 
/* 336 */     if (isSourceLine(paramLine.getLineInfo())) {
/* 337 */       if (!this.sourceLines.contains(paramLine))
/*     */       {
/* 340 */         open(false);
/*     */ 
/* 343 */         this.sourceLines.addElement(paramLine);
/*     */       }
/*     */ 
/*     */     }
/* 347 */     else if ((isTargetLine(paramLine.getLineInfo())) && 
/* 348 */       (!this.targetLines.contains(paramLine)))
/*     */     {
/* 351 */       open(false);
/*     */ 
/* 354 */       this.targetLines.addElement(paramLine);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void close(Line paramLine)
/*     */   {
/* 375 */     if (equals(paramLine))
/*     */     {
/* 377 */       return;
/*     */     }
/*     */ 
/* 380 */     this.sourceLines.removeElement(paramLine);
/* 381 */     this.targetLines.removeElement(paramLine);
/*     */ 
/* 387 */     if ((this.sourceLines.isEmpty()) && (this.targetLines.isEmpty()) && (!this.manuallyOpened))
/*     */     {
/* 389 */       close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 401 */     if (isOpen())
/*     */     {
/* 403 */       Line[] arrayOfLine = getSourceLines();
/* 404 */       for (int i = 0; i < arrayOfLine.length; i++) {
/* 405 */         arrayOfLine[i].close();
/*     */       }
/*     */ 
/* 409 */       arrayOfLine = getTargetLines();
/* 410 */       for (i = 0; i < arrayOfLine.length; i++) {
/* 411 */         arrayOfLine[i].close();
/*     */       }
/*     */ 
/* 414 */       implClose();
/*     */ 
/* 417 */       setOpen(false);
/*     */     }
/* 419 */     this.manuallyOpened = false;
/*     */   }
/*     */ 
/*     */   protected synchronized void start(Line paramLine)
/*     */   {
/* 431 */     if (equals(paramLine))
/*     */     {
/* 433 */       return;
/*     */     }
/*     */ 
/* 437 */     if (!this.started)
/*     */     {
/* 439 */       implStart();
/* 440 */       this.started = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void stop(Line paramLine)
/*     */   {
/* 455 */     if (equals(paramLine))
/*     */     {
/* 457 */       return;
/*     */     }
/*     */ 
/* 460 */     Vector localVector1 = (Vector)this.sourceLines.clone();
/* 461 */     for (int i = 0; i < localVector1.size(); i++)
/*     */     {
/* 466 */       if ((localVector1.elementAt(i) instanceof AbstractDataLine)) {
/* 467 */         AbstractDataLine localAbstractDataLine1 = (AbstractDataLine)localVector1.elementAt(i);
/* 468 */         if ((localAbstractDataLine1.isStartedRunning()) && (!localAbstractDataLine1.equals(paramLine)))
/*     */         {
/* 470 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 475 */     Vector localVector2 = (Vector)this.targetLines.clone();
/* 476 */     for (int j = 0; j < localVector2.size(); j++)
/*     */     {
/* 480 */       if ((localVector2.elementAt(j) instanceof AbstractDataLine)) {
/* 481 */         AbstractDataLine localAbstractDataLine2 = (AbstractDataLine)localVector2.elementAt(j);
/* 482 */         if ((localAbstractDataLine2.isStartedRunning()) && (!localAbstractDataLine2.equals(paramLine)))
/*     */         {
/* 484 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 491 */     this.started = false;
/* 492 */     implStop();
/*     */   }
/*     */ 
/*     */   boolean isSourceLine(Line.Info paramInfo)
/*     */   {
/* 506 */     for (int i = 0; i < this.sourceLineInfo.length; i++) {
/* 507 */       if (paramInfo.matches(this.sourceLineInfo[i])) {
/* 508 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 512 */     return false;
/*     */   }
/*     */ 
/*     */   boolean isTargetLine(Line.Info paramInfo)
/*     */   {
/* 523 */     for (int i = 0; i < this.targetLineInfo.length; i++) {
/* 524 */       if (paramInfo.matches(this.targetLineInfo[i])) {
/* 525 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 529 */     return false;
/*     */   }
/*     */ 
/*     */   Line.Info getLineInfo(Line.Info paramInfo)
/*     */   {
/* 539 */     if (paramInfo == null) {
/* 540 */       return null;
/*     */     }
/*     */ 
/* 545 */     for (int i = 0; i < this.sourceLineInfo.length; i++) {
/* 546 */       if (paramInfo.matches(this.sourceLineInfo[i])) {
/* 547 */         return this.sourceLineInfo[i];
/*     */       }
/*     */     }
/*     */ 
/* 551 */     for (i = 0; i < this.targetLineInfo.length; i++) {
/* 552 */       if (paramInfo.matches(this.targetLineInfo[i])) {
/* 553 */         return this.targetLineInfo[i];
/*     */       }
/*     */     }
/*     */ 
/* 557 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AbstractMixer
 * JD-Core Version:    0.6.2
 */