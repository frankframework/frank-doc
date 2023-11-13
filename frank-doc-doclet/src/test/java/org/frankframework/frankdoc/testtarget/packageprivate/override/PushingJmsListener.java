package org.frankframework.frankdoc.testtarget.packageprivate.override;

/**
 * JMSListener re-implemented as a pushing listener rather than a pulling listener.
 */
public class PushingJmsListener<M>  extends AbstractParent<M>  implements IPeekableListener<M> {
}
