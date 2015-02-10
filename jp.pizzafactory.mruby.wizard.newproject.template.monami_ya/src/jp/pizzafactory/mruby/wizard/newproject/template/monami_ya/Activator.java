package jp.pizzafactory.mruby.wizard.newproject.template.monami_ya;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public static final String PLUGIN_ID = "jp.pizzafactory.mruby.wizard.newproject.template.monami_ya"; //$NON-NLS-1$
    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
    }
}
