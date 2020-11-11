package org.eclipse.sed.ifl.ide.gui;

import java.util.ArrayList;
import java.util.List;
import javax.inject.*;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.sed.ifl.control.ItemMoveObject;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

enum SelectionLocation {
	LEFT, RIGHT, UNSELECTED
}

public class DualListPart<TItem extends SortingArg> extends ViewPart implements IEmbeddable, IEmbedee {
	// generic extends: generic constraints ut�nan�z�s.
	// interface f�l�falaz�sa
	public static final String ID = "org.eclipse.sed.ifl.views.IFLDualListView";

	@Inject
	IWorkbench workbench;

	private Composite composite;
	private static Boolean orderingEnabled = false;
	private boolean elementDescending;
	private SortingArg selectedArgument;
	private SortingArg swapElement;
	private SortingArg toggleElement;
	private int elementIndex;
	private int newIndex;
	private ItemMoveObject<TItem> moveObject;
	private List<String> enumNames;

	private SelectionLocation whichList;
	private String elementName;

	@FunctionalInterface
	public interface HumanReadable<TItem> {
		String getAsString(TItem t);
	}

	private Button allRight;
	private Button allUp;
	private Button oneRight;
	private Button oneUp;
	private Button oneLeft;
	private Button oneDown;
	private Button allLeft;
	private Button allDown;
	private Button currentButton;
	private GridLayout gridLayout;
	private Image allRightImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_all_right.png");
	private Image allLeftImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_all_left.png");
	private Image allUpImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_to_top.png");
	private Image allDownImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_to_bottom.png");
	private Image oneRightImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_one_right.png");
	private Image oneLeftImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_one_left.png");
	private Image oneUpImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_up.png");
	private Image oneDownImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_down.png");
	private Image ascendImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/ascend.png");
	private Image descendImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/descend.png");
	private Label infoLabel;
	private Table attributeTable;
	private Table sortingTable;
	private TableViewer attributeViewer;
	private TableViewer sortingViewer;

	public DualListPart() {
		System.out.println("dual list part ctr");

	}

	private void addUIElements(Composite parent) {
		elementDescending = false;
		selectedArgument = null;
		swapElement = null;
		toggleElement = null;
		elementIndex = 0;
		newIndex = 0;
		whichList = SelectionLocation.UNSELECTED;
		elementName = "";

		GridData buttonData = new GridData();
		buttonData.horizontalAlignment = SWT.CENTER;
		buttonData.verticalAlignment = SWT.CENTER;

		GridData labelData = new GridData();
		labelData.horizontalAlignment = SWT.CENTER;
		labelData.verticalAlignment = SWT.TOP;

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 1;
		gridData.verticalSpan = 6;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.heightHint = 70;

		infoLabel = new Label(parent, SWT.NONE);
		infoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		infoLabel.setText("Load some defined scores to enable ordering.");

		new Label(parent, SWT.NONE).setText("");
		new Label(parent, SWT.NONE).setText("");
		new Label(parent, SWT.NONE).setText("");
		new Label(parent, SWT.NONE).setText("");
		new Label(parent, SWT.NONE).setText("");

		attributeViewer = new TableViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		attributeViewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn columnLeftName = new TableViewerColumn(attributeViewer, SWT.CENTER);
		columnLeftName.getColumn().setWidth(200);
		columnLeftName.getColumn().setText("Attribute");
		columnLeftName.getColumn().setResizable(true);
		columnLeftName.getColumn().setMoveable(true);
		columnLeftName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SortingArg sortingElement = (SortingArg) element;
				return sortingElement.getDomain();
			}
		});
		attributeViewer.getControl().setLayoutData(gridData);

		attributeTable = attributeViewer.getTable();
		attributeTable.setHeaderVisible(false);
		attributeTable.setLinesVisible(false);
		attributeTable.setVisible(false);
		attributeTable.setEnabled(false);

		new Label(parent, SWT.NONE).setText("");

		sortingViewer = new TableViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		sortingViewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn columnRightName = new TableViewerColumn(sortingViewer, SWT.CENTER);
		TableViewerColumn columnRightButton = new TableViewerColumn(sortingViewer, SWT.CENTER);
		columnRightName.getColumn().setWidth(200);
		columnRightName.getColumn().setText("Attribute");
		columnRightName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SortingArg sortingElement = (SortingArg) element;
				return sortingElement.getDomain();
			}
		});
		columnRightButton.getColumn().setWidth(200);
		columnRightButton.getColumn().setText("Ordering");
		columnRightButton.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {

				TableItem item = (TableItem) cell.getItem();
				toggleElement = (SortingArg) item.getData();
				;
				currentButton = new Button((Composite) cell.getViewerRow().getControl(), SWT.TOGGLE);
				if (!toggleElement.isDescending()) {
					currentButton.setImage(ascendImage);
					currentButton.removeListener(SWT.Selection, ascendImageSetListener);
					currentButton.addListener(SWT.Selection, descendImageSetListener);
				} else {
					currentButton.setImage(descendImage);
					currentButton.removeListener(SWT.Selection, descendImageSetListener);
					currentButton.addListener(SWT.Selection, ascendImageSetListener);
				}
				currentButton.addListener(SWT.TOGGLE, new Listener() {
					@Override
					public void handleEvent(Event event) {
						DualListPart.this.toggleElement.setDescending(!DualListPart.this.toggleElement.isDescending());
						orderingDirectionChangedListener.invoke(DualListPart.this.toggleElement);
					}
				});
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				editor.setEditor(currentButton, item, cell.getColumnIndex());
				editor.layout();
			}

		});
		sortingViewer.getControl().setLayoutData(gridData);

		sortingTable = sortingViewer.getTable();
		sortingTable.setHeaderVisible(false);
		sortingTable.setLinesVisible(false);
		sortingTable.setVisible(false);
		sortingTable.setEnabled(false);

		new Label(parent, SWT.NONE).setText("");

		allRight = new Button(parent, SWT.PUSH);
		allRight.setImage(allRightImage);
		allRight.setLayoutData(buttonData);
		allRight.setSize(40, 40);
		allRight.setVisible(false);
		allRight.setEnabled(false);

		allUp = new Button(parent, SWT.PUSH);
		allUp.setImage(allUpImage);
		allUp.setLayoutData(buttonData);
		allUp.setSize(40, 40);
		allUp.setVisible(false);
		allUp.setEnabled(false);

		oneRight = new Button(parent, SWT.PUSH);
		oneRight.setImage(oneRightImage);
		oneRight.setLayoutData(buttonData);
		oneRight.setSize(40, 40);
		oneRight.setVisible(false);
		oneRight.setEnabled(false);

		oneUp = new Button(parent, SWT.PUSH);
		oneUp.setImage(oneUpImage);
		oneUp.setLayoutData(buttonData);
		oneUp.setSize(40, 40);
		oneUp.setVisible(false);
		oneUp.setEnabled(false);

		oneLeft = new Button(parent, SWT.PUSH);
		oneLeft.setImage(oneLeftImage);
		oneLeft.setLayoutData(buttonData);
		oneLeft.setSize(40, 40);
		oneLeft.setVisible(false);
		oneLeft.setEnabled(false);

		oneDown = new Button(parent, SWT.PUSH);
		oneDown.setImage(oneDownImage);
		oneDown.setLayoutData(buttonData);
		oneDown.setSize(40, 40);
		oneDown.setVisible(false);
		oneDown.setEnabled(false);

		allLeft = new Button(parent, SWT.PUSH);
		allLeft.setImage(allLeftImage);
		allLeft.setLayoutData(buttonData);
		allLeft.setSize(40, 40);
		allLeft.setVisible(false);
		allLeft.setEnabled(false);

		allDown = new Button(parent, SWT.PUSH);
		allDown.setImage(allDownImage);
		allDown.setLayoutData(buttonData);
		allDown.setSize(40, 40);
		allDown.setVisible(false);
		allDown.setEnabled(false);

		new Label(parent, SWT.NONE).setText("");

		new Label(parent, SWT.NONE).setText("");

		new Label(parent, SWT.NONE).setText("");

		new Label(parent, SWT.NONE).setText("");

	}

	@Override
	public void embed(IEmbeddable embedded) {
		embedded.setParent(composite);
	}

	@Override
	public void setParent(Composite parent) {
		composite.setParent(parent);
	}

	public String getArrayElementbyIndex(Table source, int extractIndex, HumanReadable<TItem> function) {
		TableItem extractedItem = source.getItem(extractIndex);
		SortingArg argument = (SortingArg) extractedItem.getData();
		return argument.getDomain();
	}

	public String getInfoLabel() {
		return infoLabel.getText();
	}

	public void setInfoLabel(String labelText) {
		this.infoLabel.setText(labelText);
	}

	public Image getAllRightImage() {
		return allRight.getImage();
	}

	public void setAllRightImage(Image buttonImage) {
		this.allRight.setImage(buttonImage);
	}

	public Image getAllUpImage() {
		return allUp.getImage();
	}

	public void setAllUpImage(Image buttonImage) {
		this.allUp.setImage(buttonImage);
	}

	public Image getOneRightImage() {
		return oneRight.getImage();
	}

	public void setOneRightImage(Image buttonImage) {
		this.oneRight.setImage(buttonImage);
	}

	public Image getOneUpImage() {
		return oneUp.getImage();
	}

	public void setOneUpImage(Image buttonImage) {
		this.oneUp.setImage(buttonImage);
	}

	public Image getOneLeftImage() {
		return oneLeft.getImage();
	}

	public void setOneLeftImage(Image buttonImage) {
		this.oneLeft.setImage(buttonImage);
	}

	public Image getOneDownImage() {
		return oneDown.getImage();
	}

	public void setOneDownImage(Image buttonImage) {
		this.oneDown.setImage(buttonImage);
	}

	public Image getAllLeftImage() {
		return allLeft.getImage();
	}

	public void setAllLeftImage(Image buttonImage) {
		this.allLeft.setImage(buttonImage);
	}

	public Image getAllDownImage() {
		return allDown.getImage();
	}

	public void setAllDownImage(Image buttonImage) {
		this.allDown.setImage(buttonImage);
	}

	public ArrayList<SortingArg> getSortingTable() {
		TableItem tableItems[] = this.sortingTable.getItems();
		ArrayList<SortingArg> sortingList = new ArrayList<SortingArg>();
		for (TableItem item : tableItems) {
			SortingArg argument = (SortingArg) item.getData();
			sortingList.add(argument);
		}
		return sortingList;
	}

	public void setSortingTable(List<SortingArg> sortingList) {
		sortingViewer.setInput(sortingList);
		sortingViewer.refresh();
	}

	public ArrayList<SortingArg> getAttributeTable() {
		TableItem tableItems[] = this.attributeTable.getItems();
		ArrayList<SortingArg> attributeList = new ArrayList<SortingArg>();
		for (TableItem item : tableItems) {
			SortingArg argument = (SortingArg) item.getData();
			attributeList.add(argument);
		}
		return attributeList;
	}

	public void setAttributeTable(List<SortingArg> argumentList) {
		attributeViewer.setInput(argumentList);
		attributeViewer.refresh();
	}

	public ItemMoveObject<TItem> moveInside(Table source, int elementIndex, Widget selectedButton) {
		int length = source.getItemCount();
		selectedArgument = (SortingArg) source.getItem(elementIndex).getData();

		if (selectedButton.equals(allUp))
			newIndex = 0;
		else if (selectedButton.equals(allDown))
			newIndex = length - 1;
		else if (selectedButton.equals(oneUp))
			newIndex = elementIndex - 1;
		else if (selectedButton.equals(oneDown))
			newIndex = elementIndex + 1;

		ItemMoveObject<TItem> itemMoveObject;
		itemMoveObject = new ItemMoveObject<TItem>(source, source, selectedArgument, -1, newIndex);
		return itemMoveObject;
	}

	public void refreshSelectionBetweenOne(Table source, Table destination) {
		source.setSelection(-1);
		int newSelection = destination.getItemCount() - 1;
		destination.setSelection(newSelection);
		this.moveObject.setDestinationIndex(newSelection);
	}

	public class moveBetweenListsListener implements Listener {
		@Override
		public void handleEvent(Event event) {

			if (event.widget.equals(allRight)) {
				moveObject = new ItemMoveObject<TItem>(attributeTable, sortingTable, null, -1, -1);
				sortingListChangeRequestedListener.invoke(moveObject);
				moveObject = new ItemMoveObject<TItem>(sortingTable, attributeTable, null, -2, -1);
				attributeListChangeRequestedListener.invoke(moveObject);
				whichList = SelectionLocation.UNSELECTED;

			} else if (event.widget.equals(allLeft)) {
				moveObject = new ItemMoveObject<TItem>(sortingTable, attributeTable, null, -1, -1);
				attributeListChangeRequestedListener.invoke(moveObject);
				moveObject = new ItemMoveObject<TItem>(attributeTable, sortingTable, null, -2, -1);
				sortingListChangeRequestedListener.invoke(moveObject);
				whichList = SelectionLocation.UNSELECTED;

			} else {
				switch (whichList) {
				case UNSELECTED:
					break;
				case RIGHT:
					SortingArg argument = (SortingArg) attributeTable.getItem(elementIndex).getData();
					int tableSize = sortingTable.getItemCount();
					moveObject = new ItemMoveObject<TItem>(attributeTable, sortingTable, argument, elementIndex,
							tableSize);
					sortingListChangeRequestedListener.invoke(moveObject);
					moveObject = new ItemMoveObject<TItem>(sortingTable, attributeTable, null, -2, elementIndex);
					attributeListChangeRequestedListener.invoke(moveObject);
					whichList = SelectionLocation.RIGHT;
					elementIndex = tableSize;
					break;
				case LEFT:
					SortingArg argument1 = (SortingArg) sortingTable.getItem(elementIndex).getData();
					int tableSize1 = attributeTable.getItemCount();
					moveObject = new ItemMoveObject<TItem>(sortingTable, attributeTable, argument1, elementIndex,
							tableSize1);
					attributeListChangeRequestedListener.invoke(moveObject);
					moveObject = new ItemMoveObject<TItem>(attributeTable, sortingTable, null, elementIndex, -1);
					sortingListChangeRequestedListener.invoke(moveObject);
					whichList = SelectionLocation.LEFT;
					elementIndex = tableSize1;
					break;
				}
			}

			selectionRequested.invoke(elementIndex);
		}
	}

	public class moveInsideListListener implements Listener {

		@Override
		public void handleEvent(Event event) {
			Widget selectedButton = event.widget;

			switch (whichList) {
			case UNSELECTED:
				break;
			case LEFT: {
				moveObject = moveInside(attributeTable, elementIndex, selectedButton);
				attributeListChangeRequestedListener.invoke(moveObject);
				attributeTable.setSelection(moveObject.getDestinationIndex());
				elementIndex = newIndex;
				break;
			}
			case RIGHT:
				moveObject = moveInside(sortingTable, elementIndex, selectedButton);
				sortingListChangeRequestedListener.invoke(moveObject);
				sortingTable.setSelection(moveObject.getDestinationIndex());
				elementIndex = newIndex;
				break;
			}
			selectionRequested.invoke(moveObject.getDestinationIndex());
		}

	}

	public Listener ascendImageSetListener = new Listener() {

		@Override
		public void handleEvent(Event event) {
			DualListPart.this.currentButton.setImage(ascendImage);
			DualListPart.this.currentButton.removeListener(SWT.Selection, ascendImageSetListener);
			DualListPart.this.currentButton.addListener(SWT.Selection, descendImageSetListener);
		}

	};

	public Listener descendImageSetListener = new Listener() {

		@Override
		public void handleEvent(Event event) {
			DualListPart.this.currentButton.setImage(descendImage);
			DualListPart.this.currentButton.removeListener(SWT.Selection, descendImageSetListener);
			DualListPart.this.currentButton.addListener(SWT.Selection, ascendImageSetListener);
		}

	};

	@Override
	public void createPartControl(Composite parent) {

		gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.makeColumnsEqualWidth = false;
		composite = parent;
		composite.setLayout(gridLayout);
		addUIElements(parent);

		attributeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				elementIndex = attributeTable.getSelectionIndex();
				whichList = SelectionLocation.LEFT;
				sortingTable.setSelection(-1);
				selectionRequested.invoke(elementIndex);
			}
		});

		sortingViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				elementIndex = sortingTable.getSelectionIndex();
				whichList = SelectionLocation.RIGHT;
				attributeTable.setSelection(-1);
				selectionRequested.invoke(elementIndex);
			}
		});

		/*
		 * SelectionListener toggleListener = new SelectionAdapter() {
		 * 
		 * @Override public void widgetSelected(SelectionEvent e) { Button source =
		 * (Button) e.getSource(); String buttonText = source.getText(); toggleIndex =
		 * listRight.indexOf(buttonText); currentElement = (DualListElement)
		 * arrayRight.get(toggleIndex);
		 * currentElement.setDescending(!currentElement.isDescending());
		 * arrayRight.set(toggleIndex, (TItem) currentElement); refresh();
		 * 
		 * } };
		 */

		allRight.addListener(SWT.Selection, new moveBetweenListsListener());

		oneRight.addListener(SWT.Selection, new moveBetweenListsListener());

		oneLeft.addListener(SWT.Selection, new moveBetweenListsListener());

		allLeft.addListener(SWT.Selection, new moveBetweenListsListener());

		allUp.addListener(SWT.Selection, new moveInsideListListener());

		oneUp.addListener(SWT.Selection, new moveInsideListListener());

		oneDown.addListener(SWT.Selection, new moveInsideListListener());

		allDown.addListener(SWT.Selection, new moveInsideListListener());

		/*
		 * scoreToggle.addSelectionListener(toggleListener);
		 * 
		 * nameToggle.addSelectionListener(toggleListener);
		 * 
		 * signatureToggle.addSelectionListener(toggleListener);
		 * 
		 * parentTypeToggle.addSelectionListener(toggleListener);
		 * 
		 * pathToggle.addSelectionListener(toggleListener);
		 * 
		 * contextSizeToggle.addSelectionListener(toggleListener);
		 * 
		 * positionToggle.addSelectionListener(toggleListener);
		 * 
		 * interactivityToggle.addSelectionListener(toggleListener);
		 * 
		 * lastActionToggle.addSelectionListener(toggleListener);
		 */

		if (orderingEnabled.booleanValue()) {
			enableOrdering();
		}

	}

	private NonGenericListenerCollection<SortingArg> orderingDirectionChangedListener = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<SortingArg> eventOrderingDirectionChanged() {
		return orderingDirectionChangedListener;
	}

	private NonGenericListenerCollection<Integer> selectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventSelectionRequested() {
		return selectionRequested;
	}

	private NonGenericListenerCollection<ItemMoveObject<TItem>> sortingListChangeRequestedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventSortingListChangeRequested() {
		return sortingListChangeRequestedListener;
	}

	private NonGenericListenerCollection<ItemMoveObject<TItem>> attributeListChangeRequestedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventAttributeListChangeRequested() {
		return attributeListChangeRequestedListener;
	}

	public void enableOrdering() {

		infoLabel.setText("Order your scores by (multiple) attributes.");
		attributeTable.setHeaderVisible(true);
		attributeTable.setLinesVisible(true);
		attributeTable.setVisible(true);
		attributeTable.setEnabled(true);
		sortingTable.setHeaderVisible(true);
		sortingTable.setLinesVisible(true);
		sortingTable.setVisible(true);
		sortingTable.setEnabled(true);

		allRight.setVisible(true);
		allRight.setEnabled(true);
		oneRight.setVisible(true);
		oneRight.setEnabled(true);
		allLeft.setVisible(true);
		allLeft.setEnabled(true);
		oneLeft.setVisible(true);
		oneLeft.setEnabled(true);
		allUp.setVisible(true);
		allUp.setEnabled(true);
		oneUp.setVisible(true);
		oneUp.setEnabled(true);
		allDown.setVisible(true);
		allDown.setEnabled(true);
		oneDown.setVisible(true);
		oneDown.setEnabled(true);

		orderingEnabled = true;
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {

		this.getSite().getPage().hideView(this);
	}
}