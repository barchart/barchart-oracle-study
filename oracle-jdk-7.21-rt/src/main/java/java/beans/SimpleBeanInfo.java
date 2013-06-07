/*     */ package java.beans;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.image.ImageProducer;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ public class SimpleBeanInfo
/*     */   implements BeanInfo
/*     */ {
/*     */   public BeanDescriptor getBeanDescriptor()
/*     */   {
/*  46 */     return null;
/*     */   }
/*     */ 
/*     */   public PropertyDescriptor[] getPropertyDescriptors()
/*     */   {
/*  54 */     return null;
/*     */   }
/*     */ 
/*     */   public int getDefaultPropertyIndex()
/*     */   {
/*  62 */     return -1;
/*     */   }
/*     */ 
/*     */   public EventSetDescriptor[] getEventSetDescriptors()
/*     */   {
/*  70 */     return null;
/*     */   }
/*     */ 
/*     */   public int getDefaultEventIndex()
/*     */   {
/*  78 */     return -1;
/*     */   }
/*     */ 
/*     */   public MethodDescriptor[] getMethodDescriptors()
/*     */   {
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   public BeanInfo[] getAdditionalBeanInfo()
/*     */   {
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */   public Image getIcon(int paramInt)
/*     */   {
/* 103 */     return null;
/*     */   }
/*     */ 
/*     */   public Image loadImage(final String paramString)
/*     */   {
/*     */     try
/*     */     {
/* 119 */       final Class localClass = getClass();
/* 120 */       ImageProducer localImageProducer = (ImageProducer)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Object run()
/*     */         {
/*     */           URL localURL;
/* 125 */           if ((localURL = localClass.getResource(paramString)) == null)
/* 126 */             return null;
/*     */           try
/*     */           {
/* 129 */             return localURL.getContent(); } catch (IOException localIOException) {
/*     */           }
/* 131 */           return null;
/*     */         }
/*     */       });
/* 137 */       if (localImageProducer == null)
/* 138 */         return null;
/* 139 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 140 */       return localToolkit.createImage(localImageProducer); } catch (Exception localException) {
/*     */     }
/* 142 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.SimpleBeanInfo
 * JD-Core Version:    0.6.2
 */