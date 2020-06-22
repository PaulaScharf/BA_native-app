package com.paula.nativeAppNew;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.*;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.BackgroundGrid;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.SpaceEffect;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.toolkit.ar.*;

public class MainActivity extends AppCompatActivity {

    // license with a license key
    // ArcGISRuntimeEnvironment.setLicense("XXX");
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
        ArcGISScene mScene = new ArcGISScene(getString(R.string.scene_g5_liberty));
        sceneView.setScene(mScene);
        loadScene(mScene);

        addElevationSource(mScene);

        // Turn off the space effect and atmosphere effect rendering
        sceneView.setSpaceEffect(SpaceEffect.TRANSPARENT);
        sceneView.setAtmosphereEffect(AtmosphereEffect.NONE);

        //LocationManager.getLocationManager().addNmeaListener(new InternalNmeaListener());
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

    private void addFeatureLayer(ArcGISScene mScene) {
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable("https://www.arcgis.com/home/item.html?id=e342df14ba174e2188d387bc07206cec");
        // load all attributes in the service feature table
        QueryParameters queryParams = new QueryParameters();
        queryParams.setWhereClause("1=1");
        serviceFeatureTable.queryFeaturesAsync(queryParams, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        // add feature layer(s)
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        featureLayer.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);

        // set the feature layer to render dynamically to allow extrusion
        featureLayer.setRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
        mScene.getLoadSettings().setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
        //create a simple symbol for use in a simple renderer

        //SimpleMarkerSceneSymbol markerSymbol = new SimpleMarkerSceneSymbol(SimpleMarkerSceneSymbol.Style.TETRAHEDRON, Color.RED, 12, 12, 12, AnchorPosition.CENTER);
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.RED, 10);
        //ModelSceneSymbol markerSymbol = new ModelSceneSymbol("./CathedralGLB_GLTF.glb", 10);

        SimpleRenderer renderer = new SimpleRenderer(markerSymbol);
        // set renderer extrusion mode to base height, which includes base height of each vertex in calculating z values
        renderer.getSceneProperties().setExtrusionMode(Renderer.SceneProperties.ExtrusionMode.BASE_HEIGHT);
        // set the attribute used for extrusion (if, for example, the feature layer has an attribute called 'pop2000')
        renderer.getSceneProperties().setExtrusionExpression("20");
        featureLayer.setRenderer(renderer);
        // add the feature layer to the scene
        mScene.getOperationalLayers().add(featureLayer);
    }
/*
    private void addLayerThroughPortal(ArcGISScene mScene) {
        UserCredential credential = new UserCredential("user", "password");
        Portal portal = new Portal(getString(R.string.arcgis_portal_url));
        portal.setCredential(credential);
        portal.addDoneLoadingListener(() -> {

            // check that the portal loaded correctly
            if (portal.getLoadStatus() == LoadStatus.LOADED) {

                // get license info from the portal
                ListenableFuture<LicenseInfo> licenseFuture = portal.fetchLicenseInfoAsync();

                // listen for the license info from the server
                licenseFuture.addDoneListener(() -> {
                    try {
                        LicenseInfo licenseInfo = licenseFuture.get();

                        // Get the license as a json string
                        String licenseJson = licenseInfo.toJson();

                        // the license string will need to be stored locally for starting
                        // the app when there is no network connection.

                        // Apply the license
                        ArcGISRuntimeEnvironment.setLicense(licenseInfo);

                    } catch (InterruptedException e) {
                        // error code goes here
                    } catch (ExecutionException e) {
                        // error code goes here
                    }
                });
            }
        });
        PortalItem portalItem = new PortalItem(portal, getString(R.string.vricon_integrated_mesh_layer_url));
        ArcGISSceneLayer layer = new ArcGISSceneLayer(portalItem);
        mScene.getOperationalLayers().add(layer);
    }
 */
}

/* display a web map
// get a ref to the portal
var portal = await ArcGISPortal.CreateAsync(new Uri("http://www.arcgis.com/sharing/rest"));

// use the unique item id to access the portal item
var item = await ArcGISPortalItem.CreateAsync(portal, "4b2b2784f68e4e5fb0c39c770f1b25ea");

// get the item as a web map
var webmap = await WebMap.FromPortalItemAsync(item);

// create a WebMapViewModel to contain the web map
var vm = await WebMapViewModel.LoadAsync(webmap, portal);

// assign the Map property of the view model to the Map property of the MapView control to display the web map
this.MyMapView.Map = vm.Map;
 */
