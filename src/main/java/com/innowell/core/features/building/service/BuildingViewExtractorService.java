package com.innowell.core.features.building.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BuildingViewExtractorService {

    public static String getOuterBoundary(String geoJson) throws IOException {
        return convertGeometryToGeoJson(extractOuterBoundary(geoJson));
    }

    public static Geometry extractOuterBoundary(String geoJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GeometryFactory gf = new GeometryFactory();

        // Parse GeoJSON
        FeatureCollection featureCollection = mapper.readValue(geoJson, FeatureCollection.class);

        // Create a list of JTS Polygon objects
        List<Polygon> polygons = new ArrayList<>();
        for (Feature feature : featureCollection.getFeatures()) {
            Coordinate[] coordinates = feature.getGeometry().getCoordinates();
            LinearRing outerRing = gf.createLinearRing(coordinates);
            Polygon polygon = gf.createPolygon(outerRing, null); // Assuming no holes
            polygons.add(polygon);
        }

        // Union all polygons
        Geometry union = polygons.get(0);
        for (int i = 1; i < polygons.size(); i++) {
            union = union.union(polygons.get(i));
        }

        // Extract outer boundary
        if (union instanceof MultiPolygon) {
            return union.getGeometryN(0);
        } else {
            return union;
        }
    }

    public static String convertGeometryToGeoJson(Geometry geometry) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(geometry);
    }

    // Class representing a GeoJSON FeatureCollection
    public static class FeatureCollection {
        private List<Feature> features;

        public List<Feature> getFeatures() {
            return features;
        }

        public void setFeatures(List<Feature> features) {
            this.features = features;
        }
    }

    // Class representing a GeoJSON Feature
    public static class Feature {
        private Geometry geometry;

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
    }
}



