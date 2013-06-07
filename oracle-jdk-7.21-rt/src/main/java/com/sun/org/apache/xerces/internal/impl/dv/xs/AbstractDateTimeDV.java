/*      */ package com.sun.org.apache.xerces.internal.impl.dv.xs;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl;
/*      */ import com.sun.org.apache.xerces.internal.xs.datatypes.XSDateTime;
/*      */ import java.math.BigDecimal;
/*      */ import javax.xml.datatype.DatatypeFactory;
/*      */ import javax.xml.datatype.Duration;
/*      */ import javax.xml.datatype.XMLGregorianCalendar;
/*      */ 
/*      */ public abstract class AbstractDateTimeDV extends TypeValidator
/*      */ {
/*      */   private static final boolean DEBUG = false;
/*      */   protected static final int YEAR = 2000;
/*      */   protected static final int MONTH = 1;
/*      */   protected static final int DAY = 1;
/*   66 */   protected static final DatatypeFactory datatypeFactory = new DatatypeFactoryImpl();
/*      */ 
/*      */   public short getAllowedFacets() {
/*   69 */     return 2552;
/*      */   }
/*      */ 
/*      */   public boolean isIdentical(Object value1, Object value2)
/*      */   {
/*   77 */     if ((!(value1 instanceof DateTimeData)) || (!(value2 instanceof DateTimeData))) {
/*   78 */       return false;
/*      */     }
/*      */ 
/*   81 */     DateTimeData v1 = (DateTimeData)value1;
/*   82 */     DateTimeData v2 = (DateTimeData)value2;
/*      */ 
/*   86 */     if ((v1.timezoneHr == v2.timezoneHr) && (v1.timezoneMin == v2.timezoneMin)) {
/*   87 */       return v1.equals(v2);
/*      */     }
/*      */ 
/*   90 */     return false;
/*      */   }
/*      */ 
/*      */   public int compare(Object value1, Object value2)
/*      */   {
/*   95 */     return compareDates((DateTimeData)value1, (DateTimeData)value2, true);
/*      */   }
/*      */ 
/*      */   protected short compareDates(DateTimeData date1, DateTimeData date2, boolean strict)
/*      */   {
/*  109 */     if (date1.utc == date2.utc) {
/*  110 */       return compareOrder(date1, date2);
/*      */     }
/*      */ 
/*  114 */     DateTimeData tempDate = new DateTimeData(null, this);
/*      */ 
/*  116 */     if (date1.utc == 90)
/*      */     {
/*  120 */       cloneDate(date2, tempDate);
/*  121 */       tempDate.timezoneHr = 14;
/*  122 */       tempDate.timezoneMin = 0;
/*  123 */       tempDate.utc = 43;
/*  124 */       normalize(tempDate);
/*  125 */       short c1 = compareOrder(date1, tempDate);
/*  126 */       if (c1 == -1) {
/*  127 */         return c1;
/*      */       }
/*      */ 
/*  131 */       cloneDate(date2, tempDate);
/*  132 */       tempDate.timezoneHr = -14;
/*  133 */       tempDate.timezoneMin = 0;
/*  134 */       tempDate.utc = 45;
/*  135 */       normalize(tempDate);
/*  136 */       short c2 = compareOrder(date1, tempDate);
/*  137 */       if (c2 == 1) {
/*  138 */         return c2;
/*      */       }
/*  140 */       return 2;
/*      */     }
/*  142 */     if (date2.utc == 90)
/*      */     {
/*  146 */       cloneDate(date1, tempDate);
/*  147 */       tempDate.timezoneHr = -14;
/*  148 */       tempDate.timezoneMin = 0;
/*  149 */       tempDate.utc = 45;
/*      */ 
/*  153 */       normalize(tempDate);
/*  154 */       short c1 = compareOrder(tempDate, date2);
/*      */ 
/*  159 */       if (c1 == -1) {
/*  160 */         return c1;
/*      */       }
/*      */ 
/*  164 */       cloneDate(date1, tempDate);
/*  165 */       tempDate.timezoneHr = 14;
/*  166 */       tempDate.timezoneMin = 0;
/*  167 */       tempDate.utc = 43;
/*  168 */       normalize(tempDate);
/*  169 */       short c2 = compareOrder(tempDate, date2);
/*      */ 
/*  173 */       if (c2 == 1) {
/*  174 */         return c2;
/*      */       }
/*  176 */       return 2;
/*      */     }
/*  178 */     return 2;
/*      */   }
/*      */ 
/*      */   protected short compareOrder(DateTimeData date1, DateTimeData date2)
/*      */   {
/*  191 */     if (date1.position < 1) {
/*  192 */       if (date1.year < date2.year)
/*  193 */         return -1;
/*  194 */       if (date1.year > date2.year)
/*  195 */         return 1;
/*      */     }
/*  197 */     if (date1.position < 2) {
/*  198 */       if (date1.month < date2.month)
/*  199 */         return -1;
/*  200 */       if (date1.month > date2.month)
/*  201 */         return 1;
/*      */     }
/*  203 */     if (date1.day < date2.day)
/*  204 */       return -1;
/*  205 */     if (date1.day > date2.day)
/*  206 */       return 1;
/*  207 */     if (date1.hour < date2.hour)
/*  208 */       return -1;
/*  209 */     if (date1.hour > date2.hour)
/*  210 */       return 1;
/*  211 */     if (date1.minute < date2.minute)
/*  212 */       return -1;
/*  213 */     if (date1.minute > date2.minute)
/*  214 */       return 1;
/*  215 */     if (date1.second < date2.second)
/*  216 */       return -1;
/*  217 */     if (date1.second > date2.second)
/*  218 */       return 1;
/*  219 */     if (date1.utc < date2.utc)
/*  220 */       return -1;
/*  221 */     if (date1.utc > date2.utc)
/*  222 */       return 1;
/*  223 */     return 0;
/*      */   }
/*      */ 
/*      */   protected void getTime(String buffer, int start, int end, DateTimeData data)
/*      */     throws RuntimeException
/*      */   {
/*  236 */     int stop = start + 2;
/*      */ 
/*  239 */     data.hour = parseInt(buffer, start, stop);
/*      */ 
/*  243 */     if (buffer.charAt(stop++) != ':') {
/*  244 */       throw new RuntimeException("Error in parsing time zone");
/*      */     }
/*  246 */     start = stop;
/*  247 */     stop += 2;
/*  248 */     data.minute = parseInt(buffer, start, stop);
/*      */ 
/*  251 */     if (buffer.charAt(stop++) != ':') {
/*  252 */       throw new RuntimeException("Error in parsing time zone");
/*      */     }
/*      */ 
/*  256 */     int sign = findUTCSign(buffer, start, end);
/*      */ 
/*  259 */     start = stop;
/*  260 */     stop = sign < 0 ? end : sign;
/*  261 */     data.second = parseSecond(buffer, start, stop);
/*      */ 
/*  264 */     if (sign > 0)
/*  265 */       getTimeZone(buffer, data, sign, end);
/*      */   }
/*      */ 
/*      */   protected int getDate(String buffer, int start, int end, DateTimeData date)
/*      */     throws RuntimeException
/*      */   {
/*  280 */     start = getYearMonth(buffer, start, end, date);
/*      */ 
/*  282 */     if (buffer.charAt(start++) != '-') {
/*  283 */       throw new RuntimeException("CCYY-MM must be followed by '-' sign");
/*      */     }
/*  285 */     int stop = start + 2;
/*  286 */     date.day = parseInt(buffer, start, stop);
/*  287 */     return stop;
/*      */   }
/*      */ 
/*      */   protected int getYearMonth(String buffer, int start, int end, DateTimeData date)
/*      */     throws RuntimeException
/*      */   {
/*  301 */     if (buffer.charAt(0) == '-')
/*      */     {
/*  305 */       start++;
/*      */     }
/*  307 */     int i = indexOf(buffer, start, end, '-');
/*  308 */     if (i == -1) throw new RuntimeException("Year separator is missing or misplaced");
/*  309 */     int length = i - start;
/*  310 */     if (length < 4) {
/*  311 */       throw new RuntimeException("Year must have 'CCYY' format");
/*      */     }
/*  313 */     if ((length > 4) && (buffer.charAt(start) == '0')) {
/*  314 */       throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
/*      */     }
/*  316 */     date.year = parseIntYear(buffer, i);
/*  317 */     if (buffer.charAt(i) != '-') {
/*  318 */       throw new RuntimeException("CCYY must be followed by '-' sign");
/*      */     }
/*  320 */     i++; start = i;
/*  321 */     i = start + 2;
/*  322 */     date.month = parseInt(buffer, start, i);
/*  323 */     return i;
/*      */   }
/*      */ 
/*      */   protected void parseTimeZone(String buffer, int start, int end, DateTimeData date)
/*      */     throws RuntimeException
/*      */   {
/*  338 */     if (start < end) {
/*  339 */       if (!isNextCharUTCSign(buffer, start, end)) {
/*  340 */         throw new RuntimeException("Error in month parsing");
/*      */       }
/*      */ 
/*  343 */       getTimeZone(buffer, date, start, end);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void getTimeZone(String buffer, DateTimeData data, int sign, int end)
/*      */     throws RuntimeException
/*      */   {
/*  356 */     data.utc = buffer.charAt(sign);
/*      */ 
/*  358 */     if (buffer.charAt(sign) == 'Z') {
/*  359 */       if (end > ++sign) {
/*  360 */         throw new RuntimeException("Error in parsing time zone");
/*      */       }
/*  362 */       return;
/*      */     }
/*  364 */     if (sign <= end - 6)
/*      */     {
/*  366 */       int negate = buffer.charAt(sign) == '-' ? -1 : 1;
/*      */ 
/*  368 */       sign++; int stop = sign + 2;
/*  369 */       data.timezoneHr = (negate * parseInt(buffer, sign, stop));
/*  370 */       if (buffer.charAt(stop++) != ':') {
/*  371 */         throw new RuntimeException("Error in parsing time zone");
/*      */       }
/*      */ 
/*  375 */       data.timezoneMin = (negate * parseInt(buffer, stop, stop + 2));
/*      */ 
/*  377 */       if (stop + 2 != end) {
/*  378 */         throw new RuntimeException("Error in parsing time zone");
/*      */       }
/*  380 */       if ((data.timezoneHr != 0) || (data.timezoneMin != 0))
/*  381 */         data.normalized = false;
/*      */     }
/*      */     else {
/*  384 */       throw new RuntimeException("Error in parsing time zone");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int indexOf(String buffer, int start, int end, char ch)
/*      */   {
/*  400 */     for (int i = start; i < end; i++) {
/*  401 */       if (buffer.charAt(i) == ch) {
/*  402 */         return i;
/*      */       }
/*      */     }
/*  405 */     return -1;
/*      */   }
/*      */ 
/*      */   protected void validateDateTime(DateTimeData data)
/*      */   {
/*  422 */     if (data.year == 0) {
/*  423 */       throw new RuntimeException("The year \"0000\" is an illegal year value");
/*      */     }
/*      */ 
/*  427 */     if ((data.month < 1) || (data.month > 12)) {
/*  428 */       throw new RuntimeException("The month must have values 1 to 12");
/*      */     }
/*      */ 
/*  433 */     if ((data.day > maxDayInMonthFor(data.year, data.month)) || (data.day < 1)) {
/*  434 */       throw new RuntimeException("The day must have values 1 to 31");
/*      */     }
/*      */ 
/*  438 */     if ((data.hour > 23) || (data.hour < 0)) {
/*  439 */       if ((data.hour == 24) && (data.minute == 0) && (data.second == 0.0D)) {
/*  440 */         data.hour = 0;
/*  441 */         if (++data.day > maxDayInMonthFor(data.year, data.month)) {
/*  442 */           data.day = 1;
/*  443 */           if (++data.month > 12) {
/*  444 */             data.month = 1;
/*      */ 
/*  448 */             if (++data.year == 0)
/*  449 */               data.year = 1;
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  455 */         throw new RuntimeException("Hour must have values 0-23, unless 24:00:00");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  460 */     if ((data.minute > 59) || (data.minute < 0)) {
/*  461 */       throw new RuntimeException("Minute must have values 0-59");
/*      */     }
/*      */ 
/*  465 */     if ((data.second >= 60.0D) || (data.second < 0.0D)) {
/*  466 */       throw new RuntimeException("Second must have values 0-59");
/*      */     }
/*      */ 
/*  471 */     if ((data.timezoneHr > 14) || (data.timezoneHr < -14)) {
/*  472 */       throw new RuntimeException("Time zone should have range -14:00 to +14:00");
/*      */     }
/*      */ 
/*  475 */     if (((data.timezoneHr == 14) || (data.timezoneHr == -14)) && (data.timezoneMin != 0))
/*  476 */       throw new RuntimeException("Time zone should have range -14:00 to +14:00");
/*  477 */     if ((data.timezoneMin > 59) || (data.timezoneMin < -59))
/*  478 */       throw new RuntimeException("Minute must have values 0-59");
/*      */   }
/*      */ 
/*      */   protected int findUTCSign(String buffer, int start, int end)
/*      */   {
/*  492 */     for (int i = start; i < end; i++) {
/*  493 */       int c = buffer.charAt(i);
/*  494 */       if ((c == 90) || (c == 43) || (c == 45)) {
/*  495 */         return i;
/*      */       }
/*      */     }
/*      */ 
/*  499 */     return -1;
/*      */   }
/*      */ 
/*      */   protected final boolean isNextCharUTCSign(String buffer, int start, int end)
/*      */   {
/*  506 */     if (start < end) {
/*  507 */       char c = buffer.charAt(start);
/*  508 */       return (c == 'Z') || (c == '+') || (c == '-');
/*      */     }
/*  510 */     return false;
/*      */   }
/*      */ 
/*      */   protected int parseInt(String buffer, int start, int end)
/*      */     throws NumberFormatException
/*      */   {
/*  524 */     int radix = 10;
/*  525 */     int result = 0;
/*  526 */     int digit = 0;
/*  527 */     int limit = -2147483647;
/*  528 */     int multmin = limit / radix;
/*  529 */     int i = start;
/*      */     do {
/*  531 */       digit = getDigit(buffer.charAt(i));
/*  532 */       if (digit < 0) throw new NumberFormatException("'" + buffer + "' has wrong format");
/*  533 */       if (result < multmin) throw new NumberFormatException("'" + buffer + "' has wrong format");
/*  534 */       result *= radix;
/*  535 */       if (result < limit + digit) throw new NumberFormatException("'" + buffer + "' has wrong format");
/*  536 */       result -= digit;
/*      */ 
/*  538 */       i++; } while (i < end);
/*  539 */     return -result;
/*      */   }
/*      */ 
/*      */   protected int parseIntYear(String buffer, int end)
/*      */   {
/*  544 */     int radix = 10;
/*  545 */     int result = 0;
/*  546 */     boolean negative = false;
/*  547 */     int i = 0;
/*      */ 
/*  550 */     int digit = 0;
/*      */     int limit;
/*  552 */     if (buffer.charAt(0) == '-') {
/*  553 */       negative = true;
/*  554 */       int limit = -2147483648;
/*  555 */       i++;
/*      */     }
/*      */     else
/*      */     {
/*  559 */       limit = -2147483647;
/*      */     }
/*  561 */     int multmin = limit / radix;
/*  562 */     while (i < end)
/*      */     {
/*  564 */       digit = getDigit(buffer.charAt(i++));
/*  565 */       if (digit < 0) throw new NumberFormatException("'" + buffer + "' has wrong format");
/*  566 */       if (result < multmin) throw new NumberFormatException("'" + buffer + "' has wrong format");
/*  567 */       result *= radix;
/*  568 */       if (result < limit + digit) throw new NumberFormatException("'" + buffer + "' has wrong format");
/*  569 */       result -= digit;
/*      */     }
/*      */ 
/*  572 */     if (negative)
/*      */     {
/*  574 */       if (i > 1) return result;
/*  575 */       throw new NumberFormatException("'" + buffer + "' has wrong format");
/*      */     }
/*  577 */     return -result;
/*      */   }
/*      */ 
/*      */   protected void normalize(DateTimeData date)
/*      */   {
/*  593 */     int negate = -1;
/*      */ 
/*  599 */     int temp = date.minute + negate * date.timezoneMin;
/*  600 */     int carry = fQuotient(temp, 60);
/*  601 */     date.minute = mod(temp, 60, carry);
/*      */ 
/*  607 */     temp = date.hour + negate * date.timezoneHr + carry;
/*  608 */     carry = fQuotient(temp, 24);
/*  609 */     date.hour = mod(temp, 24, carry);
/*      */ 
/*  615 */     date.day += carry;
/*      */     while (true)
/*      */     {
/*  618 */       temp = maxDayInMonthFor(date.year, date.month);
/*  619 */       if (date.day < 1) {
/*  620 */         date.day += maxDayInMonthFor(date.year, date.month - 1);
/*  621 */         carry = -1;
/*      */       } else {
/*  623 */         if (date.day <= temp) break;
/*  624 */         date.day -= temp;
/*  625 */         carry = 1;
/*      */       }
/*      */ 
/*  630 */       temp = date.month + carry;
/*  631 */       date.month = modulo(temp, 1, 13);
/*  632 */       date.year += fQuotient(temp, 1, 13);
/*  633 */       if (date.year == 0) {
/*  634 */         date.year = ((date.timezoneHr < 0) || (date.timezoneMin < 0) ? 1 : -1);
/*      */       }
/*      */     }
/*  637 */     date.utc = 90;
/*      */   }
/*      */ 
/*      */   protected void saveUnnormalized(DateTimeData date)
/*      */   {
/*  645 */     date.unNormYear = date.year;
/*  646 */     date.unNormMonth = date.month;
/*  647 */     date.unNormDay = date.day;
/*  648 */     date.unNormHour = date.hour;
/*  649 */     date.unNormMinute = date.minute;
/*  650 */     date.unNormSecond = date.second;
/*      */   }
/*      */ 
/*      */   protected void resetDateObj(DateTimeData data)
/*      */   {
/*  659 */     data.year = 0;
/*  660 */     data.month = 0;
/*  661 */     data.day = 0;
/*  662 */     data.hour = 0;
/*  663 */     data.minute = 0;
/*  664 */     data.second = 0.0D;
/*  665 */     data.utc = 0;
/*  666 */     data.timezoneHr = 0;
/*  667 */     data.timezoneMin = 0;
/*      */   }
/*      */ 
/*      */   protected int maxDayInMonthFor(int year, int month)
/*      */   {
/*  680 */     if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
/*  681 */       return 30;
/*      */     }
/*  683 */     if (month == 2) {
/*  684 */       if (isLeapYear(year)) {
/*  685 */         return 29;
/*      */       }
/*      */ 
/*  688 */       return 28;
/*      */     }
/*      */ 
/*  692 */     return 31;
/*      */   }
/*      */ 
/*      */   private boolean isLeapYear(int year)
/*      */   {
/*  699 */     return (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0));
/*      */   }
/*      */ 
/*      */   protected int mod(int a, int b, int quotient)
/*      */   {
/*  707 */     return a - quotient * b;
/*      */   }
/*      */ 
/*      */   protected int fQuotient(int a, int b)
/*      */   {
/*  716 */     return (int)Math.floor(a / b);
/*      */   }
/*      */ 
/*      */   protected int modulo(int temp, int low, int high)
/*      */   {
/*  724 */     int a = temp - low;
/*  725 */     int b = high - low;
/*  726 */     return mod(a, b, fQuotient(a, b)) + low;
/*      */   }
/*      */ 
/*      */   protected int fQuotient(int temp, int low, int high)
/*      */   {
/*  735 */     return fQuotient(temp - low, high - low);
/*      */   }
/*      */ 
/*      */   protected String dateToString(DateTimeData date)
/*      */   {
/*  740 */     StringBuffer message = new StringBuffer(25);
/*  741 */     append(message, date.year, 4);
/*  742 */     message.append('-');
/*  743 */     append(message, date.month, 2);
/*  744 */     message.append('-');
/*  745 */     append(message, date.day, 2);
/*  746 */     message.append('T');
/*  747 */     append(message, date.hour, 2);
/*  748 */     message.append(':');
/*  749 */     append(message, date.minute, 2);
/*  750 */     message.append(':');
/*  751 */     append(message, date.second);
/*  752 */     append(message, (char)date.utc, 0);
/*  753 */     return message.toString();
/*      */   }
/*      */ 
/*      */   protected final void append(StringBuffer message, int value, int nch) {
/*  757 */     if (value == -2147483648) {
/*  758 */       message.append(value);
/*  759 */       return;
/*      */     }
/*  761 */     if (value < 0) {
/*  762 */       message.append('-');
/*  763 */       value = -value;
/*      */     }
/*  765 */     if (nch == 4) {
/*  766 */       if (value < 10)
/*  767 */         message.append("000");
/*  768 */       else if (value < 100)
/*  769 */         message.append("00");
/*  770 */       else if (value < 1000)
/*  771 */         message.append('0');
/*  772 */       message.append(value);
/*      */     }
/*  774 */     else if (nch == 2) {
/*  775 */       if (value < 10)
/*  776 */         message.append('0');
/*  777 */       message.append(value);
/*      */     }
/*  780 */     else if (value != 0) {
/*  781 */       message.append((char)value);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final void append(StringBuffer message, double value) {
/*  786 */     if (value < 0.0D) {
/*  787 */       message.append('-');
/*  788 */       value = -value;
/*      */     }
/*  790 */     if (value < 10.0D) {
/*  791 */       message.append('0');
/*      */     }
/*  793 */     append2(message, value);
/*      */   }
/*      */ 
/*      */   protected final void append2(StringBuffer message, double value) {
/*  797 */     int intValue = (int)value;
/*  798 */     if (value == intValue) {
/*  799 */       message.append(intValue);
/*      */     }
/*      */     else
/*  802 */       append3(message, value);
/*      */   }
/*      */ 
/*      */   private void append3(StringBuffer message, double value)
/*      */   {
/*  807 */     String d = String.valueOf(value);
/*  808 */     int eIndex = d.indexOf('E');
/*  809 */     if (eIndex == -1) {
/*  810 */       message.append(d);
/*  811 */       return;
/*      */     }
/*      */ 
/*  814 */     if (value < 1.0D)
/*      */     {
/*      */       int exp;
/*      */       try {
/*  818 */         exp = parseInt(d, eIndex + 2, d.length());
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  823 */         message.append(d);
/*  824 */         return;
/*      */       }
/*  826 */       message.append("0.");
/*  827 */       for (int i = 1; i < exp; i++) {
/*  828 */         message.append('0');
/*      */       }
/*      */ 
/*  831 */       int end = eIndex - 1;
/*  832 */       while (end > 0) {
/*  833 */         char c = d.charAt(end);
/*  834 */         if (c != '0') {
/*      */           break;
/*      */         }
/*  837 */         end--;
/*      */       }
/*      */ 
/*  840 */       for (int i = 0; i <= end; i++) {
/*  841 */         char c = d.charAt(i);
/*  842 */         if (c != '.')
/*  843 */           message.append(c);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       int exp;
/*      */       try
/*      */       {
/*  851 */         exp = parseInt(d, eIndex + 1, d.length());
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  856 */         message.append(d);
/*  857 */         return;
/*      */       }
/*  859 */       int integerEnd = exp + 2;
/*  860 */       for (int i = 0; i < eIndex; i++) {
/*  861 */         char c = d.charAt(i);
/*  862 */         if (c != '.') {
/*  863 */           if (i == integerEnd) {
/*  864 */             message.append('.');
/*      */           }
/*  866 */           message.append(c);
/*      */         }
/*      */       }
/*      */ 
/*  870 */       for (int i = integerEnd - eIndex; i > 0; i--)
/*  871 */         message.append('0');
/*      */     }
/*      */   }
/*      */ 
/*      */   protected double parseSecond(String buffer, int start, int end)
/*      */     throws NumberFormatException
/*      */   {
/*  878 */     int dot = -1;
/*  879 */     for (int i = start; i < end; i++) {
/*  880 */       char ch = buffer.charAt(i);
/*  881 */       if (ch == '.')
/*  882 */         dot = i;
/*  883 */       else if ((ch > '9') || (ch < '0'))
/*  884 */         throw new NumberFormatException("'" + buffer + "' has wrong format");
/*      */     }
/*  886 */     if (dot == -1) {
/*  887 */       if (start + 2 != end)
/*  888 */         throw new NumberFormatException("'" + buffer + "' has wrong format");
/*      */     }
/*  890 */     else if ((start + 2 != dot) || (dot + 1 == end)) {
/*  891 */       throw new NumberFormatException("'" + buffer + "' has wrong format");
/*      */     }
/*  893 */     return Double.parseDouble(buffer.substring(start, end));
/*      */   }
/*      */ 
/*      */   private void cloneDate(DateTimeData finalValue, DateTimeData tempDate)
/*      */   {
/*  901 */     tempDate.year = finalValue.year;
/*  902 */     tempDate.month = finalValue.month;
/*  903 */     tempDate.day = finalValue.day;
/*  904 */     tempDate.hour = finalValue.hour;
/*  905 */     tempDate.minute = finalValue.minute;
/*  906 */     tempDate.second = finalValue.second;
/*  907 */     tempDate.utc = finalValue.utc;
/*  908 */     tempDate.timezoneHr = finalValue.timezoneHr;
/*  909 */     tempDate.timezoneMin = finalValue.timezoneMin;
/*      */   }
/*      */ 
/*      */   protected XMLGregorianCalendar getXMLGregorianCalendar(DateTimeData data)
/*      */   {
/* 1087 */     return null;
/*      */   }
/*      */ 
/*      */   protected Duration getDuration(DateTimeData data) {
/* 1091 */     return null;
/*      */   }
/*      */ 
/*      */   protected final BigDecimal getFractionalSecondsAsBigDecimal(DateTimeData data) {
/* 1095 */     StringBuffer buf = new StringBuffer();
/* 1096 */     append3(buf, data.unNormSecond);
/* 1097 */     String value = buf.toString();
/* 1098 */     int index = value.indexOf('.');
/* 1099 */     if (index == -1) {
/* 1100 */       return null;
/*      */     }
/* 1102 */     value = value.substring(index);
/* 1103 */     BigDecimal _val = new BigDecimal(value);
/* 1104 */     if (_val.compareTo(BigDecimal.valueOf(0L)) == 0) {
/* 1105 */       return null;
/*      */     }
/* 1107 */     return _val;
/*      */   }
/*      */ 
/*      */   static final class DateTimeData
/*      */     implements XSDateTime
/*      */   {
/*      */     int year;
/*      */     int month;
/*      */     int day;
/*      */     int hour;
/*      */     int minute;
/*      */     int utc;
/*      */     double second;
/*      */     int timezoneHr;
/*      */     int timezoneMin;
/*      */     private String originalValue;
/*  920 */     boolean normalized = true;
/*      */     int unNormYear;
/*      */     int unNormMonth;
/*      */     int unNormDay;
/*      */     int unNormHour;
/*      */     int unNormMinute;
/*      */     double unNormSecond;
/*      */     int position;
/*      */     final AbstractDateTimeDV type;
/*      */     private String canonical;
/*      */ 
/*      */     public DateTimeData(String originalValue, AbstractDateTimeDV type)
/*      */     {
/*  938 */       this.originalValue = originalValue;
/*  939 */       this.type = type;
/*      */     }
/*      */ 
/*      */     public DateTimeData(int year, int month, int day, int hour, int minute, double second, int utc, String originalValue, boolean normalized, AbstractDateTimeDV type) {
/*  943 */       this.year = year;
/*  944 */       this.month = month;
/*  945 */       this.day = day;
/*  946 */       this.hour = hour;
/*  947 */       this.minute = minute;
/*  948 */       this.second = second;
/*  949 */       this.utc = utc;
/*  950 */       this.type = type;
/*  951 */       this.originalValue = originalValue;
/*      */     }
/*      */     public boolean equals(Object obj) {
/*  954 */       if (!(obj instanceof DateTimeData))
/*  955 */         return false;
/*  956 */       return this.type.compareDates(this, (DateTimeData)obj, true) == 0;
/*      */     }
/*      */     public synchronized String toString() {
/*  959 */       if (this.canonical == null) {
/*  960 */         this.canonical = this.type.dateToString(this);
/*      */       }
/*  962 */       return this.canonical;
/*      */     }
/*      */ 
/*      */     public int getYears()
/*      */     {
/*  968 */       if ((this.type instanceof DurationDV))
/*  969 */         return 0;
/*  970 */       return this.normalized ? this.year : this.unNormYear;
/*      */     }
/*      */ 
/*      */     public int getMonths()
/*      */     {
/*  976 */       if ((this.type instanceof DurationDV)) {
/*  977 */         return this.year * 12 + this.month;
/*      */       }
/*  979 */       return this.normalized ? this.month : this.unNormMonth;
/*      */     }
/*      */ 
/*      */     public int getDays()
/*      */     {
/*  985 */       if ((this.type instanceof DurationDV))
/*  986 */         return 0;
/*  987 */       return this.normalized ? this.day : this.unNormDay;
/*      */     }
/*      */ 
/*      */     public int getHours()
/*      */     {
/*  993 */       if ((this.type instanceof DurationDV))
/*  994 */         return 0;
/*  995 */       return this.normalized ? this.hour : this.unNormHour;
/*      */     }
/*      */ 
/*      */     public int getMinutes()
/*      */     {
/* 1001 */       if ((this.type instanceof DurationDV))
/* 1002 */         return 0;
/* 1003 */       return this.normalized ? this.minute : this.unNormMinute;
/*      */     }
/*      */ 
/*      */     public double getSeconds()
/*      */     {
/* 1009 */       if ((this.type instanceof DurationDV)) {
/* 1010 */         return this.day * 24 * 60 * 60 + this.hour * 60 * 60 + this.minute * 60 + this.second;
/*      */       }
/* 1012 */       return this.normalized ? this.second : this.unNormSecond;
/*      */     }
/*      */ 
/*      */     public boolean hasTimeZone()
/*      */     {
/* 1018 */       return this.utc != 0;
/*      */     }
/*      */ 
/*      */     public int getTimeZoneHours()
/*      */     {
/* 1024 */       return this.timezoneHr;
/*      */     }
/*      */ 
/*      */     public int getTimeZoneMinutes()
/*      */     {
/* 1030 */       return this.timezoneMin;
/*      */     }
/*      */ 
/*      */     public String getLexicalValue()
/*      */     {
/* 1036 */       return this.originalValue;
/*      */     }
/*      */ 
/*      */     public XSDateTime normalize()
/*      */     {
/* 1042 */       if (!this.normalized) {
/* 1043 */         DateTimeData dt = (DateTimeData)clone();
/* 1044 */         dt.normalized = true;
/* 1045 */         return dt;
/*      */       }
/* 1047 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean isNormalized()
/*      */     {
/* 1053 */       return this.normalized;
/*      */     }
/*      */ 
/*      */     public Object clone() {
/* 1057 */       DateTimeData dt = new DateTimeData(this.year, this.month, this.day, this.hour, this.minute, this.second, this.utc, this.originalValue, this.normalized, this.type);
/*      */ 
/* 1059 */       dt.canonical = this.canonical;
/* 1060 */       dt.position = this.position;
/* 1061 */       dt.timezoneHr = this.timezoneHr;
/* 1062 */       dt.timezoneMin = this.timezoneMin;
/* 1063 */       dt.unNormYear = this.unNormYear;
/* 1064 */       dt.unNormMonth = this.unNormMonth;
/* 1065 */       dt.unNormDay = this.unNormDay;
/* 1066 */       dt.unNormHour = this.unNormHour;
/* 1067 */       dt.unNormMinute = this.unNormMinute;
/* 1068 */       dt.unNormSecond = this.unNormSecond;
/* 1069 */       return dt;
/*      */     }
/*      */ 
/*      */     public XMLGregorianCalendar getXMLGregorianCalendar()
/*      */     {
/* 1076 */       return this.type.getXMLGregorianCalendar(this);
/*      */     }
/*      */ 
/*      */     public Duration getDuration()
/*      */     {
/* 1082 */       return this.type.getDuration(this);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.dv.xs.AbstractDateTimeDV
 * JD-Core Version:    0.6.2
 */