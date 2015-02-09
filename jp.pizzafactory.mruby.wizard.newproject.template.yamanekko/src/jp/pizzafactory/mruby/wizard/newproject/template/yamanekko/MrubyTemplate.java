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

public class MrubyTemplate implements IMrubyTemplate {

    @Override
    public void deploy(IProject project, IProgressMonitor monitor) {
        URL url = Activator.getContext().getBundle().getEntry("sources.zip");
        try {
            ZipInputStream zist = new ZipInputStream(url.openStream());
            ZipEntry zipEntry;
            while ((zipEntry = zist.getNextEntry()) != null) {
                String name = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    IFolder folder = project.getFolder(name);
                    if (!folder.exists()) {
                        try {
                            folder.create(true, true, monitor);
                        } catch (CoreException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

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
                    try {
                        file.create(ist, true, monitor);
                    } catch (CoreException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                zist.closeEntry();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
