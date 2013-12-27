package jp.pizzafactory.mruby.wizard.newproject;

import java.lang.reflect.InvocationTargetException;

import jp.pizzafactory.mruby.builder.minirake.builder.MinirakeBuilder;
import jp.pizzafactory.mruby.builder.minirake.builder.MrubyNature;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class MinirakeProjectWizard extends Wizard implements INewWizard,
		IRunnableWithProgress {
	NewWizardPage newWizardPage = new NewWizardPage("New Mruby Project");

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(newWizardPage);
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, true, this);
			return true;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	static final String CCNATURE_ID = "org.eclipse.cdt.core.ccnature";

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		String projectName = newWizardPage.getProjectName();
//		URI locationURI = newWizardPage.getLocationURI();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IProject project = workspaceRoot.getProject(projectName);

		monitor.beginTask("Creating mruby Project", 5);

		IProjectDescription projectDescription = workspace
				.newProjectDescription(project.getName());
//		projectDescription.setLocationURI(locationURI);
		monitor.worked(1);
		
		projectDescription.setNatureIds(new String[] { MrubyNature.NATURE_ID, CCNATURE_ID });
		monitor.worked(1);

		ICommand command = projectDescription.newCommand();
		command.setBuilderName(MinirakeBuilder.BUILDER_ID);
		projectDescription.setBuildSpec(new ICommand[] { command });
		monitor.worked(1);

		try {
			IProject cdtProject = CCorePlugin.getDefault().createCDTProject(
					projectDescription, project, monitor);
		} catch (OperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
