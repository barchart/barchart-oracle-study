/*      */ package javax.xml.bind;
/*      */ 
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.TimeZone;
/*      */ import javax.xml.datatype.DatatypeConfigurationException;
/*      */ import javax.xml.datatype.DatatypeFactory;
/*      */ import javax.xml.datatype.XMLGregorianCalendar;
/*      */ import javax.xml.namespace.NamespaceContext;
/*      */ import javax.xml.namespace.QName;
/*      */ 
/*      */ final class DatatypeConverterImpl
/*      */   implements DatatypeConverterInterface
/*      */ {
/*      */   public static final DatatypeConverterInterface theInstance;
/*      */   private static final char[] hexCode;
/*      */   private static final byte[] decodeMap;
/*      */   private static final byte PADDING = 127;
/*      */   private static final char[] encodeMap;
/*      */   private static final DatatypeFactory datatypeFactory;
/*      */ 
/*      */   public String parseString(String lexicalXSDString)
/*      */   {
/*   65 */     return lexicalXSDString;
/*      */   }
/*      */ 
/*      */   public BigInteger parseInteger(String lexicalXSDInteger) {
/*   69 */     return _parseInteger(lexicalXSDInteger);
/*      */   }
/*      */ 
/*      */   public static BigInteger _parseInteger(CharSequence s) {
/*   73 */     return new BigInteger(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
/*      */   }
/*      */ 
/*      */   public String printInteger(BigInteger val) {
/*   77 */     return _printInteger(val);
/*      */   }
/*      */ 
/*      */   public static String _printInteger(BigInteger val) {
/*   81 */     return val.toString();
/*      */   }
/*      */ 
/*      */   public int parseInt(String s) {
/*   85 */     return _parseInt(s);
/*      */   }
/*      */ 
/*      */   public static int _parseInt(CharSequence s)
/*      */   {
/*   99 */     int len = s.length();
/*  100 */     int sign = 1;
/*      */ 
/*  102 */     int r = 0;
/*      */ 
/*  104 */     for (int i = 0; i < len; i++) {
/*  105 */       char ch = s.charAt(i);
/*  106 */       if (!WhiteSpaceProcessor.isWhiteSpace(ch))
/*      */       {
/*  108 */         if (('0' <= ch) && (ch <= '9'))
/*  109 */           r = r * 10 + (ch - '0');
/*  110 */         else if (ch == '-')
/*  111 */           sign = -1;
/*  112 */         else if (ch != '+')
/*      */         {
/*  115 */           throw new NumberFormatException("Not a number: " + s);
/*      */         }
/*      */       }
/*      */     }
/*  119 */     return r * sign;
/*      */   }
/*      */ 
/*      */   public long parseLong(String lexicalXSLong) {
/*  123 */     return _parseLong(lexicalXSLong);
/*      */   }
/*      */ 
/*      */   public static long _parseLong(CharSequence s) {
/*  127 */     return Long.valueOf(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString()).longValue();
/*      */   }
/*      */ 
/*      */   public short parseShort(String lexicalXSDShort) {
/*  131 */     return _parseShort(lexicalXSDShort);
/*      */   }
/*      */ 
/*      */   public static short _parseShort(CharSequence s) {
/*  135 */     return (short)_parseInt(s);
/*      */   }
/*      */ 
/*      */   public String printShort(short val) {
/*  139 */     return _printShort(val);
/*      */   }
/*      */ 
/*      */   public static String _printShort(short val) {
/*  143 */     return String.valueOf(val);
/*      */   }
/*      */ 
/*      */   public BigDecimal parseDecimal(String content) {
/*  147 */     return _parseDecimal(content);
/*      */   }
/*      */ 
/*      */   public static BigDecimal _parseDecimal(CharSequence content) {
/*  151 */     content = WhiteSpaceProcessor.trim(content);
/*      */ 
/*  153 */     if (content.length() <= 0) {
/*  154 */       return null;
/*      */     }
/*      */ 
/*  157 */     return new BigDecimal(content.toString());
/*      */   }
/*      */ 
/*      */   public float parseFloat(String lexicalXSDFloat)
/*      */   {
/*  173 */     return _parseFloat(lexicalXSDFloat);
/*      */   }
/*      */ 
/*      */   public static float _parseFloat(CharSequence _val) {
/*  177 */     String s = WhiteSpaceProcessor.trim(_val).toString();
/*      */ 
/*  193 */     if (s.equals("NaN")) {
/*  194 */       return (0.0F / 0.0F);
/*      */     }
/*  196 */     if (s.equals("INF")) {
/*  197 */       return (1.0F / 1.0F);
/*      */     }
/*  199 */     if (s.equals("-INF")) {
/*  200 */       return (1.0F / -1.0F);
/*      */     }
/*      */ 
/*  203 */     if ((s.length() == 0) || (!isDigitOrPeriodOrSign(s.charAt(0))) || (!isDigitOrPeriodOrSign(s.charAt(s.length() - 1))))
/*      */     {
/*  206 */       throw new NumberFormatException();
/*      */     }
/*      */ 
/*  210 */     return Float.parseFloat(s);
/*      */   }
/*      */ 
/*      */   public String printFloat(float v) {
/*  214 */     return _printFloat(v);
/*      */   }
/*      */ 
/*      */   public static String _printFloat(float v) {
/*  218 */     if (Float.isNaN(v)) {
/*  219 */       return "NaN";
/*      */     }
/*  221 */     if (v == (1.0F / 1.0F)) {
/*  222 */       return "INF";
/*      */     }
/*  224 */     if (v == (1.0F / -1.0F)) {
/*  225 */       return "-INF";
/*      */     }
/*  227 */     return String.valueOf(v);
/*      */   }
/*      */ 
/*      */   public double parseDouble(String lexicalXSDDouble) {
/*  231 */     return _parseDouble(lexicalXSDDouble);
/*      */   }
/*      */ 
/*      */   public static double _parseDouble(CharSequence _val) {
/*  235 */     String val = WhiteSpaceProcessor.trim(_val).toString();
/*      */ 
/*  237 */     if (val.equals("NaN")) {
/*  238 */       return (0.0D / 0.0D);
/*      */     }
/*  240 */     if (val.equals("INF")) {
/*  241 */       return (1.0D / 0.0D);
/*      */     }
/*  243 */     if (val.equals("-INF")) {
/*  244 */       return (-1.0D / 0.0D);
/*      */     }
/*      */ 
/*  247 */     if ((val.length() == 0) || (!isDigitOrPeriodOrSign(val.charAt(0))) || (!isDigitOrPeriodOrSign(val.charAt(val.length() - 1))))
/*      */     {
/*  250 */       throw new NumberFormatException(val);
/*      */     }
/*      */ 
/*  255 */     return Double.parseDouble(val);
/*      */   }
/*      */ 
/*      */   public boolean parseBoolean(String lexicalXSDBoolean) {
/*  259 */     return _parseBoolean(lexicalXSDBoolean).booleanValue();
/*      */   }
/*      */ 
/*      */   public static Boolean _parseBoolean(CharSequence literal) {
/*  263 */     if (literal == null) {
/*  264 */       return null;
/*      */     }
/*      */ 
/*  267 */     int i = 0;
/*  268 */     int len = literal.length();
/*      */ 
/*  270 */     boolean value = false;
/*      */ 
/*  272 */     if (literal.length() <= 0) {
/*  273 */       return null;
/*      */     }
/*      */     char ch;
/*      */     do
/*  277 */       ch = literal.charAt(i++);
/*  278 */     while ((WhiteSpaceProcessor.isWhiteSpace(ch)) && (i < len));
/*      */ 
/*  280 */     int strIndex = 0;
/*      */ 
/*  282 */     switch (ch) {
/*      */     case '1':
/*  284 */       value = true;
/*  285 */       break;
/*      */     case '0':
/*  287 */       value = false;
/*  288 */       break;
/*      */     case 't':
/*  290 */       String strTrue = "rue";
/*      */       do
/*  292 */         ch = literal.charAt(i++);
/*  293 */       while ((strTrue.charAt(strIndex++) == ch) && (i < len) && (strIndex < 3));
/*      */ 
/*  295 */       if (strIndex == 3)
/*  296 */         value = true;
/*      */       else {
/*  298 */         return Boolean.valueOf(false);
/*      */       }
/*      */ 
/*      */       break;
/*      */     case 'f':
/*  304 */       String strFalse = "alse";
/*      */       do
/*  306 */         ch = literal.charAt(i++);
/*  307 */       while ((strFalse.charAt(strIndex++) == ch) && (i < len) && (strIndex < 4));
/*      */ 
/*  310 */       if (strIndex == 4)
/*  311 */         value = false;
/*      */       else {
/*  313 */         return Boolean.valueOf(false);
/*      */       }
/*      */ 
/*      */       break;
/*      */     }
/*      */ 
/*  320 */     if (i < len) {
/*      */       do
/*  322 */         ch = literal.charAt(i++);
/*  323 */       while ((WhiteSpaceProcessor.isWhiteSpace(ch)) && (i < len));
/*      */     }
/*      */ 
/*  326 */     if (i == len) {
/*  327 */       return Boolean.valueOf(value);
/*      */     }
/*  329 */     return null;
/*      */   }
/*      */ 
/*      */   public String printBoolean(boolean val)
/*      */   {
/*  335 */     return val ? "true" : "false";
/*      */   }
/*      */ 
/*      */   public static String _printBoolean(boolean val) {
/*  339 */     return val ? "true" : "false";
/*      */   }
/*      */ 
/*      */   public byte parseByte(String lexicalXSDByte) {
/*  343 */     return _parseByte(lexicalXSDByte);
/*      */   }
/*      */ 
/*      */   public static byte _parseByte(CharSequence literal) {
/*  347 */     return (byte)_parseInt(literal);
/*      */   }
/*      */ 
/*      */   public String printByte(byte val) {
/*  351 */     return _printByte(val);
/*      */   }
/*      */ 
/*      */   public static String _printByte(byte val) {
/*  355 */     return String.valueOf(val);
/*      */   }
/*      */ 
/*      */   public QName parseQName(String lexicalXSDQName, NamespaceContext nsc) {
/*  359 */     return _parseQName(lexicalXSDQName, nsc);
/*      */   }
/*      */ 
/*      */   public static QName _parseQName(CharSequence text, NamespaceContext nsc)
/*      */   {
/*  366 */     int length = text.length();
/*      */ 
/*  369 */     int start = 0;
/*  370 */     while ((start < length) && (WhiteSpaceProcessor.isWhiteSpace(text.charAt(start)))) {
/*  371 */       start++;
/*      */     }
/*      */ 
/*  374 */     int end = length;
/*  375 */     while ((end > start) && (WhiteSpaceProcessor.isWhiteSpace(text.charAt(end - 1)))) {
/*  376 */       end--;
/*      */     }
/*      */ 
/*  379 */     if (end == start) {
/*  380 */       throw new IllegalArgumentException("input is empty");
/*      */     }
/*      */ 
/*  389 */     int idx = start + 1;
/*  390 */     while ((idx < end) && (text.charAt(idx) != ':'))
/*  391 */       idx++;
/*      */     String prefix;
/*      */     String prefix;
/*      */     String localPart;
/*      */     String uri;
/*  394 */     if (idx == end) {
/*  395 */       String uri = nsc.getNamespaceURI("");
/*  396 */       String localPart = text.subSequence(start, end).toString();
/*  397 */       prefix = "";
/*      */     }
/*      */     else {
/*  400 */       prefix = text.subSequence(start, idx).toString();
/*  401 */       localPart = text.subSequence(idx + 1, end).toString();
/*  402 */       uri = nsc.getNamespaceURI(prefix);
/*      */ 
/*  405 */       if ((uri == null) || (uri.length() == 0))
/*      */       {
/*  408 */         throw new IllegalArgumentException("prefix " + prefix + " is not bound to a namespace");
/*      */       }
/*      */     }
/*      */ 
/*  412 */     return new QName(uri, localPart, prefix);
/*      */   }
/*      */ 
/*      */   public Calendar parseDateTime(String lexicalXSDDateTime) {
/*  416 */     return _parseDateTime(lexicalXSDDateTime);
/*      */   }
/*      */ 
/*      */   public static GregorianCalendar _parseDateTime(CharSequence s) {
/*  420 */     String val = WhiteSpaceProcessor.trim(s).toString();
/*  421 */     return datatypeFactory.newXMLGregorianCalendar(val).toGregorianCalendar();
/*      */   }
/*      */ 
/*      */   public String printDateTime(Calendar val) {
/*  425 */     return _printDateTime(val);
/*      */   }
/*      */ 
/*      */   public static String _printDateTime(Calendar val) {
/*  429 */     return CalendarFormatter.doFormat("%Y-%M-%DT%h:%m:%s%z", val);
/*      */   }
/*      */ 
/*      */   public byte[] parseBase64Binary(String lexicalXSDBase64Binary) {
/*  433 */     return _parseBase64Binary(lexicalXSDBase64Binary);
/*      */   }
/*      */ 
/*      */   public byte[] parseHexBinary(String s) {
/*  437 */     int len = s.length();
/*      */ 
/*  440 */     if (len % 2 != 0) {
/*  441 */       throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
/*      */     }
/*      */ 
/*  444 */     byte[] out = new byte[len / 2];
/*      */ 
/*  446 */     for (int i = 0; i < len; i += 2) {
/*  447 */       int h = hexToBin(s.charAt(i));
/*  448 */       int l = hexToBin(s.charAt(i + 1));
/*  449 */       if ((h == -1) || (l == -1)) {
/*  450 */         throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
/*      */       }
/*      */ 
/*  453 */       out[(i / 2)] = ((byte)(h * 16 + l));
/*      */     }
/*      */ 
/*  456 */     return out;
/*      */   }
/*      */ 
/*      */   private static int hexToBin(char ch) {
/*  460 */     if (('0' <= ch) && (ch <= '9')) {
/*  461 */       return ch - '0';
/*      */     }
/*  463 */     if (('A' <= ch) && (ch <= 'F')) {
/*  464 */       return ch - 'A' + 10;
/*      */     }
/*  466 */     if (('a' <= ch) && (ch <= 'f')) {
/*  467 */       return ch - 'a' + 10;
/*      */     }
/*  469 */     return -1;
/*      */   }
/*      */ 
/*      */   public String printHexBinary(byte[] data)
/*      */   {
/*  474 */     StringBuilder r = new StringBuilder(data.length * 2);
/*  475 */     for (byte b : data) {
/*  476 */       r.append(hexCode[(b >> 4 & 0xF)]);
/*  477 */       r.append(hexCode[(b & 0xF)]);
/*      */     }
/*  479 */     return r.toString();
/*      */   }
/*      */ 
/*      */   public long parseUnsignedInt(String lexicalXSDUnsignedInt) {
/*  483 */     return _parseLong(lexicalXSDUnsignedInt);
/*      */   }
/*      */ 
/*      */   public String printUnsignedInt(long val) {
/*  487 */     return _printLong(val);
/*      */   }
/*      */ 
/*      */   public int parseUnsignedShort(String lexicalXSDUnsignedShort) {
/*  491 */     return _parseInt(lexicalXSDUnsignedShort);
/*      */   }
/*      */ 
/*      */   public Calendar parseTime(String lexicalXSDTime) {
/*  495 */     return datatypeFactory.newXMLGregorianCalendar(lexicalXSDTime).toGregorianCalendar();
/*      */   }
/*      */ 
/*      */   public String printTime(Calendar val) {
/*  499 */     return CalendarFormatter.doFormat("%h:%m:%s%z", val);
/*      */   }
/*      */ 
/*      */   public Calendar parseDate(String lexicalXSDDate) {
/*  503 */     return datatypeFactory.newXMLGregorianCalendar(lexicalXSDDate).toGregorianCalendar();
/*      */   }
/*      */ 
/*      */   public String printDate(Calendar val) {
/*  507 */     return _printDate(val);
/*      */   }
/*      */ 
/*      */   public static String _printDate(Calendar val) {
/*  511 */     return CalendarFormatter.doFormat("%Y-%M-%D" + "%z", val);
/*      */   }
/*      */ 
/*      */   public String parseAnySimpleType(String lexicalXSDAnySimpleType) {
/*  515 */     return lexicalXSDAnySimpleType;
/*      */   }
/*      */ 
/*      */   public String printString(String val)
/*      */   {
/*  521 */     return val;
/*      */   }
/*      */ 
/*      */   public String printInt(int val) {
/*  525 */     return _printInt(val);
/*      */   }
/*      */ 
/*      */   public static String _printInt(int val) {
/*  529 */     return String.valueOf(val);
/*      */   }
/*      */ 
/*      */   public String printLong(long val) {
/*  533 */     return _printLong(val);
/*      */   }
/*      */ 
/*      */   public static String _printLong(long val) {
/*  537 */     return String.valueOf(val);
/*      */   }
/*      */ 
/*      */   public String printDecimal(BigDecimal val) {
/*  541 */     return _printDecimal(val);
/*      */   }
/*      */ 
/*      */   public static String _printDecimal(BigDecimal val) {
/*  545 */     return val.toPlainString();
/*      */   }
/*      */ 
/*      */   public String printDouble(double v) {
/*  549 */     return _printDouble(v);
/*      */   }
/*      */ 
/*      */   public static String _printDouble(double v) {
/*  553 */     if (Double.isNaN(v)) {
/*  554 */       return "NaN";
/*      */     }
/*  556 */     if (v == (1.0D / 0.0D)) {
/*  557 */       return "INF";
/*      */     }
/*  559 */     if (v == (-1.0D / 0.0D)) {
/*  560 */       return "-INF";
/*      */     }
/*  562 */     return String.valueOf(v);
/*      */   }
/*      */ 
/*      */   public String printQName(QName val, NamespaceContext nsc) {
/*  566 */     return _printQName(val, nsc);
/*      */   }
/*      */ 
/*      */   public static String _printQName(QName val, NamespaceContext nsc)
/*      */   {
/*  572 */     String prefix = nsc.getPrefix(val.getNamespaceURI());
/*  573 */     String localPart = val.getLocalPart();
/*      */     String qname;
/*      */     String qname;
/*  575 */     if ((prefix == null) || (prefix.length() == 0))
/*  576 */       qname = localPart;
/*      */     else {
/*  578 */       qname = prefix + ':' + localPart;
/*      */     }
/*      */ 
/*  581 */     return qname;
/*      */   }
/*      */ 
/*      */   public String printBase64Binary(byte[] val) {
/*  585 */     return _printBase64Binary(val);
/*      */   }
/*      */ 
/*      */   public String printUnsignedShort(int val) {
/*  589 */     return String.valueOf(val);
/*      */   }
/*      */ 
/*      */   public String printAnySimpleType(String val) {
/*  593 */     return val;
/*      */   }
/*      */ 
/*      */   public static String installHook(String s)
/*      */   {
/*  602 */     DatatypeConverter.setDatatypeConverter(theInstance);
/*  603 */     return s;
/*      */   }
/*      */ 
/*      */   private static byte[] initDecodeMap()
/*      */   {
/*  610 */     byte[] map = new byte[''];
/*      */ 
/*  612 */     for (int i = 0; i < 128; i++) {
/*  613 */       map[i] = -1;
/*      */     }
/*      */ 
/*  616 */     for (i = 65; i <= 90; i++) {
/*  617 */       map[i] = ((byte)(i - 65));
/*      */     }
/*  619 */     for (i = 97; i <= 122; i++) {
/*  620 */       map[i] = ((byte)(i - 97 + 26));
/*      */     }
/*  622 */     for (i = 48; i <= 57; i++) {
/*  623 */       map[i] = ((byte)(i - 48 + 52));
/*      */     }
/*  625 */     map[43] = 62;
/*  626 */     map[47] = 63;
/*  627 */     map[61] = 127;
/*      */ 
/*  629 */     return map;
/*      */   }
/*      */ 
/*      */   private static int guessLength(String text)
/*      */   {
/*  653 */     int len = text.length();
/*      */ 
/*  656 */     for (int j = len - 1; 
/*  657 */       j >= 0; j--) {
/*  658 */       byte code = decodeMap[text.charAt(j)];
/*  659 */       if (code != 127)
/*      */       {
/*  662 */         if (code != -1)
/*      */           break;
/*  664 */         return text.length() / 4 * 3;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  669 */     j++;
/*  670 */     int padSize = len - j;
/*  671 */     if (padSize > 2)
/*      */     {
/*  673 */       return text.length() / 4 * 3;
/*      */     }
/*      */ 
/*  678 */     return text.length() / 4 * 3 - padSize;
/*      */   }
/*      */ 
/*      */   public static byte[] _parseBase64Binary(String text)
/*      */   {
/*  691 */     int buflen = guessLength(text);
/*  692 */     byte[] out = new byte[buflen];
/*  693 */     int o = 0;
/*      */ 
/*  695 */     int len = text.length();
/*      */ 
/*  698 */     byte[] quadruplet = new byte[4];
/*  699 */     int q = 0;
/*      */ 
/*  702 */     for (int i = 0; i < len; i++) {
/*  703 */       char ch = text.charAt(i);
/*  704 */       byte v = decodeMap[ch];
/*      */ 
/*  706 */       if (v != -1) {
/*  707 */         quadruplet[(q++)] = v;
/*      */       }
/*      */ 
/*  710 */       if (q == 4)
/*      */       {
/*  712 */         out[(o++)] = ((byte)(quadruplet[0] << 2 | quadruplet[1] >> 4));
/*  713 */         if (quadruplet[2] != 127) {
/*  714 */           out[(o++)] = ((byte)(quadruplet[1] << 4 | quadruplet[2] >> 2));
/*      */         }
/*  716 */         if (quadruplet[3] != 127) {
/*  717 */           out[(o++)] = ((byte)(quadruplet[2] << 6 | quadruplet[3]));
/*      */         }
/*  719 */         q = 0;
/*      */       }
/*      */     }
/*      */ 
/*  723 */     if (buflen == o)
/*      */     {
/*  725 */       return out;
/*      */     }
/*      */ 
/*  729 */     byte[] nb = new byte[o];
/*  730 */     System.arraycopy(out, 0, nb, 0, o);
/*  731 */     return nb;
/*      */   }
/*      */ 
/*      */   private static char[] initEncodeMap()
/*      */   {
/*  736 */     char[] map = new char[64];
/*      */ 
/*  738 */     for (int i = 0; i < 26; i++) {
/*  739 */       map[i] = ((char)(65 + i));
/*      */     }
/*  741 */     for (i = 26; i < 52; i++) {
/*  742 */       map[i] = ((char)(97 + (i - 26)));
/*      */     }
/*  744 */     for (i = 52; i < 62; i++) {
/*  745 */       map[i] = ((char)(48 + (i - 52)));
/*      */     }
/*  747 */     map[62] = '+';
/*  748 */     map[63] = '/';
/*      */ 
/*  750 */     return map;
/*      */   }
/*      */ 
/*      */   public static char encode(int i) {
/*  754 */     return encodeMap[(i & 0x3F)];
/*      */   }
/*      */ 
/*      */   public static byte encodeByte(int i) {
/*  758 */     return (byte)encodeMap[(i & 0x3F)];
/*      */   }
/*      */ 
/*      */   public static String _printBase64Binary(byte[] input) {
/*  762 */     return _printBase64Binary(input, 0, input.length);
/*      */   }
/*      */ 
/*      */   public static String _printBase64Binary(byte[] input, int offset, int len) {
/*  766 */     char[] buf = new char[(len + 2) / 3 * 4];
/*  767 */     int ptr = _printBase64Binary(input, offset, len, buf, 0);
/*  768 */     assert (ptr == buf.length);
/*  769 */     return new String(buf);
/*      */   }
/*      */ 
/*      */   public static int _printBase64Binary(byte[] input, int offset, int len, char[] buf, int ptr)
/*      */   {
/*  783 */     int remaining = len;
/*      */ 
/*  785 */     for (int i = offset; remaining >= 3; i += 3) {
/*  786 */       buf[(ptr++)] = encode(input[i] >> 2);
/*  787 */       buf[(ptr++)] = encode((input[i] & 0x3) << 4 | input[(i + 1)] >> 4 & 0xF);
/*      */ 
/*  790 */       buf[(ptr++)] = encode((input[(i + 1)] & 0xF) << 2 | input[(i + 2)] >> 6 & 0x3);
/*      */ 
/*  793 */       buf[(ptr++)] = encode(input[(i + 2)] & 0x3F);
/*      */ 
/*  785 */       remaining -= 3;
/*      */     }
/*      */ 
/*  796 */     if (remaining == 1) {
/*  797 */       buf[(ptr++)] = encode(input[i] >> 2);
/*  798 */       buf[(ptr++)] = encode((input[i] & 0x3) << 4);
/*  799 */       buf[(ptr++)] = '=';
/*  800 */       buf[(ptr++)] = '=';
/*      */     }
/*      */ 
/*  803 */     if (remaining == 2) {
/*  804 */       buf[(ptr++)] = encode(input[i] >> 2);
/*  805 */       buf[(ptr++)] = encode((input[i] & 0x3) << 4 | input[(i + 1)] >> 4 & 0xF);
/*      */ 
/*  807 */       buf[(ptr++)] = encode((input[(i + 1)] & 0xF) << 2);
/*  808 */       buf[(ptr++)] = '=';
/*      */     }
/*  810 */     return ptr;
/*      */   }
/*      */ 
/*      */   public static int _printBase64Binary(byte[] input, int offset, int len, byte[] out, int ptr)
/*      */   {
/*  824 */     byte[] buf = out;
/*  825 */     int remaining = len;
/*      */ 
/*  827 */     for (int i = offset; remaining >= 3; i += 3) {
/*  828 */       buf[(ptr++)] = encodeByte(input[i] >> 2);
/*  829 */       buf[(ptr++)] = encodeByte((input[i] & 0x3) << 4 | input[(i + 1)] >> 4 & 0xF);
/*      */ 
/*  832 */       buf[(ptr++)] = encodeByte((input[(i + 1)] & 0xF) << 2 | input[(i + 2)] >> 6 & 0x3);
/*      */ 
/*  835 */       buf[(ptr++)] = encodeByte(input[(i + 2)] & 0x3F);
/*      */ 
/*  827 */       remaining -= 3;
/*      */     }
/*      */ 
/*  838 */     if (remaining == 1) {
/*  839 */       buf[(ptr++)] = encodeByte(input[i] >> 2);
/*  840 */       buf[(ptr++)] = encodeByte((input[i] & 0x3) << 4);
/*  841 */       buf[(ptr++)] = 61;
/*  842 */       buf[(ptr++)] = 61;
/*      */     }
/*      */ 
/*  845 */     if (remaining == 2) {
/*  846 */       buf[(ptr++)] = encodeByte(input[i] >> 2);
/*  847 */       buf[(ptr++)] = encodeByte((input[i] & 0x3) << 4 | input[(i + 1)] >> 4 & 0xF);
/*      */ 
/*  850 */       buf[(ptr++)] = encodeByte((input[(i + 1)] & 0xF) << 2);
/*  851 */       buf[(ptr++)] = 61;
/*      */     }
/*      */ 
/*  854 */     return ptr;
/*      */   }
/*      */ 
/*      */   private static CharSequence removeOptionalPlus(CharSequence s) {
/*  858 */     int len = s.length();
/*      */ 
/*  860 */     if ((len <= 1) || (s.charAt(0) != '+')) {
/*  861 */       return s;
/*      */     }
/*      */ 
/*  864 */     s = s.subSequence(1, len);
/*  865 */     char ch = s.charAt(0);
/*  866 */     if (('0' <= ch) && (ch <= '9')) {
/*  867 */       return s;
/*      */     }
/*  869 */     if ('.' == ch) {
/*  870 */       return s;
/*      */     }
/*      */ 
/*  873 */     throw new NumberFormatException();
/*      */   }
/*      */ 
/*      */   private static boolean isDigitOrPeriodOrSign(char ch) {
/*  877 */     if (('0' <= ch) && (ch <= '9')) {
/*  878 */       return true;
/*      */     }
/*  880 */     if ((ch == '+') || (ch == '-') || (ch == '.')) {
/*  881 */       return true;
/*      */     }
/*  883 */     return false;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   59 */     theInstance = new DatatypeConverterImpl();
/*      */ 
/*  471 */     hexCode = "0123456789ABCDEF".toCharArray();
/*      */ 
/*  606 */     decodeMap = initDecodeMap();
/*      */ 
/*  733 */     encodeMap = initEncodeMap();
/*      */     try
/*      */     {
/*  889 */       datatypeFactory = DatatypeFactory.newInstance();
/*      */     } catch (DatatypeConfigurationException e) {
/*  891 */       throw new Error(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class CalendarFormatter
/*      */   {
/*      */     public static String doFormat(String format, Calendar cal) throws IllegalArgumentException {
/*  898 */       int fidx = 0;
/*  899 */       int flen = format.length();
/*  900 */       StringBuilder buf = new StringBuilder();
/*      */ 
/*  902 */       while (fidx < flen) {
/*  903 */         char fch = format.charAt(fidx++);
/*      */ 
/*  905 */         if (fch != '%') {
/*  906 */           buf.append(fch);
/*      */         }
/*      */         else
/*      */         {
/*  911 */           switch (format.charAt(fidx++)) {
/*      */           case 'Y':
/*  913 */             formatYear(cal, buf);
/*  914 */             break;
/*      */           case 'M':
/*  917 */             formatMonth(cal, buf);
/*  918 */             break;
/*      */           case 'D':
/*  921 */             formatDays(cal, buf);
/*  922 */             break;
/*      */           case 'h':
/*  925 */             formatHours(cal, buf);
/*  926 */             break;
/*      */           case 'm':
/*  929 */             formatMinutes(cal, buf);
/*  930 */             break;
/*      */           case 's':
/*  933 */             formatSeconds(cal, buf);
/*  934 */             break;
/*      */           case 'z':
/*  937 */             formatTimeZone(cal, buf);
/*  938 */             break;
/*      */           default:
/*  942 */             throw new InternalError();
/*      */           }
/*      */         }
/*      */       }
/*  946 */       return buf.toString();
/*      */     }
/*      */ 
/*      */     private static void formatYear(Calendar cal, StringBuilder buf) {
/*  950 */       int year = cal.get(1);
/*      */       String s;
/*      */       String s;
/*  953 */       if (year <= 0)
/*      */       {
/*  955 */         s = Integer.toString(1 - year);
/*      */       }
/*      */       else {
/*  958 */         s = Integer.toString(year);
/*      */       }
/*      */ 
/*  961 */       while (s.length() < 4) {
/*  962 */         s = '0' + s;
/*      */       }
/*  964 */       if (year <= 0) {
/*  965 */         s = '-' + s;
/*      */       }
/*      */ 
/*  968 */       buf.append(s);
/*      */     }
/*      */ 
/*      */     private static void formatMonth(Calendar cal, StringBuilder buf) {
/*  972 */       formatTwoDigits(cal.get(2) + 1, buf);
/*      */     }
/*      */ 
/*      */     private static void formatDays(Calendar cal, StringBuilder buf) {
/*  976 */       formatTwoDigits(cal.get(5), buf);
/*      */     }
/*      */ 
/*      */     private static void formatHours(Calendar cal, StringBuilder buf) {
/*  980 */       formatTwoDigits(cal.get(11), buf);
/*      */     }
/*      */ 
/*      */     private static void formatMinutes(Calendar cal, StringBuilder buf) {
/*  984 */       formatTwoDigits(cal.get(12), buf);
/*      */     }
/*      */ 
/*      */     private static void formatSeconds(Calendar cal, StringBuilder buf) {
/*  988 */       formatTwoDigits(cal.get(13), buf);
/*  989 */       if (cal.isSet(14)) {
/*  990 */         int n = cal.get(14);
/*  991 */         if (n != 0) {
/*  992 */           String ms = Integer.toString(n);
/*  993 */           while (ms.length() < 3) {
/*  994 */             ms = '0' + ms;
/*      */           }
/*  996 */           buf.append('.');
/*  997 */           buf.append(ms);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private static void formatTimeZone(Calendar cal, StringBuilder buf)
/*      */     {
/* 1004 */       TimeZone tz = cal.getTimeZone();
/*      */ 
/* 1006 */       if (tz == null) {
/* 1007 */         return;
/*      */       }
/*      */ 
/* 1011 */       int offset = tz.getOffset(cal.getTime().getTime());
/*      */ 
/* 1013 */       if (offset == 0) {
/* 1014 */         buf.append('Z');
/* 1015 */         return;
/*      */       }
/*      */ 
/* 1018 */       if (offset >= 0) {
/* 1019 */         buf.append('+');
/*      */       } else {
/* 1021 */         buf.append('-');
/* 1022 */         offset *= -1;
/*      */       }
/*      */ 
/* 1025 */       offset /= 60000;
/*      */ 
/* 1027 */       formatTwoDigits(offset / 60, buf);
/* 1028 */       buf.append(':');
/* 1029 */       formatTwoDigits(offset % 60, buf);
/*      */     }
/*      */ 
/*      */     private static void formatTwoDigits(int n, StringBuilder buf)
/*      */     {
/* 1035 */       if (n < 10) {
/* 1036 */         buf.append('0');
/*      */       }
/* 1038 */       buf.append(n);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.bind.DatatypeConverterImpl
 * JD-Core Version:    0.6.2
 */