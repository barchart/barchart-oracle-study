/*     */ package sun.audio;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ public class AudioPlayer extends Thread
/*     */ {
/*     */   private AudioDevice devAudio;
/*  76 */   private static boolean DEBUG = false;
/*     */ 
/*  82 */   public static final AudioPlayer player = getAudioPlayer();
/*     */ 
/*     */   private static ThreadGroup getAudioThreadGroup()
/*     */   {
/*  86 */     if (DEBUG) System.out.println("AudioPlayer.getAudioThreadGroup()");
/*  87 */     ThreadGroup localThreadGroup = currentThread().getThreadGroup();
/*  88 */     while ((localThreadGroup.getParent() != null) && (localThreadGroup.getParent().getParent() != null))
/*     */     {
/*  90 */       localThreadGroup = localThreadGroup.getParent();
/*     */     }
/*  92 */     return localThreadGroup;
/*     */   }
/*     */ 
/*     */   private static AudioPlayer getAudioPlayer()
/*     */   {
/* 101 */     if (DEBUG) System.out.println("> AudioPlayer.getAudioPlayer()");
/*     */ 
/* 103 */     PrivilegedAction local1 = new PrivilegedAction() {
/*     */       public Object run() {
/* 105 */         AudioPlayer localAudioPlayer = new AudioPlayer(null);
/* 106 */         localAudioPlayer.setPriority(10);
/* 107 */         localAudioPlayer.setDaemon(true);
/* 108 */         localAudioPlayer.start();
/* 109 */         return localAudioPlayer;
/*     */       }
/*     */     };
/* 112 */     AudioPlayer localAudioPlayer = (AudioPlayer)AccessController.doPrivileged(local1);
/* 113 */     return localAudioPlayer;
/*     */   }
/*     */ 
/*     */   private AudioPlayer()
/*     */   {
/* 121 */     super(getAudioThreadGroup(), "Audio Player");
/* 122 */     if (DEBUG) System.out.println("> AudioPlayer private constructor");
/* 123 */     this.devAudio = AudioDevice.device;
/* 124 */     this.devAudio.open();
/* 125 */     if (DEBUG) System.out.println("< AudioPlayer private constructor completed");
/*     */   }
/*     */ 
/*     */   public synchronized void start(InputStream paramInputStream)
/*     */   {
/* 136 */     if (DEBUG) {
/* 137 */       System.out.println("> AudioPlayer.start");
/* 138 */       System.out.println("  InputStream = " + paramInputStream);
/*     */     }
/* 140 */     this.devAudio.openChannel(paramInputStream);
/* 141 */     notify();
/* 142 */     if (DEBUG)
/* 143 */       System.out.println("< AudioPlayer.start completed");
/*     */   }
/*     */ 
/*     */   public synchronized void stop(InputStream paramInputStream)
/*     */   {
/* 155 */     if (DEBUG) {
/* 156 */       System.out.println("> AudioPlayer.stop");
/*     */     }
/*     */ 
/* 159 */     this.devAudio.closeChannel(paramInputStream);
/* 160 */     if (DEBUG)
/* 161 */       System.out.println("< AudioPlayer.stop completed");
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 177 */     this.devAudio.play();
/* 178 */     if (DEBUG)
/* 179 */       System.out.println("AudioPlayer mixing loop.");
/*     */     try
/*     */     {
/*     */       while (true) {
/* 183 */         Thread.sleep(5000L);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 190 */       if (DEBUG)
/* 191 */         System.out.println("AudioPlayer exited.");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.audio.AudioPlayer
 * JD-Core Version:    0.6.2
 */