import Map, { Layer, MapLayerMouseEvent, Source } from "react-map-gl";
import { geoLayer, overlayData } from "./overlays";
import React, { useEffect, useState } from "react";
import { ACCESS_TOKEN } from "./private/api";

interface LatLong {
  lat: number;
  long: number;
}

function MapBox() {
  const ProvidenceLatLong: LatLong = { lat: 41.824, long: -71.4128 };
  const initialZoom = 10;

  function onMapClick(e: MapLayerMouseEvent) {
    console.log(e.lngLat.lat);
    console.log(e.lngLat.lng);
  }

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
      mapboxAccessToken={ACCESS_TOKEN}
      {...viewState}
      onMove={(ev) => setViewState(ev.viewState)}
      style={{ width: window.innerWidth, height: window.innerHeight }}
      mapStyle={"mapbox://styles/mapbox/dark-v10"}
      onClick={(ev: MapLayerMouseEvent) => onMapClick(ev)}
    >
      <Source id="geo_data" type="geojson" data={overlay}>
        <Layer {...geoLayer} />
      </Source>
    </Map>
  );
}

export default MapBox;
