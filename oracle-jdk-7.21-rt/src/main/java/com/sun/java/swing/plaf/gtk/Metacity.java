/*      */ package com.sun.java.swing.plaf.gtk;
/*      */ 
/*      */ import java.awt.AlphaComposite;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Composite;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.GradientPaint;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.Stroke;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.PathIterator;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.RectangularShape;
/*      */ import java.awt.image.FilteredImageSource;
/*      */ import java.awt.image.RGBImageFilter;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Reader;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.StringTokenizer;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JInternalFrame;
/*      */ import javax.swing.JInternalFrame.JDesktopIcon;
/*      */ import javax.swing.plaf.ColorUIResource;
/*      */ import javax.swing.plaf.synth.ColorType;
/*      */ import javax.swing.plaf.synth.SynthConstants;
/*      */ import javax.swing.plaf.synth.SynthContext;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.xml.sax.SAXException;
/*      */ import sun.swing.SwingUtilities2;
/*      */ 
/*      */ class Metacity
/*      */   implements SynthConstants
/*      */ {
/*      */   static Metacity INSTANCE;
/*      */   private static final String[] themeNames;
/*   91 */   private static boolean errorLogged = false;
/*      */   private static DocumentBuilder documentBuilder;
/*      */   private static Document xmlDoc;
/*      */   private static String userHome;
/*      */   private Node frame_style_set;
/*      */   private Map<String, Object> frameGeometry;
/*      */   private Map<String, Map<String, Object>> frameGeometries;
/*  100 */   private LayoutManager titlePaneLayout = new TitlePaneLayout();
/*      */ 
/*  102 */   private ColorizeImageFilter imageFilter = new ColorizeImageFilter();
/*  103 */   private URL themeDir = null;
/*      */   private SynthContext context;
/*      */   private String themeName;
/*  107 */   private ArithmeticExpressionEvaluator aee = new ArithmeticExpressionEvaluator();
/*      */   private Map<String, Integer> variables;
/*      */   private RoundRectClipShape roundedClipShape;
/*  605 */   private HashMap<String, Image> images = new HashMap();
/*      */ 
/*      */   protected Metacity(String paramString)
/*      */     throws IOException, ParserConfigurationException, SAXException
/*      */   {
/*  114 */     this.themeName = paramString;
/*  115 */     this.themeDir = getThemeDir(paramString);
/*  116 */     if (this.themeDir != null) {
/*  117 */       localObject1 = new URL(this.themeDir, "metacity-theme-1.xml");
/*  118 */       xmlDoc = getXMLDoc((URL)localObject1);
/*  119 */       if (xmlDoc == null)
/*  120 */         throw new IOException(((URL)localObject1).toString());
/*      */     }
/*      */     else {
/*  123 */       throw new FileNotFoundException(paramString);
/*      */     }
/*      */ 
/*  127 */     this.variables = new HashMap();
/*  128 */     Object localObject1 = xmlDoc.getElementsByTagName("constant");
/*  129 */     int i = ((NodeList)localObject1).getLength();
/*      */     Node localNode1;
/*      */     String str1;
/*      */     Object localObject2;
/*  130 */     for (int j = 0; j < i; j++) {
/*  131 */       localNode1 = ((NodeList)localObject1).item(j);
/*  132 */       str1 = getStringAttr(localNode1, "name");
/*  133 */       if (str1 != null) {
/*  134 */         localObject2 = getStringAttr(localNode1, "value");
/*  135 */         if (localObject2 != null) {
/*      */           try {
/*  137 */             this.variables.put(str1, Integer.valueOf(Integer.parseInt((String)localObject2)));
/*      */           } catch (NumberFormatException localNumberFormatException) {
/*  139 */             logError(paramString, localNumberFormatException);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  147 */     this.frameGeometries = new HashMap();
/*  148 */     localObject1 = xmlDoc.getElementsByTagName("frame_geometry");
/*  149 */     i = ((NodeList)localObject1).getLength();
/*  150 */     for (j = 0; j < i; j++) {
/*  151 */       localNode1 = ((NodeList)localObject1).item(j);
/*  152 */       str1 = getStringAttr(localNode1, "name");
/*  153 */       if (str1 != null) {
/*  154 */         localObject2 = new HashMap();
/*  155 */         this.frameGeometries.put(str1, localObject2);
/*      */ 
/*  157 */         String str2 = getStringAttr(localNode1, "parent");
/*  158 */         if (str2 != null) {
/*  159 */           ((HashMap)localObject2).putAll((Map)this.frameGeometries.get(str2));
/*      */         }
/*      */ 
/*  162 */         ((HashMap)localObject2).put("has_title", Boolean.valueOf(getBooleanAttr(localNode1, "has_title", true)));
/*      */ 
/*  164 */         ((HashMap)localObject2).put("rounded_top_left", Boolean.valueOf(getBooleanAttr(localNode1, "rounded_top_left", false)));
/*      */ 
/*  166 */         ((HashMap)localObject2).put("rounded_top_right", Boolean.valueOf(getBooleanAttr(localNode1, "rounded_top_right", false)));
/*      */ 
/*  168 */         ((HashMap)localObject2).put("rounded_bottom_left", Boolean.valueOf(getBooleanAttr(localNode1, "rounded_bottom_left", false)));
/*      */ 
/*  170 */         ((HashMap)localObject2).put("rounded_bottom_right", Boolean.valueOf(getBooleanAttr(localNode1, "rounded_bottom_right", false)));
/*      */ 
/*  173 */         NodeList localNodeList = localNode1.getChildNodes();
/*  174 */         int k = localNodeList.getLength();
/*  175 */         for (int m = 0; m < k; m++) {
/*  176 */           Node localNode2 = localNodeList.item(m);
/*  177 */           if (localNode2.getNodeType() == 1) {
/*  178 */             str1 = localNode2.getNodeName();
/*  179 */             Object localObject3 = null;
/*  180 */             if ("distance".equals(str1))
/*  181 */               localObject3 = Integer.valueOf(getIntAttr(localNode2, "value", 0));
/*  182 */             else if ("border".equals(str1)) {
/*  183 */               localObject3 = new Insets(getIntAttr(localNode2, "top", 0), getIntAttr(localNode2, "left", 0), getIntAttr(localNode2, "bottom", 0), getIntAttr(localNode2, "right", 0));
/*      */             }
/*  187 */             else if ("aspect_ratio".equals(str1))
/*  188 */               localObject3 = new Float(getFloatAttr(localNode2, "value", 1.0F));
/*      */             else {
/*  190 */               logError(paramString, "Unknown Metacity frame geometry value type: " + str1);
/*      */             }
/*  192 */             String str3 = getStringAttr(localNode2, "name");
/*  193 */             if ((str3 != null) && (localObject3 != null)) {
/*  194 */               ((HashMap)localObject2).put(str3, localObject3);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  200 */     this.frameGeometry = ((Map)this.frameGeometries.get("normal"));
/*      */   }
/*      */ 
/*      */   public static LayoutManager getTitlePaneLayout()
/*      */   {
/*  205 */     return INSTANCE.titlePaneLayout;
/*      */   }
/*      */ 
/*      */   private Shape getRoundedClipShape(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*      */   {
/*  210 */     if (this.roundedClipShape == null) {
/*  211 */       this.roundedClipShape = new RoundRectClipShape();
/*      */     }
/*  213 */     this.roundedClipShape.setRoundedRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*      */ 
/*  215 */     return this.roundedClipShape;
/*      */   }
/*      */ 
/*      */   void paintButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  219 */     updateFrameGeometry(paramSynthContext);
/*      */ 
/*  221 */     this.context = paramSynthContext;
/*  222 */     JButton localJButton = (JButton)paramSynthContext.getComponent();
/*  223 */     String str1 = localJButton.getName();
/*  224 */     int i = paramSynthContext.getComponentState();
/*      */ 
/*  226 */     JComponent localJComponent = (JComponent)localJButton.getParent();
/*  227 */     Container localContainer = localJComponent.getParent();
/*      */     JInternalFrame localJInternalFrame;
/*  230 */     if ((localContainer instanceof JInternalFrame))
/*  231 */       localJInternalFrame = (JInternalFrame)localContainer;
/*  232 */     else if ((localContainer instanceof JInternalFrame.JDesktopIcon))
/*  233 */       localJInternalFrame = ((JInternalFrame.JDesktopIcon)localContainer).getInternalFrame();
/*      */     else {
/*  235 */       return;
/*      */     }
/*      */ 
/*  238 */     boolean bool = localJInternalFrame.isSelected();
/*  239 */     localJButton.setOpaque(false);
/*      */ 
/*  241 */     String str2 = "normal";
/*  242 */     if ((i & 0x4) != 0)
/*  243 */       str2 = "pressed";
/*  244 */     else if ((i & 0x2) != 0) {
/*  245 */       str2 = "prelight";
/*      */     }
/*      */ 
/*  248 */     String str3 = null;
/*  249 */     String str4 = null;
/*  250 */     int j = 0;
/*  251 */     int k = 0;
/*      */ 
/*  254 */     if (str1 == "InternalFrameTitlePane.menuButton") {
/*  255 */       str3 = "menu";
/*  256 */       str4 = "left_left";
/*  257 */       j = 1;
/*  258 */     } else if (str1 == "InternalFrameTitlePane.iconifyButton") {
/*  259 */       str3 = "minimize";
/*  260 */       int m = (localJInternalFrame.isIconifiable() ? 1 : 0) + (localJInternalFrame.isMaximizable() ? 1 : 0) + (localJInternalFrame.isClosable() ? 1 : 0);
/*      */ 
/*  263 */       k = m == 1 ? 1 : 0;
/*  264 */       switch (m) { case 1:
/*  265 */         str4 = "right_right"; break;
/*      */       case 2:
/*  266 */         str4 = "right_middle"; break;
/*      */       case 3:
/*  267 */         str4 = "right_left"; }
/*      */     }
/*  269 */     else if (str1 == "InternalFrameTitlePane.maximizeButton") {
/*  270 */       str3 = "maximize";
/*  271 */       k = !localJInternalFrame.isClosable() ? 1 : 0;
/*  272 */       str4 = localJInternalFrame.isClosable() ? "right_middle" : "right_right";
/*  273 */     } else if (str1 == "InternalFrameTitlePane.closeButton") {
/*  274 */       str3 = "close";
/*  275 */       k = 1;
/*  276 */       str4 = "right_right";
/*      */     }
/*      */ 
/*  279 */     Node localNode1 = getNode(this.frame_style_set, "frame", new String[] { "focus", bool ? "yes" : "no", "state", localJInternalFrame.isMaximum() ? "maximized" : "normal" });
/*      */ 
/*  284 */     if ((str3 != null) && (localNode1 != null)) {
/*  285 */       Node localNode2 = getNode("frame_style", new String[] { "name", getStringAttr(localNode1, "style") });
/*      */ 
/*  288 */       if (localNode2 != null) {
/*  289 */         Shape localShape = paramGraphics.getClip();
/*  290 */         if (((k != 0) && (getBoolean("rounded_top_right", false))) || ((j != 0) && (getBoolean("rounded_top_left", false))))
/*      */         {
/*  293 */           Point localPoint = localJButton.getLocation();
/*  294 */           if (k != 0) {
/*  295 */             paramGraphics.setClip(getRoundedClipShape(0, 0, paramInt3, paramInt4, 12, 12, 2));
/*      */           }
/*      */           else {
/*  298 */             paramGraphics.setClip(getRoundedClipShape(0, 0, paramInt3, paramInt4, 11, 11, 1));
/*      */           }
/*      */ 
/*  302 */           Rectangle localRectangle = localShape.getBounds();
/*  303 */           paramGraphics.clipRect(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */         }
/*      */ 
/*  306 */         drawButton(localNode2, str4 + "_background", str2, paramGraphics, paramInt3, paramInt4, localJInternalFrame);
/*  307 */         drawButton(localNode2, str3, str2, paramGraphics, paramInt3, paramInt4, localJInternalFrame);
/*  308 */         paramGraphics.setClip(localShape);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void drawButton(Node paramNode, String paramString1, String paramString2, Graphics paramGraphics, int paramInt1, int paramInt2, JInternalFrame paramJInternalFrame)
/*      */   {
/*  315 */     Node localNode1 = getNode(paramNode, "button", new String[] { "function", paramString1, "state", paramString2 });
/*      */ 
/*  317 */     if ((localNode1 == null) && (!paramString2.equals("normal"))) {
/*  318 */       localNode1 = getNode(paramNode, "button", new String[] { "function", paramString1, "state", "normal" });
/*      */     }
/*      */ 
/*  321 */     if (localNode1 != null)
/*      */     {
/*  323 */       String str = getStringAttr(localNode1, "draw_ops");
/*      */       Node localNode2;
/*  324 */       if (str != null)
/*  325 */         localNode2 = getNode("draw_ops", new String[] { "name", str });
/*      */       else {
/*  327 */         localNode2 = getNode(localNode1, "draw_ops", null);
/*      */       }
/*  329 */       this.variables.put("width", Integer.valueOf(paramInt1));
/*  330 */       this.variables.put("height", Integer.valueOf(paramInt2));
/*  331 */       draw(localNode2, paramGraphics, paramJInternalFrame);
/*      */     }
/*      */   }
/*      */ 
/*      */   void paintFrameBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  336 */     updateFrameGeometry(paramSynthContext);
/*      */ 
/*  338 */     this.context = paramSynthContext;
/*  339 */     JComponent localJComponent1 = paramSynthContext.getComponent();
/*  340 */     JComponent localJComponent2 = findChild(localJComponent1, "InternalFrame.northPane");
/*      */ 
/*  342 */     if (localJComponent2 == null) {
/*  343 */       return;
/*      */     }
/*      */ 
/*  346 */     JInternalFrame localJInternalFrame = null;
/*  347 */     if ((localJComponent1 instanceof JInternalFrame)) {
/*  348 */       localJInternalFrame = (JInternalFrame)localJComponent1;
/*  349 */     } else if ((localJComponent1 instanceof JInternalFrame.JDesktopIcon)) {
/*  350 */       localJInternalFrame = ((JInternalFrame.JDesktopIcon)localJComponent1).getInternalFrame();
/*      */     } else {
/*  352 */       if (!$assertionsDisabled) throw new AssertionError("component is not JInternalFrame or JInternalFrame.JDesktopIcon");
/*  353 */       return;
/*      */     }
/*      */ 
/*  356 */     boolean bool1 = localJInternalFrame.isSelected();
/*  357 */     Font localFont = paramGraphics.getFont();
/*  358 */     paramGraphics.setFont(localJComponent2.getFont());
/*  359 */     paramGraphics.translate(paramInt1, paramInt2);
/*      */ 
/*  361 */     Rectangle localRectangle1 = calculateTitleArea(localJInternalFrame);
/*  362 */     JComponent localJComponent3 = findChild(localJComponent2, "InternalFrameTitlePane.menuButton");
/*      */ 
/*  364 */     Icon localIcon = localJInternalFrame.getFrameIcon();
/*  365 */     this.variables.put("mini_icon_width", Integer.valueOf(localIcon != null ? localIcon.getIconWidth() : 0));
/*      */ 
/*  367 */     this.variables.put("mini_icon_height", Integer.valueOf(localIcon != null ? localIcon.getIconHeight() : 0));
/*      */ 
/*  369 */     this.variables.put("title_width", Integer.valueOf(calculateTitleTextWidth(paramGraphics, localJInternalFrame)));
/*  370 */     FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(localJInternalFrame, paramGraphics);
/*  371 */     this.variables.put("title_height", Integer.valueOf(localFontMetrics.getAscent() + localFontMetrics.getDescent()));
/*      */ 
/*  374 */     this.variables.put("icon_width", Integer.valueOf(32));
/*  375 */     this.variables.put("icon_height", Integer.valueOf(32));
/*      */ 
/*  377 */     if (this.frame_style_set != null) {
/*  378 */       Node localNode1 = getNode(this.frame_style_set, "frame", new String[] { "focus", bool1 ? "yes" : "no", "state", localJInternalFrame.isMaximum() ? "maximized" : "normal" });
/*      */ 
/*  383 */       if (localNode1 != null) {
/*  384 */         Node localNode2 = getNode("frame_style", new String[] { "name", getStringAttr(localNode1, "style") });
/*      */ 
/*  387 */         if (localNode2 != null) {
/*  388 */           Shape localShape = paramGraphics.getClip();
/*  389 */           boolean bool2 = getBoolean("rounded_top_left", false);
/*  390 */           boolean bool3 = getBoolean("rounded_top_right", false);
/*  391 */           boolean bool4 = getBoolean("rounded_bottom_left", false);
/*  392 */           boolean bool5 = getBoolean("rounded_bottom_right", false);
/*      */ 
/*  394 */           if ((bool2) || (bool3) || (bool4) || (bool5)) {
/*  395 */             localJInternalFrame.setOpaque(false);
/*      */ 
/*  397 */             paramGraphics.setClip(getRoundedClipShape(0, 0, paramInt3, paramInt4, 12, 12, (bool2 ? 1 : 0) | (bool3 ? 2 : 0) | (bool4 ? 4 : 0) | (bool5 ? 8 : 0)));
/*      */           }
/*      */ 
/*  404 */           Rectangle localRectangle2 = localShape.getBounds();
/*  405 */           paramGraphics.clipRect(localRectangle2.x, localRectangle2.y, localRectangle2.width, localRectangle2.height);
/*      */ 
/*  408 */           int i = localJComponent2.getHeight();
/*      */ 
/*  410 */           boolean bool6 = localJInternalFrame.isIcon();
/*  411 */           Insets localInsets = getBorderInsets(paramSynthContext, null);
/*      */ 
/*  413 */           int j = getInt("left_titlebar_edge");
/*  414 */           int k = getInt("right_titlebar_edge");
/*  415 */           int m = getInt("top_titlebar_edge");
/*  416 */           int n = getInt("bottom_titlebar_edge");
/*      */ 
/*  418 */           if (!bool6) {
/*  419 */             drawPiece(localNode2, paramGraphics, "entire_background", 0, 0, paramInt3, paramInt4, localJInternalFrame);
/*      */           }
/*      */ 
/*  422 */           drawPiece(localNode2, paramGraphics, "titlebar", 0, 0, paramInt3, i, localJInternalFrame);
/*      */ 
/*  424 */           drawPiece(localNode2, paramGraphics, "titlebar_middle", j, m, paramInt3 - j - k, i - m - n, localJInternalFrame);
/*      */ 
/*  429 */           drawPiece(localNode2, paramGraphics, "left_titlebar_edge", 0, 0, j, i, localJInternalFrame);
/*      */ 
/*  431 */           drawPiece(localNode2, paramGraphics, "right_titlebar_edge", paramInt3 - k, 0, k, i, localJInternalFrame);
/*      */ 
/*  434 */           drawPiece(localNode2, paramGraphics, "top_titlebar_edge", 0, 0, paramInt3, m, localJInternalFrame);
/*      */ 
/*  436 */           drawPiece(localNode2, paramGraphics, "bottom_titlebar_edge", 0, i - n, paramInt3, n, localJInternalFrame);
/*      */ 
/*  439 */           drawPiece(localNode2, paramGraphics, "title", localRectangle1.x, localRectangle1.y, localRectangle1.width, localRectangle1.height, localJInternalFrame);
/*      */ 
/*  441 */           if (!bool6) {
/*  442 */             drawPiece(localNode2, paramGraphics, "left_edge", 0, i, localInsets.left, paramInt4 - i, localJInternalFrame);
/*      */ 
/*  444 */             drawPiece(localNode2, paramGraphics, "right_edge", paramInt3 - localInsets.right, i, localInsets.right, paramInt4 - i, localJInternalFrame);
/*      */ 
/*  446 */             drawPiece(localNode2, paramGraphics, "bottom_edge", 0, paramInt4 - localInsets.bottom, paramInt3, localInsets.bottom, localJInternalFrame);
/*      */ 
/*  448 */             drawPiece(localNode2, paramGraphics, "overlay", 0, 0, paramInt3, paramInt4, localJInternalFrame);
/*      */           }
/*      */ 
/*  451 */           paramGraphics.setClip(localShape);
/*      */         }
/*      */       }
/*      */     }
/*  455 */     paramGraphics.translate(-paramInt1, -paramInt2);
/*  456 */     paramGraphics.setFont(localFont);
/*      */   }
/*      */ 
/*      */   private static URL getThemeDir(String paramString)
/*      */   {
/*  570 */     return (URL)new Privileged(null).doPrivileged(Privileged.GET_THEME_DIR, paramString);
/*      */   }
/*      */ 
/*      */   private static String getUserTheme() {
/*  574 */     return (String)new Privileged(null).doPrivileged(Privileged.GET_USER_THEME, null);
/*      */   }
/*      */ 
/*      */   protected void tileImage(Graphics paramGraphics, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat) {
/*  578 */     Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
/*  579 */     Composite localComposite = localGraphics2D.getComposite();
/*      */ 
/*  581 */     int i = paramImage.getWidth(null);
/*  582 */     int j = paramImage.getHeight(null);
/*  583 */     int k = paramInt2;
/*  584 */     while (k < paramInt2 + paramInt4) {
/*  585 */       j = Math.min(j, paramInt2 + paramInt4 - k);
/*  586 */       int m = paramInt1;
/*  587 */       while (m < paramInt1 + paramInt3) {
/*  588 */         float f1 = (paramArrayOfFloat.length - 1.0F) * m / (paramInt1 + paramInt3);
/*  589 */         int n = (int)f1;
/*  590 */         f1 -= (int)f1;
/*  591 */         float f2 = (1.0F - f1) * paramArrayOfFloat[n];
/*  592 */         if (n + 1 < paramArrayOfFloat.length) {
/*  593 */           f2 += f1 * paramArrayOfFloat[(n + 1)];
/*      */         }
/*  595 */         localGraphics2D.setComposite(AlphaComposite.getInstance(3, f2));
/*  596 */         int i1 = Math.min(i, paramInt1 + paramInt3 - m);
/*  597 */         paramGraphics.drawImage(paramImage, m, k, m + i1, k + j, 0, 0, i1, j, null);
/*  598 */         m += i1;
/*      */       }
/*  600 */       k += j;
/*      */     }
/*  602 */     localGraphics2D.setComposite(localComposite);
/*      */   }
/*      */ 
/*      */   protected Image getImage(String paramString, Color paramColor)
/*      */   {
/*  608 */     Image localImage = (Image)this.images.get(paramString + "-" + paramColor.getRGB());
/*  609 */     if (localImage == null) {
/*  610 */       localImage = this.imageFilter.colorize(getImage(paramString), paramColor);
/*  611 */       if (localImage != null) {
/*  612 */         this.images.put(paramString + "-" + paramColor.getRGB(), localImage);
/*      */       }
/*      */     }
/*  615 */     return localImage;
/*      */   }
/*      */ 
/*      */   protected Image getImage(String paramString) {
/*  619 */     Image localImage = (Image)this.images.get(paramString);
/*  620 */     if (localImage == null) {
/*  621 */       if (this.themeDir != null)
/*      */         try {
/*  623 */           URL localURL = new URL(this.themeDir, paramString);
/*  624 */           localImage = (Image)new Privileged(null).doPrivileged(Privileged.GET_IMAGE, localURL);
/*      */         }
/*      */         catch (MalformedURLException localMalformedURLException)
/*      */         {
/*      */         }
/*  629 */       if (localImage != null) {
/*  630 */         this.images.put(paramString, localImage);
/*      */       }
/*      */     }
/*  633 */     return localImage;
/*      */   }
/*      */ 
/*      */   protected static JComponent findChild(JComponent paramJComponent, String paramString)
/*      */   {
/*  679 */     int i = paramJComponent.getComponentCount();
/*  680 */     for (int j = 0; j < i; j++) {
/*  681 */       JComponent localJComponent = (JComponent)paramJComponent.getComponent(j);
/*  682 */       if (paramString.equals(localJComponent.getName())) {
/*  683 */         return localJComponent;
/*      */       }
/*      */     }
/*  686 */     return null;
/*      */   }
/*      */ 
/*      */   protected Map getFrameGeometry()
/*      */   {
/*  832 */     return this.frameGeometry;
/*      */   }
/*      */ 
/*      */   protected void setFrameGeometry(JComponent paramJComponent, Map paramMap) {
/*  836 */     this.frameGeometry = paramMap;
/*  837 */     if ((getInt("top_height") == 0) && (paramJComponent != null))
/*  838 */       paramMap.put("top_height", Integer.valueOf(paramJComponent.getHeight()));
/*      */   }
/*      */ 
/*      */   protected int getInt(String paramString)
/*      */   {
/*  843 */     Integer localInteger = (Integer)this.frameGeometry.get(paramString);
/*  844 */     if (localInteger == null) {
/*  845 */       localInteger = (Integer)this.variables.get(paramString);
/*      */     }
/*  847 */     return localInteger != null ? localInteger.intValue() : 0;
/*      */   }
/*      */ 
/*      */   protected boolean getBoolean(String paramString, boolean paramBoolean) {
/*  851 */     Boolean localBoolean = (Boolean)this.frameGeometry.get(paramString);
/*  852 */     return localBoolean != null ? localBoolean.booleanValue() : paramBoolean;
/*      */   }
/*      */ 
/*      */   protected void drawArc(Node paramNode, Graphics paramGraphics)
/*      */   {
/*  857 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/*  858 */     Color localColor = parseColor(getStringAttr(localNamedNodeMap, "color"));
/*  859 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/*  860 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/*  861 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"));
/*  862 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"));
/*  863 */     int n = this.aee.evaluate(getStringAttr(localNamedNodeMap, "start_angle"));
/*  864 */     int i1 = this.aee.evaluate(getStringAttr(localNamedNodeMap, "extent_angle"));
/*  865 */     boolean bool = getBooleanAttr(paramNode, "filled", false);
/*  866 */     if (getInt("width") == -1) {
/*  867 */       i -= k;
/*      */     }
/*  869 */     if (getInt("height") == -1) {
/*  870 */       j -= m;
/*      */     }
/*  872 */     paramGraphics.setColor(localColor);
/*  873 */     if (bool)
/*  874 */       paramGraphics.fillArc(i, j, k, m, n, i1);
/*      */     else
/*  876 */       paramGraphics.drawArc(i, j, k, m, n, i1);
/*      */   }
/*      */ 
/*      */   protected void drawLine(Node paramNode, Graphics paramGraphics)
/*      */   {
/*  881 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/*  882 */     Color localColor = parseColor(getStringAttr(localNamedNodeMap, "color"));
/*  883 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x1"));
/*  884 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y1"));
/*  885 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x2"));
/*  886 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y2"));
/*  887 */     int n = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"), 1);
/*  888 */     paramGraphics.setColor(localColor);
/*  889 */     if (n != 1) {
/*  890 */       Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
/*  891 */       Stroke localStroke = localGraphics2D.getStroke();
/*  892 */       localGraphics2D.setStroke(new BasicStroke(n));
/*  893 */       localGraphics2D.drawLine(i, j, k, m);
/*  894 */       localGraphics2D.setStroke(localStroke);
/*      */     } else {
/*  896 */       paramGraphics.drawLine(i, j, k, m);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void drawRectangle(Node paramNode, Graphics paramGraphics) {
/*  901 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/*  902 */     Color localColor = parseColor(getStringAttr(localNamedNodeMap, "color"));
/*  903 */     boolean bool = getBooleanAttr(paramNode, "filled", false);
/*  904 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/*  905 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/*  906 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"));
/*  907 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"));
/*  908 */     paramGraphics.setColor(localColor);
/*  909 */     if (getInt("width") == -1) {
/*  910 */       i -= k;
/*      */     }
/*  912 */     if (getInt("height") == -1) {
/*  913 */       j -= m;
/*      */     }
/*  915 */     if (bool)
/*  916 */       paramGraphics.fillRect(i, j, k, m);
/*      */     else
/*  918 */       paramGraphics.drawRect(i, j, k, m);
/*      */   }
/*      */ 
/*      */   protected void drawTile(Node paramNode, Graphics paramGraphics, JInternalFrame paramJInternalFrame)
/*      */   {
/*  923 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/*  924 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/*  925 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/*  926 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"));
/*  927 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"));
/*  928 */     int n = this.aee.evaluate(getStringAttr(localNamedNodeMap, "tile_width"));
/*  929 */     int i1 = this.aee.evaluate(getStringAttr(localNamedNodeMap, "tile_height"));
/*  930 */     int i2 = getInt("width");
/*  931 */     int i3 = getInt("height");
/*  932 */     if (i2 == -1) {
/*  933 */       i -= k;
/*      */     }
/*  935 */     if (i3 == -1) {
/*  936 */       j -= m;
/*      */     }
/*  938 */     Shape localShape = paramGraphics.getClip();
/*  939 */     if ((paramGraphics instanceof Graphics2D)) {
/*  940 */       ((Graphics2D)paramGraphics).clip(new Rectangle(i, j, k, m));
/*      */     }
/*  942 */     this.variables.put("width", Integer.valueOf(n));
/*  943 */     this.variables.put("height", Integer.valueOf(i1));
/*      */ 
/*  945 */     Node localNode = getNode("draw_ops", new String[] { "name", getStringAttr(paramNode, "name") });
/*      */ 
/*  947 */     int i4 = j;
/*  948 */     while (i4 < j + m) {
/*  949 */       int i5 = i;
/*  950 */       while (i5 < i + k) {
/*  951 */         paramGraphics.translate(i5, i4);
/*  952 */         draw(localNode, paramGraphics, paramJInternalFrame);
/*  953 */         paramGraphics.translate(-i5, -i4);
/*  954 */         i5 += n;
/*      */       }
/*  956 */       i4 += i1;
/*      */     }
/*      */ 
/*  959 */     this.variables.put("width", Integer.valueOf(i2));
/*  960 */     this.variables.put("height", Integer.valueOf(i3));
/*  961 */     paramGraphics.setClip(localShape);
/*      */   }
/*      */ 
/*      */   protected void drawTint(Node paramNode, Graphics paramGraphics) {
/*  965 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/*  966 */     Color localColor = parseColor(getStringAttr(localNamedNodeMap, "color"));
/*  967 */     float f = Float.parseFloat(getStringAttr(localNamedNodeMap, "alpha"));
/*  968 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/*  969 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/*  970 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"));
/*  971 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"));
/*  972 */     if (getInt("width") == -1) {
/*  973 */       i -= k;
/*      */     }
/*  975 */     if (getInt("height") == -1) {
/*  976 */       j -= m;
/*      */     }
/*  978 */     if ((paramGraphics instanceof Graphics2D)) {
/*  979 */       Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
/*  980 */       Composite localComposite = localGraphics2D.getComposite();
/*  981 */       AlphaComposite localAlphaComposite = AlphaComposite.getInstance(3, f);
/*  982 */       localGraphics2D.setComposite(localAlphaComposite);
/*  983 */       localGraphics2D.setColor(localColor);
/*  984 */       localGraphics2D.fillRect(i, j, k, m);
/*  985 */       localGraphics2D.setComposite(localComposite);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void drawTitle(Node paramNode, Graphics paramGraphics, JInternalFrame paramJInternalFrame) {
/*  990 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/*  991 */     String str1 = getStringAttr(localNamedNodeMap, "color");
/*  992 */     int i = str1.indexOf("gtk:fg[");
/*  993 */     if (i > 0) {
/*  994 */       str1 = str1.substring(0, i) + "gtk:text[" + str1.substring(i + 7);
/*      */     }
/*  996 */     Color localColor = parseColor(str1);
/*  997 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/*  998 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/*      */ 
/* 1000 */     String str2 = paramJInternalFrame.getTitle();
/* 1001 */     if (str2 != null) {
/* 1002 */       FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramJInternalFrame, paramGraphics);
/* 1003 */       str2 = SwingUtilities2.clipStringIfNecessary(paramJInternalFrame, localFontMetrics, str2, calculateTitleArea(paramJInternalFrame).width);
/*      */ 
/* 1005 */       paramGraphics.setColor(localColor);
/* 1006 */       SwingUtilities2.drawString(paramJInternalFrame, paramGraphics, str2, j, k + localFontMetrics.getAscent());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Dimension calculateButtonSize(JComponent paramJComponent) {
/* 1011 */     int i = getInt("button_height");
/* 1012 */     if (i == 0) {
/* 1013 */       i = paramJComponent.getHeight();
/* 1014 */       if (i == 0) {
/* 1015 */         i = 13;
/*      */       } else {
/* 1017 */         Insets localInsets = (Insets)this.frameGeometry.get("button_border");
/* 1018 */         if (localInsets != null) {
/* 1019 */           i -= localInsets.top + localInsets.bottom;
/*      */         }
/*      */       }
/*      */     }
/* 1023 */     int j = getInt("button_width");
/* 1024 */     if (j == 0) {
/* 1025 */       j = i;
/* 1026 */       Float localFloat = (Float)this.frameGeometry.get("aspect_ratio");
/* 1027 */       if (localFloat != null) {
/* 1028 */         j = (int)(i / localFloat.floatValue());
/*      */       }
/*      */     }
/* 1031 */     return new Dimension(j, i);
/*      */   }
/*      */ 
/*      */   protected Rectangle calculateTitleArea(JInternalFrame paramJInternalFrame) {
/* 1035 */     JComponent localJComponent = findChild(paramJInternalFrame, "InternalFrame.northPane");
/* 1036 */     Dimension localDimension = calculateButtonSize(localJComponent);
/* 1037 */     Insets localInsets1 = (Insets)this.frameGeometry.get("title_border");
/* 1038 */     Insets localInsets2 = (Insets)getFrameGeometry().get("button_border");
/*      */ 
/* 1040 */     Rectangle localRectangle = new Rectangle();
/* 1041 */     localRectangle.x = getInt("left_titlebar_edge");
/* 1042 */     localRectangle.y = 0;
/* 1043 */     localRectangle.height = localJComponent.getHeight();
/* 1044 */     if (localInsets1 != null) {
/* 1045 */       localRectangle.x += localInsets1.left;
/* 1046 */       localRectangle.y += localInsets1.top;
/* 1047 */       localRectangle.height -= localInsets1.top + localInsets1.bottom;
/*      */     }
/*      */ 
/* 1050 */     if (localJComponent.getParent().getComponentOrientation().isLeftToRight()) {
/* 1051 */       localRectangle.x += localDimension.width;
/* 1052 */       if (localInsets2 != null) {
/* 1053 */         localRectangle.x += localInsets2.left;
/*      */       }
/* 1055 */       localRectangle.width = (localJComponent.getWidth() - localRectangle.x - getInt("right_titlebar_edge"));
/* 1056 */       if (paramJInternalFrame.isClosable()) {
/* 1057 */         localRectangle.width -= localDimension.width;
/*      */       }
/* 1059 */       if (paramJInternalFrame.isMaximizable()) {
/* 1060 */         localRectangle.width -= localDimension.width;
/*      */       }
/* 1062 */       if (paramJInternalFrame.isIconifiable())
/* 1063 */         localRectangle.width -= localDimension.width;
/*      */     }
/*      */     else {
/* 1066 */       if (paramJInternalFrame.isClosable()) {
/* 1067 */         localRectangle.x += localDimension.width;
/*      */       }
/* 1069 */       if (paramJInternalFrame.isMaximizable()) {
/* 1070 */         localRectangle.x += localDimension.width;
/*      */       }
/* 1072 */       if (paramJInternalFrame.isIconifiable()) {
/* 1073 */         localRectangle.x += localDimension.width;
/*      */       }
/* 1075 */       localRectangle.width = (localJComponent.getWidth() - localRectangle.x - getInt("right_titlebar_edge") - localDimension.width);
/*      */ 
/* 1077 */       if (localInsets2 != null) {
/* 1078 */         localRectangle.x -= localInsets2.right;
/*      */       }
/*      */     }
/* 1081 */     if (localInsets1 != null) {
/* 1082 */       localRectangle.width -= localInsets1.right;
/*      */     }
/* 1084 */     return localRectangle;
/*      */   }
/*      */ 
/*      */   protected int calculateTitleTextWidth(Graphics paramGraphics, JInternalFrame paramJInternalFrame)
/*      */   {
/* 1089 */     String str = paramJInternalFrame.getTitle();
/* 1090 */     if (str != null) {
/* 1091 */       Rectangle localRectangle = calculateTitleArea(paramJInternalFrame);
/* 1092 */       return Math.min(SwingUtilities2.stringWidth(paramJInternalFrame, SwingUtilities2.getFontMetrics(paramJInternalFrame, paramGraphics), str), localRectangle.width);
/*      */     }
/*      */ 
/* 1095 */     return 0;
/*      */   }
/*      */ 
/*      */   protected void setClip(Node paramNode, Graphics paramGraphics) {
/* 1099 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/* 1100 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/* 1101 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/* 1102 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"));
/* 1103 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"));
/* 1104 */     if (getInt("width") == -1) {
/* 1105 */       i -= k;
/*      */     }
/* 1107 */     if (getInt("height") == -1) {
/* 1108 */       j -= m;
/*      */     }
/* 1110 */     if ((paramGraphics instanceof Graphics2D))
/* 1111 */       ((Graphics2D)paramGraphics).clip(new Rectangle(i, j, k, m));
/*      */   }
/*      */ 
/*      */   protected void drawGTKArrow(Node paramNode, Graphics paramGraphics)
/*      */   {
/* 1116 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/* 1117 */     String str1 = getStringAttr(localNamedNodeMap, "arrow");
/* 1118 */     String str2 = getStringAttr(localNamedNodeMap, "shadow");
/* 1119 */     String str3 = getStringAttr(localNamedNodeMap, "state").toUpperCase();
/* 1120 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/* 1121 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/* 1122 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"));
/* 1123 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"));
/*      */ 
/* 1125 */     int n = -1;
/* 1126 */     if ("NORMAL".equals(str3))
/* 1127 */       n = 1;
/* 1128 */     else if ("SELECTED".equals(str3))
/* 1129 */       n = 512;
/* 1130 */     else if ("INSENSITIVE".equals(str3))
/* 1131 */       n = 8;
/* 1132 */     else if ("PRELIGHT".equals(str3)) {
/* 1133 */       n = 2;
/*      */     }
/*      */ 
/* 1136 */     GTKConstants.ShadowType localShadowType = null;
/* 1137 */     if ("in".equals(str2))
/* 1138 */       localShadowType = GTKConstants.ShadowType.IN;
/* 1139 */     else if ("out".equals(str2))
/* 1140 */       localShadowType = GTKConstants.ShadowType.OUT;
/* 1141 */     else if ("etched_in".equals(str2))
/* 1142 */       localShadowType = GTKConstants.ShadowType.ETCHED_IN;
/* 1143 */     else if ("etched_out".equals(str2))
/* 1144 */       localShadowType = GTKConstants.ShadowType.ETCHED_OUT;
/* 1145 */     else if ("none".equals(str2)) {
/* 1146 */       localShadowType = GTKConstants.ShadowType.NONE;
/*      */     }
/*      */ 
/* 1149 */     GTKConstants.ArrowType localArrowType = null;
/* 1150 */     if ("up".equals(str1))
/* 1151 */       localArrowType = GTKConstants.ArrowType.UP;
/* 1152 */     else if ("down".equals(str1))
/* 1153 */       localArrowType = GTKConstants.ArrowType.DOWN;
/* 1154 */     else if ("left".equals(str1))
/* 1155 */       localArrowType = GTKConstants.ArrowType.LEFT;
/* 1156 */     else if ("right".equals(str1)) {
/* 1157 */       localArrowType = GTKConstants.ArrowType.RIGHT;
/*      */     }
/*      */ 
/* 1160 */     GTKPainter.INSTANCE.paintMetacityElement(this.context, paramGraphics, n, "metacity-arrow", i, j, k, m, localShadowType, localArrowType);
/*      */   }
/*      */ 
/*      */   protected void drawGTKBox(Node paramNode, Graphics paramGraphics)
/*      */   {
/* 1165 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/* 1166 */     String str1 = getStringAttr(localNamedNodeMap, "shadow");
/* 1167 */     String str2 = getStringAttr(localNamedNodeMap, "state").toUpperCase();
/* 1168 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/* 1169 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/* 1170 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"));
/* 1171 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"));
/*      */ 
/* 1173 */     int n = -1;
/* 1174 */     if ("NORMAL".equals(str2))
/* 1175 */       n = 1;
/* 1176 */     else if ("SELECTED".equals(str2))
/* 1177 */       n = 512;
/* 1178 */     else if ("INSENSITIVE".equals(str2))
/* 1179 */       n = 8;
/* 1180 */     else if ("PRELIGHT".equals(str2)) {
/* 1181 */       n = 2;
/*      */     }
/*      */ 
/* 1184 */     GTKConstants.ShadowType localShadowType = null;
/* 1185 */     if ("in".equals(str1))
/* 1186 */       localShadowType = GTKConstants.ShadowType.IN;
/* 1187 */     else if ("out".equals(str1))
/* 1188 */       localShadowType = GTKConstants.ShadowType.OUT;
/* 1189 */     else if ("etched_in".equals(str1))
/* 1190 */       localShadowType = GTKConstants.ShadowType.ETCHED_IN;
/* 1191 */     else if ("etched_out".equals(str1))
/* 1192 */       localShadowType = GTKConstants.ShadowType.ETCHED_OUT;
/* 1193 */     else if ("none".equals(str1)) {
/* 1194 */       localShadowType = GTKConstants.ShadowType.NONE;
/*      */     }
/* 1196 */     GTKPainter.INSTANCE.paintMetacityElement(this.context, paramGraphics, n, "metacity-box", i, j, k, m, localShadowType, null);
/*      */   }
/*      */ 
/*      */   protected void drawGTKVLine(Node paramNode, Graphics paramGraphics)
/*      */   {
/* 1201 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/* 1202 */     String str = getStringAttr(localNamedNodeMap, "state").toUpperCase();
/*      */ 
/* 1204 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/* 1205 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y1"));
/* 1206 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y2"));
/*      */ 
/* 1208 */     int m = -1;
/* 1209 */     if ("NORMAL".equals(str))
/* 1210 */       m = 1;
/* 1211 */     else if ("SELECTED".equals(str))
/* 1212 */       m = 512;
/* 1213 */     else if ("INSENSITIVE".equals(str))
/* 1214 */       m = 8;
/* 1215 */     else if ("PRELIGHT".equals(str)) {
/* 1216 */       m = 2;
/*      */     }
/*      */ 
/* 1219 */     GTKPainter.INSTANCE.paintMetacityElement(this.context, paramGraphics, m, "metacity-vline", i, j, 1, k - j, null, null);
/*      */   }
/*      */ 
/*      */   protected void drawGradient(Node paramNode, Graphics paramGraphics)
/*      */   {
/* 1224 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/* 1225 */     String str = getStringAttr(localNamedNodeMap, "type");
/* 1226 */     float f = getFloatAttr(paramNode, "alpha", -1.0F);
/* 1227 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/* 1228 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/* 1229 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"));
/* 1230 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"));
/* 1231 */     if (getInt("width") == -1) {
/* 1232 */       i -= k;
/*      */     }
/* 1234 */     if (getInt("height") == -1) {
/* 1235 */       j -= m;
/*      */     }
/*      */ 
/* 1239 */     Node[] arrayOfNode = getNodesByName(paramNode, "color");
/* 1240 */     Color[] arrayOfColor = new Color[arrayOfNode.length];
/* 1241 */     for (int n = 0; n < arrayOfNode.length; n++) {
/* 1242 */       arrayOfColor[n] = parseColor(getStringAttr(arrayOfNode[n], "value"));
/*      */     }
/*      */ 
/* 1245 */     n = ("diagonal".equals(str)) || ("horizontal".equals(str)) ? 1 : 0;
/* 1246 */     int i1 = ("diagonal".equals(str)) || ("vertical".equals(str)) ? 1 : 0;
/*      */ 
/* 1248 */     if ((paramGraphics instanceof Graphics2D)) {
/* 1249 */       Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
/* 1250 */       Composite localComposite = localGraphics2D.getComposite();
/* 1251 */       if (f >= 0.0F) {
/* 1252 */         localGraphics2D.setComposite(AlphaComposite.getInstance(3, f));
/*      */       }
/* 1254 */       int i2 = arrayOfColor.length - 1;
/* 1255 */       for (int i3 = 0; i3 < i2; i3++) {
/* 1256 */         localGraphics2D.setPaint(new GradientPaint(i + (n != 0 ? i3 * k / i2 : 0), j + (i1 != 0 ? i3 * m / i2 : 0), arrayOfColor[i3], i + (n != 0 ? (i3 + 1) * k / i2 : 0), j + (i1 != 0 ? (i3 + 1) * m / i2 : 0), arrayOfColor[(i3 + 1)]));
/*      */ 
/* 1262 */         localGraphics2D.fillRect(i + (n != 0 ? i3 * k / i2 : 0), j + (i1 != 0 ? i3 * m / i2 : 0), n != 0 ? k / i2 : k, i1 != 0 ? m / i2 : m);
/*      */       }
/*      */ 
/* 1267 */       localGraphics2D.setComposite(localComposite);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void drawImage(Node paramNode, Graphics paramGraphics) {
/* 1272 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/* 1273 */     String str1 = getStringAttr(localNamedNodeMap, "filename");
/* 1274 */     String str2 = getStringAttr(localNamedNodeMap, "colorize");
/* 1275 */     Color localColor = str2 != null ? parseColor(str2) : null;
/* 1276 */     String str3 = getStringAttr(localNamedNodeMap, "alpha");
/* 1277 */     Image localImage = localColor != null ? getImage(str1, localColor) : getImage(str1);
/* 1278 */     this.variables.put("object_width", Integer.valueOf(localImage.getWidth(null)));
/* 1279 */     this.variables.put("object_height", Integer.valueOf(localImage.getHeight(null)));
/* 1280 */     String str4 = getStringAttr(localNamedNodeMap, "fill_type");
/* 1281 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/* 1282 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/* 1283 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"));
/* 1284 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"));
/* 1285 */     if (getInt("width") == -1) {
/* 1286 */       i -= k;
/*      */     }
/* 1288 */     if (getInt("height") == -1) {
/* 1289 */       j -= m;
/*      */     }
/*      */ 
/* 1292 */     if (str3 != null)
/*      */     {
/*      */       Object localObject;
/* 1293 */       if ("tile".equals(str4)) {
/* 1294 */         StringTokenizer localStringTokenizer = new StringTokenizer(str3, ":");
/* 1295 */         localObject = new float[localStringTokenizer.countTokens()];
/* 1296 */         for (int n = 0; n < localObject.length; n++) {
/* 1297 */           localObject[n] = Float.parseFloat(localStringTokenizer.nextToken());
/*      */         }
/* 1299 */         tileImage(paramGraphics, localImage, i, j, k, m, (float[])localObject);
/*      */       } else {
/* 1301 */         float f = Float.parseFloat(str3);
/* 1302 */         if ((paramGraphics instanceof Graphics2D)) {
/* 1303 */           localObject = (Graphics2D)paramGraphics;
/* 1304 */           Composite localComposite = ((Graphics2D)localObject).getComposite();
/* 1305 */           ((Graphics2D)localObject).setComposite(AlphaComposite.getInstance(3, f));
/* 1306 */           ((Graphics2D)localObject).drawImage(localImage, i, j, k, m, null);
/* 1307 */           ((Graphics2D)localObject).setComposite(localComposite);
/*      */         }
/*      */       }
/*      */     } else {
/* 1311 */       paramGraphics.drawImage(localImage, i, j, k, m, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void drawIcon(Node paramNode, Graphics paramGraphics, JInternalFrame paramJInternalFrame) {
/* 1316 */     Icon localIcon = paramJInternalFrame.getFrameIcon();
/* 1317 */     if (localIcon == null) {
/* 1318 */       return;
/*      */     }
/*      */ 
/* 1321 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/* 1322 */     String str = getStringAttr(localNamedNodeMap, "alpha");
/* 1323 */     int i = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"));
/* 1324 */     int j = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"));
/* 1325 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"));
/* 1326 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"));
/* 1327 */     if (getInt("width") == -1) {
/* 1328 */       i -= k;
/*      */     }
/* 1330 */     if (getInt("height") == -1) {
/* 1331 */       j -= m;
/*      */     }
/*      */ 
/* 1334 */     if (str != null) {
/* 1335 */       float f = Float.parseFloat(str);
/* 1336 */       if ((paramGraphics instanceof Graphics2D)) {
/* 1337 */         Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
/* 1338 */         Composite localComposite = localGraphics2D.getComposite();
/* 1339 */         localGraphics2D.setComposite(AlphaComposite.getInstance(3, f));
/* 1340 */         localIcon.paintIcon(paramJInternalFrame, paramGraphics, i, j);
/* 1341 */         localGraphics2D.setComposite(localComposite);
/*      */       }
/*      */     } else {
/* 1344 */       localIcon.paintIcon(paramJInternalFrame, paramGraphics, i, j);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void drawInclude(Node paramNode, Graphics paramGraphics, JInternalFrame paramJInternalFrame) {
/* 1349 */     int i = getInt("width");
/* 1350 */     int j = getInt("height");
/*      */ 
/* 1352 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/* 1353 */     int k = this.aee.evaluate(getStringAttr(localNamedNodeMap, "x"), 0);
/* 1354 */     int m = this.aee.evaluate(getStringAttr(localNamedNodeMap, "y"), 0);
/* 1355 */     int n = this.aee.evaluate(getStringAttr(localNamedNodeMap, "width"), -1);
/* 1356 */     int i1 = this.aee.evaluate(getStringAttr(localNamedNodeMap, "height"), -1);
/*      */ 
/* 1358 */     if (n != -1) {
/* 1359 */       this.variables.put("width", Integer.valueOf(n));
/*      */     }
/* 1361 */     if (i1 != -1) {
/* 1362 */       this.variables.put("height", Integer.valueOf(i1));
/*      */     }
/*      */ 
/* 1365 */     Node localNode = getNode("draw_ops", new String[] { "name", getStringAttr(paramNode, "name") });
/*      */ 
/* 1368 */     paramGraphics.translate(k, m);
/* 1369 */     draw(localNode, paramGraphics, paramJInternalFrame);
/* 1370 */     paramGraphics.translate(-k, -m);
/*      */ 
/* 1372 */     if (n != -1) {
/* 1373 */       this.variables.put("width", Integer.valueOf(i));
/*      */     }
/* 1375 */     if (i1 != -1)
/* 1376 */       this.variables.put("height", Integer.valueOf(j));
/*      */   }
/*      */ 
/*      */   protected void draw(Node paramNode, Graphics paramGraphics, JInternalFrame paramJInternalFrame)
/*      */   {
/* 1381 */     if (paramNode != null) {
/* 1382 */       NodeList localNodeList = paramNode.getChildNodes();
/* 1383 */       if (localNodeList != null) {
/* 1384 */         Shape localShape = paramGraphics.getClip();
/* 1385 */         for (int i = 0; i < localNodeList.getLength(); i++) {
/* 1386 */           Node localNode = localNodeList.item(i);
/* 1387 */           if (localNode.getNodeType() == 1) {
/*      */             try {
/* 1389 */               String str = localNode.getNodeName();
/* 1390 */               if ("include".equals(str))
/* 1391 */                 drawInclude(localNode, paramGraphics, paramJInternalFrame);
/* 1392 */               else if ("arc".equals(str))
/* 1393 */                 drawArc(localNode, paramGraphics);
/* 1394 */               else if ("clip".equals(str))
/* 1395 */                 setClip(localNode, paramGraphics);
/* 1396 */               else if ("gradient".equals(str))
/* 1397 */                 drawGradient(localNode, paramGraphics);
/* 1398 */               else if ("gtk_arrow".equals(str))
/* 1399 */                 drawGTKArrow(localNode, paramGraphics);
/* 1400 */               else if ("gtk_box".equals(str))
/* 1401 */                 drawGTKBox(localNode, paramGraphics);
/* 1402 */               else if ("gtk_vline".equals(str))
/* 1403 */                 drawGTKVLine(localNode, paramGraphics);
/* 1404 */               else if ("image".equals(str))
/* 1405 */                 drawImage(localNode, paramGraphics);
/* 1406 */               else if ("icon".equals(str))
/* 1407 */                 drawIcon(localNode, paramGraphics, paramJInternalFrame);
/* 1408 */               else if ("line".equals(str))
/* 1409 */                 drawLine(localNode, paramGraphics);
/* 1410 */               else if ("rectangle".equals(str))
/* 1411 */                 drawRectangle(localNode, paramGraphics);
/* 1412 */               else if ("tint".equals(str))
/* 1413 */                 drawTint(localNode, paramGraphics);
/* 1414 */               else if ("tile".equals(str))
/* 1415 */                 drawTile(localNode, paramGraphics, paramJInternalFrame);
/* 1416 */               else if ("title".equals(str))
/* 1417 */                 drawTitle(localNode, paramGraphics, paramJInternalFrame);
/*      */               else
/* 1419 */                 System.err.println("Unknown Metacity drawing op: " + localNode);
/*      */             }
/*      */             catch (NumberFormatException localNumberFormatException) {
/* 1422 */               logError(this.themeName, localNumberFormatException);
/*      */             }
/*      */           }
/*      */         }
/* 1426 */         paramGraphics.setClip(localShape);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void drawPiece(Node paramNode, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, JInternalFrame paramJInternalFrame)
/*      */   {
/* 1433 */     Node localNode1 = getNode(paramNode, "piece", new String[] { "position", paramString });
/* 1434 */     if (localNode1 != null)
/*      */     {
/* 1436 */       String str = getStringAttr(localNode1, "draw_ops");
/*      */       Node localNode2;
/* 1437 */       if (str != null)
/* 1438 */         localNode2 = getNode("draw_ops", new String[] { "name", str });
/*      */       else {
/* 1440 */         localNode2 = getNode(localNode1, "draw_ops", null);
/*      */       }
/* 1442 */       this.variables.put("width", Integer.valueOf(paramInt3));
/* 1443 */       this.variables.put("height", Integer.valueOf(paramInt4));
/* 1444 */       paramGraphics.translate(paramInt1, paramInt2);
/* 1445 */       draw(localNode2, paramGraphics, paramJInternalFrame);
/* 1446 */       paramGraphics.translate(-paramInt1, -paramInt2);
/*      */     }
/*      */   }
/*      */ 
/*      */   Insets getBorderInsets(SynthContext paramSynthContext, Insets paramInsets)
/*      */   {
/* 1452 */     updateFrameGeometry(paramSynthContext);
/*      */ 
/* 1454 */     if (paramInsets == null) {
/* 1455 */       paramInsets = new Insets(0, 0, 0, 0);
/*      */     }
/* 1457 */     paramInsets.top = ((Insets)this.frameGeometry.get("title_border")).top;
/* 1458 */     paramInsets.bottom = getInt("bottom_height");
/* 1459 */     paramInsets.left = getInt("left_width");
/* 1460 */     paramInsets.right = getInt("right_width");
/* 1461 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private void updateFrameGeometry(SynthContext paramSynthContext)
/*      */   {
/* 1466 */     this.context = paramSynthContext;
/* 1467 */     JComponent localJComponent1 = paramSynthContext.getComponent();
/* 1468 */     JComponent localJComponent2 = findChild(localJComponent1, "InternalFrame.northPane");
/*      */ 
/* 1470 */     JInternalFrame localJInternalFrame = null;
/* 1471 */     if ((localJComponent1 instanceof JInternalFrame)) {
/* 1472 */       localJInternalFrame = (JInternalFrame)localJComponent1;
/* 1473 */     } else if ((localJComponent1 instanceof JInternalFrame.JDesktopIcon)) {
/* 1474 */       localJInternalFrame = ((JInternalFrame.JDesktopIcon)localJComponent1).getInternalFrame();
/*      */     } else {
/* 1476 */       if (!$assertionsDisabled) throw new AssertionError("component is not JInternalFrame or JInternalFrame.JDesktopIcon");
/*      */       return;
/*      */     }
/*      */     Node localNode1;
/* 1480 */     if (this.frame_style_set == null) {
/* 1481 */       localNode1 = getNode("window", new String[] { "type", "normal" });
/*      */ 
/* 1483 */       if (localNode1 != null) {
/* 1484 */         this.frame_style_set = getNode("frame_style_set", new String[] { "name", getStringAttr(localNode1, "style_set") });
/*      */       }
/*      */ 
/* 1488 */       if (this.frame_style_set == null) {
/* 1489 */         this.frame_style_set = getNode("frame_style_set", new String[] { "name", "normal" });
/*      */       }
/*      */     }
/*      */ 
/* 1493 */     if (this.frame_style_set != null) {
/* 1494 */       localNode1 = getNode(this.frame_style_set, "frame", new String[] { "focus", localJInternalFrame.isSelected() ? "yes" : "no", "state", localJInternalFrame.isMaximum() ? "maximized" : "normal" });
/*      */ 
/* 1499 */       if (localNode1 != null) {
/* 1500 */         Node localNode2 = getNode("frame_style", new String[] { "name", getStringAttr(localNode1, "style") });
/*      */ 
/* 1503 */         if (localNode2 != null) {
/* 1504 */           Map localMap = (Map)this.frameGeometries.get(getStringAttr(localNode2, "geometry"));
/*      */ 
/* 1506 */           setFrameGeometry(localJComponent2, localMap);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static void logError(String paramString, Exception paramException)
/*      */   {
/* 1514 */     logError(paramString, paramException.toString());
/*      */   }
/*      */ 
/*      */   protected static void logError(String paramString1, String paramString2) {
/* 1518 */     if (!errorLogged) {
/* 1519 */       System.err.println("Exception in Metacity for theme \"" + paramString1 + "\": " + paramString2);
/* 1520 */       errorLogged = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static Document getXMLDoc(URL paramURL)
/*      */     throws IOException, ParserConfigurationException, SAXException
/*      */   {
/* 1532 */     if (documentBuilder == null) {
/* 1533 */       documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
/*      */     }
/*      */ 
/* 1536 */     InputStream localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public InputStream run() {
/*      */         try {
/* 1540 */           return new BufferedInputStream(this.val$xmlFile.openStream()); } catch (IOException localIOException) {
/*      */         }
/* 1542 */         return null;
/*      */       }
/*      */     });
/* 1547 */     Document localDocument = null;
/* 1548 */     if (localInputStream != null) {
/* 1549 */       localDocument = documentBuilder.parse(localInputStream);
/*      */     }
/* 1551 */     return localDocument;
/*      */   }
/*      */ 
/*      */   protected Node[] getNodesByName(Node paramNode, String paramString)
/*      */   {
/* 1556 */     NodeList localNodeList = paramNode.getChildNodes();
/* 1557 */     int i = localNodeList.getLength();
/* 1558 */     ArrayList localArrayList = new ArrayList();
/* 1559 */     for (int j = 0; j < i; j++) {
/* 1560 */       Node localNode = localNodeList.item(j);
/* 1561 */       if (paramString.equals(localNode.getNodeName())) {
/* 1562 */         localArrayList.add(localNode);
/*      */       }
/*      */     }
/* 1565 */     return (Node[])localArrayList.toArray(new Node[localArrayList.size()]);
/*      */   }
/*      */ 
/*      */   protected Node getNode(String paramString, String[] paramArrayOfString)
/*      */   {
/* 1571 */     NodeList localNodeList = xmlDoc.getElementsByTagName(paramString);
/* 1572 */     return localNodeList != null ? getNode(localNodeList, paramString, paramArrayOfString) : null;
/*      */   }
/*      */ 
/*      */   protected Node getNode(Node paramNode, String paramString, String[] paramArrayOfString) {
/* 1576 */     Node localNode1 = null;
/* 1577 */     NodeList localNodeList = paramNode.getChildNodes();
/* 1578 */     if (localNodeList != null) {
/* 1579 */       localNode1 = getNode(localNodeList, paramString, paramArrayOfString);
/*      */     }
/* 1581 */     if (localNode1 == null) {
/* 1582 */       String str = getStringAttr(paramNode, "parent");
/* 1583 */       if (str != null) {
/* 1584 */         Node localNode2 = getNode(paramNode.getParentNode(), paramNode.getNodeName(), new String[] { "name", str });
/*      */ 
/* 1587 */         if (localNode2 != null) {
/* 1588 */           localNode1 = getNode(localNode2, paramString, paramArrayOfString);
/*      */         }
/*      */       }
/*      */     }
/* 1592 */     return localNode1;
/*      */   }
/*      */ 
/*      */   protected Node getNode(NodeList paramNodeList, String paramString, String[] paramArrayOfString) {
/* 1596 */     int i = paramNodeList.getLength();
/* 1597 */     for (int j = 0; j < i; j++) {
/* 1598 */       Node localNode1 = paramNodeList.item(j);
/* 1599 */       if (paramString.equals(localNode1.getNodeName())) {
/* 1600 */         if (paramArrayOfString != null) {
/* 1601 */           NamedNodeMap localNamedNodeMap = localNode1.getAttributes();
/* 1602 */           if (localNamedNodeMap != null) {
/* 1603 */             int k = 1;
/* 1604 */             int m = paramArrayOfString.length / 2;
/* 1605 */             for (int n = 0; n < m; n++) {
/* 1606 */               String str1 = paramArrayOfString[(n * 2)];
/* 1607 */               String str2 = paramArrayOfString[(n * 2 + 1)];
/* 1608 */               Node localNode2 = localNamedNodeMap.getNamedItem(str1);
/* 1609 */               if ((localNode2 == null) || ((str2 != null) && (!str2.equals(localNode2.getNodeValue()))))
/*      */               {
/* 1611 */                 k = 0;
/* 1612 */                 break;
/*      */               }
/*      */             }
/* 1615 */             if (k != 0)
/* 1616 */               return localNode1;
/*      */           }
/*      */         }
/*      */         else {
/* 1620 */           return localNode1;
/*      */         }
/*      */       }
/*      */     }
/* 1624 */     return null;
/*      */   }
/*      */ 
/*      */   protected String getStringAttr(Node paramNode, String paramString) {
/* 1628 */     String str1 = null;
/* 1629 */     NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
/* 1630 */     if (localNamedNodeMap != null) {
/* 1631 */       str1 = getStringAttr(localNamedNodeMap, paramString);
/* 1632 */       if (str1 == null) {
/* 1633 */         String str2 = getStringAttr(localNamedNodeMap, "parent");
/* 1634 */         if (str2 != null) {
/* 1635 */           Node localNode = getNode(paramNode.getParentNode(), paramNode.getNodeName(), new String[] { "name", str2 });
/*      */ 
/* 1638 */           if (localNode != null) {
/* 1639 */             str1 = getStringAttr(localNode, paramString);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1644 */     return str1;
/*      */   }
/*      */ 
/*      */   protected String getStringAttr(NamedNodeMap paramNamedNodeMap, String paramString) {
/* 1648 */     Node localNode = paramNamedNodeMap.getNamedItem(paramString);
/* 1649 */     return localNode != null ? localNode.getNodeValue() : null;
/*      */   }
/*      */ 
/*      */   protected boolean getBooleanAttr(Node paramNode, String paramString, boolean paramBoolean) {
/* 1653 */     String str = getStringAttr(paramNode, paramString);
/* 1654 */     if (str != null) {
/* 1655 */       return Boolean.valueOf(str).booleanValue();
/*      */     }
/* 1657 */     return paramBoolean;
/*      */   }
/*      */ 
/*      */   protected int getIntAttr(Node paramNode, String paramString, int paramInt) {
/* 1661 */     String str = getStringAttr(paramNode, paramString);
/* 1662 */     int i = paramInt;
/* 1663 */     if (str != null) {
/*      */       try {
/* 1665 */         i = Integer.parseInt(str);
/*      */       } catch (NumberFormatException localNumberFormatException) {
/* 1667 */         logError(this.themeName, localNumberFormatException);
/*      */       }
/*      */     }
/* 1670 */     return i;
/*      */   }
/*      */ 
/*      */   protected float getFloatAttr(Node paramNode, String paramString, float paramFloat) {
/* 1674 */     String str = getStringAttr(paramNode, paramString);
/* 1675 */     float f = paramFloat;
/* 1676 */     if (str != null) {
/*      */       try {
/* 1678 */         f = Float.parseFloat(str);
/*      */       } catch (NumberFormatException localNumberFormatException) {
/* 1680 */         logError(this.themeName, localNumberFormatException);
/*      */       }
/*      */     }
/* 1683 */     return f;
/*      */   }
/*      */ 
/*      */   protected Color parseColor(String paramString)
/*      */   {
/* 1689 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "/");
/* 1690 */     int i = localStringTokenizer.countTokens();
/* 1691 */     if (i > 1) {
/* 1692 */       String str = localStringTokenizer.nextToken();
/*      */       Color localColor1;
/* 1693 */       if ("shade".equals(str)) {
/* 1694 */         assert (i == 3);
/* 1695 */         localColor1 = parseColor2(localStringTokenizer.nextToken());
/* 1696 */         float f1 = Float.parseFloat(localStringTokenizer.nextToken());
/* 1697 */         return GTKColorType.adjustColor(localColor1, 1.0F, f1, f1);
/* 1698 */       }if ("blend".equals(str)) {
/* 1699 */         assert (i == 4);
/* 1700 */         localColor1 = parseColor2(localStringTokenizer.nextToken());
/* 1701 */         Color localColor2 = parseColor2(localStringTokenizer.nextToken());
/* 1702 */         float f2 = Float.parseFloat(localStringTokenizer.nextToken());
/* 1703 */         if (f2 > 1.0F) {
/* 1704 */           f2 = 1.0F / f2;
/*      */         }
/*      */ 
/* 1707 */         return new Color((int)(localColor1.getRed() + (localColor2.getRed() - localColor1.getRed()) * f2), (int)(localColor1.getRed() + (localColor2.getRed() - localColor1.getRed()) * f2), (int)(localColor1.getRed() + (localColor2.getRed() - localColor1.getRed()) * f2));
/*      */       }
/*      */ 
/* 1711 */       System.err.println("Unknown Metacity color function=" + paramString);
/* 1712 */       return null;
/*      */     }
/*      */ 
/* 1715 */     return parseColor2(paramString);
/*      */   }
/*      */ 
/*      */   protected Color parseColor2(String paramString)
/*      */   {
/* 1720 */     Color localColor = null;
/* 1721 */     if (paramString.startsWith("gtk:")) {
/* 1722 */       int i = paramString.indexOf('[');
/* 1723 */       if (i > 3) {
/* 1724 */         String str1 = paramString.substring(4, i).toLowerCase();
/* 1725 */         int j = paramString.indexOf(']');
/* 1726 */         if (j > i + 1) {
/* 1727 */           String str2 = paramString.substring(i + 1, j).toUpperCase();
/* 1728 */           int k = -1;
/* 1729 */           if ("ACTIVE".equals(str2))
/* 1730 */             k = 4;
/* 1731 */           else if ("INSENSITIVE".equals(str2))
/* 1732 */             k = 8;
/* 1733 */           else if ("NORMAL".equals(str2))
/* 1734 */             k = 1;
/* 1735 */           else if ("PRELIGHT".equals(str2))
/* 1736 */             k = 2;
/* 1737 */           else if ("SELECTED".equals(str2)) {
/* 1738 */             k = 512;
/*      */           }
/* 1740 */           ColorType localColorType = null;
/* 1741 */           if ("fg".equals(str1))
/* 1742 */             localColorType = GTKColorType.FOREGROUND;
/* 1743 */           else if ("bg".equals(str1))
/* 1744 */             localColorType = GTKColorType.BACKGROUND;
/* 1745 */           else if ("base".equals(str1))
/* 1746 */             localColorType = GTKColorType.TEXT_BACKGROUND;
/* 1747 */           else if ("text".equals(str1))
/* 1748 */             localColorType = GTKColorType.TEXT_FOREGROUND;
/* 1749 */           else if ("dark".equals(str1))
/* 1750 */             localColorType = GTKColorType.DARK;
/* 1751 */           else if ("light".equals(str1)) {
/* 1752 */             localColorType = GTKColorType.LIGHT;
/*      */           }
/* 1754 */           if ((k >= 0) && (localColorType != null)) {
/* 1755 */             localColor = ((GTKStyle)this.context.getStyle()).getGTKColor(this.context, k, localColorType);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1760 */     if (localColor == null) {
/* 1761 */       localColor = parseColorString(paramString);
/*      */     }
/* 1763 */     return localColor;
/*      */   }
/*      */ 
/*      */   private static Color parseColorString(String paramString) {
/* 1767 */     if (paramString.charAt(0) == '#') {
/* 1768 */       paramString = paramString.substring(1);
/*      */ 
/* 1770 */       int i = paramString.length();
/*      */ 
/* 1772 */       if ((i < 3) || (i > 12) || (i % 3 != 0)) {
/* 1773 */         return null;
/* 1776 */       }
/*      */ i /= 3;
/*      */       int j;
/*      */       int k;
/*      */       int m;
/*      */       try {
/* 1783 */         j = Integer.parseInt(paramString.substring(0, i), 16);
/* 1784 */         k = Integer.parseInt(paramString.substring(i, i * 2), 16);
/* 1785 */         m = Integer.parseInt(paramString.substring(i * 2, i * 3), 16);
/*      */       } catch (NumberFormatException localNumberFormatException) {
/* 1787 */         return null;
/*      */       }
/*      */ 
/* 1790 */       if (i == 4)
/* 1791 */         return new ColorUIResource(j / 65535.0F, k / 65535.0F, m / 65535.0F);
/* 1792 */       if (i == 1)
/* 1793 */         return new ColorUIResource(j / 15.0F, k / 15.0F, m / 15.0F);
/* 1794 */       if (i == 2) {
/* 1795 */         return new ColorUIResource(j, k, m);
/*      */       }
/* 1797 */       return new ColorUIResource(j / 4095.0F, k / 4095.0F, m / 4095.0F);
/*      */     }
/*      */ 
/* 1800 */     return XColors.lookupColor(paramString);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   60 */     themeNames = new String[] { getUserTheme(), "blueprint", "Bluecurve", "Crux", "SwingFallbackTheme" };
/*      */ 
/*   69 */     for (String str : themeNames) {
/*   70 */       if (str != null) {
/*      */         try {
/*   72 */           INSTANCE = new Metacity(str);
/*      */         } catch (FileNotFoundException localFileNotFoundException) {
/*      */         } catch (IOException localIOException) {
/*   75 */           logError(str, localIOException);
/*      */         } catch (ParserConfigurationException localParserConfigurationException) {
/*   77 */           logError(str, localParserConfigurationException);
/*      */         } catch (SAXException localSAXException) {
/*   79 */           logError(str, localSAXException);
/*      */         }
/*      */       }
/*   82 */       if (INSTANCE != null) {
/*      */         break;
/*      */       }
/*      */     }
/*   86 */     if (INSTANCE == null)
/*   87 */       throw new Error("Could not find any installed metacity theme, and fallback failed");
/*      */   }
/*      */ 
/*      */   class ArithmeticExpressionEvaluator
/*      */   {
/*      */     private Metacity.PeekableStringTokenizer tokenizer;
/*      */ 
/*      */     ArithmeticExpressionEvaluator()
/*      */     {
/*      */     }
/*      */ 
/*      */     int evaluate(String paramString)
/*      */     {
/* 1808 */       this.tokenizer = new Metacity.PeekableStringTokenizer(paramString, " \t+-*/%()", true);
/* 1809 */       return Math.round(expression());
/*      */     }
/*      */ 
/*      */     int evaluate(String paramString, int paramInt) {
/* 1813 */       return paramString != null ? evaluate(paramString) : paramInt;
/*      */     }
/*      */ 
/*      */     public float expression() {
/* 1817 */       float f1 = getTermValue();
/* 1818 */       int i = 0;
/* 1819 */       while ((i == 0) && (this.tokenizer.hasMoreTokens())) {
/* 1820 */         String str = this.tokenizer.peek();
/* 1821 */         if (("+".equals(str)) || ("-".equals(str)) || ("`max`".equals(str)) || ("`min`".equals(str)))
/*      */         {
/* 1825 */           this.tokenizer.nextToken();
/* 1826 */           float f2 = getTermValue();
/* 1827 */           if ("+".equals(str))
/* 1828 */             f1 += f2;
/* 1829 */           else if ("-".equals(str))
/* 1830 */             f1 -= f2;
/* 1831 */           else if ("`max`".equals(str))
/* 1832 */             f1 = Math.max(f1, f2);
/* 1833 */           else if ("`min`".equals(str))
/* 1834 */             f1 = Math.min(f1, f2);
/*      */         }
/*      */         else {
/* 1837 */           i = 1;
/*      */         }
/*      */       }
/* 1840 */       return f1;
/*      */     }
/*      */ 
/*      */     public float getTermValue() {
/* 1844 */       float f1 = getFactorValue();
/* 1845 */       int i = 0;
/* 1846 */       while ((i == 0) && (this.tokenizer.hasMoreTokens())) {
/* 1847 */         String str = this.tokenizer.peek();
/* 1848 */         if (("*".equals(str)) || ("/".equals(str)) || ("%".equals(str))) {
/* 1849 */           this.tokenizer.nextToken();
/* 1850 */           float f2 = getFactorValue();
/* 1851 */           if ("*".equals(str))
/* 1852 */             f1 *= f2;
/* 1853 */           else if ("/".equals(str))
/* 1854 */             f1 /= f2;
/*      */           else
/* 1856 */             f1 %= f2;
/*      */         }
/*      */         else {
/* 1859 */           i = 1;
/*      */         }
/*      */       }
/* 1862 */       return f1;
/*      */     }
/*      */ 
/*      */     public float getFactorValue()
/*      */     {
/*      */       float f;
/* 1867 */       if ("(".equals(this.tokenizer.peek())) {
/* 1868 */         this.tokenizer.nextToken();
/* 1869 */         f = expression();
/* 1870 */         this.tokenizer.nextToken();
/*      */       } else {
/* 1872 */         String str = this.tokenizer.nextToken();
/* 1873 */         if (Character.isDigit(str.charAt(0))) {
/* 1874 */           f = Float.parseFloat(str);
/*      */         } else {
/* 1876 */           Integer localInteger = (Integer)Metacity.this.variables.get(str);
/* 1877 */           if (localInteger == null) {
/* 1878 */             localInteger = (Integer)Metacity.this.getFrameGeometry().get(str);
/*      */           }
/* 1880 */           if (localInteger == null) {
/* 1881 */             Metacity.logError(Metacity.this.themeName, "Variable \"" + str + "\" not defined");
/* 1882 */             return 0.0F;
/*      */           }
/* 1884 */           f = localInteger != null ? localInteger.intValue() : 0.0F;
/*      */         }
/*      */       }
/* 1887 */       return f;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ColorizeImageFilter extends RGBImageFilter
/*      */   {
/*      */     double cr;
/*      */     double cg;
/*      */     double cb;
/*      */ 
/*      */     public ColorizeImageFilter()
/*      */     {
/*  640 */       this.canFilterIndexColorModel = true;
/*      */     }
/*      */ 
/*      */     public void setColor(Color paramColor) {
/*  644 */       this.cr = (paramColor.getRed() / 255.0D);
/*  645 */       this.cg = (paramColor.getGreen() / 255.0D);
/*  646 */       this.cb = (paramColor.getBlue() / 255.0D);
/*      */     }
/*      */ 
/*      */     public Image colorize(Image paramImage, Color paramColor) {
/*  650 */       setColor(paramColor);
/*  651 */       FilteredImageSource localFilteredImageSource = new FilteredImageSource(paramImage.getSource(), this);
/*  652 */       return new ImageIcon(Metacity.this.context.getComponent().createImage(localFilteredImageSource)).getImage();
/*      */     }
/*      */ 
/*      */     public int filterRGB(int paramInt1, int paramInt2, int paramInt3)
/*      */     {
/*  657 */       double d1 = 2 * (paramInt3 & 0xFF) / 255.0D;
/*      */       double d2;
/*      */       double d3;
/*      */       double d4;
/*  660 */       if (d1 <= 1.0D) {
/*  661 */         d2 = this.cr * d1;
/*  662 */         d3 = this.cg * d1;
/*  663 */         d4 = this.cb * d1;
/*      */       } else {
/*  665 */         d1 -= 1.0D;
/*  666 */         d2 = this.cr + (1.0D - this.cr) * d1;
/*  667 */         d3 = this.cg + (1.0D - this.cg) * d1;
/*  668 */         d4 = this.cb + (1.0D - this.cb) * d1;
/*      */       }
/*      */ 
/*  671 */       return (paramInt3 & 0xFF000000) + ((int)(d2 * 255.0D) << 16) + ((int)(d3 * 255.0D) << 8) + (int)(d4 * 255.0D);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class PeekableStringTokenizer extends StringTokenizer
/*      */   {
/* 1894 */     String token = null;
/*      */ 
/*      */     public PeekableStringTokenizer(String paramString1, String paramString2, boolean paramBoolean)
/*      */     {
/* 1898 */       super(paramString2, paramBoolean);
/* 1899 */       peek();
/*      */     }
/*      */ 
/*      */     public String peek() {
/* 1903 */       if (this.token == null) {
/* 1904 */         this.token = nextToken();
/*      */       }
/* 1906 */       return this.token;
/*      */     }
/*      */ 
/*      */     public boolean hasMoreTokens() {
/* 1910 */       return (this.token != null) || (super.hasMoreTokens());
/*      */     }
/*      */ 
/*      */     public String nextToken() {
/* 1914 */       if (this.token != null) {
/* 1915 */         str = this.token;
/* 1916 */         this.token = null;
/* 1917 */         if (hasMoreTokens()) {
/* 1918 */           peek();
/*      */         }
/* 1920 */         return str;
/*      */       }
/* 1922 */       String str = super.nextToken();
/*      */ 
/* 1924 */       while (((str.equals(" ")) || (str.equals("\t"))) && (hasMoreTokens())) {
/* 1925 */         str = super.nextToken();
/*      */       }
/* 1927 */       return str;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Privileged
/*      */     implements PrivilegedAction<Object>
/*      */   {
/*  462 */     private static int GET_THEME_DIR = 0;
/*  463 */     private static int GET_USER_THEME = 1;
/*  464 */     private static int GET_IMAGE = 2;
/*      */     private int type;
/*      */     private Object arg;
/*      */ 
/*      */     public Object doPrivileged(int paramInt, Object paramObject)
/*      */     {
/*  469 */       this.type = paramInt;
/*  470 */       this.arg = paramObject;
/*  471 */       return AccessController.doPrivileged(this);
/*      */     }
/*      */ 
/*      */     public Object run()
/*      */     {
/*      */       String str1;
/*      */       Object localObject1;
/*      */       Object localObject2;
/*      */       Object localObject4;
/*      */       Object localObject3;
/*  475 */       if (this.type == GET_THEME_DIR) {
/*  476 */         str1 = File.separator;
/*  477 */         localObject1 = new String[] { Metacity.userHome + str1 + ".themes", System.getProperty("swing.metacitythemedir"), "/usr/X11R6/share/themes", "/usr/X11R6/share/gnome/themes", "/usr/local/share/themes", "/usr/local/share/gnome/themes", "/usr/share/themes", "/usr/gnome/share/themes", "/opt/gnome2/share/themes" };
/*      */ 
/*  489 */         localObject2 = null;
/*  490 */         for (int i = 0; i < localObject1.length; i++)
/*      */         {
/*  492 */           if (localObject1[i] != null)
/*      */           {
/*  495 */             localObject4 = new File(localObject1[i] + str1 + this.arg + str1 + "metacity-1");
/*      */ 
/*  497 */             if (new File((File)localObject4, "metacity-theme-1.xml").canRead()) {
/*      */               try {
/*  499 */                 localObject2 = ((File)localObject4).toURI().toURL();
/*      */               } catch (MalformedURLException localMalformedURLException2) {
/*  501 */                 localObject2 = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  506 */         if (localObject2 == null) {
/*  507 */           localObject3 = "resources/metacity/" + this.arg + "/metacity-1/metacity-theme-1.xml";
/*      */ 
/*  509 */           localObject4 = getClass().getResource((String)localObject3);
/*  510 */           if (localObject4 != null) {
/*  511 */             String str2 = ((URL)localObject4).toString();
/*      */             try {
/*  513 */               localObject2 = new URL(str2.substring(0, str2.lastIndexOf('/')) + "/");
/*      */             } catch (MalformedURLException localMalformedURLException3) {
/*  515 */               localObject2 = null;
/*      */             }
/*      */           }
/*      */         }
/*  519 */         return localObject2;
/*  520 */       }if (this.type == GET_USER_THEME)
/*      */       {
/*      */         try {
/*  523 */           Metacity.access$002(System.getProperty("user.home"));
/*      */ 
/*  525 */           str1 = System.getProperty("swing.metacitythemename");
/*  526 */           if (str1 != null) {
/*  527 */             return str1;
/*      */           }
/*      */ 
/*  531 */           localObject1 = new URL(new File(Metacity.userHome).toURI().toURL(), ".gconf/apps/metacity/general/%25gconf.xml");
/*      */ 
/*  534 */           localObject2 = new InputStreamReader(((URL)localObject1).openStream(), "ISO-8859-1");
/*  535 */           localObject3 = new char[1024];
/*  536 */           localObject4 = new StringBuffer();
/*      */           int j;
/*  538 */           while ((j = ((Reader)localObject2).read((char[])localObject3)) >= 0) {
/*  539 */             ((StringBuffer)localObject4).append((char[])localObject3, 0, j);
/*      */           }
/*  541 */           ((Reader)localObject2).close();
/*  542 */           String str3 = ((StringBuffer)localObject4).toString();
/*  543 */           if (str3 != null) {
/*  544 */             String str4 = str3.toLowerCase();
/*  545 */             int k = str4.indexOf("<entry name=\"theme\"");
/*  546 */             if (k >= 0) {
/*  547 */               k = str4.indexOf("<stringvalue>", k);
/*  548 */               if (k > 0) {
/*  549 */                 k += "<stringvalue>".length();
/*  550 */                 int m = str3.indexOf("<", k);
/*  551 */                 return str3.substring(k, m);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (MalformedURLException localMalformedURLException1) {
/*      */         }
/*      */         catch (IOException localIOException) {
/*      */         }
/*  560 */         return null;
/*  561 */       }if (this.type == GET_IMAGE) {
/*  562 */         return new ImageIcon((URL)this.arg).getImage();
/*      */       }
/*  564 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class RoundRectClipShape extends RectangularShape
/*      */   {
/*      */     static final int TOP_LEFT = 1;
/*      */     static final int TOP_RIGHT = 2;
/*      */     static final int BOTTOM_LEFT = 4;
/*      */     static final int BOTTOM_RIGHT = 8;
/*      */     int x;
/*      */     int y;
/*      */     int width;
/*      */     int height;
/*      */     int arcwidth;
/*      */     int archeight;
/*      */     int corners;
/*      */ 
/*      */     public RoundRectClipShape()
/*      */     {
/*      */     }
/*      */ 
/*      */     public RoundRectClipShape(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*      */     {
/* 1952 */       setRoundedRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*      */     }
/*      */ 
/*      */     public void setRoundedRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*      */     {
/* 1957 */       this.corners = paramInt7;
/* 1958 */       this.x = paramInt1;
/* 1959 */       this.y = paramInt2;
/* 1960 */       this.width = paramInt3;
/* 1961 */       this.height = paramInt4;
/* 1962 */       this.arcwidth = paramInt5;
/* 1963 */       this.archeight = paramInt6;
/*      */     }
/*      */ 
/*      */     public double getX() {
/* 1967 */       return this.x;
/*      */     }
/*      */ 
/*      */     public double getY() {
/* 1971 */       return this.y;
/*      */     }
/*      */ 
/*      */     public double getWidth() {
/* 1975 */       return this.width;
/*      */     }
/*      */ 
/*      */     public double getHeight() {
/* 1979 */       return this.height;
/*      */     }
/*      */ 
/*      */     public double getArcWidth() {
/* 1983 */       return this.arcwidth;
/*      */     }
/*      */ 
/*      */     public double getArcHeight() {
/* 1987 */       return this.archeight;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 1991 */       return false;
/*      */     }
/*      */ 
/*      */     public Rectangle2D getBounds2D() {
/* 1995 */       return null;
/*      */     }
/*      */ 
/*      */     public int getCornerFlags() {
/* 1999 */       return this.corners;
/*      */     }
/*      */ 
/*      */     public void setFrame(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean contains(double paramDouble1, double paramDouble2) {
/* 2007 */       return false;
/*      */     }
/*      */ 
/*      */     private int classify(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
/* 2011 */       return 0;
/*      */     }
/*      */ 
/*      */     public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
/* 2015 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
/* 2019 */       return false;
/*      */     }
/*      */ 
/*      */     public PathIterator getPathIterator(AffineTransform paramAffineTransform) {
/* 2023 */       return new RoundishRectIterator(this, paramAffineTransform);
/*      */     }
/*      */ 
/*      */     static class RoundishRectIterator
/*      */       implements PathIterator
/*      */     {
/*      */       double x;
/*      */       double y;
/*      */       double w;
/*      */       double h;
/*      */       double aw;
/*      */       double ah;
/*      */       AffineTransform affine;
/*      */       int index;
/*      */       double[][] ctrlpts;
/*      */       int[] types;
/*      */       private static final double angle = 0.7853981633974483D;
/* 2036 */       private static final double a = 1.0D - Math.cos(0.7853981633974483D);
/* 2037 */       private static final double b = Math.tan(0.7853981633974483D);
/* 2038 */       private static final double c = Math.sqrt(1.0D + b * b) - 1.0D + a;
/* 2039 */       private static final double cv = 1.333333333333333D * a * b / c;
/* 2040 */       private static final double acv = (1.0D - cv) / 2.0D;
/*      */ 
/* 2046 */       private static final double[][] CtrlPtTemplate = { { 0.0D, 0.0D, 1.0D, 0.0D }, { 0.0D, 0.0D, 1.0D, -0.5D }, { 0.0D, 0.0D, 1.0D, -acv, 0.0D, acv, 1.0D, 0.0D, 0.0D, 0.5D, 1.0D, 0.0D }, { 1.0D, 0.0D, 1.0D, 0.0D }, { 1.0D, -0.5D, 1.0D, 0.0D }, { 1.0D, -acv, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D, -acv, 1.0D, 0.0D, 1.0D, -0.5D }, { 1.0D, 0.0D, 0.0D, 0.0D }, { 1.0D, 0.0D, 0.0D, 0.5D }, { 1.0D, 0.0D, 0.0D, acv, 1.0D, -acv, 0.0D, 0.0D, 1.0D, -0.5D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.5D, 0.0D, 0.0D }, { 0.0D, acv, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, acv, 0.0D, 0.0D, 0.0D, 0.5D }, new double[0] };
/*      */ 
/* 2069 */       private static final int[] CornerFlags = { 4, 8, 2, 1 };
/*      */ 
/*      */       RoundishRectIterator(Metacity.RoundRectClipShape paramRoundRectClipShape, AffineTransform paramAffineTransform)
/*      */       {
/* 2077 */         this.x = paramRoundRectClipShape.getX();
/* 2078 */         this.y = paramRoundRectClipShape.getY();
/* 2079 */         this.w = paramRoundRectClipShape.getWidth();
/* 2080 */         this.h = paramRoundRectClipShape.getHeight();
/* 2081 */         this.aw = Math.min(this.w, Math.abs(paramRoundRectClipShape.getArcWidth()));
/* 2082 */         this.ah = Math.min(this.h, Math.abs(paramRoundRectClipShape.getArcHeight()));
/* 2083 */         this.affine = paramAffineTransform;
/* 2084 */         if ((this.w < 0.0D) || (this.h < 0.0D))
/*      */         {
/* 2086 */           this.ctrlpts = new double[0][];
/* 2087 */           this.types = new int[0];
/*      */         } else {
/* 2089 */           int i = paramRoundRectClipShape.getCornerFlags();
/* 2090 */           int j = 5;
/* 2091 */           for (int k = 1; k < 16; k <<= 1)
/*      */           {
/* 2093 */             if ((i & k) != 0) j++;
/*      */           }
/* 2095 */           this.ctrlpts = new double[j][];
/* 2096 */           this.types = new int[j];
/* 2097 */           k = 0;
/* 2098 */           for (int m = 0; m < 4; m++) {
/* 2099 */             this.types[k] = 1;
/* 2100 */             if ((i & CornerFlags[m]) == 0) {
/* 2101 */               this.ctrlpts[(k++)] = CtrlPtTemplate[(m * 3 + 0)];
/*      */             } else {
/* 2103 */               this.ctrlpts[(k++)] = CtrlPtTemplate[(m * 3 + 1)];
/* 2104 */               this.types[k] = 3;
/* 2105 */               this.ctrlpts[(k++)] = CtrlPtTemplate[(m * 3 + 2)];
/*      */             }
/*      */           }
/* 2108 */           this.types[k] = 4;
/* 2109 */           this.ctrlpts[(k++)] = CtrlPtTemplate[12];
/* 2110 */           this.types[0] = 0;
/*      */         }
/*      */       }
/*      */ 
/*      */       public int getWindingRule() {
/* 2115 */         return 1;
/*      */       }
/*      */ 
/*      */       public boolean isDone() {
/* 2119 */         return this.index >= this.ctrlpts.length;
/*      */       }
/*      */ 
/*      */       public void next() {
/* 2123 */         this.index += 1;
/*      */       }
/*      */ 
/*      */       public int currentSegment(float[] paramArrayOfFloat) {
/* 2127 */         if (isDone()) {
/* 2128 */           throw new NoSuchElementException("roundrect iterator out of bounds");
/*      */         }
/* 2130 */         double[] arrayOfDouble = this.ctrlpts[this.index];
/* 2131 */         int i = 0;
/* 2132 */         for (int j = 0; j < arrayOfDouble.length; j += 4) {
/* 2133 */           paramArrayOfFloat[(i++)] = ((float)(this.x + arrayOfDouble[(j + 0)] * this.w + arrayOfDouble[(j + 1)] * this.aw));
/* 2134 */           paramArrayOfFloat[(i++)] = ((float)(this.y + arrayOfDouble[(j + 2)] * this.h + arrayOfDouble[(j + 3)] * this.ah));
/*      */         }
/* 2136 */         if (this.affine != null) {
/* 2137 */           this.affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, i / 2);
/*      */         }
/* 2139 */         return this.types[this.index];
/*      */       }
/*      */ 
/*      */       public int currentSegment(double[] paramArrayOfDouble) {
/* 2143 */         if (isDone()) {
/* 2144 */           throw new NoSuchElementException("roundrect iterator out of bounds");
/*      */         }
/* 2146 */         double[] arrayOfDouble = this.ctrlpts[this.index];
/* 2147 */         int i = 0;
/* 2148 */         for (int j = 0; j < arrayOfDouble.length; j += 4) {
/* 2149 */           paramArrayOfDouble[(i++)] = (this.x + arrayOfDouble[(j + 0)] * this.w + arrayOfDouble[(j + 1)] * this.aw);
/* 2150 */           paramArrayOfDouble[(i++)] = (this.y + arrayOfDouble[(j + 2)] * this.h + arrayOfDouble[(j + 3)] * this.ah);
/*      */         }
/* 2152 */         if (this.affine != null) {
/* 2153 */           this.affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, i / 2);
/*      */         }
/* 2155 */         return this.types[this.index];
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class TitlePaneLayout
/*      */     implements LayoutManager
/*      */   {
/*      */     protected TitlePaneLayout()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void addLayoutComponent(String paramString, Component paramComponent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void removeLayoutComponent(Component paramComponent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public Dimension preferredLayoutSize(Container paramContainer)
/*      */     {
/*  694 */       return minimumLayoutSize(paramContainer);
/*      */     }
/*      */ 
/*      */     public Dimension minimumLayoutSize(Container paramContainer) {
/*  698 */       JComponent localJComponent = (JComponent)paramContainer;
/*  699 */       Container localContainer = localJComponent.getParent();
/*      */       JInternalFrame localJInternalFrame;
/*  701 */       if ((localContainer instanceof JInternalFrame))
/*  702 */         localJInternalFrame = (JInternalFrame)localContainer;
/*  703 */       else if ((localContainer instanceof JInternalFrame.JDesktopIcon))
/*  704 */         localJInternalFrame = ((JInternalFrame.JDesktopIcon)localContainer).getInternalFrame();
/*      */       else {
/*  706 */         return null;
/*      */       }
/*      */ 
/*  709 */       Dimension localDimension = Metacity.this.calculateButtonSize(localJComponent);
/*  710 */       Insets localInsets1 = (Insets)Metacity.this.getFrameGeometry().get("title_border");
/*  711 */       Insets localInsets2 = (Insets)Metacity.this.getFrameGeometry().get("button_border");
/*      */ 
/*  714 */       int i = Metacity.this.getInt("left_titlebar_edge") + localDimension.width + Metacity.this.getInt("right_titlebar_edge");
/*  715 */       if (localInsets1 != null) {
/*  716 */         i += localInsets1.left + localInsets1.right;
/*      */       }
/*  718 */       if (localJInternalFrame.isClosable()) {
/*  719 */         i += localDimension.width;
/*      */       }
/*  721 */       if (localJInternalFrame.isMaximizable()) {
/*  722 */         i += localDimension.width;
/*      */       }
/*  724 */       if (localJInternalFrame.isIconifiable()) {
/*  725 */         i += localDimension.width;
/*      */       }
/*  727 */       FontMetrics localFontMetrics = localJInternalFrame.getFontMetrics(localJComponent.getFont());
/*  728 */       String str = localJInternalFrame.getTitle();
/*  729 */       int j = str != null ? SwingUtilities2.stringWidth(localJInternalFrame, localFontMetrics, str) : 0;
/*      */ 
/*  731 */       int k = str != null ? str.length() : 0;
/*      */ 
/*  734 */       if (k > 3) {
/*  735 */         m = SwingUtilities2.stringWidth(localJInternalFrame, localFontMetrics, str.substring(0, 3) + "...");
/*      */ 
/*  737 */         i += (j < m ? j : m);
/*      */       } else {
/*  739 */         i += j;
/*      */       }
/*      */ 
/*  743 */       int m = localFontMetrics.getHeight() + Metacity.this.getInt("title_vertical_pad");
/*  744 */       if (localInsets1 != null) {
/*  745 */         m += localInsets1.top + localInsets1.bottom;
/*      */       }
/*  747 */       int n = localDimension.height;
/*  748 */       if (localInsets2 != null) {
/*  749 */         n += localInsets2.top + localInsets2.bottom;
/*      */       }
/*  751 */       int i1 = Math.max(n, m);
/*      */ 
/*  753 */       return new Dimension(i, i1);
/*      */     }
/*      */ 
/*      */     public void layoutContainer(Container paramContainer) {
/*  757 */       JComponent localJComponent1 = (JComponent)paramContainer;
/*  758 */       Container localContainer = localJComponent1.getParent();
/*      */       JInternalFrame localJInternalFrame;
/*  760 */       if ((localContainer instanceof JInternalFrame))
/*  761 */         localJInternalFrame = (JInternalFrame)localContainer;
/*  762 */       else if ((localContainer instanceof JInternalFrame.JDesktopIcon))
/*  763 */         localJInternalFrame = ((JInternalFrame.JDesktopIcon)localContainer).getInternalFrame();
/*      */       else {
/*  765 */         return;
/*      */       }
/*  767 */       Map localMap = Metacity.this.getFrameGeometry();
/*      */ 
/*  769 */       int i = localJComponent1.getWidth();
/*  770 */       int j = localJComponent1.getHeight();
/*      */ 
/*  772 */       JComponent localJComponent2 = Metacity.findChild(localJComponent1, "InternalFrameTitlePane.menuButton");
/*  773 */       JComponent localJComponent3 = Metacity.findChild(localJComponent1, "InternalFrameTitlePane.iconifyButton");
/*  774 */       JComponent localJComponent4 = Metacity.findChild(localJComponent1, "InternalFrameTitlePane.maximizeButton");
/*  775 */       JComponent localJComponent5 = Metacity.findChild(localJComponent1, "InternalFrameTitlePane.closeButton");
/*      */ 
/*  777 */       Insets localInsets = (Insets)localMap.get("button_border");
/*  778 */       Dimension localDimension = Metacity.this.calculateButtonSize(localJComponent1);
/*      */ 
/*  780 */       int k = localInsets != null ? localInsets.top : 0;
/*      */       int m;
/*  781 */       if (localContainer.getComponentOrientation().isLeftToRight()) {
/*  782 */         m = Metacity.this.getInt("left_titlebar_edge");
/*      */ 
/*  784 */         localJComponent2.setBounds(m, k, localDimension.width, localDimension.height);
/*      */ 
/*  786 */         m = i - localDimension.width - Metacity.this.getInt("right_titlebar_edge");
/*  787 */         if (localInsets != null) {
/*  788 */           m -= localInsets.right;
/*      */         }
/*      */ 
/*  791 */         if (localJInternalFrame.isClosable()) {
/*  792 */           localJComponent5.setBounds(m, k, localDimension.width, localDimension.height);
/*  793 */           m -= localDimension.width;
/*      */         }
/*      */ 
/*  796 */         if (localJInternalFrame.isMaximizable()) {
/*  797 */           localJComponent4.setBounds(m, k, localDimension.width, localDimension.height);
/*  798 */           m -= localDimension.width;
/*      */         }
/*      */ 
/*  801 */         if (localJInternalFrame.isIconifiable())
/*  802 */           localJComponent3.setBounds(m, k, localDimension.width, localDimension.height);
/*      */       }
/*      */       else {
/*  805 */         m = i - localDimension.width - Metacity.this.getInt("right_titlebar_edge");
/*      */ 
/*  807 */         localJComponent2.setBounds(m, k, localDimension.width, localDimension.height);
/*      */ 
/*  809 */         m = Metacity.this.getInt("left_titlebar_edge");
/*  810 */         if (localInsets != null) {
/*  811 */           m += localInsets.left;
/*      */         }
/*      */ 
/*  814 */         if (localJInternalFrame.isClosable()) {
/*  815 */           localJComponent5.setBounds(m, k, localDimension.width, localDimension.height);
/*  816 */           m += localDimension.width;
/*      */         }
/*      */ 
/*  819 */         if (localJInternalFrame.isMaximizable()) {
/*  820 */           localJComponent4.setBounds(m, k, localDimension.width, localDimension.height);
/*  821 */           m += localDimension.width;
/*      */         }
/*      */ 
/*  824 */         if (localJInternalFrame.isIconifiable())
/*  825 */           localJComponent3.setBounds(m, k, localDimension.width, localDimension.height);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.Metacity
 * JD-Core Version:    0.6.2
 */