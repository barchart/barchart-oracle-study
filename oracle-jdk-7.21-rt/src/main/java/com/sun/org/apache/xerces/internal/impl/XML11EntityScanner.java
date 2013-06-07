/*      */ package com.sun.org.apache.xerces.internal.impl;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.XML11Char;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
/*      */ import com.sun.org.apache.xerces.internal.xni.QName;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLString;
/*      */ import com.sun.xml.internal.stream.Entity.ScannedEntity;
/*      */ import java.io.IOException;
/*      */ 
/*      */ public class XML11EntityScanner extends XMLEntityScanner
/*      */ {
/*      */   public int peekChar()
/*      */     throws IOException
/*      */   {
/*  110 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  111 */       load(0, true);
/*      */     }
/*      */ 
/*  115 */     int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*      */ 
/*  118 */     if (this.fCurrentEntity.isExternal()) {
/*  119 */       return (c != 13) && (c != 133) && (c != 8232) ? c : 10;
/*      */     }
/*      */ 
/*  122 */     return c;
/*      */   }
/*      */ 
/*      */   public int scanChar()
/*      */     throws IOException
/*      */   {
/*  138 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  139 */       load(0, true);
/*      */     }
/*      */ 
/*  143 */     int c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/*  144 */     boolean external = false;
/*  145 */     if ((c == 10) || (((c == 13) || (c == 133) || (c == 8232)) && ((external = this.fCurrentEntity.isExternal()))))
/*      */     {
/*  147 */       this.fCurrentEntity.lineNumber += 1;
/*  148 */       this.fCurrentEntity.columnNumber = 1;
/*  149 */       if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  150 */         this.fCurrentEntity.ch[0] = ((char)c);
/*  151 */         load(1, false);
/*      */       }
/*  153 */       if ((c == 13) && (external)) {
/*  154 */         int cc = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/*  155 */         if ((cc != 10) && (cc != 133)) {
/*  156 */           this.fCurrentEntity.position -= 1;
/*      */         }
/*      */       }
/*  159 */       c = 10;
/*      */     }
/*      */ 
/*  163 */     this.fCurrentEntity.columnNumber += 1;
/*  164 */     return c;
/*      */   }
/*      */ 
/*      */   public String scanNmtoken()
/*      */     throws IOException
/*      */   {
/*  185 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  186 */       load(0, true);
/*      */     }
/*      */ 
/*  190 */     int offset = this.fCurrentEntity.position;
/*      */     while (true)
/*      */     {
/*  193 */       char ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  194 */       if (XML11Char.isXML11Name(ch)) {
/*  195 */         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  196 */           int length = this.fCurrentEntity.position - offset;
/*  197 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  199 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  200 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  202 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  205 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  208 */           offset = 0;
/*  209 */           if (load(length, false))
/*      */             break;
/*      */         }
/*      */       }
/*      */       else {
/*  214 */         if (!XML11Char.isXML11NameHighSurrogate(ch)) break;
/*  215 */         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  216 */           int length = this.fCurrentEntity.position - offset;
/*  217 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  219 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  220 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  222 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  225 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  228 */           offset = 0;
/*  229 */           if (load(length, false)) {
/*  230 */             this.fCurrentEntity.startPosition -= 1;
/*  231 */             this.fCurrentEntity.position -= 1;
/*  232 */             break;
/*      */           }
/*      */         }
/*  235 */         char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  236 */         if ((!XMLChar.isLowSurrogate(ch2)) || (!XML11Char.isXML11Name(XMLChar.supplemental(ch, ch2))))
/*      */         {
/*  238 */           this.fCurrentEntity.position -= 1;
/*      */         }
/*  241 */         else if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  242 */           int length = this.fCurrentEntity.position - offset;
/*  243 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  245 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  246 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  248 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  251 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  254 */           offset = 0;
/*  255 */           if (load(length, false))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  266 */     int length = this.fCurrentEntity.position - offset;
/*  267 */     this.fCurrentEntity.columnNumber += length;
/*      */ 
/*  270 */     String symbol = null;
/*  271 */     if (length > 0) {
/*  272 */       symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
/*      */     }
/*  274 */     return symbol;
/*      */   }
/*      */ 
/*      */   public String scanName()
/*      */     throws IOException
/*      */   {
/*  296 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  297 */       load(0, true);
/*      */     }
/*      */ 
/*  301 */     int offset = this.fCurrentEntity.position;
/*  302 */     char ch = this.fCurrentEntity.ch[offset];
/*      */ 
/*  304 */     if (XML11Char.isXML11NameStart(ch)) {
/*  305 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  306 */         this.fCurrentEntity.ch[0] = ch;
/*  307 */         offset = 0;
/*  308 */         if (load(1, false)) {
/*  309 */           this.fCurrentEntity.columnNumber += 1;
/*  310 */           String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
/*  311 */           return symbol;
/*      */         }
/*      */       }
/*      */     }
/*  315 */     else if (XML11Char.isXML11NameHighSurrogate(ch)) {
/*  316 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  317 */         this.fCurrentEntity.ch[0] = ch;
/*  318 */         offset = 0;
/*  319 */         if (load(1, false)) {
/*  320 */           this.fCurrentEntity.position -= 1;
/*  321 */           this.fCurrentEntity.startPosition -= 1;
/*  322 */           return null;
/*      */         }
/*      */       }
/*  325 */       char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  326 */       if ((!XMLChar.isLowSurrogate(ch2)) || (!XML11Char.isXML11NameStart(XMLChar.supplemental(ch, ch2))))
/*      */       {
/*  328 */         this.fCurrentEntity.position -= 1;
/*  329 */         return null;
/*      */       }
/*  331 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  332 */         this.fCurrentEntity.ch[0] = ch;
/*  333 */         this.fCurrentEntity.ch[1] = ch2;
/*  334 */         offset = 0;
/*  335 */         if (load(2, false)) {
/*  336 */           this.fCurrentEntity.columnNumber += 2;
/*  337 */           String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
/*  338 */           return symbol;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  343 */       return null;
/*      */     }
/*      */     while (true)
/*      */     {
/*  347 */       ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  348 */       if (XML11Char.isXML11Name(ch)) {
/*  349 */         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  350 */           int length = this.fCurrentEntity.position - offset;
/*  351 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  353 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  354 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  356 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  359 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  362 */           offset = 0;
/*  363 */           if (load(length, false))
/*      */             break;
/*      */         }
/*      */       }
/*      */       else {
/*  368 */         if (!XML11Char.isXML11NameHighSurrogate(ch)) break;
/*  369 */         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  370 */           int length = this.fCurrentEntity.position - offset;
/*  371 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  373 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  374 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  376 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  379 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  382 */           offset = 0;
/*  383 */           if (load(length, false)) {
/*  384 */             this.fCurrentEntity.position -= 1;
/*  385 */             this.fCurrentEntity.startPosition -= 1;
/*  386 */             break;
/*      */           }
/*      */         }
/*  389 */         char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  390 */         if ((!XMLChar.isLowSurrogate(ch2)) || (!XML11Char.isXML11Name(XMLChar.supplemental(ch, ch2))))
/*      */         {
/*  392 */           this.fCurrentEntity.position -= 1;
/*      */         }
/*  395 */         else if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  396 */           int length = this.fCurrentEntity.position - offset;
/*  397 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  399 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  400 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  402 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  405 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  408 */           offset = 0;
/*  409 */           if (load(length, false))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  420 */     int length = this.fCurrentEntity.position - offset;
/*  421 */     this.fCurrentEntity.columnNumber += length;
/*      */ 
/*  424 */     String symbol = null;
/*  425 */     if (length > 0) {
/*  426 */       symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
/*      */     }
/*  428 */     return symbol;
/*      */   }
/*      */ 
/*      */   public String scanNCName()
/*      */     throws IOException
/*      */   {
/*  451 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  452 */       load(0, true);
/*      */     }
/*      */ 
/*  456 */     int offset = this.fCurrentEntity.position;
/*  457 */     char ch = this.fCurrentEntity.ch[offset];
/*      */ 
/*  459 */     if (XML11Char.isXML11NCNameStart(ch)) {
/*  460 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  461 */         this.fCurrentEntity.ch[0] = ch;
/*  462 */         offset = 0;
/*  463 */         if (load(1, false)) {
/*  464 */           this.fCurrentEntity.columnNumber += 1;
/*  465 */           String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
/*  466 */           return symbol;
/*      */         }
/*      */       }
/*      */     }
/*  470 */     else if (XML11Char.isXML11NameHighSurrogate(ch)) {
/*  471 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  472 */         this.fCurrentEntity.ch[0] = ch;
/*  473 */         offset = 0;
/*  474 */         if (load(1, false)) {
/*  475 */           this.fCurrentEntity.position -= 1;
/*  476 */           this.fCurrentEntity.startPosition -= 1;
/*  477 */           return null;
/*      */         }
/*      */       }
/*  480 */       char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  481 */       if ((!XMLChar.isLowSurrogate(ch2)) || (!XML11Char.isXML11NCNameStart(XMLChar.supplemental(ch, ch2))))
/*      */       {
/*  483 */         this.fCurrentEntity.position -= 1;
/*  484 */         return null;
/*      */       }
/*  486 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  487 */         this.fCurrentEntity.ch[0] = ch;
/*  488 */         this.fCurrentEntity.ch[1] = ch2;
/*  489 */         offset = 0;
/*  490 */         if (load(2, false)) {
/*  491 */           this.fCurrentEntity.columnNumber += 2;
/*  492 */           String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
/*  493 */           return symbol;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  498 */       return null;
/*      */     }
/*      */     while (true)
/*      */     {
/*  502 */       ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  503 */       if (XML11Char.isXML11NCName(ch)) {
/*  504 */         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  505 */           int length = this.fCurrentEntity.position - offset;
/*  506 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  508 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  509 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  511 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  514 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  517 */           offset = 0;
/*  518 */           if (load(length, false))
/*      */             break;
/*      */         }
/*      */       }
/*      */       else {
/*  523 */         if (!XML11Char.isXML11NameHighSurrogate(ch)) break;
/*  524 */         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  525 */           int length = this.fCurrentEntity.position - offset;
/*  526 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  528 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  529 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  531 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  534 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  537 */           offset = 0;
/*  538 */           if (load(length, false)) {
/*  539 */             this.fCurrentEntity.startPosition -= 1;
/*  540 */             this.fCurrentEntity.position -= 1;
/*  541 */             break;
/*      */           }
/*      */         }
/*  544 */         char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  545 */         if ((!XMLChar.isLowSurrogate(ch2)) || (!XML11Char.isXML11NCName(XMLChar.supplemental(ch, ch2))))
/*      */         {
/*  547 */           this.fCurrentEntity.position -= 1;
/*      */         }
/*  550 */         else if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  551 */           int length = this.fCurrentEntity.position - offset;
/*  552 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  554 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  555 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  557 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  560 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  563 */           offset = 0;
/*  564 */           if (load(length, false))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  575 */     int length = this.fCurrentEntity.position - offset;
/*  576 */     this.fCurrentEntity.columnNumber += length;
/*      */ 
/*  579 */     String symbol = null;
/*  580 */     if (length > 0) {
/*  581 */       symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
/*      */     }
/*  583 */     return symbol;
/*      */   }
/*      */ 
/*      */   public boolean scanQName(QName qname)
/*      */     throws IOException
/*      */   {
/*  612 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  613 */       load(0, true);
/*      */     }
/*      */ 
/*  617 */     int offset = this.fCurrentEntity.position;
/*  618 */     char ch = this.fCurrentEntity.ch[offset];
/*      */ 
/*  620 */     if (XML11Char.isXML11NCNameStart(ch)) {
/*  621 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  622 */         this.fCurrentEntity.ch[0] = ch;
/*  623 */         offset = 0;
/*  624 */         if (load(1, false)) {
/*  625 */           this.fCurrentEntity.columnNumber += 1;
/*  626 */           String name = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
/*  627 */           qname.setValues(null, name, name, null);
/*  628 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  632 */     else if (XML11Char.isXML11NameHighSurrogate(ch)) {
/*  633 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  634 */         this.fCurrentEntity.ch[0] = ch;
/*  635 */         offset = 0;
/*  636 */         if (load(1, false)) {
/*  637 */           this.fCurrentEntity.startPosition -= 1;
/*  638 */           this.fCurrentEntity.position -= 1;
/*  639 */           return false;
/*      */         }
/*      */       }
/*  642 */       char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  643 */       if ((!XMLChar.isLowSurrogate(ch2)) || (!XML11Char.isXML11NCNameStart(XMLChar.supplemental(ch, ch2))))
/*      */       {
/*  645 */         this.fCurrentEntity.position -= 1;
/*  646 */         return false;
/*      */       }
/*  648 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  649 */         this.fCurrentEntity.ch[0] = ch;
/*  650 */         this.fCurrentEntity.ch[1] = ch2;
/*  651 */         offset = 0;
/*  652 */         if (load(2, false)) {
/*  653 */           this.fCurrentEntity.columnNumber += 2;
/*  654 */           String name = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
/*  655 */           qname.setValues(null, name, name, null);
/*  656 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  661 */       return false;
/*      */     }
/*      */ 
/*  664 */     int index = -1;
/*  665 */     boolean sawIncompleteSurrogatePair = false;
/*      */     while (true) {
/*  667 */       ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  668 */       if (XML11Char.isXML11Name(ch)) {
/*  669 */         if (ch == ':') {
/*  670 */           if (index == -1)
/*      */           {
/*  673 */             index = this.fCurrentEntity.position;
/*      */           }
/*      */         } else { if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
/*  676 */           int length = this.fCurrentEntity.position - offset;
/*  677 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  679 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  680 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  682 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  685 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  688 */           if (index != -1) {
/*  689 */             index -= offset;
/*      */           }
/*  691 */           offset = 0;
/*  692 */           if (load(length, false))
/*      */             break; }
/*      */       }
/*      */       else
/*      */       {
/*  697 */         if (!XML11Char.isXML11NameHighSurrogate(ch)) break;
/*  698 */         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  699 */           int length = this.fCurrentEntity.position - offset;
/*  700 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  702 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  703 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  705 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  708 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  711 */           if (index != -1) {
/*  712 */             index -= offset;
/*      */           }
/*  714 */           offset = 0;
/*  715 */           if (load(length, false)) {
/*  716 */             sawIncompleteSurrogatePair = true;
/*  717 */             this.fCurrentEntity.startPosition -= 1;
/*  718 */             this.fCurrentEntity.position -= 1;
/*  719 */             break;
/*      */           }
/*      */         }
/*  722 */         char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  723 */         if ((!XMLChar.isLowSurrogate(ch2)) || (!XML11Char.isXML11Name(XMLChar.supplemental(ch, ch2))))
/*      */         {
/*  725 */           sawIncompleteSurrogatePair = true;
/*  726 */           this.fCurrentEntity.position -= 1;
/*      */         }
/*  729 */         else if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  730 */           int length = this.fCurrentEntity.position - offset;
/*  731 */           if (length == this.fCurrentEntity.ch.length)
/*      */           {
/*  733 */             char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
/*  734 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  736 */             this.fCurrentEntity.ch = tmp;
/*      */           }
/*      */           else {
/*  739 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  742 */           if (index != -1) {
/*  743 */             index -= offset;
/*      */           }
/*  745 */           offset = 0;
/*  746 */           if (load(length, false))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  757 */     int length = this.fCurrentEntity.position - offset;
/*  758 */     this.fCurrentEntity.columnNumber += length;
/*      */ 
/*  760 */     if (length > 0) {
/*  761 */       String prefix = null;
/*  762 */       String localpart = null;
/*  763 */       String rawname = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
/*      */ 
/*  765 */       if (index != -1) {
/*  766 */         int prefixLength = index - offset;
/*  767 */         prefix = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, prefixLength);
/*      */ 
/*  769 */         int len = length - prefixLength - 1;
/*  770 */         int startLocal = index + 1;
/*  771 */         if ((!XML11Char.isXML11NCNameStart(this.fCurrentEntity.ch[startLocal])) && ((!XML11Char.isXML11NameHighSurrogate(this.fCurrentEntity.ch[startLocal])) || (sawIncompleteSurrogatePair)))
/*      */         {
/*  774 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName", null, (short)2);
/*      */         }
/*      */ 
/*  779 */         localpart = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, index + 1, len);
/*      */       }
/*      */       else
/*      */       {
/*  784 */         localpart = rawname;
/*      */       }
/*  786 */       qname.setValues(prefix, localpart, rawname, null);
/*  787 */       return true;
/*      */     }
/*  789 */     return false;
/*      */   }
/*      */ 
/*      */   public int scanContent(XMLString content)
/*      */     throws IOException
/*      */   {
/*  822 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  823 */       load(0, true);
/*      */     }
/*  825 */     else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/*  826 */       this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[(this.fCurrentEntity.count - 1)];
/*  827 */       load(1, false);
/*  828 */       this.fCurrentEntity.position = 0;
/*  829 */       this.fCurrentEntity.startPosition = 0;
/*      */     }
/*      */ 
/*  833 */     int offset = this.fCurrentEntity.position;
/*  834 */     int c = this.fCurrentEntity.ch[offset];
/*  835 */     int newlines = 0;
/*  836 */     boolean external = this.fCurrentEntity.isExternal();
/*  837 */     if ((c == 10) || (((c == 13) || (c == 133) || (c == 8232)) && (external))) {
/*      */       do {
/*  839 */         c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/*  840 */         if ((c == 13) && (external)) {
/*  841 */           newlines++;
/*  842 */           this.fCurrentEntity.lineNumber += 1;
/*  843 */           this.fCurrentEntity.columnNumber = 1;
/*  844 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  845 */             offset = 0;
/*  846 */             this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
/*  847 */             this.fCurrentEntity.position = newlines;
/*  848 */             this.fCurrentEntity.startPosition = newlines;
/*  849 */             if (load(newlines, false)) {
/*      */               break;
/*      */             }
/*      */           }
/*  853 */           int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  854 */           if ((cc == 10) || (cc == 133)) {
/*  855 */             this.fCurrentEntity.position += 1;
/*  856 */             offset++;
/*      */           }
/*      */           else
/*      */           {
/*  860 */             newlines++;
/*      */           }
/*      */         }
/*  863 */         else if ((c == 10) || (((c == 133) || (c == 8232)) && (external))) {
/*  864 */           newlines++;
/*  865 */           this.fCurrentEntity.lineNumber += 1;
/*  866 */           this.fCurrentEntity.columnNumber = 1;
/*  867 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  868 */             offset = 0;
/*  869 */             this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
/*  870 */             this.fCurrentEntity.position = newlines;
/*  871 */             this.fCurrentEntity.startPosition = newlines;
/*  872 */             if (load(newlines, false))
/*  873 */               break;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  878 */           this.fCurrentEntity.position -= 1;
/*  879 */           break;
/*      */         }
/*      */       }
/*  881 */       while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
/*  882 */       for (int i = offset; i < this.fCurrentEntity.position; i++) {
/*  883 */         this.fCurrentEntity.ch[i] = '\n';
/*      */       }
/*  885 */       int length = this.fCurrentEntity.position - offset;
/*  886 */       if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/*  887 */         content.setValues(this.fCurrentEntity.ch, offset, length);
/*  888 */         return -1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  893 */     if (external) {
/*      */       do { if (this.fCurrentEntity.position >= this.fCurrentEntity.count) break;
/*  895 */         c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)]; }
/*  896 */       while ((XML11Char.isXML11Content(c)) && (c != 133) && (c != 8232));
/*  897 */       this.fCurrentEntity.position -= 1;
/*      */     }
/*      */     else
/*      */     {
/*  903 */       while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
/*  904 */         c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/*      */ 
/*  906 */         if (!XML11Char.isXML11InternalEntityContent(c)) {
/*  907 */           this.fCurrentEntity.position -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  912 */     int length = this.fCurrentEntity.position - offset;
/*  913 */     this.fCurrentEntity.columnNumber += length - newlines;
/*  914 */     content.setValues(this.fCurrentEntity.ch, offset, length);
/*      */ 
/*  917 */     if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
/*  918 */       c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*      */ 
/*  921 */       if (((c == 13) || (c == 133) || (c == 8232)) && (external))
/*  922 */         c = 10;
/*      */     }
/*      */     else
/*      */     {
/*  926 */       c = -1;
/*      */     }
/*  928 */     return c;
/*      */   }
/*      */ 
/*      */   public int scanLiteral(int quote, XMLString content)
/*      */     throws IOException
/*      */   {
/*  963 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  964 */       load(0, true);
/*      */     }
/*  966 */     else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/*  967 */       this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[(this.fCurrentEntity.count - 1)];
/*  968 */       load(1, false);
/*  969 */       this.fCurrentEntity.startPosition = 0;
/*  970 */       this.fCurrentEntity.position = 0;
/*      */     }
/*      */ 
/*  974 */     int offset = this.fCurrentEntity.position;
/*  975 */     int c = this.fCurrentEntity.ch[offset];
/*  976 */     int newlines = 0;
/*  977 */     boolean external = this.fCurrentEntity.isExternal();
/*  978 */     if ((c == 10) || (((c == 13) || (c == 133) || (c == 8232)) && (external))) {
/*      */       do {
/*  980 */         c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/*  981 */         if ((c == 13) && (external)) {
/*  982 */           newlines++;
/*  983 */           this.fCurrentEntity.lineNumber += 1;
/*  984 */           this.fCurrentEntity.columnNumber = 1;
/*  985 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  986 */             offset = 0;
/*  987 */             this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
/*  988 */             this.fCurrentEntity.position = newlines;
/*  989 */             this.fCurrentEntity.startPosition = newlines;
/*  990 */             if (load(newlines, false)) {
/*      */               break;
/*      */             }
/*      */           }
/*  994 */           int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  995 */           if ((cc == 10) || (cc == 133)) {
/*  996 */             this.fCurrentEntity.position += 1;
/*  997 */             offset++;
/*      */           }
/*      */           else
/*      */           {
/* 1001 */             newlines++;
/*      */           }
/*      */         }
/* 1004 */         else if ((c == 10) || (((c == 133) || (c == 8232)) && (external))) {
/* 1005 */           newlines++;
/* 1006 */           this.fCurrentEntity.lineNumber += 1;
/* 1007 */           this.fCurrentEntity.columnNumber = 1;
/* 1008 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1009 */             offset = 0;
/* 1010 */             this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
/* 1011 */             this.fCurrentEntity.position = newlines;
/* 1012 */             this.fCurrentEntity.startPosition = newlines;
/* 1013 */             if (load(newlines, false))
/* 1014 */               break;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1019 */           this.fCurrentEntity.position -= 1;
/* 1020 */           break;
/*      */         }
/*      */       }
/* 1022 */       while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
/* 1023 */       for (int i = offset; i < this.fCurrentEntity.position; i++) {
/* 1024 */         this.fCurrentEntity.ch[i] = '\n';
/*      */       }
/* 1026 */       int length = this.fCurrentEntity.position - offset;
/* 1027 */       if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/* 1028 */         content.setValues(this.fCurrentEntity.ch, offset, length);
/* 1029 */         return -1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1034 */     if (external) {
/*      */       do { if (this.fCurrentEntity.position >= this.fCurrentEntity.count) break;
/* 1036 */         c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)]; }
/* 1037 */       while ((c != quote) && (c != 37) && (XML11Char.isXML11Content(c)) && (c != 133) && (c != 8232));
/*      */ 
/* 1039 */       this.fCurrentEntity.position -= 1;
/*      */     }
/*      */     else
/*      */     {
/* 1045 */       while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
/* 1046 */         c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/*      */ 
/* 1048 */         if (((c == quote) && (!this.fCurrentEntity.literal)) || (c == 37) || (!XML11Char.isXML11InternalEntityContent(c)))
/*      */         {
/* 1050 */           this.fCurrentEntity.position -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1055 */     int length = this.fCurrentEntity.position - offset;
/* 1056 */     this.fCurrentEntity.columnNumber += length - newlines;
/* 1057 */     content.setValues(this.fCurrentEntity.ch, offset, length);
/*      */ 
/* 1060 */     if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
/* 1061 */       c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*      */ 
/* 1065 */       if ((c == quote) && (this.fCurrentEntity.literal))
/* 1066 */         c = -1;
/*      */     }
/*      */     else
/*      */     {
/* 1070 */       c = -1;
/*      */     }
/* 1072 */     return c;
/*      */   }
/*      */ 
/*      */   public boolean scanData(String delimiter, XMLStringBuffer buffer)
/*      */     throws IOException
/*      */   {
/* 1109 */     boolean done = false;
/* 1110 */     int delimLen = delimiter.length();
/* 1111 */     char charAt0 = delimiter.charAt(0);
/* 1112 */     boolean external = this.fCurrentEntity.isExternal();
/*      */     label1381: 
/*      */     do
/*      */     {
/* 1115 */       if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1116 */         load(0, true);
/*      */       }
/*      */ 
/* 1119 */       boolean bNextEntity = false;
/*      */ 
/* 1122 */       while ((this.fCurrentEntity.position >= this.fCurrentEntity.count - delimLen) && (!bNextEntity))
/*      */       {
/* 1124 */         System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
/*      */ 
/* 1130 */         bNextEntity = load(this.fCurrentEntity.count - this.fCurrentEntity.position, false);
/* 1131 */         this.fCurrentEntity.position = 0;
/* 1132 */         this.fCurrentEntity.startPosition = 0;
/*      */       }
/*      */ 
/* 1135 */       if (this.fCurrentEntity.position >= this.fCurrentEntity.count - delimLen)
/*      */       {
/* 1137 */         int length = this.fCurrentEntity.count - this.fCurrentEntity.position;
/* 1138 */         buffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, length);
/* 1139 */         this.fCurrentEntity.columnNumber += this.fCurrentEntity.count;
/* 1140 */         this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
/* 1141 */         this.fCurrentEntity.position = this.fCurrentEntity.count;
/* 1142 */         this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
/* 1143 */         load(0, true);
/* 1144 */         return false;
/*      */       }
/*      */ 
/* 1148 */       int offset = this.fCurrentEntity.position;
/* 1149 */       int c = this.fCurrentEntity.ch[offset];
/* 1150 */       int newlines = 0;
/* 1151 */       if ((c == 10) || (((c == 13) || (c == 133) || (c == 8232)) && (external))) {
/*      */         do {
/* 1153 */           c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1154 */           if ((c == 13) && (external)) {
/* 1155 */             newlines++;
/* 1156 */             this.fCurrentEntity.lineNumber += 1;
/* 1157 */             this.fCurrentEntity.columnNumber = 1;
/* 1158 */             if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1159 */               offset = 0;
/* 1160 */               this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
/* 1161 */               this.fCurrentEntity.position = newlines;
/* 1162 */               this.fCurrentEntity.startPosition = newlines;
/* 1163 */               if (load(newlines, false)) {
/*      */                 break;
/*      */               }
/*      */             }
/* 1167 */             int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/* 1168 */             if ((cc == 10) || (cc == 133)) {
/* 1169 */               this.fCurrentEntity.position += 1;
/* 1170 */               offset++;
/*      */             }
/*      */             else
/*      */             {
/* 1174 */               newlines++;
/*      */             }
/*      */           }
/* 1177 */           else if ((c == 10) || (((c == 133) || (c == 8232)) && (external))) {
/* 1178 */             newlines++;
/* 1179 */             this.fCurrentEntity.lineNumber += 1;
/* 1180 */             this.fCurrentEntity.columnNumber = 1;
/* 1181 */             if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1182 */               offset = 0;
/* 1183 */               this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
/* 1184 */               this.fCurrentEntity.position = newlines;
/* 1185 */               this.fCurrentEntity.startPosition = newlines;
/* 1186 */               this.fCurrentEntity.count = newlines;
/* 1187 */               if (load(newlines, false))
/* 1188 */                 break;
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1193 */             this.fCurrentEntity.position -= 1;
/* 1194 */             break;
/*      */           }
/*      */         }
/* 1196 */         while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
/* 1197 */         for (int i = offset; i < this.fCurrentEntity.position; i++) {
/* 1198 */           this.fCurrentEntity.ch[i] = '\n';
/*      */         }
/* 1200 */         int length = this.fCurrentEntity.position - offset;
/* 1201 */         if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/* 1202 */           buffer.append(this.fCurrentEntity.ch, offset, length);
/* 1203 */           return true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1208 */       if (external) {
/*      */         do { while (true) { if (this.fCurrentEntity.position >= this.fCurrentEntity.count) break label1381;
/* 1210 */             c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1211 */             if (c != charAt0)
/*      */               break;
/* 1213 */             int delimOffset = this.fCurrentEntity.position - 1;
/* 1214 */             for (int i = 1; i < delimLen; i++) {
/* 1215 */               if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1216 */                 this.fCurrentEntity.position -= i;
/* 1217 */                 break label1381;
/*      */               }
/* 1219 */               c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1220 */               if (delimiter.charAt(i) != c) {
/* 1221 */                 this.fCurrentEntity.position -= 1;
/* 1222 */                 break;
/*      */               }
/*      */             }
/* 1225 */             if (this.fCurrentEntity.position == delimOffset + delimLen) {
/* 1226 */               done = true;
/* 1227 */               break label1381;
/*      */             }
/*      */           }
/* 1230 */           if ((c == 10) || (c == 13) || (c == 133) || (c == 8232)) {
/* 1231 */             this.fCurrentEntity.position -= 1;
/* 1232 */             break;
/*      */           }
/*      */         }
/*      */ 
/* 1236 */         while (XML11Char.isXML11ValidLiteral(c));
/* 1237 */         this.fCurrentEntity.position -= 1;
/* 1238 */         int length = this.fCurrentEntity.position - offset;
/* 1239 */         this.fCurrentEntity.columnNumber += length - newlines;
/* 1240 */         buffer.append(this.fCurrentEntity.ch, offset, length);
/* 1241 */         return true;
/*      */       }
/*      */ 
/* 1246 */       while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
/* 1247 */         c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1248 */         if (c == charAt0)
/*      */         {
/* 1250 */           int delimOffset = this.fCurrentEntity.position - 1;
/* 1251 */           for (int i = 1; i < delimLen; i++) {
/* 1252 */             if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1253 */               this.fCurrentEntity.position -= i;
/* 1254 */               break label1381;
/*      */             }
/* 1256 */             c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1257 */             if (delimiter.charAt(i) != c) {
/* 1258 */               this.fCurrentEntity.position -= 1;
/* 1259 */               break;
/*      */             }
/*      */           }
/* 1262 */           if (this.fCurrentEntity.position == delimOffset + delimLen) {
/* 1263 */             done = true;
/*      */           }
/*      */ 
/*      */         }
/* 1267 */         else if (c == 10) {
/* 1268 */           this.fCurrentEntity.position -= 1;
/*      */         }
/* 1273 */         else if (!XML11Char.isXML11Valid(c)) {
/* 1274 */           this.fCurrentEntity.position -= 1;
/* 1275 */           int length = this.fCurrentEntity.position - offset;
/* 1276 */           this.fCurrentEntity.columnNumber += length - newlines;
/* 1277 */           buffer.append(this.fCurrentEntity.ch, offset, length);
/* 1278 */           return true;
/*      */         }
/*      */       }
/*      */ 
/* 1282 */       int length = this.fCurrentEntity.position - offset;
/* 1283 */       this.fCurrentEntity.columnNumber += length - newlines;
/* 1284 */       if (done) {
/* 1285 */         length -= delimLen;
/*      */       }
/* 1287 */       buffer.append(this.fCurrentEntity.ch, offset, length);
/*      */     }
/*      */ 
/* 1290 */     while (!done);
/* 1291 */     return !done;
/*      */   }
/*      */ 
/*      */   public boolean skipChar(int c)
/*      */     throws IOException
/*      */   {
/* 1311 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1312 */       load(0, true);
/*      */     }
/*      */ 
/* 1316 */     int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/* 1317 */     if (cc == c) {
/* 1318 */       this.fCurrentEntity.position += 1;
/* 1319 */       if (c == 10) {
/* 1320 */         this.fCurrentEntity.lineNumber += 1;
/* 1321 */         this.fCurrentEntity.columnNumber = 1;
/*      */       }
/*      */       else {
/* 1324 */         this.fCurrentEntity.columnNumber += 1;
/*      */       }
/* 1326 */       return true;
/*      */     }
/* 1328 */     if ((c == 10) && ((cc == 8232) || (cc == 133)) && (this.fCurrentEntity.isExternal())) {
/* 1329 */       this.fCurrentEntity.position += 1;
/* 1330 */       this.fCurrentEntity.lineNumber += 1;
/* 1331 */       this.fCurrentEntity.columnNumber = 1;
/* 1332 */       return true;
/*      */     }
/* 1334 */     if ((c == 10) && (cc == 13) && (this.fCurrentEntity.isExternal()))
/*      */     {
/* 1336 */       if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1337 */         this.fCurrentEntity.ch[0] = ((char)cc);
/* 1338 */         load(1, false);
/*      */       }
/* 1340 */       int ccc = this.fCurrentEntity.ch[(++this.fCurrentEntity.position)];
/* 1341 */       if ((ccc == 10) || (ccc == 133)) {
/* 1342 */         this.fCurrentEntity.position += 1;
/*      */       }
/* 1344 */       this.fCurrentEntity.lineNumber += 1;
/* 1345 */       this.fCurrentEntity.columnNumber = 1;
/* 1346 */       return true;
/*      */     }
/*      */ 
/* 1350 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean skipSpaces()
/*      */     throws IOException
/*      */   {
/* 1371 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1372 */       load(0, true);
/*      */     }
/*      */ 
/* 1382 */     if (this.fCurrentEntity == null) {
/* 1383 */       return false;
/*      */     }
/*      */ 
/* 1387 */     int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*      */ 
/* 1390 */     if (this.fCurrentEntity.isExternal()) {
/* 1391 */       if (XML11Char.isXML11Space(c)) {
/*      */         do {
/* 1393 */           boolean entityChanged = false;
/*      */ 
/* 1395 */           if ((c == 10) || (c == 13) || (c == 133) || (c == 8232)) {
/* 1396 */             this.fCurrentEntity.lineNumber += 1;
/* 1397 */             this.fCurrentEntity.columnNumber = 1;
/* 1398 */             if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/* 1399 */               this.fCurrentEntity.ch[0] = ((char)c);
/* 1400 */               entityChanged = load(1, true);
/* 1401 */               if (!entityChanged)
/*      */               {
/* 1404 */                 this.fCurrentEntity.startPosition = 0;
/* 1405 */                 this.fCurrentEntity.position = 0;
/* 1406 */               } else if (this.fCurrentEntity == null) {
/* 1407 */                 return true;
/*      */               }
/*      */             }
/*      */ 
/* 1411 */             if (c == 13)
/*      */             {
/* 1414 */               int cc = this.fCurrentEntity.ch[(++this.fCurrentEntity.position)];
/* 1415 */               if ((cc != 10) && (cc != 133))
/* 1416 */                 this.fCurrentEntity.position -= 1;
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1421 */             this.fCurrentEntity.columnNumber += 1;
/*      */           }
/*      */ 
/* 1424 */           if (!entityChanged)
/* 1425 */             this.fCurrentEntity.position += 1;
/* 1426 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1427 */             load(0, true);
/*      */ 
/* 1429 */             if (this.fCurrentEntity == null) {
/* 1430 */               return true;
/*      */             }
/*      */           }
/*      */         }
/* 1434 */         while (XML11Char.isXML11Space(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
/* 1435 */         return true;
/*      */       }
/*      */ 
/*      */     }
/* 1439 */     else if (XMLChar.isSpace(c)) {
/*      */       do {
/* 1441 */         boolean entityChanged = false;
/*      */ 
/* 1443 */         if (c == 10) {
/* 1444 */           this.fCurrentEntity.lineNumber += 1;
/* 1445 */           this.fCurrentEntity.columnNumber = 1;
/* 1446 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/* 1447 */             this.fCurrentEntity.ch[0] = ((char)c);
/* 1448 */             entityChanged = load(1, true);
/* 1449 */             if (!entityChanged)
/*      */             {
/* 1452 */               this.fCurrentEntity.startPosition = 0;
/* 1453 */               this.fCurrentEntity.position = 0;
/* 1454 */             } else if (this.fCurrentEntity == null) {
/* 1455 */               return true;
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/* 1460 */           this.fCurrentEntity.columnNumber += 1;
/*      */         }
/*      */ 
/* 1463 */         if (!entityChanged)
/* 1464 */           this.fCurrentEntity.position += 1;
/* 1465 */         if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1466 */           load(0, true);
/*      */ 
/* 1468 */           if (this.fCurrentEntity == null) {
/* 1469 */             return true;
/*      */           }
/*      */         }
/*      */       }
/* 1473 */       while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
/* 1474 */       return true;
/*      */     }
/*      */ 
/* 1478 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean skipString(String s)
/*      */     throws IOException
/*      */   {
/* 1498 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1499 */       load(0, true);
/*      */     }
/*      */ 
/* 1503 */     int length = s.length();
/* 1504 */     for (int i = 0; i < length; i++) {
/* 1505 */       char c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1506 */       if (c != s.charAt(i)) {
/* 1507 */         this.fCurrentEntity.position -= i + 1;
/* 1508 */         return false;
/*      */       }
/* 1510 */       if ((i < length - 1) && (this.fCurrentEntity.position == this.fCurrentEntity.count)) {
/* 1511 */         System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.count - i - 1, this.fCurrentEntity.ch, 0, i + 1);
/*      */ 
/* 1514 */         if (load(i + 1, false)) {
/* 1515 */           this.fCurrentEntity.startPosition -= i + 1;
/* 1516 */           this.fCurrentEntity.position -= i + 1;
/* 1517 */           return false;
/*      */         }
/*      */       }
/*      */     }
/* 1521 */     this.fCurrentEntity.columnNumber += length;
/* 1522 */     return true;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.XML11EntityScanner
 * JD-Core Version:    0.6.2
 */