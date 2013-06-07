/*      */ package javax.swing.text.html.parser;
/*      */ 
/*      */ import java.io.CharArrayReader;
/*      */ import java.io.IOException;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.io.Reader;
/*      */ import java.util.Vector;
/*      */ import javax.swing.text.ChangedCharSetException;
/*      */ import javax.swing.text.SimpleAttributeSet;
/*      */ import javax.swing.text.html.HTML;
/*      */ import javax.swing.text.html.HTML.Attribute;
/*      */ 
/*      */ public class Parser
/*      */   implements DTDConstants
/*      */ {
/*   83 */   private char[] text = new char[1024];
/*   84 */   private int textpos = 0;
/*      */   private TagElement last;
/*      */   private boolean space;
/*   88 */   private char[] str = new char[''];
/*   89 */   private int strpos = 0;
/*      */ 
/*   91 */   protected DTD dtd = null;
/*      */   private int ch;
/*      */   private int ln;
/*      */   private Reader in;
/*      */   private Element recent;
/*      */   private TagStack stack;
/*   99 */   private boolean skipTag = false;
/*  100 */   private TagElement lastFormSent = null;
/*  101 */   private SimpleAttributeSet attributes = new SimpleAttributeSet();
/*      */ 
/*  108 */   private boolean seenHtml = false;
/*  109 */   private boolean seenHead = false;
/*  110 */   private boolean seenBody = false;
/*      */   private boolean ignoreSpace;
/*  141 */   protected boolean strict = false;
/*      */   private int crlfCount;
/*      */   private int crCount;
/*      */   private int lfCount;
/*      */   private int currentBlockStartPos;
/*      */   private int lastBlockStartPos;
/*  168 */   private static final char[] cp1252Map = { '‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 'Œ', '', '', '', '', '‘', '’', '“', '”', '•', '–', '—', '˜', '™', 'š', '›', 'œ', '', '', 'Ÿ' };
/*      */   private static final String START_COMMENT = "<!--";
/*      */   private static final String END_COMMENT = "-->";
/* 1969 */   private static final char[] SCRIPT_END_TAG = "</script>".toCharArray();
/* 1970 */   private static final char[] SCRIPT_END_TAG_UPPER_CASE = "</SCRIPT>".toCharArray();
/*      */ 
/* 2276 */   private char[] buf = new char[1];
/*      */   private int pos;
/*      */   private int len;
/*      */   private int currentPosition;
/*      */ 
/*      */   public Parser(DTD paramDTD)
/*      */   {
/*  202 */     this.dtd = paramDTD;
/*      */   }
/*      */ 
/*      */   protected int getCurrentLine()
/*      */   {
/*  210 */     return this.ln;
/*      */   }
/*      */ 
/*      */   int getBlockStartPosition()
/*      */   {
/*  221 */     return Math.max(0, this.lastBlockStartPos - 1);
/*      */   }
/*      */ 
/*      */   protected TagElement makeTag(Element paramElement, boolean paramBoolean)
/*      */   {
/*  228 */     return new TagElement(paramElement, paramBoolean);
/*      */   }
/*      */ 
/*      */   protected TagElement makeTag(Element paramElement) {
/*  232 */     return makeTag(paramElement, false);
/*      */   }
/*      */ 
/*      */   protected SimpleAttributeSet getAttributes() {
/*  236 */     return this.attributes;
/*      */   }
/*      */ 
/*      */   protected void flushAttributes() {
/*  240 */     this.attributes.removeAttributes(this.attributes);
/*      */   }
/*      */ 
/*      */   protected void handleText(char[] paramArrayOfChar)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void handleTitle(char[] paramArrayOfChar)
/*      */   {
/*  255 */     handleText(paramArrayOfChar);
/*      */   }
/*      */ 
/*      */   protected void handleComment(char[] paramArrayOfChar)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void handleEOFInComment()
/*      */   {
/*  271 */     int i = strIndexOf('\n');
/*  272 */     if (i >= 0) {
/*  273 */       handleComment(getChars(0, i));
/*      */       try {
/*  275 */         this.in.close();
/*  276 */         this.in = new CharArrayReader(getChars(i + 1));
/*  277 */         this.ch = 62;
/*      */       } catch (IOException localIOException) {
/*  279 */         error("ioexception");
/*      */       }
/*      */ 
/*  282 */       resetStrBuffer();
/*      */     }
/*      */     else {
/*  285 */       error("eof.comment");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void handleEmptyTag(TagElement paramTagElement)
/*      */     throws ChangedCharSetException
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void handleStartTag(TagElement paramTagElement)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void handleEndTag(TagElement paramTagElement)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void handleError(int paramInt, String paramString)
/*      */   {
/*      */   }
/*      */ 
/*      */   void handleText(TagElement paramTagElement)
/*      */   {
/*  323 */     if (paramTagElement.breaksFlow()) {
/*  324 */       this.space = false;
/*  325 */       if (!this.strict) {
/*  326 */         this.ignoreSpace = true;
/*      */       }
/*      */     }
/*  329 */     if ((this.textpos == 0) && (
/*  330 */       (!this.space) || (this.stack == null) || (this.last.breaksFlow()) || (!this.stack.advance(this.dtd.pcdata))))
/*      */     {
/*  332 */       this.last = paramTagElement;
/*  333 */       this.space = false;
/*  334 */       this.lastBlockStartPos = this.currentBlockStartPos;
/*  335 */       return;
/*      */     }
/*      */ 
/*  338 */     if (this.space) {
/*  339 */       if (!this.ignoreSpace)
/*      */       {
/*  341 */         if (this.textpos + 1 > this.text.length) {
/*  342 */           arrayOfChar = new char[this.text.length + 200];
/*  343 */           System.arraycopy(this.text, 0, arrayOfChar, 0, this.text.length);
/*  344 */           this.text = arrayOfChar;
/*      */         }
/*      */ 
/*  348 */         this.text[(this.textpos++)] = ' ';
/*  349 */         if ((!this.strict) && (!paramTagElement.getElement().isEmpty())) {
/*  350 */           this.ignoreSpace = true;
/*      */         }
/*      */       }
/*  353 */       this.space = false;
/*      */     }
/*  355 */     char[] arrayOfChar = new char[this.textpos];
/*  356 */     System.arraycopy(this.text, 0, arrayOfChar, 0, this.textpos);
/*      */ 
/*  359 */     if (paramTagElement.getElement().getName().equals("title"))
/*  360 */       handleTitle(arrayOfChar);
/*      */     else {
/*  362 */       handleText(arrayOfChar);
/*      */     }
/*  364 */     this.lastBlockStartPos = this.currentBlockStartPos;
/*  365 */     this.textpos = 0;
/*  366 */     this.last = paramTagElement;
/*  367 */     this.space = false;
/*      */   }
/*      */ 
/*      */   protected void error(String paramString1, String paramString2, String paramString3, String paramString4)
/*      */   {
/*  375 */     handleError(this.ln, paramString1 + " " + paramString2 + " " + paramString3 + " " + paramString4);
/*      */   }
/*      */ 
/*      */   protected void error(String paramString1, String paramString2, String paramString3) {
/*  379 */     error(paramString1, paramString2, paramString3, "?");
/*      */   }
/*      */   protected void error(String paramString1, String paramString2) {
/*  382 */     error(paramString1, paramString2, "?", "?");
/*      */   }
/*      */   protected void error(String paramString) {
/*  385 */     error(paramString, "?", "?", "?");
/*      */   }
/*      */ 
/*      */   protected void startTag(TagElement paramTagElement)
/*      */     throws ChangedCharSetException
/*      */   {
/*  395 */     Element localElement = paramTagElement.getElement();
/*      */ 
/*  402 */     if ((!localElement.isEmpty()) || ((this.last != null) && (!this.last.breaksFlow())) || (this.textpos != 0))
/*      */     {
/*  405 */       handleText(paramTagElement);
/*      */     }
/*      */     else
/*      */     {
/*  411 */       this.last = paramTagElement;
/*      */ 
/*  414 */       this.space = false;
/*      */     }
/*  416 */     this.lastBlockStartPos = this.currentBlockStartPos;
/*      */ 
/*  419 */     for (AttributeList localAttributeList = localElement.atts; localAttributeList != null; localAttributeList = localAttributeList.next) {
/*  420 */       if ((localAttributeList.modifier == 2) && ((this.attributes.isEmpty()) || ((!this.attributes.isDefined(localAttributeList.name)) && (!this.attributes.isDefined(HTML.getAttributeKey(localAttributeList.name))))))
/*      */       {
/*  424 */         error("req.att ", localAttributeList.getName(), localElement.getName());
/*      */       }
/*      */     }
/*      */ 
/*  428 */     if (localElement.isEmpty()) {
/*  429 */       handleEmptyTag(paramTagElement);
/*      */     }
/*      */     else
/*      */     {
/*  435 */       this.recent = localElement;
/*  436 */       this.stack = new TagStack(paramTagElement, this.stack);
/*  437 */       handleStartTag(paramTagElement);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void endTag(boolean paramBoolean)
/*      */   {
/*  446 */     handleText(this.stack.tag);
/*      */ 
/*  448 */     if ((paramBoolean) && (!this.stack.elem.omitEnd()))
/*  449 */       error("end.missing", this.stack.elem.getName());
/*  450 */     else if (!this.stack.terminate()) {
/*  451 */       error("end.unexpected", this.stack.elem.getName());
/*      */     }
/*      */ 
/*  455 */     handleEndTag(this.stack.tag);
/*  456 */     this.stack = this.stack.next;
/*  457 */     this.recent = (this.stack != null ? this.stack.elem : null);
/*      */   }
/*      */ 
/*      */   boolean ignoreElement(Element paramElement)
/*      */   {
/*  463 */     String str1 = this.stack.elem.getName();
/*  464 */     String str2 = paramElement.getName();
/*      */ 
/*  471 */     if (((str2.equals("html")) && (this.seenHtml)) || ((str2.equals("head")) && (this.seenHead)) || ((str2.equals("body")) && (this.seenBody)))
/*      */     {
/*  474 */       return true;
/*      */     }
/*  476 */     if ((str2.equals("dt")) || (str2.equals("dd"))) {
/*  477 */       TagStack localTagStack = this.stack;
/*  478 */       while ((localTagStack != null) && (!localTagStack.elem.getName().equals("dl"))) {
/*  479 */         localTagStack = localTagStack.next;
/*      */       }
/*  481 */       if (localTagStack == null) {
/*  482 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*  486 */     if (((str1.equals("table")) && (!str2.equals("#pcdata")) && (!str2.equals("input"))) || ((str2.equals("font")) && ((str1.equals("ul")) || (str1.equals("ol")))) || ((str2.equals("meta")) && (this.stack != null)) || ((str2.equals("style")) && (this.seenBody)) || ((str1.equals("table")) && (str2.equals("a"))))
/*      */     {
/*  493 */       return true;
/*      */     }
/*  495 */     return false;
/*      */   }
/*      */ 
/*      */   protected void markFirstTime(Element paramElement)
/*      */   {
/*  504 */     String str1 = paramElement.getName();
/*  505 */     if (str1.equals("html")) {
/*  506 */       this.seenHtml = true;
/*  507 */     } else if (str1.equals("head")) {
/*  508 */       this.seenHead = true;
/*  509 */     } else if (str1.equals("body")) {
/*  510 */       if (this.buf.length == 1)
/*      */       {
/*  512 */         char[] arrayOfChar = new char[256];
/*      */ 
/*  514 */         arrayOfChar[0] = this.buf[0];
/*  515 */         this.buf = arrayOfChar;
/*      */       }
/*  517 */       this.seenBody = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean legalElementContext(Element paramElement)
/*      */     throws ChangedCharSetException
/*      */   {
/*  529 */     if (this.stack == null)
/*      */     {
/*  531 */       if (paramElement != this.dtd.html)
/*      */       {
/*  533 */         startTag(makeTag(this.dtd.html, true));
/*  534 */         return legalElementContext(paramElement);
/*      */       }
/*  536 */       return true;
/*      */     }
/*      */ 
/*  540 */     if (this.stack.advance(paramElement))
/*      */     {
/*  542 */       markFirstTime(paramElement);
/*  543 */       return true;
/*      */     }
/*  545 */     int i = 0;
/*      */ 
/*  572 */     String str1 = this.stack.elem.getName();
/*  573 */     String str2 = paramElement.getName();
/*      */ 
/*  576 */     if ((!this.strict) && (((str1.equals("table")) && (str2.equals("td"))) || ((str1.equals("table")) && (str2.equals("th"))) || ((str1.equals("tr")) && (!str2.equals("tr")))))
/*      */     {
/*  580 */       i = 1;
/*      */     }
/*      */ 
/*  584 */     if ((!this.strict) && (i == 0) && ((this.stack.elem.getName() != paramElement.getName()) || (paramElement.getName().equals("body"))))
/*      */     {
/*  586 */       if ((this.skipTag = ignoreElement(paramElement))) {
/*  587 */         error("tag.ignore", paramElement.getName());
/*  588 */         return this.skipTag;
/*      */       }
/*      */     }
/*      */     Object localObject2;
/*  595 */     if ((!this.strict) && (str1.equals("table")) && (!str2.equals("tr")) && (!str2.equals("td")) && (!str2.equals("th")) && (!str2.equals("caption")))
/*      */     {
/*  598 */       localObject1 = this.dtd.getElement("tr");
/*  599 */       localObject2 = makeTag((Element)localObject1, true);
/*  600 */       legalTagContext((TagElement)localObject2);
/*  601 */       startTag((TagElement)localObject2);
/*  602 */       error("start.missing", paramElement.getName());
/*  603 */       return legalElementContext(paramElement);
/*      */     }
/*      */ 
/*  614 */     if ((i == 0) && (this.stack.terminate()) && ((!this.strict) || (this.stack.elem.omitEnd()))) {
/*  615 */       for (localObject1 = this.stack.next; localObject1 != null; localObject1 = ((TagStack)localObject1).next) {
/*  616 */         if (((TagStack)localObject1).advance(paramElement)) {
/*  617 */           while (this.stack != localObject1) {
/*  618 */             endTag(true);
/*      */           }
/*  620 */           return true;
/*      */         }
/*  622 */         if ((!((TagStack)localObject1).terminate()) || ((this.strict) && (!((TagStack)localObject1).elem.omitEnd())))
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  632 */     Object localObject1 = this.stack.first();
/*  633 */     if ((localObject1 != null) && ((!this.strict) || (((Element)localObject1).omitStart())) && ((localObject1 != this.dtd.head) || (paramElement != this.dtd.pcdata)))
/*      */     {
/*  636 */       localObject2 = makeTag((Element)localObject1, true);
/*  637 */       legalTagContext((TagElement)localObject2);
/*  638 */       startTag((TagElement)localObject2);
/*  639 */       if (!((Element)localObject1).omitStart()) {
/*  640 */         error("start.missing", paramElement.getName());
/*      */       }
/*  642 */       return legalElementContext(paramElement);
/*      */     }
/*      */ 
/*  650 */     if (!this.strict) {
/*  651 */       localObject2 = this.stack.contentModel();
/*  652 */       Vector localVector = new Vector();
/*  653 */       if (localObject2 != null) {
/*  654 */         ((ContentModel)localObject2).getElements(localVector);
/*  655 */         for (Element localElement : localVector)
/*      */         {
/*  659 */           if (!this.stack.excluded(localElement.getIndex()))
/*      */           {
/*  663 */             int j = 0;
/*      */ 
/*  665 */             for (Object localObject3 = localElement.getAttributes(); localObject3 != null; localObject3 = ((AttributeList)localObject3).next) {
/*  666 */               if (((AttributeList)localObject3).modifier == 2) {
/*  667 */                 j = 1;
/*  668 */                 break;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*  674 */             if (j == 0)
/*      */             {
/*  678 */               localObject3 = localElement.getContent();
/*  679 */               if ((localObject3 != null) && (((ContentModel)localObject3).first(paramElement)))
/*      */               {
/*  681 */                 TagElement localTagElement = makeTag(localElement, true);
/*  682 */                 legalTagContext(localTagElement);
/*  683 */                 startTag(localTagElement);
/*  684 */                 error("start.missing", localElement.getName());
/*  685 */                 return legalElementContext(paramElement);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  695 */     if ((this.stack.terminate()) && (this.stack.elem != this.dtd.body) && ((!this.strict) || (this.stack.elem.omitEnd())))
/*      */     {
/*  697 */       if (!this.stack.elem.omitEnd()) {
/*  698 */         error("end.missing", paramElement.getName());
/*      */       }
/*      */ 
/*  701 */       endTag(true);
/*  702 */       return legalElementContext(paramElement);
/*      */     }
/*      */ 
/*  706 */     return false;
/*      */   }
/*      */ 
/*      */   void legalTagContext(TagElement paramTagElement)
/*      */     throws ChangedCharSetException
/*      */   {
/*  713 */     if (legalElementContext(paramTagElement.getElement())) {
/*  714 */       markFirstTime(paramTagElement.getElement());
/*  715 */       return;
/*      */     }
/*      */ 
/*  719 */     if ((paramTagElement.breaksFlow()) && (this.stack != null) && (!this.stack.tag.breaksFlow())) {
/*  720 */       endTag(true);
/*  721 */       legalTagContext(paramTagElement);
/*  722 */       return;
/*      */     }
/*      */ 
/*  726 */     for (TagStack localTagStack = this.stack; localTagStack != null; localTagStack = localTagStack.next) {
/*  727 */       if (localTagStack.tag.getElement() == this.dtd.head) {
/*  728 */         while (this.stack != localTagStack) {
/*  729 */           endTag(true);
/*      */         }
/*  731 */         endTag(true);
/*  732 */         legalTagContext(paramTagElement);
/*  733 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  738 */     error("tag.unexpected", paramTagElement.getElement().getName());
/*      */   }
/*      */ 
/*      */   void errorContext()
/*      */     throws ChangedCharSetException
/*      */   {
/*  746 */     for (; (this.stack != null) && (this.stack.tag.getElement() != this.dtd.body); this.stack = this.stack.next) {
/*  747 */       handleEndTag(this.stack.tag);
/*      */     }
/*  749 */     if (this.stack == null) {
/*  750 */       legalElementContext(this.dtd.body);
/*  751 */       startTag(makeTag(this.dtd.body, true));
/*      */     }
/*      */   }
/*      */ 
/*      */   void addString(int paramInt)
/*      */   {
/*  759 */     if (this.strpos == this.str.length) {
/*  760 */       char[] arrayOfChar = new char[this.str.length + 128];
/*  761 */       System.arraycopy(this.str, 0, arrayOfChar, 0, this.str.length);
/*  762 */       this.str = arrayOfChar;
/*      */     }
/*  764 */     this.str[(this.strpos++)] = ((char)paramInt);
/*      */   }
/*      */ 
/*      */   String getString(int paramInt)
/*      */   {
/*  771 */     char[] arrayOfChar = new char[this.strpos - paramInt];
/*  772 */     System.arraycopy(this.str, paramInt, arrayOfChar, 0, this.strpos - paramInt);
/*  773 */     this.strpos = paramInt;
/*  774 */     return new String(arrayOfChar);
/*      */   }
/*      */ 
/*      */   char[] getChars(int paramInt) {
/*  778 */     char[] arrayOfChar = new char[this.strpos - paramInt];
/*  779 */     System.arraycopy(this.str, paramInt, arrayOfChar, 0, this.strpos - paramInt);
/*  780 */     this.strpos = paramInt;
/*  781 */     return arrayOfChar;
/*      */   }
/*      */ 
/*      */   char[] getChars(int paramInt1, int paramInt2) {
/*  785 */     char[] arrayOfChar = new char[paramInt2 - paramInt1];
/*  786 */     System.arraycopy(this.str, paramInt1, arrayOfChar, 0, paramInt2 - paramInt1);
/*      */ 
/*  789 */     return arrayOfChar;
/*      */   }
/*      */ 
/*      */   void resetStrBuffer() {
/*  793 */     this.strpos = 0;
/*      */   }
/*      */ 
/*      */   int strIndexOf(char paramChar) {
/*  797 */     for (int i = 0; i < this.strpos; i++) {
/*  798 */       if (this.str[i] == paramChar) {
/*  799 */         return i;
/*      */       }
/*      */     }
/*      */ 
/*  803 */     return -1;
/*      */   }
/*      */ 
/*      */   void skipSpace()
/*      */     throws IOException
/*      */   {
/*      */     while (true)
/*  812 */       switch (this.ch) {
/*      */       case 10:
/*  814 */         this.ln += 1;
/*  815 */         this.ch = readCh();
/*  816 */         this.lfCount += 1;
/*  817 */         break;
/*      */       case 13:
/*  820 */         this.ln += 1;
/*  821 */         if ((this.ch = readCh()) == 10) {
/*  822 */           this.ch = readCh();
/*  823 */           this.crlfCount += 1;
/*      */         }
/*      */         else {
/*  826 */           this.crCount += 1;
/*      */         }
/*  828 */         break;
/*      */       case 9:
/*      */       case 32:
/*  831 */         this.ch = readCh();
/*      */       }
/*      */   }
/*      */ 
/*      */   boolean parseIdentifier(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  846 */     switch (this.ch) { case 65:
/*      */     case 66:
/*      */     case 67:
/*      */     case 68:
/*      */     case 69:
/*      */     case 70:
/*      */     case 71:
/*      */     case 72:
/*      */     case 73:
/*      */     case 74:
/*      */     case 75:
/*      */     case 76:
/*      */     case 77:
/*      */     case 78:
/*      */     case 79:
/*      */     case 80:
/*      */     case 81:
/*      */     case 82:
/*      */     case 83:
/*      */     case 84:
/*      */     case 85:
/*      */     case 86:
/*      */     case 87:
/*      */     case 88:
/*      */     case 89:
/*      */     case 90:
/*  852 */       if (paramBoolean)
/*  853 */         this.ch = (97 + (this.ch - 65)); case 97:
/*      */     case 98:
/*      */     case 99:
/*      */     case 100:
/*      */     case 101:
/*      */     case 102:
/*      */     case 103:
/*      */     case 104:
/*      */     case 105:
/*      */     case 106:
/*      */     case 107:
/*      */     case 108:
/*      */     case 109:
/*      */     case 110:
/*      */     case 111:
/*      */     case 112:
/*      */     case 113:
/*      */     case 114:
/*      */     case 115:
/*      */     case 116:
/*      */     case 117:
/*      */     case 118:
/*      */     case 119:
/*      */     case 120:
/*      */     case 121:
/*      */     case 122:
/*  861 */       break;
/*      */     case 91:
/*      */     case 92:
/*      */     case 93:
/*      */     case 94:
/*      */     case 95:
/*  864 */     case 96: } return false;
/*      */     while (true)
/*      */     {
/*  868 */       addString(this.ch);
/*      */ 
/*  870 */       switch (this.ch = readCh()) { case 65:
/*      */       case 66:
/*      */       case 67:
/*      */       case 68:
/*      */       case 69:
/*      */       case 70:
/*      */       case 71:
/*      */       case 72:
/*      */       case 73:
/*      */       case 74:
/*      */       case 75:
/*      */       case 76:
/*      */       case 77:
/*      */       case 78:
/*      */       case 79:
/*      */       case 80:
/*      */       case 81:
/*      */       case 82:
/*      */       case 83:
/*      */       case 84:
/*      */       case 85:
/*      */       case 86:
/*      */       case 87:
/*      */       case 88:
/*      */       case 89:
/*      */       case 90:
/*  876 */         if (paramBoolean)
/*  877 */           this.ch = (97 + (this.ch - 65)); break;
/*      */       case 45:
/*      */       case 46:
/*      */       case 48:
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*      */       case 95:
/*      */       case 97:
/*      */       case 98:
/*      */       case 99:
/*      */       case 100:
/*      */       case 101:
/*      */       case 102:
/*      */       case 103:
/*      */       case 104:
/*      */       case 105:
/*      */       case 106:
/*      */       case 107:
/*      */       case 108:
/*      */       case 109:
/*      */       case 110:
/*      */       case 111:
/*      */       case 112:
/*      */       case 113:
/*      */       case 114:
/*      */       case 115:
/*      */       case 116:
/*      */       case 117:
/*      */       case 118:
/*      */       case 119:
/*      */       case 120:
/*      */       case 121:
/*      */       case 122:
/*      */       case 47:
/*      */       case 58:
/*      */       case 59:
/*      */       case 60:
/*      */       case 61:
/*      */       case 62:
/*      */       case 63:
/*      */       case 64:
/*      */       case 91:
/*      */       case 92:
/*      */       case 93:
/*      */       case 94:
/*  895 */       case 96: }  } return true;
/*      */   }
/*      */ 
/*      */   private char[] parseEntityReference()
/*      */     throws IOException
/*      */   {
/*  904 */     int i = this.strpos;
/*      */ 
/*  906 */     if ((this.ch = readCh()) == 35) {
/*  907 */       int j = 0;
/*  908 */       this.ch = readCh();
/*  909 */       if (((this.ch >= 48) && (this.ch <= 57)) || (this.ch == 120) || (this.ch == 88))
/*      */       {
/*  912 */         if ((this.ch >= 48) && (this.ch <= 57));
/*  914 */         while ((this.ch >= 48) && (this.ch <= 57)) {
/*  915 */           j = j * 10 + this.ch - 48;
/*  916 */           this.ch = readCh(); continue;
/*      */ 
/*  920 */           this.ch = readCh();
/*  921 */           int m = (char)Character.toLowerCase(this.ch);
/*  922 */           while (((m >= 48) && (m <= 57)) || ((m >= 97) && (m <= 102)))
/*      */           {
/*  924 */             if ((m >= 48) && (m <= 57))
/*  925 */               j = j * 16 + m - 48;
/*      */             else {
/*  927 */               j = j * 16 + m - 97 + 10;
/*      */             }
/*  929 */             this.ch = readCh();
/*  930 */             m = (char)Character.toLowerCase(this.ch);
/*      */           }
/*      */         }
/*  933 */         switch (this.ch) {
/*      */         case 10:
/*  935 */           this.ln += 1;
/*  936 */           this.ch = readCh();
/*  937 */           this.lfCount += 1;
/*  938 */           break;
/*      */         case 13:
/*  941 */           this.ln += 1;
/*  942 */           if ((this.ch = readCh()) == 10) {
/*  943 */             this.ch = readCh();
/*  944 */             this.crlfCount += 1;
/*      */           }
/*      */           else {
/*  947 */             this.crCount += 1;
/*      */           }
/*  949 */           break;
/*      */         case 59:
/*  952 */           this.ch = readCh();
/*      */         }
/*      */ 
/*  955 */         localObject = new char[] { mapNumericReference((char)j) };
/*  956 */         return localObject;
/*      */       }
/*  958 */       addString(35);
/*  959 */       if (!parseIdentifier(false)) {
/*  960 */         error("ident.expected");
/*  961 */         this.strpos = i;
/*  962 */         localObject = new char[] { '&', '#' };
/*  963 */         return localObject;
/*      */       }
/*  965 */     } else if (!parseIdentifier(false)) {
/*  966 */       char[] arrayOfChar1 = { '&' };
/*  967 */       return arrayOfChar1;
/*      */     }
/*      */ 
/*  970 */     int k = 0;
/*      */ 
/*  972 */     switch (this.ch) {
/*      */     case 10:
/*  974 */       this.ln += 1;
/*  975 */       this.ch = readCh();
/*  976 */       this.lfCount += 1;
/*  977 */       break;
/*      */     case 13:
/*  980 */       this.ln += 1;
/*  981 */       if ((this.ch = readCh()) == 10) {
/*  982 */         this.ch = readCh();
/*  983 */         this.crlfCount += 1;
/*      */       }
/*      */       else {
/*  986 */         this.crCount += 1;
/*      */       }
/*  988 */       break;
/*      */     case 59:
/*  991 */       k = 1;
/*      */ 
/*  993 */       this.ch = readCh();
/*      */     }
/*      */ 
/*  997 */     Object localObject = getString(i);
/*  998 */     Entity localEntity = this.dtd.getEntity((String)localObject);
/*      */ 
/* 1004 */     if ((!this.strict) && (localEntity == null)) {
/* 1005 */       localEntity = this.dtd.getEntity(((String)localObject).toLowerCase());
/*      */     }
/* 1007 */     if ((localEntity == null) || (!localEntity.isGeneral()))
/*      */     {
/* 1009 */       if (((String)localObject).length() == 0) {
/* 1010 */         error("invalid.entref", (String)localObject);
/* 1011 */         return new char[0];
/*      */       }
/*      */ 
/* 1014 */       String str1 = "&" + (String)localObject + (k != 0 ? ";" : "");
/*      */ 
/* 1016 */       char[] arrayOfChar2 = new char[str1.length()];
/* 1017 */       str1.getChars(0, arrayOfChar2.length, arrayOfChar2, 0);
/* 1018 */       return arrayOfChar2;
/*      */     }
/* 1020 */     return localEntity.getData();
/*      */   }
/*      */ 
/*      */   private char mapNumericReference(char paramChar)
/*      */   {
/* 1036 */     if ((paramChar < '') || (paramChar > '')) {
/* 1037 */       return paramChar;
/*      */     }
/* 1039 */     return cp1252Map[(paramChar - '')];
/*      */   }
/*      */ 
/*      */   void parseComment()
/*      */     throws IOException
/*      */   {
/*      */     while (true)
/*      */     {
/* 1048 */       int i = this.ch;
/* 1049 */       switch (i)
/*      */       {
/*      */       case 45:
/* 1061 */         if ((!this.strict) && (this.strpos != 0) && (this.str[(this.strpos - 1)] == '-')) {
/* 1062 */           if ((this.ch = readCh()) == 62) {
/* 1063 */             return;
/*      */           }
/* 1065 */           if (this.ch != 33) break label343;
/* 1066 */           if ((this.ch = readCh()) == 62) {
/* 1067 */             return;
/*      */           }
/*      */ 
/* 1070 */           addString(45);
/* 1071 */           addString(33);
/* 1072 */           continue;
/*      */         }
/*      */ 
/* 1078 */         if ((this.ch = readCh()) != 45) break label343;
/* 1079 */         this.ch = readCh();
/* 1080 */         if ((this.strict) || (this.ch == 62)) {
/* 1081 */           return;
/*      */         }
/* 1083 */         if (this.ch == 33) {
/* 1084 */           if ((this.ch = readCh()) == 62) {
/* 1085 */             return;
/*      */           }
/*      */ 
/* 1088 */           addString(45);
/* 1089 */           addString(33);
/* 1090 */           continue;
/*      */         }
/*      */ 
/* 1094 */         addString(45); break;
/*      */       case -1:
/* 1099 */         handleEOFInComment();
/* 1100 */         return;
/*      */       case 10:
/* 1103 */         this.ln += 1;
/* 1104 */         this.ch = readCh();
/* 1105 */         this.lfCount += 1;
/* 1106 */         break;
/*      */       case 62:
/* 1109 */         this.ch = readCh();
/* 1110 */         break;
/*      */       case 13:
/* 1113 */         this.ln += 1;
/* 1114 */         if ((this.ch = readCh()) == 10) {
/* 1115 */           this.ch = readCh();
/* 1116 */           this.crlfCount += 1;
/*      */         }
/*      */         else {
/* 1119 */           this.crCount += 1;
/*      */         }
/* 1121 */         i = 10;
/* 1122 */         break;
/*      */       }
/* 1124 */       this.ch = readCh();
/*      */ 
/* 1128 */       label343: addString(i);
/*      */     }
/*      */   }
/*      */ 
/*      */   void parseLiteral(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*      */     while (true)
/*      */     {
/* 1137 */       int i = this.ch;
/* 1138 */       switch (i) {
/*      */       case -1:
/* 1140 */         error("eof.literal", this.stack.elem.getName());
/* 1141 */         endTag(true);
/* 1142 */         return;
/*      */       case 62:
/* 1145 */         this.ch = readCh();
/* 1146 */         int j = this.textpos - (this.stack.elem.name.length() + 2); int k = 0;
/*      */ 
/* 1149 */         if ((j < 0) || (this.text[(j++)] != '<') || (this.text[j] != '/')) break label456;
/*      */         do j++; while ((j < this.textpos) && (Character.toLowerCase(this.text[j]) == this.stack.elem.name.charAt(k++)));
/*      */ 
/* 1152 */         if (j != this.textpos) break label456;
/* 1153 */         this.textpos -= this.stack.elem.name.length() + 2;
/* 1154 */         if ((this.textpos > 0) && (this.text[(this.textpos - 1)] == '\n')) {
/* 1155 */           this.textpos -= 1;
/*      */         }
/* 1157 */         endTag(false);
/* 1158 */         return;
/*      */       case 38:
/* 1164 */         char[] arrayOfChar2 = parseEntityReference();
/* 1165 */         if (this.textpos + arrayOfChar2.length > this.text.length) {
/* 1166 */           char[] arrayOfChar3 = new char[Math.max(this.textpos + arrayOfChar2.length + 128, this.text.length * 2)];
/* 1167 */           System.arraycopy(this.text, 0, arrayOfChar3, 0, this.text.length);
/* 1168 */           this.text = arrayOfChar3;
/*      */         }
/* 1170 */         System.arraycopy(arrayOfChar2, 0, this.text, this.textpos, arrayOfChar2.length);
/* 1171 */         this.textpos += arrayOfChar2.length;
/* 1172 */         break;
/*      */       case 10:
/* 1175 */         this.ln += 1;
/* 1176 */         this.ch = readCh();
/* 1177 */         this.lfCount += 1;
/* 1178 */         break;
/*      */       case 13:
/* 1181 */         this.ln += 1;
/* 1182 */         if ((this.ch = readCh()) == 10) {
/* 1183 */           this.ch = readCh();
/* 1184 */           this.crlfCount += 1;
/*      */         }
/*      */         else {
/* 1187 */           this.crCount += 1;
/*      */         }
/* 1189 */         i = 10;
/* 1190 */         break;
/*      */       }
/* 1192 */       this.ch = readCh();
/*      */ 
/* 1197 */       label456: if (this.textpos == this.text.length) {
/* 1198 */         char[] arrayOfChar1 = new char[this.text.length + 128];
/* 1199 */         System.arraycopy(this.text, 0, arrayOfChar1, 0, this.text.length);
/* 1200 */         this.text = arrayOfChar1;
/*      */       }
/* 1202 */       this.text[(this.textpos++)] = ((char)i);
/*      */     }
/*      */   }
/*      */ 
/*      */   String parseAttributeValue(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1210 */     int i = -1;
/*      */ 
/* 1213 */     switch (this.ch) {
/*      */     case 34:
/*      */     case 39:
/* 1216 */       i = this.ch;
/* 1217 */       this.ch = readCh();
/*      */     }
/*      */ 
/*      */     while (true)
/*      */     {
/* 1223 */       int j = this.ch;
/*      */ 
/* 1225 */       switch (j) {
/*      */       case 10:
/* 1227 */         this.ln += 1;
/* 1228 */         this.ch = readCh();
/* 1229 */         this.lfCount += 1;
/* 1230 */         if (i < 0) {
/* 1231 */           return getString(0);
/*      */         }
/*      */ 
/*      */       case 13:
/* 1236 */         this.ln += 1;
/*      */ 
/* 1238 */         if ((this.ch = readCh()) == 10) {
/* 1239 */           this.ch = readCh();
/* 1240 */           this.crlfCount += 1;
/*      */         }
/*      */         else {
/* 1243 */           this.crCount += 1;
/*      */         }
/* 1245 */         if (i < 0) {
/* 1246 */           return getString(0);
/*      */         }
/*      */ 
/*      */       case 9:
/* 1251 */         if (i < 0)
/* 1252 */           j = 32;
/*      */       case 32:
/* 1254 */         this.ch = readCh();
/* 1255 */         if (i < 0) {
/* 1256 */           return getString(0);
/*      */         }
/*      */ 
/*      */       case 60:
/*      */       case 62:
/* 1262 */         if (i < 0) {
/* 1263 */           return getString(0);
/*      */         }
/* 1265 */         this.ch = readCh();
/* 1266 */         break;
/*      */       case 34:
/*      */       case 39:
/* 1270 */         this.ch = readCh();
/* 1271 */         if (j == i)
/* 1272 */           return getString(0);
/* 1273 */         if (i == -1) {
/* 1274 */           error("attvalerr");
/* 1275 */           if ((!this.strict) && (this.ch != 32)) continue;
/* 1276 */           return getString(0);
/*      */         }
/*      */ 
/*      */       case 61:
/* 1284 */         if (i < 0)
/*      */         {
/* 1289 */           error("attvalerr");
/*      */ 
/* 1293 */           if (this.strict) {
/* 1294 */             return getString(0);
/*      */           }
/*      */         }
/* 1297 */         this.ch = readCh();
/* 1298 */         break;
/*      */       case 38:
/* 1301 */         if ((this.strict) && (i < 0)) {
/* 1302 */           this.ch = readCh();
/*      */         }
/*      */         else
/*      */         {
/* 1306 */           char[] arrayOfChar = parseEntityReference();
/* 1307 */           for (int k = 0; k < arrayOfChar.length; k++) {
/* 1308 */             j = arrayOfChar[k];
/* 1309 */             addString((paramBoolean) && (j >= 65) && (j <= 90) ? 97 + j - 65 : j);
/*      */           }
/*      */         }
/* 1311 */         break;
/*      */       case -1:
/* 1314 */         return getString(0);
/*      */       default:
/* 1317 */         if ((paramBoolean) && (j >= 65) && (j <= 90)) {
/* 1318 */           j = 97 + j - 65;
/*      */         }
/* 1320 */         this.ch = readCh();
/*      */ 
/* 1323 */         addString(j);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void parseAttributeSpecificationList(Element paramElement)
/*      */     throws IOException
/*      */   {
/*      */     while (true)
/*      */     {
/* 1334 */       skipSpace();
/*      */ 
/* 1336 */       switch (this.ch) {
/*      */       case -1:
/*      */       case 47:
/*      */       case 60:
/*      */       case 62:
/* 1341 */         return;
/*      */       case 45:
/* 1344 */         if ((this.ch = readCh()) == 45) {
/* 1345 */           this.ch = readCh();
/* 1346 */           parseComment();
/* 1347 */           this.strpos = 0; continue;
/*      */         }
/* 1349 */         error("invalid.tagchar", "-", paramElement.getName());
/* 1350 */         this.ch = readCh();
/*      */ 
/* 1352 */         break;
/*      */       }
/*      */       String str1;
/*      */       AttributeList localAttributeList;
/*      */       String str2;
/* 1359 */       if (parseIdentifier(true)) {
/* 1360 */         str1 = getString(0);
/* 1361 */         skipSpace();
/* 1362 */         if (this.ch == 61) {
/* 1363 */           this.ch = readCh();
/* 1364 */           skipSpace();
/* 1365 */           localAttributeList = paramElement.getAttribute(str1);
/*      */ 
/* 1370 */           str2 = parseAttributeValue((localAttributeList != null) && (localAttributeList.type != 1) && (localAttributeList.type != 11) && (localAttributeList.type != 7));
/*      */         }
/*      */         else {
/* 1373 */           str2 = str1;
/* 1374 */           localAttributeList = paramElement.getAttributeByValue(str2);
/* 1375 */           if (localAttributeList == null) {
/* 1376 */             localAttributeList = paramElement.getAttribute(str1);
/* 1377 */             if (localAttributeList != null) {
/* 1378 */               str2 = localAttributeList.getValue();
/*      */             }
/*      */             else
/*      */             {
/* 1383 */               str2 = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       } else { if ((!this.strict) && (this.ch == 44)) {
/* 1388 */           this.ch = readCh();
/* 1389 */           continue;
/* 1390 */         }if ((!this.strict) && (this.ch == 34)) {
/* 1391 */           this.ch = readCh();
/* 1392 */           skipSpace();
/* 1393 */           if (parseIdentifier(true)) {
/* 1394 */             str1 = getString(0);
/* 1395 */             if (this.ch == 34) {
/* 1396 */               this.ch = readCh();
/*      */             }
/* 1398 */             skipSpace();
/* 1399 */             if (this.ch == 61) {
/* 1400 */               this.ch = readCh();
/* 1401 */               skipSpace();
/* 1402 */               localAttributeList = paramElement.getAttribute(str1);
/* 1403 */               str2 = parseAttributeValue((localAttributeList != null) && (localAttributeList.type != 1) && (localAttributeList.type != 11)); break label651;
/*      */             }
/*      */ 
/* 1407 */             str2 = str1;
/* 1408 */             localAttributeList = paramElement.getAttributeByValue(str2);
/* 1409 */             if (localAttributeList != null) break label651;
/* 1410 */             localAttributeList = paramElement.getAttribute(str1);
/* 1411 */             if (localAttributeList == null) break label651;
/* 1412 */             str2 = localAttributeList.getValue(); break label651;
/*      */           }
/*      */ 
/* 1417 */           localObject = new char[] { (char)this.ch };
/* 1418 */           error("invalid.tagchar", new String((char[])localObject), paramElement.getName());
/* 1419 */           this.ch = readCh();
/* 1420 */           continue;
/*      */         }
/* 1422 */         if ((!this.strict) && (this.attributes.isEmpty()) && (this.ch == 61)) {
/* 1423 */           this.ch = readCh();
/* 1424 */           skipSpace();
/* 1425 */           str1 = paramElement.getName();
/* 1426 */           localAttributeList = paramElement.getAttribute(str1);
/* 1427 */           str2 = parseAttributeValue((localAttributeList != null) && (localAttributeList.type != 1) && (localAttributeList.type != 11));
/*      */         }
/*      */         else {
/* 1430 */           if ((!this.strict) && (this.ch == 61)) {
/* 1431 */             this.ch = readCh();
/* 1432 */             skipSpace();
/* 1433 */             str2 = parseAttributeValue(true);
/* 1434 */             error("attvalerr");
/* 1435 */             return;
/*      */           }
/* 1437 */           localObject = new char[] { (char)this.ch };
/* 1438 */           error("invalid.tagchar", new String((char[])localObject), paramElement.getName());
/* 1439 */           if (!this.strict) {
/* 1440 */             this.ch = readCh();
/* 1441 */             continue;
/*      */           }
/* 1443 */           return;
/*      */         }
/*      */       }
/*      */ 
/* 1447 */       label651: if (localAttributeList != null)
/* 1448 */         str1 = localAttributeList.getName();
/*      */       else {
/* 1450 */         error("invalid.tagatt", str1, paramElement.getName());
/*      */       }
/*      */ 
/* 1454 */       if (this.attributes.isDefined(str1)) {
/* 1455 */         error("multi.tagatt", str1, paramElement.getName());
/*      */       }
/* 1457 */       if (str2 == null) {
/* 1458 */         str2 = (localAttributeList != null) && (localAttributeList.value != null) ? localAttributeList.value : "#DEFAULT";
/*      */       }
/* 1460 */       else if ((localAttributeList != null) && (localAttributeList.values != null) && (!localAttributeList.values.contains(str2))) {
/* 1461 */         error("invalid.tagattval", str1, paramElement.getName());
/*      */       }
/* 1463 */       Object localObject = HTML.getAttributeKey(str1);
/* 1464 */       if (localObject == null)
/* 1465 */         this.attributes.addAttribute(str1, str2);
/*      */       else
/* 1467 */         this.attributes.addAttribute(localObject, str2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String parseDTDMarkup()
/*      */     throws IOException
/*      */   {
/* 1478 */     StringBuilder localStringBuilder = new StringBuilder();
/* 1479 */     this.ch = readCh();
/*      */     while (true)
/* 1481 */       switch (this.ch) {
/*      */       case 62:
/* 1483 */         this.ch = readCh();
/* 1484 */         return localStringBuilder.toString();
/*      */       case -1:
/* 1486 */         error("invalid.markup");
/* 1487 */         return localStringBuilder.toString();
/*      */       case 10:
/* 1489 */         this.ln += 1;
/* 1490 */         this.ch = readCh();
/* 1491 */         this.lfCount += 1;
/* 1492 */         break;
/*      */       case 34:
/* 1494 */         this.ch = readCh();
/* 1495 */         break;
/*      */       case 13:
/* 1497 */         this.ln += 1;
/* 1498 */         if ((this.ch = readCh()) == 10) {
/* 1499 */           this.ch = readCh();
/* 1500 */           this.crlfCount += 1;
/*      */         }
/*      */         else {
/* 1503 */           this.crCount += 1;
/*      */         }
/* 1505 */         break;
/*      */       default:
/* 1507 */         localStringBuilder.append((char)(this.ch & 0xFF));
/* 1508 */         this.ch = readCh();
/*      */       }
/*      */   }
/*      */ 
/*      */   protected boolean parseMarkupDeclarations(StringBuffer paramStringBuffer)
/*      */     throws IOException
/*      */   {
/* 1522 */     if ((paramStringBuffer.length() == "DOCTYPE".length()) && (paramStringBuffer.toString().toUpperCase().equals("DOCTYPE")))
/*      */     {
/* 1524 */       parseDTDMarkup();
/* 1525 */       return true;
/*      */     }
/* 1527 */     return false;
/*      */   }
/*      */ 
/*      */   void parseInvalidTag()
/*      */     throws IOException
/*      */   {
/*      */     while (true)
/*      */     {
/* 1536 */       skipSpace();
/* 1537 */       switch (this.ch) {
/*      */       case -1:
/*      */       case 62:
/* 1540 */         this.ch = readCh();
/* 1541 */         return;
/*      */       case 60:
/* 1543 */         return;
/*      */       }
/* 1545 */       this.ch = readCh();
/*      */     }
/*      */   }
/*      */ 
/*      */   void parseTag()
/*      */     throws IOException
/*      */   {
/* 1556 */     boolean bool = false;
/* 1557 */     int i = 0;
/* 1558 */     int j = 0;
/*      */     Element localElement;
/* 1560 */     switch (this.ch = readCh()) {
/*      */     case 33:
/* 1562 */       switch (this.ch = readCh())
/*      */       {
/*      */       case 45:
/*      */         while (true) {
/* 1566 */           if (this.ch == 45) {
/* 1567 */             if ((!this.strict) || ((this.ch = readCh()) == 45)) {
/* 1568 */               this.ch = readCh();
/* 1569 */               if ((!this.strict) && (this.ch == 45)) {
/* 1570 */                 this.ch = readCh();
/*      */               }
/*      */ 
/* 1575 */               if (this.textpos != 0) {
/* 1576 */                 localObject = new char[this.textpos];
/* 1577 */                 System.arraycopy(this.text, 0, localObject, 0, this.textpos);
/* 1578 */                 handleText((char[])localObject);
/* 1579 */                 this.lastBlockStartPos = this.currentBlockStartPos;
/* 1580 */                 this.textpos = 0;
/*      */               }
/* 1582 */               parseComment();
/* 1583 */               this.last = makeTag(this.dtd.getElement("comment"), true);
/* 1584 */               handleComment(getChars(0));
/*      */             }
/* 1586 */             else if (i == 0) {
/* 1587 */               i = 1;
/* 1588 */               error("invalid.commentchar", "-");
/*      */             }
/*      */           } else {
/* 1591 */             skipSpace();
/* 1592 */             switch (this.ch) {
/*      */             case 45:
/* 1594 */               break;
/*      */             case 62:
/* 1596 */               this.ch = readCh();
/*      */             case -1:
/* 1598 */               return;
/*      */             default:
/* 1600 */               this.ch = readCh();
/* 1601 */               if (i == 0) {
/* 1602 */                 i = 1;
/* 1603 */                 error("invalid.commentchar", String.valueOf((char)this.ch));
/*      */               }
/*      */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1612 */       localObject = new StringBuffer();
/*      */       while (true) {
/* 1614 */         ((StringBuffer)localObject).append((char)this.ch);
/* 1615 */         if (parseMarkupDeclarations((StringBuffer)localObject)) {
/* 1616 */           return;
/*      */         }
/* 1618 */         switch (this.ch) {
/*      */         case 62:
/* 1620 */           this.ch = readCh();
/*      */         case -1:
/* 1622 */           error("invalid.markup");
/* 1623 */           return;
/*      */         case 10:
/* 1625 */           this.ln += 1;
/* 1626 */           this.ch = readCh();
/* 1627 */           this.lfCount += 1;
/* 1628 */           break;
/*      */         case 13:
/* 1630 */           this.ln += 1;
/* 1631 */           if ((this.ch = readCh()) == 10) {
/* 1632 */             this.ch = readCh();
/* 1633 */             this.crlfCount += 1;
/*      */           }
/*      */           else {
/* 1636 */             this.crCount += 1;
/*      */           }
/* 1638 */           break;
/*      */         default:
/* 1641 */           this.ch = readCh();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     case 47:
/* 1649 */       switch (this.ch = readCh()) {
/*      */       case 62:
/* 1651 */         this.ch = readCh();
/*      */       case 60:
/* 1654 */         if (this.recent == null) {
/* 1655 */           error("invalid.shortend");
/* 1656 */           return;
/*      */         }
/* 1658 */         localElement = this.recent;
/* 1659 */         break;
/*      */       default:
/* 1662 */         if (!parseIdentifier(true)) {
/* 1663 */           error("expected.endtagname");
/* 1664 */           return;
/*      */         }
/* 1666 */         skipSpace();
/* 1667 */         switch (this.ch) {
/*      */         case 62:
/* 1669 */           this.ch = readCh();
/*      */         case 60:
/* 1671 */           break;
/*      */         default:
/* 1674 */           error("expected", "'>'");
/* 1675 */           while ((this.ch != -1) && (this.ch != 10) && (this.ch != 62)) {
/* 1676 */             this.ch = readCh();
/*      */           }
/* 1678 */           if (this.ch == 62) {
/* 1679 */             this.ch = readCh();
/*      */           }
/*      */           break;
/*      */         }
/* 1683 */         localObject = getString(0);
/* 1684 */         if (!this.dtd.elementExists((String)localObject)) {
/* 1685 */           error("end.unrecognized", (String)localObject);
/*      */ 
/* 1687 */           if ((this.textpos > 0) && (this.text[(this.textpos - 1)] == '\n')) {
/* 1688 */             this.textpos -= 1;
/*      */           }
/* 1690 */           localElement = this.dtd.getElement("unknown");
/* 1691 */           localElement.name = ((String)localObject);
/* 1692 */           j = 1;
/*      */         } else {
/* 1694 */           localElement = this.dtd.getElement((String)localObject);
/*      */         }
/*      */ 
/*      */         break;
/*      */       }
/*      */ 
/* 1703 */       if (this.stack == null) {
/* 1704 */         error("end.extra.tag", localElement.getName());
/* 1705 */         return;
/*      */       }
/*      */ 
/* 1709 */       if ((this.textpos > 0) && (this.text[(this.textpos - 1)] == '\n'))
/*      */       {
/* 1714 */         if (this.stack.pre) {
/* 1715 */           if ((this.textpos > 1) && (this.text[(this.textpos - 2)] != '\n'))
/* 1716 */             this.textpos -= 1;
/*      */         }
/*      */         else {
/* 1719 */           this.textpos -= 1;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1740 */       if (j != 0)
/*      */       {
/* 1746 */         localObject = makeTag(localElement);
/* 1747 */         handleText((TagElement)localObject);
/* 1748 */         this.attributes.addAttribute(HTML.Attribute.ENDTAG, "true");
/* 1749 */         handleEmptyTag(makeTag(localElement));
/* 1750 */         j = 0;
/* 1751 */         return;
/*      */       }
/*      */ 
/* 1760 */       if (!this.strict) {
/* 1761 */         localObject = this.stack.elem.getName();
/*      */ 
/* 1763 */         if (((String)localObject).equals("table"))
/*      */         {
/* 1766 */           if (!localElement.getName().equals(localObject)) {
/* 1767 */             error("tag.ignore", localElement.getName());
/* 1768 */             return;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1774 */         if ((((String)localObject).equals("tr")) || (((String)localObject).equals("td")))
/*      */         {
/* 1776 */           if ((!localElement.getName().equals("table")) && (!localElement.getName().equals(localObject)))
/*      */           {
/* 1778 */             error("tag.ignore", localElement.getName());
/* 1779 */             return;
/*      */           }
/*      */         }
/*      */       }
/* 1783 */       localObject = this.stack;
/*      */ 
/* 1785 */       while ((localObject != null) && (localElement != ((TagStack)localObject).elem)) {
/* 1786 */         localObject = ((TagStack)localObject).next;
/*      */       }
/* 1788 */       if (localObject == null) {
/* 1789 */         error("unmatched.endtag", localElement.getName());
/* 1790 */         return;
/*      */       }
/*      */ 
/* 1798 */       String str1 = localElement.getName();
/* 1799 */       if ((this.stack != localObject) && ((str1.equals("font")) || (str1.equals("center"))))
/*      */       {
/* 1808 */         if (str1.equals("center")) {
/* 1809 */           while ((this.stack.elem.omitEnd()) && (this.stack != localObject)) {
/* 1810 */             endTag(true);
/*      */           }
/* 1812 */           if (this.stack.elem == localElement) {
/* 1813 */             endTag(false);
/*      */           }
/*      */         }
/* 1816 */         return;
/*      */       }
/*      */ 
/* 1825 */       while (this.stack != localObject) {
/* 1826 */         endTag(true);
/*      */       }
/*      */ 
/* 1829 */       endTag(false);
/* 1830 */       return;
/*      */     case -1:
/* 1833 */       error("eof");
/* 1834 */       return;
/*      */     }
/*      */ 
/* 1838 */     if (!parseIdentifier(true)) {
/* 1839 */       localElement = this.recent;
/* 1840 */       if ((this.ch != 62) || (localElement == null))
/* 1841 */         error("expected.tagname");
/*      */     }
/*      */     else
/*      */     {
/* 1845 */       localObject = getString(0);
/*      */ 
/* 1847 */       if (((String)localObject).equals("image")) {
/* 1848 */         localObject = "img";
/*      */       }
/*      */ 
/* 1853 */       if (!this.dtd.elementExists((String)localObject))
/*      */       {
/* 1855 */         error("tag.unrecognized ", (String)localObject);
/* 1856 */         localElement = this.dtd.getElement("unknown");
/* 1857 */         localElement.name = ((String)localObject);
/* 1858 */         j = 1;
/*      */       } else {
/* 1860 */         localElement = this.dtd.getElement((String)localObject);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1865 */     parseAttributeSpecificationList(localElement);
/*      */ 
/* 1867 */     switch (this.ch) {
/*      */     case 47:
/* 1869 */       bool = true;
/*      */     case 62:
/* 1871 */       this.ch = readCh();
/* 1872 */       if ((this.ch == 62) && (bool)) {
/* 1873 */         this.ch = readCh();
/*      */       }
/*      */     case 60:
/* 1876 */       break;
/*      */     }
/*      */ 
/* 1879 */     error("expected", "'>'");
/*      */ 
/* 1883 */     if ((!this.strict) && 
/* 1884 */       (localElement.getName().equals("script"))) {
/* 1885 */       error("javascript.unsupported");
/*      */     }
/*      */ 
/* 1891 */     if (!localElement.isEmpty()) {
/* 1892 */       if (this.ch == 10) {
/* 1893 */         this.ln += 1;
/* 1894 */         this.lfCount += 1;
/* 1895 */         this.ch = readCh();
/* 1896 */       } else if (this.ch == 13) {
/* 1897 */         this.ln += 1;
/* 1898 */         if ((this.ch = readCh()) == 10) {
/* 1899 */           this.ch = readCh();
/* 1900 */           this.crlfCount += 1;
/*      */         }
/*      */         else {
/* 1903 */           this.crCount += 1;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1909 */     Object localObject = makeTag(localElement, false);
/*      */ 
/* 1932 */     if (j == 0) {
/* 1933 */       legalTagContext((TagElement)localObject);
/*      */ 
/* 1939 */       if ((!this.strict) && (this.skipTag)) {
/* 1940 */         this.skipTag = false;
/* 1941 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1948 */     startTag((TagElement)localObject);
/*      */ 
/* 1950 */     if (!localElement.isEmpty())
/* 1951 */       switch (localElement.getType()) {
/*      */       case 1:
/* 1953 */         parseLiteral(false);
/* 1954 */         break;
/*      */       case 16:
/* 1956 */         parseLiteral(true);
/* 1957 */         break;
/*      */       default:
/* 1959 */         if (this.stack != null)
/* 1960 */           this.stack.net = bool;
/*      */         break;
/*      */       }
/*      */   }
/*      */ 
/*      */   void parseScript()
/*      */     throws IOException
/*      */   {
/* 1974 */     char[] arrayOfChar = new char[SCRIPT_END_TAG.length];
/*      */     while (true)
/*      */     {
/* 1978 */       int i = 0;
/*      */ 
/* 1980 */       while ((i < SCRIPT_END_TAG.length) && ((SCRIPT_END_TAG[i] == this.ch) || (SCRIPT_END_TAG_UPPER_CASE[i] == this.ch)))
/*      */       {
/* 1982 */         arrayOfChar[i] = ((char)this.ch);
/* 1983 */         this.ch = readCh();
/* 1984 */         i++;
/*      */       }
/* 1986 */       if (i == SCRIPT_END_TAG.length)
/*      */       {
/* 1990 */         return;
/*      */       }
/*      */ 
/* 1994 */       for (int j = 0; j < i; j++) {
/* 1995 */         addString(arrayOfChar[j]);
/*      */       }
/*      */ 
/* 1998 */       switch (this.ch) {
/*      */       case -1:
/* 2000 */         error("eof.script");
/* 2001 */         return;
/*      */       case 10:
/* 2003 */         this.ln += 1;
/* 2004 */         this.ch = readCh();
/* 2005 */         this.lfCount += 1;
/* 2006 */         addString(10);
/* 2007 */         break;
/*      */       case 13:
/* 2009 */         this.ln += 1;
/* 2010 */         if ((this.ch = readCh()) == 10) {
/* 2011 */           this.ch = readCh();
/* 2012 */           this.crlfCount += 1;
/*      */         } else {
/* 2014 */           this.crCount += 1;
/*      */         }
/* 2016 */         addString(10);
/* 2017 */         break;
/*      */       default:
/* 2019 */         addString(this.ch);
/* 2020 */         this.ch = readCh();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void parseContent()
/*      */     throws IOException
/*      */   {
/* 2031 */     Thread localThread = Thread.currentThread();
/*      */     while (true)
/*      */     {
/* 2034 */       if (localThread.isInterrupted()) {
/* 2035 */         localThread.interrupt();
/* 2036 */         break;
/*      */       }
/*      */ 
/* 2039 */       int i = this.ch;
/* 2040 */       this.currentBlockStartPos = this.currentPosition;
/*      */       Object localObject;
/* 2042 */       if (this.recent == this.dtd.script)
/*      */       {
/* 2045 */         parseScript();
/* 2046 */         this.last = makeTag(this.dtd.getElement("comment"), true);
/*      */ 
/* 2049 */         localObject = new String(getChars(0)).trim();
/* 2050 */         int j = "<!--".length() + "-->".length();
/* 2051 */         if ((((String)localObject).startsWith("<!--")) && (((String)localObject).endsWith("-->")) && (((String)localObject).length() >= j))
/*      */         {
/* 2053 */           localObject = ((String)localObject).substring("<!--".length(), ((String)localObject).length() - "-->".length());
/*      */         }
/*      */ 
/* 2058 */         handleComment(((String)localObject).toCharArray());
/* 2059 */         endTag(false);
/* 2060 */         this.lastBlockStartPos = this.currentPosition;
/*      */       }
/*      */       else
/*      */       {
/* 2064 */         switch (i) {
/*      */         case 60:
/* 2066 */           parseTag();
/* 2067 */           this.lastBlockStartPos = this.currentPosition;
/* 2068 */           break;
/*      */         case 47:
/* 2071 */           this.ch = readCh();
/* 2072 */           if ((this.stack != null) && (this.stack.net))
/*      */           {
/* 2074 */             endTag(false);
/* 2075 */           }break;
/*      */         case -1:
/* 2080 */           return;
/*      */         case 38:
/* 2083 */           if (this.textpos == 0) {
/* 2084 */             if (!legalElementContext(this.dtd.pcdata)) {
/* 2085 */               error("unexpected.pcdata");
/*      */             }
/* 2087 */             if (this.last.breaksFlow()) {
/* 2088 */               this.space = false;
/*      */             }
/*      */           }
/* 2091 */           localObject = parseEntityReference();
/* 2092 */           if (this.textpos + localObject.length + 1 > this.text.length) {
/* 2093 */             char[] arrayOfChar = new char[Math.max(this.textpos + localObject.length + 128, this.text.length * 2)];
/* 2094 */             System.arraycopy(this.text, 0, arrayOfChar, 0, this.text.length);
/* 2095 */             this.text = arrayOfChar;
/*      */           }
/* 2097 */           if (this.space) {
/* 2098 */             this.space = false;
/* 2099 */             this.text[(this.textpos++)] = ' ';
/*      */           }
/* 2101 */           System.arraycopy(localObject, 0, this.text, this.textpos, localObject.length);
/* 2102 */           this.textpos += localObject.length;
/* 2103 */           this.ignoreSpace = false;
/* 2104 */           break;
/*      */         case 10:
/* 2107 */           this.ln += 1;
/* 2108 */           this.lfCount += 1;
/* 2109 */           this.ch = readCh();
/* 2110 */           if ((this.stack == null) || (!this.stack.pre))
/*      */           {
/* 2113 */             if (this.textpos == 0) {
/* 2114 */               this.lastBlockStartPos = this.currentPosition;
/*      */             }
/* 2116 */             if (this.ignoreSpace) continue;
/* 2117 */             this.space = true; } break;
/*      */         case 13:
/* 2122 */           this.ln += 1;
/* 2123 */           i = 10;
/* 2124 */           if ((this.ch = readCh()) == 10) {
/* 2125 */             this.ch = readCh();
/* 2126 */             this.crlfCount += 1;
/*      */           }
/*      */           else {
/* 2129 */             this.crCount += 1;
/*      */           }
/* 2131 */           if ((this.stack == null) || (!this.stack.pre))
/*      */           {
/* 2134 */             if (this.textpos == 0) {
/* 2135 */               this.lastBlockStartPos = this.currentPosition;
/*      */             }
/* 2137 */             if (this.ignoreSpace) continue;
/* 2138 */             this.space = true; } break;
/*      */         case 9:
/*      */         case 32:
/* 2145 */           this.ch = readCh();
/* 2146 */           if ((this.stack == null) || (!this.stack.pre))
/*      */           {
/* 2149 */             if (this.textpos == 0) {
/* 2150 */               this.lastBlockStartPos = this.currentPosition;
/*      */             }
/* 2152 */             if (this.ignoreSpace) continue;
/* 2153 */             this.space = true; } break;
/*      */         default:
/* 2158 */           if (this.textpos == 0) {
/* 2159 */             if (!legalElementContext(this.dtd.pcdata)) {
/* 2160 */               error("unexpected.pcdata");
/*      */             }
/* 2162 */             if (this.last.breaksFlow()) {
/* 2163 */               this.space = false;
/*      */             }
/*      */           }
/* 2166 */           this.ch = readCh();
/*      */ 
/* 2172 */           if (this.textpos + 2 > this.text.length) {
/* 2173 */             localObject = new char[this.text.length + 128];
/* 2174 */             System.arraycopy(this.text, 0, localObject, 0, this.text.length);
/* 2175 */             this.text = ((char[])localObject);
/*      */           }
/*      */ 
/* 2179 */           if (this.space) {
/* 2180 */             if (this.textpos == 0) {
/* 2181 */               this.lastBlockStartPos -= 1;
/*      */             }
/* 2183 */             this.text[(this.textpos++)] = ' ';
/* 2184 */             this.space = false;
/*      */           }
/* 2186 */           this.text[(this.textpos++)] = ((char)i);
/* 2187 */           this.ignoreSpace = false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   String getEndOfLineString()
/*      */   {
/* 2196 */     if (this.crlfCount >= this.crCount) {
/* 2197 */       if (this.lfCount >= this.crlfCount) {
/* 2198 */         return "\n";
/*      */       }
/*      */ 
/* 2201 */       return "\r\n";
/*      */     }
/*      */ 
/* 2205 */     if (this.crCount > this.lfCount) {
/* 2206 */       return "\r";
/*      */     }
/*      */ 
/* 2209 */     return "\n";
/*      */   }
/*      */ 
/*      */   public synchronized void parse(Reader paramReader)
/*      */     throws IOException
/*      */   {
/* 2218 */     this.in = paramReader;
/*      */ 
/* 2220 */     this.ln = 1;
/*      */ 
/* 2222 */     this.seenHtml = false;
/* 2223 */     this.seenHead = false;
/* 2224 */     this.seenBody = false;
/*      */ 
/* 2226 */     this.crCount = (this.lfCount = this.crlfCount = 0);
/*      */     try
/*      */     {
/* 2229 */       this.ch = readCh();
/* 2230 */       this.text = new char[1024];
/* 2231 */       this.str = new char[''];
/*      */ 
/* 2233 */       parseContent();
/*      */ 
/* 2236 */       while (this.stack != null) {
/* 2237 */         endTag(true);
/*      */       }
/* 2239 */       paramReader.close();
/*      */     } catch (IOException localIOException) {
/* 2241 */       errorContext();
/* 2242 */       error("ioexception");
/* 2243 */       throw localIOException;
/*      */     } catch (Exception localException) {
/* 2245 */       errorContext();
/* 2246 */       error("exception", localException.getClass().getName(), localException.getMessage());
/* 2247 */       localException.printStackTrace();
/*      */     } catch (ThreadDeath localThreadDeath) {
/* 2249 */       errorContext();
/* 2250 */       error("terminated");
/* 2251 */       localThreadDeath.printStackTrace();
/* 2252 */       throw localThreadDeath;
/*      */     } finally {
/* 2254 */       for (; this.stack != null; this.stack = this.stack.next) {
/* 2255 */         handleEndTag(this.stack.tag);
/*      */       }
/*      */ 
/* 2258 */       this.text = null;
/* 2259 */       this.str = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private final int readCh()
/*      */     throws IOException
/*      */   {
/* 2288 */     if (this.pos >= this.len)
/*      */     {
/*      */       try
/*      */       {
/* 2294 */         this.len = this.in.read(this.buf);
/*      */       }
/*      */       catch (InterruptedIOException localInterruptedIOException) {
/* 2297 */         throw localInterruptedIOException;
/*      */       }
/*      */ 
/* 2301 */       if (this.len <= 0) {
/* 2302 */         return -1;
/*      */       }
/* 2304 */       this.pos = 0;
/*      */     }
/* 2306 */     this.currentPosition += 1;
/*      */ 
/* 2308 */     return this.buf[(this.pos++)];
/*      */   }
/*      */ 
/*      */   protected int getCurrentPos()
/*      */   {
/* 2313 */     return this.currentPosition;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.text.html.parser.Parser
 * JD-Core Version:    0.6.2
 */