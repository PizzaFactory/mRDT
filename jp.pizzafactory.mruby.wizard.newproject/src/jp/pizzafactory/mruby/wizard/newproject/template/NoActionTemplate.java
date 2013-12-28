package jp.pizzafactory.mruby.wizard.newproject.template;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Mruby Template.
 *
 * @author monaka
 *
 */
public class NoActionTemplate implements IMrubyTemplate {

	@Override
	public void deploy(IProject project, IProgressMonitor monitor) {
		return;
	}

}
