package com.sarahdev.mymapapplication;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sarahdev.mymapapplication.model.Calculs;

import static com.sarahdev.mymapapplication.Map_MainActivity.VELODYSSEE;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private List<Integer> listDeletedHeader = new ArrayList<>();
    private HashMap<String, List<String>> listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (context instanceof DisplayFilesInfo_Activity) {
            //((DisplayFilesInfo_Activity) context).isCheckedFile(groupPosition, isExpanded);
            ((DisplayFilesInfo_Activity) context).setChanges(groupPosition, isExpanded, DisplayFilesInfo_Activity.TAG_DISPLAY);
        }
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_group, null);
        }
        TextView tvListHeader = convertView.findViewById(R.id.lblListHeader);

        tvListHeader.setTypeface(null, Typeface.BOLD);
        tvListHeader.setText(headerTitle);
        if (isExpanded) {
            convertView.setBackgroundResource(R.color.blue);
            tvListHeader.setTextColor(context.getResources().getColor(R.color.green_yellow));
        } else {
            convertView.setBackgroundResource(R.color.listview_light_blue);
            tvListHeader.setTextColor(context.getResources().getColor(R.color.black));
        }
        if (listDeletedHeader.contains(groupPosition))
            tvListHeader.setTextColor(context.getResources().getColor(R.color.gray));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String childText = (String) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item, null);
            }
            TextView txtListChild = convertView.findViewById(R.id.lblListItem);
            Button button = convertView.findViewById(R.id.button);
            if (childPosition < 3 || groupPosition==0) {
                if (childText.equals(""))
                    txtListChild.setVisibility(View.GONE);
                else {
                    txtListChild.setVisibility(View.VISIBLE);
                    if (listDeletedHeader.contains(groupPosition))
                        txtListChild.setTextColor(context.getResources().getColor(R.color.gray));
                    else
                        txtListChild.setTextColor(context.getResources().getColor(R.color.black));
                    setChildText(txtListChild, childPosition, childText);
                }
                button.setVisibility(View.GONE);
            } else {
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("SUPPRIMER UN ITINERAIRE");
                        alertDialog.setMessage("Cet itinéraire sera définitivement effacé ...");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "D'ACCORD",
                                (dialog, which) ->
                                {
                                    ((DisplayFilesInfo_Activity) context).setChanges(groupPosition, true, DisplayFilesInfo_Activity.TAG_DELETE);
                                   // ((DisplayFilesInfo_Activity) context).toDeleteFile(groupPosition, true);
                                    listDeletedHeader.add(groupPosition);
                                    dialog.dismiss();
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Annuler",
                                (dialog, which) -> {
                                    ((DisplayFilesInfo_Activity) context).setChanges(groupPosition, false, DisplayFilesInfo_Activity.TAG_DELETE);
                                   // ((DisplayFilesInfo_Activity) context).toDeleteFile(groupPosition, false);
                                    listDeletedHeader.remove((Object) groupPosition);
                                    dialog.dismiss();
                                });
                        alertDialog.show();
                    }
                });

                txtListChild.setVisibility(View.GONE);
            }
        return convertView;
    }

    private void setChildText(TextView txtListChild, int childPosition, String childText) {
        if (childText.isEmpty())
            txtListChild.setText("");
        else {
            switch (childPosition) {
                case 1:
                    if (childText.isEmpty())
                        txtListChild.setText("");
                    txtListChild.setText("vitesse moyenne : " + childText + " km/h");
                    break;
                case 2:
                    String unit = " m";
                    try {
                        double distance = Double.valueOf(childText);
                        if (distance > 1000) {
                            childText = String.valueOf(Calculs.getOneDecimalFloat(distance / 1000));
                            unit = " km";
                        }
                    } catch (NumberFormatException e) {
                    }
                    txtListChild.setText("distance parcourue : " + childText + unit);
                    break;
                default:
                    txtListChild.setText(childText);
                    break;
            }
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        String filename = this.listDataHeader.get(groupPosition);
        return getDateFromFilename(filename);
    }

    private String getDateFromFilename(String filename) {
        if  (filename != null && !filename.equals(VELODYSSEE) && filename.length() > 13)
            return filename.substring(6, 8) + " / " + filename.substring(4, 6) + " / " + filename.substring(2, 4) + "  _  " + filename.substring(9, 11) + " h " + filename.substring(11, 13);
        return filename;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
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
        return true;
    }
}