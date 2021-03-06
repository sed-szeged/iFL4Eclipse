package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.source.IMethodDescription;

public class ContextSizeFilter extends ScoreFilter {

	public ContextSizeFilter(Boolean enabled) {
		super(enabled);
	}

	private Integer limit = 0;
	
	private String relation = "=";
	
	public void setLimit(Integer value) {
		limit = value;
	}
	
	public void setRelation(String text) {
		relation = text;
	}
	
	@Override
	protected boolean check(Entry<IMethodDescription, Score> arg0) {
		boolean rValue = true;
		switch (relation) {
		case ">":
			rValue =  arg0.getKey().getContext().size() > limit;
			break;
		case ">=":
			rValue = arg0.getKey().getContext().size() >= limit;
			break;
		case "=":
			rValue = arg0.getKey().getContext().size() == limit;
			break;
		case "<=":
			rValue = arg0.getKey().getContext().size() <= limit;
			break;
		case "<":
			rValue = arg0.getKey().getContext().size() < limit;
			break;
		}
		return rValue;
	}

}
