/*    */ package com.sun.corba.se.spi.ior;
/*    */ 
/*    */ import com.sun.corba.se.impl.encoding.EncapsOutputStream;
/*    */ import org.omg.CORBA_2_3.portable.InputStream;
/*    */ import org.omg.IOP.TaggedComponentHelper;
/*    */ 
/*    */ public abstract class TaggedComponentBase extends IdentifiableBase
/*    */   implements TaggedComponent
/*    */ {
/*    */   public org.omg.IOP.TaggedComponent getIOPComponent(org.omg.CORBA.ORB paramORB)
/*    */   {
/* 45 */     EncapsOutputStream localEncapsOutputStream = new EncapsOutputStream((com.sun.corba.se.spi.orb.ORB)paramORB);
/* 46 */     write(localEncapsOutputStream);
/* 47 */     InputStream localInputStream = (InputStream)localEncapsOutputStream.create_input_stream();
/* 48 */     return TaggedComponentHelper.read(localInputStream);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.TaggedComponentBase
 * JD-Core Version:    0.6.2
 */