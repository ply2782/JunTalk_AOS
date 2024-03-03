package com.cross.juntalk2.fifth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityMapBinding;
import com.cross.juntalk2.model.KakaoMapClassUtis;
import com.cross.juntalk2.retrofit.KakaoRetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.snackbar.Snackbar;

import net.daum.android.map.MapEngineManager;
import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.n.api.internal.NativePOIItemMarkerManager;
import net.daum.mf.map.task.MapTaskManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends CreateNewCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {

    private ActivityMapBinding binding;
    private LocationRecyclerViewAdapter adapter;
    private RetrofitService service;
    private String restApi = null;
    private Handler handler;
    private net.daum.mf.map.api.MapView mapView;
    private ViewGroup mapViewContainer;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private MapPOIItem marker;
    private int page;
    private String location;


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        androidx.appcompat.app.AlertDialog.Builder cautionAlert = new androidx.appcompat.app.AlertDialog.Builder(context);
        View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_alert_choose_location, null, false);
        cautionAlert.setView(cautionView);
        Button okButton = cautionView.findViewById(R.id.okButton);
        okButton.setText("확인");
        Button cancelButton = cautionView.findViewById(R.id.cancelButton);
        androidx.appcompat.app.AlertDialog alertDialog = cautionAlert.create();
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = getIntent();
                intent.putExtra("placeInfo", (KakaoMapClassUtis.Place) mapPOIItem.getUserObject());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    // CalloutBalloonAdapter 인터페이스 구현
    private class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_map_ballon_item, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            KakaoMapClassUtis.Place place = (KakaoMapClassUtis.Place) poiItem.getUserObject();
            ImageView imageView = mCalloutBalloon.findViewById(R.id.badge);
            TextView textView = mCalloutBalloon.findViewById(R.id.title);
            TextView subTextView = mCalloutBalloon.findViewById(R.id.desc);
            if (place != null) {
                Log.e("abc", place.toString());
                imageView.setImageResource(R.drawable.ic_launcher);
                textView.setText(place.place_name);
                subTextView.setText(place.address_name);
            } else {
                imageView.setImageResource(R.drawable.ic_launcher);
                textView.setText(poiItem.getItemName());
                subTextView.setText("Custom CalloutBalloon");
            }
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    public interface ClickLocation {
        void location(KakaoMapClassUtis.Place place);
    }

    private ClickLocation clickLocation;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void getInterfaceInfo() {
        if (service == null) {
            service = KakaoRetrofitClient.getInstance().getServerInterface();
        }
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Precise location access granted.
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Only approximate location access granted.
                    } else {
                        // No location access granted.
                    }
                }
        );
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    @Override
    public void getIntentInfo() {
        if (context instanceof ClickLocation) {
            clickLocation = (ClickLocation) context;
        }
        clickLocation = new ClickLocation() {
            @Override
            public void location(KakaoMapClassUtis.Place place) {
                changeMapPOIItem(place);
            }
        };
    }

    @Override
    public void init() {
        page = 1;
        handler = new Handler(Looper.getMainLooper());
        binding = DataBindingUtil.setContentView(MapActivity.this, R.layout.activity_map);
        adapter = new LocationRecyclerViewAdapter(context, clickLocation);
        binding.locationRecyclerView.setAdapter(adapter);
        restApi = getString(R.string.kakaoRestApiKey);
        mapView = new net.daum.mf.map.api.MapView(context);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.mapView);
        mapViewContainer.addView(mapView);
        mapView.setZoomLevel(7, true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);
        try {
            defaultMapPOIItem();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void changeMapPOIItem(KakaoMapClassUtis.Place place) {
        marker.setItemName(place.place_name);
        marker.setUserObject(place);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(place.y), Double.parseDouble(place.x)));
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(place.y), Double.parseDouble(place.x)), true);
        mapView.selectPOIItem(marker, true);
    }

    private void defaultMapPOIItem() throws Exception {
        marker = new MapPOIItem();
        marker.setTag(0);
        marker.setItemName("기본 장소");
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633));
        mapView.addPOIItem(marker);
    }

    @Override
    public void createThings() {

    }

    @Override
    public void clickEvent() {

        binding.beforePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                if (page < 1) {
                    page = 1;
                }
                searchKeyWord(location, page);
            }
        });

        binding.nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                if (page > 45) {
                    page = 45;
                }
                searchKeyWord(location, page);
            }
        });

        binding.locationSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                location = binding.locationTextInputEditTextView.getText().toString();
                searchKeyWord(location, page);
            }
        });

    }

    @Override
    public void getServer() {

    }

    public void searchKeyWord(String keyword, int page) {
        service.getSearchKeyword(restApi, keyword, page).enqueue(new Callback<KakaoMapClassUtis.ResultSearchKeyword>() {
            @Override
            public void onResponse(Call<KakaoMapClassUtis.ResultSearchKeyword> call, Response<KakaoMapClassUtis.ResultSearchKeyword> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setItems(response.body().documents);
                            adapter.notifyDataSetChanged();
                            KakaoMapClassUtis.PlaceMeta info = response.body().meta;
                            binding.sumTextView.setText("총 검색건수 : " + info.pageable_count);
                            if (info.is_end == false) {
                                binding.nextPage.setVisibility(View.VISIBLE);
                            } else {
                                binding.nextPage.setVisibility(View.GONE);
                            }

                            if (page == 1) {
                                binding.beforePage.setVisibility(View.GONE);
                            } else {
                                binding.beforePage.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<KakaoMapClassUtis.ResultSearchKeyword> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }
}