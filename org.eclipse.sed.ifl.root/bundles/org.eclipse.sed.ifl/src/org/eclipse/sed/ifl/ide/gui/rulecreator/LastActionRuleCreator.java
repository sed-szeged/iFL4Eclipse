package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.sed.ifl.control.score.filter.LastActionRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.ide.gui.icon.ScoreStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;

public class LastActionRuleCreator extends Composite implements RuleCreator {

	private String domain;
	private Button increasedButton;
	private Button decreasedButton;
	private Button unchangedButton;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LastActionRuleCreator(Composite parent, int style, String domain) {
		super(parent, style);
		this.domain = domain;
		setLayout(new GridLayout(1, false));
		
		Group grpLastAction = new Group(this, SWT.NONE);
		grpLastAction.setText("Last action");
		grpLastAction.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		increasedButton = new Button(grpLastAction, SWT.RADIO);
		increasedButton.setSelection(true);
		increasedButton.setText("increased");
		
		decreasedButton = new Button(grpLastAction, SWT.RADIO);
		decreasedButton.setText("decreased");
		
		unchangedButton = new Button(grpLastAction, SWT.RADIO);
		unchangedButton.setText("unchanged");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public Rule getRule() {
		ScoreStatus status = null;
		if(increasedButton.getSelection()) {
			status = ScoreStatus.INCREASED;
		} else if(decreasedButton.getSelection()) {
			status = ScoreStatus.DECREASED;
		} else if(unchangedButton.getSelection()) {
			status = ScoreStatus.UNDEFINED;
		}
		return new LastActionRule(this.domain, status);
	}

}