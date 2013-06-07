package com.sun.xml.internal.bind.api;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

/** @deprecated */
public abstract class BridgeContext
{
  public abstract void setErrorHandler(ValidationEventHandler paramValidationEventHandler);

  public abstract void setAttachmentMarshaller(AttachmentMarshaller paramAttachmentMarshaller);

  public abstract void setAttachmentUnmarshaller(AttachmentUnmarshaller paramAttachmentUnmarshaller);

  public abstract AttachmentMarshaller getAttachmentMarshaller();

  public abstract AttachmentUnmarshaller getAttachmentUnmarshaller();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.api.BridgeContext
 * JD-Core Version:    0.6.2
 */