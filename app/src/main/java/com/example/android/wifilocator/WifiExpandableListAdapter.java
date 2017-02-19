package com.example.android.wifilocator;

        import android.content.Context;
        import android.graphics.Typeface;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseExpandableListAdapter;
        import android.widget.TextView;

        import java.util.HashMap;
        import java.util.List;

/**
 * Created by Sherif Meimari on 2/5/2017.
 */

public class WifiExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    //Header Titles
    private List<String> regionNames;
    // child data in format of header title, child title
    private HashMap<String, List<String>> regionsMap;

    public WifiExpandableListAdapter(Context context, List<String> regionNames, HashMap<String, List<String>> regionsMap) {
        this.context = context;
        this.regionNames = regionNames;
        this.regionsMap = regionsMap;
    }

    @Override
    public Object getChild(int parent, int child){

        return this.regionsMap.get(this.regionNames.get(parent)).get(child);
    }

    @Override
    public long getChildId(int parent, int child){

        return child;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.regionsMap.get(this.regionNames.get(groupPosition)).size();
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_child_layout, null);
        }
        TextView childTextView = (TextView) convertView.findViewById(R.id.child_txt);
        childTextView.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.regionNames.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return this.regionNames.size();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_parent_layout, null);
        }
        TextView parentTextView = (TextView) convertView.findViewById(R.id.parent_txt);
        parentTextView.setTypeface(null, Typeface.BOLD);
        parentTextView.setText(groupTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return super.getCombinedGroupId(groupId);
    }

}
