/*     */ package com.sun.java.swing.plaf.windows;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.Timer;
/*     */ import javax.swing.UIManager;
/*     */ import sun.awt.AppContext;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.swing.UIClientPropertyKey;
/*     */ 
/*     */ class AnimationController
/*     */   implements ActionListener, PropertyChangeListener
/*     */ {
/*  72 */   private static final boolean VISTA_ANIMATION_DISABLED = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("swing.disablevistaanimation"))).booleanValue();
/*     */ 
/*  76 */   private static final Object ANIMATION_CONTROLLER_KEY = new StringBuilder("ANIMATION_CONTROLLER_KEY");
/*     */ 
/*  79 */   private final Map<JComponent, Map<TMSchema.Part, AnimationState>> animationStateMap = new WeakHashMap();
/*     */ 
/*  84 */   private final Timer timer = new Timer(33, this);
/*     */ 
/*     */   private static synchronized AnimationController getAnimationController()
/*     */   {
/*  88 */     AppContext localAppContext = AppContext.getAppContext();
/*  89 */     Object localObject = localAppContext.get(ANIMATION_CONTROLLER_KEY);
/*  90 */     if (localObject == null) {
/*  91 */       localObject = new AnimationController();
/*  92 */       localAppContext.put(ANIMATION_CONTROLLER_KEY, localObject);
/*     */     }
/*  94 */     return (AnimationController)localObject;
/*     */   }
/*     */ 
/*     */   private AnimationController() {
/*  98 */     this.timer.setRepeats(true);
/*  99 */     this.timer.setCoalesce(true);
/*     */ 
/* 101 */     UIManager.addPropertyChangeListener(this);
/*     */   }
/*     */ 
/*     */   private static void triggerAnimation(JComponent paramJComponent, TMSchema.Part paramPart, TMSchema.State paramState)
/*     */   {
/* 106 */     if (((paramJComponent instanceof JTabbedPane)) || (paramPart == TMSchema.Part.TP_BUTTON))
/*     */     {
/* 113 */       return;
/*     */     }
/* 115 */     AnimationController localAnimationController = getAnimationController();
/*     */ 
/* 117 */     TMSchema.State localState = localAnimationController.getState(paramJComponent, paramPart);
/* 118 */     if (localState != paramState) {
/* 119 */       localAnimationController.putState(paramJComponent, paramPart, paramState);
/* 120 */       if (paramState == TMSchema.State.DEFAULTED)
/*     */       {
/* 123 */         localState = TMSchema.State.HOT;
/*     */       }
/* 125 */       if (localState != null)
/*     */       {
/*     */         long l;
/* 127 */         if (paramState == TMSchema.State.DEFAULTED)
/*     */         {
/* 131 */           l = 1000L;
/*     */         }
/* 133 */         else l = XPStyle.getXP().getThemeTransitionDuration(paramJComponent, paramPart, normalizeState(localState), normalizeState(paramState), TMSchema.Prop.TRANSITIONDURATIONS);
/*     */ 
/* 139 */         localAnimationController.startAnimation(paramJComponent, paramPart, localState, paramState, l);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static TMSchema.State normalizeState(TMSchema.State paramState)
/*     */   {
/*     */     TMSchema.State localState;
/* 149 */     switch (1.$SwitchMap$com$sun$java$swing$plaf$windows$TMSchema$State[paramState.ordinal()])
/*     */     {
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/* 155 */       localState = TMSchema.State.UPPRESSED;
/* 156 */       break;
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/* 163 */       localState = TMSchema.State.UPDISABLED;
/* 164 */       break;
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/* 171 */       localState = TMSchema.State.UPHOT;
/* 172 */       break;
/*     */     case 10:
/*     */     case 11:
/*     */     case 12:
/* 179 */       localState = TMSchema.State.UPNORMAL;
/* 180 */       break;
/*     */     default:
/* 183 */       localState = paramState;
/*     */     }
/*     */ 
/* 186 */     return localState;
/*     */   }
/*     */ 
/*     */   private synchronized TMSchema.State getState(JComponent paramJComponent, TMSchema.Part paramPart) {
/* 190 */     TMSchema.State localState = null;
/* 191 */     Object localObject = paramJComponent.getClientProperty(PartUIClientPropertyKey.getKey(paramPart));
/*     */ 
/* 193 */     if ((localObject instanceof TMSchema.State)) {
/* 194 */       localState = (TMSchema.State)localObject;
/*     */     }
/* 196 */     return localState;
/*     */   }
/*     */ 
/*     */   private synchronized void putState(JComponent paramJComponent, TMSchema.Part paramPart, TMSchema.State paramState)
/*     */   {
/* 201 */     paramJComponent.putClientProperty(PartUIClientPropertyKey.getKey(paramPart), paramState);
/*     */   }
/*     */ 
/*     */   private synchronized void startAnimation(JComponent paramJComponent, TMSchema.Part paramPart, TMSchema.State paramState1, TMSchema.State paramState2, long paramLong)
/*     */   {
/* 210 */     boolean bool = false;
/* 211 */     if (paramState2 == TMSchema.State.DEFAULTED) {
/* 212 */       bool = true;
/*     */     }
/* 214 */     Object localObject = (Map)this.animationStateMap.get(paramJComponent);
/* 215 */     if (paramLong <= 0L) {
/* 216 */       if (localObject != null) {
/* 217 */         ((Map)localObject).remove(paramPart);
/* 218 */         if (((Map)localObject).size() == 0) {
/* 219 */           this.animationStateMap.remove(paramJComponent);
/*     */         }
/*     */       }
/* 222 */       return;
/*     */     }
/* 224 */     if (localObject == null) {
/* 225 */       localObject = new EnumMap(TMSchema.Part.class);
/* 226 */       this.animationStateMap.put(paramJComponent, localObject);
/*     */     }
/* 228 */     ((Map)localObject).put(paramPart, new AnimationState(paramState1, paramLong, bool));
/*     */ 
/* 230 */     if (!this.timer.isRunning())
/* 231 */       this.timer.start();
/*     */   }
/*     */ 
/*     */   static void paintSkin(JComponent paramJComponent, XPStyle.Skin paramSkin, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState)
/*     */   {
/* 237 */     if (VISTA_ANIMATION_DISABLED) {
/* 238 */       paramSkin.paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
/* 239 */       return;
/*     */     }
/* 241 */     triggerAnimation(paramJComponent, paramSkin.part, paramState);
/* 242 */     AnimationController localAnimationController = getAnimationController();
/* 243 */     synchronized (localAnimationController) {
/* 244 */       AnimationState localAnimationState = null;
/* 245 */       Map localMap = (Map)localAnimationController.animationStateMap.get(paramJComponent);
/*     */ 
/* 247 */       if (localMap != null) {
/* 248 */         localAnimationState = (AnimationState)localMap.get(paramSkin.part);
/*     */       }
/* 250 */       if (localAnimationState != null)
/* 251 */         localAnimationState.paintSkin(paramSkin, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
/*     */       else
/* 253 */         paramSkin.paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*     */   {
/* 259 */     if (("lookAndFeel" == paramPropertyChangeEvent.getPropertyName()) && (!(paramPropertyChangeEvent.getNewValue() instanceof WindowsLookAndFeel)))
/*     */     {
/* 261 */       dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void actionPerformed(ActionEvent paramActionEvent) {
/* 266 */     ArrayList localArrayList1 = null;
/* 267 */     ArrayList localArrayList2 = null;
/* 268 */     for (Iterator localIterator1 = this.animationStateMap.keySet().iterator(); localIterator1.hasNext(); ) { localJComponent = (JComponent)localIterator1.next();
/* 269 */       localJComponent.repaint();
/* 270 */       if (localArrayList2 != null) {
/* 271 */         localArrayList2.clear();
/*     */       }
/* 273 */       localMap = (Map)this.animationStateMap.get(localJComponent);
/* 274 */       if ((!localJComponent.isShowing()) || (localMap == null) || (localMap.size() == 0))
/*     */       {
/* 277 */         if (localArrayList1 == null) {
/* 278 */           localArrayList1 = new ArrayList();
/*     */         }
/* 280 */         localArrayList1.add(localJComponent);
/*     */       }
/*     */       else {
/* 283 */         for (localIterator2 = localMap.keySet().iterator(); localIterator2.hasNext(); ) { localPart = (TMSchema.Part)localIterator2.next();
/* 284 */           if (((AnimationState)localMap.get(localPart)).isDone()) {
/* 285 */             if (localArrayList2 == null) {
/* 286 */               localArrayList2 = new ArrayList();
/*     */             }
/* 288 */             localArrayList2.add(localPart);
/*     */           }
/*     */         }
/* 291 */         if (localArrayList2 != null)
/* 292 */           if (localArrayList2.size() == localMap.size())
/*     */           {
/* 294 */             if (localArrayList1 == null) {
/* 295 */               localArrayList1 = new ArrayList();
/*     */             }
/* 297 */             localArrayList1.add(localJComponent);
/*     */           } else {
/* 299 */             for (localIterator2 = localArrayList2.iterator(); localIterator2.hasNext(); ) { localPart = (TMSchema.Part)localIterator2.next();
/* 300 */               localMap.remove(localPart);
/*     */             }
/*     */           }
/*     */       }
/*     */     }
/*     */     JComponent localJComponent;
/*     */     Map localMap;
/*     */     Iterator localIterator2;
/*     */     TMSchema.Part localPart;
/* 305 */     if (localArrayList1 != null) {
/* 306 */       for (localIterator1 = localArrayList1.iterator(); localIterator1.hasNext(); ) { localJComponent = (JComponent)localIterator1.next();
/* 307 */         this.animationStateMap.remove(localJComponent);
/*     */       }
/*     */     }
/* 310 */     if (this.animationStateMap.size() == 0)
/* 311 */       this.timer.stop();
/*     */   }
/*     */ 
/*     */   private synchronized void dispose()
/*     */   {
/* 316 */     this.timer.stop();
/* 317 */     UIManager.removePropertyChangeListener(this);
/* 318 */     synchronized (AnimationController.class) {
/* 319 */       AppContext.getAppContext().put(ANIMATION_CONTROLLER_KEY, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class AnimationState
/*     */   {
/*     */     private final TMSchema.State startState;
/*     */     private final long duration;
/*     */     private long startTime;
/* 336 */     private boolean isForward = true;
/*     */     private boolean isForwardAndReverse;
/*     */     private float progress;
/*     */ 
/*     */     AnimationState(TMSchema.State paramState, long paramLong, boolean paramBoolean)
/*     */     {
/* 348 */       assert ((paramState != null) && (paramLong > 0L));
/* 349 */       assert (SwingUtilities.isEventDispatchThread());
/*     */ 
/* 351 */       this.startState = paramState;
/* 352 */       this.duration = (paramLong * 1000000L);
/* 353 */       this.startTime = System.nanoTime();
/* 354 */       this.isForwardAndReverse = paramBoolean;
/* 355 */       this.progress = 0.0F;
/*     */     }
/*     */     private void updateProgress() {
/* 358 */       assert (SwingUtilities.isEventDispatchThread());
/*     */ 
/* 360 */       if (isDone()) {
/* 361 */         return;
/*     */       }
/* 363 */       long l = System.nanoTime();
/*     */ 
/* 365 */       this.progress = ((float)(l - this.startTime) / (float)this.duration);
/*     */ 
/* 367 */       this.progress = Math.max(this.progress, 0.0F);
/* 368 */       if (this.progress >= 1.0F) {
/* 369 */         this.progress = 1.0F;
/* 370 */         if (this.isForwardAndReverse) {
/* 371 */           this.startTime = l;
/* 372 */           this.progress = 0.0F;
/* 373 */           this.isForward = (!this.isForward);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     void paintSkin(XPStyle.Skin paramSkin, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState) {
/* 379 */       assert (SwingUtilities.isEventDispatchThread());
/*     */ 
/* 381 */       updateProgress();
/* 382 */       if (!isDone()) {
/* 383 */         Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
/* 384 */         paramSkin.paintSkinRaw(localGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, this.startState);
/*     */         float f;
/* 386 */         if (this.isForward)
/* 387 */           f = this.progress;
/*     */         else {
/* 389 */           f = 1.0F - this.progress;
/*     */         }
/* 391 */         localGraphics2D.setComposite(AlphaComposite.SrcOver.derive(f));
/* 392 */         paramSkin.paintSkinRaw(localGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
/* 393 */         localGraphics2D.dispose();
/*     */       } else {
/* 395 */         paramSkin.paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
/*     */       }
/*     */     }
/*     */ 
/* 399 */     boolean isDone() { assert (SwingUtilities.isEventDispatchThread());
/*     */ 
/* 401 */       return this.progress >= 1.0F; }
/*     */   }
/*     */ 
/*     */   private static class PartUIClientPropertyKey implements UIClientPropertyKey
/*     */   {
/* 408 */     private static final Map<TMSchema.Part, PartUIClientPropertyKey> map = new EnumMap(TMSchema.Part.class);
/*     */     private final TMSchema.Part part;
/*     */ 
/*     */     static synchronized PartUIClientPropertyKey getKey(TMSchema.Part paramPart)
/*     */     {
/* 412 */       PartUIClientPropertyKey localPartUIClientPropertyKey = (PartUIClientPropertyKey)map.get(paramPart);
/* 413 */       if (localPartUIClientPropertyKey == null) {
/* 414 */         localPartUIClientPropertyKey = new PartUIClientPropertyKey(paramPart);
/* 415 */         map.put(paramPart, localPartUIClientPropertyKey);
/*     */       }
/* 417 */       return localPartUIClientPropertyKey;
/*     */     }
/*     */ 
/*     */     private PartUIClientPropertyKey(TMSchema.Part paramPart)
/*     */     {
/* 422 */       this.part = paramPart;
/*     */     }
/*     */     public String toString() {
/* 425 */       return this.part.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.AnimationController
 * JD-Core Version:    0.6.2
 */