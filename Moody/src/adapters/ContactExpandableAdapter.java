package adapters;

import java.util.ArrayList;

import restPackage.MoodleContact;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.moody.R;

/**
 * 
 * @author Code Geeks Team
 * 
 * @contributor sfbramos (MoodyProject Team)
 * 
 */
public class ContactExpandableAdapter extends BaseExpandableListAdapter {

	private Activity innerActivity;
	private LayoutInflater innerInflater;
	private OnClickListener clickEvent;
	private ArrayList<Object> childtems;
	private ArrayList<String> parentItems;

	private int groupLayoutID;
	private int rowLayoutID;

	public ContactExpandableAdapter(ArrayList<String> parents,
			ArrayList<Object> children) {

		setParentItems(parents);
		setChildtems(children);

	}

	public void setInflater(LayoutInflater inflater, Activity activity,
			int groupLayout, int rowLayout) {

		setInnerInflater(inflater);
		setInnerActivity(activity);
		setGroupLayoutID(groupLayout);
		setRowLayoutID(rowLayout);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return ((ArrayList<Object>) childtems.get(groupPosition))
				.get(childPosition);
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return ((MoodleContact) ((ArrayList<Object>) childtems
				.get(groupPosition)).get(childPosition)).getContactProfile()
				.getId();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<Object>) childtems.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return parentItems.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return parentItems.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final ArrayList<MoodleContact> childs = (ArrayList<MoodleContact>) getChildtems()
				.get(groupPosition);

		if (convertView == null)
			convertView = innerInflater.inflate(getGroupLayoutID(), null);

		((TextView) convertView.findViewById(R.id.textView1))
				.setText(((MoodleContact) childs.get(childPosition))
						.getContactProfile().getFullname());

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Toast.makeText(
						getInnerActivity(),
						((MoodleContact) childs.get(childPosition))
								.getContactProfile().getFullname(),
						Toast.LENGTH_SHORT).show();
			}

		});

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if (convertView == null)
			convertView = getInnerInflater().inflate(getRowLayoutID(), null);

		((CheckedTextView) convertView).setText(getParentItems().get(
				groupPosition));
		((CheckedTextView) convertView).setChecked(isExpanded);

		return convertView;
	}

	/**
	 * @return the innerActivity
	 */
	public Activity getInnerActivity() {
		return innerActivity;
	}

	/**
	 * @return the innerInflater
	 */
	public LayoutInflater getInnerInflater() {
		return innerInflater;
	}

	/**
	 * @return the clickEvent
	 */
	public OnClickListener getClickEvent() {
		return clickEvent;
	}

	/**
	 * @return the childtems
	 */
	public ArrayList<Object> getChildtems() {
		return childtems;
	}

	/**
	 * @return the groupLayoutID
	 */
	public int getGroupLayoutID() {
		return groupLayoutID;
	}

	/**
	 * @return the rowLayoutID
	 */
	public int getRowLayoutID() {
		return rowLayoutID;
	}

	/**
	 * @return the parentItems
	 */
	public ArrayList<String> getParentItems() {
		return parentItems;
	}

	/**
	 * @param innerActivity
	 *            the innerActivity to set
	 */
	public void setInnerActivity(Activity innerActivity) {
		this.innerActivity = innerActivity;
	}

	/**
	 * @param innerInflater
	 *            the innerInflater to set
	 */
	public void setInnerInflater(LayoutInflater innerInflater) {
		this.innerInflater = innerInflater;
	}

	/**
	 * @param clickEvent
	 *            the clickEvent to set
	 */
	public void setClickEvent(OnClickListener clickEvent) {
		this.clickEvent = clickEvent;
	}

	/**
	 * @param childtems
	 *            the childtems to set
	 */
	public void setChildtems(ArrayList<Object> childtems) {
		this.childtems = childtems;
	}

	/**
	 * @param parentItems
	 *            the parentItems to set
	 */
	public void setParentItems(ArrayList<String> parentItems) {
		this.parentItems = parentItems;
	}

	/**
	 * @param groupLayoutID
	 *            the groupLayoutID to set
	 */
	public void setGroupLayoutID(int groupLayoutID) {
		this.groupLayoutID = groupLayoutID;
	}

	/**
	 * @param rowLayoutID
	 *            the rowLayoutID to set
	 */
	public void setRowLayoutID(int rowLayoutID) {
		this.rowLayoutID = rowLayoutID;
	}
}