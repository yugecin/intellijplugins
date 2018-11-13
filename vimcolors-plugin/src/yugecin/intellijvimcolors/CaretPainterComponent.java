package yugecin.intellijvimcolors;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.EditorFactoryAdapter;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.impl.view.EditorView;
import com.intellij.openapi.util.Key;
import com.intellij.ui.JBColor;
import com.maddyhome.idea.vim.EventFacade;
import com.maddyhome.idea.vim.command.CommandState;
import com.maddyhome.idea.vim.command.CommandState.Mode;
import com.maddyhome.idea.vim.command.CommandState.SubMode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.Field;

public class CaretPainterComponent
{
	private static final Key<Mode> lastModeKey = new Key<>("yugecin.intellijvimcolors.lastmodekey");
	private static final Color
		CARET_COLOR_COMMAND = new JBColor(new Color(0xFF0000), new Color(0xFF0000)),
		CARET_COLOR_INSERT = new JBColor(new Color(0x00FF00), new Color(0x00FF00)),
		CARET_COLOR_VISUAL = new JBColor(new Color(0x800080), new Color(0x800080)),
		CARET_COLOR_VISUALBLOCK = new JBColor(new Color(0xFF8800), new Color(0xFF8800)),
		CARET_COLOR_REPEAT = new JBColor(new Color(0x0), new Color(0x0));

	private static Field editorViewField;

	static {
		for (Field field : EditorImpl.class.getDeclaredFields()) {
			if (EditorView.class.equals(field.getType())) {
				editorViewField = field;
				editorViewField.setAccessible(true);
				break;
			}
		}
		editorViewField = null;
	}

	public CaretPainterComponent()
	{
		final TypedAction typedAction = EditorActionManager.getInstance().getTypedAction();
		final TypedActionHandler originalTypedActionHandler = typedAction.getRawHandler();
		typedAction.setupRawHandler((editor, c, dataContext) -> {
			originalTypedActionHandler.execute(editor, c, dataContext);
			checkModeChanges(editor);
		});

		ActionManager.getInstance().addAnActionListener(new AnActionListener() {
			@Override
			public void beforeActionPerformed(
				AnAction anAction,
				DataContext dataContext,
				AnActionEvent anActionEvent)
			{
			}

			@Override
			public void afterActionPerformed(
				AnAction action,
				DataContext dataContext,
				AnActionEvent event)
			{
				if (event == null) {
					return;
				}
				if ("Shortcuts".equals(event.getPresentation().getText())) {
					Editor editor = dataContext.getData(PlatformDataKeys.EDITOR);
					if (editor != null) {
						try {
							checkModeChanges(editor);
						} catch (Throwable ignored) {}
					}
				}
			}

			@Override
			public void beforeEditorTyping(char c, DataContext dataContext)
			{
			}
		});

		EventFacade.getInstance().addEditorFactoryListener(new EditorFactoryAdapter()
		{
			public void editorCreated(@NotNull EditorFactoryEvent event)
			{
				final Editor editor = event.getEditor();
				checkModeChanges(editor);
				//final AnAction action = UpdateColorsAction.getInstance();
				//action.registerCustomShortcutSet(27, 0, editor.getComponent());
			}

			public void editorReleased(@NotNull EditorFactoryEvent event)
			{
				//final AnAction action = UpdateColorsAction.getInstance();
				//action.unregisterCustomShortcutSet(event.getEditor().getComponent());
			}
		}, ApplicationManager.getApplication());
	}

	public static void checkModeChanges(@NotNull Editor editor)
	{
		final Mode mode = CommandState.getInstance(editor).getMode();
		if (mode.equals(editor.getUserData(lastModeKey))) {
			return;
		}
		editor.putUserData(lastModeKey, mode);
		EditorSettings s = editor.getSettings();
		EditorColorsScheme cs = editor.getColorsScheme();

		switch (mode) {
		case INSERT:
			s.setBlockCursor(true);
			cs.setColor(EditorColors.CARET_COLOR, CARET_COLOR_INSERT);
			break;
		case VISUAL:
			if (SubMode.VISUAL_BLOCK == CommandState.getInstance(editor).getSubMode()) {
				cs.setColor(EditorColors.CARET_COLOR, CARET_COLOR_VISUALBLOCK);
			} else {
				cs.setColor(EditorColors.CARET_COLOR, CARET_COLOR_VISUAL);
			}
			break;
		case REPEAT:
			cs.setColor(EditorColors.CARET_COLOR, CARET_COLOR_REPEAT);
			break;
		default:
			cs.setColor(EditorColors.CARET_COLOR, CARET_COLOR_COMMAND);
			break;
		}

		try {
			if (editorViewField != null) {
				((EditorView) editorViewField.get(editor)).repaintCarets();
				return;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		editor.getComponent().repaint();
	}
}
