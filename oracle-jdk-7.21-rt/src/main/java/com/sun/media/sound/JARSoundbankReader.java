/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.Soundbank;
/*     */ import javax.sound.midi.spi.SoundbankReader;
/*     */ 
/*     */ public class JARSoundbankReader extends SoundbankReader
/*     */ {
/*     */   public boolean isZIP(URL paramURL)
/*     */   {
/*  47 */     boolean bool = false;
/*     */     try {
/*  49 */       InputStream localInputStream = paramURL.openStream();
/*     */       try {
/*  51 */         byte[] arrayOfByte = new byte[4];
/*  52 */         bool = localInputStream.read(arrayOfByte) == 4;
/*  53 */         if (bool) {
/*  54 */           bool = (arrayOfByte[0] == 80) && (arrayOfByte[1] == 75) && (arrayOfByte[2] == 3) && (arrayOfByte[3] == 4);
/*     */         }
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*  60 */         localInputStream.close();
/*     */       }
/*     */     } catch (IOException localIOException) {
/*     */     }
/*  64 */     return bool;
/*     */   }
/*     */ 
/*     */   public Soundbank getSoundbank(URL paramURL) throws InvalidMidiDataException, IOException
/*     */   {
/*  69 */     if (!isZIP(paramURL))
/*  70 */       return null;
/*  71 */     ArrayList localArrayList = new ArrayList();
/*  72 */     URLClassLoader localURLClassLoader = URLClassLoader.newInstance(new URL[] { paramURL });
/*  73 */     InputStream localInputStream = localURLClassLoader.getResourceAsStream("META-INF/services/javax.sound.midi.Soundbank");
/*     */ 
/*  75 */     if (localInputStream == null)
/*  76 */       return null;
/*     */     try
/*     */     {
/*  79 */       localObject1 = new BufferedReader(new InputStreamReader(localInputStream));
/*  80 */       localObject2 = ((BufferedReader)localObject1).readLine();
/*  81 */       while (localObject2 != null) {
/*  82 */         if (!((String)localObject2).startsWith("#"))
/*     */           try {
/*  84 */             Class localClass = Class.forName(((String)localObject2).trim(), true, localURLClassLoader);
/*  85 */             Object localObject3 = localClass.newInstance();
/*  86 */             if ((localObject3 instanceof Soundbank))
/*  87 */               localArrayList.add((Soundbank)localObject3);
/*     */           } catch (ClassNotFoundException localClassNotFoundException) {
/*     */           }
/*     */           catch (InstantiationException localInstantiationException) {
/*     */           }
/*     */           catch (IllegalAccessException localIllegalAccessException) {
/*     */           }
/*  94 */         localObject2 = ((BufferedReader)localObject1).readLine();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*  99 */       localInputStream.close();
/*     */     }
/* 101 */     if (localArrayList.size() == 0)
/* 102 */       return null;
/* 103 */     if (localArrayList.size() == 1)
/* 104 */       return (Soundbank)localArrayList.get(0);
/* 105 */     Object localObject1 = new SimpleSoundbank();
/* 106 */     for (Object localObject2 = localArrayList.iterator(); ((Iterator)localObject2).hasNext(); ) { Soundbank localSoundbank = (Soundbank)((Iterator)localObject2).next();
/* 107 */       ((SimpleSoundbank)localObject1).addAllInstruments(localSoundbank); }
/* 108 */     return localObject1;
/*     */   }
/*     */ 
/*     */   public Soundbank getSoundbank(InputStream paramInputStream) throws InvalidMidiDataException, IOException
/*     */   {
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   public Soundbank getSoundbank(File paramFile) throws InvalidMidiDataException, IOException
/*     */   {
/* 118 */     return getSoundbank(paramFile.toURI().toURL());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.JARSoundbankReader
 * JD-Core Version:    0.6.2
 */