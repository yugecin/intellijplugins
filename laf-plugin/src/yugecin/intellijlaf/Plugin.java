package yugecin.intellijlaf;

import com.intellij.openapi.components.*;
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
				UIManager.setLookAndFeel(lafClassName);
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
