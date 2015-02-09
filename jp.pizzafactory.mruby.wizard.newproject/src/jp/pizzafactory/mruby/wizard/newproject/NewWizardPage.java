package jp.pizzafactory.mruby.wizard.newproject;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class NewWizardPage extends WizardNewProjectCreationPage {

    public NewWizardPage(String pageName) {
        super(pageName);
        setTitle(pageName);
        setDescription("Create New Mruby Project");

        ImageDescriptor image = Activator.imageDescriptorFromPlugin(
                Activator.PLUGIN_ID, "icons/mruby.png");
        setImageDescriptor(image);
    }
}
