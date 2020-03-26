package org.eclipse.sed.ifl.model.user.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.eclipse.sed.ifl.ide.gui.icon.OptionKind;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.items.IMethodDescriptionCollectionUtil;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class CustomOption extends Option {

	private Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateSelected;
	private Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateContext;
	private Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateOther;

	public CustomOption(String id, String title, String description, OptionKind kind,
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateSelected,
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateContext,
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateOthers) {
		super(id, title, description, kind);
		this.setUpdateFunctions(updateSelected, updateContext, updateOthers);
	}

	private void setUpdateFunctions(Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateSelected,
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateContext,
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateOthers) {
		this.updateSelected = updateSelected;
		this.updateContext = updateContext;
		this.updateOther = updateOthers;
	}

	private Map<IMethodDescription, Defineable<Double>> applyAll(Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> function, Map<IMethodDescription, Defineable<Double>> items) {
		Map<IMethodDescription, Defineable<Double>> result = new HashMap<>();
		for (Entry<IMethodDescription, Defineable<Double>> item : items.entrySet()) {
			result.put(item.getKey(), function.apply(item));
		}
		return result;
	}
	
	@Override
	public Map<IMethodDescription, Defineable<Double>> apply(IUserFeedback feedback, Map<IMethodDescription, Defineable<Double>> allScores) {
		Map<IMethodDescription, Defineable<Double>> newScores = new HashMap<>();
		Map<IMethodDescription, Defineable<Double>> selected = null;
		Map<IMethodDescription, Defineable<Double>> context = null;
		Map<IMethodDescription, Defineable<Double>> other = null;
		if (updateSelected != null) {
			selected = ((CustomUserFeedback) (feedback)).getSubjectMap();
			newScores.putAll(applyAll(updateSelected, selected));
		}
		if (updateContext != null) {
			context = IMethodDescriptionCollectionUtil.collectContext(((CustomUserFeedback)(feedback)).getSubjectMap(), allScores);
			newScores.putAll(applyAll(updateContext, context));
		}
		if (updateOther != null) {
			if (selected == null) {
				selected = ((CustomUserFeedback)(feedback)).getSubjectMap();
			}
			if (context == null) {
				context = IMethodDescriptionCollectionUtil.collectContext(((CustomUserFeedback)(feedback)).getSubjectMap(), allScores);
			}
			other = IMethodDescriptionCollectionUtil.collectOther(allScores, selected, context);
			newScores.putAll(applyAll(updateOther, other));
		}
		return newScores;
	}
	
}
