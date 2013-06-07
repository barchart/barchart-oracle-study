/*    */ package javax.naming.ldap;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public class UnsolicitedNotificationEvent extends EventObject
/*    */ {
/*    */   private UnsolicitedNotification notice;
/*    */   private static final long serialVersionUID = -2382603380799883705L;
/*    */ 
/*    */   public UnsolicitedNotificationEvent(Object paramObject, UnsolicitedNotification paramUnsolicitedNotification)
/*    */   {
/* 59 */     super(paramObject);
/* 60 */     this.notice = paramUnsolicitedNotification;
/*    */   }
/*    */ 
/*    */   public UnsolicitedNotification getNotification()
/*    */   {
/* 70 */     return this.notice;
/*    */   }
/*    */ 
/*    */   public void dispatch(UnsolicitedNotificationListener paramUnsolicitedNotificationListener)
/*    */   {
/* 80 */     paramUnsolicitedNotificationListener.notificationReceived(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.ldap.UnsolicitedNotificationEvent
 * JD-Core Version:    0.6.2
 */