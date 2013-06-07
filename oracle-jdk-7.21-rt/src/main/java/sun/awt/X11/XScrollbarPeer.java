/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.peer.ScrollbarPeer;
/*     */ import javax.swing.UIDefaults;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ class XScrollbarPeer extends XComponentPeer
/*     */   implements ScrollbarPeer, XScrollbarClient
/*     */ {
/*  34 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XScrollbarPeer");
/*     */   private static final int DEFAULT_LENGTH = 50;
/*     */   private static final int DEFAULT_WIDTH_SOLARIS = 19;
/*  43 */   private static final int DEFAULT_WIDTH_LINUX = XToolkit.getUIDefaults().getInt("ScrollBar.defaultWidth");
/*     */   XScrollbar tsb;
/*     */ 
/*     */   public void preInit(XCreateWindowParams paramXCreateWindowParams)
/*     */   {
/*  47 */     super.preInit(paramXCreateWindowParams);
/*  48 */     Scrollbar localScrollbar = (Scrollbar)this.target;
/*  49 */     if (localScrollbar.getOrientation() == 1)
/*  50 */       this.tsb = new XVerticalScrollbar(this);
/*     */     else {
/*  52 */       this.tsb = new XHorizontalScrollbar(this);
/*     */     }
/*  54 */     int i = localScrollbar.getMinimum();
/*  55 */     int j = localScrollbar.getMaximum();
/*  56 */     int k = localScrollbar.getVisibleAmount();
/*  57 */     int m = localScrollbar.getValue();
/*  58 */     int n = localScrollbar.getLineIncrement();
/*  59 */     int i1 = localScrollbar.getPageIncrement();
/*  60 */     this.tsb.setValues(m, k, i, j, n, i1);
/*     */   }
/*     */ 
/*     */   XScrollbarPeer(Scrollbar paramScrollbar)
/*     */   {
/*  67 */     super(paramScrollbar);
/*  68 */     this.target = paramScrollbar;
/*  69 */     xSetVisible(true);
/*     */   }
/*     */ 
/*     */   private int getDefaultDimension()
/*     */   {
/*  77 */     if (System.getProperty("os.name").equals("Linux")) {
/*  78 */       return DEFAULT_WIDTH_LINUX;
/*     */     }
/*  80 */     return 19;
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize()
/*     */   {
/*  88 */     Scrollbar localScrollbar = (Scrollbar)this.target;
/*  89 */     return localScrollbar.getOrientation() == 1 ? new Dimension(getDefaultDimension(), 50) : new Dimension(50, getDefaultDimension());
/*     */   }
/*     */ 
/*     */   public void repaint()
/*     */   {
/*  95 */     Graphics localGraphics = getGraphics();
/*  96 */     if (localGraphics != null) paint(localGraphics);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics)
/*     */   {
/* 103 */     Scrollbar localScrollbar = (Scrollbar)this.target;
/* 104 */     Color[] arrayOfColor = getGUIcolors();
/* 105 */     paramGraphics.setColor(arrayOfColor[0]);
/* 106 */     this.tsb.paint(paramGraphics, arrayOfColor, true);
/*     */   }
/*     */ 
/*     */   public void repaintScrollbarRequest(XScrollbar paramXScrollbar)
/*     */   {
/* 111 */     repaint();
/*     */   }
/*     */ 
/*     */   public void notifyValue(XScrollbar paramXScrollbar, int paramInt1, int paramInt2, boolean paramBoolean)
/*     */   {
/* 118 */     Scrollbar localScrollbar = (Scrollbar)this.target;
/* 119 */     localScrollbar.setValue(paramInt2);
/* 120 */     postEvent(new AdjustmentEvent(localScrollbar, 601, paramInt1, paramInt2, paramBoolean));
/*     */   }
/*     */ 
/*     */   public void handleJavaMouseEvent(MouseEvent paramMouseEvent)
/*     */   {
/* 135 */     super.handleJavaMouseEvent(paramMouseEvent);
/*     */ 
/* 137 */     int i = paramMouseEvent.getX();
/* 138 */     int j = paramMouseEvent.getY();
/* 139 */     int k = paramMouseEvent.getModifiers();
/* 140 */     int m = paramMouseEvent.getID();
/*     */ 
/* 143 */     if ((paramMouseEvent.getModifiers() & 0x10) == 0) {
/* 144 */       return;
/*     */     }
/*     */ 
/* 147 */     switch (paramMouseEvent.getID()) {
/*     */     case 501:
/* 149 */       this.target.requestFocus();
/* 150 */       this.tsb.handleMouseEvent(m, k, i, j);
/* 151 */       break;
/*     */     case 502:
/* 154 */       this.tsb.handleMouseEvent(m, k, i, j);
/* 155 */       break;
/*     */     case 506:
/* 158 */       this.tsb.handleMouseEvent(m, k, i, j);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handleJavaKeyEvent(KeyEvent paramKeyEvent)
/*     */   {
/* 164 */     super.handleJavaKeyEvent(paramKeyEvent);
/* 165 */     if (log.isLoggable(300)) log.finer("KeyEvent on scrollbar: " + paramKeyEvent);
/* 166 */     if ((!paramKeyEvent.isConsumed()) && (paramKeyEvent.getID() == 402))
/* 167 */       switch (paramKeyEvent.getKeyCode()) {
/*     */       case 38:
/* 169 */         log.finer("Scrolling up");
/* 170 */         this.tsb.notifyValue(this.tsb.getValue() - this.tsb.getUnitIncrement());
/* 171 */         break;
/*     */       case 40:
/* 173 */         log.finer("Scrolling down");
/* 174 */         this.tsb.notifyValue(this.tsb.getValue() + this.tsb.getUnitIncrement());
/* 175 */         break;
/*     */       case 37:
/* 177 */         log.finer("Scrolling up");
/* 178 */         this.tsb.notifyValue(this.tsb.getValue() - this.tsb.getUnitIncrement());
/* 179 */         break;
/*     */       case 39:
/* 181 */         log.finer("Scrolling down");
/* 182 */         this.tsb.notifyValue(this.tsb.getValue() + this.tsb.getUnitIncrement());
/* 183 */         break;
/*     */       case 33:
/* 185 */         log.finer("Scrolling page up");
/* 186 */         this.tsb.notifyValue(this.tsb.getValue() - this.tsb.getBlockIncrement());
/* 187 */         break;
/*     */       case 34:
/* 189 */         log.finer("Scrolling page down");
/* 190 */         this.tsb.notifyValue(this.tsb.getValue() + this.tsb.getBlockIncrement());
/* 191 */         break;
/*     */       case 36:
/* 193 */         log.finer("Scrolling to home");
/* 194 */         this.tsb.notifyValue(0);
/* 195 */         break;
/*     */       case 35:
/* 197 */         log.finer("Scrolling to end");
/* 198 */         this.tsb.notifyValue(this.tsb.getMaximum());
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setValue(int paramInt)
/*     */   {
/* 205 */     this.tsb.setValue(paramInt);
/* 206 */     repaint();
/*     */   }
/*     */ 
/*     */   public void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 211 */     this.tsb.setValues(paramInt1, paramInt2, paramInt3, paramInt4);
/* 212 */     repaint();
/*     */   }
/*     */ 
/*     */   public void setLineIncrement(int paramInt) {
/* 216 */     this.tsb.setUnitIncrement(paramInt);
/*     */   }
/*     */ 
/*     */   public void setPageIncrement(int paramInt) {
/* 220 */     this.tsb.setBlockIncrement(paramInt);
/*     */   }
/*     */ 
/*     */   public void layout() {
/* 224 */     super.layout();
/* 225 */     this.tsb.setSize(this.width, this.height);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XScrollbarPeer
 * JD-Core Version:    0.6.2
 */