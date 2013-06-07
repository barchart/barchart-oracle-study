/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.sound.midi.ControllerEventListener;
/*     */ import javax.sound.midi.MetaEventListener;
/*     */ import javax.sound.midi.MetaMessage;
/*     */ import javax.sound.midi.ShortMessage;
/*     */ import javax.sound.sampled.LineEvent;
/*     */ import javax.sound.sampled.LineListener;
/*     */ 
/*     */ class EventDispatcher
/*     */   implements Runnable
/*     */ {
/*     */   private static final int AUTO_CLOSE_TIME = 5000;
/*     */   private ArrayList eventQueue;
/*     */   private Thread thread;
/*     */   private ArrayList<ClipInfo> autoClosingClips;
/*     */   private ArrayList<LineMonitor> lineMonitors;
/*     */   static final int LINE_MONITOR_TIME = 400;
/*     */ 
/*     */   EventDispatcher()
/*     */   {
/*  64 */     this.eventQueue = new ArrayList();
/*     */ 
/*  70 */     this.thread = null;
/*     */ 
/*  76 */     this.autoClosingClips = new ArrayList();
/*     */ 
/*  81 */     this.lineMonitors = new ArrayList();
/*     */   }
/*     */ 
/*     */   synchronized void start()
/*     */   {
/*  94 */     if (this.thread == null)
/*  95 */       this.thread = JSSecurityManager.createThread(this, "Java Sound Event Dispatcher", true, -1, true);
/*     */   }
/*     */ 
/*     */   protected void processEvent(EventInfo paramEventInfo)
/*     */   {
/* 109 */     int i = paramEventInfo.getListenerCount();
/*     */     Object localObject;
/*     */     int j;
/* 112 */     if ((paramEventInfo.getEvent() instanceof LineEvent)) {
/* 113 */       localObject = (LineEvent)paramEventInfo.getEvent();
/*     */ 
/* 115 */       for (j = 0; j < i; j++)
/*     */         try {
/* 117 */           ((LineListener)paramEventInfo.getListener(j)).update((LineEvent)localObject);
/*     */         }
/*     */         catch (Throwable localThrowable1)
/*     */         {
/*     */         }
/* 122 */       return;
/*     */     }
/*     */ 
/* 126 */     if ((paramEventInfo.getEvent() instanceof MetaMessage)) {
/* 127 */       localObject = (MetaMessage)paramEventInfo.getEvent();
/* 128 */       for (j = 0; j < i; j++)
/*     */         try {
/* 130 */           ((MetaEventListener)paramEventInfo.getListener(j)).meta((MetaMessage)localObject);
/*     */         }
/*     */         catch (Throwable localThrowable2)
/*     */         {
/*     */         }
/* 135 */       return;
/*     */     }
/*     */ 
/* 139 */     if ((paramEventInfo.getEvent() instanceof ShortMessage)) {
/* 140 */       localObject = (ShortMessage)paramEventInfo.getEvent();
/* 141 */       j = ((ShortMessage)localObject).getStatus();
/*     */ 
/* 145 */       if ((j & 0xF0) == 176) {
/* 146 */         for (int k = 0; k < i; k++)
/*     */           try {
/* 148 */             ((ControllerEventListener)paramEventInfo.getListener(k)).controlChange((ShortMessage)localObject);
/*     */           }
/*     */           catch (Throwable localThrowable3)
/*     */           {
/*     */           }
/*     */       }
/* 154 */       return;
/*     */     }
/*     */ 
/* 157 */     Printer.err("Unknown event type: " + paramEventInfo.getEvent());
/*     */   }
/*     */ 
/*     */   protected void dispatchEvents()
/*     */   {
/* 171 */     EventInfo localEventInfo = null;
/*     */ 
/* 173 */     synchronized (this)
/*     */     {
/*     */       try
/*     */       {
/* 178 */         if (this.eventQueue.size() == 0)
/* 179 */           if ((this.autoClosingClips.size() > 0) || (this.lineMonitors.size() > 0)) {
/* 180 */             int i = 5000;
/* 181 */             if (this.lineMonitors.size() > 0) {
/* 182 */               i = 400;
/*     */             }
/* 184 */             wait(i);
/*     */           } else {
/* 186 */             wait();
/*     */           }
/*     */       }
/*     */       catch (InterruptedException localInterruptedException) {
/*     */       }
/* 191 */       if (this.eventQueue.size() > 0)
/*     */       {
/* 193 */         localEventInfo = (EventInfo)this.eventQueue.remove(0);
/*     */       }
/*     */     }
/*     */ 
/* 197 */     if (localEventInfo != null) {
/* 198 */       processEvent(localEventInfo);
/*     */     } else {
/* 200 */       if (this.autoClosingClips.size() > 0) {
/* 201 */         closeAutoClosingClips();
/*     */       }
/* 203 */       if (this.lineMonitors.size() > 0)
/* 204 */         monitorLines();
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void postEvent(EventInfo paramEventInfo)
/*     */   {
/* 214 */     this.eventQueue.add(paramEventInfo);
/* 215 */     notifyAll();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     while (true)
/*     */       try
/*     */       {
/* 226 */         dispatchEvents();
/*     */       }
/*     */       catch (Throwable localThrowable)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   void sendAudioEvents(Object paramObject, List paramList)
/*     */   {
/* 238 */     if ((paramList == null) || (paramList.size() == 0))
/*     */     {
/* 241 */       return;
/*     */     }
/*     */ 
/* 244 */     start();
/*     */ 
/* 246 */     EventInfo localEventInfo = new EventInfo(paramObject, paramList);
/* 247 */     postEvent(localEventInfo);
/*     */   }
/*     */ 
/*     */   private void closeAutoClosingClips()
/*     */   {
/* 258 */     synchronized (this.autoClosingClips)
/*     */     {
/* 260 */       long l = System.currentTimeMillis();
/* 261 */       for (int i = this.autoClosingClips.size() - 1; i >= 0; i--) {
/* 262 */         ClipInfo localClipInfo = (ClipInfo)this.autoClosingClips.get(i);
/* 263 */         if (localClipInfo.isExpired(l)) {
/* 264 */           AutoClosingClip localAutoClosingClip = localClipInfo.getClip();
/*     */ 
/* 266 */           if ((!localAutoClosingClip.isOpen()) || (!localAutoClosingClip.isAutoClosing()))
/*     */           {
/* 268 */             this.autoClosingClips.remove(i);
/*     */           }
/* 270 */           else if ((!localAutoClosingClip.isRunning()) && (!localAutoClosingClip.isActive()) && (localAutoClosingClip.isAutoClosing()))
/*     */           {
/* 272 */             localAutoClosingClip.close();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getAutoClosingClipIndex(AutoClosingClip paramAutoClosingClip)
/*     */   {
/* 287 */     synchronized (this.autoClosingClips) {
/* 288 */       for (int i = this.autoClosingClips.size() - 1; i >= 0; i--) {
/* 289 */         if (paramAutoClosingClip.equals(((ClipInfo)this.autoClosingClips.get(i)).getClip())) {
/* 290 */           return i;
/*     */         }
/*     */       }
/*     */     }
/* 294 */     return -1;
/*     */   }
/*     */ 
/*     */   void autoClosingClipOpened(AutoClosingClip paramAutoClosingClip)
/*     */   {
/* 302 */     int i = 0;
/* 303 */     synchronized (this.autoClosingClips) {
/* 304 */       i = getAutoClosingClipIndex(paramAutoClosingClip);
/* 305 */       if (i == -1)
/*     */       {
/* 307 */         this.autoClosingClips.add(new ClipInfo(paramAutoClosingClip));
/*     */       }
/*     */     }
/* 310 */     if (i == -1)
/* 311 */       synchronized (this)
/*     */       {
/* 316 */         notifyAll();
/*     */       }
/*     */   }
/*     */ 
/*     */   void autoClosingClipClosed(AutoClosingClip paramAutoClosingClip)
/*     */   {
/*     */   }
/*     */ 
/*     */   private void monitorLines()
/*     */   {
/* 338 */     synchronized (this.lineMonitors)
/*     */     {
/* 340 */       for (int i = 0; i < this.lineMonitors.size(); i++)
/* 341 */         ((LineMonitor)this.lineMonitors.get(i)).checkLine();
/*     */     }
/*     */   }
/*     */ 
/*     */   void addLineMonitor(LineMonitor paramLineMonitor)
/*     */   {
/* 353 */     synchronized (this.lineMonitors) {
/* 354 */       if (this.lineMonitors.indexOf(paramLineMonitor) >= 0)
/*     */       {
/* 356 */         return;
/*     */       }
/*     */ 
/* 359 */       this.lineMonitors.add(paramLineMonitor);
/*     */     }
/* 361 */     synchronized (this)
/*     */     {
/* 363 */       notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeLineMonitor(LineMonitor paramLineMonitor)
/*     */   {
/* 373 */     synchronized (this.lineMonitors) {
/* 374 */       if (this.lineMonitors.indexOf(paramLineMonitor) < 0)
/*     */       {
/* 376 */         return;
/*     */       }
/*     */ 
/* 379 */       this.lineMonitors.remove(paramLineMonitor);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ClipInfo
/*     */   {
/*     */     private AutoClosingClip clip;
/*     */     private long expiration;
/*     */ 
/*     */     ClipInfo(AutoClosingClip arg2)
/*     */     {
/*     */       Object localObject;
/* 431 */       this.clip = localObject;
/* 432 */       this.expiration = (System.currentTimeMillis() + 5000L);
/*     */     }
/*     */ 
/*     */     AutoClosingClip getClip() {
/* 436 */       return this.clip;
/*     */     }
/*     */ 
/*     */     boolean isExpired(long paramLong) {
/* 440 */       return paramLong > this.expiration;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EventInfo
/*     */   {
/*     */     private Object event;
/*     */     private Object[] listeners;
/*     */ 
/*     */     EventInfo(Object paramList, List arg3)
/*     */     {
/* 400 */       this.event = paramList;
/*     */       Object localObject;
/* 401 */       this.listeners = localObject.toArray();
/*     */     }
/*     */ 
/*     */     Object getEvent() {
/* 405 */       return this.event;
/*     */     }
/*     */ 
/*     */     int getListenerCount() {
/* 409 */       return this.listeners.length;
/*     */     }
/*     */ 
/*     */     Object getListener(int paramInt) {
/* 413 */       return this.listeners[paramInt];
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface LineMonitor
/*     */   {
/*     */     public abstract void checkLine();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.EventDispatcher
 * JD-Core Version:    0.6.2
 */