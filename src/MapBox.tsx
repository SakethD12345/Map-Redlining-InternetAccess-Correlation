import React, { useEffect, useState } from "react";
import Map, { Layer, MapLayerMouseEvent, Source } from "react-map-gl";
// You need to make this private file api.ts yourself!
import { myKey } from "./private/key";
import { geoLayer, overlayData } from "./overlays";

interface LatLong {
  lat: number;
  long: number;
}

function MapBox() {
  const ProvidenceLatLong: LatLong = { lat: 41.824, long: -71.4128 };
  const initialZoom = 2;

  function onMapClick(e: MapLayerMouseEvent) {
    console.log(e.lngLat.lat);
    console.log(e.lngLat.lng);
  }

  //Use state hook that moves stuff around. Is a use state hook because we
  //want to be able to modify it

  const [viewState, setViewState] = useState({
    longitude: ProvidenceLatLong.long,
    latitude: ProvidenceLatLong.lat,
    zoom: initialZoom,
  });

  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );

  useEffect(() => {
    setOverlay(overlayData());
  }, []);
  return (
    <Map
      mapboxAccessToken={myKey}
      {...viewState}
      //Add an event handler to keep track of events happening
      //When we move the map around sets viewState to ev's ViewState
      onMove={(ev) => setViewState(ev.viewState)}
      //Setting the size of our map to size of whatever window its being opened in
      style={{ width: window.innerWidth, height: window.innerHeight }}
      //Theme of the program (streetview,minimalview, etc)
      mapStyle={"mapbox://styles/mapbox/streets-v12"}
      onClick={(ev: MapLayerMouseEvent) => onMapClick(ev)}
    >
      <Source id="geo_data" type="geojson" data={overlay}>
        <Layer {...geoLayer} />
      </Source>
    </Map>
  );
}

export default MapBox;
