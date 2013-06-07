/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import javax.sound.sampled.BooleanControl;
/*     */ import javax.sound.sampled.BooleanControl.Type;
/*     */ import javax.sound.sampled.CompoundControl;
/*     */ import javax.sound.sampled.CompoundControl.Type;
/*     */ import javax.sound.sampled.Control;
/*     */ import javax.sound.sampled.FloatControl;
/*     */ import javax.sound.sampled.FloatControl.Type;
/*     */ import javax.sound.sampled.Line;
/*     */ import javax.sound.sampled.Line.Info;
/*     */ import javax.sound.sampled.LineUnavailableException;
/*     */ import javax.sound.sampled.Port;
/*     */ import javax.sound.sampled.Port.Info;
/*     */ 
/*     */ class PortMixer extends AbstractMixer
/*     */ {
/*     */   private static final int SRC_UNKNOWN = 1;
/*     */   private static final int SRC_MICROPHONE = 2;
/*     */   private static final int SRC_LINE_IN = 3;
/*     */   private static final int SRC_COMPACT_DISC = 4;
/*     */   private static final int SRC_MASK = 255;
/*     */   private static final int DST_UNKNOWN = 256;
/*     */   private static final int DST_SPEAKER = 512;
/*     */   private static final int DST_HEADPHONE = 768;
/*     */   private static final int DST_LINE_OUT = 1024;
/*     */   private static final int DST_MASK = 65280;
/*     */   private Port.Info[] portInfos;
/*     */   private PortMixerPort[] ports;
/*  65 */   private long id = 0L;
/*     */ 
/*     */   PortMixer(PortMixerProvider.PortMixerInfo paramPortMixerInfo)
/*     */   {
/*  70 */     super(paramPortMixerInfo, null, null, null);
/*     */ 
/*  77 */     int i = 0;
/*  78 */     int j = 0;
/*  79 */     int k = 0;
/*     */     try
/*     */     {
/*     */       try {
/*  83 */         this.id = nOpen(getMixerIndex());
/*  84 */         if (this.id != 0L) {
/*  85 */           i = nGetPortCount(this.id);
/*  86 */           if (i < 0)
/*     */           {
/*  88 */             i = 0;
/*     */           }
/*     */         }
/*     */       } catch (Exception localException) {
/*     */       }
/*  93 */       this.portInfos = new Port.Info[i];
/*     */ 
/*  95 */       for (m = 0; m < i; m++) {
/*  96 */         int n = nGetPortType(this.id, m);
/*  97 */         j += ((n & 0xFF) != 0 ? 1 : 0);
/*  98 */         k += ((n & 0xFF00) != 0 ? 1 : 0);
/*  99 */         this.portInfos[m] = getPortInfo(m, n);
/*     */       }
/*     */     } finally {
/* 102 */       if (this.id != 0L) {
/* 103 */         nClose(this.id);
/*     */       }
/* 105 */       this.id = 0L;
/*     */     }
/*     */ 
/* 109 */     this.sourceLineInfo = new Port.Info[j];
/* 110 */     this.targetLineInfo = new Port.Info[k];
/*     */ 
/* 112 */     j = 0; k = 0;
/* 113 */     for (int m = 0; m < i; m++)
/* 114 */       if (this.portInfos[m].isSource())
/* 115 */         this.sourceLineInfo[(j++)] = this.portInfos[m];
/*     */       else
/* 117 */         this.targetLineInfo[(k++)] = this.portInfos[m];
/*     */   }
/*     */ 
/*     */   public Line getLine(Line.Info paramInfo)
/*     */     throws LineUnavailableException
/*     */   {
/* 128 */     Line.Info localInfo = getLineInfo(paramInfo);
/*     */ 
/* 130 */     if ((localInfo != null) && ((localInfo instanceof Port.Info))) {
/* 131 */       for (int i = 0; i < this.portInfos.length; i++) {
/* 132 */         if (localInfo.equals(this.portInfos[i])) {
/* 133 */           return getPort(i);
/*     */         }
/*     */       }
/*     */     }
/* 137 */     throw new IllegalArgumentException("Line unsupported: " + paramInfo);
/*     */   }
/*     */ 
/*     */   public int getMaxLines(Line.Info paramInfo)
/*     */   {
/* 142 */     Line.Info localInfo = getLineInfo(paramInfo);
/*     */ 
/* 145 */     if (localInfo == null) {
/* 146 */       return 0;
/*     */     }
/*     */ 
/* 149 */     if ((localInfo instanceof Port.Info))
/*     */     {
/* 151 */       return 1;
/*     */     }
/* 153 */     return 0;
/*     */   }
/*     */ 
/*     */   protected void implOpen()
/*     */     throws LineUnavailableException
/*     */   {
/* 161 */     this.id = nOpen(getMixerIndex());
/*     */   }
/*     */ 
/*     */   protected void implClose()
/*     */   {
/* 170 */     long l = this.id;
/* 171 */     this.id = 0L;
/* 172 */     nClose(l);
/* 173 */     if (this.ports != null)
/* 174 */       for (int i = 0; i < this.ports.length; i++)
/* 175 */         if (this.ports[i] != null)
/* 176 */           this.ports[i].disposeControls();
/*     */   }
/*     */ 
/*     */   protected void implStart()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void implStop()
/*     */   {
/*     */   }
/*     */ 
/*     */   private Port.Info getPortInfo(int paramInt1, int paramInt2)
/*     */   {
/* 190 */     switch (paramInt2) { case 1:
/* 191 */       return new PortInfo(nGetPortName(getID(), paramInt1), true, null);
/*     */     case 2:
/* 192 */       return Port.Info.MICROPHONE;
/*     */     case 3:
/* 193 */       return Port.Info.LINE_IN;
/*     */     case 4:
/* 194 */       return Port.Info.COMPACT_DISC;
/*     */     case 256:
/* 196 */       return new PortInfo(nGetPortName(getID(), paramInt1), false, null);
/*     */     case 512:
/* 197 */       return Port.Info.SPEAKER;
/*     */     case 768:
/* 198 */       return Port.Info.HEADPHONE;
/*     */     case 1024:
/* 199 */       return Port.Info.LINE_OUT;
/*     */     }
/*     */ 
/* 203 */     return null;
/*     */   }
/*     */ 
/*     */   int getMixerIndex() {
/* 207 */     return ((PortMixerProvider.PortMixerInfo)getMixerInfo()).getIndex();
/*     */   }
/*     */ 
/*     */   Port getPort(int paramInt) {
/* 211 */     if (this.ports == null) {
/* 212 */       this.ports = new PortMixerPort[this.portInfos.length];
/*     */     }
/* 214 */     if (this.ports[paramInt] == null) {
/* 215 */       this.ports[paramInt] = new PortMixerPort(this.portInfos[paramInt], this, paramInt, null);
/* 216 */       return this.ports[paramInt];
/*     */     }
/*     */ 
/* 219 */     return this.ports[paramInt];
/*     */   }
/*     */ 
/*     */   long getID() {
/* 223 */     return this.id;
/*     */   }
/*     */ 
/*     */   private static native long nOpen(int paramInt)
/*     */     throws LineUnavailableException;
/*     */ 
/*     */   private static native void nClose(long paramLong);
/*     */ 
/*     */   private static native int nGetPortCount(long paramLong);
/*     */ 
/*     */   private static native int nGetPortType(long paramLong, int paramInt);
/*     */ 
/*     */   private static native String nGetPortName(long paramLong, int paramInt);
/*     */ 
/*     */   private static native void nGetControls(long paramLong, int paramInt, Vector paramVector);
/*     */ 
/*     */   private static native void nControlSetIntValue(long paramLong, int paramInt);
/*     */ 
/*     */   private static native int nControlGetIntValue(long paramLong);
/*     */ 
/*     */   private static native void nControlSetFloatValue(long paramLong, float paramFloat);
/*     */ 
/*     */   private static native float nControlGetFloatValue(long paramLong);
/*     */ 
/*     */   private static class BoolCtrl extends BooleanControl
/*     */   {
/*     */     private long controlID;
/* 348 */     private boolean closed = false;
/*     */ 
/*     */     private static BooleanControl.Type createType(String paramString) {
/* 351 */       if (paramString.equals("Mute")) {
/* 352 */         return BooleanControl.Type.MUTE;
/*     */       }
/* 354 */       if (paramString.equals("Select"));
/* 358 */       return new BCT(paramString, null);
/*     */     }
/*     */ 
/*     */     private BoolCtrl(long paramLong, String paramString)
/*     */     {
/* 363 */       this(paramLong, createType(paramString));
/*     */     }
/*     */ 
/*     */     private BoolCtrl(long paramLong, BooleanControl.Type paramType) {
/* 367 */       super(false);
/* 368 */       this.controlID = paramLong;
/*     */     }
/*     */ 
/*     */     public void setValue(boolean paramBoolean) {
/* 372 */       if (!this.closed)
/* 373 */         PortMixer.nControlSetIntValue(this.controlID, paramBoolean ? 1 : 0);
/*     */     }
/*     */ 
/*     */     public boolean getValue()
/*     */     {
/* 378 */       if (!this.closed)
/*     */       {
/* 380 */         return PortMixer.nControlGetIntValue(this.controlID) != 0;
/*     */       }
/*     */ 
/* 383 */       return false;
/*     */     }
/*     */ 
/*     */     private static class BCT extends BooleanControl.Type
/*     */     {
/*     */       private BCT(String paramString)
/*     */       {
/* 391 */         super();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class CompCtrl extends CompoundControl
/*     */   {
/*     */     private CompCtrl(String paramString, Control[] paramArrayOfControl)
/*     */     {
/* 401 */       super(paramArrayOfControl);
/*     */     }
/*     */ 
/*     */     private static class CCT extends CompoundControl.Type
/*     */     {
/*     */       private CCT(String paramString)
/*     */       {
/* 409 */         super();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class FloatCtrl extends FloatControl
/*     */   {
/*     */     private long controlID;
/* 420 */     private boolean closed = false;
/*     */ 
/* 423 */     private static final FloatControl.Type[] FLOAT_CONTROL_TYPES = { null, FloatControl.Type.BALANCE, FloatControl.Type.MASTER_GAIN, FloatControl.Type.PAN, FloatControl.Type.VOLUME };
/*     */ 
/*     */     private FloatCtrl(long paramLong, String paramString1, float paramFloat1, float paramFloat2, float paramFloat3, String paramString2)
/*     */     {
/* 433 */       this(paramLong, new FCT(paramString1, null), paramFloat1, paramFloat2, paramFloat3, paramString2);
/*     */     }
/*     */ 
/*     */     private FloatCtrl(long paramLong, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, String paramString)
/*     */     {
/* 438 */       this(paramLong, FLOAT_CONTROL_TYPES[paramInt], paramFloat1, paramFloat2, paramFloat3, paramString);
/*     */     }
/*     */ 
/*     */     private FloatCtrl(long paramLong, FloatControl.Type paramType, float paramFloat1, float paramFloat2, float paramFloat3, String paramString)
/*     */     {
/* 443 */       super(paramFloat1, paramFloat2, paramFloat3, 1000, paramFloat1, paramString);
/* 444 */       this.controlID = paramLong;
/*     */     }
/*     */ 
/*     */     public void setValue(float paramFloat) {
/* 448 */       if (!this.closed)
/* 449 */         PortMixer.nControlSetFloatValue(this.controlID, paramFloat);
/*     */     }
/*     */ 
/*     */     public float getValue()
/*     */     {
/* 454 */       if (!this.closed)
/*     */       {
/* 456 */         return PortMixer.nControlGetFloatValue(this.controlID);
/*     */       }
/*     */ 
/* 459 */       return getMinimum();
/*     */     }
/*     */ 
/*     */     private static class FCT extends FloatControl.Type
/*     */     {
/*     */       private FCT(String paramString)
/*     */       {
/* 467 */         super();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class PortInfo extends Port.Info
/*     */   {
/*     */     private PortInfo(String paramString, boolean paramBoolean)
/*     */     {
/* 477 */       super(paramString, paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class PortMixerPort extends AbstractLine
/*     */     implements Port
/*     */   {
/*     */     private int portIndex;
/*     */     private long id;
/*     */ 
/*     */     private PortMixerPort(Port.Info paramInfo, PortMixer paramPortMixer, int paramInt)
/*     */     {
/* 239 */       super(paramPortMixer, null);
/*     */ 
/* 241 */       this.portIndex = paramInt;
/*     */     }
/*     */ 
/*     */     void implOpen()
/*     */       throws LineUnavailableException
/*     */     {
/* 251 */       long l = ((PortMixer)this.mixer).getID();
/* 252 */       if ((this.id == 0L) || (l != this.id) || (this.controls.length == 0)) {
/* 253 */         this.id = l;
/* 254 */         Vector localVector = new Vector();
/* 255 */         synchronized (localVector) {
/* 256 */           PortMixer.nGetControls(this.id, this.portIndex, localVector);
/* 257 */           this.controls = new Control[localVector.size()];
/* 258 */           for (int i = 0; i < this.controls.length; i++)
/* 259 */             this.controls[i] = ((Control)localVector.elementAt(i));
/*     */         }
/*     */       }
/*     */       else {
/* 263 */         enableControls(this.controls, true);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void enableControls(Control[] paramArrayOfControl, boolean paramBoolean)
/*     */     {
/* 269 */       for (int i = 0; i < paramArrayOfControl.length; i++)
/* 270 */         if ((paramArrayOfControl[i] instanceof PortMixer.BoolCtrl)) {
/* 271 */           ((PortMixer.BoolCtrl)paramArrayOfControl[i]).closed = (!paramBoolean);
/*     */         }
/* 273 */         else if ((paramArrayOfControl[i] instanceof PortMixer.FloatCtrl)) {
/* 274 */           ((PortMixer.FloatCtrl)paramArrayOfControl[i]).closed = (!paramBoolean);
/*     */         }
/* 276 */         else if ((paramArrayOfControl[i] instanceof CompoundControl))
/* 277 */           enableControls(((CompoundControl)paramArrayOfControl[i]).getMemberControls(), paramBoolean);
/*     */     }
/*     */ 
/*     */     private void disposeControls()
/*     */     {
/* 283 */       enableControls(this.controls, false);
/* 284 */       this.controls = new Control[0];
/*     */     }
/*     */ 
/*     */     void implClose()
/*     */     {
/* 291 */       enableControls(this.controls, false);
/*     */     }
/*     */ 
/*     */     public void open()
/*     */       throws LineUnavailableException
/*     */     {
/* 299 */       synchronized (this.mixer)
/*     */       {
/* 301 */         if (!isOpen())
/*     */         {
/* 304 */           this.mixer.open(this);
/*     */           try
/*     */           {
/* 307 */             implOpen();
/*     */ 
/* 310 */             setOpen(true);
/*     */           }
/*     */           catch (LineUnavailableException localLineUnavailableException) {
/* 313 */             this.mixer.close(this);
/* 314 */             throw localLineUnavailableException;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void close()
/*     */     {
/* 323 */       synchronized (this.mixer) {
/* 324 */         if (isOpen())
/*     */         {
/* 328 */           setOpen(false);
/*     */ 
/* 331 */           implClose();
/*     */ 
/* 334 */           this.mixer.close(this);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.PortMixer
 * JD-Core Version:    0.6.2
 */