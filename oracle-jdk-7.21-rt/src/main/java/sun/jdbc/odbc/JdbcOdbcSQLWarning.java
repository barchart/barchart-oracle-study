/*    */ package sun.jdbc.odbc;
/*    */ 
/*    */ import java.sql.SQLWarning;
/*    */ 
/*    */ public class JdbcOdbcSQLWarning extends SQLWarning
/*    */ {
/*    */   Object value;
/*    */ 
/*    */   public JdbcOdbcSQLWarning(String paramString1, String paramString2, int paramInt)
/*    */   {
/* 39 */     super(paramString1, paramString2, paramInt);
/*    */   }
/*    */ 
/*    */   public JdbcOdbcSQLWarning(String paramString1, String paramString2)
/*    */   {
/* 44 */     super(paramString1, paramString2);
/*    */   }
/*    */ 
/*    */   public JdbcOdbcSQLWarning(String paramString)
/*    */   {
/* 49 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public JdbcOdbcSQLWarning()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.jdbc.odbc.JdbcOdbcSQLWarning
 * JD-Core Version:    0.6.2
 */