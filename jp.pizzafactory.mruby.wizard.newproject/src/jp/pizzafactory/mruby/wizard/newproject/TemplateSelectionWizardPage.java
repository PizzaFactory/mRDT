package jp.pizzafactory.mruby.wizard.newproject;

import java.util.ArrayList;

import jp.pizzafactory.mruby.wizard.newproject.template.IMrubyTemplate;
import jp.pizzafactory.mruby.wizard.newproject.template.NoActionTemplate;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class TemplateSelectionWizardPage extends WizardPage {

	protected TemplateSelectionWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription("Select template set to deploy");

		ImageDescriptor image = Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/mruby.png");
		setImageDescriptor(image);

	}

	ArrayList<IMrubyTemplate> templateList = new ArrayList<IMrubyTemplate>();
	List list;

	private void setNoActionTemplate() {
		assert(list != null);
		list.add("Empty (no deploy)");
		templateList.add(new NoActionTemplate());
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		list = new List(composite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);

		setNoActionTemplate();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(Activator
				.getDefault().getBundle().getSymbolicName()
				+ ".template");

		IExtension[] extensions = point.getExtensions();

		for (IExtension extension : extensions) {

			IConfigurationElement[] elements = extension
					.getConfigurationElements();

			try {
				for (IConfigurationElement element : elements) {
					String elementName = element.getName();
					String name;
					Object instance;
					if (elementName.equals("template")) {
						instance = element.createExecutableExtension("class");
						name = element.getAttribute("name");
						if (instance != null && name != null) {
							list.add(name);
							templateList.add((IMrubyTemplate) instance);
						}
					}
				}
			} catch (CoreException e) {
				Activator
				.getDefault()
				.getLog()
				.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Error", e));
			}
			list.setSelection(0);
		}

		setControl(composite);
	}

	public void deploy(IProject project, IProgressMonitor monitor) {
		int index = list.getSelectionIndex();
		IMrubyTemplate instance = templateList.get(index);
		instance.deploy(project, monitor);
	}
}
