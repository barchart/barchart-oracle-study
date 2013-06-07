/*    */ package sun.management.snmp.jvminstr;
/*    */ 
/*    */ import com.sun.jmx.snmp.agent.SnmpMib;
/*    */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*    */ import javax.management.MBeanServer;
/*    */ import sun.management.snmp.jvmmib.JvmThreadInstanceTableMeta;
/*    */ import sun.management.snmp.jvmmib.JvmThreadingMeta;
/*    */ 
/*    */ public class JvmThreadingMetaImpl extends JvmThreadingMeta
/*    */ {
/*    */   public JvmThreadingMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*    */   {
/* 74 */     super(paramSnmpMib, paramSnmpStandardObjectServer);
/*    */   }
/*    */ 
/*    */   protected JvmThreadInstanceTableMeta createJvmThreadInstanceTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*    */   {
/* 98 */     return new JvmThreadInstanceTableMetaImpl(paramSnmpMib, this.objectserver);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.snmp.jvminstr.JvmThreadingMetaImpl
 * JD-Core Version:    0.6.2
 */