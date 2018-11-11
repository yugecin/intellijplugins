collection of plugins for IntelliJ platform based applications

laf-plugin
----------
Allows you to select any installed Look and Feel, not only 'intellij' and 'darcula' (like 'Windows Classic' and 'Mac OSX').
To change, go to 'View | Quick Switch Theme', choose 'Change LAF' and pick a LAF.
When just changed, it *might* be needed to close and reopen any open projects for all changes to apply.
It should save and apply automatically when (re)starting the IDE.
When going back to default LAFs, choose the 'Reset' option so the last saved one won't be applied at startup anymore.
If anything goes bad (or when uninstalling), the chosen laf is saved in config/options/yugeplugins.xml under the 'intellijlaf' component.

vimcolors-plugin
----------------
Needs IdeaVIM plugin
Changes the caret's color based on the current mode.
Normal = red
Insert = green(lime)
Visual = purple
Visual block = orange
