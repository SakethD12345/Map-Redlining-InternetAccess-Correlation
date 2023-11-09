import { FeatureCollection } from "geojson";
import { FillLayer, LineLayer } from "react-map-gl";

import {
  isMapsServerResponse,
  isServerErrorResponse,
  MapsServerResponse,
  ServerErrorResponse,
} from "./utils/types";

import { SERVER_URL } from "./utils/url";

// Import the raw JSON file
import rl_data from "./geodata/fullDownload.json";
// you may need to rename the donwloaded .geojson to .json

export function isFeatureCollection(json: any): json is FeatureCollection {
  if (json === undefined) {
    return false;
  }
  // console.log(json.type);
  // console.log(json.features);
  return json.type === "FeatureCollection";
}

// export function overlayData(): GeoJSON.FeatureCollection | undefined {
//   return isFeatureCollection(rl_data) ? rl_data : undefined;
// }

export const fetchData = async (url: string) => {
  try {
    const response = await fetch(url);

    console.log("A response was received");
    if (response == undefined) return undefined;

    const responseJson: MapsServerResponse | ServerErrorResponse =
      await response.json();

    // Using Type Predicate to veriy that responseJson is a ServerResponse.
    if (isMapsServerResponse(responseJson)) {
      return responseJson.data;
    } else if (isServerErrorResponse(responseJson)) {
      return responseJson;
      //alert("Could not get reponse from redlining data server: " + responseJson.message)
    } else {
      return responseJson;
      //alert("Error: Unexpected response from redline data server.")

      console.log(
        "Error: Json returned is not of type MapsServerResponse or ServerErrorResponse"
      );
    }
  } catch (e) {
    console.log(e);
    //alert("General error: " + e);
  }
};
const propertyName = "holc_grade";
export const geoLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#d11d1d",
      "#ccc",
    ],
    "fill-opacity": 0.2,
  },
};

export const geoLayerLine: LineLayer = {
  id: "geo_data2",
  type: "line",
  paint: {
    "line-color": "#d11d1d",
    "line-width": 1,
  },
};
