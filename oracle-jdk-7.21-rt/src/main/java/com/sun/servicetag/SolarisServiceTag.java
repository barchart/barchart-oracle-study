/*    */ package com.sun.servicetag;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ 
/*    */ class SolarisServiceTag
/*    */ {
/* 35 */   private static final String[] SolarisProductURNs = { "urn:uuid:a7a38948-2bd5-11d6-98ce-9d3ac1c0cfd7", "urn:uuid:4f82caac-36f3-11d6-866b-85f428ef944e", "urn:uuid:a19de03b-48bc-11d9-9607-080020a9ed93", "urn:uuid:4c35c45b-4955-11d9-9607-080020a9ed93", "urn:uuid:5005588c-36f3-11d6-9cec-fc96f718e113", "urn:uuid:6df19e63-7ef5-11db-a4bd-080020a9ed93" };
/*    */ 
/*    */   static ServiceTag getServiceTag()
/*    */     throws IOException
/*    */   {
/* 50 */     if (Registry.isSupported()) {
/* 51 */       Registry localRegistry = Registry.getSystemRegistry();
/* 52 */       for (String str : SolarisProductURNs) {
/* 53 */         Set localSet = localRegistry.findServiceTags(str);
/* 54 */         Iterator localIterator = localSet.iterator(); if (localIterator.hasNext()) { ServiceTag localServiceTag = (ServiceTag)localIterator.next();
/*    */ 
/* 56 */           return localServiceTag;
/*    */         }
/*    */       }
/*    */     }
/* 60 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.SolarisServiceTag
 * JD-Core Version:    0.6.2
 */