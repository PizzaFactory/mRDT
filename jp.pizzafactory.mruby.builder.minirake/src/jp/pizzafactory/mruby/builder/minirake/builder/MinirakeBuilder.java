package jp.pizzafactory.mruby.builder.minirake.builder;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

import jp.pizzafactory.mruby.builder.minirake.Activator;

import org.eclipse.cdt.core.resources.IConsole;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

public class MinirakeBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "jp.pizzafactory.mruby.builder.minirake.minirakeBuilder";

	private void init() throws IOException {

		System.setProperty("org.jruby.embed.compat.version", "JRuby1.9");
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();

		IFile file = project.getFile("minirake");
		if (file.exists() && file.isAccessible()) {
			debugLog("minirake found.");

			IConsole console = CUIPlugin.getDefault().getConsoleManager()
					.getConsole(project);
			console.start(project);

			try {
				init();

				ScriptingContainer container = new ScriptingContainer(
						LocalContextScope.SINGLETHREAD,
						LocalVariableBehavior.TRANSIENT);

				String rootPath = project.getLocationURI().getPath();
				ArrayList<String> arrayList = new ArrayList<String>();
				arrayList.add(rootPath);
				container.setLoadPaths(arrayList);
				container.setCurrentDirectory(rootPath);

				if (kind == CLEAN_BUILD) {
					container.put("ARGV", new String[] { "clean" });
					debugLog("Selected CLEAN_BUILD");
				} else if (kind == FULL_BUILD) {
					container.put("ARGV", new String[] { "clean", "all" });
					debugLog("Selected FULL_BUILD");
				}

				container.setError(new PrintStream(console.getErrorStream()));
				container.setOutput(new PrintStream(console.getOutputStream()));

				container.runScriptlet("load 'minirake'");
				container.runScriptlet("RakeApp.new.run");
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR,
						Activator.PLUGIN_ID, "Failed to spawn minirake", e));
			} finally {
				try {
					console.getOutputStream().close();
				} catch (IOException e) {
				}
				try {
					console.getErrorStream().close();
				} catch (IOException e) {
				}
			}
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} else {
			debugLog("minirake not found.");
		}
		return null;
	}

	private void debugLog(String message) {
		if (Activator.DEBUG) {
			IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID,
					message);
			Activator.getDefault().getLog().log(status);
		}
	}
}
