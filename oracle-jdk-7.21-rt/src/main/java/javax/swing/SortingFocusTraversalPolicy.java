/*     */ package javax.swing;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.FocusTraversalPolicy;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class SortingFocusTraversalPolicy extends InternalFrameFocusTraversalPolicy
/*     */ {
/*     */   private Comparator<? super Component> comparator;
/*  65 */   private boolean implicitDownCycleTraversal = true;
/*     */ 
/*  67 */   private PlatformLogger log = PlatformLogger.getLogger("javax.swing.SortingFocusTraversalPolicy");
/*     */   private transient Container cachedRoot;
/*     */   private transient List<Component> cachedCycle;
/*  87 */   private static final SwingContainerOrderFocusTraversalPolicy fitnessTestPolicy = new SwingContainerOrderFocusTraversalPolicy();
/*     */ 
/*  89 */   private final int FORWARD_TRAVERSAL = 0;
/*  90 */   private final int BACKWARD_TRAVERSAL = 1;
/*     */ 
/*     */   protected SortingFocusTraversalPolicy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SortingFocusTraversalPolicy(Comparator<? super Component> paramComparator)
/*     */   {
/* 105 */     this.comparator = paramComparator;
/*     */   }
/*     */ 
/*     */   private List<Component> getFocusTraversalCycle(Container paramContainer) {
/* 109 */     ArrayList localArrayList = new ArrayList();
/* 110 */     enumerateAndSortCycle(paramContainer, localArrayList);
/* 111 */     return localArrayList;
/*     */   }
/*     */   private int getComponentIndex(List<Component> paramList, Component paramComponent) {
/*     */     int i;
/*     */     try {
/* 116 */       i = Collections.binarySearch(paramList, paramComponent, this.comparator);
/*     */     } catch (ClassCastException localClassCastException) {
/* 118 */       if (this.log.isLoggable(500)) {
/* 119 */         this.log.fine("### During the binary search for " + paramComponent + " the exception occured: ", localClassCastException);
/*     */       }
/* 121 */       return -1;
/*     */     }
/* 123 */     if (i < 0)
/*     */     {
/* 128 */       i = paramList.indexOf(paramComponent);
/*     */     }
/* 130 */     return i;
/*     */   }
/*     */ 
/*     */   private void enumerateAndSortCycle(Container paramContainer, List<Component> paramList) {
/* 134 */     if (paramContainer.isShowing()) {
/* 135 */       enumerateCycle(paramContainer, paramList);
/* 136 */       Collections.sort(paramList, this.comparator);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void enumerateCycle(Container paramContainer, List<Component> paramList) {
/* 141 */     if ((!paramContainer.isVisible()) || (!paramContainer.isDisplayable())) {
/* 142 */       return;
/*     */     }
/*     */ 
/* 145 */     paramList.add(paramContainer);
/*     */ 
/* 147 */     Component[] arrayOfComponent1 = paramContainer.getComponents();
/* 148 */     for (Component localComponent : arrayOfComponent1) {
/* 149 */       if ((localComponent instanceof Container)) {
/* 150 */         Container localContainer = (Container)localComponent;
/*     */ 
/* 152 */         if ((!localContainer.isFocusCycleRoot()) && (!localContainer.isFocusTraversalPolicyProvider()) && ((!(localContainer instanceof JComponent)) || (!((JComponent)localContainer).isManagingFocus())))
/*     */         {
/* 156 */           enumerateCycle(localContainer, paramList);
/* 157 */           continue;
/*     */         }
/*     */       }
/* 160 */       paramList.add(localComponent);
/*     */     }
/*     */   }
/*     */ 
/*     */   Container getTopmostProvider(Container paramContainer, Component paramComponent) {
/* 165 */     Container localContainer1 = paramComponent.getParent();
/* 166 */     Container localContainer2 = null;
/* 167 */     while ((localContainer1 != paramContainer) && (localContainer1 != null)) {
/* 168 */       if (localContainer1.isFocusTraversalPolicyProvider()) {
/* 169 */         localContainer2 = localContainer1;
/*     */       }
/* 171 */       localContainer1 = localContainer1.getParent();
/*     */     }
/* 173 */     if (localContainer1 == null) {
/* 174 */       return null;
/*     */     }
/* 176 */     return localContainer2;
/*     */   }
/*     */ 
/*     */   private Component getComponentDownCycle(Component paramComponent, int paramInt)
/*     */   {
/* 187 */     Component localComponent = null;
/*     */ 
/* 189 */     if ((paramComponent instanceof Container)) {
/* 190 */       Container localContainer = (Container)paramComponent;
/*     */ 
/* 192 */       if (localContainer.isFocusCycleRoot()) {
/* 193 */         if (getImplicitDownCycleTraversal()) {
/* 194 */           localComponent = localContainer.getFocusTraversalPolicy().getDefaultComponent(localContainer);
/*     */ 
/* 196 */           if ((localComponent != null) && (this.log.isLoggable(500)))
/* 197 */             this.log.fine("### Transfered focus down-cycle to " + localComponent + " in the focus cycle root " + localContainer);
/*     */         }
/*     */         else
/*     */         {
/* 201 */           return null;
/*     */         }
/* 203 */       } else if (localContainer.isFocusTraversalPolicyProvider()) {
/* 204 */         localComponent = paramInt == 0 ? localContainer.getFocusTraversalPolicy().getDefaultComponent(localContainer) : localContainer.getFocusTraversalPolicy().getLastComponent(localContainer);
/*     */ 
/* 208 */         if ((localComponent != null) && (this.log.isLoggable(500))) {
/* 209 */           this.log.fine("### Transfered focus to " + localComponent + " in the FTP provider " + localContainer);
/*     */         }
/*     */       }
/*     */     }
/* 213 */     return localComponent;
/*     */   }
/*     */ 
/*     */   public Component getComponentAfter(Container paramContainer, Component paramComponent)
/*     */   {
/* 239 */     if (this.log.isLoggable(500)) {
/* 240 */       this.log.fine("### Searching in " + paramContainer + " for component after " + paramComponent);
/*     */     }
/*     */ 
/* 243 */     if ((paramContainer == null) || (paramComponent == null)) {
/* 244 */       throw new IllegalArgumentException("aContainer and aComponent cannot be null");
/*     */     }
/* 246 */     if ((!paramContainer.isFocusTraversalPolicyProvider()) && (!paramContainer.isFocusCycleRoot())) {
/* 247 */       throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
/*     */     }
/* 249 */     if ((paramContainer.isFocusCycleRoot()) && (!paramComponent.isFocusCycleRoot(paramContainer))) {
/* 250 */       throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
/*     */     }
/*     */ 
/* 255 */     Component localComponent1 = getComponentDownCycle(paramComponent, 0);
/* 256 */     if (localComponent1 != null) {
/* 257 */       return localComponent1;
/*     */     }
/*     */ 
/* 261 */     Container localContainer = getTopmostProvider(paramContainer, paramComponent);
/* 262 */     if (localContainer != null) {
/* 263 */       if (this.log.isLoggable(500)) {
/* 264 */         this.log.fine("### Asking FTP " + localContainer + " for component after " + paramComponent);
/*     */       }
/*     */ 
/* 268 */       localObject = localContainer.getFocusTraversalPolicy();
/* 269 */       Component localComponent2 = ((FocusTraversalPolicy)localObject).getComponentAfter(localContainer, paramComponent);
/*     */ 
/* 273 */       if (localComponent2 != null) {
/* 274 */         if (this.log.isLoggable(500)) this.log.fine("### FTP returned " + localComponent2);
/* 275 */         return localComponent2;
/*     */       }
/* 277 */       paramComponent = localContainer;
/*     */     }
/*     */ 
/* 280 */     Object localObject = getFocusTraversalCycle(paramContainer);
/*     */ 
/* 282 */     if (this.log.isLoggable(500)) this.log.fine("### Cycle is " + localObject + ", component is " + paramComponent);
/*     */ 
/* 284 */     int i = getComponentIndex((List)localObject, paramComponent);
/*     */ 
/* 286 */     if (i < 0) {
/* 287 */       if (this.log.isLoggable(500)) {
/* 288 */         this.log.fine("### Didn't find component " + paramComponent + " in a cycle " + paramContainer);
/*     */       }
/* 290 */       return getFirstComponent(paramContainer);
/*     */     }
/*     */ 
/* 293 */     for (i++; i < ((List)localObject).size(); i++) {
/* 294 */       localComponent1 = (Component)((List)localObject).get(i);
/* 295 */       if (accept(localComponent1))
/* 296 */         return localComponent1;
/* 297 */       if ((localComponent1 = getComponentDownCycle(localComponent1, 0)) != null) {
/* 298 */         return localComponent1;
/*     */       }
/*     */     }
/*     */ 
/* 302 */     if (paramContainer.isFocusCycleRoot()) {
/* 303 */       this.cachedRoot = paramContainer;
/* 304 */       this.cachedCycle = ((List)localObject);
/*     */ 
/* 306 */       localComponent1 = getFirstComponent(paramContainer);
/*     */ 
/* 308 */       this.cachedRoot = null;
/* 309 */       this.cachedCycle = null;
/*     */ 
/* 311 */       return localComponent1;
/*     */     }
/* 313 */     return null;
/*     */   }
/*     */ 
/*     */   public Component getComponentBefore(Container paramContainer, Component paramComponent)
/*     */   {
/* 339 */     if ((paramContainer == null) || (paramComponent == null)) {
/* 340 */       throw new IllegalArgumentException("aContainer and aComponent cannot be null");
/*     */     }
/* 342 */     if ((!paramContainer.isFocusTraversalPolicyProvider()) && (!paramContainer.isFocusCycleRoot())) {
/* 343 */       throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
/*     */     }
/* 345 */     if ((paramContainer.isFocusCycleRoot()) && (!paramComponent.isFocusCycleRoot(paramContainer))) {
/* 346 */       throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
/*     */     }
/*     */ 
/* 350 */     Container localContainer = getTopmostProvider(paramContainer, paramComponent);
/* 351 */     if (localContainer != null) {
/* 352 */       if (this.log.isLoggable(500)) {
/* 353 */         this.log.fine("### Asking FTP " + localContainer + " for component after " + paramComponent);
/*     */       }
/*     */ 
/* 357 */       localObject = localContainer.getFocusTraversalPolicy();
/* 358 */       Component localComponent1 = ((FocusTraversalPolicy)localObject).getComponentBefore(localContainer, paramComponent);
/*     */ 
/* 362 */       if (localComponent1 != null) {
/* 363 */         if (this.log.isLoggable(500)) this.log.fine("### FTP returned " + localComponent1);
/* 364 */         return localComponent1;
/*     */       }
/* 366 */       paramComponent = localContainer;
/*     */ 
/* 369 */       if (accept(paramComponent)) {
/* 370 */         return paramComponent;
/*     */       }
/*     */     }
/*     */ 
/* 374 */     Object localObject = getFocusTraversalCycle(paramContainer);
/*     */ 
/* 376 */     if (this.log.isLoggable(500)) this.log.fine("### Cycle is " + localObject + ", component is " + paramComponent);
/*     */ 
/* 378 */     int i = getComponentIndex((List)localObject, paramComponent);
/*     */ 
/* 380 */     if (i < 0) {
/* 381 */       if (this.log.isLoggable(500)) {
/* 382 */         this.log.fine("### Didn't find component " + paramComponent + " in a cycle " + paramContainer);
/*     */       }
/* 384 */       return getLastComponent(paramContainer);
/*     */     }
/*     */     Component localComponent2;
/* 390 */     for (i--; i >= 0; i--) {
/* 391 */       localComponent2 = (Component)((List)localObject).get(i);
/*     */       Component localComponent3;
/* 392 */       if ((localComponent2 != paramContainer) && ((localComponent3 = getComponentDownCycle(localComponent2, 1)) != null))
/* 393 */         return localComponent3;
/* 394 */       if (accept(localComponent2)) {
/* 395 */         return localComponent2;
/*     */       }
/*     */     }
/*     */ 
/* 399 */     if (paramContainer.isFocusCycleRoot()) {
/* 400 */       this.cachedRoot = paramContainer;
/* 401 */       this.cachedCycle = ((List)localObject);
/*     */ 
/* 403 */       localComponent2 = getLastComponent(paramContainer);
/*     */ 
/* 405 */       this.cachedRoot = null;
/* 406 */       this.cachedCycle = null;
/*     */ 
/* 408 */       return localComponent2;
/*     */     }
/* 410 */     return null;
/*     */   }
/*     */ 
/*     */   public Component getFirstComponent(Container paramContainer)
/*     */   {
/* 427 */     if (this.log.isLoggable(500)) this.log.fine("### Getting first component in " + paramContainer);
/* 428 */     if (paramContainer == null)
/* 429 */       throw new IllegalArgumentException("aContainer cannot be null");
/*     */     List localList;
/* 432 */     if (this.cachedRoot == paramContainer)
/* 433 */       localList = this.cachedCycle;
/*     */     else {
/* 435 */       localList = getFocusTraversalCycle(paramContainer);
/*     */     }
/*     */ 
/* 438 */     if (localList.size() == 0) {
/* 439 */       if (this.log.isLoggable(500)) this.log.fine("### Cycle is empty");
/* 440 */       return null;
/*     */     }
/* 442 */     if (this.log.isLoggable(500)) this.log.fine("### Cycle is " + localList);
/*     */ 
/* 444 */     for (Component localComponent : localList) {
/* 445 */       if (accept(localComponent))
/* 446 */         return localComponent;
/* 447 */       if ((localComponent != paramContainer) && ((localComponent = getComponentDownCycle(localComponent, 0)) != null))
/*     */       {
/* 450 */         return localComponent;
/*     */       }
/*     */     }
/* 453 */     return null;
/*     */   }
/*     */ 
/*     */   public Component getLastComponent(Container paramContainer)
/*     */   {
/* 469 */     if (this.log.isLoggable(500)) this.log.fine("### Getting last component in " + paramContainer);
/*     */ 
/* 471 */     if (paramContainer == null)
/* 472 */       throw new IllegalArgumentException("aContainer cannot be null");
/*     */     List localList;
/* 475 */     if (this.cachedRoot == paramContainer)
/* 476 */       localList = this.cachedCycle;
/*     */     else {
/* 478 */       localList = getFocusTraversalCycle(paramContainer);
/*     */     }
/*     */ 
/* 481 */     if (localList.size() == 0) {
/* 482 */       if (this.log.isLoggable(500)) this.log.fine("### Cycle is empty");
/* 483 */       return null;
/*     */     }
/* 485 */     if (this.log.isLoggable(500)) this.log.fine("### Cycle is " + localList);
/*     */ 
/* 487 */     for (int i = localList.size() - 1; i >= 0; i--) {
/* 488 */       Component localComponent = (Component)localList.get(i);
/* 489 */       if (accept(localComponent))
/* 490 */         return localComponent;
/* 491 */       if (((localComponent instanceof Container)) && (localComponent != paramContainer)) {
/* 492 */         Container localContainer = (Container)localComponent;
/* 493 */         if (localContainer.isFocusTraversalPolicyProvider()) {
/* 494 */           return localContainer.getFocusTraversalPolicy().getLastComponent(localContainer);
/*     */         }
/*     */       }
/*     */     }
/* 498 */     return null;
/*     */   }
/*     */ 
/*     */   public Component getDefaultComponent(Container paramContainer)
/*     */   {
/* 515 */     return getFirstComponent(paramContainer);
/*     */   }
/*     */ 
/*     */   public void setImplicitDownCycleTraversal(boolean paramBoolean)
/*     */   {
/* 533 */     this.implicitDownCycleTraversal = paramBoolean;
/*     */   }
/*     */ 
/*     */   public boolean getImplicitDownCycleTraversal()
/*     */   {
/* 550 */     return this.implicitDownCycleTraversal;
/*     */   }
/*     */ 
/*     */   protected void setComparator(Comparator<? super Component> paramComparator)
/*     */   {
/* 560 */     this.comparator = paramComparator;
/*     */   }
/*     */ 
/*     */   protected Comparator<? super Component> getComparator()
/*     */   {
/* 570 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   protected boolean accept(Component paramComponent)
/*     */   {
/* 584 */     return fitnessTestPolicy.accept(paramComponent);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.SortingFocusTraversalPolicy
 * JD-Core Version:    0.6.2
 */