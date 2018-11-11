package yugecin.intellijlaf;

import com.intellij.ide.actions.QuickSwitchSchemeAction;
import com.intellij.ide.ui.LafManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MenuAction extends QuickSwitchSchemeAction
{
	@Override
	protected void fillActions(
		Project project,
		@NotNull DefaultActionGroup group,
		@NotNull DataContext dataContext)
	{
		final LafManager manager = LafManager.getInstance();
		final UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		final UIManager.LookAndFeelInfo current = manager.getCurrentLookAndFeel();
		group.add(new DumbAwareAction("Reset", null, ourNotCurrentAction) {
			@Override
			public void actionPerformed(AnActionEvent anActionEvent) {
				Plugin.changeLaf(null);
			}
		});
		for (UIManager.LookAndFeelInfo lf : lafs) {
			final Icon icon;
			if (lf == current) {
				icon = ourCurrentAction;
			} else {
				icon = ourNotCurrentAction;
			}
			group.add(new DumbAwareAction(lf.getName(), null, icon) {
				public void actionPerformed(AnActionEvent e) {
					Plugin.changeLaf(lf.getClassName());
					try {
						manager.updateUI();
						manager.repaintUI();
					} catch (Throwable ignored) {
					}
				}
			});
		}
	}

	@Override
	protected boolean isEnabled()
	{
		return true;
	}
}
