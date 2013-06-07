/*    */ package javax.management.timer;
/*    */ 
/*    */ import javax.management.Notification;
/*    */ 
/*    */ class TimerAlarmClockNotification extends Notification
/*    */ {
/*    */   private static final long serialVersionUID = -4841061275673620641L;
/*    */ 
/*    */   public TimerAlarmClockNotification(TimerAlarmClock paramTimerAlarmClock)
/*    */   {
/* 50 */     super("", paramTimerAlarmClock, 0L);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.timer.TimerAlarmClockNotification
 * JD-Core Version:    0.6.2
 */