/*      */ package sun.security.jgss.spnego;
/*      */ 
/*      */ import com.sun.security.jgss.ExtendedGSSContext;
/*      */ import com.sun.security.jgss.InquireType;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.security.AccessController;
/*      */ import java.security.Provider;
/*      */ import org.ietf.jgss.ChannelBinding;
/*      */ import org.ietf.jgss.GSSContext;
/*      */ import org.ietf.jgss.GSSCredential;
/*      */ import org.ietf.jgss.GSSException;
/*      */ import org.ietf.jgss.GSSName;
/*      */ import org.ietf.jgss.MessageProp;
/*      */ import org.ietf.jgss.Oid;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.jgss.GSSCredentialImpl;
/*      */ import sun.security.jgss.GSSManagerImpl;
/*      */ import sun.security.jgss.GSSNameImpl;
/*      */ import sun.security.jgss.GSSUtil;
/*      */ import sun.security.jgss.spi.GSSContextSpi;
/*      */ import sun.security.jgss.spi.GSSCredentialSpi;
/*      */ import sun.security.jgss.spi.GSSNameSpi;
/*      */ import sun.security.util.BitArray;
/*      */ import sun.security.util.DerOutputStream;
/*      */ 
/*      */ public class SpNegoContext
/*      */   implements GSSContextSpi
/*      */ {
/*      */   private static final int STATE_NEW = 1;
/*      */   private static final int STATE_IN_PROCESS = 2;
/*      */   private static final int STATE_DONE = 3;
/*      */   private static final int STATE_DELETED = 4;
/*   54 */   private int state = 1;
/*      */ 
/*   60 */   private boolean credDelegState = false;
/*   61 */   private boolean mutualAuthState = true;
/*   62 */   private boolean replayDetState = true;
/*   63 */   private boolean sequenceDetState = true;
/*   64 */   private boolean confState = true;
/*   65 */   private boolean integState = true;
/*   66 */   private boolean delegPolicyState = false;
/*      */ 
/*   68 */   private GSSNameSpi peerName = null;
/*   69 */   private GSSNameSpi myName = null;
/*   70 */   private SpNegoCredElement myCred = null;
/*      */ 
/*   72 */   private GSSContext mechContext = null;
/*   73 */   private byte[] DER_mechTypes = null;
/*      */   private int lifetime;
/*      */   private ChannelBinding channelBinding;
/*      */   private boolean initiator;
/*   80 */   private Oid internal_mech = null;
/*      */   private final SpNegoMechFactory factory;
/*   86 */   static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.spnego.debug"))).booleanValue();
/*      */ 
/*      */   public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt)
/*      */     throws GSSException
/*      */   {
/*   99 */     if (paramGSSNameSpi == null)
/*  100 */       throw new IllegalArgumentException("Cannot have null peer name");
/*  101 */     if ((paramGSSCredentialSpi != null) && (!(paramGSSCredentialSpi instanceof SpNegoCredElement))) {
/*  102 */       throw new IllegalArgumentException("Wrong cred element type");
/*      */     }
/*  104 */     this.peerName = paramGSSNameSpi;
/*  105 */     this.myCred = ((SpNegoCredElement)paramGSSCredentialSpi);
/*  106 */     this.lifetime = paramInt;
/*  107 */     this.initiator = true;
/*  108 */     this.factory = paramSpNegoMechFactory;
/*      */   }
/*      */ 
/*      */   public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, GSSCredentialSpi paramGSSCredentialSpi)
/*      */     throws GSSException
/*      */   {
/*  117 */     if ((paramGSSCredentialSpi != null) && (!(paramGSSCredentialSpi instanceof SpNegoCredElement))) {
/*  118 */       throw new IllegalArgumentException("Wrong cred element type");
/*      */     }
/*  120 */     this.myCred = ((SpNegoCredElement)paramGSSCredentialSpi);
/*  121 */     this.initiator = false;
/*  122 */     this.factory = paramSpNegoMechFactory;
/*      */   }
/*      */ 
/*      */   public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, byte[] paramArrayOfByte)
/*      */     throws GSSException
/*      */   {
/*  130 */     throw new GSSException(16, -1, "GSS Import Context not available");
/*      */   }
/*      */ 
/*      */   public final void requestConf(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  138 */     if ((this.state == 1) && (isInitiator()))
/*  139 */       this.confState = paramBoolean;
/*      */   }
/*      */ 
/*      */   public final boolean getConfState()
/*      */   {
/*  146 */     return this.confState;
/*      */   }
/*      */ 
/*      */   public final void requestInteg(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  153 */     if ((this.state == 1) && (isInitiator()))
/*  154 */       this.integState = paramBoolean;
/*      */   }
/*      */ 
/*      */   public final void requestDelegPolicy(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  161 */     if ((this.state == 1) && (isInitiator()))
/*  162 */       this.delegPolicyState = paramBoolean;
/*      */   }
/*      */ 
/*      */   public final boolean getIntegState()
/*      */   {
/*  169 */     return this.integState;
/*      */   }
/*      */ 
/*      */   public final boolean getDelegPolicyState()
/*      */   {
/*  176 */     if ((isInitiator()) && (this.mechContext != null) && ((this.mechContext instanceof ExtendedGSSContext)) && ((this.state == 2) || (this.state == 3)))
/*      */     {
/*  179 */       return ((ExtendedGSSContext)this.mechContext).getDelegPolicyState();
/*      */     }
/*  181 */     return this.delegPolicyState;
/*      */   }
/*      */ 
/*      */   public final void requestCredDeleg(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  190 */     if ((this.state == 1) && (isInitiator()))
/*  191 */       this.credDelegState = paramBoolean;
/*      */   }
/*      */ 
/*      */   public final boolean getCredDelegState()
/*      */   {
/*  198 */     if ((isInitiator()) && (this.mechContext != null) && ((this.state == 2) || (this.state == 3)))
/*      */     {
/*  200 */       return this.mechContext.getCredDelegState();
/*      */     }
/*  202 */     return this.credDelegState;
/*      */   }
/*      */ 
/*      */   public final void requestMutualAuth(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  212 */     if ((this.state == 1) && (isInitiator()))
/*  213 */       this.mutualAuthState = paramBoolean;
/*      */   }
/*      */ 
/*      */   public final boolean getMutualAuthState()
/*      */   {
/*  223 */     return this.mutualAuthState;
/*      */   }
/*      */ 
/*      */   public final Oid getMech()
/*      */   {
/*  232 */     if (isEstablished()) {
/*  233 */       return getNegotiatedMech();
/*      */     }
/*  235 */     return SpNegoMechFactory.GSS_SPNEGO_MECH_OID;
/*      */   }
/*      */ 
/*      */   public final Oid getNegotiatedMech() {
/*  239 */     return this.internal_mech;
/*      */   }
/*      */ 
/*      */   public final Provider getProvider() {
/*  243 */     return SpNegoMechFactory.PROVIDER;
/*      */   }
/*      */ 
/*      */   public final void dispose() throws GSSException {
/*  247 */     this.mechContext = null;
/*  248 */     this.state = 4;
/*      */   }
/*      */ 
/*      */   public final boolean isInitiator()
/*      */   {
/*  258 */     return this.initiator;
/*      */   }
/*      */ 
/*      */   public final boolean isProtReady()
/*      */   {
/*  270 */     return this.state == 3;
/*      */   }
/*      */ 
/*      */   public final byte[] initSecContext(InputStream paramInputStream, int paramInt)
/*      */     throws GSSException
/*      */   {
/*  289 */     Object localObject1 = null;
/*  290 */     NegTokenInit localNegTokenInit = null;
/*  291 */     byte[] arrayOfByte1 = null;
/*  292 */     int i = 11;
/*      */ 
/*  294 */     if (DEBUG) {
/*  295 */       System.out.println("Entered SpNego.initSecContext with state=" + printState(this.state));
/*      */     }
/*      */ 
/*  298 */     if (!isInitiator())
/*  299 */       throw new GSSException(11, -1, "initSecContext on an acceptor GSSContext");
/*      */     try
/*      */     {
/*      */       Object localObject2;
/*  304 */       if (this.state == 1) {
/*  305 */         this.state = 2;
/*      */ 
/*  307 */         i = 13;
/*      */ 
/*  310 */         localObject2 = getAvailableMechs();
/*  311 */         this.DER_mechTypes = getEncodedMechs((Oid[])localObject2);
/*      */ 
/*  314 */         this.internal_mech = localObject2[0];
/*      */ 
/*  317 */         arrayOfByte1 = GSS_initSecContext(null);
/*      */ 
/*  319 */         i = 10;
/*      */ 
/*  321 */         localNegTokenInit = new NegTokenInit(this.DER_mechTypes, getContextFlags(), arrayOfByte1, null);
/*      */ 
/*  323 */         if (DEBUG) {
/*  324 */           System.out.println("SpNegoContext.initSecContext: sending token of type = " + SpNegoToken.getTokenName(localNegTokenInit.getType()));
/*      */         }
/*      */ 
/*  329 */         localObject1 = localNegTokenInit.getEncoded();
/*      */       }
/*  331 */       else if (this.state == 2)
/*      */       {
/*  333 */         i = 11;
/*  334 */         if (paramInputStream == null) {
/*  335 */           throw new GSSException(i, -1, "No token received from peer!");
/*      */         }
/*      */ 
/*  339 */         i = 10;
/*  340 */         localObject2 = new byte[paramInputStream.available()];
/*  341 */         SpNegoToken.readFully(paramInputStream, (byte[])localObject2);
/*  342 */         if (DEBUG) {
/*  343 */           System.out.println("SpNegoContext.initSecContext: process received token = " + SpNegoToken.getHexBytes((byte[])localObject2));
/*      */         }
/*      */ 
/*  350 */         localObject3 = new NegTokenTarg((byte[])localObject2);
/*      */ 
/*  352 */         if (DEBUG) {
/*  353 */           System.out.println("SpNegoContext.initSecContext: received token of type = " + SpNegoToken.getTokenName(((NegTokenTarg)localObject3).getType()));
/*      */         }
/*      */ 
/*  359 */         this.internal_mech = ((NegTokenTarg)localObject3).getSupportedMech();
/*  360 */         if (this.internal_mech == null)
/*      */         {
/*  362 */           throw new GSSException(i, -1, "supported mechansim from server is null");
/*      */         }
/*      */ 
/*  367 */         SpNegoToken.NegoResult localNegoResult = null;
/*  368 */         int j = ((NegTokenTarg)localObject3).getNegotiatedResult();
/*  369 */         switch (j) {
/*      */         case 0:
/*  371 */           localNegoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
/*  372 */           this.state = 3;
/*  373 */           break;
/*      */         case 1:
/*  375 */           localNegoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
/*  376 */           this.state = 2;
/*  377 */           break;
/*      */         case 2:
/*  379 */           localNegoResult = SpNegoToken.NegoResult.REJECT;
/*  380 */           this.state = 4;
/*  381 */           break;
/*      */         default:
/*  383 */           this.state = 3;
/*      */         }
/*      */ 
/*  387 */         i = 2;
/*      */ 
/*  389 */         if (localNegoResult == SpNegoToken.NegoResult.REJECT) {
/*  390 */           throw new GSSException(i, -1, this.internal_mech.toString());
/*      */         }
/*      */ 
/*  394 */         i = 10;
/*      */ 
/*  396 */         if ((localNegoResult == SpNegoToken.NegoResult.ACCEPT_COMPLETE) || (localNegoResult == SpNegoToken.NegoResult.ACCEPT_INCOMPLETE))
/*      */         {
/*  400 */           byte[] arrayOfByte2 = ((NegTokenTarg)localObject3).getResponseToken();
/*  401 */           if (arrayOfByte2 == null) {
/*  402 */             if (!isMechContextEstablished())
/*      */             {
/*  404 */               throw new GSSException(i, -1, "mechanism token from server is null");
/*      */             }
/*      */           }
/*      */           else {
/*  408 */             arrayOfByte1 = GSS_initSecContext(arrayOfByte2);
/*      */           }
/*      */ 
/*  411 */           if (!GSSUtil.useMSInterop()) {
/*  412 */             byte[] arrayOfByte3 = ((NegTokenTarg)localObject3).getMechListMIC();
/*  413 */             if (!verifyMechListMIC(this.DER_mechTypes, arrayOfByte3)) {
/*  414 */               throw new GSSException(i, -1, "verification of MIC on MechList Failed!");
/*      */             }
/*      */           }
/*      */ 
/*  418 */           if (isMechContextEstablished()) {
/*  419 */             this.state = 3;
/*  420 */             localObject1 = arrayOfByte1;
/*  421 */             if (DEBUG) {
/*  422 */               System.out.println("SPNEGO Negotiated Mechanism = " + this.internal_mech + " " + GSSUtil.getMechStr(this.internal_mech));
/*      */             }
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  428 */             localNegTokenInit = new NegTokenInit(null, null, arrayOfByte1, null);
/*      */ 
/*  430 */             if (DEBUG) {
/*  431 */               System.out.println("SpNegoContext.initSecContext: continue sending token of type = " + SpNegoToken.getTokenName(localNegTokenInit.getType()));
/*      */             }
/*      */ 
/*  436 */             localObject1 = localNegTokenInit.getEncoded();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*  442 */       else if (DEBUG) {
/*  443 */         System.out.println(this.state);
/*      */       }
/*      */ 
/*  446 */       if ((DEBUG) && 
/*  447 */         (localObject1 != null)) {
/*  448 */         System.out.println("SNegoContext.initSecContext: sending token = " + SpNegoToken.getHexBytes((byte[])localObject1));
/*      */       }
/*      */     }
/*      */     catch (GSSException localGSSException)
/*      */     {
/*  453 */       localObject3 = new GSSException(i, -1, localGSSException.getMessage());
/*      */ 
/*  455 */       ((GSSException)localObject3).initCause(localGSSException);
/*  456 */       throw ((Throwable)localObject3);
/*      */     } catch (IOException localIOException) {
/*  458 */       Object localObject3 = new GSSException(11, -1, localIOException.getMessage());
/*      */ 
/*  460 */       ((GSSException)localObject3).initCause(localIOException);
/*  461 */       throw ((Throwable)localObject3);
/*      */     }
/*      */ 
/*  464 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public final byte[] acceptSecContext(InputStream paramInputStream, int paramInt)
/*      */     throws GSSException
/*      */   {
/*  483 */     byte[] arrayOfByte1 = null;
/*      */ 
/*  485 */     boolean bool = true;
/*      */ 
/*  487 */     if (DEBUG) {
/*  488 */       System.out.println("Entered SpNegoContext.acceptSecContext with state=" + printState(this.state));
/*      */     }
/*      */ 
/*  492 */     if (isInitiator())
/*  493 */       throw new GSSException(11, -1, "acceptSecContext on an initiator GSSContext");
/*      */     try
/*      */     {
/*      */       byte[] arrayOfByte2;
/*      */       Object localObject2;
/*      */       SpNegoToken.NegoResult localNegoResult;
/*  498 */       if (this.state == 1) {
/*  499 */         this.state = 2;
/*      */ 
/*  502 */         arrayOfByte2 = new byte[paramInputStream.available()];
/*  503 */         SpNegoToken.readFully(paramInputStream, arrayOfByte2);
/*  504 */         if (DEBUG) {
/*  505 */           System.out.println("SpNegoContext.acceptSecContext: receiving token = " + SpNegoToken.getHexBytes(arrayOfByte2));
/*      */         }
/*      */ 
/*  512 */         localObject1 = new NegTokenInit(arrayOfByte2);
/*      */ 
/*  514 */         if (DEBUG) {
/*  515 */           System.out.println("SpNegoContext.acceptSecContext: received token of type = " + SpNegoToken.getTokenName(((NegTokenInit)localObject1).getType()));
/*      */         }
/*      */ 
/*  520 */         localObject2 = ((NegTokenInit)localObject1).getMechTypeList();
/*  521 */         this.DER_mechTypes = ((NegTokenInit)localObject1).getMechTypes();
/*  522 */         if (this.DER_mechTypes == null) {
/*  523 */           bool = false;
/*      */         }
/*      */ 
/*  527 */         byte[] arrayOfByte3 = ((NegTokenInit)localObject1).getMechToken();
/*      */ 
/*  534 */         Oid[] arrayOfOid = getAvailableMechs();
/*  535 */         Oid localOid = negotiate_mech_type(arrayOfOid, (Oid[])localObject2);
/*      */ 
/*  537 */         if (localOid == null) {
/*  538 */           bool = false;
/*      */         }
/*      */ 
/*  541 */         this.internal_mech = localOid;
/*      */ 
/*  544 */         byte[] arrayOfByte4 = GSS_acceptSecContext(arrayOfByte3);
/*      */ 
/*  547 */         if ((!GSSUtil.useMSInterop()) && (bool)) {
/*  548 */           bool = verifyMechListMIC(this.DER_mechTypes, ((NegTokenInit)localObject1).getMechListMIC());
/*      */         }
/*      */ 
/*  553 */         if (bool) {
/*  554 */           if (isMechContextEstablished()) {
/*  555 */             localNegoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
/*  556 */             this.state = 3;
/*      */ 
/*  558 */             setContextFlags();
/*      */ 
/*  560 */             if (DEBUG) {
/*  561 */               System.out.println("SPNEGO Negotiated Mechanism = " + this.internal_mech + " " + GSSUtil.getMechStr(this.internal_mech));
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  566 */             localNegoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
/*  567 */             this.state = 2;
/*      */           }
/*      */         } else {
/*  570 */           localNegoResult = SpNegoToken.NegoResult.REJECT;
/*  571 */           this.state = 3;
/*      */         }
/*      */ 
/*  574 */         if (DEBUG) {
/*  575 */           System.out.println("SpNegoContext.acceptSecContext: mechanism wanted = " + localOid);
/*      */ 
/*  577 */           System.out.println("SpNegoContext.acceptSecContext: negotiated result = " + localNegoResult);
/*      */         }
/*      */ 
/*  582 */         NegTokenTarg localNegTokenTarg = new NegTokenTarg(localNegoResult.ordinal(), localOid, arrayOfByte4, null);
/*      */ 
/*  584 */         if (DEBUG) {
/*  585 */           System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + SpNegoToken.getTokenName(localNegTokenTarg.getType()));
/*      */         }
/*      */ 
/*  590 */         arrayOfByte1 = localNegTokenTarg.getEncoded();
/*      */       }
/*  592 */       else if (this.state == 2)
/*      */       {
/*  594 */         arrayOfByte2 = new byte[paramInputStream.available()];
/*  595 */         SpNegoToken.readFully(paramInputStream, arrayOfByte2);
/*  596 */         localObject1 = GSS_acceptSecContext(arrayOfByte2);
/*  597 */         if (localObject1 == null) {
/*  598 */           bool = false;
/*      */         }
/*      */ 
/*  602 */         if (bool) {
/*  603 */           if (isMechContextEstablished()) {
/*  604 */             localNegoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
/*  605 */             this.state = 3;
/*      */           } else {
/*  607 */             localNegoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
/*  608 */             this.state = 2;
/*      */           }
/*      */         } else {
/*  611 */           localNegoResult = SpNegoToken.NegoResult.REJECT;
/*  612 */           this.state = 3;
/*      */         }
/*      */ 
/*  616 */         localObject2 = new NegTokenTarg(localNegoResult.ordinal(), null, (byte[])localObject1, null);
/*      */ 
/*  618 */         if (DEBUG) {
/*  619 */           System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + SpNegoToken.getTokenName(((NegTokenTarg)localObject2).getType()));
/*      */         }
/*      */ 
/*  624 */         arrayOfByte1 = ((NegTokenTarg)localObject2).getEncoded();
/*      */       }
/*  628 */       else if (DEBUG) {
/*  629 */         System.out.println("AcceptSecContext: state = " + this.state);
/*      */       }
/*      */ 
/*  632 */       if (DEBUG)
/*  633 */         System.out.println("SpNegoContext.acceptSecContext: sending token = " + SpNegoToken.getHexBytes(arrayOfByte1));
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  637 */       Object localObject1 = new GSSException(11, -1, localIOException.getMessage());
/*      */ 
/*  639 */       ((GSSException)localObject1).initCause(localIOException);
/*  640 */       throw ((Throwable)localObject1);
/*      */     }
/*      */ 
/*  643 */     if (this.state == 3)
/*      */     {
/*  645 */       setContextFlags();
/*      */     }
/*  647 */     return arrayOfByte1;
/*      */   }
/*      */ 
/*      */   private Oid[] getAvailableMechs()
/*      */   {
/*  654 */     if (this.myCred != null) {
/*  655 */       Oid[] arrayOfOid = new Oid[1];
/*  656 */       arrayOfOid[0] = this.myCred.getInternalMech();
/*  657 */       return arrayOfOid;
/*      */     }
/*  659 */     return this.factory.availableMechs;
/*      */   }
/*      */ 
/*      */   private byte[] getEncodedMechs(Oid[] paramArrayOfOid)
/*      */     throws IOException, GSSException
/*      */   {
/*  669 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*  670 */     for (int i = 0; i < paramArrayOfOid.length; i++) {
/*  671 */       arrayOfByte = paramArrayOfOid[i].getDER();
/*  672 */       localDerOutputStream1.write(arrayOfByte);
/*      */     }
/*      */ 
/*  675 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*  676 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/*  677 */     byte[] arrayOfByte = localDerOutputStream2.toByteArray();
/*  678 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   private BitArray getContextFlags()
/*      */   {
/*  685 */     BitArray localBitArray = new BitArray(7);
/*      */ 
/*  687 */     if (getCredDelegState()) localBitArray.set(0, true);
/*  688 */     if (getMutualAuthState()) localBitArray.set(1, true);
/*  689 */     if (getReplayDetState()) localBitArray.set(2, true);
/*  690 */     if (getSequenceDetState()) localBitArray.set(3, true);
/*  691 */     if (getConfState()) localBitArray.set(5, true);
/*  692 */     if (getIntegState()) localBitArray.set(6, true);
/*      */ 
/*  694 */     return localBitArray;
/*      */   }
/*      */ 
/*      */   private void setContextFlags()
/*      */   {
/*  702 */     if (this.mechContext != null)
/*      */     {
/*  704 */       if (this.mechContext.getCredDelegState()) {
/*  705 */         this.credDelegState = true;
/*      */       }
/*      */ 
/*  708 */       if (!this.mechContext.getMutualAuthState()) {
/*  709 */         this.mutualAuthState = false;
/*      */       }
/*  711 */       if (!this.mechContext.getReplayDetState()) {
/*  712 */         this.replayDetState = false;
/*      */       }
/*  714 */       if (!this.mechContext.getSequenceDetState()) {
/*  715 */         this.sequenceDetState = false;
/*      */       }
/*  717 */       if (!this.mechContext.getIntegState()) {
/*  718 */         this.integState = false;
/*      */       }
/*  720 */       if (!this.mechContext.getConfState())
/*  721 */         this.confState = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean verifyMechListMIC(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*      */     throws GSSException
/*      */   {
/*  775 */     if (paramArrayOfByte2 == null) {
/*  776 */       if (DEBUG) {
/*  777 */         System.out.println("SpNegoContext: no MIC token validation");
/*      */       }
/*  779 */       return true;
/*      */     }
/*      */ 
/*  783 */     if (!this.mechContext.getIntegState()) {
/*  784 */       if (DEBUG) {
/*  785 */         System.out.println("SpNegoContext: no MIC token validation - mechanism does not support integrity");
/*      */       }
/*      */ 
/*  788 */       return true;
/*      */     }
/*      */ 
/*  792 */     boolean bool = false;
/*      */     try {
/*  794 */       MessageProp localMessageProp = new MessageProp(0, true);
/*  795 */       verifyMIC(paramArrayOfByte2, 0, paramArrayOfByte2.length, paramArrayOfByte1, 0, paramArrayOfByte1.length, localMessageProp);
/*      */ 
/*  797 */       bool = true;
/*      */     } catch (GSSException localGSSException) {
/*  799 */       bool = false;
/*  800 */       if (DEBUG) {
/*  801 */         System.out.println("SpNegoContext: MIC validation failed! " + localGSSException.getMessage());
/*      */       }
/*      */     }
/*      */ 
/*  805 */     return bool;
/*      */   }
/*      */ 
/*      */   private byte[] GSS_initSecContext(byte[] paramArrayOfByte)
/*      */     throws GSSException
/*      */   {
/*  812 */     byte[] arrayOfByte = null;
/*      */ 
/*  814 */     if (this.mechContext == null)
/*      */     {
/*  816 */       localObject = this.factory.manager.createName(this.peerName.toString(), this.peerName.getStringNameType(), this.internal_mech);
/*      */ 
/*  819 */       GSSCredentialImpl localGSSCredentialImpl = null;
/*  820 */       if (this.myCred != null)
/*      */       {
/*  822 */         localGSSCredentialImpl = new GSSCredentialImpl(this.factory.manager, this.myCred.getInternalCred());
/*      */       }
/*      */ 
/*  825 */       this.mechContext = this.factory.manager.createContext((GSSName)localObject, this.internal_mech, localGSSCredentialImpl, 0);
/*      */ 
/*  828 */       this.mechContext.requestConf(this.confState);
/*  829 */       this.mechContext.requestInteg(this.integState);
/*  830 */       this.mechContext.requestCredDeleg(this.credDelegState);
/*  831 */       this.mechContext.requestMutualAuth(this.mutualAuthState);
/*  832 */       this.mechContext.requestReplayDet(this.replayDetState);
/*  833 */       this.mechContext.requestSequenceDet(this.sequenceDetState);
/*  834 */       if ((this.mechContext instanceof ExtendedGSSContext)) {
/*  835 */         ((ExtendedGSSContext)this.mechContext).requestDelegPolicy(this.delegPolicyState);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  841 */     if (paramArrayOfByte != null)
/*  842 */       arrayOfByte = paramArrayOfByte;
/*      */     else {
/*  844 */       arrayOfByte = new byte[0];
/*      */     }
/*      */ 
/*  848 */     Object localObject = this.mechContext.initSecContext(arrayOfByte, 0, arrayOfByte.length);
/*      */ 
/*  850 */     return localObject;
/*      */   }
/*      */ 
/*      */   private byte[] GSS_acceptSecContext(byte[] paramArrayOfByte)
/*      */     throws GSSException
/*      */   {
/*  858 */     if (this.mechContext == null)
/*      */     {
/*  860 */       localObject = null;
/*  861 */       if (this.myCred != null)
/*      */       {
/*  863 */         localObject = new GSSCredentialImpl(this.factory.manager, this.myCred.getInternalCred());
/*      */       }
/*      */ 
/*  866 */       this.mechContext = this.factory.manager.createContext((GSSCredential)localObject);
/*      */     }
/*      */ 
/*  871 */     Object localObject = this.mechContext.acceptSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */ 
/*  874 */     return localObject;
/*      */   }
/*      */ 
/*      */   private static Oid negotiate_mech_type(Oid[] paramArrayOfOid1, Oid[] paramArrayOfOid2)
/*      */   {
/*  887 */     for (int i = 0; i < paramArrayOfOid1.length; i++) {
/*  888 */       for (int j = 0; j < paramArrayOfOid2.length; j++) {
/*  889 */         if (paramArrayOfOid2[j].equals(paramArrayOfOid1[i])) {
/*  890 */           if (DEBUG) {
/*  891 */             System.out.println("SpNegoContext: negotiated mechanism = " + paramArrayOfOid2[j]);
/*      */           }
/*      */ 
/*  894 */           return paramArrayOfOid2[j];
/*      */         }
/*      */       }
/*      */     }
/*  898 */     return null;
/*      */   }
/*      */ 
/*      */   public final boolean isEstablished() {
/*  902 */     return this.state == 3;
/*      */   }
/*      */ 
/*      */   public final boolean isMechContextEstablished() {
/*  906 */     if (this.mechContext != null) {
/*  907 */       return this.mechContext.isEstablished();
/*      */     }
/*  909 */     if (DEBUG) {
/*  910 */       System.out.println("The underlying mechansim context has not been initialized");
/*      */     }
/*      */ 
/*  913 */     return false;
/*      */   }
/*      */ 
/*      */   public final byte[] export() throws GSSException
/*      */   {
/*  918 */     throw new GSSException(16, -1, "GSS Export Context not available");
/*      */   }
/*      */ 
/*      */   public final void setChannelBinding(ChannelBinding paramChannelBinding)
/*      */     throws GSSException
/*      */   {
/*  928 */     this.channelBinding = paramChannelBinding;
/*      */   }
/*      */ 
/*      */   final ChannelBinding getChannelBinding() {
/*  932 */     return this.channelBinding;
/*      */   }
/*      */ 
/*      */   public final void requestAnonymity(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*      */   }
/*      */ 
/*      */   public final boolean getAnonymityState()
/*      */   {
/*  953 */     return false;
/*      */   }
/*      */ 
/*      */   public void requestLifetime(int paramInt)
/*      */     throws GSSException
/*      */   {
/*  961 */     if ((this.state == 1) && (isInitiator()))
/*  962 */       this.lifetime = paramInt;
/*      */   }
/*      */ 
/*      */   public final int getLifetime()
/*      */   {
/*  969 */     if (this.mechContext != null) {
/*  970 */       return this.mechContext.getLifetime();
/*      */     }
/*  972 */     return 2147483647;
/*      */   }
/*      */ 
/*      */   public final boolean isTransferable() throws GSSException
/*      */   {
/*  977 */     return false;
/*      */   }
/*      */ 
/*      */   public final void requestSequenceDet(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  985 */     if ((this.state == 1) && (isInitiator()))
/*  986 */       this.sequenceDetState = paramBoolean;
/*      */   }
/*      */ 
/*      */   public final boolean getSequenceDetState()
/*      */   {
/*  994 */     return (this.sequenceDetState) || (this.replayDetState);
/*      */   }
/*      */ 
/*      */   public final void requestReplayDet(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/* 1002 */     if ((this.state == 1) && (isInitiator()))
/* 1003 */       this.replayDetState = paramBoolean;
/*      */   }
/*      */ 
/*      */   public final boolean getReplayDetState()
/*      */   {
/* 1011 */     return (this.replayDetState) || (this.sequenceDetState);
/*      */   }
/*      */ 
/*      */   public final GSSNameSpi getTargName()
/*      */     throws GSSException
/*      */   {
/* 1017 */     if (this.mechContext != null) {
/* 1018 */       GSSNameImpl localGSSNameImpl = (GSSNameImpl)this.mechContext.getTargName();
/* 1019 */       this.peerName = localGSSNameImpl.getElement(this.internal_mech);
/* 1020 */       return this.peerName;
/*      */     }
/* 1022 */     if (DEBUG) {
/* 1023 */       System.out.println("The underlying mechansim context has not been initialized");
/*      */     }
/*      */ 
/* 1026 */     return null;
/*      */   }
/*      */ 
/*      */   public final GSSNameSpi getSrcName()
/*      */     throws GSSException
/*      */   {
/* 1033 */     if (this.mechContext != null) {
/* 1034 */       GSSNameImpl localGSSNameImpl = (GSSNameImpl)this.mechContext.getSrcName();
/* 1035 */       this.myName = localGSSNameImpl.getElement(this.internal_mech);
/* 1036 */       return this.myName;
/*      */     }
/* 1038 */     if (DEBUG) {
/* 1039 */       System.out.println("The underlying mechansim context has not been initialized");
/*      */     }
/*      */ 
/* 1042 */     return null;
/*      */   }
/*      */ 
/*      */   public final GSSCredentialSpi getDelegCred()
/*      */     throws GSSException
/*      */   {
/* 1057 */     if ((this.state != 2) && (this.state != 3))
/* 1058 */       throw new GSSException(12);
/* 1059 */     if (this.mechContext != null) {
/* 1060 */       GSSCredentialImpl localGSSCredentialImpl = (GSSCredentialImpl)this.mechContext.getDelegCred();
/*      */ 
/* 1063 */       boolean bool = false;
/* 1064 */       if (localGSSCredentialImpl.getUsage() == 1) {
/* 1065 */         bool = true;
/*      */       }
/* 1067 */       GSSCredentialSpi localGSSCredentialSpi = localGSSCredentialImpl.getElement(this.internal_mech, bool);
/*      */ 
/* 1069 */       SpNegoCredElement localSpNegoCredElement = new SpNegoCredElement(localGSSCredentialSpi);
/* 1070 */       return localSpNegoCredElement.getInternalCred();
/*      */     }
/* 1072 */     throw new GSSException(12, -1, "getDelegCred called in invalid state!");
/*      */   }
/*      */ 
/*      */   public final int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2)
/*      */     throws GSSException
/*      */   {
/* 1079 */     if (this.mechContext != null) {
/* 1080 */       return this.mechContext.getWrapSizeLimit(paramInt1, paramBoolean, paramInt2);
/*      */     }
/* 1082 */     throw new GSSException(12, -1, "getWrapSizeLimit called in invalid state!");
/*      */   }
/*      */ 
/*      */   public final byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1089 */     if (this.mechContext != null) {
/* 1090 */       return this.mechContext.wrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
/*      */     }
/* 1092 */     throw new GSSException(12, -1, "Wrap called in invalid state!");
/*      */   }
/*      */ 
/*      */   public final void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1099 */     if (this.mechContext != null)
/* 1100 */       this.mechContext.wrap(paramInputStream, paramOutputStream, paramMessageProp);
/*      */     else
/* 1102 */       throw new GSSException(12, -1, "Wrap called in invalid state!");
/*      */   }
/*      */ 
/*      */   public final byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1110 */     if (this.mechContext != null) {
/* 1111 */       return this.mechContext.unwrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
/*      */     }
/* 1113 */     throw new GSSException(12, -1, "UnWrap called in invalid state!");
/*      */   }
/*      */ 
/*      */   public final void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1120 */     if (this.mechContext != null)
/* 1121 */       this.mechContext.unwrap(paramInputStream, paramOutputStream, paramMessageProp);
/*      */     else
/* 1123 */       throw new GSSException(12, -1, "UnWrap called in invalid state!");
/*      */   }
/*      */ 
/*      */   public final byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1131 */     if (this.mechContext != null) {
/* 1132 */       return this.mechContext.getMIC(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
/*      */     }
/* 1134 */     throw new GSSException(12, -1, "getMIC called in invalid state!");
/*      */   }
/*      */ 
/*      */   public final void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1141 */     if (this.mechContext != null)
/* 1142 */       this.mechContext.getMIC(paramInputStream, paramOutputStream, paramMessageProp);
/*      */     else
/* 1144 */       throw new GSSException(12, -1, "getMIC called in invalid state!");
/*      */   }
/*      */ 
/*      */   public final void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1153 */     if (this.mechContext != null) {
/* 1154 */       this.mechContext.verifyMIC(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, paramMessageProp);
/*      */     }
/*      */     else
/* 1157 */       throw new GSSException(12, -1, "verifyMIC called in invalid state!");
/*      */   }
/*      */ 
/*      */   public final void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1164 */     if (this.mechContext != null)
/* 1165 */       this.mechContext.verifyMIC(paramInputStream1, paramInputStream2, paramMessageProp);
/*      */     else
/* 1167 */       throw new GSSException(12, -1, "verifyMIC called in invalid state!");
/*      */   }
/*      */ 
/*      */   private static String printState(int paramInt)
/*      */   {
/* 1173 */     switch (paramInt) {
/*      */     case 1:
/* 1175 */       return "STATE_NEW";
/*      */     case 2:
/* 1177 */       return "STATE_IN_PROCESS";
/*      */     case 3:
/* 1179 */       return "STATE_DONE";
/*      */     case 4:
/* 1181 */       return "STATE_DELETED";
/*      */     }
/* 1183 */     return "Unknown state " + paramInt;
/*      */   }
/*      */ 
/*      */   public Object inquireSecContext(InquireType paramInquireType)
/*      */     throws GSSException
/*      */   {
/* 1192 */     if (this.mechContext == null) {
/* 1193 */       throw new GSSException(12, -1, "Underlying mech not established.");
/*      */     }
/*      */ 
/* 1196 */     if ((this.mechContext instanceof ExtendedGSSContext)) {
/* 1197 */       return ((ExtendedGSSContext)this.mechContext).inquireSecContext(paramInquireType);
/*      */     }
/* 1199 */     throw new GSSException(2, -1, "inquireSecContext not supported by underlying mech.");
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.jgss.spnego.SpNegoContext
 * JD-Core Version:    0.6.2
 */