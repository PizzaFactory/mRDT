package jp.pizzafactory.mruby.wizard.newproject.template;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMrubyTemplate {

    public void deploy(IProject project, IProgressMonitor monitor)
            throws CoreException;
}
