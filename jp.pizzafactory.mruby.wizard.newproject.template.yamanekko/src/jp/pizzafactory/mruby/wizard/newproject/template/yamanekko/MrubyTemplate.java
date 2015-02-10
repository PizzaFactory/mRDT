package jp.pizzafactory.mruby.wizard.newproject.template.yamanekko;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jp.pizzafactory.mruby.wizard.newproject.template.IMrubyTemplate;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

public class MrubyTemplate implements IMrubyTemplate {

    private void unzipFolder(String name, IProject project,
            IProgressMonitor monitor) throws CoreException {
        IFolder folder = project.getFolder(name);
        if (!folder.exists()) {
            try {
                folder.create(true, true, monitor);
            } catch (CoreException e) {
                IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID,
                        NLS.bind("Can't create folder {0}", name), e);
                throw new CoreException(status);
            }
        }
    }

    private void unzipFile(String name, IProject project, ZipInputStream zist,
            IProgressMonitor monitor) throws CoreException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            byte[] buffer = new byte[1024];
            int len = zist.read(buffer);
            if (len <= 0) {
                break;
            }
            baos.write(buffer, 0, len);
        }
        byte[] entryData = baos.toByteArray();
        InputStream ist = new ByteArrayInputStream(entryData);

        IFile file = project.getFile(name);
        file.create(ist, true, monitor);
    }

    private void unzip(IProject project, IProgressMonitor monitor)
            throws IOException, CoreException {
        MultiStatus multiStatus = new MultiStatus(Activator.PLUGIN_ID,
                IStatus.OK, "Error(s) in setup mruby environment.", null);

        URL url = Activator.getContext().getBundle().getEntry("sources.zip");
        ZipInputStream zist = new ZipInputStream(url.openStream());
        ZipEntry zipEntry;
        while ((zipEntry = zist.getNextEntry()) != null) {
            try {
                String name = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    unzipFolder(name, project, monitor);
                } else {
                    unzipFile(name, project, zist, monitor);
                }
            } catch (CoreException e) {
                multiStatus.add(e.getStatus());
            }
            zist.closeEntry();
        }

        if (!multiStatus.isOK()) {
            throw new CoreException(multiStatus);
        }
    }

    @Override
    public void deploy(IProject project, IProgressMonitor monitor)
            throws CoreException {
        try {
            unzip(project, monitor);
        } catch (IOException e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    "sources.zip may be broken.", e);
            throw new CoreException(status);
        }
    }
}
