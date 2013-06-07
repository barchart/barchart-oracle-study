/*    */ package java.util.prefs;
/*    */ 
/*    */ class FileSystemPreferencesFactory
/*    */   implements PreferencesFactory
/*    */ {
/*    */   public Preferences userRoot()
/*    */   {
/* 41 */     return FileSystemPreferences.getUserRoot();
/*    */   }
/*    */ 
/*    */   public Preferences systemRoot() {
/* 45 */     return FileSystemPreferences.getSystemRoot();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.prefs.FileSystemPreferencesFactory
 * JD-Core Version:    0.6.2
 */