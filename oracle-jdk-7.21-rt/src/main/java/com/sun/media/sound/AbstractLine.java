/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import javax.sound.sampled.Control;
/*     */ import javax.sound.sampled.Control.Type;
/*     */ import javax.sound.sampled.Line;
/*     */ import javax.sound.sampled.Line.Info;
/*     */ import javax.sound.sampled.LineEvent;
/*     */ import javax.sound.sampled.LineEvent.Type;
/*     */ import javax.sound.sampled.LineListener;
/*     */ import javax.sound.sampled.LineUnavailableException;
/*     */ 
/*     */ abstract class AbstractLine
/*     */   implements Line
/*     */ {
/*     */   protected Line.Info info;
/*     */   protected Control[] controls;
/*     */   protected AbstractMixer mixer;
/*  49 */   private boolean open = false;
/*  50 */   private Vector listeners = new Vector();
/*     */ 
/*  64 */   private static final EventDispatcher eventDispatcher = new EventDispatcher();
/*     */ 
/*     */   protected AbstractLine(Line.Info paramInfo, AbstractMixer paramAbstractMixer, Control[] paramArrayOfControl)
/*     */   {
/*  76 */     if (paramArrayOfControl == null) {
/*  77 */       paramArrayOfControl = new Control[0];
/*     */     }
/*     */ 
/*  80 */     this.info = paramInfo;
/*  81 */     this.mixer = paramAbstractMixer;
/*  82 */     this.controls = paramArrayOfControl;
/*     */   }
/*     */ 
/*     */   public Line.Info getLineInfo()
/*     */   {
/*  89 */     return this.info;
/*     */   }
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/*  94 */     return this.open;
/*     */   }
/*     */ 
/*     */   public void addLineListener(LineListener paramLineListener)
/*     */   {
/* 100 */     synchronized (this.listeners) {
/* 101 */       if (!this.listeners.contains(paramLineListener))
/* 102 */         this.listeners.addElement(paramLineListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeLineListener(LineListener paramLineListener)
/*     */   {
/* 113 */     this.listeners.removeElement(paramLineListener);
/*     */   }
/*     */ 
/*     */   public Control[] getControls()
/*     */   {
/* 125 */     Control[] arrayOfControl = new Control[this.controls.length];
/*     */ 
/* 127 */     for (int i = 0; i < this.controls.length; i++) {
/* 128 */       arrayOfControl[i] = this.controls[i];
/*     */     }
/*     */ 
/* 131 */     return arrayOfControl;
/*     */   }
/*     */ 
/*     */   public boolean isControlSupported(Control.Type paramType)
/*     */   {
/* 138 */     if (paramType == null) {
/* 139 */       return false;
/*     */     }
/*     */ 
/* 142 */     for (int i = 0; i < this.controls.length; i++) {
/* 143 */       if (paramType == this.controls[i].getType()) {
/* 144 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 148 */     return false;
/*     */   }
/*     */ 
/*     */   public Control getControl(Control.Type paramType)
/*     */   {
/* 155 */     if (paramType != null)
/*     */     {
/* 157 */       for (int i = 0; i < this.controls.length; i++) {
/* 158 */         if (paramType == this.controls[i].getType()) {
/* 159 */           return this.controls[i];
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 164 */     throw new IllegalArgumentException("Unsupported control type: " + paramType);
/*     */   }
/*     */ 
/*     */   protected void setOpen(boolean paramBoolean)
/*     */   {
/* 179 */     int i = 0;
/* 180 */     long l = getLongFramePosition();
/*     */ 
/* 182 */     synchronized (this) {
/* 183 */       if (this.open != paramBoolean) {
/* 184 */         this.open = paramBoolean;
/* 185 */         i = 1;
/*     */       }
/*     */     }
/*     */ 
/* 189 */     if (i != 0)
/* 190 */       if (paramBoolean)
/* 191 */         sendEvents(new LineEvent(this, LineEvent.Type.OPEN, l));
/*     */       else
/* 193 */         sendEvents(new LineEvent(this, LineEvent.Type.CLOSE, l));
/*     */   }
/*     */ 
/*     */   protected void sendEvents(LineEvent paramLineEvent)
/*     */   {
/* 204 */     eventDispatcher.sendAudioEvents(paramLineEvent, this.listeners);
/*     */   }
/*     */ 
/*     */   public final int getFramePosition()
/*     */   {
/* 214 */     return (int)getLongFramePosition();
/*     */   }
/*     */ 
/*     */   public long getLongFramePosition()
/*     */   {
/* 223 */     return -1L;
/*     */   }
/*     */ 
/*     */   protected AbstractMixer getMixer()
/*     */   {
/* 231 */     return this.mixer;
/*     */   }
/*     */ 
/*     */   protected EventDispatcher getEventDispatcher() {
/* 235 */     return eventDispatcher;
/*     */   }
/*     */ 
/*     */   public abstract void open()
/*     */     throws LineUnavailableException;
/*     */ 
/*     */   public abstract void close();
/*     */ 
/*     */   static
/*     */   {
/*  65 */     eventDispatcher.start();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AbstractLine
 * JD-Core Version:    0.6.2
 */