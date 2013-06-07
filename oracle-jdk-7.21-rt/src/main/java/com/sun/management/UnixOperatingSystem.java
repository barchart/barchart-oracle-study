/*    */ package com.sun.management;
/*    */ 
/*    */ import sun.management.OperatingSystemImpl;
/*    */ import sun.management.VMManagement;
/*    */ 
/*    */ class UnixOperatingSystem extends OperatingSystemImpl
/*    */   implements UnixOperatingSystemMXBean
/*    */ {
/*    */   UnixOperatingSystem(VMManagement paramVMManagement)
/*    */   {
/* 42 */     super(paramVMManagement); } 
/*    */   public native long getCommittedVirtualMemorySize();
/*    */ 
/*    */   public native long getTotalSwapSpaceSize();
/*    */ 
/*    */   public native long getFreeSwapSpaceSize();
/*    */ 
/*    */   public native long getProcessCpuTime();
/*    */ 
/*    */   public native long getFreePhysicalMemorySize();
/*    */ 
/*    */   public native long getTotalPhysicalMemorySize();
/*    */ 
/*    */   public native long getOpenFileDescriptorCount();
/*    */ 
/*    */   public native long getMaxFileDescriptorCount();
/*    */ 
/*    */   public native double getSystemCpuLoad();
/*    */ 
/*    */   public native double getProcessCpuLoad();
/*    */ 
/*    */   private static native void initialize();
/*    */ 
/* 57 */   static { initialize(); }
/*    */ 
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.management.UnixOperatingSystem
 * JD-Core Version:    0.6.2
 */