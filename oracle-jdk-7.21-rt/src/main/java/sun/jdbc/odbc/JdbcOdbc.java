/*      */ package sun.jdbc.odbc;
/*      */ 
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.security.AccessController;
/*      */ import java.sql.DataTruncation;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import sun.security.action.LoadLibraryAction;
/*      */ 
/*      */ public class JdbcOdbc extends JdbcOdbcObject
/*      */ {
/*      */   public static final int MajorVersion = 2;
/*      */   public static final int MinorVersion = 1;
/*      */   public String charSet;
/*      */   public String odbcDriverName;
/*      */   private static Map hstmtMap;
/* 7165 */   public JdbcOdbcTracer tracer = new JdbcOdbcTracer();
/*      */ 
/*      */   public static void addHstmt(long paramLong1, long paramLong2)
/*      */   {
/*   34 */     hstmtMap.put(new Long(paramLong1), new Long(paramLong2));
/*      */   }
/*      */ 
/*      */   JdbcOdbc(JdbcOdbcTracer paramJdbcOdbcTracer, String paramString)
/*      */     throws SQLException
/*      */   {
/*   50 */     this.tracer = paramJdbcOdbcTracer;
/*      */     try
/*      */     {
/*   54 */       if (paramJdbcOdbcTracer.isTracing()) {
/*   55 */         java.util.Date localDate = new java.util.Date();
/*      */ 
/*   57 */         String str = "";
/*   58 */         int i = 1;
/*      */ 
/*   63 */         if (i < 1000) str = str + "0";
/*   64 */         if (i < 100) str = str + "0";
/*   65 */         if (i < 10) str = str + "0";
/*   66 */         str = str + "" + i;
/*      */ 
/*   68 */         paramJdbcOdbcTracer.trace("JDBC to ODBC Bridge 2." + str);
/*   69 */         paramJdbcOdbcTracer.trace("Current Date/Time: " + localDate.toString());
/*   70 */         paramJdbcOdbcTracer.trace("Loading " + paramString + "JdbcOdbc library");
/*      */       }
/*   72 */       AccessController.doPrivileged(new LoadLibraryAction(paramString + "JdbcOdbc"));
/*      */ 
/*   76 */       if (hstmtMap == null) {
/*   77 */         hstmtMap = Collections.synchronizedMap(new HashMap());
/*      */       }
/*      */     }
/*      */     catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
/*      */     {
/*   82 */       if (paramJdbcOdbcTracer.isTracing()) {
/*   83 */         paramJdbcOdbcTracer.trace("Unable to load " + paramString + "JdbcOdbc library");
/*      */       }
/*      */ 
/*   86 */       throw new SQLException("Unable to load " + paramString + "JdbcOdbc library");
/*      */     }
/*      */   }
/*      */ 
/*      */   public long SQLAllocConnect(long paramLong)
/*      */     throws SQLException
/*      */   {
/*  104 */     long l = 0L;
/*      */ 
/*  107 */     if (this.tracer.isTracing()) {
/*  108 */       this.tracer.trace("Allocating Connection handle (SQLAllocConnect)");
/*      */     }
/*      */ 
/*  111 */     byte[] arrayOfByte = new byte[1];
/*  112 */     l = allocConnect(paramLong, arrayOfByte);
/*      */ 
/*  114 */     if (arrayOfByte[0] != 0) {
/*  115 */       throwGenericSQLException();
/*      */     }
/*  118 */     else if (this.tracer.isTracing()) {
/*  119 */       this.tracer.trace("hDbc=" + l);
/*      */     }
/*      */ 
/*  122 */     return l;
/*      */   }
/*      */ 
/*      */   public long SQLAllocEnv()
/*      */     throws SQLException
/*      */   {
/*  132 */     long l = 0L;
/*      */ 
/*  135 */     if (this.tracer.isTracing()) {
/*  136 */       this.tracer.trace("Allocating Environment handle (SQLAllocEnv)");
/*      */     }
/*      */ 
/*  139 */     byte[] arrayOfByte = new byte[1];
/*  140 */     l = allocEnv(arrayOfByte);
/*      */ 
/*  142 */     if (arrayOfByte[0] != 0) {
/*  143 */       throwGenericSQLException();
/*      */     }
/*  146 */     else if (this.tracer.isTracing()) {
/*  147 */       this.tracer.trace("hEnv=" + l);
/*      */     }
/*      */ 
/*  150 */     return l;
/*      */   }
/*      */ 
/*      */   public long SQLAllocStmt(long paramLong)
/*      */     throws SQLException
/*      */   {
/*  161 */     long l = 0L;
/*      */ 
/*  164 */     if (this.tracer.isTracing()) {
/*  165 */       this.tracer.trace("Allocating Statement Handle (SQLAllocStmt), hDbc=" + paramLong);
/*      */     }
/*      */ 
/*  168 */     byte[] arrayOfByte = new byte[1];
/*  169 */     l = allocStmt(paramLong, arrayOfByte);
/*      */ 
/*  171 */     if (arrayOfByte[0] != 0) {
/*  172 */       throwGenericSQLException();
/*      */     }
/*  175 */     else if (this.tracer.isTracing()) {
/*  176 */       this.tracer.trace("hStmt=" + l);
/*      */     }
/*      */ 
/*  179 */     addHstmt(l, paramLong);
/*  180 */     return l;
/*      */   }
/*      */ 
/*      */   public void SQLBindColAtExec(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  198 */     if (this.tracer.isTracing()) {
/*  199 */       this.tracer.trace("Binding Column DATA_AT_EXEC (SQLBindCol), hStmt=" + paramLong + ", icol=" + paramInt1 + ", SQLtype=" + paramInt2);
/*      */     }
/*      */ 
/*  202 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  204 */     bindColAtExec(paramLong, paramInt1, paramInt2, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfLong, arrayOfByte);
/*      */ 
/*  206 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  209 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindColBinary(long paramLong, int paramInt1, Object[] paramArrayOfObject, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  232 */     if (this.tracer.isTracing()) {
/*  233 */       this.tracer.trace("Bind column binary (SQLBindColBinary), hStmt=" + paramLong + ", icol=" + paramInt1);
/*      */     }
/*      */ 
/*  236 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  238 */     bindColBinary(paramLong, paramInt1, paramArrayOfObject, paramArrayOfByte1, paramInt2, paramArrayOfByte2, paramArrayOfLong, arrayOfByte);
/*      */ 
/*  240 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  243 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindColDate(long paramLong, int paramInt, Object[] paramArrayOfObject, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  263 */     if (this.tracer.isTracing()) {
/*  264 */       this.tracer.trace("Bound Column Date (SQLBindColDate), hStmt=" + paramLong + ", icol=" + paramInt);
/*      */     }
/*      */ 
/*  269 */     java.sql.Date localDate = null;
/*      */ 
/*  271 */     int i = paramArrayOfObject.length;
/*      */ 
/*  273 */     int[] arrayOfInt1 = new int[i];
/*  274 */     int[] arrayOfInt2 = new int[i];
/*  275 */     int[] arrayOfInt3 = new int[i];
/*      */ 
/*  277 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  279 */     Calendar localCalendar = Calendar.getInstance();
/*      */ 
/*  281 */     for (int j = 0; j < i; j++)
/*      */     {
/*  283 */       if (paramArrayOfObject[j] != null)
/*      */       {
/*  285 */         localDate = (java.sql.Date)paramArrayOfObject[j];
/*      */ 
/*  287 */         localCalendar.setTime(localDate);
/*      */ 
/*  289 */         arrayOfInt1[j] = localCalendar.get(1);
/*  290 */         arrayOfInt2[j] = (localCalendar.get(2) + 1);
/*  291 */         arrayOfInt3[j] = localCalendar.get(5);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  296 */     bindColDate(paramLong, paramInt, arrayOfInt1, arrayOfInt2, arrayOfInt3, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfLong, arrayOfByte);
/*      */ 
/*  305 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  308 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindColDefault(long paramLong, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*      */     throws SQLException
/*      */   {
/*  328 */     if (this.tracer.isTracing()) {
/*  329 */       this.tracer.trace("Binding default column (SQLBindCol), hStmt=" + paramLong + ", ipar=" + paramInt + ", \t\t\tlength=" + paramArrayOfByte1.length);
/*      */     }
/*      */ 
/*  332 */     byte[] arrayOfByte = new byte[1];
/*  333 */     bindColDefault(paramLong, paramInt, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte);
/*      */ 
/*  335 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  338 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindColDouble(long paramLong, int paramInt, Object[] paramArrayOfObject, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  358 */     if (this.tracer.isTracing()) {
/*  359 */       this.tracer.trace("Bind column Double (SQLBindColDouble), hStmt=" + paramLong + ", icol=" + paramInt);
/*      */     }
/*      */ 
/*  364 */     double[] arrayOfDouble = new double[paramArrayOfObject.length];
/*      */ 
/*  366 */     for (int i = 0; i < paramArrayOfObject.length; i++)
/*      */     {
/*  368 */       if (paramArrayOfObject[i] != null) {
/*  369 */         arrayOfDouble[i] = ((Double)paramArrayOfObject[i]).doubleValue();
/*      */       }
/*      */     }
/*  372 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  374 */     bindColDouble(paramLong, paramInt, arrayOfDouble, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfLong, arrayOfByte);
/*      */ 
/*  376 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  379 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindColFloat(long paramLong, int paramInt, Object[] paramArrayOfObject, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  402 */     float[] arrayOfFloat = new float[paramArrayOfObject.length];
/*      */ 
/*  404 */     for (int i = 0; i < paramArrayOfObject.length; i++)
/*      */     {
/*  406 */       if (paramArrayOfObject[i] != null) {
/*  407 */         arrayOfFloat[i] = ((Float)paramArrayOfObject[i]).floatValue();
/*      */       }
/*      */     }
/*      */ 
/*  411 */     if (this.tracer.isTracing()) {
/*  412 */       this.tracer.trace("Binding default column (SQLBindCol Float), hStmt=" + paramLong + ", icol=" + paramInt);
/*      */     }
/*      */ 
/*  415 */     byte[] arrayOfByte = new byte[1];
/*  416 */     bindColFloat(paramLong, paramInt, arrayOfFloat, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfLong, arrayOfByte);
/*      */ 
/*  418 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  421 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindColInteger(long paramLong, int paramInt, Object[] paramArrayOfObject, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  442 */     if (this.tracer.isTracing()) {
/*  443 */       this.tracer.trace("Binding default column (SQLBindCol Integer), hStmt=" + paramLong + ", icol=" + paramInt);
/*      */     }
/*      */ 
/*  446 */     int[] arrayOfInt = new int[paramArrayOfObject.length];
/*      */ 
/*  448 */     for (int i = 0; i < paramArrayOfObject.length; i++)
/*      */     {
/*  450 */       if (paramArrayOfObject[i] != null) {
/*  451 */         arrayOfInt[i] = ((Integer)paramArrayOfObject[i]).intValue();
/*      */       }
/*      */     }
/*  454 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  456 */     bindColInteger(paramLong, paramInt, arrayOfInt, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfLong, arrayOfByte);
/*      */ 
/*  458 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  461 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindColString(long paramLong, int paramInt1, int paramInt2, Object[] paramArrayOfObject, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  485 */     if (this.tracer.isTracing()) {
/*  486 */       this.tracer.trace("Binding string/decimal Column (SQLBindColString), hStmt=" + paramLong + ", icol=" + paramInt1 + ", SQLtype=" + paramInt2 + ", rgbValue=" + paramArrayOfObject);
/*      */     }
/*      */ 
/*  491 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  493 */     bindColString(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramArrayOfObject, paramInt3, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfLong, arrayOfByte);
/*      */ 
/*  496 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  499 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindColTime(long paramLong, int paramInt, Object[] paramArrayOfObject, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  519 */     if (this.tracer.isTracing()) {
/*  520 */       this.tracer.trace("Bind column Time (SQLBindColTime), hStmt=" + paramLong + ", icol=" + paramInt);
/*      */     }
/*      */ 
/*  527 */     Time localTime = null;
/*      */ 
/*  529 */     int i = paramArrayOfObject.length;
/*      */ 
/*  531 */     int[] arrayOfInt1 = new int[i];
/*  532 */     int[] arrayOfInt2 = new int[i];
/*  533 */     int[] arrayOfInt3 = new int[i];
/*      */ 
/*  535 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  537 */     Calendar localCalendar = Calendar.getInstance();
/*      */ 
/*  539 */     for (int j = 0; j < i; j++)
/*      */     {
/*  541 */       if (paramArrayOfObject[j] != null)
/*      */       {
/*  543 */         localTime = (Time)paramArrayOfObject[j];
/*      */ 
/*  545 */         localCalendar.setTime(localTime);
/*      */ 
/*  547 */         arrayOfInt1[j] = localCalendar.get(11);
/*  548 */         arrayOfInt2[j] = localCalendar.get(12);
/*  549 */         arrayOfInt3[j] = localCalendar.get(13);
/*      */       }
/*      */     }
/*      */ 
/*  553 */     bindColTime(paramLong, paramInt, arrayOfInt1, arrayOfInt2, arrayOfInt3, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfLong, arrayOfByte);
/*      */ 
/*  562 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  565 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindColTimestamp(long paramLong, int paramInt, Object[] paramArrayOfObject, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  586 */     if (this.tracer.isTracing()) {
/*  587 */       this.tracer.trace("Bind Column Timestamp (SQLBindColTimestamp), hStmt=" + paramLong + ", icol=" + paramInt);
/*      */     }
/*      */ 
/*  594 */     Timestamp localTimestamp = null;
/*      */ 
/*  596 */     int i = paramArrayOfObject.length;
/*      */ 
/*  598 */     int[] arrayOfInt1 = new int[i];
/*  599 */     int[] arrayOfInt2 = new int[i];
/*  600 */     int[] arrayOfInt3 = new int[i];
/*  601 */     int[] arrayOfInt4 = new int[i];
/*  602 */     int[] arrayOfInt5 = new int[i];
/*  603 */     int[] arrayOfInt6 = new int[i];
/*  604 */     int[] arrayOfInt7 = new int[i];
/*      */ 
/*  606 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  608 */     Calendar localCalendar = Calendar.getInstance();
/*      */ 
/*  610 */     for (int j = 0; j < i; j++)
/*      */     {
/*  612 */       if (paramArrayOfObject[j] != null)
/*      */       {
/*  614 */         localTimestamp = (Timestamp)paramArrayOfObject[j];
/*      */ 
/*  616 */         localCalendar.setTime(localTimestamp);
/*      */ 
/*  618 */         arrayOfInt1[j] = localCalendar.get(1);
/*  619 */         arrayOfInt2[j] = (localCalendar.get(2) + 1);
/*  620 */         arrayOfInt3[j] = localCalendar.get(5);
/*  621 */         arrayOfInt4[j] = localCalendar.get(11);
/*  622 */         arrayOfInt5[j] = localCalendar.get(12);
/*  623 */         arrayOfInt6[j] = localCalendar.get(13);
/*  624 */         arrayOfInt7[j] = localTimestamp.getNanos();
/*      */       }
/*      */     }
/*      */ 
/*  628 */     bindColTimestamp(paramLong, paramInt, arrayOfInt1, arrayOfInt2, arrayOfInt3, arrayOfInt4, arrayOfInt5, arrayOfInt6, arrayOfInt7, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfLong, arrayOfByte);
/*      */ 
/*  638 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  641 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterAtExec(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  664 */     if (this.tracer.isTracing()) {
/*  665 */       this.tracer.trace("Binding DATA_AT_EXEC parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", len=" + paramInt3);
/*      */     }
/*      */ 
/*  668 */     byte[] arrayOfByte = new byte[1];
/*  669 */     bindInParameterAtExec(paramLong, paramInt1, paramInt2, paramInt3, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/*  672 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  675 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterAtExec(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, int paramInt5, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  700 */     if (this.tracer.isTracing()) {
/*  701 */       this.tracer.trace("Binding DATA_AT_EXEC parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt3 + ", streamLength = " + paramInt5 + " ,dataBufLen = " + paramInt4);
/*      */     }
/*      */ 
/*  706 */     byte[] arrayOfByte = new byte[1];
/*  707 */     bindInOutParameterAtExec(paramLong, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte1, paramInt5, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/*  710 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  713 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterBinary(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  740 */     int i = 0;
/*      */ 
/*  748 */     if (paramArrayOfByte2.length < 8000)
/*  749 */       i = paramArrayOfByte2.length;
/*      */     else {
/*  751 */       i = 8000;
/*      */     }
/*  753 */     if (this.tracer.isTracing()) {
/*  754 */       this.tracer.trace("Binding IN binary parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2);
/*      */ 
/*  756 */       dumpByte(paramArrayOfByte1, paramArrayOfByte1.length);
/*      */     }
/*      */ 
/*  759 */     byte[] arrayOfByte = new byte[1];
/*  760 */     bindInParameterBinary(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramArrayOfByte1, i, paramArrayOfByte2, paramArrayOfByte3, arrayOfByte, paramArrayOfLong);
/*      */ 
/*  765 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  768 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterDate(long paramLong, int paramInt, java.sql.Date paramDate, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  788 */     if (this.tracer.isTracing()) {
/*  789 */       this.tracer.trace("Binding IN parameter date (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt + ", rgbValue=" + paramDate.toString());
/*      */     }
/*      */ 
/*  793 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  795 */     Calendar localCalendar = Calendar.getInstance();
/*  796 */     localCalendar.setTime(paramDate);
/*      */ 
/*  798 */     bindInParameterDate(paramLong, paramInt, localCalendar.get(1), localCalendar.get(2) + 1, localCalendar.get(5), paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/*  804 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  807 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterCalendarDate(long paramLong, int paramInt, Calendar paramCalendar, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  827 */     if (this.tracer.isTracing()) {
/*  828 */       this.tracer.trace("Binding IN parameter date (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt + ", rgbValue=" + paramCalendar.toString());
/*      */     }
/*      */ 
/*  832 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  834 */     bindInParameterDate(paramLong, paramInt, paramCalendar.get(1), paramCalendar.get(2) + 1, paramCalendar.get(5), paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/*  840 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  843 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterDouble(long paramLong, int paramInt1, int paramInt2, int paramInt3, double paramDouble, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  866 */     if (this.tracer.isTracing()) {
/*  867 */       this.tracer.trace("Binding IN parameter double (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", scale=" + paramInt3 + ", rgbValue=" + paramDouble);
/*      */     }
/*      */ 
/*  872 */     byte[] arrayOfByte = new byte[1];
/*  873 */     bindInParameterDouble(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramDouble, paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/*  878 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  881 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterFloat(long paramLong, int paramInt1, int paramInt2, int paramInt3, float paramFloat, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  903 */     if (this.tracer.isTracing()) {
/*  904 */       this.tracer.trace("Binding IN parameter float (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", scale=" + paramInt3 + ", rgbValue=" + paramFloat);
/*      */     }
/*      */ 
/*  909 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/*  911 */     bindInParameterFloat(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramFloat, paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/*  915 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  918 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterInteger(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  939 */     if (this.tracer.isTracing()) {
/*  940 */       this.tracer.trace("Binding IN parameter integer (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + "SQLtype=" + paramInt2 + ", rgbValue=" + paramInt3);
/*      */     }
/*      */ 
/*  944 */     byte[] arrayOfByte = new byte[1];
/*  945 */     bindInParameterInteger(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/*  949 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  952 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterNull(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/*  974 */     if (this.tracer.isTracing()) {
/*  975 */       this.tracer.trace("Binding IN NULL parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2);
/*      */     }
/*      */ 
/*  979 */     byte[] arrayOfByte = new byte[1];
/*  980 */     bindInParameterNull(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramInt4, paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/*  982 */     if (arrayOfByte[0] != 0)
/*      */     {
/*  985 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterString(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1008 */     if (this.tracer.isTracing()) {
/* 1009 */       this.tracer.trace("Binding IN string parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", precision=" + paramInt3 + ", scale=" + paramInt4 + ", rgbValue=" + paramArrayOfByte1);
/*      */     }
/*      */ 
/* 1015 */     byte[] arrayOfByte = new byte[1];
/* 1016 */     bindInParameterString(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramArrayOfByte1, paramInt3, paramInt4, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1021 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1024 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterTime(long paramLong, int paramInt, Time paramTime, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1044 */     if (this.tracer.isTracing()) {
/* 1045 */       this.tracer.trace("Binding IN parameter time (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt + ", rgbValue=" + paramTime.toString());
/*      */     }
/*      */ 
/* 1049 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 1051 */     Calendar localCalendar = Calendar.getInstance();
/* 1052 */     localCalendar.setTime(paramTime);
/*      */ 
/* 1054 */     bindInParameterTime(paramLong, paramInt, localCalendar.get(11), localCalendar.get(12), localCalendar.get(13), paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1060 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1063 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterCalendarTime(long paramLong, int paramInt, Calendar paramCalendar, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1083 */     if (this.tracer.isTracing()) {
/* 1084 */       this.tracer.trace("Binding IN parameter time (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt + ", rgbValue=" + paramCalendar.toString());
/*      */     }
/*      */ 
/* 1088 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 1091 */     bindInParameterTime(paramLong, paramInt, paramCalendar.get(11), paramCalendar.get(12), paramCalendar.get(13), paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1097 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1100 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterTimestamp(long paramLong, int paramInt, Timestamp paramTimestamp, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1121 */     if (this.tracer.isTracing()) {
/* 1122 */       this.tracer.trace("Binding IN parameter timestamp (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt + ", rgbValue=" + paramTimestamp.toString());
/*      */     }
/*      */ 
/* 1126 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 1128 */     Calendar localCalendar = Calendar.getInstance();
/* 1129 */     localCalendar.setTime(paramTimestamp);
/*      */ 
/* 1131 */     bindInParameterTimestamp(paramLong, paramInt, localCalendar.get(1), localCalendar.get(2) + 1, localCalendar.get(5), localCalendar.get(11), localCalendar.get(12), localCalendar.get(13), paramTimestamp.getNanos(), paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1141 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1144 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterCalendarTimestamp(long paramLong, int paramInt, Calendar paramCalendar, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1164 */     if (this.tracer.isTracing()) {
/* 1165 */       this.tracer.trace("Binding IN parameter timestamp (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt + ", rgbValue=" + paramCalendar.toString());
/*      */     }
/*      */ 
/* 1169 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 1172 */     int i = paramCalendar.get(14) * 1000000;
/*      */ 
/* 1174 */     bindInParameterTimestamp(paramLong, paramInt, paramCalendar.get(1), paramCalendar.get(2) + 1, paramCalendar.get(5), paramCalendar.get(11), paramCalendar.get(12), paramCalendar.get(13), i, paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1184 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1187 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterBigint(long paramLong1, int paramInt1, int paramInt2, int paramInt3, long paramLong2, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1209 */     if (this.tracer.isTracing()) {
/* 1210 */       this.tracer.trace("Binding IN parameter bigint (SQLBindParameter), hStmt=" + paramLong1 + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", scale=" + paramInt3 + ", rgbValue=" + paramLong2);
/*      */     }
/*      */ 
/* 1215 */     byte[] arrayOfByte = new byte[1];
/* 1216 */     bindInParameterBigint(paramLong1, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramLong2, paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1220 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1223 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindOutParameterString(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1245 */     if (this.tracer.isTracing()) {
/* 1246 */       this.tracer.trace("Binding OUT string parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", prec=" + (paramArrayOfByte1.length - 1) + ", scale=" + paramInt3);
/*      */     }
/*      */ 
/* 1251 */     byte[] arrayOfByte = new byte[1];
/* 1252 */     bindOutParameterString(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1256 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1259 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterDate(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1281 */     if (this.tracer.isTracing()) {
/* 1282 */       this.tracer.trace("Binding IN OUT date parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", prec=" + (paramArrayOfByte1.length - 1) + ", scale=" + paramInt2);
/*      */     }
/*      */ 
/* 1287 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 1289 */     bindInOutParameterDate(paramLong, paramInt1, paramInt2, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1292 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1295 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterTime(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1317 */     if (this.tracer.isTracing()) {
/* 1318 */       this.tracer.trace("Binding IN OUT time parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", prec=" + (paramArrayOfByte1.length - 1) + ", scale=" + paramInt2);
/*      */     }
/*      */ 
/* 1323 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 1325 */     bindInOutParameterTime(paramLong, paramInt1, paramInt2, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1328 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1331 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterTimestamp(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1353 */     if (this.tracer.isTracing()) {
/* 1354 */       this.tracer.trace("Binding IN OUT time parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", scale=" + paramInt3 + "length = " + (paramArrayOfByte1.length - 1) + ", precision=" + paramInt2);
/*      */     }
/*      */ 
/* 1359 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 1361 */     bindInOutParameterTimestamp(paramLong, paramInt1, paramInt2, paramInt3, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1364 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1367 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterString(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1391 */     if (this.tracer.isTracing()) {
/* 1392 */       this.tracer.trace("Binding INOUT string parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", precision=" + paramInt3 + ", scale=" + paramInt4 + ", rgbValue=" + paramArrayOfByte1 + ", lenBuf=" + paramArrayOfByte2);
/*      */     }
/*      */ 
/* 1398 */     byte[] arrayOfByte = new byte[1];
/* 1399 */     bindInOutParameterString(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramInt4, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1404 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1407 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterStr(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong, int paramInt4)
/*      */     throws SQLException
/*      */   {
/* 1438 */     if (this.tracer.isTracing()) {
/* 1439 */       this.tracer.trace("Binding INOUT string parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", precision=" + paramInt3 + ", rgbValue=" + paramArrayOfByte1 + ", lenBuf=" + paramArrayOfByte2);
/*      */     }
/*      */ 
/* 1445 */     byte[] arrayOfByte = new byte[1];
/* 1446 */     bindInOutParameterStr(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong, paramInt4);
/*      */ 
/* 1452 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1455 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterBin(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong, int paramInt4)
/*      */     throws SQLException
/*      */   {
/* 1487 */     if (this.tracer.isTracing()) {
/* 1488 */       this.tracer.trace("Binding INOUT binary parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", precision=" + paramInt3 + ", rgbValue=" + paramArrayOfByte1 + ", lenBuf=" + paramArrayOfByte2);
/*      */     }
/*      */ 
/* 1494 */     byte[] arrayOfByte = new byte[1];
/* 1495 */     bindInOutParameterBin(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong, paramInt4);
/*      */ 
/* 1500 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1503 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterBinary(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1527 */     if (this.tracer.isTracing()) {
/* 1528 */       this.tracer.trace("Binding INOUT binary parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", precision=" + paramInt3 + ", scale=" + paramInt4 + ", rgbValue=" + paramArrayOfByte1 + ", lenBuf=" + paramArrayOfByte2);
/*      */     }
/*      */ 
/* 1534 */     byte[] arrayOfByte = new byte[1];
/* 1535 */     bindInOutParameterBinary(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramInt4, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1540 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1543 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterFixed(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1572 */     if (this.tracer.isTracing()) {
/* 1573 */       this.tracer.trace("Binding IN OUT parameter for fixed types (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + "SQLtype=" + paramInt2 + ", maxLen=" + paramInt3);
/*      */     }
/*      */ 
/* 1577 */     byte[] arrayOfByte = new byte[1];
/* 1578 */     bindInOutParameterFixed(paramLong, paramInt1, OdbcDef.jdbcTypeToCType(paramInt2), OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1582 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1585 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterTimeStamp(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1610 */     if (this.tracer.isTracing()) {
/* 1611 */       this.tracer.trace("Binding INOUT string parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", precision=" + paramInt3 + ", scale=" + paramInt4 + ", rgbValue=" + paramArrayOfByte1 + ", lenBuf=" + paramArrayOfByte2);
/*      */     }
/*      */ 
/* 1617 */     byte[] arrayOfByte = new byte[1];
/* 1618 */     bindInOutParameterTimeStamp(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramInt4, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1623 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1626 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameter(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1648 */     if (this.tracer.isTracing()) {
/* 1649 */       this.tracer.trace("Binding INOUT parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", precision=" + paramInt3 + ", scale=" + paramInt4 + ", rgbValue=" + paramDouble);
/*      */     }
/*      */ 
/* 1655 */     byte[] arrayOfByte = new byte[1];
/* 1656 */     bindInOutParameter(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramInt4, paramDouble, paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1661 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1664 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInOutParameterNull(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 1686 */     if (this.tracer.isTracing()) {
/* 1687 */       this.tracer.trace("Binding IN OUT NULL parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2);
/*      */     }
/*      */ 
/* 1691 */     byte[] arrayOfByte = new byte[1];
/* 1692 */     bindInOutParameterNull(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramInt4, paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 1694 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1697 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterStringArray(long paramLong, int paramInt1, int paramInt2, Object[] paramArrayOfObject, int paramInt3, int paramInt4, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 1718 */     int i = paramArrayOfObject.length;
/*      */ 
/* 1720 */     Object[] arrayOfObject = new Object[i];
/*      */ 
/* 1722 */     if ((paramInt2 == 2) || (paramInt2 == 3))
/*      */     {
/* 1725 */       for (int j = 0; j < i; j++)
/*      */       {
/* 1727 */         if (paramArrayOfObject[j] != null)
/*      */         {
/* 1730 */           localObject = (BigDecimal)paramArrayOfObject[j];
/*      */ 
/* 1732 */           String str1 = ((BigDecimal)localObject).toString();
/*      */ 
/* 1737 */           int k = str1.indexOf('.');
/*      */ 
/* 1739 */           if (k != -1)
/*      */           {
/* 1741 */             String str2 = str1.substring(k + 1, str1.length());
/*      */ 
/* 1743 */             int m = str2.length();
/*      */ 
/* 1745 */             if (m < paramInt4)
/*      */             {
/* 1747 */               for (int n = 0; n < paramInt4 - m; n++)
/*      */               {
/* 1749 */                 str1 = str1 + "0";
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1755 */           arrayOfObject[j] = str1;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1760 */       arrayOfObject = paramArrayOfObject;
/*      */     }
/*      */ 
/* 1763 */     if (this.tracer.isTracing()) {
/* 1764 */       this.tracer.trace("Binding IN parameter timestamp (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1);
/*      */     }
/*      */ 
/* 1771 */     byte[] arrayOfByte = new byte[1];
/* 1772 */     Object localObject = new byte[(paramInt3 + 1) * i];
/*      */ 
/* 1774 */     bindInParameterStringArray(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), arrayOfObject, (byte[])localObject, paramInt3, paramInt4, paramArrayOfInt, arrayOfByte);
/*      */ 
/* 1779 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1782 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterIntegerArray(long paramLong, int paramInt1, int paramInt2, Object[] paramArrayOfObject, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 1802 */     int[] arrayOfInt = new int[paramArrayOfObject.length];
/*      */ 
/* 1805 */     for (int i = 0; i < paramArrayOfObject.length; i++)
/*      */     {
/* 1807 */       if (paramArrayOfObject[i] != null) {
/* 1808 */         arrayOfInt[i] = ((Integer)paramArrayOfObject[i]).intValue();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1814 */     if (this.tracer.isTracing()) {
/* 1815 */       this.tracer.trace("Binding IN parameter Integer Array (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1);
/*      */     }
/*      */ 
/* 1819 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 1821 */     bindInParameterIntegerArray(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), arrayOfInt, paramArrayOfInt, arrayOfByte);
/*      */ 
/* 1824 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1827 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterFloatArray(long paramLong, int paramInt1, int paramInt2, Object[] paramArrayOfObject, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 1847 */     float[] arrayOfFloat = new float[paramArrayOfObject.length];
/*      */ 
/* 1849 */     for (int i = 0; i < paramArrayOfObject.length; i++)
/*      */     {
/* 1851 */       if (paramArrayOfObject[i] != null) {
/* 1852 */         arrayOfFloat[i] = ((Float)paramArrayOfObject[i]).floatValue();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1857 */     if (this.tracer.isTracing()) {
/* 1858 */       this.tracer.trace("Binding IN parameter timestamp (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1);
/*      */     }
/*      */ 
/* 1862 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 1864 */     bindInParameterFloatArray(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), 0, arrayOfFloat, paramArrayOfInt, arrayOfByte);
/*      */ 
/* 1867 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1870 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterDoubleArray(long paramLong, int paramInt1, int paramInt2, Object[] paramArrayOfObject, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 1890 */     double[] arrayOfDouble = new double[paramArrayOfObject.length];
/*      */ 
/* 1892 */     for (int i = 0; i < paramArrayOfObject.length; i++)
/*      */     {
/* 1894 */       if (paramArrayOfObject[i] != null) {
/* 1895 */         arrayOfDouble[i] = ((Double)paramArrayOfObject[i]).doubleValue();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1900 */     if (this.tracer.isTracing()) {
/* 1901 */       this.tracer.trace("Binding IN parameter timestamp (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1);
/*      */     }
/*      */ 
/* 1905 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 1907 */     bindInParameterDoubleArray(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), 0, arrayOfDouble, paramArrayOfInt, arrayOfByte);
/*      */ 
/* 1910 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 1913 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterDateArray(long paramLong, int paramInt, Object[] paramArrayOfObject, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 1932 */     if (this.tracer.isTracing()) {
/* 1933 */       this.tracer.trace("Binding IN parameter timestamp (SQLBindParameterDateArray), hStmt=" + paramLong + ", ipar=" + paramInt);
/*      */     }
/*      */ 
/* 1937 */     int i = paramArrayOfObject.length;
/*      */ 
/* 1939 */     int[] arrayOfInt1 = new int[i];
/* 1940 */     int[] arrayOfInt2 = new int[i];
/* 1941 */     int[] arrayOfInt3 = new int[i];
/*      */ 
/* 1946 */     byte[] arrayOfByte1 = new byte[1];
/* 1947 */     byte[] arrayOfByte2 = new byte[11 * i];
/*      */     Calendar localCalendar;
/* 1951 */     if ((java.sql.Date)paramArrayOfObject[0] != null)
/*      */     {
/* 1954 */       localCalendar = Calendar.getInstance();
/*      */ 
/* 1956 */       java.sql.Date localDate = null;
/*      */ 
/* 1958 */       for (int k = 0; k < i; k++)
/*      */       {
/* 1960 */         if (paramArrayOfObject[k] != null)
/*      */         {
/* 1962 */           localDate = (java.sql.Date)paramArrayOfObject[k];
/*      */ 
/* 1964 */           localCalendar.setTime(localDate);
/*      */ 
/* 1966 */           arrayOfInt1[k] = localCalendar.get(1);
/* 1967 */           arrayOfInt2[k] = (localCalendar.get(2) + 1);
/* 1968 */           arrayOfInt3[k] = localCalendar.get(5);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1978 */       for (int j = 0; j < i; j++)
/*      */       {
/* 1980 */         if (paramArrayOfObject[j] != null)
/*      */         {
/* 1982 */           localCalendar = (Calendar)paramArrayOfObject[j];
/*      */ 
/* 1984 */           arrayOfInt1[j] = localCalendar.get(1);
/* 1985 */           arrayOfInt2[j] = (localCalendar.get(2) + 1);
/* 1986 */           arrayOfInt3[j] = localCalendar.get(5);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1994 */     bindInParameterDateArray(paramLong, paramInt, arrayOfInt1, arrayOfInt2, arrayOfInt3, arrayOfByte2, arrayOfByte1, paramArrayOfInt);
/*      */ 
/* 2000 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 2003 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterTimeArray(long paramLong, int paramInt, Object[] paramArrayOfObject, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 2021 */     if (this.tracer.isTracing()) {
/* 2022 */       this.tracer.trace("Binding IN parameter timestamp (SQLBindParameterTimeArray), hStmt=" + paramLong + ", ipar=" + paramInt);
/*      */     }
/*      */ 
/* 2026 */     int i = paramArrayOfObject.length;
/*      */ 
/* 2028 */     int[] arrayOfInt1 = new int[i];
/* 2029 */     int[] arrayOfInt2 = new int[i];
/* 2030 */     int[] arrayOfInt3 = new int[i];
/*      */ 
/* 2035 */     byte[] arrayOfByte1 = new byte[1];
/* 2036 */     byte[] arrayOfByte2 = new byte[9 * i];
/*      */     Calendar localCalendar;
/* 2040 */     if ((Time)paramArrayOfObject[0] != null)
/*      */     {
/* 2042 */       localCalendar = Calendar.getInstance();
/*      */ 
/* 2044 */       Time localTime = null;
/*      */ 
/* 2046 */       for (int k = 0; k < i; k++)
/*      */       {
/* 2048 */         if (paramArrayOfObject[k] != null)
/*      */         {
/* 2050 */           localTime = (Time)paramArrayOfObject[k];
/*      */ 
/* 2052 */           localCalendar.setTime(localTime);
/*      */ 
/* 2054 */           arrayOfInt1[k] = localCalendar.get(11);
/* 2055 */           arrayOfInt2[k] = localCalendar.get(12);
/* 2056 */           arrayOfInt3[k] = localCalendar.get(13);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 2065 */       for (int j = 0; j < i; j++)
/*      */       {
/* 2067 */         if (paramArrayOfObject[j] != null)
/*      */         {
/* 2069 */           localCalendar = (Calendar)paramArrayOfObject[j];
/*      */ 
/* 2071 */           arrayOfInt1[j] = localCalendar.get(11);
/* 2072 */           arrayOfInt2[j] = localCalendar.get(12);
/* 2073 */           arrayOfInt3[j] = localCalendar.get(13);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2080 */     bindInParameterTimeArray(paramLong, paramInt, arrayOfInt1, arrayOfInt2, arrayOfInt3, arrayOfByte2, arrayOfByte1, paramArrayOfInt);
/*      */ 
/* 2086 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 2089 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterTimestampArray(long paramLong, int paramInt, Object[] paramArrayOfObject, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 2107 */     if (this.tracer.isTracing()) {
/* 2108 */       this.tracer.trace("Binding IN parameter timestamp (SQLBindParameterTimestampArray), hStmt=" + paramLong + ", ipar=" + paramInt);
/*      */     }
/*      */ 
/* 2112 */     int i = paramArrayOfObject.length;
/*      */ 
/* 2114 */     int[] arrayOfInt1 = new int[i];
/* 2115 */     int[] arrayOfInt2 = new int[i];
/* 2116 */     int[] arrayOfInt3 = new int[i];
/* 2117 */     int[] arrayOfInt4 = new int[i];
/* 2118 */     int[] arrayOfInt5 = new int[i];
/* 2119 */     int[] arrayOfInt6 = new int[i];
/* 2120 */     int[] arrayOfInt7 = new int[i];
/*      */ 
/* 2125 */     byte[] arrayOfByte1 = new byte[1];
/* 2126 */     byte[] arrayOfByte2 = new byte[30 * i];
/*      */     Calendar localCalendar;
/* 2130 */     if ((Timestamp)paramArrayOfObject[0] != null)
/*      */     {
/* 2133 */       localCalendar = Calendar.getInstance();
/*      */ 
/* 2135 */       Timestamp localTimestamp = null;
/*      */ 
/* 2137 */       for (int k = 0; k < i; k++)
/*      */       {
/* 2139 */         if (paramArrayOfObject[k] != null)
/*      */         {
/* 2141 */           localTimestamp = (Timestamp)paramArrayOfObject[k];
/*      */ 
/* 2143 */           localCalendar.setTime(localTimestamp);
/*      */ 
/* 2145 */           arrayOfInt1[k] = localCalendar.get(1);
/* 2146 */           arrayOfInt2[k] = (localCalendar.get(2) + 1);
/* 2147 */           arrayOfInt3[k] = localCalendar.get(5);
/* 2148 */           arrayOfInt4[k] = localCalendar.get(11);
/* 2149 */           arrayOfInt5[k] = localCalendar.get(12);
/* 2150 */           arrayOfInt6[k] = localCalendar.get(13);
/* 2151 */           arrayOfInt7[k] = localTimestamp.getNanos();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 2160 */       for (int j = 0; j < i; j++)
/*      */       {
/* 2162 */         if (paramArrayOfObject[j] != null)
/*      */         {
/* 2164 */           localCalendar = (Calendar)paramArrayOfObject[j];
/*      */ 
/* 2166 */           arrayOfInt1[j] = localCalendar.get(1);
/* 2167 */           arrayOfInt2[j] = (localCalendar.get(2) + 1);
/* 2168 */           arrayOfInt3[j] = localCalendar.get(5);
/* 2169 */           arrayOfInt4[j] = localCalendar.get(11);
/* 2170 */           arrayOfInt5[j] = localCalendar.get(12);
/* 2171 */           arrayOfInt6[j] = localCalendar.get(13);
/* 2172 */           arrayOfInt7[j] = localCalendar.get(14);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2180 */     bindInParameterTimestampArray(paramLong, paramInt, arrayOfInt1, arrayOfInt2, arrayOfInt3, arrayOfInt4, arrayOfInt5, arrayOfInt6, arrayOfInt7, arrayOfByte2, arrayOfByte1, paramArrayOfInt);
/*      */ 
/* 2190 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 2193 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterBinaryArray(long paramLong, int paramInt1, int paramInt2, Object[] paramArrayOfObject, int paramInt3, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 2215 */     int i = paramArrayOfObject.length;
/*      */ 
/* 2221 */     int j = 8000;
/*      */ 
/* 2223 */     if (this.tracer.isTracing()) {
/* 2224 */       this.tracer.trace("Binding IN binary parameter (SQLBindParameterBinaryArray), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2);
/*      */     }
/*      */ 
/* 2229 */     byte[] arrayOfByte1 = new byte[1];
/* 2230 */     byte[] arrayOfByte2 = new byte[paramInt3 * i];
/*      */ 
/* 2232 */     bindInParameterBinaryArray(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramArrayOfObject, paramInt3, arrayOfByte2, paramArrayOfInt, arrayOfByte1);
/*      */ 
/* 2236 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 2239 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindInParameterAtExecArray(long paramLong, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 2261 */     if (this.tracer.isTracing()) {
/* 2262 */       this.tracer.trace("Binding DATA_AT_EXEC Array parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", len=" + paramInt3);
/*      */     }
/*      */ 
/* 2266 */     byte[] arrayOfByte1 = new byte[1];
/* 2267 */     byte[] arrayOfByte2 = new byte[paramArrayOfInt.length];
/*      */ 
/* 2269 */     bindInParameterAtExecArray(paramLong, paramInt1, paramInt2, paramInt3, arrayOfByte2, paramArrayOfInt, arrayOfByte1);
/*      */ 
/* 2271 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 2274 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindOutParameterNull(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 2298 */     if (this.tracer.isTracing()) {
/* 2299 */       this.tracer.trace("Binding OUT NULL parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2);
/*      */     }
/*      */ 
/* 2303 */     byte[] arrayOfByte = new byte[1];
/* 2304 */     bindOutParameterNull(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramInt4, paramArrayOfByte, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 2307 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2310 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindOutParameterFixed(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 2333 */     if (this.tracer.isTracing()) {
/* 2334 */       this.tracer.trace("Binding OUT string parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", maxLen=" + paramInt3);
/*      */     }
/*      */ 
/* 2339 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 2341 */     bindOutParameterFixed(paramLong, paramInt1, OdbcDef.jdbcTypeToCType(paramInt2), OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 2344 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2347 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindOutParameterBinary(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 2371 */     if (this.tracer.isTracing()) {
/* 2372 */       this.tracer.trace("Binding INOUT binary parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", SQLtype=" + paramInt2 + ", precision=" + paramInt3 + ", scale=" + paramInt4 + ", rgbValue=" + paramArrayOfByte1 + ", lenBuf=" + paramArrayOfByte2);
/*      */     }
/*      */ 
/* 2378 */     byte[] arrayOfByte = new byte[1];
/* 2379 */     bindOutParameterBinary(paramLong, paramInt1, OdbcDef.jdbcTypeToOdbc(paramInt2), paramInt3, paramInt4, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 2384 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2386 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindOutParameterDate(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 2409 */     if (this.tracer.isTracing()) {
/* 2410 */       this.tracer.trace("Binding IN OUT date parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", prec=" + (paramArrayOfByte1.length - 1) + ", scale=" + paramInt2);
/*      */     }
/*      */ 
/* 2415 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 2417 */     bindOutParameterDate(paramLong, paramInt1, paramInt2, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 2420 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2423 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindOutParameterTime(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 2446 */     if (this.tracer.isTracing()) {
/* 2447 */       this.tracer.trace("Binding IN OUT time parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", prec=" + (paramArrayOfByte1.length - 1) + ", scale=" + paramInt2);
/*      */     }
/*      */ 
/* 2452 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 2454 */     bindOutParameterTime(paramLong, paramInt1, paramInt2, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 2457 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2460 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLBindOutParameterTimestamp(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 2482 */     if (this.tracer.isTracing()) {
/* 2483 */       this.tracer.trace("Binding OUT time parameter (SQLBindParameter), hStmt=" + paramLong + ", ipar=" + paramInt1 + ", prec=" + (paramArrayOfByte1.length - 1) + ", precision=" + paramInt2);
/*      */     }
/*      */ 
/* 2488 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 2490 */     bindOutParameterTimestamp(paramLong, paramInt1, paramInt2, paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 2493 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2496 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String SQLBrowseConnect(long paramLong, String paramString)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 2518 */     String str = null;
/*      */ 
/* 2520 */     if (this.tracer.isTracing()) {
/* 2521 */       this.tracer.trace("Connecting (SQLBrowseConnect), hDbc=" + paramLong + ", szConnStrIn=" + paramString);
/*      */     }
/*      */ 
/* 2524 */     byte[] arrayOfByte1 = new byte[1];
/* 2525 */     byte[] arrayOfByte2 = new byte[2000];
/*      */ 
/* 2527 */     byte[] arrayOfByte3 = null;
/* 2528 */     char[] arrayOfChar = null;
/* 2529 */     if (paramString != null)
/* 2530 */       arrayOfChar = paramString.toCharArray();
/*      */     try {
/* 2532 */       if (paramString != null)
/* 2533 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 2536 */     browseConnect(paramLong, arrayOfByte3, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 2540 */     if (arrayOfByte1[0] == 99) {
/* 2541 */       str = new String(arrayOfByte2);
/* 2542 */       str = str.trim();
/* 2543 */       arrayOfByte1[0] = 0;
/*      */     }
/*      */ 
/* 2546 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 2550 */       standardError((short)arrayOfByte1[0], 0L, paramLong, 0L);
/*      */     }
/*      */ 
/* 2554 */     if (this.tracer.isTracing()) {
/* 2555 */       this.tracer.trace("Attributes=" + str);
/*      */     }
/*      */ 
/* 2558 */     return str;
/*      */   }
/*      */ 
/*      */   public void SQLCancel(long paramLong)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 2572 */     if (this.tracer.isTracing()) {
/* 2573 */       this.tracer.trace("Cancelling (SQLCancel), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 2576 */     byte[] arrayOfByte = new byte[1];
/* 2577 */     cancel(paramLong, arrayOfByte);
/*      */ 
/* 2579 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2583 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int SQLColAttributes(long paramLong, int paramInt1, int paramInt2)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 2598 */     int i = 0;
/*      */ 
/* 2601 */     if (this.tracer.isTracing()) {
/* 2602 */       this.tracer.trace("Column attributes (SQLColAttributes), hStmt=" + paramLong + ", icol=" + paramInt1 + ", type=" + paramInt2);
/*      */     }
/*      */ 
/* 2606 */     byte[] arrayOfByte = new byte[1];
/* 2607 */     i = colAttributes(paramLong, paramInt1, paramInt2, arrayOfByte);
/*      */ 
/* 2609 */     if (arrayOfByte[0] != 0) {
/*      */       try
/*      */       {
/* 2612 */         standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 2617 */         if (this.tracer.isTracing()) {
/* 2618 */           this.tracer.trace("value (int)=" + i);
/*      */         }
/*      */ 
/* 2624 */         localJdbcOdbcSQLWarning.value = BigDecimal.valueOf(i);
/*      */ 
/* 2628 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */ 
/*      */     }
/* 2633 */     else if (this.tracer.isTracing()) {
/* 2634 */       this.tracer.trace("value (int)=" + i);
/*      */     }
/*      */ 
/* 2638 */     return i;
/*      */   }
/*      */ 
/*      */   public String SQLColAttributesString(long paramLong, int paramInt1, int paramInt2)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 2654 */     if (this.tracer.isTracing()) {
/* 2655 */       this.tracer.trace("Column attributes (SQLColAttributes), hStmt=" + paramLong + ", icol=" + paramInt1 + ", type=" + paramInt2);
/*      */     }
/*      */ 
/* 2659 */     byte[] arrayOfByte2 = new byte[1];
/* 2660 */     byte[] arrayOfByte1 = new byte[300];
/* 2661 */     colAttributesString(paramLong, paramInt1, paramInt2, arrayOfByte1, arrayOfByte2);
/*      */ 
/* 2663 */     if (arrayOfByte2[0] != 0) {
/*      */       try
/*      */       {
/* 2666 */         standardError((short)arrayOfByte2[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 2675 */         String str2 = new String();
/*      */         try {
/* 2677 */           str2 = BytesToChars(this.charSet, arrayOfByte1);
/*      */         } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
/* 2679 */           System.out.println(localUnsupportedEncodingException2);
/*      */         }
/*      */ 
/* 2682 */         if (this.tracer.isTracing()) {
/* 2683 */           this.tracer.trace("value (String)=" + str2.trim());
/*      */         }
/*      */ 
/* 2686 */         localJdbcOdbcSQLWarning.value = str2.trim();
/*      */ 
/* 2690 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */     }
/*      */ 
/* 2694 */     String str1 = new String();
/*      */     try {
/* 2696 */       str1 = BytesToChars(this.charSet, arrayOfByte1);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
/* 2698 */       System.out.println(localUnsupportedEncodingException1);
/*      */     }
/*      */ 
/* 2701 */     if (this.tracer.isTracing()) {
/* 2702 */       this.tracer.trace("value (String)=" + str1.trim());
/*      */     }
/* 2704 */     return str1.trim();
/*      */   }
/*      */ 
/*      */   public void SQLColumns(long paramLong, String paramString1, String paramString2, String paramString3, String paramString4)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 2721 */     if (this.tracer.isTracing()) {
/* 2722 */       this.tracer.trace("(SQLColumns), hStmt=" + paramLong + ", catalog=" + paramString1 + ", schema=" + paramString2 + ", table=" + paramString3 + ", column=" + paramString4);
/*      */     }
/*      */ 
/* 2727 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 2729 */     byte[] arrayOfByte2 = null;
/* 2730 */     byte[] arrayOfByte3 = null;
/* 2731 */     byte[] arrayOfByte4 = null;
/* 2732 */     byte[] arrayOfByte5 = null;
/* 2733 */     char[] arrayOfChar1 = null;
/* 2734 */     char[] arrayOfChar2 = null;
/* 2735 */     char[] arrayOfChar3 = null;
/* 2736 */     char[] arrayOfChar4 = null;
/* 2737 */     if (paramString1 != null)
/* 2738 */       arrayOfChar1 = paramString1.toCharArray();
/* 2739 */     if (paramString2 != null)
/* 2740 */       arrayOfChar2 = paramString2.toCharArray();
/* 2741 */     if (paramString3 != null)
/* 2742 */       arrayOfChar3 = paramString3.toCharArray();
/* 2743 */     if (paramString4 != null)
/* 2744 */       arrayOfChar4 = paramString4.toCharArray();
/*      */     try {
/* 2746 */       if (paramString1 != null)
/* 2747 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar1);
/* 2748 */       if (paramString2 != null)
/* 2749 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar2);
/* 2750 */       if (paramString3 != null)
/* 2751 */         arrayOfByte4 = CharsToBytes(this.charSet, arrayOfChar3);
/* 2752 */       if (paramString4 != null)
/* 2753 */         arrayOfByte5 = CharsToBytes(this.charSet, arrayOfChar4);
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 2757 */     columns(paramLong, arrayOfByte2, paramString1 == null, arrayOfByte3, paramString2 == null, arrayOfByte4, paramString3 == null, arrayOfByte5, paramString4 == null, arrayOfByte1);
/*      */ 
/* 2764 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 2768 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLColumnPrivileges(long paramLong, String paramString1, String paramString2, String paramString3, String paramString4)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 2787 */     if (this.tracer.isTracing()) {
/* 2788 */       this.tracer.trace("(SQLColumnPrivileges), hStmt=" + paramLong + ", catalog=" + paramString1 + ", schema=" + paramString2 + ", table=" + paramString3 + ", column=" + paramString4);
/*      */     }
/*      */ 
/* 2793 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 2795 */     byte[] arrayOfByte2 = null;
/* 2796 */     byte[] arrayOfByte3 = null;
/* 2797 */     byte[] arrayOfByte4 = null;
/* 2798 */     byte[] arrayOfByte5 = null;
/* 2799 */     char[] arrayOfChar1 = null;
/* 2800 */     char[] arrayOfChar2 = null;
/* 2801 */     char[] arrayOfChar3 = null;
/* 2802 */     char[] arrayOfChar4 = null;
/* 2803 */     if (paramString1 != null)
/* 2804 */       arrayOfChar1 = paramString1.toCharArray();
/* 2805 */     if (paramString2 != null)
/* 2806 */       arrayOfChar2 = paramString2.toCharArray();
/* 2807 */     if (paramString3 != null)
/* 2808 */       arrayOfChar3 = paramString3.toCharArray();
/* 2809 */     if (paramString4 != null)
/* 2810 */       arrayOfChar4 = paramString4.toCharArray();
/*      */     try {
/* 2812 */       if (paramString1 != null)
/* 2813 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar1);
/* 2814 */       if (paramString2 != null)
/* 2815 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar2);
/* 2816 */       if (paramString3 != null)
/* 2817 */         arrayOfByte4 = CharsToBytes(this.charSet, arrayOfChar3);
/* 2818 */       if (paramString4 != null)
/* 2819 */         arrayOfByte5 = CharsToBytes(this.charSet, arrayOfChar4);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 2822 */     columnPrivileges(paramLong, arrayOfByte2, paramString1 == null, arrayOfByte3, paramString2 == null, arrayOfByte4, paramString3 == null, arrayOfByte5, paramString4 == null, arrayOfByte1);
/*      */ 
/* 2829 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 2833 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean SQLDescribeParamNullable(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 2849 */     boolean bool = false;
/*      */ 
/* 2851 */     if (this.tracer.isTracing()) {
/* 2852 */       this.tracer.trace("Parameter nullable (SQLDescribeParam), hStmt=" + paramLong + ", ipar=" + paramInt);
/*      */     }
/*      */ 
/* 2856 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 2861 */     int i = describeParam(paramLong, paramInt, 4, arrayOfByte);
/*      */ 
/* 2863 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2867 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 2871 */     if (i == 1) {
/* 2872 */       bool = true;
/*      */     }
/*      */ 
/* 2875 */     if (this.tracer.isTracing()) {
/* 2876 */       this.tracer.trace("nullable=" + bool);
/*      */     }
/*      */ 
/* 2879 */     return bool;
/*      */   }
/*      */ 
/*      */   public int SQLDescribeParamPrecision(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 2893 */     if (this.tracer.isTracing()) {
/* 2894 */       this.tracer.trace("Parameter precision (SQLDescribeParam), hStmt=" + paramLong + ", ipar=" + paramInt);
/*      */     }
/*      */ 
/* 2898 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 2903 */     int i = describeParam(paramLong, paramInt, 2, arrayOfByte);
/*      */ 
/* 2905 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2909 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 2913 */     if (this.tracer.isTracing()) {
/* 2914 */       this.tracer.trace("precision=" + i);
/*      */     }
/*      */ 
/* 2917 */     return i;
/*      */   }
/*      */ 
/*      */   public int SQLDescribeParamScale(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 2932 */     if (this.tracer.isTracing()) {
/* 2933 */       this.tracer.trace("Parameter scale (SQLDescribeParam), hStmt=" + paramLong + ", ipar=" + paramInt);
/*      */     }
/*      */ 
/* 2937 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 2942 */     int i = describeParam(paramLong, paramInt, 3, arrayOfByte);
/*      */ 
/* 2944 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2948 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 2952 */     if (this.tracer.isTracing()) {
/* 2953 */       this.tracer.trace("scale=" + i);
/*      */     }
/*      */ 
/* 2956 */     return i;
/*      */   }
/*      */ 
/*      */   public int SQLDescribeParamType(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 2971 */     if (this.tracer.isTracing()) {
/* 2972 */       this.tracer.trace("Parameter type (SQLDescribeParam), hStmt=" + paramLong + ", ipar=" + paramInt);
/*      */     }
/*      */ 
/* 2976 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 2981 */     int i = describeParam(paramLong, paramInt, 1, arrayOfByte);
/*      */ 
/* 2983 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 2987 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 2991 */     if (this.tracer.isTracing()) {
/* 2992 */       this.tracer.trace("type=" + i);
/*      */     }
/*      */ 
/* 2995 */     return i;
/*      */   }
/*      */ 
/*      */   public void SQLDisconnect(long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3007 */     if (this.tracer.isTracing()) {
/* 3008 */       this.tracer.trace("Disconnecting (SQLDisconnect), hDbc=" + paramLong);
/*      */     }
/*      */ 
/* 3012 */     Set localSet = hstmtMap.keySet();
/* 3013 */     Object[] arrayOfObject = localSet.toArray();
/* 3014 */     int i = arrayOfObject.length;
/*      */ 
/* 3016 */     for (int j = 0; j < i; j++) {
/* 3017 */       Long localLong = (Long)hstmtMap.get(arrayOfObject[j]);
/* 3018 */       if ((localLong != null) && 
/* 3019 */         (localLong.longValue() == paramLong)) {
/* 3020 */         SQLFreeStmt(((Long)arrayOfObject[j]).longValue(), 1);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3025 */     byte[] arrayOfByte = new byte[1];
/* 3026 */     disconnect(paramLong, arrayOfByte);
/*      */ 
/* 3028 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 3032 */       standardError((short)arrayOfByte[0], 0L, paramLong, 0L);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLDriverConnect(long paramLong, String paramString)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 3048 */     if (this.tracer.isTracing()) {
/* 3049 */       this.tracer.trace("Connecting (SQLDriverConnect), hDbc=" + paramLong + ", szConnStrIn=" + paramString);
/*      */     }
/*      */ 
/* 3053 */     byte[] arrayOfByte1 = new byte[1];
/* 3054 */     byte[] arrayOfByte2 = null;
/* 3055 */     char[] arrayOfChar = null;
/*      */ 
/* 3057 */     if (paramString != null)
/* 3058 */       arrayOfChar = paramString.toCharArray();
/*      */     try
/*      */     {
/* 3061 */       if (paramString != null)
/* 3062 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar);
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 3066 */     driverConnect(paramLong, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 3068 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 3072 */       standardError((short)arrayOfByte1[0], 0L, paramLong, 0L);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLExecDirect(long paramLong, String paramString)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 3087 */     if (this.tracer.isTracing()) {
/* 3088 */       this.tracer.trace("Executing (SQLExecDirect), hStmt=" + paramLong + ", szSqlStr=" + paramString);
/*      */     }
/*      */ 
/* 3092 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 3094 */     byte[] arrayOfByte2 = null;
/* 3095 */     char[] arrayOfChar = null;
/* 3096 */     if (paramString != null)
/* 3097 */       arrayOfChar = paramString.toCharArray();
/*      */     try {
/* 3099 */       if (paramString != null)
/* 3100 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 3103 */     execDirect(paramLong, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 3105 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 3109 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean SQLExecute(long paramLong)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 3124 */     boolean bool = false;
/*      */ 
/* 3126 */     if (this.tracer.isTracing()) {
/* 3127 */       this.tracer.trace("Executing (SQLExecute), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 3130 */     byte[] arrayOfByte = new byte[1];
/* 3131 */     execute(paramLong, arrayOfByte);
/*      */ 
/* 3136 */     if (arrayOfByte[0] == 99) {
/* 3137 */       if (this.tracer.isTracing()) {
/* 3138 */         this.tracer.trace("SQL_NEED_DATA returned");
/*      */       }
/* 3140 */       bool = true;
/* 3141 */       arrayOfByte[0] = 0;
/*      */     }
/*      */ 
/* 3144 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 3148 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 3151 */     return bool;
/*      */   }
/*      */ 
/*      */   public boolean SQLFetch(long paramLong)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 3162 */     boolean bool = true;
/*      */ 
/* 3164 */     if (this.tracer.isTracing()) {
/* 3165 */       this.tracer.trace("Fetching (SQLFetch), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 3168 */     byte[] arrayOfByte = new byte[1];
/* 3169 */     fetch(paramLong, arrayOfByte);
/*      */ 
/* 3175 */     if (arrayOfByte[0] == 100) {
/* 3176 */       bool = false;
/* 3177 */       arrayOfByte[0] = 0;
/* 3178 */       if (this.tracer.isTracing()) {
/* 3179 */         this.tracer.trace("End of result set (SQL_NO_DATA)");
/*      */       }
/*      */     }
/*      */ 
/* 3183 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 3187 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 3190 */     return bool;
/*      */   }
/*      */ 
/*      */   public boolean SQLFetchScroll(long paramLong, short paramShort, int paramInt)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 3203 */     boolean bool = true;
/*      */ 
/* 3205 */     if (this.tracer.isTracing()) {
/* 3206 */       this.tracer.trace("Fetching (SQLFetchScroll), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 3209 */     byte[] arrayOfByte = new byte[1];
/* 3210 */     fetchScroll(paramLong, paramShort, paramInt, arrayOfByte);
/*      */ 
/* 3216 */     if (arrayOfByte[0] == 100) {
/* 3217 */       bool = false;
/* 3218 */       arrayOfByte[0] = 0;
/* 3219 */       if (this.tracer.isTracing()) {
/* 3220 */         this.tracer.trace("End of result set (SQL_NO_DATA)");
/*      */       }
/*      */     }
/*      */ 
/* 3224 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 3228 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 3231 */     return bool;
/*      */   }
/*      */ 
/*      */   public void SQLForeignKeys(long paramLong, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 3250 */     if (this.tracer.isTracing()) {
/* 3251 */       this.tracer.trace("(SQLForeignKeys), hStmt=" + paramLong + ", Pcatalog=" + paramString1 + ", Pschema=" + paramString2 + ", Ptable=" + paramString3 + ", Fcatalog=" + paramString4 + ", Fschema=" + paramString5 + ", Ftable=" + paramString6);
/*      */     }
/*      */ 
/* 3257 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 3259 */     byte[] arrayOfByte2 = null;
/* 3260 */     byte[] arrayOfByte3 = null;
/* 3261 */     byte[] arrayOfByte4 = null;
/* 3262 */     byte[] arrayOfByte5 = null;
/* 3263 */     byte[] arrayOfByte6 = null;
/* 3264 */     byte[] arrayOfByte7 = null;
/* 3265 */     char[] arrayOfChar1 = null;
/* 3266 */     char[] arrayOfChar2 = null;
/* 3267 */     char[] arrayOfChar3 = null;
/* 3268 */     char[] arrayOfChar4 = null;
/* 3269 */     char[] arrayOfChar5 = null;
/* 3270 */     char[] arrayOfChar6 = null;
/* 3271 */     if (paramString1 != null)
/* 3272 */       arrayOfChar1 = paramString1.toCharArray();
/* 3273 */     if (paramString2 != null)
/* 3274 */       arrayOfChar2 = paramString2.toCharArray();
/* 3275 */     if (paramString3 != null)
/* 3276 */       arrayOfChar3 = paramString3.toCharArray();
/* 3277 */     if (paramString4 != null)
/* 3278 */       arrayOfChar4 = paramString4.toCharArray();
/* 3279 */     if (paramString5 != null)
/* 3280 */       arrayOfChar5 = paramString5.toCharArray();
/* 3281 */     if (paramString6 != null)
/* 3282 */       arrayOfChar6 = paramString6.toCharArray();
/*      */     try {
/* 3284 */       if (paramString1 != null)
/* 3285 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar1);
/* 3286 */       if (paramString2 != null)
/* 3287 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar2);
/* 3288 */       if (paramString3 != null)
/* 3289 */         arrayOfByte4 = CharsToBytes(this.charSet, arrayOfChar3);
/* 3290 */       if (paramString4 != null)
/* 3291 */         arrayOfByte5 = CharsToBytes(this.charSet, arrayOfChar4);
/* 3292 */       if (paramString5 != null)
/* 3293 */         arrayOfByte6 = CharsToBytes(this.charSet, arrayOfChar5);
/* 3294 */       if (paramString6 != null)
/* 3295 */         arrayOfByte7 = CharsToBytes(this.charSet, arrayOfChar6);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 3298 */     foreignKeys(paramLong, arrayOfByte2, paramString1 == null, arrayOfByte3, paramString2 == null, arrayOfByte4, paramString3 == null, arrayOfByte5, paramString4 == null, arrayOfByte6, paramString5 == null, arrayOfByte7, paramString6 == null, arrayOfByte1);
/*      */ 
/* 3307 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 3311 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLFreeConnect(long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3326 */     if (this.tracer.isTracing()) {
/* 3327 */       this.tracer.trace("Closing connection (SQLFreeConnect), hDbc=" + paramLong);
/*      */     }
/*      */ 
/* 3330 */     byte[] arrayOfByte = new byte[1];
/* 3331 */     freeConnect(paramLong, arrayOfByte);
/*      */ 
/* 3333 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 3337 */       standardError((short)arrayOfByte[0], 0L, paramLong, 0L);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLFreeEnv(long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3352 */     if (this.tracer.isTracing()) {
/* 3353 */       this.tracer.trace("Closing environment (SQLFreeEnv), hEnv=" + paramLong);
/*      */     }
/*      */ 
/* 3356 */     byte[] arrayOfByte = new byte[1];
/* 3357 */     freeEnv(paramLong, arrayOfByte);
/*      */ 
/* 3359 */     if (arrayOfByte[0] != 0)
/* 3360 */       throwGenericSQLException();
/*      */   }
/*      */ 
/*      */   public synchronized void SQLFreeStmt(long paramLong, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3376 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 3379 */     Long localLong = new Long(paramLong);
/* 3380 */     if (paramInt == 1) {
/* 3381 */       if (hstmtMap.containsKey(localLong)) {
/* 3382 */         hstmtMap.remove(localLong);
/* 3383 */         freeStmt(paramLong, paramInt, arrayOfByte);
/* 3384 */         if (this.tracer.isTracing())
/* 3385 */           this.tracer.trace("Free statement (SQLFreeStmt), hStmt=" + paramLong + ", fOption=" + paramInt);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 3390 */       freeStmt(paramLong, paramInt, arrayOfByte);
/* 3391 */       if (this.tracer.isTracing()) {
/* 3392 */         this.tracer.trace("Free statement (SQLFreeStmt), hStmt=" + paramLong + ", fOption=" + paramInt);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3397 */     if (arrayOfByte[0] != 0)
/* 3398 */       throwGenericSQLException();
/*      */   }
/*      */ 
/*      */   public long SQLGetConnectOption(long paramLong, short paramShort)
/*      */     throws SQLException
/*      */   {
/* 3414 */     if (this.tracer.isTracing()) {
/* 3415 */       this.tracer.trace("Connection Option (SQLGetConnectOption), hDbc=" + paramLong + ", fOption=" + paramShort);
/*      */     }
/*      */ 
/* 3419 */     byte[] arrayOfByte = new byte[1];
/* 3420 */     long l = getConnectOption(paramLong, paramShort, arrayOfByte);
/*      */ 
/* 3422 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 3426 */       standardError((short)arrayOfByte[0], 0L, paramLong, 0L);
/*      */     }
/*      */ 
/* 3430 */     if (this.tracer.isTracing()) {
/* 3431 */       this.tracer.trace("option value (int)=" + l);
/*      */     }
/*      */ 
/* 3434 */     return l;
/*      */   }
/*      */ 
/*      */   public String SQLGetConnectOptionString(long paramLong, short paramShort)
/*      */     throws SQLException
/*      */   {
/* 3449 */     if (this.tracer.isTracing()) {
/* 3450 */       this.tracer.trace("Connection Option (SQLGetConnectOption), hDbc=" + paramLong + ", fOption=" + paramShort);
/*      */     }
/*      */ 
/* 3454 */     byte[] arrayOfByte1 = new byte[1];
/* 3455 */     byte[] arrayOfByte2 = new byte[300];
/*      */ 
/* 3457 */     getConnectOptionString(paramLong, paramShort, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 3459 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 3463 */       standardError((short)arrayOfByte1[0], 0L, paramLong, 0L);
/*      */     }
/*      */ 
/* 3467 */     String str = new String();
/*      */     try {
/* 3469 */       str = BytesToChars(this.charSet, arrayOfByte2);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 3472 */     if (this.tracer.isTracing()) {
/* 3473 */       this.tracer.trace("option value (int)=" + str.trim());
/*      */     }
/*      */ 
/* 3476 */     return str.trim();
/*      */   }
/*      */ 
/*      */   public String SQLGetCursorName(long paramLong)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 3490 */     if (this.tracer.isTracing()) {
/* 3491 */       this.tracer.trace("Cursor name (SQLGetCursorName), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 3494 */     byte[] arrayOfByte2 = new byte[1];
/* 3495 */     byte[] arrayOfByte1 = new byte[300];
/* 3496 */     getCursorName(paramLong, arrayOfByte1, arrayOfByte2);
/*      */ 
/* 3498 */     if (arrayOfByte2[0] != 0) {
/*      */       try
/*      */       {
/* 3501 */         standardError((short)arrayOfByte2[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 3510 */         String str2 = new String();
/*      */         try {
/* 3512 */           str2 = BytesToChars(this.charSet, arrayOfByte1);
/*      */         } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */         }
/* 3515 */         if (this.tracer.isTracing()) {
/* 3516 */           this.tracer.trace("value=" + str2.trim());
/*      */         }
/* 3518 */         localJdbcOdbcSQLWarning.value = str2.trim();
/*      */ 
/* 3522 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */     }
/*      */ 
/* 3526 */     String str1 = new String(arrayOfByte1);
/* 3527 */     if (this.tracer.isTracing()) {
/* 3528 */       this.tracer.trace("value=" + str1.trim());
/*      */     }
/* 3530 */     return str1.trim();
/*      */   }
/*      */ 
/*      */   public int SQLGetDataBinary(long paramLong, int paramInt, byte[] paramArrayOfByte)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 3543 */     return SQLGetDataBinary(paramLong, paramInt, -2, paramArrayOfByte, paramArrayOfByte.length);
/*      */   }
/*      */ 
/*      */   public int SQLGetDataBinary(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 3560 */     int i = 0;
/*      */ 
/* 3562 */     if (this.tracer.isTracing()) {
/* 3563 */       this.tracer.trace("Get binary data (SQLGetData), hStmt=" + paramLong + ", column=" + paramInt1 + ", type=" + paramInt2 + ", length=" + paramInt3);
/*      */     }
/*      */ 
/* 3572 */     byte[] arrayOfByte = new byte[2];
/* 3573 */     i = getDataBinary(paramLong, paramInt1, paramInt2, paramArrayOfByte, paramInt3, arrayOfByte);
/*      */ 
/* 3578 */     if (arrayOfByte[0] == 100) {
/* 3579 */       i = -1;
/* 3580 */       arrayOfByte[0] = 0;
/*      */     }
/*      */ 
/* 3583 */     if (arrayOfByte[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 3587 */         standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 3594 */         if (this.tracer.isTracing()) {
/* 3595 */           if (i == -1) {
/* 3596 */             this.tracer.trace("NULL");
/*      */           }
/* 3599 */           else if (this.tracer.isTracing()) {
/* 3600 */             this.tracer.trace("Bytes: " + i);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3609 */         localJdbcOdbcSQLWarning.value = new Integer(i);
/*      */ 
/* 3612 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */     }
/* 3615 */     if (this.tracer.isTracing()) {
/* 3616 */       if (i == -1) {
/* 3617 */         this.tracer.trace("NULL");
/*      */       }
/* 3620 */       else if (this.tracer.isTracing()) {
/* 3621 */         this.tracer.trace("Bytes: " + i);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3626 */     return i;
/*      */   }
/*      */ 
/*      */   public Double SQLGetDataDouble(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 3640 */     if (this.tracer.isTracing()) {
/* 3641 */       this.tracer.trace("Get double data (SQLGetData), hStmt=" + paramLong + ", column=" + paramInt);
/*      */     }
/*      */ 
/* 3649 */     byte[] arrayOfByte = new byte[2];
/* 3650 */     double d = getDataDouble(paramLong, paramInt, arrayOfByte);
/*      */ 
/* 3652 */     if (arrayOfByte[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 3656 */         standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 3666 */         if (arrayOfByte[1] == 0) {
/* 3667 */           if (this.tracer.isTracing()) {
/* 3668 */             this.tracer.trace("value=" + d);
/*      */           }
/*      */ 
/* 3672 */           localJdbcOdbcSQLWarning.value = new Double(d);
/*      */         }
/*      */         else
/*      */         {
/* 3676 */           if (this.tracer.isTracing()) {
/* 3677 */             this.tracer.trace("NULL");
/*      */           }
/*      */ 
/* 3681 */           localJdbcOdbcSQLWarning.value = null;
/*      */         }
/*      */ 
/* 3685 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3691 */     if (arrayOfByte[1] == 0) {
/* 3692 */       if (this.tracer.isTracing()) {
/* 3693 */         this.tracer.trace("value=" + d);
/*      */       }
/*      */ 
/* 3696 */       return new Double(d);
/*      */     }
/*      */ 
/* 3699 */     if (this.tracer.isTracing()) {
/* 3700 */       this.tracer.trace("NULL");
/*      */     }
/*      */ 
/* 3703 */     return null;
/*      */   }
/*      */ 
/*      */   public Float SQLGetDataFloat(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 3718 */     if (this.tracer.isTracing()) {
/* 3719 */       this.tracer.trace("Get float data (SQLGetData), hStmt=" + paramLong + ", column=" + paramInt);
/*      */     }
/*      */ 
/* 3727 */     byte[] arrayOfByte = new byte[2];
/*      */ 
/* 3729 */     float f = (float)getDataFloat(paramLong, paramInt, arrayOfByte);
/*      */ 
/* 3731 */     if (arrayOfByte[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 3735 */         standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 3745 */         if (arrayOfByte[1] == 0) {
/* 3746 */           if (this.tracer.isTracing()) {
/* 3747 */             this.tracer.trace("value=" + f);
/*      */           }
/*      */ 
/* 3750 */           localJdbcOdbcSQLWarning.value = new Float(f);
/*      */         }
/*      */         else {
/* 3753 */           if (this.tracer.isTracing()) {
/* 3754 */             this.tracer.trace("NULL");
/*      */           }
/*      */ 
/* 3757 */           localJdbcOdbcSQLWarning.value = null;
/*      */         }
/*      */ 
/* 3761 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3767 */     if (arrayOfByte[1] == 0)
/*      */     {
/* 3769 */       if (this.tracer.isTracing()) {
/* 3770 */         this.tracer.trace("value=" + f);
/*      */       }
/* 3772 */       return new Float(f);
/*      */     }
/*      */ 
/* 3775 */     if (this.tracer.isTracing()) {
/* 3776 */       this.tracer.trace("NULL");
/*      */     }
/*      */ 
/* 3779 */     return null;
/*      */   }
/*      */ 
/*      */   public Integer SQLGetDataInteger(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 3794 */     if (this.tracer.isTracing()) {
/* 3795 */       this.tracer.trace("Get integer data (SQLGetData), hStmt=" + paramLong + ", column=" + paramInt);
/*      */     }
/*      */ 
/* 3803 */     byte[] arrayOfByte = new byte[2];
/* 3804 */     int i = getDataInteger(paramLong, paramInt, arrayOfByte);
/*      */ 
/* 3806 */     if (arrayOfByte[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 3810 */         standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 3820 */         if (arrayOfByte[1] == 0) {
/* 3821 */           if (this.tracer.isTracing()) {
/* 3822 */             this.tracer.trace("value=" + i);
/*      */           }
/*      */ 
/* 3825 */           localJdbcOdbcSQLWarning.value = new Integer(i);
/*      */         }
/*      */         else
/*      */         {
/* 3829 */           if (this.tracer.isTracing()) {
/* 3830 */             this.tracer.trace("NULL");
/*      */           }
/*      */ 
/* 3833 */           localJdbcOdbcSQLWarning.value = null;
/*      */         }
/*      */ 
/* 3837 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3843 */     if (arrayOfByte[1] == 0) {
/* 3844 */       if (this.tracer.isTracing()) {
/* 3845 */         this.tracer.trace("value=" + i);
/*      */       }
/*      */ 
/* 3848 */       return new Integer(i);
/*      */     }
/*      */ 
/* 3851 */     if (this.tracer.isTracing()) {
/* 3852 */       this.tracer.trace("NULL");
/*      */     }
/*      */ 
/* 3855 */     return null;
/*      */   }
/*      */ 
/*      */   public String SQLGetDataString(long paramLong, int paramInt1, int paramInt2, boolean paramBoolean)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 3872 */     if (this.tracer.isTracing()) {
/* 3873 */       this.tracer.trace("Get string data (SQLGetData), hStmt=" + paramLong + ", column=" + paramInt1 + ", maxLen=" + paramInt2);
/*      */     }
/*      */ 
/* 3881 */     byte[] arrayOfByte1 = new byte[2];
/* 3882 */     byte[] arrayOfByte2 = new byte[paramInt2];
/*      */ 
/* 3884 */     int i = getDataString(paramLong, paramInt1, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 3889 */     if (i < 0) {
/* 3890 */       arrayOfByte1[1] = 1;
/*      */     }
/*      */ 
/* 3898 */     if (i > paramInt2)
/* 3899 */       i = paramInt2;
/*      */     char[] arrayOfChar;
/* 3902 */     if (arrayOfByte1[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 3906 */         standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 3916 */         if (arrayOfByte1[1] == 0)
/*      */         {
/* 3920 */           arrayOfChar = new char[i];
/* 3921 */           String str2 = new String();
/* 3922 */           if (i > 0)
/*      */             try {
/* 3924 */               str2 = BytesToChars(this.charSet, arrayOfByte2);
/*      */             } catch (UnsupportedEncodingException localUnsupportedEncodingException3) {
/* 3926 */               System.out.println(localUnsupportedEncodingException3);
/*      */             }
/*      */           else {
/*      */             try
/*      */             {
/* 3931 */               str2 = BytesToChars(this.charSet, arrayOfByte2);
/*      */             } catch (UnsupportedEncodingException localUnsupportedEncodingException4) {
/* 3933 */               System.out.println(localUnsupportedEncodingException4);
/*      */             }
/*      */           }
/*      */ 
/* 3937 */           if (this.tracer.isTracing()) {
/* 3938 */             this.tracer.trace(str2.trim());
/*      */           }
/* 3940 */           if (paramBoolean) {
/* 3941 */             localJdbcOdbcSQLWarning.value = str2.trim();
/*      */           }
/*      */           else
/* 3944 */             localJdbcOdbcSQLWarning.value = str2;
/*      */         }
/*      */         else
/*      */         {
/* 3948 */           if (this.tracer.isTracing()) {
/* 3949 */             this.tracer.trace("NULL");
/*      */           }
/*      */ 
/* 3952 */           localJdbcOdbcSQLWarning.value = null;
/*      */         }
/*      */ 
/* 3956 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3962 */     if (arrayOfByte1[1] == 0)
/*      */     {
/* 3966 */       String str1 = new String();
/* 3967 */       arrayOfChar = new char[i];
/* 3968 */       if (i > 0)
/*      */         try {
/* 3970 */           str1 = BytesToChars(this.charSet, arrayOfByte2);
/*      */         } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
/* 3972 */           System.out.println(localUnsupportedEncodingException1);
/*      */         }
/*      */       else {
/*      */         try
/*      */         {
/* 3977 */           str1 = BytesToChars(this.charSet, arrayOfByte2);
/*      */         } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
/* 3979 */           System.out.println(localUnsupportedEncodingException2);
/*      */         }
/*      */       }
/*      */ 
/* 3983 */       if (this.tracer.isTracing()) {
/* 3984 */         this.tracer.trace(str1.trim());
/*      */       }
/*      */ 
/* 3989 */       if (paramBoolean) {
/* 3990 */         return str1.trim();
/*      */       }
/*      */ 
/* 3993 */       return str1;
/*      */     }
/*      */ 
/* 3997 */     if (this.tracer.isTracing()) {
/* 3998 */       this.tracer.trace("NULL");
/*      */     }
/* 4000 */     return null;
/*      */   }
/*      */ 
/*      */   public String SQLGetDataStringDate(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 4015 */     if (this.tracer.isTracing()) {
/* 4016 */       this.tracer.trace("Get date data (SQLGetData), hStmt=" + paramLong + ", column=" + paramInt);
/*      */     }
/*      */ 
/* 4024 */     byte[] arrayOfByte1 = new byte[2];
/* 4025 */     byte[] arrayOfByte2 = new byte[11];
/*      */ 
/* 4027 */     getDataStringDate(paramLong, paramInt, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 4029 */     if (arrayOfByte1[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 4033 */         standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 4043 */         if (arrayOfByte1[1] == 0)
/*      */         {
/* 4045 */           String str2 = new String();
/*      */           try {
/* 4047 */             str2 = BytesToChars(this.charSet, arrayOfByte2);
/*      */           } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
/*      */           }
/* 4050 */           if (this.tracer.isTracing()) {
/* 4051 */             this.tracer.trace(str2.trim());
/*      */           }
/* 4053 */           localJdbcOdbcSQLWarning.value = str2.trim();
/*      */         }
/*      */         else {
/* 4056 */           if (this.tracer.isTracing()) {
/* 4057 */             this.tracer.trace("NULL");
/*      */           }
/*      */ 
/* 4060 */           localJdbcOdbcSQLWarning.value = null;
/*      */         }
/*      */ 
/* 4064 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4070 */     if (arrayOfByte1[1] == 0) {
/* 4071 */       String str1 = new String();
/*      */       try {
/* 4073 */         str1 = BytesToChars(this.charSet, arrayOfByte2);
/*      */       } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
/*      */       }
/* 4076 */       if (this.tracer.isTracing()) {
/* 4077 */         this.tracer.trace(str1.trim());
/*      */       }
/*      */ 
/* 4082 */       return str1.trim();
/*      */     }
/*      */ 
/* 4085 */     if (this.tracer.isTracing()) {
/* 4086 */       this.tracer.trace("NULL");
/*      */     }
/* 4088 */     return null;
/*      */   }
/*      */ 
/*      */   public String SQLGetDataStringTime(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 4103 */     if (this.tracer.isTracing()) {
/* 4104 */       this.tracer.trace("Get time data (SQLGetData), hStmt=" + paramLong + ", column=" + paramInt);
/*      */     }
/*      */ 
/* 4112 */     byte[] arrayOfByte1 = new byte[2];
/* 4113 */     byte[] arrayOfByte2 = new byte[9];
/*      */ 
/* 4115 */     getDataStringTime(paramLong, paramInt, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 4117 */     if (arrayOfByte1[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 4121 */         standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 4131 */         if (arrayOfByte1[1] == 0)
/*      */         {
/* 4133 */           String str2 = new String();
/*      */           try {
/* 4135 */             str2 = BytesToChars(this.charSet, arrayOfByte2);
/*      */           } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
/*      */           }
/* 4138 */           if (this.tracer.isTracing()) {
/* 4139 */             this.tracer.trace(str2.trim());
/*      */           }
/* 4141 */           localJdbcOdbcSQLWarning.value = str2.trim();
/*      */         }
/*      */         else {
/* 4144 */           if (this.tracer.isTracing()) {
/* 4145 */             this.tracer.trace("NULL");
/*      */           }
/*      */ 
/* 4148 */           localJdbcOdbcSQLWarning.value = null;
/*      */         }
/*      */ 
/* 4152 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4158 */     if (arrayOfByte1[1] == 0) {
/* 4159 */       String str1 = new String();
/*      */       try {
/* 4161 */         str1 = BytesToChars(this.charSet, arrayOfByte2);
/*      */       } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
/*      */       }
/* 4164 */       if (this.tracer.isTracing()) {
/* 4165 */         this.tracer.trace(str1.trim());
/*      */       }
/*      */ 
/* 4170 */       return str1.trim();
/*      */     }
/*      */ 
/* 4173 */     if (this.tracer.isTracing()) {
/* 4174 */       this.tracer.trace("NULL");
/*      */     }
/* 4176 */     return null;
/*      */   }
/*      */ 
/*      */   public String SQLGetDataStringTimestamp(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 4191 */     if (this.tracer.isTracing()) {
/* 4192 */       this.tracer.trace("Get timestamp data (SQLGetData), hStmt=" + paramLong + ", column=" + paramInt);
/*      */     }
/*      */ 
/* 4200 */     byte[] arrayOfByte1 = new byte[2];
/* 4201 */     byte[] arrayOfByte2 = new byte[30];
/*      */ 
/* 4203 */     getDataStringTimestamp(paramLong, paramInt, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 4205 */     if (arrayOfByte1[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 4209 */         standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 4219 */         if (arrayOfByte1[1] == 0)
/*      */         {
/* 4221 */           String str2 = new String();
/*      */           try {
/* 4223 */             str2 = BytesToChars(this.charSet, arrayOfByte2);
/*      */           } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
/*      */           }
/* 4226 */           if (this.tracer.isTracing()) {
/* 4227 */             this.tracer.trace(str2.trim());
/*      */           }
/* 4229 */           localJdbcOdbcSQLWarning.value = str2.trim();
/*      */         }
/*      */         else {
/* 4232 */           if (this.tracer.isTracing()) {
/* 4233 */             this.tracer.trace("NULL");
/*      */           }
/*      */ 
/* 4236 */           localJdbcOdbcSQLWarning.value = null;
/*      */         }
/*      */ 
/* 4240 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4246 */     if (arrayOfByte1[1] == 0) {
/* 4247 */       String str1 = new String();
/*      */       try {
/* 4249 */         str1 = BytesToChars(this.charSet, arrayOfByte2);
/*      */       } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
/*      */       }
/* 4252 */       if (this.tracer.isTracing()) {
/* 4253 */         this.tracer.trace(str1.trim());
/*      */       }
/*      */ 
/* 4258 */       return str1.trim();
/*      */     }
/*      */ 
/* 4261 */     if (this.tracer.isTracing()) {
/* 4262 */       this.tracer.trace("NULL");
/*      */     }
/* 4264 */     return null;
/*      */   }
/*      */ 
/*      */   public int SQLGetInfo(long paramLong, short paramShort)
/*      */     throws SQLException
/*      */   {
/* 4280 */     if (this.tracer.isTracing()) {
/* 4281 */       this.tracer.trace("Get connection info (SQLGetInfo), hDbc=" + paramLong + ", fInfoType=" + paramShort);
/*      */     }
/*      */ 
/* 4285 */     byte[] arrayOfByte = new byte[1];
/* 4286 */     int i = getInfo(paramLong, paramShort, arrayOfByte);
/*      */ 
/* 4288 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 4292 */       standardError((short)arrayOfByte[0], 0L, paramLong, 0L);
/*      */     }
/*      */ 
/* 4295 */     if (this.tracer.isTracing()) {
/* 4296 */       this.tracer.trace(" int value=" + i);
/*      */     }
/* 4298 */     return i;
/*      */   }
/*      */ 
/*      */   public int SQLGetInfoShort(long paramLong, short paramShort)
/*      */     throws SQLException
/*      */   {
/* 4313 */     if (this.tracer.isTracing()) {
/* 4314 */       this.tracer.trace("Get connection info (SQLGetInfo), hDbc=" + paramLong + ", fInfoType=" + paramShort);
/*      */     }
/*      */ 
/* 4318 */     byte[] arrayOfByte = new byte[1];
/* 4319 */     int i = getInfoShort(paramLong, paramShort, arrayOfByte);
/*      */ 
/* 4321 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 4325 */       standardError((short)arrayOfByte[0], 0L, paramLong, 0L);
/*      */     }
/*      */ 
/* 4328 */     if (this.tracer.isTracing()) {
/* 4329 */       this.tracer.trace(" short value=" + i);
/*      */     }
/* 4331 */     return i;
/*      */   }
/*      */ 
/*      */   public String SQLGetInfoString(long paramLong, short paramShort)
/*      */     throws SQLException
/*      */   {
/* 4344 */     return SQLGetInfoString(paramLong, paramShort, 300);
/*      */   }
/*      */ 
/*      */   public String SQLGetInfoString(long paramLong, short paramShort, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 4363 */     if (this.tracer.isTracing()) {
/* 4364 */       this.tracer.trace("Get connection info string (SQLGetInfo), hDbc=" + paramLong + ", fInfoType=" + paramShort + ", len=" + paramInt);
/*      */     }
/*      */ 
/* 4368 */     byte[] arrayOfByte1 = new byte[1];
/* 4369 */     byte[] arrayOfByte2 = new byte[paramInt];
/*      */ 
/* 4371 */     getInfoString(paramLong, paramShort, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 4373 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 4377 */       standardError((short)arrayOfByte1[0], 0L, paramLong, 0L);
/*      */     }
/*      */ 
/* 4381 */     String str = new String();
/*      */     try {
/* 4383 */       str = BytesToChars(this.charSet, arrayOfByte2);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 4386 */     if (this.tracer.isTracing()) {
/* 4387 */       this.tracer.trace(str.trim());
/*      */     }
/* 4389 */     return str.trim();
/*      */   }
/*      */ 
/*      */   public long SQLGetStmtOption(long paramLong, short paramShort)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 4401 */     long l = 0L;
/*      */ 
/* 4404 */     if (this.tracer.isTracing()) {
/* 4405 */       this.tracer.trace("Get statement option (SQLGetStmtOption), hStmt=" + paramLong + ", fOption=" + paramShort);
/*      */     }
/*      */ 
/* 4409 */     byte[] arrayOfByte = new byte[1];
/* 4410 */     l = getStmtOption(paramLong, paramShort, arrayOfByte);
/*      */ 
/* 4412 */     if (arrayOfByte[0] != 0) {
/*      */       try
/*      */       {
/* 4415 */         standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 4421 */         if (this.tracer.isTracing()) {
/* 4422 */           this.tracer.trace("value=" + l);
/*      */         }
/*      */ 
/* 4427 */         localJdbcOdbcSQLWarning.value = BigDecimal.valueOf(l);
/*      */ 
/* 4431 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */     }
/*      */ 
/* 4435 */     if (this.tracer.isTracing()) {
/* 4436 */       this.tracer.trace("value=" + l);
/*      */     }
/* 4438 */     return l;
/*      */   }
/*      */ 
/*      */   public int SQLGetStmtAttr(long paramLong, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 4453 */     if (this.tracer.isTracing()) {
/* 4454 */       this.tracer.trace("Get Statement Attribute (SQLGetStmtAttr), hDbc=" + paramLong + ", AttrType=" + paramInt);
/*      */     }
/*      */ 
/* 4458 */     byte[] arrayOfByte = new byte[1];
/* 4459 */     int i = getStmtAttr(paramLong, paramInt, arrayOfByte);
/*      */ 
/* 4461 */     if (arrayOfByte[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 4466 */         standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 4472 */         if (this.tracer.isTracing()) {
/* 4473 */           this.tracer.trace("value=" + i);
/*      */         }
/*      */ 
/* 4479 */         localJdbcOdbcSQLWarning.value = BigDecimal.valueOf(i);
/*      */ 
/* 4483 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4488 */     if (this.tracer.isTracing()) {
/* 4489 */       this.tracer.trace(" int value=" + i);
/*      */     }
/* 4491 */     return i;
/*      */   }
/*      */ 
/*      */   public void SQLGetTypeInfo(long paramLong, short paramShort)
/*      */     throws SQLException
/*      */   {
/* 4506 */     if (this.tracer.isTracing()) {
/* 4507 */       this.tracer.trace("Get type info (SQLGetTypeInfo), hStmt=" + paramLong + ", fSqlType=" + paramShort);
/*      */     }
/*      */ 
/* 4511 */     byte[] arrayOfByte = new byte[1];
/* 4512 */     getTypeInfo(paramLong, paramShort, arrayOfByte);
/*      */ 
/* 4514 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 4518 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean SQLMoreResults(long paramLong)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 4531 */     boolean bool = true;
/*      */ 
/* 4533 */     if (this.tracer.isTracing()) {
/* 4534 */       this.tracer.trace("Get more results (SQLMoreResults), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 4537 */     byte[] arrayOfByte = new byte[1];
/* 4538 */     moreResults(paramLong, arrayOfByte);
/*      */ 
/* 4544 */     if (arrayOfByte[0] == 100) {
/* 4545 */       bool = false;
/* 4546 */       arrayOfByte[0] = 0;
/*      */     }
/*      */ 
/* 4549 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 4553 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 4556 */     if (this.tracer.isTracing()) {
/* 4557 */       this.tracer.trace("More results: " + bool);
/*      */     }
/* 4559 */     return bool;
/*      */   }
/*      */ 
/*      */   public String SQLNativeSql(long paramLong, String paramString)
/*      */     throws SQLException
/*      */   {
/* 4574 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 4579 */     int i = 1024;
/* 4580 */     if (paramString.length() * 4 > i) {
/* 4581 */       i = paramString.length() * 4;
/* 4582 */       if (i > 32768) {
/* 4583 */         i = 32768;
/*      */       }
/*      */     }
/*      */ 
/* 4587 */     if (this.tracer.isTracing()) {
/* 4588 */       this.tracer.trace("Convert native SQL (SQLNativeSql), hDbc=" + paramLong + ", nativeLen=" + i + ", SQL=" + paramString);
/*      */     }
/*      */ 
/* 4593 */     byte[] arrayOfByte2 = new byte[i];
/* 4594 */     byte[] arrayOfByte3 = null;
/* 4595 */     char[] arrayOfChar = null;
/* 4596 */     if (paramString != null)
/* 4597 */       arrayOfChar = paramString.toCharArray();
/*      */     try {
/* 4599 */       if (paramString != null)
/* 4600 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
/*      */     }
/* 4603 */     nativeSql(paramLong, arrayOfByte3, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 4605 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 4609 */       standardError((short)arrayOfByte1[0], 0L, paramLong, 0L);
/*      */     }
/*      */ 
/* 4613 */     String str = new String();
/*      */     try {
/* 4615 */       str = BytesToChars(this.charSet, arrayOfByte2);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
/*      */     }
/* 4618 */     if (this.tracer.isTracing()) {
/* 4619 */       this.tracer.trace("Native SQL=" + str.trim());
/*      */     }
/* 4621 */     return str.trim();
/*      */   }
/*      */ 
/*      */   public int SQLNumParams(long paramLong)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 4632 */     int i = 0;
/*      */ 
/* 4634 */     if (this.tracer.isTracing()) {
/* 4635 */       this.tracer.trace("Number of parameter markers (SQLNumParams), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 4638 */     byte[] arrayOfByte = new byte[1];
/* 4639 */     i = numParams(paramLong, arrayOfByte);
/*      */ 
/* 4641 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 4645 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 4648 */     if (this.tracer.isTracing()) {
/* 4649 */       this.tracer.trace("value=" + i);
/*      */     }
/* 4651 */     return i;
/*      */   }
/*      */ 
/*      */   public int SQLNumResultCols(long paramLong)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 4662 */     int i = 0;
/*      */ 
/* 4664 */     if (this.tracer.isTracing()) {
/* 4665 */       this.tracer.trace("Number of result columns (SQLNumResultCols), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 4668 */     byte[] arrayOfByte = new byte[1];
/* 4669 */     i = numResultCols(paramLong, arrayOfByte);
/*      */ 
/* 4671 */     if (arrayOfByte[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 4675 */         standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 4680 */         if (this.tracer.isTracing()) {
/* 4681 */           this.tracer.trace("value=" + i);
/*      */         }
/*      */ 
/* 4687 */         localJdbcOdbcSQLWarning.value = BigDecimal.valueOf(i);
/*      */ 
/* 4691 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */     }
/* 4694 */     if (this.tracer.isTracing()) {
/* 4695 */       this.tracer.trace("value=" + i);
/*      */     }
/* 4697 */     return i;
/*      */   }
/*      */ 
/*      */   public int SQLParamData(long paramLong)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 4709 */     int i = 0;
/*      */ 
/* 4711 */     if (this.tracer.isTracing()) {
/* 4712 */       this.tracer.trace("Get parameter number (SQLParamData), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 4716 */     byte[] arrayOfByte = new byte[1];
/* 4717 */     i = paramData(paramLong, arrayOfByte);
/*      */ 
/* 4722 */     if (arrayOfByte[0] == 99) {
/* 4723 */       arrayOfByte[0] = 0;
/*      */     }
/*      */     else
/*      */     {
/* 4729 */       i = -1;
/*      */     }
/*      */ 
/* 4732 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 4736 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 4740 */     if (this.tracer.isTracing()) {
/* 4741 */       this.tracer.trace("Parameter needing data=" + i);
/*      */     }
/*      */ 
/* 4744 */     return i;
/*      */   }
/*      */ 
/*      */   public int SQLParamDataInBlock(long paramLong, int paramInt)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 4758 */     int i = 0;
/*      */ 
/* 4760 */     if (this.tracer.isTracing()) {
/* 4761 */       this.tracer.trace("Get parameter number (SQLParamData in block-cursor), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 4765 */     byte[] arrayOfByte = new byte[1];
/* 4766 */     i = paramDataInBlock(paramLong, paramInt, arrayOfByte);
/*      */ 
/* 4771 */     if (arrayOfByte[0] == 99) {
/* 4772 */       arrayOfByte[0] = 0;
/*      */     }
/*      */     else
/*      */     {
/* 4778 */       i = -1;
/*      */     }
/*      */ 
/* 4781 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 4785 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 4789 */     if (this.tracer.isTracing()) {
/* 4790 */       this.tracer.trace("Parameter needing data=" + i);
/*      */     }
/*      */ 
/* 4793 */     return i;
/*      */   }
/*      */ 
/*      */   public void SQLPrepare(long paramLong, String paramString)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 4806 */     if (this.tracer.isTracing()) {
/* 4807 */       this.tracer.trace("Preparing (SQLPrepare), hStmt=" + paramLong + ", szSqlStr=" + paramString);
/*      */     }
/*      */ 
/* 4811 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 4813 */     byte[] arrayOfByte2 = null;
/* 4814 */     char[] arrayOfChar = null;
/* 4815 */     if (paramString != null)
/* 4816 */       arrayOfChar = paramString.toCharArray();
/*      */     try {
/* 4818 */       if (paramString != null)
/* 4819 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar);
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 4823 */     prepare(paramLong, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 4825 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 4829 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLPutData(long paramLong, byte[] paramArrayOfByte, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 4846 */     if (this.tracer.isTracing()) {
/* 4847 */       this.tracer.trace("Putting data (SQLPutData), hStmt=" + paramLong + ", len=" + paramInt);
/*      */     }
/*      */ 
/* 4852 */     byte[] arrayOfByte = new byte[1];
/* 4853 */     putData(paramLong, paramArrayOfByte, paramInt, arrayOfByte);
/*      */ 
/* 4855 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 4858 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLPrimaryKeys(long paramLong, String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 4877 */     if (this.tracer.isTracing()) {
/* 4878 */       this.tracer.trace("Primary keys (SQLPrimaryKeys), hStmt=" + paramLong + ", catalog=" + paramString1 + ", schema=" + paramString2 + ", table=" + paramString3);
/*      */     }
/*      */ 
/* 4883 */     byte[] arrayOfByte2 = null;
/* 4884 */     byte[] arrayOfByte3 = null;
/* 4885 */     byte[] arrayOfByte4 = null;
/* 4886 */     char[] arrayOfChar1 = null;
/* 4887 */     char[] arrayOfChar2 = null;
/* 4888 */     char[] arrayOfChar3 = null;
/* 4889 */     if (paramString1 != null)
/* 4890 */       arrayOfChar1 = paramString1.toCharArray();
/* 4891 */     if (paramString2 != null)
/* 4892 */       arrayOfChar2 = paramString2.toCharArray();
/* 4893 */     if (paramString3 != null)
/* 4894 */       arrayOfChar3 = paramString3.toCharArray();
/*      */     try {
/* 4896 */       if (paramString1 != null)
/* 4897 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar1);
/* 4898 */       if (paramString2 != null)
/* 4899 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar2);
/* 4900 */       if (paramString3 != null)
/* 4901 */         arrayOfByte4 = CharsToBytes(this.charSet, arrayOfChar3);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 4904 */     byte[] arrayOfByte1 = new byte[1];
/* 4905 */     primaryKeys(paramLong, arrayOfByte2, paramString1 == null, arrayOfByte3, paramString2 == null, arrayOfByte4, paramString3 == null, arrayOfByte1);
/*      */ 
/* 4911 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 4915 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLProcedures(long paramLong, String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 4933 */     if (this.tracer.isTracing()) {
/* 4934 */       this.tracer.trace("Procedures (SQLProcedures), hStmt=" + paramLong + ", catalog=" + paramString1 + ", schema=" + paramString2 + ", procedure=" + paramString3);
/*      */     }
/*      */ 
/* 4939 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 4941 */     byte[] arrayOfByte2 = null;
/* 4942 */     byte[] arrayOfByte3 = null;
/* 4943 */     byte[] arrayOfByte4 = null;
/* 4944 */     char[] arrayOfChar1 = null;
/* 4945 */     char[] arrayOfChar2 = null;
/* 4946 */     char[] arrayOfChar3 = null;
/* 4947 */     if (paramString1 != null)
/* 4948 */       arrayOfChar1 = paramString1.toCharArray();
/* 4949 */     if (paramString2 != null)
/* 4950 */       arrayOfChar2 = paramString2.toCharArray();
/* 4951 */     if (paramString3 != null)
/* 4952 */       arrayOfChar3 = paramString3.toCharArray();
/*      */     try {
/* 4954 */       if (paramString1 != null)
/* 4955 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar1);
/* 4956 */       if (paramString2 != null)
/* 4957 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar2);
/* 4958 */       if (paramString3 != null)
/* 4959 */         arrayOfByte4 = CharsToBytes(this.charSet, arrayOfChar3);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 4962 */     procedures(paramLong, arrayOfByte2, paramString1 == null, arrayOfByte3, paramString2 == null, arrayOfByte4, paramString3 == null, arrayOfByte1);
/*      */ 
/* 4968 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 4972 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLProcedureColumns(long paramLong, String paramString1, String paramString2, String paramString3, String paramString4)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 4991 */     if (this.tracer.isTracing()) {
/* 4992 */       this.tracer.trace("Procedure columns (SQLProcedureColumns), hStmt=" + paramLong + ", catalog=" + paramString1 + ", schema=" + paramString2 + ", procedure=" + paramString3 + ", column=" + paramString4);
/*      */     }
/*      */ 
/* 4997 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 4999 */     byte[] arrayOfByte2 = null;
/* 5000 */     byte[] arrayOfByte3 = null;
/* 5001 */     byte[] arrayOfByte4 = null;
/* 5002 */     byte[] arrayOfByte5 = null;
/* 5003 */     char[] arrayOfChar1 = null;
/* 5004 */     char[] arrayOfChar2 = null;
/* 5005 */     char[] arrayOfChar3 = null;
/* 5006 */     char[] arrayOfChar4 = null;
/* 5007 */     if (paramString1 != null)
/* 5008 */       arrayOfChar1 = paramString1.toCharArray();
/* 5009 */     if (paramString2 != null)
/* 5010 */       arrayOfChar2 = paramString2.toCharArray();
/* 5011 */     if (paramString3 != null)
/* 5012 */       arrayOfChar3 = paramString3.toCharArray();
/* 5013 */     if (paramString4 != null)
/* 5014 */       arrayOfChar4 = paramString4.toCharArray();
/*      */     try {
/* 5016 */       if (paramString1 != null)
/* 5017 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar1);
/* 5018 */       if (paramString2 != null)
/* 5019 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar2);
/* 5020 */       if (paramString3 != null)
/* 5021 */         arrayOfByte4 = CharsToBytes(this.charSet, arrayOfChar3);
/* 5022 */       if (paramString4 != null)
/* 5023 */         arrayOfByte5 = CharsToBytes(this.charSet, arrayOfChar4);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 5026 */     procedureColumns(paramLong, arrayOfByte2, paramString1 == null, arrayOfByte3, paramString2 == null, arrayOfByte4, paramString3 == null, arrayOfByte5, paramString4 == null, arrayOfByte1);
/*      */ 
/* 5033 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 5037 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int SQLRowCount(long paramLong)
/*      */     throws SQLException, JdbcOdbcSQLWarning
/*      */   {
/* 5050 */     int i = 0;
/*      */ 
/* 5052 */     if (this.tracer.isTracing()) {
/* 5053 */       this.tracer.trace("Number of affected rows (SQLRowCount), hStmt=" + paramLong);
/*      */     }
/*      */ 
/* 5056 */     byte[] arrayOfByte = new byte[1];
/* 5057 */     i = rowCount(paramLong, arrayOfByte);
/*      */ 
/* 5059 */     if (arrayOfByte[0] != 0)
/*      */     {
/*      */       try
/*      */       {
/* 5063 */         standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */       }
/*      */       catch (JdbcOdbcSQLWarning localJdbcOdbcSQLWarning)
/*      */       {
/* 5069 */         if (this.tracer.isTracing()) {
/* 5070 */           this.tracer.trace("value=" + i);
/*      */         }
/*      */ 
/* 5076 */         localJdbcOdbcSQLWarning.value = BigDecimal.valueOf(i);
/*      */ 
/* 5080 */         throw localJdbcOdbcSQLWarning;
/*      */       }
/*      */     }
/* 5083 */     if (this.tracer.isTracing()) {
/* 5084 */       this.tracer.trace("value=" + i);
/*      */     }
/* 5086 */     return i;
/*      */   }
/*      */ 
/*      */   public void SQLSetConnectOption(long paramLong, short paramShort, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 5101 */     if (this.tracer.isTracing()) {
/* 5102 */       this.tracer.trace("Setting connection option (SQLSetConnectOption), hDbc=" + paramLong + ", fOption=" + paramShort + ", vParam=" + paramInt);
/*      */     }
/*      */ 
/* 5106 */     byte[] arrayOfByte = new byte[1];
/* 5107 */     setConnectOption(paramLong, paramShort, paramInt, arrayOfByte);
/*      */ 
/* 5109 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 5113 */       standardError((short)arrayOfByte[0], 0L, paramLong, 0L);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLSetConnectOption(long paramLong, short paramShort, String paramString)
/*      */     throws SQLException
/*      */   {
/* 5130 */     if (this.tracer.isTracing()) {
/* 5131 */       this.tracer.trace("Setting connection option string (SQLSetConnectOption), hDbc=" + paramLong + ", fOption=" + paramShort + ", vParam=" + paramString);
/*      */     }
/*      */ 
/* 5135 */     byte[] arrayOfByte1 = new byte[1];
/* 5136 */     byte[] arrayOfByte2 = null;
/* 5137 */     char[] arrayOfChar = null;
/* 5138 */     if (paramString != null)
/* 5139 */       arrayOfChar = paramString.toCharArray();
/*      */     try {
/* 5141 */       if (paramString != null)
/* 5142 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 5145 */     setConnectOptionString(paramLong, paramShort, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 5147 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 5151 */       standardError((short)arrayOfByte1[0], 0L, paramLong, 0L);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLSetCursorName(long paramLong, String paramString)
/*      */     throws SQLException
/*      */   {
/* 5167 */     if (this.tracer.isTracing()) {
/* 5168 */       this.tracer.trace("Setting cursor name (SQLSetCursorName), hStmt=" + paramLong + ", szCursor=" + paramString);
/*      */     }
/*      */ 
/* 5172 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 5174 */     byte[] arrayOfByte2 = null;
/* 5175 */     char[] arrayOfChar = null;
/* 5176 */     if (paramString != null)
/* 5177 */       arrayOfChar = paramString.toCharArray();
/*      */     try {
/* 5179 */       if (paramString != null)
/* 5180 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 5183 */     setCursorName(paramLong, arrayOfByte2, arrayOfByte1);
/*      */ 
/* 5185 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 5189 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLSetStmtOption(long paramLong, short paramShort, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 5206 */     if (this.tracer.isTracing()) {
/* 5207 */       this.tracer.trace("Setting statement option (SQLSetStmtOption), hStmt=" + paramLong + ", fOption=" + paramShort + ", vParam=" + paramInt);
/*      */     }
/*      */ 
/* 5211 */     byte[] arrayOfByte = new byte[1];
/* 5212 */     setStmtOption(paramLong, paramShort, paramInt, arrayOfByte);
/*      */ 
/* 5214 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 5218 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLSetStmtAttr(long paramLong, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/* 5236 */     if (this.tracer.isTracing()) {
/* 5237 */       this.tracer.trace("Setting statement option (SQLSetStmtAttr), hStmt=" + paramLong + ", fOption=" + paramInt1 + ", vParam=" + paramInt2);
/*      */     }
/*      */ 
/* 5241 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 5243 */     setStmtAttr(paramLong, paramInt1, paramInt2, paramInt3, arrayOfByte);
/*      */ 
/* 5245 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 5249 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLSetStmtAttrPtr(long paramLong, int paramInt1, int[] paramArrayOfInt, int paramInt2, long[] paramArrayOfLong)
/*      */     throws SQLException
/*      */   {
/* 5270 */     if (this.tracer.isTracing()) {
/* 5271 */       this.tracer.trace("Setting statement option (SQLSetStmtAttr), hStmt=" + paramLong + ", fOption=" + paramInt1);
/*      */     }
/*      */ 
/* 5275 */     byte[] arrayOfByte = new byte[1];
/* 5276 */     setStmtAttrPtr(paramLong, paramInt1, paramArrayOfInt, paramInt2, arrayOfByte, paramArrayOfLong);
/*      */ 
/* 5278 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 5282 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean SQLSetPos(long paramLong, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/* 5300 */     boolean bool = false;
/*      */ 
/* 5302 */     if (this.tracer.isTracing()) {
/* 5303 */       this.tracer.trace("Setting row position (SQLSetPos), hStmt=" + paramLong + ", operation = " + paramInt2);
/*      */     }
/*      */ 
/* 5307 */     byte[] arrayOfByte = new byte[1];
/*      */ 
/* 5309 */     setPos(paramLong, paramInt1, paramInt2, paramInt3, arrayOfByte);
/*      */ 
/* 5314 */     if (arrayOfByte[0] == 99) {
/* 5315 */       if (this.tracer.isTracing()) {
/* 5316 */         this.tracer.trace("SQL_NEED_DATA returned");
/*      */       }
/* 5318 */       bool = true;
/* 5319 */       arrayOfByte[0] = 0;
/*      */     }
/*      */ 
/* 5322 */     if (arrayOfByte[0] != 0)
/*      */     {
/* 5326 */       standardError((short)arrayOfByte[0], 0L, 0L, paramLong);
/*      */     }
/*      */ 
/* 5330 */     return bool;
/*      */   }
/*      */ 
/*      */   public void SQLSpecialColumns(long paramLong, short paramShort, String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 5350 */     if (this.tracer.isTracing()) {
/* 5351 */       this.tracer.trace("Special columns (SQLSpecialColumns), hStmt=" + paramLong + ", fColType=" + paramShort + ",catalog=" + paramString1 + ", schema=" + paramString2 + ", table=" + paramString3 + ", fScope=" + paramInt + ", fNullable=" + paramBoolean);
/*      */     }
/*      */ 
/* 5357 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 5359 */     byte[] arrayOfByte2 = null;
/* 5360 */     byte[] arrayOfByte3 = null;
/* 5361 */     byte[] arrayOfByte4 = null;
/* 5362 */     char[] arrayOfChar1 = null;
/* 5363 */     char[] arrayOfChar2 = null;
/* 5364 */     char[] arrayOfChar3 = null;
/* 5365 */     if (paramString1 != null)
/* 5366 */       arrayOfChar1 = paramString1.toCharArray();
/* 5367 */     if (paramString2 != null)
/* 5368 */       arrayOfChar2 = paramString2.toCharArray();
/* 5369 */     if (paramString3 != null)
/* 5370 */       arrayOfChar3 = paramString3.toCharArray();
/*      */     try {
/* 5372 */       if (paramString1 != null)
/* 5373 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar1);
/* 5374 */       if (paramString2 != null)
/* 5375 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar2);
/* 5376 */       if (paramString3 != null)
/* 5377 */         arrayOfByte4 = CharsToBytes(this.charSet, arrayOfChar3);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 5380 */     specialColumns(paramLong, paramShort, arrayOfByte2, paramString1 == null, arrayOfByte3, paramString2 == null, arrayOfByte4, paramString3 == null, paramInt, paramBoolean, arrayOfByte1);
/*      */ 
/* 5387 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 5391 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLStatistics(long paramLong, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 5411 */     if (this.tracer.isTracing()) {
/* 5412 */       this.tracer.trace("Statistics (SQLStatistics), hStmt=" + paramLong + ",catalog=" + paramString1 + ", schema=" + paramString2 + ", table=" + paramString3 + ", unique=" + paramBoolean1 + ", approximate=" + paramBoolean2);
/*      */     }
/*      */ 
/* 5418 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 5420 */     byte[] arrayOfByte2 = null;
/* 5421 */     byte[] arrayOfByte3 = null;
/* 5422 */     byte[] arrayOfByte4 = null;
/* 5423 */     char[] arrayOfChar1 = null;
/* 5424 */     char[] arrayOfChar2 = null;
/* 5425 */     char[] arrayOfChar3 = null;
/* 5426 */     if (paramString1 != null)
/* 5427 */       arrayOfChar1 = paramString1.toCharArray();
/* 5428 */     if (paramString2 != null)
/* 5429 */       arrayOfChar2 = paramString2.toCharArray();
/* 5430 */     if (paramString3 != null)
/* 5431 */       arrayOfChar3 = paramString3.toCharArray();
/*      */     try {
/* 5433 */       if (paramString1 != null)
/* 5434 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar1);
/* 5435 */       if (paramString2 != null)
/* 5436 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar2);
/* 5437 */       if (paramString3 != null)
/* 5438 */         arrayOfByte4 = CharsToBytes(this.charSet, arrayOfChar3);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 5441 */     statistics(paramLong, arrayOfByte2, paramString1 == null, arrayOfByte3, paramString2 == null, arrayOfByte4, paramString3 == null, paramBoolean1, paramBoolean2, arrayOfByte1);
/*      */ 
/* 5448 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 5452 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLTables(long paramLong, String paramString1, String paramString2, String paramString3, String paramString4)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 5471 */     if (this.tracer.isTracing()) {
/* 5472 */       this.tracer.trace("Tables (SQLTables), hStmt=" + paramLong + ",catalog=" + paramString1 + ", schema=" + paramString2 + ", table=" + paramString3 + ", types=" + paramString4);
/*      */     }
/*      */ 
/* 5477 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 5479 */     byte[] arrayOfByte2 = null;
/* 5480 */     byte[] arrayOfByte3 = null;
/* 5481 */     byte[] arrayOfByte4 = null;
/* 5482 */     byte[] arrayOfByte5 = null;
/* 5483 */     char[] arrayOfChar1 = null;
/* 5484 */     char[] arrayOfChar2 = null;
/* 5485 */     char[] arrayOfChar3 = null;
/* 5486 */     char[] arrayOfChar4 = null;
/* 5487 */     if (paramString1 != null)
/* 5488 */       arrayOfChar1 = paramString1.toCharArray();
/* 5489 */     if (paramString2 != null)
/* 5490 */       arrayOfChar2 = paramString2.toCharArray();
/* 5491 */     if (paramString3 != null)
/* 5492 */       arrayOfChar3 = paramString3.toCharArray();
/* 5493 */     if (paramString4 != null)
/* 5494 */       arrayOfChar4 = paramString4.toCharArray();
/*      */     try {
/* 5496 */       if (paramString1 != null)
/* 5497 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar1);
/* 5498 */       if (paramString2 != null)
/* 5499 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar2);
/* 5500 */       if (paramString3 != null)
/* 5501 */         arrayOfByte4 = CharsToBytes(this.charSet, arrayOfChar3);
/* 5502 */       if (paramString4 != null)
/* 5503 */         arrayOfByte5 = CharsToBytes(this.charSet, arrayOfChar4);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 5506 */     tables(paramLong, arrayOfByte2, paramString1 == null, arrayOfByte3, paramString2 == null, arrayOfByte4, paramString3 == null, arrayOfByte5, paramString4 == null, arrayOfByte1);
/*      */ 
/* 5513 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 5517 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLTablePrivileges(long paramLong, String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 5535 */     if (this.tracer.isTracing()) {
/* 5536 */       this.tracer.trace("Tables (SQLTables), hStmt=" + paramLong + ",catalog=" + paramString1 + ", schema=" + paramString2 + ", table=" + paramString3);
/*      */     }
/*      */ 
/* 5541 */     byte[] arrayOfByte1 = new byte[1];
/*      */ 
/* 5543 */     byte[] arrayOfByte2 = null;
/* 5544 */     byte[] arrayOfByte3 = null;
/* 5545 */     byte[] arrayOfByte4 = null;
/* 5546 */     char[] arrayOfChar1 = null;
/* 5547 */     char[] arrayOfChar2 = null;
/* 5548 */     char[] arrayOfChar3 = null;
/* 5549 */     if (paramString1 != null)
/* 5550 */       arrayOfChar1 = paramString1.toCharArray();
/* 5551 */     if (paramString2 != null)
/* 5552 */       arrayOfChar2 = paramString2.toCharArray();
/* 5553 */     if (paramString3 != null)
/* 5554 */       arrayOfChar3 = paramString3.toCharArray();
/*      */     try {
/* 5556 */       if (paramString1 != null)
/* 5557 */         arrayOfByte2 = CharsToBytes(this.charSet, arrayOfChar1);
/* 5558 */       if (paramString2 != null)
/* 5559 */         arrayOfByte3 = CharsToBytes(this.charSet, arrayOfChar2);
/* 5560 */       if (paramString3 != null)
/* 5561 */         arrayOfByte4 = CharsToBytes(this.charSet, arrayOfChar3);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*      */     }
/* 5564 */     tablePrivileges(paramLong, arrayOfByte2, paramString1 == null, arrayOfByte3, paramString2 == null, arrayOfByte4, paramString3 == null, arrayOfByte1);
/*      */ 
/* 5570 */     if (arrayOfByte1[0] != 0)
/*      */     {
/* 5574 */       standardError((short)arrayOfByte1[0], 0L, 0L, paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SQLTransact(long paramLong1, long paramLong2, short paramShort)
/*      */     throws SQLException
/*      */   {
/* 5591 */     if (this.tracer.isTracing()) {
/* 5592 */       this.tracer.trace("Transaction (SQLTransact), hEnv=" + paramLong1 + ", hDbc=" + paramLong2 + ", fType=" + paramShort);
/*      */     }
/*      */ 
/* 5596 */     byte[] arrayOfByte = new byte[1];
/* 5597 */     transact(paramLong1, paramLong2, paramShort, arrayOfByte);
/*      */ 
/* 5599 */     if (arrayOfByte[0] != 0)
/* 5600 */       throwGenericSQLException();
/*      */   }
/*      */ 
/*      */   public native int bufferToInt(byte[] paramArrayOfByte);
/*      */ 
/*      */   public native float bufferToFloat(byte[] paramArrayOfByte);
/*      */ 
/*      */   public native double bufferToDouble(byte[] paramArrayOfByte);
/*      */ 
/*      */   public native long bufferToLong(byte[] paramArrayOfByte);
/*      */ 
/*      */   public native void convertDateString(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   public native void getDateStruct(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
/*      */ 
/*      */   public native void convertTimeString(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   public native void getTimeStruct(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
/*      */ 
/*      */   public native void getTimestampStruct(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, long paramLong);
/*      */ 
/*      */   public native void convertTimestampString(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   public static native int getSQLLENSize();
/*      */ 
/*      */   public static native void intToBytes(int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   public static native void longToBytes(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   public static native void intTo4Bytes(int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   public static SQLWarning convertWarning(JdbcOdbcSQLWarning paramJdbcOdbcSQLWarning)
/*      */   {
/* 5662 */     Object localObject = paramJdbcOdbcSQLWarning;
/*      */ 
/* 5667 */     if (paramJdbcOdbcSQLWarning.getSQLState().equals("01004")) {
/* 5668 */       DataTruncation localDataTruncation = new DataTruncation(-1, false, true, 0, 0);
/*      */ 
/* 5670 */       localObject = localDataTruncation;
/*      */     }
/* 5672 */     return localObject;
/*      */   }
/*      */ 
/*      */   protected native long allocConnect(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native long allocEnv(byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native long allocStmt(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void cancel(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void bindColAtExec(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void bindColBinary(long paramLong, int paramInt1, Object[] paramArrayOfObject, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, long[] paramArrayOfLong, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void bindColDate(long paramLong, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void bindColDefault(long paramLong, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void bindColDouble(long paramLong, int paramInt, double[] paramArrayOfDouble, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void bindColFloat(long paramLong, int paramInt, float[] paramArrayOfFloat, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void bindColInteger(long paramLong, int paramInt, int[] paramArrayOfInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void bindColString(long paramLong, int paramInt1, int paramInt2, Object[] paramArrayOfObject, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void bindColTime(long paramLong, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void bindColTimestamp(long paramLong, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4, int[] paramArrayOfInt5, int[] paramArrayOfInt6, int[] paramArrayOfInt7, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void bindInParameterAtExec(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInOutParameterAtExec(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, int paramInt5, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterBinary(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, int paramInt3, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterDate(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterDouble(long paramLong, int paramInt1, int paramInt2, int paramInt3, double paramDouble, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterFloat(long paramLong, int paramInt1, int paramInt2, int paramInt3, double paramDouble, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterBigint(long paramLong1, int paramInt1, int paramInt2, int paramInt3, long paramLong2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterInteger(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterNull(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterString(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, int paramInt3, int paramInt4, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterTime(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterTimestamp(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindOutParameterString(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInOutParameterDate(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInOutParameterTime(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInOutParameterString(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInOutParameterStr(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong, int paramInt4);
/*      */ 
/*      */   protected native void bindInOutParameterBin(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong, int paramInt4);
/*      */ 
/*      */   protected native void bindInOutParameterBinary(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInOutParameterFixed(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInOutParameterTimeStamp(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInOutParameter(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInOutParameterNull(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInOutParameterTimestamp(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindInParameterStringArray(long paramLong, int paramInt1, int paramInt2, Object[] paramArrayOfObject, byte[] paramArrayOfByte1, int paramInt3, int paramInt4, int[] paramArrayOfInt, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void bindInParameterIntegerArray(long paramLong, int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void bindInParameterFloatArray(long paramLong, int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, int[] paramArrayOfInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void bindInParameterDoubleArray(long paramLong, int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble, int[] paramArrayOfInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void bindInParameterDateArray(long paramLong, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int[] paramArrayOfInt4);
/*      */ 
/*      */   protected native void bindInParameterTimeArray(long paramLong, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int[] paramArrayOfInt4);
/*      */ 
/*      */   protected native void bindInParameterTimestampArray(long paramLong, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4, int[] paramArrayOfInt5, int[] paramArrayOfInt6, int[] paramArrayOfInt7, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int[] paramArrayOfInt8);
/*      */ 
/*      */   protected native void bindInParameterBinaryArray(long paramLong, int paramInt1, int paramInt2, Object[] paramArrayOfObject, int paramInt3, byte[] paramArrayOfByte1, int[] paramArrayOfInt, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void bindInParameterAtExecArray(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, int[] paramArrayOfInt, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void bindOutParameterNull(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindOutParameterFixed(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindOutParameterBinary(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindOutParameterDate(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindOutParameterTime(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void bindOutParameterTimestamp(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void browseConnect(long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native int colAttributes(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void colAttributesString(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void columns(long paramLong, byte[] paramArrayOfByte1, boolean paramBoolean1, byte[] paramArrayOfByte2, boolean paramBoolean2, byte[] paramArrayOfByte3, boolean paramBoolean3, byte[] paramArrayOfByte4, boolean paramBoolean4, byte[] paramArrayOfByte5);
/*      */ 
/*      */   protected native void columnPrivileges(long paramLong, byte[] paramArrayOfByte1, boolean paramBoolean1, byte[] paramArrayOfByte2, boolean paramBoolean2, byte[] paramArrayOfByte3, boolean paramBoolean3, byte[] paramArrayOfByte4, boolean paramBoolean4, byte[] paramArrayOfByte5);
/*      */ 
/*      */   protected native int describeParam(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void disconnect(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void driverConnect(long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native int error(long paramLong1, long paramLong2, long paramLong3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native void execDirect(long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void execute(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void fetch(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void fetchScroll(long paramLong, short paramShort, int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void foreignKeys(long paramLong, byte[] paramArrayOfByte1, boolean paramBoolean1, byte[] paramArrayOfByte2, boolean paramBoolean2, byte[] paramArrayOfByte3, boolean paramBoolean3, byte[] paramArrayOfByte4, boolean paramBoolean4, byte[] paramArrayOfByte5, boolean paramBoolean5, byte[] paramArrayOfByte6, boolean paramBoolean6, byte[] paramArrayOfByte7);
/*      */ 
/*      */   protected native void freeConnect(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void freeEnv(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void freeStmt(long paramLong, int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native long getConnectOption(long paramLong, short paramShort, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void getConnectOptionString(long paramLong, short paramShort, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void getCursorName(long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native long getStmtOption(long paramLong, short paramShort, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native int getStmtAttr(long paramLong, int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native int getDataBinary(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte1, int paramInt3, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native double getDataDouble(long paramLong, int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native double getDataFloat(long paramLong, int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native int getDataInteger(long paramLong, int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native int getDataString(long paramLong, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void getDataStringDate(long paramLong, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void getDataStringTime(long paramLong, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void getDataStringTimestamp(long paramLong, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native int getInfo(long paramLong, short paramShort, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native int getInfoShort(long paramLong, short paramShort, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void getInfoString(long paramLong, short paramShort, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void getTypeInfo(long paramLong, short paramShort, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void moreResults(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void nativeSql(long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3);
/*      */ 
/*      */   protected native int numParams(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native int numResultCols(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native int paramData(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native int paramDataInBlock(long paramLong, int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void prepare(long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void primaryKeys(long paramLong, byte[] paramArrayOfByte1, boolean paramBoolean1, byte[] paramArrayOfByte2, boolean paramBoolean2, byte[] paramArrayOfByte3, boolean paramBoolean3, byte[] paramArrayOfByte4);
/*      */ 
/*      */   protected native void procedures(long paramLong, byte[] paramArrayOfByte1, boolean paramBoolean1, byte[] paramArrayOfByte2, boolean paramBoolean2, byte[] paramArrayOfByte3, boolean paramBoolean3, byte[] paramArrayOfByte4);
/*      */ 
/*      */   protected native void procedureColumns(long paramLong, byte[] paramArrayOfByte1, boolean paramBoolean1, byte[] paramArrayOfByte2, boolean paramBoolean2, byte[] paramArrayOfByte3, boolean paramBoolean3, byte[] paramArrayOfByte4, boolean paramBoolean4, byte[] paramArrayOfByte5);
/*      */ 
/*      */   protected native void putData(long paramLong, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native int rowCount(long paramLong, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void setConnectOption(long paramLong, short paramShort, int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void setConnectOptionString(long paramLong, short paramShort, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void setCursorName(long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */ 
/*      */   protected native void setStmtOption(long paramLong, short paramShort, int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void setStmtAttr(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void setStmtAttrPtr(long paramLong, int paramInt1, int[] paramArrayOfInt, int paramInt2, byte[] paramArrayOfByte, long[] paramArrayOfLong);
/*      */ 
/*      */   protected native void setPos(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected native void specialColumns(long paramLong, short paramShort, byte[] paramArrayOfByte1, boolean paramBoolean1, byte[] paramArrayOfByte2, boolean paramBoolean2, byte[] paramArrayOfByte3, boolean paramBoolean3, int paramInt, boolean paramBoolean4, byte[] paramArrayOfByte4);
/*      */ 
/*      */   protected native void statistics(long paramLong, byte[] paramArrayOfByte1, boolean paramBoolean1, byte[] paramArrayOfByte2, boolean paramBoolean2, byte[] paramArrayOfByte3, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, byte[] paramArrayOfByte4);
/*      */ 
/*      */   protected native void tables(long paramLong, byte[] paramArrayOfByte1, boolean paramBoolean1, byte[] paramArrayOfByte2, boolean paramBoolean2, byte[] paramArrayOfByte3, boolean paramBoolean3, byte[] paramArrayOfByte4, boolean paramBoolean4, byte[] paramArrayOfByte5);
/*      */ 
/*      */   protected native void tablePrivileges(long paramLong, byte[] paramArrayOfByte1, boolean paramBoolean1, byte[] paramArrayOfByte2, boolean paramBoolean2, byte[] paramArrayOfByte3, boolean paramBoolean3, byte[] paramArrayOfByte4);
/*      */ 
/*      */   protected native void transact(long paramLong1, long paramLong2, short paramShort, byte[] paramArrayOfByte);
/*      */ 
/*      */   protected static native void ReleaseStoredBytes(long paramLong1, long paramLong2);
/*      */ 
/*      */   protected static native void ReleaseStoredChars(long paramLong1, long paramLong2);
/*      */ 
/*      */   protected static native void ReleaseStoredIntegers(long paramLong1, long paramLong2);
/*      */ 
/*      */   SQLException createSQLException(long paramLong1, long paramLong2, long paramLong3)
/*      */   {
/* 6923 */     int j = 0;
/* 6924 */     Object localObject1 = null;
/* 6925 */     Object localObject2 = null;
/*      */ 
/* 6927 */     if (this.tracer.isTracing())
/* 6928 */       this.tracer.trace("ERROR - Generating SQLException...");
/*      */     Object localObject3;
/*      */     String str1;
/* 6931 */     while (j == 0) {
/* 6932 */       byte[] arrayOfByte3 = new byte[1];
/* 6933 */       byte[] arrayOfByte1 = new byte[6];
/* 6934 */       byte[] arrayOfByte2 = new byte[300];
/*      */ 
/* 6937 */       int i = error(paramLong1, paramLong2, paramLong3, arrayOfByte1, arrayOfByte2, arrayOfByte3);
/*      */ 
/* 6940 */       if (arrayOfByte3[0] != 0) {
/* 6941 */         j = 1;
/*      */       }
/*      */       else {
/* 6944 */         localObject3 = null;
/*      */ 
/* 6946 */         str1 = new String();
/* 6947 */         String str2 = new String();
/*      */         try {
/* 6949 */           str1 = BytesToChars(this.charSet, arrayOfByte2);
/* 6950 */           str2 = BytesToChars(this.charSet, arrayOfByte1);
/*      */         }
/*      */         catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*      */         {
/*      */         }
/*      */ 
/* 6956 */         localObject3 = new SQLException(str1.trim(), str2.trim(), i);
/*      */ 
/* 6963 */         if (localObject1 == null) {
/* 6964 */           localObject1 = localObject3;
/*      */         }
/*      */         else {
/* 6967 */           localObject2.setNextException((SQLException)localObject3);
/*      */         }
/*      */ 
/* 6971 */         localObject2 = localObject3;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 6978 */     if (localObject1 == null) {
/* 6979 */       localObject3 = "General error";
/* 6980 */       str1 = "S1000";
/*      */ 
/* 6982 */       if (this.tracer.isTracing()) {
/* 6983 */         this.tracer.trace("ERROR - " + str1 + " " + (String)localObject3);
/*      */       }
/* 6985 */       localObject1 = new SQLException((String)localObject3, str1);
/*      */     }
/* 6987 */     return localObject1;
/*      */   }
/*      */ 
/*      */   SQLWarning createSQLWarning(long paramLong1, long paramLong2, long paramLong3)
/*      */   {
/* 7006 */     int j = 0;
/* 7007 */     Object localObject1 = null;
/* 7008 */     Object localObject2 = null;
/*      */ 
/* 7010 */     if (this.tracer.isTracing())
/* 7011 */       this.tracer.trace("WARNING - Generating SQLWarning...");
/*      */     Object localObject3;
/*      */     String str1;
/* 7014 */     while (j == 0) {
/* 7015 */       byte[] arrayOfByte3 = new byte[1];
/* 7016 */       byte[] arrayOfByte1 = new byte[6];
/* 7017 */       byte[] arrayOfByte2 = new byte[300];
/*      */ 
/* 7020 */       int i = error(paramLong1, paramLong2, paramLong3, arrayOfByte1, arrayOfByte2, arrayOfByte3);
/*      */ 
/* 7023 */       if (arrayOfByte3[0] != 0) {
/* 7024 */         j = 1;
/*      */       }
/*      */       else {
/* 7027 */         localObject3 = null;
/* 7028 */         str1 = new String();
/* 7029 */         String str2 = new String();
/*      */         try {
/* 7031 */           str1 = BytesToChars(this.charSet, arrayOfByte2);
/* 7032 */           str2 = BytesToChars(this.charSet, arrayOfByte1);
/*      */         }
/*      */         catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*      */         {
/*      */         }
/* 7037 */         localObject3 = new JdbcOdbcSQLWarning(str1.trim(), str2.trim(), i);
/*      */ 
/* 7045 */         if (localObject1 == null) {
/* 7046 */           localObject1 = localObject3;
/*      */         }
/*      */         else {
/* 7049 */           localObject2.setNextWarning((SQLWarning)localObject3);
/*      */         }
/*      */ 
/* 7053 */         localObject2 = localObject3;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 7060 */     if (localObject1 == null) {
/* 7061 */       localObject3 = "General warning";
/* 7062 */       str1 = "S1000";
/*      */ 
/* 7064 */       if (this.tracer.isTracing()) {
/* 7065 */         this.tracer.trace("WARNING - " + str1 + " " + (String)localObject3);
/*      */       }
/* 7067 */       localObject1 = new JdbcOdbcSQLWarning((String)localObject3, str1);
/*      */     }
/* 7069 */     return localObject1;
/*      */   }
/*      */ 
/*      */   void throwGenericSQLException()
/*      */     throws SQLException
/*      */   {
/* 7079 */     String str1 = "General error";
/* 7080 */     String str2 = "S1000";
/*      */ 
/* 7082 */     if (this.tracer.isTracing()) {
/* 7083 */       this.tracer.trace("ERROR - " + str2 + " " + str1);
/*      */     }
/* 7085 */     throw new SQLException(str1, str2);
/*      */   }
/*      */ 
/*      */   void standardError(short paramShort, long paramLong1, long paramLong2, long paramLong3)
/*      */     throws SQLException, SQLWarning
/*      */   {
/* 7103 */     if (this.tracer.isTracing())
/* 7104 */       this.tracer.trace("RETCODE = " + paramShort);
/*      */     String str;
/* 7107 */     switch (paramShort)
/*      */     {
/*      */     case -1:
/* 7113 */       throw createSQLException(paramLong1, paramLong2, paramLong3);
/*      */     case 1:
/* 7119 */       throw createSQLWarning(paramLong1, paramLong2, paramLong3);
/*      */     case -2:
/* 7124 */       str = "Invalid handle";
/* 7125 */       if (this.tracer.isTracing()) {
/* 7126 */         this.tracer.trace("ERROR - " + str);
/*      */       }
/* 7128 */       throw new SQLException(str);
/*      */     case 100:
/* 7133 */       str = "No data found";
/* 7134 */       if (this.tracer.isTracing()) {
/* 7135 */         this.tracer.trace("ERROR - " + str);
/*      */       }
/* 7137 */       throw new SQLException(str);
/*      */     }
/*      */ 
/* 7143 */     throwGenericSQLException();
/*      */   }
/*      */ 
/*      */   public JdbcOdbcTracer getTracer()
/*      */   {
/* 7153 */     return this.tracer;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.jdbc.odbc.JdbcOdbc
 * JD-Core Version:    0.6.2
 */