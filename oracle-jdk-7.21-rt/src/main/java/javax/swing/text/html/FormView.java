/*     */ package javax.swing.text.html;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.BitSet;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.ButtonModel;
/*     */ import javax.swing.ComboBoxModel;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPasswordField;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToggleButton.ToggleButtonModel;
/*     */ import javax.swing.ListModel;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.LookAndFeel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.event.HyperlinkEvent.EventType;
/*     */ import javax.swing.text.AttributeSet;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.ComponentView;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.Element;
/*     */ import javax.swing.text.ElementIterator;
/*     */ import javax.swing.text.PlainDocument;
/*     */ import javax.swing.text.StyleConstants;
/*     */ 
/*     */ public class FormView extends ComponentView
/*     */   implements ActionListener
/*     */ {
/*     */ 
/*     */   @Deprecated
/* 117 */   public static final String SUBMIT = new String("Submit Query");
/*     */ 
/*     */   @Deprecated
/* 126 */   public static final String RESET = new String("Reset");
/*     */   static final String PostDataProperty = "javax.swing.JEditorPane.postdata";
/*     */   private short maxIsPreferred;
/*     */ 
/*     */   public FormView(Element paramElement)
/*     */   {
/* 148 */     super(paramElement);
/*     */   }
/*     */ 
/*     */   protected Component createComponent()
/*     */   {
/* 157 */     AttributeSet localAttributeSet = getElement().getAttributes();
/* 158 */     HTML.Tag localTag = (HTML.Tag)localAttributeSet.getAttribute(StyleConstants.NameAttribute);
/*     */ 
/* 160 */     Object localObject1 = null;
/* 161 */     Object localObject2 = localAttributeSet.getAttribute(StyleConstants.ModelAttribute);
/* 162 */     if (localTag == HTML.Tag.INPUT) {
/* 163 */       localObject1 = createInputComponent(localAttributeSet, localObject2);
/*     */     }
/*     */     else
/*     */     {
/*     */       Object localObject3;
/*     */       int i;
/* 164 */       if (localTag == HTML.Tag.SELECT)
/*     */       {
/* 166 */         if ((localObject2 instanceof OptionListModel))
/*     */         {
/* 168 */           localObject3 = new JList((ListModel)localObject2);
/* 169 */           i = HTML.getIntegerAttributeValue(localAttributeSet, HTML.Attribute.SIZE, 1);
/*     */ 
/* 172 */           ((JList)localObject3).setVisibleRowCount(i);
/* 173 */           ((JList)localObject3).setSelectionModel((ListSelectionModel)localObject2);
/* 174 */           localObject1 = new JScrollPane((Component)localObject3);
/*     */         } else {
/* 176 */           localObject1 = new JComboBox((ComboBoxModel)localObject2);
/* 177 */           this.maxIsPreferred = 3;
/*     */         }
/* 179 */       } else if (localTag == HTML.Tag.TEXTAREA) {
/* 180 */         localObject3 = new JTextArea((Document)localObject2);
/* 181 */         i = HTML.getIntegerAttributeValue(localAttributeSet, HTML.Attribute.ROWS, 1);
/*     */ 
/* 184 */         ((JTextArea)localObject3).setRows(i);
/* 185 */         int j = HTML.getIntegerAttributeValue(localAttributeSet, HTML.Attribute.COLS, 20);
/*     */ 
/* 188 */         this.maxIsPreferred = 3;
/* 189 */         ((JTextArea)localObject3).setColumns(j);
/* 190 */         localObject1 = new JScrollPane((Component)localObject3, 22, 32);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 195 */     if (localObject1 != null) {
/* 196 */       ((JComponent)localObject1).setAlignmentY(1.0F);
/*     */     }
/* 198 */     return localObject1;
/*     */   }
/*     */ 
/*     */   private JComponent createInputComponent(AttributeSet paramAttributeSet, Object paramObject)
/*     */   {
/* 211 */     Object localObject1 = null;
/* 212 */     String str1 = (String)paramAttributeSet.getAttribute(HTML.Attribute.TYPE);
/*     */     String str2;
/*     */     Object localObject3;
/* 214 */     if ((str1.equals("submit")) || (str1.equals("reset"))) {
/* 215 */       str2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.VALUE);
/*     */ 
/* 217 */       if (str2 == null) {
/* 218 */         if (str1.equals("submit"))
/* 219 */           str2 = UIManager.getString("FormView.submitButtonText");
/*     */         else {
/* 221 */           str2 = UIManager.getString("FormView.resetButtonText");
/*     */         }
/*     */       }
/* 224 */       localObject3 = new JButton(str2);
/* 225 */       if (paramObject != null) {
/* 226 */         ((JButton)localObject3).setModel((ButtonModel)paramObject);
/* 227 */         ((JButton)localObject3).addActionListener(this);
/*     */       }
/* 229 */       localObject1 = localObject3;
/* 230 */       this.maxIsPreferred = 3;
/*     */     }
/*     */     else
/*     */     {
/*     */       Object localObject4;
/* 231 */       if (str1.equals("image")) {
/* 232 */         str2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.SRC);
/*     */         try
/*     */         {
/* 235 */           URL localURL = ((HTMLDocument)getElement().getDocument()).getBase();
/* 236 */           localObject4 = new URL(localURL, str2);
/* 237 */           ImageIcon localImageIcon = new ImageIcon((URL)localObject4);
/* 238 */           localObject3 = new JButton(localImageIcon);
/*     */         } catch (MalformedURLException localMalformedURLException) {
/* 240 */           localObject3 = new JButton(str2);
/*     */         }
/* 242 */         if (paramObject != null) {
/* 243 */           ((JButton)localObject3).setModel((ButtonModel)paramObject);
/* 244 */           ((JButton)localObject3).addMouseListener(new MouseEventListener());
/*     */         }
/* 246 */         localObject1 = localObject3;
/* 247 */         this.maxIsPreferred = 3;
/* 248 */       } else if (str1.equals("checkbox")) {
/* 249 */         localObject1 = new JCheckBox();
/* 250 */         if (paramObject != null) {
/* 251 */           ((JCheckBox)localObject1).setModel((JToggleButton.ToggleButtonModel)paramObject);
/*     */         }
/* 253 */         this.maxIsPreferred = 3;
/* 254 */       } else if (str1.equals("radio")) {
/* 255 */         localObject1 = new JRadioButton();
/* 256 */         if (paramObject != null) {
/* 257 */           ((JRadioButton)localObject1).setModel((JToggleButton.ToggleButtonModel)paramObject);
/*     */         }
/* 259 */         this.maxIsPreferred = 3;
/* 260 */       } else if (str1.equals("text")) {
/* 261 */         int i = HTML.getIntegerAttributeValue(paramAttributeSet, HTML.Attribute.SIZE, -1);
/*     */ 
/* 265 */         if (i > 0) {
/* 266 */           localObject3 = new JTextField();
/* 267 */           ((JTextField)localObject3).setColumns(i);
/*     */         }
/*     */         else {
/* 270 */           localObject3 = new JTextField();
/* 271 */           ((JTextField)localObject3).setColumns(20);
/*     */         }
/* 273 */         localObject1 = localObject3;
/* 274 */         if (paramObject != null) {
/* 275 */           ((JTextField)localObject3).setDocument((Document)paramObject);
/*     */         }
/* 277 */         ((JTextField)localObject3).addActionListener(this);
/* 278 */         this.maxIsPreferred = 3;
/*     */       }
/*     */       else
/*     */       {
/*     */         Object localObject2;
/*     */         int j;
/* 279 */         if (str1.equals("password")) {
/* 280 */           localObject2 = new JPasswordField();
/* 281 */           localObject1 = localObject2;
/* 282 */           if (paramObject != null) {
/* 283 */             ((JPasswordField)localObject2).setDocument((Document)paramObject);
/*     */           }
/* 285 */           j = HTML.getIntegerAttributeValue(paramAttributeSet, HTML.Attribute.SIZE, -1);
/*     */ 
/* 288 */           ((JPasswordField)localObject2).setColumns(j > 0 ? j : 20);
/* 289 */           ((JPasswordField)localObject2).addActionListener(this);
/* 290 */           this.maxIsPreferred = 3;
/* 291 */         } else if (str1.equals("file")) {
/* 292 */           localObject2 = new JTextField();
/* 293 */           if (paramObject != null) {
/* 294 */             ((JTextField)localObject2).setDocument((Document)paramObject);
/*     */           }
/* 296 */           j = HTML.getIntegerAttributeValue(paramAttributeSet, HTML.Attribute.SIZE, -1);
/*     */ 
/* 298 */           ((JTextField)localObject2).setColumns(j > 0 ? j : 20);
/* 299 */           JButton localJButton = new JButton(UIManager.getString("FormView.browseFileButtonText"));
/*     */ 
/* 301 */           localObject4 = Box.createHorizontalBox();
/* 302 */           ((Box)localObject4).add((Component)localObject2);
/* 303 */           ((Box)localObject4).add(Box.createHorizontalStrut(5));
/* 304 */           ((Box)localObject4).add(localJButton);
/* 305 */           localJButton.addActionListener(new BrowseFileAction(paramAttributeSet, (Document)paramObject));
/*     */ 
/* 307 */           localObject1 = localObject4;
/* 308 */           this.maxIsPreferred = 3;
/*     */         }
/*     */       }
/*     */     }
/* 310 */     return localObject1;
/*     */   }
/*     */ 
/*     */   public float getMaximumSpan(int paramInt)
/*     */   {
/* 329 */     switch (paramInt) {
/*     */     case 0:
/* 331 */       if ((this.maxIsPreferred & 0x1) == 1) {
/* 332 */         super.getMaximumSpan(paramInt);
/* 333 */         return getPreferredSpan(paramInt);
/*     */       }
/* 335 */       return super.getMaximumSpan(paramInt);
/*     */     case 1:
/* 337 */       if ((this.maxIsPreferred & 0x2) == 2) {
/* 338 */         super.getMaximumSpan(paramInt);
/* 339 */         return getPreferredSpan(paramInt);
/*     */       }
/* 341 */       return super.getMaximumSpan(paramInt);
/*     */     }
/*     */ 
/* 345 */     return super.getMaximumSpan(paramInt);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/* 364 */     Element localElement = getElement();
/* 365 */     StringBuilder localStringBuilder = new StringBuilder();
/* 366 */     HTMLDocument localHTMLDocument = (HTMLDocument)getDocument();
/* 367 */     AttributeSet localAttributeSet = localElement.getAttributes();
/*     */ 
/* 369 */     String str = (String)localAttributeSet.getAttribute(HTML.Attribute.TYPE);
/*     */ 
/* 371 */     if (str.equals("submit")) {
/* 372 */       getFormData(localStringBuilder);
/* 373 */       submitData(localStringBuilder.toString());
/* 374 */     } else if (str.equals("reset")) {
/* 375 */       resetForm();
/* 376 */     } else if ((str.equals("text")) || (str.equals("password"))) {
/* 377 */       if (isLastTextOrPasswordField()) {
/* 378 */         getFormData(localStringBuilder);
/* 379 */         submitData(localStringBuilder.toString());
/*     */       } else {
/* 381 */         getComponent().transferFocus();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void submitData(String paramString)
/*     */   {
/* 392 */     Element localElement = getFormElement();
/* 393 */     AttributeSet localAttributeSet = localElement.getAttributes();
/* 394 */     HTMLDocument localHTMLDocument = (HTMLDocument)localElement.getDocument();
/* 395 */     URL localURL1 = localHTMLDocument.getBase();
/*     */ 
/* 397 */     String str1 = (String)localAttributeSet.getAttribute(HTML.Attribute.TARGET);
/* 398 */     if (str1 == null) {
/* 399 */       str1 = "_self";
/*     */     }
/*     */ 
/* 402 */     String str2 = (String)localAttributeSet.getAttribute(HTML.Attribute.METHOD);
/* 403 */     if (str2 == null) {
/* 404 */       str2 = "GET";
/*     */     }
/* 406 */     str2 = str2.toLowerCase();
/* 407 */     boolean bool = str2.equals("post");
/* 408 */     if (bool) {
/* 409 */       storePostData(localHTMLDocument, str1, paramString);
/* 412 */     }
/*     */ String str3 = (String)localAttributeSet.getAttribute(HTML.Attribute.ACTION);
/*     */     URL localURL2;
/*     */     try {
/* 415 */       localURL2 = str3 == null ? new URL(localURL1.getProtocol(), localURL1.getHost(), localURL1.getPort(), localURL1.getFile()) : new URL(localURL1, str3);
/*     */ 
/* 419 */       if (!bool) {
/* 420 */         String str4 = paramString.toString();
/* 421 */         localURL2 = new URL(localURL2 + "?" + str4);
/*     */       }
/*     */     } catch (MalformedURLException localMalformedURLException) {
/* 424 */       localURL2 = null;
/*     */     }
/* 426 */     final JEditorPane localJEditorPane = (JEditorPane)getContainer();
/* 427 */     HTMLEditorKit localHTMLEditorKit = (HTMLEditorKit)localJEditorPane.getEditorKit();
/*     */ 
/* 429 */     FormSubmitEvent localFormSubmitEvent = null;
/* 430 */     if ((!localHTMLEditorKit.isAutoFormSubmission()) || (localHTMLDocument.isFrameDocument())) {
/* 431 */       localObject = bool ? FormSubmitEvent.MethodType.POST : FormSubmitEvent.MethodType.GET;
/*     */ 
/* 434 */       localFormSubmitEvent = new FormSubmitEvent(this, HyperlinkEvent.EventType.ACTIVATED, localURL2, localElement, str1, (FormSubmitEvent.MethodType)localObject, paramString);
/*     */     }
/*     */ 
/* 440 */     Object localObject = localFormSubmitEvent;
/* 441 */     final URL localURL3 = localURL2;
/* 442 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 444 */         if (this.val$fse != null)
/* 445 */           localJEditorPane.fireHyperlinkUpdate(this.val$fse);
/*     */         else
/*     */           try {
/* 448 */             localJEditorPane.setPage(localURL3);
/*     */           } catch (IOException localIOException) {
/* 450 */             UIManager.getLookAndFeel().provideErrorFeedback(localJEditorPane);
/*     */           }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void storePostData(HTMLDocument paramHTMLDocument, String paramString1, String paramString2)
/*     */   {
/* 469 */     Object localObject = paramHTMLDocument;
/* 470 */     String str = "javax.swing.JEditorPane.postdata";
/*     */ 
/* 472 */     if (paramHTMLDocument.isFrameDocument())
/*     */     {
/* 474 */       FrameView.FrameEditorPane localFrameEditorPane = (FrameView.FrameEditorPane)getContainer();
/*     */ 
/* 476 */       FrameView localFrameView = localFrameEditorPane.getFrameView();
/* 477 */       JEditorPane localJEditorPane = localFrameView.getOutermostJEditorPane();
/* 478 */       if (localJEditorPane != null) {
/* 479 */         localObject = localJEditorPane.getDocument();
/* 480 */         str = str + "." + paramString1;
/*     */       }
/*     */     }
/*     */ 
/* 484 */     ((Document)localObject).putProperty(str, paramString2);
/*     */   }
/*     */ 
/*     */   protected void imageSubmit(String paramString)
/*     */   {
/* 511 */     StringBuilder localStringBuilder = new StringBuilder();
/* 512 */     Element localElement = getElement();
/* 513 */     HTMLDocument localHTMLDocument = (HTMLDocument)localElement.getDocument();
/* 514 */     getFormData(localStringBuilder);
/* 515 */     if (localStringBuilder.length() > 0) {
/* 516 */       localStringBuilder.append('&');
/*     */     }
/* 518 */     localStringBuilder.append(paramString);
/* 519 */     submitData(localStringBuilder.toString());
/*     */   }
/*     */ 
/*     */   private String getImageData(Point paramPoint)
/*     */   {
/* 538 */     String str1 = paramPoint.x + ":" + paramPoint.y;
/* 539 */     int i = str1.indexOf(':');
/* 540 */     String str2 = str1.substring(0, i);
/* 541 */     String str3 = str1.substring(++i);
/* 542 */     String str4 = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
/*     */     String str5;
/* 545 */     if ((str4 == null) || (str4.equals(""))) {
/* 546 */       str5 = "x=" + str2 + "&y=" + str3;
/*     */     } else {
/* 548 */       str4 = URLEncoder.encode(str4);
/* 549 */       str5 = str4 + ".x" + "=" + str2 + "&" + str4 + ".y" + "=" + str3;
/*     */     }
/* 551 */     return str5;
/*     */   }
/*     */ 
/*     */   private Element getFormElement()
/*     */   {
/* 569 */     Element localElement = getElement();
/* 570 */     while (localElement != null) {
/* 571 */       if (localElement.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.FORM)
/*     */       {
/* 573 */         return localElement;
/*     */       }
/* 575 */       localElement = localElement.getParentElement();
/*     */     }
/* 577 */     return null;
/*     */   }
/*     */ 
/*     */   private void getFormData(StringBuilder paramStringBuilder)
/*     */   {
/* 593 */     Element localElement1 = getFormElement();
/* 594 */     if (localElement1 != null) {
/* 595 */       ElementIterator localElementIterator = new ElementIterator(localElement1);
/*     */       Element localElement2;
/* 598 */       while ((localElement2 = localElementIterator.next()) != null)
/* 599 */         if (isControl(localElement2)) {
/* 600 */           String str = (String)localElement2.getAttributes().getAttribute(HTML.Attribute.TYPE);
/*     */ 
/* 603 */           if ((str == null) || (!str.equals("submit")) || (localElement2 == getElement()))
/*     */           {
/* 606 */             if ((str == null) || (!str.equals("image")))
/*     */             {
/* 611 */               loadElementDataIntoBuffer(localElement2, paramStringBuilder);
/*     */             }
/*     */           }
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void loadElementDataIntoBuffer(Element paramElement, StringBuilder paramStringBuilder)
/*     */   {
/* 628 */     AttributeSet localAttributeSet = paramElement.getAttributes();
/* 629 */     String str1 = (String)localAttributeSet.getAttribute(HTML.Attribute.NAME);
/* 630 */     if (str1 == null) {
/* 631 */       return;
/*     */     }
/* 633 */     String str2 = null;
/* 634 */     HTML.Tag localTag = (HTML.Tag)paramElement.getAttributes().getAttribute(StyleConstants.NameAttribute);
/*     */ 
/* 637 */     if (localTag == HTML.Tag.INPUT)
/* 638 */       str2 = getInputElementData(localAttributeSet);
/* 639 */     else if (localTag == HTML.Tag.TEXTAREA)
/* 640 */       str2 = getTextAreaData(localAttributeSet);
/* 641 */     else if (localTag == HTML.Tag.SELECT) {
/* 642 */       loadSelectData(localAttributeSet, paramStringBuilder);
/*     */     }
/*     */ 
/* 645 */     if ((str1 != null) && (str2 != null))
/* 646 */       appendBuffer(paramStringBuilder, str1, str2);
/*     */   }
/*     */ 
/*     */   private String getInputElementData(AttributeSet paramAttributeSet)
/*     */   {
/* 660 */     Object localObject1 = paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
/* 661 */     String str1 = (String)paramAttributeSet.getAttribute(HTML.Attribute.TYPE);
/* 662 */     Object localObject2 = null;
/*     */     Object localObject3;
/* 664 */     if ((str1.equals("text")) || (str1.equals("password"))) {
/* 665 */       localObject3 = (Document)localObject1;
/*     */       try {
/* 667 */         localObject2 = ((Document)localObject3).getText(0, ((Document)localObject3).getLength());
/*     */       } catch (BadLocationException localBadLocationException1) {
/* 669 */         localObject2 = null;
/*     */       }
/* 671 */     } else if ((str1.equals("submit")) || (str1.equals("hidden"))) {
/* 672 */       localObject2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.VALUE);
/* 673 */       if (localObject2 == null)
/* 674 */         localObject2 = "";
/*     */     }
/* 676 */     else if ((str1.equals("radio")) || (str1.equals("checkbox"))) {
/* 677 */       localObject3 = (ButtonModel)localObject1;
/* 678 */       if (((ButtonModel)localObject3).isSelected()) {
/* 679 */         localObject2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.VALUE);
/* 680 */         if (localObject2 == null)
/* 681 */           localObject2 = "on";
/*     */       }
/*     */     }
/* 684 */     else if (str1.equals("file")) {
/* 685 */       localObject3 = (Document)localObject1;
/*     */       String str2;
/*     */       try {
/* 689 */         str2 = ((Document)localObject3).getText(0, ((Document)localObject3).getLength());
/*     */       } catch (BadLocationException localBadLocationException2) {
/* 691 */         str2 = null;
/*     */       }
/* 693 */       if ((str2 != null) && (str2.length() > 0)) {
/* 694 */         localObject2 = str2;
/*     */       }
/*     */     }
/* 697 */     return localObject2;
/*     */   }
/*     */ 
/*     */   private String getTextAreaData(AttributeSet paramAttributeSet)
/*     */   {
/* 706 */     Document localDocument = (Document)paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
/*     */     try {
/* 708 */       return localDocument.getText(0, localDocument.getLength()); } catch (BadLocationException localBadLocationException) {
/*     */     }
/* 710 */     return null;
/*     */   }
/*     */ 
/*     */   private void loadSelectData(AttributeSet paramAttributeSet, StringBuilder paramStringBuilder)
/*     */   {
/* 722 */     String str = (String)paramAttributeSet.getAttribute(HTML.Attribute.NAME);
/* 723 */     if (str == null) {
/* 724 */       return;
/*     */     }
/* 726 */     Object localObject1 = paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
/*     */     Object localObject2;
/* 727 */     if ((localObject1 instanceof OptionListModel)) {
/* 728 */       localObject2 = (OptionListModel)localObject1;
/*     */ 
/* 730 */       for (int i = 0; i < ((OptionListModel)localObject2).getSize(); i++)
/* 731 */         if (((OptionListModel)localObject2).isSelectedIndex(i)) {
/* 732 */           Option localOption2 = (Option)((OptionListModel)localObject2).getElementAt(i);
/* 733 */           appendBuffer(paramStringBuilder, str, localOption2.getValue());
/*     */         }
/*     */     }
/* 736 */     else if ((localObject1 instanceof ComboBoxModel)) {
/* 737 */       localObject2 = (ComboBoxModel)localObject1;
/* 738 */       Option localOption1 = (Option)((ComboBoxModel)localObject2).getSelectedItem();
/* 739 */       if (localOption1 != null)
/* 740 */         appendBuffer(paramStringBuilder, str, localOption1.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void appendBuffer(StringBuilder paramStringBuilder, String paramString1, String paramString2)
/*     */   {
/* 752 */     if (paramStringBuilder.length() > 0) {
/* 753 */       paramStringBuilder.append('&');
/*     */     }
/* 755 */     String str1 = URLEncoder.encode(paramString1);
/* 756 */     paramStringBuilder.append(str1);
/* 757 */     paramStringBuilder.append('=');
/* 758 */     String str2 = URLEncoder.encode(paramString2);
/* 759 */     paramStringBuilder.append(str2);
/*     */   }
/*     */ 
/*     */   private boolean isControl(Element paramElement)
/*     */   {
/* 766 */     return paramElement.isLeaf();
/*     */   }
/*     */ 
/*     */   boolean isLastTextOrPasswordField()
/*     */   {
/* 776 */     Element localElement1 = getFormElement();
/* 777 */     Element localElement2 = getElement();
/*     */ 
/* 779 */     if (localElement1 != null) {
/* 780 */       ElementIterator localElementIterator = new ElementIterator(localElement1);
/*     */ 
/* 782 */       int i = 0;
/*     */       Element localElement3;
/* 784 */       while ((localElement3 = localElementIterator.next()) != null) {
/* 785 */         if (localElement3 == localElement2) {
/* 786 */           i = 1;
/*     */         }
/* 788 */         else if ((i != 0) && (isControl(localElement3))) {
/* 789 */           AttributeSet localAttributeSet = localElement3.getAttributes();
/*     */ 
/* 791 */           if (HTMLDocument.matchNameAttribute(localAttributeSet, HTML.Tag.INPUT))
/*     */           {
/* 793 */             String str = (String)localAttributeSet.getAttribute(HTML.Attribute.TYPE);
/*     */ 
/* 796 */             if (("text".equals(str)) || ("password".equals(str))) {
/* 797 */               return false;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 803 */     return true;
/*     */   }
/*     */ 
/*     */   void resetForm()
/*     */   {
/* 815 */     Element localElement1 = getFormElement();
/*     */ 
/* 817 */     if (localElement1 != null) {
/* 818 */       ElementIterator localElementIterator = new ElementIterator(localElement1);
/*     */       Element localElement2;
/* 821 */       while ((localElement2 = localElementIterator.next()) != null)
/* 822 */         if (isControl(localElement2)) {
/* 823 */           AttributeSet localAttributeSet = localElement2.getAttributes();
/* 824 */           Object localObject1 = localAttributeSet.getAttribute(StyleConstants.ModelAttribute);
/*     */           Object localObject2;
/* 826 */           if ((localObject1 instanceof TextAreaDocument)) {
/* 827 */             localObject2 = (TextAreaDocument)localObject1;
/* 828 */             ((TextAreaDocument)localObject2).reset();
/* 829 */           } else if ((localObject1 instanceof PlainDocument)) {
/*     */             try {
/* 831 */               localObject2 = (PlainDocument)localObject1;
/* 832 */               ((PlainDocument)localObject2).remove(0, ((PlainDocument)localObject2).getLength());
/* 833 */               if (HTMLDocument.matchNameAttribute(localAttributeSet, HTML.Tag.INPUT))
/*     */               {
/* 835 */                 String str = (String)localAttributeSet.getAttribute(HTML.Attribute.VALUE);
/*     */ 
/* 837 */                 if (str != null)
/* 838 */                   ((PlainDocument)localObject2).insertString(0, str, null);
/*     */               }
/*     */             }
/*     */             catch (BadLocationException localBadLocationException)
/*     */             {
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/*     */             Object localObject3;
/* 843 */             if ((localObject1 instanceof OptionListModel)) {
/* 844 */               localObject3 = (OptionListModel)localObject1;
/* 845 */               int i = ((OptionListModel)localObject3).getSize();
/* 846 */               for (int j = 0; j < i; j++) {
/* 847 */                 ((OptionListModel)localObject3).removeIndexInterval(j, j);
/*     */               }
/* 849 */               BitSet localBitSet = ((OptionListModel)localObject3).getInitialSelection();
/* 850 */               for (int k = 0; k < localBitSet.size(); k++)
/* 851 */                 if (localBitSet.get(k))
/* 852 */                   ((OptionListModel)localObject3).addSelectionInterval(k, k);
/*     */             }
/*     */             else
/*     */             {
/*     */               Object localObject4;
/* 855 */               if ((localObject1 instanceof OptionComboBoxModel)) {
/* 856 */                 localObject3 = (OptionComboBoxModel)localObject1;
/* 857 */                 localObject4 = ((OptionComboBoxModel)localObject3).getInitialSelection();
/* 858 */                 if (localObject4 != null)
/* 859 */                   ((OptionComboBoxModel)localObject3).setSelectedItem(localObject4);
/*     */               }
/* 861 */               else if ((localObject1 instanceof JToggleButton.ToggleButtonModel)) {
/* 862 */                 boolean bool = (String)localAttributeSet.getAttribute(HTML.Attribute.CHECKED) != null;
/*     */ 
/* 864 */                 localObject4 = (JToggleButton.ToggleButtonModel)localObject1;
/*     */ 
/* 866 */                 ((JToggleButton.ToggleButtonModel)localObject4).setSelected(bool);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class BrowseFileAction
/*     */     implements ActionListener
/*     */   {
/*     */     private AttributeSet attrs;
/*     */     private Document model;
/*     */ 
/*     */     BrowseFileAction(AttributeSet paramDocument, Document arg3)
/*     */     {
/* 885 */       this.attrs = paramDocument;
/*     */       Object localObject;
/* 886 */       this.model = localObject;
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent paramActionEvent)
/*     */     {
/* 892 */       JFileChooser localJFileChooser = new JFileChooser();
/* 893 */       localJFileChooser.setMultiSelectionEnabled(false);
/* 894 */       if (localJFileChooser.showOpenDialog(FormView.this.getContainer()) == 0)
/*     */       {
/* 896 */         File localFile = localJFileChooser.getSelectedFile();
/*     */ 
/* 898 */         if (localFile != null)
/*     */           try {
/* 900 */             if (this.model.getLength() > 0) {
/* 901 */               this.model.remove(0, this.model.getLength());
/*     */             }
/* 903 */             this.model.insertString(0, localFile.getPath(), null);
/*     */           }
/*     */           catch (BadLocationException localBadLocationException)
/*     */           {
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class MouseEventListener extends MouseAdapter
/*     */   {
/*     */     protected MouseEventListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mouseReleased(MouseEvent paramMouseEvent)
/*     */     {
/* 497 */       String str = FormView.this.getImageData(paramMouseEvent.getPoint());
/* 498 */       FormView.this.imageSubmit(str);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.text.html.FormView
 * JD-Core Version:    0.6.2
 */