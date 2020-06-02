package com.paula.displaymap;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.IntegratedMeshLayer;
import com.esri.arcgisruntime.mapping.*;
import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.SpaceEffect;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;
import com.esri.arcgisruntime.symbology.SceneSymbol.AnchorPosition;
import com.esri.arcgisruntime.symbology.SymbolStyle.*;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.toolkit.ar.*;
import com.google.ar.sceneform.Scene;

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
        ArcGISScene mScene = new ArcGISScene("https://ct.maps.arcgis.com/home/item.html?id=51ecead507054e89bd5a4546da8aad19");
        sceneView.setScene(mScene);
        //******************************option one: Portal ****************************************
        // create an integrated mesh layer
/*
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

 */
// ************************************************* option two: feature layer *******************
/*
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable("https://www.arcgis.com/home/item.html?id=1aa6f7b927f7434aaa23d12d07af553d");
        // load all attributes in the service feature table
        QueryParameters queryParams = new QueryParameters();
        queryParams.setWhereClause("1=1");
        serviceFeatureTable.queryFeaturesAsync(queryParams, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        // add feature layer(s)
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        featureLayer.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE_TO_SCENE);

        // set the feature layer to render dynamically to allow extrusion
        featureLayer.setRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
        mScene.getLoadSettings().setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
        //create a simple symbol for use in a simple renderer

        //SimpleMarkerSceneSymbol markerSymbol = new SimpleMarkerSceneSymbol(SimpleMarkerSceneSymbol.Style.CUBE, Color.RED, 12, 12, 12, AnchorPosition.CENTER);
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.RED, 10);
        SimpleRenderer renderer = new SimpleRenderer(markerSymbol);
        // set renderer extrusion mode to base height, which includes base height of each vertex in calculating z values
        renderer.getSceneProperties().setExtrusionMode(Renderer.SceneProperties.ExtrusionMode.BASE_HEIGHT);
// set the attribute used for extrusion (if, for example, the feature layer has an attribute called 'pop2000')
        renderer.getSceneProperties().setExtrusionExpression("20");
        featureLayer.setRenderer(renderer);
// add the feature layer to the scene
        mScene.getOperationalLayers().add(featureLayer);

*/
//***************************************** option three: graphics overlay *******************************************
// note: a graphics overlay will not work with a feature layer (see: https://developers.arcgis.com/android/latest/guide/symbolize-data.htm)set
/*
        // create a graphics overlay for the scene
        GraphicsOverlay mSceneOverlay = new GraphicsOverlay();
        mSceneOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE_TO_SCENE);
        sceneView.getGraphicsOverlays().add(mSceneOverlay);

        // create renderer to handle updating plane's orientation
        SimpleRenderer renderer3D = new SimpleRenderer();
        Renderer.SceneProperties renderProperties = renderer3D.getSceneProperties();
        renderProperties.setHeadingExpression("[HEADING]");
        renderProperties.setPitchExpression("[PITCH]");
        renderProperties.setRollExpression("[ROLL]");
        mSceneOverlay.setRenderer(renderer3D);

        // create a graphics overlay for route
        GraphicsOverlay routeOverlay = new GraphicsOverlay();
        mArView.getSceneView().getGraphicsOverlays().add(routeOverlay);
        // create a placeholder graphic for showing the mission route in mini map

        SimpleMarkerSceneSymbol symbol = new SimpleMarkerSceneSymbol(SimpleMarkerSceneSymbol.Style.CUBE, Color.RED, 12, 12, 12, AnchorPosition.BOTTOM);
        Graphic mRouteGraphic = new Graphic( new Point(51.97212903467239, 7.56045252084732, 1000, sceneView.getSpatialReference()), symbol);
        routeOverlay.getGraphics().add(mRouteGraphic);
        Graphic mRouteGraphic2 = new Graphic( new Point( 7.56045252084732, 51.97212903467239, 1000, sceneView.getSpatialReference()), symbol);
        routeOverlay.getGraphics().add(mRouteGraphic2);

        // display the graphic 3 meters above the ground
        routeOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);
        routeOverlay.getSceneProperties().setAltitudeOffset(3);
*/

        // Create and add an elevation surface to the scene
        ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(getString(R.string.world_elevation_service_url));
        Surface elevationSurface = new Surface();
        elevationSurface.getElevationSources().add(elevationSource);
        sceneView.getScene().setBaseSurface(elevationSurface);
        // hide the basemap. The image feed provides map context while navigating in AR
        elevationSurface.setOpacity(0f);

        // disable plane visualization. It is not useful for this AR scenario.
        //mArView.getArSceneView().getPlaneRenderer().setEnabled(false);
        //mArView.getArSceneView().getPlaneRenderer().setVisible(false);

        // Allow the user to navigate underneath the surface
        // This would be critical for working underground or on paths that go underground (e.g. a tunnel)
        elevationSurface.setNavigationConstraint(NavigationConstraint.NONE);

        mScene.setBaseSurface(elevationSurface);

        // Turn off the space effect and atmosphere effect rendering
        sceneView.setSpaceEffect(SpaceEffect.TRANSPARENT);
        sceneView.setAtmosphereEffect(AtmosphereEffect.NONE);

        //LocationManager.getLocationManager().addNmeaListener(new InternalNmeaListener());
    }

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
