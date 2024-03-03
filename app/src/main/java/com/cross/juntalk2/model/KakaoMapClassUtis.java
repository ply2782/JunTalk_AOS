package com.cross.juntalk2.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class KakaoMapClassUtis {

    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ResultSearchKeyword implements Serializable {
        public PlaceMeta meta;
        public List<Place> documents;

    }

    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class PlaceMeta implements Serializable {
        public int total_count;
        public int pageable_count;
        public boolean is_end;
        public RegionInfo same_name;

    }

    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class RegionInfo implements Serializable {
        public List<String> region;
        public String keyword;
        public String selected_region;
    }

    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Place implements Serializable {
        public String id;
        public String place_name;
        public String category_name;
        public String category_group_code;
        public String category_group_name;
        public String phone;
        public String address_name;
        public String road_address_name;
        public String x;
        public String y;
        public String place_url;
        public String distance;

        public JSONObject placeJsonObject() {
            try {
                JSONObject object = new JSONObject();
                object.put("id", id);
                object.put("place_name", place_name);
                object.put("category_name", category_name);
                object.put("phone", phone);
                object.put("address_name", address_name);
                object.put("x", x);
                object.put("y", y);
                return object;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
