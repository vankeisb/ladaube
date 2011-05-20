package com.ladaube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class MyAdapterBase<T> extends BaseAdapter {

    private List<T> data = Collections.emptyList();
    private LayoutInflater inflater;

    public MyAdapterBase(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = Collections.unmodifiableList(data);
    }

    public int getCount() {
        return data.size();
    }

    public T getItem(int i) {
        return data.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView==null) {
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        TextView tv = (TextView)convertView;
        tv.setText(getText(getItem(i)));
        return tv;
    }

    protected String getText(T obj) {
        return obj.toString();
    }
}
