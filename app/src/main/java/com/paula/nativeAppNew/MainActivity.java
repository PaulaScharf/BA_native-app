package com.paula.nativeAppNew;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.NavigationConstraint;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.BackgroundGrid;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.SpaceEffect;
import com.esri.arcgisruntime.toolkit.ar.*;

public class MainActivity extends AppCompatActivity {

    private ArcGISArView mArView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArView = findViewById(R.id.arView);
        // Note: ArLocationDataSource is a toolkit component
        mArView.setLocationDataSource(new ArLocationDataSource(this));
    }

    // TODO – request CAMERA and ACCESS_FINE_LOCATION permissions

    @Override
    protected void onPause() {
        if (mArView != null) {
            mArView.stopTracking();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Continuous update mode
        mArView.startTracking(ArcGISArView.ARLocationTrackingMode.CONTINUOUS);

        // One-time mode
        //mArView.startTracking(ArcGISArView.ARLocationTrackingMode.INITIAL);

        configureSceneForAR();
    }

    private void configureSceneForAR() {
        // Get the scene view from the AR view
        SceneView sceneView = mArView.getSceneView();

        // Create and show a scene
        ArcGISScene mScene = new ArcGISScene(getString(R.string.scene_g3_palacio));
        sceneView.setScene(mScene);
        loadScene(mScene);

        addElevationSource(mScene);

        // Turn off the space effect and atmosphere effect rendering
        sceneView.setSpaceEffect(SpaceEffect.TRANSPARENT);
        sceneView.setAtmosphereEffect(AtmosphereEffect.NONE);
    }

    /**
     * Adds an elevation source to the provided [mScene].
     * @param mScene
     * @since 100.6.0
     */
    private void addElevationSource(ArcGISScene mScene) {
        ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(getString(R.string.world_elevation_service_url));
        Surface elevationSurface = new Surface();
        elevationSurface.getElevationSources().add(elevationSource);
        elevationSurface.setName("baseSurface");
        elevationSurface.setEnabled(true);
        elevationSurface.setOpacity(0f);
        mScene.setBaseSurface(elevationSurface);
    }

    /**
     * Loads the scene. Sets the surface to transparent and removes the baselayers
     * @param mScene
     */
    private void loadScene(ArcGISScene mScene) {
        mScene.addDoneLoadingListener(() -> {
            Surface sceneSurface = mScene.getBaseSurface();
            BackgroundGrid backgroundGrid = sceneSurface.getBackgroundGrid();
            backgroundGrid.setColor(Color.TRANSPARENT);
            backgroundGrid.setGridLineColor(Color.TRANSPARENT);
            sceneSurface.setNavigationConstraint(NavigationConstraint.STAY_ABOVE);
            Basemap basemap = mScene.getBasemap();
            basemap.addDoneLoadingListener(() -> {
                LayerList layers = basemap.getBaseLayers();
                layers.clear();
            });
        });
    }
}
