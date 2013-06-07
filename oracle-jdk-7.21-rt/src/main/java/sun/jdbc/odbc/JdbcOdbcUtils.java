/*    */ package sun.jdbc.odbc;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public class JdbcOdbcUtils
/*    */ {
/*    */   public long convertFromGMT(Date paramDate, Calendar paramCalendar)
/*    */   {
/* 27 */     long l = paramCalendar.getTimeZone().getRawOffset();
/* 28 */     return paramDate.getTime() + l;
/*    */   }
/*    */ 
/*    */   public long convertToGMT(Date paramDate, Calendar paramCalendar)
/*    */   {
/* 40 */     long l = paramCalendar.getTimeZone().getRawOffset();
/* 41 */     return paramDate.getTime() - l;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.jdbc.odbc.JdbcOdbcUtils
 * JD-Core Version:    0.6.2
 */