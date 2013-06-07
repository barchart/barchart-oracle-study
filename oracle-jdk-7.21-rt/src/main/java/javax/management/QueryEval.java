/*    */ package javax.management;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public abstract class QueryEval
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 2675899265640874796L;
/* 41 */   private static ThreadLocal<MBeanServer> server = new InheritableThreadLocal();
/*    */ 
/*    */   public void setMBeanServer(MBeanServer paramMBeanServer)
/*    */   {
/* 59 */     server.set(paramMBeanServer);
/*    */   }
/*    */ 
/*    */   public static MBeanServer getMBeanServer()
/*    */   {
/* 76 */     return (MBeanServer)server.get();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.QueryEval
 * JD-Core Version:    0.6.2
 */