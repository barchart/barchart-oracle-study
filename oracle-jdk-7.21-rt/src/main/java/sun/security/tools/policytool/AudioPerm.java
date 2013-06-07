/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class AudioPerm extends Perm
/*      */ {
/*      */   public AudioPerm()
/*      */   {
/* 3741 */     super("AudioPermission", "javax.sound.sampled.AudioPermission", new String[] { "play", "record" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.AudioPerm
 * JD-Core Version:    0.6.2
 */