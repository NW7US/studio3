package com.aptana.git.ui.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.actions.Messages;

public class SwitchBranchItem extends AbstractDynamicBranchItem
{

	public SwitchBranchItem()
	{
		super();
	}

	public SwitchBranchItem(String id)
	{
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems()
	{
		IResource resource = getSelectedResource();
		if (resource == null)
			return new IContributionItem[0];

		final GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
			return new IContributionItem[0];

		Collection<IContributionItem> contributions = new ArrayList<IContributionItem>();

		SortedSet<String> localBranches = new TreeSet<String>(repo.localBranches());
		for (final String branchName : localBranches)
		{
			contributions.add(new ContributionItem()
			{
				@Override
				public void fill(Menu menu, int index)
				{
					MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index++);
					menuItem.setText(branchName);
					menuItem.setEnabled(!branchName.equals(repo.currentBranch()));
					menuItem.addSelectionListener(new SelectionAdapter()
					{
						public void widgetSelected(SelectionEvent e)
						{
							// what to do when menu is subsequently selected.
							switchBranch(repo, branchName);
						}
					});
				}
			});
		}
		return contributions.toArray(new IContributionItem[contributions.size()]);
	}

	protected void switchBranch(final GitRepository repo, final String branchName)
	{
		if (!repo.switchBranch(branchName))
			return;
		// Now show a tooltip "toast" for 3 seconds to announce success
		final Shell shell = Display.getDefault().getActiveShell();
		String text = MessageFormat.format(Messages.SwitchBranchAction_BranchSwitch_Msg, branchName);
		DefaultToolTip toolTip = new DefaultToolTip(shell)
		{
			@Override
			public Point getLocation(Point size, Event event)
			{
				final Rectangle workbenchWindowBounds = shell.getBounds();
				int xCoord = workbenchWindowBounds.x + workbenchWindowBounds.width - size.x - 10;
				int yCoord = workbenchWindowBounds.y + workbenchWindowBounds.height - size.y - 10;
				return new Point(xCoord, yCoord);
			}
		};
		toolTip.setHideDelay(TOOLTIP_LIFETIME);
		toolTip.setText(text);
		toolTip.show(new Point(0, 0));
	}
}
