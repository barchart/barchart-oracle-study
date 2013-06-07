package com.sun.jndi.ldap;

import javax.naming.NamingEnumeration;

abstract interface ReferralEnumeration extends NamingEnumeration
{
  public abstract void appendUnprocessedReferrals(LdapReferralException paramLdapReferralException);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.ReferralEnumeration
 * JD-Core Version:    0.6.2
 */