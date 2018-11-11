package yugecin.intellijlaf;

import com.intellij.ide.ui.laf.darcula.DarculaInstaller;
import com.intellij.openapi.components.*;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.*;

import javax.swing.*;

@State(
	name = "intellijlaf",
	storages = @Storage("yugeplugins.xml")
)
public class Plugin implements PersistentStateComponent<Plugin.State>
{
	private static final State state = new State();

	@Nullable
	@Override
	public State getState()
	{
		return state;
	}

	@Override
	public void loadState(@NotNull State state)
	{
		changeLaf(state.lafClassName);
	}

	public static void changeLaf(@Nullable String lafClassName)
	{
		try {
			if (lafClassName != null) {
				final boolean uninstalldarcula = UIUtil.isUnderDarcula();
				UIManager.setLookAndFeel(lafClassName);
				if (uninstalldarcula) {
					SwingUtilities.invokeLater(DarculaInstaller::uninstall);
				}
			}
			state.lafClassName = lafClassName;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static class State
	{
		public String lafClassName;
	}
}
