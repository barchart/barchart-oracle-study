/*     */ package javax.swing;
/*     */ 
/*     */ import java.applet.Applet;
/*     */ import java.awt.Container;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import sun.awt.EmbeddedFrame;
/*     */ 
/*     */ class KeyboardManager
/*     */ {
/*  66 */   static KeyboardManager currentManager = new KeyboardManager();
/*     */   Hashtable<Container, Hashtable> containerMap;
/*     */   Hashtable<ComponentKeyStrokePair, Container> componentKeyStrokeMap;
/*     */ 
/*     */   KeyboardManager()
/*     */   {
/*  71 */     this.containerMap = new Hashtable();
/*     */ 
/*  77 */     this.componentKeyStrokeMap = new Hashtable();
/*     */   }
/*     */   public static KeyboardManager getCurrentManager() {
/*  80 */     return currentManager;
/*     */   }
/*     */ 
/*     */   public static void setCurrentManager(KeyboardManager paramKeyboardManager) {
/*  84 */     currentManager = paramKeyboardManager;
/*     */   }
/*     */ 
/*     */   public void registerKeyStroke(KeyStroke paramKeyStroke, JComponent paramJComponent)
/*     */   {
/*  94 */     Container localContainer = getTopAncestor(paramJComponent);
/*  95 */     if (localContainer == null) {
/*  96 */       return;
/*     */     }
/*  98 */     Hashtable localHashtable = (Hashtable)this.containerMap.get(localContainer);
/*     */ 
/* 100 */     if (localHashtable == null) {
/* 101 */       localHashtable = registerNewTopContainer(localContainer);
/*     */     }
/*     */ 
/* 104 */     Object localObject = localHashtable.get(paramKeyStroke);
/* 105 */     if (localObject == null) {
/* 106 */       localHashtable.put(paramKeyStroke, paramJComponent);
/*     */     }
/*     */     else
/*     */     {
/*     */       Vector localVector;
/* 107 */       if ((localObject instanceof Vector)) {
/* 108 */         localVector = (Vector)localObject;
/* 109 */         if (!localVector.contains(paramJComponent))
/* 110 */           localVector.addElement(paramJComponent);
/*     */       }
/* 112 */       else if ((localObject instanceof JComponent))
/*     */       {
/* 116 */         if (localObject != paramJComponent) {
/* 117 */           localVector = new Vector();
/* 118 */           localVector.addElement((JComponent)localObject);
/* 119 */           localVector.addElement(paramJComponent);
/* 120 */           localHashtable.put(paramKeyStroke, localVector);
/*     */         }
/*     */       } else {
/* 123 */         System.out.println("Unexpected condition in registerKeyStroke");
/* 124 */         Thread.dumpStack();
/*     */       }
/*     */     }
/* 127 */     this.componentKeyStrokeMap.put(new ComponentKeyStrokePair(paramJComponent, paramKeyStroke), localContainer);
/*     */ 
/* 131 */     if ((localContainer instanceof EmbeddedFrame))
/* 132 */       ((EmbeddedFrame)localContainer).registerAccelerator(paramKeyStroke);
/*     */   }
/*     */ 
/*     */   private static Container getTopAncestor(JComponent paramJComponent)
/*     */   {
/* 140 */     for (Container localContainer = paramJComponent.getParent(); localContainer != null; localContainer = localContainer.getParent()) {
/* 141 */       if ((((localContainer instanceof Window)) && (((Window)localContainer).isFocusableWindow())) || ((localContainer instanceof Applet)) || ((localContainer instanceof JInternalFrame)))
/*     */       {
/* 144 */         return localContainer;
/*     */       }
/*     */     }
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */   public void unregisterKeyStroke(KeyStroke paramKeyStroke, JComponent paramJComponent)
/*     */   {
/* 155 */     ComponentKeyStrokePair localComponentKeyStrokePair = new ComponentKeyStrokePair(paramJComponent, paramKeyStroke);
/*     */ 
/* 157 */     Container localContainer = (Container)this.componentKeyStrokeMap.get(localComponentKeyStrokePair);
/*     */ 
/* 159 */     if (localContainer == null) {
/* 160 */       return;
/*     */     }
/*     */ 
/* 163 */     Hashtable localHashtable = (Hashtable)this.containerMap.get(localContainer);
/* 164 */     if (localHashtable == null) {
/* 165 */       Thread.dumpStack();
/* 166 */       return;
/*     */     }
/*     */ 
/* 169 */     Object localObject = localHashtable.get(paramKeyStroke);
/* 170 */     if (localObject == null) {
/* 171 */       Thread.dumpStack();
/* 172 */       return;
/*     */     }
/*     */ 
/* 175 */     if (((localObject instanceof JComponent)) && (localObject == paramJComponent)) {
/* 176 */       localHashtable.remove(paramKeyStroke);
/*     */     }
/* 178 */     else if ((localObject instanceof Vector)) {
/* 179 */       Vector localVector = (Vector)localObject;
/* 180 */       localVector.removeElement(paramJComponent);
/* 181 */       if (localVector.isEmpty()) {
/* 182 */         localHashtable.remove(paramKeyStroke);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 187 */     if (localHashtable.isEmpty()) {
/* 188 */       this.containerMap.remove(localContainer);
/*     */     }
/*     */ 
/* 192 */     this.componentKeyStrokeMap.remove(localComponentKeyStrokePair);
/*     */ 
/* 196 */     if ((localContainer instanceof EmbeddedFrame))
/* 197 */       ((EmbeddedFrame)localContainer).unregisterAccelerator(paramKeyStroke);
/*     */   }
/*     */ 
/*     */   public boolean fireKeyboardAction(KeyEvent paramKeyEvent, boolean paramBoolean, Container paramContainer)
/*     */   {
/* 210 */     if (paramKeyEvent.isConsumed()) {
/* 211 */       System.out.println("Aquired pre-used event!");
/* 212 */       Thread.dumpStack();
/*     */     }
/*     */ 
/* 218 */     KeyStroke localKeyStroke2 = null;
/*     */     KeyStroke localKeyStroke1;
/* 221 */     if (paramKeyEvent.getID() == 400) {
/* 222 */       localKeyStroke1 = KeyStroke.getKeyStroke(paramKeyEvent.getKeyChar());
/*     */     } else {
/* 224 */       if (paramKeyEvent.getKeyCode() != paramKeyEvent.getExtendedKeyCode()) {
/* 225 */         localKeyStroke2 = KeyStroke.getKeyStroke(paramKeyEvent.getExtendedKeyCode(), paramKeyEvent.getModifiers(), !paramBoolean);
/*     */       }
/* 227 */       localKeyStroke1 = KeyStroke.getKeyStroke(paramKeyEvent.getKeyCode(), paramKeyEvent.getModifiers(), !paramBoolean);
/*     */     }
/*     */ 
/* 230 */     Hashtable localHashtable = (Hashtable)this.containerMap.get(paramContainer);
/*     */     Object localObject1;
/*     */     Object localObject2;
/* 231 */     if (localHashtable != null)
/*     */     {
/* 233 */       localObject1 = null;
/*     */ 
/* 235 */       if (localKeyStroke2 != null) {
/* 236 */         localObject1 = localHashtable.get(localKeyStroke2);
/* 237 */         if (localObject1 != null) {
/* 238 */           localKeyStroke1 = localKeyStroke2;
/*     */         }
/*     */       }
/* 241 */       if (localObject1 == null) {
/* 242 */         localObject1 = localHashtable.get(localKeyStroke1);
/*     */       }
/*     */ 
/* 245 */       if (localObject1 != null)
/*     */       {
/* 247 */         if ((localObject1 instanceof JComponent)) {
/* 248 */           localObject2 = (JComponent)localObject1;
/* 249 */           if ((((JComponent)localObject2).isShowing()) && (((JComponent)localObject2).isEnabled()))
/* 250 */             fireBinding((JComponent)localObject2, localKeyStroke1, paramKeyEvent, paramBoolean);
/*     */         }
/* 252 */         else if ((localObject1 instanceof Vector)) {
/* 253 */           localObject2 = (Vector)localObject1;
/*     */ 
/* 259 */           for (int i = ((Vector)localObject2).size() - 1; i >= 0; i--) {
/* 260 */             JComponent localJComponent = (JComponent)((Vector)localObject2).elementAt(i);
/*     */ 
/* 262 */             if ((localJComponent.isShowing()) && (localJComponent.isEnabled())) {
/* 263 */               fireBinding(localJComponent, localKeyStroke1, paramKeyEvent, paramBoolean);
/* 264 */               if (paramKeyEvent.isConsumed())
/* 265 */                 return true;
/*     */             }
/*     */           }
/*     */         } else {
/* 269 */           System.out.println("Unexpected condition in fireKeyboardAction " + localObject1);
/*     */ 
/* 271 */           Thread.dumpStack();
/*     */         }
/*     */       }
/*     */     }
/* 275 */     if (paramKeyEvent.isConsumed()) {
/* 276 */       return true;
/*     */     }
/*     */ 
/* 281 */     if (localHashtable != null) {
/* 282 */       localObject1 = (Vector)localHashtable.get(JMenuBar.class);
/* 283 */       if (localObject1 != null) {
/* 284 */         localObject2 = ((Vector)localObject1).elements();
/* 285 */         while (((Enumeration)localObject2).hasMoreElements()) {
/* 286 */           JMenuBar localJMenuBar = (JMenuBar)((Enumeration)localObject2).nextElement();
/* 287 */           if ((localJMenuBar.isShowing()) && (localJMenuBar.isEnabled())) {
/* 288 */             if (!localKeyStroke1.equals(localKeyStroke2)) {
/* 289 */               fireBinding(localJMenuBar, localKeyStroke2, paramKeyEvent, paramBoolean);
/*     */             }
/* 291 */             if ((localKeyStroke1.equals(localKeyStroke2)) || (!paramKeyEvent.isConsumed())) {
/* 292 */               fireBinding(localJMenuBar, localKeyStroke1, paramKeyEvent, paramBoolean);
/*     */             }
/* 294 */             if (paramKeyEvent.isConsumed()) {
/* 295 */               return true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 302 */     return paramKeyEvent.isConsumed();
/*     */   }
/*     */ 
/*     */   void fireBinding(JComponent paramJComponent, KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, boolean paramBoolean) {
/* 306 */     if (paramJComponent.processKeyBinding(paramKeyStroke, paramKeyEvent, 2, paramBoolean))
/*     */     {
/* 308 */       paramKeyEvent.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void registerMenuBar(JMenuBar paramJMenuBar) {
/* 313 */     Container localContainer = getTopAncestor(paramJMenuBar);
/* 314 */     if (localContainer == null) {
/* 315 */       return;
/*     */     }
/* 317 */     Hashtable localHashtable = (Hashtable)this.containerMap.get(localContainer);
/*     */ 
/* 319 */     if (localHashtable == null) {
/* 320 */       localHashtable = registerNewTopContainer(localContainer);
/*     */     }
/*     */ 
/* 323 */     Vector localVector = (Vector)localHashtable.get(JMenuBar.class);
/*     */ 
/* 325 */     if (localVector == null)
/*     */     {
/* 327 */       localVector = new Vector();
/* 328 */       localHashtable.put(JMenuBar.class, localVector);
/*     */     }
/*     */ 
/* 331 */     if (!localVector.contains(paramJMenuBar))
/* 332 */       localVector.addElement(paramJMenuBar);
/*     */   }
/*     */ 
/*     */   public void unregisterMenuBar(JMenuBar paramJMenuBar)
/*     */   {
/* 338 */     Container localContainer = getTopAncestor(paramJMenuBar);
/* 339 */     if (localContainer == null) {
/* 340 */       return;
/*     */     }
/* 342 */     Hashtable localHashtable = (Hashtable)this.containerMap.get(localContainer);
/* 343 */     if (localHashtable != null) {
/* 344 */       Vector localVector = (Vector)localHashtable.get(JMenuBar.class);
/* 345 */       if (localVector != null) {
/* 346 */         localVector.removeElement(paramJMenuBar);
/* 347 */         if (localVector.isEmpty()) {
/* 348 */           localHashtable.remove(JMenuBar.class);
/* 349 */           if (localHashtable.isEmpty())
/*     */           {
/* 351 */             this.containerMap.remove(localContainer);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 358 */   protected Hashtable registerNewTopContainer(Container paramContainer) { Hashtable localHashtable = new Hashtable();
/* 359 */     this.containerMap.put(paramContainer, localHashtable);
/* 360 */     return localHashtable;
/*     */   }
/*     */ 
/*     */   class ComponentKeyStrokePair
/*     */   {
/*     */     Object component;
/*     */     Object keyStroke;
/*     */ 
/*     */     public ComponentKeyStrokePair(Object paramObject1, Object arg3)
/*     */     {
/* 373 */       this.component = paramObject1;
/*     */       Object localObject;
/* 374 */       this.keyStroke = localObject;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object paramObject) {
/* 378 */       if (!(paramObject instanceof ComponentKeyStrokePair)) {
/* 379 */         return false;
/*     */       }
/* 381 */       ComponentKeyStrokePair localComponentKeyStrokePair = (ComponentKeyStrokePair)paramObject;
/* 382 */       return (this.component.equals(localComponentKeyStrokePair.component)) && (this.keyStroke.equals(localComponentKeyStrokePair.keyStroke));
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 386 */       return this.component.hashCode() * this.keyStroke.hashCode();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.KeyboardManager
 * JD-Core Version:    0.6.2
 */