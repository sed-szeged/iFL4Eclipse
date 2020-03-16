package org.eclipse.sed.ifl.view;


import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.dialogs.ContextBasedOptionCreatorDialog;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.Display;

public class ContextBasedOptionCreatorView extends View implements IEmbedee {

	private ContextBasedOptionCreatorDialog dialog = new ContextBasedOptionCreatorDialog(Display.getCurrent().getActiveShell());
	
	
	public ContextBasedOptionCreatorDialog getDialog() {
		return dialog;
	}

	public void display() {
		dialog.eventCustomFeedbackSelected().add(customOptionDialogListener);
		dialog.open();
	}

	@Override
	public void embed(IEmbeddable embedded) {
		dialog.embed(embedded);
	}
	
	private NonGenericListenerCollection<Boolean> customOptionDialog = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventCustomOptionDialog() {
		return customOptionDialog;
	}

	private IListener<Boolean> customOptionDialogListener = customOptionDialog::invoke;
}
