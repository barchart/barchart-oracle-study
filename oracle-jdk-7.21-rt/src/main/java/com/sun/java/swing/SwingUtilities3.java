/*     */ package com.sun.java.swing;
/*     */ 
/*     */ import java.applet.Applet;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.Window;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.concurrent.Callable;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.RepaintManager;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.EventQueueDelegate;
/*     */ import sun.awt.EventQueueDelegate.Delegate;
/*     */ 
/*     */ public class SwingUtilities3
/*     */ {
/*  59 */   private static final Object DELEGATE_REPAINT_MANAGER_KEY = new StringBuilder("DelegateRepaintManagerKey");
/*     */ 
/*  77 */   private static final Map<Container, Boolean> vsyncedMap = Collections.synchronizedMap(new WeakHashMap());
/*     */ 
/*     */   public static void setDelegateRepaintManager(JComponent paramJComponent, RepaintManager paramRepaintManager)
/*     */   {
/*  70 */     AppContext.getAppContext().put(DELEGATE_REPAINT_MANAGER_KEY, Boolean.TRUE);
/*     */ 
/*  73 */     paramJComponent.putClientProperty(DELEGATE_REPAINT_MANAGER_KEY, paramRepaintManager);
/*     */   }
/*     */ 
/*     */   public static void setVsyncRequested(Container paramContainer, boolean paramBoolean)
/*     */   {
/*  95 */     assert (((paramContainer instanceof Applet)) || ((paramContainer instanceof Window)));
/*  96 */     if (paramBoolean)
/*  97 */       vsyncedMap.put(paramContainer, Boolean.TRUE);
/*     */     else
/*  99 */       vsyncedMap.remove(paramContainer);
/*     */   }
/*     */ 
/*     */   public static boolean isVsyncRequested(Container paramContainer)
/*     */   {
/* 110 */     assert (((paramContainer instanceof Applet)) || ((paramContainer instanceof Window)));
/* 111 */     return Boolean.TRUE == vsyncedMap.get(paramContainer);
/*     */   }
/*     */ 
/*     */   public static RepaintManager getDelegateRepaintManager(Component paramComponent)
/*     */   {
/* 119 */     RepaintManager localRepaintManager = null;
/* 120 */     if (Boolean.TRUE == AppContext.getAppContext().get(DELEGATE_REPAINT_MANAGER_KEY))
/*     */     {
/* 122 */       while ((localRepaintManager == null) && (paramComponent != null))
/*     */       {
/* 124 */         while ((paramComponent != null) && (!(paramComponent instanceof JComponent))) {
/* 125 */           paramComponent = paramComponent.getParent();
/*     */         }
/* 127 */         if (paramComponent != null) {
/* 128 */           localRepaintManager = (RepaintManager)((JComponent)paramComponent).getClientProperty(DELEGATE_REPAINT_MANAGER_KEY);
/*     */ 
/* 131 */           paramComponent = paramComponent.getParent();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 136 */     return localRepaintManager;
/*     */   }
/*     */ 
/*     */   public static void setEventQueueDelegate(Map<String, Map<String, Object>> paramMap)
/*     */   {
/* 145 */     EventQueueDelegate.setDelegate(new EventQueueDelegateFromMap(paramMap));
/*     */   }
/*     */   private static class EventQueueDelegateFromMap implements EventQueueDelegate.Delegate {
/*     */     private final AWTEvent[] afterDispatchEventArgument;
/*     */     private final Object[] afterDispatchHandleArgument;
/*     */     private final Callable<Void> afterDispatchCallable;
/*     */     private final AWTEvent[] beforeDispatchEventArgument;
/*     */     private final Callable<Object> beforeDispatchCallable;
/*     */     private final EventQueue[] getNextEventEventQueueArgument;
/*     */     private final Callable<AWTEvent> getNextEventCallable;
/*     */ 
/* 162 */     public EventQueueDelegateFromMap(Map<String, Map<String, Object>> paramMap) { Map localMap = (Map)paramMap.get("afterDispatch");
/* 163 */       this.afterDispatchEventArgument = ((AWTEvent[])localMap.get("event"));
/* 164 */       this.afterDispatchHandleArgument = ((Object[])localMap.get("handle"));
/* 165 */       this.afterDispatchCallable = ((Callable)localMap.get("method"));
/*     */ 
/* 167 */       localMap = (Map)paramMap.get("beforeDispatch");
/* 168 */       this.beforeDispatchEventArgument = ((AWTEvent[])localMap.get("event"));
/* 169 */       this.beforeDispatchCallable = ((Callable)localMap.get("method"));
/*     */ 
/* 171 */       localMap = (Map)paramMap.get("getNextEvent");
/* 172 */       this.getNextEventEventQueueArgument = ((EventQueue[])localMap.get("eventQueue"));
/*     */ 
/* 174 */       this.getNextEventCallable = ((Callable)localMap.get("method")); }
/*     */ 
/*     */     public void afterDispatch(AWTEvent paramAWTEvent, Object paramObject)
/*     */       throws InterruptedException
/*     */     {
/* 179 */       this.afterDispatchEventArgument[0] = paramAWTEvent;
/* 180 */       this.afterDispatchHandleArgument[0] = paramObject;
/*     */       try {
/* 182 */         this.afterDispatchCallable.call();
/*     */       } catch (InterruptedException localInterruptedException) {
/* 184 */         throw localInterruptedException;
/*     */       } catch (RuntimeException localRuntimeException) {
/* 186 */         throw localRuntimeException;
/*     */       } catch (Exception localException) {
/* 188 */         throw new RuntimeException(localException);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Object beforeDispatch(AWTEvent paramAWTEvent) throws InterruptedException
/*     */     {
/* 194 */       this.beforeDispatchEventArgument[0] = paramAWTEvent;
/*     */       try {
/* 196 */         return this.beforeDispatchCallable.call();
/*     */       } catch (InterruptedException localInterruptedException) {
/* 198 */         throw localInterruptedException;
/*     */       } catch (RuntimeException localRuntimeException) {
/* 200 */         throw localRuntimeException;
/*     */       } catch (Exception localException) {
/* 202 */         throw new RuntimeException(localException);
/*     */       }
/*     */     }
/*     */ 
/*     */     public AWTEvent getNextEvent(EventQueue paramEventQueue) throws InterruptedException
/*     */     {
/* 208 */       this.getNextEventEventQueueArgument[0] = paramEventQueue;
/*     */       try {
/* 210 */         return (AWTEvent)this.getNextEventCallable.call();
/*     */       } catch (InterruptedException localInterruptedException) {
/* 212 */         throw localInterruptedException;
/*     */       } catch (RuntimeException localRuntimeException) {
/* 214 */         throw localRuntimeException;
/*     */       } catch (Exception localException) {
/* 216 */         throw new RuntimeException(localException);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.SwingUtilities3
 * JD-Core Version:    0.6.2
 */