package org.frankframework.frankdoc.testtarget.featurepackage;

import org.frankframework.doc.Category;
import org.frankframework.doc.CategoryType;
import org.frankframework.doc.Forward;

/**
 * Sends a message using a {@link ISender sender} and optionally receives a reply from the same sender, or
 * from a {@link ICorrelatedPullingListener listener}.
 *
 * @ff.parameters any parameters defined on the pipe will be handed to the sender, if this is a {@link ISenderWithParameters ISenderWithParameters}
 * @ff.parameter  stubFilename will <u>not</u> be handed to the sender
 * and it is used at runtime instead of the {@literal stubFilename} specified by the attribute. A lookup of the
 * file for this stubFilename will be done at runtime, while the file for the stubFilename specified
 * as an attribute will be done at configuration time.
 *
 * @author  Gerrit van Brakel
 */
@Forward(name = "timeout")
@Forward(name = "illegalResult")
@Forward(name = "presumedTimeout")
@Forward(name = "{@literal interrupt}")
@Forward(name = "&lt;defined-by-sender&gt;", description = "any {@literal forward}, as returned by name by {@link ISender sender}")
@Category(CategoryType.BASIC)
@Category(CategoryType.ADVANCED)
public class DocumentedParent implements HasSender {

	private String destinationName;
	private ISender sender = null;
	private ICorrelatedPullingListener listener = null;

	/** Name of the JMS destination (queue or topic) to use */
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getDestinationName() {
		return destinationName;
	}


	/**
	 * The sender that should send the message
	 * @ff.mandatory
	 */
	protected void setSender(ISender sender) {
		this.sender = sender;
	}

	/** Listener for responses on the request sent */
	public void setListener(ICorrelatedPullingListener listener) {
	}

	@SuppressWarnings("all")
	public ISender getSender() {
		return this.sender;
	}

	@SuppressWarnings("all")
	public ICorrelatedPullingListener getListener() {
		return this.listener;
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public String getName() {
		return null;
	}
}
