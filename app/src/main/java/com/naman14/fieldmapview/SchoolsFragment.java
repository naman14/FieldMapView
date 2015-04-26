package com.naman14.fieldmapview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naman on 22/12/14.
 */
public class SchoolsFragment extends Fragment {

    private GoogleMap mMap;
    private int resultCode;
    private RecyclerView mRecyclerView;
    SchoolAdapter myAdapter;
    Spinner spinner;
    View myView;


    ArrayList<ItemData> list = new ArrayList<ItemData>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View v = inflater.inflate(R.layout.fragment_maps, container, false);

        spinner=(Spinner)getActivity().findViewById(R.id.spinner_nav);
        spinner.setVisibility(View.GONE);
        mRecyclerView=(RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        mRecyclerView.setHasFixedSize(true);
        fetchData();

        resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if(resultCode != ConnectionResult.SUCCESS)
        {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), 69);
            dialog.setCancelable(true);

            dialog.show();
        }


        return v;
    }

    public void fetchData(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                "SchoolNames");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                doneFetching(parseObjects);
            }
        });
    }


    public void doneFetching(List<ParseObject> objects) {


        for (ParseObject item : objects) {

            ItemData itemData = new ItemData();
            itemData.title= item.getString("SCHOOL_NAME");
            itemData.imageUrl=   R.color.colorAccent;
            itemData.block= item.getString("BLOCK_NAME");
            itemData.village= item.getString("VILLAGE_NAME");
            try {
                itemData.latlong = "" + item.getParseGeoPoint("lat").getLatitude() + "," + item.getParseGeoPoint("lat").getLongitude();
                addToMap(itemData.getLatlong().toString(), itemData.getTitle());
            }
           catch (NullPointerException e){

           }

            list.add(itemData);

        }

        myAdapter=new SchoolAdapter(getActivity(),list);

        mRecyclerView.setAdapter(myAdapter);


    }


    public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.ViewHolder> {
        ArrayList<ItemData> itemsData;
        private Context context;

        private int lastPosition = -1;

        public SchoolAdapter(Context context ,  ArrayList<ItemData> itemsData) {
            this.itemsData = itemsData;
            this.context =context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view, null);
            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData
            setAnimation(viewHolder.itemView,position);

            viewHolder.txtViewTitle.setText(itemsData.get(position).getTitle());
            viewHolder.imgViewIcon.setImageResource(itemsData.get(position).getImageUrl());
            viewHolder.block.setText(itemsData.get(position).getBlock());
            viewHolder.village.setText(itemsData.get(position).getVillage());

            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }

        // inner class to hold a reference to each item of RecyclerView
        public  class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txtViewTitle,block,village;
            public ImageView imgViewIcon;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                myView=itemLayoutView;
                txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
                imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
                block=(TextView) itemLayoutView.findViewById(R.id.item_block);
                village=(TextView) itemLayoutView.findViewById(R.id.item_village);

            }
        }


        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return itemsData.size();
        }

        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }
    }


    private void addToMap(String latlong,String title){
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();


        MarkerOptions markerOptions;
        LatLng position;
        String lati=latlong.substring(0,latlong.indexOf(",")),longi=latlong.substring(latlong.indexOf(",")+1,latlong.length());

        markerOptions = new MarkerOptions();


        position = new LatLng(Double.parseDouble(lati), Double.parseDouble(longi));
        markerOptions.position(position);
        markerOptions.title(title);
        mMap.addMarker(markerOptions);

        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(position, 6.0f);


        mMap.animateCamera(cameraPosition);

    }
}
