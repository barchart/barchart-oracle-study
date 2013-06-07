/*      */ package java.awt.event;
/*      */ 
/*      */ import java.awt.Component;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.IllegalComponentStateException;
/*      */ import java.awt.Point;
/*      */ import java.awt.Toolkit;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.PrintStream;
/*      */ import sun.awt.SunToolkit;
/*      */ 
/*      */ public class MouseEvent extends InputEvent
/*      */ {
/*      */   public static final int MOUSE_FIRST = 500;
/*      */   public static final int MOUSE_LAST = 507;
/*      */   public static final int MOUSE_CLICKED = 500;
/*      */   public static final int MOUSE_PRESSED = 501;
/*      */   public static final int MOUSE_RELEASED = 502;
/*      */   public static final int MOUSE_MOVED = 503;
/*      */   public static final int MOUSE_ENTERED = 504;
/*      */   public static final int MOUSE_EXITED = 505;
/*      */   public static final int MOUSE_DRAGGED = 506;
/*      */   public static final int MOUSE_WHEEL = 507;
/*      */   public static final int NOBUTTON = 0;
/*      */   public static final int BUTTON1 = 1;
/*      */   public static final int BUTTON2 = 2;
/*      */   public static final int BUTTON3 = 3;
/*      */   int x;
/*      */   int y;
/*      */   private int xAbs;
/*      */   private int yAbs;
/*      */   int clickCount;
/*      */   int button;
/*  376 */   boolean popupTrigger = false;
/*      */   private static final long serialVersionUID = -991214153494842848L;
/*      */   private static int cachedNumberOfButtons;
/*  624 */   private transient boolean shouldExcludeButtonFromExtModifiers = false;
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   public Point getLocationOnScreen()
/*      */   {
/*  425 */     return new Point(this.xAbs, this.yAbs);
/*      */   }
/*      */ 
/*      */   public int getXOnScreen()
/*      */   {
/*  442 */     return this.xAbs;
/*      */   }
/*      */ 
/*      */   public int getYOnScreen()
/*      */   {
/*  459 */     return this.yAbs;
/*      */   }
/*      */ 
/*      */   public MouseEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6)
/*      */   {
/*  554 */     this(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, 0, 0, paramInt5, paramBoolean, paramInt6);
/*  555 */     Point localPoint = new Point(0, 0);
/*      */     try {
/*  557 */       localPoint = paramComponent.getLocationOnScreen();
/*  558 */       this.xAbs = (localPoint.x + paramInt3);
/*  559 */       this.yAbs = (localPoint.y + paramInt4);
/*      */     } catch (IllegalComponentStateException localIllegalComponentStateException) {
/*  561 */       this.xAbs = 0;
/*  562 */       this.yAbs = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public MouseEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
/*      */   {
/*  618 */     this(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean, 0);
/*      */   }
/*      */ 
/*      */   public int getModifiersEx()
/*      */   {
/*  630 */     int i = this.modifiers;
/*  631 */     if (this.shouldExcludeButtonFromExtModifiers) {
/*  632 */       i &= (InputEvent.getMaskForButton(getButton()) ^ 0xFFFFFFFF);
/*      */     }
/*  634 */     return i & 0xFFFFFFC0;
/*      */   }
/*      */ 
/*      */   public MouseEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean, int paramInt8)
/*      */   {
/*  733 */     super(paramComponent, paramInt1, paramLong, paramInt2);
/*  734 */     this.x = paramInt3;
/*  735 */     this.y = paramInt4;
/*  736 */     this.xAbs = paramInt5;
/*  737 */     this.yAbs = paramInt6;
/*  738 */     this.clickCount = paramInt7;
/*  739 */     this.popupTrigger = paramBoolean;
/*  740 */     if (paramInt8 < 0) {
/*  741 */       throw new IllegalArgumentException("Invalid button value :" + paramInt8);
/*      */     }
/*  743 */     if (paramInt8 > 3) {
/*  744 */       if (!Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled()) {
/*  745 */         throw new IllegalArgumentException("Extra mouse events are disabled " + paramInt8);
/*      */       }
/*  747 */       if (paramInt8 > cachedNumberOfButtons) {
/*  748 */         throw new IllegalArgumentException("Nonexistent button " + paramInt8);
/*      */       }
/*      */ 
/*  759 */       if ((getModifiersEx() != 0) && (
/*  760 */         (paramInt1 == 502) || (paramInt1 == 500))) {
/*  761 */         System.out.println("MEvent. CASE!");
/*  762 */         this.shouldExcludeButtonFromExtModifiers = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  767 */     this.button = paramInt8;
/*      */ 
/*  769 */     if ((getModifiers() != 0) && (getModifiersEx() == 0))
/*  770 */       setNewModifiers();
/*  771 */     else if ((getModifiers() == 0) && ((getModifiersEx() != 0) || (paramInt8 != 0)) && (paramInt8 <= 3))
/*      */     {
/*  775 */       setOldModifiers();
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getX()
/*      */   {
/*  787 */     return this.x;
/*      */   }
/*      */ 
/*      */   public int getY()
/*      */   {
/*  798 */     return this.y;
/*      */   }
/*      */ 
/*      */   public Point getPoint()
/*      */   {
/*      */     int i;
/*      */     int j;
/*  811 */     synchronized (this) {
/*  812 */       i = this.x;
/*  813 */       j = this.y;
/*      */     }
/*  815 */     return new Point(i, j);
/*      */   }
/*      */ 
/*      */   public synchronized void translatePoint(int paramInt1, int paramInt2)
/*      */   {
/*  829 */     this.x += paramInt1;
/*  830 */     this.y += paramInt2;
/*      */   }
/*      */ 
/*      */   public int getClickCount()
/*      */   {
/*  839 */     return this.clickCount;
/*      */   }
/*      */ 
/*      */   public int getButton()
/*      */   {
/*  895 */     return this.button;
/*      */   }
/*      */ 
/*      */   public boolean isPopupTrigger()
/*      */   {
/*  911 */     return this.popupTrigger;
/*      */   }
/*      */ 
/*      */   public static String getMouseModifiersText(int paramInt)
/*      */   {
/*  940 */     StringBuilder localStringBuilder = new StringBuilder();
/*  941 */     if ((paramInt & 0x8) != 0) {
/*  942 */       localStringBuilder.append(Toolkit.getProperty("AWT.alt", "Alt"));
/*  943 */       localStringBuilder.append("+");
/*      */     }
/*  945 */     if ((paramInt & 0x4) != 0) {
/*  946 */       localStringBuilder.append(Toolkit.getProperty("AWT.meta", "Meta"));
/*  947 */       localStringBuilder.append("+");
/*      */     }
/*  949 */     if ((paramInt & 0x2) != 0) {
/*  950 */       localStringBuilder.append(Toolkit.getProperty("AWT.control", "Ctrl"));
/*  951 */       localStringBuilder.append("+");
/*      */     }
/*  953 */     if ((paramInt & 0x1) != 0) {
/*  954 */       localStringBuilder.append(Toolkit.getProperty("AWT.shift", "Shift"));
/*  955 */       localStringBuilder.append("+");
/*      */     }
/*  957 */     if ((paramInt & 0x20) != 0) {
/*  958 */       localStringBuilder.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
/*  959 */       localStringBuilder.append("+");
/*      */     }
/*  961 */     if ((paramInt & 0x10) != 0) {
/*  962 */       localStringBuilder.append(Toolkit.getProperty("AWT.button1", "Button1"));
/*  963 */       localStringBuilder.append("+");
/*      */     }
/*  965 */     if ((paramInt & 0x8) != 0) {
/*  966 */       localStringBuilder.append(Toolkit.getProperty("AWT.button2", "Button2"));
/*  967 */       localStringBuilder.append("+");
/*      */     }
/*  969 */     if ((paramInt & 0x4) != 0) {
/*  970 */       localStringBuilder.append(Toolkit.getProperty("AWT.button3", "Button3"));
/*  971 */       localStringBuilder.append("+");
/*      */     }
/*      */ 
/*  981 */     for (int j = 1; j <= cachedNumberOfButtons; j++) {
/*  982 */       int i = InputEvent.getMaskForButton(j);
/*  983 */       if (((paramInt & i) != 0) && (localStringBuilder.indexOf(Toolkit.getProperty("AWT.button" + j, "Button" + j)) == -1))
/*      */       {
/*  986 */         localStringBuilder.append(Toolkit.getProperty("AWT.button" + j, "Button" + j));
/*  987 */         localStringBuilder.append("+");
/*      */       }
/*      */     }
/*      */ 
/*  991 */     if (localStringBuilder.length() > 0) {
/*  992 */       localStringBuilder.setLength(localStringBuilder.length() - 1);
/*      */     }
/*  994 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   public String paramString()
/*      */   {
/* 1004 */     StringBuilder localStringBuilder = new StringBuilder(80);
/*      */ 
/* 1006 */     switch (this.id) {
/*      */     case 501:
/* 1008 */       localStringBuilder.append("MOUSE_PRESSED");
/* 1009 */       break;
/*      */     case 502:
/* 1011 */       localStringBuilder.append("MOUSE_RELEASED");
/* 1012 */       break;
/*      */     case 500:
/* 1014 */       localStringBuilder.append("MOUSE_CLICKED");
/* 1015 */       break;
/*      */     case 504:
/* 1017 */       localStringBuilder.append("MOUSE_ENTERED");
/* 1018 */       break;
/*      */     case 505:
/* 1020 */       localStringBuilder.append("MOUSE_EXITED");
/* 1021 */       break;
/*      */     case 503:
/* 1023 */       localStringBuilder.append("MOUSE_MOVED");
/* 1024 */       break;
/*      */     case 506:
/* 1026 */       localStringBuilder.append("MOUSE_DRAGGED");
/* 1027 */       break;
/*      */     case 507:
/* 1029 */       localStringBuilder.append("MOUSE_WHEEL");
/* 1030 */       break;
/*      */     default:
/* 1032 */       localStringBuilder.append("unknown type");
/*      */     }
/*      */ 
/* 1036 */     localStringBuilder.append(",(").append(this.x).append(",").append(this.y).append(")");
/* 1037 */     localStringBuilder.append(",absolute(").append(this.xAbs).append(",").append(this.yAbs).append(")");
/*      */ 
/* 1039 */     if ((this.id != 506) && (this.id != 503)) {
/* 1040 */       localStringBuilder.append(",button=").append(getButton());
/*      */     }
/*      */ 
/* 1043 */     if (getModifiers() != 0) {
/* 1044 */       localStringBuilder.append(",modifiers=").append(getMouseModifiersText(this.modifiers));
/*      */     }
/*      */ 
/* 1047 */     if (getModifiersEx() != 0)
/*      */     {
/* 1050 */       localStringBuilder.append(",extModifiers=").append(getModifiersExText(getModifiersEx()));
/*      */     }
/*      */ 
/* 1053 */     localStringBuilder.append(",clickCount=").append(this.clickCount);
/*      */ 
/* 1055 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   private void setNewModifiers()
/*      */   {
/* 1063 */     if ((this.modifiers & 0x10) != 0) {
/* 1064 */       this.modifiers |= 1024;
/*      */     }
/* 1066 */     if ((this.modifiers & 0x8) != 0) {
/* 1067 */       this.modifiers |= 2048;
/*      */     }
/* 1069 */     if ((this.modifiers & 0x4) != 0) {
/* 1070 */       this.modifiers |= 4096;
/*      */     }
/* 1072 */     if ((this.id == 501) || (this.id == 502) || (this.id == 500))
/*      */     {
/* 1076 */       if ((this.modifiers & 0x10) != 0) {
/* 1077 */         this.button = 1;
/* 1078 */         this.modifiers &= -13;
/* 1079 */         if (this.id != 501)
/* 1080 */           this.modifiers &= -1025;
/*      */       }
/* 1082 */       else if ((this.modifiers & 0x8) != 0) {
/* 1083 */         this.button = 2;
/* 1084 */         this.modifiers &= -21;
/* 1085 */         if (this.id != 501)
/* 1086 */           this.modifiers &= -2049;
/*      */       }
/* 1088 */       else if ((this.modifiers & 0x4) != 0) {
/* 1089 */         this.button = 3;
/* 1090 */         this.modifiers &= -25;
/* 1091 */         if (this.id != 501) {
/* 1092 */           this.modifiers &= -4097;
/*      */         }
/*      */       }
/*      */     }
/* 1096 */     if ((this.modifiers & 0x8) != 0) {
/* 1097 */       this.modifiers |= 512;
/*      */     }
/* 1099 */     if ((this.modifiers & 0x4) != 0) {
/* 1100 */       this.modifiers |= 256;
/*      */     }
/* 1102 */     if ((this.modifiers & 0x1) != 0) {
/* 1103 */       this.modifiers |= 64;
/*      */     }
/* 1105 */     if ((this.modifiers & 0x2) != 0) {
/* 1106 */       this.modifiers |= 128;
/*      */     }
/* 1108 */     if ((this.modifiers & 0x20) != 0)
/* 1109 */       this.modifiers |= 8192;
/*      */   }
/*      */ 
/*      */   private void setOldModifiers()
/*      */   {
/* 1117 */     if ((this.id == 501) || (this.id == 502) || (this.id == 500))
/*      */     {
/* 1121 */       switch (this.button) {
/*      */       case 1:
/* 1123 */         this.modifiers |= 16;
/* 1124 */         break;
/*      */       case 2:
/* 1126 */         this.modifiers |= 8;
/* 1127 */         break;
/*      */       case 3:
/* 1129 */         this.modifiers |= 4;
/*      */       }
/*      */     }
/*      */     else {
/* 1133 */       if ((this.modifiers & 0x400) != 0) {
/* 1134 */         this.modifiers |= 16;
/*      */       }
/* 1136 */       if ((this.modifiers & 0x800) != 0) {
/* 1137 */         this.modifiers |= 8;
/*      */       }
/* 1139 */       if ((this.modifiers & 0x1000) != 0) {
/* 1140 */         this.modifiers |= 4;
/*      */       }
/*      */     }
/* 1143 */     if ((this.modifiers & 0x200) != 0) {
/* 1144 */       this.modifiers |= 8;
/*      */     }
/* 1146 */     if ((this.modifiers & 0x100) != 0) {
/* 1147 */       this.modifiers |= 4;
/*      */     }
/* 1149 */     if ((this.modifiers & 0x40) != 0) {
/* 1150 */       this.modifiers |= 1;
/*      */     }
/* 1152 */     if ((this.modifiers & 0x80) != 0) {
/* 1153 */       this.modifiers |= 2;
/*      */     }
/* 1155 */     if ((this.modifiers & 0x2000) != 0)
/* 1156 */       this.modifiers |= 32;
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1166 */     paramObjectInputStream.defaultReadObject();
/* 1167 */     if ((getModifiers() != 0) && (getModifiersEx() == 0))
/* 1168 */       setNewModifiers();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  390 */     NativeLibLoader.loadLibraries();
/*  391 */     if (!GraphicsEnvironment.isHeadless()) {
/*  392 */       initIDs();
/*      */     }
/*  394 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*  395 */     if ((localToolkit instanceof SunToolkit)) {
/*  396 */       cachedNumberOfButtons = ((SunToolkit)localToolkit).getNumberOfButtons();
/*      */     }
/*      */     else
/*      */     {
/*  400 */       cachedNumberOfButtons = 3;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.event.MouseEvent
 * JD-Core Version:    0.6.2
 */