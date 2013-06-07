/*     */ package com.sun.xml.internal.bind;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.TimeZone;
/*     */ import javax.xml.datatype.DatatypeConfigurationException;
/*     */ import javax.xml.datatype.DatatypeFactory;
/*     */ import javax.xml.datatype.XMLGregorianCalendar;
/*     */ import javax.xml.namespace.NamespaceContext;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamWriter;
/*     */ 
/*     */ /** @deprecated */
/*     */ public final class DatatypeConverterImpl
/*     */ {
/*     */   private static final byte[] decodeMap;
/*     */   private static final byte PADDING = 127;
/*     */   private static final char[] encodeMap;
/*     */   private static final DatatypeFactory datatypeFactory;
/*     */ 
/*     */   public static BigInteger _parseInteger(CharSequence s)
/*     */   {
/*  67 */     return new BigInteger(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
/*     */   }
/*     */ 
/*     */   public static String _printInteger(BigInteger val) {
/*  71 */     return val.toString();
/*     */   }
/*     */ 
/*     */   public static int _parseInt(CharSequence s)
/*     */   {
/*  85 */     int len = s.length();
/*  86 */     int sign = 1;
/*     */ 
/*  88 */     int r = 0;
/*     */ 
/*  90 */     for (int i = 0; i < len; i++) {
/*  91 */       char ch = s.charAt(i);
/*  92 */       if (!WhiteSpaceProcessor.isWhiteSpace(ch))
/*     */       {
/*  94 */         if (('0' <= ch) && (ch <= '9'))
/*  95 */           r = r * 10 + (ch - '0');
/*  96 */         else if (ch == '-')
/*  97 */           sign = -1;
/*  98 */         else if (ch != '+')
/*     */         {
/* 101 */           throw new NumberFormatException("Not a number: " + s);
/*     */         }
/*     */       }
/*     */     }
/* 105 */     return r * sign;
/*     */   }
/*     */ 
/*     */   public static long _parseLong(CharSequence s) {
/* 109 */     return Long.valueOf(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString()).longValue();
/*     */   }
/*     */ 
/*     */   public static short _parseShort(CharSequence s) {
/* 113 */     return (short)_parseInt(s);
/*     */   }
/*     */ 
/*     */   public static String _printShort(short val) {
/* 117 */     return String.valueOf(val);
/*     */   }
/*     */ 
/*     */   public static BigDecimal _parseDecimal(CharSequence content) {
/* 121 */     content = WhiteSpaceProcessor.trim(content);
/*     */ 
/* 123 */     if (content.length() <= 0) {
/* 124 */       return null;
/*     */     }
/*     */ 
/* 127 */     return new BigDecimal(content.toString());
/*     */   }
/*     */ 
/*     */   public static float _parseFloat(CharSequence _val)
/*     */   {
/* 143 */     String s = WhiteSpaceProcessor.trim(_val).toString();
/*     */ 
/* 159 */     if (s.equals("NaN")) {
/* 160 */       return (0.0F / 0.0F);
/*     */     }
/* 162 */     if (s.equals("INF")) {
/* 163 */       return (1.0F / 1.0F);
/*     */     }
/* 165 */     if (s.equals("-INF")) {
/* 166 */       return (1.0F / -1.0F);
/*     */     }
/*     */ 
/* 169 */     if ((s.length() == 0) || (!isDigitOrPeriodOrSign(s.charAt(0))) || (!isDigitOrPeriodOrSign(s.charAt(s.length() - 1))))
/*     */     {
/* 172 */       throw new NumberFormatException();
/*     */     }
/*     */ 
/* 176 */     return Float.parseFloat(s);
/*     */   }
/*     */ 
/*     */   public static String _printFloat(float v) {
/* 180 */     if (Float.isNaN(v)) {
/* 181 */       return "NaN";
/*     */     }
/* 183 */     if (v == (1.0F / 1.0F)) {
/* 184 */       return "INF";
/*     */     }
/* 186 */     if (v == (1.0F / -1.0F)) {
/* 187 */       return "-INF";
/*     */     }
/* 189 */     return String.valueOf(v);
/*     */   }
/*     */ 
/*     */   public static double _parseDouble(CharSequence _val) {
/* 193 */     String val = WhiteSpaceProcessor.trim(_val).toString();
/*     */ 
/* 195 */     if (val.equals("NaN")) {
/* 196 */       return (0.0D / 0.0D);
/*     */     }
/* 198 */     if (val.equals("INF")) {
/* 199 */       return (1.0D / 0.0D);
/*     */     }
/* 201 */     if (val.equals("-INF")) {
/* 202 */       return (-1.0D / 0.0D);
/*     */     }
/*     */ 
/* 205 */     if ((val.length() == 0) || (!isDigitOrPeriodOrSign(val.charAt(0))) || (!isDigitOrPeriodOrSign(val.charAt(val.length() - 1))))
/*     */     {
/* 208 */       throw new NumberFormatException(val);
/*     */     }
/*     */ 
/* 213 */     return Double.parseDouble(val);
/*     */   }
/*     */ 
/*     */   public static Boolean _parseBoolean(CharSequence literal) {
/* 217 */     if (literal == null) {
/* 218 */       return null;
/*     */     }
/*     */ 
/* 221 */     int i = 0;
/* 222 */     int len = literal.length();
/*     */ 
/* 224 */     boolean value = false;
/*     */ 
/* 226 */     if (literal.length() <= 0) {
/* 227 */       return null;
/*     */     }
/*     */     char ch;
/*     */     do
/* 231 */       ch = literal.charAt(i++);
/* 232 */     while ((WhiteSpaceProcessor.isWhiteSpace(ch)) && (i < len));
/*     */ 
/* 234 */     int strIndex = 0;
/*     */ 
/* 236 */     switch (ch) {
/*     */     case '1':
/* 238 */       value = true;
/* 239 */       break;
/*     */     case '0':
/* 241 */       value = false;
/* 242 */       break;
/*     */     case 't':
/* 244 */       String strTrue = "rue";
/*     */       do
/* 246 */         ch = literal.charAt(i++);
/* 247 */       while ((strTrue.charAt(strIndex++) == ch) && (i < len) && (strIndex < 3));
/*     */ 
/* 249 */       if (strIndex == 3)
/* 250 */         value = true;
/*     */       else {
/* 252 */         return Boolean.valueOf(false);
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 'f':
/* 258 */       String strFalse = "alse";
/*     */       do
/* 260 */         ch = literal.charAt(i++);
/* 261 */       while ((strFalse.charAt(strIndex++) == ch) && (i < len) && (strIndex < 4));
/*     */ 
/* 264 */       if (strIndex == 4)
/* 265 */         value = false;
/*     */       else {
/* 267 */         return Boolean.valueOf(false);
/*     */       }
/*     */ 
/*     */       break;
/*     */     }
/*     */ 
/* 274 */     if (i < len) {
/*     */       do
/* 276 */         ch = literal.charAt(i++);
/* 277 */       while ((WhiteSpaceProcessor.isWhiteSpace(ch)) && (i < len));
/*     */     }
/*     */ 
/* 280 */     if (i == len) {
/* 281 */       return Boolean.valueOf(value);
/*     */     }
/* 283 */     return null;
/*     */   }
/*     */ 
/*     */   public static String _printBoolean(boolean val)
/*     */   {
/* 289 */     return val ? "true" : "false";
/*     */   }
/*     */ 
/*     */   public static byte _parseByte(CharSequence literal) {
/* 293 */     return (byte)_parseInt(literal);
/*     */   }
/*     */ 
/*     */   public static String _printByte(byte val) {
/* 297 */     return String.valueOf(val);
/*     */   }
/*     */ 
/*     */   public static QName _parseQName(CharSequence text, NamespaceContext nsc)
/*     */   {
/* 304 */     int length = text.length();
/*     */ 
/* 307 */     int start = 0;
/* 308 */     while ((start < length) && (WhiteSpaceProcessor.isWhiteSpace(text.charAt(start)))) {
/* 309 */       start++;
/*     */     }
/*     */ 
/* 312 */     int end = length;
/* 313 */     while ((end > start) && (WhiteSpaceProcessor.isWhiteSpace(text.charAt(end - 1)))) {
/* 314 */       end--;
/*     */     }
/*     */ 
/* 317 */     if (end == start) {
/* 318 */       throw new IllegalArgumentException("input is empty");
/*     */     }
/*     */ 
/* 327 */     int idx = start + 1;
/* 328 */     while ((idx < end) && (text.charAt(idx) != ':'))
/* 329 */       idx++;
/*     */     String prefix;
/*     */     String prefix;
/*     */     String localPart;
/*     */     String uri;
/* 332 */     if (idx == end) {
/* 333 */       String uri = nsc.getNamespaceURI("");
/* 334 */       String localPart = text.subSequence(start, end).toString();
/* 335 */       prefix = "";
/*     */     }
/*     */     else {
/* 338 */       prefix = text.subSequence(start, idx).toString();
/* 339 */       localPart = text.subSequence(idx + 1, end).toString();
/* 340 */       uri = nsc.getNamespaceURI(prefix);
/*     */ 
/* 343 */       if ((uri == null) || (uri.length() == 0))
/*     */       {
/* 346 */         throw new IllegalArgumentException("prefix " + prefix + " is not bound to a namespace");
/*     */       }
/*     */     }
/*     */ 
/* 350 */     return new QName(uri, localPart, prefix);
/*     */   }
/*     */ 
/*     */   public static GregorianCalendar _parseDateTime(CharSequence s) {
/* 354 */     String val = WhiteSpaceProcessor.trim(s).toString();
/* 355 */     return datatypeFactory.newXMLGregorianCalendar(val).toGregorianCalendar();
/*     */   }
/*     */ 
/*     */   public static String _printDateTime(Calendar val) {
/* 359 */     return CalendarFormatter.doFormat("%Y-%M-%DT%h:%m:%s%z", val);
/*     */   }
/*     */ 
/*     */   public static String _printDate(Calendar val) {
/* 363 */     return CalendarFormatter.doFormat("%Y-%M-%D" + "%z", val);
/*     */   }
/*     */ 
/*     */   public static String _printInt(int val) {
/* 367 */     return String.valueOf(val);
/*     */   }
/*     */ 
/*     */   public static String _printLong(long val) {
/* 371 */     return String.valueOf(val);
/*     */   }
/*     */ 
/*     */   public static String _printDecimal(BigDecimal val) {
/* 375 */     return val.toPlainString();
/*     */   }
/*     */ 
/*     */   public static String _printDouble(double v) {
/* 379 */     if (Double.isNaN(v)) {
/* 380 */       return "NaN";
/*     */     }
/* 382 */     if (v == (1.0D / 0.0D)) {
/* 383 */       return "INF";
/*     */     }
/* 385 */     if (v == (-1.0D / 0.0D)) {
/* 386 */       return "-INF";
/*     */     }
/* 388 */     return String.valueOf(v);
/*     */   }
/*     */ 
/*     */   public static String _printQName(QName val, NamespaceContext nsc)
/*     */   {
/* 394 */     String prefix = nsc.getPrefix(val.getNamespaceURI());
/* 395 */     String localPart = val.getLocalPart();
/*     */     String qname;
/*     */     String qname;
/* 397 */     if ((prefix == null) || (prefix.length() == 0))
/* 398 */       qname = localPart;
/*     */     else {
/* 400 */       qname = prefix + ':' + localPart;
/*     */     }
/*     */ 
/* 403 */     return qname;
/*     */   }
/*     */ 
/*     */   private static byte[] initDecodeMap()
/*     */   {
/* 411 */     byte[] map = new byte[''];
/*     */ 
/* 413 */     for (int i = 0; i < 128; i++) {
/* 414 */       map[i] = -1;
/*     */     }
/*     */ 
/* 417 */     for (i = 65; i <= 90; i++) {
/* 418 */       map[i] = ((byte)(i - 65));
/*     */     }
/* 420 */     for (i = 97; i <= 122; i++) {
/* 421 */       map[i] = ((byte)(i - 97 + 26));
/*     */     }
/* 423 */     for (i = 48; i <= 57; i++) {
/* 424 */       map[i] = ((byte)(i - 48 + 52));
/*     */     }
/* 426 */     map[43] = 62;
/* 427 */     map[47] = 63;
/* 428 */     map[61] = 127;
/*     */ 
/* 430 */     return map;
/*     */   }
/*     */ 
/*     */   private static int guessLength(String text)
/*     */   {
/* 454 */     int len = text.length();
/*     */ 
/* 457 */     for (int j = len - 1; 
/* 458 */       j >= 0; j--) {
/* 459 */       byte code = decodeMap[text.charAt(j)];
/* 460 */       if (code != 127)
/*     */       {
/* 463 */         if (code != -1)
/*     */           break;
/* 465 */         return text.length() / 4 * 3;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 470 */     j++;
/* 471 */     int padSize = len - j;
/* 472 */     if (padSize > 2)
/*     */     {
/* 474 */       return text.length() / 4 * 3;
/*     */     }
/*     */ 
/* 479 */     return text.length() / 4 * 3 - padSize;
/*     */   }
/*     */ 
/*     */   public static byte[] _parseBase64Binary(String text)
/*     */   {
/* 492 */     int buflen = guessLength(text);
/* 493 */     byte[] out = new byte[buflen];
/* 494 */     int o = 0;
/*     */ 
/* 496 */     int len = text.length();
/*     */ 
/* 499 */     byte[] quadruplet = new byte[4];
/* 500 */     int q = 0;
/*     */ 
/* 503 */     for (int i = 0; i < len; i++) {
/* 504 */       char ch = text.charAt(i);
/* 505 */       byte v = decodeMap[ch];
/*     */ 
/* 507 */       if (v != -1) {
/* 508 */         quadruplet[(q++)] = v;
/*     */       }
/*     */ 
/* 511 */       if (q == 4)
/*     */       {
/* 513 */         out[(o++)] = ((byte)(quadruplet[0] << 2 | quadruplet[1] >> 4));
/* 514 */         if (quadruplet[2] != 127) {
/* 515 */           out[(o++)] = ((byte)(quadruplet[1] << 4 | quadruplet[2] >> 2));
/*     */         }
/* 517 */         if (quadruplet[3] != 127) {
/* 518 */           out[(o++)] = ((byte)(quadruplet[2] << 6 | quadruplet[3]));
/*     */         }
/* 520 */         q = 0;
/*     */       }
/*     */     }
/*     */ 
/* 524 */     if (buflen == o)
/*     */     {
/* 526 */       return out;
/*     */     }
/*     */ 
/* 530 */     byte[] nb = new byte[o];
/* 531 */     System.arraycopy(out, 0, nb, 0, o);
/* 532 */     return nb;
/*     */   }
/*     */ 
/*     */   private static char[] initEncodeMap()
/*     */   {
/* 537 */     char[] map = new char[64];
/*     */ 
/* 539 */     for (int i = 0; i < 26; i++) {
/* 540 */       map[i] = ((char)(65 + i));
/*     */     }
/* 542 */     for (i = 26; i < 52; i++) {
/* 543 */       map[i] = ((char)(97 + (i - 26)));
/*     */     }
/* 545 */     for (i = 52; i < 62; i++) {
/* 546 */       map[i] = ((char)(48 + (i - 52)));
/*     */     }
/* 548 */     map[62] = '+';
/* 549 */     map[63] = '/';
/*     */ 
/* 551 */     return map;
/*     */   }
/*     */ 
/*     */   public static char encode(int i) {
/* 555 */     return encodeMap[(i & 0x3F)];
/*     */   }
/*     */ 
/*     */   public static byte encodeByte(int i) {
/* 559 */     return (byte)encodeMap[(i & 0x3F)];
/*     */   }
/*     */ 
/*     */   public static String _printBase64Binary(byte[] input) {
/* 563 */     return _printBase64Binary(input, 0, input.length);
/*     */   }
/*     */ 
/*     */   public static String _printBase64Binary(byte[] input, int offset, int len) {
/* 567 */     char[] buf = new char[(len + 2) / 3 * 4];
/* 568 */     int ptr = _printBase64Binary(input, offset, len, buf, 0);
/* 569 */     assert (ptr == buf.length);
/* 570 */     return new String(buf);
/*     */   }
/*     */ 
/*     */   public static int _printBase64Binary(byte[] input, int offset, int len, char[] buf, int ptr)
/*     */   {
/* 584 */     int remaining = len;
/*     */ 
/* 586 */     for (int i = offset; remaining >= 3; i += 3) {
/* 587 */       buf[(ptr++)] = encode(input[i] >> 2);
/* 588 */       buf[(ptr++)] = encode((input[i] & 0x3) << 4 | input[(i + 1)] >> 4 & 0xF);
/*     */ 
/* 591 */       buf[(ptr++)] = encode((input[(i + 1)] & 0xF) << 2 | input[(i + 2)] >> 6 & 0x3);
/*     */ 
/* 594 */       buf[(ptr++)] = encode(input[(i + 2)] & 0x3F);
/*     */ 
/* 586 */       remaining -= 3;
/*     */     }
/*     */ 
/* 597 */     if (remaining == 1) {
/* 598 */       buf[(ptr++)] = encode(input[i] >> 2);
/* 599 */       buf[(ptr++)] = encode((input[i] & 0x3) << 4);
/* 600 */       buf[(ptr++)] = '=';
/* 601 */       buf[(ptr++)] = '=';
/*     */     }
/*     */ 
/* 604 */     if (remaining == 2) {
/* 605 */       buf[(ptr++)] = encode(input[i] >> 2);
/* 606 */       buf[(ptr++)] = encode((input[i] & 0x3) << 4 | input[(i + 1)] >> 4 & 0xF);
/*     */ 
/* 608 */       buf[(ptr++)] = encode((input[(i + 1)] & 0xF) << 2);
/* 609 */       buf[(ptr++)] = '=';
/*     */     }
/* 611 */     return ptr;
/*     */   }
/*     */ 
/*     */   public static void _printBase64Binary(byte[] input, int offset, int len, XMLStreamWriter output) throws XMLStreamException {
/* 615 */     int remaining = len;
/*     */ 
/* 617 */     char[] buf = new char[4];
/*     */ 
/* 619 */     for (int i = offset; remaining >= 3; i += 3) {
/* 620 */       buf[0] = encode(input[i] >> 2);
/* 621 */       buf[1] = encode((input[i] & 0x3) << 4 | input[(i + 1)] >> 4 & 0xF);
/*     */ 
/* 624 */       buf[2] = encode((input[(i + 1)] & 0xF) << 2 | input[(i + 2)] >> 6 & 0x3);
/*     */ 
/* 627 */       buf[3] = encode(input[(i + 2)] & 0x3F);
/* 628 */       output.writeCharacters(buf, 0, 4);
/*     */ 
/* 619 */       remaining -= 3;
/*     */     }
/*     */ 
/* 631 */     if (remaining == 1) {
/* 632 */       buf[0] = encode(input[i] >> 2);
/* 633 */       buf[1] = encode((input[i] & 0x3) << 4);
/* 634 */       buf[2] = '=';
/* 635 */       buf[3] = '=';
/* 636 */       output.writeCharacters(buf, 0, 4);
/*     */     }
/*     */ 
/* 639 */     if (remaining == 2) {
/* 640 */       buf[0] = encode(input[i] >> 2);
/* 641 */       buf[1] = encode((input[i] & 0x3) << 4 | input[(i + 1)] >> 4 & 0xF);
/*     */ 
/* 643 */       buf[2] = encode((input[(i + 1)] & 0xF) << 2);
/* 644 */       buf[3] = '=';
/* 645 */       output.writeCharacters(buf, 0, 4);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int _printBase64Binary(byte[] input, int offset, int len, byte[] out, int ptr)
/*     */   {
/* 660 */     byte[] buf = out;
/* 661 */     int remaining = len;
/*     */ 
/* 663 */     for (int i = offset; remaining >= 3; i += 3) {
/* 664 */       buf[(ptr++)] = encodeByte(input[i] >> 2);
/* 665 */       buf[(ptr++)] = encodeByte((input[i] & 0x3) << 4 | input[(i + 1)] >> 4 & 0xF);
/*     */ 
/* 668 */       buf[(ptr++)] = encodeByte((input[(i + 1)] & 0xF) << 2 | input[(i + 2)] >> 6 & 0x3);
/*     */ 
/* 671 */       buf[(ptr++)] = encodeByte(input[(i + 2)] & 0x3F);
/*     */ 
/* 663 */       remaining -= 3;
/*     */     }
/*     */ 
/* 674 */     if (remaining == 1) {
/* 675 */       buf[(ptr++)] = encodeByte(input[i] >> 2);
/* 676 */       buf[(ptr++)] = encodeByte((input[i] & 0x3) << 4);
/* 677 */       buf[(ptr++)] = 61;
/* 678 */       buf[(ptr++)] = 61;
/*     */     }
/*     */ 
/* 681 */     if (remaining == 2) {
/* 682 */       buf[(ptr++)] = encodeByte(input[i] >> 2);
/* 683 */       buf[(ptr++)] = encodeByte((input[i] & 0x3) << 4 | input[(i + 1)] >> 4 & 0xF);
/*     */ 
/* 686 */       buf[(ptr++)] = encodeByte((input[(i + 1)] & 0xF) << 2);
/* 687 */       buf[(ptr++)] = 61;
/*     */     }
/*     */ 
/* 690 */     return ptr;
/*     */   }
/*     */ 
/*     */   private static CharSequence removeOptionalPlus(CharSequence s) {
/* 694 */     int len = s.length();
/*     */ 
/* 696 */     if ((len <= 1) || (s.charAt(0) != '+')) {
/* 697 */       return s;
/*     */     }
/*     */ 
/* 700 */     s = s.subSequence(1, len);
/* 701 */     char ch = s.charAt(0);
/* 702 */     if (('0' <= ch) && (ch <= '9')) {
/* 703 */       return s;
/*     */     }
/* 705 */     if ('.' == ch) {
/* 706 */       return s;
/*     */     }
/*     */ 
/* 709 */     throw new NumberFormatException();
/*     */   }
/*     */ 
/*     */   private static boolean isDigitOrPeriodOrSign(char ch) {
/* 713 */     if (('0' <= ch) && (ch <= '9')) {
/* 714 */       return true;
/*     */     }
/* 716 */     if ((ch == '+') || (ch == '-') || (ch == '.')) {
/* 717 */       return true;
/*     */     }
/* 719 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 407 */     decodeMap = initDecodeMap();
/*     */ 
/* 534 */     encodeMap = initEncodeMap();
/*     */     try
/*     */     {
/* 725 */       datatypeFactory = DatatypeFactory.newInstance();
/*     */     } catch (DatatypeConfigurationException e) {
/* 727 */       throw new Error(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class CalendarFormatter
/*     */   {
/*     */     public static String doFormat(String format, Calendar cal) throws IllegalArgumentException {
/* 734 */       int fidx = 0;
/* 735 */       int flen = format.length();
/* 736 */       StringBuilder buf = new StringBuilder();
/*     */ 
/* 738 */       while (fidx < flen) {
/* 739 */         char fch = format.charAt(fidx++);
/*     */ 
/* 741 */         if (fch != '%') {
/* 742 */           buf.append(fch);
/*     */         }
/*     */         else
/*     */         {
/* 747 */           switch (format.charAt(fidx++)) {
/*     */           case 'Y':
/* 749 */             formatYear(cal, buf);
/* 750 */             break;
/*     */           case 'M':
/* 753 */             formatMonth(cal, buf);
/* 754 */             break;
/*     */           case 'D':
/* 757 */             formatDays(cal, buf);
/* 758 */             break;
/*     */           case 'h':
/* 761 */             formatHours(cal, buf);
/* 762 */             break;
/*     */           case 'm':
/* 765 */             formatMinutes(cal, buf);
/* 766 */             break;
/*     */           case 's':
/* 769 */             formatSeconds(cal, buf);
/* 770 */             break;
/*     */           case 'z':
/* 773 */             formatTimeZone(cal, buf);
/* 774 */             break;
/*     */           default:
/* 778 */             throw new InternalError();
/*     */           }
/*     */         }
/*     */       }
/* 782 */       return buf.toString();
/*     */     }
/*     */ 
/*     */     private static void formatYear(Calendar cal, StringBuilder buf) {
/* 786 */       int year = cal.get(1);
/*     */       String s;
/*     */       String s;
/* 789 */       if (year <= 0)
/*     */       {
/* 791 */         s = Integer.toString(1 - year);
/*     */       }
/*     */       else {
/* 794 */         s = Integer.toString(year);
/*     */       }
/*     */ 
/* 797 */       while (s.length() < 4) {
/* 798 */         s = '0' + s;
/*     */       }
/* 800 */       if (year <= 0) {
/* 801 */         s = '-' + s;
/*     */       }
/*     */ 
/* 804 */       buf.append(s);
/*     */     }
/*     */ 
/*     */     private static void formatMonth(Calendar cal, StringBuilder buf) {
/* 808 */       formatTwoDigits(cal.get(2) + 1, buf);
/*     */     }
/*     */ 
/*     */     private static void formatDays(Calendar cal, StringBuilder buf) {
/* 812 */       formatTwoDigits(cal.get(5), buf);
/*     */     }
/*     */ 
/*     */     private static void formatHours(Calendar cal, StringBuilder buf) {
/* 816 */       formatTwoDigits(cal.get(11), buf);
/*     */     }
/*     */ 
/*     */     private static void formatMinutes(Calendar cal, StringBuilder buf) {
/* 820 */       formatTwoDigits(cal.get(12), buf);
/*     */     }
/*     */ 
/*     */     private static void formatSeconds(Calendar cal, StringBuilder buf) {
/* 824 */       formatTwoDigits(cal.get(13), buf);
/* 825 */       if (cal.isSet(14)) {
/* 826 */         int n = cal.get(14);
/* 827 */         if (n != 0) {
/* 828 */           String ms = Integer.toString(n);
/* 829 */           while (ms.length() < 3) {
/* 830 */             ms = '0' + ms;
/*     */           }
/* 832 */           buf.append('.');
/* 833 */           buf.append(ms);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private static void formatTimeZone(Calendar cal, StringBuilder buf)
/*     */     {
/* 840 */       TimeZone tz = cal.getTimeZone();
/*     */ 
/* 842 */       if (tz == null) {
/* 843 */         return;
/*     */       }
/*     */ 
/* 847 */       int offset = tz.getOffset(cal.getTime().getTime());
/*     */ 
/* 849 */       if (offset == 0) {
/* 850 */         buf.append('Z');
/* 851 */         return;
/*     */       }
/*     */ 
/* 854 */       if (offset >= 0) {
/* 855 */         buf.append('+');
/*     */       } else {
/* 857 */         buf.append('-');
/* 858 */         offset *= -1;
/*     */       }
/*     */ 
/* 861 */       offset /= 60000;
/*     */ 
/* 863 */       formatTwoDigits(offset / 60, buf);
/* 864 */       buf.append(':');
/* 865 */       formatTwoDigits(offset % 60, buf);
/*     */     }
/*     */ 
/*     */     private static void formatTwoDigits(int n, StringBuilder buf)
/*     */     {
/* 871 */       if (n < 10) {
/* 872 */         buf.append('0');
/*     */       }
/* 874 */       buf.append(n);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.DatatypeConverterImpl
 * JD-Core Version:    0.6.2
 */