package javax.security.sasl;

public abstract interface SaslServer
{
  public abstract String getMechanismName();

  public abstract byte[] evaluateResponse(byte[] paramArrayOfByte)
    throws SaslException;

  public abstract boolean isComplete();

  public abstract String getAuthorizationID();

  public abstract byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException;

  public abstract byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException;

  public abstract Object getNegotiatedProperty(String paramString);

  public abstract void dispose()
    throws SaslException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.sasl.SaslServer
 * JD-Core Version:    0.6.2
 */