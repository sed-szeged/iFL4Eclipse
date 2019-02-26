package org.eclipse.sed.ifl.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.sed.ifl.model.source.ICodeChunkLocation;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;


public class ScoreListView extends View {
	ScoreListUI ui;

	public ScoreListView(ScoreListUI ui) {
		super();
		this.ui = ui;
	}

	@Override
	public Composite getUI() {
		return ui;
	}

	public void refreshScores(Map<IMethodDescription, Score> scores) {
		ui.clearMethodScores();
		ui.setMethodScore(scores);
	}

	public void createOptionsMenu(Iterable<Option> options) {
		ui.createContexMenu(options);
	}

	@Override
	public void init() {
		ui.eventOptionSelected().add(optionSelectedListener);
		ui.eventSortRequired().add(sortListener);
		ui.eventNavigateToRequired().add(navigateToListener);
		ui.eventSelectionChanged().add(selectionChangedListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		ui.eventOptionSelected().remove(optionSelectedListener);
		ui.eventSortRequired().remove(sortListener);
		ui.eventNavigateToRequired().remove(navigateToListener);
		ui.eventSelectionChanged().remove(selectionChangedListener);
		super.teardown();
	}
	
	private NonGenericListenerCollection<List<IMethodDescription>> selectionChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<List<IMethodDescription>> eventSelectionChanged() {
		return selectionChanged;
	}
	
	private IListener<Table> selectionChangedListener = event -> {
		List<IMethodDescription> selection = new ArrayList<>();
		for (var item : event.getSelection()) {
			selection.add((IMethodDescription)item.getData());
		}
		selectionChanged.invoke(selection);
	};

	private NonGenericListenerCollection<IUserFeedback> optionSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IUserFeedback> eventOptionSelected() {
		return optionSelected;

	}

	private IListener<IUserFeedback> optionSelectedListener = optionSelected::invoke;

	private NonGenericListenerCollection<SortingArg> sortRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<SortingArg> eventSortRequired() {
		return sortRequired;
	}
	
	private IListener<SortingArg> sortListener = sortRequired::invoke;
	
	private NonGenericListenerCollection<ICodeChunkLocation> navigateToRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<ICodeChunkLocation> eventNavigateToRequired() {
		return navigateToRequired;
	}
	
	private IListener<ICodeChunkLocation> navigateToListener = navigateToRequired::invoke;
	
	public void highlight(List<MethodIdentity> context) {
		ui.highlight(context);
	}
}